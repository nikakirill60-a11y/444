#!/usr/bin/env python3
"""Verify both Duplet variants and all five attachments with the selected real JAR."""
import argparse, base64, hashlib, json, math, struct, zipfile
from pathlib import Path
from verify_uboinik_pack import verify as verify_ubo, ClassSchema, TARGET
from build_duplet_pack import BASE, FOUR, MODULE, ATTACHMENTS, SOURCE_REF, originals
ROOT=Path(__file__).resolve().parents[1]
def verify(pack,mwf,jvm=None):
 verify_ubo(pack,mwf,jvm)
 with zipfile.ZipFile(pack) as z:files={n:z.read(n) for n in z.namelist()}
 def js(n):return json.loads(files[n])
 asset='assets/modularwarfare/';g=js(asset+'gltf/guns/'+BASE+'.gltf')
 assert len(g['nodes'])==7 and len(g['meshes'])==6
 assert len(g['animations'])==1 and len({n['name'] for n in g['nodes']})==7
 # Exact original geometry counts; no fake cartridges or arbitrary chopped barrel assembly.
 assert g['accessors'][g['meshes'][0]['primitives'][0]['indices']]['count']//3==5286
 assert not any(n['name'].startswith('bulletModel_') for n in g['nodes'])
 buffers=[base64.b64decode(b['uri'].split(',',1)[1],validate=True) for b in g['buffers']]
 sizes={'SCALAR':1,'VEC2':2,'VEC3':3,'VEC4':4};arrays=[]
 for a in g['accessors']:
  v=g['bufferViews'][a['bufferView']];size=sizes[a['type']];fmt='f' if a['componentType']==5126 else 'H';n=a['count']*size
  assert a.get('byteOffset',0)==0 and not v.get('byteStride')
  raw=buffers[v['buffer']][v.get('byteOffset',0):v.get('byteOffset',0)+v['byteLength']];assert len(raw)==struct.calcsize('<'+fmt*n)
  vals=struct.unpack('<'+fmt*n,raw);assert all(math.isfinite(v) for v in vals);arrays.append(vals)
 for mesh in g['meshes']:
  for p in mesh['primitives']:
   assert p['mode']==4 and g['accessors'][p['indices']]['componentType']==5123
   assert max(arrays[p['indices']])<g['accessors'][p['attributes']['POSITION']]['count']
 for a in g['animations']:
  for c in a['channels']:
   s=a['samplers'][c['sampler']];ts=arrays[s['input']];assert all(ts[i]<ts[i+1] for i in range(len(ts)-1));assert g['accessors'][s['input']]['count']==g['accessors'][s['output']]['count']
 records=[]
 for id,count,duration in [(BASE,2,3),(FOUR,4,4.8)]:
  gun=js('guns/'+id+'.json');cfg=js('guns/render/'+id+'.render.json')
  assert gun['internalAmmoStorage']==count and gun['reloadTime']==round(duration*20)
  assert gun['acceptedBullets']==['metro_12g_buckshot'] and gun['numBullets']==8 and gun['fireModes']==['semi']
  assert set(sum(gun['acceptedAttachments'].values(),[]))==set(ATTACHMENTS)
  assert cfg['modelFileName']==BASE+'.gltf' and gun['modelSkins'][0]['skinAsset']==BASE
  r=cfg['animations'];seconds=sum((r[n]['endTime']-r[n]['startTime'])/(24*r[n]['speed'])*k for n,k in [('preReloadEmpty',1),('reloadFirstEmpty',count),('reloadSecondEmpty',count-1),('postReloadEmpty',1)])
  assert math.isclose(seconds,duration,abs_tol=1e-10)
  assert js(asset+'models/item/'+id+'.json')['display']['thirdperson_righthand']['scale']==[0,0,0]
  records.extend([('com/modularwarfare/common/guns/GunType',gun),('com/modularwarfare/client/fpp/enhanced/configs/GunEnhancedRenderConfig',cfg)])
 assert js('guns/'+FOUR+'.json')['defaultAttachments']=={'barrel':MODULE}
 manifest=js('DUPLET_SOURCE_MANIFEST.json');assert manifest['sourceCommit']==SOURCE_REF and manifest['capacityBridgeRequired']
 src=originals()
 assert files[asset+'skins/guns/'+BASE+'.png']==src['texture.png']
 for id,(obj,tex,slot,name) in ATTACHMENTS.items():
  entry=js('attachments/'+id+'.json');r=js('attachments/render/'+id+'.render.json')
  assert entry['attachmentType']==slot and files[asset+'skins/attachments/'+id+'.png']==src[tex]
  assert 'o attachmentModel\n' in files['obj/attachments/'+r['modelFileName']].decode()
  for line in files['obj/attachments/'+r['modelFileName']].decode().splitlines():
   if line.startswith('f '):assert len(line.split())==4 and all(int(v.split('/')[0])>0 for v in line.split()[1:])
  records.extend([('com/modularwarfare/common/guns/AttachmentType',entry),('com/modularwarfare/client/fpp/basic/configs/AttachmentRenderConfig',r)])
 for source in manifest['sourceFiles']:assert hashlib.sha256(src[Path(source['path']).name]).hexdigest()==source['sha256']
 # Every existing Uboinik resource is byte-identical except four extended language tables.
 with zipfile.ZipFile(ROOT/'downloads/Metro_Uboinik_MWF_fix1_v5.zip') as z:
  for n in z.namelist():
   if not n.endswith('.lang'):assert files[n]==z.read(n),n
 with zipfile.ZipFile(mwf) as z:
  assert hashlib.sha256(Path(mwf).read_bytes()).hexdigest()==TARGET
  def fields(cls):
   c=ClassSchema(z.read(cls+'.class'));return {**(fields(c.parent) if c.parent and c.parent!='java/lang/Object' else {}),**c.fields}
  def record(cls,data):
   actual=fields(cls);assert set(data)<=set(actual),(cls,set(data)-set(actual))
   for key,value in data.items():
    descriptor=actual[key]
    if isinstance(value,dict) and descriptor.startswith(('Lcom/modularwarfare/','Lorg/joml/')):record(descriptor[1:-1],value)
  for cls,data in records:record(cls,data)
  for cls in ['com/modularwarfare/api/WeaponAttachmentEvent$Load','com/modularwarfare/api/WeaponAttachmentEvent$Unload']:
   assert 'Lnet/minecraftforge/fml/common/eventhandler/Cancelable;' in ClassSchema(z.read(cls+'.class')).cp
  cp=ClassSchema(z.read('com/modularwarfare/common/handler/ServerTickHandler.class')).cp
  assert all(n in cp for n in ['playerReloadCooldown','reloadEnhancedTask'])
 if jvm:
  import jpype
  if not jpype.isJVMStarted():jpype.startJVM(str(jvm),classpath=[str(Path(mwf).resolve())])
  stream=jpype.JClass('java.io.ByteArrayInputStream')(files[asset+'gltf/guns/'+BASE+'.gltf'])
  try:
   model=jpype.JClass('de.javagl.jgltf.model.io.GltfModelReader')().readWithoutReferences(stream)
   assert len(model.getNodeModels())==7 and len(model.getMeshModels())==6
  finally:stream.close()
 print('Duplet verified:',len(files),'files; 2/4 rounds; 5 attachments; real-JAR schemas/API and 7-node/6-mesh glTF; Uboinik v5 retained')
 return len(files)
if __name__=='__main__':
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('pack',type=Path);p.add_argument('--mwf-jar',type=Path,required=True);p.add_argument('--jvm',type=Path);a=p.parse_args();verify(a.pack,a.mwf_jar,a.jvm)
