#!/usr/bin/env python3
"""Build the user's Uboinik OBJ as a standalone pack for MWF 2024.2.4.6f-fix1.
Requires Pillow and NumPy. Reads pinned original assets from Git, never modifies the mod JAR.
"""
import argparse, base64, collections, hashlib, io, json, math, struct, subprocess, zipfile
from pathlib import Path
from PIL import Image
from uboinik_pose import POSE_FILE, source_pose, gun_transform, arm_transform, render_pose
ROOT=Path(__file__).resolve().parents[1]
SOURCE_REF='d335c8ed152bd16ac7f699f33e1c6f1073800dd9'
TARGET_SHA='f91a5419cbc9e3cbd18f831e37c17aa2833d577142d324fed1ca14846ce95183'
SCALE=.04  # source OBJ units -> metres; source gun is ~25 units long
GUN='metro_uboinik';ROUND='metro_12g_buckshot'
ATTACHMENTS={'metro_uboinik_suppressor':('barrel_uboinik.obj','texture.png','barrel','Глушитель Убойника'),
             'metro_uboinik_collimator':('colimator.obj','texturec.png','sight','Коллиматор Убойника'),
             'metro_uboinik_laser':('laser_uboinik.obj','texturen.png','laser','ЛЦУ Убойника')}

def jsonwrite(path,data):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')

def originals(ref=SOURCE_REF):
    paths=subprocess.check_output(['git','ls-tree','-rz','--name-only',ref,'--','Убойник'],cwd=ROOT).decode().split('\0')
    return {Path(p).name:subprocess.check_output(['git','show',ref+':'+p],cwd=ROOT) for p in paths if p}

def obj_read(data):
    vertices=[];normals=[];uv=[];groups=collections.OrderedDict();name='gunModel'
    for line in data.decode('utf-8-sig').splitlines():
        a=line.split()
        if not a:continue
        if a[0]=='v':vertices.append(tuple(float(x) for x in a[1:4]))
        elif a[0]=='vn':normals.append(tuple(float(x) for x in a[1:4]))
        elif a[0]=='vt':uv.append(tuple(float(x) for x in a[1:3]))
        elif a[0] in ['o','g']:name=' '.join(a[1:])
        elif a[0]=='f':
            face=[]
            for v in a[1:]:
                ids=v.split('/');vi=int(ids[0]);ti=int(ids[1]) if len(ids)>1 and ids[1] else 0;ni=int(ids[2]) if len(ids)>2 and ids[2] else 0
                face.append((vertices[vi-1 if vi>0 else vi],uv[ti-1 if ti>0 else ti] if ti else (0,0),normals[ni-1 if ni>0 else ni] if ni else None))
            for i in range(1,len(face)-1):
                tri=[face[0],face[i],face[i+1]]
                if any(v[2] is None for v in tri):
                    ab=[tri[1][0][j]-tri[0][0][j] for j in range(3)];ac=[tri[2][0][j]-tri[0][0][j] for j in range(3)]
                    n=[ab[1]*ac[2]-ab[2]*ac[1],ab[2]*ac[0]-ab[0]*ac[2],ab[0]*ac[1]-ab[1]*ac[0]];length=math.sqrt(sum(x*x for x in n)) or 1
                    tri=[(v[0],v[1],tuple(x/length for x in n)) for v in tri]
                groups.setdefault(name,[]).append(tri)
    return groups

