"""Balance/locale integration. Real container transactions are covered by JUnit, not these text checks."""
import json, unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'
class WeightInventory(unittest.TestCase):
    def test_installed_mwf_arsenal_has_explicit_weight_and_all_six_tiers(self):
        balance=json.loads((A/'inventory_balance.json').read_text())
        wiki=json.loads((ROOT/'website/wiki-data.json').read_text())
        guns={e['id'] for e in wiki['entries'] if e['category']=='weapons' and e['family']=='MWF'}
        self.assertEqual(25,len(guns));self.assertTrue(guns<=set(balance['items']))
        tiers={balance['items'][id]['rarity'] for id in guns}
        self.assertEqual({'common','uncommon','rare','epic','legendary','mythic'},tiers)
        for p in list(balance['items'].values())+list(balance['categories'].values()):
            self.assertGreater(p['grams'],0);self.assertGreaterEqual(p['capacityGrams'],0)
            self.assertIn(p['rarity'],tiers)
        self.assertEqual(2000,balance['categories']['armor']['capacityGrams'])
    def test_rarity_translations_survive_every_asset_regeneration(self):
        from tools.mcmod_languages import update
        files=[A/('lang/'+loc+'.lang') for loc in ['ru_ru','en_us']]
        before=[p.read_bytes() for p in files];update();self.assertEqual(before,[p.read_bytes() for p in files])
        ru=dict(line.split('=',1) for line in files[0].read_text().splitlines() if '=' in line)
        for key,name in zip(['common','uncommon','rare','epic','legendary','mythic'],['Обычный','Необычный','Редкий','Эпический','Легендарный','Мифический']):
            self.assertEqual(name,ru['metroport.rarity.'+key])
    def test_currency_keeps_its_requested_orange_name(self):
        source=(ROOT/'src/main/java/ru/metrocraft/port/client/InventoryTooltips.java').read_text()
        self.assertIn('!ItemMetrics.currency(stack)',source)
        items=json.loads((A/'mcmod_items.json').read_text())
        self.assertTrue(next(e for e in items if e['id']=='mcmod_money5')['nameRu'].startswith('§6'))
