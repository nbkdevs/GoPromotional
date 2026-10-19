# gopromotional-event-planner

Plan an event or trade show from the command line or your code: a countdown, a 12-step planning timeline with due dates, an estimate of how many giveaway items to order, and the latest order date for printed merchandise in working days.

- **Countdown** to the event in days, and in weeks + days.
- **Planning timeline**: 12 tasks from 8 weeks out to the post-event follow-up, each with a due date, days left and an overdue flag.
- **Giveaway quantity**: attendees × days × uptake × items per person, plus a buffer, rounded up.
- **Order-by date**: production + delivery + safety margin counted back in working days (Mon–Fri).

## Install

```sh
gem install gopromotional-event-planner
# or in a Gemfile:
gem "gopromotional-event-planner"
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

```ruby
require "gopromotional/event_planner"
EP = GoPromotional::EventPlanner

EP.countdown("2026-12-03", today: "2026-10-19")[:days]              # => 45
EP.giveaway_quantity(attendees: 500, days: 2)                        # => {base: 600, buffer: 60, total: 660}
EP.order_by_date("2026-12-03", today: "2026-10-19")[:order_by]       # => "2026-11-12"
EP.timeline("2026-12-03", today: "2026-10-19").select { |r| r[:overdue] }
```

Pure Ruby, no runtime dependencies, Ruby 3.0+. `require "gopromotional-event-planner"` also works.

## Links

- Website: [https://www.gopromotional.co.uk/](https://www.gopromotional.co.uk/)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/homepage/rubygems`)
- Licence: MIT
