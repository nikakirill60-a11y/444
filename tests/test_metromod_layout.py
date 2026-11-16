import gzip,json,unittest
from tools.refine_metromod_layout import run,RES
from tools.metromod_pose_reader import transform
PS=json.loads((RES/'metromod_gates.json').read_text())+json.loads((RES/'metromod_interactive.json').read_text())

class MetromodLayoutTest(unittest.TestCase):
    def test_generator_reproducible_without_original_archives(self):
        path=RES/'metromod_layout.json';before=path.read_bytes();run();self.assertEqual(before,path.read_bytes())
    def test_exact_scope_no_changed_meshes_or_unknown_ids(self):
        rows=json.loads((RES/'metromod_layout.json').read_text());self.assertEqual(50,len(rows))
        self.assertEqual({p['id'] for p in PS if p.get('kind','Germo') in ('Germo','Door','Lever')},set(rows))
    def test_switch_body_exactly_meets_wall_plane(self):
        rows=json.loads((RES/'metromod_layout.json').read_text())
        for p in PS:
            if p.get('kind')!='Lever':continue
            row=rows[p['id']];body=next(part for part in p['parts'] if not part['moving'])
            data=gzip.decompress((RES/body['mesh'].split(':',1)[1]).read_bytes()).decode()
            vertices=[transform(body['frames'][0],list(map(float,line.split()[1:4]))) for line in data.splitlines() if line.startswith('v ')]
            self.assertAlmostEqual(-.498,min(v[2] for v in vertices)+row['offset'][2],places=7)
            self.assertAlmostEqual(0,(min(v[0] for v in vertices)+max(v[0] for v in vertices))/2+row['offset'][0],places=7)
    def test_moving_panels_are_solid_not_sparse_triangle_shells(self):
        rows=json.loads((RES/'metromod_layout.json').read_text())
        for p in PS:
            if p.get('kind','Germo') not in ('Germo','Door'):continue
            for i,part in enumerate(p['parts']):
                if part.get('moving',any(part['move'])):
                    boxes=rows[p['id']]['solid_parts'][str(i)];self.assertEqual(1,len(boxes))
                    for b in part['boxes']:
                        self.assertTrue(all(boxes[0][k]<=b[k] and boxes[0][k+3]>=b[k+3] for k in range(3)))
                else:self.assertNotIn(str(i),rows[p['id']]['solid_parts'])
