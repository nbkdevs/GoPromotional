using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text.RegularExpressions;

namespace GoPromotional
{
    /// <summary>Countdown to an event, split into weeks and days.</summary>
    public sealed class CountdownResult
    {
        public string EventDate { get; set; } = "";
        public string Today { get; set; } = "";
        public int Days { get; set; }
        public int Weeks { get; set; }
        public int RemainderDays { get; set; }
        /// <summary>"upcoming", "today" or "past".</summary>
        public string Status { get; set; } = "";
    }

    /// <summary>One task of the planning timeline.</summary>
    public sealed class TimelineRow
    {
        public string Task { get; set; } = "";
        public string Category { get; set; } = "";
        public int OffsetDays { get; set; }
        public string Due { get; set; } = "";
        public int? DaysLeft { get; set; }
        public bool? Overdue { get; set; }
    }

    /// <summary>Giveaway order quantity: base + buffer = total.</summary>
    public sealed class GiveawayResult
    {
        public int Base { get; set; }
        public int Buffer { get; set; }
        public int Total { get; set; }
    }

    /// <summary>Latest order date for printed merchandise.</summary>
    public sealed class OrderByResult
    {
        public string OrderByDate { get; set; } = "";
        public int WorkingDays { get; set; }
        public int? DaysFromToday { get; set; }
        public bool? Late { get; set; }
    }

    /// <summary>
    /// Event planning helpers: a countdown, a planning timeline with due dates, a giveaway
    /// quantity estimator and the latest order date for printed merchandise (working days).
    /// Same behaviour as the npm, PyPI, crates.io and RubyGems versions of gopromotional-event-planner.
    /// </summary>
    public static class EventPlanner
    {
        private static readonly DateTime Epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
        private static readonly Regex DateRe = new Regex(@"^(\d{4})-(\d{2})-(\d{2})$");

        /// <summary>Default planning template: (days before the event, category, task). Negative = after.</summary>
        public static readonly IReadOnlyList<(int OffsetDays, string Category, string Task)> TimelineTemplate = new[]
        {
            (56, "planning", "Confirm budget and attendee estimate"),
            (49, "venue", "Book the venue"),
            (42, "merchandise", "Order branded merchandise and giveaways"),
            (35, "comms", "Send invitations or open registration"),
            (28, "programme", "Confirm speakers and agenda"),
            (21, "venue", "Arrange catering"),
            (14, "merchandise", "Approve print proofs and signage"),
            (7, "comms", "Send a reminder to attendees"),
            (3, "logistics", "Confirm deliveries and the staffing rota"),
            (1, "logistics", "Pack the event kit and giveaways"),
            (0, "logistics", "Event day: set up the stand and registration"),
            (-2, "comms", "Send thank-you and follow-up messages"),
        };

        /// <summary>Parse "YYYY-MM-DD" into a day number (days since 1970-01-01).</summary>
        public static int ToDay(string date)
        {
            var s = (date ?? "").Trim();
            var m = DateRe.Match(s);
            if (!m.Success) throw new FormatException($"Invalid date \"{date}\" (expected YYYY-MM-DD)");
            int y = int.Parse(m.Groups[1].Value), mo = int.Parse(m.Groups[2].Value), d = int.Parse(m.Groups[3].Value);
            if (y < 1 || mo < 1 || mo > 12 || d < 1 || d > DateTime.DaysInMonth(y, mo))
                throw new ArgumentOutOfRangeException(nameof(date), $"Invalid calendar date \"{date}\"");
            return (int)(new DateTime(y, mo, d, 0, 0, 0, DateTimeKind.Utc) - Epoch).TotalDays;
        }

        /// <summary>Format a day number as "YYYY-MM-DD".</summary>
        public static string FromDay(int day) => Epoch.AddDays(day).ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);

        /// <summary>ISO weekday: 1 = Monday ... 7 = Sunday.</summary>
        public static int Weekday(int day)
        {
            var w = (((day + 4) % 7) + 7) % 7;
            return w == 0 ? 7 : w;
        }

        public static string TodayIso() => DateTime.Now.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);

        public static CountdownResult Countdown(string eventDate, string? today = null)
        {
            int ev = ToDay(eventDate), t = ToDay(today ?? TodayIso());
            int days = ev - t, abs = Math.Abs(days);
            return new CountdownResult
            {
                EventDate = FromDay(ev), Today = FromDay(t), Days = days, Weeks = abs / 7, RemainderDays = abs % 7,
                Status = days > 0 ? "upcoming" : days == 0 ? "today" : "past",
            };
        }

        /// <summary>Planning timeline with a due date per task, sorted by due date. With <paramref name="today"/>, rows get DaysLeft and Overdue.</summary>
        public static List<TimelineRow> Timeline(string eventDate, string? today = null,
            IEnumerable<(int OffsetDays, string Category, string Task)>? template = null)
        {
            int ev = ToDay(eventDate);
            int? t = today == null ? (int?)null : ToDay(today);
            return (template ?? TimelineTemplate).Select(item =>
            {
                int due = ev - item.OffsetDays;
                var row = new TimelineRow { Task = item.Task, Category = item.Category, OffsetDays = item.OffsetDays, Due = FromDay(due) };
                if (t.HasValue) { row.DaysLeft = due - t.Value; row.Overdue = due < t.Value; }
                return row;
            }).OrderBy(r => r.Due, StringComparer.Ordinal).ToList();
        }

        private static int CeilClean(double x) => (int)Math.Ceiling(Math.Round(x * 1e6, MidpointRounding.AwayFromZero) / 1e6);

        /// <summary>base = attendees x days x uptake x perPerson (rounded up); buffer = base x buffer (rounded up).</summary>
        public static GiveawayResult GiveawayQuantity(double attendees, double days = 1, double uptake = 0.6, double perPerson = 1, double buffer = 0.1)
        {
            foreach (var (k, v) in new[] { ("attendees", attendees), ("days", days), ("uptake", uptake), ("perPerson", perPerson), ("buffer", buffer) })
                if (double.IsNaN(v) || double.IsInfinity(v) || v < 0) throw new ArgumentOutOfRangeException(k, $"{k} must be a non-negative number");
            if (uptake > 1) throw new ArgumentOutOfRangeException(nameof(uptake), "uptake must be between 0 and 1");
            int b = CeilClean(attendees * days * uptake * perPerson), extra = CeilClean(b * buffer);
            return new GiveawayResult { Base = b, Buffer = extra, Total = b + extra };
        }

        /// <summary>Step back <paramref name="n"/> working days (Mon-Fri) from a day number.</summary>
        public static int SubtractWorkingDays(int day, int n)
        {
            while (n > 0) { day--; if (Weekday(day) <= 5) n--; }
            return day;
        }

        /// <summary>Latest date to order printed merchandise: production + delivery + safety working days before the event.</summary>
        public static OrderByResult OrderByDate(string eventDate, int productionDays = 10, int deliveryDays = 3, int safetyDays = 2, string? today = null)
        {
            foreach (var (k, v) in new[] { ("productionDays", productionDays), ("deliveryDays", deliveryDays), ("safetyDays", safetyDays) })
                if (v < 0) throw new ArgumentOutOfRangeException(k, $"{k} must be a non-negative integer");
            int working = productionDays + deliveryDays + safetyDays;
            int by = SubtractWorkingDays(ToDay(eventDate), working);
            var o = new OrderByResult { OrderByDate = FromDay(by), WorkingDays = working };
            if (today != null) { o.DaysFromToday = by - ToDay(today); o.Late = o.DaysFromToday < 0; }
            return o;
        }
    }
}
