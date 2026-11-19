#!/usr/bin/env python3
"""Pinned v10 -> v11. Source-owned guns, explicit gameplay rebalance, no donor runtime swap."""
import argparse, base64, copy, hashlib, importlib.util, io, json, os, subprocess, sys, zipfile
from pathlib import Path
import numpy as np
from PIL import Image
from build_uboinik_pack import obj_read, icon
from build_full_mwf_pack import PackGltf, obj_bytes, wrap_triangle
from integrate_mwf_3d import transformed, bounds, mesh_bounds, png
from patch_mwf_geometry import append_mesh, texture_tile
from update_mwf_v10 import readzip, writezip, js, triangles, world
ROOT=Path(__file__).resolve().parents[1]
P='assets/modularwarfare/'
BASE_SHA='5f07771f1498dd57e9a5ca909ee8571ae20de61d3ddc40b5c01ab31f53d2801f'
PACK='Metro_Full_Arsenal_MWF_fix1_v11_guns2.zip'
JAR='modularwarfare-shining-2024.2.4.6f-fix1-metro7.jar'
# Damage per projectile, reload in server ticks. Existing pellet counts and ammo types remain untouched.
BALANCE={
 'abzac':(2.7,80),'ak':(4.5,56),'ak2012':(4.2,52),'akk':(4.5,56),'aksu':(3.8,54),
 'ashot':(1.8,36),'duplet':(2.5,72),'gatling':(4.2,120),'helsing':(6,45),
 'medved':(2.8,180),'ognemet':(3,90),'ognemet_mini':(2.6,65),'podonok':(3.8,50),
 'preved':(13,90),'railgun':(5,60),'revolver':(4,42),'rpk':(4.6,80),'saiga':(2.6,60),
 'tihar':(5.5,50),'ubludok':(3.2,64),'uboinik':(2.5,180),'velopal':(2.8,90),
 'ventil':(9.5,60),'vsv':(4.4,56),'vyhlop':(10.5,80)}
NEW={'metro_army_ak':('Армейский калаш','legendary'), 'metro_ubludok_old':('Старый Ублюдок','epic'),
     'metro_stallion':('Сталлион','exclusive'),'metro_duplet_2':('Дуплет — вариант 2','common')}

def sha(data):return hashlib.sha256(data).hexdigest()
def data(files,path):return json.loads(files[path])
def put(files,path,obj):files[path]=js(obj)
def skin(id):return [{'internalName':id,'skinAsset':id,'displayName':'Оригинал'}]
def gun(files,id):return data(files,'guns/'+id+'.json')
def render(files,id):return data(files,'guns/render/'+id+'.render.json')
def model(files,id):
 r=render(files,id);return data(files,P+'gltf/guns/'+r['modelFileName'])
def item(files,id):
 put(files,P+'models/item/'+id+'.json',{'parent':'item/generated','textures':{'layer0':'modularwarfare:items/'+id},
     'display':{'thirdperson_righthand':{'scale':[0,0,0]},'thirdperson_lefthand':{'scale':[0,0,0]}}})
def clone(files,old,new,name):
 g=copy.deepcopy(gun(files,old));g.update(internalName=new,displayName=name)
 g['modelSkins']=skin(new);put(files,'guns/'+new+'.json',g)
 r=copy.deepcopy(render(files,old));r['modelFileName']=new+'.gltf';put(files,'guns/render/'+new+'.render.json',r)
 files[P+'gltf/guns/'+new+'.gltf']=files[P+'gltf/guns/'+render(files,old)['modelFileName']]
 item(files,new);return g,r

def components(ts):
 parent=list(range(len(ts)));seen={}
 def find(i):
  while parent[i]!=i:parent[i]=parent[parent[i]];i=parent[i]
  return i
 for i,t in enumerate(ts):
  for p,uv,n in t:
   k=tuple(round(v,5) for v in p)
   if k in seen:parent[find(i)]=find(seen[k])
   seen[k]=i
 result={}
 for i,t in enumerate(ts):result.setdefault(find(i),[]).append(t)
 return list(result.values())

