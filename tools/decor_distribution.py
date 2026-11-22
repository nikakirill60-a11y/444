"""Exact distribution cut: ordinary doors/static lamps/legacy duplicates out; original Germo gates and levers in.
Source donors stay in Git for provenance. This module never rewrites those sources.
"""
import json
from pathlib import Path
A=Path(__file__).resolve().parents[1]/'src/main/resources/assets/metroport'
LEGACY_IDS=['germo','germo_open','germo_right','germo_left','germo_left45','germo_half_open']
MODEL_CLASSES=['ModelGermo','ModelGermo1','ModelGermoLeft','ModelGermoRamka','GateRenderer','GateItemRenderer']
def profiles():
    return json.loads((A/'metromod_gates.json').read_text())+json.loads((A/'metromod_interactive.json').read_text())
def dependencies(rows):
    return {'assets/'+ref.replace(':','/',1) for row in rows for part in row['parts']
            for ref in [part['mesh'],*part['materials'].values()]}
def cut():
    rows=profiles();levers=[p for p in rows if p.get('kind','Germo')=='Lever'];germo=[p for p in rows if p.get('kind','Germo')=='Germo'];retired=[p for p in rows if p.get('kind','Germo') not in ('Lever','Germo')]
    keep=dependencies(levers+germo)
    removed=dependencies(retired)-keep
    ids=LEGACY_IDS+[p['id'] for p in retired]
    removed.update('assets/metroport/'+folder+id+'.json' for id in ids for folder in ['models/item/','blockstates/'])
    removed.update('assets/metroport/models/legacy/'+name+'.obj' for name in ['germa','germaramka'])
    removed.update('assets/metroport/textures/gates/'+name+'.png' for name in ['germa','model_techne','modelgermo1','modelgermoleft'])
    removed.update('assets/metroport/'+name+'.json' for name in ['metromod_door_collision'])
    removed.update('ru/metrocraft/port/client/gates/'+name+'.class' for name in MODEL_CLASSES)
    return dict(removedIds=ids,removedFiles=sorted(removed),requiredLeverFiles=sorted(dependencies(levers)),requiredGermoFiles=sorted(dependencies(germo)|{'assets/metroport/metromod_germo_collision.json','assets/metroport/metromod_gates_provenance.json'}),leverIds=[p['id'] for p in levers],germoIds=[p['id'] for p in germo])
