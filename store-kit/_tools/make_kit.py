import os, json, zipfile
from PIL import Image
REPO='/workspace/softrank/GoPromotional'; K=f'{REPO}/store-kit'
GP='https://www.gopromotional.co.uk'
PAGES={'homepage':GP+'/','water-bottles':GP+'/branded-products/branded-water-bottles',
       'tote-bags':GP+'/branded-products/tote-bags','travel-mugs':GP+'/branded-products/travel-mugs'}
ANCHOR={'homepage':'branded merchandise','water-bottles':'branded water bottles for event marketing',
        'tote-bags':'branded tote bags for event marketing','travel-mugs':'branded travel mugs for event marketing'}
SUPPORT='https://github.com/nbkdevs/GoPromotional/issues'
EDGE_PRIV='https://www.gopromotional.co.uk/privacy'  # GoPromotional site policy (footer link; HTTP 200 on 19 Oct 2026)
problems=[]
def size(rel):
    p=f'{REPO}/{rel}'
    if not os.path.exists(p): problems.append('missing '+rel); return 'MISSING'
    im=Image.open(p); return f'{im.size[0]}x{im.size[1]} {im.mode}'
def img(rel,need,note=''):
    s=size(rel); ok='OK' if s.split()[0]==need else f'**WRONG SIZE (need {need})**'
    if ok!='OK': problems.append(f'{rel} {s} need {need}')
    return f'| `{rel}` | {s} | {need} | {ok} | {note} |'
def zipinfo(rel):
    z=zipfile.ZipFile(f'{REPO}/{rel}'); names=z.namelist()
    assert 'manifest.json' in names, rel
    m=json.loads(z.read('manifest.json'))
    return f'`{rel}` ({len(names)} entries, `manifest.json` at zip root, version {m["version"]})'
def chk(label,text,limit):
    n=len(text)
    if n>limit: problems.append(f'{label} {n}>{limit}')
    return f'**{label}** ({n}/{limit} chars)\n\n```\n{text}\n```\n'
def block(label,text): return f'**{label}**\n\n```\n{text}\n```\n'

