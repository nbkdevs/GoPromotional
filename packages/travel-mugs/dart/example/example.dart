import 'package:gopromotional_mugday/gopromotional_mugday.dart';

void main() {
  final log = CaffeineLog()
    ..add('flat-white', time: '2026-10-19T08:00', reusable: true)
    ..add('filter-coffee', time: '2026-10-19T14:00');
  final d = log.day(date: '2026-10-19');
  print('${d.total} mg today (limit ${d.limit} mg)');
  print('About ${log.inSystem(at: '2026-10-19T22:00')} mg still in your system at 22:00');
  print('Below 50 mg from about ${log.sleepReady(50)}');
}
