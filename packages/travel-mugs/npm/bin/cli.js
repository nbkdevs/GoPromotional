#!/usr/bin/env node
'use strict';
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const md = require('../lib/index.js');

const HELP = `gopromotional-mugday: caffeine and coffee intake tracker

Usage:
  gopromotional-mugday drinks                         typical caffeine per drink
  gopromotional-mugday add <drink> [--time YYYY-MM-DDTHH:MM] [--mg N] [--reusable]
  gopromotional-mugday today [--date YYYY-MM-DD] [--limit 400]
  gopromotional-mugday in-system [--at YYYY-MM-DDTHH:MM]
  gopromotional-mugday sleep-ready [--threshold 50]
  gopromotional-mugday cups [--date YYYY-MM-DD]       reusable vs disposable, last 7 days
  gopromotional-mugday undo

Options:
  --file PATH  log file (default $GOPROMOTIONAL_MUGDAY_FILE or ~/.gopromotional-mugday.json)
  --json       print JSON
Caffeine values are typical figures; real drinks vary. Not medical advice.`;

function parseArgs(argv) {
  const pos = []; const opt = {};
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    if (['--json', '--reusable', '--help', '--version'].includes(a)) opt[a.slice(2)] = true;
    else if (a.startsWith('--')) opt[a.slice(2)] = argv[++i];
    else if (a === '-h') opt.help = true;
    else pos.push(a);
  }
  return { pos, opt };
}
const defaultFile = () => process.env.GOPROMOTIONAL_MUGDAY_FILE || path.join(os.homedir(), '.gopromotional-mugday.json');
const load = (f) => (fs.existsSync(f) ? md.CaffeineLog.fromJSON(fs.readFileSync(f, 'utf8')) : new md.CaffeineLog());
const save = (f, log) => fs.writeFileSync(f, JSON.stringify(log.toJSON(), null, 2) + '\n');

function run(argv, out = console.log) {
  const { pos, opt } = parseArgs(argv);
  if (opt.version) { out(require('../package.json').version); return 0; }
  const cmd = pos[0];
  if (!cmd || opt.help) { out(HELP); return 0; }
  if (cmd === 'drinks') {
    out(opt.json ? JSON.stringify(md.DRINKS, null, 2)
      : Object.entries(md.DRINKS).map(([k, v]) => `${k.padEnd(20)} ${String(v.mg).padStart(4)} mg${v.ml ? `  (${v.ml} ml)` : ''}`).join('\n'));
    return 0;
  }
  const file = opt.file || defaultFile();
  const log = load(file);
  let result; let text;
  switch (cmd) {
    case 'add': {
      if (!pos[1]) throw new Error('add needs a drink name');
      const e = log.add(pos.slice(1).join(' '), { time: opt.time || md.nowLocal(), mg: opt.mg === undefined ? undefined : Number(opt.mg), reusable: !!opt.reusable });
      save(file, log);
      result = log.day(e.time.slice(0, 10));
      text = `Logged ${e.drink} (${e.mg} mg${e.reusable ? ', reusable cup' : ''}). Today: ${result.total}/${result.limit} mg.`;
      break;
    }
    case 'today': {
      result = log.day(opt.date || md.nowLocal().slice(0, 10), opt.limit ? Number(opt.limit) : log.limit);
      text = `${result.date}: ${result.total} mg from ${result.drinks} drink(s); ` +
        (result.over ? `${result.over} mg over the ${result.limit} mg limit.` : `${result.remaining} mg left of ${result.limit} mg.`);
      break;
    }
    case 'in-system': {
      const at = opt.at || md.nowLocal();
      result = { at, mg: log.inSystem(at) };
      text = `About ${result.mg} mg of caffeine still in your system at ${at} (half-life ${log.halfLifeHours} h).`;
      break;
    }
    case 'sleep-ready': {
      const threshold = opt.threshold ? Number(opt.threshold) : 50;
      result = { threshold, time: log.sleepReady(threshold) };
      text = result.time ? `Below ${threshold} mg from about ${result.time.replace('T', ' ')}.` : 'No drinks logged.';
      break;
    }
    case 'cups': {
      result = log.cups(opt.date || md.nowLocal().slice(0, 10));
      text = `Last 7 days: ${result.reusable} reusable, ${result.disposable} disposable (${result.rate}% reusable).`;
      break;
    }
    case 'undo': {
      result = log.undo(); save(file, log);
      text = result ? `Removed ${result.drink} at ${result.time}.` : 'Nothing to undo.';
      break;
    }
    default: throw new Error(`Unknown command "${cmd}". Run with --help.`);
  }
  out(opt.json ? JSON.stringify(result, null, 2) : text);
  return 0;
}
module.exports = { run };
if (require.main === module) {
  try { process.exitCode = run(process.argv.slice(2)); }
  catch (e) { console.error(`Error: ${e.message}`); process.exitCode = 2; }
}