def replacement(files,base,id,raw,texture,magazine=None):
 """Replace visible gun meshes only, retain original root/hand animation accessors and pose."""
 d=model(files,base);anchor=next(n for n in d['nodes'] if n['name']=='gunModel');target=mesh_bounds(d,['gunModel'])
 rotation=np.array([[0,0,1],[0,1,0],[-1,0,0]],dtype=float)
 lo,hi=bounds(transformed(raw,rotation=rotation));a,b=target
 factor=float((b[0]-a[0])/(hi[0]-lo[0]));shift=np.array([(a[0]+b[0])/2-factor*(lo[0]+hi[0])/2,b[1]-factor*hi[1],-factor*(lo[2]+hi[2])/2])
 def convert(ts,gltf=True):
  out=[]
  for tri in transformed(ts,factor,shift,rotation):
   for t in wrap_triangle(tri):out.append([(p,(u,1-v) if gltf else (u,v),n) for p,(u,v),n in t])
  return out
 for n in d['nodes']:
  if 'Arm' not in n['name']:n.pop('mesh',None)
 trs={k:copy.deepcopy(anchor[k]) for k in ('translation','rotation','scale') if k in anchor}
 index=append_mesh(d,'v11Replacement',convert(raw),trs)
 anchor['mesh']=d['nodes'][index]['mesh'];d['nodes'][0]['children'].remove(index);d['nodes'].pop()
 # Empty anchors remain to keep every original animation channel valid; no old cylinders/modules render.
 if magazine is not None:
  ammo=next(n for n in d['nodes'] if n['name']=='ammoModel');ammo.clear();ammo.update(name='ammoModel',**trs)
  files['obj/ammo/'+id+'_ammo.obj']=obj_bytes([('ammoModel',convert(magazine,False))])
 put(files,P+'gltf/guns/'+id+'.gltf',d)
 files[P+'skins/guns/'+id+'.png']=png(texture)
 r=render(files,id);r.update(attachment={},defaultHidePart=[])
 if magazine is not None:r['attachment'][id+'_ammo']={'translate':dict(x=0,y=0,z=0)}
 put(files,'guns/render/'+id+'.render.json',r)
 return {'sourceToModelScale':factor,'shift':shift.tolist(),'rotationY':90,'bodyTriangles':len(raw),
         'magazineTriangles':len(magazine or []),'handAndRootAnimationPreserved':True,'originalAnimationFileNotProvided':True},convert

