# gopromotional/packlist

Build a packing checklist from templates (work day, travel, trade show, gym), scale clothes to the number of nights, add your own items, tick things off and see what is left. Lists are stored as plain Markdown checklists you can also edit by hand.

- **4 templates**: work day (10 items), travel (19), trade show (16), gym (9). Combine them; duplicates are merged.
- **Per-night quantities**: T-shirts, underwear and socks scale to nights + 1.
- **Tick off** items by a unique part of their name; see **progress** and what is **still to pack**.
- **Markdown storage**: `- [x] 3 × T-shirts` round-trips, so the list works in any editor, GitHub or Obsidian.

## Install

```sh
composer require gopromotional/packlist
```

## Usage

```php
use GoPromotional\PackList\PackList;

$items = PackList::buildList(['trade-show'], nights: 0, extras: ['Tote bags']);
PackList::mark($items, 'gaffer');                 // unique substring match
PackList::progress($items);                       // ['packed' => 1, 'total' => 17, 'percent' => 6]
file_put_contents('expo.md', PackList::toMarkdown($items, 'Expo'));

['title' => $title, 'items' => $back] = PackList::parseMarkdown(file_get_contents('expo.md'));
```

Pure PHP 8.1+, no dependencies. Run the tests with `composer install && composer test`.

## Links

- Website: [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/tote-bags/php`)
- Also available on npm, PyPI, crates.io and RubyGems as `gopromotional-packlist`, and as the Docker image `iamnazzal/gopromotional-packlist`
- Licence: MIT
