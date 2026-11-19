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
        files={'assets/metroport/metromod_gates.json':b'[]',
               'assets/metroport/metromod_interactive.json':json.dumps([p for p in profiles() if p.get('kind')=='Lever']).encode()}
        return names,files.__getitem__
    def test_all_retired_meshes_and_java_models_are_in_removal_set(self):
        d=cut();removed=set(d['removedFiles'])
        self.assertEqual(81,len(d['removedIds']));self.assertEqual(6,len(d['leverIds']))
        for p in profiles():
            for part in p['parts']:
                mesh='assets/'+part['mesh'].replace(':','/',1)
                self.assertEqual(p.get('kind')!='Lever',mesh in removed)
        self.assertTrue(any(n.endswith('/ModelGermo.class') for n in removed))
        self.assertTrue(any(n.endswith('/germa.obj') for n in removed))
        self.assertFalse(removed & set(d['requiredLeverFiles']))
    def test_retained_lever_resources_are_real_and_complete(self):
        for path in cut()['requiredLeverFiles']:self.assertTrue((A.parent.parent/path).is_file(),path)
    def test_actual_archive_verifier_accepts_lever_only_distribution(self):
        verify_decor(*self.fixture())
    def test_archive_verifier_rejects_single_leaked_mesh(self):
        names,read=self.fixture();names.add(next(n for n in cut()['removedFiles'] if n.endswith('.obj.gz')))
        with self.assertRaises(AssertionError):verify_decor(names,read)
    def test_archive_verifier_rejects_missing_lever_dependency(self):
        names,read=self.fixture();names.remove(cut()['requiredLeverFiles'][0])
        with self.assertRaises(AssertionError):verify_decor(names,read)
