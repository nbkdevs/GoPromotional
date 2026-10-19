'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const md = require('../lib/index.js');
const { run } = require('../bin/cli.js');

function sample() {
  const log = new md.CaffeineLog();
  log.add('flat-white', { time: '2026-10-19T08:00', reusable: true });
  log.add('tea', { time: '2026-10-19T11:00', reusable: true });
  log.add('Filter coffee', { time: '2026-10-19T14:00' });
  return log;
}

test('drink table and lookup', () => {
  assert.equal(Object.keys(md.DRINKS).length, 16);
  assert.deepEqual(md.lookupDrink('Flat White'), { drink: 'flat-white', mg: 126, ml: 220 });
  assert.throws(() => md.lookupDrink('mystery'), /Unknown drink/);
});

test('daily total against limits', () => {
  const log = sample();
  assert.deepEqual(log.day('2026-10-19'), { date: '2026-10-19', drinks: 3, total: 341, limit: 400, remaining: 59, over: 0 });
  assert.deepEqual(log.day('2026-10-19', md.LIMITS.pregnant), { date: '2026-10-19', drinks: 3, total: 341, limit: 200, remaining: 0, over: 141 });
  assert.equal(log.day('2026-10-18').total, 0);
});

test('caffeine still in system (5 h half-life)', () => {
  const log = sample();
  assert.equal(log.inSystem('2026-10-19T07:59'), 0);
  assert.equal(log.inSystem('2026-10-19T14:00'), 244);
  assert.equal(log.inSystem('2026-10-19T22:00'), 81);
});

test('sleep-ready time rounds up to 15 minutes', () => {
  const log = sample();
  assert.equal(log.sleepReady(50), '2026-10-20T01:30');
  assert.equal(log.sleepReady(100), '2026-10-19T20:30');
  assert.equal(log.sleepReady(500), '2026-10-19T14:00');
  assert.equal(new md.CaffeineLog().sleepReady(), null);
});

test('custom mg, undo, cups and json', () => {
  const log = sample();
  log.add('Office machine', { time: '2026-10-12T09:00', mg: 90 });
  assert.deepEqual(log.cups('2026-10-19'), { reusable: 2, disposable: 1, rate: 67 }); // 12th is outside 13–19
  assert.deepEqual(log.cups('2026-10-12'), { reusable: 0, disposable: 1, rate: 0 });
  assert.equal(log.undo().drink, 'filter-coffee'); // entries are kept in time order
  const back = md.CaffeineLog.fromJSON(JSON.stringify(log));
  assert.equal(back.day('2026-10-19').total, 201);
  assert.throws(() => log.add('espresso', { time: '2026-13-01T08:00' }), RangeError);
});

test('cli add/today/sleep-ready', () => {
  const file = path.join(fs.mkdtempSync(path.join(os.tmpdir(), 'mug-')), 'log.json');
  const lines = []; const out = (s) => lines.push(s);
  run(['add', 'flat-white', '--time', '2026-10-19T08:00', '--reusable', '--file', file], out);
  run(['add', 'tea', '--time', '2026-10-19T11:00', '--reusable', '--file', file], out);
  run(['add', 'filter-coffee', '--time', '2026-10-19T14:00', '--file', file], out);
  assert.equal(lines[2], 'Logged filter-coffee (140 mg). Today: 341/400 mg.');
  run(['today', '--date', '2026-10-19', '--limit', '200', '--file', file], out);
  assert.match(lines[3], /141 mg over the 200 mg limit/);
  run(['sleep-ready', '--file', file, '--json'], out);
  assert.equal(JSON.parse(lines[4]).time, '2026-10-20T01:30');
  run(['in-system', '--at', '2026-10-19T22:00', '--file', file], out);
  assert.match(lines[5], /About 81 mg/);
});
