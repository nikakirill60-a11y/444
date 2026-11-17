"""Validate actual v2 export: each model folder can move independently."""
import copy
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import tempfile
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[1]


def archive(path):
    with zipfile.ZipFile(path) as z:
        assert z.testzip() is None
        assert len(z.namelist()) == len(set(z.namelist()))
        return {n: z.read(n) for n in z.namelist()}


class LocalDecorTextures(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.old = archive(os.environ.get('DECOR_V1', ROOT/'downloads/MinETRO_Decor_Germo_Doors_Lamps_v1.zip'))
        cls.new = archive(ROOT/'downloads/MinETRO_Decor_Germo_Doors_Lamps_v2_local_textures.zip')
        cls.manifest = json.loads(cls.new['MANIFEST.json'])

    def test_same_75_objects_and_no_shared_asset_directory(self):
        before = json.loads(self.old['MANIFEST.json'])
        self.assertEqual(self.manifest['objects'], before['objects'])
        self.assertEqual(self.manifest['counts'], {'Germo': 17, 'Door': 27, 'Light': 31})
        self.assertEqual(self.manifest['animated_geometry_objects'], 44)
        self.assertFalse(any(n.startswith('assets/') for n in self.new))
        self.assertEqual(self.new['CATALOG_RU.csv'], self.old['CATALOG_RU.csv'])

    def test_every_obj_and_material_are_in_same_folder(self):
        for name, data in self.new.items():
            if not name.endswith('.obj'):
                continue
            folder = PurePosixPath(name).parent
            for line in data.decode('utf-8').splitlines():
                if line.startswith('mtllib '):
                    filename = line.split(None, 1)[1]
                    self.assertEqual(filename, PurePosixPath(filename).name)
                    self.assertIn(str(folder/filename), self.new)

    def test_every_png_next_to_model_and_mtl_with_exact_original_bytes(self):
        total = 0
        for row in self.manifest['objects']:
            folder = row['folder']
            mapping = self.manifest['source_mapping'][row['id']]['textures']
            self.assertTrue(mapping)
            for original, local in mapping.items():
                self.assertEqual(local, PurePosixPath(local).name)
                self.assertEqual(self.new[folder+'/'+local], self.old[original])
            for name, data in self.new.items():
                if name.startswith(folder+'/') and name.endswith('.mtl'):
                    for line in data.decode().splitlines():
                        if line.startswith('map_Kd '):
                            texture = line.split(None, 1)[1]
                            self.assertEqual(texture, PurePosixPath(texture).name)
                            self.assertTrue(texture.endswith('.png'))
                            self.assertIn(folder+'/'+texture, self.new)
                            total += 1
        self.assertGreaterEqual(total, 143)

    def test_geometry_uv_and_all_animation_values_unchanged(self):
        for name, data in self.new.items():
            if name.endswith('.obj'):
                self.assertEqual(data, self.old[name])
            if name.endswith('/animation.json'):
                before = json.loads(self.old[name]); after = json.loads(data)
                for part in before['parts']:
                    part.pop('native_mesh')
                self.assertEqual(before, after)
                for part in after['parts']:
                    self.assertEqual(len(part['frames']), 65)
                    self.assertEqual(part['obj'], PurePosixPath(part['obj']).name)
                    self.assertIn(str(PurePosixPath(name).parent/part['obj']), self.new)

    def test_each_folder_copied_alone_resolves_all_dependencies(self):
        with tempfile.TemporaryDirectory() as tmp:
            for row in self.manifest['objects']:
                # Deliberately flatten only ONE object's folder into an unrelated
                # directory: no assets root, other objects or parent files exist.
                target = Path(tmp)/row['id']; target.mkdir()
                prefix = row['folder']+'/'
                for name, data in self.new.items():
                    if name.startswith(prefix):
                        (target/PurePosixPath(name).name).write_bytes(data)
                for obj in target.glob('*.obj'):
                    for line in obj.read_text().splitlines():
                        if line.startswith('mtllib '):
                            mtl = target/line.split(None, 1)[1]
                            self.assertTrue(mtl.is_file())
                            for entry in mtl.read_text().splitlines():
                                if entry.startswith('map_Kd '):
                                    png = target/entry.split(None, 1)[1]
                                    self.assertEqual(png.parent, target)
                                    self.assertTrue(png.is_file())

    def test_manifest_hashes_cover_every_file(self):
        self.assertEqual(set(self.manifest['files']), set(self.new)-{'MANIFEST.json'})
        for name, row in self.manifest['files'].items():
            self.assertEqual(len(self.new[name]), row['bytes'])
            self.assertEqual(hashlib.sha256(self.new[name]).hexdigest(), row['sha256'])
        self.assertFalse(self.manifest['standalone_mod'])
        self.assertFalse(self.manifest['minecraft_tested'])


if __name__ == '__main__':
    unittest.main()
