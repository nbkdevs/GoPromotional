/// Hydration helpers: a daily water goal estimate (a general guide, not
/// medical advice), amount parsing ("500ml", "0.5l", "33cl", "16oz") and a
/// day-by-day water log with progress, 7-day history and streaks. The JSON
/// format matches the npm, PyPI, crates.io and RubyGems versions of
/// gopromotional-siplog.
library;

import 'dart:convert';

const double mlPerUsFlOz = 29.5735;
const int mlPerActiveMinute = 12;
const int hotWeatherMl = 500;
const int _dayMs = 86400000;
final RegExp _dateRe = RegExp(r'^(\d{4})-(\d{2})-(\d{2})$');
final RegExp _amountRe = RegExp(r'^\s*(\d+(?:\.\d+)?)\s*(ml|cl|l|oz)?\s*$', caseSensitive: false);

int toDay(String date) {
  final s = date.trim();
  final m = _dateRe.firstMatch(s);
  if (m == null) throw FormatException('Invalid date "$date" (expected YYYY-MM-DD)');
  final t = DateTime.utc(int.parse(m[1]!), int.parse(m[2]!), int.parse(m[3]!));
  if (t.toIso8601String().substring(0, 10) != s) throw RangeError('Invalid calendar date "$date"');
  return t.millisecondsSinceEpoch ~/ _dayMs;
}

String fromDay(int day) => DateTime.fromMillisecondsSinceEpoch(day * _dayMs, isUtc: true).toIso8601String().substring(0, 10);
String todayIso() => DateTime.now().toIso8601String().substring(0, 10);
String _norm(String? date) => fromDay(toDay(date ?? todayIso()));

/// Rough daily water goal in ml: weight x mlPerKg + 12 ml per active minute
/// (+500 ml in hot weather), rounded to the nearest 50 ml.
int dailyGoal({required num weightKg, num activityMinutes = 0, bool hot = false, num mlPerKg = 33}) {
  if (!(weightKg > 0) || weightKg > 400) throw RangeError('weightKg must be between 0 and 400');
  if (activityMinutes < 0) throw RangeError('activityMinutes must be >= 0');
  final raw = weightKg * mlPerKg + activityMinutes * mlPerActiveMinute + (hot ? hotWeatherMl : 0);
  return (raw / 50 + 0.5).floor() * 50;
}

/// Parse 250, "500ml", "0.5l", "1.5 L", "33cl" or "16oz" into whole millilitres.
int parseAmount(Object input) {
  if (input is num) {
    if (!(input > 0)) throw RangeError('amount must be positive');
    return (input + 0.5).floor();
  }
  final m = _amountRe.firstMatch('$input');
  if (m == null) throw FormatException('Cannot read amount "$input" (try 250, 500ml, 0.5l, 33cl or 16oz)');
  final factor = {'ml': 1.0, 'cl': 10.0, 'l': 1000.0, 'oz': mlPerUsFlOz}[(m[2] ?? 'ml').toLowerCase()]!;
  final ml = (double.parse(m[1]!) * factor + 0.5).floor();
  if (ml <= 0) throw RangeError('amount must be positive');
  return ml;
}

/// One logged drink.
class HydrationEntry {
  final String date;
  final int ml;
  const HydrationEntry(this.date, this.ml);
  Map<String, Object> toJson() => {'date': date, 'ml': ml};
  @override
  bool operator ==(Object other) => other is HydrationEntry && other.date == date && other.ml == ml;
  @override
  int get hashCode => Object.hash(date, ml);
  @override
  String toString() => '$date: $ml ml';
}

/// Progress towards the daily goal.
class HydrationProgress {
  final String date;
  final int total, goal, percent, remaining;
  final bool reached;
  const HydrationProgress(this.date, this.total, this.goal, this.percent, this.remaining, this.reached);
  Map<String, Object> toJson() =>
      {'date': date, 'total': total, 'goal': goal, 'percent': percent, 'remaining': remaining, 'reached': reached};
}

/// A day-by-day hydration log: `{"goal": 2000, "entries": [{"date": "2026-10-19", "ml": 500}]}`.
class HydrationLog {
  int goal;
  final List<HydrationEntry> entries;

  HydrationLog({this.goal = 2000, Iterable<HydrationEntry> entries = const []})
      : entries = [for (final e in entries) HydrationEntry(_norm(e.date), parseAmount(e.ml))];

  factory HydrationLog.fromJson(Object? data) {
    final d = (data is String ? jsonDecode(data) : data) as Map<String, dynamic>? ?? {};
    return HydrationLog(
      goal: (d['goal'] as num?)?.toInt() ?? 2000,
      entries: [for (final e in (d['entries'] as List? ?? const [])) HydrationEntry(e['date'] as String, parseAmount(e['ml'] as Object))],
    );
  }

  Map<String, Object> toJson() => {'goal': goal, 'entries': [for (final e in entries) e.toJson()]};

  HydrationEntry add(Object amount, {String? date}) {
    final e = HydrationEntry(_norm(date), parseAmount(amount));
    entries.add(e);
    return e;
  }

  /// Remove the most recent entry for a day; returns it, or null.
  HydrationEntry? undo({String? date}) {
    final d = _norm(date);
    for (var i = entries.length - 1; i >= 0; i--) {
      if (entries[i].date == d) return entries.removeAt(i);
    }
    return null;
  }

  int clearDay({String? date}) {
    final d = _norm(date), before = entries.length;
    entries.removeWhere((e) => e.date == d);
    return before - entries.length;
  }

  int total({String? date}) {
    final d = _norm(date);
    return entries.fold(0, (s, e) => e.date == d ? s + e.ml : s);
  }

  HydrationProgress progress({String? date, int? goal}) {
    final g = goal ?? this.goal, t = total(date: date);
    return HydrationProgress(_norm(date), t, g, g > 0 ? (t * 100 / g + 0.5).floor() : 0, t >= g ? 0 : g - t, t >= g);
  }

  /// Totals for the 7 days ending on [endDate], newest first.
  List<({String date, int total})> week({String? endDate}) {
    final end = toDay(_norm(endDate));
    return [for (var i = 0; i < 7; i++) (date: fromDay(end - i), total: total(date: fromDay(end - i)))];
  }

  /// Consecutive days meeting the goal, ending today (or yesterday if today is not done yet).
  int streak({String? endDate, int? goal}) {
    final g = goal ?? this.goal;
    var day = toDay(_norm(endDate));
    if (total(date: fromDay(day)) < g) day -= 1;
    var n = 0;
    while (n < 3660 && total(date: fromDay(day)) >= g) {
      n += 1;
      day -= 1;
    }
    return n;
  }

  int average({String? endDate, int days = 7}) {
    final end = toDay(_norm(endDate));
    var sum = 0;
    for (var i = 0; i < days; i++) {
      sum += total(date: fromDay(end - i));
    }
    return (sum / days + 0.5).floor();
  }
}
