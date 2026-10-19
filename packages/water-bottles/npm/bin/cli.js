#!/usr/bin/env node
'use strict';
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const sl = require('../lib/index.js');

const HELP = `gopromotional-siplog: daily water goal calculator and hydration log

Usage:
  gopromotional-siplog goal --weight KG [--activity MIN] [--hot]
  gopromotional-siplog add <amount> [--date YYYY-MM-DD]     e.g. 250, 500ml, 0.5l, 33cl, 16oz
  gopromotional-siplog status [--date YYYY-MM-DD]
  gopromotional-siplog week [--date YYYY-MM-DD]
  gopromotional-siplog undo [--date YYYY-MM-DD]
  gopromotional-siplog set-goal <ml>

Options:
  --file PATH  log file (default $GOPROMOTIONAL_SIPLOG_FILE or ~/.gopromotional-siplog.json)
  --json       print JSON
  -h, --help   show help`;

function parseArgs(argv) {
  const pos = []; const opt = {};
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    if (['--json', '--hot', '--help', '--version'].includes(a)) opt[a.slice(2)] = true;
    else if (a.startsWith('--')) { opt[a.slice(2)] = argv[++i]; }
    else if (a === '-h') opt.help = true;
    else pos.push(a);
  }
  return { pos, opt };
}
const defaultFile = () => process.env.GOPROMOTIONAL_SIPLOG_FILE || path.join(os.homedir(), '.gopromotional-siplog.json');
function load(file) { return fs.existsSync(file) ? sl.HydrationLog.fromJSON(fs.readFileSync(file, 'utf8')) : new sl.HydrationLog(); }
function save(file, log) { fs.writeFileSync(file, JSON.stringify(log.toJSON(), null, 2) + '\n'); }
const bar = (pct) => '#'.repeat(Math.min(20, Math.round(pct / 5))).padEnd(20, '.');

function run(argv, out = console.log) {
  const { pos, opt } = parseArgs(argv);
  if (opt.version) { out(require('../package.json').version); return 0; }
  const cmd = pos[0];
  if (!cmd || opt.help) { out(HELP); return 0; }
  const file = opt.file || defaultFile();
  const date = opt.date || sl.todayISO();
  let result; let text;
  if (cmd === 'goal') {
    const weightKg = Number(opt.weight);
    result = { goal: sl.dailyGoal({ weightKg, activityMinutes: Number(opt.activity || 0), hot: !!opt.hot }) };
    text = `Suggested daily water goal: ${result.goal} ml (a general guide, not medical advice).`;
  } else {
    const log = load(file);
    if (cmd === 'add') {
      if (!pos[1]) throw new Error('add needs an amount');
      log.add(pos[1], date); save(file, log);
      result = log.progress(date);
      text = `Added ${sl.parseAmount(pos[1])} ml. ${result.total}/${result.goal} ml (${result.percent}%).`;
    } else if (cmd === 'status') {
      result = log.progress(date);
      text = `${result.date}: ${result.total}/${result.goal} ml [${bar(result.percent)}] ${result.percent}%` +
        (result.reached ? ' - goal reached' : ` - ${result.remaining} ml to go`) + `\nStreak: ${log.streak(date)} day(s)`;
    } else if (cmd === 'week') {
      result = log.week(date);
      text = result.map((d) => `${d.date}  ${String(d.total).padStart(5)} ml  ${d.total >= log.goal ? 'goal' : ''}`).join('\n') +
        `\nAverage: ${log.average(date)} ml/day`;
    } else if (cmd === 'undo') {
      result = log.undo(date); save(file, log);
      text = result ? `Removed ${result.ml} ml from ${result.date}.` : 'Nothing to undo.';
    } else if (cmd === 'set-goal') {
      const g = sl.parseAmount(pos[1]);
      if (g < 500 || g > 6000) throw new Error('goal must be between 500 and 6000 ml');
      log.goal = g; save(file, log);
      result = { goal: g }; text = `Daily goal set to ${g} ml.`;
    } else throw new Error(`Unknown command "${cmd}". Run with --help.`);
  }
  out(opt.json ? JSON.stringify(result, null, 2) : text);
  return 0;
}
module.exports = { run };
if (require.main === module) {
  try { process.exitCode = run(process.argv.slice(2)); }
  catch (e) { console.error(`Error: ${e.message}`); process.exitCode = 2; }
}
