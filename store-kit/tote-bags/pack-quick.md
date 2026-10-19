# Pack Quick — Firefox AMO + Microsoft Edge Add-ons (+ optional Chrome Web Store)

Page group: `tote-bags/` → https://www.gopromotional.co.uk/branded-products/tote-bags  ·  preferred anchor: **branded tote bags for event marketing**
Sources: `tote-bags/firefox-extension/` and `tote-bags/edge-extension/` (identical MV3 popup code; Firefox adds the gecko block). Version 1.1.0. Nothing has been submitted.

## Shared listing copy

**Summary — CWS / Edge short description** (116/132 chars)

```
Packing lists in your toolbar: multiple lists, work and travel templates, tick-off, filter and copy. Stored locally.
```

**Summary — AMO** (229/250 chars)

```
Keep packing lists in your toolbar. Create several named lists, start from work-day or travel templates, tick items off, filter long lists and copy a checklist. Lists stay in local storage on your device: no account, no tracking.
```

**Full description (plain text; one GoPromotional mention, last paragraph)**

```
Pack Quick keeps packing lists in your toolbar, so you can tick things off as they go into the bag. Keep separate lists for work days, trips, gym kit or event set-up, and pick up where you left off every time you open it.

Features
• Multiple named lists with a quick switcher, plus rename and delete
• Work day and Travel starter templates (only adds items not already on the list)
• Tick items as packed and see an "x/y packed" counter
• Filter to find an item in a long list
• Clear packed to reset a list for next time
• Copy list: a plain-text checklist on your clipboard, ready to share

Privacy
Pack Quick keeps your lists in your browser's local extension storage and nowhere else. It makes no network requests, has no analytics and uses a single permission: storage.

It works well for exhibitors and event teams packing a stand, as well as for everyday bags.

Packing for a trade show? GoPromotional has a range of branded tote bags for event marketing: https://www.gopromotional.co.uk/branded-products/tote-bags
```

**Single-purpose statement**

> Pack Quick lets the user create and manage packing checklists (add, tick off, filter, copy) inside the toolbar popup.

**Permission justification — `storage`**

> Pack Quick uses storage.local to save the user's packing lists: list names, the items on each list, whether each item is ticked, and which list was last open. Without it, lists would disappear when the popup closes. The data never leaves the device; the extension makes no network requests.

**Data usage (all stores): no data collected**
- Personal or sensitive data collected/transmitted: **none**. Everything stays in `storage.local` on the user's device.
- Remote code: **No**.
- CWS data-usage checkboxes: tick none (no PII, health, financial, authentication, communications, location, web history, user activity, website content). Tick all three certifications (no selling/transfer, no unrelated use, no creditworthiness use).
- Edge "Does your extension access, collect or transmit personal information?": **No** (privacy policy URL still provided).
- AMO: manifest declares `data_collection_permissions.required: ["none"]`; answer that the add-on collects no data.

| Field | Value |
|---|---|
| Website / homepage | https://www.gopromotional.co.uk/branded-products/tote-bags |
| Support URL | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy URL | https://nbkdevs.github.io/GoPromotional/privacy/pack-quick.html (intended GitHub Pages URL; **live only after docs/ is pushed to main and Pages is enabled**) |

### Images (sizes verified with PIL)
| File | Actual | Required | Check | Use |
|---|---|---|---|---|
| `store-kit/tote-bags/images/pack-quick-screenshot-01-1280x800.png` | 1280x800 RGB | 1280x800 | OK | GENERATED: real popup render (CWS/Edge/AMO screenshot 1) |
| `store-kit/tote-bags/images/pack-quick-screenshot-02-1280x800.png` | 1280x800 RGB | 1280x800 | OK | GENERATED: real popup render (screenshot 2) |
| `store-kit/tote-bags/images/pack-quick-logo-300x300.png` | 300x300 RGBA | 300x300 | OK | Edge Store logo (GENERATED from branding/extension/icon-512.png; repo logo-300 has black corners) |
| `tote-bags/branding/extension/icon-128.png` | 128x128 RGBA | 128x128 | OK | CWS store icon / AMO icon is read from the zip |
| `store-kit/tote-bags/images/pack-quick-promo-440x280.png` | 440x280 RGB | 440x280 | OK | GENERATED: Edge small promo tile (optional) / CWS small promo tile |

> Not used: `tote-bags/store-images/extension/screenshot-*`, `promo-440x280.png` and `marquee-1400x560.png`. They show the **Android app** (phone mockups branded BagPack), not this browser extension, which risks a "misleading listing" rejection. The two generated screenshots are real headless-Chrome renders of `popup.html` (with demo data and the new footer link), padded onto a 1280×800 canvas.

## Firefox AMO

