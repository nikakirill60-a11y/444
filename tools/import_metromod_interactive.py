#!/usr/bin/env python3
"""Import functional decor with fail-closed donor-pose extraction and asset hashes.

Leaves the six dev25 gate IDs/resources byte-identical. Writes a full coverage
report, including unavailable assets/behaviors; never substitutes cube meshes.
"""
import argparse,gzip,io,json,re,zipfile
from pathlib import Path
from PIL import Image
from tools.import_metromod_gates import ROOT,RES,sha,collision,write,js
from tools.inspect_metromod_decor_models import obj_metadata,PREFIX
from tools.inspect_metromod_decor_finish import material_bindings
from tools.decor_finish_template import material_text
from tools.decode_metromod_content import decode
from tools.audit_metromod_decor import catalogue,instructions,read_class,kind_fields,CATALOG,KINDS
from tools.metromod_pose_reader import PoseReader,IDENTITY,transform

KINDS_ALLOWED={'Germo','Door','Lever','Light','Inventory'}
LEGACY={'germo/germo'+str(n)+'.m2033model' for n in (1,2,4,5,6,9)}
FRAMES=65

def run(client,models,finish):
    audit=json.loads((ROOT/'reports/METRO_MOD/functional_decor_audit.json').read_text())
    if sha(Path(client).read_bytes())!=audit['source_sha256']:raise ValueError('Wrong donor client')
    ver=json.loads((ROOT/'reports/METRO_MOD/decor_geometry_verified.json').read_text())
    ext=json.loads((ROOT/'reports/METRO_MOD/decor_finish_verified.json').read_text())
    expected={e['source_path']:e['source_sha256'] for e in ver['models']+ext['files']}
    provenance={};assets={};profiles=[];coverage=[]
    with zipfile.ZipFile(models) as mz,zipfile.ZipFile(finish) as fz,zipfile.ZipFile(client) as cz:
        reader=PoseReader(cz)
        def get(name):
            if name not in expected:raise ValueError('Asset absent from verified source set: '+name)
            archive=mz if name in mz.namelist() else fz
            data=archive.read(name)
            if sha(data)!=expected[name]:raise ValueError('Source hash mismatch '+name)
            provenance[name]=sha(data);return data
        def asset(model):
            if model in assets:return assets[model]
            path=PREFIX+model;plain=decode(get(path));meta=obj_metadata(plain,path);text=plain.decode(meta['encoding']);bindings={}
            for mat in meta['material_libraries']:bindings.update(material_bindings(material_text(get(mat))))
            maps={}
            for label in meta['usemtl']:
                root=bindings[label]
                if not root:raise ValueError('No diffuse material '+label)
                tex='assets/metro2033server/textures/obj/'+root+'.512.m2033texture'
                payload=decode(get(tex));dest=Path('textures/metromod/interactive')/(root+'.png')
                with Image.open(io.BytesIO(payload)) as im:
                    im.load();buf=io.BytesIO();im.convert('RGBA').save(buf,format='PNG')
                write(dest,buf.getvalue());maps[label]='metroport:'+dest.as_posix()
            mesh=Path('models/metromod/interactive')/(model.removesuffix('.m2033model')+'.obj.gz')
            write(mesh,gzip.compress(text.encode(),mtime=0))
            result={'mesh':'metroport:'+mesh.as_posix(),'materials':maps,'boxes':collision(text),'move':[0,0,0]}
            if not result['boxes']:raise ValueError('Empty model '+model)
            assets[model]=result;return result
        entries=catalogue(instructions(read_class(cz,CATALOG))[2],kind_fields(read_class(cz,KINDS)))
        for e in entries:
            if e['kind'] not in KINDS_ALLOWED:continue
            model=e['model'];row={'model':model,'kind':e['kind']};coverage.append(row)
            if model in LEGACY:row.update(status='dev25',id='metromod_germo_'+re.search(r'germo(\d+)',model)[1]);continue
            try:
                if e['kind']=='Inventory':raise ValueError('Client Inventory tag does not establish server storage slots or pickup rules; no invented container')
                if model=='structure/mls_vorota_l06.m2033model':raise ValueError('Required donor dependency structure/mls_vorota_l06_add2.m2033model is missing; partial preview is not a complete port')
                cs=[c for c in e['profile']['components'] if c.get('args') and isinstance(c['args'][0],dict) and c['args'][0].get('class')==e['profile']['class']]
                if len(cs)>1:raise ValueError('Multiple render behaviors require explicit composition')
                poses=[reader.pose(cs[0],model,i/(FRAMES-1)) if cs else [(model,IDENTITY[:])] for i in range(FRAMES)]
                order=[m for m,_ in poses[0]]
                if any([m for m,_ in pose]!=order for pose in poses):raise ValueError('Conditional model topology')
                parts=[]
                for index,m in enumerate(order):
                    a=asset(m);part=dict(a);part['frames']=[[round(v,9) for v in pose[index][1]] for pose in poses]
                    # Render transforms are sampled, matrix-interpolated identically on both sides.
                    part['moving']=any(frame!=part['frames'][0] for frame in part['frames'][1:]);parts.append(part)
                # Authored coordinate system and proportions, grounded, no guessed scale.
                low=min(transform(frame,[b[x+3 if mask&(1<<x) else x] for x in range(3)])[1]
                        for p in parts for frame in p['frames'] for b in p['boxes'] for mask in range(8))
                ident='metromod_'+e['kind'].lower()+'_'+model.removesuffix('.m2033model').replace('/','_')
                if re.fullmatch(r'germo/germo[378]\.m2033model',model):ident='metromod_germo_'+re.search(r'germo([378])\.',model)[1]
                profile={'id':ident,'source':model.removesuffix('.m2033model'),'name_ru':e['name_ru'].strip(),
                         'kind':e['kind'],'step':.015,'ground':round(-low,9),'parts':parts}
                profiles.append(profile);row.update(status='ported',id=ident,parts=len(parts))
                js(Path('models/item')/(ident+'.json'),{'parent':'minecraft:block/iron_block'})
                js(Path('blockstates')/(ident+'.json'),{'variants':{f'facing={f}':{'model':'minecraft:iron_block'} for f in ['north','south','east','west']}})
                print('OK',e['kind'],model,len(parts),flush=True)
            except (ValueError,KeyError,IndexError) as ex:
                row.update(status='blocked',reason=str(ex));print('BLOCKED',model,str(ex),flush=True)
    used={part['mesh'].split(':',1)[1] for p in profiles for part in p['parts']}
    used|={tex.split(':',1)[1] for p in profiles for part in p['parts'] for tex in part['materials'].values()}
    for directory in ['models/metromod/interactive','textures/metromod/interactive']:
        for path in (RES/directory).rglob('*'):
            if path.is_file() and path.relative_to(RES).as_posix() not in used:path.unlink()
    for directory in ['models/item','blockstates']:
        for path in (RES/directory).glob('metromod_inventory_*.json'):path.unlink()
        for path in (RES/directory).glob('metromod_germo_germo_germo*.json'):path.unlink()
        stale=RES/directory/'metromod_germo_structure_mls_vorota_l06.json'
        if stale.exists():stale.unlink()
    js(Path('metromod_interactive.json'),profiles)
    js(Path('metromod_interactive_provenance.json'),{'client_sha256':audit['source_sha256'],'source_files':provenance,
        'frames_per_part':FRAMES,'motion':'donor preview render poses; Forge normalized tick-rate adaptation; linear matrix interpolation',
        'collision':'transformed conservative quarter-block surface voxels, AABB per cuboid',
        'behavior':'Forge adapters: redstone gates, manual/redstone doors and lamps, lever output; unverified inventory behavior excluded',
        'donor_server_reproduced':False})
    report={'total':len(coverage),'ported':sum(r['status']!='blocked' for r in coverage),'blocked':sum(r['status']=='blocked' for r in coverage),
            'all_functional_decor_ported':all(r['status']!='blocked' for r in coverage),'entries':coverage}
    (ROOT/'reports/METRO_MOD/interactive_coverage.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    print('COVERAGE',report['ported'],'/',report['total'])
if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__)
    for name in ['client','models','finish']:p.add_argument(name)
    a=p.parse_args();run(a.client,a.models,a.finish)
