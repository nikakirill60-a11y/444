#!/usr/bin/env python3
"""v12 corrective build: valid native fire modes, per-item textures, complete magazine Stallion.
Builds from pinned v10 plus the preserved supplied sources, using the v11 rebalance.
"""
import argparse,base64,copy,hashlib,io,json,os,subprocess,zipfile
from pathlib import Path
from types import SimpleNamespace
import numpy as np
from PIL import Image
import build_mwf_v11 as v11
from build_uboinik_pack import obj_read,icon,Gltf
from build_full_mwf_pack import obj_bytes,wrap_triangle
from integrate_mwf_3d import bounds,transformed,png,render_matrix
from patch_mwf_geometry import append_mesh
from update_mwf_v10 import readzip,writezip,js,triangles,world,accessor
from mwf_local_textures import split
ROOT=Path(__file__).resolve().parents[1];P=v11.P
PACK='Metro_Full_Arsenal_MWF_fix1_v12_separate_textures.zip'

def primitive_names(doc):
    """MWF stores each node's meshes in a Map keyed by material NAME, not primitive index.
    Unique keys prevent a second 20k-triangle chunk silently replacing the first.
    """
    for mi,mesh in enumerate(doc['meshes']):
        for pi,prim in enumerate(mesh['primitives']):
            mat=copy.deepcopy(doc['materials'][prim.get('material',0)])
            mat['name']=f'metro_m{mi}_p{pi}';prim['material']=len(doc['materials']);doc['materials'].append(mat)

def add_track(doc,node,times,values,path='translation'):
    part=Gltf();ti=part.accessor([[t] for t in times],1);vi=part.accessor(values,3 if path!='rotation' else 4)
    bo,vo,ao=len(doc['buffers']),len(doc['bufferViews']),len(doc['accessors'])
    doc['buffers'].append({'byteLength':len(part.data),'uri':'data:application/octet-stream;base64,'+base64.b64encode(part.data).decode()})
    for view in part.doc['bufferViews']:view['buffer']+=bo;doc['bufferViews'].append(view)
    for a in part.doc['accessors']:a['bufferView']+=vo;doc['accessors'].append(a)
    animation=doc['animations'][0]
    animation['channels']=[c for c in animation['channels'] if not(c['target']['node']==node and c['target']['path']==path)]
    si=len(animation['samplers']);animation['samplers'].append({'input':ti+ao,'output':vi+ao,'interpolation':'LINEAR'})
    animation['channels'].append({'sampler':si,'target':{'node':node,'path':path}})

def box(lo,hi):
    corners=[(x,y,z) for x in (lo[0],hi[0]) for y in (lo[1],hi[1]) for z in (lo[2],hi[2])];out=[]
    for a,b,c,d,n in [(0,1,3,2,(-1,0,0)),(4,6,7,5,(1,0,0)),(0,4,5,1,(0,-1,0)),(2,3,7,6,(0,1,0)),(0,2,6,4,(0,0,-1)),(1,5,7,3,(0,0,1))]:
        vs=[(corners[i],uv,n) for i,uv in zip((a,b,c,d),((.42,.15),(.45,.15),(.45,.22),(.42,.22)))]
        out.extend([[vs[0],vs[1],vs[2]],[vs[0],vs[2],vs[3]]])
    return out

