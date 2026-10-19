"""Event planning helpers: countdown, planning timeline, giveaway quantities and order-by dates."""
from __future__ import annotations

import math
import re
from dataclasses import dataclass
from datetime import date, timedelta
from typing import Iterable, Optional, Union

DateLike = Union[str, date]


def to_date(value: DateLike) -> date:
    """Accept a ``datetime.date`` or an ISO ``YYYY-MM-DD`` string."""
    if isinstance(value, date):
        return value
    s = str(value).strip()
    if not re.fullmatch(r"\d{4}-\d{2}-\d{2}", s):
        raise ValueError(f'Invalid date "{value}" (expected YYYY-MM-DD)')
    try:
        return date.fromisoformat(s)
    except ValueError as exc:
        raise ValueError(f'Invalid calendar date "{s}"') from exc


@dataclass(frozen=True)
class TimelineItem:
    offset_days: int
    category: str
    task: str


TIMELINE_TEMPLATE: tuple[TimelineItem, ...] = (
    TimelineItem(56, "planning", "Confirm budget and attendee estimate"),
    TimelineItem(49, "venue", "Book the venue"),
    TimelineItem(42, "merchandise", "Order branded merchandise and giveaways"),
    TimelineItem(35, "comms", "Send invitations or open registration"),
    TimelineItem(28, "programme", "Confirm speakers and agenda"),
    TimelineItem(21, "venue", "Arrange catering"),
    TimelineItem(14, "merchandise", "Approve print proofs and signage"),
    TimelineItem(7, "comms", "Send a reminder to attendees"),
    TimelineItem(3, "logistics", "Confirm deliveries and the staffing rota"),
    TimelineItem(1, "logistics", "Pack the event kit and giveaways"),
    TimelineItem(0, "logistics", "Event day: set up the stand and registration"),
    TimelineItem(-2, "comms", "Send thank-you and follow-up messages"),
)


def countdown(event_date: DateLike, today: Optional[DateLike] = None) -> dict:
    """Days until an event, split into weeks and days."""
    ev = to_date(event_date)
    now = to_date(today) if today is not None else date.today()
    days = (ev - now).days
    return {
        "event_date": ev.isoformat(),
        "today": now.isoformat(),
        "days": days,
        "weeks": abs(days) // 7,
        "remainder_days": abs(days) % 7,
        "status": "upcoming" if days > 0 else "today" if days == 0 else "past",
    }


def timeline(event_date: DateLike, today: Optional[DateLike] = None,
             template: Iterable[TimelineItem] = TIMELINE_TEMPLATE) -> list[dict]:
    """Planning tasks with due dates, sorted by date. With ``today`` each row gets ``days_left``/``overdue``."""
    ev = to_date(event_date)
    now = to_date(today) if today is not None else None
    rows = []
    for item in template:
        due = ev - timedelta(days=item.offset_days)
        row = {"task": item.task, "category": item.category, "offset_days": item.offset_days, "due": due.isoformat()}
        if now is not None:
            row["days_left"] = (due - now).days
            row["overdue"] = due < now
        rows.append(row)
    rows.sort(key=lambda r: r["due"])
    return rows


def _ceil_clean(x: float) -> int:
    # Remove binary noise (e.g. 300.00000000000006) before rounding up.
    return math.ceil(round(x * 1e6) / 1e6)


def giveaway_quantity(attendees: float, days: float = 1, uptake: float = 0.6,
                      per_person: float = 1, buffer: float = 0.1) -> dict:
    """Estimate giveaway items: attendees × days × uptake × per_person, plus a buffer (both rounded up)."""
    for name, v in (("attendees", attendees), ("days", days), ("uptake", uptake),
                    ("per_person", per_person), ("buffer", buffer)):
        if not isinstance(v, (int, float)) or isinstance(v, bool) or not math.isfinite(v) or v < 0:
            raise ValueError(f"{name} must be a non-negative number")
    if uptake > 1:
        raise ValueError("uptake must be between 0 and 1")
    base = _ceil_clean(attendees * days * uptake * per_person)
    extra = _ceil_clean(base * buffer)
    return {"base": base, "buffer": extra, "total": base + extra}


def subtract_working_days(day: DateLike, n: int) -> date:
    """Step back ``n`` working days (Monday–Friday)."""
    d = to_date(day)
    left = n
    while left > 0:
        d -= timedelta(days=1)
        if d.isoweekday() <= 5:
            left -= 1
    return d


def order_by_date(event_date: DateLike, production_days: int = 10, delivery_days: int = 3,
                  safety_days: int = 2, today: Optional[DateLike] = None) -> dict:
    """Latest order date for printed merchandise: production + delivery + safety, in working days."""
    for name, v in (("production_days", production_days), ("delivery_days", delivery_days),
                    ("safety_days", safety_days)):
        if not isinstance(v, int) or isinstance(v, bool) or v < 0:
            raise ValueError(f"{name} must be a non-negative integer")
    working = production_days + delivery_days + safety_days
    by = subtract_working_days(event_date, working)
    out = {"order_by": by.isoformat(), "working_days": working}
    if today is not None:
        out["days_from_today"] = (by - to_date(today)).days
        out["late"] = out["days_from_today"] < 0
    return out
