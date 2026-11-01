import hashlib,json,re,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/metroport';S=ROOT/'tools/blackrazid-reference'
class BlackrazidEnvironmentTest(unittest.TestCase):
    def test_every_recovered_original_matches_provenance(self):
        rows=json.loads((S/'environment-source-files.json').read_text())['files'];self.assertEqual(len(rows),len({r['path'] for r in rows}));self.assertGreaterEqual(len(rows),17)
        for r in rows:
            b=(S/r['path']).read_bytes();self.assertEqual(len(b),r['bytes']);self.assertEqual(hashlib.sha256(b).hexdigest(),r['sha256'])
    def test_all_159_cubes_and_17_helpers_are_from_native_source(self):
        sys.path.insert(0,str(ROOT/'tools'));import import_blackrazid_environment as imp
        self.assertEqual(imp.geometry(),json.loads((A/'geometry/blackrazid/tunnel.json').read_text()));self.assertEqual(159,len(imp.geometry()['parts']));self.assertEqual([128,128],imp.geometry()['textureSize'])
        self.assertEqual(imp.profiles(),json.loads((A/'blackrazid_tunnels.json').read_text()));self.assertEqual(17,len(imp.profiles()[0]['offsets']))
    def test_concrete_mesh_and_all_material_png_are_native(self):
        self.assertEqual((S/'assets/tunnels/tunnel_beton_1.obj').read_bytes(),(A/'models/blackrazid/tunnels/tunnel_beton_1.obj').read_bytes());maps=json.loads((A/'blackrazid_tunnel_materials.json').read_text());self.assertEqual(6,len(maps));self.assertEqual(5,len(set(maps.values())))
        for value in maps.values():
            target=A/value.split(':')[1];self.assertEqual(target.read_bytes(),(S/('assets/tunnels/'+target.name)).read_bytes())
        self.assertEqual((A/'textures/blackrazid/tunnels/tunnel.png').read_bytes(),(S/'assets/tunnels/tunnel.png').read_bytes());self.assertNotEqual((S/'assets/tunnels/tunnel.png').read_bytes(),(S/'assets/tunnels/block/tunnel.png').read_bytes())
    def test_environment_import_and_localization_are_reproducible(self):
        paths=[A/'blackrazid_tunnels.json',A/'geometry/blackrazid/tunnel.json',A/'blackrazid_tunnel_materials.json',A/'blackrazid_tunnel_mesh_materials.json',A/'lang/ru_ru.lang',A/'lang/en_us.lang'];before=[p.read_bytes() for p in paths];subprocess.run([sys.executable,'tools/import_blackrazid_environment.py'],cwd=ROOT,check=True,capture_output=True);self.assertEqual(before,[p.read_bytes() for p in paths])
    def test_no_excluded_faction_or_general_handler_dependency(self):
        code='\n'.join(p.read_text() for p in (ROOT/'src/main/java/ru/metrocraft/port/blackrazid/environment').glob('*.java'))
        for forbidden in ['ExtendetPlayer','CommandDefence','executeCommand','net.minecraft.client','AirSetup','createExplosion']:self.assertNotIn(forbidden,code)
        registry=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/environment/BlackrazidEnvironment.java').read_text();self.assertNotIn('new net.minecraft.item.ItemBlock(TELEPORT)',registry);self.assertNotIn('setCreativeTab(MetroPort.DECOR)',registry);self.assertIn('TransientLightTile.class',registry)
