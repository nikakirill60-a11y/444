"""Actual metro4/v8 -> metro5/v9 artifacts. No live Minecraft claims."""
import base64
import io
import json
import math
import os
from pathlib import Path
import unittest
import zipfile
import numpy as np

ROOT=Path(__file__).resolve().parents[1]
P='assets/modularwarfare/'


def read(path):
    with zipfile.ZipFile(path) as z:
        assert z.testzip() is None
        return {n:z.read(n) for n in z.namelist() if not n.endswith('/')}


def trs(node):
    m=np.eye(4);x,y,z,w=node.get('rotation',[0,0,0,1]);m[:3,:3]=np.array([[1-2*(y*y+z*z),2*(x*y-z*w),2*(x*z+y*w)],[2*(x*y+z*w),1-2*(x*x+z*z),2*(y*z-x*w)],[2*(x*z-y*w),2*(y*z+x*w),1-2*(x*x+y*y)]])@np.diag(node.get('scale',[1,1,1]));m[:3,3]=node.get('translation',[0,0,0]);return m


def world(doc,index):
    parent=next((i for i,n in enumerate(doc['nodes']) if index in n.get('children',[])),None)
    return (world(doc,parent) if parent is not None else np.eye(4))@trs(doc['nodes'][index])


class MotionUpdate(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.a=read(os.environ.get('MWF_V8',ROOT/'downloads/Metro_Full_Arsenal_MWF_fix1_v8_3d.zip'))
        cls.b=read(ROOT/'downloads/Metro_Full_Arsenal_MWF_fix1_v9_motion.zip')
        cls.ja=read(os.environ.get('MWF_METRO4',ROOT/'downloads/modularwarfare-shining-2024.2.4.6f-fix1-metro4.jar'))
        cls.jb=read(ROOT/'downloads/modularwarfare-shining-2024.2.4.6f-fix1-metro5.jar')

    def test_pack_only_targets_change(self):
        allowed={'FULL_PACK_MANIFEST.json','guns/metro_gatling.json',*[P+'gltf/guns/metro_'+g+'.gltf' for g in ('gatling','podonok','revolver')]}
        self.assertEqual({n for n in self.a if self.a[n]!=self.b.get(n)},allowed)
        self.assertEqual(set(self.b)-set(self.a),{P+'metro_motion.json','MOTION_UPDATE_MANIFEST.json','README_V9_RU.txt'})
        self.assertEqual(len([n for n in self.b if n.startswith('guns/') and n.count('/')==1]),25)

    def test_no_stock_rest_world_matrices_and_old_animation_buffers_preserved(self):
        for gun in ('gatling','podonok','revolver'):
            path=P+'gltf/guns/metro_'+gun+'.gltf';a,b=(json.loads(d[path]) for d in (self.a,self.b))
            for key in ('buffers','bufferViews','accessors','animations','meshes'):
                self.assertEqual(a[key],b[key],(gun,key))
            for i,n in enumerate(a['nodes']):
                if 'mesh' in n:
                    self.assertTrue(np.allclose(world(a,i),world(b,i),atol=1e-10),(gun,n['name']))
                if 'Arm' in n['name']:
                    self.assertEqual(n,b['nodes'][i],(gun,n['name']))
            self.assertEqual(len({n['name'] for n in b['nodes']}),len(b['nodes']))
            for i,n in enumerate(b['nodes']):
                for child in n.get('children',[]):self.assertTrue(0<=child<len(b['nodes']) and child!=i)

    def test_only_barrel_meshes_are_children_of_rotor_at_native_pivot(self):
        d=json.loads(self.b[P+'gltf/guns/metro_gatling.gltf']);rotor=next(n for n in d['nodes'] if n['name']=='metroMotionRotor');anchor=next(n for n in d['nodes'] if n['name']=='ammoModel')
        self.assertEqual(rotor['translation'],anchor['translation'])
        self.assertEqual([d['nodes'][i]['name'] for i in rotor['children']],['native_3','mod_GatlingBarrel_0'])
        # Old attachment hide/show IDs still point to the SAME mesh nodes.
        self.assertEqual(self.a['guns/render/metro_gatling.render.json'],self.b['guns/render/metro_gatling.render.json'])

    def test_stock_profiles_are_independent_and_do_not_replace_frozen_revolver_pose(self):
        profiles=json.loads(self.b[P+'metro_motion.json'])
        for gun in ('podonok','revolver'):
            d=json.loads(self.b[P+'gltf/guns/metro_'+gun+'.gltf'])
            for side in ('left','right'):
                name='metroMotion'+gun.capitalize()+side.capitalize();n=next(n for n in d['nodes'] if n['name']==name);p=profiles[name]
                self.assertEqual(p['gun'],'metro_'+gun);self.assertEqual(p,n['extras']['metroMotion'])
                self.assertEqual({d['nodes'][i]['name'] for i in n['children']},{side+'ArmModel',side+'ArmSlimModel'})
                matrix=np.array(p['matrix']).reshape(4,4).T
                self.assertTrue(np.isfinite(matrix).all());self.assertFalse(np.allclose(matrix,np.eye(4)))
                self.assertAlmostEqual(np.linalg.det(matrix[:3,:3]),1,places=6)
                self.assertNotIn('matrix',n);self.assertNotIn('translation',n)
            self.assertEqual(self.a['guns/metro_'+gun+'.json'],self.b['guns/metro_'+gun+'.json'])
        self.assertNotEqual(profiles['metroMotionPodonokLeft']['matrix'],profiles['metroMotionRevolverLeft']['matrix'])

    def test_original_jar_classes_preserved_except_explicit_seven_hooks(self):
        changed={n for n in self.ja if self.ja[n]!=self.jb.get(n)}
        expected={
            'com/modularwarfare/ModularWarfare.class',
            'com/modularwarfare/common/network/PacketExpShot$1.class',
            'com/modularwarfare/common/network/PacketExpGunFire$1.class',
            'com/modularwarfare/common/network/PacketGunReloadEnhancedStop.class',
            'com/modularwarfare/common/guns/manager/ShotManager.class',
            'com/modularwarfare/client/fpp/enhanced/renderers/RenderGunEnhanced.class',
            'mchhui/hegltf/GltfRenderModel.class',
        }
        self.assertEqual(changed,expected)
        for n in set(self.jb)-set(self.ja):
            self.assertTrue(n.startswith('ru/metrocraft/mwf/') or n==P+'metro_motion.json',n)
            self.assertNotIn('Test',n);self.assertNotIn('PatchMotion',n)
            if n.endswith('.class'):self.assertEqual(int.from_bytes(self.jb[n][6:8],'big'),52,n)
        for n in self.ja:
            if n.startswith('ru/metrocraft/mwf/'):self.assertEqual(self.ja[n],self.jb[n],n)
        self.assertIn(b'metro5',self.jb['com/modularwarfare/ModularWarfare.class'])

    def test_network_byte_abi_and_profiles_are_identical_in_jar_and_pack(self):
        self.assertEqual(self.jb[P+'metro_motion.json'],self.b[P+'metro_motion.json'])
        c=self.jb['ru/metrocraft/mwf/PacketMetroRotor.class']
        for name in (b'writeInt',b'readInt',b'writeByte',b'readByte'):self.assertIn(name,c)
        self.assertIn(b'()B',c);self.assertNotIn(b'readUnsignedByte',c)
        r=json.loads(self.b['MOTION_UPDATE_MANIFEST.json']);self.assertFalse(r['minecraftTested'])
        import hashlib
        raw=(ROOT/'downloads/modularwarfare-shining-2024.2.4.6f-fix1-metro5.jar').read_bytes()
        self.assertEqual(r['requiresJarSha256'],hashlib.sha256(raw).hexdigest())
        f=json.loads(self.b['FULL_PACK_MANIFEST.json']);a=json.loads(self.a['FULL_PACK_MANIFEST.json'])
        for k in ('implementedMechanics','mechanicsAudio','archive3dUpdate'):self.assertEqual(a[k],f[k],k)


if __name__=='__main__':unittest.main()
