import base64, contextlib, hashlib, io, json, struct, sys, tempfile, unittest, zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'tools'))
from build_uboinik_pack import GUN,ROUND,SOURCE_REF,originals,obj_read,build,arm_mesh
from verify_uboinik_pack import verify
P=ROOT/'contentpacks/metro_uboinik'
A=P/'assets/modularwarfare'

def js(p):return json.loads(p.read_text())

class UboinikPackTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.source=originals();cls.groups=obj_read(cls.source['uboinik.obj']);cls.gltf=js(A/'gltf/guns/metro_uboinik.gltf')
        cls.archive=ROOT/'artifacts/Metro_Uboinik_MWF_fix1_v4.zip'
    def test_full_pack_structure_and_loader_dependencies(self):
        with contextlib.redirect_stdout(io.StringIO()):result=verify(P)
        self.assertEqual({'files':40,'rounds':6,'triangles':6901,'items':5},result)
    def test_original_commit_and_every_hash_are_recorded(self):
        m=js(P/'SOURCE_MANIFEST.json');self.assertEqual(SOURCE_REF,m['sourceCommit']);self.assertEqual(18,len(m['sourceFiles']))
        for r in m['sourceFiles']:
            b=self.source[Path(r['path']).name];self.assertEqual(len(b),r['bytes']);self.assertEqual(hashlib.sha256(b).hexdigest(),r['sha256'])
    def test_exact_six_source_rounds_are_kept(self):
        names={n['name'] for n in self.gltf['nodes']}
        for i in range(6):self.assertIn('bulletModel_'+str(i),names);self.assertEqual(176,len(self.groups['bulletModel_'+str(i)]))
        for remaining in range(7):
            visible=[n for n in names if n.startswith('bulletModel_') and int(n.rsplit('_',1)[1])<remaining]
            self.assertEqual(remaining,len(visible))
    def test_gun_has_no_detachable_magazine_and_one_shell_per_shot(self):
        g=js(P/'guns/metro_uboinik.json');self.assertNotIn('acceptedAmmo',g);self.assertEqual([ROUND],g['acceptedBullets']);self.assertEqual(6,g['internalAmmoStorage']);self.assertEqual(8,g['numBullets'])
    def test_attachment_textures_match_user_correction_byte_for_byte(self):
        for id,name in [('metro_uboinik_suppressor','textures.png'),('metro_uboinik_laser','texturen.png')]:
            self.assertEqual(self.source[name],(A/'skins/attachments'/f'{id}.png').read_bytes())
            self.assertEqual(name,js(P/'SOURCE_MANIFEST.json')['conversion']['attachmentTextures'][id])
    def test_all_user_sounds_are_exact_original_bytes(self):
        for name,source in [('fire','Выстрел.ogg'),('load','Перезарядка.ogg'),('bolt','Затвор.ogg')]:self.assertEqual(self.source[source],(A/f'sounds/metro_uboinik_{name}.ogg').read_bytes())
    def test_added_arms_have_outward_normals_and_winding(self):
        for side in ['left','right']:
            for slim in [False,True]:
                tris=arm_mesh(side,slim);points=[v[0] for t in tris for v in t];center=[(min(p[i] for p in points)+max(p[i] for p in points))/2 for i in range(3)]
                for tri in tris:
                    n=tri[0][2];centroid=[sum(v[0][i] for v in tri)/3 for i in range(3)]
                    self.assertGreater(sum(n[i]*(centroid[i]-center[i]) for i in range(3)),0)
                    a,b,c=[v[0] for v in tri];ab=[b[i]-a[i] for i in range(3)];ac=[c[i]-a[i] for i in range(3)]
                    cross=[ab[1]*ac[2]-ab[2]*ac[1],ab[2]*ac[0]-ab[0]*ac[2],ab[0]*ac[1]-ab[1]*ac[0]]
                    self.assertGreater(sum(n[i]*cross[i] for i in range(3)),0)
    def test_atlas_does_not_repaint_source_body_or_shells(self):
        from PIL import Image
        atlas=Image.open(A/'skins/guns/metro_uboinik.png');body=Image.open(io.BytesIO(self.source['Uboinik tex.png'])).convert('RGBA');shell=Image.open(io.BytesIO(self.source['shell (2).png'])).convert('RGBA')
        self.assertEqual((2048,1024),atlas.size);self.assertEqual(body.tobytes(),atlas.crop((0,0,1024,1024)).tobytes())
        self.assertEqual(shell.resize((1024,1024),Image.Resampling.NEAREST).tobytes(),atlas.crop((1024,0,2048,1024)).tobytes())
    def test_every_source_mesh_preserves_triangle_count(self):
        for mesh in self.gltf['meshes']:
            if mesh['name'] not in self.groups:continue
            accessor=self.gltf['accessors'][mesh['primitives'][0]['indices']]
            self.assertEqual(len(self.groups[mesh['name']])*3,accessor['count'])
    def test_obj_attachment_location_is_pack_root_not_resource_root(self):
        self.assertFalse((A/'obj').exists())
        self.assertEqual(3,len(list((P/'obj/attachments').glob('*.obj'))))
        for p in (P/'obj/attachments').glob('*.obj'):
            self.assertEqual({'attachmentModel'},set(obj_read(p.read_bytes())))
    def test_shell_dependency_exists_even_with_ejection_disabled(self):
        b=js(P/'bullets/metro_12g_buckshot.json');self.assertEqual({'shellModel'},set(obj_read((P/'obj/shells'/b['shellModelFileName']).read_bytes())))
        self.assertFalse(js(P/'guns/metro_uboinik.json')['dropBulletCasing'])
    def test_ads_has_explicit_matrix_offset_not_only_unused_aim_clip(self):
        aim=js(P/'guns/render/metro_uboinik.render.json')['aim']['translateAimPosition'];self.assertAlmostEqual(-.15,aim['x']);self.assertAlmostEqual(.299,aim['y']);self.assertAlmostEqual(-.2,aim['z'])
    def test_third_person_flat_icon_is_disabled_to_avoid_double_render(self):
        m=js(A/'models/item/metro_uboinik.json')
        for side in ['thirdperson_lefthand','thirdperson_righthand']:self.assertEqual([0,0,0],m['display'][side]['scale'])
    def test_archive_has_no_wrapper_folder_or_code(self):
        with zipfile.ZipFile(self.archive) as z:
            self.assertIn('guns/metro_uboinik.json',z.namelist());self.assertNotIn('metro_uboinik/guns/metro_uboinik.json',z.namelist());self.assertFalse(any(n.endswith(('.jar','.class')) for n in z.namelist()))
            for p in P.rglob('*'):
                if p.is_file():self.assertEqual(p.read_bytes(),z.read(p.relative_to(P).as_posix()))
    def bad_pack(self,mutate):
        with tempfile.TemporaryDirectory() as d:
            out=Path(d)/'bad.zip'
            with zipfile.ZipFile(out,'w') as z:
                for p in P.rglob('*'):
                    if p.is_file():
                        name=p.relative_to(P).as_posix();b=p.read_bytes()
                        if name.endswith('.gltf'):
                            g=json.loads(b);mutate(g);b=json.dumps(g).encode()
                        z.writestr(name,b)
            with self.assertRaises(AssertionError),contextlib.redirect_stdout(io.StringIO()):verify(out)
    def test_rejects_external_buffers_ignored_by_selected_loader(self):self.bad_pack(lambda g:g['buffers'][0].update(uri='missing.bin'))
    def test_rejects_missing_indices_that_would_crash_hegltf(self):self.bad_pack(lambda g:g['meshes'][0]['primitives'][0].pop('indices'))
    def test_rejects_accessor_offsets_ignored_by_hegltf(self):self.bad_pack(lambda g:g['accessors'][0].update(byteOffset=4))
    def test_rejects_duplicate_names(self):self.bad_pack(lambda g:g['nodes'][1].update(name='root'))
    def test_skin_locations_follow_mwf_not_vanilla_item_convention(self):
        self.assertFalse((A/'textures/skins').exists())
        for category,id in [('guns',GUN),('bullets',ROUND),('attachments','metro_uboinik_suppressor'),('attachments','metro_uboinik_collimator'),('attachments','metro_uboinik_laser')]:
            self.assertTrue((A/'skins'/category/(id+'.png')).is_file())
    def test_rejects_original_broken_texture_layout(self):
        with tempfile.TemporaryDirectory() as d:
            out=Path(d)/'wrong-path.zip'
            with zipfile.ZipFile(self.archive) as source,zipfile.ZipFile(out,'w') as z:
                for name in source.namelist():z.writestr(name.replace('assets/modularwarfare/skins/','assets/modularwarfare/textures/skins/'),source.read(name))
            with self.assertRaisesRegex(AssertionError,'Missing assets/modularwarfare/skins/guns'):
                verify(out)
    def test_builder_is_byte_reproducible(self):
        with tempfile.TemporaryDirectory() as d,contextlib.redirect_stdout(io.StringIO()):
            out=Path(d);build(out/'pack',out/'pack.zip');self.assertEqual(self.archive.read_bytes(),(out/'pack.zip').read_bytes())

if __name__=='__main__':unittest.main()
