"""Validate the actual delivered decor ZIP, not mocked asset paths."""
import csv
import gzip
import hashlib
import io
import json
import math
import posixpath
import struct
import unittest
import zipfile
import zlib
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BUNDLE = ROOT / 'downloads/MinETRO_Decor_Germo_Doors_Lamps_v1.zip'
RES = ROOT / 'src/main/resources/assets/metroport'


class DecorAssetBundle(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        with zipfile.ZipFile(BUNDLE) as z:
            assert z.testzip() is None
            assert len(z.namelist()) == len(set(z.namelist()))
            cls.files = {n: z.read(n) for n in z.namelist()}
        cls.manifest = json.loads(cls.files['MANIFEST.json'])
        cls.profiles = json.loads((RES / 'metromod_gates.json').read_text()) + [
            p for p in json.loads((RES / 'metromod_interactive.json').read_text()) if p['kind'] != 'Lever']
        cls.by_id = {p['id']: p for p in cls.profiles}

    def test_catalog_has_exactly_requested_objects(self):
        m = self.manifest
        self.assertFalse(m['standalone_mod'])
        self.assertFalse(m['minecraft_tested'])
        self.assertEqual(m['counts'], {'Germo': 17, 'Door': 27, 'Light': 31})
        self.assertEqual(m['animated_geometry_objects'], 44)
        self.assertEqual({p['id'] for p in m['objects']}, set(self.by_id))
        table = list(csv.DictReader(io.StringIO(self.files['CATALOG_RU.csv'].decode('utf-8-sig')), delimiter=';'))
        self.assertEqual(len(table), 75)
        self.assertEqual([int(p['number']) for p in table], list(range(1, 76)))
        self.assertEqual(Counter(p['kind'] for p in table), Counter(m['counts']))
        self.assertFalse(any('mls_vorota_l06' in n or 'metromod_lever_' in n for n in self.files))

    def test_every_native_mesh_texture_and_material_dependency_present(self):
        for p in self.profiles:
            for part in p['parts']:
                refs = [part['mesh'], *part['materials'].values()]
                for ref in refs:
                    name = 'assets/' + ref.replace(':', '/', 1)
                    self.assertEqual(self.files[name], (ROOT / 'src/main/resources' / name).read_bytes())
                obj = gzip.decompress(self.files['assets/' + part['mesh'].replace(':', '/', 1)]).decode()
                used = {s.split(None, 1)[1].strip() for s in obj.splitlines() if s.startswith('usemtl ')}
                self.assertTrue(used)
                self.assertTrue(used <= part['materials'].keys())

    def test_portable_obj_preserves_uv_geometry_and_resolves_mtl_png(self):
        for row in self.manifest['objects']:
            folder = row['folder']
            for index, part in enumerate(self.by_id[row['id']]['parts']):
                name = f'{folder}/part_{index:02}.obj'
                native = gzip.decompress(self.files['assets/' + part['mesh'].replace(':', '/', 1)]).decode()
                obj = self.files[name].decode()
                strip = lambda s: [line for line in s.splitlines() if not line.startswith('mtllib ')]
                self.assertEqual(strip(native), strip(obj), name)
                mtllib = obj.splitlines()[0].split(None, 1)[1]
                mtl_name = posixpath.normpath(folder + '/' + mtllib)
                mtl = self.files[mtl_name].decode()
                materials = {s.split(None, 1)[1] for s in mtl.splitlines() if s.startswith('newmtl ')}
                self.assertEqual(materials, set(part['materials']))
                for line in mtl.splitlines():
                    if line.startswith('map_Kd '):
                        path = posixpath.normpath(folder + '/' + line.split(None, 1)[1])
                        self.assertIn(path, self.files)
                        self.assertTrue(path.startswith('assets/metroport/textures/'))

    def test_animation_preserves_frames_translation_fallback_and_grounding(self):
        ground = json.loads((RES / 'metromod_ground.json').read_text())
        layout = json.loads((RES / 'metromod_layout.json').read_text())
        for row in self.manifest['objects']:
            p = self.by_id[row['id']]
            animation = json.loads(self.files[row['folder'] + '/animation.json'])
            self.assertEqual(animation['matrix_order'], 'row-major')
            self.assertEqual(animation['geometry_animated'], row['kind'] != 'Light')
            offset = layout.get(p['id'], {}).get('offset', [0, 0, 0])
            self.assertEqual(animation['root_translation'], [offset[0], ground.get(p['id'], p['ground']) + offset[1], offset[2]])
            self.assertEqual(animation['step_per_tick'], p['step'])
            for original, portable in zip(p['parts'], animation['parts']):
                matrices = portable['frames']
                self.assertEqual(len(matrices), 65)
                if 'frames' in original:
                    self.assertEqual(matrices, original['frames'])
                else:
                    for i, matrix in enumerate(matrices):
                        self.assertEqual([matrix[3], matrix[7], matrix[11]], [v*i/64 for v in original['move']])
                for matrix in matrices:
                    self.assertEqual(len(matrix), 16)
                    self.assertTrue(all(math.isfinite(v) for v in matrix))
                    self.assertEqual(matrix[12:], [0, 0, 0, 1])

    def test_png_chunks_crc_and_nonzero_size(self):
        textures = [data for n, data in self.files.items() if n.endswith('.png')]
        self.assertTrue(textures)
        for data in textures:
            self.assertEqual(data[:8], b'\x89PNG\r\n\x1a\n')
            self.assertTrue(all(struct.unpack('>II', data[16:24])))
            offset = 8
            while offset < len(data):
                size = int.from_bytes(data[offset:offset+4], 'big')
                chunk = data[offset+4:offset+8+size]
                self.assertEqual(zlib.crc32(chunk) & 0xffffffff, int.from_bytes(data[offset+8+size:offset+12+size], 'big'))
                offset += 12 + size
            self.assertEqual(offset, len(data))

    def test_all_files_manifest_and_source_hashes_match(self):
        self.assertEqual(set(self.files) - {'MANIFEST.json'}, set(self.manifest['files']))
        for path, record in self.manifest['files'].items():
            self.assertEqual(len(self.files[path]), record['bytes'])
            self.assertEqual(hashlib.sha256(self.files[path]).hexdigest(), record['sha256'])
        for path, expected in self.manifest['source_files_sha256'].items():
            self.assertEqual(hashlib.sha256((ROOT / path).read_bytes()).hexdigest(), expected)


if __name__ == '__main__':
    unittest.main()
