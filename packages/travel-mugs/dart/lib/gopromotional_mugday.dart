/// Caffeine and coffee intake tracker: typical caffeine per drink, daily
/// totals against a limit, caffeine still in your system (half-life model),
/// a "sleep-ready" time and reusable-cup stats. The JSON format matches the
/// npm, PyPI, crates.io and RubyGems versions of gopromotional-mugday.
library;

import 'dart:convert';
import 'dart:math' as math;

/// Typical caffeine per serving (mg) and serving size (ml). Real drinks vary.
const Map<String, ({int mg, int ml})> drinks = {
  'espresso': (mg: 63, ml: 30),
  'double-espresso': (mg: 126, ml: 60),
  'americano': (mg: 126, ml: 350),
  'latte': (mg: 126, ml: 350),
  'cappuccino': (mg: 126, ml: 350),
  'flat-white': (mg: 126, ml: 220),
  'filter-coffee': (mg: 140, ml: 300),
  'instant-coffee': (mg: 100, ml: 300),
  'cold-brew': (mg: 155, ml: 350),
  'decaf-coffee': (mg: 3, ml: 300),
  'tea': (mg: 75, ml: 300),
  'green-tea': (mg: 30, ml: 300),
  'cola': (mg: 40, ml: 330),
  'energy-drink': (mg: 80, ml: 250),
  'hot-chocolate': (mg: 5, ml: 300),
  'dark-chocolate-50g': (mg: 25, ml: 0),
};

/// Daily guidance presets (mg): EFSA/NHS adult and pregnancy levels.
const Map<String, int> limits = {'adult': 400, 'pregnant': 200, 'single_dose': 200};
const double defaultHalfLifeHours = 5;

final RegExp _timeRe = RegExp(r'^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})$');
String _pad(int n) => n.toString().padLeft(2, '0');

/// Parse "YYYY-MM-DDTHH:MM" (or with a space) to minutes since 1970-01-01 (naive local time).
int toMinutes(String s) {
  final m = _timeRe.firstMatch(s.trim());
  if (m == null) throw FormatException('Invalid time "$s" (expected YYYY-MM-DDTHH:MM)');
  final min = DateTime.utc(int.parse(m[1]!), int.parse(m[2]!), int.parse(m[3]!), int.parse(m[4]!), int.parse(m[5]!)).millisecondsSinceEpoch ~/ 60000;
  if (fromMinutes(min) != '${m[1]}-${m[2]}-${m[3]}T${m[4]}:${m[5]}') throw RangeError('Invalid time "$s"');
  return min;
}

String fromMinutes(int min) {
  final d = DateTime.fromMillisecondsSinceEpoch(min * 60000, isUtc: true);
  return '${d.year.toString().padLeft(4, '0')}-${_pad(d.month)}-${_pad(d.day)}T${_pad(d.hour)}:${_pad(d.minute)}';
}

String nowLocal() {
  final n = DateTime.now();
  return '${n.year.toString().padLeft(4, '0')}-${_pad(n.month)}-${_pad(n.day)}T${_pad(n.hour)}:${_pad(n.minute)}';
}

String _norm(String drink) => drink.trim().toLowerCase().replaceAll(RegExp(r'[\s_]+'), '-');

({String drink, int mg, int ml}) lookupDrink(String name) {
  final k = _norm(name);
  final d = drinks[k];
  if (d == null) throw ArgumentError('Unknown drink "$name". Known: ${drinks.keys.join(', ')} (or pass mg yourself)');
  return (drink: k, mg: d.mg, ml: d.ml);
}

/// One logged drink.
class CaffeineEntry {
  final String drink, time;
  final num mg;
  final bool reusable;
  const CaffeineEntry(this.drink, this.mg, this.time, this.reusable);
  Map<String, Object> toJson() => {'drink': drink, 'mg': mg, 'time': time, 'reusable': reusable};
}

class CaffeineLog {
  num limit;
  num halfLifeHours;
  final List<CaffeineEntry> entries = [];

  CaffeineLog({this.limit = 400, this.halfLifeHours = defaultHalfLifeHours});

