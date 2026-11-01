"""1.8 resource provenance and integration regressions; actual GUI/server smoke tests remain manual."""
import hashlib,json,re,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/metroport'
JAVA=ROOT/'src/main/java/ru/metrocraft/port'
class EquipmentAndDecorTests(unittest.TestCase):
    def test_all_fifty_nine_original_decorations_are_accounted_for(self):
        report=json.loads((ROOT/'docs/mcmod-decor-report.json').read_text())
        entries=json.loads((ASSETS/'mcmod_decor.json').read_text())
        self.assertEqual(59,len(entries));self.assertEqual(59,len(report['registeredDecor']));self.assertEqual([],report['pending'])
        self.assertFalse(set(report['pending'])&set(report['imported']))
        self.assertEqual(set(report['registeredDecor']),set(report['pending'])|set(report['imported']))
        for e in entries:self.assertEqual(e['scale'],report['registeredDecor'][e['sourceId']]['scale'])

    def test_decor_obj_and_png_are_byte_exact_registered_sources(self):
        manifest=json.loads((ROOT/'tools/mcmod-reference/source-files.json').read_text())['files']
        for e in json.loads((ASSETS/'mcmod_decor.json').read_text()):
            for ext,prefix in [('obj','models'),('png','textures')]:
                rel='assets/decor/'+e['sourceId']+'.'+ext
                original=(ROOT/'tools/mcmod-reference'/rel).read_bytes()
                self.assertEqual(original,(ASSETS/(prefix+'/mcmod/decor/'+e['sourceId']+'.'+ext)).read_bytes())
                self.assertEqual(hashlib.sha256(original).hexdigest(),e['sha256'][ext]);self.assertTrue(manifest[rel]['driveId'])

    def test_decor_import_is_reproducible_including_shared_locales_and_reports(self):
        paths=[ASSETS/'mcmod_decor.json',ROOT/'docs/mcmod-decor-report.json',ROOT/'tools/mcmod-reference/source-files.json']
        paths+=[ASSETS/('lang/'+loc+'.lang') for loc in ['ru_ru','en_us']]
        paths+=list((ASSETS/'models/block/mcmod_decor').glob('*.json'))
        paths+=list((ASSETS/'models/item').glob('mcmod_decor_*.json'))
        before={p:p.read_bytes() for p in paths}
        subprocess.run([sys.executable,str(ROOT/'tools/import_mcmod_decor.py')],cwd=ROOT,check=True,capture_output=True)
        self.assertEqual(before,{p:p.read_bytes() for p in paths})

    def test_orange_currency_changes_only_the_five_round_name(self):
        entries=json.loads((ASSETS/'mcmod_items.json').read_text())
        for e in entries:
            for field in ['nameRu','nameEn']:self.assertEqual(e['sourceId']=='money5',e[field].startswith('§6'))
        original=ROOT/'tools/mcmod-reference/assets/items/money5.png'
        self.assertEqual(original.read_bytes(),(ASSETS/'textures/mcmod/items/money5.png').read_bytes())

    def test_nvg_has_persistence_tracking_migration_and_live_slot_backing(self):
        nvg=(JAVA/'inventory/NvgEquipment.java').read_text()
        for marker in ['WeakIdentityMap','getCompoundTag(KEY).copy()','inventory.load(stack)','PlayerChangedDimensionEvent','PlayerRespawnEvent','PlayerLoggedInEvent','PlayerEvent.StartTracking','LivingDropsEvent','NoDrop.marked','keepInventory']:
            self.assertIn(marker,nvg)
        container=(JAVA/'inventory/MetroContainer.java').read_text()
        self.assertIn('new Slot(nvg,0,140,18)',container);self.assertIn('NvgEquipment.accepts(stack)',container)
        self.assertLess(container.index('for(int i=0;i<9;i++)'),container.index('new Slot(nvg,0,140,18)'))
        main=(JAVA/'MetroPort.java').read_text();self.assertIn('NvgSyncPacket.class,4,Side.CLIENT',main)
        client=(JAVA/'ClientEvents.java').read_text();self.assertIn('Keyboard.KEY_I',client);self.assertIn('new ru.metrocraft.port.client.NvgLayer',client)

    def test_suit_guards_cover_direct_use_containers_and_server_bypasses(self):
        rules=(JAVA/'inventory/EquipmentRules.java').read_text()
        for marker in ['sealedId','"himza"','"satanarmor"','"mcmod_officer_ozk"','PlayerContainerEvent.Open','PlayerInteractEvent.RightClickItem','TickEvent.PlayerTickEvent','NoDrop.retain','hasBindingCurse','original.onTake','original.isItemValid']:
            self.assertIn(marker,rules)
        armor=(JAVA/'MetroArmor.java').read_text()
        self.assertIn('EquipmentRules.allowed(player,armorType,stack)',armor)
        self.assertIn('definition.gasMask || ru.metrocraft.port.inventory.EquipmentRules.sealedId',armor)

    def test_runtime_annotation_build_and_artifact_use_the_same_release_version(self):
        version=re.search(r"^version = '([^']+)'",(ROOT/'build.gradle').read_text(),re.M).group(1)
        runtime=re.search(r'@Mod\(modid = MetroPort.ID, name = "MetroCraft Port", version = "([^"]+)"',(JAVA/'MetroPort.java').read_text()).group(1)
        self.assertEqual(version,runtime)
        self.assertIn('metrocraft-port-1.12.2-'+version+'.jar',(ROOT/'.github/workflows/build.yml').read_text())
