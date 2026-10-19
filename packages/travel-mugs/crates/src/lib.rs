//! # gopromotional-mugday
//!
//! Caffeine and coffee tracker. Typical caffeine per drink, daily totals against a limit,
//! an estimate of caffeine still in your system (exponential half-life model), when it should
//! fall below a "sleep-ready" level, and reusable vs disposable cup stats.
//! Values are typical figures; real drinks vary. Not medical advice.
//!
//! The JSON log (`{limit, halfLifeHours, entries:[{drink, mg, time, reusable}]}`) is shared with
//! the npm, PyPI and RubyGems versions of this tool.
//!
//! ```
//! use gopromotional_mugday::CaffeineLog;
//! let mut log = CaffeineLog::default();
//! log.add("flat-white", "2026-10-19T08:00", None, true).unwrap();
//! assert_eq!(log.in_system("2026-10-19T13:00").unwrap(), 63);
//! ```
pub mod date;

use serde::{Deserialize, Serialize, Serializer};

/// (name, caffeine mg, typical volume ml)
pub const DRINKS: &[(&str, u32, u32)] = &[
    ("espresso", 63, 30), ("double-espresso", 126, 60), ("americano", 126, 350), ("latte", 126, 350),
    ("cappuccino", 126, 350), ("flat-white", 126, 220), ("filter-coffee", 140, 300), ("instant-coffee", 100, 300),
    ("cold-brew", 155, 350), ("decaf-coffee", 3, 300), ("tea", 75, 300), ("green-tea", 30, 300),
    ("cola", 40, 330), ("energy-drink", 80, 250), ("hot-chocolate", 5, 300), ("dark-chocolate-50g", 25, 0),
];
pub const LIMIT_ADULT: f64 = 400.0;
pub const LIMIT_PREGNANT: f64 = 200.0;
pub const LIMIT_SINGLE_DOSE: f64 = 200.0;
pub const DEFAULT_HALF_LIFE_HOURS: f64 = 5.0;

fn round(x: f64) -> i64 { (x + 0.5).floor() as i64 }

/// Format a number without a trailing `.0` when it is whole.
pub fn fmt_num(x: f64) -> String { if x.fract() == 0.0 { format!("{}", x as i64) } else { format!("{x}") } }

fn ser_num<S: Serializer>(x: &f64, s: S) -> Result<S::Ok, S::Error> {
    if x.fract() == 0.0 && x.abs() < 1e15 { s.serialize_i64(*x as i64) } else { s.serialize_f64(*x) }
}

fn norm(name: &str) -> String {
    name.trim().to_lowercase().split(|c: char| c.is_whitespace() || c == '_').filter(|s| !s.is_empty()).collect::<Vec<_>>().join("-")
}

/// Look up a drink: (canonical name, mg).
pub fn lookup_drink(name: &str) -> Result<(String, f64), String> {
    let k = norm(name);
    DRINKS.iter().find(|d| d.0 == k).map(|d| (k.clone(), d.1 as f64)).ok_or_else(|| {
        format!("Unknown drink \"{name}\". Known: {} (or pass mg yourself)", DRINKS.iter().map(|d| d.0).collect::<Vec<_>>().join(", "))
    })
}

/// `YYYY-MM-DDTHH:MM` (naive local time) to minutes since 1970-01-01.
pub fn to_minutes(s: &str) -> Result<i64, String> {
    let s = s.trim();
    let bad = || format!("Invalid time \"{s}\" (expected YYYY-MM-DDTHH:MM)");
    if s.len() != 16 || !s.is_char_boundary(10) { return Err(bad()); }
    let sep = s.as_bytes()[10];
    if sep != b'T' && sep != b' ' || s.as_bytes()[13] != b':' { return Err(bad()); }
    let day = date::parse_day(&s[..10]).map_err(|_| format!("Invalid time \"{s}\""))?;
    let num = |r: std::ops::Range<usize>| s[r].parse::<i64>().ok().filter(|_| true);
    let (h, m) = (num(11..13).ok_or_else(bad)?, num(14..16).ok_or_else(bad)?);
    if !(0..24).contains(&h) || !(0..60).contains(&m) || !s[11..16].chars().enumerate().all(|(i, c)| i == 2 || c.is_ascii_digit()) {
        return Err(format!("Invalid time \"{s}\""));
    }
    Ok(day * 1440 + h * 60 + m)
}

pub fn from_minutes(m: i64) -> String {
    let (day, rem) = (m.div_euclid(1440), m.rem_euclid(1440));
    format!("{}T{:02}:{:02}", date::format_day(day), rem / 60, rem % 60)
}

