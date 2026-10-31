"""Shared locale generation: ordinary imports must not remove MCMOD tooltips/names."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/metroport'

def lines_for(locale):
    lines=[]
    for catalog in ['mcmod_armor.json','mcmod_items.json']:
        path=ASSETS/catalog
        if path.exists():
            for entry in json.loads(path.read_text()):
                lines.append('item.metroport.'+entry['id']+'.name='+entry['nameRu' if locale=='ru_ru' else 'nameEn'])
    shared=json.loads((ROOT/'tools/mcmod-localization.json').read_text())
    return lines+[key+'='+value for key,value in shared[locale].items()]

def update():
    for locale in ['ru_ru','en_us']:
        path=ASSETS/('lang/'+locale+'.lang')
        lines=[l for l in path.read_text().splitlines() if not l.startswith(('item.metroport.mcmod_','metroport.mcmod.'))]
        path.write_text('\n'.join(lines+lines_for(locale))+'\n')
