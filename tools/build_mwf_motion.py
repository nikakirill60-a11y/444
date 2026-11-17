#!/usr/bin/env python3
"""Incremental metro4/v8 -> metro5/v9: Gatling rotor and stock-dependent hand poses.
Requires JDK 17+ (javac), NumPy, Pillow. No Forge/upstream downloads or facade shipping.
"""
import argparse
import base64
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import shutil
import subprocess
import zipfile
import numpy as np
from uboinik_pose import rotation

ROOT=Path(__file__).resolve().parents[1]
SOURCE=ROOT/'tools/mwf-motion'
SUPPORT=ROOT/'tools/mwf-mechanics'
BASE_JAR='dc78d8251b98d82006489432d09fb98486df888b779aaf675a62987bc5160823'
BASE_PACK='e6b1aa6c10bbb3059db061167d12e12df2f76b3deb378defe6757da9f2a294f5'
JAR='modularwarfare-shining-2024.2.4.6f-fix1-metro5.jar'
PACK='Metro_Full_Arsenal_MWF_fix1_v9_motion.zip'
EXPORTS=[f'--add-exports=java.base/jdk.internal.org.objectweb.asm{s}=ALL-UNNAMED' for s in ('','.tree','.tree.analysis')]
P='assets/modularwarfare/'


