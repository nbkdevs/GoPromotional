# gopromotional/mugday

Log coffee, tea and other drinks with typical caffeine values, compare the day's total with a limit (400 mg adult, 200 mg in pregnancy), estimate how much caffeine is still in your system, find out when it should drop below a sleep-friendly level, and track how often you used a reusable cup. Typical values only; not medical advice.

- **16 drinks** with typical caffeine (espresso 63 mg, filter coffee 140 mg, tea 75 mg, cola 40 mg...), or pass your own mg.
- **Daily total** against a limit: 400 mg (adult) or 200 mg (pregnancy), or your own.
- **Caffeine in your system** using a 5-hour half-life (configurable).
- **Sleep-ready time**: when the level should fall below a threshold (default 50 mg), rounded up to 15 minutes.
- **Reusable-cup stats** for the last 7 days.
- Plain **JSON log** shared by the npm, PyPI, crates.io and RubyGems versions.

## Install

```sh
composer require gopromotional/mugday
```

## Usage

```php
use GoPromotional\MugDay\MugDay;

$log = new MugDay();                              // 400 mg limit, 5 h half-life
$log->add('flat-white', '2026-10-19T08:00', reusable: true);
$log->add('filter-coffee', '2026-10-19T14:00');
$log->day('2026-10-19');                          // ['total' => 266, 'limit' => 400, 'remaining' => 134, ...]
$log->inSystem('2026-10-19T22:00');               // mg still in your system
$log->sleepReady(50);                             // '2026-10-20T00:00'
$log->cups('2026-10-19');                         // ['reusable' => 1, 'disposable' => 1, 'rate' => 50]
```

Pure PHP 8.1+, no dependencies. Run the tests with `composer install && composer test`.

## Links

- Website: [branded travel mugs for event marketing](https://www.gopromotional.co.uk/branded-products/travel-mugs)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/travel-mugs/php`)
- Also available on npm, PyPI, crates.io and RubyGems as `gopromotional-mugday`, and as the Docker image `iamnazzal/gopromotional-mugday`
- Licence: MIT
