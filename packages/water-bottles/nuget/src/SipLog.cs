using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Text.RegularExpressions;

namespace GoPromotional
{
    /// <summary>One logged drink: a date and an amount in whole millilitres.</summary>
    public sealed class HydrationEntry
    {
        [JsonPropertyName("date")] public string Date { get; set; } = "";
        [JsonPropertyName("ml")] public int Ml { get; set; }
    }

    /// <summary>Progress towards the daily goal.</summary>
    public sealed class HydrationProgress
    {
        public string Date { get; set; } = "";
        public int Total { get; set; }
        public int Goal { get; set; }
        public int Percent { get; set; }
        public int Remaining { get; set; }
        public bool Reached { get; set; }
    }

    /// <summary>
    /// Hydration helpers and a day-by-day water log with progress, 7-day history and streaks.
    /// The JSON format (<c>{"goal":2000,"entries":[{"date":"2026-10-19","ml":500}]}</c>) matches the
    /// npm, PyPI, crates.io and RubyGems versions of gopromotional-siplog.
    /// </summary>
    public sealed class SipLog
    {
        public const double MlPerUsFlOz = 29.5735;
        public const int MlPerActiveMinute = 12;
        public const int HotWeatherMl = 500;
        private static readonly DateTime Epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
        private static readonly Regex DateRe = new Regex(@"^(\d{4})-(\d{2})-(\d{2})$");
        private static readonly Regex AmountRe = new Regex(@"^\s*(\d+(?:\.\d+)?)\s*(ml|cl|l|oz)?\s*$", RegexOptions.IgnoreCase);

        [JsonPropertyName("goal")] public int Goal { get; set; } = 2000;
        [JsonPropertyName("entries")] public List<HydrationEntry> Entries { get; set; } = new List<HydrationEntry>();

        public SipLog() { }
        public SipLog(int goal) { Goal = goal; }

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

        public static string FromDay(int day) => Epoch.AddDays(day).ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
        private static string Norm(string? date) => FromDay(ToDay(date ?? DateTime.Now.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)));

        /// <summary>Rough daily goal in ml (a general guide, not medical advice), rounded to the nearest 50 ml.</summary>
        public static int DailyGoal(double weightKg, double activityMinutes = 0, bool hot = false, double mlPerKg = 33)
        {
            if (!(weightKg > 0) || weightKg > 400) throw new ArgumentOutOfRangeException(nameof(weightKg), "weightKg must be between 0 and 400");
            if (activityMinutes < 0) throw new ArgumentOutOfRangeException(nameof(activityMinutes), "activityMinutes must be >= 0");
            double raw = weightKg * mlPerKg + activityMinutes * MlPerActiveMinute + (hot ? HotWeatherMl : 0);
            return (int)Math.Floor(raw / 50 + 0.5) * 50;
        }

        /// <summary>Whole millilitres from a number of ml.</summary>
        public static int ParseAmount(double ml)
        {
            if (!(ml > 0)) throw new ArgumentOutOfRangeException(nameof(ml), "amount must be positive");
            return (int)Math.Floor(ml + 0.5);
        }

        /// <summary>Parse "500", "500ml", "0.5l", "1.5 L", "33cl" or "16oz" into whole millilitres.</summary>
        public static int ParseAmount(string input)
        {
            var m = AmountRe.Match(input ?? "");
            if (!m.Success) throw new FormatException($"Cannot read amount \"{input}\" (try 250, 500ml, 0.5l, 33cl or 16oz)");
            double n = double.Parse(m.Groups[1].Value, CultureInfo.InvariantCulture);
            string unit = m.Groups[2].Success ? m.Groups[2].Value.ToLowerInvariant() : "ml";
            double factor = unit == "cl" ? 10 : unit == "l" ? 1000 : unit == "oz" ? MlPerUsFlOz : 1;
            int ml = (int)Math.Floor(n * factor + 0.5);
            if (ml <= 0) throw new ArgumentOutOfRangeException(nameof(input), "amount must be positive");
            return ml;
        }

        public static SipLog FromJson(string json)
        {
            var log = JsonSerializer.Deserialize<SipLog>(json) ?? new SipLog();
            log.Entries = log.Entries.Select(e => new HydrationEntry { Date = Norm(e.Date), Ml = ParseAmount(e.Ml) }).ToList();
            return log;
        }

        public string ToJson() => JsonSerializer.Serialize(this, new JsonSerializerOptions { WriteIndented = true });

        public HydrationEntry Add(string amount, string? date = null) => Push(ParseAmount(amount), date);
        public HydrationEntry Add(double ml, string? date = null) => Push(ParseAmount(ml), date);
        private HydrationEntry Push(int ml, string? date)
        {
            var e = new HydrationEntry { Date = Norm(date), Ml = ml };
            Entries.Add(e);
            return e;
        }

        /// <summary>Remove the most recent entry for a day; returns it, or null.</summary>
        public HydrationEntry? Undo(string? date = null)
        {
            var d = Norm(date);
            for (int i = Entries.Count - 1; i >= 0; i--)
                if (Entries[i].Date == d) { var e = Entries[i]; Entries.RemoveAt(i); return e; }
            return null;
        }

        public int ClearDay(string? date = null) { var d = Norm(date); return Entries.RemoveAll(e => e.Date == d); }

        public int Total(string? date = null) { var d = Norm(date); return Entries.Where(e => e.Date == d).Sum(e => e.Ml); }

        public HydrationProgress Progress(string? date = null, int? goal = null)
        {
            int g = goal ?? Goal, total = Total(date);
            return new HydrationProgress
            {
                Date = Norm(date), Total = total, Goal = g,
                Percent = g > 0 ? (int)Math.Floor(total * 100.0 / g + 0.5) : 0,
                Remaining = Math.Max(0, g - total), Reached = total >= g,
            };
        }

        /// <summary>Totals for the 7 days ending on <paramref name="endDate"/>, newest first.</summary>
        public List<(string Date, int Total)> Week(string? endDate = null)
        {
            int end = ToDay(Norm(endDate));
            return Enumerable.Range(0, 7).Select(i => (FromDay(end - i), Total(FromDay(end - i)))).ToList();
        }

        /// <summary>Consecutive days meeting the goal, ending today (or yesterday if today is not done yet).</summary>
        public int Streak(string? endDate = null, int? goal = null)
        {
            int g = goal ?? Goal, day = ToDay(Norm(endDate)), n = 0;
            if (Total(FromDay(day)) < g) day--;
            while (n < 3660 && Total(FromDay(day)) >= g) { n++; day--; }
            return n;
        }

        public int Average(string? endDate = null, int days = 7)
        {
            int end = ToDay(Norm(endDate));
            double sum = Enumerable.Range(0, days).Sum(i => Total(FromDay(end - i)));
            return (int)Math.Floor(sum / days + 0.5);
        }
    }
}
