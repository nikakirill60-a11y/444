#!/usr/bin/env python3
"""Import the user's weapon.zip melee meshes without replacing the older MCMOD catalogue."""
import hashlib,json,sys
from pathlib import Path
from PIL import Image
import io,zipfile
from build_uboinik_pack import obj_read,icon
from integrate_mwf_3d import transformed,png
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'
SPECS=[('hammer','hammer/props_hammer.obj','Молоток','Hammer','rare',1200,2,3,5,-2.6,350,.12),
       ('crowbar','Crowbar/props_crow-bar.obj','Монтировка','Crowbar','epic',1600,1,4,6,-2.4,500,.18),
       ('axe','Axe/props_axe.obj','Топор','Axe','legendary',2200,2,4,8,-3,650,.18)]
def write(path,data):
    path.parent.mkdir(parents=True,exist_ok=True);path.write_bytes(data)
def main():
    z=zipfile.ZipFile(ROOT/'tools/weapon3/sources.zip');entries=[]
    for id,path,ru,en,tier,grams,w,h,damage,speed,durability,grip in SPECS:
        mesh=z.read(path);texture=z.read(path.rsplit('/',1)[0]+'/props_instru.png')
        write(A/f'models/legacy/mcmod_tools/{id}.obj',mesh)
        write(A/f'textures/mcmod/tools/{id}.png',texture)
        raw=sum(obj_read(mesh).values(),[])
        # Source tools lie along Z; stand them upright with the original handle in the palm.
        triangles=transformed(raw,rotation=[[1,0,0],[0,0,1],[0,-1,0]])
        write(A/f'textures/items/{id}.png',png(icon(triangles,Image.open(io.BytesIO(texture)),256)))
        write(A/f'models/item/{id}.json',(json.dumps({'parent':'item/handheld','textures':{'layer0':'metroport:items/'+id}},indent=2)+'\n').encode())
        entries.append(dict(id=id,nameRu=ru,nameEn=en,rarity=tier,grams=grams,width=w,height=h,damage=damage,attackSpeed=speed,durability=durability,gripOffset=grip,triangles=len(raw),source=path,objSha256=hashlib.sha256(mesh).hexdigest(),textureSha256=hashlib.sha256(texture).hexdigest()))
    write(A/'melee_weapons.json',(json.dumps(entries,ensure_ascii=False,indent=2)+'\n').encode())
if __name__=='__main__':main()
