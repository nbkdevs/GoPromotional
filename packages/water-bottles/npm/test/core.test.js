'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const sl = require('../lib/index.js');
const { run } = require('../bin/cli.js');

test('daily goal estimate rounds to 50 ml', () => {
  assert.equal(sl.dailyGoal({ weightKg: 70 }), 2300);
  assert.equal(sl.dailyGoal({ weightKg: 70, activityMinutes: 30, hot: true }), 3150);
  assert.equal(sl.dailyGoal({ weightKg: 82.5, activityMinutes: 45 }), 3250);
  assert.throws(() => sl.dailyGoal({ weightKg: 0 }), RangeError);
});

test('parses amounts with units', () => {
  assert.equal(sl.parseAmount('500ml'), 500);
  assert.equal(sl.parseAmount('0.5l'), 500);
  assert.equal(sl.parseAmount('1.5 L'), 1500);
  assert.equal(sl.parseAmount('33cl'), 330);
  assert.equal(sl.parseAmount('330'), 330);
  assert.equal(sl.parseAmount('16oz'), 473);
  assert.throws(() => sl.parseAmount('a glass'), TypeError);
  assert.throws(() => sl.parseAmount('0'), RangeError);
});

test('log totals, undo and progress', () => {
  const log = new sl.HydrationLog({ goal: 2000 });
  log.add(250, '2026-10-19'); log.add('500ml', '2026-10-19'); log.add('33cl', '2026-10-19');
  log.add(900, '2026-10-18');
  assert.equal(log.total('2026-10-19'), 1080);
  assert.deepEqual(log.undo('2026-10-19'), { date: '2026-10-19', ml: 330 });
  assert.deepEqual(log.progress('2026-10-19'),
    { date: '2026-10-19', total: 750, goal: 2000, percent: 38, remaining: 1250, reached: false });
  assert.equal(log.undo('2026-10-01'), null);
  assert.equal(log.clearDay('2026-10-18'), 1);
});

test('week, average and streak', () => {
  const log = new sl.HydrationLog({ goal: 2000 });
  for (const d of ['2026-10-16', '2026-10-17', '2026-10-18']) log.add(2100, d);
  log.add(500, '2026-10-19');
  const w = log.week('2026-10-19');
  assert.equal(w.length, 7);
  assert.deepEqual(w[0], { date: '2026-10-19', total: 500 });
  assert.deepEqual(w[3], { date: '2026-10-16', total: 2100 });
  assert.equal(log.streak('2026-10-19'), 3);
  log.add(1500, '2026-10-19');
  assert.equal(log.streak('2026-10-19'), 4);
  assert.equal(log.average('2026-10-19'), 1186);
});

test('json round trip', () => {
  const log = new sl.HydrationLog({ goal: 2500 });
  log.add(400, '2026-10-19');
  const back = sl.HydrationLog.fromJSON(JSON.stringify(log));
  assert.equal(back.goal, 2500); assert.equal(back.total('2026-10-19'), 400);
});

test('cli writes to a log file', () => {
  const file = path.join(fs.mkdtempSync(path.join(os.tmpdir(), 'siplog-')), 'log.json');
  const lines = []; const out = (s) => lines.push(s);
  run(['add', '500ml', '--date', '2026-10-19', '--file', file], out);
  run(['add', '0.25l', '--date', '2026-10-19', '--file', file], out);
  run(['status', '--date', '2026-10-19', '--file', file, '--json'], out);
  assert.equal(JSON.parse(lines[2]).total, 750);
  run(['goal', '--weight', '70', '--activity', '30', '--hot'], out);
  assert.match(lines[3], /3150 ml/);
  run(['undo', '--date', '2026-10-19', '--file', file], out);
  assert.match(lines[4], /Removed 250 ml/);
});