def prepare(files,source):
 before=copy.copy(files);report={'basePackSha256':BASE_SHA,'sourceZipSha256':sha((ROOT/'tools/metro-guns2/sources.zip').read_bytes()),
  'minecraftLiveTested':False,'rebalance':{},'newGuns':{},'limitations':[
   'No Minecraft/Forge/OpenGL playtest. Geometry fit and gameplay values are adaptations, not recovered Metro data.',
   'New OBJ guns inherit existing hand/root-motion timelines; source archive contains no skeletal animation.',
   'Stallion is the base semi-auto seven-round version; no invented full-auto/extended-mag upgrade variants.',
   'Stallion uses seven-round internal ammunition storage with existing Magnum rounds; no detachable Stallion magazine item.',
   'Army AK uses the Kalash model and original UV-correct Kalash skin: supplied wpn_aka is an incompatible AKSU/RPK unwrap; source retained, not applied blindly.']}
 for short,(damage,reload) in BALANCE.items():
  id='metro_'+short;g=gun(files,id);r=render(files,id);old=g['reloadTime']
  report['rebalance'][id]={'name':g['displayName'],'damageBefore':g['gunDamage'],'damageAfter':damage,
      'pelletsUnchanged':g['numBullets'],'reloadTicksBefore':old,'reloadTicksAfter':reload}
  g.update(gunDamage=damage,reloadTime=reload);put(files,'guns/'+id+'.json',g)
  # Enhanced MWF uses render ranges/speeds as well as GunType.reloadTime.
  for name,a in r.get('animations',{}).items():
   if any(word in name.lower() for word in ('reload','load','unload')):a['speed']=a.get('speed',1)*old/reload
  put(files,'guns/render/'+id+'.render.json',r)
 # Kalash model/sockets/skins intentionally identical: the supplied different unwrap must not corrupt UVs.
 id='metro_army_ak';g,r=clone(files,'metro_ak',id,NEW[id][0]);g['modelSkins']=copy.deepcopy(gun(files,'metro_ak')['modelSkins'])
 g.update(gunDamage=4.8,bulletSpread=.55,recoilPitch=1.5,recoilYaw=.5)
 put(files,'guns/'+id+'.json',g)
 report['newGuns'][id]={'rarity':'legendary','model':'identical Kalash mesh/UV, all existing attachments and magazines',
    'unusedAlternateTexture':'Army Kalash/wpn_aka.1024.png (different UV layout; retained in sources.zip)'}
 # Independent item ID with the same updated Duplet config, model, modules, and mechanics.
 id='metro_duplet_2';g,r=clone(files,'metro_duplet',id,NEW[id][0])
 files[P+'skins/guns/'+id+'.png']=source['Metro guns 2/duplet_2_variant.png']
 report['newGuns'][id]={'rarity':'common','sameGameplayAs':'metro_duplet','textureSha256':sha(files[P+'skins/guns/'+id+'.png'])}
 # Old Bastard: separate native side magazine, not the current Bastard's magazine or attachments.
 id='metro_ubludok_old';g,r=clone(files,'metro_ubludok',id,NEW[id][0]);groups=obj_read(source['Metro guns 2/Ublud old/ubludok.obj'])
 body=[];mag=[]
 for ts in components(groups['wpn_ubludok']):
  lo,hi=bounds(ts)
  ismag=(lo[2]>=.069-1e-5 and hi[2]<=.1052 and lo[1]>=-.008 and hi[1]<=.017) or (lo[2]>=.069 and hi[2]<=.0703 and hi[0]>.002)
  (mag if ismag else body).extend(ts)
 assert 300<len(mag)<700,(len(mag),len(body))
 # Bullets are not baked into the body or magazine: an empty magazine must not show immutable cartridges.
 tex=Image.open(io.BytesIO(source['Metro guns 2/Ublud old/wpn_ubludok.1024.png'])).convert('RGBA')
 geom,convert=replacement(files,'metro_ubludok',id,body,tex,mag)
 g.update(acceptedAttachments={},acceptedAmmo=[id+'_ammo'],gunDamage=3.9,bulletSpread=.85,roundsPerMin=400)
 put(files,'guns/'+id+'.json',g)
 a=data(files,'ammo/metro_ubludok_ammo.json');a.update(internalName=id+'_ammo',displayName='Магазин Старого Ублюдка',modelSkins=skin(id),ammoCapacity=30)
 put(files,'ammo/'+id+'_ammo.json',a);put(files,'ammo/render/'+id+'_ammo.render.json',{'modelFileName':id+'_ammo.obj','extra':{'modelScale':1}})
 files[P+'skins/ammo/'+id+'.png']=png(tex);item(files,id+'_ammo')
 files[P+'textures/items/'+id+'_ammo.png']=png(icon(convert(mag,False),tex,256))
 report['newGuns'][id]={'rarity':'epic','separateMagazine':id+'_ammo','capacity':30,'attachments':[],**geom}
 id='metro_stallion';g,r=clone(files,'metro_revolver',id,NEW[id][0]);raw=[t for ts in obj_read(source['Metro guns 2/Stallion/stall.obj']).values() for t in ts]
 tex=Image.open(io.BytesIO(source['Metro guns 2/Stallion/Material.003_Base_Color.png'])).convert('RGBA')
 # Keep full source texture in sources.zip; runtime skin 2048 max avoids a 4K/8K GPU allocation.
 tex.thumbnail((2048,2048),Image.Resampling.LANCZOS)
 geom,_=replacement(files,'metro_revolver',id,raw,tex)
 g.update(acceptedAttachments={},acceptedBullets=['metro_bullet_magnum'],defaultBullet='metro_bullet_magnum',
          internalAmmoStorage=7,gunDamage=5.2,roundsPerMin=300,bulletSpread=.55,recoilPitch=1.5,reloadTime=48)
 r=render(files,id)
 for name,a in r['animations'].items():
  if any(w in name.lower() for w in ('reload','load','unload')):a['speed']*=42/48
 put(files,'guns/'+id+'.json',g);put(files,'guns/render/'+id+'.render.json',r)
 report['newGuns'][id]={'rarity':'exclusive','ammo':'.44 Magnum','capacity':7,'fireModes':['semi'],
  'source':'https://metrovideogame.fandom.com/wiki/Stallion','statsAreGameplayAdaptation':True,**geom}
 # Genuine offline renders of the four new visible models, no changes to existing icon assets.
 for id,(name,tier) in NEW.items():
  d=model(files,id);r=render(files,id);g=gun(files,id);ts=list(triangles(d,r));tex=Image.open(io.BytesIO(files[P+'skins/guns/'+g['modelSkins'][0]['skinAsset']+'.png']))
  if id=='metro_ubludok_old':
   from integrate_mwf_3d import render_matrix
   node=next(n for n in d['nodes'] if n['name']=='ammoModel');m=world(d,d['nodes'].index(node))
   for tri in obj_read(files['obj/ammo/'+id+'_ammo.obj'])['ammoModel']:
    ts.append([((m@[*p,1])[:3],uv,n) for p,uv,n in tri])
  files[P+'textures/items/'+id+'.png']=png(icon(ts,tex,384).resize((256,256),Image.Resampling.LANCZOS))
  print('ICON',id,len(ts),flush=True)
 for path in [p for p in files if p.startswith(P+'lang/') and p.endswith('.lang')]:
  lines=['item.'+id+'.name='+name for id,(name,tier) in NEW.items()]+['item.metro_ubludok_old_ammo.name=Магазин Старого Ублюдка']
  files[path]=files[path].rstrip()+('\n'+'\n'.join(lines)+'\n').encode()
 manifest=data(files,'FULL_PACK_MANIFEST.json');manifest['inventoryGuns'] += [dict(sourceId='Metro guns 2',internalName=id,name=name) for id,(name,tier) in NEW.items()]
 manifest['counts']={cat:sum(n.startswith(cat+'/') and n.count('/')==1 and n.endswith('.json') for n in files) for cat in ('guns','ammo','bullets','attachments')}
 manifest['guns2Update']=report;put(files,'FULL_PACK_MANIFEST.json',manifest)
 report['changedFiles']=sorted(n for n in before if files[n]!=before[n]);report['addedFiles']=sorted(set(files)-set(before))
 return report

