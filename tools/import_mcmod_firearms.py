#!/usr/bin/env python3
"""Remaining registered MCMOD guns, derived from recovered constructor arguments."""
import ast,json,re
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/metroport'
NAMES={'ashot':('Ашот','Ashot'),'duplet':('Дуплет','Duplet'),'ubo':('Убойник','Uboinik'),'vsv':('ВСВ','VSV'),'rev':('Револьвер','Revolver'),'ubludok':('Ублюдок','Bastard'),'ventil':('Вентиль','Valve'),'preved':('Превед','Preved'),'railgun':('Рельса','Railgun'),'tihar':('Тихарь','Tikhar'),'ak2012':('АК-2012','AK-2012'),'ak74u':('АКС-74У','AKS-74U'),'rpk':('РПК','RPK'),'helsing':('Хельсинг','Helsing')}
AMMO={1:'5x45',2:'12x70',3:'44',4:'762',5:'145',6:'_ball',7:'_bolts'}
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def main():
 text=(ROOT/'tools/mcmod-reference/java/metro/common/regstration/RegisterGuns.java').read_text();entries=[];constructors={}
 for name,(ru,en) in NAMES.items():
  raw=re.search(r'\b'+name+r' = new ItemGun\((.*?)\)\.',text).group(1);args=ast.literal_eval('['+re.sub(r'(?<=\d)f\b','',raw).replace('false','False').replace('true','True')+']');constructors[name]=args
  air,damage,mode,delay,shoot,reload,accuracy,weight,capacity,ammo,recoil,yaw,_,ticks,distance,effective,condition=args
  d={'id':'mcmod_'+name,'nameRu':ru+' [MCMOD]','nameEn':en+' [MCMOD]','kind':'gun','capacity':capacity,'reloadTicks':ticks,'delay':int(delay)+1,'damage':damage,'durability':condition,'ammo':'mcmod_ammo'+AMMO[ammo],'automatic':mode==1,'pellets':6 if mode==3 else 1,'bulletSpeed':20,'spread':accuracy,'recoil':recoil,'randomYaw':abs(yaw)*2,'shootSound':'mcmod_'+shoot,'reloadSound':'mcmod_'+reload,'stackSize':1,'mcmodAir':air,'mcmodFireMode':mode}
  entries.append(d);write(A/('models/item/'+d['id']+'.json'),{'parent':'item/generated','textures':{'layer0':'metroport:mcmod/guns/'+name+'_icon'}})
 write(A/'mcmod_firearms.json',entries)
 # The original base AK follows the same recovered flight model, without changing the Flan AK.
 p=A/'mcmod_guns.json';base=json.loads(p.read_text());base[0].update(bulletSpeed=20,mcmodFireMode=1);base[1]['fallSpeed']=.05;write(p,base)
 sounds=json.loads((A/'sounds.json').read_text())
 for path in sorted((A/'sounds').glob('mcmod_*.ogg')):sounds[path.stem]={'sounds':['metroport:'+path.stem]}
 write(A/'sounds.json',dict(sorted(sounds.items())))
 write(ROOT/'docs/mcmod-firearms-report.json',{'sourceConstructors':constructors,'registeredGuns':15,'excludedUnregistered':'g9a91','damageScale':'Original numerical damage on vanilla 20 HP, no 1000-HP player conversion.','sourceAudit':{'NoDrop':'TutEventHandler.doPlayerDropsEvent is empty; unconditional death cancellation belongs to the excluded original health system. ItemGun tooltip alone is not a working retention mechanic. Existing explicit NoDrop tags continue through the port retention queue.','upgrades':'Four implemented pages: modStvol, modScope (1 optical / 2 collimator), modMag, modRessiver. Scope3 and modPriklad are mesh/NBT placeholders, not installable in recovered GUI/handler.','costs':'Displayed prices, not the original inclusive-loop +1 overcharge. Removal returns 8 metal/4 chemicals only for an installed module.','flight':'20 blocks/tick, Gaussian .0075 × accuracy, .001 downward acceleration. Vanilla Throwable drag .99 / water .8; port swept collision, lag history and unloaded-terrain safeguards retained. Original block-impact exceptions and launch-origin offset are not reproduced; solid ray occlusion is retained to avoid skipping walls behind transparent blocks.', 'gui':'Original icons and panel atlases in a compact native 1.12 layout; not a pixel-identical 1.7.10 GuiScreen. Server-bound container prevents forged prices, arbitrary inventory edits, repeated refunds and changed-hand operations.', 'pressure':'Original cap 30 and damage thresholds 8/15/20; V holds server-limited pumping every 8 ticks, original pump sound and new skin-hand pump animation. Counter/gauge mirrors original thresholds; timing and controls are adapted.', 'animation':'Original articulated groups, native magazine/bolt/revolver-shell/meter visibility and new reload/recoil/pump skin-hand animations. Original per-frame renderer curves, smoke and muzzle transforms are not claimed pixel-identical.'}})
 from mcmod_languages import update
 update()
if __name__=='__main__':main()
