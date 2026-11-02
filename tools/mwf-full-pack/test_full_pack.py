"""Focused unit tests plus pack verification lives in verify_full_mwf_pack.py."""
import io, itertools, json, math, sys, unittest, zipfile
from pathlib import Path
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
import numpy as np
from build_full_mwf_pack import Assets, Atlas, ROOT, SOURCE, condition, evaluate, key, parse_obj, prepare, special, transform, wrap_triangle


class GeometryTests(unittest.TestCase):
    def test_negative_obj_indices_and_material_boundaries(self):
        obj=b'v 0 0 0\nv 1 0 0\nv 0 1 0\nvt 0 0\nvt 1 0\nvt 0 1\nusemtl body\nf -3/-3 -2/-2 -1/-1\nusemtl addon\nf 1/1 2/2 3/3\n'
        triangles=parse_obj(obj);self.assertEqual([m for m,t in triangles],['body','addon'])
        self.assertTrue(np.allclose(triangles[0][1][0][2],[0,0,1]))
    def test_source_matrix_order(self):
        m=transform('translate',[1,2,3])@transform('rotate',[90,0,1,0])@transform('scale',[2,2,2])
        self.assertTrue(np.allclose(m@np.array([0,0,1,1]),[3,2,3,1]))
    def test_condition_does_not_execute_source(self):
        e={'MAG':True,'BIG':False,'rounds':5,'slots':['Optica','','','','']}
        self.assertTrue(condition({'op':'and','terms':[{'op':'MAG'},{'op':'S0EQOptica'},{'op':'R5'}]},e))
        self.assertFalse(condition({'op':'R6'},e))
        with self.assertRaises(ValueError):condition({'op':'arbitrary_java_call'},e)
    def test_uv_clipping_preserves_area_and_wraps(self):
        tri=[((0,0,0),(-.2,.2),(0,0,1)),((1,0,0),(1.4,.2),(0,0,1)),((0,1,0),(.2,1.3),(0,0,1))]
        pieces=list(wrap_triangle(tri));self.assertGreater(len(pieces),1)
        area=sum(np.linalg.norm(np.cross(np.subtract(t[1][0],t[0][0]),np.subtract(t[2][0],t[0][0])))/2 for t in pieces)
        self.assertAlmostEqual(area,.5,places=7)
        self.assertTrue(all(0<=u<=1 for t in pieces for p,uv,n in t for u in uv))
    def test_source_archive_hashes(self):
        import hashlib
        manifest=json.loads((SOURCE/'native-sources.json').read_text())
        with zipfile.ZipFile(SOURCE/'native-sources.zip') as z:
            self.assertEqual(set(z.namelist()),{f['path'] for f in manifest['files']})
            for f in manifest['files']:
                data=z.read(f['path']);self.assertEqual(hashlib.sha256(data).hexdigest(),f['sha256']);self.assertEqual(len(data),f['bytes'])
    def test_no_removed_metroport_source_restored(self):
        p=ROOT/'src/main/java/ru/metrocraft/port/blackrazid/weapons/BlackrazidWeapons.java'
        before=p.read_bytes() if p.exists() else None
        prepare(ROOT/'.cache/full-pack/build')
        self.assertEqual(p.read_bytes() if p.exists() else None,before)


class NativeSceneTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.legacy,cls.native,cls.scenes=prepare(ROOT/'.cache/full-pack/build')
        cls.assets=Assets(cls.legacy,cls.native)
        cls.data=cls.legacy/'src/main/resources/assets/metroport'
        cls.fittings=json.loads((cls.data/'blackrazid_module_fittings.json').read_text())
    def test_compiled_scene_matrices_are_balanced(self):
        for name,scene in self.scenes.items():
            for big in (False,True):
                e={'MAG':True,'BIG':big,'rounds':40,'slots':['']*5}
                for d in evaluate(scene,e):self.assertTrue(np.isfinite(d[2]).all(),name)
    def test_all_supported_independent_module_combinations(self):
        # Source branches must compose exactly, not leave 3+5 Gatling barrels or duplicate stocks.
        count=0
        for name,scene in self.scenes.items():
            def body(e):return {key(d) for d in evaluate(scene,e) if d[1] not in ('magazine','shell')}
            e={'MAG':True,'BIG':False,'rounds':40,'slots':['']*5};default=body(e);variants={}
            for slot,choices in enumerate(self.fittings[name]):
                for mod in choices:
                    other={**e,'slots':list(e['slots'])};other['slots'][slot]=mod;v=body(other)
                    if v!=default:variants[mod]=(v-default,default-v)
            opts=[['']+[m for m in choices if m in variants] for choices in self.fittings[name]]
            for mods in itertools.product(*opts):
                expected=body({**e,'slots':list(mods)});added=set();removed=set()
                for mod in mods:
                    if mod:
                        a,r=variants[mod];added|=a;removed|=r
                self.assertEqual((default-removed)|added,expected,(name,mods));count+=1
        self.assertGreater(count,200)
    def test_gatling_barrels_replace_not_stack(self):
        for barrel,motor in itertools.product(('','GatlingBarrel'),('','GatlingMotor','GatlingGiro')):
            e={'MAG':True,'BIG':False,'rounds':150,'slots':[barrel,'',motor,'','']};draws=special('Gatling',e)
            barrels=[d[0] for d in draws if d[0] in ('gatling/stvol.obj','gatling/stvol5.obj')];self.assertEqual(len(barrels),1)
    def test_podonok_has_simultaneous_body_and_addon_textures(self):
        triangles=self.assets.triangles(('podonok/podonok.obj','body',np.eye(4)));paths={Path(m[1]).name for m,t in triangles}
        self.assertEqual(paths,{'wpn34_padonocheg.1024c.png','wpn34_padonocheg_addon.1024c.png'})
    def test_railgun_keeps_all_four_primary_images(self):
        triangles=self.assets.triangles(('railgun/railgun.obj','body',np.eye(4)));paths={Path(m[1]).name for m,t in triangles}
        self.assertEqual(paths,{'wpn_tihar.2048.png','wpn_railgun.2048.png','veh_zuzik.2048.png','wpn34_gun_pricel.2048.png'})
    def test_shared_atlas_repeating_uvs_are_bounded(self):
        part=self.assets.triangles(('akk/mag.obj','body',np.eye(4)));a=Atlas([part]);converted=a.convert(part)
        self.assertTrue(a.repeat);self.assertTrue(all(0<=u<=1 for t in converted for p,uv,n in t for u in uv))


if __name__=='__main__':unittest.main()
