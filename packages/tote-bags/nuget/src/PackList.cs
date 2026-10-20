using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;

namespace GoPromotional
{
    /// <summary>One checklist item.</summary>
    public sealed class PackItem
    {
        public string Name { get; set; } = "";
        public string Category { get; set; } = "";
        public int Qty { get; set; } = 1;
        public bool Packed { get; set; }

        public override bool Equals(object? obj) => obj is PackItem o && o.Name == Name && o.Category == Category && o.Qty == Qty && o.Packed == Packed;
        public override int GetHashCode() => (Name, Category, Qty, Packed).GetHashCode();
        public override string ToString() => $"{(Packed ? "[x]" : "[ ]")} {PackList.Label(this)} ({Category})";
    }

    /// <summary>Packing progress.</summary>
    public sealed class PackProgress
    {
        public int Packed { get; set; }
        public int Total { get; set; }
        public int Percent { get; set; }
    }

    /// <summary>
    /// Packing checklists: built-in templates (work day, travel, trade show, gym), per-night quantities,
    /// merging without duplicates, progress, and a Markdown format that round-trips, so a checklist can
    /// live in a plain .md file. Same behaviour as the other gopromotional-packlist versions.
    /// </summary>
    public static class PackList
    {
        private static (string, string, bool) R(string n, string c, bool perNight = false) => (n, c, perNight);

        /// <summary>Built-in templates: (name, category, perNight).</summary>
        public static readonly IReadOnlyDictionary<string, IReadOnlyList<(string Name, string Category, bool PerNight)>> Templates =
            new Dictionary<string, IReadOnlyList<(string, string, bool)>>
            {
                ["work-day"] = new[] { R("Laptop", "Work"), R("Laptop charger", "Work"), R("Phone charger", "Work"), R("Badge / ID", "Work"),
                    R("Notebook", "Work"), R("Pen", "Work"), R("Headphones", "Work"), R("Water bottle", "Work"),
                    R("Reusable coffee cup", "Work"), R("Lunch / snacks", "Work") },
                ["travel"] = new[] { R("Passport / ID", "Documents"), R("Tickets / boarding pass", "Documents"), R("Bank cards", "Documents"),
                    R("T-shirts", "Clothes", true), R("Underwear", "Clothes", true), R("Socks", "Clothes", true),
                    R("Trousers / skirt", "Clothes"), R("Jumper", "Clothes"), R("Sleepwear", "Clothes"),
                    R("Toothbrush", "Toiletries"), R("Toothpaste", "Toiletries"), R("Deodorant", "Toiletries"), R("Medications", "Toiletries"),
                    R("Phone charger", "Tech"), R("Power bank", "Tech"), R("Plug adapter", "Tech"), R("Headphones", "Tech"),
                    R("Reusable water bottle", "Other"), R("Tote bag", "Other") },
                ["trade-show"] = new[] { R("Pop-up banner / stand", "Stand"), R("Table cloth", "Stand"), R("Branded giveaways", "Stand"),
                    R("Business cards", "Stand"), R("Brochures / flyers", "Stand"), R("Lead capture sheet or scanner", "Stand"),
                    R("Extension lead", "Kit"), R("Laptop / tablet", "Kit"), R("Phone charger", "Kit"), R("Gaffer tape", "Kit"),
                    R("Scissors", "Kit"), R("Cable ties", "Kit"), R("Pens", "Kit"),
                    R("Water bottles", "Team"), R("Snacks", "Team"), R("First-aid kit", "Team") },
                ["gym"] = new[] { R("Gym clothes", "Gym"), R("Trainers", "Gym"), R("Towel", "Gym"), R("Water bottle", "Gym"), R("Padlock", "Gym"),
                    R("Headphones", "Gym"), R("Shower gel", "Gym"), R("Deodorant", "Gym"), R("Snack", "Gym") },
            };

        private static string Key(string s) => (s ?? "").Trim().ToLowerInvariant();

