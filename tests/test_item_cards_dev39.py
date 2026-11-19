import json,unittest
from pathlib import Path
R=Path(__file__).resolve().parents[1]
A=R/'src/main/resources/assets/metroport'
class ItemCards39(unittest.TestCase):
    def test_exclusive_only_for_requested_pair_with_old_dimensions(self):
        items=json.loads((A/'inventory_balance.json').read_text())['items']
        self.assertEqual({'metroport:mcmod_zapal_helmet','metroport:mcmod_zapal_vest'},{k for k,v in items.items() if v['rarity']=='exclusive'})
        self.assertEqual((4,4),tuple(items['metroport:mcmod_zapal_vest'][k] for k in ['width','height']))
        self.assertEqual((2,2),tuple(items['metroport:mcmod_zapal_helmet'][k] for k in ['width','height']))
    def test_requested_rectangles_and_preserved_masses(self):
        items=json.loads((A/'inventory_balance.json').read_text())['items']
        for id in ['rem','armorrem','remkomplekt']:self.assertEqual((2,2),tuple(items['metroport:'+id][k] for k in ['width','height']))
        self.assertEqual((3,3),tuple(items['metroport:kanistra'][k] for k in ['width','height']))
        for id,weight in [('armorrem',3000),('remkomplekt',3000),('kanistra',6000)]:self.assertEqual(weight,items['metroport:'+id]['grams'])
    def test_card_information_is_drawn_without_hover_condition(self):
        source=(R/'src/main/java/ru/metrocraft/port/client/ItemCards.java').read_text()
        for field in ['data.name','data.id','data.weight','data.primary','data.secondary']:self.assertIn(field,source)
        self.assertNotIn('isHovered',source)
        self.assertIn('ItemCards.draw',(R/'src/main/java/ru/metrocraft/port/client/MetroInventoryGui.java').read_text())
    def test_metadata_and_namespace_agree(self):
        self.assertEqual('minetro',json.loads((R/'src/main/resources/mcmod.info').read_text())[0]['modid'])
        self.assertIn('String ID = "minetro"',(R/'src/main/java/ru/metrocraft/port/MetroPort.java').read_text())
