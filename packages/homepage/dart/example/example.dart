import 'package:gopromotional_event_planner/gopromotional_event_planner.dart';

void main() {
  final c = countdown('2026-12-03', today: '2026-10-19');
  print('${c.days} days to go (${c.weeks} weeks ${c.remainderDays} days)');
  print('Giveaways: ${giveawayQuantity(attendees: 500, days: 2).total} items');
  print('Order merchandise by ${orderByDate('2026-12-03').orderBy}');
  for (final row in timeline('2026-12-03', today: '2026-10-19')) {
    print('${row.due}  ${row.task}${row.overdue == true ? ' (overdue)' : ''}');
  }
}
