# Mug Tap — Firefox AMO + Microsoft Edge Add-ons

Page group: `travel-mugs/` → `https://www.gopromotional.co.uk/branded-products/travel-mugs`  ·  preferred anchor: **branded travel mugs for event marketing**
Sources: `travel-mugs/firefox-extension/` and `travel-mugs/edge-extension/` (identical MV3 popup code; Firefox adds the gecko block). Version 1.1.1. Nothing has been submitted.

## Shared listing copy

**Summary — CWS / Edge short description** (117/132 chars)

```
Log reusable vs disposable cups in one tap and see your weekly reusable rate and streak. Stored locally, no tracking.
```

**Summary — AMO** (223/250 chars)

```
A tiny habit tracker for reusable cups. Tap once per drink to log a reusable or disposable cup, then see today's count, your weekly reusable rate and streak, and copy a week summary. Stored locally: no account, no tracking.
```

**Full description (plain text; no vendor links or mentions)**

```
Mug Tap is a tiny habit tracker for reusable cups. Each time you get a drink, tap whether you used your own cup or a disposable one. Mug Tap shows today's count, your reusable rate for the week and how many days in a row you've remembered your cup.

Features
• One tap to log a reusable or a disposable cup, with an optional note (for example "flat white")
• Today's reusable count and your current streak
• Last-7-days totals and reusable rate
• A recent log of your last 10 drinks
• Undo the last entry, or clear the week to start again
• Copy week summary as plain text

Privacy
Mug Tap keeps your log in your browser's local extension storage and nowhere else. It makes no network requests, has no analytics and uses a single permission: storage.

A simple nudge for anyone trying to cut down on single-use cups.
```

**Single-purpose statement**

> Mug Tap lets the user log whether each drink used a reusable or disposable cup and shows a simple daily/weekly habit summary inside the toolbar popup.

**Permission justification — `storage`**

> Mug Tap uses storage.local to save the user's drink log: for each entry, reusable or disposable, the date and time of the tap, and an optional short note the user types. This is needed so today's count, weekly rate and streak survive closing the popup. The data never leaves the device; the extension makes no network requests.

**Data usage (all stores): no data collected**
- Personal or sensitive data collected/transmitted: **none**. Everything stays in `storage.local` on the user's device.
- Remote code: **No**.
- CWS data-usage checkboxes: tick none (no PII, health, financial, authentication, communications, location, web history, user activity, website content). Tick all three certifications (no selling/transfer, no unrelated use, no creditworthiness use).
- Edge "Does your extension access, collect or transmit personal information?": **No**. Privacy policy URL: https://www.gopromotional.co.uk/privacy (GoPromotional site policy).
- AMO: manifest declares `data_collection_permissions.required: ["none"]`; answer that the add-on collects no data.

| Field | Value |
|---|---|
| Website / homepage | https://www.gopromotional.co.uk/branded-products/travel-mugs |
| Support URL | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy URL | Chrome Web Store and AMO: **leave blank**. Edge: https://www.gopromotional.co.uk/privacy |

### Images (sizes verified with PIL)
| File | Actual | Required | Check | Use |
|---|---|---|---|---|
| `store-kit/travel-mugs/images/mug-tap-screenshot-01-1280x800.png` | 1280x800 RGB | 1280x800 | OK | GENERATED: real popup render (CWS/Edge/AMO screenshot 1) |
| `store-kit/travel-mugs/images/mug-tap-screenshot-02-1280x800.png` | 1280x800 RGB | 1280x800 | OK | GENERATED: real popup render (screenshot 2) |
| `travel-mugs/edge-extension/store-logo-300.png` | 300x300 RGBA | 300x300 | OK | Edge Store logo (existing, transparent corners) |
| `store-kit/travel-mugs/images/mug-tap-logo-300x300.png` | 300x300 RGBA | 300x300 | OK | GENERATED alt Edge logo (same art, transparent) |
| `travel-mugs/branding/extension/icon-128.png` | 128x128 RGBA | 128x128 | OK | CWS store icon / AMO icon is read from the zip |
| `store-kit/travel-mugs/images/mug-tap-promo-440x280.png` | 440x280 RGB | 440x280 | OK | GENERATED: Edge small promo tile (optional) / CWS small promo tile |

> Not used: `travel-mugs/store-images/extension/screenshot-*`, `promo-440x280.png` and `marquee-1400x560.png`. They show the **Android app** (phone mockups branded MugDay), not this browser extension, which risks a "misleading listing" rejection. The two generated screenshots are real headless-Chrome renders of `popup.html` (with demo data), padded onto a 1280×800 canvas.

