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
 await page.locator('#packInput').setInputFiles(path.join(root,'downloads/Metro_Full_Arsenal_MWF_fix1_v8_3d.zip'));
 await page.waitForFunction(()=>mwfEditor.ready);
 assert.equal(await page.evaluate(()=>mwfEditor.guns.length),25);
 for(const id of ['metro_medved','metro_ognemet_mini','metro_uboinik','metro_ubludok','metro_ashot','metro_ak2012','metro_revolver']){
  await page.locator('[data-panel=library]').click();
  await page.locator('#libraryGun').selectOption(id);
  await page.waitForFunction(id=>mwfEditor.ready&&mwfEditor.gun===id,id);
  if(id==='metro_uboinik'||id==='metro_ashot'){
   await page.locator('[data-panel=mods]').click();
   for(const mod of ['metro_mod_laser','metro_mod_kolimator','metro_mod_barrel']){
    if(!await page.locator(`#modSelect option[value=${mod}]`).count())continue;
    await page.locator('#modSelect').selectOption(mod);
    await page.getByRole('checkbox',{name:'Показать этот обвес в 3D',exact:true}).check();
   }
  }
  await page.locator('[data-view=fp]').click();
  await page.screenshot({path:path.join(root,`artifacts/mwf-armory/${id}-v8-fp.png`)});
  await page.locator('[data-view=orbit]').click();
  const png=await page.screenshot({path:path.join(root,`artifacts/mwf-armory/${id}-v8-orbit.png`)});
  assert.ok(png.length>20000);
 }
 assert.deepEqual(errors,[]);assert.deepEqual(requests,[]);
 console.log('PASS v8 actual 25-gun ZIP opens offline; seven changed/critical weapons render FP/orbit, shared Uboinik/Ashot attachments enabled, no JS errors or HTTP. Not Minecraft verification.');
}finally{await browser.close();}
