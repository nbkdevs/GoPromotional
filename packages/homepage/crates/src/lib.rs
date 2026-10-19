//! # gopromotional-event-planner
//!
//! Event planning helpers: a countdown, a planning timeline with due dates, a giveaway
//! quantity estimator and the latest order date for printed merchandise (working days).
//!
//! ```
//! use gopromotional_event_planner::{countdown, giveaway_quantity, order_by_date, GiveawayInput};
//! let c = countdown("2026-12-03", "2026-10-19").unwrap();
//! assert_eq!((c.days, c.weeks, c.remainder_days), (45, 6, 3));
//! let q = giveaway_quantity(&GiveawayInput { attendees: 500.0, days: 2.0, ..Default::default() }).unwrap();
//! assert_eq!(q.total, 660);
//! assert_eq!(order_by_date("2026-12-03", 10, 3, 2, None).unwrap().order_by, "2026-11-12");
//! ```
pub mod date;

use date::{format_day, parse_day, weekday};
use serde::Serialize;

/// Days-until-event summary.
#[derive(Debug, Clone, PartialEq, Eq, Serialize)]
pub struct Countdown {
    pub event_date: String,
    pub today: String,
    pub days: i64,
    pub weeks: i64,
    pub remainder_days: i64,
    pub status: &'static str,
}

/// Days until an event, split into weeks and days.
pub fn countdown(event_date: &str, today: &str) -> Result<Countdown, String> {
    let ev = parse_day(event_date)?;
    let now = parse_day(today)?;
    let days = ev - now;
    Ok(Countdown {
        event_date: format_day(ev),
        today: format_day(now),
        days,
        weeks: days.abs() / 7,
        remainder_days: days.abs() % 7,
        status: if days > 0 { "upcoming" } else if days == 0 { "today" } else { "past" },
    })
}

/// One template task: due `offset_days` before the event (negative = after).
#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize)]
pub struct TimelineItem {
    pub offset_days: i64,
    pub category: &'static str,
    pub task: &'static str,
}

/// The default planning template.
pub const TIMELINE_TEMPLATE: [TimelineItem; 12] = [
    TimelineItem { offset_days: 56, category: "planning", task: "Confirm budget and attendee estimate" },
    TimelineItem { offset_days: 49, category: "venue", task: "Book the venue" },
    TimelineItem { offset_days: 42, category: "merchandise", task: "Order branded merchandise and giveaways" },
    TimelineItem { offset_days: 35, category: "comms", task: "Send invitations or open registration" },
    TimelineItem { offset_days: 28, category: "programme", task: "Confirm speakers and agenda" },
    TimelineItem { offset_days: 21, category: "venue", task: "Arrange catering" },
    TimelineItem { offset_days: 14, category: "merchandise", task: "Approve print proofs and signage" },
    TimelineItem { offset_days: 7, category: "comms", task: "Send a reminder to attendees" },
    TimelineItem { offset_days: 3, category: "logistics", task: "Confirm deliveries and the staffing rota" },
    TimelineItem { offset_days: 1, category: "logistics", task: "Pack the event kit and giveaways" },
    TimelineItem { offset_days: 0, category: "logistics", task: "Event day: set up the stand and registration" },
    TimelineItem { offset_days: -2, category: "comms", task: "Send thank-you and follow-up messages" },
];

/// A timeline row with its due date.
#[derive(Debug, Clone, PartialEq, Eq, Serialize)]
pub struct TimelineRow {
    pub task: &'static str,
    pub category: &'static str,
    pub offset_days: i64,
    pub due: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub days_left: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub overdue: Option<bool>,
}

/// Planning tasks with due dates, sorted by date.
pub fn timeline(event_date: &str, today: Option<&str>, template: &[TimelineItem]) -> Result<Vec<TimelineRow>, String> {
    let ev = parse_day(event_date)?;
    let now = today.map(parse_day).transpose()?;
    let mut rows: Vec<TimelineRow> = template
        .iter()
        .map(|it| {
            let due = ev - it.offset_days;
            TimelineRow {
                task: it.task,
                category: it.category,
                offset_days: it.offset_days,
                due: format_day(due),
                days_left: now.map(|t| due - t),
                overdue: now.map(|t| due < t),
            }
        })
        .collect();
    rows.sort_by(|a, b| a.due.cmp(&b.due));
    Ok(rows)
}

/// Inputs for [`giveaway_quantity`].
#[derive(Debug, Clone, Copy, PartialEq)]
pub struct GiveawayInput {
    pub attendees: f64,
    pub days: f64,
    pub uptake: f64,
    pub per_person: f64,
    pub buffer: f64,
}
impl Default for GiveawayInput {
    fn default() -> Self {
        Self { attendees: 0.0, days: 1.0, uptake: 0.6, per_person: 1.0, buffer: 0.1 }
    }
}

/// Giveaway estimate: expected items, buffer and total to order.
#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize)]
pub struct Giveaways {
    pub base: u64,
    pub buffer: u64,
    pub total: u64,
}

fn ceil_clean(x: f64) -> u64 {
    // Remove binary noise (e.g. 300.00000000000006) before rounding up.
    ((x * 1e6).round() / 1e6).ceil() as u64
}

/// attendees × days × uptake × per_person (rounded up) plus a buffer (rounded up).
pub fn giveaway_quantity(i: &GiveawayInput) -> Result<Giveaways, String> {
    for (name, v) in [("attendees", i.attendees), ("days", i.days), ("uptake", i.uptake), ("per_person", i.per_person), ("buffer", i.buffer)] {
        if !v.is_finite() || v < 0.0 {
            return Err(format!("{name} must be a non-negative number"));
        }
    }
    if i.uptake > 1.0 {
        return Err("uptake must be between 0 and 1".into());
    }
    let base = ceil_clean(i.attendees * i.days * i.uptake * i.per_person);
    let buffer = ceil_clean(base as f64 * i.buffer);
    Ok(Giveaways { base, buffer, total: base + buffer })
}

/// Step back `n` working days (Monday–Friday) from a day number.
pub fn subtract_working_days(day: i64, n: u32) -> i64 {
    let (mut d, mut left) = (day, n);
    while left > 0 {
        d -= 1;
        if weekday(d) <= 5 {
            left -= 1;
        }
    }
    d
}

/// Latest order date result.
#[derive(Debug, Clone, PartialEq, Eq, Serialize)]
pub struct OrderBy {
    pub order_by: String,
    pub working_days: u32,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub days_from_today: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub late: Option<bool>,
}

/// Latest order date: production + delivery + safety margin, counted in working days before the event.
pub fn order_by_date(event_date: &str, production_days: u32, delivery_days: u32, safety_days: u32, today: Option<&str>) -> Result<OrderBy, String> {
    let working = production_days + delivery_days + safety_days;
    let by = subtract_working_days(parse_day(event_date)?, working);
    let dft = today.map(parse_day).transpose()?.map(|t| by - t);
    Ok(OrderBy { order_by: format_day(by), working_days: working, days_from_today: dft, late: dft.map(|d| d < 0) })
}
