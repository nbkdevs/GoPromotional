"""Packing checklists with templates, per-night quantities and a round-trip Markdown format."""
from __future__ import annotations

import math
import re
from dataclasses import dataclass
from typing import Iterable, Optional


@dataclass(frozen=True)
class TemplateItem:
    name: str
    category: str
    per_night: bool = False


def _t(rows):
    return tuple(TemplateItem(*r) for r in rows)


TEMPLATES: dict[str, tuple[TemplateItem, ...]] = {
    "work-day": _t([
        ("Laptop", "Work"), ("Laptop charger", "Work"), ("Phone charger", "Work"), ("Badge / ID", "Work"),
        ("Notebook", "Work"), ("Pen", "Work"), ("Headphones", "Work"), ("Water bottle", "Work"),
        ("Reusable coffee cup", "Work"), ("Lunch / snacks", "Work"),
    ]),
    "travel": _t([
        ("Passport / ID", "Documents"), ("Tickets / boarding pass", "Documents"), ("Bank cards", "Documents"),
        ("T-shirts", "Clothes", True), ("Underwear", "Clothes", True), ("Socks", "Clothes", True),
        ("Trousers / skirt", "Clothes"), ("Jumper", "Clothes"), ("Sleepwear", "Clothes"),
        ("Toothbrush", "Toiletries"), ("Toothpaste", "Toiletries"), ("Deodorant", "Toiletries"),
        ("Medications", "Toiletries"),
        ("Phone charger", "Tech"), ("Power bank", "Tech"), ("Plug adapter", "Tech"), ("Headphones", "Tech"),
        ("Reusable water bottle", "Other"), ("Tote bag", "Other"),
    ]),
    "trade-show": _t([
        ("Pop-up banner / stand", "Stand"), ("Table cloth", "Stand"), ("Branded giveaways", "Stand"),
        ("Business cards", "Stand"), ("Brochures / flyers", "Stand"), ("Lead capture sheet or scanner", "Stand"),
        ("Extension lead", "Kit"), ("Laptop / tablet", "Kit"), ("Phone charger", "Kit"), ("Gaffer tape", "Kit"),
        ("Scissors", "Kit"), ("Cable ties", "Kit"), ("Pens", "Kit"),
        ("Water bottles", "Team"), ("Snacks", "Team"), ("First-aid kit", "Team"),
    ]),
    "gym": _t([
        ("Gym clothes", "Gym"), ("Trainers", "Gym"), ("Towel", "Gym"), ("Water bottle", "Gym"), ("Padlock", "Gym"),
        ("Headphones", "Gym"), ("Shower gel", "Gym"), ("Deodorant", "Gym"), ("Snack", "Gym"),
    ]),
}


@dataclass
class Item:
    name: str
    category: str
    qty: int = 1
    packed: bool = False

    @property
    def label(self) -> str:
        return f"{self.qty} × {self.name}" if self.qty > 1 else self.name


def _key(s: str) -> str:
    return str(s).strip().lower()


def build_list(templates: Iterable[str] = ("travel",), nights: int = 0, extras: Iterable[str] = ()) -> list[Item]:
    """Build a checklist. Per-night items get nights + 1 (one spare); duplicate names are merged (first wins)."""
    if not isinstance(nights, int) or isinstance(nights, bool) or not 0 <= nights <= 365:
        raise ValueError("nights must be an integer from 0 to 365")
    seen: set[str] = set()
    items: list[Item] = []

    def push(it: Item) -> None:
        k = _key(it.name)
        if k and k not in seen:
            seen.add(k)
            items.append(it)

    for t in templates:
        if t not in TEMPLATES:
            raise ValueError(f'Unknown template "{t}". Choose from: {", ".join(TEMPLATES)}')
        for r in TEMPLATES[t]:
            push(Item(r.name, r.category, nights + 1 if r.per_night and nights > 0 else 1))
    for e in extras:
        push(Item(str(e).strip(), "Extras"))
    return items


def find(items: list[Item], name: str) -> Item:
    k = _key(name)
    for i in items:
        if _key(i.name) == k:
            return i
    partial = [i for i in items if k in _key(i.name)]
    if len(partial) == 1:
        return partial[0]
    if partial:
        raise ValueError(f'"{name}" matches {len(partial)} items: {", ".join(i.name for i in partial)}')
    raise ValueError(f'No item matches "{name}"')


def mark(items: list[Item], name: str, packed: Optional[bool] = None) -> Item:
    """Mark an item packed (toggle when ``packed`` is None). Exact name first, then a unique substring."""
    it = find(items, name)
    it.packed = (not it.packed) if packed is None else bool(packed)
    return it


def progress(items: list[Item]) -> dict:
    packed = sum(1 for i in items if i.packed)
    return {"packed": packed, "total": len(items),
            "percent": math.floor(packed * 100 / len(items) + 0.5) if items else 0}


def remaining(items: list[Item]) -> list[Item]:
    return [i for i in items if not i.packed]


def filter_items(items: list[Item], query: str) -> list[Item]:
    q = _key(query)
    return [i for i in items if q in _key(i.name) or q in _key(i.category)]


def categories(items: list[Item]) -> list[str]:
    out: list[str] = []
    for i in items:
        if i.category not in out:
            out.append(i.category)
    return out


def to_markdown(items: list[Item], title: str = "Packing list") -> str:
    """Markdown checkboxes grouped by category; ``parse_markdown`` reads it back."""
    lines = [f"# {title}", ""]
    for c in categories(items):
        lines.append(f"## {c}")
        lines += [f'- [{"x" if i.packed else " "}] {i.label}' for i in items if i.category == c]
        lines.append("")
    return "\n".join(lines)


def to_text(items: list[Item]) -> str:
    return "\n".join("\n".join([c] + [f'  {"[x]" if i.packed else "[ ]"} {i.label}' for i in items if i.category == c])
                     for c in categories(items))


_H1 = re.compile(r"^#\s+(.+)$")
_H2 = re.compile(r"^##\s+(.+)$")
_BOX = re.compile(r"^[-*]\s+\[([ xX])\]\s+(.+)$")
_QTY = re.compile(r"^(\d+)\s*[×x]\s+(.+)$")


def parse_markdown(md: str) -> tuple[str, list[Item]]:
    title, category, items = "Packing list", "Items", []
    for raw in str(md).splitlines():
        line = raw.strip()
        if m := _H1.match(line):
            title = m.group(1).strip()
        elif m := _H2.match(line):
            category = m.group(1).strip()
        elif m := _BOX.match(line):
            name, qty = m.group(2).strip(), 1
            if q := _QTY.match(name):
                qty, name = int(q.group(1)), q.group(2).strip()
            items.append(Item(name, category, qty, m.group(1) != " "))
    return title, items
