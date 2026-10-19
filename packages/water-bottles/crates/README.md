# gopromotional-siplog

A small hydration tracker: estimate a daily water goal from weight, exercise and weather, log drinks in ml, cl, litres or US fl oz, and see today's progress, the last 7 days, your average and your streak. The log is a plain JSON file.

- **Daily goal** estimate: weight × 33 ml + 12 ml per active minute (+500 ml in hot weather), rounded to 50 ml. A general guide, not medical advice.
- **Flexible amounts**: `250`, `500ml`, `33cl`, `0.5l`, `16oz`.
- **Progress**, **7-day history**, **average** and **streak** of days on target.
- **Undo** the last entry, **set** your own goal.
- Plain **JSON log** (`{goal, entries:[{date, ml}]}`), the same format in every language version.

## Install

```sh
cargo install gopromotional-siplog     # CLI
cargo add gopromotional-siplog         # library
```

## Command line

```console
$ gopromotional-siplog goal --weight 70 --activity 30 --hot
Suggested daily water goal: 3150 ml (a general guide, not medical advice).

$ gopromotional-siplog set-goal 2.5l
Daily goal set to 2500 ml.

$ gopromotional-siplog add 500ml --date 2026-10-19
Added 500 ml. 500/2500 ml (20%).

$ gopromotional-siplog add 16oz --date 2026-10-19
Added 473 ml. 973/2500 ml (39%).

$ gopromotional-siplog status --date 2026-10-19
2026-10-19: 973/2500 ml [########............] 39% - 1527 ml to go
Streak: 0 day(s)
```

Every command accepts `--json` for machine-readable output. Run `gopromotional-siplog --help` for all options.

Data is kept in `~/.gopromotional-siplog.json` (set `GOPROMOTIONAL_SIPLOG_FILE` or pass `--file` to use another file). The JSON format is the same across the npm, PyPI, crates.io and RubyGems versions, so you can switch tools without losing your log.

## Library

```rust
use gopromotional_siplog::{daily_goal, parse_amount, HydrationLog};

assert_eq!(daily_goal(70.0, 30.0, true).unwrap(), 3150);
assert_eq!(parse_amount("16oz").unwrap(), 473);
let mut log = HydrationLog::new(2000);
log.add("500ml", "2026-10-19").unwrap();
log.add("250", "2026-10-19").unwrap();
assert_eq!(log.progress("2026-10-19").unwrap().percent, 38);
let json = log.to_json(); // {"goal": 2000, "entries": [...]}
assert!(json.contains(r#""goal": 2000"#));
```

Dates are handled without a date crate; the CLI's default "today"/"now" is UTC (pass `--today`, `--date` or `--time` to be explicit).

## Links

- Website: [https://www.gopromotional.co.uk/branded-products/branded-water-bottles](https://www.gopromotional.co.uk/branded-products/branded-water-bottles)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/water-bottles/crates`)
- Licence: MIT
