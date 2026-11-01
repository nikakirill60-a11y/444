"""Shared locale generation: ordinary imports must not remove MCMOD tooltips/names."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/metroport'

def lines_for(locale):
    lines=[]
    for catalog in ['mcmod_armor.json','mcmod_items.json','faction_patches.json','mcmod_decor.json','mcmod_guns.json','mcmod_firearms.json','mcmod_support.json']:
        path=ASSETS/catalog
        if path.exists():
            data=json.loads(path.read_text())
            for entry in (data['patches'] if isinstance(data,dict) else data):
                prefix='tile' if entry.get('kind') in ['decor','collision','radiation','air','respawn'] else 'item'
                lines.append(prefix+'.metroport.'+entry['id']+'.name='+entry['nameRu' if locale=='ru_ru' else 'nameEn'])
    shared=json.loads((ROOT/'tools/mcmod-localization.json').read_text())
    lines += [key+'='+value for key,value in shared[locale].items()]
    blackrazid=ASSETS/'blackrazid_equipment.json'
    if blackrazid.exists():
        for entry in json.loads(blackrazid.read_text()):
            lines.append('item.metroport.'+entry['id']+'.name='+entry['nameRu' if locale=='ru_ru' else 'nameEn']+' [BlackRazid]')
        lines.extend(['metroport.blackrazid.open='+('Открыть' if locale=='ru_ru' else 'Open'),
            'metroport.blackrazid.storage='+('Хранилище: %s ячеек' if locale=='ru_ru' else 'Storage: %s slots'),
            'metroport.blackrazid.help='+('ПКМ — открыть; слот рюкзака — надеть' if locale=='ru_ru' else 'Right click to open; backpack slot to equip')])
    weapons=ASSETS/'blackrazid_weapons.json'
    if weapons.exists():
        for entry in json.loads(weapons.read_text()):
            lines.append('item.metroport.'+entry['id']+'.name='+entry['nameRu' if locale=='ru_ru' else 'nameEn']+' [BlackRazid]')
    survival=ASSETS/'blackrazid_survival.json'
    if survival.exists():
        for entry in json.loads(survival.read_text()):
            lines.append('item.metroport.'+entry['id']+'.name='+entry['nameRu' if locale=='ru_ru' else 'nameEn']+' [BlackRazid]')
        shared=json.loads((ROOT/'tools/blackrazid-localization.json').read_text())
        lines += [key+'='+value for key,value in shared[locale].items()]
    return lines

def update():
    for locale in ['ru_ru','en_us']:
        path=ASSETS/('lang/'+locale+'.lang')
        lines=[l for l in path.read_text().splitlines() if not l.startswith(('item.metroport.mcmod_','metroport.mcmod.','item.metroport.patch_','metroport.patch.','metroport.nvg.','key.metroport.inventory','key.metroport.mods','key.metroport.pump','tile.metroport.mcmod_decor_','metroport.armor.builtin_head','metroport.inventory.','metroport.watch.','metroport.respawn.','tile.metroport.mcmod_','item.metroport.blackrazid_','metroport.blackrazid.','key.metroport.blackrazid_lamp'))]
        path.write_text('\n'.join(lines+lines_for(locale))+'\n')
