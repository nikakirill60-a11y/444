#!/usr/bin/env python3
"""All registered MCMOD armor, from downloaded original files + CFR reference, never invented meshes."""
import hashlib
import json
import re
from import_content import ROOT, ASSETS, put, write_json

SOURCE = ROOT / 'tools/mcmod-reference'
WHOLE = {'officer_ozk', 'banditkurtka', 'kurtka', 'plash', 'ghillie'}
ALLOWED_GROUPS = {'head', 'body', 'leftarm', 'rightarm', 'leftleg', 'rightleg'}
# User's in-game identification: fix the three-way mesh/atlas association, not item statistics.
WORN_MODEL_OVERRIDES = {'officer_ozk': 'banditkurtka', 'banditkurtka': 'kurtka', 'kurtka': 'officer_ozk'}
TEXTURES = {'gasmask': 'texp.png', 'cherv': 'tex.png', 'reih': 'tex.png', 'mgu': 'tex.png'}


def decode_obj(data):
    try:
        return data.decode('utf-8-sig'), 'utf-8'
    except UnicodeDecodeError:
        return data.decode('cp1251'), 'windows-1251'


def geometry(text):
    groups, triangles, group = {}, {}, None
    for line in text.splitlines():
        p = line.split('#', 1)[0].split()
        if not p:
            continue
        if p[0] in ('g', 'o'):
            group = p[1]
        elif p[0] == 'f':
            if group not in ALLOWED_GROUPS or len(p) < 4:
                raise ValueError('Invalid armor group/face: ' + str(group))
            groups[group] = groups.get(group, 0) + 1
            triangles[group] = triangles.get(group, 0) + len(p) - 3
    if not groups:
        raise ValueError('Armor has no geometry')
    return groups, triangles


def modes_for(key, groups):
    if key in WHOLE:
        return ['whole']
    return (['helmet'] if 'head' in groups else []) + (['vest'] if set(groups) - {'head'} else [])


def radiation_sources():
    source = (SOURCE / 'java/metro/common/ItemCustomArmor.java').read_text()
    return set(re.findall(r'item.func_77973_b\(\) == RegisterArmor\.(\w+)', source))


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
    radiation = radiation_sources()
    names = json.loads((ROOT / 'tools/mcmod-names.json').read_text())
    for key in sorted(originals):
        original = originals[key]
        mesh = (SOURCE / ('assets/armor/' + key + '/model.obj')).read_bytes()
        runtime = key.lower()
        texture_name = TEXTURES.get(key, 'texture.png')
        icon_name = ('hanzaz' if key == 'hanza' else runtime) + '.png'
        texture = (SOURCE / ('assets/armor/' + key + '/' + texture_name)).read_bytes()
        icon = (SOURCE / ('assets/icons/' + icon_name)).read_bytes()
        if not texture.startswith(b'\x89PNG\r\n\x1a\n') or not icon.startswith(b'\x89PNG\r\n\x1a\n'):
            raise ValueError('Invalid original PNG: ' + key)
        text, encoding = decode_obj(mesh)
        faces_by_group, triangles_by_group = geometry(text)
        groups = sorted(faces_by_group)
        for suffix, data in [('model.obj', mesh), ('texture.png', texture)]:
            put(ASSETS / ('models/mcmod/' + runtime + '/' + suffix) if suffix.endswith('.obj')
                else ASSETS / ('textures/mcmod/armor/' + runtime + '.png'), data)
        put(ASSETS / ('textures/mcmod/icons/' + runtime + '.png'), icon)
        models[key] = {'runtimeId': runtime, 'encoding': encoding, 'sourceTexture': texture_name, 'sourceIcon': icon_name,
                       'facesByGroup': faces_by_group, 'trianglesByGroup': triangles_by_group, 'groups': groups, 'vertices': len(re.findall(r'^v ', text, re.M)),
                       'faces': len(re.findall(r'^f ', text, re.M)),
                       'sha256': {name: hashlib.sha256(data).hexdigest() for name, data in
                                  [('model.obj', mesh), ('texture.png', texture), ('icon.png', icon)]}}
        modes = modes_for(key, groups)
        for mode in modes:
            id_ = 'mcmod_' + runtime + ('' if mode == 'whole' else '_' + mode)
            # Splitting must not accidentally double the original set's protection.
            share = 1 if len(modes) == 1 else .2 if mode == 'helmet' else .8
            name = names[key]['ru'][mode]
            icon_id = 'mcmod/icons/' + runtime if mode == 'whole' else 'mcmod/armor_icons/' + id_
            entries.append({'id': id_, 'model': runtime, 'visualModel': WORN_MODEL_OVERRIDES.get(runtime, runtime), 'sourceId': key, 'mode': mode, 'nameRu': name, 'icon': icon_id,
                            'nameEn': names[key]['en'][mode],
                            'condition': original['condition'], 'sourceProtect': original['protect'],
                            'defence': original['protect'] / 100 * share,
                            'radiation': key in radiation and mode in ('whole', 'helmet'), 'sourceType': original['type']})
            write_json(ASSETS / ('models/item/' + id_ + '.json'), {'parent': 'item/generated',
                       'textures': {'layer0': 'metroport:' + icon_id}})
    write_json(ASSETS / 'mcmod_armor.json', entries)
    write_json(ROOT / 'docs/mcmod-report.json', {'batch': 3, 'source': 'User-supplied MCMod.jar, unpacked on Google Drive',
        'registeredOriginalArmor': originals, 'importedModels': models, 'items': entries,
        'unusedModelFolders': ['boi_armor'], 'wornModelOverrides': WORN_MODEL_OVERRIDES,
        'pendingArmor': [key for key in originals if key not in models],
        'remainingContent': ['Other guns, ammunition and weapon upgrades (base AK-74 imported; see mcmod-ak-report.json)',
                             'Original player/inventory/network/condition/radiation mechanics audit'],
        'adaptations': ['User-verified visual correction: officer OZK uses banditkurtka mesh+atlas, bandit jacket uses kurtka, jacket uses officer_ozk. Item ids, slots, condition and protection are unchanged. Separate helmet/vest icons are now rendered from real source groups.',
                       'Five whole outfits stay CHEST; 25 other sets use HEAD + CHEST. Gasmask has only HEAD; kitel/vatnik only CHEST, based on non-empty source groups.',
                       'Split sets assign 20%/80% of source protection to helmet/vest, without doubling. Single-part items retain 100% of source protection. Each part retains source max condition.',
                       'Condition is represented by normal armor damage and mirrored to condition NBT; original server damage formula still needs audit.',
                       'Original radiation flags enable port filters on the corresponding helmet (or whole officer OZK); split vests alone do not provide breathing protection. Original MCMOD radiation formulas still need audit.',
                       'Original NoDrop tooltip alone does not prove retention implementation; use existing givenodrop command until original events audited.',
                       'Head of a whole outfit is hidden when a separate head-slot item is worn, avoiding duplicate helmets.',
                       'The five whole outfits are unchanged; sniper_camo is a separate original set and uses HEAD + CHEST.',
                       'BTBKZ uses its own supplied atlas instead of the original loader typo referencing BKZ texture. Hanza uses the registered hanzaz icon.',
                       'Raw OBJ bytes stay unchanged, including windows-1251 material labels in gasmask; both encodings are supported.',
                       'All 33 original OBJ/atlas/icon triples are preserved; group animation uses original limb transforms.']})
    from mcmod_languages import update
    update()
    print('MCMOD armor:', len(models), 'armor meshes,', len(entries), 'items;', len(originals)-len(models), 'armor models pending')


if __name__ == '__main__':
    main()
