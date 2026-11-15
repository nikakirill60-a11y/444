#!/usr/bin/env python3
"""Validate a collected encrypted model ZIP and inspect geometry without executing Java.

No donor path is extracted. Optional decoded output is a NEW compressed ZIP.
The JSON report is provenance/dependencies, NOT proof of an in-game renderer.
"""
import argparse
import hashlib
import json
import math
from pathlib import Path, PurePosixPath
import re
import stat
import zipfile
from tools.decode_metromod_content import decode

PREFIX='assets/metro2033server/models/obj/'
MAX_FILE=32*1024*1024
MAX_TOTAL=128*1024*1024

def sha(data):return hashlib.sha256(data).hexdigest()

def safe_path(name):
    if not name or any(c in name for c in ('\\',':','\x00')) or name.startswith('/') or any(p in ('','.','..') for p in name.split('/')):
        raise ValueError('Unsafe path: '+repr(name))
    return PurePosixPath(name)

def obj_metadata(data,path):
    path=safe_path(path)
    try:text=data.decode('utf-8-sig');encoding='utf-8'
    except UnicodeDecodeError:text=data.decode('cp1251');encoding='cp1251'
    if '\x00' in text:raise ValueError('NUL in OBJ')
    counts={'v':0,'vt':0,'vn':0,'f':0};lo=[math.inf]*3;hi=[-math.inf]*3
    libraries=set();materials=set();triangles=0;polygons={};max_indices=[0,0,0]
    for line in text.splitlines():
        line=line.split('#',1)[0].strip()
        if not line:continue
        parts=line.split();key=parts[0];values=parts[1:]
        if key in ('v','vt','vn'):
            minimum=2 if key=='vt' else 3
            if not minimum<=len(values)<=4:raise ValueError('Invalid '+key)
            nums=[float(v) for v in values]
            if not all(math.isfinite(v) and abs(v)<1e10 for v in nums):raise ValueError('Non-finite/extreme coordinate')
            counts[key]+=1
            if key=='v':
                for i in range(3):lo[i]=min(lo[i],nums[i]);hi[i]=max(hi[i],nums[i])
        elif key=='f':
            if not 3<=len(values)<=64:raise ValueError('Invalid polygon size')
            counts[key]+=1;triangles+=len(values)-2;polygons[len(values)]=polygons.get(len(values),0)+1
            for vertex in values:
                indices=vertex.split('/')
                if not 1<=len(indices)<=3 or not indices[0]:raise ValueError('Invalid face reference')
                for i,token in enumerate(indices):
                    if not token:continue
                    index=int(token)
                    if index==0:raise ValueError('Zero OBJ index')
                    if index<0:
                        if -index>counts[('v','vt','vn')[i]]:raise ValueError('Invalid relative index')
                    else:max_indices[i]=max(max_indices[i],index)
        elif key=='mtllib':
            # The donor treats the remainder as ONE filename, not as executable
            # statements or a list of whitespace-delimited include paths.
            ref=line[len(key):].strip()
            if ref.startswith('./'):ref=ref[2:]
            safe_path(ref)
            if not ref.endswith('.mtl'):raise ValueError('Unexpected material extension')
            material=str(path.parent.joinpath(ref[:-4]+'.m2033gl4'));safe_path(material)
            if not material.startswith(PREFIX):raise ValueError('Material escapes model namespace')
            libraries.add(material)
        elif key=='usemtl':
            ref=line[len(key):].strip()
            if not ref:raise ValueError('Empty material name')
            materials.add(ref)
        elif key not in ('g','o','s'):raise ValueError('Unsupported OBJ directive: '+key)
    if not counts['v'] or not counts['f']:raise ValueError('Empty mesh')
    if any(max_indices[i]>counts[k] for i,k in enumerate(('v','vt','vn'))):raise ValueError('Out-of-bounds face reference')
    return {'encoding':encoding,'counts':counts,'triangles':triangles,'polygon_sizes':{str(k):v for k,v in sorted(polygons.items())},
            'bounds':{'min':lo,'max':hi},'material_libraries':sorted(libraries),'usemtl':sorted(materials)}

