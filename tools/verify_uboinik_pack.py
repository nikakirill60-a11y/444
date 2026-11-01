#!/usr/bin/env python3
"""Check pack resources and MWF-specific glTF constraints; optionally use actual JAR's glTF reader."""
import argparse, base64, hashlib, json, math, os, struct, zipfile
from pathlib import Path
TARGET='f91a5419cbc9e3cbd18f831e37c17aa2833d577142d324fed1ca14846ce95183'

class ClassSchema:
    def __init__(self,b):
        self.b=b;self.i=8;self.cp=[None];count=self.u2()
        while len(self.cp)<count:
            tag=self.u1()
            if tag==1:self.cp.append(self.read(self.u2()).decode(errors='replace'))
            elif tag in [3,4]:self.cp.append(self.read(4))
            elif tag in [5,6]:self.cp.extend([self.read(8),None])
            elif tag in [7,8,16,19,20]:self.cp.append(self.u2())
            elif tag in [9,10,11,12,17,18]:self.cp.append((self.u2(),self.u2()))
            elif tag==15:self.cp.append((self.u1(),self.u2()))
            else:raise AssertionError('Unknown constant-pool tag '+str(tag))
        self.u2();self.u2();parent=self.u2();self.parent=self.cp[self.cp[parent]] if parent else None
        self.read(2*self.u2());self.fields={}
        for _ in range(self.u2()):
            flags=self.u2();name=self.cp[self.u2()];typ=self.cp[self.u2()]
            for _ in range(self.u2()):self.u2();self.read(self.u4())
            if not flags&0x1008:self.fields[name]=typ # ignore static and synthetic fields
    def read(self,n):r=self.b[self.i:self.i+n];self.i+=n;return r
    def u1(self):return self.read(1)[0]
    def u2(self):return struct.unpack('>H',self.read(2))[0]
    def u4(self):return struct.unpack('>I',self.read(4))[0]

