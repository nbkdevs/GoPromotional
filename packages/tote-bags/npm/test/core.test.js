'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const pl = require('../lib/index.js');
const { run } = require('../bin/cli.js');

test('templates have the expected sizes', () => {
  assert.deepEqual(Object.fromEntries(Object.entries(pl.TEMPLATES).map(([k, v]) => [k, v.length])),
    { 'work-day': 10, travel: 19, 'trade-show': 16, gym: 9 });
});

test('per-night quantities include a spare', () => {
  const items = pl.buildList({ templates: ['travel'], nights: 3 });
  assert.equal(items.find((i) => i.name === 'T-shirts').qty, 4);
  assert.equal(items.find((i) => i.name === 'Jumper').qty, 1);
  assert.equal(pl.buildList({ templates: ['travel'] }).find((i) => i.name === 'Socks').qty, 1);
});

test('merging templates removes duplicates and adds extras', () => {
  const items = pl.buildList({ templates: ['work-day', 'travel'], extras: ['Umbrella', 'laptop'] });
  assert.equal(items.length, 28);
  assert.equal(items.filter((i) => i.name === 'Phone charger').length, 1);
  assert.equal(items.find((i) => i.name === 'Phone charger').category, 'Work');
  assert.deepEqual(items[items.length - 1], { name: 'Umbrella', category: 'Extras', qty: 1, packed: false });
  assert.throws(() => pl.buildList({ templates: ['moon'] }), /Unknown template/);
});

test('mark, progress, remaining and filter', () => {
  const items = pl.buildList({ templates: ['gym'] });
  pl.mark(items, 'towel', true);
  pl.mark(items, 'Trainers');
  assert.deepEqual(pl.progress(items), { packed: 2, total: 9, percent: 22 });
  assert.equal(pl.remaining(items).length, 7);
  assert.deepEqual(pl.filterItems(items, 'sh').map((i) => i.name), ['Shower gel']);
  assert.throws(() => pl.mark(items, 'e'), /matches/);
  assert.throws(() => pl.mark(items, 'kayak'), /No item/);
});

test('markdown round trip', () => {
  const items = pl.buildList({ templates: ['travel'], nights: 2 });
  pl.mark(items, 'Passport / ID', true);
  const md = pl.toMarkdown(items, 'Lisbon');
  assert.match(md, /^# Lisbon\n\n## Documents\n- \[x\] Passport \/ ID\n/);
  assert.match(md, /- \[ \] 3 × T-shirts/);
  const back = pl.parseMarkdown(md);
  assert.equal(back.title, 'Lisbon');
  assert.deepEqual(back.items, items);
});

test('cli new/check/status on a file', () => {
  const file = path.join(fs.mkdtempSync(path.join(os.tmpdir(), 'pack-')), 'show.md');
  const lines = []; const out = (s) => lines.push(s);
  run(['new', '--template', 'trade-show', '--extra', 'Tote bags', '--title', 'Expo', '--out', file], out);
  assert.equal(lines[0], `Wrote 17 items to ${file}`);
  run(['check', file, 'gaffer'], out);
  assert.equal(lines[1], '1/17 packed (6%)');
  run(['status', file, '--json'], out);
  assert.equal(JSON.parse(lines[2]).remaining.length, 16);
  assert.match(fs.readFileSync(file, 'utf8'), /- \[x\] Gaffer tape/);
});
