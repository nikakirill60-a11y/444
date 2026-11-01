"""Independent matrix regressions against BASIC RenderGunStatic/RenderArms.
No Minecraft/OpenGL execution: only neutral camera transforms and sampled poses.
"""
import base64, hashlib, json, math, struct, sys, unittest
from pathlib import Path
import numpy as np
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT/'tools'))
from uboinik_pose import POSE_FILE, source_pose, arm_transform
P=ROOT/'contentpacks/metro_uboinik'

def vec(d):return np.array([d[k] for k in 'xyz'])
def T(v):m=np.eye(4);m[:3,3]=v;return m
def S(v):return np.diag([*([v]*3 if np.isscalar(v) else v),1])
def R(axis,degrees):
    a=math.radians(degrees);c,s=math.cos(a),math.sin(a);m=np.eye(4)
    if axis=='x':m[1:3,1:3]=[[c,-s],[s,c]]
    if axis=='y':m[np.ix_([0,2],[0,2])]=[[c,s],[-s,c]]
    if axis=='z':m[:2,:2]=[[c,-s],[s,c]]
    return m
def xyz_rotation(v):return R('x',v[0])@R('y',v[1])@R('z',v[2])
def Q(q):
    x,y,z,w=q;m=np.eye(4)
    m[:3,:3]=[[1-2*(y*y+z*z),2*(x*y-z*w),2*(x*z+y*w)],
              [2*(x*y+z*w),1-2*(x*x+z*z),2*(y*z-x*w)],
              [2*(x*z-y*w),2*(y*z+x*w),1-2*(x*x+y*y)]]
    return m
def node_matrix(node):return T(node.get('translation',[0,0,0]))@Q(node.get('rotation',[0,0,0,1]))@S(node.get('scale',[1,1,1]))

class PoseConversionTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.pose=source_pose();cls.g=json.loads((P/'assets/modularwarfare/gltf/guns/metro_uboinik.gltf').read_bytes());cls.config=json.loads((P/'guns/render/metro_uboinik.render.json').read_bytes());cls.nodes={n['name']:n for n in cls.g['nodes']}
    def legacy_gun(self):
        p=self.pose;v=vec(p['extra']['translateAll'])*[1,-1,-1]
        return S(p['extra']['modelScale'])@T((np.array([3,5.37,.01])+v)/16)@S(1/16)
    def basic_camera(self,ads):
        p=self.pose;h=vec(p['aim']['rotateHipPosition']);ar=vec(p['aim']['rotateAimPosition']);hp=vec(p['aim']['translateHipPosition'])+[0,.04,-.15];ap=vec(p['aim']['translateAimPosition'])
        angles=[h[0]-(ar[0]+h[0])*ads,46+h[1]-(1+ar[1]+h[1])*ads,1+h[2]-(1+ar[2]+h[2])*ads]
        pos=np.array([-1.3,.834,-1.05])+hp-(np.array([0,-.064,.35])+ap+hp)*ads
        pos[1]-=.25*(1-ads) # approved HIP-only lowering after in-game screenshot
        return T([.56,-.52,-.71999997])@R('y',45)@S(.4)@xyz_rotation(angles)@T(pos)
    def enhanced_camera(self,ads):
        c=self.config;gl=c['global'];a=c['aim'];angles=vec(gl['globalRotate'])*3.14/math.pi
        return (R('y',90)@S(.0001)@R('y',90)@T(vec(gl['globalTranslate']))@S(vec(gl['globalScale']))@R('y',-90)
                @R('y',angles[1])@R('x',angles[0])@R('z',angles[2])
                @xyz_rotation(vec(a['rotateHipPosition']))@xyz_rotation(vec(a['rotateAimPosition'])*ads)
                @T(vec(a['translateHipPosition'])+vec(a['translateAimPosition'])*ads))
    def test_fp_idle_matches_supplied_basic_gun_transform(self):
        actual=self.enhanced_camera(0)@node_matrix(self.nodes['gunModel'])@S(.04)
        np.testing.assert_allclose(actual,self.basic_camera(0)@self.legacy_gun(),atol=2e-12)
    def test_fp_ads_endpoint_matches_supplied_basic_gun_transform(self):
        actual=self.enhanced_camera(1)@node_matrix(self.nodes['gunModel'])@S(.04)
        np.testing.assert_allclose(actual,self.basic_camera(1)@self.legacy_gun(),atol=2e-12)
    def projected_body_fraction(self,camera,fov=70,aspect=16/9):
        g=self.g;p=g['meshes'][self.nodes['gunModel']['mesh']]['primitives'][0]
        a=g['accessors'][p['attributes']['POSITION']];v=g['bufferViews'][a['bufferView']];raw=base64.b64decode(g['buffers'][0]['uri'].split(',',1)[1])
        points=np.frombuffer(raw,dtype='<f4',offset=v['byteOffset'],count=a['count']*3).reshape(-1,3)
        eye=(camera@node_matrix(self.nodes['gunModel'])@np.c_[points,np.ones(len(points))].T).T
        z=-eye[:,2];f=1/math.tan(math.radians(fov)/2)
        return np.mean((z>.01)&(abs(eye[:,0]*f/aspect)<z)&(abs(eye[:,1]*f)<z))
    def test_body_is_in_frustum_without_inheriting_outer_modelview(self):
        # Enhanced starts from identity. Never append ClientRenderHooks outside
        # enhanced_camera: doing so would conceal the actual v2 off-screen bug.
        for ads in [0,.5,1]:
            for fov in [50,70,90]:
                for aspect in [4/3,16/9]:self.assertGreater(self.projected_body_fraction(self.enhanced_camera(ads),fov,aspect),.1)
    def test_old_v2_camera_fails_actual_frustum_regression(self):
        from copy import deepcopy
        correct=self.config;self.config=deepcopy(correct)
        self.config['global']['globalTranslate']={k:0 for k in 'xyz'}
        self.config['global']['globalScale']={k:10000 for k in 'xyz'}
        self.config['global']['globalRotate']['y']=-44*math.pi/3.14
        try:self.assertLess(self.projected_body_fraction(self.enhanced_camera(0)),.01)
        finally:self.config=correct
    def test_lowering_leaves_ads_endpoint_and_other_transforms_unchanged(self):
        from uboinik_pose import render_pose
        base=render_pose(self.pose,hip_lowering=0);new=render_pose(self.pose)
        for key in ['global','thirdPerson','sprint','extra']:self.assertEqual(base[key],new[key])
        self.assertAlmostEqual(base['aim']['translateHipPosition']['y']-.25,new['aim']['translateHipPosition']['y'])
        for axis in 'xyz':
            self.assertAlmostEqual(base['aim']['translateHipPosition'][axis]+base['aim']['translateAimPosition'][axis],new['aim']['translateHipPosition'][axis]+new['aim']['translateAimPosition'][axis])
        self.assertEqual(base['aim']['rotateAimPosition'],new['aim']['rotateAimPosition'])
    def test_third_person_matches_supplied_scale_and_offsets(self):
        c=self.config['thirdPerson']['renderElements']['player'];r=vec(c['rot'])
        actual=T(vec(c['pos']))@S(.1)@S(vec(c['size']))@R('y',-r[1])@R('x',-r[0])@R('z',-r[2])@node_matrix(self.nodes['gunModel'])@S(.04)
        p=self.pose['thirdPerson']
        expected=T([-.06,.38,-.02])@R('y',-90)@R('z',90)@T([.25,0,-.05])@S(p['thirdPersonScale'])@T(vec(p['thirdPersonOffset']))@self.legacy_gun()
        np.testing.assert_allclose(actual,expected,atol=2e-12)
    def basic_arm(self,side,progress):
        a=self.pose['arms'][side+'Arm'];p=vec(a['armPos'])*(1-progress)+vec(a['armReloadPos'])*progress;r=vec(a['armRot'])*(1-progress)+vec(a['armReloadRot'])*progress;pivot=np.array([.225 if side=='left' else -.225,.75,0])
        # Moving arm is rendered inside the weapon matrix, then cancels modelScale.
        origin=self.legacy_gun()@S(16/self.pose['extra']['modelScale']) if side=='left' else np.eye(4)
        return origin@T(p)@T(pivot)@xyz_rotation(r)@T(-pivot)@S(vec(a['armScale']))
    def test_rest_arm_transforms_match_basic_shoulder_pivots(self):
        for side in ['left','right']:
            for slim in ['', 'Slim']:
                actual=node_matrix(self.nodes[side+'Arm'+slim+'Model'])
                np.testing.assert_allclose(actual,self.basic_arm(side,0),atol=2e-12)
    def test_reload_arm_transforms_match_supplied_reload_parameters(self):
        for side in ['left','right']:
            for progress in [0,.25,.5,.75,1]:
                np.testing.assert_allclose(node_matrix(arm_transform(self.pose,side,progress)),self.basic_arm(side,progress),atol=2e-12)
    def test_gun_rounds_flash_and_attachment_anchor_have_same_frame(self):
        expected=node_matrix(self.nodes['gunModel'])
        for name in ['flashModel']+['bulletModel_'+str(i) for i in range(6)]:np.testing.assert_allclose(node_matrix(self.nodes[name]),expected,atol=0)
        self.assertEqual([0,0,0],self.nodes['root']['translation'])
    def test_original_pose_file_hash_is_recorded(self):
        m=json.loads((P/'SOURCE_MANIFEST.json').read_bytes());self.assertEqual(4,m['packRevision']);self.assertEqual(hashlib.sha256(POSE_FILE.read_bytes()).hexdigest(),m['poseSource']['sha256'])
    def test_supplied_recoil_and_sprint_settings_are_preserved(self):
        self.assertEqual(self.pose['sprint'],self.config['sprint'])
        for key in ['modelRecoilBackwards','modelRecoilUpwards','modelRecoilShake']:self.assertEqual(self.pose['extra'][key],self.config['extra'][key])
    def test_arms_are_animated_with_supplied_rest_and_reload_poses(self):
        anim=self.g['animations'][0];buffer=base64.b64decode(self.g['buffers'][0]['uri'].split(',',1)[1])
        def values(index):
            a=self.g['accessors'][index];v=self.g['bufferViews'][a['bufferView']];size={'SCALAR':1,'VEC3':3,'VEC4':4}[a['type']]
            raw=buffer[v['byteOffset']:v['byteOffset']+v['byteLength']];out=struct.unpack('<'+'f'*a['count']*size,raw);return np.array(out).reshape((-1,size))
        pre=self.config['animations']['preLoad'];post=self.config['animations']['postLoad']
        for channel in anim['channels']:
            target=channel['target'];name=self.g['nodes'][target['node']]['name']
            if 'Arm' not in name:continue
            sampler=anim['samplers'][channel['sampler']];times=values(sampler['input'])[:,0];out=values(sampler['output']);side='left' if name.startswith('left') else 'right'
            for frame,progress in [(0,0),(pre['endTime'],1),(post['endTime'],0)]:
                i=int(np.argmin(abs(times-frame/24)));expected=arm_transform(self.pose,side,progress)[target['path']]
                np.testing.assert_allclose(out[i],expected,atol=1e-7)

if __name__=='__main__':unittest.main()
