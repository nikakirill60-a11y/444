import * as Basic from './basic.mjs';
import {reloadSequence} from './core.mjs';
export {Basic};
import {Vector2,Vector3,Quaternion,Euler,ShapeUtils,MathUtils} from 'three';
import {zipSync,strFromU8,strToU8} from 'fflate';
import {loadPack as parsePack,selectGun as selectGunCore,jsonBytes,fromBase64,bytesToBase64,exportPack,defaults} from './core.mjs';
export {jsonBytes,defaults};
export function loadPack(bytes){return parsePack(bytes);}
export function editableType(type){try{validateType(type);return true;}catch{return false;}}
export function selectGun(pack,id){
 const entry=id?pack.guns.find(g=>g.type.internalName===id):pack.guns.find(g=>editableType(g.type));
 if(!entry)throw Error(id?'В паке нет оружия '+id:'Нет поддерживаемого BASIC/Enhanced-оружия.');
 validateType(entry.type);return Basic.isBasic(entry.type)?Basic.select(pack,entry,parseOBJ,appendGeometry):selectGunCore(pack,entry.type.internalName);
}
export function previewHiddenParts(config,selected){
 const hidden=new Set(config.defaultHidePart||[]);
 for(const show of [false,true])for(const {id,slot} of selected){
  for(const cfg of [config.attachmentGroup?.[slot],config.attachment?.[id]])for(const part of cfg?.[show?'showPart':'hidePart']||[]){if(show)hidden.delete(part);else hidden.add(part);}
 }
 return hidden;
}
export const SLOTS=['barrel','sight','grip','stock','laser'];
export const WEAPON_TYPES=['custom','pistol','revolver','mp','smg','carbine','rifle','ar','dmr','semisniper','boltsniper','shotgun','launcher'];
export const LIMITS={objBytes:20*1024*1024,triangles:180000,textures:8,imageSide:4096};
export const clone=x=>structuredClone(x);
export function validId(id){if(!/^[a-z][a-z0-9_]{2,63}$/.test(id))throw Error('ID: 3–64 символа, латинская буква в начале, затем a–z, 0–9 или _.');return id;}
export function readJSON(pack,path){if(!pack.files[path])throw Error('Нет файла '+path);return JSON.parse(strFromU8(pack.files[path]));}
export function workspace(files){return loadPack(zipSync(files,{level:1}));}
export function persistType(files,type){files[`guns/${nativeId(type.internalName)}.json`]=jsonBytes(type);}
export function setReload(gun,config,seconds){
 if(!Number.isFinite(seconds)||seconds<.2||seconds>120)throw Error('Перезарядка: от 0.2 до 120 секунд');
 if(Basic.isBasic(gun)){gun.reloadTime=Math.round(seconds*20);return;}
 const seq=reloadSequence(config,gun.acceptedAmmo?.length?1:gun.internalAmmoStorage,!!gun.acceptedAmmo?.length);
 if(!seq.length)throw Error('В конфиге нет фаз перезарядки');
 const current=seq.reduce((n,s)=>n+s.seconds,0),ratio=current/seconds;
 const keys=new Set(['preLoad','load','postLoad','preUnload','unload','postUnload',...Object.keys(config.animations).filter(k=>/reload/i.test(k))]);
 for(const key of keys)if(config.animations[key])config.animations[key].speed=(config.animations[key].speed||1)*ratio;
 gun.reloadTime=Math.round(seconds*20);
}
export function reloadSeconds(type,config){if(Basic.isBasic(type))return (type.reloadTime??40)/20;const seq=reloadSequence(config,type.acceptedAmmo?.length?1:type.internalAmmoStorage,!!type.acceptedAmmo?.length);return seq.length?seq.reduce((t,c)=>t+c.seconds,0):(type.reloadTime??40)/20;}
export function nativeId(id){if(typeof id!=='string'||! /^[a-zA-Z0-9_][a-zA-Z0-9_.-]{0,127}$/.test(id)||id==='..')throw Error('Небезопасный ID ресурса');return id;}
export function ammoEntries(pack,type){return (type.acceptedAmmo||[]).map(id=>readJSON(pack,`ammo/${nativeId(id)}.json`));}
export function capacity(pack,type,ammoId){if(!type.acceptedAmmo?.length)return type.internalAmmoStorage??1;const list=ammoEntries(pack,type);return (list.find(a=>a.internalName===ammoId)||list[0])?.ammoCapacity??1;}
export function bulletIds(pack,type,ammoId){if(!type.acceptedAmmo?.length)return type.acceptedBullets||[];const list=ammoEntries(pack,type);return (list.find(a=>a.internalName===ammoId)||list[0])?.subAmmo||[];}
export function validateAmmo(a){nativeId(a.internalName);for(const [k,min,max] of [['ammoCapacity',1,100000],['magazineCount',1,100000]])if(a[k]!=null&&(!Number.isInteger(a[k])||a[k]<min||a[k]>max))throw Error('Некорректный магазин: '+k);for(const k of ['reloadTimeFactor','reloadSpeedFactor'])if(a[k]!=null&&(!Number.isFinite(a[k])||a[k]<=0||a[k]>100))throw Error('Некорректный магазин: '+k);if(a.subAmmo!=null&&(!Array.isArray(a.subAmmo)||a.subAmmo.some(id=>{nativeId(id);return false;})))throw Error('Некорректный список патронов магазина');return a;}
export function cloneMagazine(pack,type,source,id,name){validId(id);if(pack.files[`ammo/${id}.json`]||pack.files[`guns/${id}.json`]||pack.files[`bullets/${id}.json`])throw Error('Этот ID уже существует');if(!name.trim())throw Error('Нужно название магазина');if(!type.acceptedAmmo?.includes(source))throw Error('Магазин не разрешён для оружия');const a=clone(readJSON(pack,`ammo/${source}.json`));a.internalName=id;a.displayName=name.trim();a.iconName=a.iconName||source;validateAmmo(a);pack.files[`ammo/${id}.json`]=jsonBytes(a);const rp=`ammo/render/${source}.render.json`;if(pack.files[rp])pack.files[`ammo/render/${id}.render.json`]=pack.files[rp].slice();pack.files[`assets/modularwarfare/models/item/${id}.json`]=jsonBytes({parent:'item/generated',textures:{layer0:'modularwarfare:items/'+a.iconName}});type.acceptedAmmo=[...type.acceptedAmmo,id];return a;}

