# Event Snapshot — Firefox AMO + Microsoft Edge Add-ons (+ Chrome Web Store)

Page group: `homepage/` → `https://www.gopromotional.co.uk/`  ·  preferred anchor: **branded merchandise**
Sources: `homepage/firefox-extension/` and `homepage/edge-extension/` (identical MV3 popup code; Firefox adds the gecko block). Version 1.1.0. Nothing has been submitted from the current AMO account (gecko id changed to `eventsnapshot@gopromotional.co.uk` because the signed-in account has 0 add-ons and the old `eventsnapshot@softrank.local` belongs to another account).

## Shared listing copy

**Summary — CWS / Edge short description** (112/132 chars)

```
Plan an event from your toolbar: countdown, prioritised checklist, notes and a copyable summary. Stored locally.
```

**Summary — AMO** (212/250 chars)

```
Plan an upcoming event from your toolbar. Set a date for the countdown, keep a prioritised checklist, add notes and copy a ready summary. Everything stays in local storage on your device: no account, no tracking.
```

**Full description (plain text; one GoPromotional mention, last paragraph)**

```
Event Snapshot is a small event planner that lives in your toolbar. Name the event, set the date and venue, keep a prioritised checklist and jot agenda notes. When you need to share the plan, copy a plain-text summary in one click. There is no account, no sign-up and nothing to sync.

Features
• Event name, date, time and venue fields that save as you go
• Countdown chip showing days to go (or today / days ago)
• Prioritised checklist with High / Normal priority and tick-off
• Starter checklist for common prep steps (venue, invitations, headcount, agenda, AV, arrival)
• Notes / agenda box for speakers and reminders
• Copy summary: a plain-text plan on your clipboard
• Clear done tasks, or Reset to wipe the whole plan

Privacy
Event Snapshot keeps the event fields, checklist and notes in your browser's local extension storage and nowhere else. It makes no network requests, has no analytics and uses a single permission: storage.

Handy for meetups, launches, offsites and stand set-up days.

If you're planning event giveaways, the popup footer also links to GoPromotional's branded merchandise: https://www.gopromotional.co.uk/
```

**Single-purpose statement**

> Event Snapshot lets the user plan an upcoming event (name, date, venue, prioritised checklist and notes) and copy a summary, all inside the toolbar popup.

**Permission justification — `storage`**

> Event Snapshot uses storage.local to save the event name, date, time, venue, notes and the checklist (title, priority and done state for each task) under the key "event_snapshot_v1". Without it, the plan would be lost every time the popup closes. The data never leaves the device; the extension makes no network requests.

**Data usage (all stores): no data collected**
- Personal or sensitive data collected/transmitted: **none**. Everything stays in `storage.local` on the user's device.
- Remote code: **No**.
- CWS data-usage checkboxes: tick none (no PII, health, financial, authentication, communications, location, web history, user activity, website content). Tick all three certifications (no selling/transfer, no unrelated use, no creditworthiness use).
- Edge "Does your extension access, collect or transmit personal information?": **No**. Privacy policy URL: https://www.gopromotional.co.uk/privacy (GoPromotional site policy).
- AMO: manifest declares `data_collection_permissions.required: ["none"]`; answer that the add-on collects no data.

| Field | Value |
|---|---|
| Website / homepage | https://www.gopromotional.co.uk/ |
| Support URL | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy URL | Chrome Web Store and AMO: **leave blank**. Edge: https://www.gopromotional.co.uk/privacy |

### Images (sizes verified with PIL)
| File | Actual | Required | Check | Use |
|---|---|---|---|---|
| `store-kit/homepage/images/event-snapshot-screenshot-01-1280x800.png` | 1280x800 RGB | 1280x800 | OK | GENERATED: real popup render (CWS/Edge/AMO screenshot 1) |
| `store-kit/homepage/images/event-snapshot-screenshot-02-1280x800.png` | 1280x800 RGB | 1280x800 | OK | GENERATED: real popup render (screenshot 2) |
| `store-kit/homepage/images/event-snapshot-logo-300x300.png` | 300x300 RGBA | 300x300 | OK | GENERATED Edge Store logo (from branding/extension/icon-512.png) |
| `homepage/branding/extension/icon-128.png` | 128x128 RGBA | 128x128 | OK | CWS store icon / AMO icon is read from the zip |
| `store-kit/homepage/images/event-snapshot-logo-128x128.png` | 128x128 RGBA | 128x128 | OK | GENERATED alt 128 icon |
| `store-kit/homepage/images/event-snapshot-promo-440x280.png` | 440x280 RGB | 440x280 | OK | GENERATED: Edge small promo tile (optional) / CWS small promo tile |

> Not used: phone-mockup images under `homepage/` that show the Android Event Planner Themes app. The two generated screenshots are real headless-Chrome renders of `popup.html` (with demo data and the footer link), padded onto a 1280×800 canvas.

## Firefox AMO

