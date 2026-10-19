use gopromotional_siplog::*;
use std::process::Command;

#[test]
fn goal_estimate() {
    assert_eq!(daily_goal(70.0, 0.0, false).unwrap(), 2300);
    assert_eq!(daily_goal(70.0, 30.0, true).unwrap(), 3150);
    assert_eq!(daily_goal(82.5, 45.0, false).unwrap(), 3250);
    assert!(daily_goal(0.0, 0.0, false).is_err());
}

#[test]
fn amounts() {
    for (s, ml) in [("500ml", 500), ("0.5l", 500), ("1.5 L", 1500), ("33cl", 330), ("330", 330), ("16oz", 473)] {
        assert_eq!(parse_amount(s).unwrap(), ml, "{s}");
    }
    assert!(parse_amount("a glass").is_err());
    assert!(parse_amount("0").is_err());
    assert!(parse_amount("5 gallons").is_err());
}

#[test]
fn totals_undo_progress() {
    let mut log = HydrationLog::new(2000);
    for a in ["250", "500ml", "33cl"] { log.add(a, "2026-10-19").unwrap(); }
    log.add("900", "2026-10-18").unwrap();
    assert_eq!(log.total("2026-10-19").unwrap(), 1080);
    assert_eq!(log.undo("2026-10-19").unwrap(), Some(Entry { date: "2026-10-19".into(), ml: 330 }));
    let p = log.progress("2026-10-19").unwrap();
    assert_eq!((p.total, p.goal, p.percent, p.remaining, p.reached), (750, 2000, 38, 1250, false));
    assert_eq!(log.undo("2026-10-01").unwrap(), None);
    assert_eq!(log.clear_day("2026-10-18").unwrap(), 1);
}

#[test]
fn week_average_streak_json() {
    let mut log = HydrationLog::new(2000);
    for d in ["2026-10-16", "2026-10-17", "2026-10-18"] { log.add("2100", d).unwrap(); }
    log.add("500", "2026-10-19").unwrap();
    let w = log.week("2026-10-19").unwrap();
    assert_eq!((w.len(), w[0].total, w[3].date.as_str(), w[3].total), (7, 500, "2026-10-16", 2100));
    assert_eq!(log.streak("2026-10-19").unwrap(), 3);
    log.add("1500", "2026-10-19").unwrap();
    assert_eq!(log.streak("2026-10-19").unwrap(), 4);
    assert_eq!(log.average("2026-10-19", 7).unwrap(), 1186);
    // Files written by the npm/PyPI versions load here too.
    let back = HydrationLog::from_json(r#"{"goal": 2500, "entries": [{"date": "2026-10-19", "ml": 400}]}"#).unwrap();
    assert_eq!((back.goal, back.total("2026-10-19").unwrap()), (2500, 400));
    assert_eq!(HydrationLog::from_json(&back.to_json()).unwrap(), back);
}

#[test]
fn cli_file_roundtrip() {
    let dir = std::env::temp_dir().join(format!("siplog-test-{}", std::process::id()));
    std::fs::create_dir_all(&dir).unwrap();
    let file = dir.join("log.json");
    let _ = std::fs::remove_file(&file);
    let bin = env!("CARGO_BIN_EXE_gopromotional-siplog");
    let f = file.to_str().unwrap();
    for a in ["500ml", "0.25l"] { assert!(Command::new(bin).args(["add", a, "--date", "2026-10-19", "--file", f]).status().unwrap().success()); }
    let out = Command::new(bin).args(["status", "--date", "2026-10-19", "--file", f, "--json"]).output().unwrap();
    let v: serde_json::Value = serde_json::from_slice(&out.stdout).unwrap();
    assert_eq!(v["total"], 750);
    let out = Command::new(bin).args(["goal", "--weight", "70", "--activity", "30", "--hot"]).output().unwrap();
    assert!(String::from_utf8_lossy(&out.stdout).contains("3150 ml"));
    let out = Command::new(bin).args(["undo", "--date", "2026-10-19", "--file", f]).output().unwrap();
    assert!(String::from_utf8_lossy(&out.stdout).contains("Removed 250 ml"));
}
