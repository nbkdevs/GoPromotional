# iamnazzal/gopromotional-event-planner

Plan an event or trade show from the command line or your code: a countdown, a 12-step planning timeline with due dates, an estimate of how many giveaway items to order, and the latest order date for printed merchandise in working days.

- **Countdown** to the event in days, and in weeks + days.
- **Planning timeline**: 12 tasks from 8 weeks out to the post-event follow-up, each with a due date, days left and an overdue flag.
- **Giveaway quantity**: attendees × days × uptake × items per person, plus a buffer, rounded up.
- **Order-by date**: production + delivery + safety margin counted back in working days (Mon–Fri).

A small image (`node:20-alpine`, no dependencies, runs as the unprivileged `node` user) with the `gopromotional-event-planner` CLI as its entrypoint.

## Usage

```sh
docker run --rm iamnazzal/gopromotional-event-planner --help
docker run --rm iamnazzal/gopromotional-event-planner countdown 2026-12-03 --today 2026-10-19
```

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

## Build

```sh
cd packages/homepage
docker build -f docker/Dockerfile -t iamnazzal/gopromotional-event-planner:1.0.0 -t iamnazzal/gopromotional-event-planner:latest .
```

Image labels follow the OCI spec: `org.opencontainers.image.url` points to the project page and `org.opencontainers.image.source` to the repository.

## Links

- Website: [branded merchandise](https://www.gopromotional.co.uk/)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/homepage/docker`)
- Licence: MIT
