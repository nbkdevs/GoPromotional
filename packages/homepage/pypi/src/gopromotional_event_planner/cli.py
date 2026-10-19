"""Command line interface for gopromotional-event-planner."""
from __future__ import annotations

import argparse
import json
import sys
from datetime import date
from typing import Optional, Sequence

from . import __version__
from .core import countdown, giveaway_quantity, order_by_date, timeline


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(prog="gopromotional-event-planner",
                                description="Event countdown, planning timeline, giveaway quantities and order-by dates.")
    p.add_argument("-v", "--version", action="version", version=__version__)
    sub = p.add_subparsers(dest="command", required=True)
    common = argparse.ArgumentParser(add_help=False)
    common.add_argument("--json", action="store_true", help="print JSON")
    common.add_argument("--today", default=None, help="override today (YYYY-MM-DD)")
    s = sub.add_parser("countdown", parents=[common], help="days until the event")
    s.add_argument("date")
    s = sub.add_parser("timeline", parents=[common], help="planning checklist with due dates")
    s.add_argument("date")
    s = sub.add_parser("giveaways", parents=[common], help="how many giveaway items to order")
    s.add_argument("--attendees", type=float, required=True)
    s.add_argument("--days", type=float, default=1)
    s.add_argument("--uptake", type=float, default=0.6)
    s.add_argument("--per-person", type=float, default=1)
    s.add_argument("--buffer", type=float, default=0.1)
    s = sub.add_parser("order-by", parents=[common], help="latest order date for printed merchandise")
    s.add_argument("date")
    s.add_argument("--production", type=int, default=10)
    s.add_argument("--delivery", type=int, default=3)
    s.add_argument("--safety", type=int, default=2)
    return p


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    today = args.today or date.today().isoformat()
    try:
        if args.command == "countdown":
            r = countdown(args.date, today)
            if r["status"] == "today":
                text = f'{r["event_date"]} is today.'
            elif r["status"] == "upcoming":
                text = (f'{r["days"]} days to go ({r["weeks"]} weeks {r["remainder_days"]} days) '
                        f'until {r["event_date"]}.')
            else:
                text = f'{r["event_date"]} was {-r["days"]} days ago.'
        elif args.command == "timeline":
            r = timeline(args.date, today)
            text = "\n".join(f'{"!" if x["overdue"] else " "} {x["due"]}  {x["task"]}{"  (overdue)" if x["overdue"] else ""}'
                             for x in r)
        elif args.command == "giveaways":
            r = giveaway_quantity(args.attendees, args.days, args.uptake, args.per_person, args.buffer)
            text = f'Order {r["total"]} items ({r["base"]} expected + {r["buffer"]} buffer).'
        else:
            r = order_by_date(args.date, args.production, args.delivery, args.safety, today)
            text = f'Order by {r["order_by"]} ({r["working_days"]} working days before the event)'
            text += (f' - that is {-r["days_from_today"]} days ago, so ask about express production.' if r["late"]
                     else f', {r["days_from_today"]} days from today.')
    except ValueError as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 2
    print(json.dumps(r, indent=2) if args.json else text)
    return 0


if __name__ == "__main__":  # pragma: no cover
    sys.exit(main())
