#!/usr/bin/env python3
"""Explicit, reproducible inventory balance; never changes armor protection or existing stack NBT."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'
TIERS=['common','uncommon','rare','epic','legendary','mythic','exclusive']
# User-approved firearm tiers. AKK was not listed; retain its existing rare tier.
WEAPON_RARITIES={'duplet': 'common',
 'revolver': 'common',
 'ubludok': 'common',
 'ashot': 'uncommon',
 'aksu': 'uncommon',
 'ak': 'rare',
 'podonok': 'rare',
 'uboinik': 'rare',
 'tihar': 'epic',
 'ventil': 'epic',
 'rpk': 'epic',
 'velopal': 'epic',
 'helsing': 'epic',
 'saiga': 'legendary',
 'vsv': 'legendary',
 'gatling': 'legendary',
 'preved': 'legendary',
 'medved': 'mythic',
 'ognemet': 'mythic',
 'ognemet_mini': 'mythic',
 'abzac': 'mythic',
 'ak2012': 'mythic',
 'vyhlop': 'mythic',
 'railgun': 'mythic',
 'akk': 'rare'}
NATIVE={
 'armor':'uncommon','bandit':'uncommon','bazaltarmor':'legendary','bazalthelmet':'legendary',
 'custarmor':'common','ganzaarmor':'rare','ganzahelm':'rare','ganzatyaj':'epic','ganza_helmet':'epic',
 'gasmask':'common','hardhelmet':'legendary','heavy_helmet':'legendary','helmet':'uncommon','himza':'rare',
 'kontrabody':'epic','legionarmor':'epic','legionhelmet':'epic','m12':'uncommon','nvgdbody':'epic','nvgdhelmet':'epic',
 'pnv':'rare','plasharmor':'uncommon','polis':'rare','redkaska':'rare','redarmor':'rare',
 'reicharmor':'epic','reichhelmet':'epic','reichhelmet2':'rare','reichla':'rare','satanarmor':'mythic',
 'standarthelmet':'uncommon','tajbandit':'rare','tyaj':'rare','vatnik':'common','heavy':'legendary','light_armor':'common'}
MCMOD={
 'btbkz':'epic','scout_armor':'rare','bandit_armor':'uncommon','banditkurtka':'common',
 'bkz':'rare','cherv':'rare','gasmask':'common','ghillie':'uncommon','gorka':'uncommon',
 'hanza':'uncommon','himza':'uncommon','juggernaut':'legendary','kitel':'common','kurtka':'common',
 'mgu':'rare','officer_ozk':'rare','olkhon':'epic','ozk':'uncommon','plash':'common','police':'uncommon',
 'povstanec':'rare','prorok':'rare','psh1b':'rare','reih':'rare','sniper':'rare','sniper_camo':'uncommon',
 'standart_k':'legendary','sverchok':'epic','sverchok_mp6':'epic','taiga':'rare','vatnik':'common',
 'voen_tactical':'epic','zapal':'exclusive'}
BLACKRAZID={
 'himza':'rare','heavy':'legendary','clean':'epic','c_clean':'uncommon','normal':'epic',
 'kozh':'common','easy':'rare','heavy1':'epic','sparta':'mythic','heavy_hat':'legendary',
 'nazi_hat':'epic','heavy1_hat':'epic','red':'rare','nazi':'epic','gas_mask':'common'}
SIZES={'item':(1,1),'firearm':(4,2),'melee':(1,3),'armor':(3,3),'helmet':(2,2),
       'backpack':(3,3),'nvg':(2,1),'watch':(1,1),'ammunition':(1,1),'magazine':(1,2),
       'attachment':(1,1),'food':(1,1),'block':(2,2)}
def main():
    p=A/'inventory_balance.json';balance=json.loads(p.read_text())
    for category,(width,height) in SIZES.items():balance['categories'][category].update(width=width,height=height)
    for id,profile in balance['items'].items():
        w,h=(1,1)
        if id.startswith('modularwarfare:metro_'):
            gun=id.split('metro_',1)[1];w,h=(4,2)
            if gun in WEAPON_RARITIES:profile['rarity']=WEAPON_RARITIES[gun]
            if gun in ['ashot','revolver','podonok','aksu','ognemet_mini']:w,h=3,2
            if gun in ['gatling','preved','vyhlop','ventil','railgun']:w,h=5,2
        elif 'back_pack_' in id:
            w,h={'small':(2,3),'medium':(3,3),'large':(3,4)}[id.rsplit('_',1)[1]]
        profile.update(width=w,height=h)
    armor=[]
    def add(entry,tier,role):
        profile=dict(balance['categories'][role]);profile['rarity']=tier
        if role=='armor' and tier in ['legendary','mythic','exclusive']:profile.update(width=4,height=4)
        balance['items']['metroport:'+entry['id']]=profile
        armor.append(('metroport:'+entry['id'],tier,profile['width'],profile['height']))
    for e in json.loads((A/'content.json').read_text()):
        if e['kind']=='armor':add(e,NATIVE[e['id']],'nvg' if e['id']=='pnv' else 'armor' if e['slot']=='chest' else 'helmet')
    for e in json.loads((A/'mcmod_armor.json').read_text()):
        key=e['id'].removeprefix('mcmod_').removesuffix('_helmet').removesuffix('_vest')
        add(e,MCMOD[key],'helmet' if e['mode']=='helmet' else 'armor')
    for e in json.loads((A/'blackrazid_equipment.json').read_text()):
        if e['mode']!='backpack':add(e,BLACKRAZID[e['id'].removeprefix('blackrazid_')],'helmet' if e['mode']=='helmet' else 'armor')
    add({'id':'exodus_gasmask_pumpkin'},'common','helmet')
    for id,size in {'smallrucksack':(2,3),'mediumrucksack':(3,3),'bigrucksack':(3,4)}.items():
        profile=dict(balance['categories']['backpack']);profile.update(width=size[0],height=size[1]);balance['items']['metroport:'+id]=profile
    for id,size in {'medkit':(2,2),'mcmod_aptech':(2,2),'blackrazid_flyashka':(1,2),'blackrazid_lamp':(1,2),'blackrazid_power':(2,2),
                    'blackrazid_grenade':(1,2),'blackrazid_grenade_big':(2,2)}.items():
        profile=dict(balance['categories']['item']);profile.update(width=size[0],height=size[1]);balance['items']['metroport:'+id]=profile
    balance['categories']['patch']=dict(balance['categories']['item'],grams=100)
    for id,grams in {'armorrem':3000,'remkomplekt':3000,'kanistra':6000}.items():
        balance['items']['metroport:'+id]=dict(balance['categories']['item'],grams=grams)
    for id in ['rem','armorrem','remkomplekt']:
        balance['items'].setdefault('metroport:'+id,dict(balance['categories']['item'])).update(width=2,height=2)
    balance['items']['metroport:kanistra'].update(width=3,height=3)
    balance['items']['metroport:mcmod_watch']=dict(balance['categories']['watch'],rarity='epic')
    p.write_text(json.dumps(balance,indent=2)+'\n')
    wiki=json.loads((ROOT/'website/wiki-data.json').read_text());names={e['id']:e['name'] for e in wiki['entries']}
    labels=['Обычный — серый','Необычный — зелёный','Редкий — голубой','Эпический — фиолетовый','Легендарный — жёлтый','Мифический — красный','Эксклюзив — розовый']
    lines=['# Редкости и размеры брони — dev35','','Явные профили для всех 110 предметов раздела брони, включая ПНВ.','Защита, прочность и бонус грузоподъёмности не меняются.','Шлем и броня одного комплекта получают одинаковую редкость.','']
    for tier,label in zip(TIERS,labels):
        lines+=['## '+label,'','| Предмет | Размер в сетке | ID |','|---|---|---|']
        for id,t,w,h in armor:
            if t==tier:lines+=['| '+names.get(id,id)+' | '+str(w)+'×'+str(h)+' | `'+id+'` |']
        lines+=['']
    (ROOT/'docs/ARMOR_RARITIES_DEV35_RU.md').write_text('\n'.join(lines)+'\n')
    return armor
if __name__=='__main__':print('Configured',len(main()),'armor/NVG entries')
