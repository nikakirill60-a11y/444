"""The helper collects only identified source geometry, never a fake completed menu."""
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
import zipfile
ROOT=Path(__file__).resolve().parents[1]
spec=importlib.util.spec_from_file_location('collect_menu',ROOT/'downloads/collect_metromod_menu.py')
helper=importlib.util.module_from_spec(spec);spec.loader.exec_module(helper)

class MenuCollectionTest(unittest.TestCase):
    def test_dependencies_match_static_report(self):
        report=json.loads((ROOT/'reports/METRO_MOD/menu_audit.json').read_text())
        self.assertEqual(tuple(report['model_references']),helper.MODEL_REFERENCES)
        self.assertEqual(35,len(helper.MODEL_REFERENCES));self.assertFalse(report['target_implemented'])
        self.assertEqual('localhost:25565',report['requested_port_target'])
    def test_only_selected_bytes_and_namespace_are_preserved(self):
        with tempfile.TemporaryDirectory() as temp:
            p=Path(temp);jar=p/'Models.jar'
            with zipfile.ZipFile(jar,'w') as z:
                for ref in helper.MODEL_REFERENCES:z.writestr('assets/metro2033server/models/'+ref,b'original encrypted model')
                z.writestr('loginpass.txt',b'never collect');z.writestr('assets/weapon/gun.m2033model',b'never collect')
            r=helper.collect(jar,p/'out');self.assertEqual(35,r['matched']);self.assertEqual([],r['missing'])
            with zipfile.ZipFile(p/'out'/r['parts'][0]['name']) as z:
                self.assertEqual(35,len(z.namelist()));self.assertNotIn('loginpass.txt',z.namelist())
                for n in z.namelist():self.assertEqual(b'original encrypted model',z.read(n))
    def test_missing_dependencies_are_explicit(self):
        with tempfile.TemporaryDirectory() as temp:
            p=Path(temp)
            with zipfile.ZipFile(p/'in.jar','w'):pass
            r=helper.collect(p/'in.jar',p/'out');self.assertEqual(35,len(r['missing']));self.assertEqual(0,r['matched'])
    def test_parts_remain_independent_zips_under_limit(self):
        with tempfile.TemporaryDirectory() as temp:
            p=Path(temp);refs=['menu/a.m2033model','menu/b.m2033model']
            with zipfile.ZipFile(p/'in.jar','w') as z:
                for ref in refs:z.writestr(ref,b'A'*400)
            r=helper.collect(p/'in.jar',p/'out',references=refs,limit=1024)
            self.assertEqual(2,len(r['parts']))
            for part in r['parts']:
                self.assertLessEqual(part['bytes'],1024)
                with zipfile.ZipFile(p/'out'/part['name']) as z:self.assertIsNone(z.testzip())
    def test_existing_output_is_never_overwritten(self):
        with tempfile.TemporaryDirectory() as temp:
            p=Path(temp);out=p/'out';out.mkdir()
            with self.assertRaises(FileExistsError):helper.collect(p/'absent.jar',out)
    def test_unsafe_and_ambiguous_members_are_rejected_before_output(self):
        for names in [['../menu/a.m2033model'],['one/menu/a.m2033model','two/menu/a.m2033model']]:
            with tempfile.TemporaryDirectory() as temp:
                p=Path(temp)
                with zipfile.ZipFile(p/'in.jar','w') as z:
                    for name in names:z.writestr(name,b'bytes')
                with self.assertRaises(ValueError):helper.collect(p/'in.jar',p/'out',references=['menu/a.m2033model'])
                self.assertFalse((p/'out').exists())
    def test_oversized_model_is_rejected_before_output(self):
        with tempfile.TemporaryDirectory() as temp:
            p=Path(temp)
            with zipfile.ZipFile(p/'in.jar','w') as z:z.writestr('menu/a.m2033model',b'A'*1024)
            with self.assertRaises(ValueError):helper.collect(p/'in.jar',p/'out',references=['menu/a.m2033model'],limit=1024)
            self.assertFalse((p/'out').exists())
