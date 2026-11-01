import hashlib,json,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'tools'))
from audit_blackrazid_expansion import report,GUNS
A=ROOT/'src/main/resources/assets/metroport'
S=ROOT/'tools/blackrazid-reference'
class BlackrazidExpansionTests(unittest.TestCase):
    def test_verified_original_expansion_sources(self):
        entries=json.loads((S/'expansion-source-files.json').read_text())['files']
        self.assertEqual(len(entries),74)
        self.assertEqual(len({e['path'] for e in entries}),74)
        for e in entries:
            b=(S/e['path']).read_bytes();self.assertTrue(e['driveId']);self.assertEqual(len(b),e['bytes']);self.assertEqual(hashlib.sha256(b).hexdigest(),e['sha256'])
    def test_audit_is_reproducible_and_explicitly_incomplete(self):
        r=report();self.assertEqual(r,json.loads((ROOT/'docs/blackrazid-expansion-report.json').read_text()))
        self.assertIn('INCOMPLETE',r['status']);self.assertEqual(len(r['weapons']),22);self.assertEqual(len(r['mutants']),12)
        self.assertEqual({w['sourceId'] for w in r['weapons']},set(GUNS));self.assertEqual(22,sum(w['runtimeStatus'].startswith('basic-firearm') for w in r['weapons']));self.assertEqual(0,sum(w['runtimeStatus']=='pending' for w in r['weapons']))
        self.assertIn('CommandDefence',r['excluded'])
    def test_only_five_new_runtime_items_claimed(self):
        entries=json.loads((A/'blackrazid_survival.json').read_text());self.assertEqual(len(entries),5)
        self.assertEqual({e['sourceId'] for e in entries},{'Flyashka','Lamp','Power','Grenade','GrenadeBig'})
        for e in entries:
            self.assertEqual((A/('textures/blackrazid/icons/'+e['icon']+'.png')).read_bytes(),(S/('assets/icons/'+e['icon']+'.png')).read_bytes())
            self.assertTrue((A/('models/item/'+e['id']+'.json')).exists())
    def test_native_sounds_and_catalog_regenerate_without_changes(self):
        paths=[A/'sounds.json',A/'blackrazid_survival.json',A/'lang/ru_ru.lang',A/'lang/en_us.lang']
        before=[p.read_bytes() for p in paths]
        subprocess.run([sys.executable,'tools/import_blackrazid_survival.py'],cwd=ROOT,check=True,capture_output=True)
        self.assertEqual(before,[p.read_bytes() for p in paths])
        for name in ['flyashka','railgun_air']:self.assertEqual((A/('sounds/blackrazid_'+name+'.ogg')).read_bytes(),(S/('assets/sounds/'+name+'.ogg')).read_bytes())
    def test_general_handlers_never_copied_to_runtime(self):
        runtime=ROOT/'src/main/java'
        for p in runtime.rglob('*.java'):
            self.assertNotIn('import mods.provoda',p.read_text())
            self.assertNotIn(p.stem,['ExtendetPlayer','CommandDefence','AirSetup','PlayerTracker'])
    def test_server_cannot_accept_client_water_energy_or_light_positions(self):
        packet=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/survival/LampPacket.java').read_text()
        self.assertIn('getServerWorld().addScheduledTask',packet)
        self.assertIn('SurvivalSystem.hasLamp(p)',packet)
        self.assertIn('fromBytes(ByteBuf b){}',packet)
        entity=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/grenades/GrenadeEntity.java').read_text()
        self.assertNotRegex(entity,r'world\.(?:createExplosion|newExplosion)\(')
        self.assertIn('onExplosionStart',entity);self.assertIn('onExplosionDetonate',entity);self.assertIn('isPVPEnabled',entity)
    def test_locales_keep_existing_equipment_and_new_names_once(self):
        entries=json.loads((A/'blackrazid_equipment.json').read_text())+json.loads((A/'blackrazid_survival.json').read_text())
        for locale in ['ru_ru','en_us']:
            lines=(A/('lang/'+locale+'.lang')).read_text().splitlines()
            for e in entries:self.assertEqual(sum(l.startswith('item.metroport.'+e['id']+'.name=') for l in lines),1)
            self.assertEqual(sum(l.startswith('key.metroport.blackrazid_lamp=') for l in lines),1)
    def test_lamp_never_places_blocks_or_changes_nvg(self):
        client=(ROOT/'src/main/java/ru/metrocraft/port/client/BlackrazidSurvivalClient.java').read_text()
        self.assertNotRegex(client,r'\.(?:setBlockState|setBlockToAir|addPotionEffect)\(')
        self.assertIn('world.checkLightFor',client);self.assertIn('WorldEvent.Unload',client);self.assertIn('world.isBlockLoaded(p)',client)
