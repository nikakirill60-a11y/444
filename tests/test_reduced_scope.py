import fnmatch, gzip, hashlib, json, subprocess, sys, unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'tools'))
from create_reduced_scope import report
A=ROOT/'src/main/resources/assets/metroport'
S=ROOT/'tools/blackrazid-reference/gadgets'

class ReducedScopeTests(unittest.TestCase):
    def test_manifest_is_reproducible_and_keeps_explicit_exceptions(self):
        data=report();self.assertEqual(data,json.loads((A/'reduced_scope.json').read_text()))
        removed=set(data['removedIds']);self.assertEqual(220,len(removed))
        self.assertTrue({'exodus_gasmask_gold','exodus_gasmask_diamond','testpickaxe'}<=removed)
        self.assertFalse(set(data['retainedSpecialItems']) & removed)
        self.assertNotIn('exodus_gasmask_pumpkin',removed)
        self.assertNotIn('fivearmyrounds',removed)
    def test_distribution_removes_complex_models_but_retains_live_dependencies(self):
        patterns=report()['jarExcludes']
        def removed(path):return any(fnmatch.fnmatchcase('assets/metroport/'+path,p) for p in patterns)
        for path in ['models/mcmod/guns/test.obj.gz','models/mcmod/decor/test.obj.gz','models/blackrazid/weapons/test.obj.gz','models/blackrazid/rails/test.obj.gz','models/blackrazid/tunnels/test.obj.gz']:
            self.assertTrue(removed(path),path)
        for path in ['models/block/exodus/diamond_block.json','models/block/exodus/pumpkin.json','models/blackrazid/gadgets/dynamo.obj.gz','textures/blackrazid/gadgets/wpn_atach.2048.png']:
            self.assertTrue((A/path).exists(),path);self.assertFalse(removed(path),path)
    def test_all_original_gadget_files_have_unique_correct_provenance_and_hashes(self):
        rows=json.loads((S/'sources.json').read_text())['files'];self.assertEqual(18,len(rows))
        self.assertEqual(len(rows),len({r['driveId'] for r in rows}));self.assertEqual(len(rows),len({r['path'] for r in rows}))
        for row in rows:
            b=(S/row['path']).read_bytes();self.assertEqual(row['bytes'],len(b));self.assertEqual(row['sha256'],hashlib.sha256(b).hexdigest())
        ids={r['path']:r['driveId'] for r in rows}
        self.assertEqual('1_xkxhfV23zgTRkGZumGYEmb28Q_j6HsH',ids['clock.png'])
        self.assertEqual('1fPeNoGIqJPpE4dhVGXD4Fuw9ncJLvaxC',ids['HourArrowModel.java'])
        self.assertEqual('1-U6V_Rc9pOHVLwmUTfaKH230iSXiDmJ9',ids['flashlight.mtl'])
    def test_original_geometry_and_material_textures_are_not_replaced_by_firearm_assets(self):
        meshes=json.loads((A/'blackrazid_gadget_meshes.json').read_text());self.assertEqual({'flashlight','flyashka','dynamo','meh','strelka'},set(meshes))
        for name,mesh in meshes.items():
            self.assertEqual((S/(name+'.obj')).read_bytes(),gzip.decompress((A/('models/blackrazid/gadgets/'+name+'.obj.gz')).read_bytes()))
            for texture in mesh['materials'].values():
                path=texture.removeprefix('metroport:');self.assertIn('blackrazid/gadgets/',path)
                self.assertEqual((S/Path(path).name).read_bytes(),(A/path).read_bytes())
        self.assertEqual((S/'clock.png').read_bytes(),(A/'textures/blackrazid/gadgets/clock.png').read_bytes())
    def test_reimport_preserves_the_three_survival_display_models(self):
        paths=[A/('models/item/'+id+'.json') for id in ['blackrazid_lamp','blackrazid_power','blackrazid_flyashka','blackrazid_watch']]
        before=[p.read_bytes() for p in paths]
        subprocess.run([sys.executable,'tools/import_blackrazid_survival.py'],cwd=ROOT,check=True,capture_output=True)
        self.assertEqual(before,[p.read_bytes() for p in paths])
        for p in paths[:3]:self.assertIn('firstperson_righthand',json.loads(p.read_text())['display'])
    def test_native_watch_uses_world_time_without_radiation_or_oxygen_hand(self):
        renderer=(ROOT/'src/main/java/ru/metrocraft/port/client/BlackrazidWatchRenderer.java').read_text()
        self.assertIn('getWorldTime()',renderer);self.assertNotIn('SimpleDateFormat',renderer);self.assertNotIn('Oxygen',renderer)
        hud=(ROOT/'src/main/java/ru/metrocraft/port/client/WatchClient.java').read_text()
        self.assertIn('if(!timeOnly)',hud)
    def test_retired_firearm_input_packets_and_render_subscribers_are_not_registered(self):
        metro=(ROOT/'src/main/java/ru/metrocraft/port/MetroPort.java').read_text()
        for name in ['GunActionPacket.Handler.class','ShootPacket.Handler.class','ReloadPacket.Handler.class']:
            self.assertFalse(any(name in line and not line.lstrip().startswith('//') for line in metro.splitlines()))
        client=(ROOT/'src/main/java/ru/metrocraft/port/ClientEvents.java').read_text()
        self.assertNotIn('FOVUpdateEvent',client)
        for name in ['NativeLaser.java']:
            code=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/weapons'/name).read_text()
            self.assertNotIn('@Mod.EventBusSubscriber',code)
