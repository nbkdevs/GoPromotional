# GoPromotional registry packages

Four small, dependency-free tools, one per GoPromotional page, each published to six registries
(npm, PyPI, crates.io, RubyGems, Docker Hub, Hugging Face Spaces): 24 assets in total.
Every language port has the same behaviour, the same CLI commands and output, and the same JSON file
formats, verified against shared test fixtures and a cross-language CLI parity check.

| Page folder | Package | What it does | Page link |
|---|---|---|---|
| `homepage` | `gopromotional-event-planner` | Event countdown, planning timeline, giveaway quantities and print order-by dates. | [branded merchandise](https://www.gopromotional.co.uk/) |
| `water-bottles` | `gopromotional-siplog` | Daily water goal estimate and a simple hydration log with streaks. | [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles) |
| `tote-bags` | `gopromotional-packlist` | Packing checklists with templates, per-night quantities and a Markdown format. | [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags) |
| `travel-mugs` | `gopromotional-mugday` | Caffeine tracker with half-life estimate, sleep-ready time and reusable-cup stats. | [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs) |

Layout: `packages/<page>/<registry>/` with `npm/`, `pypi/`, `crates/`, `rubygems/`, `docker/` and
`huggingface-space/`. The Docker image and the HF Space reuse the npm code: the image copies `npm/`
(build context `packages/<page>`, see `.dockerignore`) and the Space ships a copy of `npm/lib/index.js`
as `lib.js`.

Common metadata: version 1.0.1, MIT licence, author "GoPromotional", repository
https://github.com/nbkdevs/GoPromotional. Every homepage/url/project_urls field points to the page URL.

## Link map

Every in-text link to a target page uses that page's exact anchor text, everywhere: the **Links**
section of each README, the HF Space README text and page footer, and the Docker Hub overview. No
rotation, brand, partial-keyword or bare-URL anchors. Only URL-only fields keep the plain URL
(`homepage`, `project_urls`, `homepage_uri`, the OCI `org.opencontainers.image.url` label).

| Page | Registry | Name | Link target | Anchor (exact) |
|---|---|---|---|---|
| homepage | npm | `gopromotional-event-planner` | `https://www.gopromotional.co.uk/` | [branded merchandise](https://www.gopromotional.co.uk/) |
| homepage | PyPI | `gopromotional-event-planner` | `https://www.gopromotional.co.uk/` | [branded merchandise](https://www.gopromotional.co.uk/) |
| homepage | crates.io | `gopromotional-event-planner` | `https://www.gopromotional.co.uk/` | [branded merchandise](https://www.gopromotional.co.uk/) |
| homepage | RubyGems | `gopromotional-event-planner` | `https://www.gopromotional.co.uk/` | [branded merchandise](https://www.gopromotional.co.uk/) |
| homepage | Docker Hub | `iamnazzal/gopromotional-event-planner` | `https://www.gopromotional.co.uk/` | [branded merchandise](https://www.gopromotional.co.uk/) |
| homepage | Hugging Face Space | `iamnazzal/gopromotional-event-planner` | `https://www.gopromotional.co.uk/` | [branded merchandise](https://www.gopromotional.co.uk/) |
| water-bottles | npm | `gopromotional-siplog` | `https://www.gopromotional.co.uk/branded-products/branded-water-bottles` | [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles) |
| water-bottles | PyPI | `gopromotional-siplog` | `https://www.gopromotional.co.uk/branded-products/branded-water-bottles` | [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles) |
| water-bottles | crates.io | `gopromotional-siplog` | `https://www.gopromotional.co.uk/branded-products/branded-water-bottles` | [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles) |
| water-bottles | RubyGems | `gopromotional-siplog` | `https://www.gopromotional.co.uk/branded-products/branded-water-bottles` | [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles) |
| water-bottles | Docker Hub | `iamnazzal/gopromotional-siplog` | `https://www.gopromotional.co.uk/branded-products/branded-water-bottles` | [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles) |
| water-bottles | Hugging Face Space | `iamnazzal/gopromotional-siplog` | `https://www.gopromotional.co.uk/branded-products/branded-water-bottles` | [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles) |
| tote-bags | npm | `gopromotional-packlist` | `https://www.gopromotional.co.uk/branded-products/tote-bags` | [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags) |
| tote-bags | PyPI | `gopromotional-packlist` | `https://www.gopromotional.co.uk/branded-products/tote-bags` | [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags) |
| tote-bags | crates.io | `gopromotional-packlist` | `https://www.gopromotional.co.uk/branded-products/tote-bags` | [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags) |
| tote-bags | RubyGems | `gopromotional-packlist` | `https://www.gopromotional.co.uk/branded-products/tote-bags` | [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags) |
| tote-bags | Docker Hub | `iamnazzal/gopromotional-packlist` | `https://www.gopromotional.co.uk/branded-products/tote-bags` | [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags) |
| tote-bags | Hugging Face Space | `iamnazzal/gopromotional-packlist` | `https://www.gopromotional.co.uk/branded-products/tote-bags` | [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags) |
| travel-mugs | npm | `gopromotional-mugday` | `https://www.gopromotional.co.uk/branded-products/travel-mugs` | [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs) |
| travel-mugs | PyPI | `gopromotional-mugday` | `https://www.gopromotional.co.uk/branded-products/travel-mugs` | [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs) |
| travel-mugs | crates.io | `gopromotional-mugday` | `https://www.gopromotional.co.uk/branded-products/travel-mugs` | [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs) |
| travel-mugs | RubyGems | `gopromotional-mugday` | `https://www.gopromotional.co.uk/branded-products/travel-mugs` | [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs) |
| travel-mugs | Docker Hub | `iamnazzal/gopromotional-mugday` | `https://www.gopromotional.co.uk/branded-products/travel-mugs` | [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs) |
| travel-mugs | Hugging Face Space | `iamnazzal/gopromotional-mugday` | `https://www.gopromotional.co.uk/branded-products/travel-mugs` | [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs) |

## Test and dry-run

```sh
# unit tests
(cd packages/<page>/npm && npm test)                         # node --test
(cd packages/<page>/pypi && python -m pytest)                # pytest
(cd packages/<page>/crates && cargo test)                    # unit + CLI + doc tests
(cd packages/<page>/rubygems && rake test)                   # minitest (or: ruby -Ilib test/test_*.rb)
# packaging dry runs
(cd packages/<page>/npm && npm pack --dry-run)
(cd packages/<page>/pypi && python -m build && twine check dist/*)
(cd packages/<page>/crates && cargo package)
(cd packages/<page>/rubygems && gem build *.gemspec)
(cd packages/<page> && docker build -f docker/Dockerfile -t iamnazzal/<package>:1.0.1 .)
python3 packages/tools/hf_smoke.py                           # headless Chrome test of the 4 Spaces
packages/tools/parity.sh                                     # same CLI output from all 4 languages
```

## Publish commands

All 24 are published (see `STATUS.md`). Credentials needed for a release:

| Registry | Credential | How it is used |
|---|---|---|
| npm | npm access token (Automation or Granular with publish rights), on an npm account that will own the 4 packages | `npm login` or `NPM_TOKEN` in `~/.npmrc` |
| PyPI | PyPI API token (account-wide for the first upload, since the projects don't exist yet) | `TWINE_USERNAME=__token__ TWINE_PASSWORD=pypi-...` |
| crates.io | crates.io API token with `publish-new` scope (account needs a verified email) | `cargo login` or `CARGO_REGISTRY_TOKEN` |
| RubyGems | RubyGems API key with "Push rubygem" scope; the gemspecs set `rubygems_mfa_required`, so MFA must be enabled on the account | `GEM_HOST_API_KEY` or `gem signin` |
| Docker Hub | Docker Hub personal access token (Read & Write) for **iamnazzal** | `docker login -u iamnazzal` |
| Hugging Face | HF access token with **write** permission for **iamnazzal** | `hf auth login` or `HF_TOKEN` |

### homepage: `gopromotional-event-planner`

```sh
cd packages/homepage
(cd npm && npm test && npm publish --access public)
(cd pypi && rm -rf dist && python -m build && twine check dist/* && twine upload dist/*)
(cd crates && cargo test && cargo publish)
(cd rubygems && gem build gopromotional-event-planner.gemspec && gem push gopromotional-event-planner-1.0.1.gem)
docker build -f docker/Dockerfile -t iamnazzal/gopromotional-event-planner:1.0.1 -t iamnazzal/gopromotional-event-planner:latest . \
  && docker push iamnazzal/gopromotional-event-planner:1.0.1 && docker push iamnazzal/gopromotional-event-planner:latest
hf repos create iamnazzal/gopromotional-event-planner --repo-type space --sdk static
hf upload iamnazzal/gopromotional-event-planner huggingface-space . --repo-type space
```

### water-bottles: `gopromotional-siplog`

```sh
cd packages/water-bottles
(cd npm && npm test && npm publish --access public)
(cd pypi && rm -rf dist && python -m build && twine check dist/* && twine upload dist/*)
(cd crates && cargo test && cargo publish)
(cd rubygems && gem build gopromotional-siplog.gemspec && gem push gopromotional-siplog-1.0.1.gem)
docker build -f docker/Dockerfile -t iamnazzal/gopromotional-siplog:1.0.1 -t iamnazzal/gopromotional-siplog:latest . \
  && docker push iamnazzal/gopromotional-siplog:1.0.1 && docker push iamnazzal/gopromotional-siplog:latest
hf repos create iamnazzal/gopromotional-siplog --repo-type space --sdk static
hf upload iamnazzal/gopromotional-siplog huggingface-space . --repo-type space
```

### tote-bags: `gopromotional-packlist`

```sh
cd packages/tote-bags
(cd npm && npm test && npm publish --access public)
(cd pypi && rm -rf dist && python -m build && twine check dist/* && twine upload dist/*)
(cd crates && cargo test && cargo publish)
(cd rubygems && gem build gopromotional-packlist.gemspec && gem push gopromotional-packlist-1.0.1.gem)
docker build -f docker/Dockerfile -t iamnazzal/gopromotional-packlist:1.0.1 -t iamnazzal/gopromotional-packlist:latest . \
  && docker push iamnazzal/gopromotional-packlist:1.0.1 && docker push iamnazzal/gopromotional-packlist:latest
hf repos create iamnazzal/gopromotional-packlist --repo-type space --sdk static
hf upload iamnazzal/gopromotional-packlist huggingface-space . --repo-type space
```

### travel-mugs: `gopromotional-mugday`

```sh
cd packages/travel-mugs
(cd npm && npm test && npm publish --access public)
(cd pypi && rm -rf dist && python -m build && twine check dist/* && twine upload dist/*)
(cd crates && cargo test && cargo publish)
(cd rubygems && gem build gopromotional-mugday.gemspec && gem push gopromotional-mugday-1.0.1.gem)
docker build -f docker/Dockerfile -t iamnazzal/gopromotional-mugday:1.0.1 -t iamnazzal/gopromotional-mugday:latest . \
  && docker push iamnazzal/gopromotional-mugday:1.0.1 && docker push iamnazzal/gopromotional-mugday:latest
hf repos create iamnazzal/gopromotional-mugday --repo-type space --sdk static
hf upload iamnazzal/gopromotional-mugday huggingface-space . --repo-type space
```

After pushing the repo, also check the `source_code_uri`/`documentation` links that point at
`tree/main/packages/...`: they only resolve once this branch is merged into `main` on GitHub.
