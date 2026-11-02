#!/usr/bin/env python3
"""Validate every full-pack registry, resource, scene and optionally the exact closed JAR.
This is an offline validator, NOT an in-game rendering/handling test.
"""
import argparse, base64, hashlib, io, json, math, re, struct, zipfile
from pathlib import Path
from PIL import Image
from verify_uboinik_pack import ClassSchema, TARGET
ROOT=Path(__file__).resolve().parents[1]
ASSET='assets/modularwarfare/'


def verify(pack,jar_path=None,jvm=None):
    with zipfile.ZipFile(pack) as z:
        assert z.testzip() is None
        assert len(z.namelist())==len(set(z.namelist()))
        files={n:z.read(n) for n in z.namelist()}
    def read(n):assert n in files,'Missing '+n;return files[n]
    def js(n):return json.loads(read(n))
    def records(folder):return {js(n)['internalName']:js(n) for n in files if re.fullmatch(folder+r'/[^/]+\.json',n)}
    assert not any(n.endswith(('.jar','.class')) or n.startswith('/') or '..' in Path(n).parts for n in files)
    for n,b in files.items():
        if n.endswith(('.json','.gltf','.mcmeta')):json.loads(b)
    manifest=js('FULL_PACK_MANIFEST.json');assert manifest['targetSha256']==TARGET
    assert not manifest['companionMod'] and not manifest['specialMechanicsImplemented']
    guns=records('guns');mods=records('attachments');mags=records('ammo');bullets=records('bullets')
    assert [len(x) for x in (guns,mods,mags,bullets)]==[23,31,21,9]
    assert len(set(guns)|set(mods)|set(mags)|set(bullets))==sum(map(len,(guns,mods,mags,bullets)))
    assert len(manifest['inventoryGuns'])==23
    assert len({g['sourceId'] for g in manifest['inventoryGuns']})==23
    assert {g['internalName'] for g in manifest['inventoryGuns']}==set(guns)
    assert len(manifest['sourceModuleMap'])==28
    assert set(manifest['sourceModuleMap'].values())<=mods.keys()
    accepted={id for g in guns.values() for ids in g['acceptedAttachments'].values() for id in ids}
    assert set(manifest['sourceModuleMap'].values())<=accepted,'Uninstallable source module'
    assert guns['metro_duplet']['internalAmmoStorage']==2 and 'metro_duplet_4' not in guns
    assert 'metro_duplet_extra_barrels' in guns['metro_duplet']['acceptedAttachments']['barrel']
    assert 'metro_mod_auto_fire' not in guns['metro_abzac'].get('defaultAttachments',{}).values()
    assert guns['metro_abzac']['fireModes']==['semi'],'Do not pretend the decorative AutoFire switches modes'
    schemas=[];gltfs={};triangle_count=0;native_rounds=0
    slots={'sight','slide','grip','flashlight','charm','skin','barrel','stock','laser'}
    sounds=js(ASSET+'sounds.json')
    for category,items in [('guns',guns),('attachments',mods),('ammo',mags),('bullets',bullets)]:
        for id,item in items.items():
            assert id==item['internalName']
            for skin in item['modelSkins']:
                with Image.open(io.BytesIO(read(ASSET+f'skins/{category}/'+skin['skinAsset']+'.png'))) as image:
                    assert image.format=='PNG';assert image.width<=4096 and image.height<=4096
            model=js(ASSET+f'models/item/{id}.json');tex=model['textures']['layer0'];assert tex.startswith('modularwarfare:')
            read(ASSET+'textures/'+tex.split(':',1)[1]+'.png')
            for lang in ('ru_ru','ru_RU','en_us','en_US'):
                assert f'item.{id}.name=' in read(ASSET+f'lang/{lang}.lang').decode()
            cls={'guns':'GunType','attachments':'AttachmentType','ammo':'AmmoType','bullets':'BulletType'}[category]
            schemas.append(('com/modularwarfare/common/guns/'+cls,item))
            if category=='guns':
                r=js(f'guns/render/{id}.render.json');schemas.append(('com/modularwarfare/client/fpp/enhanced/configs/GunEnhancedRenderConfig',r))
                assert item['weaponType'] in {'rifle','pistol','shotgun','boltsniper','semisniper'}
                assert bool(item.get('acceptedAmmo'))!=bool(item.get('acceptedBullets'))
                if item.get('acceptedAmmo'):assert set(item['acceptedAmmo'])<=mags.keys()
                else:assert set(item['acceptedBullets'])<=bullets.keys() and item['defaultBullet'] in bullets and item['internalAmmoStorage']>0
                for slot,ids in item['acceptedAttachments'].items():
                    assert slot in slots and len(ids)==len(set(ids))
                    for mid in ids:assert mid in mods and mods[mid]['attachmentType']==slot
                for event,entries in item['weaponSoundMap'].items():
                    for snd in entries:
                        assert snd['soundEvent']==event
                        for sound in sounds[snd['soundName']]['sounds']:assert read(ASSET+'sounds/'+sound['name'].split(':',1)[1]+'.ogg').startswith(b'OggS')
                path=ASSET+'gltf/guns/'+r['modelFileName'];g=js(path);gltfs[path]=g
                assert g['asset']['version']=='2.0';names=[n['name'] for n in g['nodes']];assert len(names)==len(set(names));assert 'gunModel' in names
                assert {'leftArmModel','rightArmModel','leftArmSlimModel','rightArmSlimModel'}<=set(names)
                assert set(r.get('defaultHidePart',[]))<=set(names)
                for binding,cfg in r.get('attachment',{}).items():
                    assert binding in mods or binding in mags
                    assert cfg.get('binding','gunModel') in names
                    assert set(cfg.get('hidePart',[])+cfg.get('showPart',[]))<=set(names)
                    if cfg.get('showPart'):assert set(cfg['showPart'])<=set(r['defaultHidePart'])
                buffers=[]
                for b in g['buffers']:
                    assert b['uri'].startswith('data:application/octet-stream;base64,');raw=base64.b64decode(b['uri'].split(',',1)[1],validate=True);assert len(raw)==b['byteLength'];buffers.append(raw)
                arrays=[]
                for a in g['accessors']:
                    assert a.get('byteOffset',0)==0
                    v=g['bufferViews'][a['bufferView']];assert not v.get('byteStride')
                    kind,size={5126:('f',4),5123:('H',2)}[a['componentType']];num={'SCALAR':1,'VEC2':2,'VEC3':3,'VEC4':4}[a['type']]*a['count']
                    assert v['byteLength']==size*num
                    raw=buffers[v['buffer']][v.get('byteOffset',0):v.get('byteOffset',0)+v['byteLength']];values=struct.unpack('<'+kind*num,raw);assert all(math.isfinite(v) for v in values);arrays.append(values)
                for mesh in g['meshes']:
                    assert mesh['primitives']
                    for p in mesh['primitives']:
                        assert p['mode']==4;attrs=p['attributes'];assert {'POSITION','NORMAL','TEXCOORD_0'}<=attrs.keys()
                        count=g['accessors'][attrs['POSITION']]['count'];indices=arrays[p['indices']];assert len(indices)%3==0 and max(indices)<count<=65535
                        assert count==g['accessors'][attrs['NORMAL']]['count']==g['accessors'][attrs['TEXCOORD_0']]['count'];triangle_count+=len(indices)//3
                        if id not in ('metro_duplet','metro_uboinik'):assert min(arrays[attrs['TEXCOORD_0']])>=-1e-6 and max(arrays[attrs['TEXCOORD_0']])<=1.00001
                assert len(g['animations'])==1
                end=0
                for a in g['animations'][0]['samplers']:
                    t=arrays[a['input']];assert all(t[i]<t[i+1] for i in range(len(t)-1));end=max(end,max(t))
                    assert g['accessors'][a['input']]['count']==g['accessors'][a['output']]['count']
                for a in r['animations'].values():assert 0<=a['startTime']<a['endTime'] and a['speed']>0 and a['endTime']/r['FPS']<=end+1e-6
            elif category in ('attachments','ammo'):
                folder='attachments' if category=='attachments' else 'ammo';r=js(f'{folder}/render/{id}.render.json')
                schemas.append(('com/modularwarfare/client/fpp/basic/configs/'+('AttachmentRenderConfig' if category=='attachments' else 'AmmoRenderConfig'),r))
                obj=read(f'obj/{folder}/'+r['modelFileName']).decode();assert 'o '+('attachmentModel' if category=='attachments' else 'ammoModel')+'\n' in obj
                for line in obj.splitlines():
                    if line.startswith('f '):assert len(line.split())==4 and all(int(v.split('/')[0])>0 for v in line.split()[1:])
                if category=='ammo':
                    assert item['ammoCapacity']>0 and item['magazineCount']==1 and item['isDynamicAmmo'];assert set(item['subAmmo'])<=bullets.keys()
                    indices=[int(i) for i in re.findall(r'^o bulletModel_(\d+)$',obj,re.M)];assert len(indices)==len(set(indices)) and all(0<=i<item['ammoCapacity'] for i in indices)
                    if indices:assert max(indices)==item['ammoCapacity']-1,'First shot must remove a native visible round'
                    native_rounds+=len(indices)
            else:read('obj/shells/'+item['shellModelFileName'])
    # The baseline weapons, arm nodes, poses, skins and three Ubo attachments must remain byte-identical.
    with zipfile.ZipFile(ROOT/'downloads/Metro_Uboinik_Duplet_MWF_fix1_v2.zip') as baseline:
        for n in baseline.namelist():
            if n.startswith(('guns/metro_uboinik','guns/metro_duplet.json','guns/render/metro_uboinik','guns/render/metro_duplet.render','attachments/metro_uboinik','attachments/render/metro_uboinik','assets/modularwarfare/gltf/','assets/modularwarfare/skins/','obj/attachments/')):
                assert read(n)==baseline.read(n),'Baseline was unexpectedly modified: '+n
    if jar_path:
        assert hashlib.sha256(Path(jar_path).read_bytes()).hexdigest()==TARGET
        with zipfile.ZipFile(jar_path) as jar:
            memo={}
            def fields(cls):
                if cls not in memo:
                    c=ClassSchema(jar.read(cls+'.class'));memo[cls]={**(fields(c.parent) if c.parent not in (None,'java/lang/Object') else {}),**c.fields}
                return memo[cls]
            def check(cls,record):
                fs=fields(cls);assert set(record)<=fs.keys(),(cls,set(record)-fs.keys())
                for k,v in record.items():
                    typ=fs[k]
                    if isinstance(v,dict) and typ.startswith(('Lcom/modularwarfare/','Lorg/joml/')):check(typ[1:-1],v)
                    if isinstance(v,str) and typ.startswith('Lcom/modularwarfare/common/guns/'):
                        c=ClassSchema(jar.read(typ[1:-1]+'.class'))
                        if c.parent=='java/lang/Enum':assert v in c.cp,('Unknown target enum',cls,k,v)
            for cls,record in schemas:check(cls,record)
        if jvm:
            import jpype
            if not jpype.isJVMStarted():jpype.startJVM(str(jvm),'-Xmx512m',classpath=[str(Path(jar_path).resolve())])
            reader=jpype.JClass('de.javagl.jgltf.model.io.GltfModelReader')()
            for path,g in gltfs.items():
                stream=jpype.JClass('java.io.ByteArrayInputStream')(files[path])
                try:
                    model=reader.readWithoutReferences(stream);assert len(model.getNodeModels())==len(g['nodes']);assert len(model.getAnimationModels())==1
                finally:stream.close()
            print('Actual selected-JAR glTF loader passed:',len(gltfs),'models')
    result={'files':len(files),'guns':len(guns),'attachments':len(mods),'ammo':len(mags),'bullets':len(bullets),'triangles':triangle_count,'nativeMagazineRounds':native_rounds,'gameTested':False}
    print(json.dumps(result,ensure_ascii=False));return result


if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('pack',type=Path);p.add_argument('--mwf-jar',type=Path);p.add_argument('--jvm',type=Path);a=p.parse_args();verify(a.pack,a.mwf_jar,a.jvm)
