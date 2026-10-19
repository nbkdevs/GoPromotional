# Sip Quick — Firefox AMO + Microsoft Edge Add-ons (+ optional Chrome Web Store)

Page group: `water-bottles/` → https://www.gopromotional.co.uk/branded-products/branded-water-bottles  ·  preferred anchor: **branded water bottles for event marketing**
Sources: `water-bottles/firefox-extension/` and `water-bottles/edge-extension/` (identical MV3 popup code; Firefox adds the gecko block). Version 1.1.0. Nothing has been submitted.

## Shared listing copy

**Summary — CWS / Edge short description** (116/132 chars)

```
Log water in one click, track a daily goal, review the last 7 days and copy your stats. Stored locally, no tracking.
```

**Summary — AMO** (205/250 chars)

```
Log water intake from your toolbar in one click. Set a daily goal, undo mistakes, review the last 7 days and copy a stats summary. Everything stays in local storage on your device: no account, no tracking.
```

**Full description (plain text; one GoPromotional mention, last paragraph)**

```
Sip Quick is a small hydration tracker that lives in your toolbar. Click the icon, tap the amount you just drank and watch today's total move towards your goal. There is no account, no sign-up and nothing to sync.

Features
• One-click +150, +250, +330 and +500 ml buttons, plus any custom amount
• Daily goal with a progress bar and an "ml to go" counter
• Undo the last entry if you tap the wrong button
• The last 7 days at a glance, with each day's total
• Copy stats: puts a short plain-text summary on your clipboard for a note or chat
• Clear today to start the day again

Privacy
Sip Quick keeps your goal and daily totals in your browser's local extension storage and nowhere else. It makes no network requests, has no analytics and uses a single permission: storage.

It is handy on desk days, at conferences and during long events, when it is easy to forget to drink.

If you're planning event giveaways, the popup footer also links to GoPromotional's branded water bottles for event marketing: https://www.gopromotional.co.uk/branded-products/branded-water-bottles
```

**Single-purpose statement**

> Sip Quick lets the user log daily water intake, track it against a daily goal and review the last 7 days, all inside the toolbar popup.

**Permission justification — `storage`**

> Sip Quick uses storage.local to save the user's daily goal, the water total for each calendar day and the list of amounts added each day (so Undo can remove the last entry). Without it, totals would be lost every time the popup closes. The data never leaves the device; the extension makes no network requests.

**Data usage (all stores): no data collected**
- Personal or sensitive data collected/transmitted: **none**. Everything stays in `storage.local` on the user's device.
- Remote code: **No**.
- CWS data-usage checkboxes: tick none (no PII, health, financial, authentication, communications, location, web history, user activity, website content). Tick all three certifications (no selling/transfer, no unrelated use, no creditworthiness use).
- Edge "Does your extension access, collect or transmit personal information?": **No**. Privacy policy URL: https://www.gopromotional.co.uk/privacy (GoPromotional site policy).
- AMO: manifest declares `data_collection_permissions.required: ["none"]`; answer that the add-on collects no data.

| Field | Value |
|---|---|
| Website / homepage | https://www.gopromotional.co.uk/branded-products/branded-water-bottles |
| Support URL | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy URL | Chrome Web Store and AMO: **leave blank**. Edge: https://www.gopromotional.co.uk/privacy |

### Images (sizes verified with PIL)
| File | Actual | Required | Check | Use |
|---|---|---|---|---|
| `store-kit/water-bottles/images/sip-quick-screenshot-01-1280x800.png` | 1280x800 RGB | 1280x800 | OK | GENERATED: real popup render (CWS/Edge/AMO screenshot 1) |
| `store-kit/water-bottles/images/sip-quick-screenshot-02-1280x800.png` | 1280x800 RGB | 1280x800 | OK | GENERATED: real popup render (screenshot 2) |
| `water-bottles/edge-extension/store-logo-300.png` | 300x300 RGBA | 300x300 | OK | Edge Store logo (existing, transparent corners) |
| `store-kit/water-bottles/images/sip-quick-logo-300x300.png` | 300x300 RGBA | 300x300 | OK | GENERATED alt Edge logo (same art, transparent) |
| `water-bottles/branding/extension/icon-128.png` | 128x128 RGBA | 128x128 | OK | CWS store icon / AMO icon is read from the zip |
| `store-kit/water-bottles/images/sip-quick-promo-440x280.png` | 440x280 RGB | 440x280 | OK | GENERATED: Edge small promo tile (optional) / CWS small promo tile |

> Not used: `water-bottles/store-images/extension/screenshot-*`, `promo-440x280.png` and `marquee-1400x560.png`. They show the **Android app** (phone mockups branded SipLog), not this browser extension, which risks a "misleading listing" rejection. The two generated screenshots are real headless-Chrome renders of `popup.html` (with demo data and the new footer link), padded onto a 1280×800 canvas.

## Firefox AMO

