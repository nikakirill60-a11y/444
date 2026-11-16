"""Ground closed Door/Germo meshes exactly; no rounded collision-voxel minima."""
import gzip,json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources/assets/metroport'
def calculate():
    result={}
    for name in ['metromod_gates.json','metromod_interactive.json']:
        for p in json.loads((RES/name).read_text()):
            if p.get('kind','Germo') not in ('Door','Germo'):continue
            low=float('inf')
            for part in p['parts']:
                m=part.get('frames',[[1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]])[0]
                for line in gzip.decompress((ROOT/'src/main/resources/assets'/part['mesh'].replace(':','/',1)).read_bytes()).decode().splitlines():
                    if line.startswith('v '):
                        v=list(map(float,line.split()[1:4]))
                        low=min(low,sum(m[4+i]*v[i] for i in range(3))+m[7])
            result[p['id']]=round(-low,12)
    return result
if __name__=='__main__':
    (RES/'metromod_ground.json').write_text(json.dumps(calculate(),ensure_ascii=False,indent=2)+'\n')
