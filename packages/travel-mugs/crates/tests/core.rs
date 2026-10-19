use gopromotional_mugday::*;
use std::process::Command;

fn sample() -> CaffeineLog {
    let mut log = CaffeineLog::default();
    log.add("flat-white", "2026-10-19T08:00", None, true).unwrap();
    log.add("filter coffee", "2026-10-19T14:00", None, false).unwrap();
    log.add("Tea", "2026-10-19T11:00", None, true).unwrap();
    log
}

#[test]
fn drinks_and_times() {
    assert_eq!(DRINKS.len(), 16);
    assert_eq!(lookup_drink("Double Espresso").unwrap(), ("double-espresso".into(), 126.0));
    assert!(lookup_drink("mocha-frappe").is_err());
    assert_eq!(from_minutes(to_minutes("2026-10-19 08:05").unwrap()), "2026-10-19T08:05");
    assert!(to_minutes("2026-13-01T08:00").is_err());
    assert!(to_minutes("2026-10-19T24:00").is_err());
}

#[test]
fn day_totals() {
    let log = sample();
    assert_eq!(log.entries.iter().map(|e| e.drink.as_str()).collect::<Vec<_>>(), vec!["flat-white", "tea", "filter-coffee"]);
    let d = log.day("2026-10-19", None);
    assert_eq!((d.total, d.remaining, d.over), (341.0, 59.0, 0.0));
    assert_eq!(log.day("2026-10-19", Some(LIMIT_PREGNANT)).over, 141.0);
}

#[test]
fn half_life_and_sleep() {
    let log = sample();
    assert_eq!(log.in_system("2026-10-19T07:59").unwrap(), 0);
    assert_eq!(log.in_system("2026-10-19T14:00").unwrap(), 244);
    assert_eq!(log.in_system("2026-10-19T22:00").unwrap(), 81);
    assert_eq!(log.sleep_ready(50.0).unwrap(), "2026-10-20T01:30");
    assert_eq!(log.sleep_ready(100.0).unwrap(), "2026-10-19T20:30");
    assert_eq!(log.sleep_ready(500.0).unwrap(), "2026-10-19T14:00");
    assert_eq!(CaffeineLog::default().sleep_ready(50.0), None);
}

#[test]
fn cups_and_json() {
    let mut log = sample();
    log.add("latte", "2026-10-12T09:00", None, true).unwrap();
    assert_eq!(log.cups("2026-10-19").unwrap(), Cups { reusable: 2, disposable: 1, rate: 67 });
    let back = CaffeineLog::from_json(&log.to_json()).unwrap();
    assert_eq!(back, log);
    assert!(log.to_json().contains("\"halfLifeHours\": 5"));
    assert_eq!(log.undo().unwrap().drink, "filter-coffee");
}

#[test]
fn cli_file() {
    let f = std::env::temp_dir().join(format!("mugday-test-{}.json", std::process::id()));
    let _ = std::fs::remove_file(&f);
    let bin = env!("CARGO_BIN_EXE_gopromotional-mugday");
    let run = |args: &[&str]| { let o = Command::new(bin).args(args).arg("--file").arg(&f).output().unwrap(); String::from_utf8_lossy(&o.stdout).trim().to_string() };
    assert_eq!(run(&["add", "flat", "white", "--time", "2026-10-19T08:00", "--reusable"]), "Logged flat-white (126 mg, reusable cup). Today: 126/400 mg.");
    run(&["add", "tea", "--time", "2026-10-19T11:00", "--reusable"]);
    run(&["add", "filter-coffee", "--time", "2026-10-19T14:00"]);
    assert_eq!(run(&["today", "--date", "2026-10-19"]), "2026-10-19: 341 mg from 3 drink(s); 59 mg left of 400 mg.");
    assert_eq!(run(&["sleep-ready"]), "Below 50 mg from about 2026-10-20 01:30.");
    assert_eq!(run(&["cups", "--date", "2026-10-19"]), "Last 7 days: 2 reusable, 1 disposable (67% reusable).");
    let _ = std::fs::remove_file(&f);
}
