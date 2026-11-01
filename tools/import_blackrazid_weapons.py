#!/usr/bin/env python3
"""Native ammunition and all 22 basic firearm profiles. Models/modules still pending."""
import json,re
from import_content import ROOT,ASSETS,put,write_json
S=ROOT/'tools/blackrazid-reference'
J=S/'java/mods/provoda'
AUTOMATICS='Ubludok AK2012 AK AKSU Rpk Vsv'.split()
SPECIALS=json.loads((ROOT/'tools/blackrazid-special-profiles.json').read_text())
BASIC_GUNS=AUTOMATICS+list(SPECIALS)
def identifier(name):
    name=re.sub(r'([A-Z]+)([A-Z][a-z])',r'\1_\2',name)
    return 'blackrazid_'+re.sub(r'([a-z0-9])([A-Z])',r'\1_\2',name).lower()
def catalog():
    registration=(J/'ModBase.java').read_text()
    labels={k:bytes(v,'ascii').decode('unicode_escape') for k,v in re.findall(r'LanguageRegistry.addName\(\(Object\)(\w+), \(String\)"((?:\\.|[^"\\])*)"',registration)}
    entries=[]
    magazines={name:(round_,int(cap)) for name,round_,cap in re.findall(r'\b(\w+Ammo\w*) = new \w+\(\d+, ModBase\.(\w+)\.cp, (\d+),',registration) if name!='AKKAmmo'}
    for p in sorted((J/'guns/ammo').glob('*.java')):
        name=p.stem;text=p.read_text();icon=re.search(r'a\("provoda:([^"\n]+)"',text)[1]
        entry=dict(id=identifier(name),sourceId=name,kind='round' if name.startswith('Bullet') else 'magazine',icon=icon,nameRu=labels[name],nameEn=name)
        if name in magazines:
            round_,capacity=magazines[name]
            entry.update(roundItem=identifier(round_),capacity=capacity,refillable=True,defaultRounds=0,sourceCapacityArgument=capacity)
            if name=='OgnemetAmmo':entry.update(capacity=200,refillable=False,defaultRounds=200) # Ammo NBT + 200, NOT constructor's unused 5.
        entries.append(entry)
    for name in AUTOMATICS:
        text=(J/('guns/'+name+'.java')).read_text()
        args=re.search(r'\b'+name+r' = new '+name+r'\(\d+, (\d+), (\d+)\)',registration)
        shot=re.search(r'new EntityBullet\(\(mp\)p, this.damage, (\d+), ([\d.]+)f',text)
        loading=int(re.search(r'"LoadingTimer", (\d+)\);\s*break;',text)[1])
        unloading=int(re.search(r'"UnloadingTimer", ([1-9]\d*)\);',text)[1])
        choices=list(dict.fromkeys(re.findall(r'ModBase\.(\w+Ammo\w*)\.cp',text)))
        sounds={'Ubludok':('ubludok_shoot','ubludok_reload','ubludok_unload'),'AK2012':('rpk_shoot','aknew_reload','aknew_unload'),
                'AK':('ak_shoot','rpk_reload','rpk_unload'),'AKSU':('ak_shoot','rpk_reload','rpk_unload'),
                'Rpk':('rpk_shoot','rpk_reload','rpk_unload'),'Vsv':('vsv_shoot','saiga_reload','saiga_unload')}[name]
        entries.append(dict(id=identifier(name),sourceId=name,kind='gun',icon=re.search(r'a\("provoda:([^"\n]+)"',text)[1],nameRu=labels[name],nameEn=name,
            magazines=[identifier(c) for c in choices],damage=int(args[2]),speed=float(shot[2]),accuracy=int(shot[1]),delay=min(map(int,re.findall(r'ep.upST\((\d+)\)',text))),
            loadingTicks=loading,unloadingTicks=unloading,recoil=.4 if name=='Vsv' else .6,
            shootSound='blackrazid_'+sounds[0],reloadSound='blackrazid_'+sounds[1],unloadSound='blackrazid_'+sounds[2],runtimeStatus='basic-firearm with native attachment effects; native-mesh/animations pending'))
    for name,profile in SPECIALS.items():
        text=(J/('guns/'+name+'.java')).read_text()
        args=re.search(r'\b'+name+r' = new '+name+r'\(\d+, (\d+), (\d+)\)',registration)
        choices=profile.get('magazineOverride',list(dict.fromkeys(re.findall(r'ModBase\.(\w+Ammo\w*)\.cp',text))))
        choices=[c for c in choices if c in magazines]
        e=dict(id=identifier(name),sourceId=name,kind='gun',icon=re.search(r'a\("provoda:([^"\n]+)"',text)[1],nameRu=labels[name],nameEn=name,
            magazines=[identifier(c) for c in choices],damage=4 if profile.get('flame') else int(args[2]),sourceConstructorDamage=int(args[2]),
            loadingTicks=profile.get('loadingTicks',profile.get('looseLoadingTicks',1)),unloadingTicks=profile.get('unloadingTicks',1),
            runtimeStatus='basic-firearm with native attachment effects; native-mesh/animations pending')
        e.update({k:v for k,v in profile.items() if k not in ['sounds','magazineOverride']})
        if 'looseRound' in e:e['looseRound']=identifier(e['looseRound'])
        for key in ['looseReloadSound','cycleSound','pumpSound']:
            if key in e:e[key]='blackrazid_'+e[key]
        e.update(zip(['shootSound','reloadSound','unloadSound'],['blackrazid_'+v for v in profile['sounds']]))
        e['emptySound']='blackrazid_ognemet_empty' if name in ['Ognemet','Helsing','Tihar'] else 'blackrazid_empty'
        entries.append(e)
    return entries

def main():
    entries=catalog();write_json(ASSETS/'blackrazid_weapons.json',entries)
    for e in entries:
        put(ASSETS/('textures/blackrazid/icons/'+e['icon']+'.png'),(S/('assets/icons/'+e['icon']+'.png')).read_bytes())
        write_json(ASSETS/('models/item/'+e['id']+'.json'),dict(parent='item/handheld' if e['kind']=='gun' else 'item/generated',textures=dict(layer0='metroport:blackrazid/icons/'+e['icon'])))
    sounds=json.loads((ASSETS/'sounds.json').read_text())
    source_files=sum([json.loads((S/name).read_text())['files'] for name in ['weapons-source-files.json','weapons-special-source-files.json']],[])
    for e in source_files:
        if not e['path'].startswith('assets/sounds/'):continue
        name=e['path'].split('/')[-1][:-4];key='blackrazid_'+name
        put(ASSETS/('sounds/'+key+'.ogg'),(S/e['path']).read_bytes());sounds[key]=dict(sounds=['metroport:'+key])
    write_json(ASSETS/'sounds.json',dict(sorted(sounds.items())))
    from mcmod_languages import update
    update()
    print('Imported 9 rounds, 20 magazines and 22 basic native guns; full expansion remains incomplete.')
if __name__=='__main__':main()
