using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Text.RegularExpressions;

namespace GoPromotional
{
    /// <summary>One logged drink.</summary>
    public sealed class CaffeineEntry
    {
        [JsonPropertyName("drink")] public string Drink { get; set; } = "";
        [JsonPropertyName("mg")] public double Mg { get; set; }
        [JsonPropertyName("time")] public string Time { get; set; } = "";
        [JsonPropertyName("reusable")] public bool Reusable { get; set; }
    }

    /// <summary>Caffeine total for a day against the limit.</summary>
    public sealed class CaffeineDay
    {
        public string Date { get; set; } = "";
        public int Drinks { get; set; }
        public double Total { get; set; }
        public double Limit { get; set; }
        public double Remaining { get; set; }
        public double Over { get; set; }
    }

    /// <summary>Reusable vs disposable cups over 7 days.</summary>
    public sealed class CupStats
    {
        public int Reusable { get; set; }
        public int Disposable { get; set; }
        public int Rate { get; set; }
    }

    /// <summary>
    /// Caffeine and coffee intake tracker: typical caffeine per drink, daily totals against a limit,
    /// caffeine still in your system (half-life model), a "sleep-ready" time and reusable-cup stats.
    /// The JSON format matches the npm, PyPI, crates.io and RubyGems versions of gopromotional-mugday.
    /// </summary>
    public sealed class MugDay
    {
        /// <summary>Typical caffeine per serving (mg) and serving size (ml). Real drinks vary.</summary>
        public static readonly IReadOnlyDictionary<string, (int Mg, int Ml)> Drinks = new Dictionary<string, (int, int)>
        {
            ["espresso"] = (63, 30), ["double-espresso"] = (126, 60), ["americano"] = (126, 350), ["latte"] = (126, 350),
            ["cappuccino"] = (126, 350), ["flat-white"] = (126, 220), ["filter-coffee"] = (140, 300), ["instant-coffee"] = (100, 300),
            ["cold-brew"] = (155, 350), ["decaf-coffee"] = (3, 300), ["tea"] = (75, 300), ["green-tea"] = (30, 300),
            ["cola"] = (40, 330), ["energy-drink"] = (80, 250), ["hot-chocolate"] = (5, 300), ["dark-chocolate-50g"] = (25, 0),
        };

        /// <summary>Daily guidance presets (mg): EFSA/NHS adult and pregnancy levels.</summary>
        public static readonly IReadOnlyDictionary<string, int> Limits = new Dictionary<string, int> { ["adult"] = 400, ["pregnant"] = 200, ["single_dose"] = 200 };
        public const double DefaultHalfLifeHours = 5;

        private static readonly DateTime Epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
        private static readonly Regex TimeRe = new Regex(@"^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})$");

        [JsonPropertyName("limit")] public double Limit { get; set; } = 400;
        [JsonPropertyName("halfLifeHours")] public double HalfLifeHours { get; set; } = DefaultHalfLifeHours;
        [JsonPropertyName("entries")] public List<CaffeineEntry> Entries { get; set; } = new List<CaffeineEntry>();

        public MugDay() { }
        public MugDay(double limit, double halfLifeHours = DefaultHalfLifeHours) { Limit = limit; HalfLifeHours = halfLifeHours; }

        /// <summary>Parse "YYYY-MM-DDTHH:MM" (or with a space) to minutes since 1970-01-01 (naive local time).</summary>
        public static long ToMinutes(string s)
        {
            var t = (s ?? "").Trim();
            var m = TimeRe.Match(t);
            if (!m.Success) throw new FormatException($"Invalid time \"{s}\" (expected YYYY-MM-DDTHH:MM)");
            int y = int.Parse(m.Groups[1].Value), mo = int.Parse(m.Groups[2].Value), d = int.Parse(m.Groups[3].Value);
            int h = int.Parse(m.Groups[4].Value), mi = int.Parse(m.Groups[5].Value);
            if (y < 1 || mo < 1 || mo > 12 || d < 1 || d > DateTime.DaysInMonth(y, mo) || h > 23 || mi > 59)
                throw new ArgumentOutOfRangeException(nameof(s), $"Invalid time \"{s}\"");
            return (long)(new DateTime(y, mo, d, h, mi, 0, DateTimeKind.Utc) - Epoch).TotalMinutes;
        }

        public static string FromMinutes(long min) => Epoch.AddMinutes(min).ToString("yyyy-MM-dd'T'HH:mm", CultureInfo.InvariantCulture);
        public static string NowLocal() => DateTime.Now.ToString("yyyy-MM-dd'T'HH:mm", CultureInfo.InvariantCulture);
        private static string Norm(string drink) => Regex.Replace((drink ?? "").Trim().ToLowerInvariant(), @"[\s_]+", "-");

