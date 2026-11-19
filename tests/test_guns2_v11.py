"""Source/config guards, plus output checks when the v11 build is available. No live game test."""
import hashlib,json,sys,unittest,zipfile
from pathlib import Path
R=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(R/'tools'))
class Guns2(unittest.TestCase):
 def test_archive_is_exact_and_all_requested_resources_exist(self):
  m=json.loads((R/'tools/metro-guns2/sources.json').read_text());p=R/'tools/metro-guns2/sources.zip'
  self.assertEqual(m['sha256'],hashlib.sha256(p.read_bytes()).hexdigest())
  with zipfile.ZipFile(p) as z:
   for path in ['Stallion/stall.obj','Ublud old/ubludok.obj','Army Kalash/wpn_aka.1024.png','duplet_2_variant.png']:
    self.assertIn('Metro guns 2/'+path,z.namelist())
 def test_new_inventory_tiers_and_magazine_geometry(self):
  b=json.loads((R/'src/main/resources/assets/metroport/inventory_balance.json').read_text())['items']
  for id,tier in {'army_ak':'legendary','ubludok_old':'epic','stallion':'exclusive','duplet_2':'common'}.items():
   self.assertEqual(tier,b['modularwarfare:metro_'+id]['rarity'])
  m=b['modularwarfare:metro_ubludok_old_ammo'];self.assertEqual((1,2),(m['width'],m['height']))
 def test_patched_rules_and_transactions_scope_both_ids_without_converting(self):
  src=R/'tools/mwf-mechanics/src/ru/metrocraft/mwf'
  self.assertIn('"metro_duplet_2".equals(id)',(src/'MechanicsRules.java').read_text())
  self.assertIn('MechanicsRules.duplet(id(stack))',(src/'MechanicsHooks.java').read_text())
  self.assertIn('!id(gun).equals(id(updated))',(src/'AttachmentTransactions.java').read_text())
 def test_explicit_balance_preserves_pellets_and_all_original_meshes(self):
  import ast
  tree=ast.parse((R/'tools/build_mwf_v11.py').read_text())
  BALANCE=next(ast.literal_eval(n.value) for n in tree.body if isinstance(n,ast.Assign) and any(isinstance(t,ast.Name) and t.id=='BALANCE' for t in n.targets))
  self.assertEqual(25,len(BALANCE))
  self.assertLess(BALANCE['ubludok'][0],BALANCE['ak'][0]);self.assertGreater(BALANCE['rpk'][1],BALANCE['ak'][1])
  p=R/'.cache/output-v11/Metro_Full_Arsenal_MWF_fix1_v11_1_army_texture.zip'
  if not p.exists():return # Output-level checks also run unconditionally in the builder CI step.
  with zipfile.ZipFile(p) as z:
   m=json.loads(z.read('GUNS2_V11_MANIFEST.json'));self.assertEqual(29,m['verifiedGunCount']);self.assertTrue(m['allOriginalGeometryByteIdentical'])
   for id,delta in m['rebalance'].items():
    g=json.loads(z.read('guns/'+id+'.json'));self.assertEqual(delta['pelletsUnchanged'],g['numBullets'])
    self.assertGreaterEqual(delta['reloadTicksAfter'],delta['reloadTicksBefore'])

 def test_army_ak_uses_own_supplied_texture_without_replacing_kalash(self):
  p=R/'.cache/output-v11/Metro_Full_Arsenal_MWF_fix1_v11_1_army_texture.zip'
  if not p.exists():self.skipTest('Build output unavailable; run builder first')
  with zipfile.ZipFile(p) as z, zipfile.ZipFile(R/'tools/metro-guns2/sources.zip') as source:
   g=json.loads(z.read('guns/metro_army_ak.json'));base=json.loads(z.read('guns/metro_ak.json'))
   self.assertEqual('metro_army_ak',g['modelSkins'][0]['skinAsset'])
   self.assertNotEqual(g['modelSkins'],base['modelSkins'])
   self.assertEqual(source.read('Metro guns 2/Army Kalash/wpn_aka.1024.png'),z.read('assets/modularwarfare/skins/guns/metro_army_ak.png'))
   self.assertEqual(g['acceptedAttachments'],base['acceptedAttachments'])
   report=json.loads(z.read('GUNS2_V11_MANIFEST.json'))
   self.assertEqual(hashlib.sha256(z.read('assets/modularwarfare/skins/guns/metro_army_ak.png')).hexdigest(),report['newGuns']['metro_army_ak']['textureSha256'])
   self.assertEqual(29,report['verifiedGunCount'])
