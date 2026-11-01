#!/usr/bin/env python3
"""Exact original survival OBJ/MTL/texture assets; no firearm renderer dependencies."""
import gzip,json,re,shutil
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
S=ROOT/'tools/blackrazid-reference/gadgets';A=ROOT/'src/main/resources/assets/metroport'
def bounds(path):
 vs=[list(map(float,line.split()[1:])) for line in path.read_text().splitlines() if line.startswith('v ')]
 return [min(v[i] for v in vs) for i in range(3)]+[max(v[i] for v in vs) for i in range(3)]
def main():
 meshes={}
 for name in ['flashlight','flyashka','dynamo','meh','strelka']:
  path=S/(name+'.obj');materials={};material=None
  for line in path.with_suffix('.mtl').read_text().splitlines():
   if line.startswith('newmtl '):material=line[7:].strip()
   elif line.strip().startswith('map_Kd '):
    texture=line.strip()[7:];assert (S/texture).is_file();materials[material]='metroport:textures/blackrazid/gadgets/'+texture
    dest=A/'textures/blackrazid/gadgets'/texture;dest.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(S/texture,dest)
  used=set(re.findall(r'^usemtl (.+)',path.read_text(),re.M));assert used<=materials.keys()
  dest=A/'models/blackrazid/gadgets'/(name+'.obj.gz');dest.parent.mkdir(parents=True,exist_ok=True);dest.write_bytes(gzip.compress(path.read_bytes(),mtime=0))
  b=bounds(path);meshes[name]=dict(materials=materials,bounds=b,center=[round((b[i]+b[i+3])/2,6) for i in range(3)])
 for key,body in [('blackrazid_lamp','flashlight'),('blackrazid_flyashka','flyashka'),('blackrazid_power','dynamo')]:
  icon=json.loads((A/'models/item'/(key+'.json')).read_text())
  icon['parent']='item/handheld';icon['display']={'firstperson_righthand':dict(rotation=[0,-45,20],translation=[0,1,0],scale=[.85]*3),'firstperson_lefthand':dict(rotation=[0,45,-20],translation=[0,1,0],scale=[.85]*3),'thirdperson_righthand':dict(rotation=[0,0,0],translation=[0,2,0],scale=[.65]*3),'thirdperson_lefthand':dict(rotation=[0,0,0],translation=[0,2,0],scale=[.65]*3),'ground':dict(rotation=[0,0,0],translation=[0,2,0],scale=[.5]*3)}
  (A/'models/item'/(key+'.json')).write_text(json.dumps(icon,ensure_ascii=False,indent=2)+'\n')
 shutil.copyfile(S/'clock.png',A/'textures/blackrazid/gadgets/clock.png')
 (A/'blackrazid_gadget_meshes.json').write_text(json.dumps(meshes,ensure_ascii=False,indent=2)+'\n')
 (A/'models/item/blackrazid_watch.json').write_text(json.dumps({'parent':'item/handheld','textures':{'layer0':'metroport:blackrazid/gadgets/watch_icon'}},indent=2)+'\n')
 # An inventory preview cut from the original clock atlas; the 3D body remains original boxes.
 from PIL import Image
 im=Image.open(S/'clock.png').convert('RGBA');im.crop((290,264,390,364)).resize((64,64)).save(A/'textures/blackrazid/gadgets/watch_icon.png')
 print('Imported five exact survival OBJ meshes, original materials and clock texture.')
if __name__=='__main__':main()
