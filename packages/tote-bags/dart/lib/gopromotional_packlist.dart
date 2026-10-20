/// Packing checklists: built-in templates (work day, travel, trade show, gym),
/// per-night quantities, merging without duplicates, progress, and a Markdown
/// format that round-trips, so a checklist can live in a plain .md file. Same
/// behaviour as the npm, PyPI, crates.io and RubyGems versions of
/// gopromotional-packlist.
library;

/// A template row.
class TemplateRow {
  final String name, category;
  final bool perNight;
  const TemplateRow(this.name, this.category, [this.perNight = false]);
}

/// One checklist item.
class PackItem {
  String name, category;
  int qty;
  bool packed;
  PackItem(this.name, this.category, {this.qty = 1, this.packed = false});
  Map<String, Object> toJson() => {'name': name, 'category': category, 'qty': qty, 'packed': packed};
  @override
  bool operator ==(Object other) => other is PackItem && other.name == name && other.category == category && other.qty == qty && other.packed == packed;
  @override
  int get hashCode => Object.hash(name, category, qty, packed);
  @override
  String toString() => '${packed ? '[x]' : '[ ]'} ${label(this)} ($category)';
}

const Map<String, List<TemplateRow>> templates = {
  'work-day': [
    TemplateRow('Laptop', 'Work'), TemplateRow('Laptop charger', 'Work'), TemplateRow('Phone charger', 'Work'), TemplateRow('Badge / ID', 'Work'),
    TemplateRow('Notebook', 'Work'), TemplateRow('Pen', 'Work'), TemplateRow('Headphones', 'Work'), TemplateRow('Water bottle', 'Work'),
    TemplateRow('Reusable coffee cup', 'Work'), TemplateRow('Lunch / snacks', 'Work'),
  ],
  'travel': [
    TemplateRow('Passport / ID', 'Documents'), TemplateRow('Tickets / boarding pass', 'Documents'), TemplateRow('Bank cards', 'Documents'),
    TemplateRow('T-shirts', 'Clothes', true), TemplateRow('Underwear', 'Clothes', true), TemplateRow('Socks', 'Clothes', true),
    TemplateRow('Trousers / skirt', 'Clothes'), TemplateRow('Jumper', 'Clothes'), TemplateRow('Sleepwear', 'Clothes'),
    TemplateRow('Toothbrush', 'Toiletries'), TemplateRow('Toothpaste', 'Toiletries'), TemplateRow('Deodorant', 'Toiletries'), TemplateRow('Medications', 'Toiletries'),
    TemplateRow('Phone charger', 'Tech'), TemplateRow('Power bank', 'Tech'), TemplateRow('Plug adapter', 'Tech'), TemplateRow('Headphones', 'Tech'),
    TemplateRow('Reusable water bottle', 'Other'), TemplateRow('Tote bag', 'Other'),
  ],
  'trade-show': [
    TemplateRow('Pop-up banner / stand', 'Stand'), TemplateRow('Table cloth', 'Stand'), TemplateRow('Branded giveaways', 'Stand'),
    TemplateRow('Business cards', 'Stand'), TemplateRow('Brochures / flyers', 'Stand'), TemplateRow('Lead capture sheet or scanner', 'Stand'),
    TemplateRow('Extension lead', 'Kit'), TemplateRow('Laptop / tablet', 'Kit'), TemplateRow('Phone charger', 'Kit'), TemplateRow('Gaffer tape', 'Kit'),
    TemplateRow('Scissors', 'Kit'), TemplateRow('Cable ties', 'Kit'), TemplateRow('Pens', 'Kit'),
    TemplateRow('Water bottles', 'Team'), TemplateRow('Snacks', 'Team'), TemplateRow('First-aid kit', 'Team'),
  ],
  'gym': [
    TemplateRow('Gym clothes', 'Gym'), TemplateRow('Trainers', 'Gym'), TemplateRow('Towel', 'Gym'), TemplateRow('Water bottle', 'Gym'), TemplateRow('Padlock', 'Gym'),
    TemplateRow('Headphones', 'Gym'), TemplateRow('Shower gel', 'Gym'), TemplateRow('Deodorant', 'Gym'), TemplateRow('Snack', 'Gym'),
  ],
};

