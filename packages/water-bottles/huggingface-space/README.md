---
title: GoPromotional SipLog
emoji: 💧
colorFrom: blue
colorTo: green
sdk: static
app_file: index.html
pinned: false
license: mit
short_description: Daily water goal and hydration log with streaks
tags:
  - hydration
  - water
  - tracker
  - health
---

# GoPromotional SipLog

A small hydration tracker: estimate a daily water goal from weight, exercise and weather, log drinks in ml, cl, litres or US fl oz, and see today's progress, the last 7 days, your average and your streak. The log is a plain JSON file.

Everything runs in your browser; nothing is uploaded. Your data stays in this browser's local storage.

- **Daily goal** estimate: weight × 33 ml + 12 ml per active minute (+500 ml in hot weather), rounded to 50 ml. A general guide, not medical advice.
- **Flexible amounts**: `250`, `500ml`, `33cl`, `0.5l`, `16oz`.
- **Progress**, **7-day history**, **average** and **streak** of days on target.
- **Undo** the last entry, **set** your own goal.
- Plain **JSON log** (`{goal, entries:[{date, ml}]}`), the same format in every language version.

The same logic ships as a library and CLI on npm, PyPI, crates.io and RubyGems as `gopromotional-siplog`, and as the Docker image `iamnazzal/gopromotional-siplog`. Built by GoPromotional; see [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles).

## Links

- Website: [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/water-bottles/huggingface-space`)
- Licence: MIT