| Field | Value |
|---|---|
| Upload zip | `tote-bags/firefox-extension/firefox-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.0) |
| Add-on id | `pack-quick@gopromotional.co.uk` (changed from `pack-quick@local`), strict_min_version 109.0 |
| Name | Pack Quick |
| Suggested slug | `pack-quick` |
| Categories | Other |
| Homepage | https://www.gopromotional.co.uk/branded-products/tote-bags |
| Support website | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy | https://nbkdevs.github.io/GoPromotional/privacy/pack-quick.html |
| License | All Rights Reserved (or MIT if the repo gets a LICENSE) |
| Source code submission | No (code is not minified or transpiled) |
| Screenshots | `store-kit/tote-bags/images/pack-quick-screenshot-01-1280x800.png`, `store-kit/tote-bags/images/pack-quick-screenshot-02-1280x800.png` |

**Notes to reviewer (AMO)**

```
Pack Quick is a self-contained toolbar popup (popup.html / popup.js / popup.css, no build step, no minification, no third-party libraries), so no source code upload is needed.
It requests only the "storage" permission and stores data with storage.local under the key "pack_quick_v2". It makes no network requests and declares data_collection_permissions: {"required": ["none"]}.
The only external URL is a plain footer link (target=_blank) to https://www.gopromotional.co.uk/branded-products/tote-bags.
To test: click the toolbar icon and follow these steps:
1. Type "Umbrella" in "Add item" and click "Add". Expected: the item appears in the list and the counter shows "0/1 packed".
2. Click "Travel". Expected: travel starter items (Passport / ID, Tickets, Phone charger, …) are added; clicking it again does not add duplicates.
3. Tick two items. Expected: they are marked packed and move below unpacked items; the counter updates (for example "2/9 packed").
4. Type "ch" in "Filter items…". Expected: only matching items (e.g. "Phone charger") are shown. Clear the filter.
5. Close the popup and reopen it. Expected: the items and their ticked state are still there, saved via the storage permission.
6. Click "Copy list" and paste into any text field. Expected: the list name followed by "[x]"/"[ ]" lines; a green "List copied" message appears.
7. Click "Clear packed". Expected: the ticked items are removed (for example "0/7 packed").
8. Click "New", type "Trade show" in "Rename list" and click "Rename". Expected: a second list named "Trade show" is selected; you can switch lists with the dropdown.
9. With "Trade show" selected, click "Delete list" and confirm the dialog. Expected: that list is removed and "Today’s bag" is shown again. Clicking "Delete list" when only one list remains shows "Keep at least one list" instead.
10. The footer link "More: gopromotional.co.uk" opens the GoPromotional tote bags page in a new tab. It is a plain link; no data is sent.
```

## Microsoft Edge Add-ons

| Field | Value |
|---|---|
| Upload zip | `tote-bags/edge-extension/edge-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.0) |
| Display name | Pack Quick (from manifest) |
| Category | Productivity |
| Website URL | https://www.gopromotional.co.uk/branded-products/tote-bags |
| Support contact | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy required? | No personal data is collected; still enter https://nbkdevs.github.io/GoPromotional/privacy/pack-quick.html |
| Store logo (300×300) | `store-kit/tote-bags/images/pack-quick-logo-300x300.png` |
| Small promo tile (440×280, optional) | `store-kit/tote-bags/images/pack-quick-promo-440x280.png` |
| Screenshots | `store-kit/tote-bags/images/pack-quick-screenshot-01-1280x800.png`, `store-kit/tote-bags/images/pack-quick-screenshot-02-1280x800.png` |
| Mature content | No |

Edge's description field needs at least 250 characters; the full description above is 1022 characters.

**NOTES_FOR_CERTIFICATION (paste into 'Notes for certification')**

```
1. Install the package and click the Pack Quick (checklist bag) icon in the toolbar; pin it from the Extensions menu if it is hidden. The popup opens with a list called "Today’s bag", "0/0 packed" and "No items yet. Add something or use a template."
2. Type "Umbrella" in "Add item" and click "Add". Expected: the item appears in the list and the counter shows "0/1 packed".
3. Click "Travel". Expected: travel starter items (Passport / ID, Tickets, Phone charger, …) are added; clicking it again does not add duplicates.
4. Tick two items. Expected: they are marked packed and move below unpacked items; the counter updates (for example "2/9 packed").
5. Type "ch" in "Filter items…". Expected: only matching items (e.g. "Phone charger") are shown. Clear the filter.
6. Close the popup and reopen it. Expected: the items and their ticked state are still there, saved via the storage permission.
7. Click "Copy list" and paste into any text field. Expected: the list name followed by "[x]"/"[ ]" lines; a green "List copied" message appears.
8. Click "Clear packed". Expected: the ticked items are removed (for example "0/7 packed").
9. Click "New", type "Trade show" in "Rename list" and click "Rename". Expected: a second list named "Trade show" is selected; you can switch lists with the dropdown.
10. With "Trade show" selected, click "Delete list" and confirm the dialog. Expected: that list is removed and "Today’s bag" is shown again. Clicking "Delete list" when only one list remains shows "Keep at least one list" instead.
11. The footer link "More: gopromotional.co.uk" opens the GoPromotional tote bags page in a new tab. It is a plain link; no data is sent.

Permissions: "storage" only, used for storage.local to keep the data above on the device. No host permissions, no content scripts, no background script, no remote code, no network requests. No account or login is needed.
Privacy policy: https://nbkdevs.github.io/GoPromotional/privacy/pack-quick.html
```

## Chrome Web Store extension (optional)

Built from the Edge folder (same MV3 code, no gecko block): `store-kit/tote-bags/dist/pack-quick-chrome-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.0).
Use the shared copy above with category **Productivity → Workflow & Planning (or Lifestyle → Travel)**, the 128 icon, both screenshots and the 440×280 tile. Privacy tab: single purpose and storage justification as above, remote code **No**, no data types ticked, all three certifications ticked.
