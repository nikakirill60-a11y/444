"""Physical JAR-resource cut, not a creative-tab visibility check."""
import json,unittest
from pathlib import Path
from tools.decor_distribution import cut,profiles,dependencies
from tools.verify_reduced_jar import verify_decor
A=Path(__file__).resolve().parents[1]/'src/main/resources/assets/metroport'
class DecorDistribution(unittest.TestCase):
    def fixture(self):
        d=cut();names={str(p.relative_to(A.parent.parent)) for p in A.rglob('*') if p.is_file()}
        names-=set(d['removedFiles'])
        active=[p for p in profiles() if p.get('kind','Germo') in ('Germo','Lever')]
        files={'assets/metroport/metromod_gates.json':json.dumps([p for p in active if p in json.loads((A/'metromod_gates.json').read_text())]).encode(),
               'assets/metroport/metromod_interactive.json':json.dumps([p for p in active if p in json.loads((A/'metromod_interactive.json').read_text())]).encode()}
        files.update({'assets/metroport/'+name:(A/name).read_bytes() for name in ['metromod_ground.json']})
        layout=json.loads((A/'metromod_layout.json').read_text())
        files['assets/metroport/metromod_layout.json']=json.dumps({k:v for k,v in layout.items() if k in d['leverIds']+d['germoIds']}).encode()
        return names,files.__getitem__
    def test_all_retired_meshes_and_java_models_are_in_removal_set(self):
        d=cut();removed=set(d['removedFiles'])
        self.assertEqual(64,len(d['removedIds']));self.assertEqual(17,len(d['germoIds']));self.assertEqual(6,len(d['leverIds']))
        for p in profiles():
            for part in p['parts']:
                mesh='assets/'+part['mesh'].replace(':','/',1)
                self.assertEqual(p.get('kind','Germo') not in ('Lever','Germo'),mesh in removed)
        self.assertTrue(any(n.endswith('/ModelGermo.class') for n in removed))
        self.assertTrue(any(n.endswith('/germa.obj') for n in removed))
        self.assertFalse(removed & set(d['requiredLeverFiles']))
    def test_restored_germo_assets_and_collision_metadata_are_not_excluded(self):
        d=cut()
        self.assertFalse(set(d['germoIds']) & set(d['removedIds']))
        self.assertFalse(set(d['requiredGermoFiles']) & set(d['removedFiles']))
        for path in d['requiredGermoFiles']:self.assertTrue((A.parent.parent/path).is_file(),path)
        rules=json.loads((A/'metromod_germo_collision.json').read_text())
        self.assertEqual(set(d['germoIds']),set(rules))
    def test_retained_lever_resources_are_real_and_complete(self):
        for path in cut()['requiredLeverFiles']:self.assertTrue((A.parent.parent/path).is_file(),path)
    def test_actual_archive_verifier_accepts_germo_and_lever_distribution(self):
        verify_decor(*self.fixture())
    def test_archive_verifier_rejects_single_leaked_mesh(self):
        names,read=self.fixture();names.add(next(n for n in cut()['removedFiles'] if n.endswith('.obj.gz')))
        with self.assertRaises(AssertionError):verify_decor(names,read)
    def test_archive_verifier_rejects_missing_germo_collision_metadata(self):
        names,read=self.fixture();names.remove('assets/metroport/metromod_germo_collision.json')
        with self.assertRaises(AssertionError):verify_decor(names,read)
    def test_archive_verifier_rejects_missing_lever_dependency(self):
        names,read=self.fixture();names.remove(cut()['requiredLeverFiles'][0])
        with self.assertRaises(AssertionError):verify_decor(names,read)
