import * as THREE from 'three';
import {OrbitControls} from 'three/addons/controls/OrbitControls.js';
import {GLTFLoader} from 'three/addons/loaders/GLTFLoader.js';
import {OBJLoader} from 'three/addons/loaders/OBJLoader.js';
import {strFromU8} from 'fflate';
import {defaults,validateEdits,loadPack,selectGun,bytesToBase64,fromBase64,cameraMatrix,applyPose,reloadWeight,actionAt,exportPack,HAND_NAMES} from './core.mjs';
const $=id=>document.getElementById(id);
let pack,gun,rig,edits=defaults(),config,side='left',editMode='idle',view='fp',playing=false,time=0,dirty=false,skinTexture,attachmentRoots=[],rest=[],tracks=[],token=0;
let undo=[],redo=[],lastSaved='',pending=false;
const preview={fov:70,aspect:16/9,ads:0,slim:false,rounds:6,action:'default'};
function message(text,error=false){$('message').textContent=text;$('message').hidden=!text;$('message').className=error?'error':'';}
function safe(fn){return async(...args)=>{try{await fn(...args);}catch(e){console.error(e);$('loading').hidden=!!rig;if(!rig)$('loading').textContent='Не удалось открыть модель. Выбери другой пак ZIP.';if(gun)$('gunSelect').value=gun.type.internalName;message(e.message||String(e),true);}};}
function download(bytes,name,type='application/octet-stream'){
  const blob=new Blob([bytes],{type}),url=URL.createObjectURL(blob),a=document.createElement('a');a.href=url;a.download=name;document.body.appendChild(a);a.click();a.remove();setTimeout(()=>URL.revokeObjectURL(url),60000);
}
function snapshot(){return JSON.stringify({edits,config});}
function commit(){if(!config)return;const now=snapshot();if(now!==lastSaved){undo.push(lastSaved);undo=undo.slice(-60);redo=[];lastSaved=now;dirty=true;}$('undo').disabled=!undo.length;$('redo').disabled=!redo.length;$('saveStatus').textContent=dirty?'Есть изменения · экспортируй ZIP для игры':'Изменений пока нет';}
function resetHistory(){undo=[];redo=[];lastSaved=snapshot();dirty=false;commit();}
function restoreSnapshot(text){const s=JSON.parse(text);edits=s.edits;config=s.config;lastSaved=text;dirty=true;buildHandControls();buildWeaponControls();commit();}
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
  const box=$('handControls');box.replaceChildren();const edit=()=>edits[side];
  numericGroup(box,'Положение',['x','y','z'],i=>edit()[editMode].p[i],(i,v)=>edit()[editMode].p[i]=v,{min:-1.5,max:1.5,step:.005,unit:'ед. модели'});
  numericGroup(box,'Поворот',['x','y','z'],i=>edit()[editMode].r[i],(i,v)=>edit()[editMode].r[i]=v,{min:-180,max:180,step:1,unit:'градусы'});
  numericGroup(box,'Масштаб руки',['s'],()=>edit().scale,(_,v)=>edit().scale=v,{min:.25,max:2,step:.01,unit:'для всех поз'});
}
function buildWeaponControls(){
  const box=$('weaponControls');box.replaceChildren();config.aim??={};let refreshAim=()=>{};
  for(const [name,key,rotation] of [['Обычное положение','translateHipPosition',false],['Обычный угол','rotateHipPosition',true],['Смещение ADS','translateAimPosition',false]]){
    config.aim[key]??={x:0,y:0,z:0};
    const sync=numericGroup(box,name,['x','y','z'],i=>config.aim[key]['xyz'[i]],(i,v)=>{
      const axis='xyz'[i],old=config.aim[key][axis];config.aim[key][axis]=v;
      if($('keepAds').checked&&key.includes('Hip')){const other=rotation?'rotateAimPosition':'translateAimPosition';config.aim[other]??={x:0,y:0,z:0};config.aim[other][axis]-=v-old;refreshAim();}
    },{min:rotation?-180:-5,max:rotation?180:5,step:rotation?1:.005,unit:rotation?'градусы':'ед. модели'});
    if(key==='translateAimPosition')refreshAim=sync;
  }
}
const scene=new THREE.Scene();scene.background=new THREE.Color('#829ab5');scene.fog=new THREE.Fog('#829ab5',10,35);
const camera=new THREE.PerspectiveCamera(70,16/9,.01,100);camera.position.set(0,0,0);camera.lookAt(0,0,-1);
let renderer;
try{renderer=new THREE.WebGLRenderer({antialias:true,alpha:false,preserveDrawingBuffer:true});}
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
  else{weaponGroup.updateWorldMatrix(true,true);const bounds=new THREE.Box3();rig.traverseVisible(o=>{if(o.isMesh){o.geometry.computeBoundingBox();bounds.union(o.geometry.boundingBox.clone().applyMatrix4(o.matrixWorld));}});const center=bounds.getCenter(new THREE.Vector3()),radius=bounds.getSize(new THREE.Vector3()).length()/2;const distance=Math.max(.1,radius)/Math.sin(THREE.MathUtils.degToRad(preview.fov)/2)*1.25;camera.position.copy(center).add(new THREE.Vector3(.8,.35,.75).normalize().multiplyScalar(distance));controls.target.copy(center);controls.update();}
  $('viewHint').textContent=view==='fp'?'Предпросмотр MWF, не запущенный Minecraft':'Мышь — поворот · колесо — масштаб · ПКМ — перемещение';
}
function textureFromBytes(bytes){return new Promise((resolve,reject)=>{const url=URL.createObjectURL(new Blob([bytes],{type:'image/png'}));new THREE.TextureLoader().load(url,t=>{URL.revokeObjectURL(url);t.flipY=false;t.colorSpace=THREE.SRGBColorSpace;t.magFilter=THREE.NearestFilter;t.minFilter=THREE.NearestFilter;t.wrapS=t.wrapT=THREE.RepeatWrapping;resolve(t);},undefined,()=>{URL.revokeObjectURL(url);reject(Error('Не удалось прочитать PNG'));});});}
function disposeRig(){if(rig){rig.traverse(o=>{o.geometry?.dispose();if(o.material){for(const m of [].concat(o.material)){if(m.map!==skinTexture)m.map?.dispose();m.dispose();}}});weaponGroup.remove(rig);}for(const a of attachmentRoots)a.removeFromParent();attachmentRoots=[];}
async function openGun(id){
  const mine=++token;playing=false;time=0;$('play').textContent='▶';$('loading').hidden=false;message('');
  const sourcePack=pack,selected=selectGun(sourcePack,id);
  // MWF binds its own atlas: discard embedded material image URIs to keep imports offline.
  const displayGltf=structuredClone(selected.gltf);delete displayGltf.images;delete displayGltf.textures;
  displayGltf.materials=(displayGltf.materials||[]).map(m=>({name:m.name,doubleSided:true}));
  const loaded=await new GLTFLoader().parseAsync(JSON.stringify(displayGltf),'');const tex=await textureFromBytes(sourcePack.files[selected.texturePath]);
  if(mine!==token)return;disposeRig();gun=selected;config=structuredClone(gun.config);edits=defaults();rig=loaded.scene;weaponGroup.add(rig);rest=[];tracks=[];
  rig.traverse(o=>{rest.push({o,p:o.position.clone(),q:o.quaternion.clone(),s:o.scale.clone(),visible:o.visible});
    if(o.isMesh){const hand=Object.values(HAND_NAMES).flat().includes(o.name);o.material=new THREE.MeshLambertMaterial({map:hand?skinTexture:tex,side:THREE.DoubleSide,alphaTest:.05});}
  });
  for(const clip of loaded.animations)for(const track of clip.tracks){const split=track.name.lastIndexOf('.'),name=track.name.slice(0,split),property=track.name.slice(split+1);const o=rig.getObjectByName(name)||rig.getObjectByProperty('uuid',name);if(o&&['position','quaternion','scale'].includes(property))tracks.push({o,property,interpolant:track.createInterpolant()});}
  $('gunTitle').textContent=gun.type.displayName||gun.type.internalName;$('gunSelect').value=gun.type.internalName;
  $('rounds').max=gun.type.internalAmmoStorage||6;preview.rounds=Number($('rounds').max);$('rounds').value=preview.rounds;
  $('action').value='default';preview.action='default';buildHandControls();buildWeaponControls();await loadAttachments();resetHistory();$('loading').hidden=true;resize();
  if(gun.present.length<4)message('В модели найдены не все варианты рук. Изменятся только существующие узлы.');
}
async function loadAttachments(){
  $('attachments').replaceChildren();
  for(const [slot,ids] of Object.entries(gun.type.acceptedAttachments||{}))for(const id of ids){
    try{
      const type=JSON.parse(strFromU8(pack.files[`attachments/${id}.json`])),cfg=JSON.parse(strFromU8(pack.files[`attachments/render/${id}.render.json`]));const data=pack.files['obj/attachments/'+cfg.modelFileName];if(!data)continue;
      const object=new OBJLoader().parse(strFromU8(data)),skin=type.modelSkins?.[0],mapPath=`assets/modularwarfare/skins/attachments/${skin?.skinAsset||skin?.internalName||id}.png`;
      const tex=type.sameTextureAsGun?await textureFromBytes(pack.files[gun.texturePath]):pack.files[mapPath]?await textureFromBytes(pack.files[mapPath]):null;
      if(tex){tex.flipY=true;tex.needsUpdate=true;} // MWF's OBJ loader flips V.
      object.traverse(o=>{if(o.isMesh)o.material=new THREE.MeshLambertMaterial({map:tex,color:tex?'#ffffff':'#ce9b58',side:THREE.DoubleSide});});
      const t=gun.config.attachment?.[id]||{},binding=rig.getObjectByName(t.binding||'gunModel');if(!binding)continue;
      const wrapper=new THREE.Group();wrapper.add(object);const trans=t.translate||{},rot=t.rotate||{},s=t.scale||{};wrapper.position.set(trans.x||0,trans.y||0,trans.z||0);wrapper.rotation.set(THREE.MathUtils.degToRad(rot.x||0),THREE.MathUtils.degToRad(rot.y||0),THREE.MathUtils.degToRad(rot.z||0),'YXZ');wrapper.scale.set(s.x??1,s.y??1,s.z??1);wrapper.visible=gun.type.defaultAttachments?.[slot]===id;wrapper.userData.slot=slot;binding.add(wrapper);attachmentRoots.push(wrapper);
      const label=document.createElement('label');label.className='switch';const text=document.createElement('span');text.textContent=type.displayName||id;const input=document.createElement('input');input.type='checkbox';input.checked=wrapper.visible;wrapper.userData.input=input;input.onchange=()=>{wrapper.visible=input.checked;if(input.checked)for(const other of attachmentRoots)if(other!==wrapper&&other.userData.slot===slot){other.visible=false;other.userData.input.checked=false;}};label.append(text,input);$('attachments').append(label);
    }catch(e){console.warn('Обвес не показан:',id,e.message);}
  }
}
async function openPackBytes(bytes,id){const candidate=loadPack(bytes);selectGun(candidate,id);const previous=pack;pack=candidate;try{await openGun(id);}catch(e){pack=previous;throw e;}$('gunSelect').replaceChildren();for(const g of pack.guns){const o=document.createElement('option');o.value=g.type.internalName;o.textContent=g.type.displayName||o.value;$('gunSelect').append(o);}$('gunSelect').value=gun.type.internalName;}
function render(now){requestAnimationFrame(render);if(!rig||!config)return;
  const dt=Math.min((now-(render.last||now))/1000,.1);render.last=now;
  let sample=actionAt(config,preview.action,time,preview.rounds);
  if(playing){time+=dt;if(time>sample.duration)time=0;sample=actionAt(config,preview.action,time,preview.rounds);}
  $('timeline').max=sample.duration;$('timeline').value=time;$('timeLabel').textContent=`${time.toFixed(2)} / ${sample.duration.toFixed(2)} с`;
  for(const r of rest){r.o.position.copy(r.p);r.o.quaternion.copy(r.q);r.o.scale.copy(r.s);}
  for(const track of tracks)track.o[track.property].fromArray(track.interpolant.evaluate(sample.frame/(config.FPS||24)));
  const weight=reloadWeight(sample.frame,gun.config);
  for(const currentSide of ['left','right'])for(const name of HAND_NAMES[currentSide]){
    const o=rig.getObjectByName(name);if(!o)continue;
    const result=applyPose({translation:o.position.toArray(),rotation:o.quaternion.toArray(),scale:o.scale.toArray()},edits[currentSide],weight);
    o.position.fromArray(result.translation);o.quaternion.fromArray(result.rotation);o.scale.fromArray(result.scale);o.visible=name.includes('Slim')===preview.slim&&!name.includes('Layer');
  }
  rig.traverse(o=>{if(/^bulletModel_\d+$/.test(o.name))o.visible=Number(o.name.split('_').pop())<(preview.action==='reload'?sample.rounds:preview.rounds);if(o.name==='flashModel'||/^shellModel_\d+$/.test(o.name))o.visible=false;});
  weaponGroup.matrix.copy(cameraMatrix(config,preview.ads));scene.updateMatrixWorld(true);
  if($('highlight').checked){const target=rig.getObjectByName(side+'Arm'+(preview.slim?'Slim':'')+'Model');if(target){handBox.setFromObject(target);handBox.visible=true;}}else handBox.visible=false;
  $('stateBadge').textContent=(preview.ads?'ADS / ПРИЦЕЛИВАНИЕ':'ОБЫЧНЫЙ ХВАТ')+(preview.action==='reload'?' · ЗАРЯДКА':'');
  if(view==='orbit')controls.update();renderer.render(scene,camera);
}
requestAnimationFrame(render);
for(const button of $('handTabs').children)button.onclick=()=>{side=button.dataset.side;for(const b of $('handTabs').children)b.classList.toggle('selected',b===button);buildHandControls();};
for(const button of $('viewTabs').children)button.onclick=()=>setView(button.dataset.view);
$('resetView').onclick=()=>setView(view);
$('editMode').onchange=()=>{editMode=$('editMode').value;if(editMode==='reload'){$('action').value='reload';preview.action='reload';time=actionAt(config,'reload',0,preview.rounds).duration*.35;}else{$('action').value='default';preview.action='default';time=0;}playing=false;$('play').textContent='▶';buildHandControls();};
$('ads').onchange=()=>{preview.ads=$('ads').checked?1:0;};
$('bodyType').onchange=()=>preview.slim=$('bodyType').value==='slim';
$('aspect').onchange=()=>{preview.aspect=$('aspect').value==='auto'?'auto':Number($('aspect').value);resize();};
numericGroup($('fovControl'),'Поле зрения',['°'],()=>preview.fov,(_,v)=>{preview.fov=v;resize();},{min:40,max:110,step:1,unit:'FOV'});
$('rounds').oninput=()=>{preview.rounds=THREE.MathUtils.clamp(Math.round(Number($('rounds').value)||0),0,Number($('rounds').max));time=0;};
$('action').onchange=()=>{preview.action=$('action').value;time=0;};
$('timeline').oninput=()=>{time=Number($('timeline').value);playing=false;$('play').textContent='▶';};
$('play').onclick=()=>{playing=!playing;$('play').textContent=playing?'Ⅱ':'▶';};
$('resetHand').onclick=()=>{edits[side]=defaults()[side];buildHandControls();commit();};
$('resetWeapon').onclick=()=>{config.aim=structuredClone(gun.config.aim);buildWeaponControls();commit();};
$('keepAds').onchange=()=>buildWeaponControls();
$('undo').onclick=()=>{if(!undo.length)return;redo.push(snapshot());restoreSnapshot(undo.pop());};
$('redo').onclick=()=>{if(!redo.length)return;undo.push(snapshot());restoreSnapshot(redo.pop());};
$('openPack').onclick=()=>$('packInput').click();$('openProject').onclick=()=>$('projectInput').click();$('openSkin').onclick=()=>$('skinInput').click();
$('packInput').onchange=safe(async()=>{const f=$('packInput').files[0];if(f&&(!dirty||confirm('Заменить текущий пак? Несохранённые изменения будут потеряны.')))await openPackBytes(new Uint8Array(await f.arrayBuffer()));$('packInput').value='';});
$('gunSelect').onchange=safe(async()=>{if(!dirty||confirm('Переключить оружие и сбросить текущие изменения?'))await openGun($('gunSelect').value);});
$('skinInput').onchange=safe(async()=>{const f=$('skinInput').files[0];if(!f)return;const bitmap=await createImageBitmap(f);if(bitmap.width!==64||bitmap.height!==64)throw Error('Нужен скин PNG размером 64×64');bitmap.close();const old=skinTexture;skinTexture=await textureFromBytes(new Uint8Array(await f.arrayBuffer()));rig?.traverse(o=>{if(Object.values(HAND_NAMES).flat().includes(o.name)&&o.isMesh)o.material.map=skinTexture;});old.dispose();message('Скин используется только для предпросмотра. В игре будет скин игрока.');});
$('saveProject').onclick=safe(async()=>{commit();const project={format:'MWF-Pose-Studio',version:1,gun:gun.type.internalName,packBase64:bytesToBase64(pack.bytes),edits,config,preview};download(JSON.stringify(project),gun.type.internalName+'.pose-project.json','application/json');message('Проект сохранён. Это не контент-пак: для игры нужен экспорт ZIP.');});
$('projectInput').onchange=safe(async()=>{const f=$('projectInput').files[0];if(!f)return;if(f.size>90*1024*1024)throw Error('Проект слишком большой');const p=JSON.parse(await f.text());if(p.format!=='MWF-Pose-Studio'||p.version!==1)throw Error('Это не проект MWF Pose Studio');validateEdits(p.edits);if(!p.config?.animations)throw Error('В проекте нет render-конфигурации');await openPackBytes(fromBase64(p.packBase64),p.gun);edits=p.edits;config=p.config;buildHandControls();buildWeaponControls();resetHistory();dirty=true;commit();message('Проект открыт. Настройки предпросмотра можно выбрать заново.');$('projectInput').value='';});
$('exportPack').onclick=safe(async()=>{
  if(pending)return;pending=true;$('exportPack').disabled=true;message('Собираю ZIP…');
  try{await new Promise(r=>setTimeout(r,30));const result=exportPack(pack,gun,edits,config);download(result,gun.type.internalName+'_POSE_EDIT.zip','application/zip');message('ZIP готов. Уберите старый пак с теми же предметами, установите экспорт и полностью перезапустите Minecraft. Для сервера нужен тот же ZIP.');dirty=false;commit();}finally{pending=false;$('exportPack').disabled=false;}
});
window.addEventListener('beforeunload',e=>{if(dirty){e.preventDefault();e.returnValue='';}});
// Expose a read-only test bridge, not an external API or network service.
window.mwfEditor={get ready(){return !!rig&&$('loading').hidden;},get gun(){return gun?.type.internalName;},get edits(){return structuredClone(edits);},get config(){return structuredClone(config);},exportBytes:()=>exportPack(pack,gun,edits,config)};
safe(async()=>{await openPackBytes(fromBase64(__DEFAULT_PACK_B64__),__DEFAULT_GUN__);setView('fp');message('Готово. Встроенный пак: '+pack.guns.map(g=>g.type.displayName).join(', ')+'. Исходный ZIP не изменяется.');})();
