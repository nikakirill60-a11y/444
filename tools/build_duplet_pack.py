#!/usr/bin/env python3
"""Add the user's Duplet to the verified Uboinik v5 ZIP, without altering its entries."""
import argparse, copy, hashlib, io, json, subprocess, zipfile
from pathlib import Path
from PIL import Image
import numpy as np
from build_uboinik_pack import Gltf, obj_read, obj_write, icon, arm_mesh, animations, jsonwrite, SCALE, TARGET_SHA
from uboinik_pose import source_pose, gun_transform, arm_transform, render_pose
ROOT=Path(__file__).resolve().parents[1]
SOURCE_REF='ce2783694767296a102221f69f622d35264924e4'
BASE='metro_duplet';FOUR=BASE+'_4';ROUND='metro_12g_buckshot';MODULE=BASE+'_extra_barrels'
ATTACHMENTS={
 MODULE:('barrel_Duplet (+2 ствола).obj','texture.png','barrel','Дуплет: +2 ствола'),
 BASE+'_suppressor':('barrel_Duplet (Глушитель).obj','obvestexture.png','barrel','Глушитель Дуплета'),
 BASE+'_long_barrel':('barrel_Duplet (удлененый ствол).obj','texture.png','barrel','Удлинитель стволов Дуплета'),
 BASE+'_grip':('grip_Duplet.obj','texture.png','grip','Рукоять Дуплета'),
 BASE+'_stock':('stock_Duplet.obj','texture.png','stock','Приклад Дуплета'),
}
def originals(ref=SOURCE_REF):
 paths=subprocess.check_output(['git','ls-tree','-rz','--name-only',ref,'--','Дуплет'],cwd=ROOT).decode().split('\0')
 return {Path(p).name:subprocess.check_output(['git','show',ref+':'+p],cwd=ROOT) for p in paths if p}
def duplet_pose():
 """Adapt the supplied arm rotations to the actual Duplet grip/fore-end coordinates."""
 pose=copy.deepcopy(source_pose());gt=gun_transform(pose,SCALE)
 # Model-space palm targets on the source grip and fore-end; provisional, user-editable.
 for side,target in [('right',[-6.1,-3.15,0]),('left',[.5,-3.5,0])]:
  pose['arms'][side+'Arm']['armScale']={'x':.7 if side=='left' else .6,'y':.85,'z':.7 if side=='left' else .6}
  t=arm_transform(pose,side);x=.375 if side=='left' else -.375
  palm=np.array([x,.70,0])*np.array(t['scale'])
  q=np.array(t['rotation']);xx,yy,zz,w=q
  r=np.array([[1-2*(yy*yy+zz*zz),2*(xx*yy-zz*w),2*(xx*zz+yy*w)],
              [2*(xx*yy+zz*w),1-2*(xx*xx+zz*zz),2*(yy*zz-xx*w)],
              [2*(xx*zz-yy*w),2*(yy*zz+xx*w),1-2*(xx*xx+yy*yy)]])
  current=np.array(t['translation'])+r@palm
  wanted=np.array(gt['translation'])+np.array(gt['scale'])*SCALE*np.array(target)
  delta=wanted-current
  for key in ['armPos','armReloadPos']:
   for i,axis in enumerate('xyz'):pose['arms'][side+'Arm'][key][axis]+=float(delta[i])
 return pose

