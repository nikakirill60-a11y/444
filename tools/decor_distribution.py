"""Exact distribution cut: doors/gates/static lamps out, six levers and shared textures in.
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
    rows=profiles();levers=[p for p in rows if p.get('kind','Germo')=='Lever'];retired=[p for p in rows if p.get('kind','Germo')!='Lever']
    keep=dependencies(levers)
    removed=dependencies(retired)-keep
    ids=LEGACY_IDS+[p['id'] for p in retired]
    removed.update('assets/metroport/'+folder+id+'.json' for id in ids for folder in ['models/item/','blockstates/'])
    removed.update('assets/metroport/models/legacy/'+name+'.obj' for name in ['germa','germaramka'])
    removed.update('assets/metroport/textures/gates/'+name+'.png' for name in ['germa','model_techne','modelgermo1','modelgermoleft'])
    removed.update('assets/metroport/'+name+'.json' for name in ['metromod_gates_provenance','metromod_door_collision','metromod_germo_collision'])
    removed.update('ru/metrocraft/port/client/gates/'+name+'.class' for name in MODEL_CLASSES)
    return dict(removedIds=ids,removedFiles=sorted(removed),requiredLeverFiles=sorted(keep),leverIds=[p['id'] for p in levers])