def stallion(files,source):
    raw=sum(obj_read(source['Metro guns 2/Stallion/stall.obj']).values(),[])
    cs=sorted(v11.components(raw),key=len,reverse=True)
    # The supplied model is a single object but its original magazine baseplate is disconnected.
    plate=[c for c in cs if len(c)==68 and bounds(c)[1][1]<-.10]
    assert len(plate)==1
    upper=[c for c in cs if (len(c)==2398 or len(c) in (436,212))]
    assert sum(map(len,upper))==3046
    body=[t for c in cs if c is not plate[0] and all(c is not u for u in upper) for t in c]
    # Interior magazine shell is new adaptation geometry, hidden inside the grip when inserted.
    # Its original 68-triangle baseplate is preserved, NOT duplicated in the fixed gun body.
    mag=plate[0]+box((-.008,-.106,-.115),(.004,.003,-.077))
    d=v11.model(files,'metro_revolver');anchor=next(n for n in d['nodes'] if n['name']=='gunModel');trs={k:copy.deepcopy(anchor[k]) for k in ('translation','rotation','scale') if k in anchor}
    a,b=v11.mesh_bounds(d,['gunModel']);rot=np.array([[0,0,1],[0,1,0],[-1,0,0]],float)
    lo,hi=bounds(transformed(raw,rotation=rot));factor=float((b[0]-a[0])/(hi[0]-lo[0]));shift=np.array([(a[0]+b[0])/2-factor*(lo[0]+hi[0])/2,b[1]-factor*hi[1],-factor*(lo[2]+hi[2])/2])
    def convert(ts,gltf=True):
        return [[(p,(u,1-v) if gltf else (u,v),n) for p,(u,v),n in tri] for t in transformed(ts,factor,shift,rot) for tri in wrap_triangle(t)]
    for n in d['nodes']:
        if 'Arm' not in n['name']:n.pop('mesh',None)
    i=append_mesh(d,'stallionBody',convert(body),trs);anchor['mesh']=d['nodes'][i]['mesh'];d['nodes'][0]['children'].remove(i);d['nodes'].pop()
    slide=append_mesh(d,'slideModel',convert(sum(upper,[])),trs)
    ammo=next(i for i,n in enumerate(d['nodes']) if n['name']=='ammoModel');d['nodes'][ammo].clear();d['nodes'][ammo].update(name='ammoModel',**trs)
    # Native MWF owns the magazine visibility/transfer; this track only moves the anchor.
    base=np.array(trs.get('translation',[0,0,0]));scale=np.array(trs.get('scale',[1,1,1]));down=base+scale*np.array([0,-.15*factor,0])
    frames=[0,299,300,310,325,326,345,358,400]
    add_track(d,ammo,[f/24 for f in frames],[base.tolist(),base.tolist(),base.tolist(),base.tolist(),down.tolist(),down.tolist(),base.tolist(),base.tolist(),base.tolist()])
    back=base+scale*np.array([-.018*factor,0,0]);frames=[0,60,61,63,67,68,299,345,349,354,358,400]
    add_track(d,slide,[f/24 for f in frames],[base.tolist(),base.tolist(),base.tolist(),back.tolist(),base.tolist(),base.tolist(),base.tolist(),base.tolist(),back.tolist(),base.tolist(),base.tolist(),base.tolist()])
    # A fresh magazine-reload timeline: no per-round revolver load cycle.
    # Move the support hand toward the grip during magazine exchange, preserving idle/draw/fire.
    left=next(i for i,n in enumerate(d['nodes']) if n['name']=='leftArmModel')
    for path in ('translation','rotation'):
        anim=d['animations'][0];channel=next((c for c in anim['channels'] if c['target']=={'node':left,'path':path}),None)
        if not channel:continue
        sam=anim['samplers'][channel['sampler']];times=accessor(d,sam['input']).reshape(-1).tolist();vals=accessor(d,sam['output']).tolist()
        pairs=[(t,v) for t,v in zip(times,vals) if t<300/24]
        neutral=vals[0];reach=list(neutral)
        if path=='translation':reach[1]-=.00002;reach[2]+=.00001
        pairs += [(300/24,neutral),(310/24,reach),(345/24,reach),(358/24,neutral),(400/24,neutral)]
        add_track(d,left,[t for t,v in pairs],[v for t,v in pairs],path)
    r=v11.render(files,'metro_stallion');r['attachment']={'metro_stallion_ammo':{'translate':dict(x=0,y=0,z=0)}};r['defaultHidePart']=[]
    speed=58/(24*(48/20))
    for key,range_ in {'preReload':(300,310),'reloadFirst':(310,325),'reloadSecond':(325,345),'postReload':(345,358)}.items():
        for suffix in (('','Empty','Quickly') if key in ('reloadFirst','reloadSecond') else ('','Empty')):
            r['animations'][key+suffix]={'startTime':range_[0],'endTime':range_[1],'speed':speed}
    for name in ('preLoad','load','postLoad','preUnload','unload','postUnload'):r['animations'].pop(name,None)
    # Unload uses the same full-magazine, not per-round animation stages.
    for key,range_ in {'preUnload':(300,310),'unload':(310,325),'postUnload':(345,358)}.items():r['animations'][key]={'startTime':range_[0],'endTime':range_[1],'speed':speed}
    g=v11.gun(files,'metro_stallion')
    for key in ('acceptedBullets','defaultBullet','internalAmmoStorage'):g.pop(key,None)
    g['acceptedAmmo']=['metro_stallion_ammo']
    # Keep pistol report/reload sound within available original resources; don't reuse revolver loading audio.
    sm=v11.gun(files,'metro_ak')['weaponSoundMap']
    for key in list(g['weaponSoundMap']):
        if any(x in key.lower() for x in ('load','unload')) and key in sm:g['weaponSoundMap'][key]=copy.deepcopy(sm[key])
    files['guns/metro_stallion.json']=js(g);files['guns/render/metro_stallion.render.json']=js(r);files[P+'gltf/guns/metro_stallion.gltf']=js(d)
    a=json.loads(files['ammo/metro_ubludok_old_ammo.json']);a.update(internalName='metro_stallion_ammo',displayName='Магазин Сталлиона',ammoCapacity=7,subAmmo=['metro_bullet_magnum'],modelSkins=v11.skin('metro_stallion'))
    files['ammo/metro_stallion_ammo.json']=js(a);files['ammo/render/metro_stallion_ammo.render.json']=js({'modelFileName':'metro_stallion_ammo.obj','extra':{'modelScale':1}})
    files['obj/ammo/metro_stallion_ammo.obj']=obj_bytes([('ammoModel',convert(mag,False))]);files[P+'skins/ammo/metro_stallion.png']=files[P+'skins/guns/metro_stallion.png'];v11.item(files,'metro_stallion_ammo')
    tex=Image.open(io.BytesIO(files[P+'skins/guns/metro_stallion.png']));files[P+'textures/items/metro_stallion_ammo.png']=png(icon(convert(mag,False),tex,256))
    for name in list(files):
        if name.startswith(P+'lang/') and name.endswith('.lang'):files[name]+=b'\nitem.metro_stallion_ammo.name='+ 'Магазин Сталлиона\n'.encode()
    return {'sourceTriangles':len(raw),'bodyTriangles':len(body),'slideTriangles':3046,'originalMagazineBaseplateTriangles':68,'addedInteriorMagazineShellTriangles':12,'magazineCapacity':7,'nativeStorage':'acceptedAmmo','independentMagazineTimelineFrames':[300,358]}

