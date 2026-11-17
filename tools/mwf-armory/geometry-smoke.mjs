import assert from 'node:assert/strict';
import {fileURLToPath,pathToFileURL} from 'node:url';
import path from 'node:path';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'../..');
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE||'playwright-core');
const browser=await chromium.launch({headless:true,executablePath:process.env.MWF_CHROMIUM||undefined,args:['--no-sandbox','--no-zygote','--in-process-gpu','--use-gl=angle','--use-angle=swiftshader','--enable-unsafe-swiftshader']});
try{
 const page=await browser.newPage({viewport:{width:1440,height:1000}}),errors=[],requests=[];
 page.setDefaultTimeout(60000);page.on('pageerror',e=>errors.push(e.message));
 page.on('request',r=>{if(/^https?:/.test(r.url()))requests.push(r.url());});
 await page.route('https://**/*',r=>r.abort());await page.route('http://**/*',r=>r.abort());
 await page.goto(pathToFileURL(path.join(root,'artifacts/mwf-armory/index.html')).href);
 await page.waitForFunction(()=>window.mwfEditor?.empty);
 await page.locator('#packInput').setInputFiles(path.join(root,'downloads/Metro_Full_Arsenal_MWF_fix1_v7_geometry.zip'));
 await page.waitForFunction(()=>mwfEditor.ready);
 assert.equal(await page.evaluate(()=>mwfEditor.guns.length),23);
 for(const id of ['metro_ak2012','metro_revolver']){
  await page.locator('#libraryGun').selectOption(id);
  await page.waitForFunction(id=>mwfEditor.ready&&mwfEditor.gun===id,id);
  assert.equal(await page.evaluate(()=>mwfEditor.type.internalName),id);
  if(id==='metro_ak2012')assert.deepEqual(await page.evaluate(()=>mwfEditor.config.attachment.metro_ak2012_ammo.scale),{x:.1,y:.1,z:.107});
  const png=await page.screenshot({path:path.join(root,`artifacts/mwf-armory/${id}-v7-browser.png`)});
  assert.ok(png.length>20000,'nonempty rendered UI');
 }
 assert.deepEqual(errors,[]);assert.deepEqual(requests,[]);
 console.log('PASS v7 real 23-gun ZIP opens offline; AK2012 and revolver render without JS errors. Browser render only, not live Minecraft verification.');
}finally{await browser.close();}