def inspect(source,inventory,report_path,decoded_zip=None):
    source,inventory,report_path=map(Path,(source,inventory,report_path))
    paths=[source,inventory,report_path]+([Path(decoded_zip)] if decoded_zip else [])
    if len({p.resolve() for p in paths})!=len(paths):raise ValueError('Paths must be distinct')
    if report_path.exists() or (decoded_zip and Path(decoded_zip).exists()):raise FileExistsError('Output already exists')
    if source.stat().st_size>MAX_TOTAL or inventory.stat().st_size>2*1024*1024:raise ValueError('Input exceeds size limit')
    raw=source.read_bytes();given=json.loads(inventory.read_text(encoding='utf-8-sig'))
    part=[p for p in given['parts'] if p['name']==source.name]
    if len(part)!=1 or part[0]['bytes']!=len(raw) or part[0]['sha256']!=sha(raw):raise ValueError('Part hash/size mismatch')
    rows=[r for r in given['files'] if r['part']==source.name]
    expected={r['path']:r for r in rows}
    if len(rows)!=len(expected):raise ValueError('Duplicate inventory path')
    models=[];total=0;writer=None
    try:
        if decoded_zip:writer=zipfile.ZipFile(decoded_zip,'x',compression=zipfile.ZIP_DEFLATED,compresslevel=9)
        with zipfile.ZipFile(source) as z:
            infos=z.infolist()
            if len(infos)!=len(expected) or {i.filename for i in infos}!=set(expected):raise ValueError('Archive/inventory membership mismatch')
            for info in infos:
                safe_path(info.filename)
                if not info.filename.startswith(PREFIX) or not info.filename.endswith('.m2033model') or stat.S_ISLNK(info.external_attr>>16) or info.flag_bits&1:
                    raise ValueError('Unexpected model member')
                if info.file_size>MAX_FILE:raise ValueError('Model exceeds size limit')
                total+=info.file_size
                if total>MAX_TOTAL:raise ValueError('Models exceed total size limit')
                data=z.read(info);row=expected[info.filename]
                if row['bytes']!=len(data) or row['sha256']!=sha(data):raise ValueError('Member hash/size mismatch')
                plain=decode(data);meta=obj_metadata(plain,info.filename)
                output=info.filename[:-len('.m2033model')]+'.obj'
                if writer:writer.writestr(output,plain)
                models.append({'reference':row['reference'],'source_path':info.filename,'source_bytes':len(data),'source_sha256':sha(data),
                               'decoded_bytes':len(plain),'decoded_sha256':sha(plain),'decoded_path':output,**meta})
    finally:
        if writer:writer.close()
    report={'source_zip':source.name,'source_zip_bytes':len(raw),'source_zip_sha256':sha(raw),
            'inventory_sha256':sha(inventory.read_bytes()),'original_models_jar_sha256':given['source_sha256'],
            'models':models,'validated_models':len(models),'geometry_totals':{k:sum(r['counts'][k] for r in models) for k in ('v','vt','vn','f')},
            'material_paths':sorted({m for r in models for m in r['material_libraries']}),
            'original_collector_missing':given['missing'],
            'dynamic_suffix_correction':{'not_literal_files':['_add1.m2033model','_add2.m2033model'],
               'constructor':'null/jililjllIIllIijIljlliiiljllIijjj.<init>(profile,String)',
               'resolved_models':['door/d6_gate_platform_add1.m2033model','door/d6_gate_platform_add2.m2033model','structure/gates_01_add1.m2033model','structure/gates_01_add2.m2033model']},
            'material_files_in_this_zip':0,'texture_files_in_this_zip':0,
            'runtime_port_implemented':False,'donor_java_executed':False,
            'status':str(len(models))+' geometry files verified; materials and textures still required for original appearance'}
    if decoded_zip:
        p=Path(decoded_zip);report['decoded_zip']={'name':p.name,'bytes':p.stat().st_size,'sha256':sha(p.read_bytes())}
    with report_path.open('x',encoding='utf-8') as f:f.write(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    return report

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('source');parser.add_argument('inventory');parser.add_argument('report');parser.add_argument('--decoded-zip');args=parser.parse_args()
    r=inspect(args.source,args.inventory,args.report,args.decoded_zip)
    print(r['status']);print(r['geometry_totals']);print('Material files required:',len(r['material_paths']))
