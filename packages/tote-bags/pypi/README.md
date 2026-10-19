# gopromotional-packlist

Build a packing checklist from templates (work day, travel, trade show, gym), scale clothes to the number of nights, add your own items, tick things off and see what is left. Lists are stored as plain Markdown checklists you can also edit by hand.

- **4 templates**: work day (10 items), travel (19), trade show (16), gym (9). Combine them; duplicates are merged.
- **Per-night quantities**: T-shirts, underwear and socks scale to nights + 1.
- **Tick off** items by a unique part of their name; see **progress** and what is **still to pack**.
- **Markdown storage**: `- [x] 3 × T-shirts` round-trips, so the list works in any editor, GitHub or Obsidian.

## Install

```sh
pip install gopromotional-packlist
# or, for the CLI only:
pipx install gopromotional-packlist
```

## Command line

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

Every command accepts `--json` for machine-readable output. Run `gopromotional-packlist --help` for all options.

## Library

```python
from gopromotional_packlist import build_list, mark, progress, to_markdown, parse_markdown

items = build_list(["travel", "work-day"], nights=3, extras=["Umbrella"])
mark(items, "passport", True)
progress(items)                      # {'packed': 1, 'total': 28, 'percent': 4}
md = to_markdown(items, "Lisbon")
title, back = parse_markdown(md)
```

Pure Python, no dependencies, Python 3.9+.

## Links

- Website: [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/tote-bags/pypi`)
- Licence: MIT