THEMES={
 'homepage':dict(slug='harbor-dusk',fx_intro='Harbor Dusk gives Firefox the calm of a harbour at dusk: a moonlit pink-and-violet sky with warm pier lights fades in behind the right side of the tab strip, while tabs and the toolbar stay in soft slate grey and white with dark, easy-to-read text.',name='Harbor Dusk',
   summary='A calm photographic harbour at dusk for Chrome: moonlit water, warm pier lights and a soft evening sky on every new tab.',
   amo_summary='A calm photographic harbour at dusk: slate-grey tab strip, white toolbar and a moonlit sky with warm pier lights. Matches the Chrome theme of the same name.',
   body='''Harbor Dusk brings a quiet harbour at dusk to your browser. Each new tab opens on a photographic scene of still water, a wooden pier lit by warm lamps and a fading pink-and-violet sky, while the tab strip and toolbar stay in soft slate grey and white with dark, easy-to-read text.

What you get
• Full-width 1920×1080 photographic new tab background
• Muted slate-grey tab strip with high-contrast tab and bookmark text
• White toolbar and address bar for everyday readability
• Theme only: no permissions, no scripts and no data collection

To switch back at any time, open Settings → Appearance → Reset to default.''',
   fx_body_extra='Firefox themes cannot set a new-tab background image, so the Firefox version carries the Harbor Dusk colours and a dusk-sky header behind the tab strip.',
   mention='Harbor Dusk is one of a small set of free themes published alongside GoPromotional, a UK supplier of branded merchandise: https://www.gopromotional.co.uk/',
   single='Changes the colours and new tab background of Chrome to a dusk harbour photo theme. It is a theme only and contains no code.',
   cws_cat='Themes (theme uploads are listed under Themes; if a sub-category is offered, choose the landscape/nature option)'),
 'water-bottles':dict(slug='alpine-mist',fx_intro='Alpine Mist gives Firefox a cool, light look: misty mountains and a pine-fringed alpine lake fade in behind the right side of the tab strip, while tabs sit on a pale blue-white background with deep teal text and the toolbar stays white.',name='Alpine Mist',
   summary='A calm photographic alpine lake theme for Chrome: misty turquoise water, cool morning light and soft mountain haze.',
   amo_summary='A cool, light theme with a misty alpine lake header, pale blue-white tab strip and teal accents. Matches the Chrome theme of the same name.',
   body='''Alpine Mist is a light, cool-toned theme built around a photograph of a still alpine lake at dawn: turquoise water, drifting mist, pine forest and distant peaks in soft morning light. The tab strip is a pale blue-white with deep teal text, and the toolbar stays white so pages and the address bar remain easy to read.

What you get
• Full-width 1920×1080 photographic new tab background
• Pale blue-white tab strip with deep teal tab and bookmark text
• Clean white toolbar and address bar
• Theme only: no permissions, no scripts and no data collection

To switch back at any time, open Settings → Appearance → Reset to default.''',
   fx_body_extra='Firefox themes cannot set a new-tab background image, so the Firefox version carries the Alpine Mist colours and a misty-lake header behind the tab strip.',
   mention='Alpine Mist is part of a small set of free themes published alongside GoPromotional, who supply branded water bottles for event marketing: https://www.gopromotional.co.uk/branded-products/branded-water-bottles',
   single='Changes the colours and new tab background of Chrome to an alpine lake photo theme. It is a theme only and contains no code.',
   cws_cat='Themes (choose the landscape/nature sub-category if offered)'),
 'tote-bags':dict(slug='canvas-trail',fx_intro='Canvas Trail gives Firefox a warm, light look: golden-hour hills and a lone tree fade in behind the right side of the tab strip, while tabs sit on soft linen grey with dark brown text and the toolbar stays a warm off-white.',name='Canvas Trail',
   summary='A warm photographic golden-hour trail theme for Chrome: sunlit grass, soft rolling hills and an open evening sky.',
   amo_summary='A warm, light theme with a golden-hour countryside header, soft linen tab strip and brown accents. Matches the Chrome theme of the same name.',
   body='''Canvas Trail is a warm, light theme based on a photograph of a dirt trail winding through golden grass and gentle hills in late-afternoon sun, with a lone tree on the skyline. The tab strip is a soft linen grey, the toolbar a warm off-white, and text is a deep brown for comfortable contrast.

What you get
• Full-width 1920×1080 photographic new tab background
• Linen-grey tab strip with dark brown tab text and amber-brown bookmark text
• Warm off-white toolbar and white address bar
• Theme only: no permissions, no scripts and no data collection

To switch back at any time, open Settings → Appearance → Reset to default.''',
   fx_body_extra='Firefox themes cannot set a new-tab background image, so the Firefox version carries the Canvas Trail colours and a golden-hour header behind the tab strip.',
   mention='Canvas Trail is part of a small set of free themes published alongside GoPromotional, who supply branded tote bags for event marketing: https://www.gopromotional.co.uk/branded-products/tote-bags',
   single='Changes the colours and new tab background of Chrome to a golden-hour countryside photo theme. It is a theme only and contains no code.',
   cws_cat='Themes (choose the landscape/nature sub-category if offered)'),
 'travel-mugs':dict(slug='ember-steam',fx_intro='Ember Steam gives Firefox a cosy dark look: rain on a window with amber lamplight and soft bokeh fades in behind the right side of the tab strip, while tabs and the toolbar stay deep espresso brown with cream and soft-amber text.',name='Ember Steam',
   summary='A cosy dark photographic theme for Chrome: rain on a window at dusk, amber lamplight, soft bokeh and a warm interior glow.',
   amo_summary='A cosy dark theme with a rainy-window header, deep espresso-brown tab strip and warm cream text. Matches the Chrome theme of the same name.',
   body='''Ember Steam is a dark, warm theme built around a photograph of rain running down a window on an autumn evening, with amber lamplight inside and blurred street lights beyond. The tab strip and toolbar are deep espresso brown with cream and soft-amber text, which is easy on the eyes in the evening.

What you get
• Full-width 1920×1080 photographic new tab background
• Dark espresso-brown tab strip and toolbar with cream tab text
• Amber bookmark text and a dark address bar
• Theme only: no permissions, no scripts and no data collection

To switch back at any time, open Settings → Appearance → Reset to default.''',
   fx_body_extra='Firefox themes cannot set a new-tab background image, so the Firefox version carries the Ember Steam colours and a rainy-window header behind the tab strip.',
   mention='Ember Steam is part of a small set of free themes published alongside GoPromotional, who supply branded travel mugs for event marketing: https://www.gopromotional.co.uk/branded-products/travel-mugs',
   single='Changes the colours and new tab background of Chrome to a dark rainy-window photo theme. It is a theme only and contains no code.',
   cws_cat='Themes (choose the dark sub-category if offered)'),
}
EXTS={
 'water-bottles':dict(slug='sip-quick',name='Sip Quick',key='sip_quick_v1',
   cws_summary='Log water in one click, track a daily goal, review the last 7 days and copy your stats. Stored locally, no tracking.',
   amo_summary='Log water intake from your toolbar in one click. Set a daily goal, undo mistakes, review the last 7 days and copy a stats summary. Everything stays in local storage on your device: no account, no tracking.',
   body='''Sip Quick is a small hydration tracker that lives in your toolbar. Click the icon, tap the amount you just drank and watch today's total move towards your goal. There is no account, no sign-up and nothing to sync.

Features
• One-click +150, +250, +330 and +500 ml buttons, plus any custom amount
• Daily goal with a progress bar and an "ml to go" counter
• Undo the last entry if you tap the wrong button
• The last 7 days at a glance, with each day's total
• Copy stats: puts a short plain-text summary on your clipboard for a note or chat
• Clear today to start the day again

Privacy
Sip Quick keeps your goal and daily totals in your browser's local extension storage and nowhere else. It makes no network requests, has no analytics and uses a single permission: storage.

It is handy on desk days, at conferences and during long events, when it is easy to forget to drink.''',
   mention="If you're planning event giveaways, the popup footer also links to GoPromotional's branded water bottles for event marketing: https://www.gopromotional.co.uk/branded-products/branded-water-bottles",
   single='Sip Quick lets the user log daily water intake, track it against a daily goal and review the last 7 days, all inside the toolbar popup.',
   storage='Sip Quick uses storage.local to save the user\'s daily goal, the water total for each calendar day and the list of amounts added each day (so Undo can remove the last entry). Without it, totals would be lost every time the popup closes. The data never leaves the device; the extension makes no network requests.',
   amo_cat='Other', edge_cat='Productivity', cws_cat='Lifestyle → Well-being',
   tests=[
    'Install the package and click the Sip Quick (water bottle) icon in the toolbar; pin it from the Extensions menu if it is hidden. The popup opens showing "0 ml today", "0%" and "Goal 2000 ml · 2000 ml to go".',
    'Click "+250". Expected: the total shows 250, the chip shows 13%, the progress bar fills slightly and the label reads "Goal 2000 ml · 1750 ml to go". The "Today" row under "Last 7 days" shows 250 ml.',
    'Click "+500", then type 120 in "Custom ml" and click "Add". Expected: total 870 ml (44%).',
    'Click "Undo". Expected: the last entry (120 ml) is removed and the total returns to 750 ml.',
    'Type 1500 in "Daily goal ml" and click "Set goal". Expected: "Goal saved" appears, the label reads "Goal 1500 ml · 750 ml to go" and the chip shows 50%.',
    'Close the popup and open it again. Expected: the total (750 ml) and goal (1500 ml) are still there, saved via the storage permission.',
    'Click "Copy stats" and paste into any text field. Expected: "Hydration goal: 1500 ml" followed by one line per day for the last 7 days; a green "Stats copied" message appears.',
    'Click "Clear today". Expected: today\'s total returns to 0 ml; the goal is kept.',
    'The footer link "More: branded water bottles for event marketing" opens the GoPromotional water bottles page in a new tab. It is a plain link; no data is sent.',
   ]),
 'tote-bags':dict(slug='pack-quick',name='Pack Quick',key='pack_quick_v2',
   cws_summary='Packing lists in your toolbar: multiple lists, work and travel templates, tick-off, filter and copy. Stored locally.',
   amo_summary='Keep packing lists in your toolbar. Create several named lists, start from work-day or travel templates, tick items off, filter long lists and copy a checklist. Lists stay in local storage on your device: no account, no tracking.',
   body='''Pack Quick keeps packing lists in your toolbar, so you can tick things off as they go into the bag. Keep separate lists for work days, trips, gym kit or event set-up, and pick up where you left off every time you open it.

Features
• Multiple named lists with a quick switcher, plus rename and delete
• Work day and Travel starter templates (only adds items not already on the list)
• Tick items as packed and see an "x/y packed" counter
• Filter to find an item in a long list
• Clear packed to reset a list for next time
• Copy list: a plain-text checklist on your clipboard, ready to share

Privacy
Pack Quick keeps your lists in your browser's local extension storage and nowhere else. It makes no network requests, has no analytics and uses a single permission: storage.

It works well for exhibitors and event teams packing a stand, as well as for everyday bags.''',
   mention="Packing for a trade show? GoPromotional has a range of branded tote bags for event marketing: https://www.gopromotional.co.uk/branded-products/tote-bags",
   single='Pack Quick lets the user create and manage packing checklists (add, tick off, filter, copy) inside the toolbar popup.',
   storage='Pack Quick uses storage.local to save the user\'s packing lists: list names, the items on each list, whether each item is ticked, and which list was last open. Without it, lists would disappear when the popup closes. The data never leaves the device; the extension makes no network requests.',
   amo_cat='Other', edge_cat='Productivity', cws_cat='Productivity → Workflow & Planning (or Lifestyle → Travel)',
   tests=[
    'Install the package and click the Pack Quick (checklist bag) icon in the toolbar; pin it from the Extensions menu if it is hidden. The popup opens with a list called "Today’s bag", "0/0 packed" and "No items yet. Add something or use a template."',
    'Type "Umbrella" in "Add item" and click "Add". Expected: the item appears in the list and the counter shows "0/1 packed".',
    'Click "Travel". Expected: travel starter items (Passport / ID, Tickets, Phone charger, …) are added; clicking it again does not add duplicates.',
    'Tick two items. Expected: they are marked packed and move below unpacked items; the counter updates (for example "2/9 packed").',
    'Type "ch" in "Filter items…". Expected: only matching items (e.g. "Phone charger") are shown. Clear the filter.',
    'Close the popup and reopen it. Expected: the items and their ticked state are still there, saved via the storage permission.',
    'Click "Copy list" and paste into any text field. Expected: the list name followed by "[x]"/"[ ]" lines; a green "List copied" message appears.',
    'Click "Clear packed". Expected: the ticked items are removed (for example "0/7 packed").',
    'Click "New", type "Trade show" in "Rename list" and click "Rename". Expected: a second list named "Trade show" is selected; you can switch lists with the dropdown.',
    'With "Trade show" selected, click "Delete list" and confirm the dialog. Expected: that list is removed and "Today’s bag" is shown again. Clicking "Delete list" when only one list remains shows "Keep at least one list" instead.',
    'The footer link "More: branded tote bags for event marketing" opens the GoPromotional tote bags page in a new tab. It is a plain link; no data is sent.',
   ]),
 'travel-mugs':dict(slug='mug-tap',name='Mug Tap',key='mug_tap_v2',
   cws_summary='Log reusable vs disposable cups in one tap and see your weekly reusable rate and streak. Stored locally, no tracking.',
   amo_summary='A tiny habit tracker for reusable cups. Tap once per drink to log a reusable or disposable cup, then see today\'s count, your weekly reusable rate and streak, and copy a week summary. Stored locally: no account, no tracking.',
   body='''Mug Tap is a tiny habit tracker for reusable cups. Each time you get a drink, tap whether you used your own cup or a disposable one. Mug Tap shows today's count, your reusable rate for the week and how many days in a row you've remembered your cup.

Features
• One tap to log a reusable or a disposable cup, with an optional note (for example "flat white")
• Today's reusable count and your current streak
• Last-7-days totals and reusable rate
• A recent log of your last 10 drinks
• Undo the last entry, or clear the week to start again
• Copy week summary as plain text

Privacy
Mug Tap keeps your log in your browser's local extension storage and nowhere else. It makes no network requests, has no analytics and uses a single permission: storage.

A simple nudge for anyone trying to cut down on single-use cups.''',
   mention="Kitting out a team or an event? GoPromotional supplies branded travel mugs for event marketing: https://www.gopromotional.co.uk/branded-products/travel-mugs",
   single='Mug Tap lets the user log whether each drink used a reusable or disposable cup and shows a simple daily/weekly habit summary inside the toolbar popup.',
   storage='Mug Tap uses storage.local to save the user\'s drink log: for each entry, reusable or disposable, the date and time of the tap, and an optional short note the user types. This is needed so today\'s count, weekly rate and streak survive closing the popup. The data never leaves the device; the extension makes no network requests.',
   amo_cat='Other', edge_cat='Productivity', cws_cat='Lifestyle → Well-being',
   tests=[
    'Install the package and click the Mug Tap (mug) icon in the toolbar; pin it from the Extensions menu if it is hidden. The popup shows "0 reusable today", "streak 0" and "No drinks logged yet."',
    'Type "coffee" in the drink note field and click "Used reusable cup". Expected: today shows 1, the chip shows "streak 1", the "Recent" list shows "Reusable · coffee" with today\'s date, and the note field clears.',
    'Click "Disposable cup". Expected: the week line reads "Last 7 days: 1 reusable · 1 disposable" and the reusable rate shows 50%.',
    'Click "Undo". Expected: the disposable entry is removed and the rate returns to 100%.',
    'Close the popup and reopen it. Expected: the entry is still listed, saved via the storage permission.',
    'Click "Copy week summary" and paste into any text field. Expected: "Mug habit · last 7 days" with reusable/disposable counts, rate and streak; a green "Summary copied" message appears.',
    'Click "Clear week" and confirm the dialog. Expected: entries from the last 7 days are cleared and counts return to 0.',
    'The footer link "More: branded travel mugs for event marketing" opens the GoPromotional travel mugs page in a new tab. It is a plain link; no data is sent.',
   ]),
}
CWS_PRIV_THEME='''- Permission justification: none requested (themes cannot request permissions)
- Remote code: **No, I am not using remote code**
- Data usage: tick **none** of the data types (no personally identifiable info, health, financial, authentication, personal communications, location, web history, user activity or website content)
- Certifications (tick all three): I do not sell or transfer user data to third parties outside the approved use cases · I do not use or transfer user data for purposes unrelated to the item's single purpose · I do not use or transfer user data to determine creditworthiness or for lending
- (The CWS dashboard may not show the Privacy tab for theme packages; if it does, use these answers.)'''
def write(path,txt):
    os.makedirs(os.path.dirname(path),exist_ok=True); open(path,'w').write(txt)

