#!/usr/bin/env python3
"""Build the static wiki from the ACTIVE MetroPort registries and MWF v10.
No removed legacy firearms, dormant furniture, guessed drops or recipes.
Usage: python tools/build_website_wiki.py [--pack path/to/v10.zip]
"""
import argparse
from collections import Counter
import hashlib
from html import escape
import json
from pathlib import Path
import re
import zipfile

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT/'src/main/resources/assets/metroport'
JAVA = ROOT/'src/main/java/ru/metrocraft/port'
SITE = ROOT/'website'
BASE = 'f9ce878638eec2df390d877992b43f0515b4532c'
PACK_SHA = '5f07771f1498dd57e9a5ca909ee8571ae20de61d3ddc40b5c01ab31f53d2801f'
CATEGORIES = {'weapons': ('Оружие', 'АРСЕНАЛ'), 'armor': ('Броня', 'ЗАЩИТА'), 'mutants': ('Мутанты', 'ОБИТАТЕЛИ'), 'items': ('Предметы', 'ПРИПАСЫ')}

def load(name):
    return json.loads((ASSETS/(name+'.json')).read_text())

def clean(text):
    return re.sub(r'§.', '', str(text)).strip()

def number(value):
    return f'{value:g}' if isinstance(value, (float, int)) else str(value)

def source(name):
    return str((ASSETS/(name+'.json')).relative_to(ROOT))

