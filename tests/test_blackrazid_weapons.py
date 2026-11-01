import hashlib,json,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'tools'))
from import_blackrazid_weapons import catalog,AUTOMATICS,BASIC_GUNS,SPECIALS,identifier
A=ROOT/'src/main/resources/assets/metroport'
S=ROOT/'tools/blackrazid-reference'
class NativeWeaponTests(unittest.TestCase):
    def test_all_78_additional_original_files_are_hashed(self):
        files=json.loads((S/'weapons-source-files.json').read_text())['files']
        self.assertEqual(78,len(files));self.assertEqual(78,len({f['path'] for f in files}));self.assertEqual(78,len({f['driveId'] for f in files}))
        for f in files:
            b=(S/f['path']).read_bytes();self.assertEqual(len(b),f['bytes']);self.assertEqual(hashlib.sha256(b).hexdigest(),f['sha256'])
    def test_catalog_reproduced_from_actual_round_and_magazine_classes(self):
        entries=catalog();self.assertEqual(entries,json.loads((A/'blackrazid_weapons.json').read_text()))
        self.assertEqual(9,sum(e['kind']=='round' for e in entries));self.assertEqual(20,sum(e['kind']=='magazine' for e in entries));self.assertEqual(22,sum(e['kind']=='gun' for e in entries))
        self.assertEqual(set(BASIC_GUNS),{e['sourceId'] for e in entries if e['kind']=='gun'})
        self.assertTrue(all(e['id'].startswith('blackrazid_') for e in entries))
        for e in entries:
            self.assertEqual((A/('textures/blackrazid/icons/'+e['icon']+'.png')).read_bytes(),(S/('assets/icons/'+e['icon']+'.png')).read_bytes())
            self.assertTrue((A/('models/item/'+e['id']+'.json')).exists())
    def test_native_capacity_not_constructor_gun_argument_and_fuel_is_not_a_bullet_magazine(self):
        e={e['sourceId']:e for e in catalog()}
        self.assertEqual([30,40,100,150,8,15],[e[n]['capacity'] for n in ['AKAmmo','RpkAmmo','RpkAmmoBig','GatlingAmmo','HelsingAmmo','TiharAmmo']])
        self.assertEqual(200,e['OgnemetAmmo']['capacity']);self.assertEqual(5,e['OgnemetAmmo']['sourceCapacityArgument']);self.assertFalse(e['OgnemetAmmo']['refillable'])
        self.assertEqual(200,e['OgnemetAmmo']['defaultRounds'])
    def test_all_six_basic_guns_use_native_magazines_not_mcmod(self):
        e={e['sourceId']:e for e in catalog()};byid={v['id']:v for v in e.values()}
        for name in AUTOMATICS:
            gun=e[name];self.assertIn('pending',gun['runtimeStatus']);self.assertGreater(gun['loadingTicks'],0)
            self.assertTrue(all(byid[m]['kind']=='magazine' for m in gun['magazines']))
        self.assertEqual([identifier('RpkAmmo'),identifier('AKAmmo')],e['AK']['magazines'])
        self.assertEqual([identifier('RpkAmmoBig'),identifier('RpkAmmo')],e['Rpk']['magazines'])
        self.assertEqual(10,e['Ubludok']['speed']);self.assertEqual(4,e['AKSU']['speed']);self.assertEqual(2,e['Vsv']['delay'])
    def test_generator_and_other_importers_preserve_weapons_sounds_and_locales(self):
        paths=[A/'blackrazid_weapons.json',A/'sounds.json',A/'lang/ru_ru.lang',A/'lang/en_us.lang']
        before=[p.read_bytes() for p in paths]
        subprocess.run([sys.executable,'tools/import_blackrazid_weapons.py'],cwd=ROOT,check=True,capture_output=True)
        self.assertEqual(before,[p.read_bytes() for p in paths])
        for f in json.loads((S/'weapons-source-files.json').read_text())['files']:
            if f['path'].startswith('assets/sounds/'):
                self.assertEqual((S/f['path']).read_bytes(),(A/('sounds/blackrazid_'+Path(f['path']).name)).read_bytes())
    def test_no_shared_ammo_cooldown_or_modification_state_in_native_gun(self):
        text=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/weapons/BlackrazidGunItem.java').read_text()
        self.assertNotRegex(text,r'static\s+int\s+\w+\s*[;=]');self.assertNotIn('AttachmentItem.multiplier',text)
        self.assertIn('BlackrazidMagazine',text);self.assertIn('getHeldItemMainhand()==gun',text)
        self.assertIn('if(!fired)return false',text);self.assertIn('if(p.world.spawnEntity(bullet))fired=true',text)
        self.assertIn('insertMagazine(gun,p)',text);self.assertIn('ejectMagazine(gun,p)',text)

    def test_special_native_asset_hashes_and_no_fabricated_missing_shoot_sound(self):
        files=json.loads((S/'weapons-special-source-files.json').read_text())['files']
        self.assertEqual(59,len(files));self.assertEqual(59,len({f['driveId'] for f in files}))
        for f in files:
            b=(S/f['path']).read_bytes();self.assertEqual(f['bytes'],len(b));self.assertEqual(f['sha256'],hashlib.sha256(b).hexdigest())
        self.assertFalse((S/'assets/sounds/vyhlop_shoot.ogg').exists())
        e={e['sourceId']:e for e in catalog()};self.assertIn('fallback',e['Vyhlop']['soundAdaptation'])
    def test_distinct_unmodified_special_gun_profiles(self):
        e={e['sourceId']:e for e in catalog()}
        self.assertEqual([8,8,10,10,10,10],[e[n]['pellets'] for n in ['Ashot','Duplet','Saiga','Abzac','Uboinik','Velopal']])
        self.assertEqual([35,35,10],[e[n]['delay'] for n in ['Preved','Ventil','Vyhlop']])
        self.assertEqual([16,32,40,400,180],[e[n]['airCapacity'] for n in ['Helsing','Tihar','Railgun','Gatling','Ognemet']])
        self.assertEqual([2,4,5,100,45],[e[n]['airStep'] for n in ['Helsing','Tihar','Railgun','Gatling','Ognemet']])
        self.assertEqual(6,e['Uboinik']['looseMinimum']);self.assertEqual(110,e['Uboinik']['looseLoadingTicks'])
        self.assertEqual([identifier('VyhlopAmmo')],e['Velopal']['magazines'])
        self.assertTrue(e['Ventil']['keepChamber']);self.assertTrue(e['Ognemet']['flame'])
    def test_pump_server_route_has_no_client_supplied_pressure(self):
        route=(ROOT/'src/main/java/ru/metrocraft/port/mcmod/GunActionPacket.java').read_text()
        self.assertIn('getServerWorld().addScheduledTask',route);self.assertIn('startPump(gun,player)',route)
        self.assertNotIn('setInteger("BlackrazidPressure",msg.',route)
