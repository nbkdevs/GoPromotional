import 'dart:convert';

import 'package:gopromotional_mugday/gopromotional_mugday.dart';
import 'package:test/test.dart';

CaffeineLog sample() => CaffeineLog()
  ..add('flat-white', time: '2026-10-19T08:00', reusable: true)
  ..add('tea', time: '2026-10-19T11:00', reusable: true)
  ..add('Filter coffee', time: '2026-10-19T14:00');

void main() {
  test('drink table and lookup', () {
    expect(drinks.length, 16);
    expect(lookupDrink('Flat White'), (drink: 'flat-white', mg: 126, ml: 220));
    expect(() => lookupDrink('mystery'), throwsArgumentError);
  });

  test('daily total against limits', () {
    final log = sample();
    expect(log.day(date: '2026-10-19'), (date: '2026-10-19', drinks: 3, total: 341, limit: 400, remaining: 59, over: 0));
    expect(log.day(date: '2026-10-19', limit: limits['pregnant']), (date: '2026-10-19', drinks: 3, total: 341, limit: 200, remaining: 0, over: 141));
    expect(log.day(date: '2026-10-18').total, 0);
  });

  test('caffeine still in system (5 h half-life)', () {
    final log = sample();
    expect(log.inSystem(at: '2026-10-19T07:59'), 0);
    expect(log.inSystem(at: '2026-10-19T14:00'), 244);
    expect(log.inSystem(at: '2026-10-19T22:00'), 81);
  });

  test('sleep-ready time rounds up to 15 minutes', () {
    final log = sample();
    expect(log.sleepReady(50), '2026-10-20T01:30');
    expect(log.sleepReady(100), '2026-10-19T20:30');
    expect(log.sleepReady(500), '2026-10-19T14:00');
    expect(CaffeineLog().sleepReady(), isNull);
  });

  test('custom mg, undo, cups and json', () {
    final log = sample()..add('Office machine', time: '2026-10-12T09:00', mg: 90);
    expect(log.cups(endDate: '2026-10-19'), (reusable: 2, disposable: 1, rate: 67));
    expect(log.cups(endDate: '2026-10-12'), (reusable: 0, disposable: 1, rate: 0));
    expect(log.undo()!.drink, 'filter-coffee');
    expect(CaffeineLog.fromJson(jsonEncode(log)).day(date: '2026-10-19').total, 201);
    expect(() => log.add('espresso', time: '2026-13-01T08:00'), throwsRangeError);
  });
}
