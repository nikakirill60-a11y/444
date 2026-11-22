#!/usr/bin/env python3
"""Prepare/upload content-addressed MinETRO dev updates. Offline and dry-run by default."""
import argparse
import hashlib
import json
import os
import re
import tempfile
from pathlib import Path
from urllib.error import HTTPError
from urllib.parse import urlsplit
from urllib.request import Request, build_opener, HTTPRedirectHandler

CHUNK = 8 * 1024 * 1024
MAX_JSON = 512 * 1024
MANAGED = {'mods', 'ModularWarfare', 'resourcepacks', 'scripts'}
SHA = re.compile(r'^[a-f0-9]{64}$')


def safe_path(path):
    if not isinstance(path, str) or not path or len(path) > 230 or '\\' in path or any(ord(c) < 32 or c in ':*?"<>|' for c in path):
        raise ValueError('Unsafe Windows path')
    for part in path.split('/'):
        if not part or part in ('.', '..') or part.endswith(('.', ' ')) or re.fullmatch(r'CON|PRN|AUX|NUL|COM[0-9]|LPT[0-9]', part.split('.')[0], re.I):
            raise ValueError('Unsafe Windows path')
    return path


def personal(path):
    return any(p.lower() == 'packv4.json' for p in path.split('/')) or path.lower() == 'modularwarfare/mod_config.json'


def mode_for(path):
    safe_path(path)
    if personal(path) or '/' not in path:
        return None
    top = path.split('/')[0]
    return 'managed' if top in MANAGED else 'seed' if top == 'config' else None


def encode(value):
    return json.dumps(value, ensure_ascii=False, separators=(',', ':')).encode('utf-8')


def validate(manifest):
    if manifest.get('schema') != 2 or not re.fullmatch(r'[A-Za-z0-9][A-Za-z0-9._-]{0,39}', manifest.get('version', '')):
        raise ValueError('Invalid release version/schema')
    files = manifest.get('files', [])
    if not 2 <= len(files) <= 5000 or len(encode(manifest)) > MAX_JSON:
        raise ValueError('Invalid manifest size')
    paths, sizes, total = set(), {}, 0
    for f in files:
        p, mode, size, chunks = f['path'], f['mode'], f['bytes'], f['chunks']
        if mode_for(p) != mode or mode is None or p.lower() in paths or not SHA.fullmatch(f['sha256']) or type(size) is not int or not 0 <= size <= 8 * 1024**3 or len(chunks) > 1024:
            raise ValueError('Invalid file or protected path')
        paths.add(p.lower())
        for c in chunks:
            sha, count = c['sha256'], c['bytes']
            if not SHA.fullmatch(sha) or type(count) is not int or not 1 <= count <= CHUNK or (sha in sizes and sizes[sha] != count):
                raise ValueError('Invalid chunk')
            sizes[sha] = count
        if sum(c['bytes'] for c in chunks) != size or (size == 0 and f['sha256'] != hashlib.sha256(b'').hexdigest()):
            raise ValueError('Chunk sizes do not match file')
        total += size
    for p in paths:
        parts = p.split('/')
        if any('/'.join(parts[:i]) in paths for i in range(1, len(parts))):
            raise ValueError('File/directory collision')
    mods = [f for f in files if f['path'].startswith('mods/') and f['path'].endswith('.jar')]
    if len(mods) < 2 or not any(f['path'].startswith('mods/metrocraft-port-') for f in mods) or total > 32 * 1024**3:
        raise ValueError('Need the complete MinETRO pack, not a single JAR')
    return manifest


def prepare(source, out, version):
    source, out = source.resolve(), out.resolve()
    if out == source or source in out.parents or out in source.parents:
        raise ValueError('Output and game folder must be separate')
    manifest = {'schema': 2, 'version': version, 'files': []}
    if not source.is_dir() or not re.fullmatch(r'[A-Za-z0-9][A-Za-z0-9._-]{0,39}', version):
        raise ValueError('Need game-data folder and safe version')
    out.mkdir(parents=True, exist_ok=True)
    objects = out / 'chunks'
    objects.mkdir(exist_ok=True)
    # Only walk managed roots. Personal saves/screenshots are not read or uploaded.
    for top in sorted(MANAGED | {'config'}):
        folder = source / top
        if folder.is_symlink():
            raise ValueError('Symbolic links are forbidden')
        if not folder.exists():
            continue
        for base, dirs, names in os.walk(folder, followlinks=False):
            for name in dirs + names:
                if (Path(base) / name).is_symlink():
                    raise ValueError('Symbolic links are forbidden')
            for name in sorted(names):
                path = Path(base) / name
                relative = path.relative_to(source).as_posix()
                mode = mode_for(relative)
                if mode is None:
                    continue
                digest, chunks, size = hashlib.sha256(), [], 0
                with path.open('rb') as data:
                    while block := data.read(CHUNK):
                        sha = hashlib.sha256(block).hexdigest()
                        target = objects / sha
                        valid = False
                        if target.exists() and target.stat().st_size == len(block):
                            with target.open('rb') as existing:
                                valid = hashlib.file_digest(existing, 'sha256').hexdigest() == sha
                        if not valid:
                            with tempfile.NamedTemporaryFile(dir=objects, delete=False) as temp:
                                temp.write(block)
                                tmp = Path(temp.name)
                            tmp.replace(target)
                        digest.update(block)
                        size += len(block)
                        chunks.append({'sha256': sha, 'bytes': len(block)})
                manifest['files'].append({'path': relative, 'mode': mode, 'bytes': size, 'sha256': digest.hexdigest(), 'chunks': chunks})
    manifest['files'].sort(key=lambda f: f['path'])
    validate(manifest)
    target = out / 'manifest.json'
    with tempfile.NamedTemporaryFile(dir=out, delete=False) as temp:
        temp.write(encode(manifest))
        tmp = Path(temp.name)
    tmp.replace(target)
    return manifest


