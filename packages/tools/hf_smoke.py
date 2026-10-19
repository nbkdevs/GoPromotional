#!/usr/bin/env python3
"""Headless-Chrome smoke test for the four Hugging Face Space pages.

Copies each packages/<page>/huggingface-space/ to a temp dir, injects a small script that drives the UI
(clicks, inputs) and records the results, then loads it with `google-chrome --headless=new --dump-dom`.
Fails on any uncaught JS error or wrong result. Also writes a screenshot per page to /tmp/hf-smoke/.
Usage: python3 packages/tools/hf_smoke.py
"""
import html, json, pathlib, re, shutil, subprocess, sys, tempfile

PK = pathlib.Path(__file__).resolve().parents[1]
HARNESS = """
<script>
window.__errors = [];
window.addEventListener('error', function (e) { window.__errors.push(String(e.message)); });
function __set(id, v) { var el = document.getElementById(id); if (el.type === 'checkbox') el.checked = v; else el.value = v; el.dispatchEvent(new Event('input', { bubbles: true })); el.dispatchEvent(new Event('change', { bubbles: true })); }
function __txt(id) { return document.getElementById(id).textContent.trim(); }
function __pad(n) { return String(n).padStart(2, '0'); }
function __today(off) { var d = new Date(Date.now() + (off || 0) * 864e5); return d.getFullYear() + '-' + __pad(d.getMonth() + 1) + '-' + __pad(d.getDate()); }
window.addEventListener('load', function () {
  var r = {};
  try { (function () { %s })(); } catch (e) { window.__errors.push('test: ' + e.message); }
  r.errors = window.__errors;
  var pre = document.createElement('pre'); pre.id = '__smoke'; pre.textContent = JSON.stringify(r); document.body.appendChild(pre);
});
</script>
"""
TESTS = {
  "homepage": ("""
    __set('date', __today(45));
    r.countdown = __txt('cd');
    __set('att', 250); __set('upt', 35); __set('days', 1); __set('pp', 1); __set('buf', 10);
    r.giveaways = __txt('gq');
    r.rows = document.querySelectorAll('#tl tr').length;
    document.querySelector('#tl input').click();
    r.summary = __txt('tlsum');
    r.orderBy = __txt('ob');
  """, lambda r: r["countdown"] == "45 days to go" and r["giveaways"] == "97 items" and r["rows"] == 12
                 and r["summary"] == "1 of 12 tasks done." and re.match(r"^\d{4}-\d{2}-\d{2}$", r["orderBy"])),
  "water-bottles": ("""
    document.querySelector('[data-ml="500"]').click();
    __set('amt', '16oz'); document.getElementById('add').click();
    r.total = __txt('tot');
    __set('w', 70); __set('act', 30); __set('hot', true);
    r.suggested = __txt('sugg');
    document.getElementById('use').click();
    r.sub = __txt('sub');
    document.getElementById('undo').click();
    r.afterUndo = __txt('tot');
    __set('amt', 'a glass'); document.getElementById('add').click();
    r.err = __txt('err');
    r.weekRows = document.querySelectorAll('#week tr').length;
    r.saved = JSON.parse(localStorage.getItem('gopromotional-siplog')).goal;
  """, lambda r: r["total"] == "973 ml" and r["suggested"] == "3150 ml" and r["sub"].startswith("31% of 3150 ml")
                 and r["afterUndo"] == "500 ml" and "Cannot read amount" in r["err"] and r["weekRows"] == 7 and r["saved"] == 3150),
  "tote-bags": ("""
    document.querySelectorAll('#tpls input').forEach(function (x) { x.checked = x.value === 'gym'; });
    __set('title', 'Gym bag'); __set('nights', 0); __set('extras', 'Protein bar');
    document.getElementById('build').click();
    r.before = __txt('prog');
    document.querySelector('#list input').click();
    r.after = __txt('prog');
    r.md = document.getElementById('md').value.split('\\n').slice(0, 4).join('|');
    __set('q', 'towel'); r.filtered = document.querySelectorAll('#list input').length; __set('q', '');
    document.getElementById('md').value = '# Trip\\n## Clothes\\n- [ ] 4 × T-shirts\\n- [x] Socks\\n';
    document.getElementById('imp').click();
    r.imported = __txt('prog') + ' / ' + __txt('lt');
  """, lambda r: r["before"] == "0/10 packed (0%)" and r["after"] == "1/10 packed (10%)"
                 and r["md"] == "# Gym bag||## Gym|- [x] Gym clothes" and r["filtered"] == 1 and r["imported"] == "1/2 packed (50%) / Trip"),
  "travel-mugs": ("""
    __set('drink', 'flat-white'); __set('time', __today() + 'T08:00'); __set('reu', true);
    document.getElementById('add').click();
    __set('drink', 'filter-coffee'); __set('time', __today() + 'T09:00'); __set('reu', false);
    document.getElementById('add').click();
    r.total = __txt('tot');
    r.rows = document.querySelectorAll('#list tr').length;
    r.rate = __txt('rate');
    __set('lim', '200'); r.sub = __txt('sub');
    r.sleep = __txt('sleep');
    r.chart = document.querySelectorAll('#chart polyline').length;
    __set('drink', 'tea'); __set('mg', '5000'); document.getElementById('add').click(); r.err = __txt('err');
  """, lambda r: r["total"] == "266 mg" and r["rows"] == 2 and r["rate"] == "50%" and "66 mg over the 200 mg limit" in r["sub"]
                 and (r["sleep"].startswith("Below 50 mg from about") or r["sleep"].startswith("Already below")) and r["chart"] == 1
                 and "mg must be between 0 and 1000" in r["err"]),
}

def main():
    out = pathlib.Path("/tmp/hf-smoke"); out.mkdir(exist_ok=True)
    ok = True
    for page, (js, check) in TESTS.items():
        with tempfile.TemporaryDirectory() as t:
            space = pathlib.Path(t) / "space"; shutil.copytree(PK / page / "huggingface-space", space)
            src = (space / "index.html").read_text()
            # Plain page first: screenshot + no-error check without the harness.
            shot = subprocess.run(["google-chrome", "--headless=new", "--no-sandbox", "--disable-gpu", f"--user-data-dir={t}/p1",
                                   "--window-size=1100,1400", "--virtual-time-budget=3000", f"--screenshot={out}/{page}.png",
                                   (space / "index.html").as_uri()], capture_output=True, text=True, timeout=120)
            (space / "index.html").write_text(src.replace("</head>", HARNESS % js + "</head>", 1))
            dom = subprocess.run(["google-chrome", "--headless=new", "--no-sandbox", "--disable-gpu", f"--user-data-dir={t}/p2",
                                  "--virtual-time-budget=5000", "--dump-dom", (space / "index.html").as_uri()],
                                 capture_output=True, text=True, timeout=120).stdout
            m = re.search(r'<pre id="__smoke">(.*?)</pre>', dom, re.S)
            r = json.loads(html.unescape(m.group(1))) if m else {"errors": ["no result (page did not load?)"]}
            good = bool(m) and not r["errors"] and bool(check(r))
            ok &= good
            print(("PASS" if good else "FAIL"), page, json.dumps(r, ensure_ascii=False))
    print(f"screenshots: {out}/<page>.png")
    return 0 if ok else 1

if __name__ == "__main__":
    sys.exit(main())
