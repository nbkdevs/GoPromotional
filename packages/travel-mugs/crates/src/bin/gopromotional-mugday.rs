use gopromotional_mugday::*;
use std::path::PathBuf;
use std::process::ExitCode;

const HELP: &str = "gopromotional-mugday: caffeine and coffee intake tracker

Usage:
  gopromotional-mugday drinks                     typical caffeine per drink
  gopromotional-mugday add <drink> [--time T] [--mg N] [--reusable]
  gopromotional-mugday today [--date D] [--limit N]
  gopromotional-mugday in-system [--at T]
  gopromotional-mugday sleep-ready [--threshold N]   (default 50 mg)
  gopromotional-mugday cups [--date D]            reusable vs disposable, last 7 days
  gopromotional-mugday undo
Options: --file PATH (default $GOPROMOTIONAL_MUGDAY_FILE or ~/.gopromotional-mugday.json), --json
Times are YYYY-MM-DDTHH:MM; the default \"now\" is UTC. Values are typical figures; not medical advice.";

fn default_file() -> PathBuf {
    if let Some(f) = std::env::var_os("GOPROMOTIONAL_MUGDAY_FILE").filter(|f| !f.is_empty()) { return f.into(); }
    let home = std::env::var_os("HOME").or_else(|| std::env::var_os("USERPROFILE")).unwrap_or_default();
    PathBuf::from(home).join(".gopromotional-mugday.json")
}

fn run(args: Vec<String>) -> Result<String, String> {
    let mut pos = Vec::new();
    let (mut file, mut json, mut reusable) = (None, false, false);
    let (mut time, mut mg, mut date, mut limit, mut at, mut threshold) = (None, None, None, None, None, 50.0);
    let num = |s: String, f: &str| s.parse::<f64>().map_err(|_| format!("{f} must be a number"));
    let mut it = args.into_iter();
    while let Some(a) = it.next() {
        let mut val = || it.next().ok_or(format!("{a} needs a value"));
        match a.as_str() {
            "--json" => json = true,
            "--reusable" => reusable = true,
            "-h" | "--help" => return Ok(HELP.into()),
            "-v" | "--version" => return Ok(env!("CARGO_PKG_VERSION").into()),
            "--file" => file = Some(PathBuf::from(val()?)),
            "--time" => time = Some(val()?),
            "--mg" => mg = Some(num(val()?, "--mg")?),
            "--date" => date = Some(val()?),
            "--limit" => limit = Some(num(val()?, "--limit")?),
            "--at" => at = Some(val()?),
            "--threshold" => threshold = num(val()?, "--threshold")?,
            _ => pos.push(a),
        }
    }
    let Some(cmd) = pos.first().cloned() else { return Ok(HELP.into()) };
    if cmd == "drinks" {
        return Ok(if json {
            let m: serde_json::Map<String, serde_json::Value> = DRINKS.iter().map(|d| (d.0.to_string(), serde_json::json!({"mg": d.1, "ml": d.2}))).collect();
            serde_json::to_string_pretty(&m).unwrap()
        } else {
            DRINKS.iter().map(|d| format!("{:<20} {:>4} mg{}", d.0, d.1, if d.2 > 0 { format!("  ({} ml)", d.2) } else { String::new() })).collect::<Vec<_>>().join("\n")
        });
    }
    let path = file.unwrap_or_else(default_file);
    let mut log = if path.exists() { CaffeineLog::from_json(&std::fs::read_to_string(&path).map_err(|e| e.to_string())?)? } else { CaffeineLog::default() };
    let save = |log: &CaffeineLog| std::fs::write(&path, log.to_json() + "\n").map_err(|e| e.to_string());
    let today = || now()[..10].to_string();
    let (value, text): (serde_json::Value, String) = match cmd.as_str() {
        "add" => {
            let name = pos[1..].join(" ");
            if name.is_empty() { return Err("add needs a drink name (see `drinks`)".into()); }
            let e = log.add(&name, &time.unwrap_or_else(now), mg, reusable)?;
            save(&log)?;
            let r = log.day(&e.time[..10], None);
            (serde_json::to_value(&e).unwrap(), format!("Logged {} ({} mg{}). Today: {}/{} mg.", e.drink, fmt_num(e.mg), if e.reusable { ", reusable cup" } else { "" }, fmt_num(r.total), fmt_num(r.limit)))
        }
        "today" => {
            let r = log.day(&date.unwrap_or_else(today), limit);
            let tail = if r.over > 0.0 { format!("{} mg over the {} mg limit.", fmt_num(r.over), fmt_num(r.limit)) } else { format!("{} mg left of {} mg.", fmt_num(r.remaining), fmt_num(r.limit)) };
            let text = format!("{}: {} mg from {} drink(s); {tail}", r.date, fmt_num(r.total), r.drinks);
            (serde_json::to_value(&r).unwrap(), text)
        }
        "in-system" => {
            let at = at.unwrap_or_else(now);
            let v = log.in_system(&at)?;
            (serde_json::json!({"at": at, "mg": v}), format!("About {v} mg of caffeine still in your system at {at} (half-life {} h).", fmt_num(log.half_life_hours)))
        }
        "sleep-ready" => {
            if threshold <= 0.0 { return Err("--threshold must be above 0".into()); }
            let t = log.sleep_ready(threshold);
            let text = match &t { Some(t) => format!("Below {} mg from about {}.", fmt_num(threshold), t.replace('T', " ")), None => "No drinks logged.".into() };
            (serde_json::json!({"threshold": threshold, "time": t}), text)
        }
        "cups" => {
            let c = log.cups(&date.unwrap_or_else(today))?;
            (serde_json::to_value(c).unwrap(), format!("Last 7 days: {} reusable, {} disposable ({}% reusable).", c.reusable, c.disposable, c.rate))
        }
        "undo" => {
            let e = log.undo();
            save(&log)?;
            let text = match &e { Some(e) => format!("Removed {} at {}.", e.drink, e.time), None => "Nothing to undo.".into() };
            (serde_json::to_value(&e).unwrap(), text)
        }
        other => return Err(format!("Unknown command \"{other}\". Run with --help.")),
    };
    Ok(if json { serde_json::to_string_pretty(&value).unwrap() } else { text })
}

fn main() -> ExitCode {
    match run(std::env::args().skip(1).collect()) {
        Ok(s) => { println!("{s}"); ExitCode::SUCCESS }
        Err(e) => { eprintln!("Error: {e}"); ExitCode::from(2) }
    }
}
