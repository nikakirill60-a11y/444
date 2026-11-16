#!/usr/bin/env python3
"""Import the six fully understood translational METRO_MOD gates, not old gate assets.

Only allowlisted verified meshes/materials/diffuse maps are packaged. Conservative
1/4-block surface voxels are merged for server collision. No donor Java execution.
"""
import argparse
import gzip
import hashlib
import io
import itertools
import json
import math
from pathlib import Path
import zipfile
from PIL import Image
from tools.decode_metromod_content import decode
from tools.inspect_metromod_decor_finish import material_bindings
from tools.inspect_metromod_decor_models import obj_metadata, PREFIX
from tools.decor_finish_template import material_text
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources/assets/metroport'
# Translations are from each original behavior's render method (negative state x).
SPECS=[(1,[(0,[0,3,0])]),(2,[(0,[0,0,0]),(1,[0,3.76,0])]),
       (4,[(0,[2.4,0,0]),(1,[-2.4,0,0])]),(5,[(0,[-2.6,0,0]),(1,[2.6,0,0])]),
       (6,[(0,[0,0,0]),(1,[-3.3,0,0]),(2,[3.3,0,0])]),
       (9,[(0,[7.15,0,0]),(1,[-7.15,0,0])])]


def triangles(text):
    vertices=[]
    for line in text.splitlines():
        p=line.split()
        if not p:continue
        if p[0]=='v':vertices.append(tuple(map(float,p[1:4])))
        if p[0]=='f':
            points=[vertices[int(t.split('/')[0])-1] for t in p[1:]]
            for i in range(1,len(points)-1):yield [points[0],points[i],points[i+1]]


def cross(a,b):return (a[1]*b[2]-a[2]*b[1],a[2]*b[0]-a[0]*b[2],a[0]*b[1]-a[1]*b[0])
def dot(a,b):return sum(x*y for x,y in zip(a,b))
def overlap(tri,cell):
    # Complete triangle/AABB separating axis test, with a tiny conservative epsilon.
    v=[tuple(t[i]-(cell[i]+.5)*.25 for i in range(3)) for t in tri]
    edges=[tuple(v[(j+1)%3][i]-v[j][i] for i in range(3)) for j in range(3)]
    axes=[(1,0,0),(0,1,0),(0,0,1),cross(edges[0],edges[1])]
    axes += [cross(e,a) for e in edges for a in [(1,0,0),(0,1,0),(0,0,1)]]
    for a in axes:
        radius=.125*sum(abs(x) for x in a)+1e-8
        d=[dot(p,a) for p in v]
        if min(d)>radius or max(d)<-radius:return False
    return True


def collision(text):
    cells=set()
    for tri in triangles(text):
        lo=[math.floor(min(t[i] for t in tri)*4-1e-6) for i in range(3)]
        hi=[math.floor(max(t[i] for t in tri)*4+1e-6) for i in range(3)]
        for cell in itertools.product(*(range(lo[i],hi[i]+1) for i in range(3))):
            if cell not in cells and overlap(tri,cell):cells.add(cell)
    # Greedy non-overlapping cuboids, deterministic and independent of Python hash seed.
    boxes=[]
    while cells:
        start=min(cells);end=list(start)
        for axis in (0,1,2):
            while True:
                slab=list(itertools.product(*(range(start[i],end[i]+1) if i!=axis else [end[i]+1] for i in range(3))))
                if not all(c in cells for c in slab):break
                end[axis]+=1
        for c in itertools.product(*(range(start[i],end[i]+1) for i in range(3))):cells.remove(c)
        boxes.append([x/4 for x in start]+[(x+1)/4 for x in end])
    return boxes


def write(path,data):
    p=RES/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data)
def js(path,data):write(path,(json.dumps(data,ensure_ascii=False,indent=2)+'\n').encode())
def sha(b):return hashlib.sha256(b).hexdigest()


def run(models,finish):
    verified=json.loads((ROOT/'reports/METRO_MOD/decor_geometry_verified.json').read_text())
    extra=json.loads((ROOT/'reports/METRO_MOD/decor_finish_verified.json').read_text())
    expected={e['source_path']:e['source_sha256'] for e in verified['models']+extra['files']}
    profiles=[];provenance={}
    with zipfile.ZipFile(models) as mz,zipfile.ZipFile(finish) as fz:
        def get(name):
            archive=mz if name in mz.namelist() else fz
            data=archive.read(name)
            if sha(data)!=expected[name]:raise ValueError('Unverified asset: '+name)
            provenance[name]=sha(data);return data
        for n,parts in SPECS:
            profile={'id':'metromod_germo_'+str(n),'source':'germo/germo'+str(n),
                     'name_ru':'Гермоворота METRO_MOD '+str(n),'step':.015,'parts':[]}
            for suffix,move in parts:
                stem='germo'+str(n)+('_add'+str(suffix) if suffix else '')
                path=PREFIX+'germo/'+stem+'.m2033model';plain=decode(get(path));meta=obj_metadata(plain,path)
                text=plain.decode(meta['encoding']);bindings={}
                for mat in meta['material_libraries']:bindings.update(material_bindings(material_text(get(mat))))
                maps={}
                for label in meta['usemtl']:
                    root=bindings[label]
                    if not root:raise ValueError('Untextured gate material: '+label)
                    tex='assets/metro2033server/textures/obj/'+root+'.512.m2033texture'
                    data=decode(get(tex))
                    with Image.open(io.BytesIO(data)) as im:
                        im.load();buf=io.BytesIO();im.convert('RGBA').save(buf,format='PNG')
                    write(Path('textures/metromod/gates')/(root+'.png'),buf.getvalue())
                    maps[label]='metroport:textures/metromod/gates/'+root+'.png'
                mesh=Path('models/metromod/gates')/(stem+'.obj.gz')
                write(mesh,gzip.compress(text.encode('utf-8'),mtime=0))
                boxes=collision(text)
                profile['parts'].append({'mesh':'metroport:'+str(mesh),'materials':maps,'move':move,'boxes':boxes})
                print(stem,len(boxes),'collision boxes',flush=True)
            # Ground the original model without rescaling its authored geometry.
            low=min(b[1] for part in profile['parts'] for b in part['boxes'])
            profile['ground']=-low
            profiles.append(profile)
            js(Path('models/item')/(profile['id']+'.json'),{'parent':'minecraft:block/iron_block'})
            js(Path('blockstates')/(profile['id']+'.json'),{'variants':{f'facing={f}':{'model':'minecraft:iron_block'} for f in ['north','south','east','west']}})
    js(Path('metromod_gates.json'),profiles)
    js(Path('metromod_gates_provenance.json'),{'source_files':provenance,'profiles':[p['id'] for p in profiles],
       'collision':'conservative triangle/AABB surface voxelization at 0.25 blocks, merged cuboids',
       'animation':'original translational endpoints; Forge tick-rate adapter 0.015 normalized per tick',
       'remaining_germo_models':[3,7,8],'all_functional_decor_ported':False})


if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('models');p.add_argument('finish');a=p.parse_args();run(a.models,a.finish)
