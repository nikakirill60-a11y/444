import hashlib,json,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];ASSETS=ROOT/'src/main/resources/assets/metroport';JAVA=ROOT/'src/main/java/ru/metrocraft/port'
class CompletionTests(unittest.TestCase):
    def test_only_registered_ak_and_original_ammunition_are_imported(self):
        gun,ammo=json.loads((ASSETS/'mcmod_guns.json').read_text())
        self.assertEqual('mcmod_ak74',gun['id']);self.assertEqual('gun',gun['kind'])
        self.assertEqual('mcmod_ammo5x45',ammo['id']);self.assertEqual(ammo['id'],gun['ammo'])
        self.assertEqual((30,60,2,1600,13),(gun['capacity'],gun['reloadTicks'],gun['delay'],gun['durability'],gun['damage']))
        report=json.loads((ROOT/'docs/mcmod-ak-report.json').read_text());self.assertEqual(['ak74'],report['importedGuns'])
    def test_ak_assets_are_original_bytes_and_generator_is_reproducible(self):
        import importlib.util
        spec=importlib.util.spec_from_file_location('ak',ROOT/'tools/import_mcmod_ak.py');mod=importlib.util.module_from_spec(spec);spec.loader.exec_module(mod)
        for name,(_,runtime) in mod.FILES.items():
            if runtime:self.assertEqual((mod.SOURCE/('assets/guns/ak74/'+name)).read_bytes(),(ASSETS/runtime).read_bytes())
        paths=[ASSETS/'mcmod_guns.json',ASSETS/'sounds.json',ROOT/'docs/mcmod-ak-report.json',ROOT/'tools/mcmod-reference/source-files.json',ASSETS/'lang/ru_ru.lang',ASSETS/'lang/en_us.lang']
        before={p:p.read_bytes() for p in paths};subprocess.run([sys.executable,str(ROOT/'tools/import_mcmod_ak.py')],cwd=ROOT,check=True,capture_output=True)
        self.assertEqual(before,{p:p.read_bytes() for p in paths})
    def test_standard_inventory_comparator_is_removed_without_duplicating_storage(self):
        for f in ['inventory/InventoryModes.java','inventory/InventoryModePacket.java','inventory/InventoryModeSyncPacket.java','client/InventoryComparison.java']:
            self.assertFalse((JAVA/f).exists())
        equipment=(JAVA/'inventory/OpenInventoryPacket.java').read_text()
        self.assertIn('InventoryCapacity.emptyForOpen(player)',equipment)
        self.assertIn('player.openContainer==player.inventoryContainer',equipment)
        for f in ['mechanics/CapacityRules.java','inventory/Backpacks.java','inventory/MetroContainer.java','client/MetroInventoryGui.java']:
            self.assertIn('InventoryCapacity.rows(player)',(JAVA/f).read_text())
        capacity=(JAVA/'inventory/InventoryCapacity.java').read_text()
        self.assertNotIn('getBoolean',capacity)
        self.assertNotIn('InventoryModePacket',(JAVA/'MetroPort.java').read_text())
    def test_survival_inventory_redirects_to_metro_and_creative_catalogue_is_retained(self):
        events=(JAVA/'ClientEvents.java').read_text()
        self.assertIn('GuiInventory',events)
        self.assertIn('!mc.player.capabilities.isCreativeMode',events)
        self.assertNotIn('InventoryModes',events)
        self.assertNotIn('new GuiInventory',events)
