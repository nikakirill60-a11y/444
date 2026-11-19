"""User-requested dev37 balance and regeneration contract; gameplay tests live in JUnit."""
import json,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'
class EquipmentDev37(unittest.TestCase):
    def test_exact_requested_weights_and_watch_rarity(self):
        b=json.loads((A/'inventory_balance.json').read_text())
        self.assertEqual(100,b['categories']['patch']['grams'])
        for id,grams in {'armorrem':3000,'remkomplekt':3000,'kanistra':6000}.items():
            self.assertEqual(grams,b['items']['metroport:'+id]['grams'])
        self.assertEqual('epic',b['items']['metroport:mcmod_watch']['rarity'])
        self.assertEqual('uncommon',b['categories']['watch']['rarity'])
    def test_balance_generator_is_byte_reproducible(self):
        p=A/'inventory_balance.json';before=p.read_bytes()
        subprocess.run([sys.executable,'tools/configure_inventory_grid.py'],cwd=ROOT,check=True,capture_output=True)
        self.assertEqual(before,p.read_bytes())
    def test_chest_keeps_original_container_and_does_not_open_a_new_server_window(self):
        s=(ROOT/'src/main/java/ru/metrocraft/port/client/MetroChestGui.java').read_text()
        self.assertIn('super(original.inventorySlots)',s)
        self.assertNotIn('sendToServer',s)
        self.assertNotIn('new ItemStack',s)
        self.assertIn('ItemRarity.of',s)
    def test_repair_kit_exception_does_not_reintroduce_firearms(self):
        from tools.create_reduced_scope import report
        r=report();self.assertNotIn('remkomplekt',r['removedIds'])
        self.assertIn('ak74',r['removedIds'])
        self.assertNotIn('assets/metroport/models/item/remkomplekt.json',r['jarExcludes'])
