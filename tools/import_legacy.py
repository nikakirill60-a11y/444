#!/usr/bin/env python3
"""Recover geometric data and additional resources from the supplied legacy content.
The reference Java is parsed as DATA: never eval/execute decompiled Java.
"""
import collections
import io
import json
from pathlib import Path
import re
import zipfile
from import_content import ROOT, ASSETS, put, write_json, parse, decode, identifier

REFERENCE = ROOT / 'tools/legacy-reference'


def numbers(text):
    values = []
    for token in text.split(','):
        token = token.strip()
        token = token.replace('(float)', '').replace('Math.PI', '3.141592653589793')
        token = re.sub(r'(?<=\d)[fFdD]\b', '', token)
        import ast
        import operator
        operators = {ast.Add: operator.add, ast.Sub: operator.sub, ast.Mult: operator.mul, ast.Div: operator.truediv}
        def literal(node):
            if isinstance(node, ast.Constant) and isinstance(node.value, (int, float)):
                return node.value
            if isinstance(node, ast.UnaryOp) and isinstance(node.op, (ast.UAdd, ast.USub)):
                return literal(node.operand) * (-1 if isinstance(node.op, ast.USub) else 1)
            if isinstance(node, ast.BinOp) and type(node.op) in operators:
                return operators[type(node.op)](literal(node.left), literal(node.right))
            raise ValueError('Nonliteral geometry parameter: ' + token)
        values.append(float(literal(ast.parse(token, mode='eval').body)))
    return values


def geometry(path):
    text = path.read_text(encoding='utf-8')
    sizes = {k: int(v) for k, v in re.findall(r'int (texture[XY]) = (\d+);', text)}
    parts = {}
    current = None
    globals_ = []
    missing = set()
    for line in text.splitlines():
        # Split constructor helper methods contain the same literal statements.
        m = re.search(r'this\.(\w+)\[(\d+)\] = new ModelRendererTurbo\((.*)\);', line)
        if m:
            group, index, args = m.groups()
            # "this" may be cast to ModelBase; the remaining four arguments are numeric or textureX/Y.
            args = args.split(',', 1)[1].replace('this.textureX', str(sizes.get('textureX', 64))).replace('this.textureY', str(sizes.get('textureY', 32)))
            uv = numbers(args)
            parts[group, int(index)] = {'group': group, 'index': int(index), 'uv': uv, 'ops': []}
            continue
        m = re.search(r'this\.(\w+)\[(\d+)\]\.(\w+)\((.*)\);', line)
        if m:
            group, index, method, args = m.groups()
            part = parts[group, int(index)]
            if method == 'addObj':
                missing.add(args.strip('"'))
                continue
            if method == 'setDefaultTexture':
                continue
            method = {'func_78790_a': 'box', 'addShapeBox': 'shape', 'func_78793_a': 'point'}.get(method, method)
            if method not in ('box', 'shape', 'point'):
                raise ValueError('Unsupported geometry method: ' + method)
            part['ops'].append({'op': method, 'values': numbers(args)})
            continue
        m = re.search(r'this\.(\w+)\[(\d+)\]\.(field_\d+_\w+) = ([^;]+);', line)
        if m:
            group, index, field, value = m.groups()
            names = {'field_78795_f': 'rx', 'field_78796_g': 'ry', 'field_78808_h': 'rz',
                     'field_78800_c': 'px', 'field_78797_d': 'py', 'field_78798_e': 'pz'}
            if field not in names:
                raise ValueError('Unsupported geometry field: ' + field)
            parts[group, int(index)]['ops'].append({'op': names[field], 'values': numbers(value)})
            continue
        m = re.search(r'this\.(flipAll|translateAll|translateWeapon|translateShoot)\((.*?)\);', line)
        if m:
            method, args = m.groups()
            globals_.append({'op': method, 'values': numbers(args) if args else []})
    # Muzzle flash OBJ files referenced by the old classes are NOT in any supplied archive.
    result = {'parts': [p for p in parts.values() if p['ops']], 'transforms': globals_}
    expected = len(re.findall(r'\.(?:addShapeBox|func_78790_a)\(', text))
    actual = sum(o['op'] in ('box', 'shape') for p in result['parts'] for o in p['ops'])
    if expected != actual:
        raise ValueError(f'Incomplete geometry {path}: {actual}/{expected}')
    return result, missing