def verify(path,mwf_jar=None,jvm=None):
    path=Path(path)
    if path.is_dir():files={p.relative_to(path).as_posix():p.read_bytes() for p in path.rglob('*') if p.is_file()}
    else:
        with zipfile.ZipFile(path) as z:
            assert z.testzip() is None,'ZIP CRC error';assert len(z.namelist())==len(set(z.namelist())),'Duplicate ZIP paths';files={n:z.read(n) for n in z.namelist()}
    def read(p):assert p in files,'Missing '+p;return files[p]
    def js(p):return json.loads(read(p))
    assert not any(n.startswith('/') or '..' in Path(n).parts or n.endswith(('.class','.jar')) for n in files),'Unexpected code/path in content pack'
    for n,b in files.items():
        if n.endswith(('.json','.gltf','.mcmeta')):json.loads(b)
    asset='assets/modularwarfare/'
    gun=js('guns/metro_uboinik.json');bullet=js('bullets/metro_12g_buckshot.json');config=js('guns/render/metro_uboinik.render.json')
    assert gun['animationType']=='enhanced' and gun['internalAmmoStorage']==6
    assert gun['acceptedBullets']==[bullet['internalName']] and 'acceptedAmmo' not in gun
    assert gun['numBullets']==8 and bullet['isDynamicBullet'] is False
    assert gun['fireModes']==['semi']
    for category,entry in [('guns',gun),('bullets',bullet)]:
        for skin in entry['modelSkins']:
            # CustomItemRenderer resolves skins/%s/%s.png, NOT textures/skins/...
            b=read(asset+'skins/'+category+'/'+skin['skinAsset']+'.png');assert b.startswith(b'\x89PNG\r\n\x1a\n')
    assert not any(n.startswith(asset+'textures/skins/') for n in files),'Obsolete skin directory: MWF binds modularwarfare:skins/...'
    assert gun['dropBulletCasing'] is False
    read('obj/shells/'+bullet['shellModelFileName']) # always loaded by BulletType, even without casing effects
    g=js(asset+'gltf/guns/'+config['modelFileName']);assert g['asset']['version']=='2.0'
    names=[n['name'] for n in g['nodes']];assert len(names)==len(set(names)),'HE-GLTF requires unique node names'
    rounds={n for n in names if n.startswith('bulletModel_')};assert rounds=={'bulletModel_'+str(i) for i in range(6)}
    assert {'root','gunModel','flashModel','leftArmModel','rightArmModel','leftArmSlimModel','rightArmSlimModel'}<=set(names)
    buffers=[]
    for b in g['buffers']:
        assert b['uri'].startswith('data:application/octet-stream;base64,'),'HE-GLTF readWithoutReferences needs embedded buffers'
        raw=base64.b64decode(b['uri'].split(',',1)[1],validate=True);assert len(raw)==b['byteLength'];buffers.append(raw)
    sizes={'SCALAR':1,'VEC2':2,'VEC3':3,'VEC4':4};types={5126:('f',4),5123:('H',2)}
    arrays=[]
    for a in g['accessors']:
        assert a.get('byteOffset',0)==0,'HE-GLTF reads buffer views without applying accessor byte offsets'
        v=g['bufferViews'][a['bufferView']];assert not v.get('byteStride'),'Use dedicated tightly packed buffer views'
        fmt,size=types[a['componentType']];count=a['count']*sizes[a['type']]
        assert v['byteLength']==count*size
        raw=buffers[v['buffer']][v.get('byteOffset',0):v.get('byteOffset',0)+v['byteLength']]
        assert len(raw)==v['byteLength'];values=struct.unpack('<'+fmt*count,raw);assert all(math.isfinite(x) for x in values)
        arrays.append(values)
    triangles=0
    for mesh in g['meshes']:
        for p in mesh['primitives']:
            assert p['mode']==4 and 'indices' in p,'HE-GLTF requires TRIANGLES and an index accessor'
            attrs=p['attributes'];assert {'POSITION','NORMAL','TEXCOORD_0'}<=set(attrs)
            n=g['accessors'][attrs['POSITION']]['count'];assert n==g['accessors'][attrs['NORMAL']]['count']==g['accessors'][attrs['TEXCOORD_0']]['count']
            indices=arrays[p['indices']];assert len(indices)%3==0 and max(indices)<n;triangles+=len(indices)//3
            assert 'material' in p and 'name' in g['materials'][p['material']]
    assert len(g['animations'])==1,'HE-GLTF merges node channels from clips'
    for anim in g['animations']:
        for channel in anim['channels']:
            assert channel['target']['path'] in ['translation','rotation','scale']
            sampler=anim['samplers'][channel['sampler']];times=arrays[sampler['input']]
            assert all(times[i]<times[i+1] for i in range(len(times)-1));assert g['accessors'][sampler['input']]['count']==g['accessors'][sampler['output']]['count']
    for action in ['default','draw','aim','fire','postFire','preFire','sprint','preLoad','load','postLoad','preUnload','unload','postUnload']:
        a=config['animations'][action];assert 0<=a['startTime']<a['endTime'];assert a['endTime']/config['FPS']<=max(times)+1e-6
    schemas={'com/modularwarfare/common/guns/GunType':gun,'com/modularwarfare/common/guns/BulletType':bullet,
             'com/modularwarfare/client/fpp/enhanced/configs/GunEnhancedRenderConfig':config}
    ids=['metro_uboinik','metro_12g_buckshot']
    for category,attachments in gun['acceptedAttachments'].items():
        assert category in ['barrel','sight','laser']
        for id in attachments:
            ids.append(id);entry=js('attachments/'+id+'.json');assert entry['attachmentType']==category
            r=js('attachments/render/'+id+'.render.json');obj=read('obj/attachments/'+r['modelFileName']).decode();assert 'o attachmentModel\n' in obj
            # MWF's own ObjModelBuilder accepts only positive triangle/quad faces.
            for l in obj.splitlines():
                if l.startswith('f '):assert len(l.split())==4 and all(int(v.split('/')[0])>0 for v in l.split()[1:])
            for skin in entry['modelSkins']:read(asset+'skins/attachments/'+skin['skinAsset']+'.png')
            assert config['attachment'][id]['binding'] in names
            schemas['com/modularwarfare/common/guns/AttachmentType#'+id]=entry
            schemas['com/modularwarfare/client/fpp/basic/configs/AttachmentRenderConfig#'+id]=r
    for id in ids:
        model=js(asset+'models/item/'+id+'.json');tex=model['textures']['layer0'].split(':',1)[1];read(asset+'textures/'+tex+'.png')
    sounds=js(asset+'sounds.json')
    for entries in gun['weaponSoundMap'].values():
        for entry in entries:
            assert entry['soundName'] in sounds
            for sound in sounds[entry['soundName']]['sounds']:
                b=read(asset+'sounds/'+sound['name'].split(':',1)[1]+'.ogg');assert b.startswith(b'OggS')
    assert js('SOURCE_MANIFEST.json')['targetSha256']==TARGET
    if mwf_jar:
        assert hashlib.sha256(Path(mwf_jar).read_bytes()).hexdigest()==TARGET,'Not the selected closed JAR'
        with zipfile.ZipFile(mwf_jar) as jar:
            binding=ClassSchema(jar.read('com/modularwarfare/client/fpp/basic/models/objects/CustomItemRenderer.class'))
            assert 'skins/%s/%s.png' in binding.cp,'Target renderer skin path convention changed'
            def fields(name):
                c=ClassSchema(jar.read(name+'.class'));return {**(fields(c.parent) if c.parent and c.parent!='java/lang/Object' else {}),**c.fields}
            def check_record(cls,record):
                actual=fields(cls);assert set(record)<=set(actual),'Unknown actual-JAR fields in '+cls+': '+str(set(record)-set(actual))
                for key,value in record.items():
                    descriptor=actual[key]
                    if isinstance(value,dict) and descriptor.startswith(('Lcom/modularwarfare/','Lorg/joml/')):
                        check_record(descriptor[1:-1],value)
            for cls,record in schemas.items():check_record(cls.split('#')[0],record)
            animation_constants=set(x for x in ClassSchema(jar.read('com/modularwarfare/client/fpp/enhanced/AnimationType.class')).cp if isinstance(x,str))
            assert set(config['animations'])<=animation_constants,'Unknown animation action names in selected JAR'
            sound_constants=set(x for x in ClassSchema(jar.read('com/modularwarfare/common/guns/WeaponSoundType.class')).cp if isinstance(x,str))
            assert set(gun['weaponSoundMap'])<=sound_constants,'Unknown sound event names in selected JAR'
        if jvm:
            import jpype
            if not jpype.isJVMStarted():jpype.startJVM(str(jvm),'-Xmx256m',classpath=[str(Path(mwf_jar).resolve())])
            stream=jpype.JClass('java.io.ByteArrayInputStream')(read(asset+'gltf/guns/'+config['modelFileName']))
            try:
                model=jpype.JClass('de.javagl.jgltf.model.io.GltfModelReader')().readWithoutReferences(stream)
                assert len(model.getNodeModels())==len(g['nodes'])
                assert len(model.getAnimationModels())==1
                print('Selected JAR glTF reader passed:',len(model.getNodeModels()),'nodes,',len(model.getMeshModels()),'meshes')
            finally:stream.close()
    print('Pack verified:',len(files),'files;',len(rounds),'visible rounds;',triangles,'triangles;',len(ids),'items')
    return {'files':len(files),'rounds':len(rounds),'triangles':triangles,'items':len(ids)}

if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('pack',type=Path);p.add_argument('--mwf-jar',type=Path);p.add_argument('--jvm',type=Path)
    a=p.parse_args();verify(a.pack,a.mwf_jar,a.jvm)
