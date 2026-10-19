# Store submission kit (local only, nothing submitted)

Prepared 19 Oct 2026 (PKT). Branch `store-kit`, not pushed. Each file is ready to paste into the store forms.

| Page | Target URL (website/homepage field) | Item | Stores | Kit file |
|---|---|---|---|---|
| homepage | `https://www.gopromotional.co.uk/` | Harbor Dusk (theme) | Chrome Web Store · *optional* Firefox AMO theme | [homepage/harbor-dusk.md](homepage/harbor-dusk.md) |
| water-bottles | `https://www.gopromotional.co.uk/branded-products/branded-water-bottles` | Alpine Mist (theme) | CWS · *optional* AMO theme | [water-bottles/alpine-mist.md](water-bottles/alpine-mist.md) |
| water-bottles | 〃 | Sip Quick (extension) | AMO · Edge · *optional* CWS | [water-bottles/sip-quick.md](water-bottles/sip-quick.md) |
| tote-bags | `https://www.gopromotional.co.uk/branded-products/tote-bags` | Canvas Trail (theme) | CWS · *optional* AMO theme | [tote-bags/canvas-trail.md](tote-bags/canvas-trail.md) |
| tote-bags | 〃 | Pack Quick (extension) | AMO · Edge · *optional* CWS | [tote-bags/pack-quick.md](tote-bags/pack-quick.md) |
| travel-mugs | `https://www.gopromotional.co.uk/branded-products/travel-mugs` | Ember Steam (theme) | CWS · *optional* AMO theme | [travel-mugs/ember-steam.md](travel-mugs/ember-steam.md) |
| travel-mugs | 〃 | Mug Tap (extension) | AMO · Edge · *optional* CWS | [travel-mugs/mug-tap.md](travel-mugs/mug-tap.md) |

Support URL everywhere: https://github.com/nbkdevs/GoPromotional/issues (issues are enabled on the repo).

## Privacy policy URLs (decision 19 Oct 2026)
- **Chrome Web Store and Firefox AMO:** leave the privacy policy field **blank**. None of the items collects data.
- **Microsoft Edge:** use GoPromotional's own policy, https://www.gopromotional.co.uk/privacy. It is the "Privacy Policy" link in the site footer and returned HTTP 200 on 19 Oct 2026 (`/privacy-policy` is a 404).
- No push and no GitHub Pages. `docs/` (draft per-item policies) stays in the branch for reference only and is not used by the kit.

## What changed in the code
- Firefox ids: `sip-quick@local` → `sip-quick@gopromotional.co.uk`, `pack-quick@local` → `pack-quick@gopromotional.co.uk`,
  `mug-tap@local` → `mug-tap@gopromotional.co.uk`; `data_collection_permissions` kept (`required: ["none"]`).
- All 4 Firefox extension manifests (incl. Event Snapshot): `gecko.strict_min_version` 140.0 + `gecko_android.strict_min_version` 142.0. Zips rebuilt; addons-linter: 0 errors, 0 warnings.
- Event Snapshot (homepage, live on AMO): id set back to the live `eventsnapshot@softrank.local` so v1.1.0 can ship as an update. No other change to it.
- Popup footer `More: <exact page anchor>` (e.g. "More: branded water bottles for event marketing"; target=_blank, rel=noopener noreferrer) in the Sip Quick, Pack Quick and Mug Tap popups, Firefox and Edge. Permissions unchanged (`storage` only).
- Rebuilt upload zips from inside each folder (`zip -r -X <name>.zip manifest.json popup.html popup.js popup.css icons`): 4 Firefox, 3 Edge.
- New optional Firefox static themes: `<page>/firefox-theme/` (manifest + 1920×200 header, zip).
- New optional Chrome extension zips (copied from the Edge folders): `store-kit/<page>/dist/<slug>-chrome-extension.zip`.

## Generated images (`store-kit/<page>/images/`)
- `<slug>-screenshot-01/02-1280x800.png`: headless-Chrome renders of the real popup with demo data, on a 1280×800 canvas. Made because the
  existing `store-images/extension/*` screenshots, promo tiles and marquees show the **Android apps** (SipLog/BagPack/MugDay), not the extensions.
- `<slug>-promo-440x280.png`: small promo tile from the extension icon and name.
- `<slug>-logo-300x300.png` / `-logo-128x128.png`: resized from `branding/extension/icon-512.png` with transparent corners. Needed for Pack Quick
  (no Edge `store-logo-300.png`; `store-images/extension/logo-300.png` has black corners). Optional for the other two.
- Theme images were all present at the right sizes; nothing generated for themes except the Firefox header.
- Regenerate with `store-kit/_tools/*.py` (render_popups → make_kit_images; make_firefox_themes; make_kit). `make_privacy.py` only rebuilds the unused `docs/` drafts.

## Open issues to decide before submitting
1. **Sip Quick and Mug Tap already exist on AMO (not public; they return 401).** The new ids create *new* add-ons. Their old drafts may already hold the `sip-quick` / `mug-tap` slugs. Delete or disable the old drafts, or use a different slug.
2. The Edge privacy URL is GoPromotional's general site policy. It doesn't mention the extensions, which is acceptable because they collect no data. If Edge asks, the kit's NOTES_FOR_CERTIFICATION states that no data is collected.
3. CWS theme packages have no `icons` key in the manifest. The store icon is uploaded in the listing form instead.
4. The loose `homepage/chrome 1280.png` / `chrome-440.png` files show the old illustrated theme. Don't use them.
5. The optional Firefox *themes* still use strict_min_version 109.0, so addons-linter reports 2 warnings (data_collection_permissions) on each. Apply the same 140.0/142.0 change if they are submitted.
