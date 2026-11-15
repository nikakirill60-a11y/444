#!/usr/bin/env python3
"""Verify the dev.24 JAR/JUnit reports before publishing the small non-weapon increment."""
import hashlib
import json
import sys
from pathlib import Path
import xml.etree.ElementTree as ET
import zipfile

VERSION = '1.13.0-dev.24'
REQUIRED = [
    'ru/metrocraft/port/client/MetroGuiFactory.class',
    'ru/metrocraft/port/client/VisualSettingsScreen.class',
    'ru/metrocraft/port/metromod/VisualOptions.class',
    'ru/metrocraft/port/metromod/MetroAudio.class',
    'ru/metrocraft/port/metromod/MedicalMaterialItem.class',
    'ru/metrocraft/port/metromod/OverlayRules.class',
    'ru/metrocraft/port/client/MetroOverlays.class',
    'ru/metrocraft/port/metromod/MetroConsumables.class',
    'ru/metrocraft/port/metromod/MetroFoodItem.class',
    'ru/metrocraft/port/inventory/NvgControl.class',
    'ru/metrocraft/port/inventory/NvgTogglePacket.class',
    'ru/metrocraft/port/inventory/NvgStatePacket.class',
    'ru/metrocraft/port/client/NvgEffects.class',
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
    expected = {'ru.metrocraft.port.metromod.VisualOptionsTest': 5, 'ru.metrocraft.port.metromod.MetroSelectionTest': 10,
                'ru.metrocraft.port.blackrazid.MetroSoundUseTest': 4, 'ru.metrocraft.port.metromod.MetroFoodTest': 12, 'ru.metrocraft.port.blackrazid.NvgControlTest': 7,
                'ru.metrocraft.port.blackrazid.SurvivalSafetyTest': 5,
                'ru.metrocraft.port.gates.GateMotionTest': 9}
    counts = {s.attrib['name']: int(s.attrib['tests']) for s in suites}
    for name, count in expected.items():
        if counts.get(name) != count:
            raise ValueError('Missing regression suite: ' + name)
    selected.update({name: counts[name] for name in expected})
    with zipfile.ZipFile(jar) as archive:
        if archive.testzip():
            raise ValueError('Corrupt JAR')
        names = set(archive.namelist())
        if any(n.startswith('ru/metrocraft/port/editor/') or n.startswith('ru/metrocraft/port/client/EditorPreviewRenderer') for n in names):
            raise ValueError('Removed MetroEdit classes remain in JAR')
        if set(REQUIRED) - names:
            raise ValueError('Required implementation missing: ' + repr(set(REQUIRED) - names))
        if ('Implementation-Version: ' + VERSION) not in archive.read('META-INF/MANIFEST.MF').decode():
            raise ValueError('Wrong manifest version')
        for item in ['konservi', 'galeti', 'gribtea', 'waterbottle']:
            path = 'assets/metroport/models/item/metromod_' + item + '.json'
            if path not in names:
                raise ValueError('Missing food model: ' + item)
        for row in json.loads(archive.read('assets/metroport/metromod_consumables.json')):
            texture = 'assets/metroport/textures/metromod/items/' + row['sourceId'] + '.png'
            if texture not in names or hashlib.sha256(archive.read(texture)).hexdigest() != row['iconSha256']:
                raise ValueError('Original donor icon missing or modified: ' + texture)
            if row['visualStatus'] != 'original decoded METRO_MOD icon':
                raise ValueError('Provisional icon retained')
        if len(json.loads(archive.read('assets/metroport/metromod_consumables.json'))) != 4:
            raise ValueError('Wrong consumables catalogue')
        for row in json.loads(archive.read('assets/metroport/metromod_selected_sources.json')):
            target = 'assets/metroport/' + row['target']
            if target not in names or hashlib.sha256(archive.read(target)).hexdigest() != row['sha256']:
                raise ValueError('Selected donor resource missing or modified: ' + target)
        materials = json.loads(archive.read('assets/metroport/metromod_medical_materials.json'))
        if len(materials) != 3 or any(row['healingEffect'] is not None for row in materials):
            raise ValueError('Invalid medical material catalogue')
        for locale in ['ru_ru', 'en_us']:
            if 'metroport.watch.filter=' not in archive.read('assets/metroport/lang/' + locale + '.lang').decode('utf-8'):
                raise ValueError('Missing filter HUD localization')
    return {'version': VERSION, 'source_commit': source, 'ci_run_url': run_url,
            'jar': jar.name, 'bytes': jar.stat().st_size, 'sha256': hashlib.sha256(jar.read_bytes()).hexdigest(),
            'zip_crc': 'PASS', 'junit': totals, 'new_junit_suites': selected,
            'included': ['MetroEdit removed: command, blueprint storage, packet16 and preview rendering; visual settings retained',
                         'client visual options GUI in Forge Mods list with live apply, mask opacity and noise strength', '8 original audio events: flask/medkit/filter/3 watch alarms/gate open-close',
                         'original clean mask and NVG goggles/noise texture compatibility overlays; individually configurable',
                         '3 medical materials with original icons and donor stack limits; no invented healing', 'three donor-profile foods and bottled water; stack limit 8; completion-only server use',
                         'four byte-identical decoded original METRO_MOD icons; creative catalogue integration',
                         'server-owned N-key NVG toggle; optional rebuilt tint, no battery rebalance',
                         'flask/medkit stack validation; generator bounds checks; stale lamp light cleanup',
                         'six-gate server motion/collision regression coverage; part origin and finite progress validation',
                         'dev17 filter/watch mechanics retained; original completion/alarm audio adapted'],
            'not_included': ['MetroEdit (removed at user request); full donor model/microblock/region/NPC editing suite', 'donor weapons', 'donor 3D models and the full GLSL 330 framebuffer pipeline', 'unconfirmed healing/bleeding effects',
                             'replacement Kikimora/Nosach models; existing BlackRazid mobs retained'],
            'minecraft_gameplay_verified': False,
            'installation': 'Replace only MetroPort JAR on client and server together. Keep MWF metro4 and customized packv4 unchanged.'}


if __name__ == '__main__':
    jar, results, source, run_url, output = sys.argv[1:]
    report = verify(Path(jar), Path(results), source, run_url)
    Path(output).write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(json.dumps(report, ensure_ascii=False, indent=2))
