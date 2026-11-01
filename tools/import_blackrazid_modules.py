#!/usr/bin/env python3
"""Native attachment items, explicit effective fittings and original inventory icons (not meshes)."""
import json,re
from import_content import ROOT,ASSETS,put,write_json
from import_blackrazid_weapons import J,S,identifier

def catalog():
    registration=(J/'ModBase.java').read_text()
    labels={k:bytes(v,'ascii').decode('unicode_escape') for k,v in re.findall(r'LanguageRegistry.addName\(\(Object\)(\w+), \(String\)"((?:\\.|[^"\\])*)"',registration)}
    rows=[]
    for p in sorted((S/'classes/mods/provoda/items/att').glob('*.class')):
        icon=re.findall(rb'provoda:([a-z0-9_]+)',p.read_bytes());assert len(icon)==1
        rows.append(dict(id=identifier(p.stem).replace('blackrazid_','blackrazid_mod_',1),sourceId=p.stem,icon=icon[0].decode(),nameRu=labels[p.stem],nameEn=p.stem))
    assert len(rows)==28
    return rows

def fittings():
    # Five ORIGINAL inventory indices; specialization repairs legacy category slots accepting unrelated parts.
    scope='Optica OpticaSmall Kolimator TiharOptica'.split()
    rows={}
    def fit(name,slots):rows[name]=[[s for s in slot.split()] if isinstance(slot,str) else slot for slot in slots]+[[]]*(5-len(slots))
    for name in ['Rpk','AKSU','Vyhlop','Helsing','Tihar']:fit(name,[scope,'Laser'])
    for name in ['AK','AK2012','Saiga']:fit(name,[scope,'Laser','Glushak'])
    fit('Vsv',[scope,'Laser','VsvGlushak'])
    fit('Ventil',[scope,'Laser','VentilGlushak'])
    fit('Preved',[scope,'Laser','VentilGlushak Barrel'])
    fit('Uboinik',[scope,'Laser','Glushak UboinikBarrel'])
    fit('Ashot',[scope,'Laser','Glushak UboinikBarrel','AshotButt'])
    fit('Ubludok',[scope,'Laser','Glushak','UbludokButt'])
    fit('Revolver',[scope,'Laser','Glushak Barrel','Butt'])
    fit('Podonok',[scope,'Laser','Glushak Barrel','PodonokButt','PodonokGas'])
    fit('Abzac',['AutoFire','Laser','AbzacGlushak'])
    fit('Duplet',['Laser','DupletGlushak DupletBarrel QuadBarrel','DupletButt','DupletPriklad'])
    fit('Railgun',[scope,'Laser','RailgunAkk RailgunKon'])
    fit('Gatling',['GatlingBarrel','GatlingButt','GatlingGiro GatlingMotor'])
    fit('Velopal',[]);fit('Ognemet',[])
    return dict(sorted(rows.items()))

def main():
    rows=catalog();write_json(ASSETS/'blackrazid_modules.json',rows);write_json(ASSETS/'blackrazid_module_fittings.json',fittings())
    for e in rows:
        put(ASSETS/('textures/blackrazid/icons/'+e['icon']+'.png'),(S/('assets/icons/'+e['icon']+'.png')).read_bytes())
        write_json(ASSETS/('models/item/'+e['id']+'.json'),dict(parent='item/generated',textures=dict(layer0='metroport:blackrazid/icons/'+e['icon'])))
    sounds=json.loads((ASSETS/'sounds.json').read_text())
    for name in ['abzac_fastshoot','saiga_closeshoot']:
        key='blackrazid_'+name;put(ASSETS/('sounds/'+key+'.ogg'),(S/('assets/sounds/'+name+'.ogg')).read_bytes());sounds[key]=dict(sounds=['metroport:'+key])
    write_json(ASSETS/'sounds.json',dict(sorted(sounds.items())))
    from mcmod_languages import update
    update()
    print('Imported 28 native attachments and effective five-slot fittings; native meshes still pending.')
if __name__=='__main__':main()
