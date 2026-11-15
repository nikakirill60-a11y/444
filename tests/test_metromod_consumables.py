"""Integration/provenance checks; native consumption behavior is exercised by JUnit."""
import json
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'

class MetroConsumablesTest(unittest.TestCase):
    def test_only_confirmed_profiles_no_duplicate_flask_or_medkit(self):
        rows=json.loads((A/'metromod_consumables.json').read_text())
        self.assertEqual(['konservi','galeti','gribtea','waterbottle'],[r['sourceId'] for r in rows])
        self.assertEqual([(3,.3,0),(2,.2,0),(3,.35,0),(0,0,30)],[(r['hunger'],r['saturation'],r['water']) for r in rows])
        self.assertEqual(4,len({r['id'] for r in rows}))
        proof=json.loads((ROOT/'reports/METRO_MOD/dev18_consumables_evidence.json').read_text())
        self.assertTrue({r['sourceId'] for r in rows}.issubset({r['item'] for r in proof['constructor_evidence']}))
    def test_original_icons_and_models_present(self):
        for row in json.loads((A/'metromod_consumables.json').read_text()):
            model=json.loads((A/'models/item'/f"{row['id']}.json").read_text())
            self.assertEqual('minecraft:item/generated',model['parent'])
            self.assertEqual(row['icon'],model['textures']['layer0'])
            self.assertEqual('original decoded METRO_MOD icon',row['visualStatus'])
            self.assertEqual(__import__('hashlib').sha256((A/'textures/metromod/items'/f"{row['sourceId']}.png").read_bytes()).hexdigest(),row['iconSha256'])
    def test_localization_once_and_generator_retains_it(self):
        for locale in ['ru_ru','en_us']:
            keys=[l.split('=',1)[0] for l in (A/'lang'/f'{locale}.lang').read_text().splitlines() if '=' in l]
            for row in json.loads((A/'metromod_consumables.json').read_text()):self.assertEqual(1,keys.count('item.metroport.'+row['id']+'.name'))
            self.assertEqual(1,keys.count('metroport.metromod.original_icon'))
        source=(ROOT/'tools/mcmod_languages.py').read_text();self.assertIn("'metromod_consumables.json'",source)
    def test_subscriber_registers_in_existing_things_and_model_registry(self):
        src=(ROOT/'src/main/java/ru/metrocraft/port/metromod/MetroConsumables.java').read_text()
        self.assertIn('@Mod.EventBusSubscriber',src);self.assertIn('MetroPort.THINGS',src);self.assertIn('MetroPort.ITEMS.put',src)
        self.assertIn('MetroPort.ITEMS.values()', (ROOT/'src/main/java/ru/metrocraft/port/ClientEvents.java').read_text())
