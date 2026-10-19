import json

import pytest

from gopromotional_siplog import HydrationLog, daily_goal, parse_amount
from gopromotional_siplog.cli import main


def test_daily_goal():
    assert daily_goal(70) == 2300
    assert daily_goal(70, activity_minutes=30, hot=True) == 3150
    assert daily_goal(82.5, activity_minutes=45) == 3250
    with pytest.raises(ValueError):
        daily_goal(0)


@pytest.mark.parametrize("raw,ml", [("500ml", 500), ("0.5l", 500), ("1.5 L", 1500), ("33cl", 330),
                                    ("330", 330), ("16oz", 473), (250, 250)])
def test_parse_amount(raw, ml):
    assert parse_amount(raw) == ml


def test_parse_amount_errors():
    with pytest.raises(ValueError):
        parse_amount("a glass")
    with pytest.raises(ValueError):
        parse_amount("0")


def test_log_totals_undo_progress():
    log = HydrationLog(goal=2000)
    log.add(250, "2026-10-19"); log.add("500ml", "2026-10-19"); log.add("33cl", "2026-10-19")
    log.add(900, "2026-10-18")
    assert log.total("2026-10-19") == 1080
    e = log.undo("2026-10-19")
    assert (e.date, e.ml) == ("2026-10-19", 330)
    assert log.progress("2026-10-19") == {"date": "2026-10-19", "total": 750, "goal": 2000, "percent": 38,
                                          "remaining": 1250, "reached": False}
    assert log.undo("2026-10-01") is None
    assert log.clear_day("2026-10-18") == 1


def test_week_average_streak():
    log = HydrationLog(goal=2000)
    for d in ("2026-10-16", "2026-10-17", "2026-10-18"):
        log.add(2100, d)
    log.add(500, "2026-10-19")
    w = log.week("2026-10-19")
    assert len(w) == 7 and w[0] == {"date": "2026-10-19", "total": 500} and w[3] == {"date": "2026-10-16", "total": 2100}
    assert log.streak("2026-10-19") == 3
    log.add(1500, "2026-10-19")
    assert log.streak("2026-10-19") == 4
    assert log.average("2026-10-19") == 1186


def test_json_round_trip():
    log = HydrationLog(goal=2500)
    log.add(400, "2026-10-19")
    back = HydrationLog.from_json(log.to_json())
    assert back.goal == 2500 and back.total("2026-10-19") == 400


def test_cli(tmp_path, capsys):
    f = str(tmp_path / "log.json")
    assert main(["add", "500ml", "--date", "2026-10-19", "--file", f]) == 0
    assert main(["add", "0.25l", "--date", "2026-10-19", "--file", f]) == 0
    capsys.readouterr()
    assert main(["status", "--date", "2026-10-19", "--file", f, "--json"]) == 0
    assert json.loads(capsys.readouterr().out)["total"] == 750
    assert main(["goal", "--weight", "70", "--activity", "30", "--hot"]) == 0
    assert "3150 ml" in capsys.readouterr().out
    assert main(["undo", "--date", "2026-10-19", "--file", f]) == 0
    assert "Removed 250 ml" in capsys.readouterr().out
    assert main(["add", "lots", "--file", f]) == 2