class Gltf:
    def __init__(self):
        self.data=bytearray();self.doc={'asset':{'version':'2.0','generator':'Metro Uboinik deterministic OBJ conversion'},'scene':0,'scenes':[{'nodes':[0]}],
          'nodes':[{'name':'root','children':[]}],'meshes':[],'materials':[{'name':'uboinik_atlas','doubleSided':True}],
          'bufferViews':[],'accessors':[]}
    def accessor(self,values,size):
        while len(self.data)%4:self.data.append(0)
        raw=struct.pack('<'+'f'*(len(values)*size),*(x for row in values for x in row))
        view=len(self.doc['bufferViews']);self.doc['bufferViews'].append({'buffer':0,'byteOffset':len(self.data),'byteLength':len(raw)});self.data.extend(raw)
        a={'bufferView':view,'byteOffset':0,'componentType':5126,'count':len(values),'type':{1:'SCALAR',2:'VEC2',3:'VEC3',4:'VEC4'}[size]}
        a['min']=[min(row[i] for row in values) for i in range(size)];a['max']=[max(row[i] for row in values) for i in range(size)]
        self.doc['accessors'].append(a);return len(self.doc['accessors'])-1
    def indices(self,count):
        while len(self.data)%4:self.data.append(0)
        raw=struct.pack("<"+"H"*count,*range(count));view=len(self.doc["bufferViews"])
        self.doc["bufferViews"].append({"buffer":0,"byteOffset":len(self.data),"byteLength":len(raw)});self.data.extend(raw)
        self.doc["accessors"].append({"bufferView":view,"byteOffset":0,"componentType":5123,"count":count,"type":"SCALAR","min":[0],"max":[count-1]})
        return len(self.doc["accessors"])-1
    def mesh(self,name,triangles,uvmap=lambda uv:uv):
        v=[x for t in triangles for x in t]
        primitive={'attributes':{'POSITION':self.accessor([x[0] for x in v],3),'NORMAL':self.accessor([x[2] for x in v],3),'TEXCOORD_0':self.accessor([uvmap(x[1]) for x in v],2)},'mode':4,'material':0,'indices':self.indices(len(v))}
        m=len(self.doc['meshes']);self.doc['meshes'].append({'name':name,'primitives':[primitive]})
        i=len(self.doc['nodes']);self.doc['nodes'].append({'name':name,'mesh':m});self.doc['nodes'][0]['children'].append(i);return i
    def finish(self,path):
        self.doc['buffers']=[{'byteLength':len(self.data),'uri':'data:application/octet-stream;base64,'+base64.b64encode(self.data).decode()}]
        jsonwrite(path,self.doc)

def qz(angle):
    x=math.radians(angle)/2;return [0,0,math.sin(x),math.cos(x)]

def animations(g,pose):
    # One embedded timeline: HE-GLTF merges channels by node, not by clip name.
    ranges={};times=[];positions=[];rotations=[];base=[0,0,0];frame=0
    arm_keys={n["name"]:{"translation":[],"rotation":[]} for n in g.doc["nodes"] if "Arm" in n["name"]}
    clips=[('default',30),('draw',12),('aim',10),('preFire',1),('fire',6),('postFire',4),('preLoad',10),('load',14),('postLoad',10),
           ('preUnload',10),('unload',14),('postUnload',10),('sprint',24),('inspect',40),('takedown',12),('modeChange',8)]
    for name,duration in clips:
        ranges[name]={'startTime':frame,'endTime':frame+duration,'speed':1}
        for part in [0,.25,.5,.75,1]:
            p=list(base);rot=0;s=math.sin(math.pi*part)
            if name=='default':p[1]+=.002*math.sin(2*math.pi*part)
            elif name=='draw':p[1]-=.22*(1-part);rot=-25*(1-part)
            elif name=='takedown':p[1]-=.22*part;rot=-25*part
            elif name=='aim':p[1]=base[1]*(1-part);p[2]=base[2]*(1-part)
            elif name=='fire':p[0]-=.035*s;rot=3*s
            elif name in ['preLoad','preUnload']:p[1]-=.06*part;rot=-15*part
            elif name in ['load','unload']:p[1]-=.06+.008*s;rot=-15+2*s
            elif name in ['postLoad','postUnload']:p[1]-=.06*(1-part);rot=-15*(1-part)
            elif name=='sprint':p[1]-=.1;rot=-25
            elif name=='inspect':rot=18*s;p[2]+=.07*s
            times.append([round((frame+duration*part)/24,8)]);positions.append(p);rotations.append(qz(rot))
            reload=part if name in ['preLoad','preUnload'] else 1-part if name in ['postLoad','postUnload'] else 1 if name in ['load','unload'] else 0
            for node_name,keys in arm_keys.items():
                trs=arm_transform(pose,'left' if node_name.startswith('left') else 'right',reload)
                for key in keys:keys[key].append(trs[key])
        frame+=duration+2
    aliases={'defaultEmpty':'default','drawEmpty':'draw','takedownEmpty':'takedown','inspectEmpty':'inspect','fireLast':'fire','fireADS':'fire','fireLastADS':'fire','preFireADS':'preFire','postFireADS':'postFire','postFireEmpty':'postFire','postFireADSEmpty':'postFire',
      'preReload':'preLoad','preReloadEmpty':'preLoad','reloadFirst':'load','reloadFirstEmpty':'load','reloadSecond':'load','reloadSecondEmpty':'load','reloadFirstQuickly':'load','reloadSecondQuickly':'load','postReload':'postLoad','postReloadEmpty':'postLoad'}
    for alias,target in aliases.items():ranges[alias]=ranges[target].copy()
    a=g.accessor(times,1);p=g.accessor(positions,3);r=g.accessor(rotations,4)
    g.doc['nodes'][0]['translation']=base
    g.doc['animations']=[{'name':'uboinik_actions','samplers':[{'input':a,'output':p,'interpolation':'LINEAR'},{'input':a,'output':r,'interpolation':'LINEAR'}],
       'channels':[{'sampler':0,'target':{'node':0,'path':'translation'}},{'sampler':1,'target':{'node':0,'path':'rotation'}}]}]
    for node_name,keys in arm_keys.items():
        node=next(i for i,n in enumerate(g.doc['nodes']) if n['name']==node_name)
        for path,values in keys.items():
            out=g.accessor(values,3 if path=='translation' else 4);sampler=len(g.doc['animations'][0]['samplers'])
            g.doc['animations'][0]['samplers'].append({'input':a,'output':out,'interpolation':'LINEAR'})
            g.doc['animations'][0]['channels'].append({'sampler':sampler,'target':{'node':node,'path':path}})
    return ranges

