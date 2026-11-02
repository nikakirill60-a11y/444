import * as THREE from 'three';
import {OrbitControls} from 'three/addons/controls/OrbitControls.js';
import {GLTFLoader} from 'three/addons/loaders/GLTFLoader.js';
import {OBJLoader} from 'three/addons/loaders/OBJLoader.js';
import {strFromU8,zipSync} from 'fflate';
import * as Armory from './armory.mjs';
import {defaults,validateEdits,bytesToBase64,fromBase64,cameraMatrix,applyPose,reloadWeight,actionAt,exportPack,HAND_NAMES} from './core.mjs';
const $=id=>document.getElementById(id);
const loadPack=Armory.loadPack,selectGun=Armory.selectGun,Basic=Armory.Basic;
let activeAmmo='',magazineRoot,magazineToken=0;
const basic=()=>!!gun?.basic;
function sampleAction(action,t,rounds){if(!basic())return actionAt(config,action,t,rounds,!!gun.type.acceptedAmmo?.length);const duration=action==='reload'?(gun.type.reloadTime??40)/20:action==='fire'?60/(gun.type.roundsPerMin||600):1.25;return {duration,frame:0,rounds,weight:action==='reload'?Math.sin(Math.PI*Math.min(1,t/duration)):0};}
function updateRounds(){const n=Armory.capacity(pack,gun.type,activeAmmo);$('rounds').max=n;preview.rounds=Math.min(preview.rounds,n);$('rounds').value=preview.rounds;}
let pack,gun,rig,edits=defaults(),config,side='left',editMode='idle',view='fp',playing=false,time=0,dirty=false,skinTexture,attachmentRoots=[],rest=[],tracks=[],token=0;
let undo=[],redo=[],lastSaved='',pending=false,modelEdit=Armory.modelDefaults(),importData=null,activeMod='';
const preview={fov:70,aspect:16/9,ads:0,slim:false,rounds:6,action:'default'};
function message(text,error=false){$('message').textContent=text;$('message').hidden=!text;$('message').className=error?'error':'';}
function safe(fn){return async(...args)=>{try{await fn(...args);}catch(e){console.error(e);$('loading').hidden=true;$('emptyState').hidden=!!rig;if(gun)$('gunSelect').value=gun.type.internalName;message(e.message||String(e),true);}};}
function download(bytes,name,type='application/octet-stream'){
  const blob=new Blob([bytes],{type}),url=URL.createObjectURL(blob),a=document.createElement('a');a.href=url;a.download=name;document.body.appendChild(a);a.click();a.remove();setTimeout(()=>URL.revokeObjectURL(url),60000);
}
function snapshot(){return JSON.stringify({edits,config,type:gun.type,modelEdit,mods:Object.fromEntries(Object.keys(pack.files).filter(p=>/^(attachments\/(?:render\/)?|bullets\/|ammo\/(?:render\/)?)[^/]+\.json$/.test(p)).map(p=>[p,JSON.parse(strFromU8(pack.files[p]))]))});}
function commit(){if(!config)return;const now=snapshot();if(now!==lastSaved){undo.push(lastSaved);undo=undo.slice(-60);redo=[];lastSaved=now;dirty=true;}$('undo').disabled=!undo.length;$('redo').disabled=!redo.length;$('saveStatus').textContent=dirty?'Есть изменения · экспортируй ZIP для игры':'Изменений пока нет';}
function resetHistory(){undo=[];redo=[];lastSaved=snapshot();dirty=false;commit();}
function restoreSnapshot(text){const s=JSON.parse(text);edits=s.edits;config=s.config;gun.type=s.type;modelEdit=s.modelEdit;for(const [p,v] of Object.entries(s.mods))pack.files[p]=Armory.jsonBytes(v);lastSaved=text;dirty=true;buildHandControls();buildWeaponControls();buildStudio();commit();loadAttachments().then(loadMagazine).catch(e=>message(e.message,true));}
function numericGroup(parent,title,keys,get,set,{min=-1,max=1,step=.01,unit=''}={}){
  const heading=document.createElement('h3');heading.textContent=title;const small=document.createElement('small');small.textContent=unit;heading.append(small);parent.append(heading);
  const syncers=[];
  for(const [i,key] of keys.entries()){
    const row=document.createElement('div');row.className='control-row';const label=document.createElement('label');label.textContent=key.toUpperCase();
    const range=document.createElement('input');range.type='range';range.min=min;range.max=max;range.step=step;
    const number=document.createElement('input');number.type='number';number.step=step;number.min=min;number.max=max;number.setAttribute('aria-label',title+' '+key);range.setAttribute('aria-label',title+' '+key+' ползунок');
    label.htmlFor='control-'+title+'-'+key;number.id=label.htmlFor;const sync=()=>{const value=get(i);range.value=value;number.value=Math.round(value*10000)/10000;};sync();syncers.push(sync);
    for(const input of [range,number]){input.addEventListener('input',()=>{const value=Number(input.value);if(input.value===''||!Number.isFinite(value)||value<min||value>max)return;set(i,value);const other=input===range?number:range;other.value=value;dirty=true;});input.addEventListener('change',()=>{sync();commit();});}
    row.append(label,range,number);parent.append(row);
  }
  return ()=>syncers.forEach(fn=>fn());
}
function buildHandControls(){
  const box=$('handControls');box.replaceChildren();
  if(basic()){config.arms??={};const a=config.arms[side+'Arm']??=Basic.armConfig(config,side),mode=editMode==='reload'?'Reload':'';
   for(const [title,key,rotation] of [['Положение','arm'+mode+'Pos',false],['Поворот','arm'+mode+'Rot',true],['Масштаб руки','armScale',false]]){a[key]??=Basic.armConfig(config,side)[key];numericGroup(box,title,['x','y','z'],i=>a[key]['xyz'[i]],(i,v)=>a[key]['xyz'[i]]=v,{min:key==='armScale'?.1:rotation?-180:-5,max:key==='armScale'?4:rotation?180:5,step:rotation?1:.005,unit:rotation?'градусы':'BASIC: родные значения'});}return;
  }
  if(!gun.present.some(n=>n.startsWith(side+'Arm'))){box.textContent='В этой модели нет отдельных узлов выбранной руки. Баланс и положение оружия доступны; геометрия рук не создаётся автоматически.';return;}
  const edit=()=>edits[side];
  numericGroup(box,'Положение',['x','y','z'],i=>edit()[editMode].p[i],(i,v)=>edit()[editMode].p[i]=v,{min:-1.5,max:1.5,step:.005,unit:'ед. модели'});
  numericGroup(box,'Поворот',['x','y','z'],i=>edit()[editMode].r[i],(i,v)=>edit()[editMode].r[i]=v,{min:-180,max:180,step:1,unit:'градусы'});
  numericGroup(box,'Масштаб руки',['s'],()=>edit().scale,(_,v)=>edit().scale=v,{min:.25,max:2,step:.01,unit:'для всех поз'});
}
function buildWeaponControls(){
  const box=$('weaponControls');box.replaceChildren();config.aim??={};let refreshAim=()=>{};
  for(const [name,key,rotation] of [['Обычное положение','translateHipPosition',false],['Обычный угол','rotateHipPosition',true],['Смещение ADS','translateAimPosition',false],['Угол ADS','rotateAimPosition',true]]){
    config.aim[key]??={x:0,y:0,z:0};
    const sync=numericGroup(box,name,['x','y','z'],i=>config.aim[key]['xyz'[i]],(i,v)=>{
      const axis='xyz'[i],old=config.aim[key][axis];config.aim[key][axis]=v;
      if(!basic()&&$('keepAds').checked&&key.includes('Hip')){const other=rotation?'rotateAimPosition':'translateAimPosition';config.aim[other]??={x:0,y:0,z:0};config.aim[other][axis]-=v-old;refreshAim();}
    },{min:rotation?-180:-5,max:rotation?180:5,step:rotation?1:.005,unit:rotation?'градусы':'ед. модели'});
    if(key==='translateAimPosition')refreshAim=sync;
  }
}
const scene=new THREE.Scene();scene.background=new THREE.Color('#829ab5');scene.fog=new THREE.Fog('#829ab5',10,35);
const camera=new THREE.PerspectiveCamera(70,16/9,.01,100);camera.position.set(0,0,0);camera.lookAt(0,0,-1);
let renderer;
try{renderer=new THREE.WebGLRenderer({antialias:true,alpha:true,preserveDrawingBuffer:true});}
catch(e){$('loading').textContent='WebGL2 недоступен. Открой файл в современном Chrome/Edge с аппаратным ускорением.';throw e;}
renderer.setPixelRatio(Math.min(devicePixelRatio,2));renderer.outputColorSpace=THREE.SRGBColorSpace;renderer.toneMapping=THREE.NoToneMapping;$('canvasMount').append(renderer.domElement);
const controls=new OrbitControls(camera,renderer.domElement);controls.enabled=false;controls.enableDamping=true;
const weaponGroup=new THREE.Group();weaponGroup.matrixAutoUpdate=false;scene.add(weaponGroup);
const ground=new THREE.Mesh(new THREE.PlaneGeometry(80,80),new THREE.MeshLambertMaterial({color:'#647155'}));ground.rotation.x=-Math.PI/2;ground.position.y=-1.6;scene.add(ground);
const grid=new THREE.GridHelper(80,160,'#748162','#6a775a');grid.position.y=-1.599;scene.add(grid);
scene.add(new THREE.HemisphereLight('#ffffff','#64735a',2.2));const light=new THREE.DirectionalLight('#fff4e3',2.1);light.position.set(-3,5,2);scene.add(light);
const crosshair=new THREE.Group();const lineMaterial=new THREE.LineBasicMaterial({color:'#ffffff',depthTest:false});
for(const [a,b] of [[[-.009,0,-1],[.009,0,-1]],[[0,-.009,-1],[0,.009,-1]]])crosshair.add(new THREE.Line(new THREE.BufferGeometry().setFromPoints([new THREE.Vector3(...a),new THREE.Vector3(...b)]),lineMaterial));
camera.add(crosshair);scene.add(camera);
let handBox=new THREE.BoxHelper(new THREE.Object3D(),0xe7ac54);handBox.visible=false;scene.add(handBox);
function placeholderSkin(){
  const c=document.createElement('canvas');c.width=c.height=64;const ctx=c.getContext('2d');ctx.fillStyle='#b99c7d';ctx.fillRect(0,0,64,64);
  ctx.fillStyle='#636e5b';ctx.fillRect(40,16,16,7);ctx.fillRect(32,48,16,7);
  for(let y=0;y<64;y+=4)for(let x=0;x<64;x+=4){ctx.fillStyle=(x+y)%8===0?'#ffffff0b':'#00000008';ctx.fillRect(x,y,4,4);}
  const t=new THREE.CanvasTexture(c);t.flipY=false;t.colorSpace=THREE.SRGBColorSpace;t.magFilter=THREE.NearestFilter;t.minFilter=THREE.NearestFilter;return t;
}
skinTexture=placeholderSkin();
function resize(){
  const box=$('canvasMount'),width=box.clientWidth,height=box.clientHeight;const aspect=preview.aspect==='auto'?width/height:preview.aspect;
  const w=Math.min(width,height*aspect),h=w/aspect;renderer.setSize(Math.max(1,w),Math.max(1,h),false);renderer.domElement.style.width=w+'px';renderer.domElement.style.height=h+'px';camera.aspect=aspect;camera.fov=preview.fov;camera.updateProjectionMatrix();
  $('dimensions').textContent=`${$('aspect').selectedOptions[0].text} / FOV ${preview.fov}°`;
}
new ResizeObserver(resize).observe($('canvasMount'));
function setView(next){
  view=next;for(const b of $('viewTabs').children)b.classList.toggle('selected',b.dataset.view===view);
  controls.enabled=view==='orbit';crosshair.visible=view==='fp';
  if(view==='fp'){camera.position.set(0,0,0);camera.quaternion.identity();controls.target.set(0,0,-1);}
  else if(rig){weaponGroup.updateWorldMatrix(true,true);const bounds=new THREE.Box3();rig.traverseVisible(o=>{if(o.isMesh){o.geometry.computeBoundingBox();bounds.union(o.geometry.boundingBox.clone().applyMatrix4(o.matrixWorld));}});const center=bounds.getCenter(new THREE.Vector3()),radius=bounds.getSize(new THREE.Vector3()).length()/2;const distance=Math.max(.1,radius)/Math.sin(THREE.MathUtils.degToRad(preview.fov)/2)*1.25;camera.position.copy(center).add(new THREE.Vector3(.8,.35,.75).normalize().multiplyScalar(distance));controls.target.copy(center);controls.update();}
  $('viewHint').textContent=view==='fp'?(basic()?'BASIC: родные позы; перезарядка схематична':'Предпросмотр MWF, не запущенный Minecraft'):'Мышь — поворот · колесо — масштаб · ПКМ — перемещение';
}
function textureFromBytes(bytes){return new Promise((resolve,reject)=>{const url=URL.createObjectURL(new Blob([bytes],{type:'image/png'}));new THREE.TextureLoader().load(url,t=>{URL.revokeObjectURL(url);t.flipY=false;t.colorSpace=THREE.SRGBColorSpace;t.magFilter=THREE.NearestFilter;t.minFilter=THREE.NearestFilter;t.wrapS=t.wrapT=THREE.RepeatWrapping;resolve(t);},undefined,()=>{URL.revokeObjectURL(url);reject(Error('Не удалось прочитать PNG'));});});}
function disposeRig(){++magazineToken;magazineRoot=undefined;if(rig){rig.traverse(o=>{o.geometry?.dispose();if(o.material){for(const m of [].concat(o.material)){if(m.map!==skinTexture)m.map?.dispose();m.dispose();}}});weaponGroup.remove(rig);}for(const a of attachmentRoots)a.removeFromParent();attachmentRoots=[];}
async function openGun(id){
  const mine=++token;playing=false;time=0;$('play').textContent='▶';$('loading').hidden=false;message('');
  $('emptyState').hidden=true;
  const sourcePack=pack,selected=selectGun(sourcePack,id);Armory.validateType(selected.type);
  // MWF binds its own atlas: discard embedded material image URIs to keep imports offline.
  const displayGltf=structuredClone(selected.gltf);delete displayGltf.images;delete displayGltf.textures;
  displayGltf.materials=(displayGltf.materials||[]).map(m=>({name:m.name,doubleSided:true}));
  const loaded=await new GLTFLoader().parseAsync(JSON.stringify(displayGltf),'');const tex=await textureFromBytes(sourcePack.files[selected.texturePath]);
  if(mine!==token)return;disposeRig();gun=selected;config=structuredClone(gun.config);edits=defaults();modelEdit=Armory.modelDefaults();rig=loaded.scene;weaponGroup.add(rig);rest=[];tracks=[];
  rig.traverse(o=>{rest.push({o,p:o.position.clone(),q:o.quaternion.clone(),s:o.scale.clone(),visible:o.visible});
    if(o.isMesh){const hand=Object.values(HAND_NAMES).flat().includes(o.name);o.material=new THREE.MeshLambertMaterial({map:hand?skinTexture:tex,side:THREE.DoubleSide,alphaTest:.05});}
  });
  for(const clip of loaded.animations)for(const track of clip.tracks){const split=track.name.lastIndexOf('.'),name=track.name.slice(0,split),property=track.name.slice(split+1);const o=rig.getObjectByName(name)||rig.getObjectByProperty('uuid',name);if(o&&['position','quaternion','scale'].includes(property))tracks.push({o,property,interpolant:track.createInterpolant()});}
  $('gunTitle').textContent=gun.type.displayName||gun.type.internalName;$('gunSelect').value=gun.type.internalName;
  activeAmmo=gun.type.defaultAmmo&&gun.type.acceptedAmmo?.includes(gun.type.defaultAmmo)?gun.type.defaultAmmo:gun.type.acceptedAmmo?.[0]||'';preview.rounds=Armory.capacity(pack,gun.type,activeAmmo);updateRounds();$('objLength').value=basic()?'16':'.64';$('keepAds').disabled=basic();
  $('action').value='default';preview.action='default';buildHandControls();buildWeaponControls();await loadAttachments();await loadMagazine();buildStudio();resetHistory();$('loading').hidden=true;setWorkspaceEnabled(true);$('keepAds').disabled=basic();resize();setView(view);
  if(basic())message('BASIC: родные OBJ и настройки рук сохраняются без преобразования в Enhanced. Перезарядка в просмотре схематична: Java-механика не эмулируется.');
  else if(gun.present.length<4)message('В модели найдены не все варианты рук. Изменятся только существующие узлы.');
}
async function loadAttachments(){
  for(const a of attachmentRoots){a.traverse(o=>{o.geometry?.dispose();for(const m of [].concat(o.material||[])){m.map?.dispose();m.dispose();}});a.removeFromParent();}attachmentRoots=[];
  $('attachments').replaceChildren();const textures=new Map();
  const attachmentTexture=path=>{if(!textures.has(path))textures.set(path,textureFromBytes(pack.files[path]));return textures.get(path);};
  for(const [slot,ids] of Object.entries(gun.type.acceptedAttachments||{}))for(const id of ids){
    try{
      const type=JSON.parse(strFromU8(pack.files[`attachments/${id}.json`])),cfg=JSON.parse(strFromU8(pack.files[`attachments/render/${id}.render.json`]));const data=pack.files['obj/attachments/'+cfg.modelFileName];if(!data)continue;
      const object=new OBJLoader().parse(strFromU8(data)),skin=type.modelSkins?.[0],mapPath=`assets/modularwarfare/skins/attachments/${skin?.skinAsset||skin?.internalName||id}.png`;
      const tex=type.sameTextureAsGun?await attachmentTexture(gun.texturePath):pack.files[mapPath]?await attachmentTexture(mapPath):null;
      if(tex){tex.flipY=true;tex.needsUpdate=true;} // MWF's OBJ loader flips V.
      object.traverse(o=>{if(o.isMesh&&!['attachmentModel','attachmentModel_glow'].includes(o.name))o.visible=false;if(o.isMesh)o.material=new THREE.MeshLambertMaterial({map:tex,color:tex?'#ffffff':'#ce9b58',side:THREE.DoubleSide});});
      const t=config.attachment?.[id]||{},binding=rig.getObjectByName(basic()?'gunModel':t.binding||'gunModel');if(!binding)continue;
      const wrapper=new THREE.Group();wrapper.add(object);const trans=t.translate||{},rot=t.rotate||{},s=t.scale||{};wrapper.position.set(trans.x||0,trans.y||0,trans.z||0);wrapper.rotation.set(THREE.MathUtils.degToRad(rot.x||0),THREE.MathUtils.degToRad(rot.y||0),THREE.MathUtils.degToRad(rot.z||0),'YXZ');wrapper.scale.set(s.x??1,s.y??1,s.z??1);wrapper.visible=gun.type.defaultAttachments?.[slot]===id;wrapper.userData.modelScale=cfg.extra?.modelScale??1;wrapper.userData.slot=slot;wrapper.userData.id=id;wrapper.matrixAutoUpdate=false;binding.add(wrapper);attachmentRoots.push(wrapper);
      const label=document.createElement('label');label.className='switch';const text=document.createElement('span');text.textContent=type.displayName||id;const input=document.createElement('input');input.type='checkbox';input.checked=wrapper.visible;wrapper.userData.input=input;input.onchange=()=>{wrapper.visible=input.checked;if(input.checked)for(const other of attachmentRoots)if(other!==wrapper&&other.userData.slot===slot){other.visible=false;other.userData.input.checked=false;}};label.append(text,input);$('attachments').append(label);
    }catch(e){console.warn('Обвес не показан:',id,e.message);}
  }
}
async function loadMagazine(){
 const mine=++magazineToken;if(magazineRoot){magazineRoot.traverse(o=>{o.geometry?.dispose();for(const m of [].concat(o.material||[])){m.map?.dispose();m.dispose();}});magazineRoot.removeFromParent();magazineRoot=undefined;}
 rig?.traverse(o=>{if(o.isMesh&&o.name==='ammoModel')o.material.visible=true;});if(!activeAmmo)return;const a=Armory.readJSON(pack,`ammo/${activeAmmo}.json`);if(!a.isDynamicAmmo)return;rig.traverse(o=>{if(o.isMesh&&o.name==='ammoModel')o.material.visible=false;});
 const rp=`ammo/render/${activeAmmo}.render.json`;if(!pack.files[rp]){message('Магазин '+activeAmmo+': нет render-конфига. Параметры доступны, геометрия не показана.');return;}
 const c=Armory.readJSON(pack,rp),path='obj/ammo/'+c.modelFileName;if(!pack.files[path]){message('Магазин '+activeAmmo+': нет '+path);return;}
 const object=new OBJLoader().parse(strFromU8(pack.files[path])),skin=a.modelSkins?.[0],tp=a.sameTextureAsGun?gun.texturePath:`assets/modularwarfare/skins/ammo/${skin?.skinAsset||skin?.internalName||activeAmmo}.png`,tex=pack.files[tp]?await textureFromBytes(pack.files[tp]):null;
 if(mine!==magazineToken){tex?.dispose();object.traverse(o=>o.geometry?.dispose());return;}if(tex){tex.flipY=true;tex.needsUpdate=true;}
 object.traverse(o=>{if(o.isMesh){o.visible=/^(ammoModel|bulletModel(?:_\d+)?|ammoModel_glow)$/.test(o.name);o.material=new THREE.MeshLambertMaterial({map:tex,color:tex?'#ffffff':'#cba171',side:THREE.DoubleSide});}});
 const binding=rig.getObjectByName(basic()?'gunModel':'ammoModel');if(!binding){message('В модели нет ammoModel для динамического магазина. Параметры доступны; добавь узел при импорте модели.');return;}
 magazineRoot=new THREE.Group();magazineRoot.add(object);magazineRoot.matrixAutoUpdate=false;magazineRoot.userData.modelScale=c.extra?.modelScale??1;binding.add(magazineRoot);
}
function buildMagazineControls(box){
 const t=gun.type,all=Object.keys(pack.files).filter(p=>/^ammo\/[^/]+\.json$/.test(p)).map(p=>Armory.readJSON(pack,p));if(!t.acceptedAmmo.includes(activeAmmo))activeAmmo=t.acceptedAmmo[0];
 selectField(box,'Магазин для настройки / просмотра',Armory.ammoEntries(pack,t).map(a=>[a.internalName,a.displayName||a.internalName]),activeAmmo,async id=>{activeAmmo=id;updateRounds();await loadMagazine();buildStats();buildBulletControls();});
 const path=`ammo/${activeAmmo}.json`,a=Armory.readJSON(pack,path),save=()=>{Armory.validateAmmo(a);pack.files[path]=Armory.jsonBytes(a);};
 const note=document.createElement('p');note.className='hint';note.textContent='Параметры магазина общие для всех пушек с этим ID. Для независимой настройки создай копию ниже. Перезарядка оружия задаёт базовое время; множители магазина также учитываются MWF.';box.append(note);
 textField(box,'Название магазина',a.displayName||a.internalName,v=>{a.displayName=v;save();});
 numberField(box,'Ёмкость магазина',a.ammoCapacity??1,1,100000,1,n=>{a.ammoCapacity=n;save();updateRounds();});
 numberField(box,'Магазинов в блоке',a.magazineCount??1,1,100000,1,n=>{a.magazineCount=n;save();});
 for(const [key,title] of [['reloadTimeFactor','Множитель времени магазина'],['reloadSpeedFactor','Множитель скорости магазина']])numberField(box,title,a[key]??1,.01,100,.01,n=>{a[key]=n;save();});
 checkbox(box,'Разрешать пустые магазины',a.allowEmptyMagazines,v=>{a.allowEmptyMagazines=v;save();});
 checkbox(box,'Магазин по умолчанию',t.defaultAmmo===activeAmmo,v=>{if(v)t.defaultAmmo=activeAmmo;else delete t.defaultAmmo;});
 heading(box,'Положение выбранного магазина');
 let tr;if(basic()){config.maps??={};config.maps.ammoMap??={};const m=config.maps.ammoMap[activeAmmo]??={offset:{x:0,y:0,z:0},scale:{x:config.extra?.modelScale??1,y:config.extra?.modelScale??1,z:config.extra?.modelScale??1}};m.offset??={x:0,y:0,z:0};m.scale??={x:1,y:1,z:1};tr={translate:m.offset,scale:m.scale};}else{config.attachment??={};tr=config.attachment[activeAmmo]??={};}
 tr.translate??={x:0,y:0,z:0};tr.rotate??={x:0,y:0,z:0};
 for(const [title,key,rotation] of [['Смещение магазина','translate',false],...(!basic()?[['Поворот магазина','rotate',true]]:[])])numericGroup(box,title,['x','y','z'],i=>tr[key]['xyz'[i]],(i,v)=>tr[key]['xyz'[i]]=v,{min:rotation?-180:-20,max:rotation?180:20,step:rotation?1:.005});
 {tr.scale??={x:1,y:1,z:1};numericGroup(box,'Размер магазина',['x','y','z'],i=>tr.scale['xyz'[i]],(i,v)=>tr.scale['xyz'[i]]=v,{min:.01,max:10,step:.01});}
 heading(box,'Патроны выбранного магазина');
 for(const p of Object.keys(pack.files).filter(p=>/^bullets\/[^/]+\.json$/.test(p))){const b=Armory.readJSON(pack,p);checkbox(box,(b.displayName||b.internalName)+' ['+b.internalName+']',(a.subAmmo||[]).includes(b.internalName),v=>{a.subAmmo=v?[...new Set([...(a.subAmmo||[]),b.internalName])]:(a.subAmmo||[]).filter(id=>id!==b.internalName);save();buildBulletControls();});}
 heading(box,'Разрешённые магазины');
 for(const mag of all)checkbox(box,(mag.displayName||mag.internalName)+' ['+mag.internalName+']',t.acceptedAmmo.includes(mag.internalName),async v=>{const next=v?[...new Set([...t.acceptedAmmo,mag.internalName])]:t.acceptedAmmo.filter(id=>id!==mag.internalName);if(!next.length){buildStats();throw Error('Оставь хотя бы один магазин');}t.acceptedAmmo=next;if(t.defaultAmmo&&!next.includes(t.defaultAmmo))delete t.defaultAmmo;if(!next.includes(activeAmmo))activeAmmo=next[0];updateRounds();await loadMagazine();buildStats();buildBulletControls();});
 heading(box,'Независимая копия магазина');let id='',name='';textField(box,'ID копии магазина','',v=>id=v);textField(box,'Имя копии магазина','',v=>name=v);const btn=document.createElement('button');btn.className='wide';btn.textContent='Создать копию магазина';btn.onclick=safe(async()=>{const old=activeAmmo,a=Armory.cloneMagazine(pack,t,old,id,name);if(basic()){if(config.maps?.ammoMap?.[old])config.maps.ammoMap[id]=structuredClone(config.maps.ammoMap[old]);}else if(config.attachment?.[old])config.attachment[id]=structuredClone(config.attachment[old]);activeAmmo=a.internalName;updateRounds();await loadMagazine();buildStats();buildBulletControls();resetHistory();dirty=true;commit();message('Копия магазина создана. Исходный магазин не изменён.');});box.append(btn);
}

