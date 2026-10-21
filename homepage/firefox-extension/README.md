# Event Snapshot (Firefox)

Plan an upcoming event from your toolbar: countdown, prioritized checklist, notes, and a copyable summary.

## Install

### Firefox
1. Open `about:debugging#/runtime/this-firefox`
2. **Load Temporary Add-on…**
3. Select `manifest.json`

### Edge
1. Open `edge://extensions`
2. Enable Developer mode
3. **Load unpacked** → this folder

## Privacy
Uses `storage` only for local data on your device. No accounts. No tracking. No remote calls.

## Add-on id (important)
The previous AMO listing was signed with `eventsnapshot@softrank.local`, which belongs to another AMO account.
The signed-in account currently has 0 add-ons, so this package uses `eventsnapshot@gopromotional.co.uk` and will be submitted as a **new** add-on at v1.1.0.

## Build zip
`zip -r -X firefox-extension.zip manifest.json popup.html popup.js popup.css icons`
