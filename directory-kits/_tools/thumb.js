const { chromium } = require('playwright-core'); const fs=require('fs');
(async () => {
  const b = await chromium.launch({ executablePath: '/usr/bin/google-chrome', args: ['--no-sandbox'] });
  const p = await b.newPage({ viewport: { width: 240, height: 240 } });
  const svg = fs.readFileSync('fav.svg').toString('base64');
  await p.setContent(`<html><body style="margin:0;background:#fff;display:flex;align-items:center;justify-content:center;width:240px;height:240px"><img src="data:image/svg+xml;base64,${svg}" style="width:184px;height:184px;object-fit:contain"></body></html>`);
  await p.waitForTimeout(500); await p.screenshot({ path: 'thumb-240.png' }); await b.close();
})();
