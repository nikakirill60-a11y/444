#!/usr/bin/env python3
"""CI-only loopback cache for the official Forge Maven, using retrying curl TLS.

FG3/Gradle4's old Java HTTP client repeatedly times out against the Forge CDN.
No replacement artifacts or third-party mirrors: every cache miss goes to the
same official HTTPS origin. Does not run as part of the Minecraft mod.
"""
import argparse
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
import re
import subprocess
import threading
from urllib.parse import unquote, urlsplit

ORIGIN = 'https://maven.minecraftforge.net/'
# Both are official Forge distribution endpoints, not third-party mirrors.
ORIGINS = (ORIGIN, 'https://files.minecraftforge.net/maven/')


def safe_path(request):
    parsed = urlsplit(request)
    path = unquote(parsed.path).lstrip('/')
    if parsed.scheme or parsed.netloc or parsed.query or parsed.fragment or not path:
        raise ValueError('Invalid Maven request')
    if not re.fullmatch(r'[A-Za-z0-9_.+/@-]+', path) or any(p in ('', '.', '..') for p in path.split('/')):
        raise ValueError('Invalid Maven path')
    return path


class Cache:
    def __init__(self, directory):
        self.directory = Path(directory).resolve()
        self.directory.mkdir(parents=True, exist_ok=True)
        self.guard = threading.Lock()
        self.locks = {}
        self.missing = set()

    def get(self, path):
        with self.guard:
            lock = self.locks.setdefault(path, threading.Lock())
        with lock:
            target = self.directory / path
            if target.is_file():
                return 200, target
            if path in self.missing:
                return 404, None
            target.parent.mkdir(parents=True, exist_ok=True)
            partial = target.with_name(target.name + '.partial')
            statuses = []
            try:
                for origin in ORIGINS:
                    try:
                        result = subprocess.run([
                            'curl', '--silent', '--show-error', '--location', '--proto', '=https',
                            '--retry', '1', '--retry-delay', '2', '--retry-max-time', '50',
                            '--connect-timeout', '10', '--max-time', '25',
                            '--output', str(partial), '--write-out', '%{http_code}', origin + path,
                        ], capture_output=True, text=True, timeout=70)
                        status = int(result.stdout) if result.stdout.isdigit() else 502
                        if result.returncode == 0 and status == 200:
                            partial.replace(target)
                            return 200, target
                        statuses.append(status if result.returncode == 0 else 502)
                    except (OSError, subprocess.TimeoutExpired):
                        statuses.append(502)
                    partial.unlink(missing_ok=True)
                missing = all(status == 404 for status in statuses)
                if missing:
                    self.missing.add(path)
                # 404 is normal during Maven repository probing. Only transient
                # failures deserve warnings; never cache those as "missing".
                if not missing:
                    print('::warning title=Official Forge Maven download::'+str(statuses)+' '+path, flush=True)
                return (404 if missing else 502), None
            finally:
                partial.unlink(missing_ok=True)


def handler(cache):
    class Handler(BaseHTTPRequestHandler):
        def do_HEAD(self):
            self.respond(False)

        def do_GET(self):
            self.respond(True)

        def respond(self, body):
            try:
                path = safe_path(self.path)
            except ValueError:
                self.send_error(400)
                return
            code, file = cache.get(path)
            self.send_response(code)
            self.send_header('Content-Length', str(file.stat().st_size if file else 0))
            self.end_headers()
            if body and file:
                try:
                    with file.open('rb') as source:
                        while chunk := source.read(1024 * 256):
                            self.wfile.write(chunk)
                except (BrokenPipeError, ConnectionResetError):
                    pass

        def log_message(self, *args):
            pass
    return Handler


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--cache', required=True)
    parser.add_argument('--port', default=18765, type=int)
    args = parser.parse_args()
    # Loopback is intentional: CI build dependency helper, NOT a browser preview.
    server = ThreadingHTTPServer(('127.0.0.1', args.port), handler(Cache(args.cache)))
    print('Official Forge Maven CI cache listening on', server.server_address, flush=True)
    server.serve_forever()


if __name__ == '__main__':
    main()