def arm_mesh(side,slim=False):
    # Standard Minecraft ModelPlayer boxes, including their +/-5,2,0 pivots.
    # User armPos/Rot/Scale are applied on nodes, not invented wrist locations.
    width=3 if slim else 4
    xmin,xmax=(-4-width,-4) if side=='right' else (4,4+width)
    corners=[(x/16,y/16,z/16) for x,y,z in [(xmin,0,-2),(xmax,0,-2),(xmax,12,-2),(xmin,12,-2),
                                          (xmin,0,2),(xmax,0,2),(xmax,12,2),(xmin,12,2)]]
    # Standard 64x64 right-arm (40,16), left-arm (32,48) unwrap.
    u,v=(40,16) if side=='right' else (32,48);pw=3 if slim else 4;ph=12;pd=4
    faces=[((1,5,6,2),(u+pd+pw,v+pd,pd,ph)),((4,0,3,7),(u,v+pd,pd,ph)),((0,1,2,3),(u+pd,v+pd,pw,ph)),
           ((5,4,7,6),(u+2*pd+pw,v+pd,pw,ph)),((4,5,1,0),(u+pd,v,pw,pd)),((3,2,6,7),(u+pd+pw,v,pw,pd))]
    out=[]
    for ids,(tu,tv,tw,th) in faces:
        uv=[(tu/64,tv/64),((tu+tw)/64,tv/64),((tu+tw)/64,(tv+th)/64),(tu/64,(tv+th)/64)]
        a,b,c=[corners[i] for i in ids[:3]];ab=[b[j]-a[j] for j in range(3)];ac=[c[j]-a[j] for j in range(3)]
        normal=(ab[1]*ac[2]-ab[2]*ac[1],ab[2]*ac[0]-ab[0]*ac[2],ab[0]*ac[1]-ab[1]*ac[0]);l=math.sqrt(sum(v*v for v in normal));normal=tuple(-v/l for v in normal)
        face=[(corners[i],uv[j],normal) for j,i in enumerate(ids)]
        face.reverse() # Cube templates above have inward winding; Minecraft culls those faces.
        out.extend([[face[0],face[1],face[2]],[face[0],face[2],face[3]]])
    return out

def obj_write(triangles,name="attachmentModel"):
    out=['# Converted from user-provided mesh. Units: metres.','o '+name]
    for tri in triangles:
        for pos,uv,n in tri:out.append('v '+' '.join(f'{x*SCALE:.8f}' for x in pos))
    for tri in triangles:
        for pos,uv,n in tri:out.append('vt '+' '.join(f'{x:.8f}' for x in uv))
    for tri in triangles:
        for pos,uv,n in tri:out.append('vn '+' '.join(f'{x:.8f}' for x in n))
    for i in range(0,len(triangles)*3,3):out.append('f '+' '.join(f'{k}/{k}/{k}' for k in [i+1,i+2,i+3]))
    return '\n'.join(out)+'\n'

