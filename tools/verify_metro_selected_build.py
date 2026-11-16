#!/usr/bin/env python3
"""Verify the dev.30 JAR/JUnit reports before publishing the small non-weapon increment."""
import hashlib
import gzip
import json
import sys
from pathlib import Path
import xml.etree.ElementTree as ET
import zipfile

VERSION = '1.13.0-dev.30'
REQUIRED = [
    'ru/metrocraft/port/structures/MetroNetworkCommand.class',
    'ru/metrocraft/port/structures/MetroNetworkPlan.class',
    'ru/metrocraft/port/structures/MetroNetworkJob.class',
    'ru/metrocraft/port/structures/MetroNetworkBackup.class',
    'ru/metrocraft/port/metromod/decor/OriginalDecorCollisions.class',
    'ru/metrocraft/port/metromod/decor/OriginalGateTile.class',
    'ru/metrocraft/port/metromod/decor/DonorDoorCollision.class',
    'assets/metroport/metromod_door_collision.json',
    'assets/metroport/metromod_germo_collision.json',
    'ru/metrocraft/port/metromod/decor/DonorGermoBlockRules.class',
    'ru/metrocraft/port/metromod/decor/DonorGermoMotion.class',
    'ru/metrocraft/port/metromod/decor/DonorGermoCoordinates.class',
    'ru/metrocraft/port/metromod/decor/DonorGermoCollision.class',
    'ru/metrocraft/port/metromod/decor/OriginalGateProfile.class',
    'ru/metrocraft/port/metromod/decor/OriginalGateItem.class',
    'ru/metrocraft/port/client/gates/OriginalGateRenderer.class',
    'ru/metrocraft/port/client/gates/OriginalGateItemRenderer.class',
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
                'ru.metrocraft.port.gates.GateMotionTest': 9,
                'ru.metrocraft.port.metromod.decor.OriginalGateTest': 14,
                'ru.metrocraft.port.metromod.decor.OriginalGatePlacementTest': 7,
                'ru.metrocraft.port.metromod.decor.OriginalInteractiveTest': 11,
                'ru.metrocraft.port.metromod.decor.OriginalLayoutTest': 10,
                'ru.metrocraft.port.metromod.decor.OriginalWorldCollisionTest': 11,
                'ru.metrocraft.port.metromod.decor.DonorDoorCollisionTest': 11,
                'ru.metrocraft.port.metromod.decor.DonorGermoCollisionTest': 9,
                'ru.metrocraft.port.structures.MetroNetworkPlanTest': 8,
                'ru.metrocraft.port.structures.MetroNetworkJobTest': 12,
                'ru.metrocraft.port.structures.MetroNetworkCommandTest': 4}
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
        if json.loads(archive.read('mcmod.info'))[0]['name'] != 'MinETRO':
            raise ValueError('Requested MinETRO branding missing')
        profiles = json.loads(archive.read('assets/metroport/metromod_gates.json'))
        if {p['id'] for p in profiles} != {'metromod_germo_'+str(n) for n in [1,2,4,5,6,9]}:
            raise ValueError('Wrong original gate catalogue')
        interactive=json.loads(archive.read('assets/metroport/metromod_interactive.json'))
        if len(interactive)!=75 or len({p['id'] for p in interactive})!=75:raise ValueError('Wrong interactive catalogue')
        donor=json.loads(archive.read('assets/metroport/metromod_door_collision.json'))
        if set(donor)!={p['id'] for p in interactive if p['kind']=='Door'} or len(donor)!=27:
            raise ValueError('Incomplete donor Door collision catalogue')
        germo=json.loads(archive.read('assets/metroport/metromod_germo_collision.json'))
        expected_germo={p['id'] for p in profiles+interactive if p.get('kind','Germo')=='Germo'}
        if set(germo)!=expected_germo or len(germo)!=17:raise ValueError('Incomplete donor Germo collision catalogue')
        layout=json.loads(archive.read('assets/metroport/metromod_layout.json'))
        if len(layout)!=50 or sum('offset' in row for row in layout.values())!=6:raise ValueError('Missing wall/solid panel layout')
        profiles+=interactive
        for profile in profiles:
            for part in profile['parts']:
                model = 'assets/' + part['mesh'].replace(':','/',1)
                if not gzip.decompress(archive.read(model)).strip():
                    raise ValueError('Missing original gate mesh: '+model)
                for texture in part['materials'].values():
                    if not archive.read('assets/'+texture.replace(':','/',1)).startswith(b'\x89PNG'):
                        raise ValueError('Missing original gate texture')
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
            'included': ['dev30: donor model-mode collision contract for 27 Doors; activation-based collision suppression, replaceable open selection proxies; custom local AABB NBT; 17 Germo use donor stateful block callbacks with transactional motion, NBT/packet snapshots and UUID-safe proxy migration',
                         'dev29: new four-station vanilla metro ring; two connected rail loops, pedestrian bridges, service rooms, surface ladder vestibule; operator preview/confirm command with durable conservative undo; standalone schematic',
                         'MinETRO display name; saved-world namespace metroport unchanged',
                         'Previous approximate collision implementation retained for Lever/Light only; wall lever offsets and clicked-face orientation retained',
                         '81 of 112 tagged functional decor entries: 17 gates, 27 doors, 6 levers, 31 lights; native meshes; Forge behavior adapters; 31 blocked entries explicitly excluded','MetroEdit removed: command, blueprint storage, packet16 and preview rendering; visual settings retained',
                         'client visual options GUI in Forge Mods list with live apply, mask opacity and noise strength', '8 original audio events: flask/medkit/filter/3 watch alarms/gate open-close',
                         'original clean mask and NVG goggles/noise texture compatibility overlays; individually configurable',
                         '3 medical materials with original icons and donor stack limits; no invented healing', 'three donor-profile foods and bottled water; stack limit 8; completion-only server use',
                         'four byte-identical decoded original METRO_MOD icons; creative catalogue integration',
                         'server-owned N-key NVG toggle; optional rebuilt tint, no battery rebalance',
                         'flask/medkit stack validation; generator bounds checks; stale lamp light cleanup',
                         'six-gate server motion/collision regression coverage; part origin and finite progress validation',
                         'dev17 filter/watch mechanics retained; original completion/alarm audio adapted'],
            'not_included': ['Original server map state and per-instance transforms; map-specific Door custom AABBs/transforms unavailable', 'MetroEdit (removed at user request); full donor model/microblock/region/NPC editing suite', 'donor weapons', '30 Inventory profiles: unconfirmed server behavior; mls_vorota_l06: missing add2; full donor GLSL pipeline', 'unconfirmed healing/bleeding effects',
                             'replacement Kikimora/Nosach models; existing BlackRazid mobs retained'],
            'minecraft_gameplay_verified': False,
            'installation': 'Replace only MetroPort JAR on client and server together. Keep MWF metro4 and customized packv4 unchanged.'}


if __name__ == '__main__':
    jar, results, source, run_url, output = sys.argv[1:]
    report = verify(Path(jar), Path(results), source, run_url)
    Path(output).write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(json.dumps(report, ensure_ascii=False, indent=2))
