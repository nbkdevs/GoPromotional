# GoPromotional.EventPlanner

Plan an event or trade show from the command line or your code: a countdown, a 12-step planning timeline with due dates, an estimate of how many giveaway items to order, and the latest order date for printed merchandise in working days.

- **Countdown** to the event in days, and in weeks + days.
- **Planning timeline**: 12 tasks from 8 weeks out to the post-event follow-up, each with a due date, days left and an overdue flag.
- **Giveaway quantity**: attendees × days × uptake × items per person, plus a buffer, rounded up.
- **Order-by date**: production + delivery + safety margin counted back in working days (Mon–Fri).

## Install

```sh
dotnet add package GoPromotional.EventPlanner
```

## Usage

```csharp
using GoPromotional;

var c = EventPlanner.Countdown("2026-12-03", today: "2026-10-19");
Console.WriteLine($"{c.Days} days to go ({c.Weeks} weeks {c.RemainderDays} days)");   // 45 days to go (6 weeks 3 days)

var q = EventPlanner.GiveawayQuantity(attendees: 500, days: 2);                      // Base 600, Buffer 60, Total 660
var o = EventPlanner.OrderByDate("2026-12-03", today: "2026-10-19");                 // OrderByDate "2026-11-12", Late false

foreach (var row in EventPlanner.Timeline("2026-12-03", today: "2026-10-19"))
    Console.WriteLine($"{row.Due}  {row.Task}{(row.Overdue == true ? " (overdue)" : "")}");
```

Targets .NET Standard 2.0 and .NET 8. Run the tests with `dotnet test tests`.

## Links

- Website: [branded merchandise](https://www.gopromotional.co.uk/)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/homepage/nuget`)
- Also available on npm, PyPI, crates.io and RubyGems as `gopromotional-event-planner`, and as the Docker image `iamnazzal/gopromotional-event-planner`
- Licence: MIT
