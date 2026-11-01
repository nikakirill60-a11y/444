#!/usr/bin/env python3
"""Byte-exact native multipart assets; poses are an explicit 1.12 renderer adaptation, not old GL stack pops."""
import gzip, hashlib, json, re
from pathlib import Path
from import_content import ROOT, ASSETS, put, write_json
from blackrazid_weapon_profiles import models,poses
S=ROOT/'tools/blackrazid-reference'; A=S/'assets/weapons'; J=S/'java/mods/provoda'

def material_map(path):
    result={};name=None
    for line in path.read_text().splitlines():
        if line.startswith('newmtl '):name=line[7:].strip()
        elif line.strip().startswith('map_Kd '):
            texture=line.strip()[7:].strip();assert '/' not in texture and '\\' not in texture
            source=path.parent/texture
            if not source.exists():source=A/'textures'/texture
            assert source.is_file(),source
            relative=source.relative_to(A).as_posix().lower();runtime='textures/blackrazid/weapons/'+relative
            put(ASSETS/runtime,source.read_bytes());result[name]='metroport:'+runtime
    assert result
    return result

def bounds(path):
    v=[list(map(float,line.split()[1:])) for line in path.read_text().splitlines() if line.startswith('v ')]
    assert v and all(len(p)==3 for p in v)
    return [min(p[i] for p in v) for i in range(3)]+[max(p[i] for p in v) for i in range(3)]

def catalog():
    modules=[]
    for path in sorted((J/'items/att/renderers').glob('Render*.java')):
        source=path.stem[6:];text=path.read_text();mesh=re.findall(r'new GLModel\("/mods/provoda/guns/models/([^"\n]+)"\)',text)
        assert len(mesh)==1 and re.search(r'registerItemRenderer\([^\n]*ModBase\.'+source+r'\.cp, [^\n]*new '+path.stem+r'\(', (J/'ClientProxy.java').read_text())
        id_='blackrazid_mod_'+re.sub(r'(?<!^)(?=[A-Z])','_',source).lower()
        raw=A/mesh[0];b=bounds(raw);center=[(b[i]+b[i+3])/2 for i in range(3)];scale=.48/max(b[i+3]-b[i] for i in range(3))
        modules.append(dict(id=id_,sourceId=source,mesh=mesh[0],center=center,scale=scale,source='items/att/renderers/'+path.name))
    assert len(modules)==7
    return modules

def main():
    meshes={}
    for path in sorted(A.glob('*/*.obj')):
        key=path.relative_to(A).as_posix();maps=material_map(path.with_suffix('.mtl'))
        used={x.strip() for x in re.findall(r'^usemtl (.+)',path.read_text(),re.M)};assert used<=maps.keys(),(key,used-maps.keys())
        put(ASSETS/('models/blackrazid/weapons/'+key+'.gz'),gzip.compress(path.read_bytes(),mtime=0))
        meshes[key]=dict(materials=maps,bounds=bounds(path),sha256=hashlib.sha256(path.read_bytes()).hexdigest())
    assert len(meshes)==41
    for name,model in models().items():
        text=(J/('guns/renderers/Render_'+name+'.java')).read_text()
        declared=re.findall(r'new GLModel\("/mods/provoda/guns/models/([^"\n]+)"\)',text)
        assert model['body'] in declared and set(declared)<=meshes.keys(),name
    write_json(ASSETS/'blackrazid_weapon_meshes.json',meshes)
    write_json(ASSETS/'blackrazid_weapon_module_models.json',catalog())
    write_json(ASSETS/'blackrazid_weapon_models.json',models())
    # Independent appended profiles: never replace the approved revolver or earlier weapon poses.
    write_json(ASSETS/'geometry/blackrazid_weapon_poses.json',poses())
    print('Imported 41 native meshes: five complete declared gun OBJ sets and seven registered standalone module models; old frame animations/visual acceptance remain pending.')
if __name__=='__main__':main()
