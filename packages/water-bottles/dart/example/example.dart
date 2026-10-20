import 'package:gopromotional_siplog/gopromotional_siplog.dart';

void main() {
  print('Suggested goal: ${dailyGoal(weightKg: 70, activityMinutes: 30, hot: true)} ml');
  final log = HydrationLog(goal: 2000)
    ..add('500ml', date: '2026-10-19')
    ..add('33cl', date: '2026-10-19');
  final p = log.progress(date: '2026-10-19');
  print('${p.total} ml today, ${p.percent}% of ${p.goal} ml, ${p.remaining} ml to go');
  print(log.toJson());
}
