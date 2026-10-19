import json

import pytest

import gopromotional_mugday as md
from gopromotional_mugday.cli import main


def sample():
    log = md.CaffeineLog()
    log.add("flat-white", time="2026-10-19T08:00", reusable=True)
    log.add("tea", time="2026-10-19T11:00", reusable=True)
    log.add("Filter coffee", time="2026-10-19T14:00")
    return log


def test_drinks():
    assert len(md.DRINKS) == 16
    assert md.lookup_drink("Flat White") == {"drink": "flat-white", "mg": 126, "ml": 220}
    with pytest.raises(ValueError, match="Unknown drink"):
        md.lookup_drink("mystery")


def test_day_totals():
    log = sample()
    assert log.day("2026-10-19") == {"date": "2026-10-19", "drinks": 3, "total": 341, "limit": 400,
                                     "remaining": 59, "over": 0}
    assert log.day("2026-10-19", md.LIMITS["pregnant"])["over"] == 141
    assert log.day("2026-10-18")["total"] == 0


def test_in_system():
    log = sample()
    assert log.in_system("2026-10-19T07:59") == 0
    assert log.in_system("2026-10-19T14:00") == 244
    assert log.in_system("2026-10-19T22:00") == 81


def test_sleep_ready():
    log = sample()
    assert log.sleep_ready(50) == "2026-10-20T01:30"
    assert log.sleep_ready(100) == "2026-10-19T20:30"
    assert log.sleep_ready(500) == "2026-10-19T14:00"
    assert md.CaffeineLog().sleep_ready() is None


def test_custom_undo_cups_json():
    log = sample()
    log.add("Office machine", time="2026-10-12T09:00", mg=90)
    assert log.cups("2026-10-19") == {"reusable": 2, "disposable": 1, "rate": 67}
    assert log.cups("2026-10-12") == {"reusable": 0, "disposable": 1, "rate": 0}
    assert log.undo().drink == "filter-coffee"
    back = md.CaffeineLog.from_json(log.to_json())
    assert back.day("2026-10-19")["total"] == 201
    with pytest.raises(ValueError):
        log.add("espresso", time="2026-13-01T08:00")


def test_cli(tmp_path, capsys):
    f = str(tmp_path / "log.json")
    main(["add", "flat-white", "--time", "2026-10-19T08:00", "--reusable", "--file", f])
    main(["add", "tea", "--time", "2026-10-19T11:00", "--reusable", "--file", f])
    capsys.readouterr()
    assert main(["add", "filter-coffee", "--time", "2026-10-19T14:00", "--file", f]) == 0
    assert capsys.readouterr().out.strip() == "Logged filter-coffee (140 mg). Today: 341/400 mg."
    main(["today", "--date", "2026-10-19", "--limit", "200", "--file", f])
    assert "141 mg over the 200 mg limit" in capsys.readouterr().out
    main(["sleep-ready", "--file", f, "--json"])
    assert json.loads(capsys.readouterr().out)["time"] == "2026-10-20T01:30"
    main(["in-system", "--at", "2026-10-19T22:00", "--file", f])
    assert "About 81 mg" in capsys.readouterr().out
    assert main(["add", "mystery", "--file", f]) == 2