async function openPackBytes(bytes,id){
 message('Читаю ZIP… Большой пак может потребовать время и много памяти.');
 await new Promise(resolve=>setTimeout(resolve,30));
 const candidate=loadPack(bytes),selected=selectGun(candidate,id),previous=pack;pack=candidate;
 try{await openGun(selected.type.internalName);}catch(e){pack=previous;throw e;}
 refreshGunLists();const unsupported=pack.guns.filter(g=>!Armory.editableType(g.type)).length;
 message(unsupported?`Пак открыт: ${pack.guns.length} оружия. ${unsupported} неподдерживаемых оружий сохранены в паке без редактирования; они помечены в списке.`:'Пак открыт. Изменения сохраняются только в проект или новый ZIP.');
}
function setWorkspaceEnabled(enabled){
 for(const el of document.querySelectorAll('main input,main select,main button')){
  if(el.matches('[data-panel],#loadLocal,#emptyOpenPack'))continue;
  el.disabled=!enabled;
 }
 $('saveProject').disabled=$('exportPack').disabled=!enabled;
 $('applyOBJ').disabled=!enabled||!importData;$('undo').disabled=!enabled||!undo.length;$('redo').disabled=!enabled||!redo.length;
}

function render(now){requestAnimationFrame(render);if(!rig||!config){renderer.render(scene,camera);return;}
  const dt=Math.min((now-(render.last||now))/1000,.1);render.last=now;
  let sample=sampleAction(preview.action,time,preview.rounds);
  if(playing){time+=dt;if(time>sample.duration)time=0;sample=sampleAction(preview.action,time,preview.rounds);}
  $('timeline').max=sample.duration;$('timeline').value=time;$('timeLabel').textContent=`${time.toFixed(2)} / ${sample.duration.toFixed(2)} с`;
  for(const r of rest){r.o.position.copy(r.p);r.o.quaternion.copy(r.q);r.o.scale.copy(r.s);r.o.visible=r.visible;}
  for(const track of tracks)track.o[track.property].fromArray(track.interpolant.evaluate(sample.frame/(config.FPS||24)));
  if(JSON.stringify(modelEdit)!==JSON.stringify(Armory.modelDefaults()))for(const base of Armory.modelNodes(gun.gltf)){const o=rig.getObjectByName(base.name);if(o){const v=Armory.modelTRS(base,modelEdit);o.position.fromArray(v.translation);o.quaternion.fromArray(v.rotation);o.scale.fromArray(v.scale);}}
  for(const a of attachmentRoots)updateAttachment(a);
  const weight=basic()?sample.weight||0:reloadWeight(sample.frame,config);
  if(basic()){const body=rig.getObjectByName('gunModel'),t=Basic.gunTRS(config);body.position.fromArray(t.translation);body.scale.fromArray(t.scale);}
  if(magazineRoot){const t=config.attachment?.[activeAmmo]||{};magazineRoot.matrix.copy(basic()?Basic.magazineMatrix(config,activeAmmo):transformMatrix(config.attachmentGroup?.ammo).multiply(transformMatrix(t)));}
  for(const currentSide of ['left','right'])for(const name of HAND_NAMES[currentSide]){
    const o=rig.getObjectByName(name);if(!o)continue;
    const result=basic()?Basic.armTRS(config,currentSide,weight):applyPose({translation:o.position.toArray(),rotation:o.quaternion.toArray(),scale:o.scale.toArray()},edits[currentSide],weight);
    o.position.fromArray(result.translation);o.quaternion.fromArray(result.rotation);o.scale.fromArray(result.scale);o.visible=name.includes('Slim')===preview.slim&&!name.includes('Layer');
  }
  rig.traverse(o=>{if(/^bulletModel_\d+$/.test(o.name))o.visible=Number(o.name.split('_').pop())<(preview.action==='reload'?sample.rounds:preview.rounds);if(o.name==='flashModel'||/^shellModel_\d+$/.test(o.name))o.visible=false;});
  const selected=attachmentRoots.filter(a=>a.visible).map(a=>a.userData);if(activeAmmo)selected.push({id:activeAmmo,slot:'ammo'});const hidden=Armory.previewHiddenParts(config,selected);
  if(basic()){if(attachmentRoots.some(a=>a.visible&&a.userData.slot==='sight'))hidden.add('defaultScopeModel');if(attachmentRoots.some(a=>a.visible&&a.userData.slot==='barrel'))hidden.add('defaultBarrelModel');}
  for(const r of rest)if(hidden.has(r.o.name))r.o.visible=false;
  weaponGroup.matrix.copy(basic()?Basic.cameraMatrix(config,preview.ads):cameraMatrix(config,preview.ads));
  const sight=attachmentRoots.find(a=>a.visible&&a.userData.slot==='sight');if(sight&&preview.ads&&!basic()){const t=config.attachment?.[sight.userData.id]||{},p=t.sightAimPosOffset||{},r=t.sightAimRotOffset||{};weaponGroup.matrix.multiply(new THREE.Matrix4().makeTranslation(p.x||0,p.y||0,p.z||0)).multiply(new THREE.Matrix4().makeRotationFromEuler(new THREE.Euler(...['x','y','z'].map(k=>THREE.MathUtils.degToRad((r[k]||0)*3.14/Math.PI)),'YXZ')));}
  scene.updateMatrixWorld(true);
  if($('highlight').checked){const target=rig.getObjectByName(side+'Arm'+(preview.slim?'Slim':'')+'Model');if(target){handBox.setFromObject(target);handBox.visible=true;}}else handBox.visible=false;
  $('stateBadge').textContent=(preview.ads?'ADS / ПРИЦЕЛИВАНИЕ':'ОБЫЧНЫЙ ХВАТ')+(preview.action==='reload'?' · ЗАРЯДКА':'');
  if(view==='orbit')controls.update();renderer.render(scene,camera);
}
requestAnimationFrame(render);
for(const button of $('handTabs').children)button.onclick=()=>{side=button.dataset.side;for(const b of $('handTabs').children)b.classList.toggle('selected',b===button);buildHandControls();};
for(const button of $('viewTabs').children)button.onclick=()=>setView(button.dataset.view);
$('resetView').onclick=()=>setView(view);
$('editMode').onchange=()=>{editMode=$('editMode').value;if(editMode==='reload'){$('action').value='reload';preview.action='reload';time=sampleAction('reload',0,preview.rounds).duration*.35;}else{$('action').value='default';preview.action='default';time=0;}playing=false;$('play').textContent='▶';buildHandControls();};
$('ads').onchange=()=>{preview.ads=$('ads').checked?1:0;};
$('bodyType').onchange=()=>preview.slim=$('bodyType').value==='slim';
$('aspect').onchange=()=>{preview.aspect=$('aspect').value==='auto'?'auto':Number($('aspect').value);resize();};
numericGroup($('fovControl'),'Поле зрения',['°'],()=>preview.fov,(_,v)=>{preview.fov=v;resize();},{min:40,max:110,step:1,unit:'FOV'});
$('rounds').oninput=()=>{preview.rounds=THREE.MathUtils.clamp(Math.round(Number($('rounds').value)||0),0,Number($('rounds').max));time=0;};
$('action').onchange=()=>{preview.action=$('action').value;time=0;};
$('timeline').oninput=()=>{time=Number($('timeline').value);playing=false;$('play').textContent='▶';};
$('play').onclick=()=>{playing=!playing;$('play').textContent=playing?'Ⅱ':'▶';};
$('resetHand').onclick=()=>{if(basic()){config.arms??={};config.arms[side+'Arm']=Basic.armConfig(gun.config,side);}edits[side]=defaults()[side];buildHandControls();commit();};
$('resetWeapon').onclick=()=>{config.aim=structuredClone(gun.config.aim);buildWeaponControls();commit();};
$('keepAds').onchange=()=>buildWeaponControls();
$('undo').onclick=()=>{if(!undo.length)return;redo.push(snapshot());restoreSnapshot(undo.pop());};
$('redo').onclick=()=>{if(!redo.length)return;undo.push(snapshot());restoreSnapshot(redo.pop());};
$('openPack').onclick=()=>$('packInput').click();$('openProject').onclick=()=>$('projectInput').click();$('openSkin').onclick=()=>$('skinInput').click();
$('packInput').onchange=safe(async()=>{const f=$('packInput').files[0];if(f&&(!dirty||confirm('Заменить текущий пак? Несохранённые изменения будут потеряны.')))await openPackBytes(new Uint8Array(await f.arrayBuffer()));$('packInput').value='';});
$('gunSelect').onchange=safe(async()=>switchGun($('gunSelect').value));
$('skinInput').onchange=safe(async()=>{const f=$('skinInput').files[0];if(!f)return;const bitmap=await createImageBitmap(f);if(bitmap.width!==64||bitmap.height!==64)throw Error('Нужен скин PNG размером 64×64');bitmap.close();const old=skinTexture;skinTexture=await textureFromBytes(new Uint8Array(await f.arrayBuffer()));rig?.traverse(o=>{if(Object.values(HAND_NAMES).flat().includes(o.name)&&o.isMesh)o.material.map=skinTexture;});old.dispose();message('Скин используется только для предпросмотра. В игре будет скин игрока.');});
$('saveProject').onclick=safe(async()=>{commit();download(JSON.stringify(projectObject()),gun.type.internalName+'.armory-project.json','application/json');message('Проект сохранён со всеми пушками и ресурсами. Для игры нужен отдельный экспорт ZIP.');});
$('projectInput').onchange=safe(async()=>{const f=$('projectInput').files[0];if(!f)return;if(dirty&&!confirm('Открыть другой проект вместо текущего?'))return;await restoreProject(JSON.parse(await f.text()));$('projectInput').value='';});
$('exportPack').onclick=safe(async()=>{
 if(pending)return;pending=true;$('exportPack').disabled=true;message('Проверяю ресурсы и собираю ZIP…');
 try{await new Promise(r=>setTimeout(r,30));const result=buildExport();download(result,gun.type.internalName+'_MWF_ARMORY.zip','application/zip');dirty=false;commit();message('ZIP готов. Закрой Minecraft, убери старые паки с теми же ID, установи новый ZIP целиком в ModularWarfare/. На сервере нужен такой же пак.');}finally{pending=false;$('exportPack').disabled=false;}
});
window.addEventListener('beforeunload',e=>{if(dirty){e.preventDefault();e.returnValue='';}});
// Expose a read-only test bridge, not an external API or network service.
window.mwfEditor={get ready(){return !!rig&&$('loading').hidden;},get gun(){return gun?.type.internalName;},get edits(){return structuredClone(edits);},get config(){return structuredClone(config);},get type(){return structuredClone(gun?.type);},get model(){return structuredClone(modelEdit);},get guns(){return (pack?.guns||[]).map(g=>g.type.internalName);},get empty(){return !gun&&!rig;},exportBytes:()=>buildExport(),project:()=>projectObject()};
// Boot after authoring UI handlers below have been installed.
function panel(name){for(const b of document.querySelectorAll('[data-panel]'))b.classList.toggle('selected',b.dataset.panel===name);for(const el of document.querySelectorAll('.tool-panel'))el.hidden=el.id!=='panel-'+name;}
for(const b of document.querySelectorAll('[data-panel]'))b.onclick=()=>{panel(b.dataset.panel);if(b.dataset.panel==='assets')refreshJSON();};
function textField(box,label,value,onChange){const l=document.createElement('label');l.className='field-title';const input=document.createElement('input');input.type='text';input.value=value||'';input.maxLength=120;input.setAttribute('aria-label',label);l.textContent=label;input.onchange=safe(async()=>{if(!input.value.trim())throw Error('Поле не может быть пустым');onChange(input.value.trim());commit();});box.append(l,input);return input;}
function numberField(box,label,value,min,max,step,onChange){const row=document.createElement('label');row.className='field-pair';const text=document.createElement('span');text.textContent=label;const input=document.createElement('input');input.type='number';Object.assign(input,{min,max,step,value});input.setAttribute('aria-label',label);input.onchange=safe(async()=>{const n=Number(input.value);if(!input.value||!Number.isFinite(n)||n<min||n>max||(step===1&&!Number.isInteger(n))){input.value=value;throw Error(label+': от '+min+' до '+max+(step===1?', целое число':''));}onChange(n);value=n;commit();});row.append(text,input);box.append(row);return input;}
function checkbox(box,label,value,onChange){const row=document.createElement('label');row.className='switch';const text=document.createElement('span');text.textContent=label;const input=document.createElement('input');input.type='checkbox';input.checked=!!value;input.onchange=safe(async()=>{await onChange(input.checked);commit();});row.append(text,input);box.append(row);return input;}
function selectField(box,label,options,value,onChange){const l=document.createElement('label');l.className='field-title';l.textContent=label;const select=document.createElement('select');select.setAttribute('aria-label',label);for(const [id,title] of options){const o=document.createElement('option');o.value=id;o.textContent=title;select.append(o);}select.value=value;select.onchange=safe(async()=>{await onChange(select.value);commit();});box.append(l,select);return select;}
function heading(box,label){const h=document.createElement('h3');h.className='form-heading';h.textContent=label;box.append(h);}
function buildStats(){
 const box=$('statsControls');box.replaceChildren();const t=gun.type;
 textField(box,'Название в игре',t.displayName,v=>{t.displayName=v;$('gunTitle').textContent=v;refreshGunLists();});
 selectField(box,'Класс оружия',Armory.WEAPON_TYPES.map(t=>[t,t]),t.weaponType,v=>t.weaponType=v);
 heading(box,'Урон и дальность');
 for(const [key,label,min,max,step,fallback] of [['gunDamage','Урон за пулю / дробину',0,10000,.1,1],['numBullets','Пуль / дробин за выстрел',1,128,1,1],['gunDamageHeadshotBonus','Бонус урона в голову',0,10000,.1,0],['bulletSpread','Разброс',0,180,.05,0],['weaponEffectiveRange','Эффективная дальность',1,10000,1,50],['weaponMaxRange','Максимальная дальность',1,10000,1,100]])numberField(box,label,t[key]??fallback,min,max,step,n=>t[key]=n);
 heading(box,'Боезапас и темп');
 const bullets=Object.keys(pack.files).filter(p=>/^bullets\/[^/]+\.json$/.test(p)).map(p=>{const b=Armory.readJSON(pack,p);return [b.internalName,b.displayName||b.internalName];});
 if(!t.acceptedAmmo?.length)selectField(box,'Патрон',bullets,t.acceptedBullets?.[0],v=>{t.acceptedBullets=[v];t.defaultBullet=v;buildBulletControls();});
 else buildMagazineControls(box);
 const duration=Armory.reloadSeconds(t,config);
 if(!t.acceptedAmmo?.length)numberField(box,'Ёмкость (внутренний боезапас)',t.internalAmmoStorage,1,120,1,n=>{const seconds=Armory.reloadSeconds(t,config);t.internalAmmoStorage=n;Armory.setReload(t,config,seconds);$('rounds').max=n;preview.rounds=Math.min(preview.rounds,n);$('rounds').value=preview.rounds;});
 numberField(box,'Перезарядка пустого, секунд',duration,.2,120,.1,n=>Armory.setReload(t,config,n));
 numberField(box,'Темп, выстрелов / минуту',t.roundsPerMin,1,1200,1,n=>t.roundsPerMin=n);
 numberField(box,'Выстрелов в очереди',t.numBurstRounds||3,1,20,1,n=>t.numBurstRounds=n);
 for(const [mode,label] of [['semi','Одиночный'],['full','Автоматический'],['burst','Фиксированная очередь']])checkbox(box,label,(t.fireModes||['semi']).some(m=>(m==='auto'?'full':m)===mode),v=>{const modes=(t.fireModes||['semi']).map(m=>m==='auto'?'full':m),next=v?[...modes,mode]:modes.filter(m=>m!==mode);if(!next.length){buildStats();throw Error('Оставь хотя бы один режим стрельбы');}t.fireModes=[...new Set(next)];});
 heading(box,'Отдача и поведение');
 for(const [key,label,fallback] of [['recoilPitch','Вертикальная отдача',1],['recoilYaw','Горизонтальная отдача',1],['randomRecoilPitch','Случайная вертикальная',0],['randomRecoilYaw','Случайная горизонтальная',0]])numberField(box,label,t[key]??fallback,0,180,.05,n=>t[key]=n);
 numberField(box,'Множитель скорости движения',t.moveSpeedModifier??1,.05,5,.05,n=>t.moveSpeedModifier=n);
 checkbox(box,'Разрешить стрельбу при беге',t.allowSprintFiring??true,v=>t.allowSprintFiring=v);
 checkbox(box,'Выбрасывать гильзы (если настроены)',t.dropBulletCasing,v=>t.dropBulletCasing=v);
}
function refreshGunLists(){for(const name of ['gunSelect','libraryGun']){const select=$(name);select.replaceChildren();for(const g of pack.guns){const o=document.createElement('option');o.value=g.type.internalName;o.textContent=g.type.internalName===gun.type.internalName?gun.type.displayName:g.type.displayName||g.type.internalName;o.disabled=!Armory.editableType(g.type);if(o.disabled)o.textContent+=' — без редактирования';select.append(o);}select.value=gun.type.internalName;}}
function buildStudio(){if(!gun)return;refreshGunLists();buildStats();buildModelControls();buildModList();buildBulletControls();refreshJSON();$('packInfo').textContent=`${pack.guns.length} оружия · ${Object.keys(pack.files).length} файлов · ${basic()?'BASIC / OBJ':'Enhanced / glTF'} · ${gun.type.acceptedAmmo?.length?'отъёмный магазин':'внутренний боезапас'} · ID: ${gun.type.internalName}`;}
function refreshJSON(){if(gun)$('jsonView').textContent=JSON.stringify(gun.type,null,2);}
$('refreshJSON').onclick=refreshJSON;
function buildModelControls(){const box=$('modelControls');box.replaceChildren();const indices=new Set(Armory.modelNodes(gun.gltf).map(n=>gun.gltf.nodes.indexOf(n))),animated=(gun.gltf.animations||[]).some(a=>a.channels.some(c=>indices.has(c.target.node)));
 if(animated){const p=document.createElement('p');p.className='hint';p.textContent='Детали этой модели анимированы: используй положение всего оружия справа или импортируй статичный OBJ. Трансформация mesh отдельно здесь отключена, чтобы не повредить ключи.';box.append(p);return;}
 numericGroup(box,'Смещение модели',['x','y','z'],i=>modelEdit.p[i],(i,v)=>modelEdit.p[i]=v,{min:-3,max:3,step:.005,unit:'руки не сдвигаются'});
 numericGroup(box,'Углы модели',['x','y','z'],i=>modelEdit.r[i],(i,v)=>modelEdit.r[i]=v,{min:-180,max:180,step:1,unit:'градусы'});
 numericGroup(box,'Масштаб модели',['s'],()=>modelEdit.s,(_,v)=>modelEdit.s=v,{min:.05,max:10,step:.01});
}
$('resetModel').onclick=()=>{modelEdit=Armory.modelDefaults();buildModelControls();commit();};
function buildExport(){validateEdits(edits);return Armory.exportWorkspace(pack,gun,gun.type,config,edits,modelEdit);}
async function flushCurrent(){const bytes=buildExport();pack=loadPack(bytes);}
async function switchGun(id){if(!gun||id===gun.type.internalName)return;selectGun(pack,id);const changed=dirty;await flushCurrent();await openGun(id);dirty=changed;commit();}
$('libraryGun').onchange=safe(async()=>switchGun($('libraryGun').value));
$('createGun').onclick=safe(async()=>{const id=$('newGunId').value.trim(),name=$('newGunName').value.trim();Armory.validId(id);if(!name)throw Error('Введи название оружия');await flushCurrent();pack=Armory.createWeapon(pack,gun.type.internalName,id,name);await openGun(id);dirty=true;commit();panel('model');message('Создан новый предмет '+id+'. Теперь загрузи свой OBJ/PNG и настрой параметры.');});
function transformMatrix(t={}){const p=t.translate||{},r=t.rotate||{},s=t.scale||{};return new THREE.Matrix4().makeTranslation(p.x||0,p.y||0,p.z||0).multiply(new THREE.Matrix4().makeScale(s.x??1,s.y??1,s.z??1)).multiply(new THREE.Matrix4().makeRotationFromEuler(new THREE.Euler(...['x','y','z'].map(k=>THREE.MathUtils.degToRad(r[k]||0)),'YXZ')));}
function updateAttachment(root){if(basic()){root.matrix.copy(Basic.attachmentMatrix(config,root.userData.id,root.userData.slot,root.userData.modelScale));return;}root.matrix.copy(transformMatrix(config.attachmentGroup?.[root.userData.slot])).multiply(transformMatrix(config.attachment?.[root.userData.id]));}
function allAttachmentIds(){return Object.keys(pack.files).filter(p=>/^attachments\/[^/]+\.json$/.test(p)).map(p=>Armory.readJSON(pack,p));}
function buildModList(){const ids=Object.values(gun.type.acceptedAttachments||{}).flat();if(!ids.includes(activeMod))activeMod=ids[0]||'';$('modSelect').replaceChildren();for(const id of ids){const a=Armory.readJSON(pack,`attachments/${id}.json`),o=document.createElement('option');o.value=id;o.textContent=a.displayName||id;$('modSelect').append(o);}$('modSelect').value=activeMod;$('linkMod').replaceChildren();for(const a of allAttachmentIds().filter(a=>!ids.includes(a.internalName)&&Armory.SLOTS.includes(a.attachmentType))){const o=document.createElement('option');o.value=a.internalName;o.textContent=(a.displayName||a.internalName)+' / '+a.attachmentType;$('linkMod').append(o);}buildModControls();}
$('modSelect').onchange=()=>{activeMod=$('modSelect').value;buildModControls();};
function buildModControls(){const box=$('modControls');box.replaceChildren();if(!activeMod){box.textContent='У этого оружия нет обвесов. Импортируй новый или добавь существующий ниже.';return;}
 const path=`attachments/${activeMod}.json`,type=Armory.readJSON(pack,path),slot=type.attachmentType;const save=()=>pack.files[path]=Armory.jsonBytes(type);
 textField(box,'Название обвеса',type.displayName,v=>{type.displayName=v;save();buildModList();loadAttachments().catch(e=>message(e.message,true));});
 checkbox(box,'Установлен по умолчанию',gun.type.defaultAttachments?.[slot]===activeMod,v=>{gun.type.defaultAttachments??={};if(v)gun.type.defaultAttachments[slot]=activeMod;else delete gun.type.defaultAttachments[slot];for(const a of attachmentRoots)if(a.userData.slot===slot){a.visible=v&&a.userData.id===activeMod;a.userData.input.checked=a.visible;}});
 checkbox(box,'Показать этот обвес в 3D',attachmentRoots.find(a=>a.userData.id===activeMod)?.visible,v=>{for(const a of attachmentRoots)if(a.userData.id===activeMod||(v&&a.userData.slot===slot)){a.visible=v&&a.userData.id===activeMod;a.userData.input.checked=a.visible;}});
 const transforms=basic()?(config.attachments??={},config.attachments.positionPointMap??={}):(config.attachment??={});let tr;if(basic()){const pair=transforms[activeMod]??=[{x:0,y:0,z:0},{x:0,y:0,z:0}];tr={translate:pair[0],rotate:pair[1],scale:{x:1,y:1,z:1}};}else tr=transforms[activeMod]??={binding:'gunModel'};tr.translate??={x:0,y:0,z:0};tr.rotate??={x:0,y:0,z:0};tr.scale??={x:1,y:1,z:1};
 numericGroup(box,'Положение обвеса',['x','y','z'],i=>tr.translate[['x','y','z'][i]],(i,v)=>tr.translate[['x','y','z'][i]]=v,{min:-5,max:5,step:.005,unit:'от узла gunModel'});
 numericGroup(box,'Поворот обвеса',['x','y','z'],i=>tr.rotate[['x','y','z'][i]],(i,v)=>tr.rotate[['x','y','z'][i]]=v,{min:-180,max:180,step:1});
 if(!basic())numericGroup(box,'Размер обвеса',['x','y','z'],i=>tr.scale[['x','y','z'][i]],(i,v)=>tr.scale[['x','y','z'][i]]=v,{min:.01,max:10,step:.01});
 if(slot==='sight'){if(basic()){config.attachments.aimPointMap??={};const pair=config.attachments.aimPointMap[activeMod]??=[{x:0,y:0,z:0},{x:0,y:0,z:0}];tr.sightAimPosOffset=pair[0];}tr.sightAimPosOffset??={x:0,y:0,z:0};numericGroup(box,'ADS через прицел',['x','y','z'],i=>tr.sightAimPosOffset[['x','y','z'][i]],(i,v)=>tr.sightAimPosOffset[['x','y','z'][i]]=v,{min:-3,max:3,step:.005});const rp=`attachments/render/${activeMod}.render.json`,rc=Armory.readJSON(pack,rp);rc.sight??={};numberField(box,'Кратность / FOV zoom',rc.sight.fovZoom||1.15,1,12,.05,v=>{rc.sight.fovZoom=v;pack.files[rp]=Armory.jsonBytes(rc);});const note=document.createElement('p');note.className='hint';note.textContent='Предпросмотр показывает геометрию и смещение ADS. PIP, стекло, сетка прицела и эффект увеличения здесь не эмулируются.';box.append(note);}
 const factors={barrel:['recoilPitchFactor','recoilYawFactor','accuracyFactor'],grip:['recoilPitchFactor','recoilYawFactor'],stock:['recoilPitchFactor','recoilYawFactor','aimSpeedFactor'],laser:['recoilPitchFactor','recoilYawFactor','accuracyFactor','aimSpeedFactor']};
 if(factors[slot]){heading(box,'Игровые множители (1 = без изменения)');type[slot]??={};for(const key of factors[slot])numberField(box,key,type[slot][key]??1,.05,5,.05,v=>{type[slot][key]=v;save();});}
 if(slot==='barrel'){checkbox(box,'Глушитель',type.barrel.isSuppressor,v=>{type.barrel.isSuppressor=v;save();});checkbox(box,'Скрыть вспышку',type.barrel.hideFlash,v=>{type.barrel.hideFlash=v;save();});}
 const remove=document.createElement('button');remove.className='wide danger';remove.textContent='Запретить этот обвес для оружия';remove.onclick=safe(async()=>{gun.type.acceptedAttachments[slot]=gun.type.acceptedAttachments[slot].filter(id=>id!==activeMod);if(gun.type.defaultAttachments?.[slot]===activeMod)delete gun.type.defaultAttachments[slot];buildModList();await loadAttachments();commit();});box.append(remove);
}
$('linkAttachment').onclick=safe(async()=>{const id=$('linkMod').value;if(!id)throw Error('Нет других обвесов в паке');const a=Armory.readJSON(pack,`attachments/${id}.json`);gun.type.acceptedAttachments??={};(gun.type.acceptedAttachments[a.attachmentType]??=[]).push(id);if(basic()){config.attachments??={};config.attachments.positionPointMap??={};config.attachments.positionPointMap[id]??=[{x:0,y:0,z:0},{x:0,y:0,z:0}];}else{config.attachment??={};config.attachment[id]??={binding:'gunModel'};}activeMod=id;buildModList();await loadAttachments();commit();message('Совместимость добавлена. Теперь настрой положение обвеса.');});
$('goImportMod').onclick=()=>{panel('model');$('importKind').value='attachment';$('attachmentIdentity').hidden=false;$('objLength').value='.2';};
$('importKind').onchange=()=>{$('attachmentIdentity').hidden=$('importKind').value!=='attachment';$('objLength').value=basic()?($('importKind').value==='gun'?'16':'4'):($('importKind').value==='gun'?'.64':'.2');};
$('objFiles').onchange=safe(async()=>{
 $('applyOBJ').disabled=true;importData=null;const files=[...$('objFiles').files];if(files.length>16)throw Error('Выбери один OBJ, один MTL и до 8 PNG');const obj=files.filter(f=>/\.obj$/i.test(f.name)),mtls=files.filter(f=>/\.mtl$/i.test(f.name)),pngs=files.filter(f=>/\.png$/i.test(f.name));if(obj.length!==1)throw Error('Выбери ровно один OBJ вместе с его PNG и MTL');if(obj[0].size>Armory.LIMITS.objBytes||mtls.some(f=>f.size>1024*1024)||pngs.some(f=>f.size>20*1024*1024))throw Error('Один из файлов слишком большой');if(!pngs.length||pngs.length>8||mtls.length>1)throw Error('Нужны 1–8 PNG и не больше одного MTL');
 const parsed=Armory.parseOBJ(await obj[0].text()),mtl=mtls.length?Armory.parseMTL(await mtls[0].text()):{},images={};for(const f of pngs){if(images[f.name])throw Error('Совпадающие имена PNG');const bytes=new Uint8Array(await f.arrayBuffer());Armory.pngSize(bytes);images[f.name]=bytes;}
 const mapping={};$('materialControls').replaceChildren();for(const mat of parsed.materials){const guess=pngs.find(f=>f.name.toLowerCase()===(mtl[mat]||'').toLowerCase())||pngs.find(f=>f.name.replace(/\.png$/i,'').toLowerCase()===mat.toLowerCase())||pngs[0];mapping[mat]=guess.name;selectField($('materialControls'),mat,pngs.map(f=>[f.name,f.name]),guess.name,v=>mapping[mat]=v);}
 importData={parsed,images,mapping};$('objInfo').textContent=`${obj[0].name}: ${parsed.vertices} вершин, ${parsed.triangles.length} треугольников, ${parsed.materials.length} материалов. Проверь назначения PNG.`;$('applyOBJ').disabled=false;message('OBJ разобран. Укажи направление ствола и нажми «Применить импорт».');
});
async function atlasTexture(data){
 const names=[...new Set(Object.values(data.mapping))];if(names.length===1){return {bytes:data.images[names[0]],rects:Object.fromEntries(data.parsed.materials.map(m=>[m,[0,0,1,1]]))};}
 for(const t of data.parsed.triangles)for(const v of t.v)if(v.uv.some(n=>n<0||n>1))throw Error('Несколько PNG: UV должны лежать в 0..1. Повторяющиеся UV сначала запеки в Blender.');
 const sizes=names.map(n=>Armory.pngSize(data.images[n])),cols=Math.ceil(Math.sqrt(names.length)),rows=Math.ceil(names.length/cols),cw=Math.max(...sizes.map(s=>s[0]))+4,ch=Math.max(...sizes.map(s=>s[1]))+4,pot=n=>2**Math.ceil(Math.log2(n)),w=pot(cols*cw),h=pot(rows*ch);if(w>4096||h>4096)throw Error('Атлас превышает 4096². Уменьши исходные PNG. Редактор не снижает качество молча.');
 const c=document.createElement('canvas');c.width=w;c.height=h;const ctx=c.getContext('2d');ctx.imageSmoothingEnabled=false;const rectsByName={};for(const [i,name] of names.entries()){const img=await createImageBitmap(new Blob([data.images[name]],{type:'image/png'})),x=i%cols*cw+2,y=Math.floor(i/cols)*ch+2;ctx.drawImage(img,x,y);ctx.drawImage(img,0,0,img.width,1,x,y-2,img.width,2);ctx.drawImage(img,0,img.height-1,img.width,1,x,y+img.height,img.width,2);ctx.drawImage(img,0,0,1,img.height,x-2,y,2,img.height);ctx.drawImage(img,img.width-1,0,1,img.height,x+img.width,y,2,img.height);rectsByName[name]=[x/w,y/h,img.width/w,img.height/h];img.close();}
 const blob=await new Promise(r=>c.toBlob(r,'image/png'));return {bytes:new Uint8Array(await blob.arrayBuffer()),rects:Object.fromEntries(Object.entries(data.mapping).map(([m,name])=>[m,rectsByName[name]]))};
}
$('applyOBJ').onclick=safe(async()=>{if(!importData)throw Error('Сначала выбери файлы');const kind=$('importKind').value,id=gun.type.internalName,modId=$('newModId').value.trim();if(kind==='attachment')Armory.validId(modId);if(kind==='ammo'&&!activeAmmo)throw Error('Выбранное оружие не использует магазины');if(!confirm(kind==='gun'?'Заменить геометрию текущего оружия? Сохрани проект, если нужна резервная копия.':kind==='ammo'?'Заменить модель выбранного магазина? Изменение общее для всех пушек с этим ID магазина.':'Создать новый обвес и разрешить его для текущего оружия?'))return;
 $('applyOBJ').disabled=true;try{message('Преобразую OBJ и собираю текстуры…');await new Promise(r=>setTimeout(r,30));const atlas=await atlasTexture(importData),triangles=Armory.orientTriangles(importData.parsed,{forward:$('objForward').value,length:Number($('objLength').value),center:$('objCenter').checked});await flushCurrent();const next=kind==='gun'?Armory.replaceModel(pack,id,triangles,atlas.bytes,atlas.rects):kind==='ammo'?Armory.replaceMagazine(pack,id,activeAmmo,triangles,atlas.bytes,atlas.rects):Armory.addAttachment(pack,id,modId,$('newModName').value,$('newModSlot').value,triangles,atlas.bytes,atlas.rects);pack=next;await openGun(id);dirty=true;commit();if(kind==='attachment'){activeMod=modId;buildModList();panel('mods');const root=attachmentRoots.find(a=>a.userData.id===modId);if(root){root.visible=true;root.userData.input.checked=true;}}message('Импорт готов. Модель/текстуры встроены в проект. Проверь хват и ADS, затем экспортируй ZIP.');}finally{$('applyOBJ').disabled=false;}
});
function projectObject(){validateEdits(edits);Armory.validateType(gun.type);return {format:'MWF-Armory-Studio',version:1,gun:gun.type.internalName,packBase64:bytesToBase64(zipSync(pack.files,{level:3})),edits:structuredClone(edits),config:structuredClone(config),type:structuredClone(gun.type),model:structuredClone(modelEdit),preview:{...preview,ammoId:activeAmmo}};}
async function restoreProject(p){if(p.format!=='MWF-Armory-Studio'||p.version!==1)throw Error('Неподдерживаемый формат проекта MWF Armory Studio');validateEdits(p.edits);Armory.validateType(p.type);if(p.gun!==p.type.internalName||!p.config||(!Basic.isBasic(p.type)&&(!p.config.animations||!p.config.global)))throw Error('Повреждён проект');Armory.modelTRS({},p.model);if(typeof p.packBase64!=='string')throw Error('Повреждён проект: нет данных ZIP');const candidate=loadPack(fromBase64(p.packBase64));if(!candidate.guns.some(g=>g.type.internalName===p.gun))throw Error('В проекте отсутствует выбранная пушка');const testGun=selectGun(candidate,p.gun);Armory.exportWorkspace(candidate,testGun,p.type,p.config,p.edits,p.model);await openPackBytes(candidate.bytes,p.gun);edits=p.edits;config=p.config;gun.type=p.type;modelEdit=p.model;if(gun.type.acceptedAmmo?.includes(p.preview?.ammoId))activeAmmo=p.preview.ammoId;updateRounds();buildHandControls();buildWeaponControls();buildStudio();await loadAttachments();await loadMagazine();resetHistory();dirty=true;commit();message('Проект восстановлен. Все пушки, ресурсы и настройки на месте.');}
async function localStore(mode,value){return new Promise((resolve,reject)=>{const request=indexedDB.open('MWF-Armory-Studio',1);request.onupgradeneeded=()=>request.result.createObjectStore('projects');request.onerror=()=>reject(Error('Хранилище недоступно. Используй файл проекта.'));request.onsuccess=()=>{const db=request.result,tx=db.transaction('projects',mode==='save'?'readwrite':'readonly'),store=tx.objectStore('projects'),r=mode==='save'?store.put(value,'latest'):store.get('latest');let result;r.onsuccess=()=>result=r.result;tx.oncomplete=()=>{db.close();resolve(result);};tx.onerror=()=>{db.close();reject(Error('Не удалось сохранить/открыть: проверь квоту браузера. Используй файл проекта.'));};};});}
$('saveLocal').onclick=safe(async()=>{await localStore('save',projectObject());message('Сохранено в браузере. Для переноса на другой ПК сохрани также файл проекта.');});
$('loadLocal').onclick=safe(async()=>{const p=await localStore('load');if(!p)throw Error('В этом браузере ещё нет сохранения');if(dirty&&!confirm('Восстановить сохранение вместо текущего проекта?'))return;await restoreProject(p);});
$('shootPreview').onclick=()=>{if(!preview.rounds){message('Пусто. Увеличь число патронов справа для нового теста.');return;}preview.rounds--;$('rounds').value=preview.rounds;preview.action='fire';$('action').value='fire';time=0;playing=false;message('Выстрел: осталось '+preview.rounds+(basic()?'. BASIC: это счётчик просмотра; игровую видимость патронов определяет штатный Java-рендерер.':'. Видимость работает только для отдельных групп bulletModel_N в модели.'));};
$('replaceSkin').onclick=()=>$('assetSkinInput').click();$('replaceIcon').onclick=()=>$('iconInput').click();$('importSound').onclick=()=>$('soundInput').click();
$('assetSkinInput').onchange=safe(async()=>{const f=$('assetSkinInput').files[0];if(!f)return;if(f.size>20*1024*1024)throw Error('PNG больше 20 МБ');const bytes=new Uint8Array(await f.arrayBuffer());Armory.pngSize(bytes);const tex=await textureFromBytes(bytes),id=gun.type.internalName,skin=id+'_armory_skin';gun.type.modelSkins=[{internalName:id,skinAsset:skin,displayName:'Основная'}];pack.files[`assets/modularwarfare/skins/guns/${skin}.png`]=bytes;gun.texturePath=`assets/modularwarfare/skins/guns/${skin}.png`;rig.traverse(o=>{if(o.isMesh&&!Object.values(HAND_NAMES).flat().includes(o.name)&&!(()=>{let p=o.parent;while(p){if(p===magazineRoot)return true;p=p.parent;}return false;})()&&!attachmentRoots.some(root=>{let p=o.parent;while(p){if(p===root)return true;p=p.parent;}return false;}))o.material.map=tex;});dirty=true;resetHistory();dirty=true;commit();message('Текстура заменена для выбранной пушки. Это граница истории отмены; проект/экспорт сохранят PNG.');$('assetSkinInput').value='';});
function storeIcon(bytes){const name=gun.type.internalName+'_armory_icon';gun.type.iconName=name;pack.files[`assets/modularwarfare/textures/items/${name}.png`]=bytes;pack.files[`assets/modularwarfare/models/item/${gun.type.internalName}.json`]=Armory.jsonBytes({parent:'item/generated',textures:{layer0:'modularwarfare:items/'+name},display:{thirdperson_righthand:{scale:[0,0,0]},thirdperson_lefthand:{scale:[0,0,0]}}});resetHistory();dirty=true;commit();}
$('iconInput').onchange=safe(async()=>{const f=$('iconInput').files[0];if(!f)return;if(f.size>20*1024*1024)throw Error('PNG больше 20 МБ');const bytes=new Uint8Array(await f.arrayBuffer());Armory.pngSize(bytes);storeIcon(bytes);message('Иконка загружена.');$('iconInput').value='';});
$('captureIcon').onclick=safe(async()=>{if(view!=='orbit')throw Error('Сначала включи «Осмотреть 3D» и настрой ракурс');const saved=[];for(const o of [ground,grid,crosshair,handBox,...Object.values(HAND_NAMES).flat().map(n=>rig.getObjectByName(n)).filter(Boolean)]){saved.push([o,o.visible]);o.visible=false;}const bg=scene.background;scene.background=null;renderer.setClearColor(0x000000,0);try{renderer.render(scene,camera);const c=document.createElement('canvas');c.width=c.height=256;const ctx=c.getContext('2d'),src=renderer.domElement,tmp=document.createElement('canvas');tmp.width=src.width;tmp.height=src.height;const tc=tmp.getContext('2d');tc.drawImage(src,0,0);const pixels=tc.getImageData(0,0,tmp.width,tmp.height).data;let x0=tmp.width,y0=tmp.height,x1=0,y1=0;for(let y=0;y<tmp.height;y++)for(let x=0;x<tmp.width;x++)if(pixels[(y*tmp.width+x)*4+3]>10){x0=Math.min(x0,x);y0=Math.min(y0,y);x1=Math.max(x1,x);y1=Math.max(y1,y);}if(x1<x0||y1<y0)throw Error('Модель не видна в кадре');const w=x1-x0+1,h=y1-y0+1,scale=224/Math.max(w,h);ctx.drawImage(tmp,x0,y0,w,h,(256-w*scale)/2,(256-h*scale)/2,w*scale,h*scale);const blob=await new Promise(r=>c.toBlob(r,'image/png'));storeIcon(new Uint8Array(await blob.arrayBuffer()));message('Иконка 256×256 сохранена в проект.');}finally{for(const [o,v] of saved)o.visible=v;scene.background=bg;renderer.setClearColor(0x000000,1);}});
$('soundInput').onchange=safe(async()=>{const f=$('soundInput').files[0];if(!f)return;if(f.size>12*1024*1024)throw Error('OGG больше 12 МБ');Armory.setSound(pack.files,gun.type,$('soundEvent').value,new Uint8Array(await f.arrayBuffer()));resetHistory();dirty=true;commit();message('Звук привязан к выбранному событию и будет в ZIP.');$('soundInput').value='';});
let soundPlayer,soundURL;
$('listenSound').onclick=safe(async()=>{const list=gun.type.weaponSoundMap?.[$('soundEvent').value];if(!list?.length)throw Error('Для события не задан звук');const key=list[0].soundName,sounds=Armory.readJSON(pack,'assets/modularwarfare/sounds.json'),entry=sounds[key]?.sounds?.[0],name=typeof entry==='string'?entry:entry?.name;if(!name)throw Error('Звук не найден в sounds.json');const path='assets/modularwarfare/sounds/'+name.replace(/^modularwarfare:/,'')+'.ogg',bytes=pack.files[path];if(!bytes)throw Error('Нет файла '+path);soundPlayer?.pause();if(soundURL)URL.revokeObjectURL(soundURL);soundURL=URL.createObjectURL(new Blob([bytes],{type:'audio/ogg'}));soundPlayer=new Audio(soundURL);await soundPlayer.play();});
function buildBulletControls(){
 const box=$('bulletControls');box.replaceChildren();const id=Armory.bulletIds(pack,gun.type,activeAmmo)[0];if(!id){box.textContent='У выбранного магазина нет subAmmo. Назначь патрон в настройках магазина.';return;}const path=`bullets/${id}.json`,b=Armory.readJSON(pack,path),save=()=>pack.files[path]=Armory.jsonBytes(b);const p=document.createElement('p');p.className='hint';p.textContent='Текущий ID: '+id+'. Изменение этого патрона влияет на все пушки, использующие его. Для независимой настройки создай копию.';box.append(p);
 textField(box,'Название патрона',b.displayName,v=>{b.displayName=v;save();});numberField(box,'Множитель урона патрона',b.bulletDamageFactor??1,.01,100,.05,v=>{b.bulletDamageFactor=v;save();});numberField(box,'Множитель точности патрона',b.bulletAccuracyFactor??1,.01,10,.05,v=>{b.bulletAccuracyFactor=v;save();});numberField(box,'Патронов в стаке',b.maxStackSize??64,1,64,1,v=>{b.maxStackSize=v;save();});checkbox(box,'Пуля / slug вместо дроби',b.isSlug,v=>{b.isSlug=v;save();});
}
$('createBullet').onclick=safe(async()=>{Armory.cloneBullet(pack,gun.type,$('newBulletId').value.trim(),$('newBulletName').value.trim(),activeAmmo);buildStats();buildBulletControls();resetHistory();dirty=true;commit();message('Создан новый патрон и назначен текущей пушке. Геометрия/иконка гильзы унаследованы.');});
$('emptyOpenPack').onclick=()=>$('packInput').click();
setWorkspaceEnabled(false);$('loading').hidden=true;$('emptyState').hidden=false;setView('fp');panel('library');resize();
message('Встроенных паков нет. Открой свой ZIP или проект. Ограничений размера ZIP/проекта в программе нет; доступный объём зависит от памяти браузера.');
