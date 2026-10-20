using System;
using System.Linq;
using GoPromotional;
using Xunit;

public class PackListTests
{
    [Fact]
    public void TemplateSizes()
    {
        Assert.Equal(new[] { 10, 19, 16, 9 }, new[] { "work-day", "travel", "trade-show", "gym" }.Select(k => PackList.Templates[k].Count));
    }

    [Fact]
    public void PerNightIncludesSpare()
    {
        var items = PackList.BuildList(new[] { "travel" }, nights: 3);
        Assert.Equal(4, items.First(i => i.Name == "T-shirts").Qty);
        Assert.Equal(1, items.First(i => i.Name == "Jumper").Qty);
        Assert.Equal(1, PackList.BuildList(new[] { "travel" }).First(i => i.Name == "Socks").Qty);
    }

    [Fact]
    public void MergesAndAddsExtras()
    {
        var items = PackList.BuildList(new[] { "work-day", "travel" }, extras: new[] { "Umbrella", "laptop" });
        Assert.Equal(28, items.Count);
        Assert.Single(items, i => i.Name == "Phone charger");
        Assert.Equal("Work", items.First(i => i.Name == "Phone charger").Category);
        Assert.Equal(new PackItem { Name = "Umbrella", Category = "Extras", Qty = 1 }, items[^1]);
        Assert.Contains("Unknown template", Assert.Throws<ArgumentException>(() => PackList.BuildList(new[] { "moon" })).Message);
    }

    [Fact]
    public void MarkProgressFilter()
    {
        var items = PackList.BuildList(new[] { "gym" });
        PackList.Mark(items, "towel", true);
        PackList.Mark(items, "Trainers");
        var p = PackList.Progress(items);
        Assert.Equal((2, 9, 22), (p.Packed, p.Total, p.Percent));
        Assert.Equal(7, PackList.Remaining(items).Count);
        Assert.Equal(new[] { "Shower gel" }, PackList.FilterItems(items, "sh").Select(i => i.Name));
        Assert.Contains("matches", Assert.Throws<ArgumentException>(() => PackList.Mark(items, "e")).Message);
        Assert.Contains("No item", Assert.Throws<ArgumentException>(() => PackList.Mark(items, "kayak")).Message);
    }

    [Fact]
    public void MarkdownRoundTrip()
    {
        var items = PackList.BuildList(new[] { "travel" }, nights: 2);
        PackList.Mark(items, "Passport / ID", true);
        var md = PackList.ToMarkdown(items, "Lisbon");
        Assert.StartsWith("# Lisbon\n\n## Documents\n- [x] Passport / ID\n", md);
        Assert.Contains("- [ ] 3 × T-shirts", md);
        var (title, back) = PackList.ParseMarkdown(md);
        Assert.Equal("Lisbon", title);
        Assert.Equal(items, back);
    }
}
