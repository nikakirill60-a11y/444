#!/usr/bin/env python3
"""Prepare only the six original 3D-menu sound events; never runs donor Java.

Does not install/enable a replacement GUI. Playback categories and loop lifetimes
belong to the future GUI, not to sounds.json in Minecraft 1.12.2.
"""
import argparse
import hashlib
import io
import json
from pathlib import Path
import zipfile

from tools.decode_metromod_content import decode, validate

ROOT = Path(__file__).resolve().parents[1]
CONTENT_SHA = '318cdcf0a48f076571300e4f8336651c702601cd9beafbc0a72cf028d29af9ab'
EVENTS = {
    'menu_carnoise': ('ambient', ['menu_carnoise1']),
    'menu_intro_loop': ('music', ['menu_intro_loop']),
    'menu_railnoise': ('ambient', ['menu_railnoise' + str(i) for i in range(1, 6)]),
    'menu_wheel_knock_noise': ('ambient', ['menu_wheel_knock_noise' + str(i) for i in range(1, 9)]),
    'regina_engine_idle': ('ambient', ['regina_engine_idle']),
    'switch2_light': ('master', ['switch2_light' + str(i) for i in range(1, 6)]),
}


def sha(data):
    return hashlib.sha256(data).hexdigest()


def prepare(data):
    """Verify pinned source, full event membership and Vorbis before any writes."""
    if sha(data) != CONTENT_SHA:
        raise ValueError('Unexpected METRO_MOD Content.jar SHA256')
    assets = {}; events = {}; records = []
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        names = archive.namelist()
        if len(set(names)) != len(names):
            raise ValueError('Duplicate ZIP members')
        definitions = json.loads(archive.read('assets/metro2033server/sounds.json'))
        for name, (category, samples) in EVENTS.items():
            source_event = 'menu.' + name
            expected = {'category': category, 'sounds': ['menu/' + s for s in samples]}
            if definitions.get(source_event) != expected:
                raise ValueError('Unexpected source event definition: ' + source_event)
            # No streaming/loop flag is guessed from the event name. Preserve the
            # donor's default sample loading; looping is controlled by GUI code.
            target_event = 'metromod.menu.' + name
            events[target_event] = {'sounds': ['metroport:metromod/menu/' + s for s in samples]}
            for sample in samples:
                source = 'assets/metro2033server/sounds/menu/' + sample + '.m2033sound'
                entry = archive.getinfo(source)
                if entry.file_size > 16 * 1024 * 1024 or entry.flag_bits & 1:
                    raise ValueError('Oversized/encrypted ZIP member')
                encrypted = archive.read(entry)
                plain = decode(encrypted)
                metadata = validate(plain, '.ogg')
                output = 'sounds/metromod/menu/' + sample + '.ogg'
                assets[output] = plain
                records.append({'source_path': source, 'source_sha256': sha(encrypted),
                                'target_path': output, 'bytes': len(plain),
                                'sha256': sha(plain), **metadata})
    report = {
        'source': 'Metro2033Content.jar', 'source_sha256': CONTENT_SHA,
        'source_bytes': len(data), 'events': events,
        'donor_categories': {'metromod.menu.' + n: v[0] for n, v in EVENTS.items()},
        'files': records, 'audio_prepared': True,
        'menu_implemented': False, 'in_game_playback_verified': False,
        'notes': [
            'OGG bytes are the exact decrypted donor payloads, without re-encoding.',
            'Vorbis streams decoded completely offline; this is not in-game playback.',
            'No GUI hook, automatic sound playback, Java event registration or network request added.',
            'Camera, scene, buttons, animation and sound lifetimes still require the original GUI port.'
        ]
    }
    return assets, events, report


def install(source, resource_root, report_path):
    assets, events, report = prepare(Path(source).read_bytes())
    resource_root = Path(resource_root)
    definitions = resource_root / 'sounds.json'
    existing = json.loads(definitions.read_text(encoding='utf-8'))
    # Refuse to overwrite unrelated events or different previously prepared bytes.
    for name, value in events.items():
        if name in existing and existing[name] != value:
            raise ValueError('Conflicting sound event: ' + name)
    for name, data in assets.items():
        target = resource_root / name
        if target.exists() and target.read_bytes() != data:
            raise ValueError('Conflicting sound asset: ' + name)
    for name, data in assets.items():
        target = resource_root / name
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(data)
    existing.update(events)
    definitions.write_text(json.dumps(dict(sorted(existing.items())), ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    Path(report_path).write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    return report


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('content')
    args = parser.parse_args()
    report = install(args.content, ROOT / 'src/main/resources/assets/metroport',
                     ROOT / 'reports/METRO_MOD/menu_audio_verified.json')
    print('Prepared', len(report['events']), 'events,', len(report['files']), 'original OGG files; GUI not enabled.')
