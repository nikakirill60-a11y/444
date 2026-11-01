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
 from mcmod_languages import update
 update()
if __name__=='__main__':
 (A/'textures/mcmod/support').mkdir(parents=True,exist_ok=True);main()
