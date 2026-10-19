"""Command line interface for gopromotional-siplog."""
from __future__ import annotations

import argparse
import json
import os
import sys
from datetime import date
from pathlib import Path
from typing import Optional, Sequence

from . import __version__
from .core import HydrationLog, daily_goal, parse_amount


def default_file() -> Path:
    return Path(os.environ.get("GOPROMOTIONAL_SIPLOG_FILE") or Path.home() / ".gopromotional-siplog.json")


def _load(path: Path) -> HydrationLog:
    return HydrationLog.from_json(path.read_text(encoding="utf-8")) if path.exists() else HydrationLog()


def _save(path: Path, log: HydrationLog) -> None:
    path.write_text(log.to_json() + "\n", encoding="utf-8")


def _bar(pct: int) -> str:
    return ("#" * min(20, int(pct / 5 + 0.5))).ljust(20, ".")


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(prog="gopromotional-siplog", description="Daily water goal calculator and hydration log.")
    p.add_argument("-v", "--version", action="version", version=__version__)
    common = argparse.ArgumentParser(add_help=False)
    common.add_argument("--file", type=Path, default=None, help="log file (default ~/.gopromotional-siplog.json)")
    common.add_argument("--date", default=None, help="day to use (YYYY-MM-DD, default today)")
    common.add_argument("--json", action="store_true")
    sub = p.add_subparsers(dest="command", required=True)
    g = sub.add_parser("goal", parents=[common], help="suggest a daily water goal")
    g.add_argument("--weight", type=float, required=True, help="body weight in kg")
    g.add_argument("--activity", type=float, default=0, help="minutes of exercise")
    g.add_argument("--hot", action="store_true", help="hot weather")
    a = sub.add_parser("add", parents=[common], help="log a drink, e.g. 250, 500ml, 0.5l, 33cl, 16oz")
    a.add_argument("amount")
    sub.add_parser("status", parents=[common], help="today's progress")
    sub.add_parser("week", parents=[common], help="last 7 days")
    sub.add_parser("undo", parents=[common], help="remove the last entry for the day")
    s = sub.add_parser("set-goal", parents=[common], help="set the daily goal in ml")
    s.add_argument("ml")
    return p


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    day = args.date or date.today().isoformat()
    path = args.file or default_file()
    try:
        if args.command == "goal":
            r = {"goal": daily_goal(args.weight, args.activity, args.hot)}
            text = f'Suggested daily water goal: {r["goal"]} ml (a general guide, not medical advice).'
        else:
            log = _load(path)
            if args.command == "add":
                ml = log.add(args.amount, day).ml
                _save(path, log)
                r = log.progress(day)
                text = f'Added {ml} ml. {r["total"]}/{r["goal"]} ml ({r["percent"]}%).'
            elif args.command == "status":
                r = log.progress(day)
                text = (f'{r["date"]}: {r["total"]}/{r["goal"]} ml [{_bar(r["percent"])}] {r["percent"]}%'
                        + (" - goal reached" if r["reached"] else f' - {r["remaining"]} ml to go')
                        + f"\nStreak: {log.streak(day)} day(s)")
            elif args.command == "week":
                r = log.week(day)
                text = "\n".join(f'{d["date"]}  {d["total"]:>5} ml  {"goal" if d["total"] >= log.goal else ""}' for d in r)
                text += f"\nAverage: {log.average(day)} ml/day"
            elif args.command == "undo":
                e = log.undo(day)
                _save(path, log)
                r = None if e is None else {"date": e.date, "ml": e.ml}
                text = f"Removed {e.ml} ml from {e.date}." if e else "Nothing to undo."
            else:
                g = parse_amount(args.ml)
                if not 500 <= g <= 6000:
                    raise ValueError("goal must be between 500 and 6000 ml")
                log.goal = g
                _save(path, log)
                r = {"goal": g}
                text = f"Daily goal set to {g} ml."
    except ValueError as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 2
    print(json.dumps(r, indent=2) if args.json else text)
    return 0


if __name__ == "__main__":  # pragma: no cover
    sys.exit(main())
