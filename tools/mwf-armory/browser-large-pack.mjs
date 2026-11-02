// Real browser acceptance: empty standalone program -> >64 MiB ZIP -> >95 MiB project -> restore/export.
import assert from 'node:assert/strict';
import {readFileSync,statSync} from 'node:fs';
import {fileURLToPath,pathToFileURL} from 'node:url';
import path from 'node:path';
import {unzipSync,strFromU8} from 'fflate';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'../..');
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE||'playwright-core');
const browser=await chromium.launch({headless:true,executablePath:process.env.MWF_CHROMIUM||undefined,args:['--no-sandbox','--no-zygote','--single-process','--in-process-gpu','--use-gl=angle','--use-angle=swiftshader','--enable-unsafe-swiftshader']});
try{
 const page=await browser.newPage({viewport:{width:1500,height:1000}}),errors=[],requests=[];page.setDefaultTimeout(120000);
 page.on('pageerror',e=>errors.push(e.message));page.on('dialog',d=>d.accept());page.on('request',r=>{if(/^https?:/.test(r.url()))requests.push(r.url());});
 await page.route('https://**/*',r=>r.abort());await page.route('http://**/*',r=>r.abort());
 await page.goto(pathToFileURL(path.join(root,'artifacts/mwf-armory/index.html')).href);await page.waitForFunction(()=>window.mwfEditor?.empty);
 assert.deepEqual(await page.evaluate(()=>mwfEditor.guns),[]);assert.equal(await page.locator('#exportPack').isDisabled(),true);assert.equal(await page.locator('#createGun').isDisabled(),true);
 assert.equal(await page.locator('#loadLocal').isEnabled(),true);assert.equal(await page.locator('#openProject').isEnabled(),true);assert.equal(await page.locator('#emptyState').isVisible(),true);
 await page.screenshot({path:path.join(root,'artifacts/armory-v2-empty.png'),fullPage:true});
 await page.locator('#packInput').setInputFiles(path.join(root,'downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip'));
 await page.waitForFunction(()=>mwfEditor.ready&&mwfEditor.guns.length===23);
 assert.equal(await page.evaluate(()=>mwfEditor.gun),'metro_ashot');assert.equal(await page.locator('#libraryGun option:disabled').count(),19);
 assert.equal(await page.locator('#emptyState').isVisible(),false);assert.equal(await page.locator('#exportPack').isEnabled(),true);
 await page.locator('[data-panel=stats]').click();const damage=page.getByRole('spinbutton',{name:'Урон за пулю / дробину',exact:true});await damage.fill('7');await damage.press('Tab');
 const [project]=await Promise.all([page.waitForEvent('download'),page.locator('#saveProject').click()]);const projectPath=path.join(root,'artifacts/armory-large-project.json');await project.saveAs(projectPath);
 assert.ok(statSync(projectPath).size>95*1024*1024,'Exercise the old project-file AND base64 limits');
 await damage.fill('8');await damage.press('Tab');
 await page.locator('#projectInput').setInputFiles(projectPath);await page.waitForFunction(()=>mwfEditor.ready&&mwfEditor.type.gunDamage===7&&document.getElementById('message').textContent.startsWith('Проект восстановлен'));
 assert.equal(await page.evaluate(()=>mwfEditor.guns.length),23);
 const [download]=await Promise.all([page.waitForEvent('download'),page.locator('#exportPack').click()]);const out=path.join(root,'artifacts/armory-large-export.zip');await download.saveAs(out);
 assert.ok(statSync(out).size>64*1024*1024,'Export must also exceed the old ZIP limit');
 const source=unzipSync(readFileSync(path.join(root,'downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip'))),exported=unzipSync(readFileSync(out));
 assert.equal(Object.keys(exported).filter(n=>/^guns\/[^/]+\.json$/.test(n)).length,23);
 assert.equal(JSON.parse(strFromU8(exported['guns/metro_ashot.json'])).gunDamage,7);
 for(const name of ['guns/metro_ak.json','ammo/metro_rpk_ammo_big.json','assets/modularwarfare/skins/guns/metro_native_atlas.png'])assert.deepEqual(exported[name],source[name],name);
 await page.screenshot({path:path.join(root,'artifacts/armory-v2-large-pack.png'),fullPage:true});
 assert.deepEqual(errors,[]);assert.deepEqual(requests,[]);
 console.log(`Large-pack browser PASS: empty boot; 23 guns imported, 19 explicitly preserved without editing; ${statSync(projectPath).size} byte project saved/restored; ${statSync(out).size} byte ZIP exported; edited Ashot retained, magazine gun/ammo/shared atlas unchanged; 0 page errors, 0 HTTP requests.`);
}finally{await browser.close();}
