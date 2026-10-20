import 'package:gopromotional_event_planner/gopromotional_event_planner.dart';
import 'package:test/test.dart';

void main() {
  test('countdown splits into weeks and days', () {
    expect(countdown('2026-12-03', today: '2026-10-19').toJson(),
        {'eventDate': '2026-12-03', 'today': '2026-10-19', 'days': 45, 'weeks': 6, 'remainderDays': 3, 'status': 'upcoming'});
    expect(countdown('2026-10-19', today: '2026-10-19').status, 'today');
    final past = countdown('2026-10-10', today: '2026-10-19');
    expect([past.days, past.status], [-9, 'past']);
  });

  test('rejects invalid dates', () {
    expect(() => toDay('2026-02-30'), throwsRangeError);
    expect(() => toDay('19/10/2026'), throwsFormatException);
  });

  test('weekday is ISO numbered', () {
    expect(weekday(toDay('2026-10-19')), 1);
    expect(weekday(toDay('2026-12-03')), 4);
    expect(weekday(toDay('2026-10-25')), 7);
  });

  test('timeline dates and overdue flags', () {
    final t = timeline('2026-12-03', today: '2026-10-19');
    expect(t.length, 12);
    expect([t.first.due, t.first.overdue], ['2026-10-08', true]);
    final merch = t.firstWhere((r) => r.task.startsWith('Order branded'));
    expect([merch.due, merch.daysLeft, merch.overdue], ['2026-10-22', 3, false]);
    expect(t.last.due, '2026-12-05');
  });

  test('giveaway quantities round up', () {
    expect(giveawayQuantity(attendees: 500, days: 2, uptake: 0.6, buffer: 0.1).toJson(), {'base': 600, 'buffer': 60, 'total': 660});
    expect(giveawayQuantity(attendees: 250, uptake: 0.35).toJson(), {'base': 88, 'buffer': 9, 'total': 97});
    expect(giveawayQuantity(attendees: 0).total, 0);
    expect(() => giveawayQuantity(attendees: 10, uptake: 1.5), throwsRangeError);
    expect(() => giveawayQuantity(attendees: -1), throwsRangeError);
  });

  test('order-by skips weekends', () {
    final o = orderByDate('2026-12-03', today: '2026-10-19');
    expect([o.orderBy, o.workingDays, o.daysFromToday, o.late], ['2026-11-12', 15, 24, false]);
    expect(orderByDate('2026-10-26', productionDays: 1, deliveryDays: 0, safetyDays: 0).orderBy, '2026-10-23');
    expect(orderByDate('2026-10-21', today: '2026-10-19').late, true);
  });
}
