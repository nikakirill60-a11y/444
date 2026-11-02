#!/usr/bin/env python3
"""Standalone inventory-complete Metro content pack for the pinned closed MWF JAR.
Requires Pillow, NumPy, git, and the repository history. Never restores MetroPort guns.
Native scene branches become data-only MWF hide/show geometry, not executable Java.
"""
import argparse, base64, collections, copy, hashlib, importlib.util, io, json, math, re, shutil, struct, subprocess, tarfile, tempfile, zipfile
from pathlib import Path
import numpy as np
from PIL import Image
from build_uboinik_pack import Gltf, animations, arm_mesh, icon, jsonwrite, TARGET_SHA, ROOT
from uboinik_pose import source_pose, gun_transform, arm_transform, render_pose
LEGACY_REF='10a6aa2'
SOURCE=ROOT/'tools/mwf-full-pack'
BASELINE=ROOT/'downloads/Metro_Uboinik_Duplet_MWF_fix1_v2.zip'
PREFIX='assets/modularwarfare'
REPLACEMENTS={'Uboinik':'metro_uboinik','Duplet':'metro_duplet'}
MODULE_REPLACEMENTS={'DupletBarrel':'metro_duplet_long_barrel','DupletButt':'metro_duplet_grip','DupletGlushak':'metro_duplet_suppressor','DupletPriklad':'metro_duplet_stock','QuadBarrel':'metro_duplet_extra_barrels'}
SLOT={'AutoFire':'slide','PodonokGas':'grip','RailgunAkk':'barrel','RailgunKon':'barrel','GatlingBarrel':'barrel','GatlingMotor':'grip','GatlingGiro':'grip','GatlingButt':'stock','AshotButt':'stock','Butt':'stock','PodonokButt':'stock','UbludokButt':'stock','Laser':'laser','Kolimator':'sight','Optica':'sight','OpticaSmall':'sight','TiharOptica':'sight'}
SPECIAL={'Gatling','Abzac','Vyhlop','Velopal','Ognemet'}


def prepare(cache):
    legacy=cache/'legacy'
    if not (legacy/'tools/blackrazid-reference/assets/weapons/ak/ak.obj').exists():
        legacy.mkdir(parents=True,exist_ok=True)
        raw=subprocess.check_output(['git','archive',LEGACY_REF,'tools/blackrazid-reference','src/main/resources/assets/metroport'],cwd=ROOT)
        with tarfile.open(fileobj=io.BytesIO(raw)) as t:
            for entry in t.getmembers():
                if Path(entry.name).is_absolute() or '..' in Path(entry.name).parts or entry.issym() or entry.islnk():raise ValueError('Unsafe source archive entry')
            t.extractall(legacy)
    native=cache/'native';native.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(SOURCE/'native-sources.zip') as z:z.extractall(native)
    spec=importlib.util.spec_from_file_location('metro_scene_compiler',SOURCE/'scene_compiler.py');compiler=importlib.util.module_from_spec(spec);spec.loader.exec_module(compiler)
    compiler.J=legacy/'tools/blackrazid-reference/java/mods/provoda/guns/renderers'
    scenes={name:compiler.compile_scene(name) for name in sorted(p.stem[7:] for p in compiler.J.glob('Render_*.java')) if name not in SPECIAL|set(REPLACEMENTS)}
    return legacy,native,scenes


def transform(op,v):
    m=np.eye(4)
    if op=='translate':m[:3,3]=v
    elif op=='scale':m[:3,:3]=np.diag(v)
    elif op=='rotate':
        angle,*axis=v;axis=np.array(axis,dtype=float);n=np.linalg.norm(axis)
        if n<1e-12:return m
        x,y,z=axis/n;c=math.cos(math.radians(angle));s=math.sin(math.radians(angle));d=1-c
        m[:3,:3]=[[c+x*x*d,x*y*d-z*s,x*z*d+y*s],[y*x*d+z*s,c+y*y*d,y*z*d-x*s],[z*x*d-y*s,z*y*d+x*s,c+z*z*d]]
    else:raise ValueError(op)
    return m


def condition(c,e):
    op=c['op']
    if op=='and':return all(condition(t,e) for t in c['terms'])
    if op=='or':return any(condition(t,e) for t in c['terms'])
    if op=='not':return not condition(c['terms'][0],e)
    if op=='false':return False
    if op in ('MAG','BIG'):return e[op]
    if op in ('AIM','RELOAD','UNLOAD','INSERTED'):return False
    if re.fullmatch(r'R\d+',op):return e['rounds']>=int(op[1:])
    if op.startswith('S'):
        s=e['slots'][int(op[1])];test=op[2:]
        if test=='EMPTY':return not s
        if test=='PRESENT':return bool(s)
        if test.startswith('EQ'):return s==test[2:]
        if test.startswith('NE'):return s!=test[2:]
    raise ValueError(op)


def evaluate(scene,e):
    matrix=np.eye(4);stack=[];out=[]
    def walk(nodes):
        nonlocal matrix
        for n in nodes:
            op=n['op']
            if op=='if':walk(n['yes'] if condition(n['condition'],e) else n['no'])
            elif op=='push':stack.append(matrix.copy())
            elif op=='pop':matrix=stack.pop()
            elif op=='draw':out.append((n['mesh'],n['role'],matrix.copy()))
            else:matrix=matrix@transform(op,[a+b*e['rounds'] for a,b in zip(n['values'],n['rounds'])])
    walk(scene['nodes']);assert not stack
    return out


