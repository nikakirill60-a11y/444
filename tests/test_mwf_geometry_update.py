"""Regression checks against actual released packs; no Minecraft/parity claims."""
import base64
import json
import math
from pathlib import Path
import struct
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / 'downloads/Metro_Full_Arsenal_MWF_fix1_v6_mechanics.zip'
NEXT = ROOT / 'downloads/Metro_Full_Arsenal_MWF_fix1_v7_geometry.zip'
MODEL = 'assets/modularwarfare/gltf/guns/metro_revolver.gltf'


class GeometryUpdate(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        with zipfile.ZipFile(BASE) as archive:
            cls.before = {n: archive.read(n) for n in archive.namelist()}
        with zipfile.ZipFile(NEXT) as archive:
            cls.after = {n: archive.read(n) for n in archive.namelist()}

    def test_only_target_geometry_and_manifest_change(self):
        changed = {n for n in self.before if self.before[n] != self.after.get(n)}
        self.assertEqual(changed, {MODEL, 'guns/render/metro_ak2012.render.json', 'FULL_PACK_MANIFEST.json'})
        self.assertEqual(set(self.after) - set(self.before), {'GEOMETRY_UPDATE_MANIFEST.json'})
        for prefix in ('ammo/', 'bullets/', 'attachments/'):
            for name in self.before:
                if name.startswith(prefix):
                    self.assertEqual(self.before[name], self.after[name], name)

    def test_ak2012_native_scale_not_guessed_pose(self):
        path = 'guns/render/metro_ak2012.render.json'
        before, after = (json.loads(data[path]) for data in (self.before, self.after))
        binding = after['attachment']['metro_ak2012_ammo']
        self.assertEqual(binding.pop('scale'), {'x': .1, 'y': .1, 'z': .107})
        self.assertEqual(before, after)
        source = (ROOT / 'tools/blackrazid-reference/java/mods/provoda/guns/renderers/Render_AK2012.java').read_text()
        self.assertIn('this.mag.render()', source)
        self.assertIn('glScaled((double)0.1f, (double)0.1f, (double)0.1f)', source)
        self.assertIn('glScaled((double)1.0, (double)1.0, (double)1.07f)', source)

    def test_cylinder_does_not_move_hands_or_animation_or_rounds(self):
        before, after = (json.loads(data[MODEL]) for data in (self.before, self.after))
        cylinder = after['nodes'][-1]
        self.assertEqual(cylinder['name'], 'revolverCylinderModel')
        self.assertIn(len(after['nodes']) - 1, after['nodes'][0]['children'])
        self.assertEqual(before['animations'], after['animations'])
        self.assertEqual(before['accessors'], after['accessors'][:len(before['accessors'])])
        self.assertEqual(before['buffers'], after['buffers'][:len(before['buffers'])])
        for i, node in enumerate(before['nodes']):
            if i:
                self.assertEqual(node, after['nodes'][i], node['name'])
        self.assertEqual(len([n for n in after['nodes'] if n['name'].startswith('bulletModel_')]), 6)
        gun = next(n for n in after['nodes'] if n['name'] == 'gunModel')
        for key in ('translation', 'scale'):
            self.assertEqual(cylinder[key], gun[key])

    def test_cylinder_buffer_bounds_and_existing_atlas(self):
        doc = json.loads(self.after[MODEL])
        node = doc['nodes'][-1]
        count = 0
        for primitive in doc['meshes'][node['mesh']]['primitives']:
            self.assertEqual(primitive['material'], 0)
            for key, index in primitive['attributes'].items():
                accessor = doc['accessors'][index]
                view = doc['bufferViews'][accessor['bufferView']]
                data = base64.b64decode(doc['buffers'][view['buffer']]['uri'].split(',')[1])
                size = {'VEC2': 2, 'VEC3': 3}[accessor['type']]
                self.assertLessEqual(view.get('byteOffset', 0) + view['byteLength'], len(data))
                values = struct.unpack_from('<' + 'f' * accessor['count'] * size, data, view.get('byteOffset', 0))
                self.assertTrue(all(math.isfinite(v) for v in values))
                if key == 'TEXCOORD_0':
                    self.assertTrue(all(0 <= v <= 1 for v in values))
                if key == 'POSITION':
                    count += accessor['count']
        self.assertGreater(count, 100)
        report = json.loads(self.after['GEOMETRY_UPDATE_MANIFEST.json'])
        self.assertFalse(report['minecraftVerified'])
        self.assertEqual(count, report['fixes']['revolverCylinder']['triangles'] * 3)

    def test_integrated_mechanics_and_all_23_guns_preserved(self):
        before, after = (json.loads(data['FULL_PACK_MANIFEST.json']) for data in (self.before, self.after))
        for key in ('implementedMechanics', 'integratedMechanicsVersion', 'mechanicsRequiresJarSha256', 'mechanicsAudio'):
            self.assertEqual(before[key], after[key])
        guns = [n for n in self.after if n.startswith('guns/') and n.count('/') == 1]
        self.assertEqual(len(guns), 23)
        for name in guns:
            self.assertEqual(self.before[name], self.after[name])


if __name__ == '__main__':
    unittest.main()
