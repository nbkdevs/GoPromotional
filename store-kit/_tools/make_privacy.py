import os, html
REPO='/workspace/softrank/GoPromotional'; D=f'{REPO}/docs'; os.makedirs(f'{D}/privacy',exist_ok=True)
BASE='https://nbkdevs.github.io/GoPromotional'
ISSUES='https://github.com/nbkdevs/GoPromotional/issues'
EFFECTIVE='19 October 2026'
GP='https://www.gopromotional.co.uk'
EXT={
 'sip-quick':dict(name='Sip Quick',page=GP+'/branded-products/branded-water-bottles',
   what='Log water intake from your toolbar, track a daily goal, review the last 7 days and copy your stats.',
   stored=['your daily goal (in millilitres)','your daily water totals, keyed by calendar date','the individual amounts you add each day, so the <em>Undo</em> button can remove the last entry'],
   clear='Use <em>Clear today</em> in the popup to remove today\u2019s entries'),
 'pack-quick':dict(name='Pack Quick',page=GP+'/branded-products/tote-bags',
   what='Manage packing lists in your toolbar: multiple lists, templates, filters and copy-to-clipboard.',
   stored=['the names of your packing lists','the items on each list and whether each item is ticked as packed','which list was open last'],
   clear='Use <em>Clear packed</em> or <em>Delete list</em> in the popup to remove items or whole lists'),
 'mug-tap':dict(name='Mug Tap',page=GP+'/branded-products/travel-mugs',
   what='Track reusable vs disposable cup use, see weekly rates and streaks, and copy a short habit summary.',
   stored=['each cup you log: whether it was reusable or disposable, the date and time you tapped the button, and the optional short drink note you type (for example \u201ccoffee\u201d)'],
   clear='Use <em>Undo</em> to remove the last entry or <em>Clear week</em> to remove the log'),
}
THEME={
 'harbor-dusk':dict(name='Harbor Dusk',page=GP+'/',desc='a photographic dusk harbour theme'),
 'alpine-mist':dict(name='Alpine Mist',page=GP+'/branded-products/branded-water-bottles',desc='a photographic alpine lake theme'),
 'canvas-trail':dict(name='Canvas Trail',page=GP+'/branded-products/tote-bags',desc='a photographic golden-hour trail theme'),
 'ember-steam':dict(name='Ember Steam',page=GP+'/branded-products/travel-mugs',desc='a photographic rainy-window evening theme'),
}
ANCHOR={GP+'/':'branded merchandise',GP+'/branded-products/branded-water-bottles':'branded water bottles for event marketing',
        GP+'/branded-products/tote-bags':'branded tote bags for event marketing',GP+'/branded-products/travel-mugs':'branded travel mugs for event marketing'}
for _e in [*EXT.values(),*THEME.values()]: _e['anchor']=ANCHOR[_e['page']]  # exact anchor text for every in-text link
CSS='''body{margin:0;font:16px/1.6 system-ui,-apple-system,"Segoe UI",Roboto,sans-serif;color:#1c2430;background:#f6f7f9}
main{max-width:760px;margin:0 auto;padding:40px 22px 64px;background:#fff;min-height:100vh;box-sizing:border-box}
h1{font-size:1.8rem;line-height:1.25;margin:0 0 4px}h2{font-size:1.15rem;margin:2em 0 .4em}
.meta{color:#5f6b7a;margin:0 0 1.5em}a{color:#0e6a85}table{border-collapse:collapse;width:100%;margin:.6em 0}
th,td{border:1px solid #d7dde5;padding:8px 10px;text-align:left;vertical-align:top}th{background:#f1f4f7}
.summary{background:#eef6f8;border-left:4px solid #0e7490;padding:12px 16px;margin:1em 0}footer{margin-top:3em;color:#5f6b7a;font-size:.9rem}'''
def page(title,body,canon):
    return f'''<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{html.escape(title)}</title>
<meta name="description" content="{html.escape(title)}: what data is handled and how.">
<link rel="canonical" href="{canon}">
<style>{CSS}</style>
</head>
<body>
<main>
{body}
<footer><a href="{BASE}/">All privacy policies</a> · Questions: <a href="{ISSUES}">open an issue on GitHub</a></footer>
</main>
</body>
</html>
'''
for slug,e in EXT.items():
    url=f'{BASE}/privacy/{slug}.html'
    li=''.join(f'<li>{s}</li>' for s in e['stored'])
    body=f'''<h1>{e["name"]} privacy policy</h1>
<p class="meta">Applies to the {e["name"]} browser extension for Firefox, Microsoft Edge and Google Chrome. Effective {EFFECTIVE}.</p>
<div class="summary"><strong>Summary:</strong> {e["name"]} does not collect, transmit, sell or share any personal data. Everything you enter stays in your browser\u2019s local extension storage on your own device.</div>

<h2>What {e["name"]} does</h2>
<p>{e["what"]}</p>

<h2>Data stored on your device</h2>
<p>To remember your entries between popup opens, {e["name"]} saves the following using the browser\u2019s extension storage API (<code>storage.local</code>):</p>
<ul>{li}</ul>
<p>This data is stored only in your browser profile on your device. It is not sent to us or to anyone else, and it is not synchronised to any account or cloud service by the extension.</p>

<h2>Data we collect</h2>
<p>None. The extension has no server, makes no network requests, and contains no analytics, advertising, tracking pixels, crash reporting or remotely hosted code. We do not receive your browsing history, page content, location, contacts, identifiers or any other information.</p>

<h2>Permissions</h2>
<table>
<tr><th>Permission</th><th>Why it is needed</th></tr>
<tr><td><code>storage</code></td><td>Saves the data listed above locally so it is still there the next time you open the popup.</td></tr>
</table>
<p>{e["name"]} requests no host permissions, no access to tabs or browsing history, and no clipboard permission. The <em>Copy</em> buttons write a short text summary to your clipboard only when you click them.</p>

<h2>External link</h2>
<p>The popup shows a small footer link, \u201cMore: <a href="{e["page"]}">{e["anchor"]}</a>\u201d. Nothing happens unless you click it. If you do, the page opens in a new tab and is governed by that website\u2019s own privacy and cookie policies, not this one. The extension does not pass any of your data to that site.</p>

<h2>Deleting your data</h2>
<p>{e["clear"]}. Uninstalling the extension removes all of its stored data from your browser.</p>

<h2>Children</h2>
<p>The extension does not knowingly collect information from anyone, including children, because it collects no information at all.</p>

<h2>Changes to this policy</h2>
<p>If a future version changes how data is handled, this page will be updated before that version is released, and the effective date above will change.</p>

<h2>Contact</h2>
<p>Questions or requests: <a href="{ISSUES}">{ISSUES}</a>.</p>'''
    open(f'{D}/privacy/{slug}.html','w').write(page(f'{e["name"]} privacy policy',body,url))
