import assert from 'node:assert/strict';
import {mkdirSync} from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..');
const base=process.env.SITE_URL||'http://127.0.0.1:3000';
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE||'playwright-core');
const browser=await chromium.launch({headless:true,executablePath:process.env.CHROMIUM_EXECUTABLE_PATH||undefined,args:['--no-sandbox','--disable-dev-shm-usage']});
const out=path.join(root,'artifacts/website-wiki');mkdirSync(out,{recursive:true});
try{
 for(const width of [320,390,768,1024,1440]){
  const page=await browser.newPage({viewport:{width,height:1000}}),errors=[],bad=[];
  page.on('pageerror',e=>errors.push(e.message));page.on('response',r=>{if(r.status()>=400)bad.push(r.url()+':'+r.status());});
  await page.goto(base);if(await page.locator('.menu-toggle').isVisible())await page.locator('.menu-toggle').click();
  await page.locator('#navigation').getByRole('link',{name:'Вики',exact:true}).click();
  await page.waitForURL('**/wiki.html');await page.getByRole('tab',{name:'Оружие',exact:true}).waitFor();
  assert.equal(await page.getByRole('tab').count(),4);
  const check=async name=>{await page.getByRole('tab',{name,exact:true}).click();assert.equal(await page.getByRole('tab',{selected:true}).textContent().then(s=>s.replace(/0[1-4]|↗/g,'').trim()),name);assert.equal(await page.getByRole('tabpanel').count(),1);await page.getByRole('tabpanel').getByRole('heading',{name,exact:true}).waitFor();};
  for(const name of ['Броня','Мутанты','Предметы','Оружие'])await check(name);
  const active=()=>page.getByRole('tab',{selected:true});
  await active().focus();await page.keyboard.press('End');assert.equal(await active().getAttribute('aria-controls'),'items');
  await page.keyboard.press('ArrowRight');assert.equal(await active().getAttribute('aria-controls'),'weapons');
  await page.keyboard.press('ArrowLeft');assert.equal(await active().getAttribute('aria-controls'),'items');
  await page.keyboard.press('Home');assert.equal(await active().getAttribute('aria-controls'),'weapons');
  await page.keyboard.press('Tab');assert.equal(await page.evaluate(()=>document.activeElement.getAttribute('role')),'tabpanel');
  await check('Броня');await check('Мутанты');await page.goBack();assert.equal(await active().getAttribute('aria-controls'),'armor');await page.goForward();assert.equal(await active().getAttribute('aria-controls'),'mutants');
  await page.goto(base+'/wiki.html#items');await page.getByRole('tab',{name:'Предметы',selected:true}).waitFor();
  await page.reload();await page.getByRole('tab',{name:'Предметы',selected:true}).waitFor();
  await page.goto(base+'/wiki.html#unknown');await page.getByRole('tab',{name:'Оружие',selected:true}).waitFor();
  assert.equal(await page.evaluate(()=>document.documentElement.scrollWidth>innerWidth),false,'horizontal overflow at '+width);
  if(await page.locator('.menu-toggle').isVisible()){
   const menu=page.locator('.menu-toggle');await menu.click();assert.equal(await menu.getAttribute('aria-expanded'),'true');await page.keyboard.press('Escape');assert.equal(await menu.getAttribute('aria-expanded'),'false');
  }
  await page.evaluate(()=>scrollTo(0,0));if(width===390||width===1440)await page.screenshot({path:path.join(out,'wiki-'+width+'.png'),fullPage:true,animations:'disabled'});
  assert.deepEqual(errors,[]);assert.deepEqual(bad,[]);await page.close();console.log('PASS wiki '+width+'px: four tabs, keyboard, history, deep links, menu, no overflow/errors');
 }
 const context=await browser.newContext({javaScriptEnabled:false,viewport:{width:390,height:900}}),page=await context.newPage();
 await page.goto(base+'/wiki.html');assert.equal(await page.locator('.wiki-panel:visible').count(),4);await page.locator('#tab-armor').click();assert.ok(page.url().endsWith('#armor'));await context.close();
 console.log('PASS wiki without JS: four readable sections and working anchors');
}finally{await browser.close();}
