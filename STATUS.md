# Registry packages: status

**1.0.1 (2026-10-19): exact anchors.** Every in-text link to a target page now uses that page's exact
anchor text (branded merchandise / branded water bottles for event marketing / branded tote bags for
event marketing / branded travel mugs for event marketing). No rotation, brand, partial or bare-URL
anchors. URL-only fields (homepage, project_urls, homepage_uri, OCI url label) keep the plain URL.

Verified on 2026-10-19 after republishing:
- npm (`iamnazzal.com`), PyPI, crates.io, RubyGems: latest version 1.0.1, homepage = page URL, and the README
  the registry serves (npm `readme`, PyPI `description` rendered with readme_renderer as PyPI does,
  crates.io rendered `/1.0.1/readme`) has exactly one link to the page, with the exact anchor.
- Docker Hub: tags 1.0.1 and latest pushed (1.0.0 kept); overview PATCHed through the Hub API with the
  1.0.1 docker README (exact anchor, verified via GET /v2/repositories/iamnazzal/<repo>/).
- Hugging Face Spaces: 1.0.1 files uploaded, RUNNING; README (text + Links) and app footer use the exact anchor.
- RubyGems: 1.0.1 pushed by Nazzal (2FA) at 15:20 PKT; verified via /api/v1/gems/<name>.json and
  /api/v1/versions/<name>.json: version 1.0.1, homepage_uri = page URL, and the downloaded .gem matches
  the local build (same SHA-256), with the exact anchor in its README.

| # | Page | Registry | Name | Source | Status |
|---|---|---|---|---|---|
| 1 | homepage | npm | `gopromotional-event-planner` | `packages/homepage/npm/` | Live: https://www.npmjs.com/package/gopromotional-event-planner (1.0.1, exact anchor verified 2026-10-19) |
| 2 | homepage | PyPI | `gopromotional-event-planner` | `packages/homepage/pypi/` | Live: https://pypi.org/project/gopromotional-event-planner/ (1.0.1, exact anchor verified 2026-10-19) |
| 3 | homepage | crates.io | `gopromotional-event-planner` | `packages/homepage/crates/` | Live: https://crates.io/crates/gopromotional-event-planner (1.0.1, exact anchor verified 2026-10-19) |
| 4 | homepage | RubyGems | `gopromotional-event-planner` | `packages/homepage/rubygems/` | Live: https://rubygems.org/gems/gopromotional-event-planner (1.0.1, exact anchor verified 2026-10-19) |
| 5 | homepage | Docker Hub | `iamnazzal/gopromotional-event-planner` | `packages/homepage/docker/` | Live: https://hub.docker.com/r/iamnazzal/gopromotional-event-planner (tags 1.0.1 + latest, overview updated, exact anchor verified 2026-10-19) |
| 6 | homepage | Hugging Face Space | `iamnazzal/gopromotional-event-planner` | `packages/homepage/huggingface-space/` | Live: https://huggingface.co/spaces/iamnazzal/gopromotional-event-planner (static, RUNNING, 1.0.1 files, exact anchor verified 2026-10-19) |
| 7 | water-bottles | npm | `gopromotional-siplog` | `packages/water-bottles/npm/` | Live: https://www.npmjs.com/package/gopromotional-siplog (1.0.1, exact anchor verified 2026-10-19) |
| 8 | water-bottles | PyPI | `gopromotional-siplog` | `packages/water-bottles/pypi/` | Live: https://pypi.org/project/gopromotional-siplog/ (1.0.1, exact anchor verified 2026-10-19) |
| 9 | water-bottles | crates.io | `gopromotional-siplog` | `packages/water-bottles/crates/` | Live: https://crates.io/crates/gopromotional-siplog (1.0.1, exact anchor verified 2026-10-19) |
| 10 | water-bottles | RubyGems | `gopromotional-siplog` | `packages/water-bottles/rubygems/` | Live: https://rubygems.org/gems/gopromotional-siplog (1.0.1, exact anchor verified 2026-10-19) |
| 11 | water-bottles | Docker Hub | `iamnazzal/gopromotional-siplog` | `packages/water-bottles/docker/` | Live: https://hub.docker.com/r/iamnazzal/gopromotional-siplog (tags 1.0.1 + latest, overview updated, exact anchor verified 2026-10-19) |
| 12 | water-bottles | Hugging Face Space | `iamnazzal/gopromotional-siplog` | `packages/water-bottles/huggingface-space/` | Live: https://huggingface.co/spaces/iamnazzal/gopromotional-siplog (static, RUNNING, 1.0.1 files, exact anchor verified 2026-10-19) |
| 13 | tote-bags | npm | `gopromotional-packlist` | `packages/tote-bags/npm/` | Live: https://www.npmjs.com/package/gopromotional-packlist (1.0.1, exact anchor verified 2026-10-19) |
| 14 | tote-bags | PyPI | `gopromotional-packlist` | `packages/tote-bags/pypi/` | Live: https://pypi.org/project/gopromotional-packlist/ (1.0.1, exact anchor verified 2026-10-19) |
| 15 | tote-bags | crates.io | `gopromotional-packlist` | `packages/tote-bags/crates/` | Live: https://crates.io/crates/gopromotional-packlist (1.0.1, exact anchor verified 2026-10-19) |
| 16 | tote-bags | RubyGems | `gopromotional-packlist` | `packages/tote-bags/rubygems/` | Live: https://rubygems.org/gems/gopromotional-packlist (1.0.1, exact anchor verified 2026-10-19) |
| 17 | tote-bags | Docker Hub | `iamnazzal/gopromotional-packlist` | `packages/tote-bags/docker/` | Live: https://hub.docker.com/r/iamnazzal/gopromotional-packlist (tags 1.0.1 + latest, overview updated, exact anchor verified 2026-10-19) |
| 18 | tote-bags | Hugging Face Space | `iamnazzal/gopromotional-packlist` | `packages/tote-bags/huggingface-space/` | Live: https://huggingface.co/spaces/iamnazzal/gopromotional-packlist (static, RUNNING, 1.0.1 files, exact anchor verified 2026-10-19) |
| 19 | travel-mugs | npm | `gopromotional-mugday` | `packages/travel-mugs/npm/` | Live: https://www.npmjs.com/package/gopromotional-mugday (1.0.1, exact anchor verified 2026-10-19) |
| 20 | travel-mugs | PyPI | `gopromotional-mugday` | `packages/travel-mugs/pypi/` | Live: https://pypi.org/project/gopromotional-mugday/ (1.0.1, exact anchor verified 2026-10-19) |
| 21 | travel-mugs | crates.io | `gopromotional-mugday` | `packages/travel-mugs/crates/` | Live: https://crates.io/crates/gopromotional-mugday (1.0.1, exact anchor verified 2026-10-19) |
| 22 | travel-mugs | RubyGems | `gopromotional-mugday` | `packages/travel-mugs/rubygems/` | Live: https://rubygems.org/gems/gopromotional-mugday (1.0.1, exact anchor verified 2026-10-19) |
| 23 | travel-mugs | Docker Hub | `iamnazzal/gopromotional-mugday` | `packages/travel-mugs/docker/` | Live: https://hub.docker.com/r/iamnazzal/gopromotional-mugday (tags 1.0.1 + latest, overview updated, exact anchor verified 2026-10-19) |
| 24 | travel-mugs | Hugging Face Space | `iamnazzal/gopromotional-mugday` | `packages/travel-mugs/huggingface-space/` | Live: https://huggingface.co/spaces/iamnazzal/gopromotional-mugday (static, RUNNING, 1.0.1 files, exact anchor verified 2026-10-19) |