        /// <summary>Build a checklist. Per-night items get nights + 1 (one spare); duplicates are merged by name (first wins).</summary>
        public static List<PackItem> BuildList(IEnumerable<string>? templates = null, int nights = 0, IEnumerable<string>? extras = null)
        {
            if (nights < 0 || nights > 365) throw new ArgumentOutOfRangeException(nameof(nights), "nights must be an integer from 0 to 365");
            var seen = new HashSet<string>();
            var items = new List<PackItem>();
            void Push(PackItem it) { var k = Key(it.Name); if (k.Length > 0 && seen.Add(k)) items.Add(it); }
            foreach (var t in templates ?? new[] { "travel" })
            {
                if (!Templates.TryGetValue(t, out var tpl))
                    throw new ArgumentException($"Unknown template \"{t}\". Choose from: {string.Join(", ", Templates.Keys)}", nameof(templates));
                foreach (var r in tpl) Push(new PackItem { Name = r.Name, Category = r.Category, Qty = r.PerNight && nights > 0 ? nights + 1 : 1 });
            }
            foreach (var e in extras ?? Array.Empty<string>()) Push(new PackItem { Name = e.Trim(), Category = "Extras" });
            return items;
        }

        /// <summary>Find by exact name, then by a unique substring.</summary>
        public static PackItem Find(IList<PackItem> items, string name)
        {
            var k = Key(name);
            var exact = items.FirstOrDefault(i => Key(i.Name) == k);
            if (exact != null) return exact;
            var partial = items.Where(i => Key(i.Name).Contains(k)).ToList();
            if (partial.Count == 1) return partial[0];
            if (partial.Count > 1) throw new ArgumentException($"\"{name}\" matches {partial.Count} items: {string.Join(", ", partial.Select(i => i.Name))}", nameof(name));
            throw new ArgumentException($"No item matches \"{name}\"", nameof(name));
        }

        /// <summary>Mark an item packed, or toggle it when <paramref name="packed"/> is null.</summary>
        public static PackItem Mark(IList<PackItem> items, string name, bool? packed = null)
        {
            var it = Find(items, name);
            it.Packed = packed ?? !it.Packed;
            return it;
        }

        public static PackProgress Progress(IList<PackItem> items)
        {
            int packed = items.Count(i => i.Packed);
            return new PackProgress { Packed = packed, Total = items.Count, Percent = items.Count > 0 ? (int)Math.Floor(packed * 100.0 / items.Count + 0.5) : 0 };
        }

        public static List<PackItem> Remaining(IEnumerable<PackItem> items) => items.Where(i => !i.Packed).ToList();

        public static List<PackItem> FilterItems(IEnumerable<PackItem> items, string q) =>
            items.Where(i => Key(i.Name).Contains(Key(q)) || Key(i.Category).Contains(Key(q))).ToList();

        public static List<string> Categories(IEnumerable<PackItem> items) => items.Select(i => i.Category).Distinct().ToList();

        public static string Label(PackItem i) => i.Qty > 1 ? $"{i.Qty} × {i.Name}" : i.Name;

        /// <summary>Markdown checkboxes grouped by category. <see cref="ParseMarkdown"/> reads it back.</summary>
        public static string ToMarkdown(IList<PackItem> items, string title = "Packing list")
        {
            var o = new List<string> { $"# {title}", "" };
            foreach (var c in Categories(items))
            {
                o.Add($"## {c}");
                o.AddRange(items.Where(x => x.Category == c).Select(i => $"- [{(i.Packed ? "x" : " ")}] {Label(i)}"));
                o.Add("");
            }
            return string.Join("\n", o);
        }

        public static string ToText(IList<PackItem> items) => string.Join("\n", Categories(items).Select(c =>
            string.Join("\n", new[] { c }.Concat(items.Where(x => x.Category == c).Select(i => $"  {(i.Packed ? "[x]" : "[ ]")} {Label(i)}")))));

        public static (string Title, List<PackItem> Items) ParseMarkdown(string md)
        {
            string title = "Packing list", category = "Items";
            var items = new List<PackItem>();
            foreach (var raw in Regex.Split(md ?? "", "\r?\n"))
            {
                var line = raw.Trim();
                Match m;
                if ((m = Regex.Match(line, @"^#\s+(.+)$")).Success) title = m.Groups[1].Value.Trim();
                else if ((m = Regex.Match(line, @"^##\s+(.+)$")).Success) category = m.Groups[1].Value.Trim();
                else if ((m = Regex.Match(line, @"^[-*]\s+\[([ xX])\]\s+(.+)$")).Success)
                {
                    var name = m.Groups[2].Value.Trim(); var qty = 1;
                    var q = Regex.Match(name, @"^(\d+)\s*[×x]\s+(.+)$");
                    if (q.Success) { qty = int.Parse(q.Groups[1].Value); name = q.Groups[2].Value.Trim(); }
                    items.Add(new PackItem { Name = name, Category = category, Qty = qty, Packed = m.Groups[1].Value != " " });
                }
            }
            return (title, items);
        }
    }
}
