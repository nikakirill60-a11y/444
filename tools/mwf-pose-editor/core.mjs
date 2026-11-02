import {Matrix4, Euler, Quaternion, Vector3, MathUtils} from 'three';
import {unzipSync, zipSync, strFromU8, strToU8} from 'fflate';
export const HAND_NAMES={left:['leftArmModel','leftArmSlimModel','leftArmLayerModel','leftArmLayerSlimModel'],right:['rightArmModel','rightArmSlimModel','rightArmLayerModel','rightArmLayerSlimModel']};
const rad=MathUtils.degToRad;
export function defaults(){return {left:{scale:1,idle:{p:[0,0,0],r:[0,0,0]},reload:{p:[0,0,0],r:[0,0,0]}},right:{scale:1,idle:{p:[0,0,0],r:[0,0,0]},reload:{p:[0,0,0],r:[0,0,0]}}};}
export function validateEdits(edits){
  for(const side of ['left','right']){
    const e=edits?.[side];if(!e||!Number.isFinite(e.scale)||e.scale<.1||e.scale>4)throw Error('Некорректный масштаб руки');
    for(const mode of ['idle','reload'])for(const key of ['p','r'])if(!Array.isArray(e[mode]?.[key])||e[mode][key].length!==3||e[mode][key].some(v=>!Number.isFinite(v)||Math.abs(v)>1000))throw Error('Некорректные координаты рук');
  }return edits;
}
export function bytesToBase64(bytes){let s='';for(let i=0;i<bytes.length;i+=32768)s+=String.fromCharCode(...bytes.subarray(i,i+32768));return btoa(s);}
export function fromBase64(text){return Uint8Array.from(atob(text),c=>c.charCodeAt(0));}
export function jsonBytes(value){return strToU8(JSON.stringify(value,null,2)+'\n');}
function readJSON(files,path){if(!files[path])throw Error('Нет файла '+path);return JSON.parse(strFromU8(files[path]));}
export function loadPack(bytes){
  if(bytes.length>64*1024*1024)throw Error('ZIP больше 64 МБ. Этот редактор рассчитан на отдельный пак оружия.');
  const files=unzipSync(bytes);let total=0;
  for(const [name,data] of Object.entries(files)){total+=data.length;if(name.startsWith('/')||name.split('/').includes('..'))throw Error('Небезопасный путь в ZIP');}
  if(total>160*1024*1024||Object.keys(files).length>10000)throw Error('Слишком большой распакованный пак');
  const guns=Object.keys(files).filter(n=>/^guns\/[^/]+\.json$/i.test(n)).map(path=>({path,type:readJSON(files,path)})).filter(x=>x.type.animationType==='enhanced');
  if(!guns.length)throw Error('Нет Enhanced-оружия. BASIC/OBJ не поддерживается этой версией.');
  return {bytes,files,guns};
}
export function selectGun(pack,id){
  const entry=pack.guns.find(g=>g.type.internalName===id)||pack.guns[0], name=entry.type.internalName;
  const configPath=`guns/render/${name}.render.json`, config=readJSON(pack.files,configPath);
  const gltfPath='assets/modularwarfare/gltf/guns/'+config.modelFileName;
  if(!gltfPath.endsWith('.gltf'))throw Error('Нужна текстовая модель .gltf, не GLB/OBJ');
  const gltf=readJSON(pack.files,gltfPath);
  if((gltf.buffers||[]).some(b=>!b.uri?.startsWith('data:')))throw Error('Нужен glTF с встроенными буферами, как в паке «Убойник».');
  if((gltf.skins||[]).length)throw Error('Скелетные модели с skin/joints пока не поддерживаются. Нужны отдельные узлы рук, как у «Убойника».');
  const present=Object.values(HAND_NAMES).flat().filter(n=>gltf.nodes.some(x=>x.name===n));
  if(!present.length)throw Error('В модели нет стандартных узлов рук MWF');
  const skin=entry.type.modelSkins?.[0];const texturePath=`assets/modularwarfare/skins/guns/${skin?.skinAsset||skin?.internalName||name}.png`;
  if(!pack.files[texturePath])throw Error('Нет скина оружия '+texturePath);
  return {...entry,configPath,config,gltfPath,gltf,texturePath,present};
}
export function deltaPose(edit,weight){
  const p=edit.idle.p.map((v,i)=>v+(edit.reload.p[i]-v)*weight);
  const a=new Quaternion().setFromEuler(new Euler(...edit.idle.r.map(rad),'XYZ'));
  const b=new Quaternion().setFromEuler(new Euler(...edit.reload.r.map(rad),'XYZ'));
  return {p,q:a.slerp(b,weight),scale:edit.scale};
}
export function applyPose(base,edit,weight){
  const d=deltaPose(edit,weight);
  return {translation:(base.translation||[0,0,0]).map((v,i)=>v+d.p[i]),rotation:new Quaternion().fromArray(base.rotation||[0,0,0,1]).multiply(d.q).normalize().toArray(),scale:(base.scale||[1,1,1]).map(v=>v*d.scale)};
}
export function reloadWeight(frame,config){
  for(const [names,kind] of [[['preLoad','preUnload','preReload','preReloadEmpty'],'pre'],[['load','unload','reloadFirst','reloadFirstEmpty','reloadSecond','reloadSecondEmpty','reloadFirstQuickly','reloadSecondQuickly'],'hold'],[['postLoad','postUnload','postReload','postReloadEmpty'],'post']]){
    for(const name of names){const a=config.animations?.[name];if(a&&frame>=a.startTime-1e-5&&frame<=a.endTime+1e-5){const t=MathUtils.clamp((frame-a.startTime)/(a.endTime-a.startTime||1),0,1);return kind==='pre'?t:kind==='post'?1-t:1;}}
  }return 0;
}
export function cameraMatrix(config,ads=0){
  const m=new Matrix4();const rotate=(axis,degrees)=>m.multiply(new Matrix4().makeRotationAxis(axis,rad(degrees)));
  const x=new Vector3(1,0,0),y=new Vector3(0,1,0),z=new Vector3(0,0,1);
  const vector=(v,fallback)=>new Vector3(...(v?['x','y','z'].map(k=>v[k]??fallback):[fallback,fallback,fallback]));
  const translate=v=>m.multiply(new Matrix4().makeTranslation(v.x,v.y,v.z));
  rotate(y,90);m.scale(new Vector3(.0001,.0001,.0001));rotate(y,90);
  const g=config.global||{};translate(vector(g.globalTranslate,0));m.scale(vector(g.globalScale,1));rotate(y,-90);
  const r=vector(g.globalRotate,0).multiplyScalar(3.14/Math.PI);rotate(y,r.y);rotate(x,r.x);rotate(z,r.z);
  const a=config.aim||{},hip=vector(a.rotateHipPosition,0),aim=vector(a.rotateAimPosition,0).multiplyScalar(ads);
  // Actual renderer combines hip/aim angles per axis before translating.
  hip.add(aim);rotate(x,hip.x);rotate(y,hip.y);rotate(z,hip.z);
  translate(vector(a.translateHipPosition,0).add(vector(a.translateAimPosition,0).multiplyScalar(ads)));
  return m;
}
export function reloadSequence(config,count=6){
  const a=config.animations||{};const pick=(...names)=>names.find(n=>a[n]);let sequence=[];
  const add=(key,rounds)=>{if(!key)return;const c=a[key];sequence.push({key,rounds,seconds:(c.endTime-c.startTime)/(config.FPS||24)/(c.speed||1)});};
  add(pick('preReloadEmpty','preReload','preLoad'),0);
  for(let i=0;i<count;i++){add(pick('reloadFirstEmpty','reloadFirst','load'),i);if(i<count-1)add(pick('reloadSecondEmpty','reloadSecond','load'),i+1);}
  add(pick('postReloadEmpty','postReload','postLoad'),count);
  return sequence;
}
export function actionAt(config,action,time,rounds=6){
  if(action==='reload'){
    const seq=reloadSequence(config,rounds),duration=seq.reduce((v,c)=>v+c.seconds,0);let t=MathUtils.clamp(time,0,duration);
    for(let i=0;i<seq.length;i++){const s=seq[i];if(t<=s.seconds||i===seq.length-1){const a=config.animations[s.key];return {frame:MathUtils.lerp(a.startTime,a.endTime,MathUtils.clamp(t/s.seconds,0,1)),duration,rounds:s.rounds};}t-=s.seconds;}
  }
  const a=config.animations?.[action]||config.animations?.default;
  if(!a)throw Error('Нет анимации default');
  const duration=(a.endTime-a.startTime)/(config.FPS||24)/(a.speed||1);
  return {frame:MathUtils.lerp(a.startTime,a.endTime,MathUtils.clamp(time/(duration||1),0,1)),duration,rounds};
}
function accessorValues(gltf,buffers,index){
  const a=gltf.accessors[index],v=gltf.bufferViews[a.bufferView],size={SCALAR:1,VEC3:3,VEC4:4}[a.type];
  if(a.componentType!==5126||!size||a.sparse)throw Error('Не поддерживается формат ключевых кадров: нужны FLOAT SCALAR/VEC3/VEC4');
  const d=new DataView(buffers[v.buffer].buffer,buffers[v.buffer].byteOffset,buffers[v.buffer].byteLength),out=[];
  for(let i=0;i<a.count;i++){const row=[];for(let j=0;j<size;j++)row.push(d.getFloat32((v.byteOffset||0)+(a.byteOffset||0)+i*(v.byteStride||4*size)+j*4,true));out.push(row);}
  return out;
}
const identityEdit=e=>e.scale===1&&['idle','reload'].every(mode=>['p','r'].every(key=>e[mode][key].every(v=>v===0)));
export function exportPack(pack,gun,edits,config){
  validateEdits(edits);const g=structuredClone(gun.gltf),files={...pack.files};
  const buffers=g.buffers.map(b=>fromBase64(b.uri.split(',')[1]));const appended=[];let byteLength=0,handChanged=false;
  function append(values,size){
    const bytes=new Uint8Array(values.length*size*4),dv=new DataView(bytes.buffer);values.forEach((row,i)=>row.forEach((v,j)=>{if(!Number.isFinite(v))throw Error('Нечисловой ключевой кадр');dv.setFloat32((i*size+j)*4,v,true);}));
    const view=g.bufferViews.length;g.bufferViews.push({buffer:g.buffers.length,byteOffset:byteLength,byteLength:bytes.length});byteLength+=bytes.length;appended.push(bytes);
    const index=g.accessors.length;g.accessors.push({bufferView:view,byteOffset:0,componentType:5126,count:values.length,type:{1:'SCALAR',3:'VEC3',4:'VEC4'}[size],min:Array.from({length:size},(_,i)=>Math.min(...values.map(r=>r[i]))),max:Array.from({length:size},(_,i)=>Math.max(...values.map(r=>r[i])))});return index;
  }
  for(const side of ['left','right']){
    const edit=edits[side];if(identityEdit(edit))continue;
    for(let ni=0;ni<g.nodes.length;ni++){
      const node=g.nodes[ni];if(!HAND_NAMES[side].includes(node.name))continue;
      if(node.matrix)throw Error('Узлы рук с matrix вместо TRS пока не поддерживаются');
      handChanged=true;const original=structuredClone(node);Object.assign(node,applyPose(node,edit,0));
      const paths=new Set();
      for(const anim of g.animations||[])for(const channel of anim.channels){
        const path=channel.target.path;if(channel.target.node!==ni||!['translation','rotation','scale'].includes(path))continue;
        paths.add(path);const sampler=anim.samplers[channel.sampler];if(sampler.interpolation==='CUBICSPLINE')throw Error('CUBICSPLINE рук пока не поддерживается');
        const times=accessorValues(g,buffers,sampler.input),old=accessorValues(g,buffers,sampler.output);
        const values=old.map((row,i)=>{const d=deltaPose(edit,reloadWeight(times[i][0]*(gun.config.FPS||24),gun.config));return path==='translation'?row.map((v,j)=>v+d.p[j]):path==='scale'?row.map(v=>v*edit.scale):new Quaternion().fromArray(row).multiply(d.q).normalize().toArray();});
        // A sampler/accessor can be shared between nodes: never mutate it in place.
        const next={...sampler,output:append(values,path==='rotation'?4:3)};channel.sampler=anim.samplers.length;anim.samplers.push(next);
      }
      // A static hand still needs tracks when idle/reload edits differ.
      if(!g.animations?.length)g.animations=[{samplers:[],channels:[]}];
      const frames=[...new Set([0,...Object.values(gun.config.animations||{}).flatMap(a=>[a.startTime,a.endTime])])].sort((a,b)=>a-b);
      for(const path of ['translation','rotation'])if(!paths.has(path)){
        const values=frames.map(frame=>applyPose(original,edit,reloadWeight(frame,gun.config))[path]);
        const anim=g.animations[0],input=append(frames.map(f=>[f/(gun.config.FPS||24)]),1),output=append(values,path==='rotation'?4:3);
        anim.channels.push({sampler:anim.samplers.length,target:{node:ni,path}});anim.samplers.push({input,output,interpolation:'LINEAR'});
      }
    }
  }
  if(appended.length){const all=new Uint8Array(byteLength);let offset=0;for(const b of appended){all.set(b,offset);offset+=b.length;}g.buffers.push({byteLength:all.length,uri:'data:application/octet-stream;base64,'+bytesToBase64(all)});}
  // Validate/strip arbitrary UI data: only the existing render configuration is emitted.
  const cfg=structuredClone(config);
  for(const section of ['aim','global'])for(const group of Object.values(cfg[section]||{}))if(group&&typeof group==='object')for(const v of Object.values(group))if(typeof v==='number'&&!Number.isFinite(v))throw Error('Некорректное положение оружия');
  if(handChanged)files[gun.gltfPath]=jsonBytes(g);
  if(JSON.stringify(cfg)!==JSON.stringify(gun.config))files[gun.configPath]=jsonBytes(cfg);
  files['POSE_EDITOR_CHANGELOG.txt']=strToU8('Edited in MWF Pose Editor v1.\nHand TRS nodes and animation keys are baked into glTF. Render JSON includes weapon positioning.\nClient/server must use the same exported pack. Source manifest hashes describe the original source, not this edited output.\n');
  return zipSync(files,{level:6});
}
