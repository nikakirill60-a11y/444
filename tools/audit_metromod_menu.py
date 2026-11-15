#!/usr/bin/env python3
"""Static menu dependency inventory. Requires jawa==2.2.0; never loads donor Java."""
import hashlib
import io
import json
from pathlib import Path
import sys
import zipfile
from jawa.cf import ClassFile
from jawa.constants import String

CLIENT_SHA = '66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc'
MENU_CLASS = 'null/iilIjliijlIIjilillIjjjlIliIjijiI'


def audit(client):
    data = Path(client).read_bytes()
    if hashlib.sha256(data).hexdigest() != CLIENT_SHA:
        raise ValueError('Unexpected donor client SHA256')
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        cf = ClassFile(io.BytesIO(archive.read(MENU_CLASS + '.class')))
    strings = sorted({c.string.value for c in cf.constants.find(type_=String)})
    models = [s for s in strings if s.endswith('.m2033model')]
    return {
        'source': 'user-supplied METRO_MOD client.jar', 'source_sha256': CLIENT_SHA,
        'menu_class': MENU_CLASS, 'superclass': cf.super_.name.value,
        'model_references': models,
        'sound_events': [s for s in strings if s.startswith('metro2033server:menu.')],
        'additional_texture_literals': [s for s in strings if s.endswith('.png')],
        'original_server_literal': 'play.metro2033server.ru:25565',
        'requested_port_target': 'localhost:25565',
        'target_implemented': False,
        'status': 'blocked: original scene model bytes not available; no substitute menu installed',
        'limitations': [
            'Constant-pool references are not extracted model files or a completed menu port.',
            'Model-internal materials, textures, animations and recursive dependencies remain unaudited.',
            'Original website/skin/status endpoints must not be activated by the port.',
            'localhost means the computer running the Minecraft client, not a hosted remote server.',
            'The original 1.7.10 rendering code needs adaptation for Forge 1.12.2.'
        ]
    }


if __name__ == '__main__':
    if len(sys.argv) != 3:
        raise SystemExit('Usage: python tools/audit_metromod_menu.py client.jar report.json')
    result = audit(sys.argv[1])
    Path(sys.argv[2]).write_text(json.dumps(result, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print('Inventoried', len(result['model_references']), 'model references; no donor code executed.')
