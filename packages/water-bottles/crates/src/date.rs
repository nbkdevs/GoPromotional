//! Minimal proleptic-Gregorian date helpers (no external date crate needed).

/// Days since 1970-01-01 for a civil date (Howard Hinnant's algorithm).
pub fn days_from_civil(y: i64, m: u32, d: u32) -> i64 {
    let y = if m <= 2 { y - 1 } else { y };
    let era = if y >= 0 { y } else { y - 399 } / 400;
    let yoe = y - era * 400;
    let mp = (m as i64 + 9) % 12;
    let doy = (153 * mp + 2) / 5 + d as i64 - 1;
    let doe = yoe * 365 + yoe / 4 - yoe / 100 + doy;
    era * 146097 + doe - 719468
}

/// Civil date (y, m, d) for days since 1970-01-01.
pub fn civil_from_days(z: i64) -> (i64, u32, u32) {
    let z = z + 719468;
    let era = if z >= 0 { z } else { z - 146096 } / 146097;
    let doe = z - era * 146097;
    let yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365;
    let y = yoe + era * 400;
    let doy = doe - (365 * yoe + yoe / 4 - yoe / 100);
    let mp = (5 * doy + 2) / 153;
    let d = (doy - (153 * mp + 2) / 5 + 1) as u32;
    let m = if mp < 10 { mp + 3 } else { mp - 9 } as u32;
    (if m <= 2 { y + 1 } else { y }, m, d)
}

/// Parse `YYYY-MM-DD` into a day number, rejecting impossible dates.
pub fn parse_day(s: &str) -> Result<i64, String> {
    let s = s.trim();
    let b = s.as_bytes();
    if b.len() != 10 || b[4] != b'-' || b[7] != b'-' {
        return Err(format!("Invalid date \"{s}\" (expected YYYY-MM-DD)"));
    }
    let num = |r: std::ops::Range<usize>| s[r].parse::<i64>().map_err(|_| format!("Invalid date \"{s}\" (expected YYYY-MM-DD)"));
    let (y, m, d) = (num(0..4)?, num(5..7)?, num(8..10)?);
    if !(1..=12).contains(&m) || !(1..=31).contains(&d) {
        return Err(format!("Invalid calendar date \"{s}\""));
    }
    let day = days_from_civil(y, m as u32, d as u32);
    if civil_from_days(day) != (y, m as u32, d as u32) {
        return Err(format!("Invalid calendar date \"{s}\""));
    }
    Ok(day)
}

/// Format a day number as `YYYY-MM-DD`.
pub fn format_day(day: i64) -> String {
    let (y, m, d) = civil_from_days(day);
    format!("{y:04}-{m:02}-{d:02}")
}

/// ISO weekday: 1 = Monday … 7 = Sunday.
pub fn weekday(day: i64) -> u32 {
    let w = (day + 4).rem_euclid(7) as u32; // 1970-01-01 was a Thursday
    if w == 0 { 7 } else { w }
}

/// Today's date (UTC) as a day number.
pub fn today() -> i64 {
    let secs = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .map(|d| d.as_secs() as i64)
        .unwrap_or(0);
    secs.div_euclid(86_400)
}