for g,t in THEMES.items():
    slug=t['slug']; url=PAGES[g]
    desc=t['body']+'\n\n'+t['mention']
    imgs=[img(f'{g}/store-images/chrome-theme/icon-128.png','128x128','CWS store icon'),
          img(f'{g}/store-images/chrome-theme/screenshot-1280x800.png','1280x800','CWS screenshot (photographic v2, matches theme)'),
          img(f'{g}/chrome-theme/images/store_screenshot.png','1280x800','optional 2nd screenshot'),
          img(f'{g}/store-images/chrome-theme/promo-440x280.png','440x280','CWS small promo tile (required)'),
          img(f'{g}/store-images/chrome-theme/marquee-1400x560.png','1400x560','CWS marquee (optional)'),
          img(f'{g}/firefox-theme/images/header.png','1920x200','Firefox theme header (inside zip; AMO auto-generates theme previews)')]
    extra=''
    if g=='homepage':
        extra='\n> Do **not** use the loose `homepage/chrome 1280.png` / `chrome-440.png` (and their `(copy)` duplicates): they show the old illustrated harbour, not the photographic v2.0.0 theme.\n'
    feats='\n'.join(l for l in t['body'].split('\n\n')[1].split('\n') if '1920×1080' not in l)
    fx_desc=t['fx_intro']+'\n\n'+feats+'\n\n'+t['fx_body_extra']+'\n\nTo switch back, open Add-ons and themes → Themes and enable the default theme.\n\n'+t['mention']
    md=f'''# {t["name"]} — Chrome Web Store theme (+ optional Firefox theme)

Page group: `{g}/` → `{url}`  ·  preferred anchor: **{ANCHOR[g]}**
Source: `{g}/chrome-theme/` (MV3 theme, v2.0.0). Nothing has been submitted.

## Chrome Web Store (theme)

| Field | Value |
|---|---|
| Name | {t["name"]} (from manifest) |
| Upload zip | {zipinfo(f"{g}/chrome-theme/chrome-theme.zip")} |
| Category | {t["cws_cat"]} |
| Language | English (UK) |
| Homepage URL / website | {url} |
| Support URL | {SUPPORT} |
| Privacy policy URL | **leave blank** (decision: no privacy policy URL on Chrome Web Store) |
| Mature content | No |

{chk("Summary (CWS short description)",t["summary"],132)}
{block("Full description (plain text; one GoPromotional mention, last paragraph)",desc)}
**Privacy practices**
- Single purpose: {t["single"]}
{CWS_PRIV_THEME}

### Images (sizes verified with PIL)
| File | Actual | Required | Check | Use |
|---|---|---|---|---|
{chr(10).join(imgs)}
{extra}
## Firefox AMO theme (optional)

Built from the Chrome colours: `{g}/firefox-theme/` (MV2 static theme, gecko id `{slug}@gopromotional.co.uk`, strict_min_version 109.0, data_collection_permissions none).

| Field | Value |
|---|---|
| Upload zip | {zipinfo(f"{g}/firefox-theme/firefox-theme.zip")} |
| Name | {t["name"]} |
| Suggested slug | `{slug}` |
| Category | Scenery |
| Homepage | {url} |
| Support website | {SUPPORT} |
| Privacy policy | **leave blank** (decision: no privacy policy on AMO) |
| License | All Rights Reserved (or MIT if the repo gets a LICENSE) |

{chk("Summary (AMO)",t["amo_summary"],250)}
{block("Description (AMO)",fx_desc)}
{block("Notes to reviewer (AMO)",f"Static theme only: manifest.json + one header image (images/header.png). No scripts, no permissions, no data collection. Colours are converted from our {t['name']} Chrome theme. Test: about:addons → Themes → enable {t['name']}.")}
'''
    write(f'{K}/{g}/{slug}.md',md)

