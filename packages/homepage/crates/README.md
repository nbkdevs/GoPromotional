# gopromotional-event-planner

Plan an event or trade show from the command line or your code: a countdown, a 12-step planning timeline with due dates, an estimate of how many giveaway items to order, and the latest order date for printed merchandise in working days.

- **Countdown** to the event in days, and in weeks + days.
- **Planning timeline**: 12 tasks from 8 weeks out to the post-event follow-up, each with a due date, days left and an overdue flag.
- **Giveaway quantity**: attendees × days × uptake × items per person, plus a buffer, rounded up.
- **Order-by date**: production + delivery + safety margin counted back in working days (Mon–Fri).

## Install

```sh
cargo install gopromotional-event-planner     # CLI
cargo add gopromotional-event-planner         # library
```

## Command line

```console
$ gopromotional-event-planner countdown 2026-12-03 --today 2026-10-19
45 days to go (6 weeks 3 days) until 2026-12-03.

$ gopromotional-event-planner giveaways --attendees 500 --days 2 --uptake 0.6
Order 660 items (600 expected + 60 buffer).

$ gopromotional-event-planner order-by 2026-12-03 --today 2026-10-19
Order by 2026-11-12 (15 working days before the event), 24 days from today.

$ gopromotional-event-planner timeline 2026-12-03 --today 2026-10-19
! 2026-10-08  Confirm budget and attendee estimate  (overdue)
! 2026-10-15  Book the venue  (overdue)
  2026-10-22  Order branded merchandise and giveaways
  2026-10-29  Send invitations or open registration
  2026-11-05  Confirm speakers and agenda
...
```

Every command accepts `--json` for machine-readable output. Run `gopromotional-event-planner --help` for all options.

## Library

```rust
use gopromotional_event_planner::{countdown, giveaway_quantity, order_by_date, timeline, GiveawayInput, TIMELINE_TEMPLATE};

let c = countdown("2026-12-03", "2026-10-19").unwrap();
assert_eq!((c.days, c.weeks, c.remainder_days), (45, 6, 3));
let g = giveaway_quantity(&GiveawayInput { attendees: 500.0, days: 2.0, ..Default::default() }).unwrap();
assert_eq!(g.total, 660);
let o = order_by_date("2026-12-03", 10, 3, 2, Some("2026-10-19")).unwrap(); // production, delivery, safety days
assert_eq!(o.order_by, "2026-11-12");
let rows = timeline("2026-12-03", Some("2026-10-19"), &TIMELINE_TEMPLATE).unwrap();
assert_eq!(rows[0].due, "2026-10-08");
```

Dates are handled without a date crate; the CLI's default "today"/"now" is UTC (pass `--today`, `--date` or `--time` to be explicit).

## Links

- Website: [branded merchandise](https://www.gopromotional.co.uk/)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/homepage/crates`)
- Licence: MIT
