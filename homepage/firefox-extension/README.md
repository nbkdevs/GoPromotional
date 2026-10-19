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
The live AMO listing (https://addons.mozilla.org/firefox/addon/event-snapshot/, v1.0.0) was signed with the id
`eventsnapshot@softrank.local`. The repo previously said `event-snapshot@local`, which AMO would treat as a different
add-on. The manifest now uses `eventsnapshot@softrank.local` again so v1.1.0 can be uploaded as an **update**.
Do not change this id.