for g,e in EXTS.items():
    slug=e['slug']; url=PAGES[g]
    desc=e['body']+'\n\n'+e['mention']
    im=f'store-kit/{g}/images/{slug}'
    edge_logo=f'{g}/edge-extension/store-logo-300.png' if g!='tote-bags' else f'{im}-logo-300x300.png'
    imgs=[img(f'{im}-screenshot-01-1280x800.png','1280x800','GENERATED: real popup render (CWS/Edge/AMO screenshot 1)'),
          img(f'{im}-screenshot-02-1280x800.png','1280x800','GENERATED: real popup render (screenshot 2)'),
          img(edge_logo,'300x300','Edge Store logo'+(' (GENERATED from branding/extension/icon-512.png; repo logo-300 has black corners)' if g=='tote-bags' else ' (existing, transparent corners)')),
          img(f'{im}-logo-300x300.png','300x300','GENERATED alt Edge logo (same art, transparent)') if g!='tote-bags' else '',
          img(f'{g}/branding/extension/icon-128.png','128x128','CWS store icon / AMO icon is read from the zip'),
          img(f'{im}-promo-440x280.png','440x280','GENERATED: Edge small promo tile (optional) / CWS small promo tile')]
    imgs=[x for x in imgs if x]
    notes='\n'.join(f'{i}. {s}' for i,s in enumerate(e['tests'],1))
    notes+=f'\n\nPermissions: "storage" only, used for storage.local to keep the data above on the device. No host permissions, no content scripts, no background script, no remote code, no network requests. No account or login is needed.\nPrivacy policy: {EDGE_PRIV}'
    amo_notes=f'''{e["name"]} is a self-contained toolbar popup (popup.html / popup.js / popup.css, no build step, no minification, no third-party libraries), so no source code upload is needed.
It requests only the "storage" permission and stores data with storage.local under the key "{e["key"]}". It makes no network requests and declares data_collection_permissions: {{"required": ["none"]}}.
The only external URL is a plain footer link (target=_blank) to {url}.
To test: click the toolbar icon and follow these steps:
''' + '\n'.join(f'{i}. {s}' for i,s in enumerate(e['tests'][1:],1))
    md=f'''# {e["name"]} — Firefox AMO + Microsoft Edge Add-ons (+ optional Chrome Web Store)

Page group: `{g}/` → `{url}`  ·  preferred anchor: **{ANCHOR[g]}**
Sources: `{g}/firefox-extension/` and `{g}/edge-extension/` (identical MV3 popup code; Firefox adds the gecko block). Version 1.1.0. Nothing has been submitted.

## Shared listing copy

{chk("Summary — CWS / Edge short description",e["cws_summary"],132)}
{chk("Summary — AMO",e["amo_summary"],250)}
{block("Full description (plain text; one GoPromotional mention, last paragraph)",desc)}
**Single-purpose statement**

> {e["single"]}

**Permission justification — `storage`**

> {e["storage"]}

**Data usage (all stores): no data collected**
- Personal or sensitive data collected/transmitted: **none**. Everything stays in `storage.local` on the user's device.
- Remote code: **No**.
- CWS data-usage checkboxes: tick none (no PII, health, financial, authentication, communications, location, web history, user activity, website content). Tick all three certifications (no selling/transfer, no unrelated use, no creditworthiness use).
- Edge "Does your extension access, collect or transmit personal information?": **No**. Privacy policy URL: {EDGE_PRIV} (GoPromotional site policy).
- AMO: manifest declares `data_collection_permissions.required: ["none"]`; answer that the add-on collects no data.

| Field | Value |
|---|---|
| Website / homepage | {url} |
| Support URL | {SUPPORT} |
| Privacy policy URL | Chrome Web Store and AMO: **leave blank**. Edge: {EDGE_PRIV} |

### Images (sizes verified with PIL)
| File | Actual | Required | Check | Use |
|---|---|---|---|---|
{chr(10).join(imgs)}

> Not used: `{g}/store-images/extension/screenshot-*`, `promo-440x280.png` and `marquee-1400x560.png`. They show the **Android app** (phone mockups branded {"SipLog" if g=="water-bottles" else "BagPack" if g=="tote-bags" else "MugDay"}), not this browser extension, which risks a "misleading listing" rejection. The two generated screenshots are real headless-Chrome renders of `popup.html` (with demo data and the new footer link), padded onto a 1280×800 canvas.

## Firefox AMO

| Field | Value |
|---|---|
| Upload zip | {zipinfo(f"{g}/firefox-extension/firefox-extension.zip")} |
| Add-on id | `{slug}@gopromotional.co.uk` (changed from `{slug}@local`), strict_min_version 140.0 (desktop), gecko_android strict_min_version 142.0 |
| Name | {e["name"]} |
| Suggested slug | `{slug}`{" — the old `@local` submission may already hold this slug; fall back to `"+slug+"-tracker` or delete the stale draft first" if g!="tote-bags" else ""} |
| Categories | {e["amo_cat"]} |
| Homepage | {url} |
| Support website | {SUPPORT} |
| Privacy policy | **leave blank** (decision: no privacy policy on AMO) |
| License | All Rights Reserved (or MIT if the repo gets a LICENSE) |
| Source code submission | No (code is not minified or transpiled) |
| Screenshots | `{im}-screenshot-01-1280x800.png`, `{im}-screenshot-02-1280x800.png` |

{block("Notes to reviewer (AMO)",amo_notes)}
## Microsoft Edge Add-ons

| Field | Value |
|---|---|
| Upload zip | {zipinfo(f"{g}/edge-extension/edge-extension.zip")} |
| Display name | {e["name"]} (from manifest) |
| Category | {e["edge_cat"]} |
| Website URL | {url} |
| Support contact | {SUPPORT} |
| Privacy policy URL | {EDGE_PRIV} (GoPromotional's own site privacy policy, linked from the site footer; returns HTTP 200) |
| Store logo (300×300) | `{edge_logo}` |
| Small promo tile (440×280, optional) | `{im}-promo-440x280.png` |
| Screenshots | `{im}-screenshot-01-1280x800.png`, `{im}-screenshot-02-1280x800.png` |
| Mature content | No |

Edge's description field needs at least 250 characters; the full description above is {len(desc)} characters.

{block("NOTES_FOR_CERTIFICATION (paste into 'Notes for certification')",notes)}
## Chrome Web Store extension (optional)

Built from the Edge folder (same MV3 code, no gecko block): {zipinfo(f"store-kit/{g}/dist/{slug}-chrome-extension.zip")}.
Use the shared copy above with category **{e["cws_cat"]}**, the 128 icon, both screenshots and the 440×280 tile. Privacy policy URL: **leave blank**. Privacy tab: single purpose and storage justification as above, remote code **No**, no data types ticked, all three certifications ticked.
'''
    write(f'{K}/{g}/{slug}.md',md)
print('\n'.join(problems) or 'no problems')
