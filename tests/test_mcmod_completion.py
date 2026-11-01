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
    def test_inventory_switch_is_server_authoritative_and_never_copies_inventory(self):
        modes=(JAVA/'inventory/InventoryModes.java').read_text()
        for marker in ['mode!=0 && mode!=1','emptyForSwitch(player)','player.openContainer!=player.inventoryContainer','MetroContainer','player.closeScreen()','PlayerLoggedInEvent','PlayerRespawnEvent','PlayerChangedDimensionEvent','PlayerEvent.Clone']:
            self.assertIn(marker,modes)
        for forbidden in ['writeToNBT','readFromNBT','clearMatchingItems','setInventorySlotContents']:self.assertNotIn(forbidden,modes)
        self.assertIn('addScheduledTask',(JAVA/'inventory/InventoryModePacket.java').read_text())
        for f in ['mechanics/CapacityRules.java','inventory/Backpacks.java','inventory/MetroContainer.java','client/MetroInventoryGui.java']:
            self.assertIn('InventoryModes.rows(player)',(JAVA/f).read_text())
    def test_inventory_source_is_vanilla_not_a_fabricated_original_container(self):
        events=(ROOT/'tools/mcmod-reference/java/metro/client/events/EventsClient.java').read_text()
        handler=(ROOT/'tools/mcmod-reference/java/metro/client/gui/GuiHandler.java').read_text()
        self.assertIn('guiUpgrade = event.gui instanceof GuiMods',events)
        self.assertNotIn('new Container',handler);self.assertIn('return null',handler)
        self.assertIn('InventoryModes.vanilla(mc.player)',(JAVA/'ClientEvents.java').read_text())
        self.assertIn('new GuiInventory(mc.player)',(JAVA/'client/InventoryComparison.java').read_text())
