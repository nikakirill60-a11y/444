import assert from 'node:assert/strict';
import {mkdirSync} from 'node:fs';
const {chromium} = await import(process.env.PLAYWRIGHT_MODULE || 'playwright-core');
const browser = await chromium.launch({headless:true,executablePath:process.env.CHROMIUM_EXECUTABLE_PATH||undefined,args:['--no-sandbox','--disable-dev-shm-usage']});
const base = process.env.SITE_URL || 'http://127.0.0.1:3000';
mkdirSync('artifacts/website-wiki',{recursive:true});
try {
  for (const width of [320,390,768,1024,1440]) {
    const page = await browser.newPage({viewport:{width,height:1000}}), errors=[];
    page.on('pageerror', e=>errors.push(e.message));
    page.on('response', r=>{if(r.status()>=400)errors.push(`${r.status()} ${r.url()}`);});
    await page.goto(base+'/wiki.html');
    const search = page.getByLabel('Поиск в выбранном разделе');
    const result = page.getByRole('status');
    const visible = page.locator('.catalog-card:visible');
    await result.filter({hasText:'Найдено: 28'}).waitFor();
    assert.equal(await visible.count(),24);
    await page.getByRole('button',{name:'Показать ещё 4',exact:true}).click();
    assert.equal(await visible.count(),28);
    await search.fill('Велопал'); assert.equal(await visible.count(),1);
    const velopal = page.locator('#entry-modularwarfare-metro_velopal');
    await velopal.locator('summary').click();
    assert.ok((await velopal.textContent()).includes('Внутренняя ёмкость6'));
    await velopal.getByRole('link',{name:'Дробовой патрон 12 калибра',exact:true}).click();
    await page.getByRole('tab',{name:/Предметы/,selected:true}).waitFor();
    assert.equal(await search.inputValue(),'');
    const bullet = page.locator('#entry-modularwarfare-metro_12g_buckshot');
    assert.ok(await bullet.locator('details').getAttribute('open')!==null);
    await page.goBack(); await page.getByRole('tab',{name:/Оружие/,selected:true}).waitFor();
    await page.goto(base+'/wiki.html#entry-metroport-mcmod_zapal_vest');
    await page.getByRole('tab',{name:/Броня/,selected:true}).waitFor();
    const late = page.locator('#entry-metroport-mcmod_zapal_vest');
    assert.ok(await late.isVisible()); assert.ok(await late.locator('details').getAttribute('open')!==null);
    await page.reload(); await late.locator('details[open]').waitFor();
    await page.getByRole('tab',{name:/Мутанты/}).click();
    assert.equal(await visible.count(),18);
    await page.getByLabel('Подкатегория').selectOption('BlackRazid');assert.equal(await visible.count(),12);
    await search.fill('КРЫСА');assert.equal(await visible.count(),1);
    await page.locator('#entry-metroport-blackrazid_rat summary').click();
    assert.ok((await page.locator('#entry-metroport-blackrazid_rat').textContent()).includes('Не атакует'));
    await search.fill('<img src=x onerror=alert(1)>');assert.equal(await visible.count(),0);
    await page.locator('.catalog-no-results:visible').waitFor();
    assert.equal(await page.locator('img[src="x"]').count(),0);
    await page.getByRole('button',{name:'Сбросить',exact:true}).click();assert.equal(await visible.count(),18);
    await search.fill('темный');assert.equal(await visible.count(),1); // ё / е normalization
    await page.getByRole('button',{name:'Сбросить',exact:true}).click();
    await page.getByRole('tab',{name:/Предметы/}).click();
    await page.getByLabel('Подкатегория').selectOption('Обвесы');
    await result.filter({hasText:'Найдено: 28'}).waitFor();
    await page.getByRole('button',{name:'Показать ещё 4',exact:true}).click();assert.equal(await visible.count(),28);
    await search.fill('радиатор');assert.equal(await visible.count(),1);
    await page.goto(base+'/wiki.html#entry-modularwarfare-metro_velopal');
    await velopal.locator('details[open]').waitFor();
    await page.waitForFunction(()=>[...document.querySelectorAll('.catalog-card:not([hidden]) img')].every(i=>i.complete&&i.naturalWidth>0));
    assert.equal(await page.evaluate(()=>document.documentElement.scrollWidth>innerWidth),false,'overflow '+width);
    if(width===390||width===1440){await page.evaluate(()=>scrollTo(0,0));await page.screenshot({path:`artifacts/website-wiki/catalog-${width}.png`,fullPage:true,animations:'disabled'});}
    assert.deepEqual(errors,[]);
    await page.close();console.log(`PASS ${width}px: catalogue, search, filters, pagination, stats, cross-links, history, deep links, image loads, reflow`);
  }
  const context=await browser.newContext({javaScriptEnabled:false,viewport:{width:390,height:900}}),page=await context.newPage();
  await page.goto(base+'/wiki.html#entry-modularwarfare-metro_velopal');
  assert.equal(await page.locator('.catalog-card:visible').count(),260);
  await page.locator('#entry-modularwarfare-metro_velopal summary').click();
  assert.ok((await page.locator('#entry-modularwarfare-metro_velopal').textContent()).includes('Внутренняя ёмкость6'));
  await context.close(); console.log('PASS without JavaScript: all 260 articles, native disclosure, anchors');
} finally { await browser.close(); }