def sha(b):return hashlib.sha256(b).hexdigest()
def js(v):return (json.dumps(v,ensure_ascii=False,indent=2,allow_nan=False)+'\n').encode()
def run(args):
    p=subprocess.run(list(map(str,args)),stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    print(p.stdout,end='',flush=True)
    if p.returncode:raise subprocess.CalledProcessError(p.returncode,args,p.stdout)
    return p.stdout


def profiles(files):
    result={'metroMotionRotor':{'gun':'metro_gatling','rotor':True}}
    # Stock poses are authored deltas in the existing tuned MWF frame, not
    # wholesale imports of the donor's incompatible camera/arm scale.
    authored={
        'podonok':{'attachment':'metro_mod_podonok_butt','left':([.10,-.025,0],[0,-10,-10]),'right':([.02,-.005,0],[0,0,4])},
        'revolver':{'attachment':'metro_mod_butt','left':([.075,-.02,0],[0,-14,-8]),'right':([.015,-.005,0],[0,0,3])},
    }
    for gun,config in authored.items():
        doc=json.loads(files[P+'gltf/guns/metro_'+gun+'.gltf'])
        for side in ('left','right'):
            node=next(n for n in doc['nodes'] if n['name']==side+'ArmModel')
            x,y,z,w=node['rotation']
            q=np.array([[1-2*(y*y+z*z),2*(x*y-z*w),2*(x*z+y*w)],[2*(x*y+z*w),1-2*(x*x+z*z),2*(y*z-x*w)],[2*(x*z-y*w),2*(y*z+x*w),1-2*(x*x+y*y)]])
            # Rotate the forearm about its existing wrist, not the node origin.
            # The Minecraft arm box ends at y=.75; keeping that grip point stable
            # avoids raising a blocky palm over the barrel when a stock is fitted.
            wrist=np.array([.375 if side=='left' else -.375,.75,0])
            pivot=np.array(node['translation'])+q@(np.array(node['scale'])*wrist)
            delta,angles=config[side];r=rotation(angles)
            m=np.eye(4);m[:3,:3]=r;m[:3,3]=pivot+delta-r@pivot
            result['metroMotion'+gun.capitalize()+side.capitalize()]={'gun':'metro_'+gun,'attachment':config['attachment'],'matrix':m.T.flatten().tolist()}
    return result


def wrap(doc,children,name,trs,profile):
    index=len(doc['nodes'])
    for child in children:
        for n in doc['nodes']:
            if child in n.get('children',[]):n['children'].remove(child)
    doc['nodes'].append({'name':name,'children':children,**trs,'extras':{'metroMotion':profile}})
    doc['nodes'][0]['children'].append(index)


def patch_pack(source,output,jar,profile):
    with zipfile.ZipFile(source) as z:files={n:z.read(n) for n in z.namelist()}
    report={'basePackSha256':BASE_PACK,'baseJarSha256':BASE_JAR,'requiresJarSha256':sha(jar.read_bytes()),'minecraftTested':False,'profiles':profile,
            'rules':{'spinupTicks':20,'motorSpinupTicks':10,'spindownTicks':30,'minimumFiringSpeed':.25,'clientMinimumSpeed':.30,'normalRpm':400,'fiveBarrelRpm':600,'fullSpeedDegreesPerSecond':1200,'triggerHeartbeatTicks':4,'triggerExpiryTicks':10},
            'poseBasis':'Authored stock deltas in preserved v8 MWF frame. No-stock poses/old animation buffers unchanged.',
            'limitations':['No live Minecraft/Forge/GL verification. Not a claim of exact Metro timing or balance.','Donor mechanical Air reservoir/hand-cranking/recharge sound is not newly implemented.','Stock hand offsets are applied to existing animation samples; there is no new reload sequence.']}
    path=P+'gltf/guns/metro_gatling.gltf';doc=json.loads(files[path]);nodes=doc['nodes']
    body=next(n for n in nodes if n['name']=='gunModel');anchor=next(n for n in nodes if n['name']=='ammoModel')
    shift=(np.array(anchor['translation'])-body['translation'])/body['scale'][0]
    # Native renderer rotates stvol/stvol5 about the original Z origin. The
    # exporter baked Ry(90), turning that axis into X. Retain the exact source pivot.
    children=[i for i,n in enumerate(nodes) if n['name'] in ('native_3','mod_GatlingBarrel_0')]
    assert len(children)==2
    for i in children:
        assert nodes[i]['translation']==body['translation'] and nodes[i]['scale']==body['scale']
        nodes[i]['translation']=(-shift).tolist();nodes[i]['scale']=[1,1,1]
    wrap(doc,children,'metroMotionRotor',{'translation':anchor['translation'],'scale':body['scale']},profile['metroMotionRotor'])
    files[path]=js(doc);report['rotorPivot']={'translation':anchor['translation'],'meshShift':shift.tolist(),'axis':'X','source':'Render_Gatling.java: glRotatef(stvolrot,0,0,1) after native frame; stvol/stvol5 only'}
    for gun in ('podonok','revolver'):
        path=P+'gltf/guns/metro_'+gun+'.gltf';doc=json.loads(files[path])
        for side in ('left','right'):
            children=[i for i,n in enumerate(doc['nodes']) if n['name'] in (side+'ArmModel',side+'ArmSlimModel')]
            name='metroMotion'+gun.capitalize()+side.capitalize();assert len(children)==2
            wrap(doc,children,name,{},profile[name])
        files[path]=js(doc)
    path='guns/metro_gatling.json';g=json.loads(files[path]);g['roundsPerMin']=600;g['fireModes']=['full'];files[path]=js(g)
    files[P+'metro_motion.json']=js(profile)
    files['MOTION_UPDATE_MANIFEST.json']=js(report)
    manifest=json.loads(files['FULL_PACK_MANIFEST.json']);manifest['motionUpdate']=report
    manifest['mechanicsRequiresJarSha256']=sha(jar.read_bytes());manifest['integratedMechanicsVersion']='1.4.0'
    files['FULL_PACK_MANIFEST.json']=js(manifest)
    files['README_V9_RU.txt']=('v9 / MWF metro5: устанавливать ОБА новых файла одинаково на клиент и сервер.\n'
      'Заменить metro4 JAR и v8 ZIP, не ставить рядом. Сделать копию мира.\n'
      'Ручной пулемёт: удерживай кнопку выстрела; блок стволов плавно раскручивается и замедляется.\n'
      'Обычный блок до 400 выстр./мин, 5 стволов до 600; мотор ускоряет раскрутку.\n'
      'Подонок/револьвер: установка приклада автоматически меняет хват обеих рук.\n'
      'Без приклада сохранены прежние руки/анимации, включая замороженный хват револьвера.\n'
      'Проверка в Minecraft не выполнена, точное совпадение с Metro не заявляется.\n').encode()
    with zipfile.ZipFile(output,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for name,data in sorted(files.items()):
            i=zipfile.ZipInfo(name,(2026,10,30,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,data)
    return report


def main(args):
    assert sha(args.jar.read_bytes())==BASE_JAR,'Expected exact metro4 JAR'
    assert sha(args.pack.read_bytes())==BASE_PACK,'Expected exact v8 ZIP'
    args.work.mkdir(parents=True,exist_ok=True);args.output.mkdir(parents=True,exist_ok=True)
    spec=importlib.util.spec_from_file_location('motion_support',SUPPORT/'test_support.py');support=importlib.util.module_from_spec(spec);spec.loader.exec_module(support)
    stubs,helpers,tool=[args.work/n for n in ('stubs','helpers','tool')]
    for p in (stubs,helpers,tool):
        if p.exists():shutil.rmtree(p)
        p.mkdir(parents=True)
    sources=args.work/'stub-sources';stub_sources=support.materialize(sources)
    # Add genuine ByteBuf int ABI to compile-only facade for the rotor's fixed packet.
    f=sources/'io/netty/buffer/ByteBuf.java'
    f.write_text('''package io.netty.buffer; public class ByteBuf {
        private final java.nio.ByteBuffer b=java.nio.ByteBuffer.allocate(1024); private int read;
        public ByteBuf writeByte(int n){b.put((byte)n);return this;}
        public byte readByte(){return b.get(read++);}
        public short readUnsignedByte(){return (short)(readByte()&255);}
        public ByteBuf writeInt(int n){b.putInt(n);return this;}
        public int readInt(){int v=b.getInt(read);read+=4;return v;}
    }''')
    java8=['--release','8'] if args.java8_mode=='release' else ['-source','8','-target','8']
    run([args.javac,'-proc:none',*java8,'-d',stubs,*stub_sources])
    run([args.javac,'-proc:none',*java8,'-cp',os.pathsep.join(map(str,[stubs,args.jar])),'-d',helpers,*[SOURCE/'src/ru/metrocraft/mwf'/n for n in ('RotorRules.java','PacketMetroRotor.java','MotionHooks.java','MotionClient.java')]])
    run([args.javac,'-proc:none','-cp',args.asm,'-d',tool,SOURCE/'PatchMotion.java'])
    with zipfile.ZipFile(args.pack) as z:profile=profiles({n:z.read(n) for n in z.namelist()})
    pp=args.work/'metro_motion.json';pp.write_bytes(js(profile))
    jar=args.output/JAR;log=run([args.java,'-cp',os.pathsep.join(map(str,[tool,args.asm])),'PatchMotion',args.jar,helpers,pp,jar])
    # Execute both the old mechanics regression and the new motion rules/hooks
    # against the ACTUAL native ammo methods, not handwritten consumption logic.
    tests=args.work/'tests'
    if tests.exists():shutil.rmtree(tests)
    tests.mkdir()
    backend=support.materialize(args.work/'test-backend',True)
    cp=os.pathsep.join(map(str,[tests,helpers,stubs,jar]))
    run([args.javac,'-proc:none',*java8,'-cp',cp,'-d',tests,*backend,SUPPORT/'tests/MechanicsTest.java',SOURCE/'tests/MotionTest.java'])
    run([args.javac,'-proc:none','-cp',args.asm,'-d',tool,SOURCE/'NativeTestMethods.java'])
    run([args.java,'-cp',os.pathsep.join(map(str,[tool,args.asm])),'NativeTestMethods',jar,tests])
    testlog=''.join(run([args.java,'-Xverify:all','-cp',cp,'ru.metrocraft.mwf.'+name]) for name in ('MechanicsTest','MotionTest'))
    report=patch_pack(args.pack,args.output/PACK,jar,profile)
    report['patchLog']=log;report['testLog']=testlog;report['jarSha256']=sha(jar.read_bytes());report['packSha256']=sha((args.output/PACK).read_bytes())
    (args.output/'MWF_Motion_v5_report.json').write_bytes(js(report));print(json.dumps(report,ensure_ascii=False,indent=2))


if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('--asm',type=Path,required=True,help='ASM core/tree/analysis on one classpath (build only)');p.add_argument('--jar',type=Path,required=True);p.add_argument('--pack',type=Path,required=True)
    p.add_argument('--work',type=Path,default=ROOT/'.cache/mwf-motion');p.add_argument('--output',type=Path,default=ROOT/'downloads');p.add_argument('--javac',default='javac');p.add_argument('--java',default='java');p.add_argument('--java8-mode',choices=['release','source-target'],default='release',help='Use source-target only for a compiler runtime missing ct.sym; not a Java 8 API validation')
    main(p.parse_args())
