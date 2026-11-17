"""Real v7/v8 archives: resource integrity, preserved mechanics, explicit limitations."""
import base64
import io
import json
import math
import os
from pathlib import Path
import struct
import unittest
import zipfile

from PIL import Image, ImageChops

ROOT = Path(__file__).resolve().parents[1]
P = 'assets/modularwarfare/'


class Archive3DUpdate(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        def read(path):
            with zipfile.ZipFile(path) as z:
                return {n: z.read(n) for n in z.namelist()}
        cls.old = read(os.environ.get('MWF_V7', ROOT / 'downloads/Metro_Full_Arsenal_MWF_fix1_v7_geometry.zip'))
        cls.new = read(ROOT / 'downloads/Metro_Full_Arsenal_MWF_fix1_v8_3d.zip')
        cls.report = json.loads(cls.new['ARCHIVE_3D_UPDATE_MANIFEST.json'])

    def doc(self, path, old=False):
        return json.loads((self.old if old else self.new)[path])

    def test_existing_weapon_hand_poses_and_animation_buffers_unchanged(self):
        for path in self.old:
            if not path.startswith(P + 'gltf/guns/'):
                continue
            a, b = self.doc(path, True), self.doc(path)
            self.assertEqual(a['animations'], b['animations'], path)
            self.assertEqual(a['buffers'], b['buffers'][:len(a['buffers'])], path)
            for i, node in enumerate(a['nodes']):
                if i:
                    self.assertEqual(node, b['nodes'][i], (path, node['name']))
        for path in self.old:
            if not path.startswith('guns/render/'):
                continue
            a, b = self.doc(path, True), self.doc(path)
            for k in set(a) | set(b):
                if k not in ('attachment', 'defaultHidePart'):
                    self.assertEqual(a.get(k), b.get(k), (path, k))
        self.assertEqual(self.old['guns/metro_revolver.json'], self.new['guns/metro_revolver.json'])
        self.assertEqual(self.doc('guns/render/metro_ak2012.render.json')['attachment']['metro_ak2012_ammo']['scale'], {'x': .1, 'y': .1, 'z': .107})

    def test_runtime_mechanics_and_ammo_not_rebuilt(self):
        for path in self.old:
            if path.startswith(('ammo/', 'bullets/')) or path.endswith('.ogg'):
                self.assertEqual(self.old[path], self.new[path], path)
        a, b = self.doc('FULL_PACK_MANIFEST.json', True), self.doc('FULL_PACK_MANIFEST.json')
        for k in ('implementedMechanics', 'integratedMechanicsVersion', 'mechanicsRequiresJarSha256', 'mechanicsAudio'):
            self.assertEqual(a[k], b[k])
        self.assertEqual(b['counts']['guns'], 25)
        self.assertFalse(self.report['minecraftVerified'])

    def test_shared_items_and_no_retired_acceptance(self):
        retired = self.report['retiredAttachments']
        for path in self.new:
            self.assertNotIn(Path(path).stem.replace('.render', ''), retired, path)
        for path in [p for p in self.new if p.startswith('guns/') and p.count('/') == 1]:
            g = self.doc(path)
            for slot, ids in g['acceptedAttachments'].items():
                for id in ids:
                    self.assertNotIn(id, retired)
                    self.assertIn('attachments/' + id + '.json', self.new)
                    r = self.doc('guns/render/' + g['internalName'] + '.render.json')
                    self.assertIn(id, r['attachment'])
                    if id in ('metro_mod_laser', 'metro_mod_kolimator'):
                        self.assertNotIn('showPart', r['attachment'][id])
                        self.assertTrue(all(v > 0 for v in r['attachment'][id]['scale'].values()))
        g = self.doc('guns/metro_uboinik.json')
        self.assertEqual(g['acceptedAttachments']['barrel'], ['metro_mod_barrel', 'metro_mod_glushak'])
        self.assertEqual(self.new['obj/attachments/metro_mod_kolimator.obj'], self.old['obj/attachments/metro_uboinik_collimator.obj'])
        self.assertEqual(self.new[P + 'skins/attachments/metro_mod_kolimator.png'], self.old[P + 'skins/attachments/metro_uboinik_collimator.png'])

    def test_new_models_are_separate_items_with_intact_template_hands(self):
        for id, report in self.report['newWeapons'].items():
            g = self.doc('guns/' + id + '.json')
            self.assertEqual(g['internalName'], id)
            self.assertEqual(len(g['modelSkins']), 1)
            self.assertEqual(g['modelSkins'][0]['skinAsset'], id)
            d = self.doc(P + 'gltf/guns/' + id + '.gltf')
            a = self.doc(P + 'gltf/guns/' + report['template'] + '.gltf', True)
            self.assertEqual(a['animations'], d['animations'])
            for i, n in enumerate(a['nodes']):
                if 'Arm' in n['name']:
                    self.assertEqual(n, d['nodes'][i])
            self.assertEqual([n['name'] for n in d['nodes'] if 'mesh' in n and 'Arm' not in n['name']], ['gunModel'])
        mini = self.doc('guns/metro_ognemet_mini.json')
        self.assertEqual(mini['acceptedAmmo'], ['metro_ognemet_mini_ammo'])
        self.assertNotEqual(mini['acceptedAmmo'], self.doc('guns/metro_ognemet.json')['acceptedAmmo'])

    def test_all_gltf_buffers_finite_and_within_bounds(self):
        for id in ('metro_medved', 'metro_ognemet_mini', 'metro_ubludok'):
            d = self.doc(P + 'gltf/guns/' + id + '.gltf')
            for n in d['nodes']:
                if 'mesh' not in n:
                    continue
                for p in d['meshes'][n['mesh']]['primitives']:
                    for semantic, index in p['attributes'].items():
                        a = d['accessors'][index]; v = d['bufferViews'][a['bufferView']]
                        b = base64.b64decode(d['buffers'][v['buffer']]['uri'].split(',')[1])
                        self.assertLessEqual(v.get('byteOffset', 0) + v['byteLength'], len(b))
                        size = {'VEC2': 2, 'VEC3': 3, 'VEC4': 4}[a['type']]
                        values = struct.unpack_from('<' + 'f' * a['count'] * size, b, v.get('byteOffset', 0))
                        self.assertTrue(all(math.isfinite(x) for x in values))
                        if n['name'] in ('gunModel', 'ubludokRadiatorModel') and semantic == 'TEXCOORD_0':
                            self.assertTrue(all(0 <= x <= 1 for x in values), (id, n['name']))

    def test_atlas_changes_only_formerly_empty_tile_and_preserves_shells(self):
        path = P + 'skins/guns/metro_native_atlas.png'
        a, b = (Image.open(io.BytesIO(data[path])).convert('RGBA') for data in (self.old, self.new))
        x, y, w, h = self.report['radiator']['textureTile']
        self.assertIsNone(a.crop((x, y, x + w, y + h)).getbbox())
        b.paste(a.crop((x, y, x + w, y + h)), (x, y))
        self.assertIsNone(ImageChops.difference(a, b).convert('RGB').getbbox())
        path = P + 'skins/guns/metro_uboinik.png'
        a, b = (Image.open(io.BytesIO(data[path])).convert('RGBA') for data in (self.old, self.new))
        shells = a.crop((1024, 0, 2048, 1024)).resize((2048, 2048), Image.Resampling.NEAREST)
        self.assertIsNone(ImageChops.difference(shells, b.crop((2048, 0, 4096, 2048))).convert('RGB').getbbox())

    def test_all_resources_resolve(self):
        for kind in ('guns', 'ammo', 'attachments'):
            for path in [p for p in self.new if p.startswith(kind + '/') and p.count('/') == 1]:
                g = self.doc(path); id = g['internalName']; r = self.doc(kind + '/render/' + id + '.render.json')
                model = r['modelFileName']
                target = P + 'gltf/guns/' + model if kind == 'guns' else 'obj/' + kind + '/' + model
                self.assertIn(target, self.new, target)
                for s in g['modelSkins']:
                    self.assertIn(P + 'skins/' + kind + '/' + s['skinAsset'] + '.png', self.new)
                self.assertIn(P + 'textures/items/' + id + '.png', self.new)


if __name__ == '__main__':
    unittest.main()