for slug,t in THEME.items():
    url=f'{BASE}/privacy/{slug}.html'
    body=f'''<h1>{t["name"]} privacy policy</h1>
<p class="meta">Applies to the {t["name"]} browser theme for Google Chrome and Firefox. Effective {EFFECTIVE}.</p>
<div class="summary"><strong>Summary:</strong> {t["name"]} is {t["desc"]}. It contains only colours and images. It collects no data, stores no data and requests no permissions.</div>

<h2>What the theme contains</h2>
<p>The theme package holds a manifest with colour values and a few image files (the new tab background and toolbar artwork). It contains no JavaScript or other executable code, so it cannot read pages, track activity or make network requests.</p>

<h2>Data we collect</h2>
<p>None. The theme does not collect, store, transmit, sell or share any personal or non-personal data. There is no analytics, advertising or tracking of any kind.</p>

<h2>Local storage</h2>
<p>The theme stores nothing itself. Your browser keeps a record of which theme you have installed as part of its normal settings; that record stays in your browser and is handled under your browser\u2019s own privacy policy.</p>

<h2>Permissions</h2>
<p>None. Browser themes cannot request extension permissions.</p>

<h2>External link</h2>
<p>The store listing for this theme links to <a href="{t["page"]}">{t["anchor"]}</a>. The theme itself shows no links. If you visit that website, it is governed by its own privacy and cookie policies, not this one.</p>

<h2>Removing the theme</h2>
<p>Switch back to the default theme in your browser settings (Chrome: Settings \u2192 Appearance \u2192 Reset to default; Firefox: Add-ons and themes \u2192 Themes) or uninstall it. No data is left behind.</p>

<h2>Changes to this policy</h2>
<p>If this ever changes, this page will be updated first and the effective date above will change.</p>

<h2>Contact</h2>
<p>Questions or requests: <a href="{ISSUES}">{ISSUES}</a>.</p>'''
    open(f'{D}/privacy/{slug}.html','w').write(page(f'{t["name"]} privacy policy',body,url))
rows=''.join(f'<tr><td>{e["name"]}</td><td>Extension (Firefox, Edge, Chrome)</td><td><a href="privacy/{s}.html">{BASE}/privacy/{s}.html</a></td></tr>' for s,e in EXT.items())
rows+=''.join(f'<tr><td>{t["name"]}</td><td>Theme (Chrome, Firefox)</td><td><a href="privacy/{s}.html">{BASE}/privacy/{s}.html</a></td></tr>' for s,t in THEME.items())
body=f'''<h1>Privacy policies</h1>
<p class="meta">Browser extensions and themes in the <a href="https://github.com/nbkdevs/GoPromotional">nbkdevs/GoPromotional</a> repository. Effective {EFFECTIVE}.</p>
<p>None of these items collects or transmits personal data. The extensions keep your entries in local browser storage only; the themes contain only colours and images.</p>
<table><tr><th>Item</th><th>Type</th><th>Policy</th></tr>{rows}</table>'''
open(f'{D}/index.html','w').write(page('Privacy policies',body,BASE+'/'))
open(f'{D}/.nojekyll','w').write('')
print(sorted(os.listdir(D+'/privacy')))