def build(pack):
    assert hashlib.sha256(pack.read_bytes()).hexdigest() == PACK_SHA, 'Not the published MWF v10 pack'
    z = zipfile.ZipFile(pack)
    lang = dict(line.split('=', 1) for line in (ASSETS/'lang/ru_ru.lang').read_text().splitlines() if '=' in line and not line.startswith('#'))
    entries = []
    icons = {}
    def title(id, fallback=None, entity=False):
        for prefix in (['entity'] if entity else ['item', 'tile']):
            key = f'{prefix}.metroport.{id}.name'
            if key in lang: return clean(lang[key])
        return clean(fallback or id)
    def image(id):
        # Only genuine inventory icons / cube faces, never an unfolded armor/creature atlas.
        path = ASSETS/'models/item'/f'{id}.json'
        if not path.exists(): return None
        model = json.loads(path.read_text())
        texture = model.get('textures', {}).get('layer0')
        if not texture and id.startswith('block'):
            block = ASSETS/'models/block'/f'{id}.json'
            if block.exists():
                ts = json.loads(block.read_text()).get('textures', {})
                texture = ts.get('all') or ts.get('side')
        if not texture or not texture.startswith('metroport:'): return None
        file = ASSETS/'textures'/(texture.split(':', 1)[1]+'.png')
        if not file.is_file(): return None
        dest = '/assets/wiki/'+id+'.png'
        icons[dest] = file.read_bytes()
        return dest
    def add(id, cat, group, name, desc, stats, src, links=None, mwf=False):
        full = ('modularwarfare:' if mwf else 'metroport:')+id
        assert not any(e['id'] == full for e in entries), full
        img = None
        if mwf:
            icon = 'assets/modularwarfare/textures/items/'+id+'.png'
            if icon in z.namelist():
                img = '/assets/wiki/'+id+'.png'; icons[img] = z.read(icon)
        else: img = image(id)
        family = 'MWF' if mwf else 'BlackRazid' if id.startswith('blackrazid_') else 'MCMOD' if id.startswith('mcmod_') else 'Exodus' if id.startswith('exodus_') else 'METRO_MOD' if id.startswith('metromod_') else 'MetroCraft'
        entries.append(dict(id=full, category=cat, group=group, family=family, name=clean(name), description=desc,
                            stats=[[k, number(v)] for k, v in stats], source=src, image=img,
                            links=links or []))
    defs = {}
    for folder in ['guns', 'ammo', 'bullets', 'attachments']:
        defs[folder] = [json.loads(z.read(n)) for n in sorted(z.namelist()) if n.startswith(folder+'/') and n.count('/') == 1 and n.endswith('.json')]
    names = {d['internalName']: clean(d['displayName']) for ds in defs.values() for d in ds}
    def ref(id): return 'modularwarfare:'+id
    for d in defs['guns']:
        id = d['internalName']
        group = {'shotgun': 'Дробовики', 'pistol': 'Пистолеты', 'sniper': 'Винтовки', 'boltsniper': 'Винтовки', 'semisniper': 'Винтовки', 'rifle': 'Автоматы', 'smg': 'Пистолеты-пулемёты', 'lmg': 'Пулемёты'}.get(d.get('weaponType'), 'Огнестрельное оружие')
        if id in ['metro_tihar', 'metro_helsing', 'metro_railgun']: group = 'Особое оружие'
        if 'ognemet' in id: group = 'Особое оружие'
        if id in ['metro_gatling', 'metro_rpk']: group = 'Пулемёты'
        desc = 'Оружие из арсенала MWF v10. Показаны базовые параметры конфигурации без модификаторов обвесов.'
        notes = {
            'metro_medved': 'Шестизарядный дробовик. Сохранена дробь: восемь дробин за выстрел.',
            'metro_velopal': 'Шестизарядный дробовик с внутренним заряжанием. Использует дробь 12 калибра, не магазин Хлопушки.',
            'metro_ashot': 'Однозарядный дробовик. Установка приклада меняет хват; без приклада сохраняется исходная поза.',
            'metro_duplet': 'Двуствольный дробовик. Без дополнительных стволов — два патрона. Модуль дополнительных стволов увеличивает ёмкость до четырёх при использовании companion JAR metro6.',
            'metro_ubludok': 'Автомат с отдельным съёмным радиатором в слоте ствола. Радиатор не установлен по умолчанию и не добавляет механику охлаждения.',
            'metro_ognemet': 'Отдельное оружие в адаптации MWF. Конфигурация использует систему выстрелов MWF; это не обещание непрерывной струи огня.',
            'metro_ognemet_mini': 'Самостоятельный мини-огнемёт, не скин большого огнемёта. Использует собственный баллон и систему выстрелов MWF.',
            'metro_akk': 'Отдельный Калашников AKK. Баланс адаптации предварительный, а не подтверждённая копия старого мода.'}
        desc = notes.get(id, desc)
        stats = [('Урон / снаряд', d['gunDamage']), ('Темп, выстр./мин', d['roundsPerMin']), ('Снарядов / выстрел', d.get('numBullets', 1))]
        if 'internalAmmoStorage' in d: stats.append(('Внутренняя ёмкость', d['internalAmmoStorage']))
        else:
            caps = sorted({a['ammoCapacity'] for a in defs['ammo'] if a['internalName'] in d.get('acceptedAmmo', [])})
            stats.append(('Ёмкость магазинов', ' / '.join(map(str, caps))))
        stats += [('Перезарядка, такты', d['reloadTime']), ('Режимы огня', ', '.join({'semi': 'одиночный', 'auto': 'автоматический', 'burst': 'очередь'}.get(m, m) for m in d['fireModes'])), ('Эфф. дальность, блоки', d['weaponEffectiveRange']), ('Макс. дальность, блоки', d['weaponMaxRange'])]
        links = d.get('acceptedAmmo', []) + d.get('acceptedBullets', []) + [x for xs in d.get('acceptedAttachments', {}).values() for x in xs]
        add(id, 'weapons', group, names[id], desc, stats, 'MWF v10: guns/'+id+'.json', list(map(ref, links)), True)
    for folder, group in [('bullets', 'Боеприпасы'), ('ammo', 'Магазины и баллоны'), ('attachments', 'Обвесы')]:
        for d in defs[folder]:
            id = d['internalName']; stats = [('Стак', d.get('maxStackSize', 1))]
            desc = {'bullets': 'Боеприпас арсенала MWF. Совместимое оружие и магазины перечислены ниже.', 'ammo': 'Магазин или баллон MWF. Ёмкость и боеприпасы взяты из конфигурации.', 'attachments': 'Съёмный модуль MWF. Устанавливается только на совместимое оружие.'}[folder]
            if folder == 'ammo': stats.insert(0, ('Ёмкость', d['ammoCapacity']))
            if folder == 'attachments':
                stats.insert(0, ('Слот MWF', d['attachmentType']))
                if id == 'metro_mod_ubludok_radiator': desc = 'Съёмный радиатор Ублюдка. Не установлен по умолчанию. Не даёт охлаждения или бонусов баланса; занимает слот barrel.'
            links = d.get('subAmmo', []) + [g['internalName'] for g in defs['guns'] if id in g.get('acceptedAmmo', [])+g.get('acceptedBullets', [])+[x for xs in g.get('acceptedAttachments', {}).values() for x in xs]]
            if folder == 'bullets': links += [a['internalName'] for a in defs['ammo'] if id in a.get('subAmmo', [])]
            add(id, 'items', group, names[id], desc, stats, f'MWF v10: {folder}/{id}.json', list(map(ref, links)), True)
    # Active definitions mirror ContentPolicy.definition, not the retired donor catalogue.
    for d in load('content'):
        if d['kind'] in ['gun', 'ammo', 'attachment'] or (d.get('repairGun') and d['id']!='remkomplekt'): continue
        id = d['id']; stats = []
        if d['kind'] == 'armor':
            group = 'ПНВ' if d.get('nightVision') else 'Шлемы и маски' if d['slot'] == 'head' else 'Бронежилеты и костюмы'
            slot = 'Отдельный слот ПНВ' if d.get('nightVision') else 'Голова' if d['slot'] == 'head' else 'Тело'
            stats = [('Слот', slot), ('Защита, коэффициент', d['defence']), ('Прочность', d['durability']), ('Противогаз', 'Да' if d.get('gasMask') else 'Нет')]
            desc = 'Снаряжение MetroPort из основного набора. Коэффициент защиты — параметр предмета, не итоговая гарантия снижения любого урона.'
            if id in ['himza', 'satanarmor']: desc += ' Цельный костюм: отдельный шлем одновременно не надевается.'
            if d.get('nightVision'): desc = 'Прибор ночного видения. Использует собственный слот ПНВ, а не слот шлема.'
            add(id, 'armor', group, title(id, d['name']), desc, stats, source('content'))
        else:
            group = 'Медицина и ремонт' if d['kind'] == 'tool' else 'Валюта и материалы'
            desc = 'Предмет основного набора MetroPort.'
            if id == 'remkomplekt': desc = 'Ремкомплект оружия, масса 3 кг. Используется основной рукой на повреждённом оружии во второй руке. Чинит стандартную прочность предмета; не меняет патроны, обвесы или особые параметры MWF.'
            if id == 'armorrem': desc = 'Ремонт брони: держи повреждённую броню во второй руке и используй ремкомплект основной рукой.'
            if d.get('heal'): desc = 'Лечебный инструмент основного набора. Лечение и количество применений указаны в конфигурации.'; stats.append(('Лечение, HP', d['heal']))
            if d['kind'] == 'tool': stats.append(('Применений', d.get('uses', 1)))
            else: stats.append(('Стак', d.get('stackSize', 64)))
            add(id, 'items', group, 'Ремкомплект брони' if id == 'armorrem' else title(id, d['name']), desc, stats, source('content'))
    for filename, family in [('mcmod_armor', 'MCMOD'), ('blackrazid_equipment', 'BlackRazid')]:
        for d in load(filename):
            id = d['id']; mode = d['mode']
            if mode == 'backpack':
                add(id, 'items', 'Рюкзаки', d['nameRu'], 'Рюкзак BlackRazid с собственным переносным хранилищем. Не заменяет дополнительные ряды обычного инвентаря.', [('Ячеек хранилища', d['storageSlots'])], source(filename)); continue
            group = 'Шлемы и маски' if mode == 'helmet' else 'Бронежилеты и костюмы'
            stats = [('Слот', 'Голова' if mode == 'helmet' else 'Тело'), ('Защита, коэффициент', round(d['defence'], 4)), ('Прочность', d['condition']), ('Противогаз', 'Да' if d.get('gasMask', d.get('radiation', False)) else 'Нет')]
            desc = ('Цельный костюм' if mode == 'whole' else 'Отдельный шлем' if mode == 'helmet' else 'Отдельная броня для тела')+' из набора '+family+'. Коэффициент защиты взят из действующей конфигурации.'
            if id == 'mcmod_officer_ozk': desc += ' Не совмещается с отдельным шлемом.'
            add(id, 'armor', group, d['nameRu'], desc, stats, source(filename))
    add('exodus_gasmask_pumpkin', 'armor', 'Шлемы и маски', title('exodus_gasmask_pumpkin'), 'Противогаз Exodus с моделью из ресурс-пака. Сам по себе не даёт боевой защиты; использует систему фильтров.', [('Слот', 'Голова'), ('Защита, коэффициент', 0), ('Прочность', 165), ('Противогаз', 'Да')], 'src/main/java/ru/metrocraft/port/exodus/ExodusMask.java')
    for d in load('mcmod_items'):
        kind = d['kind']; id = d['id']
        desc = {'machete': 'Оружие ближнего боя MCMOD. При попадании по существу прочность не расходуется.', 'medkit': 'Пять применений. Удерживай использование 40 тактов (2 секунды при 20 TPS): лечение до максимального здоровья происходит только по завершении. Таймер между применениями — 100 тактов.', 'filter': 'Сменный фильтр для противогаза; работает с действующей системой фильтров.', 'currency': 'Валюта MetroPort. Не заменяет боеприпасы ModularWarfare.', 'material': 'Материал MCMOD. Сам по себе не добавляет новые рецепты.'}[kind]
        add(id, 'weapons' if kind == 'machete' else 'items', {'machete': 'Ближний бой', 'medkit': 'Медицина и ремонт', 'filter': 'Выживание', 'currency': 'Валюта и материалы', 'material': 'Валюта и материалы'}[kind], d['nameRu'], desc, [('Стак', d['stackSize'])], source('mcmod_items'))
    for d in load('blackrazid_survival'):
        grenade = d['kind'] == 'grenade'
        desc = {'flask': 'Фляга на 10 глотков. Питьё занимает 40 тактов; глоток расходуется после завершения.', 'lamp': 'Фонарь устанавливается в отдельный слот лампы и использует энергию системы выживания.', 'power': 'Зарядное устройство для установленного фонаря. Зарядка выполняется при удержании использования.', 'grenade': 'Метательная граната. Удерживай использование и отпусти для броска.'}[d['kind']]
        add(d['id'], 'weapons' if grenade else 'items', 'Гранаты' if grenade else 'Выживание', d['nameRu'], desc, [('Стак', 5 if grenade else 1)], source('blackrazid_survival'))
    for id, desc in [('blackrazid_watch', 'Часы BlackRazid для отдельного слота часов.'), ('mcmod_watch', 'Часы-дозиметр: время и радиация в отдельном слоте часов.'), ('filter', 'Фильтр для действующей системы противогазов.')]:
        add(id, 'items', 'Выживание', title(id), desc, [], 'src/main/java/ru/metrocraft/port/'+('ExpandedContent.java' if id == 'filter' else 'blackrazid/BlackrazidExpansion.java' if id.startswith('blackrazid') else 'mcmod/McmodSupport.java'))
    for id, rows in [('smallrucksack', 1), ('mediumrucksack', 2), ('bigrucksack', 3)]:
        add(id, 'items', 'Рюкзаки', title(id), 'Экипируется в слот рюкзака и открывает дополнительные ряды инвентаря.', [('Доп. ячеек', rows*9)], 'src/main/java/ru/metrocraft/port/ExpandedContent.java')
    for d in load('faction_patches')['patches']:
        add(d['id'], 'items', 'Нашивки', d['nameRu'], 'Косметическая нашивка для брони на теле. Не даёт членства во фракции или дополнительных прав.', [('На броне', 'Одна нашивка')], source('faction_patches'))
    for d in load('metromod_consumables'):
        stats = [('Голод, ед.', d['hunger']), ('Вода, ед.', d['water']), ('Стак', d['stackSize'])]
        add(d['id'], 'items', 'Еда и вода', d['nameRu'], 'Расходник METRO_MOD. Значения взяты из профиля предмета.', stats, source('metromod_consumables'))
    for d in load('metromod_medical_materials'):
        add(d['id'], 'items', 'Медицина и ремонт', d['nameRu'], 'Медицинский материал. Сам по себе не лечит и не даёт выдуманных эффектов.', [('Стак', d['stackSize'])], source('metromod_medical_materials'))
    behaviors = {'rat': 'Пугливая крыса: гуляет и паникует при повреждении или огне. Задач атаки нет.', 'spider': 'Ближняя атака. Включённый фонарь BlackRazid с энергией больше 10 вызывает панику.', 'biblio': 'Атакует вблизи. Прямой взгляд замедляет библиотекаря только при здоровье выше 125 HP.', 'demon': 'Летающий мутант, иммунен к огню. Контакт головы наносит 10 урона; профильное значение 12 не используется как контактный урон.', 'aqua_female': 'Несмотря на название, использует наземный ИИ ближнего боя.', 'aqua_male': 'Несмотря на название, использует наземный ИИ ближнего боя.'}
    for d in load('blackrazid_mutants'):
        short = d['id'].removeprefix('blackrazid_')
        damage = 'Не атакует' if short == 'rat' else 10 if short == 'demon' else d['damage']
        stats = [('Здоровье, HP', d['health']), ('Урон', damage)]
        if short != 'demon': stats.append(('Скорость, атрибут', d['speed']))
        if 'followRange' in d: stats.append(('Радиус цели, блоки', d['followRange']))
        add(d['id'], 'mutants', 'BlackRazid', d['nameRu'], behaviors.get(short, 'Наземный мутант BlackRazid с ближней атакой. Естественный спавн в биомах не добавлен.'), stats, source('blackrazid_mutants'))
    for id, cls in [('kris', 'Kris'), ('murzik', 'Murzik'), ('tuzic', 'Tuzic'), ('jery', 'Jery')]:
        file = JAVA/'entity'/f'{cls}Entity.java'; text = file.read_text()
        def attr(key): return float(re.search(key+r'\)\.setBaseValue\(([\d.]+)\)', text)[1])
        add(id, 'mutants', 'MetroCraft', title(id, entity=True), 'Существо исходного набора MetroCraft с ближней атакой. Не заменяется одноимённым мутантом другого набора.', [('Здоровье, HP', attr('MAX_HEALTH')), ('Урон', attr('ATTACK_DAMAGE')), ('Радиус цели, блоки', attr('FOLLOW_RANGE'))], str(file.relative_to(ROOT)))
    for id in ['exodus_shrimp', 'exodus_guard']:
        add(id, 'mutants', 'Exodus', title(id, entity=True), 'Модель Exodus с обычным ИИ ближнего боя. В ресурс-паке не было боевых параметров: здесь указаны стандартные значения адаптации, не характеристики оригинальной игры.', [('Здоровье, HP', 20), ('Урон', 2), ('Скорость, атрибут', .25), ('Радиус цели, блоки', 16)], 'src/main/java/ru/metrocraft/port/exodus/ExodusMutant.java')
    # Wiki scope: gameplay items only; no blocks, map props, models or spawners.
    byid = {e['id']: e for e in entries}
    for e in entries:
        assert all(id in byid for id in e['links']), e['id']
    return entries, icons


