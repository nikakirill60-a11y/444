#!/usr/bin/env python3
"""Only the implemented survival/grenade assets. Does not claim the whole expansion is done."""
import json
from import_content import ROOT, ASSETS, put, write_json
SOURCE=ROOT/'tools/blackrazid-reference'
ENTRIES=[
    dict(id='blackrazid_flyashka',sourceId='Flyashka',kind='flask',icon='flyashka',nameRu='Фляга',nameEn='Flask'),
    dict(id='blackrazid_lamp',sourceId='Lamp',kind='lamp',icon='flashlight',nameRu='Фонарь',nameEn='Flashlight'),
    dict(id='blackrazid_power',sourceId='Power',kind='power',icon='power',nameRu='Зарядное устройство',nameEn='Charger'),
    dict(id='blackrazid_grenade',sourceId='Grenade',kind='grenade',icon='grenade_low',nameRu='Граната',nameEn='Grenade'),
    dict(id='blackrazid_grenade_big',sourceId='GrenadeBig',kind='grenade',icon='grenade_low',nameRu='Большая граната',nameEn='Large grenade'),
]
def main():
    write_json(ASSETS/'blackrazid_survival.json',ENTRIES)
    for e in ENTRIES:
        put(ASSETS/('textures/blackrazid/icons/'+e['icon']+'.png'),(SOURCE/('assets/icons/'+e['icon']+'.png')).read_bytes())
        write_json(ASSETS/('models/item/'+e['id']+'.json'),dict(parent='item/generated',textures=dict(layer0='metroport:blackrazid/icons/'+e['icon'])))
    sounds=json.loads((ASSETS/'sounds.json').read_text())
    for name in ['flyashka','railgun_air']:
        put(ASSETS/('sounds/blackrazid_'+name+'.ogg'),(SOURCE/('assets/sounds/'+name+'.ogg')).read_bytes())
        sounds['blackrazid_'+name]=dict(sounds=['metroport:blackrazid_'+name])
    write_json(ASSETS/'sounds.json',dict(sorted(sounds.items())))
    from mcmod_languages import update
    update()
    print('Imported five survival/grenade items, NOT all remaining BlackRazid content.')
if __name__=='__main__':main()
