use gopromotional_event_planner::{countdown, date, giveaway_quantity, order_by_date, timeline, GiveawayInput, TIMELINE_TEMPLATE};
use std::collections::HashMap;
use std::process::ExitCode;

const HELP: &str = "gopromotional-event-planner: event countdown, planning timeline, giveaway quantities and order-by dates

Usage:
  gopromotional-event-planner countdown <YYYY-MM-DD> [--today YYYY-MM-DD]
  gopromotional-event-planner timeline  <YYYY-MM-DD> [--today YYYY-MM-DD]
  gopromotional-event-planner giveaways --attendees N [--days 1] [--uptake 0.6] [--per-person 1] [--buffer 0.1]
  gopromotional-event-planner order-by  <YYYY-MM-DD> [--production 10] [--delivery 3] [--safety 2] [--today YYYY-MM-DD]

Options: --json, -h/--help, -V/--version";

fn run(args: Vec<String>) -> Result<String, String> {
    let mut pos = Vec::new();
    let mut opt: HashMap<String, String> = HashMap::new();
    let mut it = args.into_iter();
    while let Some(a) = it.next() {
        match a.as_str() {
            "--json" | "--help" | "-h" | "--version" | "-V" => { opt.insert(a.trim_start_matches('-').to_string(), String::new()); }
            s if s.starts_with("--") => { let v = it.next().ok_or(format!("{s} needs a value"))?; opt.insert(s[2..].to_string(), v); }
            _ => pos.push(a),
        }
    }
    if opt.contains_key("version") || opt.contains_key("V") { return Ok(env!("CARGO_PKG_VERSION").into()); }
    if pos.is_empty() || opt.contains_key("help") || opt.contains_key("h") { return Ok(HELP.into()); }
    let json = opt.contains_key("json");
    let today = opt.get("today").cloned().unwrap_or_else(|| date::format_day(date::today()));
    let f = |k: &str, d: f64| -> Result<f64, String> { opt.get(k).map(|v| v.parse::<f64>().map_err(|_| format!("--{k} must be a number"))).unwrap_or(Ok(d)) };
    let u = |k: &str, d: u32| -> Result<u32, String> { opt.get(k).map(|v| v.parse::<u32>().map_err(|_| format!("--{k} must be a whole number"))).unwrap_or(Ok(d)) };
    let need = |i: usize| pos.get(i).cloned().ok_or_else(|| format!("{} needs an event date", pos[0]));
    let to_json = |v: &dyn erased::Ser| v.json();
    match pos[0].as_str() {
        "countdown" => {
            let r = countdown(&need(1)?, &today)?;
            Ok(if json { to_json(&r) } else if r.status == "today" { format!("{} is today.", r.event_date) }
               else if r.status == "upcoming" { format!("{} days to go ({} weeks {} days) until {}.", r.days, r.weeks, r.remainder_days, r.event_date) }
               else { format!("{} was {} days ago.", r.event_date, -r.days) })
        }
        "timeline" => {
            let r = timeline(&need(1)?, Some(&today), &TIMELINE_TEMPLATE)?;
            Ok(if json { to_json(&r) } else {
                r.iter().map(|x| { let o = x.overdue == Some(true); format!("{} {}  {}{}", if o { "!" } else { " " }, x.due, x.task, if o { "  (overdue)" } else { "" }) })
                    .collect::<Vec<_>>().join("\n")
            })
        }
        "giveaways" => {
            let attendees = opt.get("attendees").ok_or("--attendees is required")?.parse::<f64>().map_err(|_| "--attendees must be a number")?;
            let r = giveaway_quantity(&GiveawayInput { attendees, days: f("days", 1.0)?, uptake: f("uptake", 0.6)?, per_person: f("per-person", 1.0)?, buffer: f("buffer", 0.1)? })?;
            Ok(if json { to_json(&r) } else { format!("Order {} items ({} expected + {} buffer).", r.total, r.base, r.buffer) })
        }
        "order-by" => {
            let r = order_by_date(&need(1)?, u("production", 10)?, u("delivery", 3)?, u("safety", 2)?, Some(&today))?;
            let d = r.days_from_today.unwrap_or(0);
            Ok(if json { to_json(&r) } else if d < 0 {
                format!("Order by {} ({} working days before the event) - that is {} days ago, so ask about express production.", r.order_by, r.working_days, -d)
            } else { format!("Order by {} ({} working days before the event), {} days from today.", r.order_by, r.working_days, d) })
        }
        other => Err(format!("Unknown command \"{other}\". Run with --help.")),
    }
}

mod erased {
    pub trait Ser { fn json(&self) -> String; }
    impl<T: serde::Serialize> Ser for T { fn json(&self) -> String { serde_json::to_string_pretty(self).unwrap_or_default() } }
}

fn main() -> ExitCode {
    match run(std::env::args().skip(1).collect()) {
        Ok(s) => { println!("{s}"); ExitCode::SUCCESS }
        Err(e) => { eprintln!("Error: {e}"); ExitCode::from(2) }
    }
}