## Firefox AMO

| Field | Value |
|---|---|
| Upload zip | `travel-mugs/firefox-extension/firefox-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.1) |
| Add-on id | `mug-tap@gopromotional.co.uk` (changed from `mug-tap@local`), strict_min_version 140.0 (desktop), gecko_android strict_min_version 142.0 |
| Name | Mug Tap |
| Suggested slug | `mug-tap` — the old `@local` submission may already hold this slug; fall back to `mug-tap-tracker` or delete the stale draft first |
| Categories | Other |
| Homepage | https://www.gopromotional.co.uk/branded-products/travel-mugs |
| Support website | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy | **leave blank** (decision: no privacy policy on AMO) |
| License | All Rights Reserved (or MIT if the repo gets a LICENSE) |
| Source code submission | No (code is not minified or transpiled) |
| Screenshots | `store-kit/travel-mugs/images/mug-tap-screenshot-01-1280x800.png`, `store-kit/travel-mugs/images/mug-tap-screenshot-02-1280x800.png` |

**Notes to reviewer (AMO)**

```
Mug Tap is a self-contained toolbar popup (popup.html / popup.js / popup.css, no build step, no minification, no third-party libraries), so no source code upload is needed.
It requests only the "storage" permission and stores data with storage.local under the key "mug_tap_v2". It makes no network requests and declares data_collection_permissions: {"required": ["none"]}.
There are no external URLs in the popup UI.
To test: click the toolbar icon and follow these steps:
1. Type "coffee" in the drink note field and click "Used reusable cup". Expected: today shows 1, the chip shows "streak 1", the "Recent" list shows "Reusable · coffee" with today's date, and the note field clears.
2. Click "Disposable cup". Expected: the week line reads "Last 7 days: 1 reusable · 1 disposable" and the reusable rate shows 50%.
3. Click "Undo". Expected: the disposable entry is removed and the rate returns to 100%.
4. Close the popup and reopen it. Expected: the entry is still listed, saved via the storage permission.
5. Click "Copy week summary" and paste into any text field. Expected: "Mug habit · last 7 days" with reusable/disposable counts, rate and streak; a green "Summary copied" message appears.
6. Click "Clear week" and confirm the dialog. Expected: entries from the last 7 days are cleared and counts return to 0.
```

## Microsoft Edge Add-ons

| Field | Value |
|---|---|
| Upload zip | `travel-mugs/edge-extension/edge-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.1) |
| Display name | Mug Tap (from manifest) |
| Category | Productivity |
| Website URL | https://www.gopromotional.co.uk/branded-products/travel-mugs |
| Support contact | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy URL | https://www.gopromotional.co.uk/privacy (GoPromotional's own site privacy policy, linked from the site footer; returns HTTP 200) |
| Store logo (300×300) | `travel-mugs/edge-extension/store-logo-300.png` |
| Small promo tile (440×280, optional) | `store-kit/travel-mugs/images/mug-tap-promo-440x280.png` |
| Screenshots | `store-kit/travel-mugs/images/mug-tap-screenshot-01-1280x800.png`, `store-kit/travel-mugs/images/mug-tap-screenshot-02-1280x800.png` |
| Mature content | No |

Edge's description field needs at least 250 characters; the full description above is 816 characters.

**NOTES_FOR_CERTIFICATION (paste into 'Notes for certification')**

```
1. Install the package and click the Mug Tap (mug) icon in the toolbar; pin it from the Extensions menu if it is hidden. The popup shows "0 reusable today", "streak 0" and "No drinks logged yet."
2. Type "coffee" in the drink note field and click "Used reusable cup". Expected: today shows 1, the chip shows "streak 1", the "Recent" list shows "Reusable · coffee" with today's date, and the note field clears.
3. Click "Disposable cup". Expected: the week line reads "Last 7 days: 1 reusable · 1 disposable" and the reusable rate shows 50%.
4. Click "Undo". Expected: the disposable entry is removed and the rate returns to 100%.
5. Close the popup and reopen it. Expected: the entry is still listed, saved via the storage permission.
6. Click "Copy week summary" and paste into any text field. Expected: "Mug habit · last 7 days" with reusable/disposable counts, rate and streak; a green "Summary copied" message appears.
7. Click "Clear week" and confirm the dialog. Expected: entries from the last 7 days are cleared and counts return to 0.

Permissions: "storage" only, used for storage.local to keep the data above on the device. No host permissions, no content scripts, no background script, no remote code, no network requests. No account or login is needed.
Privacy policy: https://www.gopromotional.co.uk/privacy
```


