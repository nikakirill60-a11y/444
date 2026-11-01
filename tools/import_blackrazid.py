#!/usr/bin/env python3
"""Data-only migration of the supplied BlackRazid models. Never execute recovered Java.
All box/UV/joint literals come from CFR reference. Existing Metro content is untouched.
"""
import hashlib
import json
import re
from pathlib import Path
from import_content import ROOT, ASSETS, put, write_json
from import_legacy import numbers

SOURCE = ROOT / 'tools/blackrazid-reference'
JAVA = SOURCE / 'java/mods/provoda'
JOINTS = dict(zip('cdefghi', ['head', 'headwear', 'body', 'rightarm', 'leftarm', 'rightleg', 'leftleg']))
# Registration -> model selected by the ORIGINAL ClientProxy, not similarly named unused models.
MODELS = {
    'Himza': 'him/models/HimCheest', 'Heavy': 'heavy/models/HeavyChest',
    'Clean': 'clean/models/Clean', 'CClean': 'cclean/models/CCleanModel',
    'Normal': 'normal/models/ModelNormal', 'Kozh': 'kozh/ModelKozh',
    'Easy': 'easy/ModelEasy', 'Heavy1': 'heavy1/ModelHeavy1', 'Sparta': 'sparta/ModelSparta',
    'HeavyHat': 'hats/ModelHeavyHat', 'NaziHat': 'hats/ModelNaziHat', 'Heavy1Hat': 'hats/ModelHeavy1Hat',
    'Red': 'red/ModelRed', 'Nazi': 'nazi/ModelNazi', 'gas_mask': 'gasmask/models/Protivogas',
    'BackPackSmall': 'backpacksmall/ModelSmallBackPack',
    'BackPackMedium': 'backpackmedium/ModelBackPackMedium', 'BackPackLarge': 'backpacklarge/ModelBackPackLarge',
}
ITEMS = {
    'Himza': 'him/HimArmor', 'Heavy': 'heavy/HeavyArmor', 'Clean': 'clean/Clean',
    'CClean': 'cclean/CClean', 'Normal': 'normal/Normal', 'Kozh': 'kozh/Kozh',
    'Easy': 'easy/Easy', 'Heavy1': 'heavy1/Heavy1', 'Sparta': 'sparta/Sparta',
    'HeavyHat': 'hats/HeavyHat', 'NaziHat': 'hats/NaziHat', 'Heavy1Hat': 'hats/Heavy1Hat',
    'Red': 'red/ArmorRed', 'Nazi': 'nazi/ArmorNazi', 'gas_mask': 'gasmask/GasMask',
}
EN = ['Chemical protection suit', 'Heavy armor (reinforced)', 'Clean Wind', 'Clean Wind +',
      'Heavy armor', 'Leather armor', 'Light armor', 'Heavy armor (assault)', 'Spartan armor',
      'Armored gas mask', 'Fourth Reich gas mask', 'Protective helmet', 'Red Line armor',
      'Fourth Reich armor', 'PPM-88 gas mask', 'Small backpack', 'Medium backpack', 'Large backpack']


def point_updates(text):
    return {key: numbers(args) for key, args in re.findall(r'this\.(\w+)\.a\(([-\d.f, ]+)\);', text)
            if len(numbers(args)) == 3}


def braced(text, pos):
    start = text.index('{', pos)
    depth = 1
    for end in range(start+1, len(text)):
        depth += (text[end] == '{') - (text[end] == '}')
        if depth == 0:
            return text[start+1:end], end+1
    raise ValueError('Unbalanced source block')


def geometry(path):
    text = path.read_text()
    constructor_pos = text.index('    public '+path.stem+'(')
    body, end = braced(text, constructor_pos)
    # Field initializers precede the constructor, and are semantically part of construction.
    literal = text[:constructor_pos] + body
    dims = [int(x) for x in re.search(r'super\(var1, 0.0f, (\d+), (\d+)\)', body).groups()]
    parts = {}
    for name, u, v in re.findall(r'(?:this\.)?(\w+) = new bdi\(\(bcd\)this, (\d+), (\d+)\);', literal):
        parts[name] = dict(name=name, uv=[int(u), int(v)], size=dims[:], point=[0, 0, 0], rotation=[0, 0, 0], boxes=[], mirror=False, parents=[])
    for line in literal.splitlines():
        m = re.search(r'this\.(\w+)\.a\(([-\d.f, ]+)\);', line)
        if m and m[1] in parts:
            vals = numbers(m[2]); p = parts[m[1]]
            if len(vals) == 3: p['point'] = vals
            elif len(vals) in (6, 7): p['boxes'].append({'values': vals, 'mirror': p['mirror']})
            else: raise ValueError('Unsupported model operation: '+line)
        m = re.search(r'this\.(\w+)\.b\((\d+), (\d+)\)', line)
        if m and m[1] in parts: parts[m[1]]['size'] = [int(m[2]), int(m[3])]
        m = re.search(r'this\.(\w+)\.i = (true|false)', line)
        if m and m[1] in parts: parts[m[1]]['mirror'] = m[2] == 'true'
        m = re.search(r'this.setRotation\(this\.(\w+), (.*?)\);', line)
        if m: parts[m[1]]['rotation'] = numbers(m[2])
        m = re.search(r'this\.(\w+)\.a\(this\.(\w+)\);', line)
        if m:
            parent = JOINTS.get(m[1], m[1]); p = parts[m[2]]
            if parent not in p['parents']: p['parents'].append(parent) # Native source sometimes adds the SAME child twice.
    # Preserve source rest and armed shoulder-pad offsets without importing old item IDs.
    tail = text[end:]; armed, rest = {}, {}
    gun = tail.find('if (p.cd() != null')
    if gun >= 0:
        block, e = braced(tail, gun); armed = point_updates(block)
        if re.match(r'\s*else\s*{', tail[e:]):
            block, e = braced(tail, e); rest = point_updates(block)
        # Remaining unconditional updates (not GL-only rendering) apply in both states.
        for key, val in point_updates(tail[e:]).items():
            if key not in rest: rest[key] = val
            if key not in armed: armed[key] = val
    for p in parts.values():
        if not p['boxes']: raise ValueError('Unparsed box: '+p['name'])
        if not p['parents']:
            # Some old sources render a free-standing Shape1 manually using GL.
            # Attach it once to the body instead of reproducing double rendering.

            if re.search(r'this\.'+p['name']+r'\.a\(0\.0625f\)', tail):
                p['parents'] = ['body']; p['recoveredFreePart'] = True
            else: p['unused'] = True
        # A source detail attached to two DIFFERENT limbs is intentionally rendered on both.
    return dict(textureSize=dims, inflation=1, parts=list(parts.values()), armed=armed, rest=rest)


