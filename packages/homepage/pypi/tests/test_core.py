import json

import pytest

import gopromotional_event_planner as ep
from gopromotional_event_planner.cli import main


def test_countdown():
    assert ep.countdown("2026-12-03", "2026-10-19") == {
        "event_date": "2026-12-03", "today": "2026-10-19", "days": 45, "weeks": 6,
        "remainder_days": 3, "status": "upcoming"}
    assert ep.countdown("2026-10-19", "2026-10-19")["status"] == "today"
    past = ep.countdown("2026-10-10", "2026-10-19")
    assert past["days"] == -9 and past["status"] == "past"


def test_invalid_dates():
    with pytest.raises(ValueError):
        ep.to_date("2026-02-30")
    with pytest.raises(ValueError):
        ep.to_date("19/10/2026")


def test_timeline():
    t = ep.timeline("2026-12-03", "2026-10-19")
    assert len(t) == 12
    assert t[0]["due"] == "2026-10-08" and t[0]["overdue"] is True
    merch = next(r for r in t if r["task"].startswith("Order branded"))
    assert (merch["due"], merch["days_left"], merch["overdue"]) == ("2026-10-22", 3, False)
    assert t[-1]["due"] == "2026-12-05"


def test_giveaways():
    assert ep.giveaway_quantity(500, days=2, uptake=0.6, buffer=0.1) == {"base": 600, "buffer": 60, "total": 660}
    assert ep.giveaway_quantity(250, uptake=0.35) == {"base": 88, "buffer": 9, "total": 97}
    assert ep.giveaway_quantity(0) == {"base": 0, "buffer": 0, "total": 0}
    with pytest.raises(ValueError):
        ep.giveaway_quantity(10, uptake=1.5)
    with pytest.raises(ValueError):
        ep.giveaway_quantity(-1)


def test_order_by():
    assert ep.order_by_date("2026-12-03", today="2026-10-19") == {
        "order_by": "2026-11-12", "working_days": 15, "days_from_today": 24, "late": False}
    assert ep.order_by_date("2026-10-26", 1, 0, 0)["order_by"] == "2026-10-23"
    assert ep.order_by_date("2026-10-21", today="2026-10-19")["late"] is True


def test_cli(capsys):
    assert main(["countdown", "2026-12-03", "--today", "2026-10-19"]) == 0
    assert "45 days to go (6 weeks 3 days)" in capsys.readouterr().out
    assert main(["giveaways", "--attendees", "500", "--days", "2", "--json"]) == 0
    assert json.loads(capsys.readouterr().out)["total"] == 660
    assert main(["order-by", "2026-12-03", "--today", "2026-10-19"]) == 0
    assert "Order by 2026-11-12" in capsys.readouterr().out
    assert main(["countdown", "nope"]) == 2
