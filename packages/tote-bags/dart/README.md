# gopromotional_packlist

Build a packing checklist from templates (work day, travel, trade show, gym), scale clothes to the number of nights, add your own items, tick things off and see what is left. Lists are stored as plain Markdown checklists you can also edit by hand.

- **4 templates**: work day (10 items), travel (19), trade show (16), gym (9). Combine them; duplicates are merged.
- **Per-night quantities**: T-shirts, underwear and socks scale to nights + 1.
- **Tick off** items by a unique part of their name; see **progress** and what is **still to pack**.
- **Markdown storage**: `- [x] 3 × T-shirts` round-trips, so the list works in any editor, GitHub or Obsidian.

## Install

```sh
dart pub add gopromotional_packlist
```

## Usage

```dart
import 'package:gopromotional_packlist/gopromotional_packlist.dart';

void main() {
  final items = buildList(templateNames: ['trade-show'], extras: ['Tote bags']);
  mark(items, 'gaffer');                 // unique substring match
  print(progress(items));                // (packed: 1, percent: 6, total: 17)
  final md = toMarkdown(items, 'Expo');  // Markdown checklist
  print(parseMarkdown(md).items.length); // 17
}
```

Pure Dart, no dependencies; works on the VM, Flutter and the web. Run the tests with `dart test`.

## Links

- Website: [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/tote-bags/dart`)
- Also available on npm, PyPI, crates.io and RubyGems as `gopromotional-packlist`, and as the Docker image `iamnazzal/gopromotional-packlist`
- Licence: MIT
