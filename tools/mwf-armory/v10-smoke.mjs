import assert from 'node:assert/strict';
import {fileURLToPath,pathToFileURL} from 'node:url';
import {mkdirSync} from 'node:fs';
import path from 'node:path';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'../..'),out=path.join(root,'artifacts/mwf-v10');mkdirSync(out,{recursive:true});
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE||'playwright-core');
console.log('Launching Chromium');
const browser=await chromium.launch({headless:true,executablePath:process.env.MWF_CHROMIUM||undefined,args:['--no-sandbox','--no-zygote','--in-process-gpu','--use-gl=angle','--use-angle=swiftshader','--enable-unsafe-swiftshader']});
try{
 const page=await browser.newPage({viewport:{width:1440,height:1000}}),errors=[],requests=[];
 page.setDefaultTimeout(60000);page.on('pageerror',e=>errors.push(e.message));page.on('request',r=>{if(/^https?:/.test(r.url()))requests.push(r.url());});
 await page.route('https://**/*',r=>r.abort());await page.route('http://**/*',r=>r.abort());
 await page.goto(pathToFileURL(path.join(root,'artifacts/mwf-armory/index.html')).href);await page.waitForFunction(()=>mwfEditor.empty);
 await page.locator('#packInput').setInputFiles(path.join(root,'downloads/Metro_Full_Arsenal_MWF_fix1_v10_icons.zip'));await page.waitForFunction(()=>mwfEditor.ready);
 const ids=await page.evaluate(()=>mwfEditor.guns);assert.equal(ids.length,25);
 const select=async id=>{await page.locator('[data-panel=library]').click();await page.locator('#libraryGun').selectOption(id);await page.waitForFunction(id=>mwfEditor.ready&&mwfEditor.gun===id,id);};
 // Load EVERY gun, not only the changed ones, against the existing offline editor.
 for(const id of ids){console.log('Load',id);await select(id);assert.equal(await page.evaluate(()=>mwfEditor.type.internalName),id);}
 console.log('All 25 loaded; testing stock hands');
 const visibleBox=()=>page.getByRole('checkbox',{name:'Показать этот обвес в 3D',exact:true});
 for(const [id,stock] of [['metro_ashot','metro_mod_ashot_butt'],['metro_podonok','metro_mod_podonok_butt'],['metro_revolver','metro_mod_butt']]){
  await select(id);await page.locator('[data-panel=mods]').click();await page.locator('#modSelect').selectOption(stock);await visibleBox().uncheck();
  const before=await page.evaluate(()=>mwfEditor.motion.nodes);
  if(id==='metro_ashot')await page.screenshot({path:path.join(out,'ashot-no-stock.png')});
  await visibleBox().check();await page.waitForFunction(before=>JSON.stringify(mwfEditor.motion.nodes)!==JSON.stringify(before),before);
  const stockPose=await page.evaluate(()=>mwfEditor.motion.nodes);assert.notDeepEqual(stockPose.leftArmModel,before.leftArmModel);assert.notDeepEqual(stockPose.rightArmModel,before.rightArmModel);
  if(id==='metro_ashot')await page.screenshot({path:path.join(out,'ashot-stock.png')});
  await page.locator('#ads').check();await page.locator('#bodyType').selectOption('slim');await page.locator('#action').selectOption('reload');
  if(id==='metro_ashot')await page.screenshot({path:path.join(out,'ashot-stock-ads-reload.png')});
  await page.locator('#action').selectOption('default');await page.locator('#bodyType').selectOption('classic');await page.locator('#ads').uncheck();await visibleBox().uncheck();
  await page.waitForFunction(before=>JSON.stringify(mwfEditor.motion.nodes)===JSON.stringify(before),before);
 }
 await select('metro_velopal');assert.equal(await page.locator('#rounds').inputValue(),'6');
 assert.deepEqual(await page.evaluate(()=>mwfEditor.type.acceptedBullets),['metro_12g_buckshot']);
 assert.equal(await page.evaluate(()=>mwfEditor.type.modelSkins[0].skinAsset),'metro_velopal');
 await page.locator('[data-view=orbit]').click();await page.screenshot({path:path.join(out,'velopal-new-texture.png')});
 for(let left=5;left>=0;left--){await page.locator('#shootPreview').click();assert.equal(await page.locator('#rounds').inputValue(),String(left));}
 await page.locator('#shootPreview').click();assert.equal(await page.locator('#rounds').inputValue(),'0');
 await select('metro_ubludok');await page.locator('[data-panel=mods]').click();await page.locator('#modSelect').selectOption('metro_mod_ubludok_radiator');
 assert.equal(await visibleBox().isChecked(),false);
 const no=await page.screenshot({path:path.join(out,'ubludok-no-radiator.png')});await visibleBox().check();
 await page.waitForFunction(()=>document.querySelector('#modSelect').value==='metro_mod_ubludok_radiator');
 await page.screenshot({path:path.join(out,'ubludok-radiator.png')}).then(yes=>assert.notDeepEqual(yes,no));
 await page.locator('#modSelect').selectOption('metro_mod_glushak');await visibleBox().check();
 await page.locator('#modSelect').selectOption('metro_mod_ubludok_radiator');assert.equal(await visibleBox().isChecked(),false,'barrel slot excludes simultaneous suppressor/radiator');
 await select('metro_medved');assert.equal(await page.evaluate(()=>mwfEditor.type.numBullets),8);assert.deepEqual(await page.evaluate(()=>mwfEditor.type.acceptedBullets),['metro_12g_buckshot']);
 await select('metro_gatling');const button=page.locator('#rotorPreview');await button.focus();await page.keyboard.down('Space');await page.waitForFunction(()=>mwfEditor.motion.rotor.speed>.99);
 const first=await page.evaluate(()=>mwfEditor.motion.nodes.metroMotionRotor);await page.waitForFunction(first=>JSON.stringify(mwfEditor.motion.nodes.metroMotionRotor)!==JSON.stringify(first),first);
 await page.keyboard.up('Space');await page.waitForFunction(()=>mwfEditor.motion.rotor.speed===0);
 assert.deepEqual(errors,[]);assert.deepEqual(requests,[]);
 console.log('PASS actual v10 offline: all 25 guns, Ashot/Podonok/revolver stock both hands and exact removal, ADS/reload, Velopal six-round buckshot and new skin, radiator off/on and barrel exclusivity, Medved buckshot, Gatling spin/coast. No JS errors/HTTP. NOT Minecraft.');
}finally{await browser.close();}
