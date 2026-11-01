#!/usr/bin/env python3
"""Validate the actual distribution, not merely source resources (Python 3.8+)."""
import fnmatch
import json
import sys
import zipfile
from pathlib import Path


def verify(path):
    with zipfile.ZipFile(path) as jar:
        names=set(jar.namelist())
        scope=json.loads(jar.read('assets/metroport/reduced_scope.json'))
        leaked=sorted(n for n in names if any(fnmatch.fnmatchcase(n,p) for p in scope['jarExcludes']))
        if leaked:raise AssertionError('Retired distribution resources: '+repr(leaked[:20]))
        required=['models/item/'+id+'.json' for id in scope['retainedSpecialItems']+scope['decorCubeIds']]
        required += ['models/blackrazid/gadgets/'+name+'.obj.gz' for name in ['flashlight','flyashka','dynamo','meh','strelka']]
        required += ['textures/blackrazid/gadgets/'+name for name in ['clock.png','flashlight_d.png','veh_zuzik.2048.png','wpn_atach.2048.png']]
        required += ['models/block/exodus/diamond_block.json','models/block/exodus/pumpkin.json']
        for relative in required:
            if 'assets/metroport/'+relative not in names:raise AssertionError('Missing retained dependency: '+relative)
        # Validate parent/texture references of every surviving Metro item/block model.
        missing=set()
        for name in sorted(names):
            if not name.startswith('assets/metroport/models/') or not name.endswith('.json'):continue
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
