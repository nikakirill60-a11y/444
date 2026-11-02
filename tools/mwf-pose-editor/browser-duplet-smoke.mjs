// Optional: install playwright-core and set MWF_CHROMIUM to an installed Chromium binary.
import assert from 'node:assert/strict';
import {fileURLToPath,pathToFileURL} from 'node:url';
import path from 'node:path';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'../..');
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE||'playwright-core');
const browser=await chromium.launch({headless:true,executablePath:process.env.MWF_CHROMIUM||undefined,args:['--no-sandbox','--no-zygote','--single-process','--in-process-gpu','--use-gl=angle','--use-angle=swiftshader','--enable-unsafe-swiftshader']});
try{
const page=await browser.newPage({viewport:{width:1500,height:1000}}),errors=[],requests=[];
page.on('pageerror',e=>errors.push(e.message));page.on('dialog',d=>d.accept());page.on('request',r=>{if(/^https?:/.test(r.url()))requests.push(r.url());});
await page.route('http://**/*',r=>r.abort());await page.route('https://**/*',r=>r.abort());
await page.goto(pathToFileURL(path.join(root,'artifacts/mwf-pose-editor/index.html')).href);
await page.waitForFunction(()=>window.mwfEditor?.ready&&mwfEditor.gun==='metro_duplet');
await page.screenshot({path:path.join(root,'artifacts/duplet-fp.png'),fullPage:true});
await page.locator('summary').filter({hasText:'Выбор оружия / другой пак'}).click();assert.equal(await page.locator('#gunSelect option').count(),3);
await page.locator('#gunSelect').selectOption('metro_duplet_4');await page.waitForFunction(()=>mwfEditor.ready&&mwfEditor.gun==='metro_duplet_4');
assert.equal(await page.locator('#rounds').getAttribute('max'),'2');assert.equal(await page.locator('#attachments input').count(),5);
await page.locator('summary').filter({hasText:'Обвес в предпросмотре'}).click();assert.equal(await page.locator('#attachments input:checked').count(),1);
const checks=page.locator('#attachments input');await checks.nth(1).check();assert.equal(await page.locator('#attachments input:checked').count(),1);assert.equal(await checks.nth(0).isChecked(),false);await checks.nth(0).check();await checks.nth(3).check();await checks.nth(4).check();
await page.locator('#ads').check();await page.screenshot({path:path.join(root,'artifacts/duplet-ads-idle.png'),fullPage:true});await page.locator('#ads').uncheck();
await page.locator('#action').selectOption('reload');await page.waitForFunction(()=>document.getElementById('timeLabel').textContent.includes('3.00'));
await page.locator('#timeline').fill('2');await page.locator('#timeline').dispatchEvent('input');
await page.locator('#ads').check();await page.screenshot({path:path.join(root,'artifacts/duplet-ads.png'),fullPage:true});
await page.locator('[data-view=orbit]').click();await page.screenshot({path:path.join(root,'artifacts/duplet-four-orbit.png'),fullPage:true});
await page.getByRole('spinbutton',{name:'Положение x',exact:true}).fill('.02');await page.locator('#gunTitle').click();
await page.locator('#undo').click();assert.equal(await page.evaluate(()=>mwfEditor.edits.left.idle.p[0]),0);await page.locator('#redo').click();
const [project]=await Promise.all([page.waitForEvent('download'),page.locator('#saveProject').click()]);const projectPath=path.join(root,'artifacts/duplet-project.json');await project.saveAs(projectPath);
await page.locator('#resetHand').click();await page.locator('#projectInput').setInputFiles(projectPath);await page.waitForFunction(()=>mwfEditor.ready&&mwfEditor.edits.left.idle.p[0]===.02);
const [zip]=await Promise.all([page.waitForEvent('download'),page.locator('#exportPack').click()]);await zip.saveAs(path.join(root,'artifacts/duplet-browser-export.zip'));
await page.locator('#gunSelect').selectOption('metro_uboinik');await page.waitForFunction(()=>mwfEditor.ready&&mwfEditor.gun==='metro_uboinik');assert.equal(await page.locator('#rounds').getAttribute('max'),'6');
assert.deepEqual(errors,[]);assert.deepEqual(requests,[]);
console.log('Duplet browser smoke PASS: offline file://, three guns, 2/2/6 capacity, default +2, exclusive barrel previews, stock/grip, reload 3s; cosmetic +2 without bridge, ADS/orbit, hand editing, undo/redo, project download/import and actual ZIP export. No page errors or HTTP requests.');
}finally{await browser.close();}
