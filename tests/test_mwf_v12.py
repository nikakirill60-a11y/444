"""Output integration + small geometry guards. NOT a Minecraft usability/play test."""
import ast,base64,io,json,unittest,zipfile,sys
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT/'tools'))
PACK=ROOT/'.cache/output-v12/Metro_Full_Arsenal_MWF_fix1_v12_separate_textures.zip'
class V12Output(unittest.TestCase):
 @classmethod
 def setUpClass(cls):
  if not PACK.exists():raise unittest.SkipTest('Full output checked in the dedicated v12 workflow')
  cls.z=zipfile.ZipFile(PACK);cls.manifest=json.loads(cls.z.read('CORRECTIVE_V12_MANIFEST.json'))
 @classmethod
 def tearDownClass(cls):cls.z.close()
 def get(self,n):return json.loads(self.z.read(n))
 def test_native_fire_mode_spellings_are_never_null(self):
  guns=[self.get(n) for n in self.z.namelist() if n.startswith('guns/') and n.count('/')==1 and n.endswith('.json')]
  self.assertEqual(29,len(guns))
  for g in guns:self.assertTrue(g['fireModes']);self.assertTrue(set(g['fireModes'])<={'semi','full','burst'},g['internalName'])
  self.assertEqual(['semi','full'],self.get('guns/metro_ak.json')['fireModes'])
 def test_no_shared_atlas_or_references_remain(self):
  self.assertFalse(any('metro_native_atlas' in n for n in self.z.namelist()))
  for cat in ('guns','ammo','attachments'):
   for n in self.z.namelist():
    if not(n.startswith(cat+'/') and n.count('/')==1 and n.endswith('.json')):continue
    g=self.get(n)
    for skin in g.get('modelSkins',[]):
     self.assertNotEqual('metro_native_atlas',skin['skinAsset'])
     self.assertIn('assets/modularwarfare/skins/'+cat+'/'+skin['skinAsset']+'.png',self.z.namelist())
  self.assertGreater(len(self.manifest['separateItemTextures']),60)
 def test_kalash_variants_map_to_the_right_native_material(self):
  info=self.manifest['separateItemTextures']
  self.assertIn('ak/wpn34_ak.2048.png',[r['source'] for r in info['metro_ak']['materials']])
  self.assertIn('Army Kalash/wpn_aka.1024.png',[r['source'] for r in info['metro_army_ak']['materials']])
  self.assertNotEqual(self.z.read('assets/modularwarfare/skins/guns/metro_ak.png'),self.z.read('assets/modularwarfare/skins/guns/metro_army_ak.png'))
 def test_stallion_uses_seven_round_magnum_magazine_and_complete_mesh(self):
  g=self.get('guns/metro_stallion.json');self.assertEqual(['metro_stallion_ammo'],g['acceptedAmmo'])
  for k in ('internalAmmoStorage','defaultBullet','acceptedBullets'):self.assertNotIn(k,g)
  a=self.get('ammo/metro_stallion_ammo.json');self.assertEqual(7,a['ammoCapacity']);self.assertEqual(['metro_bullet_magnum'],a['subAmmo']);self.assertTrue(a['isDynamicAmmo'])
  m=self.manifest['stallion'];self.assertEqual(m['sourceTriangles'],m['bodyTriangles']+m['slideTriangles']+m['originalMagazineBaseplateTriangles'])
  self.assertEqual(30648,m['sourceTriangles']);self.assertEqual(12,m['addedInteriorMagazineShellTriangles'])
  d=self.get('assets/modularwarfare/gltf/guns/metro_stallion.gltf');self.assertIn('slideModel',[n['name'] for n in d['nodes']])
  indices={i for i,n in enumerate(d['nodes']) if n['name'] in ('slideModel','ammoModel')}
  self.assertTrue(indices<={c['target']['node'] for a in d['animations'] for c in a['channels']})
  r=self.get('guns/render/metro_stallion.render.json');self.assertGreaterEqual(r['animations']['reloadFirst']['startTime'],300);self.assertNotIn('load',r['animations'])
 def test_no_mwf_material_name_map_overwrite_or_unsupported_animation_keys(self):
  allowed={'default','defaultEmpty','draw','drawEmpty','takedown','takedownEmpty','aim','inspect','inspectEmpty','preLoad','load','postLoad','preUnload','unload','postUnload','preReload','preReloadEmpty','reloadFirst','reloadFirstEmpty','reloadSecond','reloadSecondEmpty','reloadFirstQuickly','reloadSecondQuickly','postReload','postReloadEmpty','preFire','fire','fireLast','postFire','postFireEmpty','preFireADS','fireADS','fireLastADS','postFireADS','postFireADSEmpty','modeChange','sprint','custom','custom1','custom2','custom3','custom4','custom5','custom6','custom7','custom8','primarySkill','secondarySkill'}
  for name in self.z.namelist():
   if name.startswith('assets/modularwarfare/gltf/') and name.endswith('.gltf'):
    d=self.get(name)
    for mesh in d['meshes']:
     runtime_map={d['materials'][p['material']]['name']:p for p in mesh['primitives']}
     self.assertEqual(len(runtime_map),len(mesh['primitives']),name)
   if name.startswith('guns/render/') and name.endswith('.json'):self.assertTrue(set(self.get(name)['animations'])<=allowed,name)
  self.assertTrue(self.manifest['originalPositionsNormalsIndicesAndTimelinesPreserved'])

class V12SmallUnits(unittest.TestCase):
 def test_atlas_right_edge_is_not_misclassified_as_the_next_tile(self):
  from PIL import Image
  from mwf_local_textures import Splitter
  s=Splitter.__new__(Splitter);s.atlas=Image.new('RGBA',(4096,4096))
  self.assertEqual((0,0),s.key((512/4096,512/4096),True))
  self.assertEqual((1,1),s.key((516/4096,516/4096),True))
 def test_multichunk_material_keys_are_unique_and_keep_every_primitive(self):
  from build_mwf_v12 import primitive_names
  d={'materials':[{'name':'old'}],'meshes':[{'primitives':[{'material':0},{'material':0}]}]};primitive_names(d)
  keys=[d['materials'][p['material']]['name'] for p in d['meshes'][0]['primitives']];self.assertEqual(2,len(set(keys)))
 def test_inventory_magazine_profile_and_wiki_tiers_match(self):
  b=json.loads((ROOT/'src/main/resources/assets/metroport/inventory_balance.json').read_text())['items'];w=json.loads((ROOT/'website/wiki-data.json').read_text());byid={e['id']:e for e in w['entries']}
  self.assertEqual(28,sum(e['category']=='weapons' and e['family']=='MWF' for e in w['entries']))
  for id in ('metro_army_ak','metro_stallion','metro_ubludok_old','metro_duplet_2'):
   full='modularwarfare:'+id;self.assertEqual(b[full]['rarity'],byid[full]['rarity'])
  self.assertEqual('7',dict(byid['modularwarfare:metro_stallion_ammo']['stats'])['Ёмкость'])
  self.assertIn('modularwarfare:metro_stallion_ammo',byid['modularwarfare:metro_stallion']['links'])
  self.assertEqual((250,1,2),tuple(b['modularwarfare:metro_stallion_ammo'][k] for k in ('grams','width','height')))
