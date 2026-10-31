#!/usr/bin/env python3
"""Import ExodusRPV3 models into our namespace, without replacing vanilla assets.
Missing original textures have explicit, auditable fallbacks in exodus-report.json.
"""
import copy
import hashlib
import json
import math
import re
import zipfile
from import_content import ROOT, ASSETS, put, write_json

MASKS = {
    'gold_ore': ('exodus_gasmask_gold', 'Противогаз Exodus — золотая руда', 'Exodus gas mask — gold ore'),
    'diamond_ore': ('exodus_gasmask_diamond', 'Противогаз Exodus — алмазная руда', 'Exodus gas mask — diamond ore'),
    'pumpkin': ('exodus_gasmask_pumpkin', 'Противогаз Exodus — тыква', 'Exodus gas mask — pumpkin'),
}
MUTANTS = {
    'lapis_block': ('exodus_shrimp', 'Креветка Exodus', 'Exodus shrimp'),
    'diamond_block': ('exodus_guard', 'Страж Exodus', 'Exodus guard'),
}
# These block overrides are gun models, NOT scenery. Do not turn them into furniture.
WEAPON_BLOCKS = {'black_concrete', 'blue_concrete', 'green_concrete', 'orange_concrete'}
DECOR_NAMES = {
    'acacia_stairs': ('Кровать', 'Bed'),
    'carpet_blue': ('Тело в противогазе', 'Body with gas mask'),
    'carpet_green': ('Поддон с кирпичами', 'Pallet of bricks'),
    'carpet_lime': ('Напольный декор', 'Floor decoration'),
    'carpet_pink': ('Лежащее тело', 'Lying body'),
    'carpet_red': ('Деревянный декор', 'Wooden decoration'),
    'carpet_white': ('Дерево', 'Tree'),
    'cyan_glazed_terracotta': ('Декор — губка и ткань', 'Sponge and fabric decoration'),
    'dispenser': ('Печь', 'Furnace'),
    'dropper': ('Деревянный декор с решёткой', 'Wood and grate decoration'),
    'emerald_block': ('Металлический декор', 'Metal decoration'),
    'emerald_ore': ('Деревянный ящик', 'Wooden crate'),
    'end_portal_frame_empty': ('Печка Exodus', 'Exodus stove'),
    'flower_pot': ('Мешок', 'Sack'),
    'glass_black': ('Каменный декор', 'Stone decoration'),
    'glass_red': ('Деревянный декор с растительностью', 'Wood and vegetation decoration'),
    'quartz_stairs': ('Стул', 'Chair'),
    'sandstone_stairs': ('Забор', 'Fence'),
    'spruce_stairs': ('Каменный декор — еловые ступени', 'Stone decoration — spruce stairs'),
    'wooden_button': ('Электрощиток', 'Fuse box'),
    'wooden_button_pressed': ('Электрощиток — второй вариант', 'Fuse box — second variant'),
    'chest_minecart': ('Ящик Exodus', 'Exodus crate'),
}
# The pack references these files but does not contain them. These are replacements,
# not recovered originals. Vanilla-name redirects are documented separately as well.
FALLBACKS = {
    'blocks2/mags': 'blocks2/gaz_mask11',
    'blocks2/poldekor1': 'minecraft:blocks/planks_oak',
    'blocks2/detail': 'minecraft:blocks/iron_block',
    'blocks2/wood122': 'minecraft:blocks/planks_spruce',
    'blocks2/wood_spruce': 'minecraft:blocks/planks_spruce',
    'blocks2/wool_colored_silver2': 'minecraft:blocks/wool_colored_silver',
    'blocks2/fuse_box': 'minecraft:blocks/iron_block',
    'dop_textures/sack_dirty': 'minecraft:blocks/wool_colored_brown',
    'dop_textures/spruce_planks': 'minecraft:blocks/planks_spruce',
}
VANILLA = set('wool_colored_purple wool_colored_cyan wool_colored_red sponge wool_colored_gray stone_diorite brewing_stand_base hardened_clay_stained_cyan iron_block planks_jungle grass_side_overlay melon_top log_spruce melon_side rail_activator planks_birch'.split())


def clean(path):
    return re.sub(r'[^a-z0-9_./-]', '_', path.lower())


def bounds(elements):
    points = []
    for e in elements:
        rotation = e.get('rotation', {})
        angle = math.radians(rotation.get('angle', 0))
        axis = 'xyz'.index(rotation.get('axis', 'x'))
        origin = rotation.get('origin', [0, 0, 0])
        for bits in range(8):
            p = [e['to' if bits & (1 << i) else 'from'][i] for i in range(3)]
            if angle:
                p = [p[i] - origin[i] for i in range(3)]
                a, b = (axis + 1) % 3, (axis + 2) % 3
                p[a], p[b] = p[a] * math.cos(angle) - p[b] * math.sin(angle), p[a] * math.sin(angle) + p[b] * math.cos(angle)
                if rotation.get('rescale'):
                    p[a] /= math.cos(angle)
                    p[b] /= math.cos(angle)
                p = [p[i] + origin[i] for i in range(3)]
            points.append(p)
    return [round(min(p[i] for p in points) / 16, 7) for i in range(3)] + [round(max(p[i] for p in points) / 16, 7) for i in range(3)]


