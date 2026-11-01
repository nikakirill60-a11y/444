import gzip,hashlib,json,math,re,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT/'tools'))
from import_blackrazid_weapon_models import catalog
S=ROOT/'tools/blackrazid-reference';A=ROOT/'src/main/resources/assets/metroport'
class NativeWeaponModelTests(unittest.TestCase):
    def test_all_originals_are_verified_and_class_paths_match_actual_packages(self):
        rows=json.loads((S/'weapons-model-source-files.json').read_text())['files']
        self.assertEqual(29,sum(r['path'].endswith('.class') for r in rows));self.assertEqual(len(rows),len({r['driveId'] for r in rows}));self.assertEqual(len(rows),len({r['path'] for r in rows}))
        for r in rows:
            data=(S/r['path']).read_bytes();self.assertEqual(r['bytes'],len(data));self.assertEqual(r['sha256'],hashlib.sha256(data).hexdigest())
            if r['path'].endswith('.class'):self.assertIn(r['path'][8:-6].encode(),data)
    def test_all22_original_gun_renderers_are_recovered_but_not_misreported_as_runtime_meshes(self):
        rows=list((S/'java/mods/provoda/guns/renderers').glob('Render_*.java'));self.assertEqual(22,len(rows))
        self.assertTrue(all('GLModel' in p.read_text() for p in rows))
    def test_all22_bundled_meshes_decompress_to_exact_original_bytes(self):
        meshes=json.loads((A/'blackrazid_weapon_meshes.json').read_text());self.assertEqual(22,len(meshes))
        self.assertEqual(15,sum(k.startswith('gatling/') for k in meshes))
        for key,row in meshes.items():
            data=(S/('assets/weapons/'+key)).read_bytes();self.assertEqual(data,gzip.decompress((A/('models/blackrazid/weapons/'+key+'.gz')).read_bytes()));self.assertEqual(row['sha256'],hashlib.sha256(data).hexdigest())
            materials={m.strip() for m in re.findall(r'^usemtl (.+)$',data.decode(),re.M)};self.assertTrue(materials<=row['materials'].keys());self.assertTrue(all(math.isfinite(x) for x in row['bounds']))
            for texture in row['materials'].values():self.assertTrue((A/texture.split(':')[1]).is_file());self.assertEqual(texture,texture.lower())
    def test_all_seven_and_only_registered_standalone_3d_module_items_use_their_own_native_mesh(self):
        rows=catalog();self.assertEqual(rows,json.loads((A/'blackrazid_weapon_module_models.json').read_text()));self.assertEqual({'Barrel','Butt','Kolimator','Laser','Optica','OpticaSmall','UbludokButt'},{r['sourceId'] for r in rows})
        registered={r['id'] for r in json.loads((A/'blackrazid_modules.json').read_text())};self.assertTrue({r['id'] for r in rows}<=registered)
        self.assertTrue(all(r['scale']>0 and all(math.isfinite(x) for x in r['center']) for r in rows))
    def test_runtime_atlases_are_exact_originals_including_native_material_variants(self):
        for target in (A/'textures/blackrazid/weapons').rglob('*.png'):
            relative=target.relative_to(A/'textures/blackrazid/weapons');matches=[p for p in (S/'assets/weapons').rglob('*.png') if p.relative_to(S/'assets/weapons').as_posix().lower()==relative.as_posix()]
            self.assertEqual(1,len(matches));self.assertEqual(matches[0].read_bytes(),target.read_bytes())
        self.assertNotEqual((S/'assets/weapons/textures/wpn_obves.2048.png').read_bytes(),(S/'assets/weapons/gatling/wpn_obves.2048.png').read_bytes())
    def test_import_is_reproducible_without_rewriting_existing_approved_poses(self):
        paths=[A/'blackrazid_weapon_meshes.json',A/'blackrazid_weapon_module_models.json',A/'geometry/blackrazid_weapon_poses.json',A/'geometry/gun_poses.json'];before=[p.read_bytes() for p in paths]
        subprocess.run([sys.executable,'tools/import_blackrazid_weapon_models.py'],cwd=ROOT,check=True,capture_output=True);self.assertEqual(before,[p.read_bytes() for p in paths]);self.assertEqual({'blackrazid_gatling'},set(json.loads(paths[2].read_text())))
    def test_renderer_caches_native_meshes_restores_gl_state_and_does_not_copy_watch_or_excluded_handlers(self):
        code=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/weapons/NativeWeaponRenderer.java').read_text();self.assertIn('LISTS.clear()',code);self.assertIn('GLAllocation.deleteDisplayLists',code);self.assertIn('GlStateManager.tryBlendFuncSeparate(srcRGB,dstRGB,srcA,dstA)',code);self.assertIn('GlStateManager.bindTexture(texture)',code)
        for forbidden in ['ExtendetPlayer','AirSetup','new Clock','GL11.glPopMatrix','setTagCompound','setInteger','setLong','MinecraftForge.EVENT_BUS']:self.assertNotIn(forbidden,code)
        wiring=(ROOT/'src/main/java/ru/metrocraft/port/ClientEvents.java').read_text();self.assertIn('NativeWeaponVisuals.supported(stack)',wiring);self.assertIn('NativeWeaponRenderer.INSTANCE',wiring)
        reload=(ROOT/'src/main/java/ru/metrocraft/port/client/ClientProxy.java').read_text();self.assertIn('NativeWeaponRenderer.reload()',reload);self.assertIn('GunRenderPoses.reload()',reload)
    def test_native_gatling_mesh_selection_and_offsets_agree_with_source(self):
        text=(S/'java/mods/provoda/guns/renderers/Render_Gatling.java').read_text()
        for source in ['GatlingBarrel','GatlingButt','GatlingMotor','GatlingGiro','this.stvol5.render()','this.base5.render()','this.giro_wheel.render()','(float)0.002f, (float)-0.002f, (float)-0.32f','(float)-0.0f, (float)-0.093f, (float)-0.441f']:self.assertIn(source,text)
        runtime=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/weapons/NativeWeaponVisuals.java').read_text();self.assertIn('drive==2?"base5":"base3"',runtime);self.assertIn('five?"stvol5":"stvol"',runtime)