def build(output,archive,ref=SOURCE_REF):
 src=originals(ref);output.mkdir(parents=True,exist_ok=True)
 base=ROOT/'downloads/Metro_Uboinik_MWF_fix1_v5.zip'
 with zipfile.ZipFile(base) as z:
  for name in z.namelist():
   p=output/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(z.read(name))
 asset=output/'assets/modularwarfare';body=Image.open(io.BytesIO(src['texture.png'])).convert('RGBA')
 p=asset/'skins/guns'/f'{BASE}.png';p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(src['texture.png'])
 groups=obj_read(src['Duplet.obj']);pose=duplet_pose();g=Gltf();g.doc['asset']['generator']='Metro Duplet deterministic source conversion';g.doc['materials'][0]['name']='duplet_skin'
 for name,tris in groups.items():
  scaled=[[(tuple(x*SCALE for x in p),uv,n) for p,uv,n in t] for t in tris]
  node=g.mesh(name,scaled,lambda uv:(uv[0],1-uv[1]));g.doc['nodes'][node].update(gun_transform(pose,SCALE))
 for side in ['left','right']:
  for slim in [False,True]:
   node=g.mesh(side+'Arm'+('Slim' if slim else '')+'Model',arm_mesh(side,slim));g.doc['nodes'][node].update(arm_transform(pose,side))
 ranges=animations(g,pose);g.doc['animations'][0]['name']='duplet_actions';g.finish(asset/'gltf/guns'/f'{BASE}.gltf')
 accepted={}
 for id,(_,_,slot,_) in ATTACHMENTS.items():accepted.setdefault(slot,[]).append(id)
 ubo=json.loads((output/'guns/metro_uboinik.json').read_text())
 icons={BASE:icon(groups['gunModel'],body)}
 extra=obj_read(src[ATTACHMENTS[MODULE][0]])['attachmentModel'];icons[FOUR]=icon(groups['gunModel']+extra,body)
 for id,count,seconds in [(BASE,2,3),(FOUR,4,4.8)]:
  gun=copy.deepcopy(ubo);gun.update(internalName=id,displayName='Дуплет' if count==2 else 'Дуплет (4 ствола)',internalAmmoStorage=count,reloadTime=round(seconds*20),roundsPerMin=150,bulletSpread=3,acceptedAttachments=accepted,modelSkins=[{'internalName':id,'skinAsset':BASE,'displayName':'Оригинал'}])
  if count==4:gun['defaultAttachments']={'barrel':MODULE}
  jsonwrite(output/'guns'/f'{id}.json',gun)
  r=copy.deepcopy(ranges);speed=(20+(2*count-1)*14)/(24*seconds)
  load_names={'preLoad','load','postLoad','preReload','preReloadEmpty','reloadFirst','reloadFirstEmpty','reloadSecond','reloadSecondEmpty','reloadFirstQuickly','reloadSecondQuickly','postReload','postReloadEmpty'}
  for name in load_names:r[name]['speed']=speed
  view_pose=render_pose(pose)
  # Shorter source gun: pull HIP away from the camera and raise it, preserving ADS.
  for axis,delta in [('x',.12),('y',.15)]:
   view_pose['aim']['translateHipPosition'][axis]+=delta;view_pose['aim']['translateAimPosition'][axis]-=delta
  if count==4:view_pose['aim']['translateAimPosition']['y']-=.08 # clearance for the upper barrel pair
  render={'modelFileName':BASE+'.gltf','FPS':24,'showHandArmorType':'none','animations':r,**view_pose,'specialEffect':{'oldFlashModel':True,'postSmokeFactor':0},'attachment':{id:{'binding':'gunModel','sightAimPosOffset':{'x':0,'y':0,'z':0}} for id in ATTACHMENTS}}
  jsonwrite(output/'guns/render'/f'{id}.render.json',render)
 for id,(obj,texture,slot,name) in ATTACHMENTS.items():
  tris=[t for ts in obj_read(src[obj]).values() for t in ts]
  p=output/'obj/attachments'/f'{id}.obj';p.parent.mkdir(parents=True,exist_ok=True);p.write_text(obj_write(tris),encoding='utf8')
  p=asset/'skins/attachments'/f'{id}.png';p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(src[texture])
  entry={'internalName':id,'displayName':name,'attachmentType':slot,'maxStackSize':1,'sameTextureAsGun':False,'modelSkins':[{'internalName':id,'skinAsset':id}]}
  if slot=='barrel':entry['barrel']={'isSuppressor':id.endswith('_suppressor'),'hideFlash':id.endswith('_suppressor'),'accuracyFactor':.85 if id.endswith('_long_barrel') else 1}
  if slot=='grip':entry['grip']={'recoilPitchFactor':.85,'recoilYawFactor':.9}
  if slot=='stock':entry['stock']={'recoilPitchFactor':.8,'recoilYawFactor':.85,'aimSpeedFactor':1}
  jsonwrite(output/'attachments'/f'{id}.json',entry)
  jsonwrite(output/'attachments/render'/f'{id}.render.json',{'modelFileName':id+'.obj','extra':{'modelScale':1}})
  icons[id]=icon(tris,Image.open(io.BytesIO(src[texture])))
 for id,image in icons.items():
  p=asset/'textures/items'/f'{id}.png';p.parent.mkdir(parents=True,exist_ok=True);image.save(p)
  jsonwrite(asset/'models/item'/f'{id}.json',{'parent':'item/generated','textures':{'layer0':'modularwarfare:items/'+id},'display':{k:{'scale':[0,0,0] if id in [BASE,FOUR] else [.4,.4,.4]} for k in ['thirdperson_righthand','thirdperson_lefthand']}})
 names={BASE:'Дуплет',FOUR:'Дуплет (4 ствола)',**{id:v[3] for id,v in ATTACHMENTS.items()}}
 for lang in ['ru_ru','ru_RU','en_us','en_US']:
  p=asset/'lang'/f'{lang}.lang';old=p.read_text();p.write_text(old+'\n'+'\n'.join(f'item.{id}.name={name}' for id,name in names.items())+'\n',encoding='utf8')
 manifest={'sourceCommit':ref,'sourceFolder':'Дуплет','targetSha256':TARGET_SHA,'sourceFiles':[{'path':'Дуплет/'+n,'bytes':len(b),'sha256':hashlib.sha256(b).hexdigest()} for n,b in sorted(src.items())],
 'textures':{id:v[1] for id,v in ATTACHMENTS.items()},'capacityBridgeRequired':True,'unloadedOnlyAttachmentChanges':True,'sourceAnimationsProvided':False,'sourceSoundsProvided':False,
 'pose':'Provisional source-geometry palm targets; edit in Pose Studio. Not game-validated.','soundReuse':'Existing Uboinik v5 shotgun sounds, unchanged original bytes. No Duplet OGG supplied.',
 'geometry':'Original merged gunModel retained. No invented break-action hinge or visible cartridge meshes.'}
 jsonwrite(output/'DUPLET_SOURCE_MANIFEST.json',manifest)
 (output/'README_DUPLET_RU.txt').write_text(README,encoding='utf8')
 archive.parent.mkdir(parents=True,exist_ok=True)
 with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for p in sorted(output.rglob('*')):
   if p.is_file():
    info=zipfile.ZipInfo(p.relative_to(output).as_posix(),(2026,11,2,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;info.external_attr=0o644<<16;z.writestr(info,p.read_bytes())
 print('Built',archive,archive.stat().st_size,'bytes')
 return output
README='''ДУПЛЕТ + УБОЙНИК v5 / Minecraft 1.12.2 / MWF 2024.2.4.6f-fix1

Это ОБЩИЙ пак: Убойник v5 сохранён без изменения его моделей/конфигов.
Полностью уберите прежний Metro_Uboinik ZIP/распакованные дубликаты и замените
его ЭТИМ ZIP целиком в .minecraft/ModularWarfare/. Не держите оба пака одновременно.

Для рабочего съёмного дополнения +2 ствола положите также
metro-duplet-bridge-1.0.0.jar в .minecraft/mods/ (на сервере тоже).
Закрытый MWF JAR НЕ менять. Без переходника ёмкость по обвесу не переключается.

Дуплет — 2 патрона. Установленный модуль +2 ствола — 4 патрона.
Внутри используются два зарегистрированных варианта; переходник меняет
предмет и сохраняет его NBT. Стрельба — один патрон за нажатие, 8 дробин,
общие патроны с Убойником. Это стартовый баланс, не исходные ТТХ.
В креативе есть и вариант «Дуплет (4 ствола)» с модулем по умолчанию.

СМЕНА ЛЮБОГО ОБВЕСА — ТОЛЬКО НА ПОЛНОСТЬЮ РАЗРЯЖЕННОМ ДУПЛЕТЕ и вне
перезарядки/выгрузки. Сначала выгрузите патроны штатным действием MWF.
Если нет места для возвращаемого обвеса, операция отменяется целиком.
Переходник не изменяет общий GunType/internalAmmoStorage для всех игроков.
В одном слоте barrel: +2 ствола ИЛИ глушитель ИЛИ удлинитель; вместе нельзя.
Рукоять и приклад — отдельные слоты grip и stock.

Текстуры: оружие/+2/удлинитель/рукоять/приклад — исходная texture.png;
глушитель — исходная obvestexture.png, по ссылкам MTL.
Исходные OBJ/MTL/PNG из папки «Дуплет» в main. Авторство не меняется.
Звуки для Дуплета не загружены — временно используются звуки Убойника.
Исходных анимаций и отдельного шарнира нет: базовые движения и руки адаптированы,
слитый gunModel не разрезан для выдуманной переломки. Видимые патроны/экстракция
не добавлены: отдельных исходных деталей патронов в Дуплете нет.
Поза стартовая, рассчитана по рукояти/цевью; её нужно проверить в игре.
Номинальная полная зарядка: 3 с (2 патрона), 4.8 с (4 патрона).

Проверки структуры/JAR/модульные тесты НЕ заменяют запуск Minecraft.
Совместимость с другими модами и визуальная посадка требуют игровой проверки.
OpenGL 1281 этим паком не исправляется.
'''
if __name__=='__main__':
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('--output',type=Path,default=ROOT/'artifacts/duplet-contentpack');p.add_argument('--zip',type=Path,default=ROOT/'downloads/Metro_Uboinik_Duplet_MWF_fix1_v1.zip');p.add_argument('--source-ref',default=SOURCE_REF);a=p.parse_args();build(a.output,a.zip,a.source_ref)
