import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import {Accounts, validateSkin, validate, AUTH_URL, PUBLIC_KEY, CONFIRM_URL} from '../website/accounts.js';
const id='11111111-1111-4111-8111-111111111111';
const pass='test-only-long-password';
const response=(data,status=200)=>new Response(status===204?null:JSON.stringify(data),{status,headers:{'Content-Type':'application/json'}});
test('same project and public key as launcher, no secret key',()=>{
 const cfg=JSON.parse(readFileSync(new URL('../launcher/accounts.public.json',import.meta.url)));
 assert.equal(AUTH_URL,cfg.url);assert.equal(PUBLIC_KEY,cfg.publicKey);assert.ok(PUBLIC_KEY.startsWith('sb_publishable_'));
});
test('launcher-compatible validation before request',async()=>{
 const a=new Accounts(()=>{throw Error('must not request')});
 for(const nick of ['a','bad nick','тест','a'.repeat(17)])await assert.rejects(a.signUp('x@example.org',pass,nick));
 assert.throws(()=>validate('invalid',pass));assert.throws(()=>validate('x@example.org','short'));assert.throws(()=>validate('x@example.org','a'.repeat(129)));
 assert.equal(validate(' x@example.org ',pass,'Tester_1'),'x@example.org');
});
test('signup matches trigger schema and redirect, ignores auto-session',async()=>{
 const a=new Accounts(async(url,options)=>{
  assert.equal(url,AUTH_URL+'/auth/v1/signup?redirect_to='+encodeURIComponent(CONFIRM_URL));
  assert.equal(options.credentials,'omit');assert.equal(options.redirect,'error');
  assert.equal(options.headers.apikey,PUBLIC_KEY);assert.equal(options.headers.Authorization,undefined);
  assert.deepEqual(JSON.parse(options.body),{email:'x@example.org',password:pass,data:{nickname:'Tester'}});
  return response({access_token:'unexpected-token',user:{id}});
 });await a.signUp('x@example.org',pass,'Tester');assert.equal(a.session,null);
});
test('signin scopes protected profile to authenticated owner and logout clears memory',async()=>{
 let count=0;
 const a=new Accounts(async(url,options)=>{
  count++;
  if(count===1)return response({access_token:'test-token',user:{id},expires_in:3600});
  assert.equal(options.headers.Authorization,'Bearer test-token');
  if(count===2){assert.equal(options.method,'GET');assert.ok(url.includes('profiles?id=eq.'+id));return response([{id,nickname:'Tester'}]);}
  assert.ok(url.endsWith('logout?scope=local'));return response(null,204);
 });await a.signIn('x@example.org',pass);assert.equal(a.session.nickname,'Tester');await a.signOut();assert.equal(a.session,null);assert.equal(count,3);
});
test('profile mismatch never produces a session',async()=>{
 let count=0;const a=new Accounts(async()=>++count===1?response({access_token:'x',user:{id},expires_in:3600}):response([{id:'wrong',nickname:'Tester'}]));
 await assert.rejects(a.signIn('x@example.org',pass));assert.equal(a.session,null);
});
test('rate limits and raw server details are not reflected to users',async()=>{
 for(const code of [400,401,422,429,500]){
  const a=new Accounts(async()=>response({message:'PRIVATE_SQL_SECRET<script>bad()</script>'},code));
  await assert.rejects(a.signUp('x@example.org',pass,'Tester'),error=>!error.message.includes('PRIVATE')&&!error.message.includes('<script>'));
 }
});
test('resend redirect and local sign-out despite service failure',async()=>{
 const a=new Accounts(async(url,options)=>{assert.ok(url.includes('/resend?redirect_to='));assert.deepEqual(JSON.parse(options.body),{email:'x@example.org',type:'signup'});return response({});});
 await a.resend('x@example.org');
 a.session={token:'x'};a.request=async()=>{throw Error('offline')};await assert.rejects(a.signOut());assert.equal(a.session,null);
});
test('no persistent tokens, unsafe HTML, URL token adoption or password GET form',()=>{
 const ui=readFileSync(new URL('../website/account.js',import.meta.url),'utf8');
 const api=readFileSync(new URL('../website/accounts.js',import.meta.url),'utf8');
 for(const unsafe of ['localStorage','sessionStorage','innerHTML','console.log','document.cookie'])assert.ok(!(ui+api).includes(unsafe),unsafe);
 assert.ok(ui.includes('history.replaceState'));assert.ok(ui.includes('pagehide'));
 const html=readFileSync(new URL('../website/account.html',import.meta.url),'utf8');assert.ok(html.includes('method="post"'));
});
test('recovery sends only email and pins return URL',async()=>{
 const a=new Accounts(async(url,options)=>{
  assert.equal(url,AUTH_URL+'/auth/v1/recover?redirect_to='+encodeURIComponent(CONFIRM_URL));
  assert.deepEqual(JSON.parse(options.body),{email:'x@example.org'});
  assert.equal(options.headers.Authorization,undefined);return response({});
 });await a.requestRecovery(' x@example.org ');
 await assert.rejects(a.requestRecovery('invalid'));
 assert.equal(a.session,null);assert.equal(a.recovery,null);
});
test('recovery validates bearer with Auth, resets password, revokes sessions and clears memory',async()=>{
 const calls=[];const a=new Accounts(async(url,options)=>{
  calls.push(url);assert.equal(options.headers.Authorization,'Bearer recovery-only');
  if(calls.length===1){assert.equal(options.method,'GET');assert.equal(url,AUTH_URL+'/auth/v1/user');return response({id});}
  if(calls.length===2){assert.equal(options.method,'PUT');assert.deepEqual(JSON.parse(options.body),{password:pass});return response({id});}
  assert.equal(url,AUTH_URL+'/auth/v1/logout?scope=global');return response(null,204);
 });
 await assert.rejects(a.resetPassword(pass));assert.equal(calls.length,0);
 await a.acceptRecovery('recovery-only');assert.equal(a.session,null);
 await assert.rejects(a.resetPassword('short'));assert.equal(calls.length,1);
 assert.equal(await a.resetPassword(pass),true);assert.equal(calls.length,3);
 assert.equal(a.recovery,null);assert.equal(a.session,null);
});
test('expired or malformed recovery never permits changing password',async()=>{
 for(const r of [response({},401),response({id:'wrong'})]){
  const a=new Accounts(async()=>r);await assert.rejects(a.acceptRecovery('expired'));
  assert.equal(a.recovery,null);await assert.rejects(a.resetPassword(pass));
 }
});
test('password change failure clears recovery and logout failure does not claim password change failed',async()=>{
 for(const failsAt of [2,3]){
  let n=0;const a=new Accounts(async()=>{n++;if(n===failsAt)return response({},500);return n===1?response({id}):response({});});
  await a.acceptRecovery('verified');
  if(failsAt===2)await assert.rejects(a.resetPassword(pass));else assert.equal(await a.resetPassword(pass),false);
  assert.equal(a.recovery,null);
 }
});

