#!/usr/bin/env python3
"""First MCMOD batch, from downloaded original files + CFR reference, never invented meshes."""
import hashlib
import json
import re
from import_content import ROOT, ASSETS, put, write_json

SOURCE = ROOT / 'tools/mcmod-reference'
WHOLE = {'officer_ozk', 'banditkurtka', 'kurtka', 'plash', 'ghillie'}
IMPORTED = WHOLE | {'bkz'}


def registrations():
    source = (SOURCE / 'java/metro/common/regstration/RegisterArmor.java').read_text()
    names = {key: bytes(name, 'ascii').decode('unicode_escape') for key, name in
             re.findall(r'LanguageRegistry.addName\(\(Object\)(\w+), \(String\)"([^"]+)"\)', source)}
    entries = {}
    for key, durability, _, type_, protect in re.findall(r'(\w+) = new ItemCustomArmor\((\d+), (\d+), (\d+), (\d+)\)', source):
        entries[key] = {'sourceId': key, 'condition': int(durability), 'type': int(type_), 'protect': int(protect), 'name': names[key]}
    for durability, key, name, type_, protect in re.findall(r'RegisterArmor.regArmor\((\d+), \w+, "([^"]+)", "([^"]+)", (\d+), (\d+)\)', source):
        entries[key] = {'sourceId': key, 'condition': int(durability), 'type': int(type_), 'protect': int(protect),
                        'name': bytes(name, 'ascii').decode('unicode_escape')}
    return dict(sorted(entries.items(), key=lambda pair: pair[1]['type']))


def main():
    originals = registrations()
    entries, models = [], {}
    for key in sorted(IMPORTED):
        original = originals[key]
        mesh = (SOURCE / ('assets/armor/' + key + '/model.obj')).read_bytes()
        texture = (SOURCE / ('assets/armor/' + key + '/texture.png')).read_bytes()
        icon = (SOURCE / ('assets/icons/' + key + '.png')).read_bytes()
        if not texture.startswith(b'\x89PNG\r\n\x1a\n') or not icon.startswith(b'\x89PNG\r\n\x1a\n'):
            raise ValueError('Invalid original PNG: ' + key)
        text = mesh.decode('utf-8-sig')
        groups = sorted(set(re.findall(r'^[go]\s+(\S+)', text, re.M)))
        if not set(groups) <= {'head', 'body', 'leftarm', 'rightarm', 'leftleg', 'rightleg'}:
            raise ValueError('Unexpected OBJ groups: ' + key)
        for suffix, data in [('model.obj', mesh), ('texture.png', texture)]:
            put(ASSETS / ('models/mcmod/' + key + '/' + suffix) if suffix.endswith('.obj')
                else ASSETS / ('textures/mcmod/armor/' + key + '.png'), data)
        put(ASSETS / ('textures/mcmod/icons/' + key + '.png'), icon)
        models[key] = {'groups': groups, 'vertices': len(re.findall(r'^v ', text, re.M)),
                       'faces': len(re.findall(r'^f ', text, re.M)),
                       'sha256': {name: hashlib.sha256(data).hexdigest() for name, data in
                                  [('model.obj', mesh), ('texture.png', texture), ('icon.png', icon)]}}
        modes = ['whole'] if key in WHOLE else ['helmet', 'vest']
        for mode in modes:
            id_ = 'mcmod_' + key + ('' if mode == 'whole' else '_' + mode)
            # Splitting must not accidentally double the original set's protection.
            share = 1 if mode == 'whole' else .2 if mode == 'helmet' else .8
            name = original['name'] + ('' if mode == 'whole' else ' — шлем' if mode == 'helmet' else ' — бронежилет')
            entries.append({'id': id_, 'model': key, 'mode': mode, 'nameRu': name + ' [MCMOD]',
                            'nameEn': key.replace('_', ' ').title() + ' — ' + mode + ' [MCMOD]',
                            'condition': original['condition'], 'sourceProtect': original['protect'],
                            'defence': original['protect'] / 100 * share,
                            'radiation': key == 'officer_ozk', 'sourceType': original['type']})
            write_json(ASSETS / ('models/item/' + id_ + '.json'), {'parent': 'item/generated',
                       'textures': {'layer0': 'metroport:mcmod/icons/' + key}})
    write_json(ASSETS / 'mcmod_armor.json', entries)
    write_json(ROOT / 'docs/mcmod-report.json', {'batch': 1, 'source': 'User-supplied MCMod.jar, unpacked on Google Drive',
        'registeredOriginalArmor': originals, 'importedModels': models, 'items': entries,
        'pendingArmor': [key for key in originals if key not in IMPORTED],
        'remainingContent': ['Other 27 armor models', 'Guns and ammunition', 'Decorative blocks and items',
                             'Original player/inventory/network/condition/radiation mechanics audit'],
        'adaptations': ['Whole five outfits use CHEST; other sets use HEAD + CHEST (first example BKZ).',
                       'Split BKZ assigns 20%/80% of source protection; total stays 60%, not 120%. Each part retains source max condition.',
                       'Condition is represented by normal armor damage and mirrored to condition NBT; original server damage formula still needs audit.',
                       'Officer OZK uses existing port radiation + filter mechanics; not yet claimed identical to MCMOD radiation.',
                       'Original NoDrop tooltip alone does not prove retention implementation; use existing givenodrop command until original events audited.',
                       'Head of a whole outfit is hidden when a separate head-slot item is worn, avoiding duplicate helmets.',
                       'Kamuflyazh interpreted as ghillie (original name Kamuflirovannaya bronya); sniper_camo remains pending.',
                       'All six original OBJ/atlas/icon bytes are preserved; group animation uses original limb transforms.']})
    for locale, name in [('ru_ru', 'nameRu'), ('en_us', 'nameEn')]:
        file = ASSETS / ('lang/' + locale + '.lang')
        lines = [line for line in file.read_text().splitlines() if not line.startswith('item.metroport.mcmod_')]
        lines.extend('item.metroport.' + entry['id'] + '.name=' + entry[name] for entry in entries)
        put(file, '\n'.join(lines) + '\n')
    print('MCMOD batch 1:', len(models), 'armor meshes,', len(entries), 'items;', len(originals)-len(models), 'armor models pending')


if __name__ == '__main__':
    main()
