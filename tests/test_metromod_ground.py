import json,gzip,unittest
from tools.ground_metromod_doors import calculate,RES,ROOT
class GroundedDoorTest(unittest.TestCase):
    def test_generated_ground_matches_exact_closed_mesh_vertices(self):
        self.assertEqual(calculate(),json.loads((RES/'metromod_ground.json').read_text()))
    def test_all_44_closed_visual_bottoms_are_exactly_on_floor(self):
        fixes=calculate();count=0
        for name in ['metromod_gates.json','metromod_interactive.json']:
            for p in json.loads((RES/name).read_text()):
                if p.get('kind','Germo') not in ('Door','Germo'):continue
                self.assertIn(p['id'],fixes);low=float('inf')
                for part in p['parts']:
                    m=part.get('frames',[[1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]])[0]
                    for line in gzip.decompress((ROOT/'src/main/resources/assets'/part['mesh'].replace(':','/',1)).read_bytes()).decode().splitlines():
                        if line.startswith('v '):
                            v=list(map(float,line.split()[1:4]));low=min(low,sum(m[4+i]*v[i] for i in range(3))+m[7]+fixes[p['id']])
                self.assertAlmostEqual(0,low,places=9,msg=p['id']);count+=1
        self.assertEqual(44,count)
