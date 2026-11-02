import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import {strFromU8,zipSync} from 'fflate';
import * as A from './armory.mjs';
import {loadPack as coreLoadPack} from './core.mjs';
const baseline=()=>A.loadPack(readFileSync(new URL('../../downloads/Metro_Uboinik_Duplet_MWF_fix1_v2.zip',import.meta.url)));

test('ZIP without built-in download dependency in the build',()=>{
 const code=readFileSync(new URL('./build.mjs',import.meta.url),'utf8');
 assert.ok(!code.includes('__DEFAULT_PACK_B64__'));assert.ok(!code.includes('Metro_Uboinik'));assert.ok(!code.includes('metro_duplet'));
});
test('more than 10000 safe ZIP entries are accepted',()=>{
 const files={...baseline().files};for(let i=0;i<10001;i++)files[`data/${i}.bin`]=new Uint8Array();
 const zipped=zipSync(files,{level:0});assert.ok(Object.keys(A.loadPack(zipped).files).length>10000);assert.ok(Object.keys(coreLoadPack(zipped).files).length>10000);
});
test('more than 160 MiB unpacked payload is accepted',()=>{
 const files={...baseline().files,'data/large.bin':new Uint8Array(161*1024*1024).fill(42)};
 const zipped=zipSync(files,{level:1});const p=A.loadPack(zipped);assert.equal(p.files['data/large.bin'].length,161*1024*1024);assert.equal(p.files['data/large.bin'][0],42);
});
test('real full-arsenal ZIP above 64 MiB opens and exports all unsupported guns unchanged',()=>{
 const bytes=readFileSync(new URL('../../downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip',import.meta.url));assert.ok(bytes.length>64*1024*1024);
 const p=A.loadPack(bytes),g=A.selectGun(p);assert.equal(p.guns.length,23);assert.equal(g.type.internalName,'metro_ashot');
 assert.equal(p.guns.filter(g=>A.editableType(g.type)).length,4);assert.throws(()=>A.selectGun(p,'metro_ak'),/магазин/i);
 A.validateConfig(g.config);const type={...g.type,gunDamage:7};
 const q=A.loadPack(A.exportWorkspace(p,g,type,g.config,A.defaults(),A.modelDefaults()));assert.equal(q.guns.length,23);assert.equal(A.selectGun(q,'metro_ashot').type.gunDamage,7);
 for(const path of Object.keys(p.files).filter(n=>/^(ammo\/|guns\/metro_ak|assets\/modularwarfare\/skins\/)/.test(n)))assert.deepEqual(q.files[path],p.files[path],path);
});
test('zero attachment scales remain valid for native hide/show geometry',()=>{
 const g=A.selectGun(baseline()),c=A.clone(g.config);c.attachment.test_hide={scale:{x:0,y:0,z:0}};assert.equal(A.validateConfig(c),c);
 c.attachment.test_hide.scale.x=-1;assert.throws(()=>A.validateConfig(c));
});
test('preview mask respects default, group and per-item hide/show parts',()=>{
 const cfg={defaultHidePart:['native_mod'],attachmentGroup:{stock:{hidePart:['old_stock']}},attachment:{test_stock:{showPart:['native_mod']}}};
 assert.deepEqual([...A.previewHiddenParts(cfg,[])],['native_mod']);assert.deepEqual([...A.previewHiddenParts(cfg,[{id:'test_stock',slot:'stock'}])],['old_stock']);
});
