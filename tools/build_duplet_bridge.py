#!/usr/bin/env python3
"""Compile Java-8 bridge against the selected MWF JAR and compile-only test ABI.
No Forge/Minecraft/MWF classes are copied into the deliverable.
ECJ + Java-8 boot API can be provided for restricted build environments.
"""
import argparse, hashlib, json, subprocess, struct, zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
TARGET='f91a5419cbc9e3cbd18f831e37c17aa2833d577142d324fed1ca14846ce95183'
def build(java,ecj,boot,mwf,out):
 assert hashlib.sha256(mwf.read_bytes()).hexdigest()==TARGET,'Wrong MWF JAR'
 classes=ROOT/'artifacts/duplet-bridge/classes';classes.mkdir(parents=True,exist_ok=True)
 sources=list((ROOT/'addons/mwf-duplet-bridge/src/main/java').rglob('*.java'))+list((ROOT/'tests_duplet/java').rglob('*.java'))
 cmd=[str(java),'-cp',str(ecj),'org.eclipse.jdt.internal.compiler.batch.Main','-source','1.8','-target','1.8','-encoding','UTF-8','-proc:none','-bootclasspath',str(boot),'-classpath',str(mwf),'-d',str(classes),*[str(p) for p in sources]]
 subprocess.run(cmd,check=True,cwd=ROOT)
 subprocess.run([str(java),'-cp',str(classes)+':'+str(mwf),'ru.metrocraft.duplet.BridgeTests'],check=True,cwd=ROOT)
 out.parent.mkdir(parents=True,exist_ok=True)
 metadata=[{'modid':'metro_duplet_bridge','name':'Metro Duplet Bridge','version':'1.0.0','mcversion':'1.12.2','description':'Transactional unloaded-only Duplet attachment capacity switching for MWF fix1.','authorList':['MetroCraft pack integration'],'dependencies':['modularwarfare']}]
 entries={'META-INF/MANIFEST.MF':b'Manifest-Version: 1.0\r\nImplementation-Title: Metro Duplet Bridge\r\nImplementation-Version: 1.0.0\r\n\r\n','mcmod.info':json.dumps(metadata).encode(),'README_RU.txt':(ROOT/'addons/mwf-duplet-bridge/README_RU.txt').read_bytes()}
 for p in classes.glob('ru/metrocraft/duplet/DupletBridge*.class'):
  assert struct.unpack('>H',p.read_bytes()[6:8])[0]==52
  entries[p.relative_to(classes).as_posix()]=p.read_bytes()
 assert len([n for n in entries if n.endswith('.class')])==2
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for name,b in sorted(entries.items()):
   i=zipfile.ZipInfo(name,(2026,11,2,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;i.external_attr=0o644<<16;z.writestr(i,b)
 print('Bridge built:',out,out.stat().st_size,'bytes; Java 8; no bundled game classes')
if __name__=='__main__':
 p=argparse.ArgumentParser(description=__doc__)
 for name in ['java','ecj','boot','mwf']:p.add_argument('--'+name,type=Path,required=True)
 p.add_argument('--out',type=Path,default=ROOT/'downloads/metro-duplet-bridge-1.0.0.jar');a=p.parse_args();build(a.java,a.ecj,a.boot,a.mwf,a.out)
