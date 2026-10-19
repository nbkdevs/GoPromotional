"""Hydration helpers: daily water goal estimate, amount parsing and a day-by-day water log."""
from __future__ import annotations

import json
import math
import re
from dataclasses import asdict, dataclass, field
from datetime import date, timedelta
from typing import Optional, Union

ML_PER_US_FL_OZ = 29.5735
ML_PER_ACTIVE_MINUTE = 12
HOT_WEATHER_ML = 500
_AMOUNT = re.compile(r"^\s*(\d+(?:\.\d+)?)\s*(ml|cl|l|oz)?\s*$", re.IGNORECASE)
_FACTORS = {"ml": 1, "cl": 10, "l": 1000, "oz": ML_PER_US_FL_OZ}


def _round(x: float) -> int:
    return math.floor(x + 0.5)


def _iso(value: Union[str, date, None]) -> str:
    if value is None:
        return date.today().isoformat()
    if isinstance(value, date):
        return value.isoformat()
    try:
        return date.fromisoformat(str(value).strip()).isoformat()
    except ValueError as exc:
        raise ValueError(f'Invalid date "{value}" (expected YYYY-MM-DD)') from exc


def daily_goal(weight_kg: float, activity_minutes: float = 0, hot: bool = False, ml_per_kg: float = 33) -> int:
    """Rough daily water goal in ml (a general guide, not medical advice).

    weight × ml_per_kg + active minutes × 12 ml (+500 ml in hot weather), rounded to the nearest 50 ml.
    """
    if not isinstance(weight_kg, (int, float)) or not (0 < weight_kg <= 400):
        raise ValueError("weight_kg must be between 0 and 400")
    if activity_minutes < 0:
        raise ValueError("activity_minutes must be >= 0")
    raw = weight_kg * ml_per_kg + activity_minutes * ML_PER_ACTIVE_MINUTE + (HOT_WEATHER_ML if hot else 0)
    return math.floor(raw / 50 + 0.5) * 50


def parse_amount(value: Union[str, int, float]) -> int:
    """Parse ``250``, ``"500ml"``, ``"0.5l"``, ``"33cl"`` or ``"16oz"`` into whole millilitres."""
    if isinstance(value, (int, float)) and not isinstance(value, bool):
        if not value > 0:
            raise ValueError("amount must be positive")
        return _round(value)
    m = _AMOUNT.match(str(value))
    if not m:
        raise ValueError(f'Cannot read amount "{value}" (try 250, 500ml, 0.5l, 33cl or 16oz)')
    ml = _round(float(m.group(1)) * _FACTORS[(m.group(2) or "ml").lower()])
    if ml <= 0:
        raise ValueError("amount must be positive")
    return ml


@dataclass
class Entry:
    date: str
    ml: int


@dataclass
class HydrationLog:
    goal: int = 2000
    entries: list = field(default_factory=list)

    def __post_init__(self) -> None:
        self.entries = [e if isinstance(e, Entry) else Entry(_iso(e["date"]), parse_amount(e["ml"]))
                        for e in self.entries]

    @classmethod
    def from_json(cls, data: Union[str, dict]) -> "HydrationLog":
        d = json.loads(data) if isinstance(data, str) else dict(data or {})
        return cls(goal=d.get("goal", 2000), entries=d.get("entries", []))

    def to_dict(self) -> dict:
        return {"goal": self.goal, "entries": [asdict(e) for e in self.entries]}

    def to_json(self) -> str:
        return json.dumps(self.to_dict(), indent=2)

    def add(self, amount, day=None) -> Entry:
        e = Entry(_iso(day), parse_amount(amount))
        self.entries.append(e)
        return e

    def undo(self, day=None) -> Optional[Entry]:
        """Remove the most recent entry for a day."""
        d = _iso(day)
        for i in range(len(self.entries) - 1, -1, -1):
            if self.entries[i].date == d:
                return self.entries.pop(i)
        return None

    def clear_day(self, day=None) -> int:
        d = _iso(day)
        before = len(self.entries)
        self.entries = [e for e in self.entries if e.date != d]
        return before - len(self.entries)

    def total(self, day=None) -> int:
        d = _iso(day)
        return sum(e.ml for e in self.entries if e.date == d)

    def progress(self, day=None, goal: Optional[int] = None) -> dict:
        g = self.goal if goal is None else goal
        d = _iso(day)
        total = self.total(d)
        return {"date": d, "total": total, "goal": g,
                "percent": math.floor(total * 100 / g + 0.5) if g > 0 else 0,
                "remaining": max(0, g - total), "reached": total >= g}

    def week(self, end=None) -> list[dict]:
        """Totals for the 7 days ending on ``end``, newest first."""
        e = date.fromisoformat(_iso(end))
        return [{"date": (e - timedelta(days=i)).isoformat(), "total": self.total(e - timedelta(days=i))}
                for i in range(7)]

    def streak(self, end=None, goal: Optional[int] = None) -> int:
        """Consecutive days meeting the goal, ending today (or yesterday if today is not done yet)."""
        g = self.goal if goal is None else goal
        d = date.fromisoformat(_iso(end))
        if self.total(d) < g:
            d -= timedelta(days=1)
        n = 0
        while n < 3660 and self.total(d) >= g:
            n += 1
            d -= timedelta(days=1)
        return n

    def average(self, end=None, days: int = 7) -> int:
        e = date.fromisoformat(_iso(end))
        return _round(sum(self.total(e - timedelta(days=i)) for i in range(days)) / days)
