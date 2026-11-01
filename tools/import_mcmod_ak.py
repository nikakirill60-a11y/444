#!/usr/bin/env python3
"""Only the registered MCMOD AK-74 and its ammunition; no other guns are imported."""
from pathlib import Path
import json,hashlib,re
ROOT=Path(__file__).resolve().parents[1];SOURCE=ROOT/'tools/mcmod-reference';ASSETS=ROOT/'src/main/resources/assets/metroport'
FILES={
 'model.obj':('16ycT013Eehf4vGhaOTbj1tIFhFpKOaT5','models/mcmod/guns/ak74.obj'),
 'texture.png':('1hSwAoZ6CiMJMV_RA_QH7xNZhjW-LZ-em','textures/mcmod/guns/ak74.png'),
 'texture_low.png':('1wDg2gxabHeleR--9eus-Qcu-66yJypJK','textures/mcmod/guns/ak74_low.png'),
 'flash.png':('1LBhuelWDzuIgWP-OebP8HpuxTRygXetp','textures/mcmod/guns/ak74_flash.png'),
 'model.mtl':('1GaoVOlIx1ZcXyImo1CGMWitFKbC-2PWB',None),
 'ak74.png':('1O_7Qeg22yFtrRL5CE1Lfl6IWkVaryr_D','textures/mcmod/guns/ak74_icon.png'),
 'x545.png':('1gATEdyTBzrjyY_dSgJj7jS-yH8QYjaiX','textures/mcmod/guns/ammo5x45.png'),
 'ak_boom.ogg':('1aj251CSvSxi81JlF94FkY5CHgYwjxf-s','sounds/mcmod/ak_boom.ogg'),
 'ak74_reload.ogg':('1b7P_RQshrr3sNQQbjoD_SQvPGm79RJKR','sounds/mcmod/ak74_reload.ogg')}
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def main():
 manifest=json.loads((SOURCE/'source-files.json').read_text())
 for name,(id,target) in FILES.items():
  rel='assets/guns/ak74/'+name;b=(SOURCE/rel).read_bytes();manifest['files'][rel]={'driveId':id,'bytes':len(b),'sha256':hashlib.sha256(b).hexdigest()}
  if target:p=ASSETS/target;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(b)
 write(SOURCE/'source-files.json',manifest)
 text=(SOURCE/'java/metro/common/regstration/RegisterGuns.java').read_text()
 raw=re.search(r'ak74 = new ItemGun\((.*?)\)\.',text).group(1)
 assert raw=='false, 13.0, 1, 1.0f, "ak_boom", "ak74_reload", 1.0f, 1.5f, 30, 1, 1.0f, -1.0f, "ak74", 60, 25.0, 30.0f, 1600'
 entries=[{'id':'mcmod_ak74','nameRu':'АК-74 [MCMOD]','nameEn':'AK-74 [MCMOD]','kind':'gun','capacity':30,'reloadTicks':60,'delay':2,'damage':13,'durability':1600,'ammo':'mcmod_ammo5x45','automatic':True,'bulletSpeed':4,'spread':1,'recoil':1,'randomRecoil':0,'randomYaw':2,'shootSound':'mcmod_ak_boom','reloadSound':'mcmod_ak74_reload','stackSize':1},
 {'id':'mcmod_ammo5x45','nameRu':'Патроны 5×45 [MCMOD]','nameEn':'5x45 ammunition [MCMOD]','kind':'ammo','stackSize':64,'fallSpeed':.01}]
 write(ASSETS/'mcmod_guns.json',entries)
 for entry,texture in zip(entries,['ak74_icon','ammo5x45']):write(ASSETS/('models/item/'+entry['id']+'.json'),{'parent':'item/generated','textures':{'layer0':'metroport:mcmod/guns/'+texture}})
 sounds=json.loads((ASSETS/'sounds.json').read_text())
 for id,name in [('mcmod_ak_boom','ak_boom'),('mcmod_ak74_reload','ak74_reload')]:sounds[id]={'sounds':['metroport:mcmod/'+name]}
 write(ASSETS/'sounds.json',sounds)
 write(ROOT/'docs/mcmod-ak-report.json',{'importedGuns':['ak74'],'importedAmmunition':['ammo5x45'],'sourceConstructor':raw,'sourceFireInterval':'Server counter resets to 0 on firing; checks >=1 before increment: one shot per 2 ticks.','preserved':['Original base OBJ groups body/mag/zatv, UV, normals, high/low textures, inventory icon, flash and both original sounds.','Magazine 30, reload 60 ticks, raw damage 13, base accuracy 1, condition 1600.','Separate registry IDs; original Flan AK-74 and its ammunition unchanged.'],
 'adaptations':['Uses existing server-authoritative port projectiles, recoil and controls (RMB fire, Alt aim, R or sneak+RMB reload); no exact MCMOD 1000-HP damage system claimed.','Projectile speed 4 blocks/tick and gravity .01 are port settings, not recovered MCMOD parameters.','Condition zero jams but retains the gun; repair restores functionality.','Original native OBJ is mapped into the tested existing AK hand space; player skin hands, magazine and bolt animation use port timing.','Only base AK-74. Original upgrade GUI, optics, silencers, clocks, laser, grenade launcher and other MCMOD weapons are not included.']})
 from mcmod_languages import update
 update()
if __name__=='__main__':main()
