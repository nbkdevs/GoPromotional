# GoPromotional.SipLog

A small hydration tracker: estimate a daily water goal from weight, exercise and weather, log drinks in ml, cl, litres or US fl oz, and see today's progress, the last 7 days, your average and your streak. The log is a plain JSON file.

- **Daily goal** estimate: weight × 33 ml + 12 ml per active minute (+500 ml in hot weather), rounded to 50 ml. A general guide, not medical advice.
- **Flexible amounts**: `250`, `500ml`, `33cl`, `0.5l`, `16oz`.
- **Progress**, **7-day history**, **average** and **streak** of days on target.
- **Undo** the last entry, **set** your own goal.
- Plain **JSON log** (`{goal, entries:[{date, ml}]}`), the same format in every language version.

## Install

```sh
dotnet add package GoPromotional.SipLog
```

## Usage

```csharp
using GoPromotional;

SipLog.DailyGoal(weightKg: 70, activityMinutes: 30, hot: true);   // 3150
SipLog.ParseAmount("33cl");                                        // 330

var log = SipLog.FromJson(File.ReadAllText("log.json"));          // same file as the CLI versions
log.Add("500ml", "2026-10-19");
var p = log.Progress("2026-10-19");                                // Total 500, Percent 25, Remaining 1500
Console.WriteLine($"{p.Total} ml, streak {log.Streak("2026-10-19")} days");
File.WriteAllText("log.json", log.ToJson());
```

Targets .NET Standard 2.0 and .NET 8. Run the tests with `dotnet test tests`.

## Links

- Website: [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/water-bottles/nuget`)
- Also available on npm, PyPI, crates.io and RubyGems as `gopromotional-siplog`, and as the Docker image `iamnazzal/gopromotional-siplog`
- Licence: MIT
