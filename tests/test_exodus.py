"""Checks against the actual supplied resource pack; not a Minecraft visual/gameplay test."""
import hashlib
import importlib.util
import json
import subprocess
import sys
import unittest
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/metroport'
ENTRIES = json.loads((ASSETS / 'exodus.json').read_text())
REPORT = json.loads((ROOT / 'docs/exodus-report.json').read_text())


class ExodusTests(unittest.TestCase):
    def test_catalogue_and_classification(self):
        self.assertEqual({k: sum(e['kind'] == k for e in ENTRIES) for k in ('mask', 'mutant', 'decor')},
                         {'mask': 3, 'mutant': 2, 'decor': 22})
        self.assertEqual(len({e['id'] for e in ENTRIES}), 27)
        self.assertEqual(sum(e['elements'] for e in ENTRIES), 1014)
        masks = {Path(e['source']).stem for e in ENTRIES if e['kind'] == 'mask'}
        mutants = {Path(e['source']).stem for e in ENTRIES if e['kind'] == 'mutant'}
        self.assertEqual(masks, {'gold_ore', 'diamond_ore', 'pumpkin'})
        self.assertEqual(mutants, {'lapis_block', 'diamond_block'})
        for e in ENTRIES:
            self.assertRegex(e['id'], r'^exodus_[a-z0-9_]+$')
            self.assertNotIn(Path(e['source']).stem, REPORT['excludedWeaponBlockOverrides'])

    def test_geometry_uvs_rotations_and_display_preserved(self):
        with zipfile.ZipFile(ROOT / 'ExodusRPV3.zip') as z:
            for e in ENTRIES:
                original = json.loads(z.read(e['source']))
                imported = json.loads((ASSETS / ('models/' + e['model'] + '.json')).read_text())
                expected = original['elements']
                for element in expected:
                    for face in element['faces'].values():
                        face.pop('cullface', None)
                self.assertEqual(imported['elements'], expected, e['id'])
                self.assertEqual(imported.get('display'), original.get('display'), e['id'])
                self.assertEqual(len(expected), e['elements'])
                self.assertEqual(json.loads((ASSETS / ('models/item/' + e['id'] + '.json')).read_text()),
                                 {'parent': 'metroport:' + e['model']})

    def test_textures_are_original_bytes_and_animation_is_preserved(self):
        with zipfile.ZipFile(ROOT / 'ExodusRPV3.zip') as z:
            self.assertEqual(REPORT['sha256'], hashlib.sha256((ROOT / 'ExodusRPV3.zip').read_bytes()).hexdigest())
            for target, source in REPORT['textures'].items():
                self.assertEqual((ASSETS / ('textures/' + target + '.png')).read_bytes(), z.read(source))
                if source + '.mcmeta' in z.namelist():
                    self.assertEqual((ASSETS / ('textures/' + target + '.png.mcmeta')).read_bytes(), z.read(source + '.mcmeta'))
            self.assertTrue((ASSETS / 'textures/exodus/blocks2/1straj2.png.mcmeta').is_file())

    def test_all_face_textures_resolve_with_documented_fallbacks(self):
        self.assertEqual(len(REPORT['missingTextureReplacements']), 25)
        self.assertIn('blocks2/mags', REPORT['missingTextureReplacements'])
        self.assertEqual(len(REPORT['undefinedFaceTextureReplacements']), 1)
        for e in ENTRIES:
            model = json.loads((ASSETS / ('models/' + e['model'] + '.json')).read_text())
            for value in model['textures'].values():
                if value.startswith('metroport:'):
                    self.assertTrue((ASSETS / ('textures/' + value.split(':', 1)[1] + '.png')).is_file(), value)
                else:
                    self.assertTrue(value.startswith('minecraft:blocks/'), value)
            for element in model['elements']:
                for face in element['faces'].values():
                    self.assertIn(face['texture'][1:], model['textures'], e['id'])
        self.assertFalse((ROOT / 'src/main/resources/assets/minecraft').exists())

    def test_decor_states_have_four_orientations_and_bounds(self):
        for e in ENTRIES:
            self.assertEqual(len(e['bounds']), 6)
            self.assertTrue(all(e['bounds'][i] < e['bounds'][i+3] for i in range(3)))
            if e['kind'] != 'decor':
                continue
            states = json.loads((ASSETS / ('blockstates/' + e['id'] + '.json')).read_text())['variants']
            self.assertEqual(set(states), {'facing=north', 'facing=east', 'facing=south', 'facing=west'})
            self.assertEqual({s['y'] for s in states.values()}, {0, 90, 180, 270})
            self.assertTrue(all(s['model'] == 'metroport:' + e['model'] for s in states.values()))

    def test_masks_use_real_armor_and_existing_filters(self):
        code = (ROOT / 'src/main/java/ru/metrocraft/port/exodus/ExodusMask.java').read_text()
        self.assertIn('extends MetroArmor', code)
        self.assertIn('d.slot="head"; d.gasMask=true;', code)
        registration = (ROOT / 'src/main/java/ru/metrocraft/port/exodus/ExodusContent.java').read_text()
        self.assertIn('new ExodusMask(entry.id).setCreativeTab(MetroPort.ARMOR)', registration)
        self.assertIn('new ItemBlock(BLOCKS.get(entry.id)).setCreativeTab(MetroPort.DECOR)', registration)
        client = (ROOT / 'src/main/java/ru/metrocraft/port/client/ExodusMaskModel.java').read_text()
        self.assertIn('bipedHead.postRender(scale)', client)
        self.assertIn('TransformType.HEAD', client)
        self.assertIn('npcPose(', client)

    def test_mutant_models_and_eggs_are_registered_without_natural_spawns(self):
        registration = (ROOT / 'src/main/java/ru/metrocraft/port/exodus/ExodusContent.java').read_text()
        for id_ in ('exodus_shrimp', 'exodus_guard'):
            self.assertIn('"' + id_ + '"', registration)
        self.assertNotIn('addSpawn(', registration)
        client = (ROOT / 'src/main/java/ru/metrocraft/port/client/ExodusClient.java').read_text()
        self.assertIn('registerItemVariants(Items.SPAWN_EGG', client)
        self.assertIn('add(ExodusMutant.Shrimp.class)', client)
        self.assertIn('add(ExodusMutant.Guard.class)', client)
        self.assertIn('renderItem(display, model)', client)
        # Don't call legacy Turbo geometry for resource-pack meshes.
        self.assertNotIn('LegacyGeometry', client)

    def test_localization(self):
        for locale, label in [('ru_ru', 'ru'), ('en_us', 'en')]:
            lines = dict(line.split('=', 1) for line in (ASSETS / ('lang/' + locale + '.lang')).read_text().splitlines() if '=' in line)
            for e in ENTRIES:
                kind = {'mask': 'item', 'mutant': 'entity', 'decor': 'tile'}[e['kind']]
                self.assertEqual(lines[kind + '.metroport.' + e['id'] + '.name'], e[label])

    def test_import_is_reproducible(self):
        def digest():
            return {str(p.relative_to(ROOT)): hashlib.sha256(p.read_bytes()).hexdigest()
                    for base in (ROOT/'src', ROOT/'docs') for p in base.rglob('*') if p.is_file()}
        before = digest()
        for tool in ('import_exodus.py', 'import_content.py'):
            subprocess.run([sys.executable, str(ROOT/'tools'/tool)], cwd=ROOT, check=True, capture_output=True)
        self.assertEqual(before, digest())


if __name__ == '__main__':
    unittest.main()
