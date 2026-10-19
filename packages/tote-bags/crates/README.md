# gopromotional-packlist

Build a packing checklist from templates (work day, travel, trade show, gym), scale clothes to the number of nights, add your own items, tick things off and see what is left. Lists are stored as plain Markdown checklists you can also edit by hand.

- **4 templates**: work day (10 items), travel (19), trade show (16), gym (9). Combine them; duplicates are merged.
- **Per-night quantities**: T-shirts, underwear and socks scale to nights + 1.
- **Tick off** items by a unique part of their name; see **progress** and what is **still to pack**.
- **Markdown storage**: `- [x] 3 × T-shirts` round-trips, so the list works in any editor, GitHub or Obsidian.

## Install

```sh
cargo install gopromotional-packlist     # CLI
cargo add gopromotional-packlist         # library
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

```rust
use gopromotional_packlist::{build_list, mark, parse_markdown, progress, to_markdown};

let mut items = build_list(&["travel", "work-day"], 3, &["Umbrella"]).unwrap();
mark(&mut items, "passport", Some(true)).unwrap();
assert_eq!(progress(&items).packed, 1);
let md = to_markdown(&items, "Lisbon");
let (title, back) = parse_markdown(&md);
assert_eq!((title.as_str(), back), ("Lisbon", items));
```

Dates are handled without a date crate; the CLI's default "today"/"now" is UTC (pass `--today`, `--date` or `--time` to be explicit).

## Links

- Website: [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/tote-bags/crates`)
- Licence: MIT
