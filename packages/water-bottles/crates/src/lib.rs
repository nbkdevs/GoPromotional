//! # gopromotional-siplog
//!
//! Hydration helpers: a daily water goal estimate, amount parsing (`500ml`, `0.5l`, `33cl`, `16oz`)
//! and a day-by-day water log with progress, 7-day history, averages and streaks.
//!
//! ```
//! use gopromotional_siplog::{daily_goal, parse_amount, HydrationLog};
//! assert_eq!(daily_goal(70.0, 30.0, true).unwrap(), 3150);
//! assert_eq!(parse_amount("0.5l").unwrap(), 500);
//! let mut log = HydrationLog::new(2000);
//! log.add("500ml", "2026-10-19").unwrap();
//! assert_eq!(log.progress("2026-10-19").unwrap().percent, 25);
//! ```
pub mod date;

use date::{format_day, parse_day};
use serde::{Deserialize, Serialize};

pub const ML_PER_US_FL_OZ: f64 = 29.5735;
pub const ML_PER_ACTIVE_MINUTE: f64 = 12.0;
pub const HOT_WEATHER_ML: f64 = 500.0;

fn round(x: f64) -> i64 { (x + 0.5).floor() as i64 }

/// Rough daily goal in ml (a general guide, not medical advice):
/// weight × 33 ml + active minutes × 12 ml (+500 ml if hot), rounded to the nearest 50 ml.
pub fn daily_goal(weight_kg: f64, activity_minutes: f64, hot: bool) -> Result<i64, String> {
    daily_goal_with(weight_kg, activity_minutes, hot, 33.0)
}

/// [`daily_goal`] with a custom ml-per-kg factor.
pub fn daily_goal_with(weight_kg: f64, activity_minutes: f64, hot: bool, ml_per_kg: f64) -> Result<i64, String> {
    if !(weight_kg > 0.0 && weight_kg <= 400.0) { return Err("weight_kg must be between 0 and 400".into()); }
    if !(activity_minutes >= 0.0) { return Err("activity_minutes must be >= 0".into()); }
    let raw = weight_kg * ml_per_kg + activity_minutes * ML_PER_ACTIVE_MINUTE + if hot { HOT_WEATHER_ML } else { 0.0 };
    Ok((raw / 50.0 + 0.5).floor() as i64 * 50)
}

/// Parse `250`, `500ml`, `0.5l`, `1.5 L`, `33cl` or `16oz` into whole millilitres.
pub fn parse_amount(input: &str) -> Result<i64, String> {
    let s = input.trim().to_ascii_lowercase();
    let split = s.find(|c: char| !(c.is_ascii_digit() || c == '.')).unwrap_or(s.len());
    let (num, unit) = (&s[..split], s[split..].trim());
    let err = || format!("Cannot read amount \"{input}\" (try 250, 500ml, 0.5l, 33cl or 16oz)");
    if num.is_empty() || num.starts_with('.') || num.ends_with('.') || num.matches('.').count() > 1 { return Err(err()); }
    let n: f64 = num.parse().map_err(|_| err())?;
    let factor = match unit { "" | "ml" => 1.0, "cl" => 10.0, "l" => 1000.0, "oz" => ML_PER_US_FL_OZ, _ => return Err(err()) };
    let ml = round(n * factor);
    if ml <= 0 { return Err("amount must be positive".into()); }
    Ok(ml)
}

/// One logged drink.
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct Entry { pub date: String, pub ml: i64 }

/// Progress for one day.
#[derive(Debug, Clone, PartialEq, Eq, Serialize)]
pub struct Progress { pub date: String, pub total: i64, pub goal: i64, pub percent: i64, pub remaining: i64, pub reached: bool }

/// A day total.
#[derive(Debug, Clone, PartialEq, Eq, Serialize)]
pub struct DayTotal { pub date: String, pub total: i64 }

/// Day-by-day hydration log (JSON-compatible with the npm and PyPI packages).
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct HydrationLog {
    #[serde(default = "default_goal")]
    pub goal: i64,
    #[serde(default)]
    pub entries: Vec<Entry>,
}
fn default_goal() -> i64 { 2000 }
impl Default for HydrationLog { fn default() -> Self { Self::new(2000) } }

impl HydrationLog {
    pub fn new(goal: i64) -> Self { Self { goal, entries: Vec::new() } }
    pub fn from_json(s: &str) -> Result<Self, String> { serde_json::from_str(s).map_err(|e| e.to_string()) }
    pub fn to_json(&self) -> String { serde_json::to_string_pretty(self).unwrap_or_default() }

    pub fn add(&mut self, amount: &str, date: &str) -> Result<Entry, String> {
        let e = Entry { date: format_day(parse_day(date)?), ml: parse_amount(amount)? };
        self.entries.push(e.clone());
        Ok(e)
    }
    /// Remove the most recent entry for a day.
    pub fn undo(&mut self, date: &str) -> Result<Option<Entry>, String> {
        let d = format_day(parse_day(date)?);
        Ok(self.entries.iter().rposition(|e| e.date == d).map(|i| self.entries.remove(i)))
    }
    pub fn clear_day(&mut self, date: &str) -> Result<usize, String> {
        let d = format_day(parse_day(date)?);
        let before = self.entries.len();
        self.entries.retain(|e| e.date != d);
        Ok(before - self.entries.len())
    }
    fn total_day(&self, day: i64) -> i64 { let d = format_day(day); self.entries.iter().filter(|e| e.date == d).map(|e| e.ml).sum() }
    pub fn total(&self, date: &str) -> Result<i64, String> { Ok(self.total_day(parse_day(date)?)) }
    pub fn progress(&self, date: &str) -> Result<Progress, String> {
        let total = self.total(date)?;
        let g = self.goal;
        Ok(Progress {
            date: format_day(parse_day(date)?), total, goal: g,
            percent: if g > 0 { (total as f64 * 100.0 / g as f64 + 0.5).floor() as i64 } else { 0 },
            remaining: (g - total).max(0), reached: total >= g,
        })
    }
    /// Totals for the 7 days ending on `end`, newest first.
    pub fn week(&self, end: &str) -> Result<Vec<DayTotal>, String> {
        let e = parse_day(end)?;
        Ok((0..7).map(|i| DayTotal { date: format_day(e - i), total: self.total_day(e - i) }).collect())
    }
    /// Consecutive days meeting the goal, ending today (or yesterday if today is not done yet).
    pub fn streak(&self, end: &str) -> Result<u32, String> {
        let mut d = parse_day(end)?;
        if self.total_day(d) < self.goal { d -= 1; }
        let mut n = 0;
        while n < 3660 && self.total_day(d) >= self.goal { n += 1; d -= 1; }
        Ok(n)
    }
    pub fn average(&self, end: &str, days: i64) -> Result<i64, String> {
        let e = parse_day(end)?;
        let sum: i64 = (0..days).map(|i| self.total_day(e - i)).sum();
        Ok(round(sum as f64 / days as f64))
    }
}
