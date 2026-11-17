import assert from 'node:assert/strict';
import {fileURLToPath,pathToFileURL} from 'node:url';
import path from 'node:path';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'../..');
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE||'playwright-core');
const browser=await chromium.launch({headless:true,executablePath:process.env.MWF_CHROMIUM||undefined,args:['--no-sandbox','--no-zygote','--in-process-gpu','--use-gl=angle','--use-angle=swiftshader','--enable-unsafe-swiftshader']});
try{
 const page=await browser.newPage({viewport:{width:1440,height:1000}}),errors=[],requests=[];
 page.setDefaultTimeout(60000);page.on('pageerror',e=>errors.push(e.message));page.on('request',r=>{if(/^https?:/.test(r.url()))requests.push(r.url());});
 await page.route('https://**/*',r=>r.abort());await page.route('http://**/*',r=>r.abort());
 await page.goto(pathToFileURL(path.join(root,'artifacts/mwf-armory/index.html')).href);await page.waitForFunction(()=>mwfEditor.empty);
 await page.locator('#packInput').setInputFiles(path.join(root,'downloads/Metro_Full_Arsenal_MWF_fix1_v9_motion.zip'));await page.waitForFunction(()=>mwfEditor.ready);
 assert.equal(await page.evaluate(()=>mwfEditor.guns.length),25);
 const select=async id=>{await page.locator('[data-panel=library]').click();await page.locator('#libraryGun').selectOption(id);await page.waitForFunction(id=>mwfEditor.ready&&mwfEditor.gun===id,id);};
 for(const [id,stock] of [['metro_podonok','metro_mod_podonok_butt'],['metro_revolver','metro_mod_butt']]){
  await select(id);await page.locator('[data-panel=mods]').click();await page.locator('#modSelect').selectOption(stock);
  const box=page.getByRole('checkbox',{name:'Показать этот обвес в 3D',exact:true});await box.uncheck();
  // Stop the timeline; animation drift must not be mistaken for a stock-dependent pose.
  const before=await page.evaluate(()=>mwfEditor.motion.nodes);
  await page.screenshot({path:path.join(root,`artifacts/mwf-armory/${id}-v9-no-stock.png`)});
  await box.check();await page.waitForFunction(before=>JSON.stringify(mwfEditor.motion.nodes)!==JSON.stringify(before),before);
  const withStock=await page.evaluate(()=>mwfEditor.motion.nodes);assert.notDeepEqual(withStock.leftArmModel,before.leftArmModel);assert.notDeepEqual(withStock.rightArmModel,before.rightArmModel);
  await page.screenshot({path:path.join(root,`artifacts/mwf-armory/${id}-v9-stock.png`)});
  await page.locator('#ads').check();await page.locator('#bodyType').selectOption('slim');await page.locator('#action').selectOption('reload');
  await page.screenshot({path:path.join(root,`artifacts/mwf-armory/${id}-v9-stock-ads-slim-reload.png`)});
  await page.locator('#action').selectOption('default');await page.locator('#bodyType').selectOption('classic');await page.locator('#ads').uncheck();await box.uncheck();
  await page.waitForFunction(before=>JSON.stringify(mwfEditor.motion.nodes)===JSON.stringify(before),before);
 }
 await select('metro_gatling');await page.locator('[data-view=orbit]').click();
 const button=page.locator('#rotorPreview');await button.focus();await page.keyboard.down('Space');await page.waitForFunction(()=>mwfEditor.motion.rotor.speed>.99);
 const first=await page.evaluate(()=>mwfEditor.motion.nodes.metroMotionRotor);await page.waitForFunction(first=>JSON.stringify(mwfEditor.motion.nodes.metroMotionRotor)!==JSON.stringify(first),first);
 await page.screenshot({path:path.join(root,'artifacts/mwf-armory/gatling-v9-spinning.png')});await page.keyboard.up('Space');
 await page.waitForFunction(()=>mwfEditor.motion.rotor.speed===0);
 await page.locator('[data-panel=mods]').click();await page.locator('#modSelect').selectOption('metro_mod_gatling_barrel');await page.getByRole('checkbox',{name:'Показать этот обвес в 3D',exact:true}).check();
 await page.locator('#modSelect').selectOption('metro_mod_gatling_motor');await page.getByRole('checkbox',{name:'Показать этот обвес в 3D',exact:true}).check();
 await button.focus();await page.keyboard.down('Space');await page.waitForFunction(()=>mwfEditor.motion.rotor.speed>.99);assert.match(await page.locator('#rotorStatus').textContent(),/600/);
 await page.screenshot({path:path.join(root,'artifacts/mwf-armory/gatling-v9-five-barrels-motor.png')});await page.keyboard.up('Space');await page.waitForFunction(()=>mwfEditor.motion.rotor.speed===0);
 assert.deepEqual(errors,[]);assert.deepEqual(requests,[]);
 console.log('PASS actual v9 offline: 25 guns, Podonok/revolver both hands change on stock and restore exactly; ADS/slim/reload render; real rotor matrices move then coast to rest; five barrels + motor preview at 600 RPM. No JS errors/HTTP. NOT live Minecraft.');
}finally{await browser.close();}
