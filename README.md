# GoPromotional — Asset Development

Useful digital assets organised by the website page they relate to. Each group has its own purpose; links to GoPromotional appear only where they are contextually helpful.

| Folder | Target page | Preferred link anchor (when linking) |
|--------|-------------|--------------------------------------|
| [`homepage/`](homepage/) | [branded merchandise](https://www.gopromotional.co.uk) | branded merchandise |
| [`water-bottles/`](water-bottles/) | [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles) | branded water bottles for event marketing |
| [`tote-bags/`](tote-bags/) | [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags) | branded tote bags for event marketing |
| [`travel-mugs/`](travel-mugs/) | [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs) | branded travel mugs for event marketing |

## Current status

Each folder includes an Android app, Firefox extension, Edge extension, and Chrome theme.

1. **Homepage** — Event Planner & Themes · Event Snapshot · Harbor Dusk
2. **Water bottles** — SipLog · Sip Quick · Alpine Mist
3. **Tote bags** — BagPack · Pack Quick · Canvas Trail
4. **Travel mugs** — MugDay · Mug Tap · Ember Steam

## Developer packages and agent skills

- [`packages/`](packages/) holds four small open-source tools (event planner, SipLog, PackList, MugDay) published to npm, PyPI, crates.io, RubyGems, Docker Hub and Hugging Face Spaces, with PHP, .NET and Dart library ports.
- [`skills/`](skills/) holds an Agent Skills `SKILL.md` for each tool so AI agents can run them.
- Release status: [`STATUS.md`](STATUS.md).

## Find the tools elsewhere

Each of the four tools in this repository is published on the main package registries, as an agent skill, and as an app and browser extension. Pick whichever suits your stack.

### Packages

One tool per row, with a link to each registry listing.

| Tool | npm | PyPI | crates.io | RubyGems | Packagist | NuGet | pub.dev | Docker Hub |
|---|---|---|---|---|---|---|---|---|
| **Event Planner** (event countdown, planning timeline and giveaway quantities) | [npm](https://www.npmjs.com/package/gopromotional-event-planner) | [PyPI](https://pypi.org/project/gopromotional-event-planner/) | [crates.io](https://crates.io/crates/gopromotional-event-planner) | [RubyGems](https://rubygems.org/gems/gopromotional-event-planner) | [Packagist](https://packagist.org/packages/gopromotional/event-planner) | [NuGet](https://www.nuget.org/packages/GoPromotional.EventPlanner) | [pub.dev](https://pub.dev/packages/gopromotional_event_planner) | [Docker Hub](https://hub.docker.com/r/iamnazzal/gopromotional-event-planner) |
| **SipLog** (daily water goal and hydration log) | [npm](https://www.npmjs.com/package/gopromotional-siplog) | [PyPI](https://pypi.org/project/gopromotional-siplog/) | [crates.io](https://crates.io/crates/gopromotional-siplog) | [RubyGems](https://rubygems.org/gems/gopromotional-siplog) | [Packagist](https://packagist.org/packages/gopromotional/siplog) | [NuGet](https://www.nuget.org/packages/GoPromotional.SipLog) | [pub.dev](https://pub.dev/packages/gopromotional_siplog) | [Docker Hub](https://hub.docker.com/r/iamnazzal/gopromotional-siplog) |
| **PackList** (packing checklists with templates) | [npm](https://www.npmjs.com/package/gopromotional-packlist) | [PyPI](https://pypi.org/project/gopromotional-packlist/) | [crates.io](https://crates.io/crates/gopromotional-packlist) | [RubyGems](https://rubygems.org/gems/gopromotional-packlist) | [Packagist](https://packagist.org/packages/gopromotional/packlist) | [NuGet](https://www.nuget.org/packages/GoPromotional.PackList) | [pub.dev](https://pub.dev/packages/gopromotional_packlist) | [Docker Hub](https://hub.docker.com/r/iamnazzal/gopromotional-packlist) |
| **MugDay** (caffeine tracker and reusable-cup stats) | [npm](https://www.npmjs.com/package/gopromotional-mugday) | [PyPI](https://pypi.org/project/gopromotional-mugday/) | [crates.io](https://crates.io/crates/gopromotional-mugday) | [RubyGems](https://rubygems.org/gems/gopromotional-mugday) | [Packagist](https://packagist.org/packages/gopromotional/mugday) | [NuGet](https://www.nuget.org/packages/GoPromotional.MugDay) | [pub.dev](https://pub.dev/packages/gopromotional_mugday) | [Docker Hub](https://hub.docker.com/r/iamnazzal/gopromotional-mugday) |

### Agent skills and demos

| Tool | Hugging Face Space | Smithery | ClawHub | Socket | npm.io |
|---|---|---|---|---|---|
| **Event Planner** | [Space](https://huggingface.co/spaces/iamnazzal/gopromotional-event-planner) | [Skill](https://smithery.ai/skills/nbkdevs/gopromotional-event-planner) | [Skill](https://clawhub.ai/nbkdevs/skills/gopromotional-event-planner) | [Report](https://socket.dev/npm/package/gopromotional-event-planner) | [Listing](https://npm.io/package/gopromotional-event-planner) |
| **SipLog** | [Space](https://huggingface.co/spaces/iamnazzal/gopromotional-siplog) | [Skill](https://smithery.ai/skills/nbkdevs/gopromotional-siplog) | [Skill](https://clawhub.ai/nbkdevs/skills/gopromotional-siplog) | [Report](https://socket.dev/npm/package/gopromotional-siplog) | [Listing](https://npm.io/package/gopromotional-siplog) |
| **PackList** | [Space](https://huggingface.co/spaces/iamnazzal/gopromotional-packlist) | [Skill](https://smithery.ai/skills/nbkdevs/gopromotional-packlist) | [Skill](https://clawhub.ai/nbkdevs/skills/gopromotional-packlist) | [Report](https://socket.dev/npm/package/gopromotional-packlist) | [Listing](https://npm.io/package/gopromotional-packlist) |
| **MugDay** | [Space](https://huggingface.co/spaces/iamnazzal/gopromotional-mugday) | [Skill](https://smithery.ai/skills/nbkdevs/gopromotional-mugday) | [Skill](https://clawhub.ai/nbkdevs/skills/gopromotional-mugday) | [Report](https://socket.dev/npm/package/gopromotional-mugday) | [Listing](https://npm.io/package/gopromotional-mugday) |

### Apps and extensions

- **Event Planner & Themes** (homepage group): Android app on the Amazon Appstore — [US](https://www.amazon.com/gp/product/B0HMHDLY67), [UK](https://www.amazon.co.uk/gp/product/B0HMHDLY67), [Germany](https://www.amazon.de/gp/product/B0HMHDLY67), [Italy](https://www.amazon.it/gp/product/B0HMHDLY67), [France](https://www.amazon.fr/gp/product/B0HMHDLY67), [Spain](https://www.amazon.es/gp/product/B0HMHDLY67), [Japan](https://www.amazon.co.jp/gp/product/B0HMHDLY67), [Canada](https://www.amazon.ca/gp/product/B0HMHDLY67), [Brazil](https://www.amazon.com.br/gp/product/B0HMHDLY67), [Mexico](https://www.amazon.com.mx/gp/product/B0HMHDLY67), [Australia](https://www.amazon.com.au/gp/product/B0HMHDLY67).
- **SipLog** (water bottles group): Android app on the Amazon Appstore — [US](https://www.amazon.com/gp/product/B0HMKCKJH1), [UK](https://www.amazon.co.uk/gp/product/B0HMKCKJH1), [Germany](https://www.amazon.de/gp/product/B0HMKCKJH1), [Italy](https://www.amazon.it/gp/product/B0HMKCKJH1), [France](https://www.amazon.fr/gp/product/B0HMKCKJH1), [Spain](https://www.amazon.es/gp/product/B0HMKCKJH1), [Japan](https://www.amazon.co.jp/gp/product/B0HMKCKJH1), [Canada](https://www.amazon.ca/gp/product/B0HMKCKJH1), [Brazil](https://www.amazon.com.br/gp/product/B0HMKCKJH1), [Mexico](https://www.amazon.com.mx/gp/product/B0HMKCKJH1), [Australia](https://www.amazon.com.au/gp/product/B0HMKCKJH1).
- **BagPack** (tote bags group): Android app on the Amazon Appstore — [US](https://www.amazon.com/gp/product/B0HMJ4MXYN), [UK](https://www.amazon.co.uk/gp/product/B0HMJ4MXYN), [Germany](https://www.amazon.de/gp/product/B0HMJ4MXYN), [Italy](https://www.amazon.it/gp/product/B0HMJ4MXYN), [France](https://www.amazon.fr/gp/product/B0HMJ4MXYN), [Spain](https://www.amazon.es/gp/product/B0HMJ4MXYN), [Japan](https://www.amazon.co.jp/gp/product/B0HMJ4MXYN), [Canada](https://www.amazon.ca/gp/product/B0HMJ4MXYN), [Brazil](https://www.amazon.com.br/gp/product/B0HMJ4MXYN), [Mexico](https://www.amazon.com.mx/gp/product/B0HMJ4MXYN), [Australia](https://www.amazon.com.au/gp/product/B0HMJ4MXYN).
- **MugDay** (travel mugs group): Android app on the Amazon Appstore — [US](https://www.amazon.com/gp/product/B0HMLC6RQ6), [UK](https://www.amazon.co.uk/gp/product/B0HMLC6RQ6), [Germany](https://www.amazon.de/gp/product/B0HMLC6RQ6), [Italy](https://www.amazon.it/gp/product/B0HMLC6RQ6), [France](https://www.amazon.fr/gp/product/B0HMLC6RQ6), [Spain](https://www.amazon.es/gp/product/B0HMLC6RQ6), [Japan](https://www.amazon.co.jp/gp/product/B0HMLC6RQ6), [Canada](https://www.amazon.ca/gp/product/B0HMLC6RQ6), [Brazil](https://www.amazon.com.br/gp/product/B0HMLC6RQ6), [Mexico](https://www.amazon.com.mx/gp/product/B0HMLC6RQ6), [Australia](https://www.amazon.com.au/gp/product/B0HMLC6RQ6).

Browser themes and extensions, which each work on their own:

- [Harbor Dusk on Chrome Web Store](https://chromewebstore.google.com/detail/pbhgdahgkigmpflogdmiafncnlnkdpbh): Chrome theme.
- [Alpine Mist on Chrome Web Store](https://chromewebstore.google.com/detail/kflfodmgbbdhkpikoomebopbfbhodfcl): Chrome theme.
- [Canvas Trail on Chrome Web Store](https://chromewebstore.google.com/detail/demnhebpjflkiigbjiaafanimnbbfkda): Chrome theme.
- [Ember Steam on Chrome Web Store](https://chromewebstore.google.com/detail/npikdfifgofdlamkcmpaoodooklondfo): Chrome theme.
- [Event Snapshot on Firefox Add-ons](https://addons.mozilla.org/en-US/firefox/addon/event-snapshot/): Firefox add-on.
- [Sip Quick on Firefox Add-ons](https://addons.mozilla.org/en-US/firefox/addon/sip-quick/): Firefox add-on.
- [Pack Quick on Firefox Add-ons](https://addons.mozilla.org/en-US/firefox/addon/pack-quick/): Firefox add-on.
- [Mug Tap on Firefox Add-ons](https://addons.mozilla.org/en-US/firefox/addon/mug-tap/): Firefox add-on.
- [Event Snapshot on Microsoft Edge Add-ons](https://microsoftedge.microsoft.com/addons/detail/event-snapshot/ofgjldcijmjhkibpckocnihajbagpbok): Edge add-on.
- [Sip Quick on Microsoft Edge Add-ons](https://microsoftedge.microsoft.com/addons/detail/jkopfgffoijcncmglafhpgpkbkmaekem): Edge add-on.
- [Pack Quick on Microsoft Edge Add-ons](https://microsoftedge.microsoft.com/addons/detail/mhjpfmdlafmjihpnjknkichaklpgaogj): Edge add-on.

### Mirrors and directories

- [GitLab mirror: nbkdevs/gopromotional](https://gitlab.com/nbkdevs/gopromotional): a copy of this repository on GitLab.
- Homepage listings: [Product Hunt](https://www.producthunt.com/products/gopromotional-2), [Indie Hackers](https://www.indiehackers.com/product/gopromotional), [Design Nominees](https://www.designnominees.com/sites/gopromotional-promotional-products-branded-merchandise)
- Water bottles listings: [Product Hunt](https://www.producthunt.com/products/gopromotional-water-bottles), [Indie Hackers](https://www.indiehackers.com/product/gopromotional-water-bottles), [Design Nominees](https://www.designnominees.com/sites/branded-water-bottles-by-gopromotional)
- Tote bags listings: [Product Hunt](https://www.producthunt.com/products/gopromotional-tote-bags), [Indie Hackers](https://www.indiehackers.com/product/gopromotional-tote-bags), [Design Nominees](https://www.designnominees.com/sites/branded-tote-bags-by-gopromotional)
- Travel mugs listings: [Product Hunt](https://www.producthunt.com/products/gopromotional-travel-mugs), [Indie Hackers](https://www.indiehackers.com/product/gopromotional-travel-mugs), [Design Nominees](https://www.designnominees.com/sites/branded-travel-mugs-by-gopromotional)

## Link guidance

- Use the preferred anchors above on GitHub READMEs and similar docs when a contextual link is appropriate.
- Do not place the same promotional link on every screen or every file.
- Assets must remain useful if the external link is removed.
