/*! gopromotional-packlist v1.0.1 | MIT | https://www.gopromotional.co.uk/branded-products/tote-bags */
// Packing checklists: built-in templates (work day, travel, trade show, gym),
// per-night quantities, merging without duplicates, progress, and a Markdown
// format that round-trips so a checklist can live in a plain .md file.
(function (root, factory) {
  if (typeof module === 'object' && module.exports) module.exports = factory();
  else root.PackList = factory();
})(typeof self !== 'undefined' ? self : this, function () {
  'use strict';

  // [name, category, perNight]
  const T = (rows) => Object.freeze(rows.map(([name, category, perNight = false]) => Object.freeze({ name, category, perNight })));
  const TEMPLATES = Object.freeze({
    'work-day': T([
      ['Laptop', 'Work'], ['Laptop charger', 'Work'], ['Phone charger', 'Work'], ['Badge / ID', 'Work'],
      ['Notebook', 'Work'], ['Pen', 'Work'], ['Headphones', 'Work'], ['Water bottle', 'Work'],
      ['Reusable coffee cup', 'Work'], ['Lunch / snacks', 'Work']
    ]),
    travel: T([
      ['Passport / ID', 'Documents'], ['Tickets / boarding pass', 'Documents'], ['Bank cards', 'Documents'],
      ['T-shirts', 'Clothes', true], ['Underwear', 'Clothes', true], ['Socks', 'Clothes', true],
      ['Trousers / skirt', 'Clothes'], ['Jumper', 'Clothes'], ['Sleepwear', 'Clothes'],
      ['Toothbrush', 'Toiletries'], ['Toothpaste', 'Toiletries'], ['Deodorant', 'Toiletries'], ['Medications', 'Toiletries'],
      ['Phone charger', 'Tech'], ['Power bank', 'Tech'], ['Plug adapter', 'Tech'], ['Headphones', 'Tech'],
      ['Reusable water bottle', 'Other'], ['Tote bag', 'Other']
    ]),
    'trade-show': T([
      ['Pop-up banner / stand', 'Stand'], ['Table cloth', 'Stand'], ['Branded giveaways', 'Stand'],
      ['Business cards', 'Stand'], ['Brochures / flyers', 'Stand'], ['Lead capture sheet or scanner', 'Stand'],
      ['Extension lead', 'Kit'], ['Laptop / tablet', 'Kit'], ['Phone charger', 'Kit'], ['Gaffer tape', 'Kit'],
      ['Scissors', 'Kit'], ['Cable ties', 'Kit'], ['Pens', 'Kit'],
      ['Water bottles', 'Team'], ['Snacks', 'Team'], ['First-aid kit', 'Team']
    ]),
    gym: T([
      ['Gym clothes', 'Gym'], ['Trainers', 'Gym'], ['Towel', 'Gym'], ['Water bottle', 'Gym'], ['Padlock', 'Gym'],
      ['Headphones', 'Gym'], ['Shower gel', 'Gym'], ['Deodorant', 'Gym'], ['Snack', 'Gym']
    ])
  });

  const key = (name) => String(name).trim().toLowerCase();

  /**
   * Build a checklist from one or more templates plus extra items.
   * Per-night items get nights + 1 (one spare); duplicates are merged by name (first wins).
   */
  function buildList({ templates = ['travel'], nights = 0, extras = [] } = {}) {
    if (!Number.isInteger(nights) || nights < 0 || nights > 365) throw new RangeError('nights must be an integer from 0 to 365');
    const seen = new Set(); const items = [];
    const push = (it) => { const k = key(it.name); if (!k || seen.has(k)) return; seen.add(k); items.push(it); };
    for (const t of templates) {
      const tpl = TEMPLATES[t];
      if (!tpl) throw new RangeError(`Unknown template "${t}". Choose from: ${Object.keys(TEMPLATES).join(', ')}`);
      for (const r of tpl) push({ name: r.name, category: r.category, qty: r.perNight && nights > 0 ? nights + 1 : 1, packed: false });
    }
    for (const e of extras) push({ name: String(e).trim(), category: 'Extras', qty: 1, packed: false });
    return items;
  }

  function find(items, name) {
    const k = key(name);
    const exact = items.find((i) => key(i.name) === k);
    if (exact) return exact;
    const partial = items.filter((i) => key(i.name).includes(k));
    if (partial.length === 1) return partial[0];
    if (partial.length > 1) throw new RangeError(`"${name}" matches ${partial.length} items: ${partial.map((i) => i.name).join(', ')}`);
    throw new RangeError(`No item matches "${name}"`);
  }
  /** Mark an item packed (or toggle when packed is undefined). Matches exact name, then a unique substring. */
  function mark(items, name, packed) {
    const it = find(items, name);
    it.packed = packed === undefined ? !it.packed : !!packed;
    return it;
  }
  function progress(items) {
    const packed = items.filter((i) => i.packed).length;
    return { packed, total: items.length, percent: items.length ? Math.floor((packed * 100) / items.length + 0.5) : 0 };
  }
  const remaining = (items) => items.filter((i) => !i.packed);
  const filterItems = (items, q) => items.filter((i) => key(i.name).includes(key(q)) || key(i.category).includes(key(q)));

  function categories(items) {
    const order = [];
    for (const i of items) if (!order.includes(i.category)) order.push(i.category);
    return order;
  }
  const label = (i) => (i.qty > 1 ? `${i.qty} × ${i.name}` : i.name);

  /** Render as Markdown checkboxes grouped by category. parseMarkdown() reads it back. */
  function toMarkdown(items, title = 'Packing list') {
    const out = [`# ${title}`, ''];
    for (const c of categories(items)) {
      out.push(`## ${c}`);
      for (const i of items.filter((x) => x.category === c)) out.push(`- [${i.packed ? 'x' : ' '}] ${label(i)}`);
      out.push('');
    }
    return out.join('\n');
  }
  function toText(items) {
    return categories(items).map((c) => [c, ...items.filter((x) => x.category === c).map((i) => `  ${i.packed ? '[x]' : '[ ]'} ${label(i)}`)].join('\n')).join('\n');
  }
  function parseMarkdown(md) {
    let title = 'Packing list'; let category = 'Items'; const items = [];
    for (const raw of String(md).split(/\r?\n/)) {
      const line = raw.trim();
      let m;
      if ((m = /^#\s+(.+)$/.exec(line))) title = m[1].trim();
      else if ((m = /^##\s+(.+)$/.exec(line))) category = m[1].trim();
      else if ((m = /^[-*]\s+\[([ xX])\]\s+(.+)$/.exec(line))) {
        let name = m[2].trim(); let qty = 1; let q;
        if ((q = /^(\d+)\s*[×x]\s+(.+)$/.exec(name))) { qty = Number(q[1]); name = q[2].trim(); }
        items.push({ name, category, qty, packed: m[1] !== ' ' });
      }
    }
    return { title, items };
  }

  return { TEMPLATES, buildList, mark, progress, remaining, filterItems, toMarkdown, toText, parseMarkdown, categories };
});