| Field | Value |
|---|---|
| Upload zip | `homepage/firefox-extension/firefox-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.0) |
| Add-on id | `eventsnapshot@gopromotional.co.uk` (changed from `eventsnapshot@softrank.local` — that id belongs to another AMO account; this account has 0 add-ons so this is a new listing), strict_min_version 140.0 (desktop), gecko_android strict_min_version 142.0 |
| Name | Event Snapshot |
| Suggested slug | `event-snapshot` |
| Categories | Other |
| Homepage | https://www.gopromotional.co.uk/ |
| Support website | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy | **leave blank** (decision: no privacy policy on AMO) |
| License | All Rights Reserved (or MIT if the repo gets a LICENSE) |
| Source code submission | No (code is not minified or transpiled) |
| Screenshots | `store-kit/homepage/images/event-snapshot-screenshot-01-1280x800.png`, `store-kit/homepage/images/event-snapshot-screenshot-02-1280x800.png` |

**Notes to reviewer (AMO)**

```
Event Snapshot is a self-contained toolbar popup (popup.html / popup.js / popup.css, no build step, no minification, no third-party libraries), so no source code upload is needed.
It requests only the "storage" permission and stores data with storage.local under the key "event_snapshot_v1". It makes no network requests and declares data_collection_permissions: {"required": ["none"]}.
The only external URL is a plain footer link (target=_blank) to https://www.gopromotional.co.uk/.
To test: click the toolbar icon and follow these steps:
1. Type "Product launch meetup" in Event name, pick a date about two weeks out, type "18:30" in Time and "Harbour Studio" in Venue. Expected: the countdown shows a positive day count and "days to go"; the done chip still reads "0/0 done".
2. Click "Starter". Expected: six tasks appear (Confirm venue, Send invitations, Lock headcount, Prepare agenda, Check AV / equipment, Plan guest arrival flow); high-priority ones are marked with ●.
3. Tick "Confirm venue" and "Send invitations". Expected: they move below open tasks, the chip updates to "2/6 done", and opacity drops on the done titles.
4. Set priority to High, type "Order catering" in "Add a task" and click "Add". Expected: the new high-priority task appears near the top of the open list.
5. Type a short agenda in Notes / agenda (for example "Welcome drinks 18:00"). Expected: the field keeps the text after the field loses focus (saved via storage).
6. Close the popup and open it again. Expected: name, date, time, venue, notes and the checklist (including done state) are still there.
7. Click "Copy summary" and paste into any text field. Expected: the event name, date/time, venue, open tasks as "- [ ] …", done tasks as "- [x] …", and notes; a green "Summary copied" toast appears.
8. Click "Clear done". Expected: the two ticked tasks are removed and the chip updates (for example "0/5 done").
9. Click "Reset" and confirm the dialog. Expected: all fields and tasks are cleared; countdown returns to "—" / "set a date".
10. The footer link "More: branded merchandise" opens https://www.gopromotional.co.uk/ in a new tab. It is a plain link; no data is sent.
```

## Microsoft Edge Add-ons

| Field | Value |
|---|---|
| Upload zip | `homepage/edge-extension/edge-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.0) |
| Display name | Event Snapshot (from manifest) |
| Category | Productivity |
| Website URL | https://www.gopromotional.co.uk/ |
| Support contact | https://github.com/nbkdevs/GoPromotional/issues |
| Privacy policy URL | https://www.gopromotional.co.uk/privacy (GoPromotional's own site privacy policy, linked from the site footer; returns HTTP 200) |
| Store logo (300×300) | `store-kit/homepage/images/event-snapshot-logo-300x300.png` |
| Small promo tile (440×280, optional) | `store-kit/homepage/images/event-snapshot-promo-440x280.png` |
| Screenshots | `store-kit/homepage/images/event-snapshot-screenshot-01-1280x800.png`, `store-kit/homepage/images/event-snapshot-screenshot-02-1280x800.png` |
| Mature content | No |

Edge's description field needs at least 250 characters; the full description above is 1140 characters.

**NOTES_FOR_CERTIFICATION (paste into 'Notes for certification')**

```
1. Install the package and click the Event Snapshot icon in the toolbar; pin it from the Extensions menu if it is hidden. The popup opens with empty fields, countdown "—" / "set a date" and "0/0 done".
2. Type "Product launch meetup" in Event name, pick a date about two weeks out, type "18:30" in Time and "Harbour Studio" in Venue. Expected: the countdown shows a positive day count and "days to go".
3. Click "Starter". Expected: six starter tasks appear; high-priority ones are marked with ●.
4. Tick "Confirm venue" and "Send invitations". Expected: they move below open tasks and the chip updates to "2/6 done".
5. Set priority to High, type "Order catering" and click "Add". Expected: the new high-priority task appears among the open tasks.
6. Type agenda notes and leave the field. Close the popup and reopen it. Expected: all fields, notes and checklist state are still there (storage.local).
7. Click "Copy summary" and paste into any text field. Expected: name, date/time, venue, open/done checklist lines and notes; a green "Summary copied" toast appears.
8. Click "Clear done". Expected: ticked tasks are removed. Click "Reset" and confirm. Expected: everything is cleared.
9. The footer link "More: branded merchandise" opens https://www.gopromotional.co.uk/ in a new tab. It is a plain link; no data is sent.

Permissions: "storage" only, used for storage.local to keep the data above on the device. No host permissions, no content scripts, no background script, no remote code, no network requests. No account or login is needed.
Privacy policy: https://www.gopromotional.co.uk/privacy
```

## Chrome Web Store extension

Built from the Edge folder (same MV3 code, no gecko block): `store-kit/homepage/dist/event-snapshot-chrome-extension.zip` (12 entries, `manifest.json` at zip root, version 1.1.0).
Use the shared copy above with category **Productivity → Workflow & Planning**, the 128 icon, both screenshots and the 440×280 tile. Privacy policy URL: **leave blank**. Privacy tab: single purpose and storage justification as above, remote code **No**, no data types ticked, all three certifications ticked.
