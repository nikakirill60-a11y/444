#!/usr/bin/env python3
r"""Collect missing functional-decor materials, textures and model parts. Python 3 only.

Usage: py -3 finishdecor.py "D:\New Folder\Minecraft Metro 2033 Client\content"
Reads only named local archives, never changes them, never runs donor Java or
scripts, never reads account files. No pip, network or external commands needed.
Output: NEW metro_decor_finish_parts folder with ZIPs <=90 MiB and JSON inventory.
Textures: highest available resolution <=512 by default, or lowest above it if
no smaller variant exists; normal/specular/emission maps included when available.
This collects ORIGINAL source bytes, NOT an installable mod or shader port.
"""
import argparse
from contextlib import ExitStack
import hashlib
import json
from pathlib import Path
import re
import stat
import zipfile

MODEL_MATERIALS = ()
EXTRA_MODELS = ()
EXPECTED_MODELS_SHA = ''
MODEL_PREFIX='assets/metro2033server/models/obj/'
TEXTURE_PREFIX='assets/metro2033server/textures/obj/'
LIMIT=90*1024*1024
MAX_META=1024*1024


def safe_path(name):
    if not name or name.startswith('/') or any(c in name for c in ('\\',':','\x00')) or any(p in ('','.','..') for p in name.split('/')):
        raise ValueError('Unsafe resource path: '+repr(name))
    return name


def mul(a,b):
    result=0
    while b:
        if b&1:result^=a
        a=((a<<1)^ (0x11b if a&0x80 else 0))&255;b>>=1
    return result


def _tables():
    forward=[]
    for x in range(256):
        inv=0 if x==0 else 1
        if x:
            for _ in range(254):inv=mul(inv,x)
        y=inv
        for shift in range(1,5):y^=((inv<<shift)|(inv>>(8-shift)))&255
        forward.append(y^0x63)
    inverse=[0]*256
    for i,n in enumerate(forward):inverse[n]=i
    return forward,inverse,{n:[mul(i,n) for i in range(256)] for n in (9,11,13,14)}


_TABLES=None

def decrypt_blocks(key,data):
    """AES-128 ECB only, to read existing source metadata (not a crypto API)."""
    if len(key)!=16 or len(data)%16:raise ValueError('Invalid AES block/key size')
    global _TABLES
    if _TABLES is None:_TABLES=_tables()
    sbox,inv,tables=_TABLES
    expanded=list(key);rcon=1
    while len(expanded)<176:
        temp=expanded[-4:]
        if len(expanded)%16==0:
            temp=[sbox[x] for x in temp[1:]+temp[:1]];temp[0]^=rcon;rcon=mul(rcon,2)
        for n in temp:expanded.append(expanded[-16]^n)
    keys=[expanded[i:i+16] for i in range(0,176,16)]
    t9,t11,t13,t14=(tables[x] for x in (9,11,13,14))
    result=bytearray()
    for at in range(0,len(data),16):
        state=[a^b for a,b in zip(data[at:at+16],keys[10])]
        for round_ in range(9,-1,-1):
            state=[inv[state[i]] for i in (0,13,10,7,4,1,14,11,8,5,2,15,12,9,6,3)]
            state=[a^b for a,b in zip(state,keys[round_])]
            if round_:
                for i in range(0,16,4):
                    a,b,c,d=state[i:i+4]
                    state[i:i+4]=[t14[a]^t11[b]^t13[c]^t9[d],t9[a]^t14[b]^t11[c]^t13[d],t13[a]^t9[b]^t14[c]^t11[d],t11[a]^t13[b]^t9[c]^t14[d]]
        result.extend(state)
    return bytes(result)


def decode_envelope(data):
    if len(data)<32 or len(data)%16:raise ValueError('Invalid encrypted metadata length')
    plain=decrypt_blocks(data[:16],data[16:]);pad=plain[-1]
    if not 1<=pad<=16 or plain[-pad:]!=bytes([pad])*pad:raise ValueError('Invalid encrypted metadata padding')
    return plain[:-pad]


def material_text(data):
    # Plain MTL is accepted for synthetic fixtures/unmodified exports. The donor
    # .m2033gl4 uses the same 16-byte-key AES envelope as .m2033model.
    if not data.lstrip().startswith((b'#',b'newmtl')):data=decode_envelope(data)
    try:return data.decode('utf-8-sig')
    except UnicodeDecodeError:return data.decode('cp1251')


