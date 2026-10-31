#!/usr/bin/env python3
"""Nine ordinary items verified against RegisterGuns, excluding pending guns/ammunition."""
import hashlib
import json
import re
from import_content import ASSETS, ROOT, put, write_json
from import_mcmod import SOURCE

KINDS = {'res_metall':('material','Scrap Metal'), 'res_xim':('material','Chemicals'),
         'money':('currency','Military Round'), 'money5':('currency','Military Rounds — Clip of 5'),
         'money30':('currency','Military Rounds — Magazine of 30'), 'moneyj':('currency','Metro Token'),
         'filtr':('filter','Gas Mask Filter'), 'aptech':('medkit','Medkit'), 'machete':('machete','Machete')}


def main():
    source=(SOURCE/'java/metro/common/regstration/RegisterGuns.java').read_text()
    names={key:bytes(name,'ascii').decode('unicode_escape') for key,name in
           re.findall(r'LanguageRegistry.addName\(\(Object\)(\w+), \(String\)"([^"]+)"\)',source)}
    entries=[]
    for key,(kind,en) in KINDS.items():
        if not re.search(r'GameRegistry.registerItem\(\(Item\)'+key+r', \(String\)"'+key+r'"\)',source):
            raise ValueError('Item was not registered in MCMOD: '+key)
        original=SOURCE/('assets/items/'+key+'.png'); data=original.read_bytes()
        if not data.startswith(b'\x89PNG\r\n\x1a\n'):raise ValueError('Invalid source icon '+key)
        id_='mcmod_'+key
        put(ASSETS/('textures/mcmod/items/'+key+'.png'),data)
        write_json(ASSETS/('models/item/'+id_+'.json'),{'parent':'item/handheld' if kind=='machete' else 'item/generated',
                   'textures':{'layer0':'metroport:mcmod/items/'+key}})
        entries.append({'id':id_,'sourceId':key,'kind':kind,'nameRu':'Мачете' if key=='machete' else names[key],
                        'nameEn':en,'sourceName':names[key],'stackSize':1 if kind in ['medkit','machete'] else 64,
                        'iconSha256':hashlib.sha256(data).hexdigest()})
    write_json(ASSETS/'mcmod_items.json',entries)
    write_json(ROOT/'docs/mcmod-items-report.json',{'items':entries,'source':'RegisterGuns / ItemAptech / ItemAxeB',
       'adaptations':['All nine icons copied byte-for-byte; no 1.7.10 executable classes are included.',
                     'Currency/materials are ordinary items; no unverified exchange/crafting recipes were invented.',
                     'Filter reuses the existing 6000-tick port filter mechanism, not a claim of original MCMOD radiation parity.',
                     'Medkit now requires a 40-tick native hold, heals/spends only on successful server finish, and retains five syringes, 100-tick per-stack timer and spent kit. Full heal uses player max health, not the old global 1000-health system; creative does not spend syringes. No effect at full health.',
                     'Machete is a native diamond axe with the source no-durability-loss-on-entity-hit override. 1.12 attack speed/damage are the native diamond-axe adaptation.',
                     'GUI uses original flat icons; held/ground/frame views use byte-exact original OBJ/atlases. New native-swing slash and syringe treatment animations replace the old render code. Current Steve/Alex skin hands replace the fixed source arms. Original medkit sound remains pending.'],
       'pending':['MCMOD firearms and ammunition','Decorative blocks','Remaining original server mechanics']})
    from mcmod_languages import update
    update()
    print('Imported',len(entries),'ordinary MCMOD items')

if __name__=='__main__':main()
