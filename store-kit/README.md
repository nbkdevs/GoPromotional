# Store submission kit (local only, nothing submitted)

Prepared 19 Oct 2026 (PKT). Branch `store-kit`, not pushed. Each file is ready to paste into the store forms.

| Page | Target URL (website/homepage field) | Item | Stores | Kit file |
|---|---|---|---|---|
| homepage | https://www.gopromotional.co.uk/ | Harbor Dusk (theme) | Chrome Web Store · *optional* Firefox AMO theme | [homepage/harbor-dusk.md](homepage/harbor-dusk.md) |
| water-bottles | https://www.gopromotional.co.uk/branded-products/branded-water-bottles | Alpine Mist (theme) | CWS · *optional* AMO theme | [water-bottles/alpine-mist.md](water-bottles/alpine-mist.md) |
| water-bottles | 〃 | Sip Quick (extension) | AMO · Edge · *optional* CWS | [water-bottles/sip-quick.md](water-bottles/sip-quick.md) |
| tote-bags | https://www.gopromotional.co.uk/branded-products/tote-bags | Canvas Trail (theme) | CWS · *optional* AMO theme | [tote-bags/canvas-trail.md](tote-bags/canvas-trail.md) |
| tote-bags | 〃 | Pack Quick (extension) | AMO · Edge · *optional* CWS | [tote-bags/pack-quick.md](tote-bags/pack-quick.md) |
| travel-mugs | https://www.gopromotional.co.uk/branded-products/travel-mugs | Ember Steam (theme) | CWS · *optional* AMO theme | [travel-mugs/ember-steam.md](travel-mugs/ember-steam.md) |
| travel-mugs | 〃 | Mug Tap (extension) | AMO · Edge · *optional* CWS | [travel-mugs/mug-tap.md](travel-mugs/mug-tap.md) |

Support URL everywhere: https://github.com/nbkdevs/GoPromotional/issues (issues are enabled on the repo).

## Privacy policy URLs (intended, not live yet)
Source: `docs/index.html` and `docs/privacy/<slug>.html`. These go live only after `docs/` is merged to `main`, pushed,
and GitHub Pages is enabled (Settings → Pages → Deploy from branch `main`, folder `/docs`; the repo currently has Pages off).
- https://nbkdevs.github.io/GoPromotional/ (index)
- https://nbkdevs.github.io/GoPromotional/privacy/sip-quick.html · pack-quick.html · mug-tap.html
- https://nbkdevs.github.io/GoPromotional/privacy/harbor-dusk.html · alpine-mist.html · canvas-trail.html · ember-steam.html

## What changed in the code
- Firefox ids: `sip-quick@local` → `sip-quick@gopromotional.co.uk`, `pack-quick@local` → `pack-quick@gopromotional.co.uk`,
  `mug-tap@local` → `mug-tap@gopromotional.co.uk`; `strict_min_version` 109.0 on all three; `data_collection_permissions` kept (`required: ["none"]`).
- Event Snapshot (homepage, live on AMO): id set back to the live `eventsnapshot@softrank.local` so v1.1.0 can ship as an update. No other change to it.
- Popup footer `More: gopromotional.co.uk` (target=_blank, rel=noopener noreferrer) in the Sip Quick, Pack Quick and Mug Tap popups, Firefox and Edge. Permissions unchanged (`storage` only).
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
- Regenerate with `store-kit/_tools/*.py` (render_popups → make_kit_images; make_firefox_themes; make_privacy; make_kit).

## Open issues to decide before submitting
1. **AMO warnings (not errors):** addons-linter gives 0 errors and 2 warnings per Firefox zip: `data_collection_permissions` needs Firefox 140+ (Android 142+), but `strict_min_version` is 109.0. Sip Quick and Mug Tap had 142.0 before, probably for this reason. To clear the warnings, set `strict_min_version` to `140.0` and add `"gecko_android": {"strict_min_version": "142.0"}`.
2. **Sip Quick and Mug Tap already exist on AMO (not public; they return 401).** The new ids create *new* add-ons. Their old drafts may already hold the `sip-quick` / `mug-tap` slugs. Delete or disable the old drafts, or use a different slug.
3. Privacy URLs return 404 until Pages is set up (see above). Edge and CWS reviewers open them.
4. CWS theme packages have no `icons` key in the manifest. The store icon is uploaded in the listing form instead.
5. The loose `homepage/chrome 1280.png` / `chrome-440.png` files show the old illustrated theme. Don't use them.
