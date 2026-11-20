"""Split the shared native atlas into independent item PNGs; preserve geometry and timelines.
MWF binds one skin per item. A multi-material item's own PNG holds only that item's
materials (not the whole arsenal). Primary original PNGs are restored at up to 2048px.
"""
import base64, collections, copy, io, json, math, subprocess, tarfile, zipfile
from pathlib import Path
import numpy as np
from PIL import Image
from update_mwf_v10 import accessor, js
from integrate_mwf_3d import png
P='assets/modularwarfare/'

def originals(root,work):
    work.mkdir(parents=True,exist_ok=True)
    legacy=work/'legacy'
    if not legacy.exists():
        raw=subprocess.check_output(['git','archive','10a6aa2','tools/blackrazid-reference/assets/weapons'],cwd=root)
        with tarfile.open(fileobj=io.BytesIO(raw)) as t:
            for e in t.getmembers():
                assert not Path(e.name).is_absolute() and '..' not in Path(e.name).parts and not e.issym() and not e.islnk()
            t.extractall(legacy)
    images={p.relative_to(legacy/'tools/blackrazid-reference/assets/weapons').as_posix():p.read_bytes() for p in legacy.rglob('*.png')}
    with zipfile.ZipFile(root/'tools/mwf-full-pack/native-sources.zip') as z:
        images.update({n:z.read(n) for n in z.namelist() if n.lower().endswith('.png')})
    return images

