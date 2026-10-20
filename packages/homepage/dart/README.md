# gopromotional_event_planner

Plan an event or trade show from the command line or your code: a countdown, a 12-step planning timeline with due dates, an estimate of how many giveaway items to order, and the latest order date for printed merchandise in working days.

- **Countdown** to the event in days, and in weeks + days.
- **Planning timeline**: 12 tasks from 8 weeks out to the post-event follow-up, each with a due date, days left and an overdue flag.
- **Giveaway quantity**: attendees × days × uptake × items per person, plus a buffer, rounded up.
- **Order-by date**: production + delivery + safety margin counted back in working days (Mon–Fri).

## Install

```sh
dart pub add gopromotional_event_planner
```

## Usage

```dart
import 'package:gopromotional_event_planner/gopromotional_event_planner.dart';

void main() {
  final c = countdown('2026-12-03', today: '2026-10-19');
  print('${c.days} days to go (${c.weeks} weeks ${c.remainderDays} days)'); // 45 days to go (6 weeks 3 days)
  print(giveawayQuantity(attendees: 500, days: 2).toJson());                // {base: 600, buffer: 60, total: 660}
  print(orderByDate('2026-12-03', today: '2026-10-19').orderBy);            // 2026-11-12
  for (final row in timeline('2026-12-03', today: '2026-10-19')) {
    print('${row.due}  ${row.task}${row.overdue == true ? ' (overdue)' : ''}');
  }
}
```

Pure Dart, no dependencies; works on the VM, Flutter and the web. Run the tests with `dart test`.

## Links

- Website: [branded merchandise](https://www.gopromotional.co.uk/)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/homepage/dart`)
- Also available on npm, PyPI, crates.io and RubyGems as `gopromotional-event-planner`, and as the Docker image `iamnazzal/gopromotional-event-planner`
- Licence: MIT
