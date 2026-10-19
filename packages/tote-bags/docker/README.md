# iamnazzal/gopromotional-packlist

Build a packing checklist from templates (work day, travel, trade show, gym), scale clothes to the number of nights, add your own items, tick things off and see what is left. Lists are stored as plain Markdown checklists you can also edit by hand.

- **4 templates**: work day (10 items), travel (19), trade show (16), gym (9). Combine them; duplicates are merged.
- **Per-night quantities**: T-shirts, underwear and socks scale to nights + 1.
- **Tick off** items by a unique part of their name; see **progress** and what is **still to pack**.
- **Markdown storage**: `- [x] 3 × T-shirts` round-trips, so the list works in any editor, GitHub or Obsidian.

A small image (`node:20-alpine`, no dependencies, runs as the unprivileged `node` user) with the `gopromotional-packlist` CLI as its entrypoint.

## Usage

```sh
docker run --rm iamnazzal/gopromotional-packlist --help
docker run --rm -v "$PWD:/data" --user "$(id -u):$(id -g)" iamnazzal/gopromotional-packlist new --template travel --nights 3 --out trip.md
```

The working directory is `/data`; mount a host folder there to keep your lists between runs.

```console
$ gopromotional-packlist templates
work-day (10 items)
travel (19 items)
trade-show (16 items)
gym (9 items)

$ gopromotional-packlist new --template trade-show --extra "Tote bags" --title Expo --out expo.md
Wrote 17 items to expo.md

$ gopromotional-packlist check expo.md gaffer
1/17 packed (6%)

$ gopromotional-packlist status expo.md
Expo: 1/17 packed (6%)
Still to pack: Pop-up banner / stand, Table cloth, Branded giveaways, Business cards, Brochures / flyers, Lead capture sheet or scanner, Extension lead, Laptop / tablet, Phone charger, Scissors, Cable ties, Pens, Water bottles, Snacks, First-aid kit, Tote bags
```

## Build

```sh
cd packages/tote-bags
docker build -f docker/Dockerfile -t iamnazzal/gopromotional-packlist:1.0.0 -t iamnazzal/gopromotional-packlist:latest .
```

Image labels follow the OCI spec: `org.opencontainers.image.url` points to the project page and `org.opencontainers.image.source` to the repository.

## Links

- Website: [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/tote-bags/docker`)
- Licence: MIT
