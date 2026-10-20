import 'package:gopromotional_packlist/gopromotional_packlist.dart';

void main() {
  final items = buildList(templateNames: ['trade-show'], extras: ['Tote bags']);
  mark(items, 'gaffer');
  final p = progress(items);
  print('${p.packed}/${p.total} packed (${p.percent}%)');
  print(toMarkdown(items, 'Expo'));
}
