"""Original MCMOD armor integrity/slot contracts, not an in-game visual acceptance test."""
import hashlib
import json
from pathlib import Path
import re
import struct
import subprocess
import sys
import unittest
import zlib

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/metroport'
SOURCE = ROOT / 'tools/mcmod-reference'
ENTRIES = json.loads((ASSETS / 'mcmod_armor.json').read_text())
REPORT = json.loads((ROOT / 'docs/mcmod-report.json').read_text())
WHOLE = {'officer_ozk', 'banditkurtka', 'kurtka', 'plash', 'ghillie'}


class McmodTests(unittest.TestCase):
    def test_inventory_of_originals_does_not_hide_remaining_content(self):
        self.assertEqual(33, len(REPORT['registeredOriginalArmor']))
        self.assertEqual([], REPORT['pendingArmor'])
        self.assertEqual(set(REPORT['registeredOriginalArmor']), set(REPORT['pendingArmor']) | set(REPORT['importedModels']))
        self.assertIn('sniper_camo', REPORT['importedModels'])
        self.assertEqual('Камуфлированная броня', REPORT['registeredOriginalArmor']['ghillie']['name'])
        self.assertTrue(REPORT['remainingContent'])

    def test_raw_source_checksums_and_class_headers(self):
        files = json.loads((SOURCE / 'source-files.json').read_text())['files']
        self.assertEqual(289, len(files))  # Includes held tools and original decoration registration/render references.
        for name, record in files.items():
            data = (SOURCE / name).read_bytes()
            self.assertEqual(record['bytes'], len(data), name)
            self.assertEqual(record['sha256'], hashlib.sha256(data).hexdigest(), name)
            if name.endswith('.class'):
                self.assertTrue(data.startswith(b'\xca\xfe\xba\xbe'), name)

    def test_bundled_obj_atlas_icons_are_byte_exact_originals(self):
        for key, report in REPORT['importedModels'].items():
            runtime = report['runtimeId']
            files = [('model.obj', SOURCE / ('assets/armor/' + key + '/model.obj'), ASSETS / ('models/mcmod/' + runtime + '/model.obj')),
                     ('texture.png', SOURCE / ('assets/armor/' + key + '/' + report['sourceTexture']), ASSETS / ('textures/mcmod/armor/' + runtime + '.png')),
                     ('icon.png', SOURCE / ('assets/icons/' + report['sourceIcon']), ASSETS / ('textures/mcmod/icons/' + runtime + '.png'))]
            for name, original, bundled in files:
                self.assertEqual(original.read_bytes(), bundled.read_bytes(), key + '/' + name)
                self.assertEqual(report['sha256'][name], hashlib.sha256(bundled.read_bytes()).hexdigest())

    def test_source_groups_uv_indices_normals_and_faces_are_valid(self):
        for key, report in REPORT['importedModels'].items():
            count = {'v': 0, 'vt': 0, 'vn': 0}
            group, groups, faces = None, set(), 0
            runtime = report['runtimeId']
            for line in (ASSETS / ('models/mcmod/' + runtime + '/model.obj')).read_text(encoding=report['encoding']).splitlines():
                p = line.split('#', 1)[0].split()
                if not p:
                    continue
                if p[0] in count:
                    count[p[0]] += 1
                elif p[0] in ('g', 'o'):
                    group = p[1]
                elif p[0] == 'f':
                    self.assertIsNotNone(group, key)
                    groups.add(group)
                    self.assertGreaterEqual(len(p), 4)
                    for vertex in p[1:]:
                        for name, value in zip(['v', 'vt', 'vn'], vertex.split('/')):
                            if value:
                                i = int(value)
                                self.assertNotEqual(0, i)
                                self.assertTrue(-count[name] <= i <= count[name], (key, vertex))
                    faces += 1
            self.assertEqual(report['groups'], sorted(groups))
            self.assertEqual(report['vertices'], count['v'])
            self.assertEqual(report['faces'], faces)
            self.assertTrue(groups <= {'head','body','leftarm','rightarm','leftleg','rightleg'})
            self.assertTrue(groups)

    def test_png_chunks_have_valid_crcs_and_decompress(self):
        for path in (ASSETS / 'textures/mcmod').rglob('*.png'):
            data = path.read_bytes()
            self.assertEqual(b'\x89PNG\r\n\x1a\n', data[:8])
            offset, compressed = 8, b''
            while offset < len(data):
                size = struct.unpack('>I', data[offset:offset+4])[0]
                chunk = data[offset+4:offset+8]
                payload = data[offset+8:offset+8+size]
                crc = struct.unpack('>I', data[offset+8+size:offset+12+size])[0]
                self.assertEqual(zlib.crc32(chunk + payload) & 0xffffffff, crc, str(path))
                if chunk == b'IDAT':
                    compressed += payload
                offset += size + 12
            self.assertTrue(zlib.decompress(compressed))

    def test_exactly_five_whole_outfits_and_bkz_split(self):
        self.assertEqual(58, len(ENTRIES))
        whole = [e for e in ENTRIES if e['mode'] == 'whole']
        self.assertEqual(WHOLE, {e['model'] for e in whole})
        self.assertEqual(WHOLE, {e['id'].removeprefix('mcmod_') for e in whole})
        bkz = [e for e in ENTRIES if e['model'] == 'bkz']
        self.assertEqual({'helmet', 'vest'}, {e['mode'] for e in bkz})
        self.assertAlmostEqual(.6, sum(e['defence'] for e in bkz))
        for e in ENTRIES:
            original = REPORT['registeredOriginalArmor'][e['sourceId']]
            self.assertEqual(original['condition'], e['condition'])
            self.assertEqual(original['protect'], e['sourceProtect'])
            self.assertEqual(original['type'], e['sourceType'])

    def test_icons_native_models_and_localizations_are_complete(self):
        for e in ENTRIES:
            model = json.loads((ASSETS / ('models/item/' + e['id'] + '.json')).read_text())
            self.assertEqual('item/generated', model['parent'])
            texture = model['textures']['layer0'].split(':', 1)[1]
            self.assertTrue((ASSETS / ('textures/' + texture + '.png')).is_file())
            for locale, key in [('ru_ru', 'nameRu'), ('en_us', 'nameEn')]:
                lines = (ASSETS / ('lang/' + locale + '.lang')).read_text().splitlines()
                target = 'item.metroport.' + e['id'] + '.name=' + e[key]
                self.assertEqual(1, lines.count(target))
        java = (ROOT / 'src/main/java/ru/metrocraft/port/mcmod/McmodContent.java').read_text()
        self.assertIn('setCreativeTab(MetroPort.ARMOR)', java)
        self.assertIn('setRegistryName(MetroPort.ID,entry.id)', java)

    def test_no_empty_items_and_each_source_group_is_assigned_once(self):
        for key, model in REPORT['importedModels'].items():
            entries = [e for e in ENTRIES if e['sourceId'] == key]
            self.assertTrue(entries, key)
            modes = {e['mode'] for e in entries}
            if key in WHOLE:
                self.assertEqual({'whole'}, modes)
            else:
                self.assertEqual('head' in model['groups'], 'helmet' in modes, key)
                self.assertEqual(bool(set(model['groups']) - {'head'}), 'vest' in modes, key)
            for group in model['groups']:
                assigned = [e for e in entries if e['mode'] == 'whole' or
                            (e['mode'] == 'helmet' if group == 'head' else e['mode'] == 'vest')]
                self.assertEqual(1, len(assigned), (key, group))
            expected = REPORT['registeredOriginalArmor'][key]['protect'] / 100
            self.assertAlmostEqual(expected, sum(e['defence'] for e in entries), msg=key)

    def test_original_batch_ids_slots_and_statistics_remain_compatible(self):
        previous = json.loads((ROOT / 'tests/fixtures/mcmod-batch1.json').read_text())
        current = {e['id']: e for e in ENTRIES}
        self.assertEqual(7, len(previous))
        for entry in previous:
            for key in ['id', 'model', 'mode', 'condition', 'sourceProtect', 'sourceType', 'defence', 'radiation']:
                self.assertEqual(entry[key], current[entry['id']][key], (entry['id'], key))
        for entry in ENTRIES:
            self.assertRegex(entry['id'], r'^mcmod_[a-z0-9_]+$')
            self.assertRegex(entry['model'], r'^[a-z0-9_]+$')

    def test_single_parts_special_atlases_and_original_radiation_flags(self):
        by_source = {key: [e for e in ENTRIES if e['sourceId'] == key] for key in REPORT['registeredOriginalArmor']}
        for key, mode in [('gasmask', 'helmet'), ('kitel', 'vest'), ('vatnik', 'vest')]:
            self.assertEqual([mode], [e['mode'] for e in by_source[key]])
        self.assertEqual('hanzaz.png', REPORT['importedModels']['hanza']['sourceIcon'])
        self.assertEqual('texp.png', REPORT['importedModels']['gasmask']['sourceTexture'])
        self.assertEqual('windows-1251', REPORT['importedModels']['gasmask']['encoding'])
        self.assertEqual('btbkz', REPORT['importedModels']['BTBKZ']['runtimeId'])
        self.assertNotEqual(REPORT['importedModels']['BTBKZ']['sha256']['texture.png'], REPORT['importedModels']['bkz']['sha256']['texture.png'])
        self.assertEqual(11, sum(e['radiation'] for e in ENTRIES))
        for entry in ENTRIES:
            if entry['radiation']:
                self.assertIn(entry['mode'], ['whole', 'helmet'])

    def test_user_verified_three_way_visual_correction_preserves_icons_and_other_models(self):
        corrections = {'officer_ozk': 'banditkurtka', 'banditkurtka': 'kurtka', 'kurtka': 'officer_ozk'}
        self.assertEqual(corrections, REPORT['wornModelOverrides'])
        for entry in ENTRIES:
            target = corrections.get(entry['model'], entry['model'])
            self.assertEqual(target, entry['visualModel'])
            self.assertTrue((ASSETS / ('models/mcmod/' + target + '/model.obj')).is_file())
            self.assertTrue((ASSETS / ('textures/mcmod/armor/' + target + '.png')).is_file())
            icon = json.loads((ASSETS / ('models/item/' + entry['id'] + '.json')).read_text())
            expected = 'mcmod/icons/' + entry['model'] if entry['mode']=='whole' else 'mcmod/armor_icons/' + entry['id']
            self.assertEqual('metroport:' + expected, icon['textures']['layer0'])
        renderer = (ROOT / 'src/main/java/ru/metrocraft/port/client/McmodArmorModel.java').read_text()
        self.assertIn('MESHES.computeIfAbsent(entry.geometryModel(),', renderer)
        self.assertIn('+entry.geometryModel()+".png"', renderer)

    def test_importer_is_reproducible(self):
        files = [ROOT / 'docs/mcmod-report.json', ASSETS / 'mcmod_armor.json']
        files += list((ASSETS / 'models/mcmod').rglob('*.*')) + list((ASSETS / 'textures/mcmod').rglob('*.png'))
        files += [ASSETS / ('lang/' + locale + '.lang') for locale in ['ru_ru', 'en_us']]
        before = {p: p.read_bytes() for p in files}
        subprocess.run([sys.executable, str(ROOT / 'tools/import_mcmod.py')], cwd=ROOT, check=True, capture_output=True)
        self.assertEqual(before, {p: p.read_bytes() for p in files})


if __name__ == '__main__':
    unittest.main()