def special(name,e):
    out=[]
    def add(mesh,role='body',pos=(0,0,0),angle=0,axis=(0,0,1)):
        out.append((mesh,role,transform('translate',pos)@transform('rotate',[angle,*axis])))
    s=e['slots']
    if name=='Gatling':
        for p in ['gatling','zvezda','base5' if s[2]=='GatlingMotor' else 'base3']:add('gatling/'+p+'.obj')
        add('gatling/'+('stvol5' if s[0]=='GatlingBarrel' else 'stvol')+'.obj')
        if e['MAG']:add('gatling/mag.obj','magazine')
        if s[1]=='GatlingButt':add('gatling/butt.obj')
        if s[2]=='GatlingGiro':add('gatling/giro.obj');add('gatling/giro_wheel.obj',pos=(.002,-.002,-.32))
        if s[2]!='GatlingMotor':add('gatling/prujmeh.obj');add('gatling/pruj.obj',pos=(0,-.093,-.441))
    elif name=='Abzac':
        add('abzac/abzac.obj');add('abzac/zatvor.obj','bolt')
        if e['MAG']:add('abzac/'+('magbig' if e['BIG'] else 'mag')+'.obj','magazine')
        if s[0]=='AutoFire':add('abzac/autofire.obj')
        if s[2]=='AbzacGlushak':add('abzac/compens.obj')
        if s[1]=='Laser':add('rpk/laser.obj',pos=(.01,.04,.03))
        for x,y,a in [(0,.065,180),(.04,.065,-30),(.075,.045,-64),(.092,.01,-110),(.082,-.035,-100)][:min(5,e['rounds'])]:add('abzac/bullet.obj','shell',(x,y,0),a)
    elif name=='Vyhlop':
        add('vyhlop/vyhlop1.obj');add('vyhlop/zatvor.obj','bolt')
        if e['MAG']:add('vyhlop/mag.obj','magazine')
        if s[0]=='Optica':add('railgun/optica.obj',pos=(.0025,-.078,.1));add('railgun/test.obj',pos=(.0025,-.078,.34))
        elif s[0]=='Kolimator':add('rpk/kolimator.obj',pos=(0,.12,-.05))
        elif s[0] in ('OpticaSmall','TiharOptica'):add('rpk/optica.obj',pos=(0,.12,-.05))
        else:add('vyhlop/pricel.obj')
        if s[1]=='Laser':add('rpk/laser.obj',pos=(.01,-.005,-.25))
    elif name=='Velopal':add('vyhlop/velo.obj')
    elif name=='Ognemet':
        add('ognemet/ognemet1.obj');add('ognemet/zatvor.obj','bolt',angle=-50,axis=(1,0,0));add('ognemet/strelka.obj',pos=(.037,.1,-.002),angle=100)
        if e['MAG']:add('ognemet/mag.obj','magazine')
    elif name=='AKK':
        add('akk/ak.obj');add('akk/zatvor.obj','bolt')
        if e['MAG']:add('akk/mag.obj','magazine')
    else:raise ValueError(name)
    return out


def key(draw):return (draw[0],draw[1],tuple(np.round(draw[2].flatten(),8)))


def parse_obj(data):
    """Keep usemtl boundaries (the small-pack OBJ reader intentionally does not)."""
    ps=[];uvs=[];ns=[];out=[];material=None
    for line in data.decode('utf-8-sig').splitlines():
        a=line.split()
        if not a:continue
        if a[0]=='v':ps.append(tuple(map(float,a[1:4])))
        elif a[0]=='vt':uvs.append(tuple(map(float,a[1:3])))
        elif a[0]=='vn':ns.append(tuple(map(float,a[1:4])))
        elif a[0]=='usemtl':material=' '.join(a[1:])
        elif a[0]=='f':
            face=[]
            for item in a[1:]:
                ids=item.split('/');idx=int(ids[0]);ti=int(ids[1]) if len(ids)>1 and ids[1] else 0;ni=int(ids[2]) if len(ids)>2 and ids[2] else 0
                face.append((ps[idx-1 if idx>0 else idx],uvs[ti-1 if ti>0 else ti] if ti else (0,0),ns[ni-1 if ni>0 else ni] if ni else None))
            for i in range(1,len(face)-1):
                tri=[face[0],face[i],face[i+1]]
                if any(v[2] is None for v in tri):
                    normal=np.cross(np.subtract(tri[1][0],tri[0][0]),np.subtract(tri[2][0],tri[0][0]));normal/=np.linalg.norm(normal) or 1
                    tri=[(p,u,tuple(normal)) for p,u,n in tri]
                out.append((material,tri))
    assert out,'Empty OBJ'
    return out


