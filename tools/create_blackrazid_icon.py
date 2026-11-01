#!/usr/bin/env python3
"""PPM-88 has no registered source icon. Render its actual boxes and atlas, not invented artwork."""
import json, math, tempfile
from pathlib import Path
import numpy as np
from import_content import ASSETS
from create_mcmod_icons import render

def main():
    data=json.loads((ASSETS/'geometry/blackrazid/blackrazid_gas_mask.json').read_text())
    out=[]; vertex=1
    def box(values,uv,size,point=(0,0,0),angles=(0,0,0)):
        nonlocal vertex
        x,y,z,w,h,d=values[:6];grow=values[6] if len(values)>6 else 0
        p=np.array([[x-grow,y-grow,z-grow],[x+w+grow,y-grow,z-grow],[x+w+grow,y+h+grow,z-grow],[x-grow,y+h+grow,z-grow],
                    [x-grow,y-grow,z+d+grow],[x+w+grow,y-grow,z+d+grow],[x+w+grow,y+h+grow,z+d+grow],[x-grow,y+h+grow,z+d+grow]],dtype=float)
        ax,ay,az=angles;cx,sx=math.cos(ax),math.sin(ax);cy,sy=math.cos(ay),math.sin(ay);cz,sz=math.cos(az),math.sin(az)
        rot=np.array([[cz,-sz,0],[sz,cz,0],[0,0,1]])@np.array([[cy,0,sy],[0,1,0],[-sy,0,cy]])@np.array([[1,0,0],[0,cx,-sx],[0,sx,cx]])
        p=p@rot.T+np.array(point);u,v=uv
        faces=[([5,1,2,6],[u+d+w,v+d,u+d+w+d,v+d+h]),([0,4,7,3],[u,v+d,u+d,v+d+h]),
               ([5,4,0,1],[u+d,v,u+d+w,v+d]),([2,3,7,6],[u+d+w,v+d,u+d+w+w,v]),
               ([1,0,3,2],[u+d,v+d,u+d+w,v+d+h]),([4,5,6,7],[u+d+w+d,v+d,u+d+w+d+w,v+d+h])]
        for indices,(u1,v1,u2,v2) in faces:
            for i,(tu,tv) in zip(indices,[(u2,v1),(u1,v1),(u1,v2),(u2,v2)]):
                a,b,c=p[i];out.extend([f'v {-c} {-b} {a}',f'vt {tu/size[0]} {1-tv/size[1]}'])
            out.append('f '+' '.join(f'{vertex+i}/{vertex+i}' for i in range(4)));vertex+=4
    # Retain the source ModelBiped(1) head and headwear as well as custom children.
    out.append('g head')
    box([-4,-8,-4,8,8,8,1],[0,0],data['textureSize'])
    box([-4,-8,-4,8,8,8,1.5],[32,0],data['textureSize'])
    for p in data['parts']:
        if 'head' in p['parents'] or 'headwear' in p['parents']:
            for b in p['boxes']:box(b['values'],p['uv'],p['size'],p['point'],p['rotation'])
    with tempfile.TemporaryDirectory() as tmp:
        obj=Path(tmp)/'model.obj';obj.write_text('\n'.join(out)+'\n')
        render(obj,ASSETS/'textures/blackrazid/armor/protivogas.png',{'head'},ASSETS/'textures/blackrazid/icons/gas_mask_generated.png')
if __name__=='__main__':main()
