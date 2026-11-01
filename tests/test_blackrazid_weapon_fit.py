"""Fit regressions against original OBJ geometry, not arbitrary shared AK anchors."""
import hashlib,json,math,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'tools'))
from blackrazid_weapon_profiles import FITS,GUN_MODELS,fitted_point,models,poses
A=ROOT/'src/main/resources/assets/metroport'
S=ROOT/'tools/blackrazid-reference/assets/weapons'

def sub(a,b):return [x-y for x,y in zip(a,b)]
def dot(a,b):return sum(x*y for x,y in zip(a,b))
def cross(a,b):return [a[1]*b[2]-a[2]*b[1],a[2]*b[0]-a[0]*b[2],a[0]*b[1]-a[1]*b[0]]
def segment_distance(p,a,b):
 edge=sub(b,a);length=dot(edge,edge)
 t=max(0,min(1,dot(sub(p,a),edge)/length)) if length else 0
 return math.dist(p,[x+t*y for x,y in zip(a,edge)])
def triangle_distance(p,a,b,c):
 ab=sub(b,a);ac=sub(c,a);ap=sub(p,a)
 aa=dot(ab,ab);cc=dot(ac,ac);bb=dot(ab,ac);den=aa*cc-bb*bb
 if den>1e-18:
  u=(cc*dot(ap,ab)-bb*dot(ap,ac))/den;v=(aa*dot(ap,ac)-bb*dot(ap,ab))/den
  if u>=0 and v>=0 and u+v<=1:
   n=cross(ab,ac);return abs(dot(ap,n))/math.sqrt(dot(n,n))
 return min(segment_distance(p,a,b),segment_distance(p,b,c),segment_distance(p,c,a))
def triangles(name):
 vertices=[];faces=[]
 for line in (S/GUN_MODELS[name]).read_text().splitlines():
  if line.startswith('v '):vertices.append(list(map(float,line.split()[1:])))
  elif line.startswith('f '):
   face=[int(p.split('/')[0])-1 for p in line.split()[1:]]
   faces.extend((face[0],face[i],face[i+1]) for i in range(1,len(face)-1))
 return [[vertices[i] for i in face] for face in faces]

class NativeWeaponFitTests(unittest.TestCase):
 def test_rear_palm_contacts_are_on_actual_handles_not_the_shared_receiver(self):
  for name in FITS:
   with self.subTest(weapon=name):
    distance=min(triangle_distance(FITS[name][1],*tri) for tri in triangles(name))
    self.assertLess(distance,.025,'rear grip must be within 25 mm of the original handle surface')
 def test_support_pad_contact_is_on_the_actual_underside_mesh_surface(self):
  for name in FITS:
   with self.subTest(weapon=name):
    distance=min(triangle_distance(FITS[name][2],*tri) for tri in triangles(name))
    self.assertLess(distance,.000003,'support fingertip pad must touch the original underside surface')
 def test_palm_origin_and_support_are_generated_from_each_models_own_fit(self):
  for name,model in models().items():
   pose=poses()[model['id']];scale,grip,surface,sight,sign=FITS[name]
   self.assertEqual([0,0,0],fitted_point(name,grip));self.assertEqual([0,0,0],pose['trigger']);self.assertEqual([0,0,0],pose['grip'])
   support=fitted_point(name,surface)
   self.assertEqual(support,pose['support']);self.assertEqual(fitted_point(name,sight),pose['aimSight'])
   self.assertGreater(support[0],0);self.assertEqual(sign*90,model['nativeYaw']);self.assertGreaterEqual(scale,1.35)
 def test_no_shared_flan_contacts_and_complete_gatling_barrels_establish_muzzle_sign(self):
  self.assertEqual(12,len(FITS));self.assertEqual(set(GUN_MODELS),set(FITS))
  self.assertTrue(all(FITS[n][4]==1 for n in FITS))
  meshes=json.loads((A/'blackrazid_weapon_meshes.json').read_text())
  # The body alone extends mostly backwards. The SEPARATE barrels establish
  # the muzzle direction; do not accidentally reverse the complete assembly.
  for barrel in ['gatling/stvol.obj','gatling/stvol5.obj']:
   self.assertGreater(meshes[barrel]['bounds'][5],.70)
  self.assertLess(meshes['gatling/butt.obj']['bounds'][2],-.70)
  self.assertEqual(12,len({tuple(p['support']) for p in poses().values()}))
 def test_runtime_uses_same_matrix_for_gatling_and_every_other_native_assembly(self):
  renderer=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/weapons/NativeWeaponRenderer.java').read_text()
  self.assertIn('matrix=profile.modelMatrix()',renderer);self.assertIn('applyModel(profile)',renderer)
  self.assertIn('applyModel(NativeWeaponVisuals.gun(gun.entry.sourceId))',renderer)
  self.assertNotIn('GlStateManager.translate(.12,.164,-.126)',renderer)
  self.assertEqual(models(),json.loads((A/'blackrazid_weapon_models.json').read_text()))
  self.assertEqual(poses(),json.loads((A/'geometry/blackrazid_weapon_poses.json').read_text()))
 def test_approved_legacy_pose_file_is_byte_exact_unchanged(self):
  self.assertEqual('be26346d52fd6f38a8e430a8bd1780374f36512c4847dcc98a55a38d7b88b381',hashlib.sha256((A/'geometry/gun_poses.json').read_bytes()).hexdigest())
