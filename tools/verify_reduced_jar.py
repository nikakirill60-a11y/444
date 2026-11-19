#!/usr/bin/env python3
"""Validate the actual distribution, not merely source resources (Python 3.8+)."""
import fnmatch
import json
import sys
import zipfile
from pathlib import Path


def verify_decor(names,read):
    # Derive the forbidden files independently of the manifest packed into the JAR.
    try:
        from .decor_distribution import cut
    except ImportError:
        from decor_distribution import cut
    decor=cut()
    aliases={p.replace('assets/metroport/','assets/minetro/',1) for p in decor['removedFiles'] if p.startswith('assets/metroport/')}
    assert not (aliases & names), 'Retired door/lamp/gate aliases leaked'
    assert not (set(decor['removedFiles']) & names), 'Retired door/lamp/gate resources leaked'
    assert set(decor['requiredLeverFiles']) <= names, 'Missing lever mesh/shared texture'
    assert json.loads(read('assets/metroport/metromod_gates.json'))==[], 'Retired gate profiles leaked'
    levers=json.loads(read('assets/metroport/metromod_interactive.json'))
    assert len(levers)==6 and {p['id'] for p in levers}==set(decor['leverIds'])
    assert all(p['kind']=='Lever' for p in levers)
    for id in decor['leverIds']:
        assert 'assets/metroport/models/item/'+id+'.json' in names
        assert 'assets/metroport/blockstates/'+id+'.json' in names
    print('Decor verified: 75 imported door/gate/lamp models and 6 legacy gate variants absent; 6 levers intact')


def verify(path):
    with zipfile.ZipFile(path) as jar:
        names=set(jar.namelist())
        verify_decor(names,jar.read)
        scope=json.loads(jar.read('assets/metroport/reduced_scope.json'))
        leaked=sorted(n for n in names if any(fnmatch.fnmatchcase(n,p) for p in scope['jarExcludes']))
        if leaked:raise AssertionError('Retired distribution resources: '+repr(leaked[:20]))
        required=['models/item/'+id+'.json' for id in scope['retainedSpecialItems']+scope['decorCubeIds']]
        required += ['models/blackrazid/gadgets/'+name+'.obj.gz' for name in ['flashlight','flyashka','dynamo','meh','strelka']]
        required += ['textures/blackrazid/gadgets/'+name for name in ['clock.png','flashlight_d.png','veh_zuzik.2048.png','wpn_atach.2048.png']]
        required += ['models/block/exodus/diamond_block.json','models/block/exodus/pumpkin.json']
        for relative in required:
            if 'assets/metroport/'+relative not in names:raise AssertionError('Missing retained dependency: '+relative)
        metadata=json.loads(jar.read('mcmod.info'))
        assert metadata[0]['modid']=='minetro'
        for name in names:
            if name.startswith(('assets/metroport/models/','assets/metroport/blockstates/')) and name.endswith('.json'):
                assert name.replace('assets/metroport/','assets/minetro/',1) in names, 'Missing registry asset alias: '+name
        assert 'assets/minetro/sounds.json' in names
        assert 'item.minetro.mcmod_watch.name=' in jar.read('assets/minetro/lang/ru_ru.lang').decode('utf-8')
        # Validate parent/texture references of every surviving Metro item/block model.
        missing=set()
        for name in sorted(names):
            if not name.startswith(('assets/metroport/models/','assets/minetro/models/')) or not name.endswith('.json'):continue
            model=json.loads(jar.read(name))
            refs=[]
            parent=model.get('parent','')
            if parent.startswith('metroport:'):refs.append('assets/metroport/models/'+parent.split(':',1)[1]+'.json')
            for tex in model.get('textures',{}).values():
                if tex.startswith('metroport:'):refs.append('assets/metroport/textures/'+tex.split(':',1)[1]+'.png')
            for ref in refs:
                if ref not in names:missing.add((name,ref))
        if missing:raise AssertionError('Broken model dependencies: '+repr(sorted(missing)[:25]))
        print('Distribution verified:',len(scope['removedIds']),'retired IDs;',len(required),'required retained resources;',Path(path).stat().st_size,'bytes')

if __name__=='__main__':
    if len(sys.argv)!=2:raise SystemExit('Usage: python tools/verify_reduced_jar.py path/to/mod.jar')
    verify(sys.argv[1])