class Assets:
    def __init__(self,legacy,native):
        self.root=legacy/'tools/blackrazid-reference/assets/weapons';self.native=native
        self.paths={p.relative_to(self.root).as_posix().lower():p for p in self.root.rglob('*') if p.is_file()}
        self.paths.update({p.relative_to(native).as_posix().lower():p for p in native.rglob('*') if p.is_file()})
        self.textures=collections.defaultdict(list);self.mats={};self.local={};self.parsed={};self.used={};self.canonical={}
        for name,p in sorted(self.paths.items()):
            if p.suffix.lower()=='.png':self.textures[p.name.lower()].append(p)
            if p.suffix=='.mtl':
                mats={};current=None
                for line in p.read_text(errors='replace').splitlines():
                    a=line.strip().split(maxsplit=1)
                    if not a:continue
                    if a[0]=='newmtl':current=a[1];mats[current]={'color':(.64,.64,.64),'texture':None}
                    elif current and a[0]=='Kd':mats[current]['color']=tuple(map(float,a[1].split()))
                    elif current and a[0]=='map_Kd':mats[current]['texture']=a[1].replace('\\','/').split('/')[-1]
                self.local[name]=mats
                for k,v in mats.items():self.mats.setdefault(k,v)
    def resolve(self,mesh,mat):
        folder=Path(mesh).parent.as_posix()
        record=self.local.get(str(Path(mesh).with_suffix('.mtl')),{}).get(mat)
        if not record:
            for k,v in self.local.items():
                if str(Path(k).parent)==folder and mat in v:record=v[mat];break
        if not record:record=self.mats.get(mat)
        if not record:
            # Legacy OBJ material names embed the original texture basename.
            candidates=[]
            for name,paths in self.textures.items():
                stem=re.sub(r'\.(?:\d+)(?:c)?\.png$','',name)
                if stem in (mat or '').lower():candidates.append((len(stem),name))
            if candidates:record={'texture':max(candidates)[1]}
        if not record:raise ValueError('Unresolved native material: '+mesh+' / '+str(mat))
        if not record.get('texture'):return ('solid',*record['color'])
        name=record['texture'].lower();paths=self.textures.get(name,[])
        if not paths:raise ValueError('Missing simultaneous texture '+mesh+': '+name)
        path=next((p for p in paths if p.parent==self.paths[mesh].parent),paths[0]);self.used[str(path)]=hashlib.sha256(path.read_bytes()).hexdigest()
        digest=self.used[str(path)];canonical=self.canonical.setdefault(digest,str(path))
        return ('image',canonical)
    def triangles(self,draw,basis=None):
        mesh,role,m=draw
        if mesh not in self.parsed:self.parsed[mesh]=[(self.resolve(mesh,mat),tri) for mat,tri in parse_obj(self.paths[mesh].read_bytes())]
        m=(basis if basis is not None else np.eye(4))@m;normal=np.linalg.inv(m[:3,:3]).T
        out=[]
        for material,t in self.parsed[mesh]:
            tri=[]
            for p,uv,n in t:
                nn=normal@np.array(n);nn/=np.linalg.norm(nn) or 1
                tri.append((tuple((m@np.array([*p,1]))[:3]),uv,tuple(nn)))
            out.append((material,tri))
        return out


def wrap_triangle(tri):
    """Clip at integer UV seams before atlasing; modulo-at-vertices tears triangles."""
    lo=[math.floor(min(v[1][i] for v in tri)+1e-9) for i in range(2)]
    hi=[math.ceil(max(v[1][i] for v in tri)-1e-9) for i in range(2)]
    for i in range(2):hi[i]=max(hi[i],lo[i]+1)
    if (hi[0]-lo[0])*(hi[1]-lo[1])>256:raise ValueError('Unbounded native UV range')
    def clip(poly,axis,bound,greater):
        result=[]
        for a,b in zip(poly,poly[1:]+poly[:1]):
            ia=a[1][axis]>=bound-1e-10 if greater else a[1][axis]<=bound+1e-10
            ib=b[1][axis]>=bound-1e-10 if greater else b[1][axis]<=bound+1e-10
            if ia:result.append(a)
            if ia!=ib:
                f=(bound-a[1][axis])/(b[1][axis]-a[1][axis]);result.append(tuple(tuple(x+f*(y-x) for x,y in zip(aa,bb)) for aa,bb in zip(a,b)))
        return result
    for u in range(lo[0],hi[0]):
        for v in range(lo[1],hi[1]):
            poly=tri
            for axis,bound,greater in [(0,u,True),(0,u+1,False),(1,v,True),(1,v+1,False)]:
                poly=clip(poly,axis,bound,greater)
                if not poly:break
            for i in range(1,len(poly)-1):yield [(p,(max(0,min(1,uv[0]-u)),max(0,min(1,uv[1]-v))),n) for p,uv,n in [poly[0],poly[i],poly[i+1]]]


