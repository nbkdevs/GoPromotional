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


## Phase 2 (2026-10-20): GitHub, Socket.dev, agent skills; PHP, .NET and Dart ports

Repo pushed to https://github.com/nbkdevs/GoPromotional (main and registry-packages in sync), description, homepage
(https://www.gopromotional.co.uk) and topics set; the `tree/main/packages/...` source links from all 24
published packages now resolve (HTTP 200).

| Page | Platform | Name | Source | Status |
|---|---|---|---|---|
| homepage | GitHub | `nbkdevs/GoPromotional` | repo root / `homepage/` | Live: https://github.com/nbkdevs/GoPromotional (public; README Links use the exact anchor; verified 2026-10-20) |
| homepage | Socket.dev | `gopromotional-event-planner` (npm) | auto-indexed from npm | Live: https://socket.dev/npm/package/gopromotional-event-planner (README shown with the exact anchor; verified 2026-10-20) |
| homepage | GitHub (agent skill) | `skills/gopromotional-event-planner/SKILL.md` | `skills/gopromotional-event-planner/` | Live in repo: https://github.com/nbkdevs/GoPromotional/tree/main/skills/gopromotional-event-planner (installable with `npx skills add https://github.com/nbkdevs/GoPromotional --skill gopromotional-event-planner`) |
| water-bottles | GitHub | `nbkdevs/GoPromotional` | repo root / `water-bottles/` | Live: https://github.com/nbkdevs/GoPromotional/tree/main/water-bottles (public; README Links use the exact anchor; verified 2026-10-20) |
| water-bottles | Socket.dev | `gopromotional-siplog` (npm) | auto-indexed from npm | Live: https://socket.dev/npm/package/gopromotional-siplog (README shown with the exact anchor; verified 2026-10-20) |
| water-bottles | GitHub (agent skill) | `skills/gopromotional-siplog/SKILL.md` | `skills/gopromotional-siplog/` | Live in repo: https://github.com/nbkdevs/GoPromotional/tree/main/skills/gopromotional-siplog (installable with `npx skills add https://github.com/nbkdevs/GoPromotional --skill gopromotional-siplog`) |
| tote-bags | GitHub | `nbkdevs/GoPromotional` | repo root / `tote-bags/` | Live: https://github.com/nbkdevs/GoPromotional/tree/main/tote-bags (public; README Links use the exact anchor; verified 2026-10-20) |
| tote-bags | Socket.dev | `gopromotional-packlist` (npm) | auto-indexed from npm | Live: https://socket.dev/npm/package/gopromotional-packlist (README shown with the exact anchor; verified 2026-10-20) |
| tote-bags | GitHub (agent skill) | `skills/gopromotional-packlist/SKILL.md` | `skills/gopromotional-packlist/` | Live in repo: https://github.com/nbkdevs/GoPromotional/tree/main/skills/gopromotional-packlist (installable with `npx skills add https://github.com/nbkdevs/GoPromotional --skill gopromotional-packlist`) |
| travel-mugs | GitHub | `nbkdevs/GoPromotional` | repo root / `travel-mugs/` | Live: https://github.com/nbkdevs/GoPromotional/tree/main/travel-mugs (public; README Links use the exact anchor; verified 2026-10-20) |
| travel-mugs | Socket.dev | `gopromotional-mugday` (npm) | auto-indexed from npm | Live: https://socket.dev/npm/package/gopromotional-mugday (README shown with the exact anchor; verified 2026-10-20) |
| travel-mugs | GitHub (agent skill) | `skills/gopromotional-mugday/SKILL.md` | `skills/gopromotional-mugday/` | Live in repo: https://github.com/nbkdevs/GoPromotional/tree/main/skills/gopromotional-mugday (installable with `npx skills add https://github.com/nbkdevs/GoPromotional --skill gopromotional-mugday`) |

Built and tested, waiting on an account or token:

| Page | Platform | Name | Source | Status |
|---|---|---|---|---|
| homepage | Packagist | `gopromotional/event-planner` | split repo https://github.com/nbkdevs/gopromotional-event-planner-php (from `packages/homepage/php/`) | Live: https://packagist.org/packages/gopromotional/event-planner (v1.0.1, homepage = page URL, README exact anchor verified 2026-10-20) |
| homepage | NuGet | `GoPromotional.EventPlanner` | `packages/homepage/nuget/` | Built, 1.0.1, xunit green, nupkg packed (netstandard2.0 + net8.0, README + MIT). Needs NUGET_API_KEY |
| homepage | pub.dev | `gopromotional_event_planner` | `packages/homepage/dart/` | Built, 1.0.1, `dart analyze` clean, tests green, `dart pub publish --dry-run` 0 warnings. Needs Google sign-in on pub.dev |
| homepage | Smithery | `nbkdevs/gopromotional-event-planner` | `skills/gopromotional-event-planner/` | Live: https://smithery.ai/skills/nbkdevs/gopromotional-event-planner (HTTP 200; exact anchor rendered as a plain <a> with no rel, i.e. dofollow; verified 2026-10-20) |
| homepage | ClawHub | `gopromotional-event-planner` | `skills/gopromotional-event-planner/` | SKILL.md in repo. Needs ClawHub clh_ token (GitHub OAuth) |
| water-bottles | Packagist | `gopromotional/siplog` | split repo https://github.com/nbkdevs/gopromotional-siplog-php (from `packages/water-bottles/php/`) | Live: https://packagist.org/packages/gopromotional/siplog (v1.0.1, homepage = page URL, README exact anchor verified 2026-10-20) |
| water-bottles | NuGet | `GoPromotional.SipLog` | `packages/water-bottles/nuget/` | Built, 1.0.1, xunit green, nupkg packed (netstandard2.0 + net8.0, README + MIT). Needs NUGET_API_KEY |
| water-bottles | pub.dev | `gopromotional_siplog` | `packages/water-bottles/dart/` | Built, 1.0.1, `dart analyze` clean, tests green, `dart pub publish --dry-run` 0 warnings. Needs Google sign-in on pub.dev |
| water-bottles | Smithery | `nbkdevs/gopromotional-siplog` | `skills/gopromotional-siplog/` | Live: https://smithery.ai/skills/nbkdevs/gopromotional-siplog (HTTP 200; exact anchor rendered as a plain <a> with no rel, i.e. dofollow; verified 2026-10-20) |
| water-bottles | ClawHub | `gopromotional-siplog` | `skills/gopromotional-siplog/` | SKILL.md in repo. Needs ClawHub clh_ token (GitHub OAuth) |
| tote-bags | Packagist | `gopromotional/packlist` | split repo https://github.com/nbkdevs/gopromotional-packlist-php (from `packages/tote-bags/php/`) | Live: https://packagist.org/packages/gopromotional/packlist (v1.0.1, homepage = page URL, README exact anchor verified 2026-10-20) |
| tote-bags | NuGet | `GoPromotional.PackList` | `packages/tote-bags/nuget/` | Built, 1.0.1, xunit green, nupkg packed (netstandard2.0 + net8.0, README + MIT). Needs NUGET_API_KEY |
| tote-bags | pub.dev | `gopromotional_packlist` | `packages/tote-bags/dart/` | Built, 1.0.1, `dart analyze` clean, tests green, `dart pub publish --dry-run` 0 warnings. Needs Google sign-in on pub.dev |
| tote-bags | Smithery | `nbkdevs/gopromotional-packlist` | `skills/gopromotional-packlist/` | Live: https://smithery.ai/skills/nbkdevs/gopromotional-packlist (HTTP 200; exact anchor rendered as a plain <a> with no rel, i.e. dofollow; verified 2026-10-20) |
| tote-bags | ClawHub | `gopromotional-packlist` | `skills/gopromotional-packlist/` | SKILL.md in repo. Needs ClawHub clh_ token (GitHub OAuth) |
| travel-mugs | Packagist | `gopromotional/mugday` | split repo https://github.com/nbkdevs/gopromotional-mugday-php (from `packages/travel-mugs/php/`) | Live: https://packagist.org/packages/gopromotional/mugday (v1.0.1, homepage = page URL, README exact anchor verified 2026-10-20) |
| travel-mugs | NuGet | `GoPromotional.MugDay` | `packages/travel-mugs/nuget/` | Built, 1.0.1, xunit green, nupkg packed (netstandard2.0 + net8.0, README + MIT). Needs NUGET_API_KEY |
| travel-mugs | pub.dev | `gopromotional_mugday` | `packages/travel-mugs/dart/` | Built, 1.0.1, `dart analyze` clean, tests green, `dart pub publish --dry-run` 0 warnings. Needs Google sign-in on pub.dev |
| travel-mugs | Smithery | `nbkdevs/gopromotional-mugday` | `skills/gopromotional-mugday/` | Live: https://smithery.ai/skills/nbkdevs/gopromotional-mugday (HTTP 200; exact anchor rendered as a plain <a> with no rel, i.e. dofollow; verified 2026-10-20) |
| travel-mugs | ClawHub | `gopromotional-mugday` | `skills/gopromotional-mugday/` | SKILL.md in repo. Needs ClawHub clh_ token (GitHub OAuth) |
