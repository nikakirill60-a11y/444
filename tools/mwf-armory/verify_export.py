#!/usr/bin/env python3
"""Verify Armory-generated pack resources and native selected-JAR schemas/glTF reader."""
import argparse,base64,hashlib,io,json,math,struct,sys,zipfile
from pathlib import Path
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
from verify_uboinik_pack import ClassSchema,TARGET

def verify(pack,mwf,jvm=None):
 with zipfile.ZipFile(pack) as z:
  assert z.testzip() is None
  assert len(set(z.namelist()))==len(z.namelist())
  files={n:z.read(n) for n in z.namelist()}
 assert not any(n.startswith('/') or '\\' in n or '..' in n.split('/') or n.endswith(('.jar','.class')) for n in files)
 def js(p):return json.loads(files[p])
 for p in files:
  if p.endswith(('.json','.gltf','.mcmeta')):js(p)
 asset='assets/modularwarfare/';records=[];gltfs=set();guns=[];attachments=[]
 for p in files:
  if p.startswith('guns/') and p.count('/')==1 and p.endswith('.json'):
   t=js(p);id=t['internalName'];guns.append(id);assert t['animationType']=='enhanced';assert t['internalAmmoStorage']>0
   records.append(('com/modularwarfare/common/guns/GunType',t));cfg=js(f'guns/render/{id}.render.json');records.append(('com/modularwarfare/client/fpp/enhanced/configs/GunEnhancedRenderConfig',cfg))
   for a in cfg['animations'].values():assert a['speed']>0 and a['endTime']>a['startTime']>=0
   path=asset+'gltf/guns/'+cfg['modelFileName'];gltfs.add(path);g=js(path);names={n['name'] for n in g['nodes']}
   assert {'gunModel','leftArmModel','rightArmModel'}<=names
   for skin in t['modelSkins']:assert files[asset+'skins/guns/'+skin.get('skinAsset',skin['internalName'])+'.png'].startswith(b'\x89PNG')
   for bullet in t['acceptedBullets']:assert js('bullets/'+bullet+'.json')['internalName']==bullet
   for slot,ids in t.get('acceptedAttachments',{}).items():
    for id in ids:
     at=js('attachments/'+id+'.json');assert at['attachmentType']==slot
     if id in cfg.get('attachment',{}):assert cfg['attachment'][id].get('binding','gunModel') in names
   for slot,id in t.get('defaultAttachments',{}).items():assert id in t['acceptedAttachments'][slot]
   for lists in t.get('weaponSoundMap',{}).values():
    sounds=js(asset+'sounds.json')
    for sound in lists:
     key=sound['soundName'];assert key in sounds
     for s in sounds[key]['sounds']:
      n=s if isinstance(s,str) else s['name'];assert files[asset+'sounds/'+n.replace('modularwarfare:','')+'.ogg'].startswith(b'OggS')
  if p.startswith('attachments/') and p.count('/')==1 and p.endswith('.json'):
   t=js(p);id=t['internalName'];attachments.append(id);r=js(f'attachments/render/{id}.render.json');records.extend([('com/modularwarfare/common/guns/AttachmentType',t),('com/modularwarfare/client/fpp/basic/configs/AttachmentRenderConfig',r)])
   obj=files['obj/attachments/'+r['modelFileName']].decode();assert 'o attachmentModel\n' in obj;verts=0
   for line in obj.splitlines():
    if line.startswith('v '):verts+=1
    if line.startswith('f '):assert len(line.split())==4 and all(0<int(v.split('/')[0])<=verts for v in line.split()[1:])
   for skin in t['modelSkins']:assert files[asset+'skins/attachments/'+skin.get('skinAsset',skin['internalName'])+'.png'].startswith(b'\x89PNG')
  if p.startswith('bullets/') and p.count('/')==1 and p.endswith('.json'):
   b=js(p);records.append(('com/modularwarfare/common/guns/BulletType',b));assert 'obj/shells/'+b['shellModelFileName'] in files
 with zipfile.ZipFile(mwf) as jar:
  assert hashlib.sha256(Path(mwf).read_bytes()).hexdigest()==TARGET
  cache={}
  def fields(cls):
   if cls not in cache:
    c=ClassSchema(jar.read(cls+'.class'));cache[cls]={**(fields(c.parent) if c.parent and c.parent!='java/lang/Object' else {}),**c.fields}
   return cache[cls]
  def record(cls,data):
   actual=fields(cls);assert set(data)<=set(actual),(cls,set(data)-set(actual))
   for key,v in data.items():
    desc=actual[key]
    if v is None:continue
    if desc in ('F','D','I','Ljava/lang/Integer;','Ljava/lang/Float;','Ljava/lang/Double;'):assert isinstance(v,(int,float)) and not isinstance(v,bool) and math.isfinite(v),(key,v)
    if desc in ('I','Ljava/lang/Integer;'):assert isinstance(v,int),(key,v)
    if desc=='Z':assert isinstance(v,bool),(key,v)
    if isinstance(v,dict) and desc.startswith(('Lcom/modularwarfare/','Lorg/joml/')):record(desc[1:-1],v)
    if key=='modelSkins':
     for skin in v:record('com/modularwarfare/common/guns/SkinType',skin)
    if cls.endswith('GunEnhancedRenderConfig') and key in ('attachment','attachmentGroup'):
     for x in v.values():record(cls+('$Attachment' if key=='attachment' else '$AttachmentGroup'),x)
    if cls.endswith('GunEnhancedRenderConfig') and key=='animations':
     for x in v.values():record(cls+'$Animation',x)
  for cls,data in records:record(cls,data)
  sound_constants={x for x in ClassSchema(jar.read('com/modularwarfare/common/guns/WeaponSoundType.class')).cp if isinstance(x,str)}
  for id in guns:
   for key in js(f'guns/{id}.json').get('weaponSoundMap',{}):assert key in sound_constants
 for p in gltfs:
  g=js(p);assert len(g['nodes'])==len({n['name'] for n in g['nodes']});buffers=[base64.b64decode(b['uri'].split(',',1)[1],validate=True) for b in g['buffers']]
  for b,data in zip(g['buffers'],buffers):assert len(data)==b['byteLength']
  arrays=[]
  for a in g['accessors']:
   v=g['bufferViews'][a['bufferView']];assert a.get('byteOffset',0)==0 and not v.get('byteStride');fmt={5126:'f',5123:'H'}[a['componentType']];count=a['count']*{'SCALAR':1,'VEC2':2,'VEC3':3,'VEC4':4}[a['type']];raw=buffers[v['buffer']][v.get('byteOffset',0):v.get('byteOffset',0)+v['byteLength']];assert len(raw)==struct.calcsize('<'+fmt*count);vals=struct.unpack('<'+fmt*count,raw);assert all(math.isfinite(x) for x in vals);arrays.append(vals)
  for mesh in g['meshes']:
   assert mesh['primitives']
   for prim in mesh['primitives']:
    assert prim['mode']==4;assert g['accessors'][prim['indices']]['componentType']==5123;assert max(arrays[prim['indices']])<g['accessors'][prim['attributes']['POSITION']]['count'];assert g['materials'][prim['material']]['name']
  for a in g['animations']:
   for c in a['channels']:
    s=a['samplers'][c['sampler']];ts=arrays[s['input']];assert all(ts[i]<ts[i+1] for i in range(len(ts)-1));assert g['accessors'][s['input']]['count']==g['accessors'][s['output']]['count']
 if jvm:
  import jpype
  if not jpype.isJVMStarted():jpype.startJVM(str(jvm),'-Xmx256m',classpath=[str(Path(mwf).resolve())])
  for p in gltfs:
   stream=jpype.JClass('java.io.ByteArrayInputStream')(files[p])
   try:
    model=jpype.JClass('de.javagl.jgltf.model.io.GltfModelReader')().readWithoutReferences(stream);assert len(model.getNodeModels())==len(js(p)['nodes'])
   finally:stream.close()
 print('Armory export verified:',len(files),'files;',len(guns),'guns;',len(attachments),'attachments;',len(gltfs),'native glTFs; actual selected-JAR fields/resources/indices/animation keys', '+ JAR reader' if jvm else '')
 return files
if __name__=='__main__':
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('pack',type=Path);p.add_argument('--mwf-jar',required=True,type=Path);p.add_argument('--jvm',type=Path);a=p.parse_args();verify(a.pack,a.mwf_jar,a.jvm)
