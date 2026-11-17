"""Verified menu meshes and a source-only material collector, not a runtime GUI."""
import copy
import hashlib
import importlib.util
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch
import zipfile

from tools import decor_finish_template as template
from tools.inspect_metromod_menu_models import check_inventory, MODELS_SHA
from tools.prepare_menu_finish import generate

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / 'reports/METRO_MOD/menu_geometry_verified.json'
INVENTORY = ROOT / 'reports/METRO_MOD/menu_model_inventory.json'


class MenuGeometryTest(unittest.TestCase):
    def setUp(self):
        self.report = json.loads(REPORT.read_text())
        self.given = json.loads(INVENTORY.read_text())
        self.references = json.loads((ROOT / 'reports/METRO_MOD/menu_audit.json').read_text())['model_references']

    def test_all_35_direct_models_and_provenance(self):
        check_inventory(self.given, self.references)
        report = self.report
        self.assertEqual(MODELS_SHA, report['original_models_jar_sha256'])
        self.assertEqual(35, report['validated_models'])
        self.assertEqual(35, len(report['material_paths']))
        self.assertEqual(109791, report['geometry_totals']['triangles'])
        self.assertEqual(hashlib.sha256(INVENTORY.read_bytes()).hexdigest(), report['inventory_sha256'])
        expected = {r['path']: r for r in self.given['files']}
        for row in report['models']:
            self.assertEqual(expected[row['source_path']]['sha256'], row['source_sha256'])
        self.assertNotIn('dynamic_suffix_correction', report)
        self.assertFalse(report['runtime_port_implemented'])
        self.assertFalse(report['donor_java_executed'])
        self.assertEqual([], report['original_collector_missing'])

    def test_wrong_source_missing_duplicate_or_noncanonical_reference_refused(self):
        cases = []
        bad = copy.deepcopy(self.given); bad['source_sha256'] = '0' * 64; cases.append(bad)
        bad = copy.deepcopy(self.given); bad['files'].pop(); cases.append(bad)
        bad = copy.deepcopy(self.given); bad['files'][-1] = bad['files'][0]; cases.append(bad)
        bad = copy.deepcopy(self.given); bad['missing'] = ['menu/part1.m2033model']; cases.append(bad)
        bad = copy.deepcopy(self.given); bad['files'][0]['path'] = '../menu/model'; cases.append(bad)
        for bad in cases:
            with self.assertRaises(ValueError):
                check_inventory(bad, self.references)

    def test_collector_regenerates_and_runs_without_repository_imports(self):
        source = generate(self.report)
        self.assertEqual(source, (ROOT / 'downloads/finishmenu.py').read_text())
        self.assertIn('EXTRA_MODELS = ()', source)
        self.assertIn('menu_finish_inventory.json', source)
        self.assertNotIn('metro_decor_finish', source)
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / 'finishmenu.py'; path.write_text(source)
            result = subprocess.run([sys.executable, '-I', str(path), '--help'], cwd=temp,
                                    capture_output=True, text=True, check=True)
            self.assertIn('3D-menu', result.stdout)
            spec = importlib.util.spec_from_file_location('menu_finish_fixture', path)
            module = importlib.util.module_from_spec(spec); spec.loader.exec_module(module)
            self.assertEqual(tuple(self.report['material_paths']), module.MODEL_MATERIALS)
            self.assertEqual(MODELS_SHA, module.EXPECTED_MODELS_SHA)

    def test_unverified_geometry_cannot_generate_collector(self):
        for value in ({}, {'direct_menu_reference_set_verified': True, 'validated_models': 34}):
            with self.assertRaises(ValueError):
                generate(value)

    def test_standalone_collector_copies_only_menu_material_texture_closure(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp); script = root / 'finishmenu.py'; script.write_text(generate(self.report))
            spec = importlib.util.spec_from_file_location('menu_finish_fixture', script)
            module = importlib.util.module_from_spec(spec); spec.loader.exec_module(module)
            material = template.MODEL_PREFIX + 'menu/part1.m2033gl4'
            texture = template.TEXTURE_PREFIX + 'menu_diffuse.512.m2033texture'
            with zipfile.ZipFile(root / 'Metro2033Models.jar', 'w') as archive:
                archive.writestr(material, 'newmtl Material\nmap_Kd menu_diffuse.dds\n')
                archive.writestr('loginpass.txt', 'never copied')
            with zipfile.ZipFile(root / 'Metro2033Textures512.jar', 'w') as archive:
                archive.writestr(texture, b'original encrypted bytes retained')
                archive.writestr(template.TEXTURE_PREFIX + 'weapon.512.m2033texture', b'excluded')
            with patch.object(module, 'MODEL_MATERIALS', (material,)):
                result = module.collect(root, expected_model_sha='')
            self.assertEqual([], result['missing_materials'])
            self.assertEqual([], result['missing_diffuse_roots'])
            self.assertEqual({material, texture}, {r['path'] for r in result['files']})
            out = root / 'metro_menu_finish_parts'
            self.assertTrue((out / 'menu_finish_inventory.json').is_file())
            with zipfile.ZipFile(out / result['parts'][0]['name']) as archive:
                self.assertIsNone(archive.testzip())
                self.assertEqual(b'original encrypted bytes retained', archive.read(texture))
            with self.assertRaises(FileExistsError):
                module.collect(root, expected_model_sha='')
