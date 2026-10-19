# gopromotional-mugday

Log coffee, tea and other drinks with typical caffeine values, compare the day's total with a limit (400 mg adult, 200 mg in pregnancy), estimate how much caffeine is still in your system, find out when it should drop below a sleep-friendly level, and track how often you used a reusable cup. Typical values only; not medical advice.

- **16 drinks** with typical caffeine (espresso 63 mg, filter coffee 140 mg, tea 75 mg, cola 40 mg...), or pass your own mg.
- **Daily total** against a limit: 400 mg (adult) or 200 mg (pregnancy), or your own.
- **Caffeine in your system** using a 5-hour half-life (configurable).
- **Sleep-ready time**: when the level should fall below a threshold (default 50 mg), rounded up to 15 minutes.
- **Reusable-cup stats** for the last 7 days.
- Plain **JSON log** shared by the npm, PyPI, crates.io and RubyGems versions.

## Install

```sh
cargo install gopromotional-mugday     # CLI
cargo add gopromotional-mugday         # library
```

## Command line

```console
$ gopromotional-mugday drinks
espresso               63 mg  (30 ml)
double-espresso       126 mg  (60 ml)
americano             126 mg  (350 ml)
latte                 126 mg  (350 ml)
cappuccino            126 mg  (350 ml)
...

$ gopromotional-mugday add flat-white --time 2026-10-19T08:00 --reusable
Logged flat-white (126 mg, reusable cup). Today: 126/400 mg.

$ gopromotional-mugday add tea --time 2026-10-19T11:00 --reusable
Logged tea (75 mg, reusable cup). Today: 201/400 mg.

$ gopromotional-mugday add filter-coffee --time 2026-10-19T14:00
Logged filter-coffee (140 mg). Today: 341/400 mg.

$ gopromotional-mugday today --date 2026-10-19
2026-10-19: 341 mg from 3 drink(s); 59 mg left of 400 mg.

$ gopromotional-mugday in-system --at 2026-10-19T22:00
About 81 mg of caffeine still in your system at 2026-10-19T22:00 (half-life 5 h).

$ gopromotional-mugday sleep-ready
Below 50 mg from about 2026-10-20 01:30.

$ gopromotional-mugday cups --date 2026-10-19
Last 7 days: 2 reusable, 1 disposable (67% reusable).
```

Every command accepts `--json` for machine-readable output. Run `gopromotional-mugday --help` for all options.

Data is kept in `~/.gopromotional-mugday.json` (set `GOPROMOTIONAL_MUGDAY_FILE` or pass `--file` to use another file). The JSON format is the same across the npm, PyPI, crates.io and RubyGems versions, so you can switch tools without losing your log.

## Library

```rust
use gopromotional_mugday::CaffeineLog;

let mut log = CaffeineLog::default();          // limit 400 mg, half-life 5 h
log.add("flat-white", "2026-10-19T08:00", None, true).unwrap();
log.add("filter-coffee", "2026-10-19T14:00", None, false).unwrap();
assert_eq!(log.day("2026-10-19", None).total, 266.0);
assert_eq!(log.in_system("2026-10-19T22:00").unwrap(), 64);
assert_eq!(log.sleep_ready(50.0).as_deref(), Some("2026-10-20T00:00"));
```

Dates are handled without a date crate; the CLI's default "today"/"now" is UTC (pass `--today`, `--date` or `--time` to be explicit).

## Links

- Website: [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/travel-mugs/crates`)
- Licence: MIT
