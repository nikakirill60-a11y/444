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
