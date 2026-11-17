// Same 20 Hz state machine as RotorRules.java. Only custom marker wrappers change;
// original hand nodes, animation samples and the exporter remain untouched.
export function rotorTick(s,pressed,motor=false){
 const before=s.speed;s.speed=Math.max(0,Math.min(1,s.speed+(pressed?(motor?.1:.05):-1/30)));
 s.angle=(s.angle+(before+s.speed)*.5*60)%360;return s;
}
export function rotorRpm(s,five=false){return Math.round((five?600:400)*(.25+.75*s.speed));}
export function motionFrame(rig,selected,dt,pressed=false){
 const ids=new Set(selected.map(a=>a.id)),state=rig.userData.metroRotor??={speed:0,angle:0,accum:0};
 state.accum+=Math.max(0,Math.min(.1,dt));
 while(state.accum>=.05-1e-10){rotorTick(state,pressed,ids.has('metro_mod_gatling_motor'));state.accum=Math.max(0,state.accum-.05);}
 rig.traverse(o=>{
  const p=o.userData?.metroMotion;if(!p)return;
  o.updateMatrix();o.matrixAutoUpdate=false;
  if(p.rotor)o.matrix.multiply(new o.matrix.constructor().makeRotationX((state.angle+state.accum*1200*state.speed)*Math.PI/180));
  else if(ids.has(p.attachment))o.matrix.multiply(new o.matrix.constructor().fromArray(p.matrix));
 });
 return {speed:state.speed,angle:state.angle,rpm:state.speed>=.25-1e-8?1200/Math.ceil(1200/rotorRpm(state,ids.has('metro_mod_gatling_barrel'))-1e-8):0};
}
export function rotorControls(document){
 const box=document.createElement('div');box.id='motionControls';box.hidden=true;
 const button=document.createElement('button');button.id='rotorPreview';button.textContent='Удерживай: раскрутка пулемёта';button.className='wide';
 const output=document.createElement('output');output.id='rotorStatus';output.style.display='block';output.style.padding='10px 0';
 let down=false;
 button.onpointerdown=e=>{if(e.button!==0)return;down=true;button.setPointerCapture(e.pointerId);};
 button.onpointerup=button.onpointercancel=button.onlostpointercapture=()=>down=false;
 button.onkeydown=e=>{if(e.code==='Space'||e.code==='Enter'){e.preventDefault();down=true;}};
 button.onkeyup=button.onblur=()=>down=false;
 document.defaultView.addEventListener('blur',()=>down=false);
 box.append(button,output);document.getElementById('rounds').after(box);
 return {get pressed(){return down;},update(gun,s){box.hidden=gun!=='metro_gatling';output.textContent=`Раскрутка ${Math.round(s.speed*100)}% · ${Math.round(s.rpm)} выстр./мин (предпросмотр)`;},reset(){down=false;}};
}
