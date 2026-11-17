"""Actual v10 pack/JAR regression: icons, Velopal, stock grip and optional radiator."""
import base64
import copy
import hashlib
import io
import json
import os
from pathlib import Path
import sys
import unittest
import zipfile

import numpy as np
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT/'tools'))
from update_mwf_v10 import accessor, world
P = 'assets/modularwarfare/'


def archive(path):
    with zipfile.ZipFile(path) as z:
        assert z.testzip() is None
        return {n: z.read(n) for n in z.namelist() if not n.endswith('/')}


class InventoryV10(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.a = archive(os.environ.get('MWF_V9', ROOT/'downloads/Metro_Full_Arsenal_MWF_fix1_v9_motion.zip'))
        cls.b = archive(ROOT/'downloads/Metro_Full_Arsenal_MWF_fix1_v10_icons.zip')
        cls.oldjar = archive(os.environ.get('MWF_METRO5', ROOT/'downloads/modularwarfare-shining-2024.2.4.6f-fix1-metro5.jar'))
        cls.jar = archive(ROOT/'downloads/modularwarfare-shining-2024.2.4.6f-fix1-metro6.jar')
        cls.source = archive(ROOT/'tools/mwf-3d/sources.zip')
        cls.guns = [n for n in cls.a if n.startswith('guns/') and n.count('/') == 1]

    def test_all_25_icons_are_new_transparent_textured_rasters(self):
        self.assertEqual(len(self.guns), 25)
        hashes = set()
        for path in self.guns:
            ident = json.loads(self.a[path])['internalName']; icon = P+'textures/items/'+ident+'.png'
            self.assertNotEqual(self.a[icon], self.b[icon], ident)
            image = Image.open(io.BytesIO(self.b[icon])); self.assertEqual(image.size, (256, 256)); self.assertEqual(image.mode, 'RGBA')
            self.assertEqual(image.getpixel((0, 0))[3], 0)
            pixels = np.asarray(image); self.assertGreater((pixels[:, :, 3] > 8).sum(), 200)
            self.assertGreater(len(np.unique(pixels.reshape(-1, 4), axis=0)), 100)
            hashes.add(hashlib.sha256(self.b[icon]).hexdigest())
            model = json.loads(self.b[P+'models/item/'+ident+'.json'])
            self.assertEqual(model['textures']['layer0'], 'modularwarfare:items/'+ident)
        self.assertEqual(len(hashes), 25)

    def test_velopal_is_six_shot_buckshot_without_vyhlop_mag(self):
        g = json.loads(self.b['guns/metro_velopal.json'])
        self.assertEqual(g['internalAmmoStorage'], 6)
        self.assertEqual(g['acceptedBullets'], ['metro_12g_buckshot'])
        self.assertEqual(g['defaultBullet'], 'metro_12g_buckshot')
        self.assertEqual(g['numBullets'], 10); self.assertEqual(g['weaponType'], 'shotgun')
        self.assertNotIn('acceptedAmmo', g); self.assertNotIn('defaultAmmo', g)
        r = json.loads(self.b['guns/render/metro_velopal.render.json'])
        self.assertNotIn('metro_vyhlop_ammo', r['attachment'])
        self.assertFalse(json.loads(self.b['bullets/metro_12g_buckshot.json'])['isSlug'])

    def test_velopal_uses_exact_rar_texture_and_uv_deatlas_preserves_everything_else(self):
        self.assertEqual(self.b[P+'skins/guns/metro_velopal.png'], self.source['wpn34_bigun.png'])
        self.assertEqual(json.loads(self.b['guns/metro_velopal.json'])['modelSkins'][0]['skinAsset'], 'metro_velopal')
        name = P+'gltf/guns/metro_velopal.gltf'; a, b = [json.loads(f[name]) for f in (self.a, self.b)]
        report = json.loads(self.b['INVENTORY_V10_UPDATE_MANIFEST.json'])['velopal']
        x, y, w, h = report['oldTile']; aw, ah = report['oldAtlasSize']
        for key in ('nodes', 'animations'): self.assertEqual(a[key], b[key])
        self.assertEqual(a['buffers'], b['buffers'][:len(a['buffers'])])
        for i, mesh in enumerate(a['meshes']):
            for j, primitive in enumerate(mesh['primitives']):
                other = b['meshes'][i]['primitives'][j]
                for attribute in ('POSITION', 'NORMAL'):
                    self.assertEqual(primitive['attributes'][attribute], other['attributes'][attribute])
                self.assertEqual(primitive['indices'], other['indices'])
                if i == a['nodes'][1]['mesh']:
                    before = accessor(a, primitive['attributes']['TEXCOORD_0']); after = accessor(b, other['attributes']['TEXCOORD_0'])
                    np.testing.assert_allclose(before[:, 0], (after[:, 0]*w+x)/aw, atol=1e-7)
                    np.testing.assert_allclose(before[:, 1], (after[:, 1]*h+y)/ah, atol=1e-7)
                    self.assertGreaterEqual(after.min(), -1e-5); self.assertLessEqual(after.max(), 1+1e-5)

    def test_medved_remains_buckshot_per_user_and_uboinik_texture_restored(self):
        for prefix in ('guns/', 'guns/render/', P+'gltf/guns/'):
            suffix = '.gltf' if 'gltf' in prefix else '.render.json' if '/render/' in prefix else '.json'
            path = prefix+'metro_medved'+suffix
            self.assertEqual(self.a[path], self.b[path])
        g = json.loads(self.b['guns/metro_medved.json'])
        self.assertEqual(g['acceptedBullets'], ['metro_12g_buckshot']); self.assertEqual(g['numBullets'], 8)
        v7 = archive(os.environ.get('MWF_V7', ROOT/'downloads/Metro_Full_Arsenal_MWF_fix1_v7_geometry.zip'))
        self.assertEqual(self.b[P+'skins/guns/metro_uboinik.png'], v7[P+'skins/guns/metro_uboinik.png'])

    def test_ashot_wrappers_leave_original_meshes_arms_animation_and_world_matrices_unchanged(self):
        path = P+'gltf/guns/metro_ashot.gltf'; a, b = [json.loads(f[path]) for f in (self.a, self.b)]
        for key in ('animations', 'buffers', 'bufferViews', 'accessors', 'meshes'): self.assertEqual(a[key], b[key])
        self.assertEqual(len(b['nodes']), len(a['nodes'])+2)
        for i, node in enumerate(a['nodes']):
            if i: self.assertEqual(node, b['nodes'][i])
            if 'mesh' in node: np.testing.assert_allclose(world(a, i), world(b, i), atol=1e-12)
        profiles = json.loads(self.b[P+'metro_motion.json']); old = json.loads(self.a[P+'metro_motion.json'])
        self.assertEqual(len(profiles), 7)
        for name in old: self.assertEqual(profiles[name], old[name])
        for side in ('left', 'right'):
            name = 'metroMotionAshot'+side.capitalize(); n = next(n for n in b['nodes'] if n['name'] == name)
            self.assertEqual(profiles[name], n['extras']['metroMotion'])
            self.assertEqual(profiles[name]['attachment'], 'metro_mod_ashot_butt')
            self.assertEqual({b['nodes'][i]['name'] for i in n['children']}, {side+'ArmModel', side+'ArmSlimModel'})
            self.assertFalse(np.allclose(np.array(profiles[name]['matrix']).reshape(4, 4).T, np.eye(4)))

    def test_radiator_is_optional_barrel_attachment_not_baked_default(self):
        ident = 'metro_mod_ubludok_radiator'; g = json.loads(self.b['guns/metro_ubludok.json'])
        self.assertIn(ident, g['acceptedAttachments']['barrel']); self.assertNotIn(ident, g.get('defaultAttachments', {}).values())
        r = json.loads(self.b['guns/render/metro_ubludok.render.json'])
        self.assertIn('ubludokRadiatorModel', r['defaultHidePart'])
        self.assertEqual(r['attachment'][ident]['showPart'], ['ubludokRadiatorModel'])
        a = json.loads(self.b['attachments/'+ident+'.json']); self.assertEqual(a['attachmentType'], 'barrel')
        self.assertFalse(a['barrel']['isSuppressor']); self.assertFalse(a['barrel']['hideFlash'])
        for path in ['obj/attachments/'+ident+'.obj', P+'skins/attachments/'+ident+'.png', P+'textures/items/'+ident+'.png']:
            self.assertIn(path, self.b)
        self.assertEqual(self.a[P+'gltf/guns/metro_ubludok.gltf'], self.b[P+'gltf/guns/metro_ubludok.gltf'])

    def test_no_unrelated_resources_guns_or_runtime_classes_changed(self):
        allowed = {P+'textures/items/'+json.loads(self.a[path])['internalName']+'.png' for path in self.guns}
        allowed |= {'guns/metro_velopal.json', 'guns/metro_ubludok.json', 'guns/render/metro_velopal.render.json',
                    'guns/render/metro_ubludok.render.json', P+'gltf/guns/metro_velopal.gltf', P+'gltf/guns/metro_ashot.gltf',
                    P+'skins/guns/metro_uboinik.png', P+'metro_motion.json', 'MOTION_UPDATE_MANIFEST.json', 'FULL_PACK_MANIFEST.json'}
        allowed |= {n for n in self.a if n.startswith(P+'lang/') and n.endswith('.lang')}
        self.assertEqual({n for n in self.a if self.a[n] != self.b.get(n)}, allowed)
        self.assertEqual(set(self.jar), set(self.oldjar))
        self.assertEqual({n for n in self.jar if self.jar[n] != self.oldjar[n]},
                         {'ru/metrocraft/mwf/MotionClient.class', 'com/modularwarfare/ModularWarfare.class', P+'metro_motion.json'})
        self.assertEqual(int.from_bytes(self.jar['ru/metrocraft/mwf/MotionClient.class'][6:8], 'big'), 52)
        self.assertIn(b'metro_ashot', self.jar['ru/metrocraft/mwf/MotionClient.class'])
        self.assertIn(b'metro6', self.jar['com/modularwarfare/ModularWarfare.class'])
        self.assertEqual(self.jar[P+'metro_motion.json'], self.b[P+'metro_motion.json'])
        manifest = json.loads(self.b['FULL_PACK_MANIFEST.json'])
        jarsha = hashlib.sha256((ROOT/'downloads/modularwarfare-shining-2024.2.4.6f-fix1-metro6.jar').read_bytes()).hexdigest()
        self.assertEqual(manifest['mechanicsRequiresJarSha256'], jarsha)
        self.assertEqual(json.loads(self.b['MOTION_UPDATE_MANIFEST.json'])['requiresJarSha256'], jarsha)
        self.assertEqual(manifest['counts']['guns'], 25)


if __name__ == '__main__': unittest.main()
