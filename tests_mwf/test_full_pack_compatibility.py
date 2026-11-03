"""Offline regression tests for the compatibility-only arsenal update. No Minecraft claim.

MWF_BASELINE_ZIP and MWF_COMPAT_ZIP can override the default published ZIP paths.
MWF_JAR enables an additional schema test against the exact selected closed build.
"""
import base64
import hashlib
import importlib.util
import json
import math
import os
from pathlib import Path
import sys
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))
from tools.patch_full_mwf_compatibility import ADDITIONS, BASE_SHA256, OUTPUT, bounds, obj_points, patch


class CompatibilityTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.base_path = Path(os.environ.get('MWF_BASELINE_ZIP', ROOT / 'downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip'))
        cls.output_path = Path(os.environ.get('MWF_COMPAT_ZIP', OUTPUT))
        assert hashlib.sha256(cls.base_path.read_bytes()).hexdigest() == BASE_SHA256
        with zipfile.ZipFile(cls.base_path) as z:
            cls.before = {n: z.read(n) for n in z.namelist()}
        with zipfile.ZipFile(cls.output_path) as z:
            assert z.testzip() is None
            assert len(z.namelist()) == len(set(z.namelist()))
            cls.after = {n: z.read(n) for n in z.namelist()}

    def js(self, name, before=False):
        return json.loads((self.before if before else self.after)[name])

    def test_release_counts_and_paths(self):
        expected = {'guns': 23, 'attachments': 31, 'ammo': 21, 'bullets': 9}
        for folder, count in expected.items():
            self.assertEqual(sum(n.startswith(folder + '/') and n.count('/') == 1 and n.endswith('.json')
                                 for n in self.after), count)
        self.assertEqual(len(self.after), len(self.before) + 1)
        self.assertTrue(set(self.before) <= set(self.after))
        self.assertFalse(any(n.startswith('/') or '..' in Path(n).parts or n.endswith(('.jar', '.class')) for n in self.after))

    def test_exact_build_and_idempotence(self):
        self.assertEqual(patch(self.before), self.after)
        self.assertEqual(patch(self.after), self.after)

    def test_only_allowlisted_entries_changed(self):
        allowed = {'FULL_PACK_MANIFEST.json', 'README_FULL_PACK_RU.txt', 'README_COMPATIBILITY_V2_RU.txt'}
        for gun in ADDITIONS:
            allowed.update((f'guns/{gun}.json', f'guns/render/{gun}.render.json'))
        changed = {n for n in self.after if self.after[n] != self.before.get(n)}
        self.assertEqual(changed, allowed)

    def test_only_accepted_attachments_changed_in_gun_types(self):
        for gun, slots in ADDITIONS.items():
            old, new = self.js(f'guns/{gun}.json', True), self.js(f'guns/{gun}.json')
            old_slots, new_slots = old.pop('acceptedAttachments'), new.pop('acceptedAttachments')
            self.assertEqual(old, new, gun)  # ammo, capacity, damage, modes, sound, reload all unchanged
            expected = {slot: list(ids) for slot, ids in old_slots.items()}
            for slot, ids in slots.items():
                expected.setdefault(slot, []).extend(ids)
            self.assertEqual(new_slots, expected, gun)

    def test_existing_mounts_poses_hands_and_animations_unchanged(self):
        for gun, slots in ADDITIONS.items():
            old, new = self.js(f'guns/render/{gun}.render.json', True), self.js(f'guns/render/{gun}.render.json')
            old_maps, new_maps = old.pop('attachment'), new.pop('attachment')
            self.assertEqual(old, new, gun)
            additions = {a for ids in slots.values() for a in ids}
            self.assertEqual(set(new_maps), set(old_maps) | additions)
            for attachment, mount in old_maps.items():
                self.assertEqual(mount, new_maps[attachment], (gun, attachment))

    def test_all_twelve_connections_have_visible_obj_mounts(self):
        count = 0
        for gun, slots in ADDITIONS.items():
            for slot, ids in slots.items():
                for attachment in ids:
                    count += 1
                    a = self.js(f'attachments/{attachment}.json')
                    self.assertEqual(a['attachmentType'], slot)
                    self.assertFalse(a['sameTextureAsGun'])
                    self.assertIn(attachment, self.js(f'guns/{gun}.json')['acceptedAttachments'][slot])
                    m = self.js(f'guns/render/{gun}.render.json')['attachment'][attachment]
                    self.assertEqual(m['binding'], 'gunModel')
                    self.assertEqual(m['rotate'], dict(x=0, y=90, z=0))
                    self.assertNotIn('showPart', m)  # no nonexistent native mesh/hidden zero-scale placeholder
                    for key in ('translate', 'rotate', 'scale', 'sightAimPosOffset'):
                        self.assertEqual(set(m[key]), set('xyz'))
                        self.assertTrue(all(math.isfinite(v) for v in m[key].values()))
                    self.assertTrue(all(v > 0 for v in m['scale'].values()))
                    lo, hi = bounds(obj_points(self.after, attachment))
                    self.assertTrue(all(hi[i] > lo[i] for i in range(3)))
        self.assertEqual(count, 12)

    def test_muzzle_modules_overlap_the_actual_gun_muzzle(self):
        from tools.patch_full_mwf_compatibility import gun_points
        for gun, slots in ADDITIONS.items():
            for attachment in slots.get('barrel', []):
                lo, hi = bounds(obj_points(self.after, attachment))
                _, gun_hi = bounds(gun_points(self.after, gun))
                m = self.js(f'guns/render/{gun}.render.json')['attachment'][attachment]
                rear = lo[2] * m['scale']['x'] + m['translate']['x']
                front = hi[2] * m['scale']['x'] + m['translate']['x']
                self.assertAlmostEqual(rear, gun_hi[0] - .012, places=6)
                self.assertGreater(front, gun_hi[0] + .10)

    def test_all_references_and_texture_assets_resolve(self):
        for n in self.after:
            if not (n.startswith('guns/') and n.count('/') == 1 and n.endswith('.json')):
                continue
            g = self.js(n)
            r = self.js('guns/render/' + g['internalName'] + '.render.json')
            model = self.js('assets/modularwarfare/gltf/guns/' + r['modelFileName'])
            node_names = {node['name'] for node in model['nodes']}
            for slot, ids in g['acceptedAttachments'].items():
                self.assertEqual(len(ids), len(set(ids)))
                for attachment in ids:
                    a = self.js('attachments/' + attachment + '.json')
                    self.assertEqual(a['attachmentType'], slot)
                    m = r['attachment'][attachment]
                    self.assertIn(m.get('binding', 'gunModel'), node_names)
                    for part in m.get('showPart', []) + m.get('hidePart', []):
                        self.assertIn(part, node_names)
                    ar = self.js('attachments/render/' + attachment + '.render.json')
                    self.assertIn('obj/attachments/' + ar['modelFileName'], self.after)
                    for skin in a['modelSkins']:
                        self.assertIn('assets/modularwarfare/skins/attachments/' + skin['skinAsset'] + '.png', self.after)

    def test_all_models_uv_textures_and_sounds_byte_identical(self):
        for name, data in self.before.items():
            if name.startswith(('obj/', 'assets/', 'attachments/', 'ammo/', 'bullets/')):
                self.assertEqual(data, self.after[name], name)

    def test_duplet_two_rounds_and_cosmetic_extra_barrels(self):
        self.assertEqual(self.js('guns/metro_duplet.json')['internalAmmoStorage'], 2)
        self.assertEqual(self.before['attachments/metro_duplet_extra_barrels.json'],
                         self.after['attachments/metro_duplet_extra_barrels.json'])
        m = self.js('FULL_PACK_MANIFEST.json')
        self.assertFalse(m['companionMod'])
        self.assertFalse(m['specialMechanicsImplemented'])
        self.assertEqual(m['dupletCapacity'], 2)

    def test_manifest_truthful_and_resolved_omissions(self):
        m = self.js('FULL_PACK_MANIFEST.json')
        update = m['compatibilityUpdate']
        self.assertFalse(update['gameTested'])
        self.assertFalse(update['newModels'])
        self.assertFalse(update['newTextures'])
        self.assertFalse(update['opticEffectsChanged'])
        self.assertFalse(update['specialMechanicsChanged'])
        self.assertEqual(len(update['connections']), 12)
        self.assertEqual(update['baselineSha256'], BASE_SHA256)
        self.assertEqual({(s['gun'], s['module']) for s in update['resolvedOriginalOmissions']},
                         {('Saiga', 'Glushak'), ('Ventil', 'Optica')})
        self.assertEqual(len(m['omittedSockets']), len(self.js('FULL_PACK_MANIFEST.json', True)['omittedSockets']) - 2)

    @unittest.skipUnless(os.environ.get('MWF_JAR'), 'Set MWF_JAR for actual selected-JAR schema validation')
    def test_actual_selected_jar_schemas(self):
        # Reuse the repository's existing class-file parser; no Java/OpenGL runtime needed.
        from tools.verify_uboinik_pack import ClassSchema, TARGET
        jar_path = Path(os.environ['MWF_JAR'])
        self.assertEqual(hashlib.sha256(jar_path.read_bytes()).hexdigest(), TARGET)
        with zipfile.ZipFile(jar_path) as jar:
            memo = {}
            def fields(cls):
                if cls not in memo:
                    c = ClassSchema(jar.read(cls + '.class'))
                    inherited = fields(c.parent) if c.parent and c.parent != 'java/lang/Object' else {}
                    memo[cls] = {**inherited, **c.fields}
                return memo[cls]
            def check(cls, value):
                declared = fields(cls)
                self.assertTrue(set(value) <= set(declared), (cls, set(value) - set(declared)))
                for key, v in value.items():
                    typ = declared[key]
                    if isinstance(v, dict) and typ == 'Lorg/lwjgl/util/vector/Vector3f;':
                        # LWJGL comes from Minecraft, not the content-pack JAR.
                        self.assertTrue(set(v) <= set('xyz'))
                        self.assertTrue(all(isinstance(n, (int, float)) and math.isfinite(n) for n in v.values()))
                    elif isinstance(v, dict) and typ.startswith('Lcom/modularwarfare/'):
                        check(typ[1:-1], v)
            for gun, slots in ADDITIONS.items():
                check('com/modularwarfare/common/guns/GunType', self.js(f'guns/{gun}.json'))
                r = self.js(f'guns/render/{gun}.render.json')
                check('com/modularwarfare/client/fpp/enhanced/configs/GunEnhancedRenderConfig', r)
                for ids in slots.values():
                    for a in ids:
                        check('com/modularwarfare/client/fpp/enhanced/configs/GunEnhancedRenderConfig$Attachment', r['attachment'][a])
                        check('com/modularwarfare/common/guns/AttachmentType', self.js(f'attachments/{a}.json'))
                        check('com/modularwarfare/client/fpp/basic/configs/AttachmentRenderConfig', self.js(f'attachments/render/{a}.render.json'))


if __name__ == '__main__':
    unittest.main()
