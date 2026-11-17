const assert=require('node:assert/strict');
const {chromium}=require('playwright');
(async()=>{
 const browser=await chromium.launch({executablePath:process.env.CHROMIUM_EXECUTABLE_PATH||undefined,args:['--no-sandbox','--disable-dev-shm-usage']});
 try{
  for(const width of [1440,390,320,768]){
   const page=await browser.newPage({viewport:{width,height:900}}),errors=[],requests=[];
   page.on('pageerror',e=>errors.push(e.message));
   await page.route('https://xmhxucddhkitxqtweifu.supabase.co/**',async route=>{
    const req=route.request(),url=req.url();requests.push({url,data:req.postDataJSON()});
    let data={};if(url.includes('/token?'))data={access_token:'mock-token',user:{id:'11111111-1111-4111-8111-111111111111'},expires_in:3600};
    if(url.includes('/profiles?'))data=[{id:'11111111-1111-4111-8111-111111111111',nickname:'Test_Survivor'}];
    await route.fulfill({status:200,contentType:'application/json',body:JSON.stringify(data)});
   });
   await page.goto('http://localhost:3000/account.html#access_token=never-adopt-this');
   assert.equal(await page.evaluate(()=>location.hash),'');assert.equal(requests.length,0);
   await page.locator('#mode-signup').click();await page.locator('#nickname').fill('Test_Survivor');await page.locator('#email').fill('test@example.org');await page.locator('#password').fill('example-password-123');await page.locator('#repeat-password').fill('example-password-124');await page.locator('#submit-account').click();
   await assert.equal(await page.locator('#account-status').textContent(),'Пароли не совпадают.');assert.equal(requests.length,0);
   await page.locator('#repeat-password').fill('example-password-123');await page.locator('#submit-account').click();await page.waitForFunction(()=>document.querySelector('#account-status').textContent.includes('Запрос регистрации принят'));
   assert.ok(requests[0].url.includes('/signup?redirect_to='));assert.equal(requests[0].data.data.nickname,'Test_Survivor');assert.equal(await page.locator('#password').inputValue(),'');
   await page.locator('#mode-signin').click();await page.locator('#password').fill('example-password-123');await page.locator('#submit-account').click();await page.locator('#signed-in').waitFor({state:'visible'});
   assert.equal(await page.locator('#profile-nickname').textContent(),'Test_Survivor');
   assert.equal(await page.evaluate(()=>localStorage.length+sessionStorage.length),0);
   await page.locator('#sign-out').click();await page.locator('#guest').waitFor({state:'visible'});
   assert.ok(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth),`overflow ${width}`);
   assert.deepEqual(errors,[]);await page.close();console.log(`PASS account ${width}: confirmation stripping, signup, validation, signin, owner profile, logout, no persistent tokens, reflow`);
  }
 }finally{await browser.close();}
})().catch(e=>{console.error(e);process.exitCode=1;});
