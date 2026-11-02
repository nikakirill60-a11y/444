"""Duplet bundle regressions; no Minecraft runtime claims."""
import hashlib, json, sys, tempfile, unittest, zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT/'tools'))
from build_duplet_pack import build, originals, BASE, FOUR, MODULE, ATTACHMENTS
import tests_mwf.test_uboinik_pose as reference
class DupletPackTests(unittest.TestCase):
 @classmethod
 def setUpClass(cls):
  cls.archive=ROOT/'downloads/Metro_Uboinik_Duplet_MWF_fix1_v1.zip'
  with zipfile.ZipFile(cls.archive) as z:cls.files={n:z.read(n) for n in z.namelist()}
  cls.src=originals();cls.g=json.loads(cls.files['assets/modularwarfare/gltf/guns/metro_duplet.gltf'])
 def js(self,n):return json.loads(self.files[n])
 def test_two_and_four_config_with_default_module(self):
  for id,n in [(BASE,2),(FOUR,4)]:
   gun=self.js('guns/'+id+'.json');self.assertEqual(gun['internalAmmoStorage'],n);self.assertEqual(gun['numBullets'],8);self.assertEqual(gun['acceptedBullets'],['metro_12g_buckshot'])
  self.assertEqual(self.js('guns/'+FOUR+'.json')['defaultAttachments'],{'barrel':MODULE})
 def test_exact_texture_mappings_from_source_mtl(self):
  for id,(obj,texture,slot,name) in ATTACHMENTS.items():
   self.assertEqual(self.files['assets/modularwarfare/skins/attachments/'+id+'.png'],self.src[texture])
   mtl=self.src[obj.replace('.obj','.mtl')].decode();self.assertIn('wpn_obves.2048.png' if texture=='obvestexture.png' else 'wpn_duplet.2048.png',mtl)
 def test_ubo_unchanged_except_extended_languages(self):
  with zipfile.ZipFile(ROOT/'downloads/Metro_Uboinik_MWF_fix1_v5.zip') as z:
   for n in z.namelist():
    if not n.endswith('.lang'):self.assertEqual(self.files[n],z.read(n),n)
 def test_geometry_and_index_limits(self):
  self.assertEqual(len(self.g['nodes']),7);self.assertEqual(len(self.g['meshes']),6)
  for m in self.g['meshes']:
   for p in m['primitives']:
    a=self.g['accessors'][p['indices']];self.assertEqual(a['componentType'],5123);self.assertLess(a['count'],65536)
 def test_nominal_reload_durations(self):
  for id,n,duration in [(BASE,2,3),(FOUR,4,4.8)]:
   a=self.js('guns/render/'+id+'.render.json')['animations'];total=sum((a[k]['endTime']-a[k]['startTime'])/(24*a[k]['speed'])*count for k,count in [('preReloadEmpty',1),('reloadFirstEmpty',n),('reloadSecondEmpty',n-1),('postReloadEmpty',1)])
   self.assertAlmostEqual(total,duration)
 def test_camera_full_pipeline_frustum_for_both_variants(self):
  for id in [BASE,FOUR]:
   ref=reference.PoseConversionTests();ref.g=self.g;ref.nodes={n['name']:n for n in self.g['nodes']};ref.config=self.js('guns/render/'+id+'.render.json')
   for ads in [0,.5,1]:
    for fov in [50,70,90]:self.assertGreater(ref.projected_body_fraction(ref.enhanced_camera(ads),fov),.1)
 def test_original_mesh_not_falsely_claimed_as_break_action(self):
  names={n['name'] for n in self.g['nodes']};self.assertNotIn('barrelModel',names);self.assertFalse(any(n.startswith('bulletModel_') for n in names))
  self.assertFalse(self.js('DUPLET_SOURCE_MANIFEST.json')['sourceAnimationsProvided'])
 def test_hidden_flat_third_person_icon(self):
  for id in [BASE,FOUR]:self.assertEqual(self.js('assets/modularwarfare/models/item/'+id+'.json')['display']['thirdperson_righthand']['scale'],[0,0,0])
 def test_reproducible_bundle(self):
  with tempfile.TemporaryDirectory() as d:
   p=Path(d);build(p/'pack',p/'pack.zip');self.assertEqual(hashlib.sha256(self.archive.read_bytes()).digest(),hashlib.sha256((p/'pack.zip').read_bytes()).digest())
