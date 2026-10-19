'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const ep = require('../lib/index.js');
const { run } = require('../bin/cli.js');

test('countdown splits into weeks and days', () => {
  assert.deepEqual(ep.countdown('2026-12-03', '2026-10-19'),
    { eventDate: '2026-12-03', today: '2026-10-19', days: 45, weeks: 6, remainderDays: 3, status: 'upcoming' });
  assert.equal(ep.countdown('2026-10-19', '2026-10-19').status, 'today');
  const past = ep.countdown('2026-10-10', '2026-10-19');
  assert.equal(past.days, -9); assert.equal(past.status, 'past');
});

test('rejects invalid dates', () => {
  assert.throws(() => ep.toDay('2026-02-30'), RangeError);
  assert.throws(() => ep.toDay('19/10/2026'), TypeError);
});

test('weekday is ISO numbered', () => {
  assert.equal(ep.weekday(ep.toDay('2026-10-19')), 1); // Monday
  assert.equal(ep.weekday(ep.toDay('2026-12-03')), 4); // Thursday
  assert.equal(ep.weekday(ep.toDay('2026-10-25')), 7); // Sunday
});

test('timeline dates and overdue flags', () => {
  const t = ep.timeline('2026-12-03', { today: '2026-10-19' });
  assert.equal(t.length, 12);
  assert.equal(t[0].due, '2026-10-08'); assert.equal(t[0].overdue, true);
  const merch = t.find((r) => r.task.startsWith('Order branded'));
  assert.equal(merch.due, '2026-10-22'); assert.equal(merch.daysLeft, 3); assert.equal(merch.overdue, false);
  assert.equal(t[t.length - 1].due, '2026-12-05');
});

test('giveaway quantities round up', () => {
  assert.deepEqual(ep.giveawayQuantity({ attendees: 500, days: 2, uptake: 0.6, buffer: 0.1 }), { base: 600, buffer: 60, total: 660 });
  assert.deepEqual(ep.giveawayQuantity({ attendees: 250, uptake: 0.35 }), { base: 88, buffer: 9, total: 97 });
  assert.deepEqual(ep.giveawayQuantity({ attendees: 0 }), { base: 0, buffer: 0, total: 0 });
  assert.throws(() => ep.giveawayQuantity({ attendees: 10, uptake: 1.5 }), RangeError);
  assert.throws(() => ep.giveawayQuantity({ attendees: -1 }), RangeError);
});

test('order-by skips weekends', () => {
  assert.deepEqual(ep.orderByDate('2026-12-03', { today: '2026-10-19' }),
    { orderBy: '2026-11-12', workingDays: 15, daysFromToday: 24, late: false });
  // Monday event, 1 working day back = previous Friday
  assert.equal(ep.orderByDate('2026-10-26', { productionDays: 1, deliveryDays: 0, safetyDays: 0 }).orderBy, '2026-10-23');
  assert.equal(ep.orderByDate('2026-10-21', { today: '2026-10-19' }).late, true);
});

test('cli prints text and json', () => {
  const lines = []; const out = (s) => lines.push(s);
  run(['countdown', '2026-12-03', '--today', '2026-10-19'], out);
  assert.match(lines[0], /45 days to go \(6 weeks 3 days\)/);
  run(['giveaways', '--attendees', '500', '--days', '2', '--json'], out);
  assert.equal(JSON.parse(lines[1]).total, 660);
  run(['order-by', '2026-12-03', '--today', '2026-10-19'], out);
  assert.match(lines[2], /Order by 2026-11-12/);
  assert.throws(() => run(['nope'], out), /Unknown command/);
});
