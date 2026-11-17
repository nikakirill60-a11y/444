"""Original scene asset closure, without claiming an installed or rendered GUI."""
import copy
import gzip
import hashlib
import json
from pathlib import Path
import unittest
from PIL import Image

from tools.import_metromod_menu_assets import prepare, verify_closure
from tools.inspect_metromod_decor_models import obj_metadata

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'src/main/resources/assets/metroport'
REPORTS = ROOT / 'reports/METRO_MOD'


class OriginalMenuAssetsTest(unittest.TestCase):
    def setUp(self):
        self.manifest = json.loads((RES / 'metromod_menu.json').read_text())
        self.geometry = json.loads((REPORTS / 'menu_geometry_verified.json').read_text())
        self.finish = json.loads((REPORTS / 'menu_finish_verified.json').read_text())

    def test_complete_collected_mesh_material_and_diffuse_closure(self):
        verify_closure(self.geometry, self.finish)
        self.assertEqual(35, self.manifest['model_count'])
        self.assertEqual(97, self.manifest['texture_count'])
        self.assertEqual(132, len(self.manifest['assets']))
        self.assertEqual(self.geometry['source_zip_sha256'], self.manifest['models_zip_sha256'])
        self.assertEqual(self.finish['source_zip_sha256'], self.manifest['materials_zip_sha256'])
        self.assertFalse(self.manifest['gui_implemented'])
        self.assertFalse(self.manifest['in_game_verified'])
        self.assertFalse(self.finish['donor_java_executed'])
        inventory = (REPORTS / 'menu_finish_inventory.json').read_bytes()
        self.assertEqual(self.finish['inventory_sha256'], hashlib.sha256(inventory).hexdigest())

    def test_all_packaged_resources_match_manifest_hashes_and_paths(self):
        paths = set()
        for asset in self.manifest['assets']:
            relative = asset['path']
            self.assertNotIn('..', relative)
            self.assertNotIn('\\', relative)
            self.assertNotIn(relative, paths); paths.add(relative)
            data = (RES / relative).read_bytes()
            self.assertEqual(asset['bytes'], len(data))
            self.assertEqual(asset['sha256'], hashlib.sha256(data).hexdigest())
        actual = {p.relative_to(RES).as_posix()
                  for folder in ('models/metromod/menu', 'textures/metromod/menu')
                  for p in (RES / folder).rglob('*') if p.is_file()}
        self.assertEqual(paths, actual)

    def test_obj_geometry_uv_normals_and_material_labels_are_unchanged(self):
        expected = {r['reference']: r for r in self.geometry['models']}
        self.assertEqual(set(expected), {m['source_reference'] for m in self.manifest['models']})
        for mesh in self.manifest['models']:
            original = expected[mesh['source_reference']]
            path = RES / mesh['mesh'].removeprefix('metroport:')
            plain = gzip.decompress(path.read_bytes())
            self.assertEqual(original['decoded_sha256'], hashlib.sha256(plain).hexdigest())
            parsed = obj_metadata(plain, original['source_path'])
            self.assertEqual(original['counts'], parsed['counts'])
            self.assertEqual(original['bounds'], parsed['bounds'])
            self.assertEqual(original['triangles'], mesh['triangles'])
            self.assertEqual(set(parsed['usemtl']), set(mesh['materials']))
            for channels in mesh['materials'].values():
                self.assertIn('diffuse', channels)
                self.assertLessEqual(set(channels), {'diffuse', 'normal', 'specular', 'emission'})
                for location in channels.values():
                    self.assertTrue(location.startswith('metroport:textures/metromod/menu/'))
                    self.assertTrue((RES / location.removeprefix('metroport:')).is_file())

    def test_every_texture_is_the_verified_original_decode(self):
        expected = {r['source_path'].split('/textures/obj/', 1)[1].removesuffix('.m2033texture') + '.png': r
                    for r in self.finish['files'] if r['type'] == 'texture'}
        for name, row in expected.items():
            path = RES / 'textures/metromod/menu' / name
            self.assertEqual(row['decoded_sha256'], hashlib.sha256(path.read_bytes()).hexdigest())
            with Image.open(path) as image:
                self.assertEqual((row['width'], row['height']), image.size)
                image.verify()

    def test_incomplete_closure_is_refused_instead_of_using_an_atlas_substitute(self):
        for key in ('missing_materials', 'missing_material_labels', 'missing_diffuse_textures',
                    'original_missing_models', 'resolved_collector_material_errors'):
            bad = copy.deepcopy(self.finish); bad[key] = ['missing']
            with self.assertRaisesRegex(ValueError, 'Incomplete'):
                verify_closure(self.geometry, bad)
        with self.assertRaisesRegex(ValueError, 'SHA256'):
            prepare(b'wrong original models', b'wrong materials')
