#!/usr/bin/env python3
r"""Collect only the 35 direct original METRO_MOD 3D menu models, never execute them.

Usage (Python 3, no third-party dependencies):
    python collect_metromod_menu.py "C:\Metro\content\Metro2033Models.jar"

Output: NEW directory metro_menu_parts with independent ZIPs <= 90 MiB and a
SHA256 inventory. Upload these small ZIPs and inventory to the shared Drive.
These are encrypted/original source bytes, NOT an installable menu or texture pack.
Textures referenced inside models cannot be determined until models are decoded.
No login files, Java classes, weapon assets or unrelated models are collected.
"""
import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import stat
import zipfile

MODEL_REFERENCES = ('decor_metro/trub.m2033model', 'decor_metro/trub90.m2033model', 'decor_metro/web1.m2033model', 'decor_metro/web2.m2033model', 'decor_metro/web3.m2033model', 'drezina/regina.m2033model', 'drezina/regina_well.m2033model', 'menu/germo_left.m2033model', 'menu/germo_right.m2033model', 'menu/part1.m2033model', 'menu/part2.m2033model', 'menu/part3.m2033model', 'menu/part3_1.m2033model', 'menu/part4.m2033model', 'menu/part5.m2033model', 'menu/part5_1.m2033model', 'menu/part6.m2033model', 'menu/part6_1.m2033model', 'menu/part7.m2033model', 'menu/part7_1.m2033model', 'menu/part8.m2033model', 'menu/part8_1.m2033model', 'menu/part8_2.m2033model', 'menu/part9.m2033model', 'menu/part9_1.m2033model', 'menu/tun.m2033model', 'plant/root1.m2033model', 'plant/root2.m2033model', 'tunnels/rails.m2033model', 'tunnels/tunnel1.m2033model', 'tunnels/tunnel2.m2033model', 'tunnels/tunnel3.m2033model', 'tunnels/tunnel4.m2033model', 'tunnels/tunnel5.m2033model', 'tunnels/tunnel_betonnew_1.m2033model')

LIMIT = 90 * 1024 * 1024


def safe_name(name):
    p = PurePosixPath(name)
    return (not p.is_absolute() and not any(s in ('.', '..') for s in name.split('/'))
            and not any(c in name for c in ('\\', ':', '\x00')))


def collect(source, output, references=MODEL_REFERENCES, limit=LIMIT):
    source, output = Path(source), Path(output)
    if limit < 1024 or limit > LIMIT:
        raise ValueError('Invalid ZIP size limit')
    # Never overwrite previous results, even partially completed ones.
    if output.exists():
        raise FileExistsError('Output already exists; choose a new --output directory: ' + str(output))
    selected, missing = {}, []
    with zipfile.ZipFile(source) as archive:
        entries = archive.infolist()
        for ref in references:
            matches = [i for i in entries if not i.is_dir() and (i.filename == ref or i.filename.endswith('/' + ref))]
            if len(matches) > 1:
                raise ValueError('Ambiguous archive paths for ' + ref)
            if not matches:
                missing.append(ref)
                continue
            info = matches[0]
            if not safe_name(info.filename) or stat.S_ISLNK(info.external_attr >> 16):
                raise ValueError('Unsafe archive path: ' + info.filename)
            # STORE avoids unpredictable compression expansion. Reserve ZIP headers.
            cost = info.file_size + 2 * len(info.filename.encode('utf-8')) + 256
            if cost + 256 > limit:
                raise ValueError('A single model exceeds part limit: ' + info.filename)
            selected[ref] = (info, cost)
        output.mkdir(parents=True)
        records, parts = [], []
        writer, size, part = None, 256, None
        try:
            for ref, (info, cost) in selected.items():
                if writer is None or size + cost > limit:
                    if writer is not None:
                        writer.close()
                    part = output / ('metro_menu_models_%02d.zip' % (len(parts) + 1))
                    parts.append(part)
                    writer = zipfile.ZipFile(part, 'x', compression=zipfile.ZIP_STORED, allowZip64=False)
                    size = 256
                digest, count = hashlib.sha256(), 0
                with archive.open(info) as src, writer.open(info.filename, 'w') as dst:
                    for chunk in iter(lambda: src.read(1024 * 1024), b''):
                        count += len(chunk)
                        if count > info.file_size:
                            raise ValueError('Member size exceeded: ' + info.filename)
                        dst.write(chunk)
                        digest.update(chunk)
                if count != info.file_size:
                    raise ValueError('Incomplete member: ' + info.filename)
                records.append(dict(reference=ref, path=info.filename, bytes=count,
                                    sha256=digest.hexdigest(), part=part.name))
                size += cost
        finally:
            if writer is not None:
                writer.close()
    def sha(path):
        h = hashlib.sha256()
        with path.open('rb') as f:
            for chunk in iter(lambda: f.read(1024 * 1024), b''):
                h.update(chunk)
        return h.hexdigest()
    part_records = []
    for p in parts:
        if p.stat().st_size > limit:
            raise ValueError('Output exceeds upload limit: ' + str(p))
        with zipfile.ZipFile(p) as z:
            if z.testzip() is not None:
                raise ValueError('Output CRC failure: ' + str(p))
        part_records.append(dict(name=p.name, bytes=p.stat().st_size, sha256=sha(p)))
    report = dict(source=source.name, source_sha256=sha(source), matched=len(records),
                  expected=len(references), missing=missing, files=records, parts=part_records,
                  status='original source models only; internal texture dependencies not yet audited')
    (output / 'menu_model_inventory.json').write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    return report


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('models_jar', help='Local Metro2033Models.jar, not a Drive link')
    parser.add_argument('--output', default='metro_menu_parts', help='New output directory')
    args = parser.parse_args()
    result = collect(args.models_jar, args.output)
    print('Collected %d/%d direct model references into %d ZIP(s).' % (result['matched'], result['expected'], len(result['parts'])))
    print('Output:', Path(args.output).resolve())
    print('No donor code executed. No source files changed. Texture dependencies are not included.')
    if result['missing']:
        print('Missing references listed in menu_model_inventory.json; do not treat this as a complete scene.')
        raise SystemExit(2)
