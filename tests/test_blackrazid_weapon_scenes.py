import hashlib,json,math,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT/'tools'))
from blackrazid_weapon_scenes import scenes,condition,linear
from blackrazid_weapon_profiles import SCENE_BODIES
A=ROOT/'src/main/resources/assets/metroport'
S=ROOT/'tools/blackrazid-reference'

def nodes(tree):
    for n in tree:
        yield n
        if n['op']=='if':yield from nodes(n['yes']);yield from nodes(n['no'])

class NativeWeaponSceneTests(unittest.TestCase):
    def test_all17_remaining_source_graphs_compile_but_only_seven_closed_assemblies_are_enabled(self):
        compiled=scenes();self.assertEqual(17,len(compiled));self.assertEqual(7,len(SCENE_BODIES))
        active=json.loads((A/'blackrazid_weapon_scenes.json').read_text());self.assertEqual({k:compiled[k] for k in SCENE_BODIES},active)
        meshes=json.loads((A/'blackrazid_weapon_meshes.json').read_text())
        for name,scene in active.items():
            self.assertIn(SCENE_BODIES[name],{n['mesh'] for n in nodes(scene['nodes']) if n['op']=='draw'})
            for n in nodes(scene['nodes']):
                if n['op']=='draw':self.assertIn(n['mesh'],meshes);self.assertEqual(n['mesh'],n['mesh'].lower())
                if 'values' in n:self.assertTrue(all(math.isfinite(v) for v in n['values']+n['rounds']))

    def test_native_sources_are_hashed_and_only_the_known_matrix_leak_is_repaired(self):
        for name,scene in scenes().items():
            text=(S/('java/mods/provoda/'+scene['source'])).read_text()
            self.assertEqual(hashlib.sha256(text.encode()).hexdigest(),scene['sha256'])
            self.assertTrue(set(scene['excludedNonObjModels'])<={'laserline','shoot'})
            self.assertEqual(name=='Ubludok',bool(scene['matrixRepairs']))
            self.assertIn('adaptation',scene['firstPerson'])
            for n in nodes(scene['nodes']):self.assertIn(n['op'],{'if','push','pop','draw','translate','rotate','scale'})

    def test_compiler_accepts_only_explicit_source_conditions_and_linear_transforms(self):
        self.assertEqual((-0.01,.005),tuple(round(v,8) for v in linear('(float)(0.14f + 0.005f * (float)(z - 30))')))
        self.assertEqual('and',condition('revinv.a(1) != null && revinv.a(1).b() == ModBase.Laser')['op'])
        for expression in ['unknown','mc.player.getHealth()','__import__("os")']:
            with self.assertRaises((ValueError,AssertionError,SyntaxError)):condition(expression)
        for expression in ['z*z','1/0','this.unknown','arbitrary()']:
            with self.assertRaises((ValueError,AssertionError,SyntaxError)):linear(expression)

    def test_runtime_reads_detached_equipment_and_flattens_matrices_without_legacy_gl_stack(self):
        scene=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/weapons/NativeWeaponScene.java').read_text()
        for forbidden in ['org.lwjgl','Minecraft.getMinecraft','setTagCompound','setInteger','setLong']:self.assertNotIn(forbidden,scene)
        renderer=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/weapons/NativeWeaponRenderer.java').read_text()
        self.assertIn('NativeWeaponScene.draw(gun.entry.sourceId,equipment)',renderer)
        self.assertIn('GlStateManager.multMatrix(matrix)',renderer);self.assertIn('GlStateBuffers.colorQuery()',renderer)
        visuals=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/weapons/NativeWeaponVisuals.java').read_text()
        self.assertIn('BlackrazidModules.get(stack,i)',visuals);self.assertIn('gun.entry.magazines[0].equals',visuals)