def main():
    outer = zipfile.ZipFile(ROOT / 'Flan-20261030T152711Z-1-001.zip')
    pack = zipfile.ZipFile(io.BytesIO(outer.read('Flan/MetroCraft -Pack-1.7.10-4.10.0.jar')))
    paths = {n.lower(): n for n in pack.namelist()}
    catalogue = {}
    unavailable = set()
    for name in sorted(pack.namelist()):
        if not name.endswith('.txt') or not name.startswith(('guns/', 'armorFiles/', 'attachments/')):
            continue
        d = parse(decode(pack.read(name)))
        key = identifier(d['ShortName'])
        model = d['Model'].split('.')[-1]
        path = REFERENCE / ('com/flansmod/client/model/MetroCraft/Model' + model + '.java')
        data, missing = geometry(path)
        unavailable.update(missing)
        put(ASSETS / ('geometry/' + key + '.json'), json.dumps(data, separators=(',', ':')) + '\n')
        texture = d.get('ArmourTexture') if name.startswith('armorFiles/') else d.get('Texture')
        source = ('assets/flansmod/armor/' + texture + '_1.png') if name.startswith('armorFiles/') else ('assets/flansmod/skins/' + texture + '.png')
        actual = paths.get(source.lower())
        if not actual:
            raise ValueError('Missing model texture ' + source)
        put(ASSETS / ('textures/legacy/' + key + '.png'), pack.read(actual))
        catalogue[key] = {'geometry': key, 'texture': 'metroport:textures/legacy/' + key + '.png',
                          'scale': float(d.get('ModelScale', '1')), 'sourceModel': path.name,
                          'kind': 'armor' if name.startswith('armorFiles/') else 'gun' if name.startswith('guns/') else 'attachment'}
    write_json(ASSETS / 'geometry/catalogue.json', catalogue)
    # Block names/texture indices match the 58 registrations in the original Main.preLoad.
    with zipfile.ZipFile(ROOT / 'StilianMetro.jar') as archive:
        for index in range(58):
            key = 'block' + str(index)
            put(ASSETS / ('textures/blocks/' + key + '.png'), archive.read('assets/stilmetro/textures/blocks/' + str(index) + '.png'))
            write_json(ASSETS / ('blockstates/' + key + '.json'), {'variants': {'normal': {'model': 'metroport:' + key}}})
            write_json(ASSETS / ('models/block/' + key + '.json'), {'parent': 'block/cube_all', 'textures': {'all': 'metroport:blocks/' + key}})
            write_json(ASSETS / ('models/item/' + key + '.json'), {'parent': 'metroport:block/' + key})
        put(ASSETS / 'textures/items/testpickaxe.png', archive.read('assets/stilmetro/textures/items/Pix.png'))
    with zipfile.ZipFile(ROOT / '(Metro) Flans-v1.1.jar') as archive:
        put(ASSETS / 'textures/items/filter.png', archive.read('assets/flansmod/textures/items/filter.png'))
    write_json(ASSETS / 'models/item/radiation.json', {'parent': 'item/generated', 'textures': {'layer0': 'minecraft:items/barrier'}})
    write_json(ASSETS / 'blockstates/radiation.json', {'variants': {'normal': {'model': 'metroport:block0'}}})
    for key in ('filter', 'testpickaxe'):
        write_json(ASSETS / ('models/item/' + key + '.json'), {'parent': 'item/generated', 'textures': {'layer0': 'metroport:items/' + key}})
    write_json(ROOT / 'docs/geometry-report.json', {'models': len(catalogue),
        'boxes': sum(sum(o['op'] in ('box', 'shape') for p in json.loads((ASSETS / ('geometry/' + key + '.json')).read_text())['parts'] for o in p['ops']) for key in catalogue),
        'externalAssetsNotSupplied': sorted(unavailable), 'decorativeBlocks': 58})
    print('Recovered', len(catalogue), 'models and 58 decorative blocks')


if __name__ == '__main__':
    main()