def render(entries):
    h = escape
    byid = {e['id']: e for e in entries}
    def anchor(id): return 'entry-'+id.replace(':', '-')
    sections = []
    for index, (cat, (name, tag)) in enumerate(CATEGORIES.items(), 1):
        rows = [e for e in entries if e['category'] == cat]
        cards = []
        for e in rows:
            id = anchor(e['id'])
            img = f'<img src="{h(e["image"])}" alt="" loading="lazy" width="160" height="112">' if e['image'] else '<span class="catalog-no-image" aria-hidden="true">'+{'weapons': '↗', 'armor': '◇', 'mutants': '◎', 'items': '▥'}[cat]+'</span><span class="catalog-no-image-label">Без иллюстрации</span>'
            stats = ''.join(f'<div><dt>{h(k)}</dt><dd>{h(v)}</dd></div>' for k, v in e['stats'])
            links = ''.join(f'<li><a href="#{anchor(target)}">{h(byid[target]["name"])}</a></li>' for target in e['links'])
            src = e['source']; path = 'downloads/Metro_Full_Arsenal_MWF_fix1_v10_icons.zip' if src.startswith('MWF') else src
            cards.append(f'''<article class="catalog-card" id="{id}" data-group="{h(e['group'])}" data-entry="{h(e['id'])}">
<details><summary><span class="catalog-image">{img}</span><span class="catalog-type">{h(e['group'])} · {h(e['family'])}</span><h3>{h(e['name'])}</h3><span class="catalog-open">Параметры и описание <span aria-hidden="true">+</span></span></summary>
<div class="catalog-body"><p>{h(e['description'])}</p><dl>{stats}</dl>{'<h4>Совместимость</h4><ul class="catalog-links">'+links+'</ul>' if links else ''}<p class="catalog-id">{h(e['id'])}</p><a class="catalog-permalink" href="#{id}">Ссылка на карточку ↗</a><p class="catalog-source">Источник: <a href="https://github.com/nikakirill60-a11y/444/blob/{BASE}/{h(path)}" target="_blank" rel="noopener noreferrer">{h(src)}</a></p></div></details></article>''')
        sections.append(f'''<section id="{cat}" class="wiki-panel" aria-labelledby="{cat}-title"><div class="wiki-panel-heading"><div><span class="section-tag">0{index} / {tag}</span><h2 id="{cat}-title">{name}</h2></div><p>{len(rows)} записей · открой карточку, чтобы увидеть параметры и совместимость.</p></div><div class="catalog-grid">{''.join(cards)}</div></section>''')
    return '\n'.join(sections)