def patch_runtime(args):
 spec=importlib.util.spec_from_file_location('support',ROOT/'tools/mwf-mechanics/test_support.py');support=importlib.util.module_from_spec(spec);spec.loader.exec_module(support)
 work=args.work;work.mkdir(parents=True,exist_ok=True)
 stubs=work/'stubs';helpers=work/'helpers';tests=work/'tests'
 for p in [stubs,helpers,tests]:p.mkdir(exist_ok=True)
 def run(*cmd):subprocess.run(list(map(str,cmd)),check=True)
 facades=support.materialize(work/'stub-sources')
 # Motion packet regression requires a sequential byte/int buffer, not the single-byte pump fixture.
 (work/'stub-sources/io/netty/buffer/ByteBuf.java').write_text('''package io.netty.buffer; public class ByteBuf {
 private final java.nio.ByteBuffer b=java.nio.ByteBuffer.allocate(1024); private int read;
 public ByteBuf writeByte(int n){b.put((byte)n);return this;}public byte readByte(){return b.get(read++);}
 public short readUnsignedByte(){return(short)(readByte()&255);}public ByteBuf writeInt(int n){b.putInt(n);return this;}
 public int readInt(){int v=b.getInt(read);read+=4;return v;}}''')
 run('javac','--release','8','-proc:none','-d',stubs,*facades)
 cp=os.pathsep.join(map(str,[stubs,args.jar]));src=ROOT/'tools/mwf-mechanics/src/ru/metrocraft/mwf'
 run('javac','--release','8','-proc:none','-cp',cp,'-d',helpers,*[src/(n+'.java') for n in ['MechanicsRules','MechanicsHooks','AttachmentTransactions']])
 jarfiles=readzip(args.jar);assert sha(args.jar.read_bytes())=='240cf48fd2265104648de7ec0e3f56865aa4c1aa2ada7547dee0c63c5a50e3ab'
 replaced=[]
 for p in helpers.rglob('*.class'):
  name=p.relative_to(helpers).as_posix();assert name.startswith('ru/metrocraft/mwf/');jarfiles[name]=p.read_bytes();replaced.append(name)
 version='com/modularwarfare/ModularWarfare.class';assert b'2024.2.4.6f-metro6' in jarfiles[version]
 jarfiles[version]=jarfiles[version].replace(b'2024.2.4.6f-metro6',b'2024.2.4.6f-metro7')
 # Equal-length constant replacement updates advertised network version without rewriting unrelated native bytecode.
 out=args.output/JAR;writezip(out,jarfiles)
 backend=support.materialize(work/'backend',True);cp=os.pathsep.join(map(str,[tests,stubs,out]))
 test=ROOT/'tools/metro-guns2/DupletVariantTest.java'
 run('javac','--release','8','-proc:none','-cp',cp,'-d',tests,*backend,ROOT/'tools/mwf-mechanics/tests/MechanicsTest.java',ROOT/'tools/mwf-motion/tests/MotionTest.java',ROOT/'tools/mwf-v10/AshotPoseTest.java',test)
 # Native ammo methods are injected for isolated tests; never ship these test classes.
 # Use JDK17's bundled ASM only for test fixtures; helper bytecode remains Java8.
 tool=work/'tool';tool.mkdir(exist_ok=True)
 native=work/'NativeTestMethods.java'
 native.write_text((ROOT/'tools/mwf-motion/NativeTestMethods.java').read_text().replace('org.objectweb.asm','jdk.internal.org.objectweb.asm'))
 exports=['--add-exports=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED']
 run('javac',*exports,'-d',tool,native)
 run('java',*exports,'-cp',tool,'NativeTestMethods',out,tests)
 logs=[]
 profiles=json.loads(jarfiles[P+'metro_motion.json'])
 for name in ('DupletVariantTest','MotionTest','AshotPoseTest'):
  params=[] if name!='AshotPoseTest' else [str(v) for side in ('Left','Right') for v in profiles['metroMotionAshot'+side]['matrix']]
  result=subprocess.run(['java','-Xverify:all','-cp',cp,'ru.metrocraft.mwf.'+name,*params],check=True,text=True,capture_output=True)
  print(result.stdout,flush=True);logs.append(result.stdout)
 (args.output/'MWF_V11_native_tests.txt').write_text(''.join(logs)+'Tests use isolated API facades, NOT a live game.\n')
 original=readzip(args.jar)
 assert set(jarfiles)==set(original),'No compile facade or test classes may ship'
 assert {n for n in original if jarfiles[n]!=original[n]}<=set(replaced)|{version},'Unrelated runtime bytecode/resources must remain unchanged'
 for n in replaced:assert jarfiles[n][:8]==bytes.fromhex('cafebabe00000034'),'Java8 helper target'
 return out,replaced

