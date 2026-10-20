# gopromotional_mugday

Log coffee, tea and other drinks with typical caffeine values, compare the day's total with a limit (400 mg adult, 200 mg in pregnancy), estimate how much caffeine is still in your system, find out when it should drop below a sleep-friendly level, and track how often you used a reusable cup. Typical values only; not medical advice.

- **16 drinks** with typical caffeine (espresso 63 mg, filter coffee 140 mg, tea 75 mg, cola 40 mg...), or pass your own mg.
- **Daily total** against a limit: 400 mg (adult) or 200 mg (pregnancy), or your own.
- **Caffeine in your system** using a 5-hour half-life (configurable).
- **Sleep-ready time**: when the level should fall below a threshold (default 50 mg), rounded up to 15 minutes.
- **Reusable-cup stats** for the last 7 days.
- Plain **JSON log** shared by the npm, PyPI, crates.io and RubyGems versions.

## Install

```sh
dart pub add gopromotional_mugday
```

## Usage

```dart
import 'package:gopromotional_mugday/gopromotional_mugday.dart';

void main() {
  final log = CaffeineLog()
    ..add('flat-white', time: '2026-10-19T08:00', reusable: true)
    ..add('filter-coffee', time: '2026-10-19T14:00');
  print(log.day(date: '2026-10-19').total);       // 266
  print(log.inSystem(at: '2026-10-19T22:00'));    // mg still in your system
  print(log.sleepReady(50));                      // 2026-10-20T00:00
  print(log.cups(endDate: '2026-10-19'));         // (disposable: 1, rate: 50, reusable: 1)
}
```

Pure Dart, no dependencies; works on the VM, Flutter and the web. Run the tests with `dart test`.

## Links

- Website: [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/travel-mugs/dart`)
- Also available on npm, PyPI, crates.io and RubyGems as `gopromotional-mugday`, and as the Docker image `iamnazzal/gopromotional-mugday`
- Licence: MIT