  factory CaffeineLog.fromJson(Object? data) {
    final d = (data is String ? jsonDecode(data) : data) as Map<String, dynamic>? ?? {};
    final log = CaffeineLog(limit: d['limit'] as num? ?? limits['adult']!, halfLifeHours: d['halfLifeHours'] as num? ?? defaultHalfLifeHours);
    for (final e in (d['entries'] as List? ?? const [])) {
      log.add(e['drink'] as String, time: e['time'] as String?, mg: e['mg'] as num?, reusable: e['reusable'] == true);
    }
    return log;
  }

  Map<String, Object> toJson() => {'limit': limit, 'halfLifeHours': halfLifeHours, 'entries': [for (final e in entries) e.toJson()]};

  /// Log a drink; [mg] overrides the typical value. Entries stay in time order.
  CaffeineEntry add(String drink, {String? time, num? mg, bool reusable = false}) {
    var name = _norm(drink);
    var caffeine = mg;
    if (caffeine == null) {
      final d = lookupDrink(drink);
      name = d.drink;
      caffeine = d.mg;
    }
    if (caffeine.isNaN || caffeine < 0 || caffeine > 1000) throw RangeError('mg must be between 0 and 1000');
    final e = CaffeineEntry(name, caffeine, fromMinutes(toMinutes(time ?? nowLocal())), reusable);
    // insert after any entry at the same or earlier time (stable, like the other ports)
    var i = entries.length;
    while (i > 0 && toMinutes(entries[i - 1].time) > toMinutes(e.time)) {
      i--;
    }
    entries.insert(i, e);
    return e;
  }

  CaffeineEntry? undo() => entries.isEmpty ? null : entries.removeLast();

  List<CaffeineEntry> forDay(String date) => entries.where((e) => e.time.substring(0, 10) == date).toList();

  /// Total caffeine for a calendar day compared with the limit.
  ({String date, int drinks, num total, num limit, num remaining, num over}) day({String? date, num? limit}) {
    final d = date ?? nowLocal().substring(0, 10);
    final lim = limit ?? this.limit;
    final items = forDay(d);
    final total = items.fold<num>(0, (s, e) => s + e.mg);
    return (date: d, drinks: items.length, total: total, limit: lim, remaining: math.max(0, lim - total), over: math.max(0, total - lim));
  }

  /// Estimated caffeine (mg) still in the body at [at].
  int inSystem({String? at}) {
    final t = toMinutes(at ?? nowLocal());
    var sum = 0.0;
    for (final e in entries) {
      final dt = t - toMinutes(e.time);
      if (dt >= 0) sum += e.mg * math.pow(0.5, dt / 60 / halfLifeHours);
    }
    return (sum + 0.5).floor();
  }

  /// Earliest time (rounded up to 15 minutes) when caffeine falls below [threshold] mg.
  String? sleepReady([num threshold = 50]) {
    if (entries.isEmpty) return null;
    final last = toMinutes(entries.last.time);
    var level = 0.0;
    for (final e in entries) {
      level += e.mg * math.pow(0.5, (last - toMinutes(e.time)) / 60 / halfLifeHours);
    }
    if (level <= threshold) return fromMinutes(last);
    final minutes = halfLifeHours * 60 * (math.log(level / threshold) / math.ln2);
    return fromMinutes(((last + minutes) / 15).ceil() * 15);
  }

  /// Reusable vs disposable cups over the 7 days ending on [endDate].
  ({int reusable, int disposable, int rate}) cups({String? endDate}) {
    final end = toMinutes('${endDate ?? nowLocal().substring(0, 10)}T00:00') ~/ 1440;
    final week = entries.where((e) {
      final d = toMinutes('${e.time.substring(0, 10)}T00:00') ~/ 1440;
      return d <= end && d > end - 7;
    }).toList();
    final reusable = week.where((e) => e.reusable).length;
    return (reusable: reusable, disposable: week.length - reusable, rate: week.isEmpty ? 0 : (reusable * 100 / week.length + 0.5).floor());
  }
}