def prepare(files,source,work):
    before=dict(files);prior=v11.prepare(files,source);v11.verify(files,before,prior)
    army=Image.open(io.BytesIO(source['Metro guns 2/Army Kalash/wpn_aka.1024.png'])).convert('RGBA')
    report={'minecraftLiveTested':False,'basePackSha256':v11.BASE_SHA,'sourceZipSha256':prior['sourceZipSha256'],'rebalance':prior['rebalance']}
    # Correct the spelling against the REAL closed JAR's WeaponFireMode enum (SEMI/FULL/BURST).
    for n in list(files):
        if n.startswith('guns/') and n.count('/')==1 and n.endswith('.json'):
            g=json.loads(files[n]);g['fireModes']=[{'auto':'full'}.get(m,m).lower() for m in g['fireModes']];assert set(g['fireModes'])<= {'semi','full','burst'};files[n]=js(g)
    report['separateItemTextures']=split(files,ROOT,work/'textures',army)
    report['stallion']=stallion(files,source)
    # Every primitive gets a distinct material key in each mesh, including existing multi-chunk models.
    for n in list(files):
        if n.startswith(P+'gltf/') and n.endswith('.gltf'):
            d=json.loads(files[n]);primitive_names(d);files[n]=js(d)
    # Re-render icons for all weapons from their current geometry and own skin.
    for n in sorted(files):
        if not n.startswith('guns/') or n.count('/')!=1 or not n.endswith('.json'):continue
        g=json.loads(files[n]);id=g['internalName'];d=v11.model(files,id);r=v11.render(files,id);ts=list(triangles(d,r));skin=g['modelSkins'][0]['skinAsset'];tex=Image.open(io.BytesIO(files[P+'skins/guns/'+skin+'.png']))
        # Dynamic native magazines are not part of the glTF gun meshes.
        for aid in g.get('acceptedAmmo',[])[:1]:
            ar=json.loads(files['ammo/render/'+aid+'.render.json']);a=json.loads(files['ammo/'+aid+'.json']);amskin=a['modelSkins'][0]['skinAsset']
            node=next((i for i,v in enumerate(d['nodes']) if v['name']=='ammoModel'),None)
            if node is not None:
                # Temporary software-render sheet ONLY, never saved as a pack texture.
                preview=Image.new('RGBA',(2048,1024));preview.paste(tex.resize((1024,1024)),(0,0))
                magtex=Image.open(io.BytesIO(files[P+'skins/ammo/'+amskin+'.png']));preview.paste(magtex.resize((1024,1024)),(1024,0))
                ts=[[(p,(uv[0]/2,uv[1]),n) for p,uv,n in t] for t in ts];tex=preview
                m=world(d,node)@render_matrix(r['attachment'].get(aid,{}));m[:3,:3]*=ar.get('extra',{}).get('modelScale',1)
                for group in obj_read(files['obj/ammo/'+ar['modelFileName']]).values():
                    ts.extend([[((m@[*p,1])[:3],((uv[0]+1)/2,uv[1]),n) for p,uv,n in t] for t in group])
        files[P+'textures/items/'+id+'.png']=png(icon(ts,tex,384).resize((256,256),Image.Resampling.LANCZOS))
    manifest=json.loads(files['FULL_PACK_MANIFEST.json']);manifest['counts']={cat:sum(n.startswith(cat+'/') and n.count('/')==1 and n.endswith('.json') for n in files) for cat in ('guns','ammo','bullets','attachments')};manifest['correctiveV12']=report
    # Preserve old provenance, but obsolete limitations are explicitly superseded by v12.
    manifest['limitations']=[s for s in manifest.get('limitations',[]) if 'Atlas material' not in s]
    files['FULL_PACK_MANIFEST.json']=js(manifest)
    verify(files,before,report);return report

