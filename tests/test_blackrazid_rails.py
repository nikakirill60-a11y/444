import hashlib,json,re,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/metroport';S=ROOT/'tools/blackrazid-reference'

class BlackrazidRailsTest(unittest.TestCase):
    def test_transport_provenance_is_original_and_complete(self):
        rows=json.loads((S/'transport-source-files.json').read_text())['files'];self.assertEqual(19,len(rows));self.assertEqual(19,len({r['path'] for r in rows}))
        for row in rows:
            b=(S/row['path']).read_bytes();self.assertEqual(len(b),row['bytes']);self.assertEqual(hashlib.sha256(b).hexdigest(),row['sha256']);self.assertTrue(row['driveId'])
    def test_all_twelve_footprints_match_native_array_not_invented_tracks(self):
        sys.path.insert(0,str(ROOT/'tools'))
        import import_blackrazid_rails as importer
        rows=json.loads((A/'blackrazid_rails.json').read_text());self.assertEqual(rows,importer.profiles());self.assertEqual(list(range(12)),[r['id'] for r in rows])
        self.assertEqual([[-1,0,0],[1,0,0]],rows[0]['offsets']);self.assertGreater(len(rows[3]['offsets']),60)
        for r in rows:
            for language in ['ru_ru','en_us']:self.assertEqual(1,(A/('lang/'+language+'.lang')).read_text().count('item.metroport.blackrazid_wide_rails_'+str(r['id'])+'.name='))
    def test_every_original_obj_material_has_its_native_png(self):
        maps=json.loads((A/'blackrazid_rail_materials.json').read_text());self.assertEqual({'rails','rails90','railsx'},set(maps))
        for source in ['rails','rails90','railsX']:
            native=(S/('assets/rails/'+source+'.obj')).read_bytes();self.assertEqual(native,(A/('models/blackrazid/rails/'+source.lower()+'.obj')).read_bytes())
            referenced=set(re.findall(r'^usemtl (\S+)',native.decode(),re.M));self.assertEqual(referenced,set(maps[source.lower()]))
            for png in maps[source.lower()].values():
                target=A/png.split(':',1)[1];self.assertEqual((S/('assets/rails/'+target.name)).read_bytes(),target.read_bytes())
    def test_missing_vehicle_meshes_and_empty_source_draws_are_not_claimed_done(self):
        gaps=json.loads((ROOT/'docs/blackrazid-transport-source-gaps.json').read_text());self.assertEqual(5,len(gaps['missingVehicleMeshes']));self.assertEqual([2,5,6,7,8,9,10,11],gaps['emptyNativeDrawBranches'])
        code='\n'.join(p.read_text() for p in (ROOT/'src/main/java/ru/metrocraft/port/blackrazid/transport').glob('*.java'))
        for forbidden in ['ExtendetPlayer','executeCommand(', 'createExplosion(', 'EntityMinecart', 'net.minecraft.client']:self.assertNotIn(forbidden,code)
        self.assertNotIn('Entityregina.class',code);self.assertNotIn('EntityDrezina.class',code)
    def test_importer_is_reproducible(self):
        files=[A/'blackrazid_rails.json',A/'blackrazid_rail_materials.json',A/'lang/ru_ru.lang',A/'lang/en_us.lang',ROOT/'docs/blackrazid-transport-source-gaps.json']
        before=[p.read_bytes() for p in files];subprocess.run([sys.executable,'tools/import_blackrazid_rails.py'],cwd=ROOT,check=True,capture_output=True);self.assertEqual(before,[p.read_bytes() for p in files])
    def test_material_reader_retains_old_group_mode_and_renderer_state(self):
        parser=(ROOT/'src/main/java/ru/metrocraft/port/mcmod/McmodObjMesh.java').read_text();self.assertIn('readMaterials(InputStream',parser);self.assertIn('if(!byMaterial)group',parser)
        renderer=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/transport/WideRailRenderer.java').read_text()
        for marker in ['McmodObjMesh.readMaterials','GlStateManager.bindTexture(texture)','GlStateManager.shadeModel(shade)','GlStateManager.disableNormalize()']:self.assertIn(marker,renderer)
