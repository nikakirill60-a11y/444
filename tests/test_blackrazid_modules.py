import hashlib,json,re,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT/'tools'))
from import_blackrazid_modules import catalog,fittings
from import_blackrazid_weapons import BASIC_GUNS
S=ROOT/'tools/blackrazid-reference';A=ROOT/'src/main/resources/assets/metroport'
class NativeModuleTests(unittest.TestCase):
    def test_all_67_native_files_have_unique_ids_paths_and_verified_hashes(self):
        rows=json.loads((S/'modules-source-files.json').read_text())['files'];self.assertEqual(67,len(rows))
        self.assertEqual(len(rows),len({r['path'] for r in rows}));self.assertEqual(len(rows),len({r['driveId'] for r in rows}))
        for r in rows:
            b=(S/r['path']).read_bytes();self.assertEqual(r['bytes'],len(b));self.assertEqual(r['sha256'],hashlib.sha256(b).hexdigest())
    def test_all_28_are_native_items_with_native_icons_and_bilingual_names(self):
        rows=catalog();self.assertEqual(28,len(rows));self.assertEqual(rows,json.loads((A/'blackrazid_modules.json').read_text()))
        for r in rows:
            self.assertEqual((S/('assets/icons/'+r['icon']+'.png')).read_bytes(),(A/('textures/blackrazid/icons/'+r['icon']+'.png')).read_bytes())
            self.assertTrue((A/('models/item/'+r['id']+'.json')).exists())
            for locale in ['ru_ru','en_us']:self.assertEqual(1,(A/('lang/'+locale+'.lang')).read_text().count('item.metroport.'+r['id']+'.name='))
    def test_original_five_socket_indices_and_specialization_are_complete(self):
        rows=fittings();self.assertEqual(set(BASIC_GUNS),set(rows));self.assertEqual(rows,json.loads((A/'blackrazid_module_fittings.json').read_text()))
        self.assertTrue(all(len(slots)==5 for slots in rows.values()))
        self.assertEqual({r['sourceId'] for r in catalog()},{name for slots in rows.values() for slot in slots for name in slot})
        self.assertEqual(['Laser'],rows['Duplet'][0]);self.assertEqual(['GatlingBarrel'],rows['Gatling'][0]);self.assertEqual(['PodonokGas'],rows['Podonok'][4]);self.assertEqual([],rows['AKSU'][2]);self.assertEqual([[]]*5,rows['Velopal'])
    def test_all_specializations_remain_inside_the_original_category_whitelists(self):
        gui=(S/'java/mods/provoda/gui/modifi/ModifiContainer.java').read_text()
        categories={}
        for category,path in [('OpticaSlot','gui/guns/railgun'),('BarrelSlot','gui/guns/revolver'),('ButtSlot','gui/guns/revolver'),('GlushakSlot','gui/modifi'),('ModifiSlot','gui/modifi'),('LaserSlot','gui/modifi')]:
            text=(S/('java/mods/provoda/'+path+'/'+category+'.java')).read_text();categories[category]=set(re.findall(r'instanceof (\w+)',text))
        blocks=re.split(r'(?:if|else if) \(var1.bK.h\(\).b\(\) == ModBase\.(\w+)\)',gui)
        found={}
        for i in range(1,len(blocks),2):
            found[blocks[i]]={int(offset or 0):kind for kind,offset in re.findall(r'new (\w+Slot)\(this.inventory, j(?: \+ (\d+))?,',blocks[i+1])}
        for gun,slots in fittings().items():
            for i,parts in enumerate(slots):
                if parts:self.assertTrue(set(parts)<=categories[found[gun][i]],(gun,i,parts))
    def test_importer_is_idempotent_and_preserves_other_sounds_and_languages(self):
        paths=[A/'blackrazid_modules.json',A/'blackrazid_module_fittings.json',A/'sounds.json',A/'lang/ru_ru.lang',A/'lang/en_us.lang']
        before=[p.read_bytes() for p in paths];subprocess.run([sys.executable,'tools/import_blackrazid_modules.py'],cwd=ROOT,check=True,capture_output=True);self.assertEqual(before,[p.read_bytes() for p in paths])
        for name in ['abzac_fastshoot','saiga_closeshoot']:self.assertEqual((S/('assets/sounds/'+name+'.ogg')).read_bytes(),(A/('sounds/blackrazid_'+name+'.ogg')).read_bytes())
    def test_packet_is_scheduled_window_bound_and_has_no_client_supplied_stats_or_nbt(self):
        packet=(ROOT/'src/main/java/ru/metrocraft/port/mcmod/GunActionPacket.java').read_text();self.assertIn('getServerWorld().addScheduledTask',packet);self.assertIn('player.openContainer.windowId!=msg.window',packet)
        code=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/weapons/BlackrazidModules.java').read_text();self.assertIn('p.getHeldItemMainhand()!=gun',code);self.assertIn('p.world.isRemote',code);self.assertNotIn('static ModifiInventory',code);self.assertNotIn('dropItem(',code)
    def test_rendering_is_client_only_and_optical_adaptation_does_not_set_game_settings(self):
        code=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/weapons/NativeLaser.java').read_text();self.assertNotIn('@Mod.EventBusSubscriber',code);self.assertIn('mc.player.rayTrace(32',code);self.assertNotIn('disableDepth',code)
        fov=(ROOT/'src/main/java/ru/metrocraft/port/ClientEvents.java').read_text();self.assertNotIn('BlackrazidModules.zoom(held)',fov);self.assertNotIn('fovSetting=',fov)