def main():
    parser = argparse.ArgumentParser(); parser.add_argument('--pack', type=Path, default=ROOT/'downloads/Metro_Full_Arsenal_MWF_fix1_v10_icons.zip'); args = parser.parse_args()
    entries, icons = build(args.pack)
    counts = dict(Counter(e['category'] for e in entries))
    doc = {'version': 'MetroPort 1.13.0-dev.33 + MWF v10 / metro6', 'sourceCommit': BASE, 'packSha256': PACK_SHA, 'counts': counts, 'entries': entries}
    (SITE/'wiki-data.json').write_text(json.dumps(doc, ensure_ascii=False, indent=2)+'\n')
    # Remove only obsolete icons owned by the previous generated manifest.
    old_manifest = SITE/'wiki-manifest.json'
    if old_manifest.exists():
        for dest in json.loads(old_manifest.read_text()).get('icons', {}):
            if dest not in icons and re.fullmatch(r'/assets/wiki/[a-z0-9_]+\.png', dest):
                (SITE/dest.lstrip('/')).unlink(missing_ok=True)
    for dest, data in sorted(icons.items()):
        path = SITE/dest.lstrip('/'); path.parent.mkdir(parents=True, exist_ok=True); path.write_bytes(data)
    html = (SITE/'wiki.html').read_text()
    html = re.sub(r'<!-- CATALOG BEGIN -->.*?<!-- CATALOG END -->', lambda _: '<!-- CATALOG BEGIN -->\n'+render(entries)+'\n<!-- CATALOG END -->', html, flags=re.S)
    for cat, count in counts.items():
        html = re.sub(r'(<span\b[^>]*data-count="'+cat+r'"[^>]*>).*?(</span>)', lambda m: m[1]+str(count)+m[2], html)
    (SITE/'wiki.html').write_text(html)
    manifest = {'sources': BASE, 'packSha256': PACK_SHA, 'counts': counts, 'icons': {k: hashlib.sha256(v).hexdigest() for k, v in sorted(icons.items())}}
    (SITE/'wiki-manifest.json').write_text(json.dumps(manifest, ensure_ascii=False, indent=2)+'\n')
    print(json.dumps({'counts': counts, 'total': len(entries), 'icons': len(icons)}, ensure_ascii=False))

if __name__ == '__main__': main()
