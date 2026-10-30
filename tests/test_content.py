"""Resource/catalogue checks; these do not replace compiling or launching Minecraft."""
import collections
import hashlib
import importlib.util
import json
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/metroport'
ENTRIES = json.loads((ASSETS / 'content.json').read_text(encoding='utf-8'))


class ContentTests(unittest.TestCase):
    def test_original_catalogue_counts(self):
        self.assertEqual(collections.Counter(e['kind'] for e in ENTRIES),
                         {'gun': 16, 'armor': 36, 'ammo': 6, 'attachment': 5, 'part': 5, 'tool': 3})

    def test_unique_valid_registry_names(self):
        ids = [e['id'] for e in ENTRIES]
        self.assertEqual(len(ids), len(set(ids)))
        for key in ids:
            self.assertRegex(key, r'^[a-z0-9_]+$')

    def test_models_and_textures(self):
        for e in ENTRIES:
            model = json.loads((ASSETS / ('models/item/' + e['id'] + '.json')).read_text())
            self.assertEqual(model['textures']['layer0'], 'metroport:items/' + e['id'])
            texture = ASSETS / ('textures/items/' + e['id'] + '.png')
            self.assertTrue(texture.read_bytes().startswith(b'\x89PNG\r\n\x1a\n'))
        self.assertEqual(len(list((ASSETS / 'models/item').glob('*.json'))), 71)

    def test_gun_ammo_references_and_parameters(self):
        ammo = {e['id'] for e in ENTRIES if e['kind'] == 'ammo'}
        for e in ENTRIES:
            if e['kind'] != 'gun':
                continue
            self.assertIn(e['ammo'], ammo)
            for field in ('capacity', 'reloadTicks', 'delay', 'pellets', 'damage', 'durability'):
                self.assertGreater(e[field], 0, (e['id'], field))
            self.assertGreaterEqual(e['spread'], 0)
            self.assertIsInstance(e['automatic'], bool)

    def test_sounds_exist(self):
        sounds = json.loads((ASSETS / 'sounds.json').read_text())
        used = {e[f] for e in ENTRIES for f in ('shootSound', 'reloadSound') if f in e}
        self.assertEqual(set(sounds), used)
        for key, sound in sounds.items():
            self.assertRegex(key, r'^[a-z0-9_]+$')
            self.assertEqual(sound['sounds'], ['metroport:' + key])
            self.assertTrue((ASSETS / ('sounds/' + key + '.ogg')).read_bytes().startswith(b'OggS'))

    def test_armor_slots_and_defence(self):
        for e in ENTRIES:
            if e['kind'] == 'armor':
                self.assertIn(e['slot'], ('head', 'chest'))
                self.assertGreater(e['durability'], 0)
                self.assertGreaterEqual(e['defence'], 0)
                self.assertLessEqual(e['defence'], 1)

    def test_localization_and_three_tabs(self):
        for locale in ('ru_ru', 'en_us'):
            text = (ASSETS / ('lang/' + locale + '.lang')).read_text(encoding='utf-8')
            keys = [line.split('=', 1)[0] for line in text.splitlines() if '=' in line]
            self.assertEqual(len(keys), len(set(keys)))
            for e in ENTRIES:
                self.assertIn('item.metroport.' + e['id'] + '.name', keys)
            self.assertEqual(sum(k.startswith('itemGroup.') for k in keys), 3)
        russian = (ASSETS / 'lang/ru_ru.lang').read_text(encoding='utf-8')
        for name in ('Оружие', 'Броня', 'Вещи'):
            self.assertIn('=' + name + '\n', russian)

    def test_import_is_reproducible(self):
        def hashes():
            return {str(p.relative_to(ROOT)): hashlib.sha256(p.read_bytes()).hexdigest()
                    for base in (ROOT / 'src/main/resources', ROOT / 'docs')
                    for p in base.rglob('*') if p.is_file()}
        before = hashes()
        spec = importlib.util.spec_from_file_location('content_importer', ROOT / 'tools/import_content.py')
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        module.main()
        self.assertEqual(before, hashes())

    def test_json_metadata(self):
        info = json.loads((ROOT / 'src/main/resources/mcmod.info').read_text())
        self.assertEqual(info[0]['modid'], 'metroport')
        self.assertEqual(info[0]['mcversion'], '1.12.2')
        self.assertEqual(json.loads((ROOT / 'src/main/resources/pack.mcmeta').read_text())['pack']['pack_format'], 3)


if __name__ == '__main__':
    unittest.main()
