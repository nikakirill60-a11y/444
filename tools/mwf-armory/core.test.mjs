import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync,writeFileSync,mkdirSync} from 'node:fs';
import {Quaternion,Euler,Vector3} from 'three';
import {defaults,validateEdits,loadPack,selectGun,fromBase64,bytesToBase64,cameraMatrix,applyPose,reloadWeight,actionAt,reloadSequence,exportPack} from './core.mjs';
const input=new Uint8Array(readFileSync(new URL('../../downloads/Metro_Uboinik_MWF_fix1_v5.zip',import.meta.url)));
const pack=loadPack(input),gun=selectGun(pack,'metro_uboinik');
function exported(edits=defaults(),config=gun.config){const bytes=exportPack(pack,gun,edits,config),p=loadPack(bytes);return {bytes,p,g:selectGun(p,gun.type.internalName)};}
function values(g,index){const a=g.accessors[index],v=g.bufferViews[a.bufferView],b=fromBase64(g.buffers[v.buffer].uri.split(',')[1]),size={SCALAR:1,VEC3:3,VEC4:4}[a.type],d=new DataView(b.buffer,b.byteOffset,b.length);return Array.from({length:a.count},(_,i)=>Array.from({length:size},(_,j)=>d.getFloat32((v.byteOffset||0)+(a.byteOffset||0)+i*(v.byteStride||size*4)+j*4,true)));}
function channel(g,name,path){const i=g.nodes.findIndex(n=>n.name===name),a=g.animations[0],c=a.channels.find(c=>c.target.node===i&&c.target.path===path);return a.samplers[c.sampler];}
function close(a,b,epsilon=1e-6){assert.equal(a.length,b.length);a.forEach((v,i)=>assert.ok(Math.abs(v-b[i])<epsilon,`${v} != ${b[i]}`));}