class NoRedirect(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        raise ValueError('Redirect refused; credentials were not forwarded')


class Storage:
    def __init__(self, url, token):
        u = urlsplit(url)
        if u.scheme != 'https' or not re.fullmatch(r'[a-z0-9-]+\.supabase\.co', u.netloc) or u.path not in ('', '/') or u.query or u.fragment or not token:
            raise ValueError('Need project HTTPS URL and server-only publisher key in environment')
        self.url, self.token, self.http = url.rstrip('/'), token, build_opener(NoRedirect())

    def request(self, method, path, data=None, content_type='application/json'):
        req = Request(self.url + path, data=data, method=method, headers={'Authorization': 'Bearer ' + self.token, 'apikey': self.token, 'Content-Type': content_type, 'x-upsert': 'false'})
        try:
            return self.http.open(req, timeout=120)
        except HTTPError as error:
            code = error.code
            error.close()
            # Do not log response bodies, headers, secret tokens or signed URLs.
            raise RuntimeError('Publisher HTTP ' + str(code)) from None

    def exists(self, sha, size):
        try:
            with self.request('HEAD', '/storage/v1/object/authenticated/dev-packs/chunks/' + sha) as response:
                if int(response.headers.get('Content-Length', '-1')) != size:
                    raise ValueError('Remote object has unexpected size: ' + sha)
                return True
        except RuntimeError as error:
            if str(error) == 'Publisher HTTP 404':
                return False
            raise

    def upload(self, sha, data):
        with self.request('POST', '/storage/v1/object/dev-packs/chunks/' + sha, data, 'application/octet-stream'):
            pass

    def rpc(self, name, payload):
        with self.request('POST', '/rest/v1/rpc/' + name, encode(payload)) as response:
            return json.loads(response.read(MAX_JSON + 1))


def upload(prepared, storage, apply=False, activate=False, approval=None):
    prepared = Path(prepared)
    if activate and (not apply or not approval or not approval.strip()):
        raise ValueError('Activation requires --apply and explicit --approval; tests are not owner acceptance')
    manifest = validate(json.loads((prepared / 'manifest.json').read_text(encoding='utf-8')))
    if storage.rpc('file_distribution_protocol', {}) != 2:
        raise ValueError('Deploy the schema-2 migration first')
    # Optimistic lock: activation fails if another release changed while uploading.
    with storage.request('GET', '/rest/v1/launcher_dev_release?singleton=eq.true&select=manifest') as response:
        current = json.loads(response.read(MAX_JSON + 1))
    expected = current[0]['manifest']['version'] if current else None
    chunks = {c['sha256']: c['bytes'] for f in manifest['files'] for c in f['chunks']}
    uploaded, skipped, pending = 0, 0, 0
    for sha, size in sorted(chunks.items()):
        p = prepared / 'chunks' / sha
        if p.is_symlink() or p.stat().st_size != size:
            raise ValueError('Missing/invalid local chunk: ' + sha)
        with p.open('rb') as data:
            if hashlib.file_digest(data, 'sha256').hexdigest() != sha:
                raise ValueError('Local chunk checksum mismatch: ' + sha)
        if storage.exists(sha, size):
            skipped += size
        elif apply:
            storage.upload(sha, p.read_bytes())
            if not storage.exists(sha, size):
                raise ValueError('Upload verification failed')
            uploaded += size
        else:
            pending += size
    if activate:
        storage.rpc('publish_dev_file_release', {'new_manifest': manifest, 'expected_version': expected, 'approval': approval.strip()})
    return {'version': manifest['version'], 'uploadedBytes': uploaded, 'reusedBytes': skipped, 'pendingBytes': pending, 'activated': activate}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest='command', required=True)
    p = sub.add_parser('prepare'); p.add_argument('source', type=Path); p.add_argument('--out', type=Path, required=True); p.add_argument('--version')
    p = sub.add_parser('upload'); p.add_argument('prepared', type=Path); p.add_argument('--apply', action='store_true'); p.add_argument('--activate', action='store_true'); p.add_argument('--approval')
    args = parser.parse_args()
    if args.command == 'prepare':
        result = prepare(args.source, args.out, args.version or input("Версия dev (латиница, цифры, точка, дефис): ").strip())
        print(json.dumps({'files': len(result['files']), 'bytes': sum(f['bytes'] for f in result['files']), 'activated': False}))
    else:
        storage = Storage(os.environ.get('MINETRO_STORAGE_URL', ''), os.environ.get('MINETRO_PUBLISHER_KEY', ''))
        print(json.dumps(upload(args.prepared, storage, args.apply, args.activate, args.approval)))

if __name__ == '__main__':
    try:
        main()
    except (ValueError, RuntimeError, OSError, KeyError, TypeError) as error:
        raise SystemExit(str(error)) from None