def verify(files,old,report):
 guns={json.loads(b)['internalName']:json.loads(b) for n,b in files.items() if n.startswith('guns/') and n.count('/')==1 and n.endswith('.json')}
 assert len(guns)==29
 for id,g in guns.items():
  r=render(files,id);d=model(files,id);names=[n['name'] for n in d['nodes']];assert len(names)==len(set(names))
  assert g['internalName']==id and g['gunDamage']>0 and g['reloadTime']>0
  for k in ('acceptedAmmo','acceptedBullets'):
   for target in g.get(k,[]):assert ('ammo/' if k=='acceptedAmmo' else 'bullets/')+target+'.json' in files
  assert set(r.get('defaultHidePart',[]))<=set(names)
  for slot,items in g['acceptedAttachments'].items():
   for item in items:assert 'attachments/'+item+'.json' in files and item in r['attachment']
  for a in d.get('animations',[]):
   for c in a['channels']:assert c['target']['node']<len(d['nodes'])
  for a in d['accessors']:
   assert a['count']>0 and all(np.isfinite(a.get('min',[]))) and all(np.isfinite(a.get('max',[])))
 assert guns['metro_army_ak']['acceptedAttachments']==guns['metro_ak']['acceptedAttachments']
 assert guns['metro_ubludok_old']['acceptedAttachments']=={}
 assert set(guns['metro_ubludok_old']['acceptedAmmo']).isdisjoint(guns['metro_ubludok']['acceptedAmmo'])
 a=copy.deepcopy(guns['metro_duplet']);b=copy.deepcopy(guns['metro_duplet_2'])
 for obj in (a,b):
  for k in ('internalName','displayName','modelSkins'):obj.pop(k)
 assert a==b,'Variant must stay the same gameplay weapon with an independent registry ID'
 for n in old:
  if n.startswith(P+'gltf/') or n.startswith(P+'metro_motion') or n.startswith('obj/') or n.startswith(P+'skins/'):
   assert files[n]==old[n],'Existing geometry/hand poses must be byte-identical: '+n
 assert guns['metro_medved']['numBullets']==8 and guns['metro_medved']['defaultBullet']=='metro_12g_buckshot'
 assert files[P+'skins/guns/metro_duplet_2.png']!=files[P+'skins/guns/metro_duplet.png']
 report['verifiedGunCount']=len(guns);report['allOriginalGeometryByteIdentical']=True
 return report

