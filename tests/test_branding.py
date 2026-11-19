"""MinETRO display branding and user-requested lowercase technical namespace."""
import json
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]


class BrandingTest(unittest.TestCase):
    def test_mod_list_and_forge_annotation_use_exact_requested_name(self):
        info = json.loads((ROOT/'src/main/resources/mcmod.info').read_text())[0]
        source = (ROOT/'src/main/java/ru/metrocraft/port/MetroPort.java').read_text()
        self.assertEqual('MinETRO', info['name'])
        self.assertRegex(source, r'@Mod\(modid = MetroPort.ID, name = "MinETRO"')
        self.assertEqual('minetro', info['modid'])
        self.assertIn('public static final String ID = "minetro";', source)
        self.assertIn('newSimpleChannel(ID)', source)

    def test_jar_manifest_and_resource_pack_branding(self):
        gradle = (ROOT/'build.gradle').read_text()
        self.assertIn("'Implementation-Title': 'MinETRO'", gradle)
        # Artifact paths remain stable for existing build/install tooling.
        self.assertIn("archivesBaseName = 'metrocraft-port-1.12.2'", gradle)
        pack = json.loads((ROOT/'src/main/resources/pack.mcmeta').read_text())
        self.assertIn('MinETRO', pack['pack']['description'])
        self.assertEqual(3, pack['pack']['pack_format'])

    def test_generated_language_labels_retain_branding(self):
        generated = json.loads((ROOT/'tools/mcmod-localization.json').read_text())
        for locale in ('ru_ru','en_us'):
            text = (ROOT/('src/main/resources/assets/metroport/lang/'+locale+'.lang')).read_text()
            self.assertEqual(['MinETRO'], re.findall(r'^key.categories.metroport=(.*)$',text,re.M))
            self.assertIn('MinETRO', re.search(r'^metroport.visual.title=(.*)$',text,re.M)[1])
        self.assertIn('MinETRO', str(generated))
        importer = (ROOT/'tools/import_content.py').read_text()
        self.assertNotIn('key.categories.metroport=MetroCraft', importer)
        self.assertEqual(2, importer.count('key.categories.metroport=MinETRO'))


if __name__ == '__main__':
    unittest.main()
