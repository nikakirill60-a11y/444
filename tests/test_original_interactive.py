"""Packaged coverage, affine poses, original geometry and dependency closure."""
import collections,gzip,hashlib,json,math,re,unittest
from pathlib import Path
from PIL import Image
from tools.metromod_pose_reader import IDENTITY,multiply,transform,PoseReader
from tools.inspect_metromod_decor_models import obj_metadata
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources/assets/metroport'
PROFILES=json.loads((RES/'metromod_interactive.json').read_text())
COVERAGE=json.loads((ROOT/'reports/METRO_MOD/interactive_coverage.json').read_text())

class OriginalInteractiveTest(unittest.TestCase):
    def test_complete_accounting_including_blockers_not_silent_success(self):
        self.assertEqual(112,COVERAGE['total']);self.assertEqual(81,COVERAGE['ported']);self.assertEqual(31,COVERAGE['blocked']);self.assertFalse(COVERAGE['all_functional_decor_ported'])
        self.assertEqual(75,len(PROFILES));self.assertEqual(75,len({p['id'] for p in PROFILES}))
        self.assertEqual({'Germo':11,'Door':27,'Lever':6,'Light':31},dict(collections.Counter(p['kind'] for p in PROFILES)))
        self.assertEqual({p['id'] for p in PROFILES},{r['id'] for r in COVERAGE['entries'] if r['status']=='ported'})
        self.assertTrue(all(r.get('reason') for r in COVERAGE['entries'] if r['status']=='blocked'))
    def test_mesh_material_texture_and_block_dependency_closure(self):
        for p in PROFILES:
            for path in ['models/item/','blockstates/']:self.assertTrue((RES/(path+p['id']+'.json')).exists())
            for part in p['parts']:
                data=gzip.decompress((RES/part['mesh'].split(':',1)[1]).read_bytes())
                meta=obj_metadata(data,'assets/metro2033server/models/obj/a.m2033model');self.assertEqual(set(meta['usemtl']),set(part['materials']))
                for texture in part['materials'].values():
                    with Image.open(RES/texture.split(':',1)[1]) as im:im.verify()
    def test_native_obj_hashes_no_cube_replacement_or_rescaling(self):
        records=json.loads((ROOT/'reports/METRO_MOD/decor_geometry_verified.json').read_text())['models']
        records+=json.loads((ROOT/'reports/METRO_MOD/decor_finish_verified.json').read_text())['files']
        records={r['source_path']:r for r in records}
        for p in PROFILES:
            for part in p['parts']:
                suffix=part['mesh'].split('/interactive/',1)[1].replace('.obj.gz','.m2033model')
                rec=records['assets/metro2033server/models/obj/'+suffix]
                data=gzip.decompress((RES/part['mesh'].split(':',1)[1]).read_bytes())
                if rec.get('encoding')=='utf-8':self.assertEqual(rec['decoded_sha256'],hashlib.sha256(data).hexdigest())
    def test_affine_finite_frame_schema_and_bounded_collision(self):
        for p in PROFILES:
            self.assertRegex(p['id'],r'^metromod_(germo|door|lever|light|inventory)_[a-z0-9_]+$')
            self.assertTrue(math.isfinite(p['ground']))
            for part in p['parts']:
                self.assertEqual(65,len(part['frames']));self.assertTrue(part['boxes'])
                self.assertEqual(part['moving'],any(f!=part['frames'][0] for f in part['frames'][1:]))
                for f in part['frames']:
                    self.assertEqual(16,len(f));self.assertEqual([0,0,0,1],f[12:]);self.assertTrue(all(math.isfinite(v) and abs(v)<=128 for v in f))
                for b in part['boxes']:
                    self.assertEqual(6,len(b));self.assertTrue(all(-32<=v<=32 for v in b));self.assertTrue(all(b[i]<b[i+3] for i in range(3)))
    def test_transform_order_is_opengl_post_multiply(self):
        a=IDENTITY[:];a[3]=2;b=IDENTITY[:];b[0]=3
        self.assertEqual([5.,0.,0.],transform(multiply(a,b),[1,0,0]));self.assertEqual([9.,0.,0.],transform(multiply(b,a),[1,0,0]))
    def test_unknown_calls_and_gl_operations_rejected(self):
        reader=PoseReader(None);reader.allowed=set()
        with self.assertRaises(ValueError):reader.call('java/lang/Runtime','exec','()V',None,[])
        with self.assertRaises(ValueError):reader.call('org/lwjgl/opengl/GL11','glDrawArrays','()V',None,[])
        with self.assertRaises(ValueError):reader.field({'class':'Unknown','fields':{}},'Unknown','unknown','F')
    def test_provenance_hashes_match_verified_archives(self):
        evidence=json.loads((RES/'metromod_interactive_provenance.json').read_text())
        ver=json.loads((ROOT/'reports/METRO_MOD/decor_geometry_verified.json').read_text())['models']
        ver+=json.loads((ROOT/'reports/METRO_MOD/decor_finish_verified.json').read_text())['files']
        expected={r['source_path']:r['source_sha256'] for r in ver}
        self.assertFalse(evidence['donor_server_reproduced']);self.assertEqual(65,evidence['frames_per_part'])
        for path,digest in evidence['source_files'].items():self.assertEqual(expected[path],digest)
    def test_localization_generation_keeps_all_new_names_once(self):
        from tools.mcmod_languages import update
        update();before={p:p.read_bytes() for p in (RES/'lang').glob('*.lang')};update()
        for path,data in before.items():
            self.assertEqual(data,path.read_bytes())
            for p in PROFILES:self.assertEqual(1,path.read_text().count('tile.metroport.'+p['id']+'.name='))
    def test_lifecycle_endpoint_gates_are_present_and_animated(self):
        indexed={p['source']:p for p in PROFILES}
        for source,count in [('germo/germo7',3),('germo/germo8',3),('decor_metro/l07_vorota',4)]:
            profile=indexed[source];self.assertEqual(count,len(profile['parts']))
            self.assertTrue(any(part['moving'] for part in profile['parts']))
        # Authored germo7 initial yaw -1.25 degrees plus endpoint -9 * 10.
        leaf=indexed['germo/germo7']['parts'][1]
        self.assertAlmostEqual(math.cos(math.radians(-91.25)),leaf['frames'][-1][0],places=7)
    def test_source_wiring_uses_same_matrix_for_render_and_collision(self):
        java=ROOT/'src/main/java/ru/metrocraft/port'
        self.assertIn('part.matrix(progress)',(java/'client/gates/OriginalGateRenderer.java').read_text())
        self.assertIn('part.matrix((float)t)',(java/'metromod/decor/OriginalGateProfile.java').read_text())
        tile=(java/'metromod/decor/OriginalGateTile.java').read_text();self.assertNotIn('IInventory',tile);self.assertNotIn('displayGUIChest',tile)

if __name__=='__main__':unittest.main()