def texture_roots(text):
    roots=set();have_material=False
    for line in text.splitlines():
        line=line.strip()
        if not line or line.startswith('#'):continue
        if line.startswith('newmtl '):have_material=True
        if not line.startswith('map_Kd '):continue
        if not have_material:raise ValueError('map_Kd before newmtl')
        value=line[len('map_Kd '):].strip().strip('"')
        if value.startswith('-'):raise ValueError('Unsupported map_Kd options; refusing to guess')
        # The original loader uses the exported Windows basename and text before
        # the FIRST dot, then adds .SIZE.m2033texture under textures/obj/.
        name=value.replace('\\','/').rsplit('/',1)[-1].split('.',1)[0]
        safe_path(name)
        if '/' in name:raise ValueError('Unexpected texture basename')
        roots.add(name)
    if not have_material:raise ValueError('Not an MTL material file')
    return roots


def sha_file(path):
    h=hashlib.sha256()
    with Path(path).open('rb') as f:
        for chunk in iter(lambda:f.read(1024*1024),b''):h.update(chunk)
    return h.hexdigest()


def choose_textures(index,roots,size):
    selected=set();missing=[];choices=[]
    for root in sorted(roots):
        pattern=re.compile(re.escape(TEXTURE_PREFIX+root)+r'(?P<map>_[nse])?(?:\.(?P<size>[0-9]+))?\.(?P<ext>m2033texture|png)$')
        groups={}
        for name in index:
            m=pattern.fullmatch(name)
            if m:groups.setdefault(m['map'] or '',[]).append((int(m['size'] or 0),m['ext'],name))
        if '' not in groups:missing.append(root)
        for channel,values in sorted(groups.items()):
            sizes=sorted({v[0] for v in values});under=[s for s in sizes if s<=size];chosen=max(under) if under else min(sizes)
            candidates=sorted((v for v in values if v[0]==chosen),key=lambda v:(v[1]!='m2033texture',v[2]));name=candidates[0][2]
            selected.add(name);choices.append({'root':root,'map':channel or 'diffuse','resolution':chosen,'path':name,'above_requested_size':chosen>size})
    return selected,missing,choices


