// All Auth/Storage traffic is mocked. Never signs into or changes a real account.
import assert from 'node:assert/strict';
import {createServer} from 'node:http';
import {readFile, mkdir,writeFile,rm} from 'node:fs/promises';
import {createHash} from 'node:crypto';
import {resolve, extname} from 'node:path';
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE || 'playwright-core');
const pack=process.env.CHROMIUM_MODULE ? (await import(process.env.CHROMIUM_MODULE)).default : null;
const browser=await chromium.launch({headless:true,executablePath:pack?await pack.executablePath():process.env.CHROMIUM_EXECUTABLE_PATH,args:pack?pack.args.filter(arg=>!['--single-process','--disable-web-security'].includes(arg)):['--no-sandbox','--disable-dev-shm-usage']});
const root=resolve('website');
const server=createServer(async(req,res)=>{
 try {
  const path=resolve(root,'.'+new URL(req.url,'http://test').pathname);if(!path.startsWith(root+'/')){res.writeHead(403).end();return;}
  const data=await readFile(path);res.setHeader('Content-Type',({'.html':'text/html','.js':'text/javascript','.css':'text/css','.svg':'image/svg+xml','.woff2':'font/woff2','.jpg':'image/jpeg','.png':'image/png'})[extname(path)]||'application/octet-stream');
  res.end(data);
 } catch(_){res.writeHead(404).end();}
});
await new Promise(r=>server.listen(0,'127.0.0.1',r));const base='http://127.0.0.1:'+server.address().port;
const id='11111111-1111-4111-8111-111111111111', second='22222222-2222-4222-8222-222222222222';
async function setup(width,admin=false,publisher=false) {
 const page=await browser.newPage({viewport:{width,height:1000}}), errors=[];
 let profile={id,nickname:'TestBuilder',skin_path:null,skin_model:'steve'},state=admin?'pending':'none',uploaded=null;
 const chunks=new Map();let activated=null,sentBytes=0,activations=0;
 const inbox=[{user_id:second,nickname:'Builder_A',status:'pending',requested_at:'2026-10-30T12:00:00Z'},{user_id:'33333333-3333-4333-8333-333333333333',nickname:'Builder_B',status:'pending',requested_at:'2026-10-30T12:00:00Z'}];
 if(admin)inbox.unshift({user_id:id,nickname:'TestBuilder',status:'pending',requested_at:'2026-10-30T12:00:00Z'});
 page.on('pageerror',e=>errors.push(e.message));
 await page.route('https://xmhxucddhkitxqtweifu.supabase.co/**',async route=>{
  const req=route.request(),url=req.url();let data={};
  if(req.method()==='OPTIONS'){await route.fulfill({status:204,headers:{'access-control-allow-origin':'*','access-control-allow-methods':'GET,POST,PATCH,PUT','access-control-allow-headers':'*'}});return;}
  if(url.includes('/token?'))data={access_token:'browser-test-only',expires_in:3600,user:{id,email:'builder@example.test'}};
  else if(url.includes('/profiles?')){
   if(req.method()==='PATCH'){const body=req.postDataJSON();assert.deepEqual(Object.keys(body).sort(),['skin_model','skin_path']);profile={...profile,...body};}else data=[profile];
  }
  else if(url.includes('/storage/v1/object/skins/')){uploaded=req.postDataBuffer();assert.equal(uploaded.readUInt32BE(20),32);}
  else if(url.includes('/storage/v1/object/authenticated/skins/')){await route.fulfill({contentType:'image/png',body:uploaded,headers:{'access-control-allow-origin':'*'}});return;}
  else if(url.includes('/dev_access_requests?'))data=state==='none'?[]:[{status:state}];
  else if(url.endsWith('/owner_distribution_state'))data=publisher?{publisher:true,version:activated,objectLimitBytes:8388608,usedBytes:0,providerQuotaBytes:null}:{publisher:false};
  else if(url.endsWith('/owner_chunk_inventory')){assert.equal(publisher,true);data=req.postDataJSON().requested.filter(c=>chunks.get(c.sha256)?.length===c.bytes).map(c=>c.sha256);}
  else if(url.includes('/storage/v1/object/dev-packs/chunks/')){assert.equal(publisher,true);const bytes=req.postDataBuffer(),sha=url.split('/').at(-1);assert.equal(req.headers()['x-upsert'],'false');assert.equal(createHash('sha256').update(bytes).digest('hex'),sha);chunks.set(sha,bytes);sentBytes+=bytes.length;}
  else if(url.endsWith('/publish_own_dev_files')){assert.equal(publisher,true);const b=req.postDataJSON();assert.equal(b.expected_version,activated);assert.equal(b.approval,'Я подтверждаю публикацию '+b.new_manifest.version);for(const f of b.new_manifest.files){assert.ok(!f.path.startsWith('saves/'));for(const c of f.chunks)assert.equal(chunks.get(c.sha256).length,c.bytes);}activated=b.new_manifest.version;activations++;data=null;}
  else if(url.endsWith('/is_launcher_admin'))data=admin;
  else if(url.endsWith('/request_dev_access')){state='pending';data='pending';}
  else if(url.endsWith('/dev_access_inbox'))data=inbox;
  else if(url.endsWith('/review_dev_access')){const body=req.postDataJSON();inbox.find(r=>r.user_id===body.target).status=body.approve?'approved':'rejected';if(body.target===id)state=body.approve?'approved':'rejected';}
  else if(!url.includes('/logout?')&&!url.includes('/recover?'))throw Error('Unexpected API '+url);
  await route.fulfill({contentType:'application/json',body:JSON.stringify(data),headers:{'access-control-allow-origin':'*'}});
 });
 await page.goto(base+'/account.html');await page.getByRole('button',{name:'Вход',exact:true}).click();
 await page.locator('#email').fill('builder@example.test');await page.locator('#password').fill('test-only-long-password');await page.locator('#submit-account').click();
 await page.getByRole('status').filter({hasText:'Добро пожаловать в личный кабинет.'}).waitFor();
 return {page,errors,getSentBytes:()=>sentBytes,getActivations:()=>activations};
}
try {
 await mkdir('artifacts/website-cabinet',{recursive:true});
 for(const width of [320,390,768,1024,1440]) {
  const {page,errors}=await setup(width);
  assert.equal(await page.locator('#profile-nickname').textContent(),'TestBuilder');
  assert.equal(await page.locator('#profile-email').textContent(),'builder@example.test');
  assert.equal(await page.locator('#signed-in input[name=nickname]').count(),0);
  assert.equal(await page.locator('#admin-inbox').isVisible(),false);assert.equal(await page.locator('#owner-publisher').isVisible(),false);
  assert.equal(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth),true,'horizontal overflow '+width);
  await page.locator('#request-dev').click();await page.locator('#dev-status').filter({hasText:'Ожидает решения'}).waitFor();assert.equal(await page.locator('#request-dev').isDisabled(),true);
  const png=Buffer.from(await page.evaluate(()=>{const c=document.createElement('canvas');c.width=64;c.height=32;const x=c.getContext('2d');x.fillStyle='#e4a252';x.fillRect(0,0,64,32);return c.toDataURL('image/png').split(',')[1];}),'base64');
  await page.locator('#skin-file').setInputFiles({name:'skin.png',mimeType:'image/png',buffer:png});await page.locator('#skin-state').filter({hasText:'Выбран локально'}).waitFor();
  await page.locator('#upload-skin').click();await page.locator('#skin-state').filter({hasText:'Скин сохранён'}).waitFor();
  await page.locator('#reload-profile').click();await page.getByRole('status').filter({hasText:'Кабинет обновлён.'}).waitFor();
  await page.locator('#skin-state').filter({hasText:'Сохранённый скин профиля.'}).waitFor();
  assert.equal(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth),true);
  await page.screenshot({path:`artifacts/website-cabinet/cabinet-${width}.png`,fullPage:true});
  await page.locator('#profile-recovery').click();await page.getByRole('status').filter({hasText:'Запрос принят.'}).waitFor();
  await page.locator('#sign-out').click();await page.locator('#guest').waitFor();assert.equal(await page.locator('#profile-email').textContent(),'');
  assert.deepEqual(errors,[]);await page.close();console.log('PASS cabinet '+width+'px · profile, dev request, private skin upload/reload, recovery, logout');
 }
 const {page,errors}=await setup(1440,true);
 await page.getByRole('button',{name:'Подтвердить',exact:true}).first().click();await page.locator('#dev-status').filter({hasText:'подтверждён администратором'}).waitFor();
 await page.getByRole('button',{name:'Подтвердить',exact:true}).first().click();await page.getByRole('status').filter({hasText:'Доступ подтверждён.'}).waitFor();
 await page.getByRole('button',{name:'Отклонить',exact:true}).click();await page.getByRole('status').filter({hasText:'Заявка отклонена.'}).waitFor();
 assert.equal(await page.getByRole('button',{name:'Подтвердить',exact:true}).count(),0);assert.deepEqual(errors,[]);
 await page.screenshot({path:'artifacts/website-cabinet/admin.png',fullPage:true});await page.close();console.log('PASS admin inbox · self approval, own status refresh, confirm and reject other requests');
 const folder=resolve('artifacts/website-cabinet/publisher-fixture');
 await mkdir(folder+'/mods',{recursive:true});await mkdir(folder+'/saves/map',{recursive:true});
 await writeFile(folder+'/mods/metrocraft-port-test.jar','minetro');await writeFile(folder+'/mods/mwf.jar','mwf');await writeFile(folder+'/saves/map/level.dat','private world');
 for(const width of [390,1440]) {
  const owner=await setup(width,true,true),p=owner.page;
  await p.locator('#owner-publisher').waitFor();assert.equal(await p.locator('#publisher-activate').isDisabled(),true);
  await p.locator('#publisher-version').fill('browser-v1');await p.locator('#publisher-folder').setInputFiles(folder);
  await p.locator('#publisher-prepare').click();await p.locator('#publisher-status').filter({hasText:'Нужно отправить'}).waitFor();
  assert.equal(owner.getSentBytes(),0);assert.equal(owner.getActivations(),0);
  await p.locator('#publisher-upload').click();await p.locator('#publisher-status').filter({hasText:'Файлы приняты'}).waitFor();
  assert.equal(owner.getSentBytes(),10);assert.equal(owner.getActivations(),0);assert.equal(await p.locator('#publisher-activate').isDisabled(),true);
  await p.locator('#publisher-accept').check();await p.locator('#publisher-activate').click();await p.locator('#publisher-status').filter({hasText:'включена в раздачу'}).waitFor();assert.equal(owner.getActivations(),1);
  const before=owner.getSentBytes();await writeFile(folder+'/mods/mwf.jar','updated');
  await p.locator('#publisher-version').fill('browser-v2');await p.locator('#publisher-folder').setInputFiles(folder);
  await p.locator('#publisher-prepare').click();await p.locator('#publisher-status').filter({hasText:'Нужно отправить'}).waitFor();
  await p.locator('#publisher-upload').click();await p.locator('#publisher-status').filter({hasText:'Файлы приняты'}).waitFor();
  assert.equal(owner.getSentBytes()-before,7);assert.equal(owner.getActivations(),1);
  assert.equal(await p.evaluate(()=>document.documentElement.scrollWidth<=innerWidth),true);
  await p.screenshot({path:'artifacts/website-cabinet/publisher-'+width+'.png',fullPage:true});
  await p.locator('#sign-out').click();await p.locator('#guest').waitFor();assert.equal(await p.locator('#owner-publisher').isVisible(),false);
  assert.deepEqual(owner.errors,[]);await p.close();await writeFile(folder+'/mods/mwf.jar','mwf');console.log('PASS owner publisher '+width+'px · select folder, SHA256, explicit upload/activation, changed-file transfer, logout');
 }
 const largeFolder=resolve('.cache/large-publisher-fixture');
 await mkdir(largeFolder+'/mods',{recursive:true});await mkdir(largeFolder+'/resourcepacks/assets/textures',{recursive:true});
 await writeFile(largeFolder+'/mods/metrocraft-port-test.jar','minetro');await writeFile(largeFolder+'/mods/mwf.jar','mwf');
 for(let start=0;start<6000;start+=200)await Promise.all(Array.from({length:200},(_,i)=>writeFile(largeFolder+'/resourcepacks/assets/textures/t_'+(start+i)+'.png','asset')));
 try {
  const largeOwner=await setup(1440,true,true),p=largeOwner.page;
  await p.locator('#publisher-version').fill('large-browser-6000');await p.locator('#publisher-folder').setInputFiles(largeFolder);
  await p.locator('#publisher-prepare').click();await p.locator('#publisher-status').filter({hasText:'Проверено 6002 файлов'}).waitFor({timeout:120000});
  assert.equal(largeOwner.getSentBytes(),0);
  await p.locator('#publisher-upload').click();await p.locator('#publisher-status').filter({hasText:'Файлы приняты'}).waitFor();
  assert.equal(largeOwner.getSentBytes(),15);assert.equal(largeOwner.getActivations(),0);
  await p.locator('#publisher-accept').check();await p.locator('#publisher-activate').click();await p.locator('#publisher-status').filter({hasText:'включена в раздачу'}).waitFor();
  assert.equal(largeOwner.getActivations(),1);assert.deepEqual(largeOwner.errors,[]);await p.close();
  console.log('PASS large browser pack · 6002 files, >512 KiB metadata, deduplicated upload and explicit activation');
 } finally {await rm(largeFolder,{recursive:true,force:true});}
} finally { await browser.close(); await new Promise(r=>server.close(r)); }
