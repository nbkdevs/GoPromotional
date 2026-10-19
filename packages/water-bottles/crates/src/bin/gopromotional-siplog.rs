use gopromotional_siplog::{daily_goal, date, parse_amount, HydrationLog};
use std::collections::HashMap;
use std::path::PathBuf;
use std::process::ExitCode;

const HELP: &str = "gopromotional-siplog: daily water goal calculator and hydration log

Usage:
  gopromotional-siplog goal --weight KG [--activity MIN] [--hot]
  gopromotional-siplog add <amount> [--date YYYY-MM-DD]     e.g. 250, 500ml, 0.5l, 33cl, 16oz
  gopromotional-siplog status [--date YYYY-MM-DD]
  gopromotional-siplog week [--date YYYY-MM-DD]
  gopromotional-siplog undo [--date YYYY-MM-DD]
  gopromotional-siplog set-goal <ml>

Options:
  --file PATH  log file (default $GOPROMOTIONAL_SIPLOG_FILE or ~/.gopromotional-siplog.json)
  --json       print JSON
Dates default to today (UTC).";

fn default_file() -> PathBuf {
    if let Ok(p) = std::env::var("GOPROMOTIONAL_SIPLOG_FILE") { if !p.is_empty() { return p.into(); } }
    let home = std::env::var("HOME").or_else(|_| std::env::var("USERPROFILE")).unwrap_or_else(|_| ".".into());
    PathBuf::from(home).join(".gopromotional-siplog.json")
}
fn json<T: serde::Serialize>(v: &T) -> String { serde_json::to_string_pretty(v).unwrap_or_default() }

fn run(args: Vec<String>) -> Result<String, String> {
    let mut pos = Vec::new();
    let mut opt: HashMap<String, String> = HashMap::new();
    let mut it = args.into_iter();
    while let Some(a) = it.next() {
        match a.as_str() {
            "--json" | "--hot" | "--help" | "-h" | "--version" | "-V" => { opt.insert(a.trim_start_matches('-').into(), String::new()); }
            s if s.starts_with("--") => { let v = it.next().ok_or(format!("{s} needs a value"))?; opt.insert(s[2..].into(), v); }
            _ => pos.push(a),
        }
    }
    if opt.contains_key("version") || opt.contains_key("V") { return Ok(env!("CARGO_PKG_VERSION").into()); }
    if pos.is_empty() || opt.contains_key("help") || opt.contains_key("h") { return Ok(HELP.into()); }
    let as_json = opt.contains_key("json");
    let day = opt.get("date").cloned().unwrap_or_else(|| date::format_day(date::today()));
    if pos[0] == "goal" {
        let w: f64 = opt.get("weight").ok_or("--weight is required")?.parse().map_err(|_| "--weight must be a number")?;
        let a: f64 = opt.get("activity").map(|s| s.parse().map_err(|_| "--activity must be a number")).transpose()?.unwrap_or(0.0);
        let g = daily_goal(w, a, opt.contains_key("hot"))?;
        return Ok(if as_json { format!("{{\n  \"goal\": {g}\n}}") } else { format!("Suggested daily water goal: {g} ml (a general guide, not medical advice).") });
    }
    let file = opt.get("file").map(PathBuf::from).unwrap_or_else(default_file);
    let mut log = if file.exists() { HydrationLog::from_json(&std::fs::read_to_string(&file).map_err(|e| e.to_string())?)? } else { HydrationLog::default() };
    let save = |log: &HydrationLog| std::fs::write(&file, log.to_json() + "\n").map_err(|e| e.to_string());
    match pos[0].as_str() {
        "add" => {
            let amount = pos.get(1).ok_or("add needs an amount")?;
            let e = log.add(amount, &day)?;
            save(&log)?;
            let p = log.progress(&day)?;
            Ok(if as_json { json(&p) } else { format!("Added {} ml. {}/{} ml ({}%).", e.ml, p.total, p.goal, p.percent) })
        }
        "status" => {
            let p = log.progress(&day)?;
            if as_json { return Ok(json(&p)); }
            let filled = ((p.percent as f64 / 5.0).round() as usize).min(20);
            Ok(format!("{}: {}/{} ml [{}{}] {}%{}\nStreak: {} day(s)", p.date, p.total, p.goal, "#".repeat(filled), ".".repeat(20 - filled), p.percent,
                if p.reached { " - goal reached".to_string() } else { format!(" - {} ml to go", p.remaining) }, log.streak(&day)?))
        }
        "week" => {
            let w = log.week(&day)?;
            if as_json { return Ok(json(&w)); }
            let mut s: Vec<String> = w.iter().map(|d| format!("{}  {:>5} ml  {}", d.date, d.total, if d.total >= log.goal { "goal" } else { "" })).collect();
            s.push(format!("Average: {} ml/day", log.average(&day, 7)?));
            Ok(s.join("\n"))
        }
        "undo" => {
            let e = log.undo(&day)?;
            save(&log)?;
            Ok(if as_json { json(&e) } else { e.map(|e| format!("Removed {} ml from {}.", e.ml, e.date)).unwrap_or_else(|| "Nothing to undo.".into()) })
        }
        "set-goal" => {
            let g = parse_amount(pos.get(1).ok_or("set-goal needs a value")?)?;
            if !(500..=6000).contains(&g) { return Err("goal must be between 500 and 6000 ml".into()); }
            log.goal = g;
            save(&log)?;
            Ok(if as_json { format!("{{\n  \"goal\": {g}\n}}") } else { format!("Daily goal set to {g} ml.") })
        }
        other => Err(format!("Unknown command \"{other}\". Run with --help.")),
    }
}

fn main() -> ExitCode {
    match run(std::env::args().skip(1).collect()) {
        Ok(s) => { println!("{s}"); ExitCode::SUCCESS }
        Err(e) => { eprintln!("Error: {e}"); ExitCode::from(2) }
    }
}