def collect(content,output=None,size=512,limit=LIMIT,expected_model_sha=EXPECTED_MODELS_SHA):
    content=Path(content);output=Path(output) if output else content/'metro_decor_finish_parts'
    if output.exists():raise FileExistsError('Output exists; choose a NEW --output folder')
    if size not in (128,256,512,1024,2048,4096) or not 1024<=limit<=LIMIT:raise ValueError('Invalid texture/part size')
    models=content/'Metro2033Models.jar'
    print('Checking original models archive...',flush=True)
    if expected_model_sha and sha_file(models)!=expected_model_sha:raise ValueError('Models.jar SHA256 differs from the uploaded source inventory')
    textures=sorted(p for p in content.iterdir() if re.fullmatch(r'Metro2033Textures[0-9]+\.jar',p.name))
    connected=content/'Metro2033ConnectedTextures.jar'
    sources=[models]+textures+([connected] if connected.is_file() else [])
    if not textures:print('WARNING: No Metro2033TexturesN.jar archives found; material collection will still work.',flush=True)
    with ExitStack() as stack:
        archives=[stack.enter_context(zipfile.ZipFile(p)) for p in sources];index={}
        for i,archive in enumerate(archives):
            for info in archive.infolist():
                if not info.is_dir() and info.filename.startswith((MODEL_PREFIX,TEXTURE_PREFIX)):
                    index.setdefault(info.filename,[]).append((i,info))
        def entry(name):
            safe_path(name);matches=index.get(name,[])
            if len(matches)>1:raise ValueError('Conflicting/duplicate resource: '+name)
            if not matches:return None
            i,info=matches[0]
            if stat.S_ISLNK(info.external_attr>>16) or info.flag_bits&1:raise ValueError('Symlink or ZIP encryption: '+name)
            return i,info
        selected=set();missing_models=[];material_paths=set(MODEL_MATERIALS);header_errors=[]
        for ref in EXTRA_MODELS:
            name=MODEL_PREFIX+ref;found=entry(name)
            if not found:missing_models.append(ref);continue
            i,info=found;selected.add(name)
            # Only decode the beginning to discover mtllib. Full model validation
            # remains in the importer; copied members are read completely with CRC.
            with archives[i].open(info) as f:data=f.read(16+4096)
            if len(data)<32:header_errors.append(name);continue
            n=(len(data)-16)//16*16;head=decrypt_blocks(data[:16],data[16:16+n]).decode('latin1')
            matches=re.findall(r'^mtllib\s+([^\r\n]+)',head,re.M)
            if len(matches)!=1:header_errors.append(name);continue
            ref_=matches[0].strip().removeprefix('./');safe_path(ref_)
            if not ref_.endswith('.mtl'):header_errors.append(name);continue
            mat=name.rsplit('/',1)[0]+'/'+ref_[:-4]+'.m2033gl4';safe_path(mat);material_paths.add(mat)
        roots=set();missing_materials=[];material_errors=[]
        print('Reading material metadata (no external commands)...',flush=True)
        for name in sorted(material_paths):
            found=entry(name)
            if not found:missing_materials.append(name);continue
            i,info=found
            if info.file_size>MAX_META:raise ValueError('Material metadata too large: '+name)
            selected.add(name)
            try:roots.update(texture_roots(material_text(archives[i].read(info))))
            except (ValueError,UnicodeError) as bad:material_errors.append({'path':name,'error':str(bad)})
        texture_paths,missing_diffuse,choices=choose_textures(index,roots,size);selected.update(texture_paths)
        # Validate all selected archive paths and maximum file size before output.
        for name in selected:
            _,info=entry(name);cost=info.file_size+2*len(name.encode('utf-8'))+256
            if cost+256>limit:raise ValueError('Single resource exceeds part limit: '+name)
        output.mkdir(parents=True);writer=None;parts=[];records=[];used=256;part=None
        print('Copying',len(selected),'source files into bounded ZIP parts...',flush=True)
        try:
            for name in sorted(selected):
                i,info=entry(name);cost=info.file_size+2*len(name.encode('utf-8'))+256
                if writer is None or used+cost>limit:
                    if writer is not None:writer.close()
                    part=output/('metro_decor_finish_%02d.zip'%(len(parts)+1));parts.append(part)
                    writer=zipfile.ZipFile(part,'x',compression=zipfile.ZIP_STORED,allowZip64=False);used=256
                h=hashlib.sha256();count=0
                with archives[i].open(info) as src,writer.open(name,'w') as dst:
                    for chunk in iter(lambda:src.read(1024*1024),b''):
                        count+=len(chunk)
                        if count>info.file_size:raise ValueError('Source exceeds declared size')
                        h.update(chunk);dst.write(chunk)
                if count!=info.file_size:raise ValueError('Incomplete resource')
                records.append({'path':name,'source':sources[i].name,'bytes':count,'sha256':h.hexdigest(),'part':part.name});used+=cost
        finally:
            if writer is not None:writer.close()
    part_rows=[]
    for p in parts:
        if p.stat().st_size>limit:raise ValueError('Part exceeds output limit')
        with zipfile.ZipFile(p) as z:
            if z.testzip() is not None:raise ValueError('Output CRC mismatch')
        part_rows.append({'name':p.name,'bytes':p.stat().st_size,'sha256':sha_file(p)})
    report={'sources':[{'name':p.name,'bytes':p.stat().st_size} for p in sources],
            'original_models_sha256_verified':bool(expected_model_sha),'source_textures_full_hash_computed':False,
            'requested_texture_size':size,'files':records,'parts':part_rows,'material_paths':sorted(material_paths),'texture_roots':sorted(roots),
            'texture_choices':choices,'missing_models':missing_models,'missing_materials':missing_materials,'material_errors':material_errors,
            'model_header_errors':header_errors,'missing_diffuse_roots':missing_diffuse,
            'runtime_port_implemented':False,'complete_pack_verified':False,
            'status':'original source dependencies only; no installation, no donor execution, no source changes'}
    with (output/'decor_finish_inventory.json').open('x',encoding='utf-8') as f:f.write(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    print('Output:',output.resolve());print('ZIP parts:',len(parts),'Missing materials:',len(missing_materials),'Missing diffuse textures:',len(missing_diffuse))
    print('Upload ZIP parts AND decor_finish_inventory.json. Do not install these as a mod.')
    return report


if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('content');p.add_argument('--output');p.add_argument('--size',type=int,default=512);args=p.parse_args()
    try:collect(args.content,args.output,args.size)
    except (ValueError,OSError,zipfile.BadZipFile) as error:raise SystemExit('ERROR: '+str(error))
