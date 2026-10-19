use gopromotional_event_planner::*;

#[test]
fn countdown_splits_weeks() {
    let c = countdown("2026-12-03", "2026-10-19").unwrap();
    assert_eq!((c.days, c.weeks, c.remainder_days, c.status), (45, 6, 3, "upcoming"));
    assert_eq!(countdown("2026-10-19", "2026-10-19").unwrap().status, "today");
    let p = countdown("2026-10-10", "2026-10-19").unwrap();
    assert_eq!((p.days, p.status), (-9, "past"));
}

#[test]
fn rejects_bad_dates() {
    assert!(date::parse_day("2026-02-30").is_err());
    assert!(date::parse_day("19/10/2026").is_err());
    assert_eq!(date::weekday(date::parse_day("2026-10-19").unwrap()), 1);
    assert_eq!(date::weekday(date::parse_day("2026-10-25").unwrap()), 7);
    assert_eq!(date::format_day(date::parse_day("2024-02-29").unwrap()), "2024-02-29");
}

#[test]
fn timeline_dates() {
    let t = timeline("2026-12-03", Some("2026-10-19"), &TIMELINE_TEMPLATE).unwrap();
    assert_eq!(t.len(), 12);
    assert_eq!((t[0].due.as_str(), t[0].overdue), ("2026-10-08", Some(true)));
    let m = t.iter().find(|r| r.task.starts_with("Order branded")).unwrap();
    assert_eq!((m.due.as_str(), m.days_left, m.overdue), ("2026-10-22", Some(3), Some(false)));
    assert_eq!(t.last().unwrap().due, "2026-12-05");
}

#[test]
fn giveaways_round_up() {
    let g = |attendees, days, uptake| giveaway_quantity(&GiveawayInput { attendees, days, uptake, ..Default::default() });
    assert_eq!(g(500.0, 2.0, 0.6).unwrap(), Giveaways { base: 600, buffer: 60, total: 660 });
    assert_eq!(g(250.0, 1.0, 0.35).unwrap(), Giveaways { base: 88, buffer: 9, total: 97 });
    assert_eq!(g(0.0, 1.0, 0.6).unwrap().total, 0);
    assert!(g(10.0, 1.0, 1.5).is_err());
    assert!(g(-1.0, 1.0, 0.6).is_err());
}

#[test]
fn order_by_skips_weekends() {
    let o = order_by_date("2026-12-03", 10, 3, 2, Some("2026-10-19")).unwrap();
    assert_eq!((o.order_by.as_str(), o.working_days, o.days_from_today, o.late), ("2026-11-12", 15, Some(24), Some(false)));
    assert_eq!(order_by_date("2026-10-26", 1, 0, 0, None).unwrap().order_by, "2026-10-23");
    assert_eq!(order_by_date("2026-10-21", 10, 3, 2, Some("2026-10-19")).unwrap().late, Some(true));
}

#[test]
fn cli_binary() {
    let out = std::process::Command::new(env!("CARGO_BIN_EXE_gopromotional-event-planner"))
        .args(["countdown", "2026-12-03", "--today", "2026-10-19"]).output().unwrap();
    assert!(String::from_utf8_lossy(&out.stdout).contains("45 days to go (6 weeks 3 days)"));
    let out = std::process::Command::new(env!("CARGO_BIN_EXE_gopromotional-event-planner"))
        .args(["giveaways", "--attendees", "500", "--days", "2", "--json"]).output().unwrap();
    let v: serde_json::Value = serde_json::from_slice(&out.stdout).unwrap();
    assert_eq!(v["total"], 660);
    let bad = std::process::Command::new(env!("CARGO_BIN_EXE_gopromotional-event-planner")).arg("nope").output().unwrap();
    assert_eq!(bad.status.code(), Some(2));
}
