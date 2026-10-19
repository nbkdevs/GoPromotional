# gopromotional-siplog

A small hydration tracker: estimate a daily water goal from weight, exercise and weather, log drinks in ml, cl, litres or US fl oz, and see today's progress, the last 7 days, your average and your streak. The log is a plain JSON file.

- **Daily goal** estimate: weight × 33 ml + 12 ml per active minute (+500 ml in hot weather), rounded to 50 ml. A general guide, not medical advice.
- **Flexible amounts**: `250`, `500ml`, `33cl`, `0.5l`, `16oz`.
- **Progress**, **7-day history**, **average** and **streak** of days on target.
- **Undo** the last entry, **set** your own goal.
- Plain **JSON log** (`{goal, entries:[{date, ml}]}`), the same format in every language version.

## Install

```sh
pip install gopromotional-siplog
# or, for the CLI only:
pipx install gopromotional-siplog
```

## Command line

```console
$ gopromotional-siplog goal --weight 70 --activity 30 --hot
Suggested daily water goal: 3150 ml (a general guide, not medical advice).

$ gopromotional-siplog set-goal 2.5l
Daily goal set to 2500 ml.

$ gopromotional-siplog add 500ml --date 2026-10-19
Added 500 ml. 500/2500 ml (20%).

$ gopromotional-siplog add 16oz --date 2026-10-19
Added 473 ml. 973/2500 ml (39%).

$ gopromotional-siplog status --date 2026-10-19
2026-10-19: 973/2500 ml [########............] 39% - 1527 ml to go
Streak: 0 day(s)
```

Every command accepts `--json` for machine-readable output. Run `gopromotional-siplog --help` for all options.

Data is kept in `~/.gopromotional-siplog.json` (set `GOPROMOTIONAL_SIPLOG_FILE` or pass `--file` to use another file). The JSON format is the same across the npm, PyPI, crates.io and RubyGems versions, so you can switch tools without losing your log.

## Library

```python
from gopromotional_siplog import HydrationLog, daily_goal, parse_amount

daily_goal(70, activity_minutes=30, hot=True)   # 3150
parse_amount("16oz")                            # 473
log = HydrationLog(goal=2000)
log.add("500ml", "2026-10-19"); log.add(250, "2026-10-19")
log.progress("2026-10-19")["percent"]           # 38
log.streak("2026-10-19"), log.average("2026-10-19")
```

Pure Python, no dependencies, Python 3.9+.

## Links

- Website: [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/water-bottles/pypi`)
- Licence: MIT