def main():
 p=argparse.ArgumentParser();p.add_argument('--pack',type=Path,required=True);p.add_argument('--jar',type=Path);p.add_argument('--output',type=Path,default=ROOT/'.cache/output-v11');p.add_argument('--work',type=Path,default=ROOT/'.cache/compile-v11');a=p.parse_args()
 assert sha(a.pack.read_bytes())==BASE_SHA,'Expected exact published v10'
 a.output.mkdir(parents=True,exist_ok=True);files=readzip(a.pack);old=copy.copy(files)
 report=prepare(files,readzip(ROOT/'tools/metro-guns2/sources.zip'));verify(files,old,report)
 if a.jar:
  jar,replaced=patch_runtime(a);report.update(requiresJarSha256=sha(jar.read_bytes()),runtimeClassesRebuilt=replaced)
  m=data(files,'FULL_PACK_MANIFEST.json');m.update(mechanicsRequiresJarSha256=report['requiresJarSha256'],integratedMechanicsVersion='1.6.0');put(files,'FULL_PACK_MANIFEST.json',m)
  m=data(files,'MOTION_UPDATE_MANIFEST.json');m['requiresJarSha256']=report['requiresJarSha256'];put(files,'MOTION_UPDATE_MANIFEST.json',m)
 put(files,'GUNS2_V11_MANIFEST.json',report);writezip(a.output/PACK,files)
 report['packSha256']=sha((a.output/PACK).read_bytes());(a.output/'MWF_V11_report.json').write_bytes(js(report))
 print(json.dumps({'pack':PACK,'sha256':report['packSha256'],'guns':29,'minecraftLiveTested':False}))
if __name__=='__main__':main()