test('embedded pack loads real gun, hand nodes and skin',()=>{assert.equal(gun.type.internalName,'metro_uboinik');assert.equal(gun.present.length,4);assert.ok(pack.files[gun.texturePath]);});
test('base64 round trip',()=>assert.deepEqual(fromBase64(bytesToBase64(input)),input));
test('rejects missing/invalid packs',()=>{assert.throws(()=>loadPack(new Uint8Array([1,2,3])));assert.throws(()=>validateEdits({}));});
test('rejects nonfinite values and invalid scale',()=>{for(const value of [NaN,Infinity,2000]){const e=defaults();e.left.idle.p[0]=value;assert.throws(()=>validateEdits(e));}const e=defaults();e.right.scale=-1;assert.throws(()=>validateEdits(e));});
test('unchanged export preserves every gun mesh and original sound/image bytes',()=>{const {p,g}=exported();assert.deepEqual(g.gltf,gun.gltf);for(const [n,b] of Object.entries(pack.files))if(/\.(ogg|png|obj)$/.test(n))assert.deepEqual(p.files[n],b);});
test('only selected left hand moves; both classic/slim receive identical offsets',()=>{const e=defaults();e.left.idle.p=[.15,-.2,.09];const {g}=exported(e);for(const name of ['leftArmModel','leftArmSlimModel']){const a=gun.gltf.nodes.find(n=>n.name===name),b=g.gltf.nodes.find(n=>n.name===name);close(b.translation,a.translation.map((v,i)=>v+e.left.idle.p[i]));}for(const n of gun.gltf.nodes.filter(n=>!n.name.startsWith('leftArm')))assert.deepEqual(g.gltf.nodes.find(x=>x.name===n.name),n);});
test('rotations are local XYZ and output quaternions stay normalized',()=>{const e=defaults();e.right.idle.r=[12,-28,41];const {g}=exported(e);const a=gun.gltf.nodes.find(n=>n.name==='rightArmModel'),b=g.gltf.nodes.find(n=>n.name==='rightArmModel');const q=new Quaternion().fromArray(a.rotation).multiply(new Quaternion().setFromEuler(new Euler(...e.right.idle.r.map(v=>v*Math.PI/180),'XYZ')));close(q.toArray(),b.rotation);assert.ok(Math.abs(new Quaternion().fromArray(b.rotation).length()-1)<1e-10);});
test('uniform hand scale leaves source nonuniform scale intact proportionally',()=>{const e=defaults();e.right.scale=1.25;const {g}=exported(e);const a=gun.gltf.nodes.find(n=>n.name==='rightArmModel'),b=g.gltf.nodes.find(n=>n.name==='rightArmModel');close(b.scale,a.scale.map(v=>v*1.25));});
test('ordinary and reload offsets are baked into corresponding animation keyframes',()=>{const e=defaults();e.left.idle.p=[.1,.2,.3];e.left.reload.p=[-.4,-.2,.5];const {g}=exported(e);const a=channel(gun.gltf,'leftArmModel','translation'),b=channel(g.gltf,'leftArmModel','translation'),times=values(g.gltf,b.input),before=values(gun.gltf,a.output),after=values(g.gltf,b.output);for(let i=0;i<times.length;i++){const w=reloadWeight(times[i][0]*24,gun.config),d=e.left.idle.p.map((v,j)=>v+(e.left.reload.p[j]-v)*w);close(after[i],before[i].map((v,j)=>v+d[j]));}});
test('reload rotation preview and exported rotation agree',()=>{const e=defaults();e.right.reload.r=[-15,10,30];const {g}=exported(e);const a=channel(gun.gltf,'rightArmModel','rotation'),b=channel(g.gltf,'rightArmModel','rotation'),times=values(g.gltf,b.input),before=values(gun.gltf,a.output),after=values(g.gltf,b.output);for(let i=0;i<times.length;i++){const w=reloadWeight(times[i][0]*24,gun.config);close(after[i],applyPose({rotation:before[i]},e.right,w).rotation);}});
test('gun positioning changes export actual render JSON, not just UI state',()=>{const cfg=structuredClone(gun.config);cfg.aim.translateHipPosition.y-=.1;cfg.aim.translateAimPosition.y+=.1;const {g}=exported(defaults(),cfg);assert.deepEqual(g.config,JSON.parse(JSON.stringify(cfg)));close(new Vector3(0,0,0).applyMatrix4(cameraMatrix(cfg,1)).toArray(),new Vector3(0,0,0).applyMatrix4(cameraMatrix(gun.config,1)).toArray());});
test('8-second reload sequence mirrors six FIRST and five SECOND phases',()=>{const seq=reloadSequence(gun.config,6);assert.equal(seq.length,13);assert.ok(Math.abs(seq.reduce((a,s)=>a+s.seconds,0)-8)<1e-10);assert.equal(actionAt(gun.config,'reload',8).rounds,6);});
test('reload transition weights use pre/hold/post ranges',()=>{const a=gun.config.animations;assert.equal(reloadWeight(0,gun.config),0);assert.equal(reloadWeight(a.preLoad.startTime,gun.config),0);assert.equal(reloadWeight(a.load.startTime,gun.config),1);assert.equal(reloadWeight(a.postLoad.endTime,gun.config),0);});
test('new key buffers are embedded, tight packed FLOAT, and never alias original data',()=>{const e=defaults();e.left.idle.p[0]=.1;const {g}=exported(e);assert.equal(g.gltf.buffers.length,gun.gltf.buffers.length+1);for(let i=0;i<gun.gltf.buffers.length;i++)assert.deepEqual(g.gltf.buffers[i],gun.gltf.buffers[i]);for(const a of g.gltf.accessors.slice(gun.gltf.accessors.length)){assert.equal(a.componentType,5126);assert.equal(a.byteOffset,0);const v=g.gltf.bufferViews[a.bufferView];assert.equal(v.byteLength,a.count*({VEC3:3,VEC4:4}[a.type])*4);}});
test('export does not mutate source model/config/edits',()=>{const original=JSON.stringify(gun),e=defaults(),originalEdits=JSON.stringify(e);e.left.idle.p[0]=.2;const expected=JSON.stringify(e);exported(e);assert.equal(JSON.stringify(gun),original);assert.equal(JSON.stringify(e),expected);});
test('project JSON with original pack and edits can round-trip',()=>{const e=defaults();e.left.reload.r[1]=44;const json=JSON.stringify({format:'MWF-Pose-Studio',version:1,packBase64:bytesToBase64(pack.bytes),edits:e,config:gun.config});const p=JSON.parse(json);validateEdits(p.edits);assert.equal(selectGun(loadPack(fromBase64(p.packBase64))).type.internalName,gun.type.internalName);assert.equal(p.edits.left.reload.r[1],44);});
test('camera reproduces independent Python/MWF matrix references, including identity reset',()=>{
  close(cameraMatrix(gun.config,0).elements,[-.006979899340500153,.006980962574913405,-.3998781654038192,0,.00012183459618085327,.3999390780625565,.006979899340500194,0,.3999390780625565,0,-.006980962574913364,0,.08922300025559932,-.2795132666363522,-.187425742696667,1],1e-12);
  close(cameraMatrix(gun.config,1).elements,[0,0,-.4,0,0,.4,0,0,.4,0,0,0,0,-.1508,-.13999997,1],1e-12);
});
test('shared sampler does not transfer an edited hand offset onto the opposite hand',()=>{
  const synthetic=structuredClone(gun),g=synthetic.gltf,a=g.animations[0],left=a.channels.find(c=>c.target.node===9&&c.target.path==='translation'),right=a.channels.find(c=>c.target.node===11&&c.target.path==='translation');right.sampler=left.sampler;
  const before=values(g,a.samplers[left.sampler].output),e=defaults();e.left.idle.p=[.1,0,0];e.left.reload.p=[.1,0,0];
  const after=selectGun(loadPack(exportPack(pack,synthetic,e,synthetic.config))).gltf;
  assert.deepEqual(values(after,channel(after,'rightArmModel','translation').output),before);
  values(after,channel(after,'leftArmModel','translation').output).forEach((row,i)=>close(row,[before[i][0]+.1,before[i][1],before[i][2]]));
});
test('static hands gain independent animated tracks with valid input bounds',()=>{
  const synthetic=structuredClone(gun);synthetic.gltf.animations[0].channels=synthetic.gltf.animations[0].channels.filter(c=>c.target.node!==9);
  const e=defaults();e.left.reload.p=[.1,.2,.3];const g=selectGun(loadPack(exportPack(pack,synthetic,e,synthetic.config))).gltf;
  for(const path of ['translation','rotation']){const a=g.accessors[channel(g,'leftArmModel',path).input];assert.equal(a.type,'SCALAR');assert.equal(a.min[0],0);assert.ok(a.max[0]>8);}
});
test('CUBICSPLINE hand export is rejected rather than silently corrupted',()=>{
  const synthetic=structuredClone(gun);const c=synthetic.gltf.animations[0].channels.find(c=>c.target.node===9);synthetic.gltf.animations[0].samplers[c.sampler].interpolation='CUBICSPLINE';
  const e=defaults();e.left.idle.p=[.1,0,0];assert.throws(()=>exportPack(pack,synthetic,e,synthetic.config),/CUBICSPLINE/);
});
// Deterministic fixture for verification with the selected real MWF JAR/Python validator.
const fixture=defaults();fixture.left.idle.p=[.02,-.05,.03];fixture.right.reload.r=[10,0,-5];
const fixtureBytes=exportPack(pack,gun,fixture,gun.config);mkdirSync(new URL('../../artifacts/mwf-armory/',import.meta.url),{recursive:true});writeFileSync(new URL('../../artifacts/mwf-armory/export-test.zip',import.meta.url),fixtureBytes);