def icon(mesh,texture,size=128):
    # Offline orthographic source-geometry raster, not AI imagery or a placeholder icon.
    import numpy as np
    tris=[[(np.array(p)*SCALE,uv,n) for p,uv,n in t] for t in mesh]
    angle=.28;view=np.array([[1,0,0],[0,math.cos(angle),-math.sin(angle)],[0,math.sin(angle),math.cos(angle)]])
    coords=np.array([view@v[0] for t in tris for v in t]);lo=coords[:,:2].min(axis=0);hi=coords[:,:2].max(axis=0);scale=(size-8)/max(hi-lo)
    tex=np.asarray(texture.convert('RGBA'));out=np.zeros((size,size,4),dtype=np.uint8);depth=np.full((size,size),-np.inf)
    for t in tris:
        p=np.array([view@v[0] for v in t]);xy=(p[:,:2]-(lo+hi)/2)*scale+[size/2,size/2];xy[:,1]=size-xy[:,1]
        x0,y0=np.maximum(np.floor(xy.min(axis=0)).astype(int),0);x1,y1=np.minimum(np.ceil(xy.max(axis=0)).astype(int),size-1)
        if x1<x0 or y1<y0:continue
        yy,xx=np.mgrid[y0:y1+1,x0:x1+1];a,b,c=xy;den=(b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1])
        if abs(den)<1e-9:continue
        w0=((b[1]-c[1])*(xx+.5-c[0])+(c[0]-b[0])*(yy+.5-c[1]))/den
        w1=((c[1]-a[1])*(xx+.5-c[0])+(a[0]-c[0])*(yy+.5-c[1]))/den;w2=1-w0-w1
        z=w0*p[0,2]+w1*p[1,2]+w2*p[2,2];mask=(w0>=0)&(w1>=0)&(w2>=0)&(z>depth[y0:y1+1,x0:x1+1])
        uv=np.array([v[1] for v in t]);u=w0*uv[0,0]+w1*uv[1,0]+w2*uv[2,0];v=w0*uv[0,1]+w1*uv[1,1]+w2*uv[2,1]
        tx=np.minimum((np.mod(u,1)*tex.shape[1]).astype(int),tex.shape[1]-1);ty=np.minimum(((1-np.mod(v,1))*tex.shape[0]).astype(int),tex.shape[0]-1)
        colors=tex[ty,tx].copy();light=.65+.35*abs(sum(t[0][2][i]*[.2,.8,.56][i] for i in range(3)));colors[:,:,:3]=(colors[:,:,:3]*min(light,1)).astype('uint8')
        mask &=colors[:,:,3]>8;out[y0:y1+1,x0:x1+1][mask]=colors[mask];depth[y0:y1+1,x0:x1+1][mask]=z[mask]
    return Image.fromarray(out)

