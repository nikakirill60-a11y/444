#!/usr/bin/env python3
"""v12 -> v13: remove only АКК and replace mini flamethrower with supplied weapon.zip geometry."""
import argparse,copy,io,json,hashlib
from pathlib import Path
import numpy as np
from PIL import Image
import build_mwf_v11 as v11
from build_mwf_v12 import primitive_names
from build_uboinik_pack import obj_read,icon
from integrate_mwf_3d import bounds,fit,transformed,png
from patch_mwf_geometry import append_mesh
from update_mwf_v10 import readzip,writezip,js,triangles
ROOT=Path(__file__).resolve().parents[1]
P='assets/modularwarfare/'
PACK='Metro_Full_Arsenal_MWF_fix1_v13_weapon_update.zip'
BASE_SHA='5017abbfef1e88f3d9a0767c5dec5723729d3f72840e81503df8eb68e429667d'
MINI='metro_ognemet_mini'
def prepare(files,source):
    before=dict(files)
    old=v11.gun(files,'metro_akk');retired={'metro_akk',*old.get('acceptedAmmo',[])}
    removed=[]
    for n in list(files):
        if any(Path(n).name in (id+'.json',id+'.render.json',id+'.gltf',id+'.obj',id+'.png') for id in retired):
            removed.append(n);del files[n]
        elif n.startswith(P+'lang/') and n.endswith('.lang'):
            files[n]=b'\n'.join(line for line in files[n].split(b'\n') if not any(line.startswith(('item.'+id+'.').encode()) for id in retired))
    d=v11.model(files,MINI);oldDoc=copy.deepcopy(d)
    anchor=next(n for n in d['nodes'] if n['name']=='gunModel')
    target=v11.mesh_bounds(d,['gunModel'])
    raw=sum(obj_read(source['ognemet_mini/ognemet_dlc.obj']).values(),[])
    # This replacement's bore axis is X (NOT Z as in Metro guns 2). Keep every triangle/UV.
    factor,shift=fit(raw,target)
    converted=transformed(raw,factor,shift,gltf=True)
    for n in d['nodes']:
        if 'Arm' not in n['name']:n.pop('mesh',None)
    trs={k:copy.deepcopy(anchor[k]) for k in ('translation','rotation','scale') if k in anchor}
    index=append_mesh(d,'weaponZipMini',converted,trs)
    anchor['mesh']=d['nodes'][index]['mesh'];d['nodes'][0]['children'].remove(index);d['nodes'].pop()
    primitive_names(d)
    modelPath=P+'gltf/guns/'+v11.render(files,MINI)['modelFileName']
    files[modelPath]=js(d)
    # Exact original diffuse texture, no shared atlas, recolouring, resampling or UV substitution.
    files[P+'skins/guns/'+MINI+'.png']=source['ognemet_mini/wpn34_ognemet_b.png']
    tex=Image.open(io.BytesIO(files[P+'skins/guns/'+MINI+'.png']))
    ts=list(triangles(d,v11.render(files,MINI)))
    files[P+'textures/items/'+MINI+'.png']=png(icon(ts,tex,384).resize((256,256),Image.Resampling.LANCZOS))
    report=dict(minecraftLiveTested=False,basePackSha256=BASE_SHA,sourceArchive='tools/weapon3/sources.zip',sourceZipSha256=v11.sha((ROOT/'tools/weapon3/sources.zip').read_bytes()),removedItems=sorted(retired),removedFiles=sorted(removed),
        mini=dict(sourceTriangles=len(raw),sourceGroups=list(obj_read(source['ognemet_mini/ognemet_dlc.obj'])),sourceToModelScale=factor,shift=shift.tolist(),rotationDegrees=0,textureSha256=v11.sha(files[P+'skins/guns/'+MINI+'.png']),handAndRootAnimationPreserved=True,originalStaticMesh=True),
        requiresJarSha256='08dd2089f00cac9d1d4948d8db074f2f03bbb93095af6cc47cb366d9c96557d9',
        limitations=['No live Minecraft/OpenGL verification. New supplied model is static; existing hands/reload timings retained.','Mini flamethrower gameplay remains the previous MWF bullet-based adaptation, not a new flame simulation.','Replace the old pack; do not install versions together. Removed АКК items cannot be preserved in existing saves.'])
    assert d['animations']==oldDoc['animations']
    for oldNode,newNode in zip(oldDoc['nodes'],d['nodes']):
        if 'Arm' in oldNode['name']:assert oldNode==newNode
    assert report['mini']['sourceTriangles']==17418
    assert not any(Path(n).stem=='metro_akk' for n in files)
    guns=[n for n in files if n.startswith('guns/') and n.count('/')==1 and n.endswith('.json')]
    assert len(guns)==28
    for n in guns:
        g=json.loads(files[n]);assert set(g['fireModes'])<={'semi','full','burst'}
        if g['internalName']!=MINI:
            id=g['internalName'];assert files[n]==before[n]
            for path in ['guns/render/'+id+'.render.json',P+'gltf/guns/'+v11.render(files,id)['modelFileName'],P+'skins/guns/'+g['modelSkins'][0]['skinAsset']+'.png']:assert files[path]==before[path],path
    allowed=set(removed)|{n for n in before if n.startswith(P+'lang/')}|{modelPath,P+'skins/guns/'+MINI+'.png',P+'textures/items/'+MINI+'.png','FULL_PACK_MANIFEST.json'}
    assert all(files[n]==b for n,b in before.items() if n not in allowed)
    manifest=json.loads(files['FULL_PACK_MANIFEST.json']);manifest['counts']={cat:sum(n.startswith(cat+'/') and n.count('/')==1 and n.endswith('.json') for n in files) for cat in ('guns','ammo','bullets','attachments')};manifest['weaponUpdateV13']=report
    files['FULL_PACK_MANIFEST.json']=js(manifest)
    report.update(verifiedGunCount=28,sharedArsenalAtlasRemoved=True,unrelatedWeaponsByteIdentical=True)
    files['WEAPON_UPDATE_V13_MANIFEST.json']=js(report)
    return report

def main():
    p=argparse.ArgumentParser();p.add_argument('--pack',type=Path,required=True);p.add_argument('--output',type=Path,default=ROOT/'.cache/output-v13');a=p.parse_args()
    assert v11.sha(a.pack.read_bytes())==BASE_SHA,'Expected exact verified v12 baseline'
    files=readzip(a.pack);report=prepare(files,readzip(ROOT/'tools/weapon3/sources.zip'));a.output.mkdir(parents=True,exist_ok=True)
    writezip(a.output/PACK,files);report.update(pack=PACK,packSha256=v11.sha((a.output/PACK).read_bytes()))
    (a.output/'MWF_V13_report.json').write_bytes(js(report));print(json.dumps(report,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
