#!/usr/bin/env python3
"""Deterministically import the supplied 1.7.10 pack; never copy executable classes."""
import io
import json
from pathlib import Path
import re
import zipfile

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'src/main/resources'
ASSETS = RES / 'assets/metroport'
PACK = ROOT / 'Flan-20261030T152711Z-1-001.zip'


def parse(text):
    data = {}
    for line in text.splitlines():
        line = line.split('//', 1)[0].strip()
        fields = line.split(None, 1)
        if len(fields) == 2:
            data[fields[0]] = fields[1].strip()
    return data


def decode(raw):
    try:
        return raw.decode('utf-8-sig')
    except UnicodeDecodeError:
        return raw.decode('cp1251')


def identifier(value):
    return re.sub(r'[^a-z0-9_]', '_', value.lower())


def put(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    if isinstance(value, bytes):
        path.write_bytes(value)
    else:
        path.write_text(value, encoding='utf-8')


def write_json(path, value):
    put(path, json.dumps(value, ensure_ascii=False, indent=2) + '\n')


def main():
    outer = zipfile.ZipFile(PACK)
    pack = zipfile.ZipFile(io.BytesIO(outer.read('Flan/MetroCraft -Pack-1.7.10-4.10.0.jar')))
    paths = {n.lower(): n for n in pack.namelist()}
    ru = parse_lang(decode(pack.read('assets/flansmod/lang/ru_RU.lang')))
    en = parse_lang(decode(pack.read('assets/flansmod/lang/en_US.lang')))
    entries, sounds = [], {}
    folders = {'guns': 'gun', 'armorFiles': 'armor', 'bullets': 'ammo',
               'attachments': 'attachment', 'parts': 'part', 'tools': 'tool'}
    for folder, kind in folders.items():
        for name in sorted(pack.namelist()):
            if not name.startswith(folder + '/') or not name.endswith('.txt'):
                continue
            d = parse(decode(pack.read(name)))
            short = d['ShortName']
            key = identifier(short)
            entry = {'id': key, 'source': name, 'shortName': short, 'kind': kind,
                     'name': d.get('Name', short), 'stackSize': int(d.get('StackSize', d.get('MaxStackSize', '64'))),
                     'description': d.get('Description', '').replace('_', '\n'),
                     'durability': int(d.get('Durability', '0'))}
            icon = paths.get(('assets/flansmod/textures/items/' + d['Icon'] + '.png').lower())
            if icon is None:
                raise ValueError('Missing icon: ' + name)
            put(ASSETS / ('textures/items/' + key + '.png'), pack.read(icon))
            write_json(ASSETS / ('models/item/' + key + '.json'), {
                'parent': 'item/handheld' if kind == 'gun' else 'item/generated',
                'textures': {'layer0': 'metroport:items/' + key}})
            if kind == 'gun':
                entry.update(ammo=identifier(d['Ammo']), damage=float(d['Damage']),
                             capacity=int(d['LoadIntoGun']), reloadTicks=int(d['ReloadTime']),
                             delay=int(d['ShootDelay']), pellets=int(d.get('NumBullets', '1')),
                             spread=float(d.get('Accuracy', '0')), automatic=d.get('Mode') == 'FullAuto')
                for source_key, target in [('ShootSound', 'shootSound'), ('ReloadSound', 'reloadSound')]:
                    source = d.get(source_key)
                    if source:
                        sound = paths.get(('assets/flansmod/sounds/' + source + '.ogg').lower())
                        if sound is None:
                            raise ValueError('Missing sound: ' + source)
                        sid = identifier(source)
                        put(ASSETS / ('sounds/' + sid + '.ogg'), pack.read(sound))
                        sounds[sid] = {'sounds': ['metroport:' + sid]}
                        entry[target] = sid
            elif kind == 'armor':
                entry.update(slot='head' if d['Type'] == 'Helmet' else 'chest',
                             defence=float(d.get('Defence', '0')),
                             nightVision=d.get('NightVision', '').lower() == 'true',
                             gasMask=d.get('GasMask', '').lower() == 'true')
            elif kind == 'tool':
                entry.update(heal=float(d.get('HealAmount', '0')), uses=int(d.get('ToolLife', '1')),
                             repairGun=d.get('CanRepair', '').lower() == 'true',
                             repairArmor=d.get('CanArmorRepair', '').lower() == 'true')
                # Legacy repair descriptions refer to an incompatible GUI; the port uses the offhand.
                entry['description'] = ''
            entries.append(entry)
    ids = [e['id'] for e in entries]
    if len(set(ids)) != len(ids):
        raise ValueError('Duplicate registry IDs')
    write_json(ASSETS / 'content.json', entries)
    write_json(ASSETS / 'sounds.json', sounds)
    translations = {
        'ru_ru': ['itemGroup.metroport.weapons=Оружие', 'itemGroup.metroport.armor=Броня',
                  'itemGroup.metroport.things=Вещи', 'key.metroport.reload=Перезарядить',
                  'key.categories.metroport=MetroCraft',
                  'metroport.gun.help=ПКМ — огонь; R или Shift+ПКМ — перезарядка',
                  'metroport.gun.ammo=Патроны: %s / %s',
                  'metroport.gun.loading=Перезарядка…',
                  'metroport.tool.heal=ПКМ — восстановить здоровье',
                  'metroport.tool.repair=ПКМ — починить предмет во второй руке',
                  'metroport.attachment.info=Модуль: установка на оружие пока не перенесена',
                  'metroport.armor.gas=Защита от радиации пока не перенесена'],
        'en_us': ['itemGroup.metroport.weapons=Weapons', 'itemGroup.metroport.armor=Armor',
                  'itemGroup.metroport.things=Things', 'key.metroport.reload=Reload',
                  'key.categories.metroport=MetroCraft',
                  'metroport.gun.help=Right click: fire; R or Sneak + right click: reload',
                  'metroport.gun.ammo=Ammo: %s / %s', 'metroport.gun.loading=Reloading…',
                  'metroport.tool.heal=Right click to heal',
                  'metroport.tool.repair=Right click to repair the item in your offhand',
                  'metroport.attachment.info=Attachment: installation is not ported yet',
                  'metroport.armor.gas=Radiation protection is not ported yet']}
    for e in entries:
        legacy_key = 'item.' + e['shortName'] + '.name'
        translations['ru_ru'].append('item.metroport.' + e['id'] + '.name=' + ru.get(legacy_key, e['name']))
        translations['en_us'].append('item.metroport.' + e['id'] + '.name=' + en.get(legacy_key, e['name']))
    for locale, lines in translations.items():
        put(ASSETS / ('lang/' + locale + '.lang'), '\n'.join(lines) + '\n')
    # Preserve the license shipped with the original Flan code/assets.
    with zipfile.ZipFile(ROOT / '(Metro) Flans-v1.1.jar') as original:
        put(RES / 'LICENSE-original-Flans.txt', original.read('LICENSE.txt'))
    report = {kind: sum(e['kind'] == kind for e in entries) for kind in folders.values()}
    write_json(ROOT / 'docs/import-report.json', {'counts': report, 'total': len(entries),
        'source': 'Flan/MetroCraft -Pack-1.7.10-4.10.0.jar',
        'rendering': 'Original inventory icons; generated flat item models; vanilla armor geometry/texture.',
        'notPorted': ['Legacy 3D models', 'Attachment installation', 'Radiation and filters',
                      'Custom inventory and backpacks', 'Mobs and corpses', 'Decorative blocks and doors']})
    print('Imported', len(entries), 'entries:', report)


def parse_lang(text):
    return dict(line.split('=', 1) for line in text.splitlines()
                if '=' in line and not line.lstrip().startswith('#'))


if __name__ == '__main__':
    main()
