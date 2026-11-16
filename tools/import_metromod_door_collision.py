#!/usr/bin/env python3
"""Extract donor Door model-mode bounds. Static analysis only; no donor execution.

This is NOT the surface-voxel importer. Raw model extrema are inputs to the
original bounds-provider algorithm. Render matrices are deliberately not used.
Reviewed override classes are allowlisted; an unknown behavior fails closed.
"""
import argparse
import gzip
import hashlib
import json
import struct
import zipfile
from pathlib import Path
from tools.audit_metromod_decor import CATALOG, KINDS, catalogue, instructions, kind_fields, read_class

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'src/main/resources/assets/metroport'
SHA = '66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc'
BASE = 'iiiIIlilijlllijjljIIiIjiIijIjjll'
# class -> (union auxiliary model bounds, grow X/Z by one). Confirmed in bytecode.
RULES = {
    BASE: (False, False),
    'jililjllIIllIijIljlliiiljllIijjj': (False, True),
    'IiijjiIjIlllIIIjiIjllilIljIjjlII': (False, True),
    'iIIlIiIjIlIiiiiIjjIiiIijjjlIIIii': (True, True),
    'jIjiIliiIljIlIlIIIjlljjliIjIIIij': (False, True),
    'jIjlIijiIIiIjiilIjjiIjIijjjIjiII': (True, True),
    'IIIllijilijIjlIllIIjijIjIjiljIjj': (False, True),
    'jiiIIjlIilIiilljjiiljjjIIjijjill': (False, True),
}
CORE = ['llIjIlllliiiIiililjjjIjiIijijiiI', 'lIjijlilIlIIIiIiIlIIIIlIijljllij',
        'ijijjIlllljIIjijjjlljIIljiiliiiI', 'iIIijIiiiiIlIIjjIjlIjliijIIjIill',
        'jjjjIiijiiIlljllilIjliijlijIIIiI', 'IijIjjillljIIIiiiIilIljilIiIIIii']

def sha(data): return hashlib.sha256(data).hexdigest()
def f32(v): return struct.unpack('!f', struct.pack('!f', v))[0]
def extrema(text):
    vertices = [list(map(f32, map(float, s.split()[1:4]))) for s in text.splitlines() if s.startswith('v ')]
    if not vertices: raise ValueError('No vertices')
    return [min(v[i] for v in vertices) for i in range(3)] + [max(v[i] for v in vertices) for i in range(3)]

def run(client):
    if sha(Path(client).read_bytes()) != SHA: raise ValueError('Unexpected donor client')
    profiles = json.loads((RES/'metromod_interactive.json').read_text())
    by_model = {p['source']+'.m2033model': p for p in profiles if p['kind']=='Door'}
    rows = {}; sources = {}; classes = {}
    with zipfile.ZipFile(client) as z:
        entries = catalogue(instructions(read_class(z, CATALOG))[2], kind_fields(read_class(z, KINDS)))
        audit = json.loads((ROOT/'reports/METRO_MOD/functional_decor_audit.json').read_text())
        for name in CORE + list(RULES):
            path = 'null/'+name+'.class'; classes[path] = sha(z.read(path))
        for e in entries:
            if e['kind'] != 'Door': continue
            p = by_model[e['model']]
            reviewed = next(r for r in audit['entries'] if r['model']==e['model'])['behavior_classes']
            if len(reviewed)!=1: raise ValueError('Unknown behavior composition')
            cls = reviewed[0].split('/')[-1]
            union, grow = RULES[cls]
            # Parts are used only to locate raw assets, NOT their render poses.
            parts = p['parts'] if union else p['parts'][:1]
            bounds = []
            for part in parts:
                path = RES/part['mesh'].split(':',1)[1]
                text = gzip.decompress(path.read_bytes()).decode()
                bounds.append(extrema(text)); sources[part['mesh']] = sha(path.read_bytes())
            rows[p['id']] = {'models': bounds, 'grow': [1,0,1] if grow else [0,0,0],
                             'behavior': 'null/'+cls, 'source': e['model']}
    if len(rows)!=27: raise ValueError('Door coverage changed')
    (RES/'metromod_door_collision.json').write_text(json.dumps(rows,ensure_ascii=False,indent=2)+'\n')
    report = {'client_sha256': SHA, 'door_count':len(rows), 'class_sha256': classes,
              'packaged_model_sha256': sources, 'rules': rows,
              'coordinate_adapter': 'Keep current MinETRO ground/offset/facing, unit placement scale. Bounds do not follow render animation.',
              'physical_rule': 'Door activated => no model-mode AABB, independent of animX; inactive => stored bounds + translation.',
              'selection_rule': 'Keep separate static bounds for interaction; replaceable proxies are not physics owners.',
              'map_specific_custom_aabbs_available': False,
              'germo_block_mode_ported': False, 'minecraft_gameplay_verified': False}
    (ROOT/'reports/METRO_MOD/DONOR_DOOR_COLLISION.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    return report

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('client')
    print('Extracted',run(parser.parse_args().client)['door_count'],'donor Door collision profiles')
