"""Catalogue/balance integration; real geometry and slotClick operations are JUnit-tested."""
import importlib.util,json,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'
class MultiCellInventory(unittest.TestCase):
    def test_every_armor_entry_has_explicit_rarity_and_rectangle(self):
        wiki=json.loads((ROOT/'website/wiki-data.json').read_text());balance=json.loads((A/'inventory_balance.json').read_text())
        armor={e['id'] for e in wiki['entries'] if e['category']=='armor'}
        self.assertEqual(110,len(armor));self.assertTrue(armor<=balance['items'].keys())
        self.assertEqual({'common','uncommon','rare','epic','legendary','mythic'},{balance['items'][id]['rarity'] for id in armor})
        for profile in list(balance['items'].values())+list(balance['categories'].values()):
            self.assertTrue(1<=profile['width']<=8);self.assertTrue(1<=profile['height']<=8)
        for e in json.loads((A/'mcmod_armor.json').read_text()):
            if e['id'].endswith('_helmet'):
                vest=e['id'].removesuffix('_helmet')+'_vest'
                if 'metroport:'+vest in armor:self.assertEqual(balance['items']['metroport:'+e['id']]['rarity'],balance['items']['metroport:'+vest]['rarity'])
        for helmet,body in [('bazalthelmet','bazaltarmor'),('hardhelmet','heavy'),('heavy_helmet','heavy'),('blackrazid_nazi_hat','blackrazid_nazi'),('blackrazid_heavy_hat','blackrazid_heavy'),('blackrazid_heavy1_hat','blackrazid_heavy1')]:
            self.assertEqual(balance['items']['metroport:'+helmet]['rarity'],balance['items']['metroport:'+body]['rarity'])
        self.assertEqual((4,4),tuple(balance['items']['metroport:blackrazid_sparta'][k] for k in ['width','height']))
        self.assertEqual((3,2),tuple(balance['items']['modularwarfare:metro_revolver'][k] for k in ['width','height']))
    def test_balance_and_full_armor_report_regenerate_without_changes(self):
        spec=importlib.util.spec_from_file_location('grid_balance',ROOT/'tools/configure_inventory_grid.py');tool=importlib.util.module_from_spec(spec);spec.loader.exec_module(tool)
        files=[A/'inventory_balance.json',ROOT/'docs/ARMOR_RARITIES_DEV35_RU.md']
        before=[p.read_bytes() for p in files];self.assertEqual(110,len(tool.main()));self.assertEqual(before,[p.read_bytes() for p in files])
    def test_client_sends_coordinates_not_stack_copies_and_has_rotation(self):
        source=(ROOT/'src/main/java/ru/metrocraft/port/inventory/GridClickPacket.java').read_text()
        self.assertNotIn('readItemStack',source);self.assertNotIn('writeItemStack',source)
        gui=(ROOT/'src/main/java/ru/metrocraft/port/client/MetroInventoryGui.java').read_text()
        for key in ['KEY_R','grid.recordAt(cell)','new GridClickPacket','renderToolTip']:self.assertIn(key,gui)
    def test_capacity_and_weapon_rarity_do_not_change(self):
        balance=json.loads((A/'inventory_balance.json').read_text())
        self.assertEqual(2000,balance['categories']['armor']['capacityGrams'])
        self.assertEqual('mythic',balance['items']['modularwarfare:metro_railgun']['rarity'])
        for profile in balance['items'].values():self.assertGreater(profile['grams'],0)
