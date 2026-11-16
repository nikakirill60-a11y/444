"""Derive runtime layout fixes from already verified, packaged native meshes.
No donor downloads, mesh edits, rescaling or guessed wall offsets.
"""
import gzip,json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources/assets/metroport'

def run():
    profiles=json.loads((RES/'metromod_gates.json').read_text())+json.loads((RES/'metromod_interactive.json').read_text())
    result={}
    for p in profiles:
        kind=p.get('kind','Germo');row={}
        if kind in ('Germo','Door'):
            boxes={}
            for i,part in enumerate(p['parts']):
                moving=part.get('moving',any(part['move']))
                if moving:
                    bs=part['boxes'];boxes[str(i)]=[[min(b[k] for b in bs) for k in range(3)]+[max(b[k+3] for b in bs) for k in range(3)]]
            row['solid_parts']=boxes
        if kind=='Lever':
            body=next(part for part in p['parts'] if not part['moving'])
            text=gzip.decompress((RES/body['mesh'].split(':',1)[1]).read_bytes()).decode()
            m=body['frames'][0];vs=[]
            for line in text.splitlines():
                if line.startswith('v '):
                    v=list(map(float,line.split()[1:4]));vs.append([sum(m[i*4+j]*v[j] for j in range(3))+m[i*4+3] for i in range(3)])
            lo=[min(v[i] for v in vs) for i in range(3)];hi=[max(v[i] for v in vs) for i in range(3)]
            row.update(offset=[round(-(lo[0]+hi[0])/2,9),0,round(-.5+.002-lo[2],9)],body_bounds=lo+hi)
        if row:result[p['id']]=row
    (RES/'metromod_layout.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
if __name__=='__main__':run()
