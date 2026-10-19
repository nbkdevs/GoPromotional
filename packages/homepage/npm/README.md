# gopromotional-event-planner

Plan an event or trade show from the command line or your code: a countdown, a 12-step planning timeline with due dates, an estimate of how many giveaway items to order, and the latest order date for printed merchandise in working days.

- **Countdown** to the event in days, and in weeks + days.
- **Planning timeline**: 12 tasks from 8 weeks out to the post-event follow-up, each with a due date, days left and an overdue flag.
- **Giveaway quantity**: attendees × days × uptake × items per person, plus a buffer, rounded up.
- **Order-by date**: production + delivery + safety margin counted back in working days (Mon–Fri).

## Install

```sh
npm install gopromotional-event-planner          # library
npm install -g gopromotional-event-planner       # CLI
npx gopromotional-event-planner --help
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

```js
const { countdown, giveawayQuantity, orderByDate, timeline } = require('gopromotional-event-planner');

countdown('2026-12-03', '2026-10-19');               // { days: 45, weeks: 6, remainderDays: 3, status: 'upcoming', ... }
giveawayQuantity({ attendees: 500, days: 2 });         // { base: 600, buffer: 60, total: 660 }
orderByDate('2026-12-03', { today: '2026-10-19' });    // { orderBy: '2026-11-12', workingDays: 15, daysFromToday: 24, late: false }
timeline('2026-12-03', { today: '2026-10-19' })[0];    // { task: 'Confirm budget and attendee estimate', due: '2026-10-08', overdue: true, ... }
```

The library is a single dependency-free UMD file: it also works in the browser as a `<script>` tag (see the Hugging Face Space for a live demo).

## Links

- Website: [branded merchandise](https://www.gopromotional.co.uk/)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/homepage/npm`)
- Licence: MIT
