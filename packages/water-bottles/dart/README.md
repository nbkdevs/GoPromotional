# gopromotional_siplog

A small hydration tracker: estimate a daily water goal from weight, exercise and weather, log drinks in ml, cl, litres or US fl oz, and see today's progress, the last 7 days, your average and your streak. The log is a plain JSON file.

- **Daily goal** estimate: weight × 33 ml + 12 ml per active minute (+500 ml in hot weather), rounded to 50 ml. A general guide, not medical advice.
- **Flexible amounts**: `250`, `500ml`, `33cl`, `0.5l`, `16oz`.
- **Progress**, **7-day history**, **average** and **streak** of days on target.
- **Undo** the last entry, **set** your own goal.
- Plain **JSON log** (`{goal, entries:[{date, ml}]}`), the same format in every language version.

## Install

```sh
dart pub add gopromotional_siplog
```

## Usage

```dart
import 'dart:convert';
import 'dart:io';

import 'package:gopromotional_siplog/gopromotional_siplog.dart';

void main() {
  print(dailyGoal(weightKg: 70, activityMinutes: 30, hot: true)); // 3150
  final file = File('log.json');                                   // same file as the CLI versions
  final log = file.existsSync() ? HydrationLog.fromJson(file.readAsStringSync()) : HydrationLog();
  log.add('500ml', date: '2026-10-19');
  print(log.progress(date: '2026-10-19').toJson());                // {date: 2026-10-19, total: 500, goal: 2000, percent: 25, ...}
  file.writeAsStringSync(jsonEncode(log));
}
```

Pure Dart, no dependencies; works on the VM, Flutter and the web. Run the tests with `dart test`.

## Links

- Website: [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/water-bottles/dart`)
- Also available on npm, PyPI, crates.io and RubyGems as `gopromotional-siplog`, and as the Docker image `iamnazzal/gopromotional-siplog`
- Licence: MIT