String _key(String s) => s.trim().toLowerCase();

/// Build a checklist. Per-night items get nights + 1 (one spare); duplicates are merged by name (first wins).
List<PackItem> buildList({List<String> templateNames = const ['travel'], int nights = 0, List<String> extras = const []}) {
  if (nights < 0 || nights > 365) throw RangeError('nights must be an integer from 0 to 365');
  final seen = <String>{};
  final items = <PackItem>[];
  void push(PackItem it) {
    final k = _key(it.name);
    if (k.isNotEmpty && seen.add(k)) items.add(it);
  }

  for (final t in templateNames) {
    final tpl = templates[t];
    if (tpl == null) throw ArgumentError('Unknown template "$t". Choose from: ${templates.keys.join(', ')}');
    for (final r in tpl) {
      push(PackItem(r.name, r.category, qty: r.perNight && nights > 0 ? nights + 1 : 1));
    }
  }
  for (final e in extras) {
    push(PackItem(e.trim(), 'Extras'));
  }
  return items;
}

/// Find by exact name, then by a unique substring.
PackItem find(List<PackItem> items, String name) {
  final k = _key(name);
  for (final i in items) {
    if (_key(i.name) == k) return i;
  }
  final partial = items.where((i) => _key(i.name).contains(k)).toList();
  if (partial.length == 1) return partial.first;
  if (partial.length > 1) throw ArgumentError('"$name" matches ${partial.length} items: ${partial.map((i) => i.name).join(', ')}');
  throw ArgumentError('No item matches "$name"');
}

/// Mark an item packed, or toggle it when [packed] is null.
PackItem mark(List<PackItem> items, String name, [bool? packed]) {
  final it = find(items, name);
  it.packed = packed ?? !it.packed;
  return it;
}

({int packed, int total, int percent}) progress(List<PackItem> items) {
  final packed = items.where((i) => i.packed).length;
  return (packed: packed, total: items.length, percent: items.isEmpty ? 0 : (packed * 100 / items.length + 0.5).floor());
}

List<PackItem> remaining(List<PackItem> items) => items.where((i) => !i.packed).toList();

List<PackItem> filterItems(List<PackItem> items, String q) =>
    items.where((i) => _key(i.name).contains(_key(q)) || _key(i.category).contains(_key(q))).toList();

List<String> categories(List<PackItem> items) => [...{for (final i in items) i.category}];

String label(PackItem i) => i.qty > 1 ? '${i.qty} × ${i.name}' : i.name;

/// Markdown checkboxes grouped by category; [parseMarkdown] reads it back.
String toMarkdown(List<PackItem> items, [String title = 'Packing list']) {
  final out = ['# $title', ''];
  for (final c in categories(items)) {
    out.add('## $c');
    for (final i in items.where((x) => x.category == c)) {
      out.add('- [${i.packed ? 'x' : ' '}] ${label(i)}');
    }
    out.add('');
  }
  return out.join('\n');
}

String toText(List<PackItem> items) => categories(items)
    .map((c) => [c, ...items.where((x) => x.category == c).map((i) => '  ${i.packed ? '[x]' : '[ ]'} ${label(i)}')].join('\n'))
    .join('\n');

({String title, List<PackItem> items}) parseMarkdown(String md) {
  var title = 'Packing list', category = 'Items';
  final items = <PackItem>[];
  for (final raw in md.split(RegExp(r'\r?\n'))) {
    final line = raw.trim();
    RegExpMatch? m;
    if ((m = RegExp(r'^#\s+(.+)$').firstMatch(line)) != null) {
      title = m![1]!.trim();
    } else if ((m = RegExp(r'^##\s+(.+)$').firstMatch(line)) != null) {
      category = m![1]!.trim();
    } else if ((m = RegExp(r'^[-*]\s+\[([ xX])\]\s+(.+)$').firstMatch(line)) != null) {
      var name = m![2]!.trim();
      var qty = 1;
      final q = RegExp(r'^(\d+)\s*[×x]\s+(.+)$').firstMatch(name);
      if (q != null) {
        qty = int.parse(q[1]!);
        name = q[2]!.trim();
      }
      items.add(PackItem(name, category, qty: qty, packed: m[1] != ' '));
    }
  }
  return (title: title, items: items);
}
