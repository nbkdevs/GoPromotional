/// Event planning helpers: a countdown, a planning timeline with due dates,
/// a giveaway quantity estimator and the latest order date for printed
/// merchandise (working days). Same behaviour as the npm, PyPI, crates.io and
/// RubyGems versions of gopromotional-event-planner.
library;

const int _dayMs = 86400000;
final RegExp _dateRe = RegExp(r'^(\d{4})-(\d{2})-(\d{2})$');

/// Parse "YYYY-MM-DD" into a day number (days since 1970-01-01).
int toDay(String date) {
  final s = date.trim();
  final m = _dateRe.firstMatch(s);
  if (m == null) throw FormatException('Invalid date "$date" (expected YYYY-MM-DD)');
  final y = int.parse(m[1]!), mo = int.parse(m[2]!), d = int.parse(m[3]!);
  final t = DateTime.utc(y, mo, d);
  if (t.year != y || t.month != mo || t.day != d) throw RangeError('Invalid calendar date "$date"');
  return t.millisecondsSinceEpoch ~/ _dayMs;
}

/// Format a day number as "YYYY-MM-DD".
String fromDay(int day) => DateTime.fromMillisecondsSinceEpoch(day * _dayMs, isUtc: true).toIso8601String().substring(0, 10);

/// ISO weekday: 1 = Monday ... 7 = Sunday.
int weekday(int day) {
  final w = (((day + 4) % 7) + 7) % 7;
  return w == 0 ? 7 : w;
}

/// Today's local date as "YYYY-MM-DD".
String todayIso() => DateTime.now().toIso8601String().substring(0, 10);

/// Days until an event, split into weeks and days.
class Countdown {
  final String eventDate, today, status;
  final int days, weeks, remainderDays;
  const Countdown(this.eventDate, this.today, this.days, this.weeks, this.remainderDays, this.status);
  Map<String, Object> toJson() =>
      {'eventDate': eventDate, 'today': today, 'days': days, 'weeks': weeks, 'remainderDays': remainderDays, 'status': status};
}

Countdown countdown(String eventDate, {String? today}) {
  final ev = toDay(eventDate), t = toDay(today ?? todayIso());
  final days = ev - t, a = days.abs();
  return Countdown(fromDay(ev), fromDay(t), days, a ~/ 7, a % 7, days > 0 ? 'upcoming' : (days == 0 ? 'today' : 'past'));
}

/// A task in the planning template: [offsetDays] before the event (negative = after).
class TemplateItem {
  final int offsetDays;
  final String category, task;
  const TemplateItem(this.offsetDays, this.category, this.task);
}

/// Default planning template.
const List<TemplateItem> timelineTemplate = [
  TemplateItem(56, 'planning', 'Confirm budget and attendee estimate'),
  TemplateItem(49, 'venue', 'Book the venue'),
  TemplateItem(42, 'merchandise', 'Order branded merchandise and giveaways'),
  TemplateItem(35, 'comms', 'Send invitations or open registration'),
  TemplateItem(28, 'programme', 'Confirm speakers and agenda'),
  TemplateItem(21, 'venue', 'Arrange catering'),
  TemplateItem(14, 'merchandise', 'Approve print proofs and signage'),
  TemplateItem(7, 'comms', 'Send a reminder to attendees'),
  TemplateItem(3, 'logistics', 'Confirm deliveries and the staffing rota'),
  TemplateItem(1, 'logistics', 'Pack the event kit and giveaways'),
  TemplateItem(0, 'logistics', 'Event day: set up the stand and registration'),
  TemplateItem(-2, 'comms', 'Send thank-you and follow-up messages'),
];

/// One row of the timeline. [daysLeft] and [overdue] are set when `today` is given.
class TimelineRow {
  final String task, category, due;
  final int offsetDays;
  final int? daysLeft;
  final bool? overdue;
  const TimelineRow(this.task, this.category, this.offsetDays, this.due, [this.daysLeft, this.overdue]);
}

/// Planning timeline with a due date per task, sorted by due date.
List<TimelineRow> timeline(String eventDate, {String? today, List<TemplateItem> template = timelineTemplate}) {
  final ev = toDay(eventDate);
  final t = today == null ? null : toDay(today);
  final rows = [
    for (final i in template)
      t == null
          ? TimelineRow(i.task, i.category, i.offsetDays, fromDay(ev - i.offsetDays))
          : TimelineRow(i.task, i.category, i.offsetDays, fromDay(ev - i.offsetDays), ev - i.offsetDays - t, ev - i.offsetDays < t)
  ];
  // Stable sort by due date (ties keep template order), like the other ports.
  final idx = List<int>.generate(rows.length, (i) => i)
    ..sort((a, b) {
      final c = rows[a].due.compareTo(rows[b].due);
      return c != 0 ? c : a - b;
    });
  return [for (final i in idx) rows[i]];
}

int _ceilClean(double x) => ((x * 1e6).round() / 1e6).ceil();

/// Giveaway order quantity: base + buffer = total.
class GiveawayQuantity {
  final int base, buffer, total;
  const GiveawayQuantity(this.base, this.buffer, this.total);
  Map<String, int> toJson() => {'base': base, 'buffer': buffer, 'total': total};
}

/// base = attendees x days x uptake x perPerson (rounded up); buffer = base x buffer (rounded up).
GiveawayQuantity giveawayQuantity({required num attendees, num days = 1, num uptake = 0.6, num perPerson = 1, num buffer = 0.1}) {
  final nums = {'attendees': attendees, 'days': days, 'uptake': uptake, 'perPerson': perPerson, 'buffer': buffer};
  nums.forEach((k, v) {
    if (!v.isFinite || v < 0) throw RangeError('$k must be a non-negative number');
  });
  if (uptake > 1) throw RangeError('uptake must be between 0 and 1');
  final b = _ceilClean((attendees * days * uptake * perPerson).toDouble());
  final extra = _ceilClean((b * buffer).toDouble());
  return GiveawayQuantity(b, extra, b + extra);
}

/// Step back [n] working days (Mon-Fri) from a day number.
int subtractWorkingDays(int day, int n) {
  var d = day, left = n;
  while (left > 0) {
    d -= 1;
    if (weekday(d) <= 5) left -= 1;
  }
  return d;
}

/// Latest order date for printed merchandise.
class OrderBy {
  final String orderBy;
  final int workingDays;
  final int? daysFromToday;
  final bool? late;
  const OrderBy(this.orderBy, this.workingDays, [this.daysFromToday, this.late]);
}

/// Production + delivery + safety margin, in working days before the event.
OrderBy orderByDate(String eventDate, {int productionDays = 10, int deliveryDays = 3, int safetyDays = 2, String? today}) {
  for (final e in {'productionDays': productionDays, 'deliveryDays': deliveryDays, 'safetyDays': safetyDays}.entries) {
    if (e.value < 0) throw RangeError('${e.key} must be a non-negative integer');
  }
  final working = productionDays + deliveryDays + safetyDays;
  final by = subtractWorkingDays(toDay(eventDate), working);
  if (today == null) return OrderBy(fromDay(by), working);
  final fromToday = by - toDay(today);
  return OrderBy(fromDay(by), working, fromToday, fromToday < 0);
}
