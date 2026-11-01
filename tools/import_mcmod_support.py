#!/usr/bin/env python3
"""Recovered ammunition, watch atlas/digits and medkit sound; explicit 1.12 map-block adapters."""
import json,shutil
from pathlib import Path
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/metroport';S=ROOT/'tools/mcmod-reference/assets/support'
def write(path,data):path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
def main():
 entries=[]
 for id,ru,en,tex in [('ammo12x70','Патроны 12×70','12x70 ammunition','x1270'),('ammo44','Патроны .44','.44 ammunition','x44'),('ammo762','Патроны 7,62','7.62 ammunition','x762'),('ammo145','Патроны 14,5','14.5 ammunition','x145'),('ammo_ball','Железные шарики','Iron balls','ball'),('ammo_bolts','Болты','Bolts','ammo_bolts')]:
  entries.append(dict(id='mcmod_'+id,nameRu=ru+' [MCMOD]',nameEn=en+' [MCMOD]',kind='ammo',stackSize=64,texture=tex))
 for id,ru,en,kind,color in [('collision','Коллизия','Collision','collision','#cc3333'),('radiation','Радиация','Radiation','radiation','#bdd44a'),('air','Безопасный воздух','Safe air','air','#42cce5'),('respawn','Точка возрождения','Respawn point','respawn','#dddddd')]:
  entries.append(dict(id='mcmod_'+id,nameRu=ru+' [MCMOD]',nameEn=en+' [MCMOD]',kind=kind,texture=id))
  if kind=='respawn':shutil.copyfile(S/'pass.png',A/'textures/mcmod/support/respawn.png')
  elif kind=='collision':shutil.copyfile(S/'collision.png',A/'textures/mcmod/support/collision.png')
  else:
   # New editor icons, not claimed to be recovered original textures. Runtime blocks stay invisible.
   im=Image.new('RGBA',(16,16),'#202020');d=ImageDraw.Draw(im);d.rectangle((1,1,14,14),outline=color,width=2)
   if kind=='radiation':d.ellipse((5,5,10,10),fill=color)
   elif kind=='air':d.line((4,9,7,12,12,4),fill=color,width=2)
   else:d.line((4,4,12,12),fill=color,width=2);d.line((4,12,12,4),fill=color,width=2)
   im.save(A/('textures/mcmod/support/'+id+'.png'))
 entries.append(dict(id='mcmod_watch',nameRu='Наручные часы-дозиметр',nameEn='Wristwatch dosimeter',kind='watch',stackSize=1,texture='watch'))
 write(A/'mcmod_support.json',entries)
 for entry in entries:
  id=entry['id'];tex=entry['texture'];kind=entry['kind'];(A/'textures/mcmod/support').mkdir(parents=True,exist_ok=True)
  if kind=='ammo':shutil.copyfile(S/(tex+'.png'),A/('textures/mcmod/support/'+tex+'.png'))
  if kind in ['ammo','watch']:write(A/('models/item/'+id+'.json'),{'parent':'item/generated','textures':{'layer0':'metroport:mcmod/support/'+('radiation' if kind=='watch' else tex)}})
  elif kind=='collision':
   write(A/('models/block/'+id+'.json'),{'parent':'block/cube_all','textures':{'all':'metroport:mcmod/support/collision'}})
   write(A/('blockstates/'+id+'.json'),{'variants':{'normal':{'model':'metroport:'+id}}})
   write(A/('models/item/'+id+'.json'),{'parent':'metroport:block/'+id})
  elif kind=='respawn':
   write(A/('models/block/'+id+'.json'),{'parent':'block/block','textures':{'particle':'metroport:mcmod/support/respawn','all':'metroport:mcmod/support/respawn'},'elements':[{'from':[0,0,0],'to':[16,1.6,8],'faces':{f:{'texture':'#all'} for f in ['down','up','north','south','east','west']}}]})
   write(A/('blockstates/'+id+'.json'),{'variants':{'normal':{'model':'metroport:'+id}}})
   write(A/('models/item/'+id+'.json'),{'parent':'metroport:block/'+id})
  else:
   write(A/('blockstates/'+id+'.json'),{'variants':{'normal':{'model':'minecraft:air'}}})
   write(A/('models/item/'+id+'.json'),{'parent':'item/generated','textures':{'layer0':'metroport:mcmod/support/'+tex}})
 for name in ['clock-cR.png']+[str(i)+'.png' for i in range(10)]:shutil.copyfile(S/name,A/('textures/mcmod/support/'+name.lower()))
 shutil.copyfile(S/'medkit_use_heavy.ogg',A/'sounds/mcmod_medkit_use_heavy.ogg')
 sounds=json.loads((A/'sounds.json').read_text());sounds['mcmod_medkit_use_heavy']={'sounds':['metroport:mcmod_medkit_use_heavy']};write(A/'sounds.json',dict(sorted(sounds.items())))
 # Extract ONLY original clock/digit faces; retain their exact positions, UV and normals.
 source=(ROOT/'tools/mcmod-reference/assets/guns/ak74/model.obj').read_text();out=[];selected=False
 for line in source.splitlines():
  if line.startswith(('v ','vt ','vn ')):out.append(line)
  elif line.startswith(('g ','o ')):
   selected=line.split()[1] in ['clock','num1','num2','num3','num4']
   if selected:out.append(line)
  elif selected and line.startswith('f '):out.append(line)
 p=A/'models/mcmod/watch.obj';p.write_text('\n'.join(out)+'\n')
 write(ROOT/'docs/mcmod-support-report.json',{
  'version':'1.10.0','source':'User-provided MCMOD files; original ArmorArm/technical blocks decompiled with CFR 0.152',
  'newAmmunition':[e['id'] for e in entries if e['kind']=='ammo'],
  'allSevenAmmoTypesImported':True,'otherMCMODGunsDeferred':True,
  'technicalBlocks':{e['id']:e['kind'] for e in entries if e['kind'] in ['collision','radiation','air','respawn']},
  'watch':{'item':'mcmod_watch','slot':'Appended after NVG, preserving existing container indices','sourceGroups':['clock','num1','num2','num3','num4'],'sourceTexture':'clock-cR.png and ten original digit PNGs','time':'Minecraft world time; 0 ticks = 06:00, not original computer LocalTime','radiation':'Server-authoritative ambient danger, independent of mask protection; model RAD 0 / RAD ! plus HUD','lifecycle':'Persistent NBT; login/clone/respawn/dimension/tracking sync; ordinary death drop, keepInventory/NoDrop retention; binding curse respected'},
  'radiation':{'existingMechanic':'Retained: 3 HP/20 ticks; suitable mask or suit with MetroFilterTicks > 0 protects; filter refill 6000 ticks, drains while worn','legacySource':'Existing radiation ID and grow(10) entity-intersection volume retained; scheduled ticks migrate old chunks','newZones':'Centre x+.5,y+5,z+.5, strict radius 5 on each axis; safe air always overrides; overlapping sources do not stack damage','loadedSourcesOnly':True},
  'medkit':'Byte-exact medkit_use_heavy.ogg, once on successful 40-tick server finish; volume adapted from 10 to 1',
  'inventory':'Vanilla comparison mode/buttons/messages removed; old preference ignored. Survival E and I use Metro; creative E item catalogue remains.',
  'hands':'Frozen approved revolver contacts/camera/matrices; 15 adjusted underhand Flan profiles and independent MCMOD AK foregrip; Steve/Alex skin hands retained',
  'adaptations':['Original collision texture visible in creative only; renderer rebuilt on creative/survival change. Radiation/air editor icons drawn for port.','Respawn plate uses original pass.png and native forced spawn point in current dimension at block precision, not original floating-point coordinates.','No invented crafting/exchange recipes or compatibility with unrelated Flan ammunition.','Watch uses extracted original clock/digit faces and exact atlas bytes, not original fixed arm/perch skin.'],
  'excludedByUser':['Global 1000 HP','Exact original armor protection/wear formulas','Former missing-resource task 7: LastDays flashes, Barrett_Hazard and 25 Exodus textures'],
  'notVerified':'No Minecraft/OpenGL client or dedicated-server gameplay acceptance; follow TESTING.md'
 })
 from mcmod_languages import update
 update()
if __name__=='__main__':
 (A/'textures/mcmod/support').mkdir(parents=True,exist_ok=True);main()