        public static (string Drink, int Mg, int Ml) LookupDrink(string name)
        {
            var k = Norm(name);
            if (!Drinks.TryGetValue(k, out var d))
                throw new ArgumentException($"Unknown drink \"{name}\". Known: {string.Join(", ", Drinks.Keys)} (or pass mg yourself)", nameof(name));
            return (k, d.Mg, d.Ml);
        }

        public static MugDay FromJson(string json)
        {
            var raw = JsonSerializer.Deserialize<MugDay>(json) ?? new MugDay();
            var log = new MugDay(raw.Limit, raw.HalfLifeHours);
            foreach (var e in raw.Entries) log.Add(e.Drink, e.Time, e.Mg, e.Reusable);
            return log;
        }

        public string ToJson() => JsonSerializer.Serialize(this, new JsonSerializerOptions { WriteIndented = true });

        /// <summary>Log a drink; <paramref name="mg"/> overrides the typical value. Entries stay in time order.</summary>
        public CaffeineEntry Add(string drink, string? time = null, double? mg = null, bool reusable = false)
        {
            var name = Norm(drink);
            double caffeine;
            if (mg.HasValue) caffeine = mg.Value;
            else { var d = LookupDrink(drink); name = d.Drink; caffeine = d.Mg; }
            if (double.IsNaN(caffeine) || caffeine < 0 || caffeine > 1000) throw new ArgumentOutOfRangeException(nameof(mg), "mg must be between 0 and 1000");
            var e = new CaffeineEntry { Drink = name, Mg = caffeine, Time = FromMinutes(ToMinutes(time ?? NowLocal())), Reusable = reusable };
            Entries.Add(e);
            Entries = Entries.OrderBy(x => ToMinutes(x.Time)).ToList(); // stable
            return e;
        }

        public CaffeineEntry? Undo()
        {
            if (Entries.Count == 0) return null;
            var e = Entries[Entries.Count - 1];
            Entries.RemoveAt(Entries.Count - 1);
            return e;
        }

        public List<CaffeineEntry> ForDay(string date) => Entries.Where(e => e.Time.Substring(0, 10) == date).ToList();

        public CaffeineDay Day(string? date = null, double? limit = null)
        {
            var d = date ?? NowLocal().Substring(0, 10);
            var lim = limit ?? Limit;
            var items = ForDay(d);
            var total = items.Sum(e => e.Mg);
            return new CaffeineDay { Date = d, Drinks = items.Count, Total = total, Limit = lim, Remaining = Math.Max(0, lim - total), Over = Math.Max(0, total - lim) };
        }

        /// <summary>Estimated caffeine (mg) still in the body at <paramref name="at"/>.</summary>
        public int InSystem(string? at = null)
        {
            long t = ToMinutes(at ?? NowLocal());
            double sum = 0;
            foreach (var e in Entries)
            {
                long dt = t - ToMinutes(e.Time);
                if (dt >= 0) sum += e.Mg * Math.Pow(0.5, dt / 60.0 / HalfLifeHours);
            }
            return (int)Math.Floor(sum + 0.5);
        }

        /// <summary>Earliest time (rounded up to 15 minutes) when caffeine falls below <paramref name="threshold"/> mg.</summary>
        public string? SleepReady(double threshold = 50)
        {
            if (Entries.Count == 0) return null;
            long last = ToMinutes(Entries[Entries.Count - 1].Time);
            double level = Entries.Sum(e => e.Mg * Math.Pow(0.5, (last - ToMinutes(e.Time)) / 60.0 / HalfLifeHours));
            if (level <= threshold) return FromMinutes(last);
            double minutes = HalfLifeHours * 60 * Math.Log(level / threshold, 2);
            return FromMinutes((long)(Math.Ceiling((last + minutes) / 15) * 15));
        }

        /// <summary>Reusable vs disposable cups over the 7 days ending on <paramref name="endDate"/>.</summary>
        public CupStats Cups(string? endDate = null)
        {
            var end = ToMinutes($"{endDate ?? NowLocal().Substring(0, 10)}T00:00") / 1440;
            var week = Entries.Where(e => { var d = ToMinutes(e.Time.Substring(0, 10) + "T00:00") / 1440; return d <= end && d > end - 7; }).ToList();
            int reusable = week.Count(e => e.Reusable);
            return new CupStats { Reusable = reusable, Disposable = week.Count - reusable, Rate = week.Count > 0 ? (int)Math.Floor(reusable * 100.0 / week.Count + 0.5) : 0 };
        }
    }
}
