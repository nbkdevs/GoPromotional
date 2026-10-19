import json

import pytest

import gopromotional_packlist as pl
from gopromotional_packlist.cli import main


def test_template_sizes():
    assert {k: len(v) for k, v in pl.TEMPLATES.items()} == {"work-day": 10, "travel": 19, "trade-show": 16, "gym": 9}


def test_per_night_quantities():
    items = pl.build_list(["travel"], nights=3)
    assert pl.find(items, "T-shirts").qty == 4
    assert pl.find(items, "Jumper").qty == 1
    assert pl.find(pl.build_list(["travel"]), "Socks").qty == 1


def test_merge_and_extras():
    items = pl.build_list(["work-day", "travel"], extras=["Umbrella", "laptop"])
    assert len(items) == 28
    assert [i.name for i in items].count("Phone charger") == 1
    assert pl.find(items, "Phone charger").category == "Work"
    assert items[-1] == pl.Item("Umbrella", "Extras", 1, False)
    with pytest.raises(ValueError, match="Unknown template"):
        pl.build_list(["moon"])


def test_mark_progress_filter():
    items = pl.build_list(["gym"])
    pl.mark(items, "towel", True)
    pl.mark(items, "Trainers")
    assert pl.progress(items) == {"packed": 2, "total": 9, "percent": 22}
    assert len(pl.remaining(items)) == 7
    assert [i.name for i in pl.filter_items(items, "sh")] == ["Shower gel"]
    with pytest.raises(ValueError, match="matches"):
        pl.mark(items, "e")
    with pytest.raises(ValueError, match="No item"):
        pl.mark(items, "kayak")


def test_markdown_round_trip():
    items = pl.build_list(["travel"], nights=2)
    pl.mark(items, "Passport / ID", True)
    md = pl.to_markdown(items, "Lisbon")
    assert md.startswith("# Lisbon\n\n## Documents\n- [x] Passport / ID\n")
    assert "- [ ] 3 × T-shirts" in md
    title, back = pl.parse_markdown(md)
    assert title == "Lisbon" and back == items


def test_cli(tmp_path, capsys):
    f = tmp_path / "show.md"
    assert main(["new", "--template", "trade-show", "--extra", "Tote bags", "--title", "Expo", "--out", str(f)]) == 0
    assert capsys.readouterr().out.strip() == f"Wrote 17 items to {f}"
    assert main(["check", str(f), "gaffer"]) == 0
    assert capsys.readouterr().out.strip() == "1/17 packed (6%)"
    assert main(["status", str(f), "--json"]) == 0
    assert len(json.loads(capsys.readouterr().out)["remaining"]) == 16
    assert "- [x] Gaffer tape" in f.read_text(encoding="utf-8")
    assert main(["check", str(f), "zzz"]) == 2