def verify(files,baseline,report):
    assert not any(n.endswith('/metro_native_atlas.png') for n in files)
    ids=[]
    for n,b in files.items():
        if n.startswith(('guns/','ammo/','attachments/')) and n.count('/')==1 and n.endswith('.json'):
            g=json.loads(b)
            for s in g.get('modelSkins',[]):assert s['skinAsset']!='metro_native_atlas'
            if n.startswith('guns/'):
                ids.append(g['internalName']);assert all(m in ('semi','full','burst') for m in g['fireModes'])
                assert g['fireModes'] and all(g['fireModes'])
                for aid in g.get('acceptedAmmo',[]):assert 'ammo/'+aid+'.json' in files
        if n.startswith(P+'gltf/') and n.endswith('.gltf'):
            d=json.loads(b)
            for mesh in d['meshes']:
                names=[d['materials'][p['material']]['name'] for p in mesh['primitives']];assert len(names)==len(set(names)),n
    assert len(ids)==len(set(ids))==29
    for n,b in baseline.items():
        if not n.startswith(P+'gltf/') or not n.endswith('.gltf'):continue
        old=json.loads(b)
        if n.endswith('metro_stallion.gltf'):continue
        new=json.loads(files[n]);assert old['nodes']==new['nodes'];assert old.get('animations')==new.get('animations')
        for a,access in enumerate(old['accessors']):
            if access.get('type')=='VEC2':continue
            assert np.array_equal(accessor(old,a),accessor(new,a)),n
    g=v11.gun(files,'metro_stallion');assert 'internalAmmoStorage' not in g and g['acceptedAmmo']==['metro_stallion_ammo']
    for id in ('metro_ak','metro_army_ak'):
        texture=report['separateItemTextures'][id];assert any(s['source']==('Army Kalash/wpn_aka.1024.png' if id.endswith('army_ak') else 'ak/wpn34_ak.2048.png') for s in texture['materials'])
    report.update(verifiedGunCount=29,originalPositionsNormalsIndicesAndTimelinesPreserved=True,sharedArsenalAtlasRemoved=True,primitiveMaterialKeysUnique=True)

def native_enum_tests(args,files):
    modes=sorted({m for n,b in files.items() if n.startswith('guns/') and n.count('/')==1 and n.endswith('.json') for m in json.loads(b)['fireModes']})
    tests=args.work/'enum-tests';tests.mkdir(exist_ok=True)
    source=ROOT/'tools/metro-guns2/NativeEnumTest.java'
    subprocess.run(['javac','--release','8','-proc:none','-cp',str(args.jar),'-d',str(tests),str(source)],check=True)
    result=subprocess.run(['java','-Xverify:all','-cp',os.pathsep.join(map(str,[tests,args.jar])),'NativeEnumTest',*modes],check=True,capture_output=True,text=True)
    (args.output/'MWF_V12_enum_tests.txt').write_text(result.stdout);print(result.stdout)

def main():
    p=argparse.ArgumentParser();p.add_argument('--pack',type=Path,required=True);p.add_argument('--jar',type=Path);p.add_argument('--output',type=Path,default=ROOT/'.cache/output-v12');p.add_argument('--work',type=Path,default=ROOT/'.cache/compile-v12');a=p.parse_args()
    assert v11.sha(a.pack.read_bytes())==v11.BASE_SHA;a.output.mkdir(parents=True,exist_ok=True)
    files=readzip(a.pack);report=prepare(files,readzip(ROOT/'tools/metro-guns2/sources.zip'),a.work)
    required='08dd2089f00cac9d1d4948d8db074f2f03bbb93095af6cc47cb366d9c96557d9'
    report['requiresJarSha256']=required
    # Optional compilation is verification only; it must not change deterministic pack bytes.
    if a.jar:
        jar,replaced=v11.patch_runtime(a);assert v11.sha(jar.read_bytes())==required
        native_enum_tests(a,files)
    for name,key in [('FULL_PACK_MANIFEST.json','mechanicsRequiresJarSha256'),('MOTION_UPDATE_MANIFEST.json','requiresJarSha256')]:
        m=json.loads(files[name]);m[key]=required;files[name]=js(m)
    files['CORRECTIVE_V12_MANIFEST.json']=js(report);writezip(a.output/PACK,files);report.update(pack=PACK,packSha256=v11.sha((a.output/PACK).read_bytes()))
    (a.output/'MWF_V12_report.json').write_bytes(js(report));print(json.dumps({'pack':PACK,'sha256':report['packSha256'],'guns':29,'minecraftLiveTested':False}))
if __name__=='__main__':main()
