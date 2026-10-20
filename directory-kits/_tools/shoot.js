const { chromium } = require('playwright-core');
const PAGES = {
  'homepage':      ['https://www.gopromotional.co.uk/', [null, 'h3:Quality promotional items', 'h3:More than branded merchandise']],
  'water-bottles': ['https://www.gopromotional.co.uk/branded-products/branded-water-bottles', [null, 'fav', 'grid2']],
  'tote-bags':     ['https://www.gopromotional.co.uk/branded-products/tote-bags', [null, 'fav', 'grid2']],
  'travel-mugs':   ['https://www.gopromotional.co.uk/branded-products/travel-mugs', [null, 'fav', 'grid2']],
};
const UA = 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/155.0 Safari/537.36';
async function settle(p) {
  const H = await p.evaluate(() => document.body.scrollHeight);
  for (let y = 0; y < H; y += 500) { await p.evaluate(y => window.scrollTo(0, y), y); await p.waitForTimeout(150); }
  await p.evaluate(() => window.scrollTo(0, 0)); await p.waitForLoadState('networkidle').catch(() => {}); await p.waitForTimeout(1500);
}
async function hideOverlays(p) {
  // dismiss any cookie/consent/popups if present
  for (const t of ['Accept all', 'Accept All', 'Accept', 'I agree', 'Allow all', 'Got it']) {
    const b = p.getByRole('button', { name: t, exact: true }); if (await b.count() && await b.first().isVisible()) { await b.first().click().catch(() => {}); console.log('clicked', t); }
  }
}
async function yFor(p, spec) {
  if (!spec) return 0;
  return p.evaluate(spec => {
    let el;
    if (spec === 'fav' || spec === 'grid2') {
      const w = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT); let n; const prices = [];
      while ((n = w.nextNode())) if (/^£\d/.test(n.textContent.trim()) && n.parentElement.tagName === 'STRONG') prices.push(n.parentElement);
      const pe = prices[spec === 'fav' ? 0 : 16];
      return pe ? Math.round(pe.getBoundingClientRect().top + scrollY) - 300 : -1;
    }
    else { const [tag, txt] = spec.split(':'); el = [...document.querySelectorAll(tag)].find(e => e.innerText.trim().startsWith(txt)); }
    if (!el) return -1;
    return Math.max(0, Math.round(el.getBoundingClientRect().top + scrollY) - 30);
  }, spec);
}
(async () => {
  const b = await chromium.launch({ executablePath: '/usr/bin/google-chrome', args: ['--no-sandbox', '--hide-scrollbars'] });
  for (const [page, [url, shots]] of Object.entries(PAGES)) {
    const out = `/workspace/softrank/GoPromotional/directory-kits/${page}`;
    for (const [vw, vh, list] of [[1270, 760, shots], [1280, 800, [null]]]) {
      const ctx = await b.newContext({ viewport: { width: vw, height: vh }, userAgent: UA, locale: 'en-GB' });
      const p = await ctx.newPage();
      await p.goto(url, { waitUntil: 'networkidle', timeout: 90000 }).catch(e => console.log('goto', e.message));
      await p.waitForTimeout(2000); await hideOverlays(p); await settle(p); await hideOverlays(p);
      for (let i = 0; i < list.length; i++) {
        const y = await yFor(p, list[i]); if (y < 0) console.log(page, 'MISSING', list[i]);
        await p.evaluate(y => window.scrollTo(0, y), Math.max(0, y)); await p.waitForTimeout(1200);
        const f = vw === 1280 ? `${out}/screenshot-1280x800.png` : `${out}/gallery-${i + 1}.png`;
        await p.screenshot({ path: f }); console.log(page, f.split('/').pop(), 'y=', y);
      }
      await ctx.close();
    }
  }
  await b.close();
})();