class Atlas:
    def __init__(self,parts):
        self.materials=sorted(set(m for part in parts for m,t in part));self.locations={};self.repeat={}
        for mat in self.materials:
            uvs=[uv for part in parts for m,t in part if m==mat for p,uv,n in t]
            lo=[math.floor(min(u[i] for u in uvs)) for i in range(2)];hi=[max(lo[i]+1,math.ceil(max(u[i] for u in uvs))) for i in range(2)]
            if (hi[0]-lo[0])*(hi[1]-lo[1])>16:self.repeat[mat]=(lo,hi)
        cols=math.ceil(math.sqrt(len(self.materials)));rows=math.ceil(len(self.materials)/cols)
        width=2**math.ceil(math.log2(max(1,cols)*512));height=2**math.ceil(math.log2(max(1,rows)*512))
        self.image=Image.new('RGBA',(width,height),(0,0,0,0))
        for i,mat in enumerate(self.materials):
            im=Image.open(mat[1]).convert('RGBA') if mat[0]=='image' else Image.new('RGBA',(8,8),tuple(round(255*x) for x in mat[1:])+(255,))
            im.thumbnail((508,508),Image.Resampling.LANCZOS)
            if mat in self.repeat:
                lo,hi=self.repeat[mat];pixels=np.asarray(im);us=np.mod(lo[0]+(np.arange(508)+.5)/508*(hi[0]-lo[0]),1);vs=np.mod(hi[1]-(np.arange(508)+.5)/508*(hi[1]-lo[1]),1)
                xs=np.minimum((us*im.width).astype(int),im.width-1);ys=np.minimum(((1-vs)*im.height).astype(int),im.height-1)
                im=Image.fromarray(pixels[ys[:,None],xs[None,:]])
            x=(i%cols)*512+4;y=(i//cols)*512+4;w,h=im.size
            self.image.paste(im,(x,y));self.image.paste(im.crop((0,0,1,h)).resize((4,h)),(x-4,y));self.image.paste(im.crop((w-1,0,w,h)).resize((4,h)),(x+w,y))
            self.image.paste(im.crop((0,0,w,1)).resize((w,4)),(x,y-4));self.image.paste(im.crop((0,h-1,w,h)).resize((w,4)),(x,y+h))
            self.locations[mat]=(x,y,w,h)
    def convert(self,part,gltf=True):
        out=[];aw,ah=self.image.size
        for mat,tri in part:
            x,y,w,h=self.locations[mat]
            if mat in self.repeat:
                lo,hi=self.repeat[mat];wrapped=[[(p,((uv[0]-lo[0])/(hi[0]-lo[0]),(uv[1]-lo[1])/(hi[1]-lo[1])),n) for p,uv,n in tri]]
            else:wrapped=wrap_triangle(tri)
            for t in wrapped:
                out.append([(p,((x+u*w)/aw,(y+(1-v)*h)/ah if gltf else 1-(y+(1-v)*h)/ah),n) for p,(u,v),n in t])
        return out


class PackGltf(Gltf):
    def mesh(self,name,triangles,uvmap=lambda u:u):
        # Multiple primitives, one named node: keep MWF hide/show atomic even above uint16 limits.
        primitives=[]
        for start in range(0,len(triangles),20000):
            vs=[v for t in triangles[start:start+20000] for v in t]
            primitives.append({'attributes':{'POSITION':self.accessor([v[0] for v in vs],3),'NORMAL':self.accessor([v[2] for v in vs],3),'TEXCOORD_0':self.accessor([uvmap(v[1]) for v in vs],2)},'indices':self.indices(len(vs)),'mode':4,'material':0})
        mesh=len(self.doc['meshes']);self.doc['meshes'].append({'name':name,'primitives':primitives})
        node=len(self.doc['nodes']);self.doc['nodes'].append({'name':name,'mesh':mesh});self.doc['nodes'][0]['children'].append(node);return node


def obj_bytes(parts):
    out=['# Source-owned native geometry; converted units: metres.']
    index=1
    for name,tris in parts:
        out.append('o '+name)
        for t in tris:
            for p,u,n in t:out.append('v '+' '.join(f'{x:.8f}' for x in p))
        for t in tris:
            for p,u,n in t:out.append('vt '+' '.join(f'{x:.8f}' for x in u))
        for t in tris:
            for p,u,n in t:out.append('vn '+' '.join(f'{x:.8f}' for x in n))
        for i in range(index,index+len(tris)*3,3):out.append('f '+' '.join(f'{j}/{j}/{j}' for j in range(i,i+3)))
        index+=len(tris)*3
    return ('\n'.join(out)+'\n').encode()


def write_bytes(p,b):p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(b)
def skin(category,id):return [{'internalName':id,'skinAsset':'metro_native_atlas' if category in ('guns','ammo','attachments') else id,'displayName':'Оригинал'}]
def xyz(v):return dict(zip('xyz',map(float,v)))


def build(output,archive,cache):
    if output.exists() and any(output.iterdir()):raise ValueError('Use a fresh output directory to prevent stale pack entries: '+str(output))
    legacy,native,scenes=prepare(cache);assets=Assets(legacy,native);data=legacy/'src/main/resources/assets/metroport';ref=legacy/'tools/blackrazid-reference'
    weapons=json.loads((data/'blackrazid_weapons.json').read_text());modules=json.loads((data/'blackrazid_modules.json').read_text());fittings=json.loads((data/'blackrazid_module_fittings.json').read_text())
    # AKK really is separately constructed in ModBase, despite omission from the earlier MetroPort importer.
    weapons.append(dict(id='blackrazid_akk',sourceId='AKK',kind='gun',icon='ak',nameRu='Калашников (AKK)',nameEn='Kalashnikov (AKK)',magazines=['blackrazid_akk_ammo'],damage=5,delay=3,loadingTicks=40,recoil=.6,accuracy=15,shootSound='blackrazid_ak_shoot',reloadSound='blackrazid_rpk_reload',unloadSound='blackrazid_rpk_unload'))
    weapons.append(dict(id='blackrazid_akk_ammo',sourceId='AKKAmmo',kind='magazine',icon='ak_ammo',nameRu='Магазин Калашникова (AKK)',nameEn='AKK magazine',capacity=30,roundItem='blackrazid_bullet5x45'))
    fittings['AKK']=[[],[],[],[],[]] # no guessed attachment sockets for the recovered compiled-only renderer
    ids={m['sourceId']:MODULE_REPLACEMENTS.get(m['sourceId'],m['id'].replace('blackrazid_mod_','metro_mod_')) for m in modules}
    guns={w['sourceId']:w for w in weapons if w['kind']=='gun'};mags={w['id']:w for w in weapons if w['kind']=='magazine'}
    idmap={w['id']:REPLACEMENTS.get(w['sourceId'],w['id'].replace('blackrazid_','metro_')) for w in weapons}
    idmap['blackrazid_bullet12x70']='metro_12g_buckshot'
    output.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(BASELINE) as z:
        for name in z.namelist():
            # Remove the old cosmetic duplicate GUN; the actual +2 attachment is retained.
            if name in ('guns/metro_duplet_4.json','guns/render/metro_duplet_4.render.json','README_RU.txt','README_DUPLET_RU.txt'):continue
            write_bytes(output/name,z.read(name))
    asset=output/PREFIX;names={};report={'targetSha256':TARGET_SHA,'inventoryGuns':[],'sourceModuleMap':ids,'specialMechanicsImplemented':False,'companionMod':False,'dupletCapacity':2,'removedDuplicateGunIds':['metro_duplet_4'],'oldItemMigration':'Before updating, replace any old metro_duplet_4 stacks with metro_duplet plus the cosmetic extra-barrels attachment; back up the world.','limitations':[
        'No Minecraft/OpenGL run: new holding/ADS/reload poses require in-game confirmation.',
        'Pressure, pumping, rail charging, flame stream, native projectiles and rotor motion are not implemented by this data-only pack.',
        'Ognemet is explicitly a bullet-based approximation, not a functioning flamethrower.',
        'AutoFire attachment is decorative; it does not switch gun fire modes.',
        'AKK resting assembly is recovered, but handling/balance use conservative AK-family defaults; attachment sockets not guessed.',
        'Native source has limited optic branches: sockets with no source geometry delta are omitted and listed.',
        'Atlas material tiles are downsampled to at most 508 pixels; one shared atlas per skin category, all source PNG materials are used simultaneously.',
        'The current merged Duplet model has no separate shell meshes or hinge: no fabricated shell removal/hinge animation.',
        'MWF has no fuel-specific refill restriction: Ognemet canister is a normal MWF ammo item.'
    ],'omittedSockets':[],'guns':{},'magazines':{},'materialAtlases':{}}
    soundjson=json.loads((asset/'sounds.json').read_text());pose=source_pose();gt=gun_transform(pose,.04)
    native_by_gun={};module_examples={};mag_examples={};mag_shells={}
    def scene(name,e):
        draws=special(name,e) if name in SPECIAL or name=='AKK' else evaluate(scenes[name],e)
        # Native resting first-person cartridge placements absent from EQUIPPED graphs.
        if name=='Revolver':
            pos=np.array([0,.085,0.])
            for delta in [(0,0,0),(.014,-.008,0),(-.001,-.015,0),(-.014,-.007,0),(-.013,.007,0),(.001,.015,0)][:e['rounds']]:
                pos+=delta;draws.append(('revolver/bullet.obj','shell',transform('translate',pos)))
        if name=='Ashot' and e['rounds']>0:draws.append(('ashot/bullet.obj','shell',transform('translate',[0,.075,.037])@transform('scale',[1.5]*3)))
        return draws
    # One shared, deduplicated atlas, not 21 almost-identical 4K atlases in GPU memory.
    needed=set()
    for name,w in guns.items():
        if name in REPLACEMENTS:continue
        env={'MAG':bool(w.get('magazines')),'BIG':False,'rounds':max((mags[m]['capacity'] for m in w.get('magazines',[])),default=w.get('looseCapacity',1)),'slots':['']*5}
        requests=[copy.deepcopy(env)];big=copy.deepcopy(env);big['BIG']=True;requests.append(big)
        for slot,choices in enumerate(fittings[name]):
            for mod in choices:
                eq=copy.deepcopy(env);eq['slots'][slot]=mod;requests.append(eq)
        for eq in requests:needed.update(d[0] for d in scene(name,eq))
    needed.update(['ventil/glushak.obj','uboinik/glushak.obj','tihar/optica.obj','vsv/glushak.obj'])
    common=Atlas([assets.triangles((m,'body',np.eye(4))) for m in sorted(needed)])
    print('Shared atlas',common.image.size,len(common.materials),'deduplicated materials',flush=True)
    for category in ('guns','attachments','ammo'):
        folder=asset/'skins'/category;folder.mkdir(parents=True,exist_ok=True);common.image.save(folder/'metro_native_atlas.png')
    for name,w in guns.items():
        gid=idmap[w['id']];report['inventoryGuns'].append({'sourceId':name,'internalName':gid,'name':w['nameRu']})
        if name in REPLACEMENTS:
            report['guns'][gid]={'baseline':'unchanged current uploaded model, texture, pose and reload config','sourceId':name};continue
        print('Assemble',name,flush=True)
        count=max((mags[mid]['capacity'] for mid in w.get('magazines',[])),default=w.get('looseCapacity',1))
        e={'MAG':bool(w.get('magazines')),'BIG':False,'rounds':count,'slots':['']*5}
        default=scene(name,e);body=[d for d in default if d[1] not in ('magazine','shell')];shells=[d for d in default if d[1]=='shell']
        variants={};body_keys={key(d) for d in body}
        for slot,choices in enumerate(fittings[name]):
            for mod in choices:
                equipped=copy.deepcopy(e);equipped['slots'][slot]=mod;variant=scene(name,equipped);vbody=[d for d in variant if d[1] not in ('magazine','shell')];vkeys={key(d) for d in vbody}
                added=[d for d in vbody if key(d) not in body_keys];removed=[key(d) for d in body if key(d) not in vkeys]
                if name=='Ventil' and mod=='VentilGlushak':
                    # Present only in the original first-person branch, at the unchanged model origin.
                    added=[('ventil/glushak.obj','body',np.eye(4))]
                if not added and not removed:
                    report['omittedSockets'].append({'gun':name,'module':mod,'reason':'No original resting scene geometry branch'});continue
                variants[mod]=(added,removed)
                if added:module_examples.setdefault(mod,added)
        # Same legacy model coordinates are used by all sibling source parts.
        pts=np.array([p for draw in body for mat,tri in assets.triangles(draw) for p,u,n in tri]);lo=pts.min(axis=0);hi=pts.max(axis=0)
        length=max(hi[2]-lo[2],.15);factor=min(2.2,max(.6,(.62 if name in ('Ashot','Revolver') else 1.0)/length))
        basis=transform('scale',[factor]*3)@transform('rotate',[90,0,1,0]);basis[:3,3]=[-.06-factor*(lo[2]+hi[2])/2,.01-factor*hi[1],factor*(lo[0]+hi[0])/2]
        collections_parts=[];nodes=[];key_nodes={};hidden=[];attachment={}
        def add(draw,node_name):
            part=assets.triangles(draw,basis);nodes.append((node_name,part));collections_parts.append(part)
        for i,d in enumerate(body):
            node='gunModel' if i==0 else 'native_'+str(i);key_nodes[key(d)]=node;add(d,node)
        if not w.get('magazines'):
            for i,d in enumerate(shells[:count]):add(d,'bulletModel_'+str(i))
        for mod,(added,removed) in variants.items():
            show=[]
            for i,d in enumerate(added):
                node='mod_'+mod+'_'+str(i);add(d,node);show.append(node);hidden.append(node)
            attachment[ids[mod]]={'binding':'gunModel','scale':xyz([0,0,0]),'showPart':show,'hidePart':[key_nodes[k] for k in removed]}
        # Put a real default magazine OBJ in MWF's ammo renderer, not permanently in the gun mesh.
        anchor=len(nodes)
        atlas=common
        g=PackGltf();g.doc['asset']['generator']='Metro full pack: native multi-material scene baking';g.doc['materials'][0]['name']=gid+'_atlas'
        for node,part in nodes:
            index=g.mesh(node,atlas.convert(part));g.doc['nodes'][index].update(gt)
        for side in ['left','right']:
            for slim in [False,True]:
                index=g.mesh(side+'Arm'+('Slim' if slim else '')+'Model',arm_mesh(side,slim));g.doc['nodes'][index].update(arm_transform(pose,side))
        # Dynamic magazines bind here; transform includes the same native-to-Ubo frame as the gun.
        bscale=gt['scale'][0];bm=basis[:3,:3];basepos=np.array(gt['translation'])+bscale*basis[:3,3]
        q=[0,math.sqrt(.5),0,math.sqrt(.5)]
        ammo_index=len(g.doc['nodes']);g.doc['nodes'].append({'name':'ammoModel','translation':basepos.tolist(),'scale':[bscale*factor]*3,'rotation':q});g.doc['nodes'][0]['children'].append(ammo_index)
        for mid in w.get('magazines',[]):
            me=copy.deepcopy(e);me['BIG']=len(w['magazines'])>1 and mid==w['magazines'][0];me['rounds']=mags[mid]['capacity']
            draws=[d for d in scene(name,me) if d[1]=='magazine']
            if not draws:
                assert name=='Velopal',(name,mid)
                draws=mag_examples[mid]
                # The source Velopal has no separately rendered magazine. Keep that, rather than drawing a foreign floating mag.
                g.doc['nodes'][ammo_index]['scale']=[0,0,0]
            mag_examples.setdefault(mid,draws)
            mag_shells.setdefault(mid,[d for d in scene(name,me) if d[1]=='shell'])
            # Shared mags use original raw coordinates; each gun's scene gives the local placement.
            placement=draws[0][2]
            attachment[idmap[mid]]={'translate':xyz(placement[:3,3])}
        ranges=animations(g,pose)
        seconds=max(1.25,w.get('loadingTicks',40)/20)
        reload_speed=(20+(2*count-1)*14)/(24*seconds) if not w.get('magazines') else 48/(24*seconds)
        for action in ranges:
            if any(s in action.lower() for s in ('load','reload')):ranges[action]['speed']=reload_speed
        g.finish(asset/'gltf/guns'/f'{gid}.gltf')
        gun=json.loads((output/'guns/metro_uboinik.json').read_text());gun.update(internalName=gid,displayName=w['nameRu']+(' (адаптация MWF)' if name=='Ognemet' else ''),modelSkins=skin('guns',gid),gunDamage=w['damage'],numBullets=w.get('pellets',1),bulletSpread=max(.2,w.get('accuracy',15)/20),roundsPerMin=round(1200/max(1,w.get('delay',3))),reloadTime=round(seconds*20),recoilPitch=w.get('recoil',.6)*3,recoilYaw=w.get('recoil',.6),acceptedAttachments={},weaponType='shotgun' if w.get('pellets',1)>1 else 'boltsniper' if name in ('Ventil','Preved') else 'semisniper' if name in ('Vyhlop','Railgun') else 'pistol' if name=='Revolver' else 'rifle',fireModes=['semi','auto'] if name in ('AK','AKK','AK2012','AKSU','Rpk','Vsv','Ubludok','Podonok','Gatling','Ognemet') else ['semi'])
        gun['weaponMaxRange']=600 if name in ('Ventil','Preved','Vyhlop','Railgun') else 90 if gun['weaponType']=='shotgun' else 120 if name=='Revolver' else 240
        gun['weaponEffectiveRange']=250 if name in ('Ventil','Preved','Vyhlop','Railgun') else 25 if gun['weaponType']=='shotgun' else 45 if name=='Revolver' else 90
        if w.get('magazines'):
            for k in ('acceptedBullets','defaultBullet','internalAmmoStorage'):gun.pop(k,None)
            gun['acceptedAmmo']=[idmap[x] for x in w['magazines']]
        else:gun.update(acceptedBullets=[idmap[w['looseRound']]],defaultBullet=idmap[w['looseRound']],internalAmmoStorage=w['looseCapacity'])
        for mod in variants:gun['acceptedAttachments'].setdefault(SLOT.get(mod,'barrel'),[]).append(ids[mod])
        soundmap={}
        for event,source in [('weaponFire',w.get('shootSound')),('weaponFireSuppressed',w.get('shootSound')),('weaponReload',w.get('reloadSound')),('weaponReloadEmpty',w.get('reloadSound')),('weaponLoad',w.get('reloadSound')),('weaponUnload',w.get('unloadSound')),('weaponReloadSecond',w.get('reloadSound'))]:
            if not source:continue
            path=data/'sounds'/f'{source}.ogg';assert path.is_file(),path
            snd=source.replace('blackrazid_','metro_native_');write_bytes(asset/'sounds'/f'{snd}.ogg',path.read_bytes());soundjson[snd]={'sounds':[{'name':'modularwarfare:'+snd}]};soundmap[event]=[{'soundEvent':event,'soundName':snd,'soundRange':64 if 'Fire' in event else 12,'soundRandomPitch':0}]
        gun['weaponSoundMap']=soundmap;jsonwrite(output/'guns'/f'{gid}.json',gun)
        r={'modelFileName':gid+'.gltf','FPS':24,'showHandArmorType':'none','animations':ranges,**render_pose(pose),'specialEffect':{'oldFlashModel':True,'postSmokeFactor':0},'defaultHidePart':hidden,'attachment':attachment}
        jsonwrite(output/'guns/render'/f'{gid}.render.json',r)
        report['guns'][gid]={'sourceId':name,'sourceParts':[d[0] for d in default],'nativeVisibleRounds':len(shells),'capacity':count,'reloadSeconds':seconds,'attachments':list(variants),'pose':'Ubo frame fitted to native resting bounds; provisional, not game validated'}
        report['materialAtlases'][gid]={'simultaneousMaterials':[m[1] if m[0]=='image' else list(m) for m in sorted(set(m for p in collections_parts for m,t in p))],'size':list(atlas.image.size)}
        native_by_gun[name]=(basis,gt)
        names[gid]=(gun['displayName'],w['nameEn'])
        copy_icon(ref,w['icon'],asset,gid,True)
    # Source modules represented by current replacement geometry are mapped, not duplicated.
    # Some Optica/Tihar branches are absent in the original renderers. Inventory meshes still exist.
    defaults={'TiharOptica':[('tihar/optica.obj','body',np.eye(4))], 'VentilGlushak':[('ventil/glushak.obj','body',np.eye(4))], 'UboinikBarrel':[('uboinik/glushak.obj','body',np.eye(4))], 'VsvGlushak':[('vsv/glushak.obj','body',np.eye(4))]}
    module_examples.update({k:v for k,v in defaults.items() if k not in module_examples})
    for m in modules:
        mod=m['sourceId'];id=ids[mod]
        if mod in MODULE_REPLACEMENTS:continue
        draws=module_examples.get(mod)
        if not draws:raise ValueError('No native geometry for module '+mod)
        parts=[assets.triangles(d) for d in draws];at=common;tris=[t for p in parts for t in at.convert(p,False)]
        write_bytes(output/'obj/attachments'/f'{id}.obj',obj_bytes([('attachmentModel',tris)]))
        slot=SLOT.get(mod,'barrel');entry={'internalName':id,'displayName':m['nameRu']+(' (декор)' if mod=='AutoFire' else ''),'maxStackSize':1,'attachmentType':slot,'sameTextureAsGun':False,'modelSkins':skin('attachments',id)}
        if slot=='barrel':entry['barrel']={'isSuppressor':'Glushak' in mod,'hideFlash':'Glushak' in mod,'accuracyFactor':.9 if 'Barrel' in mod else 1}
        if slot=='stock':entry['stock']={'recoilPitchFactor':.85,'recoilYawFactor':.9,'aimSpeedFactor':1}
        if slot=='grip':entry['grip']={'recoilPitchFactor':.9,'recoilYawFactor':.9}
        if slot=='sight':entry['sight']={'modeType':'dot' if mod=='Kolimator' else 'simple','dotColorType':'red'}
        jsonwrite(output/'attachments'/f'{id}.json',entry);jsonwrite(output/'attachments/render'/f'{id}.render.json',{'modelFileName':id+'.obj','extra':{'modelScale':1}})
        names[id]=(entry['displayName'],m['nameEn']);copy_icon(ref,m['icon'],asset,id)
    (asset/'skins/ammo').mkdir(parents=True,exist_ok=True)
    for w in weapons:
        if w['kind']=='gun':continue
        id=idmap[w['id']]
        if id=='metro_12g_buckshot':continue
        if w['kind']=='round':
            bullet=json.loads((output/'bullets/metro_12g_buckshot.json').read_text());bullet.update(internalName=id,displayName=w['nameRu'],modelSkins=skin('bullets',id),isSlug=True)
            jsonwrite(output/'bullets'/f'{id}.json',bullet);write_bytes(asset/'skins/bullets'/f'{id}.png',(ref/'assets/icons'/f"{w['icon']}.png").read_bytes())
        else:
            draws=mag_examples[w['id']]
            # Placement belongs to each gun's binding, not the shared magazine OBJ.
            parts=[assets.triangles((d[0],d[1],np.eye(4))) for d in draws];at=common
            objparts=[('ammoModel',[t for p in parts for t in at.convert(p,False)])]
            visible=mag_shells[w['id']][:w['capacity']]
            inverse=np.linalg.inv(draws[0][2])
            for i,d in enumerate(visible):
                # Number visible native surfaces from the top of the loaded mag so the first shot removes one.
                index=w['capacity']-len(visible)+i
                objparts.append(('bulletModel_'+str(index),at.convert(assets.triangles((d[0],d[1],inverse@d[2])),False)))
            write_bytes(output/'obj/ammo'/f'{id}.obj',obj_bytes(objparts))
            ammo={'internalName':id,'displayName':w['nameRu'],'maxStackSize':1,'modelSkins':skin('ammo',id),'ammoCapacity':w['capacity'],'magazineCount':1,'allowEmptyMagazines':True,'isDynamicAmmo':True,'sameTextureAsGun':False,'subAmmo':[idmap[w['roundItem']]],'reloadTimeFactor':1,'reloadSpeedFactor':1}
            jsonwrite(output/'ammo'/f'{id}.json',ammo);jsonwrite(output/'ammo/render'/f'{id}.render.json',{'modelFileName':id+'.obj','extra':{'modelScale':1}})
            report['magazines'][id]={'capacity':w['capacity'],'meshes':[d[0] for d in draws],'visibleRounds':len(visible),'visibleIndices':list(range(w['capacity']-len(visible),w['capacity']))}
        names[id]=(w['nameRu'],w['nameEn']);copy_icon(ref,w['icon'],asset,id)
    jsonwrite(asset/'sounds.json',soundjson)
    for lang in ['ru_ru','ru_RU','en_us','en_US']:
        path=asset/'lang'/f'{lang}.lang';old=path.read_text();old='\n'.join(l for l in old.splitlines() if not l.startswith('item.metro_duplet_4.'))
        path.write_text(old+'\n'+'\n'.join(f'item.{id}.name={values[0 if lang.lower().startswith("ru") else 1]}' for id,values in sorted(names.items()))+'\n',encoding='utf8')
    # No phantom gun aliases: exactly one item for every registered source weapon.
    report['counts']={c:len(list((output/c).glob('*.json'))) for c in ('guns','attachments','ammo','bullets')}
    report['counts']['sourceModules']=len(modules)
    report['assetHashes']=json.loads((SOURCE/'native-sources.json').read_text())
    report['legacySourceCommit']=LEGACY_REF;report['baselineSha256']=hashlib.sha256(BASELINE.read_bytes()).hexdigest()
    # Make the output portable: provenance uses source-relative filenames, never sandbox paths.
    for entry in report['materialAtlases'].values():entry['simultaneousMaterials']=[str(x).split('/assets/weapons/')[-1].split('/native/')[-1] if isinstance(x,str) else x for x in entry['simultaneousMaterials']]
    jsonwrite(output/'FULL_PACK_MANIFEST.json',report)
    write_bytes(output/'README_FULL_PACK_RU.txt',(SOURCE/'README_RU.txt').read_bytes())
    jsonwrite(output/'pack.mcmeta',{'pack':{'pack_format':3,'description':'Metro full arsenal: 23 weapons, 31 attachments / MWF fix1'}})
    archive.parent.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for p in sorted(output.rglob('*')):
            if p.is_file():
                info=zipfile.ZipInfo(p.relative_to(output).as_posix(),(2026,11,2,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;info.external_attr=0o644<<16;z.writestr(info,p.read_bytes())
    print('Built',archive,archive.stat().st_size,'bytes',report['counts'],flush=True)
    return report


def copy_icon(ref,icon_name,asset,id,is_gun=False):
    path=ref/'assets/icons'/f'{icon_name}.png';assert path.is_file(),path
    write_bytes(asset/'textures/items'/f'{id}.png',path.read_bytes())
    model={'parent':'item/generated','textures':{'layer0':'modularwarfare:items/'+id}}
    if is_gun:model['display']={k:{'scale':[0,0,0]} for k in ('thirdperson_righthand','thirdperson_lefthand')}
    jsonwrite(asset/'models/item'/f'{id}.json',model)


if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('--output',type=Path,default=ROOT/'artifacts/full-mwf-pack');p.add_argument('--zip',type=Path,default=ROOT/'downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip');p.add_argument('--cache',type=Path,default=ROOT/'.cache/full-pack/build');a=p.parse_args();build(a.output,a.zip,a.cache)
