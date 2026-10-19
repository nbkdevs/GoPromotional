"""Caffeine and coffee intake tracker.

Typical caffeine per drink, daily totals against a limit, an estimate of caffeine still in
your system (half-life model), when it should drop below a "sleep-ready" level, and
reusable-cup stats. Values are typical figures; real drinks vary. Not medical advice.
"""
from __future__ import annotations

import json
import math
import re
from dataclasses import asdict, dataclass, field
from datetime import datetime, timedelta
from typing import Optional, Union

DRINKS: dict[str, dict[str, int]] = {
    "espresso": {"mg": 63, "ml": 30},
    "double-espresso": {"mg": 126, "ml": 60},
    "americano": {"mg": 126, "ml": 350},
    "latte": {"mg": 126, "ml": 350},
    "cappuccino": {"mg": 126, "ml": 350},
    "flat-white": {"mg": 126, "ml": 220},
    "filter-coffee": {"mg": 140, "ml": 300},
    "instant-coffee": {"mg": 100, "ml": 300},
    "cold-brew": {"mg": 155, "ml": 350},
    "decaf-coffee": {"mg": 3, "ml": 300},
    "tea": {"mg": 75, "ml": 300},
    "green-tea": {"mg": 30, "ml": 300},
    "cola": {"mg": 40, "ml": 330},
    "energy-drink": {"mg": 80, "ml": 250},
    "hot-chocolate": {"mg": 5, "ml": 300},
    "dark-chocolate-50g": {"mg": 25, "ml": 0},
}
LIMITS = {"adult": 400, "pregnant": 200, "single_dose": 200}
DEFAULT_HALF_LIFE_HOURS = 5
_TIME = re.compile(r"^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})$")
_EPOCH = datetime(1970, 1, 1)


def _round(x: float) -> int:
    return math.floor(x + 0.5)


def _norm(name: str) -> str:
    return re.sub(r"[\s_]+", "-", str(name).strip().lower())


def to_minutes(value: Union[str, datetime]) -> int:
    """``YYYY-MM-DDTHH:MM`` (naive local time) to minutes since 1970-01-01."""
    if isinstance(value, datetime):
        dt = value.replace(second=0, microsecond=0, tzinfo=None)
    else:
        m = _TIME.match(str(value).strip())
        if not m:
            raise ValueError(f'Invalid time "{value}" (expected YYYY-MM-DDTHH:MM)')
        try:
            dt = datetime(*map(int, m.groups()))
        except ValueError as exc:
            raise ValueError(f'Invalid time "{value}"') from exc
    return int((dt - _EPOCH).total_seconds() // 60)


def from_minutes(minutes: int) -> str:
    return (_EPOCH + timedelta(minutes=minutes)).strftime("%Y-%m-%dT%H:%M")


def now_local() -> str:
    return datetime.now().strftime("%Y-%m-%dT%H:%M")


def lookup_drink(name: str) -> dict:
    k = _norm(name)
    if k not in DRINKS:
        raise ValueError(f'Unknown drink "{name}". Known: {", ".join(DRINKS)} (or pass mg yourself)')
    return {"drink": k, **DRINKS[k]}


@dataclass
class Drink:
    drink: str
    mg: float
    time: str
    reusable: bool = False


@dataclass
class CaffeineLog:
    limit: int = LIMITS["adult"]
    half_life_hours: float = DEFAULT_HALF_LIFE_HOURS
    entries: list = field(default_factory=list)

    def __post_init__(self) -> None:
        raw, self.entries = self.entries, []
        for e in raw:
            e = asdict(e) if isinstance(e, Drink) else e
            self.add(e["drink"], time=e["time"], mg=e.get("mg"), reusable=e.get("reusable", False))

    @classmethod
    def from_json(cls, data: Union[str, dict]) -> "CaffeineLog":
        d = json.loads(data) if isinstance(data, str) else dict(data or {})
        return cls(limit=d.get("limit", LIMITS["adult"]),
                   half_life_hours=d.get("halfLifeHours", d.get("half_life_hours", DEFAULT_HALF_LIFE_HOURS)),
                   entries=d.get("entries", []))

    def to_dict(self) -> dict:
        # camelCase keys keep the file compatible with the npm/Ruby/Rust versions.
        return {"limit": self.limit, "halfLifeHours": self.half_life_hours, "entries": [asdict(e) for e in self.entries]}

    def to_json(self) -> str:
        return json.dumps(self.to_dict(), indent=2)

    def add(self, drink: str, time: Optional[str] = None, mg: Optional[float] = None, reusable: bool = False) -> Drink:
        """Log a drink. ``mg`` overrides the typical value (handy for unknown drinks)."""
        name = _norm(drink)
        if mg is None:
            info = lookup_drink(drink)
            name, mg = info["drink"], info["mg"]
        if not isinstance(mg, (int, float)) or isinstance(mg, bool) or not 0 <= mg <= 1000:
            raise ValueError("mg must be between 0 and 1000")
        e = Drink(name, mg, from_minutes(to_minutes(time or now_local())), bool(reusable))
        self.entries.append(e)
        self.entries.sort(key=lambda x: to_minutes(x.time))
        return e

    def undo(self) -> Optional[Drink]:
        return self.entries.pop() if self.entries else None

    def for_day(self, day: str) -> list[Drink]:
        return [e for e in self.entries if e.time[:10] == day]

    def day(self, day: Optional[str] = None, limit: Optional[int] = None) -> dict:
        d = day or now_local()[:10]
        lim = self.limit if limit is None else limit
        items = self.for_day(d)
        total = sum(e.mg for e in items)
        return {"date": d, "drinks": len(items), "total": total, "limit": lim,
                "remaining": max(0, lim - total), "over": max(0, total - lim)}

    def _level(self, t: int) -> float:
        s = 0.0
        for e in self.entries:
            dt = t - to_minutes(e.time)
            if dt >= 0:
                s += e.mg * math.pow(0.5, dt / 60 / self.half_life_hours)
        return s

    def in_system(self, at: Optional[str] = None) -> int:
        """Estimated caffeine (mg) still in the body at ``at``."""
        return _round(self._level(to_minutes(at or now_local())))

    def sleep_ready(self, threshold: float = 50) -> Optional[str]:
        """Earliest time (rounded up to 15 min) when caffeine falls below ``threshold`` mg, if nothing more is drunk."""
        if not self.entries:
            return None
        last = to_minutes(self.entries[-1].time)
        level = self._level(last)
        if level <= threshold:
            return from_minutes(last)
        minutes = self.half_life_hours * 60 * math.log2(level / threshold)
        return from_minutes(math.ceil((last + minutes) / 15) * 15)

    def cups(self, end: Optional[str] = None) -> dict:
        """Reusable vs disposable cups over the 7 days ending on ``end``."""
        e_day = to_minutes(f"{end or now_local()[:10]}T00:00") // 1440
        week = [x for x in self.entries if e_day - 7 < to_minutes(f"{x.time[:10]}T00:00") // 1440 <= e_day]
        reusable = sum(1 for x in week if x.reusable)
        return {"reusable": reusable, "disposable": len(week) - reusable,
                "rate": _round(reusable * 100 / len(week)) if week else 0}
