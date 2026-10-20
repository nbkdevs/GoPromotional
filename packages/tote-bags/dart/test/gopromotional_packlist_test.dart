import 'package:gopromotional_packlist/gopromotional_packlist.dart';
import 'package:test/test.dart';

void main() {
  test('templates have the expected sizes', () {
    expect(templates.map((k, v) => MapEntry(k, v.length)), {'work-day': 10, 'travel': 19, 'trade-show': 16, 'gym': 9});
  });

  test('per-night quantities include a spare', () {
    final items = buildList(templateNames: ['travel'], nights: 3);
    expect(items.firstWhere((i) => i.name == 'T-shirts').qty, 4);
    expect(items.firstWhere((i) => i.name == 'Jumper').qty, 1);
    expect(buildList().firstWhere((i) => i.name == 'Socks').qty, 1);
  });

  test('merging templates removes duplicates and adds extras', () {
    final items = buildList(templateNames: ['work-day', 'travel'], extras: ['Umbrella', 'laptop']);
    expect(items.length, 28);
    expect(items.where((i) => i.name == 'Phone charger').length, 1);
    expect(items.firstWhere((i) => i.name == 'Phone charger').category, 'Work');
    expect(items.last, PackItem('Umbrella', 'Extras'));
    expect(() => buildList(templateNames: ['moon']), throwsA(isA<ArgumentError>().having((e) => e.message, 'message', contains('Unknown template'))));
  });

  test('mark, progress, remaining and filter', () {
    final items = buildList(templateNames: ['gym']);
    mark(items, 'towel', true);
    mark(items, 'Trainers');
    expect(progress(items), (packed: 2, total: 9, percent: 22));
    expect(remaining(items).length, 7);
    expect(filterItems(items, 'sh').map((i) => i.name), ['Shower gel']);
    expect(() => mark(items, 'e'), throwsA(isA<ArgumentError>().having((e) => e.message, 'message', contains('matches'))));
    expect(() => mark(items, 'kayak'), throwsA(isA<ArgumentError>().having((e) => e.message, 'message', contains('No item'))));
  });

  test('markdown round trip', () {
    final items = buildList(templateNames: ['travel'], nights: 2);
    mark(items, 'Passport / ID', true);
    final md = toMarkdown(items, 'Lisbon');
    expect(md, startsWith('# Lisbon\n\n## Documents\n- [x] Passport / ID\n'));
    expect(md, contains('- [ ] 3 × T-shirts'));
    final back = parseMarkdown(md);
    expect(back.title, 'Lisbon');
    expect(back.items, items);
  });
}
