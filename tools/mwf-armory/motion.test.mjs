import test from 'node:test';
import assert from 'node:assert/strict';
import * as THREE from 'three';
import {rotorTick,motionFrame} from '../mwf-motion/preview.mjs';
const close=(a,b)=>assert.ok(Math.abs(a-b)<1e-6,`${a} != ${b}`);
test('rotor fixed-tick warmup, max speed, motor and coast mirror Java rules',()=>{
 const s={speed:0,angle:0};for(let i=0;i<20;i++)rotorTick(s,true);close(s.speed,1);const angle=s.angle;
 rotorTick(s,false);assert.ok(s.speed>0&&s.speed<1);assert.notEqual(s.angle,angle);
 for(let i=0;i<30;i++)rotorTick(s,false);close(s.speed,0);
 const m={speed:0,angle:0};for(let i=0;i<10;i++)rotorTick(m,true,true);close(m.speed,1);
});
test('stock wrappers change both classic/slim identically and exactly restore original poses',()=>{
 const root=new THREE.Group(),wrap=new THREE.Group();wrap.name='metroMotionRevolverLeft';wrap.userData.metroMotion={attachment:'metro_mod_butt',matrix:new THREE.Matrix4().makeTranslation(.1,.05,-.02).toArray()};root.add(wrap);
 const a=new THREE.Group(),b=new THREE.Group();a.name='leftArmModel';b.name='leftArmSlimModel';a.position.set(.2,.1,.3);b.position.copy(a.position);wrap.add(a,b);
 motionFrame(root,[],.016);root.updateMatrixWorld(true);const before=a.matrixWorld.clone();
 motionFrame(root,[{id:'metro_mod_butt'}],.016);root.updateMatrixWorld(true);const withStock=a.matrixWorld.clone();assert.notDeepEqual(withStock.elements,before.elements);assert.deepEqual(a.matrixWorld.elements,b.matrixWorld.elements);
 motionFrame(root,[{id:'metro_mod_butt'}],.016);root.updateMatrixWorld(true);assert.deepEqual(a.matrixWorld.elements,withStock.elements,'no accumulated motion');
 motionFrame(root,[],.016);root.updateMatrixWorld(true);assert.deepEqual(a.matrixWorld.elements,before.elements,'no-stock exact restoration');assert.deepEqual(a.position.toArray(),[.2,.1,.3],'authored node unchanged');
});
test('rotor frame-rate invariance, correct wrapper only and resting pivot preservation',()=>{
 const rig=()=>{const r=new THREE.Group(),w=new THREE.Group(),body=new THREE.Group();w.name='metroMotionRotor';w.position.set(.13,.14,.16);w.userData.metroMotion={rotor:true};r.add(w,body);return r;};
 const a=rig(),b=rig();for(let i=0;i<60;i++)motionFrame(a,[],1/60,true);for(let i=0;i<20;i++)motionFrame(b,[],1/20,true);
 close(a.userData.metroRotor.speed,b.userData.metroRotor.speed);close(a.userData.metroRotor.angle,b.userData.metroRotor.angle);
 close(a.children[0].matrix.elements[12],.13);assert.deepEqual(a.children[1].rotation.toArray(),[0,0,0,'XYZ']);
 const motor=rig();for(let i=0;i<10;i++)motionFrame(motor,[{id:'metro_mod_gatling_motor'}],.05,true);close(motor.userData.metroRotor.speed,1);
});