def build(output,archive,ref=SOURCE_REF):
    original=originals(ref);asset=output/'assets/modularwarfare';asset.mkdir(parents=True,exist_ok=True)
    manifest={'sourceCommit':ref,'sourceFolder':'Убойник','targetJar':'modularwarfare-shining-2024.2.4.6f-fix1.jar','targetSha256':TARGET_SHA,'sourceFiles':[{'path':'Убойник/'+name,'bytes':len(b),'sha256':hashlib.sha256(b).hexdigest()} for name,b in sorted(original.items())]}
    body=Image.open(io.BytesIO(original['Uboinik tex.png'])).convert('RGBA');shell=Image.open(io.BytesIO(original['shell (2).png'])).convert('RGBA')
    atlas=Image.new('RGBA',(2048,1024));atlas.paste(body,(0,0));atlas.paste(shell.resize((1024,1024),Image.Resampling.NEAREST),(1024,0))
    path=asset/'skins/guns'/f'{GUN}.png';path.parent.mkdir(parents=True,exist_ok=True);atlas.save(path)
    groups=obj_read(original['uboinik.obj']);g=Gltf();pose=source_pose()
    for name,tris in groups.items():
        scaled=[[(tuple(x*SCALE for x in p),uv,n) for p,uv,n in t] for t in tris]
        if name.startswith('bulletModel_'):
            uvmap=lambda uv:(.5+.5*(uv[0]%1),1-(uv[1]%1))
        elif name=='flashModel':uvmap=lambda uv:(uv[0],1-uv[1])
        else:uvmap=lambda uv:(.5*(uv[0]%1),1-(uv[1]%1))
        node=g.mesh(name,scaled,uvmap);g.doc["nodes"][node].update(gun_transform(pose,SCALE))
    for side in ['left','right']:
        for slim in [False,True]:
            node=g.mesh(side+'Arm'+('Slim' if slim else '')+'Model',arm_mesh(side,slim));g.doc['nodes'][node].update(arm_transform(pose,side))
    ranges=animations(g,pose);g.finish(asset/'gltf/guns'/f'{GUN}.gltf')
    soundmap={}
    events={'weaponReload':('Перезарядка.ogg','metro_uboinik_load'),'weaponReloadEmpty':('Перезарядка.ogg','metro_uboinik_load'),
      'weaponReloadSecond':('Перезарядка.ogg','metro_uboinik_load'),'weaponPostReload':('Затвор.ogg','metro_uboinik_bolt'),
      'weaponPostReloadEmpty':('Затвор.ogg','metro_uboinik_bolt'),'weaponFire':('Выстрел.ogg','metro_uboinik_fire'),'weaponFireSuppressed':('Выстрел.ogg','metro_uboinik_fire'),
      'weaponLoad':('Перезарядка.ogg','metro_uboinik_load'),'weaponUnload':('Перезарядка.ogg','metro_uboinik_load'),
      'weaponPostLoad':('Затвор.ogg','metro_uboinik_bolt'),'weaponPostUnload':('Затвор.ogg','metro_uboinik_bolt')}
    sounds={}
    for event,(source,key) in events.items():
        p=asset/'sounds'/f'{key}.ogg';p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(original[source])
        sounds[key]={'sounds':[{'name':'modularwarfare:'+key}]}
        soundmap[event]=[{'soundEvent':event,'soundName':key,'soundRange':64 if 'Fire' in event else 12,'soundRandomPitch':0}]
    jsonwrite(asset/'sounds.json',sounds)
    gun={'internalName':GUN,'displayName':'Убойник','maxStackSize':1,'weaponType':'shotgun','animationType':'enhanced',
      'modelSkins':[{'internalName':GUN,'skinAsset':GUN,'displayName':'Оригинал'}],
      'gunDamage':3,'numBullets':8,'bulletSpread':2.5,'roundsPerMin':120,'fireModes':['semi'],
      'weaponMaxRange':80,'weaponEffectiveRange':25,'gunDamageHeadshotBonus':0,'gunHoldLength':1.0,
      'acceptedBullets':[ROUND],'internalAmmoStorage':6,'defaultBullet':ROUND,'reloadTime':100,
      'recoilPitch':4,'recoilYaw':1,'randomRecoilPitch':.5,'randomRecoilYaw':.5,
      'restrictingFireAnimation':True,'firingReload':False,'allowReloadFiring':False,'allowReloadingSprint':False,
      'acceptedAttachments':{'barrel':['metro_uboinik_suppressor'],'sight':['metro_uboinik_collimator'],'laser':['metro_uboinik_laser']},
      'weaponSoundMap':soundmap,'allowDefaultSounds':True,'dropBulletCasing':False}
    jsonwrite(output/'guns'/f'{GUN}.json',gun)
    jsonwrite(output/'bullets'/f'{ROUND}.json',{'internalName':ROUND,'displayName':'Дробовой патрон 12 калибра','maxStackSize':64,
      'modelSkins':[{'internalName':ROUND,'skinAsset':ROUND}],'bulletDamageFactor':1,'bulletAccuracyFactor':1,'isSlug':False,
      'renderBulletModel':False,'isDynamicBullet':False,'isExplosion':False,'damageWorld':False,'shellModelFileName':'metro_12g_shell.obj'})
    # ModelShell is loaded even with casing ejection disabled; it must resolve inside this ZIP.
    raw=[v[0] for t in groups['bulletModel_0'] for v in t];center=[(min(p[i] for p in raw)+max(p[i] for p in raw))/2 for i in range(3)]
    shelltris=[[(tuple(p[i]-center[i] for i in range(3)),uv,n) for p,uv,n in t] for t in groups['bulletModel_0']]
    p=output/'obj/shells/metro_12g_shell.obj';p.parent.mkdir(parents=True,exist_ok=True);p.write_text(obj_write(shelltris,'shellModel'))
    p=asset/'skins/bullets'/f'{ROUND}.png';p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(original['shell (2).png'])
    render={'modelFileName':f'{GUN}.gltf','FPS':24,'showHandArmorType':'none','animations':ranges,
      **render_pose(pose),
      'specialEffect':{'oldFlashModel':True,'postSmokeFactor':0},
      'attachment':{id:{'binding':'gunModel','sightAimPosOffset':{'x':0,'y':0,'z':0}} for id in ATTACHMENTS}}
    jsonwrite(output/'guns/render'/f'{GUN}.render.json',render)
    icons={GUN:icon(groups['gunModel'],body),ROUND:icon(groups['bulletModel_0'],shell)}
    for id,(obj,texture,category,name) in ATTACHMENTS.items():
        tris=obj_read(original[obj])['attachmentModel'];p=output/'obj/attachments'/f'{id}.obj';p.parent.mkdir(parents=True,exist_ok=True);p.write_text(obj_write(tris))
        p=asset/'skins/attachments'/f'{id}.png';p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(original[texture])
        entry={'internalName':id,'displayName':name,'attachmentType':category,'maxStackSize':1,'sameTextureAsGun':False,'modelSkins':[{'internalName':id,'skinAsset':id}]}
        if category=='barrel':entry['barrel']={'isSuppressor':True,'hideFlash':True,'recoilPitchFactor':.9,'recoilYawFactor':1,'accuracyFactor':1}
        if category=='sight':entry['sight']={'modeType':'simple'}
        jsonwrite(output/'attachments'/f'{id}.json',entry)
        jsonwrite(output/'attachments/render'/f'{id}.render.json',{'modelFileName':f'{id}.obj','extra':{'modelScale':1},'sight':{'fovZoom':1.15},'laser':{'laserColor':[1,0,0],'laserAlpha':1,'maxDistance':64,'dotSize':1.5}})
        icons[id]=icon(tris,Image.open(io.BytesIO(original[texture])))
    for id,image in icons.items():
        p=asset/'textures/items'/f'{id}.png';p.parent.mkdir(parents=True,exist_ok=True);image.save(p)
        jsonwrite(asset/'models/item'/f'{id}.json',{'parent':'item/generated','textures':{'layer0':'modularwarfare:items/'+id},'display':{k:{'scale':[0,0,0] if id==GUN else [.4,.4,.4]} for k in ['thirdperson_righthand','thirdperson_lefthand']}})
    names={GUN:'Убойник',ROUND:'Дробовой патрон 12 калибра',**{id:v[3] for id,v in ATTACHMENTS.items()}}
    for locale in ['ru_ru','ru_RU','en_us','en_US']:
        en={GUN:'Uboinik',ROUND:'12 Gauge Buckshot','metro_uboinik_suppressor':'Uboinik Suppressor','metro_uboinik_collimator':'Uboinik Collimator','metro_uboinik_laser':'Uboinik Laser'}
        p=asset/'lang'/f'{locale}.lang';p.parent.mkdir(parents=True,exist_ok=True);p.write_text('\n'.join(f'item.{id}.name={((names if locale.startswith("ru") else en)[id])}' for id in names)+'\n',encoding='utf-8')
    jsonwrite(output/'pack.mcmeta',{'pack':{'pack_format':3,'description':'Metro Uboinik — MWF-Shining 2024.2.4.6f-fix1'}})
    manifest['conversion']={'unitScale':SCALE,'bodyTexture':'Uboinik tex.png','roundTexture':'shell (2).png','atlasSize':[2048,1024],
      'attachmentTextures':{id:v[1] for id,v in ATTACHMENTS.items()},'triangles':{name:len(t) for name,t in groups.items()},
      'animations':'Adapted root-motion timeline; no original animation data was provided.','balance':'Adapted values, not recovered original gun stats.'}
    manifest['packRevision']=2
    manifest['poseSource']={'path':POSE_FILE.relative_to(ROOT).as_posix(),'sha256':hashlib.sha256(POSE_FILE.read_bytes()).hexdigest(),'conversion':'BASIC camera/weapon/arm transforms converted to Enhanced; reload arm poses interpolated from supplied configuration.'}
    jsonwrite(output/'SOURCE_MANIFEST.json',manifest)
    (output/'README_RU.txt').write_text(README,encoding='utf-8')
    archive.parent.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for p in sorted(output.rglob('*')):
            if p.is_file():
                info=zipfile.ZipInfo(p.relative_to(output).as_posix(),date_time=(2024,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;info.external_attr=0o100644<<16
                z.writestr(info,p.read_bytes())
    print('Built',archive,'bytes:',archive.stat().st_size,'SHA256:',hashlib.sha256(archive.read_bytes()).hexdigest())
    return g.doc

README='''METRO: УБОЙНИК v2 — контент-пак для ModularWarfare-Shining 2024.2.4.6f-fix1

УСТАНОВКА
1. Нужны Minecraft 1.12.2, Forge и предоставленная пользователем версия MWF.
2. Полностью закройте Minecraft. УДАЛИТЕ предыдущий ZIP и распакованные копии
   этого пака, затем положите новый ZIP пака ЦЕЛИКОМ в .minecraft/ModularWarfare/.
   Не в mods и не в resourcepacks. Не устанавливайте одновременно ZIP и его распакованную копию.
   Полный перезапуск обязателен: MWF кэширует ошибочные пути скинов.
3. Запустите игру. В творческом каталоге ищите «Убойник» / Uboinik.
   /give @p modularwarfare:metro_uboinik
   /give @p modularwarfare:metro_12g_buckshot 64
   /give @p modularwarfare:metro_uboinik_collimator
   /give @p modularwarfare:metro_uboinik_suppressor
   /give @p modularwarfare:metro_uboinik_laser
4. Зарядите оружие отдельными патронами клавишей перезарядки MWF (обычно R).
   Установка модулей и переключение лазера — через назначенные клавиши MWF.

СОСТАВ
Шестизарядный дробовик, отдельный патрон 12 калибра, насадка, коллиматор и ЛЦУ.
После каждого выстрела исчезает одна часть bulletModel_N. Во время зарядки части
возвращаются по счётчику MWF. Геометрия шести патронов взята из переданной OBJ.
Корпус и патроны используют общий атлас без необходимости патчить мод; исходные
изображения перенесены в него с пересчётом UV. Обвес имеет собственные текстуры.
Модель переведена из OBJ в glTF 2.0 с ВСТРОЕННЫМ буфером: внешних BIN нет.
Три OGG взяты из пользовательской папки. Звук выстрела с насадкой пока тот же:
отдельной подавленной записи пользователь не предоставил.

ЧТО АДАПТИРОВАНО, А НЕ ВЗЯТО ИЗ ИСХОДНИКА
Держание, масштаб, позиции и углы рук/ADS/third-person перенесены из
предоставленного render.ubo.json с пересчётом матриц BASIC → Enhanced.
Сам JSON старого формата нельзя просто подставить в Enhanced.
Руки используют размеры Minecraft и UV скина; базовые движения оружия адаптированы,
а не восстановлены из исходных анимаций. Стартовый баланс: 6 патронов, 8 дробин,
3 единицы базового урона на дробину, 120 RPM. Это не подтверждённые исходные ТТХ.
Подвижные механизмы внутри слитого gunModel не выдумывались: барабан/затвор
не имеют восстановленной отдельной анимации. Коллиматор работает как простой
прицел без отдельной новой светящейся сетки: такой геометрии в загрузке нет.
MTL не содержат map_Kd; соответствия PNG выбраны по содержимому атласов и записаны
в SOURCE_MANIFEST.json. Посадку обвеса и правильность выбора текстур проверить в игре.

ИСПРАВЛЕНО В v2
Скины находятся в assets/modularwarfare/skins/, как требует реальный JAR,
а не textures/skins/. Исправлены пути оружия, патронов и всех трёх обвесов.
Поза и руки пересчитаны из пользовательского конфига, включая модельный масштаб 1.5,
положение при перезарядке и thirdPersonScale 0.75.

СТАТУС
Это тестовый контент-пак. Проверки структуры, ресурсов, геометрии, конфигурации
и чтения glTF не заменяют запуск Minecraft. Посадка в руках/ADS, third-person,
перезарядка, звуки и взаимодействие с другими паками требуют игровой проверки.
Мод JAR и MetroPort не изменены. Совместимость с MetroPort не заявлена.

АВТОРСТВО
Модели, изображения и звуки предоставлены пользователем в папке «Убойник».
Их исходное авторство/права не заменяются авторством конвертера. MWF в ZIP не включён.
'''

if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('--output',type=Path,default=ROOT/'contentpacks/metro_uboinik');p.add_argument('--zip',type=Path,default=ROOT/'artifacts/Metro_Uboinik_MWF_fix1_v2.zip');p.add_argument('--source-ref',default=SOURCE_REF)
    a=p.parse_args();build(a.output,a.zip,a.source_ref)
