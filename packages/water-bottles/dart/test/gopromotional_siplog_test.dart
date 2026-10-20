import 'dart:convert';

import 'package:gopromotional_siplog/gopromotional_siplog.dart';
import 'package:test/test.dart';

void main() {
  test('daily goal estimate rounds to 50 ml', () {
    expect(dailyGoal(weightKg: 70), 2300);
    expect(dailyGoal(weightKg: 70, activityMinutes: 30, hot: true), 3150);
    expect(dailyGoal(weightKg: 82.5, activityMinutes: 45), 3250);
    expect(() => dailyGoal(weightKg: 0), throwsRangeError);
  });

  test('parses amounts with units', () {
    expect([for (final a in ['500ml', '0.5l', '1.5 L', '33cl', '330', '16oz']) parseAmount(a)], [500, 500, 1500, 330, 330, 473]);
    expect(() => parseAmount('a glass'), throwsFormatException);
    expect(() => parseAmount('0'), throwsRangeError);
  });

  test('log totals, undo and progress', () {
    final log = HydrationLog(goal: 2000)
      ..add(250, date: '2026-10-19')
      ..add('500ml', date: '2026-10-19')
      ..add('33cl', date: '2026-10-19')
      ..add(900, date: '2026-10-18');
    expect(log.total(date: '2026-10-19'), 1080);
    expect(log.undo(date: '2026-10-19'), const HydrationEntry('2026-10-19', 330));
    expect(log.progress(date: '2026-10-19').toJson(),
        {'date': '2026-10-19', 'total': 750, 'goal': 2000, 'percent': 38, 'remaining': 1250, 'reached': false});
    expect(log.undo(date: '2026-10-01'), isNull);
    expect(log.clearDay(date: '2026-10-18'), 1);
  });

  test('week, average and streak', () {
    final log = HydrationLog(goal: 2000);
    for (final d in ['2026-10-16', '2026-10-17', '2026-10-18']) {
      log.add(2100, date: d);
    }
    log.add(500, date: '2026-10-19');
    final w = log.week(endDate: '2026-10-19');
    expect(w.length, 7);
    expect(w[0], (date: '2026-10-19', total: 500));
    expect(w[3], (date: '2026-10-16', total: 2100));
    expect(log.streak(endDate: '2026-10-19'), 3);
    log.add(1500, date: '2026-10-19');
    expect(log.streak(endDate: '2026-10-19'), 4);
    expect(log.average(endDate: '2026-10-19'), 1186);
  });

  test('json round trip and the shared file format', () {
    final log = HydrationLog(goal: 2500)..add(400, date: '2026-10-19');
    final back = HydrationLog.fromJson(jsonEncode(log));
    expect([back.goal, back.total(date: '2026-10-19')], [2500, 400]);
    final other = HydrationLog.fromJson('{"goal":2000,"entries":[{"date":"2026-10-19","ml":500},{"date":"2026-10-19","ml":250}]}');
    expect(other.total(date: '2026-10-19'), 750);
  });
}