export function validateType(t){
 nativeId(t.internalName);if(typeof t.displayName!=='string'||!t.displayName.trim()||t.displayName.length>120)throw Error('Укажи название оружия (до 120 символов)');
 if(!['enhanced','basic'].includes((t.animationType||'basic').toLowerCase()))throw Error('Неизвестный тип анимации');
 for(const key of ['acceptedAmmo','acceptedBullets'])if(t[key]!=null){if(!Array.isArray(t[key]))throw Error('Некорректный '+key);t[key].forEach(nativeId);}
 const ranges={gunDamage:[0,10000],numBullets:[1,128],internalAmmoStorage:[1,120],roundsPerMin:[1,1200],numBurstRounds:[1,20],bulletSpread:[0,180],reloadTime:[1,2400],weaponMaxRange:[1,10000],weaponEffectiveRange:[1,10000],recoilPitch:[0,180],recoilYaw:[0,180],randomRecoilPitch:[0,180],randomRecoilYaw:[0,180],gunDamageHeadshotBonus:[0,10000],moveSpeedModifier:[.05,5]};
 for(const [key,[min,max]] of Object.entries(ranges))if(t[key]!=null&&(!Number.isFinite(t[key])||t[key]<min||t[key]>max))throw Error('Недопустимое значение '+key);
 for(const k of ['numBullets','internalAmmoStorage','roundsPerMin','numBurstRounds','reloadTime','weaponMaxRange','weaponEffectiveRange'])if(t[k]!=null&&!Number.isInteger(t[k]))throw Error(k+' должно быть целым числом');
 if(t.weaponType!=null&&!WEAPON_TYPES.includes(t.weaponType))throw Error('Неизвестный тип оружия');
 if(t.fireModes!=null&&(!Array.isArray(t.fireModes)||!t.fireModes.length||t.fireModes.some(m=>!['semi','full','burst','auto'].includes(m))))throw Error('Нужен хотя бы один режим стрельбы');
 if(!t.acceptedAmmo?.length&&!t.acceptedBullets?.length)throw Error('Нет допустимого патрона или магазина');
 return t;
}
export function createWeapon(pack,templateId,id,name){
 validId(id);if(pack.guns.some(g=>g.type.internalName===id)||pack.files[`attachments/${id}.json`]||pack.files[`bullets/${id}.json`])throw Error('Этот ID уже существует');
 const template=selectGun(pack,templateId),files={...pack.files},type=clone(template.type),config=clone(template.config);
 type.internalName=id;type.displayName=name.trim();type.modelSkins=[{internalName:id,skinAsset:id,displayName:'Основная'}];type.iconName=id;
 type.acceptedAttachments={};delete type.defaultAttachments;
 config.attachment=Object.fromEntries(Object.entries(config.attachment||{}).filter(([key])=>type.acceptedAmmo?.includes(key)));config.attachmentGroup=Object.fromEntries(Object.entries(config.attachmentGroup||{}).filter(([key])=>['ammo','bullet'].includes(key)));config.modelFileName=id+'.gltf';
 if(Basic.isBasic(type)){delete config.attachment;delete config.attachmentGroup;config.attachments={};config.modelFileName=id+'.obj';}
 files[`guns/${id}.json`]=jsonBytes(type);files[`guns/render/${id}.render.json`]=jsonBytes(config);
 if(Basic.isBasic(type))files[`obj/guns/${id}.obj`]=pack.files[template.objPath];else files[`assets/modularwarfare/gltf/guns/${id}.gltf`]=jsonBytes(template.gltf);
 files[`assets/modularwarfare/skins/guns/${id}.png`]=pack.files[template.texturePath];
 const icon=pack.files[`assets/modularwarfare/textures/items/${template.type.iconName||template.type.internalName}.png`];
 if(icon)files[`assets/modularwarfare/textures/items/${id}.png`]=icon;
 files[`assets/modularwarfare/models/item/${id}.json`]=jsonBytes({parent:'item/generated',textures:{layer0:'modularwarfare:items/'+id},display:{thirdperson_righthand:{scale:[0,0,0]},thirdperson_lefthand:{scale:[0,0,0]}}});
 validateType(type);return workspace(files);
}
// Parse OBJ locally, retaining usemtl and standard indexed round/flash groups.
export function parseOBJ(text){
 if(typeof text!=='string'||text.length>LIMITS.objBytes)throw Error('OBJ слишком большой: максимум 20 МБ');
 const positions=[],uvs=[],normals=[],triangles=[],materials=new Set();let group='gunModel',material='default';
 const finite=a=>a.every(Number.isFinite);
 const index=(value,array)=>{const n=Number(value);if(!Number.isInteger(n)||n===0)throw Error('Некорректный индекс OBJ');const i=n>0?n-1:array.length+n;if(i<0||i>=array.length)throw Error('Индекс OBJ выходит за границы');return array[i];};
 for(const [lineNo,raw] of text.replace(/^\uFEFF/,'').split(/\r?\n/).entries()){
  const line=raw.split('#')[0].trim();if(!line)continue;const words=line.split(/\s+/),key=words.shift();
  try{
   if(key==='v'||key==='vn'){const v=words.slice(0,3).map(Number);if(v.length!==3||!finite(v)||v.some(x=>Math.abs(x)>1e8))throw Error('Недопустимая координата');(key==='v'?positions:normals).push(v);}
   else if(key==='vt'){const v=words.slice(0,2).map(Number);if(v.length!==2||!finite(v))throw Error('Недопустимый UV');uvs.push(v);}
   else if(key==='o'||key==='g')group=words.join('_')||'gunModel';
   else if(key==='usemtl')material=words.join(' ')||'default';
   else if(key==='f'){
    if(words.length<3||words.length>2048)throw Error('Грань должна иметь от 3 до 2048 вершин');
    const face=words.map(w=>{const [p,t,n]=w.split('/');return {p:index(p,positions),uv:t?index(t,uvs):[0,0],n:n?index(n,normals):null};});
    const p0=new Vector3().fromArray(face[0].p);let normal=new Vector3();
    for(let i=1;i<face.length-1&&!normal.lengthSq();i++)normal.crossVectors(new Vector3().fromArray(face[i].p).sub(p0),new Vector3().fromArray(face[i+1].p).sub(p0));
    if(normal.lengthSq()<1e-20)continue;normal.normalize();
    const abs=normal.toArray().map(Math.abs),drop=abs.indexOf(Math.max(...abs)),axes=[0,1,2].filter(i=>i!==drop);
    const polygon=face.map(v=>new Vector2(v.p[axes[0]],v.p[axes[1]]));const indices=ShapeUtils.triangulateShape(polygon,[]);
    if(indices.length!==face.length-2)throw Error('Не удалось триангулировать грань: проверь самопересечения');
    for(let ids of indices){ids=ids.slice();const v=ids.map(i=>face[i]);const cross=new Vector3().crossVectors(new Vector3().fromArray(v[1].p).sub(new Vector3().fromArray(v[0].p)),new Vector3().fromArray(v[2].p).sub(new Vector3().fromArray(v[0].p)));if(cross.dot(normal)<0)[ids[1],ids[2]]=[ids[2],ids[1]];
     triangles.push({group,material,v:ids.map(i=>({p:face[i].p.slice(),uv:face[i].uv.slice(),n:face[i].n?new Vector3().fromArray(face[i].n).normalize().toArray():normal.toArray()}))});
     if(triangles.length>LIMITS.triangles)throw Error('Максимум 180 000 треугольников');
    }materials.add(material);
   }
  }catch(e){throw Error('OBJ, строка '+(lineNo+1)+': '+e.message);}
 }
 if(!triangles.length)throw Error('В OBJ нет пригодных граней');return {triangles,materials:[...materials],vertices:positions.length};
}
export function parseMTL(text){const result={};let material;for(const raw of text.replace(/^\uFEFF/,'').split(/\r?\n/)){const line=raw.trim();if(line.startsWith('newmtl ')){material=line.slice(7).trim();}else if(material&&/^map_Kd\s/.test(line)){const p=line.replace(/^map_Kd\s+/,'').trim().replace(/^["']|["']$/g,'');result[material]=p.split(/[\\/]/).pop();}}return result;}
export function pngSize(bytes){if(bytes.length<24||[137,80,78,71,13,10,26,10].some((v,i)=>bytes[i]!==v))throw Error('Нужен настоящий PNG');const d=new DataView(bytes.buffer,bytes.byteOffset,bytes.byteLength),w=d.getUint32(16),h=d.getUint32(20);if(!w||!h||w>LIMITS.imageSide||h>LIMITS.imageSide)throw Error('PNG: максимум 4096×4096');return [w,h];}
export function orientTriangles(parsed,{forward='+x',length=.64,center=true}={}){
 if(!Number.isFinite(length)||length<.001||length>100)throw Error('Длина модели: от 0.001 до 100');
 const dirs={'+x':[1,0,0],'-x':[-1,0,0],'+y':[0,1,0],'-y':[0,-1,0],'+z':[0,0,1],'-z':[0,0,-1]};if(!dirs[forward])throw Error('Неизвестная ось');
 const q=new Quaternion().setFromUnitVectors(new Vector3(...dirs[forward]),new Vector3(1,0,0)),min=new Vector3(Infinity,Infinity,Infinity),max=new Vector3(-Infinity,-Infinity,-Infinity);
 for(const tri of parsed.triangles)for(const v of tri.v){const p=new Vector3().fromArray(v.p).applyQuaternion(q);min.min(p);max.max(p);}
 const size=max.clone().sub(min),extent=size.x>1e-6?size.x:Math.max(size.y,size.z);if(extent<1e-8)throw Error('Модель нулевого размера');
 const origin=center?min.clone().add(max).multiplyScalar(.5):new Vector3(),scale=length/extent;
 return parsed.triangles.map(t=>({...t,v:t.v.map(v=>({p:new Vector3().fromArray(v.p).applyQuaternion(q).sub(origin).multiplyScalar(scale).toArray(),uv:v.uv.slice(),n:new Vector3().fromArray(v.n).applyQuaternion(q).normalize().toArray()}))}));
}
export function appendGeometry(gltf,groups,rects={}){
 const g=clone(gltf),parts=[],bufferIndex=g.buffers.length;let offset=0;
 const append=(rows,size,index=false)=>{const bytes=new Uint8Array(rows.length*size*(index?2:4)),dv=new DataView(bytes.buffer);rows.forEach((row,i)=>row.forEach((v,j)=>{if(!Number.isFinite(v))throw Error('Нечисловая геометрия');if(index)dv.setUint16((i*size+j)*2,v,true);else dv.setFloat32((i*size+j)*4,v,true);}));while(offset%4){parts.push(new Uint8Array([0]));offset++;}const vi=g.bufferViews.length;g.bufferViews.push({buffer:bufferIndex,byteOffset:offset,byteLength:bytes.length});parts.push(bytes);offset+=bytes.length;const lo=Array(size).fill(Infinity),hi=Array(size).fill(-Infinity);for(const row of rows)row.forEach((v,i)=>{lo[i]=Math.min(lo[i],v);hi[i]=Math.max(hi[i],v);});g.accessors.push({bufferView:vi,byteOffset:0,componentType:index?5123:5126,count:rows.length,type:{1:'SCALAR',2:'VEC2',3:'VEC3'}[size],min:lo,max:hi});return g.accessors.length-1;};
 for(const [name,triangles] of Object.entries(groups)){
  let node=g.nodes.find(n=>n.name===name);if(!node){const base=g.nodes.find(n=>n.name==='gunModel');node={name,mesh:g.meshes.length,translation:clone(base.translation||[0,0,0]),scale:clone(base.scale||[1,1,1]),rotation:clone(base.rotation||[0,0,0,1])};const parent=g.nodes.find(n=>n.children?.includes(g.nodes.indexOf(base)));g.nodes.push(node);if(parent)parent.children.push(g.nodes.length-1);else for(const scene of g.scenes||[])if(scene.nodes?.includes(g.nodes.indexOf(base)))scene.nodes.push(g.nodes.length-1);g.meshes.push({name,primitives:[]});}
  if(node.mesh==null){node.mesh=g.meshes.length;g.meshes.push({name,primitives:[]});}
  const primitives=[];
  for(let i=0;i<triangles.length;i+=21845){const ts=triangles.slice(i,i+21845),verts=ts.flatMap(t=>t.v.map(v=>({...v,material:t.material}))),uv=verts.map(v=>{const r=rects[v.material]||[0,0,1,1];if(Object.values(rects).some(r=>r[2]!==1||r[3]!==1)&&(v.uv[0]<-1e-6||v.uv[0]>1.000001||v.uv[1]<-1e-6||v.uv[1]>1.000001))throw Error('Для нескольких PNG нужны UV в пределах 0..1. Запеки повторяющиеся UV в Blender.');return [r[0]+v.uv[0]*r[2],r[1]+(1-v.uv[1])*r[3]];});
   primitives.push({attributes:{POSITION:append(verts.map(v=>v.p),3),NORMAL:append(verts.map(v=>v.n),3),TEXCOORD_0:append(uv,2)},indices:append(verts.map((_,i)=>[i]),1,true),material:0,mode:4});
  }g.meshes[node.mesh]={name,primitives};
 }
 const all=new Uint8Array(offset);let pos=0;for(const p of parts){all.set(p,pos);pos+=p.length;}g.buffers.push({byteLength:all.length,uri:'data:application/octet-stream;base64,'+bytesToBase64(all)});return g;
}
export function replaceModel(pack,gunId,triangles,textureBytes,rects={}){
 pngSize(textureBytes);const gun=selectGun(pack,gunId),files={...pack.files},groups={gunModel:[]};
 if(Basic.isBasic(gun.type))return workspace(Basic.replace(files,gun,triangles,textureBytes,rects));
 for(const tri of triangles){let name=/^bulletModel_\d+$/.test(tri.group)?tri.group:tri.group==='flashModel'?'flashModel':'gunModel';(groups[name]??=[]).push(tri);}
 if(!groups.gunModel.length)throw Error('OBJ должен содержать корпус, а не только flashModel/bulletModel');
 // Existing indexed cartridges/flash are not retained from the previous model.
 let g=clone(gun.gltf);const removed=new Set();g.nodes.forEach((n,i)=>{if(/^bulletModel_\d+$/.test(n.name)||n.name==='flashModel'){removed.add(i);n.name='armory_unused_'+i+'_'+n.name;delete n.mesh;}});
 for(const n of g.nodes)if(n.children)n.children=n.children.filter(i=>!removed.has(i));
 g=appendGeometry(g,groups,rects);
 // Make this gun independent of another variant sharing its original glTF/skin.
 const cfg=clone(gun.config);cfg.modelFileName=gunId+'_armory.gltf';
 const type=clone(gun.type);type.modelSkins=[{internalName:gunId,skinAsset:gunId+'_armory',displayName:'Импорт OBJ'}];type.iconName=gunId+'_armory';
 files[`assets/modularwarfare/gltf/guns/${cfg.modelFileName}`]=jsonBytes(g);files[`guns/render/${gunId}.render.json`]=jsonBytes(cfg);persistType(files,type);
 files[`assets/modularwarfare/skins/guns/${gunId}_armory.png`]=textureBytes;
 // A cropped source atlas is a temporary inventory image until the user captures/uploads an icon.
 files[`assets/modularwarfare/textures/items/${gunId}_armory.png`]=textureBytes;
 files[`assets/modularwarfare/models/item/${gunId}.json`]=jsonBytes({parent:'item/generated',textures:{layer0:`modularwarfare:items/${gunId}_armory`},display:{thirdperson_righthand:{scale:[0,0,0]},thirdperson_lefthand:{scale:[0,0,0]}}});
 return workspace(files);
}
export function replaceMagazine(pack,gunId,ammoId,triangles,textureBytes,rects={}){
 const gun=selectGun(pack,gunId);if(!gun.type.acceptedAmmo?.includes(ammoId))throw Error('Магазин не разрешён для оружия');pngSize(textureBytes);const files={...pack.files},a=clone(readJSON(pack,`ammo/${ammoId}.json`)),path=`ammo/render/${ammoId}.render.json`,c=files[path]?clone(readJSON(pack,path)):{extra:{modelScale:1}};
 const ts=triangles.map(t=>({...t,group:/^bulletModel_\d+$/.test(t.group)?t.group:'ammoModel',v:t.v.map(v=>{const r=rects[t.material]||[0,0,1,1];if(Object.values(rects).some(r=>r[2]!==1||r[3]!==1)&&v.uv.some(n=>n<0||n>1))throw Error('Для атласа нужны UV 0..1');return {...v,uv:[r[0]+v.uv[0]*r[2],1-(r[1]+(1-v.uv[1])*r[3])]};})}));
 c.modelFileName=ammoId+'_armory.obj';a.isDynamicAmmo=true;a.sameTextureAsGun=false;a.modelSkins=[{internalName:ammoId,skinAsset:ammoId+'_armory'}];files[path]=jsonBytes(c);files[`ammo/${ammoId}.json`]=jsonBytes(a);files['obj/ammo/'+c.modelFileName]=strToU8(Basic.objText(ts));files[`assets/modularwarfare/skins/ammo/${ammoId}_armory.png`]=textureBytes;return workspace(files);
}
export function objText(triangles){const out=['# MWF Armory Studio; triangulated; original PNG/MTL bound in atlas','o attachmentModel'];for(const t of triangles)for(const v of t.v)out.push('v '+v.p.join(' '));for(const t of triangles)for(const v of t.v)out.push('vt '+v.uv.join(' '));for(const t of triangles)for(const v of t.v)out.push('vn '+v.n.join(' '));for(let i=0;i<triangles.length;i++)out.push('f '+[1,2,3].map(n=>{const v=i*3+n;return `${v}/${v}/${v}`;}).join(' '));return out.join('\n')+'\n';}
export function addAttachment(pack,gunId,id,name,slot,triangles,textureBytes,rects={}){
 validId(id);if(!SLOTS.includes(slot))throw Error('Неизвестный слот');if(pack.files[`attachments/${id}.json`]||pack.guns.some(g=>g.type.internalName===id)||pack.files[`bullets/${id}.json`])throw Error('Этот ID уже существует');pngSize(textureBytes);
 if(!name.trim())throw Error('Нужно имя обвеса');
 const files={...pack.files},gun=selectGun(pack,gunId),type=clone(gun.type),cfg=clone(gun.config);
 const ts=triangles.map(t=>({...t,v:t.v.map(v=>{const r=rects[t.material]||[0,0,1,1];if(Object.values(rects).some(r=>r[2]!==1||r[3]!==1)&&v.uv.some(n=>n<0||n>1))throw Error('Для атласа нужны UV 0..1');return {...v,uv:[r[0]+v.uv[0]*r[2],1-(r[1]+(1-v.uv[1])*r[3])]};})}));
 files[`obj/attachments/${id}.obj`]=strToU8(objText(ts));files[`assets/modularwarfare/skins/attachments/${id}.png`]=textureBytes;
 const entry={internalName:id,displayName:name.trim(),maxStackSize:1,attachmentType:slot,sameTextureAsGun:false,modelSkins:[{internalName:id,skinAsset:id}]};
 if(slot==='barrel')entry.barrel={isSuppressor:false,hideFlash:false,accuracyFactor:1,recoilPitchFactor:1,recoilYawFactor:1};
 if(slot==='sight')entry.sight={modeType:'simple'};
 if(slot==='grip')entry.grip={recoilPitchFactor:1,recoilYawFactor:1};if(slot==='stock')entry.stock={recoilPitchFactor:1,recoilYawFactor:1,aimSpeedFactor:1};if(slot==='laser')entry.laser={accuracyFactor:1,aimSpeedFactor:1,recoilPitchFactor:1,recoilYawFactor:1};
 files[`attachments/${id}.json`]=jsonBytes(entry);files[`attachments/render/${id}.render.json`]=jsonBytes({modelFileName:id+'.obj',extra:{modelScale:1},sight:{fovZoom:1.15},laser:{laserColor:[1,0,0],laserAlpha:1,maxDistance:64,dotSize:1.5}});
 files[`assets/modularwarfare/textures/items/${id}.png`]=textureBytes;files[`assets/modularwarfare/models/item/${id}.json`]=jsonBytes({parent:'item/generated',textures:{layer0:'modularwarfare:items/'+id}});
 type.acceptedAttachments??={};(type.acceptedAttachments[slot]??=[]).push(id);if(Basic.isBasic(type)){cfg.attachments??={};cfg.attachments.positionPointMap??={};cfg.attachments.positionPointMap[id]=[{x:0,y:0,z:0},{x:0,y:0,z:0}];}else{cfg.attachment??={};cfg.attachment[id]={binding:'gunModel',translate:{x:0,y:0,z:0},rotate:{x:0,y:0,z:0},scale:{x:1,y:1,z:1},sightAimPosOffset:{x:0,y:0,z:0}};}
 persistType(files,type);files[gun.configPath]=jsonBytes(cfg);return workspace(files);
}
export function modelDefaults(){return {p:[0,0,0],r:[0,0,0],s:1};}
export function modelNodes(gltf){return gltf.nodes.filter(n=>n.mesh!=null&&!/Arm/.test(n.name));}
export function modelTRS(base,e){if(!Number.isFinite(e.s)||e.s<.01||e.s>100||[...e.p,...e.r].some(n=>!Number.isFinite(n)||Math.abs(n)>1000))throw Error('Недопустимая трансформация модели');return {translation:(base.translation||[0,0,0]).map((v,i)=>v+e.p[i]),rotation:new Quaternion().fromArray(base.rotation||[0,0,0,1]).multiply(new Quaternion().setFromEuler(new Euler(...e.r.map(MathUtils.degToRad),'XYZ'))).normalize().toArray(),scale:(base.scale||[1,1,1]).map(v=>v*e.s)};}
export function exportWorkspace(pack,gun,type,config,edits,model){
 validateConfig(config,Basic.isBasic(type));validateType(type);const files={...pack.files};persistType(files,type);
 let result;
 if(Basic.isBasic(type)){result=loadPack(zipSync(Basic.exportFiles(files,gun,type,config,edits,model,parseOBJ),{level:3}));}else{
 // Do not silently edit another gun sharing the same render model/texture.
 const g=clone(gun.gltf);if(modelNodes(g).some(n=>(g.animations||[]).some(a=>a.channels.some(c=>c.target.node===g.nodes.indexOf(n))))) {if(JSON.stringify(model)!==JSON.stringify(modelDefaults()))throw Error('У модели есть собственные анимационные каналы деталей: смещение mesh в v1 для неё недоступно. Используй положение всего оружия.');}
 for(const node of modelNodes(g))Object.assign(node,modelTRS(node,model));
 const cfg=clone(config);cfg.modelFileName=type.internalName+'_armory_edit.gltf';const path='assets/modularwarfare/gltf/guns/'+cfg.modelFileName;
 const working={...gun,type,configPath:`guns/render/${type.internalName}.render.json`,gltfPath:path,gltf:g};files[path]=jsonBytes(g);files[working.configPath]=jsonBytes(cfg);
 const bytes=exportPack({...pack,files},working,edits,cfg);result=loadPack(bytes);}
 // The supplied historical arsenal used legacy auto; selected JAR serializes FULL as full.
 for(const entry of result.guns)if(entry.type.fireModes?.includes('auto')){entry.type.fireModes=[...new Set(entry.type.fireModes.map(m=>m==='auto'?'full':m))];persistType(result.files,entry.type);}
 for(const entry of result.guns){
  const t=entry.type;for(const aid of t.acceptedAmmo||[]){const a=validateAmmo(readJSON(result,`ammo/${nativeId(aid)}.json`));for(const bid of a.subAmmo||[])if(!result.files[`bullets/${nativeId(bid)}.json`])throw Error('Нет патрона магазина '+bid);}
  for(const bid of t.acceptedBullets||[])if(!result.files[`bullets/${bid}.json`])throw Error('Нет патрона '+bid);
  for(const [slot,ids] of Object.entries(t.acceptedAttachments||{}))for(const id of ids){const at=readJSON(result,`attachments/${id}.json`);if(at.attachmentType!==slot)throw Error('Слот обвеса не совпадает: '+id);}
 }
 for(const locale of ['ru_ru','ru_RU','en_us','en_US']){let text=result.files[`assets/modularwarfare/lang/${locale}.lang`]?strFromU8(result.files[`assets/modularwarfare/lang/${locale}.lang`]):'';const names=new Map(result.guns.map(g=>[g.type.internalName,g.type.displayName]));for(const n of Object.keys(result.files).filter(n=>/^(attachments|bullets|ammo)\/[^/]+\.json$/.test(n))){const a=readJSON(result,n);names.set(a.internalName,a.displayName);}text=text.split(/\r?\n/).filter(l=>!names.has(l.split('=')[0].replace(/^item\./,'').replace(/\.name$/,''))).join('\n');result.files[`assets/modularwarfare/lang/${locale}.lang`]=strToU8(text+'\n'+[...names].map(([id,n])=>`item.${id}.name=${String(n).replace(/[\r\n]/g,' ')}`).join('\n')+'\n');}
 result.files['ARMORY_README_RU.txt']=strToU8('Создано в MWF Armory Studio v3.\nУстановка: полностью закрыть игру, убрать старые ZIP с теми же ID, установить этот ZIP целиком в ModularWarfare/, перезапустить.\nДополнительный мод НЕ нужен. Новые возможности движка конфигом не добавляются.\nПредпросмотр не является Minecraft. Enhanced сохраняется как glTF, BASIC сохраняется как OBJ + родной render-конфиг. Legacy fireModes auto исправляется на full для выбранного JAR.\nИсходные SOURCE_MANIFEST описывают исходники, не хеши изменённого результата. Соблюдайте авторство ресурсов.\n');
 return zipSync(result.files,{level:6});
}
export function setSound(files,type,event,bytes){if(bytes.length<4||strFromU8(bytes.subarray(0,4))!=='OggS')throw Error('Нужен OGG, не MP3/WAV');const sig=[1,118,111,114,98,105,115];let start=-1;for(let i=0;i<Math.min(4096,bytes.length-12);i++)if(sig.every((v,j)=>bytes[i+j]===v)){start=i;break;}if(start<0||bytes[start+11]!==1)throw Error('Для Minecraft нужен mono OGG Vorbis (не stereo и не Opus)');if(bytes.length>12*1024*1024)throw Error('OGG больше 12 МБ');const key=type.internalName+'_'+event.toLowerCase();files[`assets/modularwarfare/sounds/${key}.ogg`]=bytes;const path='assets/modularwarfare/sounds.json',sounds=files[path]?JSON.parse(strFromU8(files[path])):{};sounds[key]={sounds:[{name:'modularwarfare:'+key}]};files[path]=jsonBytes(sounds);type.weaponSoundMap??={};type.weaponSoundMap[event]=[{soundEvent:event,soundName:key,soundRange:event.toLowerCase().includes('fire')?64:12,soundRandomPitch:0}];}

export function cloneBullet(pack,gunType,id,name,ammoId){
 validId(id);if(pack.files[`bullets/${id}.json`]||pack.files[`guns/${id}.json`]||pack.files[`attachments/${id}.json`])throw Error('Этот ID уже существует');if(!name.trim())throw Error('Нужно название патрона');
 const source=bulletIds(pack,gunType,ammoId)[0];if(!source)throw Error('У выбранного магазина не задан subAmmo');const b=clone(readJSON(pack,`bullets/${source}.json`));b.internalName=id;b.displayName=name.trim();b.iconName=b.iconName||source;
 pack.files[`bullets/${id}.json`]=jsonBytes(b);pack.files[`assets/modularwarfare/models/item/${id}.json`]=jsonBytes({parent:'item/generated',textures:{layer0:'modularwarfare:items/'+b.iconName}});if(gunType.acceptedAmmo?.length){const aid=ammoId||gunType.acceptedAmmo[0],a=readJSON(pack,`ammo/${aid}.json`);a.subAmmo=[id];pack.files[`ammo/${aid}.json`]=jsonBytes(a);}else{gunType.acceptedBullets=[id];gunType.defaultBullet=id;}return b;
}

export function validateConfig(c,basic=false){
 if(basic)return Basic.validate(c);
 if(!c?.animations?.default||!Number.isFinite(c.FPS)||c.FPS<=0||c.FPS>240)throw Error('Некорректная render-конфигурация / FPS');
 for(const [key,a] of Object.entries(c.animations)){if(!a||![a.startTime,a.endTime,a.speed].every(Number.isFinite)||a.startTime<0||a.endTime<=a.startTime||a.speed<=0)throw Error('Некорректная анимация '+key);}
 const vector=(v,key,scale=false)=>{if(!v)return;for(const [axis,n] of Object.entries(v))if(!['x','y','z'].includes(axis)||!Number.isFinite(n)||Math.abs(n)>1e7||(scale&&n<=0))throw Error('Некорректная трансформация '+key);};
 for(const [section,groups] of Object.entries({aim:c.aim,global:c.global}))for(const [key,v] of Object.entries(groups||{}))if(typeof v==='object')vector(v,section+'.'+key,/scale/i.test(key));
 for(const [id,t] of Object.entries(c.attachment||{})){nativeId(id);if(typeof (t.binding||'gunModel')!=='string')throw Error('Некорректный binding');for(const key of ['translate','rotate','sightAimPosOffset','sightAimRotOffset','scale']){vector(t[key],id+'.'+key,false);if(key==='scale'&&t[key]&&Object.values(t[key]).some(n=>n<0))throw Error('Некорректный масштаб обвеса');}}
 return c;
}