// Execute the actual UI module against a minimal DOM and mocked Auth transport.
// No production account/password or mailbox is touched by these tests.
async function page(fragment='',request=async()=>response({})) {
 const {runInNewContext}=await import('node:vm');
 const html=readFileSync(new URL('../website/account.html',import.meta.url),'utf8');
 const element=()=>({value:'',hidden:false,disabled:false,required:false,textContent:'',events:{},children:[],dataset:{},classList:{toggle(){}},getContext(){return {clearRect(){},drawImage(){}};},setAttribute(k,v){this[k]=v;},addEventListener(k,v){this.events[k]=v;},replaceChildren(){this.children=[];this.textContent='';},append(child){this.children.push(child);},querySelectorAll(){return this.children.flatMap(c=>c.type==='button'?[c]:c.querySelectorAll());}});
 const elements=Object.fromEntries([...html.matchAll(/id="([^"]+)"/g)].map(m=>[m[1],element()]));
 const location={hash:fragment,search:'',pathname:'/account.html'};
 const api=new Accounts(request);let erased=false;
 const script=readFileSync(new URL('../website/account.js',import.meta.url),'utf8').replace(/^import .*;$/gm,'');
 runInNewContext(script,{mountPublisher:()=>({render(){},reset(){},async load(){}}),Accounts:class {constructor(){return api;}},document:{getElementById:id=>elements[id],createElement:element},location,history:{replaceState(){erased=true;location.hash='';}},URLSearchParams,Date,validateSkin,window:{addEventListener(){}},setTimeout:(fn,ms)=>setTimeout(fn,ms).unref(),clearTimeout,setInterval:()=>0,clearInterval(){}});
 await new Promise(resolve=>setImmediate(resolve));
 return {elements,api,erased};
}
test('forgot password UI needs only email, sends recovery, then can return to login',async()=>{
 let calls=0;const {elements:e}=await page('',async(url)=>{calls++;assert.ok(url.includes('/recover?redirect_to='));return response({});});
 e['forgot-password'].onclick();assert.equal(e['form-title'].textContent,'Восстановление пароля');
 assert.equal(e.password.required,false);assert.equal(e.password.disabled,true);assert.equal(e['repeat-field'].hidden,true);
 e.email.value='x@example.org';e['account-form'].events.submit({preventDefault(){}});
 await new Promise(resolve=>setImmediate(resolve));assert.equal(calls,1);assert.match(e['account-status'].textContent,/письмо/);
 e['mode-signin'].onclick();assert.equal(e.password.required,true);assert.equal(e.password.disabled,false);
});
test('recovery landing erases URL, validates token, checks confirmation and saves new password',async()=>{
 let calls=0;const {elements:e,erased}=await page('#type=recovery&access_token=test-recovery',async(url,options)=>{
  calls++;if(calls===1){assert.equal(options.method,'GET');return response({id});}
  if(calls===2){assert.equal(options.method,'PUT');assert.deepEqual(JSON.parse(options.body),{password:pass});return response({id});}
  return response(null,204);
 });
 assert.equal(erased,true);assert.equal(e['form-title'].textContent,'Новый пароль');assert.equal(e.email.disabled,true);
 assert.equal(e['repeat-password'].required,true);assert.equal(e.password.disabled,false);
 e.password.value=pass;e['repeat-password'].value='wrong';e['account-form'].events.submit({preventDefault(){}});assert.equal(calls,1);
 e['repeat-password'].value=pass;e['account-form'].events.submit({preventDefault(){}});
 await new Promise(resolve=>setImmediate(resolve));assert.equal(calls,3);assert.equal(e['form-title'].textContent,'Войти в аккаунт');
 assert.equal(e.email.disabled,false);assert.equal(e.password.value,'');assert.match(e['account-status'].textContent,/Пароль изменён/);
});
test('confirmation and error landing never adopts a sign-in token',async()=>{
 for(const fragment of ['#type=signup&access_token=ignore','#error=access_denied&error_description=PRIVATE']){
  const {api,elements:e,erased}=await page(fragment,async()=>{throw Error('No network allowed');});
  assert.equal(erased,true);assert.equal(api.session,null);assert.equal(api.recovery,null);assert.ok(!e['account-status'].textContent.includes('PRIVATE'));
 }
});

function png(height=64) {
 const data=new Uint8Array(40);data.set([137,80,78,71,13,10,26,10]);data.set([73,72,68,82],12);
 const view=new DataView(data.buffer);view.setUint32(16,64);view.setUint32(20,height);return data;
}
const ownProfile={id,nickname:'Tester',skin_path:null,skin_model:'steve'};
const loginResponse=()=>response({access_token:'test-only-token',user:{id,email:'x@example.org'},expires_in:3600});
test('cabinet profile and private skin writes are scoped to account, never change nickname',async()=>{
 const calls=[];const a=new Accounts(async(url,options)=>{
  calls.push({url,options});
  if(url.includes('/token?'))return loginResponse();
  if(options.method==='GET')return response([ownProfile]);
  if(url.includes('/storage/')){assert.equal(options.headers['Content-Type'],'image/png');assert.equal(options.headers['x-upsert'],'true');assert.deepEqual(options.body,png());return response({});}
  assert.equal(options.method,'PATCH');assert.ok(url.endsWith('profiles?id=eq.'+id));
  assert.deepEqual(JSON.parse(options.body),{skin_path:id+'/skin.png',skin_model:'steve'});return response(null,204);
 });
 await a.signIn('x@example.org',pass);assert.equal(a.session.email,'x@example.org');await a.profile();await a.uploadSkin(png(),'steve');
 assert.ok(calls[3].url.endsWith('/storage/v1/object/skins/'+id+'/skin.png'));
 for(const call of calls.slice(1))assert.equal(call.options.headers.Authorization,'Bearer test-only-token');
});
test('skin validation rejects invalid types, dimensions, size, model and legacy Alex',()=>{
 assert.equal(validateSkin(png()),64);assert.equal(validateSkin(png(32),'steve'),32);
 for(const [data,model] of [[new Uint8Array(2),'steve'],[new Uint8Array(262145),'steve'],[png(128),'steve'],[png(32),'alex'],[png(),'unknown']])assert.throws(()=>validateSkin(data,model));
 const bad=png();bad[0]=0;assert.throws(()=>validateSkin(bad));
});
test('skin download is owner-only and bounded before decoding',async()=>{
 let count=0;const a=new Accounts(async()=>{count++;if(count===1)return loginResponse();if(count===2)return response([{...ownProfile,skin_path:id+'/skin.png'}]);return new Response(png(),{headers:{'Content-Type':'image/png'}});});
 await a.signIn('x@example.org',pass);assert.deepEqual(await a.skin(),png());
 a.session.profile.skin_path='other/skin.png';await assert.rejects(a.skin());assert.equal(count,3);
 a.session.profile.skin_path=id+'/skin.png';a.request=async()=>new Response(new Uint8Array(262145));await assert.rejects(a.skin());
});
test('protected actions require a live session and dev decisions use protected RPC',async()=>{
 const a=new Accounts(async()=>{throw Error('must not request');});
 for(const action of [()=>a.profile(),()=>a.uploadSkin(png(),'steve'),()=>a.devRequest(),()=>a.inbox(),()=>a.isAdmin()])await assert.rejects(action());
 a.session={id,token:'test-only',expires:Date.now()+30000};
 a.request=async(url,options)=>{assert.equal(url,AUTH_URL+'/rest/v1/rpc/review_dev_access');assert.equal(options.headers.Authorization,'Bearer test-only');assert.deepEqual(JSON.parse(options.body),{target:id,approve:false});return response(null,204);};
 await a.review(id,false);await assert.rejects(a.review('invalid',true));await assert.rejects(a.review(id,'yes'));
 a.session.expires=0;await assert.rejects(a.devRequest());assert.equal(a.session,null);
});
test('cabinet UI shows immutable nickname, email, dev status and hides admin inbox',async()=>{
 let requested=false;const {elements:e}=await page('',async(url,options)=>{
  if(url.includes('/token?'))return loginResponse();
  if(url.includes('/profiles?'))return response([ownProfile]);
  if(url.includes('/dev_access_requests?'))return response(requested?[{status:'pending'}]:[]);
  if(url.endsWith('/is_launcher_admin'))return response(false);
  if(url.endsWith('/request_dev_access')){requested=true;return response('pending');}
  if(url.includes('/logout?'))return response(null,204);
  throw Error('Unexpected '+url);
 });
 e['mode-signin'].onclick();e.email.value='x@example.org';e.password.value=pass;e['account-form'].events.submit({preventDefault(){}});
 await new Promise(resolve=>setImmediate(resolve));
 assert.equal(e.guest.hidden,true);assert.equal(e['signed-in'].hidden,false);assert.equal(e['profile-nickname'].textContent,'Tester');assert.equal(e['profile-email'].textContent,'x@example.org');
 assert.equal(e['admin-inbox'].hidden,true);assert.equal(e['request-dev'].disabled,false);
 e['request-dev'].onclick();await new Promise(resolve=>setImmediate(resolve));assert.equal(requested,true);assert.equal(e['request-dev'].disabled,true);assert.match(e['dev-status'].textContent,/Ожидает/);
 e['sign-out'].onclick();await new Promise(resolve=>setImmediate(resolve));assert.equal(e['signed-in'].hidden,true);assert.equal(e['profile-email'].textContent,'');
 const html=readFileSync(new URL('../website/account.html',import.meta.url),'utf8');
 assert.ok(!html.includes('id="profile-nickname" name='));assert.ok(html.includes('Ник доступен только для просмотра'));
});
test('administrator can review own request and refresh own dev status',async()=>{
 const target=id;let decision=null;
 const {elements:e}=await page('',async(url,options)=>{
  if(url.includes('/token?'))return loginResponse();if(url.includes('/profiles?'))return response([ownProfile]);
  if(url.includes('/dev_access_requests?'))return response([{status:decision===null?'pending':'approved'}]);
  if(url.endsWith('/is_launcher_admin'))return response(true);
  if(url.endsWith('/dev_access_inbox'))return response([{user_id:target,nickname:'Builder',status:decision===null?'pending':'approved',requested_at:'2026-10-30T12:00:00Z'}]);
  if(url.endsWith('/review_dev_access')){decision=JSON.parse(options.body);return response(null,204);}
  throw Error('Unexpected '+url);
 });
 e['mode-signin'].onclick();e.email.value='x@example.org';e.password.value=pass;e['account-form'].events.submit({preventDefault(){}});await new Promise(resolve=>setImmediate(resolve));
 assert.equal(e['admin-inbox'].hidden,false);assert.equal(e['request-count'].textContent,'(1)');
 const buttons=e.requests.querySelectorAll('button');assert.deepEqual(buttons.map(b=>b.textContent),['Подтвердить','Отклонить']);
 buttons[0].onclick();await new Promise(resolve=>setImmediate(resolve));assert.deepEqual(decision,{target,approve:true});assert.equal(e.requests.querySelectorAll('button').length,0);assert.match(e['dev-status'].textContent,/подтверждён/);
});
test('review permission and stale-request failures show safe actionable messages',async()=>{
 for(const [code,pattern] of [['42501',/только администратор/],['P0002',/уже рассмотрена/]]){
  const a=new Accounts(async()=>response({code,message:'PRIVATE_SQL_DIAGNOSTIC'},code==='42501'?403:404));
  a.session={id,token:'test-only',expires:Date.now()+30000};
  await assert.rejects(a.review(id,true),error=>pattern.test(error.message)&&!error.message.includes('PRIVATE_SQL'));
 }
});
