"""Command line interface for gopromotional-packlist."""
from __future__ import annotations

import argparse
import json
import sys
from dataclasses import asdict
from pathlib import Path
from typing import Optional, Sequence

from . import __version__
from .core import TEMPLATES, Item, build_list, mark, parse_markdown, progress, remaining, to_markdown


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(prog="gopromotional-packlist", description="Packing checklists you can keep in a Markdown file.")
    p.add_argument("-v", "--version", action="version", version=__version__)
    sub = p.add_subparsers(dest="command", required=True)
    t = sub.add_parser("templates", help="list built-in templates")
    t.add_argument("--json", action="store_true")
    n = sub.add_parser("new", help="create a checklist")
    n.add_argument("--template", default="travel", help="comma-separated: work-day, travel, trade-show, gym")
    n.add_argument("--nights", type=int, default=0)
    n.add_argument("--extra", action="append", default=[])
    n.add_argument("--title", default="Packing list")
    n.add_argument("--out", type=Path)
    n.add_argument("--json", action="store_true")
    for name, help_ in (("check", "mark an item packed"), ("uncheck", "mark an item not packed"), ("add", "add an item")):
        c = sub.add_parser(name, help=help_)
        c.add_argument("file", type=Path)
        c.add_argument("item", nargs="+")
    s = sub.add_parser("status", help="progress and what is still left")
    s.add_argument("file", type=Path)
    s.add_argument("--json", action="store_true")
    return p


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    try:
        if args.command == "templates":
            if args.json:
                print(json.dumps({k: [asdict(i) for i in v] for k, v in TEMPLATES.items()}, indent=2))
            else:
                print("\n".join(f"{k} ({len(v)} items)" for k, v in TEMPLATES.items()))
        elif args.command == "new":
            items = build_list([s.strip() for s in args.template.split(",") if s.strip()], args.nights, args.extra)
            md = to_markdown(items, args.title)
            if args.out:
                args.out.write_text(md, encoding="utf-8")
                print(f"Wrote {len(items)} items to {args.out}")
            else:
                print(json.dumps([asdict(i) for i in items], indent=2, ensure_ascii=False) if args.json else md)
        elif args.command in ("check", "uncheck", "add"):
            name = " ".join(args.item)
            title, items = parse_markdown(args.file.read_text(encoding="utf-8"))
            if args.command == "add":
                if any(i.name.lower() == name.lower() for i in items):
                    raise ValueError(f'"{name}" is already on the list')
                items.append(Item(name, "Extras"))
            else:
                mark(items, name, args.command == "check")
            args.file.write_text(to_markdown(items, title), encoding="utf-8")
            p = progress(items)
            print(f'{p["packed"]}/{p["total"]} packed ({p["percent"]}%)')
        else:
            title, items = parse_markdown(args.file.read_text(encoding="utf-8"))
            p, left = progress(items), remaining(items)
            if args.json:
                print(json.dumps({**p, "remaining": [asdict(i) for i in left]}, indent=2, ensure_ascii=False))
            else:
                print(f'{title}: {p["packed"]}/{p["total"]} packed ({p["percent"]}%)')
                print(f'Still to pack: {", ".join(i.name for i in left)}' if left else "All packed.")
    except (ValueError, OSError) as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":  # pragma: no cover
    sys.exit(main())
