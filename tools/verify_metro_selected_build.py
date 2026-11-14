#!/usr/bin/env python3
"""Verify the dev.17 JAR/JUnit reports before publishing the small non-weapon increment."""
import hashlib
import json
import sys
from pathlib import Path
import xml.etree.ElementTree as ET
import zipfile

VERSION = '1.13.0-dev.17'
REQUIRED = [
    'ru/metrocraft/port/FilterItem.class',
    'ru/metrocraft/port/client/FilterWatchClient.class',
    'ru/metrocraft/port/mechanics/FilterRules.class',
    'ru/metrocraft/port/mechanics/FilterAlarm.class',
    'ru/metrocraft/port/mechanics/FilterStatus.class',
    'ru/metrocraft/port/mechanics/FilterStatusPacket.class',
    'ru/metrocraft/port/inventory/WatchEquipment.class',
    'ru/metrocraft/port/inventory/WatchSyncPacket.class',
    'ru/metrocraft/port/blackrazid/BlackrazidWatchItem.class',
]


def verify(jar, results, source, run_url):
    suites = [ET.parse(p).getroot() for p in sorted(results.glob('TEST-*.xml'))]
    if not suites:
        raise ValueError('Missing JUnit reports')
    totals = {k: sum(int(s.get(k, '0')) for s in suites) for k in ['tests', 'failures', 'errors', 'skipped']}
    if totals['failures'] or totals['errors'] or totals['skipped']:
        raise ValueError('JUnit not clean: ' + repr(totals))
    selected = {s.attrib['name']: int(s.attrib['tests']) for s in suites
                if s.attrib['name'].startswith('ru.metrocraft.port.mechanics.Filter')}
    if sum(selected.values()) != 18:
        raise ValueError('Expected all 18 new mask/watch tests: ' + repr(selected))
    with zipfile.ZipFile(jar) as archive:
        if archive.testzip():
            raise ValueError('Corrupt JAR')
        names = set(archive.namelist())
        if set(REQUIRED) - names:
            raise ValueError('Required implementation missing: ' + repr(set(REQUIRED) - names))
        if ('Implementation-Version: ' + VERSION) not in archive.read('META-INF/MANIFEST.MF').decode():
            raise ValueError('Wrong manifest version')
        for locale in ['ru_ru', 'en_us']:
            if 'metroport.watch.filter=' not in archive.read('assets/metroport/lang/' + locale + '.lang').decode('utf-8'):
                raise ValueError('Missing filter HUD localization')
    return {'version': VERSION, 'source_commit': source, 'ci_run_url': run_url,
            'jar': jar.name, 'bytes': jar.stat().st_size, 'sha256': hashlib.sha256(jar.read_bytes()).hexdigest(),
            'zip_crc': 'PASS', 'junit': totals, 'new_junit_suites': selected,
            'included': ['60-tick completion-only filter replacement', 'server-owned dosimeter filter countdown',
                         'one low-filter alert per episode; BlackRazid watch remains time-only'],
            'not_included': ['donor weapons', 'decoded donor models/audio', 'remaining selected categories'],
            'minecraft_gameplay_verified': False,
            'installation': 'Replace only MetroPort JAR on client and server together. Keep MWF metro4 and customized packv4 unchanged.'}


if __name__ == '__main__':
    jar, results, source, run_url, output = sys.argv[1:]
    report = verify(Path(jar), Path(results), source, run_url)
    Path(output).write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(json.dumps(report, ensure_ascii=False, indent=2))