class Splitter:
    def __init__(self,atlas,images):
        self.atlas=atlas.convert('RGBA');self.tiles={};self.originals=images
        for name,raw in sorted(images.items()):
            im=Image.open(io.BytesIO(raw)).convert('RGBA');im.thumbnail((508,508),Image.Resampling.LANCZOS)
            sample=np.asarray(im.resize((32,32)),dtype=float)
            for y in range(4,atlas.height,512):
                for x in range(4,atlas.width,512):
                    key=(x//512,y//512)
                    if key in self.tiles:continue
                    crop=atlas.crop((x,y,x+im.width,y+im.height)).convert('RGBA')
                    err=np.abs(np.asarray(crop.resize((32,32)),dtype=float)-sample).mean()
                    if err<.45:
                        # Confirm the full image, allowing tiny Pillow-version interpolation differences.
                        err=np.abs(np.asarray(crop,dtype=float)-np.asarray(im,dtype=float)).mean()
                        if err<.45:self.tiles[key]=(name,Image.open(io.BytesIO(raw)).convert('RGBA'),im.size)
        for row in range(atlas.height//512):
            for col in range(atlas.width//512):
                key=(col,row)
                if key in self.tiles:continue
                x=col*512+4;y=row*512+4
                crop=atlas.crop((x,y,x+508,y+508)).convert('RGBA')
                # Solid material tiles occupy 8x8; repeat-baked tiles occupy 508x508.
                corner=crop.crop((0,0,8,8));size=(8,8) if len(corner.getcolors(65) or [])==1 and crop.getpixel((32,32))[3]==0 else (508,508)
                self.tiles[key]=('preserved_tile_%d_%d'%key,atlas.crop((x,y,x+size[0],y+size[1])),size)

    def key(self,uv,gltf):
        x=float(uv[0])*self.atlas.width;y=(float(uv[1]) if gltf else 1-float(uv[1]))*self.atlas.height
        return (min(self.atlas.width//512-1,max(0,int((x-.001)//512))),min(self.atlas.height//512-1,max(0,int((y-.001)//512))))

    def sheet(self,arrays,gltf,army=None,preferred=None):
        weights=collections.Counter(self.key(uv,gltf) for arr in arrays for uv in arr)
        primary=weights.most_common(1)[0][0]
        if preferred:
            matching=[key for key in weights if self.tiles[key][0].lower().startswith(preferred+'/')]
            if matching:primary=max(matching,key=lambda k:weights[k])
        tiles={};used={}
        for key in sorted(weights):
            name,im,oldsize=self.tiles[key];im=im.copy()
            if army is not None and name.lower()=='ak/wpn34_ak.2048.png':im=army.copy();name='Army Kalash/wpn_aka.1024.png'
            im.thumbnail((2048,2048) if key==primary else (512,512),Image.Resampling.LANCZOS)
            tiles[key]=im;used[key]=(name,oldsize)
        # Shelf pack, compact power-of-two dimensions; never allocate the old whole-arsenal atlas.
        area=sum((im.width+8)*(im.height+8) for im in tiles.values())
        width=2**math.ceil(math.log2(max(max(im.width+8 for im in tiles.values()),math.sqrt(area))))
        width=min(4096,width);pos={};x=y=rowh=0
        for key,im in sorted(tiles.items(),key=lambda kv:(-kv[1].height,-kv[1].width,kv[0])):
            if x+im.width+8>width:x=0;y+=rowh;rowh=0
            pos[key]=(x+4,y+4);x+=im.width+8;rowh=max(rowh,im.height+8)
        height=2**math.ceil(math.log2(max(1,y+rowh)))
        out=Image.new('RGBA',(width,height))
        for key,im in tiles.items():
            x,y=pos[key];out.paste(im,(x,y))
            out.paste(im.crop((0,0,1,im.height)).resize((4,im.height)),(x-4,y));out.paste(im.crop((im.width-1,0,im.width,im.height)).resize((4,im.height)),(x+im.width,y))
            out.paste(im.crop((0,0,im.width,1)).resize((im.width,4)),(x,y-4));out.paste(im.crop((0,im.height-1,im.width,im.height)).resize((im.width,4)),(x,y+im.height))
        def remap(arr):
            result=[]
            for uv in arr:
                key=self.key(uv,gltf);name,(ow,oh)=used[key];nx,ny=pos[key];im=tiles[key]
                u=(float(uv[0])*self.atlas.width-(key[0]*512+4))/ow
                v=((float(uv[1]) if gltf else 1-float(uv[1]))*self.atlas.height-(key[1]*512+4))/oh
                a=(nx+u*im.width)/width;b=(ny+v*im.height)/height
                result.append((a,b if gltf else 1-b))
            return np.array(result,dtype='<f4')
        report={'size':[width,height],'materials':[{'source':used[k][0],'size':list(tiles[k].size)} for k in sorted(used)]}
        return png(out),remap,report

def replace_uv(doc,index,values):
    a=doc['accessors'][index];v=doc['bufferViews'][a['bufferView']];b=doc['buffers'][v['buffer']]
    raw=bytearray(base64.b64decode(b['uri'].split(',',1)[1]));start=v.get('byteOffset',0)+a.get('byteOffset',0)
    arr=np.ndarray((a['count'],2),dtype='<f4',buffer=raw,offset=start,strides=(v.get('byteStride',8),4));arr[:]=values
    b['uri']='data:application/octet-stream;base64,'+base64.b64encode(raw).decode()
    a['min']=values.min(axis=0).astype(float).tolist();a['max']=values.max(axis=0).astype(float).tolist()

def split(files,root,work,army):
    splitter=Splitter(Image.open(io.BytesIO(files[P+'skins/guns/metro_native_atlas.png'])),originals(root,work));report={}
    original=dict(files)
    for cat in ('guns','ammo','attachments'):
        for name in sorted(original):
            if not name.startswith(cat+'/') or name.count('/')!=1 or not name.endswith('.json'):continue
            g=json.loads(original[name]);id=g['internalName']
            if id!='metro_army_ak' and not any(s['skinAsset']=='metro_native_atlas' for s in g.get('modelSkins',[])):continue
            rpath=cat+'/render/'+id+'.render.json';r=json.loads(original[rpath]);filename=r['modelFileName']
            if filename.endswith('.gltf'):
                path=P+'gltf/'+cat+'/'+filename;doc=json.loads(original[path]);indices=set()
                for node in doc['nodes']:
                    if 'mesh' not in node or 'Arm' in node['name']:continue
                    for prim in doc['meshes'][node['mesh']]['primitives']:indices.add(prim['attributes']['TEXCOORD_0'])
                arrays={a:accessor(doc,a) for a in sorted(indices)}
                image,remap,info=splitter.sheet(arrays.values(),True,army if id=='metro_army_ak' else None,{'army_ak':'ak','ak2012':'2012'}.get(id.removeprefix('metro_'),id.removeprefix('metro_')))
                for a,uv in arrays.items():replace_uv(doc,a,remap(uv))
                files[path]=js(doc)
            else:
                path='obj/'+cat+'/'+filename
                # Attachment OBJ folder uses the singular spelling in this pack.
                if path not in original:path='obj/'+('attachment' if cat=='attachments' else cat)+'/'+filename
                lines=original[path].decode().splitlines();coords=[list(map(float,l.split()[1:3])) for l in lines if l.startswith('vt ')]
                image,remap,info=splitter.sheet([coords],False);it=iter(remap(coords))
                for i,line in enumerate(lines):
                    if line.startswith('vt '):u,v=next(it);lines[i]=f'vt {u:.9f} {v:.9f}'
                newfile=id+'.obj';newpath=str(Path(path).with_name(newfile));files[newpath]=('\n'.join(lines)+'\n').encode();r['modelFileName']=newfile;files[rpath]=js(r)
            g['modelSkins']=[dict(internalName=id,skinAsset=id,displayName='Оригинал')];files[name]=js(g);files[P+'skins/'+cat+'/'+id+'.png']=image;report[id]=info
    for path in list(files):
        if path.endswith('/metro_native_atlas.png'):del files[path]
    return report
