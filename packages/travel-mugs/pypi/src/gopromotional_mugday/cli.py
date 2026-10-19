"""Command line interface for gopromotional-mugday."""
from __future__ import annotations

import argparse
import json
import os
import sys
from dataclasses import asdict
from pathlib import Path
from typing import Optional, Sequence

from . import __version__
from .core import DRINKS, CaffeineLog, now_local


def default_file() -> Path:
    return Path(os.environ.get("GOPROMOTIONAL_MUGDAY_FILE") or Path.home() / ".gopromotional-mugday.json")


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(prog="gopromotional-mugday", description="Caffeine and coffee intake tracker.",
                                epilog="Caffeine values are typical figures; real drinks vary. Not medical advice.")
    p.add_argument("-v", "--version", action="version", version=__version__)
    common = argparse.ArgumentParser(add_help=False)
    common.add_argument("--file", type=Path, default=None)
    common.add_argument("--json", action="store_true")
    sub = p.add_subparsers(dest="command", required=True)
    sub.add_parser("drinks", parents=[common], help="typical caffeine per drink")
    a = sub.add_parser("add", parents=[common], help="log a drink")
    a.add_argument("drink", nargs="+")
    a.add_argument("--time", default=None, help="YYYY-MM-DDTHH:MM (default now)")
    a.add_argument("--mg", type=float, default=None, help="caffeine in mg (overrides the typical value)")
    a.add_argument("--reusable", action="store_true", help="drank from a reusable cup")
    t = sub.add_parser("today", parents=[common], help="total for a day")
    t.add_argument("--date", default=None)
    t.add_argument("--limit", type=float, default=None)
    i = sub.add_parser("in-system", parents=[common], help="caffeine still in your system")
    i.add_argument("--at", default=None)
    s = sub.add_parser("sleep-ready", parents=[common], help="when caffeine drops below a threshold")
    s.add_argument("--threshold", type=float, default=50)
    c = sub.add_parser("cups", parents=[common], help="reusable vs disposable cups, last 7 days")
    c.add_argument("--date", default=None)
    sub.add_parser("undo", parents=[common], help="remove the last drink")
    return p


def _num(x):
    return int(x) if float(x).is_integer() else x


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    try:
        if args.command == "drinks":
            print(json.dumps(DRINKS, indent=2) if args.json else "\n".join(
                f'{k:<20} {v["mg"]:>4} mg' + (f'  ({v["ml"]} ml)' if v["ml"] else "") for k, v in DRINKS.items()))
            return 0
        path = args.file or default_file()
        log = CaffeineLog.from_json(path.read_text(encoding="utf-8")) if path.exists() else CaffeineLog()
        if args.command == "add":
            mg = None if args.mg is None else _num(args.mg)
            e = log.add(" ".join(args.drink), time=args.time or now_local(), mg=mg, reusable=args.reusable)
            path.write_text(log.to_json() + "\n", encoding="utf-8")
            r = log.day(e.time[:10])
            text = (f'Logged {e.drink} ({_num(e.mg)} mg{", reusable cup" if e.reusable else ""}). '
                    f'Today: {_num(r["total"])}/{_num(r["limit"])} mg.')
        elif args.command == "today":
            r = log.day(args.date, None if args.limit is None else _num(args.limit))
            text = f'{r["date"]}: {_num(r["total"])} mg from {r["drinks"]} drink(s); ' + (
                f'{_num(r["over"])} mg over the {_num(r["limit"])} mg limit.' if r["over"]
                else f'{_num(r["remaining"])} mg left of {_num(r["limit"])} mg.')
        elif args.command == "in-system":
            at = args.at or now_local()
            r = {"at": at, "mg": log.in_system(at)}
            text = f'About {r["mg"]} mg of caffeine still in your system at {at} (half-life {_num(log.half_life_hours)} h).'
        elif args.command == "sleep-ready":
            r = {"threshold": _num(args.threshold), "time": log.sleep_ready(args.threshold)}
            text = f'Below {_num(args.threshold)} mg from about {r["time"].replace("T", " ")}.' if r["time"] else "No drinks logged."
        elif args.command == "cups":
            r = log.cups(args.date)
            text = f'Last 7 days: {r["reusable"]} reusable, {r["disposable"]} disposable ({r["rate"]}% reusable).'
        else:
            e = log.undo()
            path.write_text(log.to_json() + "\n", encoding="utf-8")
            r = asdict(e) if e else None
            text = f"Removed {e.drink} at {e.time}." if e else "Nothing to undo."
    except ValueError as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 2
    print(json.dumps(r, indent=2) if args.json else text)
    return 0


if __name__ == "__main__":  # pragma: no cover
    sys.exit(main())
