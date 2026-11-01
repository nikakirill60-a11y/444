"""Convert the supplied BASIC render.ubo.json to Enhanced coordinates.

BASIC retains ClientRenderHooks' idle T(.56,-.52,-.71999997) Ry(45) S(.4).
Enhanced explicitly loads identity, discarding that outer camera transform.
Restore it through globalTranslate/Scale/Rotate, before porting BASIC's
Ry(46)Rz(1) and hip/ADS translations.
BASIC additionally uses S(modelScale)T(Blender offset + translateAll)S(1/16).
Keep that weapon transform on each gun/round node so attachment bindings inherit it.
Arm transforms reproduce RenderArms' shoulder pivot and standard Minecraft boxes;
leftHandAmmo's moving arm additionally inherits the translated weapon origin.
"""
import json, math
from pathlib import Path
import numpy as np
POSE_FILE=Path(__file__).resolve().parent/'uboinik-reference/render.ubo.json'

def source_pose():return json.loads(POSE_FILE.read_bytes())
def vec(v):return np.array([v[k] for k in 'xyz'],dtype=float)
def xyz(v):return dict(zip('xyz',map(float,v)))
def rotation(angles):
    x,y,z=np.radians(angles);cx,sx=math.cos(x),math.sin(x);cy,sy=math.cos(y),math.sin(y);cz,sz=math.cos(z),math.sin(z)
    return np.array([[1,0,0],[0,cx,-sx],[0,sx,cx]])@np.array([[cy,0,sy],[0,1,0],[-sy,0,cy]])@np.array([[cz,-sz,0],[sz,cz,0],[0,0,1]])
def quaternion(m):
    # Robust matrix -> x,y,z,w for any rigid rotation.
    k=np.array([[m[0,0]-m[1,1]-m[2,2],m[1,0]+m[0,1],m[2,0]+m[0,2],m[2,1]-m[1,2]],
                [m[1,0]+m[0,1],m[1,1]-m[0,0]-m[2,2],m[2,1]+m[1,2],m[0,2]-m[2,0]],
                [m[2,0]+m[0,2],m[2,1]+m[1,2],m[2,2]-m[0,0]-m[1,1],m[1,0]-m[0,1]],
                [m[2,1]-m[1,2],m[0,2]-m[2,0],m[1,0]-m[0,1],m.trace()]])/3
    values,vectors=np.linalg.eigh(k);q=vectors[:,np.argmax(values)]
    if q[3]<0:q=-q
    return list(map(float,q))
def euler_xyz(m):
    return np.degrees([math.atan2(-m[1,2],m[2,2]),math.asin(float(np.clip(m[0,2],-1,1))),math.atan2(-m[0,1],m[0,0])])
def euler_yxz(m):
    # Return x,y,z, despite the multiplication order Ry Rx Rz.
    return np.degrees([math.asin(float(np.clip(-m[1,2],-1,1))),math.atan2(m[0,2],m[2,2]),math.atan2(m[1,0],m[1,1])])
def gun_transform(pose,export_scale=.04):
    size=pose['extra']['modelScale'];t=vec(pose['extra']['translateAll'])
    offset=size*(np.array([3,5.37,.01])+t*np.array([1,-1,-1]))/16
    return {'translation':list(map(float,offset)),'scale':[size/16/export_scale]*3}

def legacy_camera(pose,ads=False):
    a=pose['aim'];h=vec(a['rotateHipPosition']);r=vec(a['rotateAimPosition'])
    angle=np.array([-r[0],45-r[1],-r[2]]) if ads else h+np.array([0,46,1])
    ht=vec(a['translateHipPosition']);at=vec(a['translateAimPosition'])
    t=np.array([-1.3-at[0],.898-at[1],-1.4-at[2]]) if ads else ht+np.array([-1.3,.874,-1.2])
    return rotation(angle),t

def arm_transform(pose,side,reload=0):
    a=pose['arms'][side+'Arm'];r=vec(a['armRot'])*(1-reload)+vec(a['armReloadRot'])*reload
    p=vec(a['armPos'])*(1-reload)+vec(a['armReloadPos'])*reload
    m=rotation(r);pivot=np.array([.225 if side=='left' else -.225,.75,0])
    moving=(side=='left')==pose['arms']['leftHandAmmo']
    offset=np.array(gun_transform(pose)['translation']) if moving else np.zeros(3)
    return {'translation':list(map(float,offset+p+pivot-m@pivot)),'rotation':quaternion(m),'scale':list(map(float,vec(a['armScale'])))}

def render_pose(pose):
    hip,ht=legacy_camera(pose);ads,at=legacy_camera(pose,True)
    # Actual JAR globalRotate uses 3.14, but aim rotations use Math.PI.
    angles=euler_yxz(rotation([0,-90,0])@rotation([0,45,0])@hip)*math.pi/3.14
    # Prefix at this point is Ry(180) S(1/10000), BEFORE globalScale.
    # Thus camera translation must be expressed in that frame, not in metres.
    camera_translate=np.array([.56,-.52,-.71999997])
    global_translate=rotation([0,-180,0])@camera_translate*10000
    global_config={'globalTranslate':xyz(global_translate),'globalScale':xyz([4000]*3),'globalRotate':xyz(angles)}
    aim={'rotateHipPosition':xyz([0,0,0]),'translateHipPosition':xyz(ht),
         'rotateAimPosition':xyz(euler_xyz(hip.T@ads)),'translateAimPosition':xyz(at-ht)}
    third={k:{'pos':xyz([0,0,0]),'rot':xyz([0,0,0]),'size':xyz([3]*3)} for k in ['player','player_offhand','itemloot','itemframe']}
    # BASIC LayerHeldGun adds a hand translation; its EQUIPPED branch then applies
    # Ry(-90) Rz(90) T(.25,0,-.05) S(thirdPersonScale) T(thirdPersonOffset).
    size=pose['thirdPerson']['thirdPersonScale'];r=rotation([0,-90,0])@rotation([0,0,90])
    pos=np.array([-.06,.38,-.02])+r@np.array([.25,0,-.05])+size*r@vec(pose['thirdPerson']['thirdPersonOffset'])
    for key in ['player','player_offhand']:
        third[key]={'pos':xyz(pos),'rot':xyz([0,90,-90]),'size':xyz([10*size]*3)}
    third['itemloot']={'pos':xyz([-.0832,.025,-.0004]),'rot':xyz([0,0,0]),'size':xyz([4]*3)}
    third['itemframe']['pos']=xyz([-.0624,-.0562,0])
    extra={k:pose['extra'][k] for k in ['modelRecoilBackwards','modelRecoilUpwards','modelRecoilShake']}
    # Sprint is a separate Enhanced action with a different interpolation; preserve
    # supplied settings, without pretending to port the old shotgun state machine.
    return {'global':global_config,'aim':aim,'thirdPerson':{'renderElements':third},'sprint':pose['sprint'],'extra':extra}