def main():
    registration = (JAVA/'ModBase.java').read_text()
    names = {key: bytes(name, 'ascii').decode('unicode_escape') for key, name in
             re.findall(r'LanguageRegistry.addName\(\(Object\)(\w+), \(String\)"([^"]+)"\)', registration)}
    entries = []
    for (key, model), en in zip(MODELS.items(), EN):
        bag = key.startswith('BackPack')
        item_path = JAVA/('items/'+key+'.java' if bag else 'items/armor/'+ITEMS[key]+'.java')
        item_source = item_path.read_text()
        texture = re.search(r'/mods/provoda/textures/armor/([\w]+)\.png', item_source)[1]
        icon = re.search(r'par1IconRegister.a\("provoda:(\w+)"\)', item_source)
        icon = icon[1] if icon else 'gas_mask_generated'
        data = geometry(JAVA/('items/armor/'+model+'.java'))
        id_ = 'blackrazid_'+re.sub(r'(?<!^)(?=[A-Z])', '_', key).lower()
        mode = 'backpack' if bag else 'helmet' if key in ('HeavyHat', 'NaziHat', 'Heavy1Hat', 'gas_mask') else 'whole'
        # Source reduction points are recorded, not its old over-cap protection formula.
        material = re.search(r'\b'+key+r' = new \w+\(\d+, (\w+), 1, [012]\)', registration)[1]
        source_points = [int(x) for x in re.search(r'\b'+material+r' = EnumHelper.addArmorMaterial\([^\n]+new int\[\]\{([^}]+)', registration)[1].split(',')]
        source_protect = source_points[0 if mode=='helmet' else 1]
        slots = 5 * (['BackPackSmall', 'BackPackMedium', 'BackPackLarge'].index(key)+1) if bag else 0
        entry = dict(id=id_, sourceId=key, model=id_, mode=mode, nameRu={'Heavy':'Тяжёлая усиленная броня','Heavy1':'Тяжёлая штурмовая броня','Kozh':'Кожаная броня'}.get(key,names[key]), sourceNameRu=names[key], nameEn=en,
                     texture=texture, icon=icon, condition=int(re.search(r'this.e\((\d+)\)', item_source)[1]),
                     sourceArmorPoints=source_protect, defence=round(min(.8, source_protect*.04), 3),
                     gasMask=key in ('gas_mask', 'HeavyHat', 'NaziHat', 'Himza', 'Clean'), storageSlots=slots,
                     sourceModel='items/armor/'+model+'.java')
        entries.append(entry)
        write_json(ASSETS/('geometry/blackrazid/'+id_+'.json'), data)
        put(ASSETS/('textures/blackrazid/armor/'+texture+'.png'), (SOURCE/('assets/armor/'+texture+'.png')).read_bytes())
        if icon != 'gas_mask_generated':
            put(ASSETS/('textures/blackrazid/icons/'+icon+'.png'), (SOURCE/('assets/icons/'+icon+'.png')).read_bytes())
        write_json(ASSETS/('models/item/'+id_+'.json'), {'parent': 'item/generated', 'textures': {'layer0': 'metroport:blackrazid/icons/'+icon}})
    write_json(ASSETS/'blackrazid_equipment.json', entries)
    from mcmod_languages import update
    update()
    write_json(ROOT/'docs/blackrazid-report.json', dict(sourceFolder='1u3snbncRSvARWyceeoHUilIw2MTdOALv', items=entries,
        armor=15, backpacks=3, registeredButUnimported=[], adaptations=[
            '15 armor items retain original one-slot registration and native box/UV/joint data.',
            'Standalone/multiply attached native details rendered once, following the body instead of manual duplicate GL rendering.',
            'Native 5/10/15 item-bound storage in the existing separate backpack slot; does not unlock additional player inventory rows.',
            'Armor protection uses current port conventions (0.04 per original armor point, capped at 0.8), not old formulas.',
            'PPM-88 icon rendered from recovered geometry/atlas: source GasMask registers no icon.',
            'Other worn head equipment hides built-in whole-outfit head mesh; existing sealed-armor rules unchanged.'
        ]))
    print('Imported', len(entries), 'BlackRazid equipment items')

if __name__ == '__main__': main()
