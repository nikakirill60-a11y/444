import test from 'node:test';
import assert from 'node:assert/strict';
import {createHash} from 'node:crypto';
import {prepareFiles,selectGameFiles,digest,inventory,uploadPrepared,CHUNK_BYTES} from '../website/publisher-core.js';
import {Accounts,AUTH_URL,PUBLIC_KEY} from '../website/accounts.js';
const file=(path,text)=>{const f=new File([text],path.split('/').at(-1));Object.defineProperty(f,'webkitRelativePath',{value:'game/'+path});return f;};
const basic=()=>[file('mods/metrocraft-port-test.jar','minetro'),file('mods/mwf.jar','mwf'),file('config/default.cfg','defaults')];
const session={id:'11111111-1111-4111-8111-111111111111',token:'unit-test-only',expires:Date.now()+3600000};
function transport(){const objects=new Map(),calls=[];return {objects,calls,api:{async rpc(name,{requested}){calls.push(name);assert.equal(name,'owner_chunk_inventory');return requested.filter(c=>objects.get(c.sha256)?.length===c.bytes).map(c=>c.sha256);},async uploadDevChunk(sha,bytes){assert.equal(digest(bytes),sha);objects.set(sha,bytes);}}};}
test('vendored SHA256 agrees with Node on boundary sizes',()=>{
 for(const n of [0,1,55,56,63,64,65,1000,CHUNK_BYTES+1]){const b=new Uint8Array(n).fill(37);assert.equal(digest(b),createHash('sha256').update(b).digest('hex'));}
});
test('selection never reads worlds, screenshots, packv4 and personal options',()=>{
 const hidden=[file('saves/world/level.dat','private'),file('screenshots/x.png','private'),file('options.txt','private'),file('ModularWarfare/packv4.json','private'),file('ModularWarfare/mod_config.json','private')];
 assert.equal(selectGameFiles([...basic(),...hidden]).length,3);
});
test('nested .minecraft instance root is accepted',()=>{
 const files=basic().map(f=>{const x=new File([f],f.name);Object.defineProperty(x,'webkitRelativePath',{value:f.webkitRelativePath.replace('game/','instance/.minecraft/')});return x;});
 assert.equal(selectGameFiles(files).length,3);
});
test('unsafe/colliding paths and incomplete packs fail before transfer',()=>{
 for(const path of ['mods/../evil.jar','mods/CON.jar','mods/x:ads.jar','mods/foo.','mods//x.jar','mods/MWF.jar'])assert.throws(()=>selectGameFiles([...basic(),file(path,'bad')]));
 assert.throws(()=>selectGameFiles([file('mods/metrocraft-port-test.jar','one')]));
});
test('file hashes and chunks match, config seed-only, no network in preparation',async()=>{
 const p=await prepareFiles(basic(),'dev-1');assert.equal(p.manifest.schema,2);assert.equal(p.chunks.size,3);
 for(const f of p.manifest.files){assert.equal(f.sha256,f.chunks[0].sha256);assert.equal(f.mode,f.path.startsWith('config/')?'seed':'managed');}
 assert.equal(p.manifest.files[0].path,'config/default.cfg');
});
test('large files are bounded chunks, duplicate blocks deduplicated',async()=>{
 const large=new Uint8Array(CHUNK_BYTES*2+2).fill(120),p=await prepareFiles([...basic(),file('scripts/large.zs',large)],'dev-1');
 const f=p.manifest.files.find(f=>f.path==='scripts/large.zs');assert.equal(f.chunks.length,3);assert.deepEqual(f.chunks[0],f.chunks[1]);
 assert.equal(f.sha256,createHash('sha256').update(large).digest('hex'));assert.equal(p.chunks.size,5);
});
test('retry uploads zero bytes and changed file uploads only new content, never activates',async()=>{
 const t=transport(),p=await prepareFiles(basic(),'dev-1');
 const first=await uploadPrepared(t.api,p,await inventory(t.api,p.chunks));assert.equal(first.sent,18);
 assert.equal((await uploadPrepared(t.api,p,await inventory(t.api,p.chunks))).sent,0);
 const f=basic();f[1]=file('mods/mwf.jar','updated');const second=await prepareFiles(f,'dev-2');
 assert.equal((await uploadPrepared(t.api,second,await inventory(t.api,second.chunks))).sent,7);
 assert.ok(t.calls.every(name=>name==='owner_chunk_inventory'));
});
test('cancellation before prepare or upload sends nothing',async()=>{
 const signal=AbortSignal.abort();await assert.rejects(prepareFiles(basic(),'dev-1',{signal}));
 const t=transport(),p=await prepareFiles(basic(),'dev-1');await assert.rejects(uploadPrepared(t.api,p,new Set(),{signal}));assert.equal(t.objects.size,0);
});
test('changed local file cannot be uploaded using old hash',async()=>{
 const t=transport(),p=await prepareFiles(basic(),'dev-1'),c=p.chunks.values().next().value;
 c.file=new Blob(['different']);await assert.rejects(uploadPrepared(t.api,p,new Set()),/изменился/);assert.equal(t.objects.size,0);
});
test('missing stored chunks cannot be reported as fully uploaded',async()=>{
 const p=await prepareFiles(basic(),'dev-1');await assert.rejects(uploadPrepared({uploadDevChunk:async()=>{},rpc:async()=>[]},p,new Set()),/Не все блоки/);
});
test('browser chunk upload uses public key, memory bearer and fixed immutable destination',async()=>{
 const bytes=new Uint8Array([1,2,3]),sha=digest(bytes),a=new Accounts(async(url,opt)=>{
  assert.equal(url,AUTH_URL+'/storage/v1/object/dev-packs/chunks/'+sha);assert.equal(opt.headers.apikey,PUBLIC_KEY);assert.equal(opt.headers.Authorization,'Bearer unit-test-only');
  assert.equal(opt.redirect,'error');assert.equal(opt.credentials,'omit');assert.equal(opt.headers['x-upsert'],'false');assert.deepEqual(opt.body,bytes);return new Response('{}');
 });a.session={...session};await a.uploadDevChunk(sha,bytes);await assert.rejects(a.uploadDevChunk('../steal',bytes));
});
test('expired upload clears session; quota response does not leak raw server data',async()=>{
 for(const status of [401,413]){const a=new Accounts(async()=>new Response('raw-private-internal',{status}));a.session={...session};
  await assert.rejects(a.uploadDevChunk(digest(new Uint8Array([1])),new Uint8Array([1])),e=>!e.message.includes('raw-private'));
  if(status===401)assert.equal(a.session,null);
 }
});
test('publisher decisions expose only safe actionable errors',async()=>{
 for(const [code,message,expected] of [['23505','private SQL','имя версии'],['P0001','Active release changed','Активная версия'],['P0001','Release chunks not fully uploaded','Часть блоков'],['P0001','private internal SQL','Сервер отклонил']]){
  const a=new Accounts(async()=>new Response(JSON.stringify({code,message}),{status:400}));a.session={...session};
  await assert.rejects(a.rpc('publish_own_dev_files',{}),e=>e.message.includes(expected)&&!e.message.includes('private'));
 }
});
test('realistic pack over 5000 files and 512 KiB metadata prepares without transfer',async()=>{
 const files=[...basic(),...Array.from({length:6000},(_,i)=>file('resourcepacks/pack/assets/textures/t_'+i+'.png','asset'))];
 const p=await prepareFiles(files,'large-6000');assert.equal(p.manifest.files.length,6003);
 assert.ok(Buffer.byteLength(JSON.stringify(p.manifest))>512*1024);assert.equal(p.chunks.size,4);
});
test('file count and volume bounds give separate measured diagnostics',()=>{
 const many=[...basic(),...Array.from({length:100000},(_,i)=>({webkitRelativePath:'game/resourcepacks/a'+i,size:1}))];
 assert.throws(()=>selectGameFiles(many),/100.*003.*100.*000/);
 const large=[...basic(),...Array.from({length:17},(_,i)=>({webkitRelativePath:'game/resourcepacks/a'+i,size:8*1024**3}))];
 assert.throws(()=>selectGameFiles(large),/136\.0 ГиБ.*128 ГиБ/);
});
test('local read failure names the exact file instead of opaque browser exception',async()=>{
 const broken=file('resourcepacks/problem.zip','asset');
 broken.slice=()=>({arrayBuffer:async()=>{throw new DOMException('opaque browser detail','NotReadableError');}});
 const progress=[];
 await assert.rejects(prepareFiles([...basic(),broken],'dev-read',{onProgress:p=>progress.push(p)}),e=>e.name==='SourceReadError'&&e.message.includes('resourcepacks/problem.zip')&&!e.message.includes('opaque browser detail'));
 assert.equal(progress.at(-1).file,'resourcepacks/problem.zip');
});
test('local upload read failure names source and never sends the unreadable chunk',async()=>{
 const p=await prepareFiles(basic(),'dev-read'),t=transport(),c=p.chunks.values().next().value;
 c.file={slice:()=>({arrayBuffer:async()=>{throw new DOMException('locked','NotReadableError');}})};
 await assert.rejects(uploadPrepared(t.api,p,new Set()),e=>e.name==='SourceReadError'&&e.message.includes(c.path));
 assert.equal(t.objects.size,0);
});
test('short local read fails preparation instead of accepting a truncated manifest',async()=>{
 const short=file('resourcepacks/short.zip','asset');short.slice=()=>new Blob(['a']);
 await assert.rejects(prepareFiles([...basic(),short],'dev-read'),/short.zip.*не полностью/);
});
test('abort during local read remains cancellation rather than permission advice',async()=>{
 const controller=new AbortController(),f=file('resourcepacks/abort.zip','asset');
 f.slice=()=>({arrayBuffer:async()=>{controller.abort();throw new DOMException('locked','NotReadableError');}});
 await assert.rejects(prepareFiles([...basic(),f],'dev-read',{signal:controller.signal}),e=>e.name==='AbortError');
});
test('cabinet version pins the complete mutable uploader import chain and disables caching',async()=>{
 const {readFile}=await import('node:fs/promises');
 const source=async name=>readFile(new URL('../website/'+name,import.meta.url),'utf8');
 assert.match(await source('account.html'),/src="\/account\.js\?v=up08"/);
 const account=await source('account.js');assert.ok(account.includes("'./publisher.js?v=up08'"));assert.ok(account.includes("'./accounts.js?v=up08'"));
 assert.ok((await source('publisher.js')).includes("'./publisher-core.js?v=up08'"));
 const config=JSON.parse(await source('vercel.json'));
 for(const name of ['account','accounts','publisher','publisher-core'])assert.ok(config.headers.some(rule=>rule.source==='/'+name+'.js'&&rule.headers.some(h=>h.key==='Cache-Control'&&h.value==='no-store')));
 assert.ok((await source('publisher-core.js')).includes("PUBLISHER_BUILD = 'UP-08'"));
});