| Field | Value |
|---|---|
| Upload zip | `water-bottles/firefox-extension/firefox-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.0) |
| Add-on id | `sip-quick@gopromotional.co.uk` (changed from `sip-quick@local`), strict_min_version 140.0 (desktop), gecko_android strict_min_version 142.0 |
| Name | Sip Quick |
| Suggested slug | `sip-quick` — the old `@local` submission may already hold this slug; fall back to `sip-quick-tracker` or delete the stale draft first |
| Categories | Other |
| Homepage | https://www.gopromotional.co.uk/branded-products/branded-water-bottles |
| Support website | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy | **leave blank** (decision: no privacy policy on AMO) |
| License | All Rights Reserved (or MIT if the repo gets a LICENSE) |
| Source code submission | No (code is not minified or transpiled) |
| Screenshots | `store-kit/water-bottles/images/sip-quick-screenshot-01-1280x800.png`, `store-kit/water-bottles/images/sip-quick-screenshot-02-1280x800.png` |

**Notes to reviewer (AMO)**

```
Sip Quick is a self-contained toolbar popup (popup.html / popup.js / popup.css, no build step, no minification, no third-party libraries), so no source code upload is needed.
It requests only the "storage" permission and stores data with storage.local under the key "sip_quick_v1". It makes no network requests and declares data_collection_permissions: {"required": ["none"]}.
The only external URL is a plain footer link (target=_blank) to https://www.gopromotional.co.uk/branded-products/branded-water-bottles.
To test: click the toolbar icon and follow these steps:
1. Click "+250". Expected: the total shows 250, the chip shows 13%, the progress bar fills slightly and the label reads "Goal 2000 ml · 1750 ml to go". The "Today" row under "Last 7 days" shows 250 ml.
2. Click "+500", then type 120 in "Custom ml" and click "Add". Expected: total 870 ml (44%).
3. Click "Undo". Expected: the last entry (120 ml) is removed and the total returns to 750 ml.
4. Type 1500 in "Daily goal ml" and click "Set goal". Expected: "Goal saved" appears, the label reads "Goal 1500 ml · 750 ml to go" and the chip shows 50%.
5. Close the popup and open it again. Expected: the total (750 ml) and goal (1500 ml) are still there, saved via the storage permission.
6. Click "Copy stats" and paste into any text field. Expected: "Hydration goal: 1500 ml" followed by one line per day for the last 7 days; a green "Stats copied" message appears.
7. Click "Clear today". Expected: today's total returns to 0 ml; the goal is kept.
8. The footer link "More: gopromotional.co.uk" opens the GoPromotional water bottles page in a new tab. It is a plain link; no data is sent.
```

## Microsoft Edge Add-ons

| Field | Value |
|---|---|
| Upload zip | `water-bottles/edge-extension/edge-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.0) |
| Display name | Sip Quick (from manifest) |
| Category | Productivity |
| Website URL | https://www.gopromotional.co.uk/branded-products/branded-water-bottles |
| Support contact | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy URL | https://www.gopromotional.co.uk/privacy (GoPromotional's own site privacy policy, linked from the site footer; returns HTTP 200) |
| Store logo (300×300) | `water-bottles/edge-extension/store-logo-300.png` |
| Small promo tile (440×280, optional) | `store-kit/water-bottles/images/sip-quick-promo-440x280.png` |
| Screenshots | `store-kit/water-bottles/images/sip-quick-screenshot-01-1280x800.png`, `store-kit/water-bottles/images/sip-quick-screenshot-02-1280x800.png` |
| Mature content | No |

Edge's description field needs at least 250 characters; the full description above is 1076 characters.

**NOTES_FOR_CERTIFICATION (paste into 'Notes for certification')**

```
1. Install the package and click the Sip Quick (water bottle) icon in the toolbar; pin it from the Extensions menu if it is hidden. The popup opens showing "0 ml today", "0%" and "Goal 2000 ml · 2000 ml to go".
2. Click "+250". Expected: the total shows 250, the chip shows 13%, the progress bar fills slightly and the label reads "Goal 2000 ml · 1750 ml to go". The "Today" row under "Last 7 days" shows 250 ml.
3. Click "+500", then type 120 in "Custom ml" and click "Add". Expected: total 870 ml (44%).
4. Click "Undo". Expected: the last entry (120 ml) is removed and the total returns to 750 ml.
5. Type 1500 in "Daily goal ml" and click "Set goal". Expected: "Goal saved" appears, the label reads "Goal 1500 ml · 750 ml to go" and the chip shows 50%.
6. Close the popup and open it again. Expected: the total (750 ml) and goal (1500 ml) are still there, saved via the storage permission.
7. Click "Copy stats" and paste into any text field. Expected: "Hydration goal: 1500 ml" followed by one line per day for the last 7 days; a green "Stats copied" message appears.
8. Click "Clear today". Expected: today's total returns to 0 ml; the goal is kept.
9. The footer link "More: gopromotional.co.uk" opens the GoPromotional water bottles page in a new tab. It is a plain link; no data is sent.

Permissions: "storage" only, used for storage.local to keep the data above on the device. No host permissions, no content scripts, no background script, no remote code, no network requests. No account or login is needed.
Privacy policy: https://www.gopromotional.co.uk/privacy
```

## Chrome Web Store extension (optional)

Built from the Edge folder (same MV3 code, no gecko block): `store-kit/water-bottles/dist/sip-quick-chrome-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.0).
Use the shared copy above with category **Lifestyle → Well-being**, the 128 icon, both screenshots and the 440×280 tile. Privacy policy URL: **leave blank**. Privacy tab: single purpose and storage justification as above, remote code **No**, no data types ticked, all three certifications ticked.
