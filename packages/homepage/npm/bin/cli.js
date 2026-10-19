#!/usr/bin/env node
'use strict';
const ep = require('../lib/index.js');

const HELP = `gopromotional-event-planner: event countdown, planning timeline, giveaway quantities and order-by dates

Usage:
  gopromotional-event-planner countdown <YYYY-MM-DD> [--today YYYY-MM-DD]
  gopromotional-event-planner timeline  <YYYY-MM-DD> [--today YYYY-MM-DD]
  gopromotional-event-planner giveaways --attendees N [--days 1] [--uptake 0.6] [--per-person 1] [--buffer 0.1]
  gopromotional-event-planner order-by  <YYYY-MM-DD> [--production 10] [--delivery 3] [--safety 2] [--today YYYY-MM-DD]

Options:
  --json      print JSON instead of text
  -h, --help  show this help
  -v, --version`;

function parseArgs(argv) {
  const pos = []; const opt = {};
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    if (a.startsWith('--')) {
      const key = a.slice(2);
      if (key === 'json' || key === 'help' || key === 'version') opt[key] = true;
      else { opt[key] = argv[i + 1]; i++; }
    } else if (a === '-h') opt.help = true;
    else if (a === '-v') opt.version = true;
    else pos.push(a);
  }
  return { pos, opt };
}
function num(v, name, def) {
  if (v === undefined) return def;
  const n = Number(v);
  if (!Number.isFinite(n)) throw new Error(`--${name} must be a number`);
  return n;
}

function run(argv, out = console.log) {
  const { pos, opt } = parseArgs(argv);
  if (opt.version) { out(require('../package.json').version); return 0; }
  const cmd = pos[0];
  if (!cmd || opt.help) { out(HELP); return cmd || opt.help ? 0 : 1; }
  const today = opt.today || ep.todayISO();
  let result; let text;
  switch (cmd) {
    case 'countdown': {
      if (!pos[1]) throw new Error('countdown needs an event date');
      result = ep.countdown(pos[1], today);
      text = result.status === 'today' ? `${result.eventDate} is today.`
        : result.status === 'upcoming'
          ? `${result.days} days to go (${result.weeks} weeks ${result.remainderDays} days) until ${result.eventDate}.`
          : `${result.eventDate} was ${-result.days} days ago.`;
      break;
    }
    case 'timeline': {
      if (!pos[1]) throw new Error('timeline needs an event date');
      result = ep.timeline(pos[1], { today });
      text = result.map((r) => `${r.overdue ? '!' : ' '} ${r.due}  ${r.task}${r.overdue ? '  (overdue)' : ''}`).join('\n');
      break;
    }
    case 'giveaways': {
      result = ep.giveawayQuantity({
        attendees: num(opt.attendees, 'attendees'), days: num(opt.days, 'days', 1),
        uptake: num(opt.uptake, 'uptake', 0.6), perPerson: num(opt['per-person'], 'per-person', 1),
        buffer: num(opt.buffer, 'buffer', 0.1)
      });
      text = `Order ${result.total} items (${result.base} expected + ${result.buffer} buffer).`;
      break;
    }
    case 'order-by': {
      if (!pos[1]) throw new Error('order-by needs an event date');
      result = ep.orderByDate(pos[1], {
        productionDays: num(opt.production, 'production', 10), deliveryDays: num(opt.delivery, 'delivery', 3),
        safetyDays: num(opt.safety, 'safety', 2), today
      });
      text = `Order by ${result.orderBy} (${result.workingDays} working days before the event)` +
        (result.late ? ` - that is ${-result.daysFromToday} days ago, so ask about express production.` : `, ${result.daysFromToday} days from today.`);
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
