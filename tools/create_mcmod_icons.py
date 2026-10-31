#!/usr/bin/env python3
"""Render separate equipment icons from actual OBJ groups and atlases, no invented helmet artwork.
Optional build tool: pip install -r tools/requirements-mcmod-icons.txt; not a game dependency.
"""
import hashlib
import json
import math
from pathlib import Path
import numpy as np
from PIL import Image
from import_mcmod import SOURCE, decode_obj
from import_content import ASSETS, ROOT, write_json


def triangles(path, groups):
    positions, uv, faces, group = [], [], [], None
    def index(text, count):
        n = int(text)
        return n-1 if n>0 else count+n
    for line in decode_obj(path.read_bytes())[0].splitlines():
        p = line.split('#',1)[0].split()
        if not p: continue
        if p[0] == 'v': positions.append([float(x) for x in p[1:4]])
        elif p[0] == 'vt': uv.append([float(x) for x in p[1:3]])
        elif p[0] in ('o','g'): group=p[1]
        elif p[0]=='f' and group in groups:
            for i in range(2,len(p)-1):
                points, tex = [], []
                for ref in [p[1],p[i],p[i+1]]:
                    v=ref.split('/')
                    points.append(positions[index(v[0],len(positions))])
                    tex.append(uv[index(v[1],len(uv))] if len(v)>1 and v[1] else [0,0])
                faces.append((points,tex))
    if not faces: raise ValueError('No icon geometry: '+str(path)+' '+str(groups))
    return faces


def render(obj, atlas, groups, output):
    faces=triangles(obj,groups)
    # Match worn orientation: original model x is forward, y is up, z is sideways.
    yaw,pitch=math.radians(-25),math.radians(12)
    rotate=np.array([[0,0,-1],[0,1,0],[1,0,0]],dtype=float)
    camera=np.array([[math.cos(yaw),0,-math.sin(yaw)],
                     [-math.sin(yaw)*math.sin(pitch),math.cos(pitch),-math.cos(yaw)*math.sin(pitch)],
                     [math.sin(yaw)*math.cos(pitch),math.sin(pitch),math.cos(yaw)*math.cos(pitch)]]) @ rotate
    xyz=np.array([points for points,_ in faces],dtype=float) @ camera.T
    low=xyz[:,:,:2].min(axis=(0,1)); high=xyz[:,:,:2].max(axis=(0,1))
    center=(low+high)/2; size=256
    scale=(size-32)/max(high-low)
    screen=xyz.copy(); screen[:,:,0]=(xyz[:,:,0]-center[0])*scale+size/2
    screen[:,:,1]=-(xyz[:,:,1]-center[1])*scale+size/2
    texture=np.array(Image.open(atlas).convert('RGBA'))
    image=np.zeros((size,size,4),dtype=np.uint8); depth=np.full((size,size),-np.inf)
    light=np.array([-.25,.6,1.]); light/=np.linalg.norm(light)
    for (points,uv), projected,view in zip(faces,screen,xyz):
        a,b,c=projected
        x0=max(0,int(math.floor(min(projected[:,0])))); x1=min(size-1,int(math.ceil(max(projected[:,0]))))
        y0=max(0,int(math.floor(min(projected[:,1])))); y1=min(size-1,int(math.ceil(max(projected[:,1]))))
        denominator=(b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1])
        if abs(denominator)<1e-10 or x0>x1 or y0>y1: continue
        y,x=np.mgrid[y0:y1+1,x0:x1+1]; x=x+.5; y=y+.5
        wa=((b[1]-c[1])*(x-c[0])+(c[0]-b[0])*(y-c[1]))/denominator
        wb=((c[1]-a[1])*(x-c[0])+(a[0]-c[0])*(y-c[1]))/denominator
        wc=1-wa-wb; z=wa*a[2]+wb*b[2]+wc*c[2]
        t=np.array(uv); u=wa*t[0,0]+wb*t[1,0]+wc*t[2,0]; v=wa*t[0,1]+wb*t[1,1]+wc*t[2,1]
        px=np.floor(np.mod(u,1)*texture.shape[1]).astype(int) % texture.shape[1]
        py=np.floor(np.mod(1-v,1)*texture.shape[0]).astype(int) % texture.shape[0]
        color=texture[py,px].copy()
        normal=np.cross(view[1]-view[0],view[2]-view[0]); length=np.linalg.norm(normal)
        shade=.72+.28*max(0,float(np.dot(normal/length,light))) if length else 1
        color[:,:,:3]=np.clip(color[:,:,:3].astype(float)*shade,0,255).astype(np.uint8)
        old=depth[y0:y1+1,x0:x1+1]
        mask=(wa>=-1e-8)&(wb>=-1e-8)&(wc>=-1e-8)&(z>=old)&(color[:,:,3]>0)
        old[mask]=z[mask]; image[y0:y1+1,x0:x1+1][mask]=color[mask]
    result=Image.fromarray(image).resize((128,128),Image.Resampling.LANCZOS)
    if result.getbbox() is None: raise ValueError('Empty icon '+str(output))
    output.parent.mkdir(parents=True,exist_ok=True); result.save(output,compress_level=9)


def main():
    entries=json.loads((ASSETS/'mcmod_armor.json').read_text()); records={}
    for entry in entries:
        if entry['mode']=='whole': continue
        model=entry.get('visualModel',entry['model'])
        obj=ASSETS/('models/mcmod/'+model+'/model.obj'); atlas=ASSETS/('textures/mcmod/armor/'+model+'.png')
        groups=['head'] if entry['mode']=='helmet' else ['body','leftarm','rightarm']
        output=ASSETS/('textures/mcmod/armor_icons/'+entry['id']+'.png')
        render(obj,atlas,groups,output)
        records[entry['id']]={'model':model,'groups':groups,'width':128,'height':128,
            'objSha256':hashlib.sha256(obj.read_bytes()).hexdigest(), 'atlasSha256':hashlib.sha256(atlas.read_bytes()).hexdigest(),
            'iconSha256':hashlib.sha256(output.read_bytes()).hexdigest()}
    write_json(ROOT/'docs/mcmod-icons.json',{'method':'Textured OBJ group render, not original supplied inventory icons','icons':records})
    print('Rendered',len(records),'separate helmet/vest icons')

if __name__=='__main__': main()