def main():
    archive = ROOT / 'ExodusRPV3.zip'
    entries, copied, replacements, missing_variables = [], {}, {}, {}
    with zipfile.ZipFile(archive) as z:
        paths = set(z.namelist())

        def texture(value):
            if value.startswith('#'):
                return value
            domain, _, path = value.partition(':')
            if not path:
                domain, path = 'minecraft', domain
            source = 'assets/' + domain + '/textures/' + path + '.png'
            if source not in paths:
                target = FALLBACKS.get(path)
                if target is None and path.startswith('blocks2/') and path.split('/')[-1] in VANILLA:
                    target = 'minecraft:blocks/' + path.split('/')[-1]
                if target is None:
                    raise ValueError('Unresolved Exodus texture: ' + value)
                replacements[value] = target
                # Prefer the pack's version of a vanilla-name fallback when present.
                candidate = target.replace('minecraft:', '')
                candidate_source = 'assets/minecraft/textures/' + candidate + '.png'
                if candidate_source in paths:
                    return texture(candidate)
                if ':' not in target:
                    return texture(target)
                return target
            target = 'exodus/' + clean(path)
            if target in copied and copied[target] != source:
                raise ValueError('Sanitized texture name collision: ' + source)
            copied[target] = source
            put(ASSETS / ('textures/' + target + '.png'), z.read(source))
            if source + '.mcmeta' in paths:
                # Preserve animation metadata, including the guard's animated texture.
                json.loads(z.read(source + '.mcmeta'))
                put(ASSETS / ('textures/' + target + '.png.mcmeta'), z.read(source + '.mcmeta'))
            return 'metroport:' + target

        models = [(n, n.rsplit('/', 1)[-1][:-5]) for n in sorted(paths)
                  if n.startswith('assets/minecraft/models/block/') and n.endswith('.json')]
        models.append(('assets/minecraft/models/item/chest_minecart.json', 'chest_minecart'))
        for source, key in models:
            if key in WEAPON_BLOCKS:
                continue
            if key in MASKS:
                kind, (id_, ru, en) = 'mask', MASKS[key]
            elif key in MUTANTS:
                kind, (id_, ru, en) = 'mutant', MUTANTS[key]
            else:
                kind, id_ = 'decor', 'exodus_' + key
                ru, en = DECOR_NAMES[key]
                ru, en = ru + ' [Exodus: ' + key + ']', en + ' [Exodus: ' + key + ']'
            original = json.loads(z.read(source))
            model = copy.deepcopy(original)
            model['textures'] = {k: texture(v) for k, v in original.get('textures', {}).items()}
            if 'particle' not in model['textures']:
                model['textures']['particle'] = next(iter(model['textures'].values()))
            for element in model.get('elements', []):
                for face in element.get('faces', {}).values():
                    ref = face.get('texture', '')
                    if ref.startswith('#') and ref[1:] not in model['textures']:
                        missing_variables[source + ':' + ref] = model['textures']['particle']
                        model['textures'][ref[1:]] = model['textures']['particle']
            # Imported furniture is non-full and must not lose faces next to other blocks.
            for element in model.get('elements', []):
                for face in element.get('faces', {}).values():
                    face.pop('cullface', None)
            model_path = 'block/exodus/' + key
            write_json(ASSETS / ('models/' + model_path + '.json'), model)
            write_json(ASSETS / ('models/item/' + id_ + '.json'), {'parent': 'metroport:' + model_path})
            if kind == 'decor':
                write_json(ASSETS / ('blockstates/' + id_ + '.json'), {'variants': {
                    'facing=' + facing: {'model': 'metroport:' + model_path, 'y': yaw}
                    for facing, yaw in [('north', 0), ('east', 90), ('south', 180), ('west', 270)]}})
            entries.append({'id': id_, 'kind': kind, 'source': source, 'model': model_path,
                            'ru': ru, 'en': en, 'bounds': bounds(original['elements']),
                            'elements': len(original['elements'])})
    write_json(ASSETS / 'exodus.json', entries)
    write_json(ROOT / 'docs/exodus-report.json', {
        'source': 'ExodusRPV3.zip', 'sha256': hashlib.sha256(archive.read_bytes()).hexdigest(),
        'counts': {kind: sum(e['kind'] == kind for e in entries) for kind in ('mask', 'mutant', 'decor')},
        'entries': entries, 'textures': copied, 'missingTextureReplacements': replacements, 'undefinedFaceTextureReplacements': missing_variables,
        'excludedWeaponBlockOverrides': sorted(WEAPON_BLOCKS),
        'notes': ['All source elements, face UVs, element rotations and item display transforms preserved.',
                  'Vanilla blocks and items are not replaced. All imported assets use metroport namespace.',
                  'The resource pack supplies static geometry only, not creature animations, AI or stats.',
                  'Mutants use standard hostile melee AI with explicitly chosen base-game stats; no natural spawning.',
                  'Masks have no invented damage resistance; 165 durability, existing filter mechanics.',
                  'Furniture collision is a bounding box clipped to its placement cell; protruding geometry does not reserve adjacent cells.']})
    print('Imported Exodus:', len(entries), 'models;', len(copied), 'textures;', len(replacements), 'documented replacements')


if __name__ == '__main__':
    main()
