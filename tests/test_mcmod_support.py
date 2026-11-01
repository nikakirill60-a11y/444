"""Source/data/lifecycle contracts, not claims of a running Minecraft or visual acceptance."""
import hashlib,json,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/metroport';J=ROOT/'src/main/java/ru/metrocraft/port';S=ROOT/'tools/mcmod-reference/assets/support'
class SupportTests(unittest.TestCase):
    def test_six_ammo_items_preserve_the_original_icons_and_no_other_guns_are_added(self):
        entries=json.loads((A/'mcmod_support.json').read_text());ammo=[d for d in entries if d['kind']=='ammo']
        self.assertEqual(6,len(ammo));self.assertEqual({'x1270','x44','x762','x145','ball','ammo_bolts'},{d['texture'] for d in ammo})
        self.assertFalse(any(d['kind']=='gun' for d in entries))
        for d in ammo:self.assertEqual((S/(d['texture']+'.png')).read_bytes(),(A/('textures/mcmod/support/'+d['texture']+'.png')).read_bytes())
        self.assertEqual(4,len([d for d in entries if d['kind'] in ['collision','radiation','air','respawn']]))
    def test_all_new_names_use_matching_item_or_tile_localization(self):
        for locale,field in [('ru_ru','nameRu'),('en_us','nameEn')]:
            lines=(A/('lang/'+locale+'.lang')).read_text().splitlines()
            for d in json.loads((A/'mcmod_support.json').read_text()):
                prefix='item' if d['kind'] in ['ammo','watch'] else 'tile'
                self.assertIn(prefix+'.metroport.'+d['id']+'.name='+d[field],lines)
            self.assertTrue(any(l.startswith('metroport.watch.radiation=') for l in lines))
            self.assertFalse(any(l.startswith('metroport.inventory.mode=') for l in lines))
    def test_watch_faces_uv_normals_and_atlases_match_the_original_weapon_model(self):
        def data(path):
            attributes=[];groups={};group=None
            for line in path.read_text().splitlines():
                if line.startswith(('v ','vt ','vn ')):attributes.append(line)
                elif line.startswith(('g ','o ')):group=line.split()[1]
                elif line.startswith('f '):groups.setdefault(group,[]).append(line)
            return attributes,groups
        original,groups=data(A/'models/mcmod/guns/ak74.obj');watch,selected=data(A/'models/mcmod/watch.obj')
        self.assertEqual(original,watch);self.assertEqual({'clock','num1','num2','num3','num4'},set(selected))
        for key,faces in selected.items():self.assertEqual(groups[key],faces)
        for name in ['clock-cR.png']+[str(n)+'.png' for n in range(10)]:self.assertEqual((S/name).read_bytes(),(A/('textures/mcmod/support/'+name.lower())).read_bytes())
    def test_medkit_sound_is_original_and_only_inside_successful_server_completion(self):
        self.assertEqual((S/'medkit_use_heavy.ogg').read_bytes(),(A/'sounds/mcmod_medkit_use_heavy.ogg').read_bytes())
        source=(J/'mcmod/McmodMedkitItem.java').read_text();before,finish=source.split('public ItemStack onItemUseFinish',1)
        self.assertNotIn('playSound',before);self.assertEqual(1,finish.count('playSound'))
        self.assertIn('!world.isRemote',finish);self.assertIn('if(canUse(',finish);self.assertIn('mcmod_medkit_use_heavy',finish)
    def test_watch_slot_is_appended_without_reindexing_nvg_and_handles_persistence_death_and_tracking(self):
        container=(J/'inventory/MetroContainer.java').read_text()
        self.assertLess(container.index('new Slot(nvg'),container.index('new Slot(watch'))
        self.assertIn('slot.inventory!=watch',container)
        equipment=(J/'inventory/WatchEquipment.java').read_text()
        for marker in ['MetroEquippedWatch','PlayerLoggedInEvent','PlayerRespawnEvent','PlayerChangedDimensionEvent','StartTracking','PlayerEvent.Clone','LivingDropsEvent','keepInventory','NoDrop.marked(stack)','stack.getCount()!=1','sendToAllTracking']:
            self.assertIn(marker,equipment)
        self.assertIn('getMaxStackSize', (ROOT/'src/test/java/ru/metrocraft/port/mcmod/WatchTest.java').read_text())
        self.assertIn('hasBindingCurse',container)
    def test_radiation_sources_unload_and_watch_receives_only_server_readings(self):
        tile=(J/'radiation/ZoneTile.java').read_text()
        for event in ['onLoad','invalidate','onChunkUnload']:self.assertIn(event,tile)
        system=(J/'radiation/RadiationSystem.java').read_text()
        for marker in ['world.isRemote','Zone.AIR','combine(radiation,air)','player.ticksExisted%20','protectedBy(player)','WorldEvent.Unload']:self.assertIn(marker,system)
        client=(J/'client/WatchClient.java').read_text();self.assertIn('getDimension()!=dimension',client);self.assertIn('addScheduledTask',client)
        self.assertNotIn('RadiationSystem.level',client)
        self.assertIn('WatchSyncPacket.class,7,Side.CLIENT',(J/'MetroPort.java').read_text())
    def test_support_generator_is_reproducible(self):
        paths=list((A/'textures/mcmod/support').glob('*'))+[A/'mcmod_support.json',A/'sounds.json',A/'models/mcmod/watch.obj',A/'lang/ru_ru.lang',A/'lang/en_us.lang']
        before={p:hashlib.sha256(p.read_bytes()).hexdigest() for p in paths}
        subprocess.run([sys.executable,str(ROOT/'tools/import_mcmod_support.py')],cwd=ROOT,check=True,capture_output=True)
        self.assertEqual(before,{p:hashlib.sha256(p.read_bytes()).hexdigest() for p in paths})
    def test_non_revolver_foregrips_retract_below_previous_overhand_supports(self):
        poses=json.loads((A/'geometry/gun_poses.json').read_text())
        self.assertEqual([.12,.19,-.175],poses['revolver']['support'])
        self.assertEqual([.4,.19,-.175],poses['ak74']['support'])
        self.assertEqual([.13,.21,-.208],poses['duplet']['support'])
        self.assertIn('McmodAkPose.handAnchor(true)',(J/'client/McmodAkRenderer.java').read_text())
        self.assertIn('armMatrix(id,wrist',(J/'client/GunHands.java').read_text())
