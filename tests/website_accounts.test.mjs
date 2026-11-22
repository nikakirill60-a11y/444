import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import {Accounts, validate, AUTH_URL, PUBLIC_KEY, CONFIRM_URL} from '../website/accounts.js';
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
 const elements=Object.fromEntries([...html.matchAll(/id="([^"]+)"/g)].map(m=>[m[1],{value:'',hidden:false,disabled:false,required:false,textContent:'',events:{},setAttribute(k,v){this[k]=v;},addEventListener(k,v){this.events[k]=v;}}]));
 const location={hash:fragment,search:'',pathname:'/account.html'};
 const api=new Accounts(request);let erased=false;
 const script=readFileSync(new URL('../website/account.js',import.meta.url),'utf8').replace("import {Accounts} from './accounts.js';",'');
 runInNewContext(script,{Accounts:class {constructor(){return api;}},document:{getElementById:id=>elements[id]},location,history:{replaceState(){erased=true;location.hash='';}},URLSearchParams,window:{addEventListener(){}},setTimeout,clearTimeout});
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
