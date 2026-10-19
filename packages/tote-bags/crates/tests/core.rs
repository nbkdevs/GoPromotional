use gopromotional_packlist::*;
use std::process::Command;

#[test]
fn template_sizes() {
    let sizes: Vec<(&str, usize)> = TEMPLATES.iter().map(|(n, t)| (*n, t.len())).collect();
    assert_eq!(sizes, vec![("work-day", 10), ("travel", 19), ("trade-show", 16), ("gym", 9)]);
}

#[test]
fn per_night_and_merge() {
    let items = build_list(&["travel"], 3, &[]).unwrap();
    assert_eq!(items[find(&items, "T-shirts").unwrap()].qty, 4);
    assert_eq!(items[find(&items, "Jumper").unwrap()].qty, 1);
    let both = build_list(&["work-day", "travel"], 0, &["Umbrella", "laptop"]).unwrap();
    assert_eq!(both.len(), 28);
    assert_eq!(both.iter().filter(|i| i.name == "Phone charger").count(), 1);
    assert_eq!(both[find(&both, "Phone charger").unwrap()].category, "Work");
    assert_eq!(both.last().unwrap(), &Item::new("Umbrella", "Extras"));
    assert!(build_list(&["moon"], 0, &[]).unwrap_err().contains("Unknown template"));
}

#[test]
fn mark_progress_filter() {
    let mut items = build_list(&["gym"], 0, &[]).unwrap();
    mark(&mut items, "towel", Some(true)).unwrap();
    mark(&mut items, "Trainers", None).unwrap();
    assert_eq!(progress(&items), Progress { packed: 2, total: 9, percent: 22 });
    assert_eq!(remaining(&items).len(), 7);
    assert_eq!(filter_items(&items, "sh").iter().map(|i| i.name.as_str()).collect::<Vec<_>>(), vec!["Shower gel"]);
    assert!(mark(&mut items, "e", None).unwrap_err().contains("matches"));
    assert!(mark(&mut items, "kayak", None).unwrap_err().contains("No item"));
}

#[test]
fn markdown_round_trip() {
    let mut items = build_list(&["travel"], 2, &[]).unwrap();
    mark(&mut items, "Passport / ID", Some(true)).unwrap();
    let md = to_markdown(&items, "Lisbon");
    assert!(md.starts_with("# Lisbon\n\n## Documents\n- [x] Passport / ID\n"));
    assert!(md.contains("- [ ] 3 × T-shirts"));
    let (title, back) = parse_markdown(&md);
    assert_eq!(title, "Lisbon");
    assert_eq!(back, items);
}

#[test]
fn cli_file() {
    let dir = std::env::temp_dir().join(format!("packlist-test-{}", std::process::id()));
    std::fs::create_dir_all(&dir).unwrap();
    let f = dir.join("show.md");
    let fs = f.to_str().unwrap();
    let bin = env!("CARGO_BIN_EXE_gopromotional-packlist");
    let o = Command::new(bin).args(["new", "--template", "trade-show", "--extra", "Tote bags", "--title", "Expo", "--out", fs]).output().unwrap();
    assert_eq!(String::from_utf8_lossy(&o.stdout).trim(), format!("Wrote 17 items to {fs}"));
    let o = Command::new(bin).args(["check", fs, "gaffer"]).output().unwrap();
    assert_eq!(String::from_utf8_lossy(&o.stdout).trim(), "1/17 packed (6%)");
    let o = Command::new(bin).args(["status", fs, "--json"]).output().unwrap();
    let v: serde_json::Value = serde_json::from_slice(&o.stdout).unwrap();
    assert_eq!(v["remaining"].as_array().unwrap().len(), 16);
    assert!(std::fs::read_to_string(&f).unwrap().contains("- [x] Gaffer tape"));
}
