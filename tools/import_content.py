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
                             spread=float(d.get('Accuracy', '0')), automatic=d.get('Mode') == 'FullAuto',
                             allowedAttachments=[identifier(a) for a in d.get('AllowAttachments', '').split()])
                entry.update(bulletSpeed=float(d.get('BulletSpeed','40')), recoil=float(d.get('Recoil','0')),
                             randomRecoil=float(d.get('RandomRecoilRange','2.5')), randomYaw=float(d.get('RandomRecoilYawRange','1')),
                             decreaseRecoil=float(d.get('DecreaseRecoil','2')), decreaseYaw=float(d.get('DecreaseRecoilYaw','1.5')),
                             scope=d.get('Scope','None'))
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
            elif kind == 'attachment':
                entry.update(attachmentSlot={'sights': 'scope'}.get(d.get('AttachmentType'), d.get('AttachmentType', 'generic')),
                             damageMultiplier=float(d.get('DamageMultiplier', '1')),
                             spreadMultiplier=float(d.get('SpreadMultiplier', '1')),
                             reloadMultiplier=float(d.get('ReloadTimeMultiplier', '1')),
                             zoom=float(d.get('ZoomLevel', '1')) * float(d.get('FOVZoomLevel', '1')),
                             recoilMultiplier=float(d.get('RecoilMultiplier','1')),
                             bulletSpeedMultiplier=float(d.get('BulletSpeedMultiplier','1')),
                             silencer=d.get('Silencer','false').lower()=='true', zoomOverlay=d.get('ZoomOverlay','None'))
            elif kind == 'armor':
                entry.update(slot='head' if d['Type'] == 'Helmet' else 'chest',
                             defence=float(d.get('Defence', '0')),
                             nightVision=d.get('NightVision', '').lower() == 'true',
                             gasMask=d.get('GasMask', '').lower() == 'true',
                             moveSpeedModifier=float(d.get('MoveSpeedModifier',d.get('Slowness','1'))),
                             knockbackModifier=float(d.get('KnockbackModifier',d.get('KnockbackReduction','.2'))),
                             jumpModifier=float(d.get('JumpModifier','1')),
                             negateFallDamage=d.get('NegateFallDamage','false').lower()=='true')
            elif kind == 'tool':
                entry.update(heal=float(d.get('HealAmount', '0')), uses=int(d.get('ToolLife', '1')),
                             repairGun=d.get('CanRepair', '').lower() == 'true',
                             repairArmor=d.get('CanArmorRepair', '').lower() == 'true')
                # Legacy repair descriptions refer to an incompatible GUI; the port uses the offhand.
                entry['description'] = ''
            if kind == 'ammo':
                entry.update(fallSpeed=float(d.get('FallSpeed','.25')), hitBoxSize=float(d.get('HitBoxSize','.5')),
                             penetrates=d.get('Penetrates','false').lower()=='true',
                             breaksGlass=d.get('BreaksGlass','false').lower()=='true')
            entries.append(entry)
    ids = [e['id'] for e in entries]
    if len(set(ids)) != len(ids):
        raise ValueError('Duplicate registry IDs')
    write_json(ASSETS / 'content.json', entries)
    sounds['germo'] = {'sounds': ['metroport:germo']}
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
                  'metroport.attachment.info=Оружие во второй руке: ПКМ — установить; Shift+ПКМ — снять',
                  'metroport.armor.gas=Защита от радиации пока не перенесена'],
        'en_us': ['itemGroup.metroport.weapons=Weapons', 'itemGroup.metroport.armor=Armor',
                  'itemGroup.metroport.things=Things', 'key.metroport.reload=Reload',
                  'key.categories.metroport=MetroCraft',
                  'metroport.gun.help=Right click: fire; R or Sneak + right click: reload',
                  'metroport.gun.ammo=Ammo: %s / %s', 'metroport.gun.loading=Reloading…',
                  'metroport.tool.heal=Right click to heal',
                  'metroport.tool.repair=Right click to repair the item in your offhand',
                  'metroport.attachment.info=Gun in offhand: right click to install; sneak + right click to remove',
                  'metroport.armor.gas=Radiation protection is not ported yet']}
    for locale, language in [('ru_ru', 'ru'), ('en_us', 'en')]:
        for key, label in {'kris': 'Паук', 'murzik': 'Мурзик', 'tuzic': 'Тузик', 'jery': 'Джери', 'trup1': 'Труп 1', 'trup2': 'Труп 2', 'trup3': 'Труп 3', 'trup4': 'Труп 4', 'komp': 'Компьютер', 'grib': 'Гриб'}.items():
            translations[locale].append('entity.metroport.' + key + '.name=' + (label if language == 'ru' else key.title()))
        extra = {'smallrucksack': 'Маленький рюкзак' if language == 'ru' else 'Small backpack',
                 'mediumrucksack': 'Средний рюкзак' if language == 'ru' else 'Medium backpack',
                 'bigrucksack': 'Большой рюкзак' if language == 'ru' else 'Big backpack',
                 'filter': 'Фильтр (5 минут)' if language == 'ru' else 'Filter (5 minutes)',
                 'testpickaxe': 'Кирка Metro' if language == 'ru' else 'Metro pickaxe'}
        for key, name in extra.items():
            translations[locale].append('item.metroport.' + key + '.name=' + name)
        for index in range(58):
            translations[locale].append('tile.metroport.block' + str(index) + '.name=' + ('Блок Metro ' if language == 'ru' else 'Metro block ') + str(index))
        translations[locale].append('tile.metroport.radiation.name=' + ('Радиация (источник)' if language == 'ru' else 'Radiation source'))
        translations[locale].append('metroport.armor.filter=' + ('Фильтр: %s сек. ПКМ с фильтром — заменить' if language == 'ru' else 'Filter: %s sec. Right click with a filter to replace'))
        translations[locale].append('key.metroport.aim=' + ('Прицеливание (удерживать)' if language == 'ru' else 'Aim (hold)'))
        translations[locale].append('death.attack.metroRadiation=' + ('%s погиб от радиации' if language == 'ru' else '%s died from radiation'))
    for e in entries:
        legacy_key = 'item.' + e['shortName'] + '.name'
        translations['ru_ru'].append('item.metroport.' + e['id'] + '.name=' + ru.get(legacy_key, e['name']))
        translations['en_us'].append('item.metroport.' + e['id'] + '.name=' + en.get(legacy_key, e['name']))
    gate_labels = [('germo', 'Гермоворота — подъёмные', 'Hermetic gate — lift'), ('germo_open', 'Гермоворота — открытые', 'Hermetic gate — open'), ('germo_right', 'Гермоворота — правые', 'Hermetic gate — right'), ('germo_left', 'Гермоворота — левые', 'Hermetic gate — left'), ('germo_left45', 'Гермоворота — левые 45°', 'Hermetic gate — left 45°'), ('germo_half_open', 'Гермоворота — распашные', 'Hermetic gate — hinged')]
    for key, ru_name, en_name in gate_labels:
        translations["ru_ru"].append("tile.metroport." + key + ".name=" + ru_name)
        translations["en_us"].append("tile.metroport." + key + ".name=" + en_name)
    translations["ru_ru"].append("metroport.gate.obstructed=Для ворот и движения створки нужно свободное место. См. инструкцию.")
    translations["en_us"].append("metroport.gate.obstructed=The gate and its movement pocket need empty space. See the instructions.")
    translations["ru_ru"].append("death.attack.metrobullet=%1$s застрелен игроком %2$s")
    translations["en_us"].append("death.attack.metrobullet=%1$s was shot by %2$s")
    for locale, lines in translations.items():
        put(ASSETS / ('lang/' + locale + '.lang'), '\n'.join(lines) + '\n')
    # Preserve the license shipped with the original Flan code/assets.
    with zipfile.ZipFile(ROOT / '(Metro) Flans-v1.1.jar') as original:
        put(RES / 'LICENSE-original-Flans.txt', original.read('LICENSE.txt'))
    report = {kind: sum(e['kind'] == kind for e in entries) for kind in folders.values()}
    write_json(ROOT / 'docs/import-report.json', {'counts': report, 'total': len(entries),
        'source': 'Flan/MetroCraft -Pack-1.7.10-4.10.0.jar',
        'rendering': 'Original GUI icons; original Turbo geometry and atlases through client renderers; geometry/OBJ integrity tested, visual alignment still needs in-game verification.',
        'expandedContent': {'decorativeBlocks': 58, 'radiationSources': 1, 'filters': 1, 'pickaxes': 1, 'backpacks': 3, 'entityTypes': 10, 'hermeticGates': 6},
        'notPorted': ['Original missing LastDays muzzle-flash meshes',
                      'Independent hand/magazine geometry not supplied by the models',
                      'Full 1.7 player bone hitboxes/lag compensation and cross-mod vehicle damage',
                      'Unknown third-party inventory slot contracts require integration testing',
                      'Client/dedicated-server visual and multiplayer acceptance'],
        'mechanics': {'projectiles': 'original speed, .99 drag / .8 water drag, .02 * FallSpeed gravity',
                      'armor': 'ISpecialArmor priority 1, Defence fraction, original movement/knockback attributes',
                      'noDrop': 'givenodrop command, toss prevention, persistent death return queue',
                      'attachments': 'original mounts, models, recoil/speed/sound modifiers and scope textures'},
        'recipes': 'Medkit FFF/PPP/FFF requires external fiberglass and strong healing potions; recharge requires 3 strong healing potions; no other supplied recipes'
        })
    print('Imported', len(entries), 'entries:', report)


def parse_lang(text):
    return dict(line.split('=', 1) for line in text.splitlines()
                if '=' in line and not line.lstrip().startswith('#'))


if __name__ == '__main__':
    main()
