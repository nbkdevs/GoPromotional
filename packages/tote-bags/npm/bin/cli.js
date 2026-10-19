#!/usr/bin/env node
'use strict';
const fs = require('node:fs');
const pl = require('../lib/index.js');

const HELP = `gopromotional-packlist: packing checklists you can keep in a Markdown file

Usage:
  gopromotional-packlist templates
  gopromotional-packlist new [--template travel[,work-day,...]] [--nights N] [--extra "Item"]... [--title T] [--out FILE]
  gopromotional-packlist check <FILE> <item>      mark packed (unique partial names work)
  gopromotional-packlist uncheck <FILE> <item>
  gopromotional-packlist add <FILE> <item>
  gopromotional-packlist status <FILE>            progress and what is still left
Options: --json, -h/--help`;

function parseArgs(argv) {
  const pos = []; const opt = { extra: [] };
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    if (a === '--json' || a === '--help' || a === '--version') opt[a.slice(2)] = true;
    else if (a === '--extra') opt.extra.push(argv[++i]);
    else if (a.startsWith('--')) opt[a.slice(2)] = argv[++i];
    else if (a === '-h') opt.help = true;
    else pos.push(a);
  }
  return { pos, opt };
}
function read(file) { return pl.parseMarkdown(fs.readFileSync(file, 'utf8')); }
function write(file, doc) { fs.writeFileSync(file, pl.toMarkdown(doc.items, doc.title)); }

function run(argv, out = console.log) {
  const { pos, opt } = parseArgs(argv);
  if (opt.version) { out(require('../package.json').version); return 0; }
  const [cmd, file, ...rest] = pos;
  if (!cmd || opt.help) { out(HELP); return 0; }
  const name = rest.join(' ');
  switch (cmd) {
    case 'templates':
      out(opt.json ? JSON.stringify(pl.TEMPLATES, null, 2)
        : Object.entries(pl.TEMPLATES).map(([k, v]) => `${k} (${v.length} items)`).join('\n'));
      return 0;
    case 'new': {
      const items = pl.buildList({
        templates: String(opt.template || 'travel').split(',').map((s) => s.trim()).filter(Boolean),
        nights: opt.nights === undefined ? 0 : Number(opt.nights), extras: opt.extra
      });
      const md = pl.toMarkdown(items, opt.title || 'Packing list');
      if (opt.out) { fs.writeFileSync(opt.out, md); out(`Wrote ${items.length} items to ${opt.out}`); }
      else out(opt.json ? JSON.stringify(items, null, 2) : md);
      return 0;
    }
    case 'check': case 'uncheck': case 'add': {
      if (!file || !name) throw new Error(`${cmd} needs a file and an item name`);
      const doc = read(file);
      if (cmd === 'add') {
        if (doc.items.some((i) => i.name.toLowerCase() === name.toLowerCase())) throw new Error(`"${name}" is already on the list`);
        doc.items.push({ name, category: 'Extras', qty: 1, packed: false });
      } else pl.mark(doc.items, name, cmd === 'check');
      write(file, doc);
      const p = pl.progress(doc.items);
      out(`${p.packed}/${p.total} packed (${p.percent}%)`);
      return 0;
    }
    case 'status': {
      if (!file) throw new Error('status needs a file');
      const doc = read(file); const p = pl.progress(doc.items); const left = pl.remaining(doc.items);
      out(opt.json ? JSON.stringify({ ...p, remaining: left }, null, 2)
        : `${doc.title}: ${p.packed}/${p.total} packed (${p.percent}%)` + (left.length ? `\nStill to pack: ${left.map((i) => i.name).join(', ')}` : '\nAll packed.'));
      return 0;
    }
    default: throw new Error(`Unknown command "${cmd}". Run with --help.`);
  }
}
module.exports = { run };
if (require.main === module) {
  try { process.exitCode = run(process.argv.slice(2)); }
  catch (e) { console.error(`Error: ${e.message}`); process.exitCode = 2; }
}