/// Current time (UTC) as `YYYY-MM-DDTHH:MM`.
pub fn now() -> String {
    let secs = std::time::SystemTime::now().duration_since(std::time::UNIX_EPOCH).map(|d| d.as_secs() as i64).unwrap_or(0);
    from_minutes(secs.div_euclid(60))
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
pub struct Drink {
    pub drink: String,
    #[serde(serialize_with = "ser_num")]
    pub mg: f64,
    pub time: String,
    #[serde(default)]
    pub reusable: bool,
}

#[derive(Debug, Clone, PartialEq, Serialize)]
pub struct DaySummary { pub date: String, pub drinks: usize, pub total: f64, pub limit: f64, pub remaining: f64, pub over: f64 }

#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize)]
pub struct Cups { pub reusable: usize, pub disposable: usize, pub rate: i64 }

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
pub struct CaffeineLog {
    #[serde(default = "default_limit", serialize_with = "ser_num")]
    pub limit: f64,
    #[serde(rename = "halfLifeHours", alias = "half_life_hours", default = "default_hl", serialize_with = "ser_num")]
    pub half_life_hours: f64,
    #[serde(default)]
    pub entries: Vec<Drink>,
}
fn default_limit() -> f64 { LIMIT_ADULT }
fn default_hl() -> f64 { DEFAULT_HALF_LIFE_HOURS }
impl Default for CaffeineLog { fn default() -> Self { Self { limit: LIMIT_ADULT, half_life_hours: DEFAULT_HALF_LIFE_HOURS, entries: vec![] } } }

impl CaffeineLog {
    pub fn from_json(s: &str) -> Result<Self, String> {
        let raw: CaffeineLog = serde_json::from_str(s).map_err(|e| format!("Invalid log file: {e}"))?;
        let mut log = CaffeineLog { entries: vec![], ..raw.clone() };
        for e in raw.entries { log.add(&e.drink, &e.time, Some(e.mg), e.reusable)?; }
        Ok(log)
    }
    pub fn to_json(&self) -> String { serde_json::to_string_pretty(self).unwrap() }

    /// Log a drink; `mg` overrides the typical value. Entries stay sorted by time.
    pub fn add(&mut self, drink: &str, time: &str, mg: Option<f64>, reusable: bool) -> Result<Drink, String> {
        let (name, mg) = match mg { Some(m) => (norm(drink), m), None => lookup_drink(drink)? };
        if !(0.0..=1000.0).contains(&mg) { return Err("mg must be between 0 and 1000".into()); }
        let e = Drink { drink: name, mg, time: from_minutes(to_minutes(time)?), reusable };
        self.entries.push(e.clone());
        self.entries.sort_by_key(|x| to_minutes(&x.time).unwrap_or(0));
        Ok(e)
    }
    pub fn undo(&mut self) -> Option<Drink> { self.entries.pop() }
    pub fn for_day(&self, day: &str) -> Vec<&Drink> { self.entries.iter().filter(|e| &e.time[..10] == day).collect() }

    pub fn day(&self, day: &str, limit: Option<f64>) -> DaySummary {
        let lim = limit.unwrap_or(self.limit);
        let items = self.for_day(day);
        let total: f64 = items.iter().map(|e| e.mg).sum();
        DaySummary { date: day.into(), drinks: items.len(), total, limit: lim, remaining: (lim - total).max(0.0), over: (total - lim).max(0.0) }
    }

    fn level(&self, t: i64) -> f64 {
        self.entries.iter().filter_map(|e| {
            let dt = t - to_minutes(&e.time).ok()?;
            (dt >= 0).then(|| e.mg * 0.5f64.powf(dt as f64 / 60.0 / self.half_life_hours))
        }).sum()
    }
    /// Estimated caffeine (mg) still in the body at `at`.
    pub fn in_system(&self, at: &str) -> Result<i64, String> { Ok(round(self.level(to_minutes(at)?))) }

    /// Earliest time (rounded up to 15 min) caffeine falls below `threshold`, if nothing more is drunk.
    pub fn sleep_ready(&self, threshold: f64) -> Option<String> {
        let last = to_minutes(&self.entries.last()?.time).ok()?;
        let level = self.level(last);
        if level <= threshold { return Some(from_minutes(last)); }
        let minutes = self.half_life_hours * 60.0 * (level / threshold).log2();
        Some(from_minutes(((last as f64 + minutes) / 15.0).ceil() as i64 * 15))
    }

    /// Reusable vs disposable cups over the 7 days ending on `end` (YYYY-MM-DD).
    pub fn cups(&self, end: &str) -> Result<Cups, String> {
        let e = date::parse_day(end)?;
        let week: Vec<&Drink> = self.entries.iter().filter(|x| date::parse_day(&x.time[..10]).map(|d| e - 7 < d && d <= e).unwrap_or(false)).collect();
        let reusable = week.iter().filter(|x| x.reusable).count();
        let rate = if week.is_empty() { 0 } else { round(reusable as f64 * 100.0 / week.len() as f64) };
        Ok(Cups { reusable, disposable: week.len() - reusable, rate })
    }
}
