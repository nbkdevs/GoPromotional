---
title: GoPromotional MugDay
emoji: ☕
colorFrom: red
colorTo: yellow
sdk: static
app_file: index.html
pinned: false
license: mit
short_description: "Caffeine tracker: half-life, sleep-ready time, cup stats"
tags:
  - caffeine
  - coffee
  - tracker
  - health
---

# GoPromotional MugDay

Log coffee, tea and other drinks with typical caffeine values, compare the day's total with a limit (400 mg adult, 200 mg in pregnancy), estimate how much caffeine is still in your system, find out when it should drop below a sleep-friendly level, and track how often you used a reusable cup. Typical values only; not medical advice.

Everything runs in your browser; nothing is uploaded. Your data stays in this browser's local storage.

- **16 drinks** with typical caffeine (espresso 63 mg, filter coffee 140 mg, tea 75 mg, cola 40 mg...), or pass your own mg.
- **Daily total** against a limit: 400 mg (adult) or 200 mg (pregnancy), or your own.
- **Caffeine in your system** using a 5-hour half-life (configurable).
- **Sleep-ready time**: when the level should fall below a threshold (default 50 mg), rounded up to 15 minutes.
- **Reusable-cup stats** for the last 7 days.
- Plain **JSON log** shared by the npm, PyPI, crates.io and RubyGems versions.

The same logic ships as a library and CLI on npm, PyPI, crates.io and RubyGems as `gopromotional-mugday`, and as the Docker image `iamnazzal/gopromotional-mugday`. Built by GoPromotional; see [branded travel mugs](https://www.gopromotional.co.uk/branded-products/travel-mugs).

## Links

- Website: [branded travel mugs](https://www.gopromotional.co.uk/branded-products/travel-mugs)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/travel-mugs/huggingface-space`)
- Licence: MIT
