/* Optional browser smoke test. Install playwright in a temporary directory and
 * put its node_modules on NODE_PATH. Supply CHROMIUM_EXECUTABLE_PATH when using
 * a system/serverless Chromium, otherwise Playwright's browser is used.
 * Start website/ on port 3000 first. No downloads or external links are opened.
 * Screenshots/logs go into ignored .cache/, never presented as game screenshots.
 */
'use strict';
const assert = require('node:assert/strict');
const fs = require('node:fs');
const { chromium } = require('playwright');

(async () => {
  fs.mkdirSync('.cache', { recursive: true });
  const browser = await chromium.launch({
    executablePath: process.env.CHROMIUM_EXECUTABLE_PATH || undefined,
    args: ['--no-sandbox', '--disable-dev-shm-usage', '--use-gl=angle', '--use-angle=swiftshader', '--enable-unsafe-swiftshader'],
    headless: true,
  });
  const errors = [];
  try {
    for (const [width, height] of [[1440, 1000], [390, 844], [320, 740], [768, 1024]]) {
      const context = await browser.newContext({ viewport: { width, height }, reducedMotion: 'reduce' });
      const page = await context.newPage();
      page.on('pageerror', error => errors.push(error.message));
      page.on('response', response => {
        if (response.status() >= 400) errors.push(response.status() + ' ' + response.url());
      });
      await page.goto(process.env.SITE_URL || 'http://127.0.0.1:3000', { waitUntil: 'networkidle' });
      await page.evaluate(() => document.fonts.ready);
      assert.equal(await page.evaluate(() => document.documentElement.scrollWidth > innerWidth), false, 'horizontal overflow at ' + width);
      await page.screenshot({ path: '.cache/minetro-website-' + width + '.png', fullPage: true });
      await page.locator('summary').first().click();
      assert.notEqual(await page.locator('details').first().getAttribute('open'), null);
      if (width < 760) {
        const toggle = page.locator('.menu-toggle');
        await toggle.click();
        assert.equal(await toggle.getAttribute('aria-expanded'), 'true');
        await page.locator('#navigation a').first().click();
        assert.equal(await toggle.getAttribute('aria-expanded'), 'false');
        await toggle.click();
        await page.keyboard.press('Escape');
        assert.equal(await toggle.getAttribute('aria-expanded'), 'false');
      }
      // Exercise both clipboard outcomes deterministically, including denied access.
      for (const success of [true, false]) {
        await page.evaluate(success => {
          window.copiedAddress = null;
          Object.defineProperty(navigator, 'clipboard', { configurable: true, value: {
            writeText: async text => {
              if (!success) throw new Error('Permission denied');
              window.copiedAddress = text;
            }
          }});
        }, success);
        await page.locator('#copy-server').click();
        await page.waitForFunction(success => document.querySelector('#copy-status').textContent === (success ? 'Адрес скопирован' : 'Скопируй вручную: localhost:25565'), success);
        assert.equal(await page.evaluate(() => window.copiedAddress), success ? 'localhost:25565' : null);
      }
      console.log('PASS viewport ' + width + ': assets, reflow, FAQ, navigation, clipboard branches');
      await context.close();
    }
    assert.deepEqual(errors, []);
  } finally {
    await browser.close();
  }
})().catch(error => { console.error(error); process.exitCode = 1; });
