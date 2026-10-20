using System;
using GoPromotional;
using Xunit;

public class MugDayTests
{
    private static MugDay Sample()
    {
        var log = new MugDay();
        log.Add("flat-white", "2026-10-19T08:00", reusable: true);
        log.Add("tea", "2026-10-19T11:00", reusable: true);
        log.Add("Filter coffee", "2026-10-19T14:00");
        return log;
    }

    [Fact]
    public void DrinkTable()
    {
        Assert.Equal(16, MugDay.Drinks.Count);
        Assert.Equal(("flat-white", 126, 220), MugDay.LookupDrink("Flat White"));
        Assert.Contains("Unknown drink", Assert.Throws<ArgumentException>(() => MugDay.LookupDrink("mystery")).Message);
    }

    [Fact]
    public void DailyTotals()
    {
        var d = Sample().Day("2026-10-19");
        Assert.Equal((3, 341.0, 400.0, 59.0, 0.0), (d.Drinks, d.Total, d.Limit, d.Remaining, d.Over));
        d = Sample().Day("2026-10-19", MugDay.Limits["pregnant"]);
        Assert.Equal((0.0, 141.0), (d.Remaining, d.Over));
        Assert.Equal(0, Sample().Day("2026-10-18").Total);
    }

    [Fact]
    public void InSystemAndSleepReady()
    {
        var log = Sample();
        Assert.Equal(0, log.InSystem("2026-10-19T07:59"));
        Assert.Equal(244, log.InSystem("2026-10-19T14:00"));
        Assert.Equal(81, log.InSystem("2026-10-19T22:00"));
        Assert.Equal("2026-10-20T01:30", log.SleepReady(50));
        Assert.Equal("2026-10-19T20:30", log.SleepReady(100));
        Assert.Equal("2026-10-19T14:00", log.SleepReady(500));
        Assert.Null(new MugDay().SleepReady());
    }

    [Fact]
    public void CupsUndoJson()
    {
        var log = Sample();
        log.Add("Office machine", "2026-10-12T09:00", 90);
        var c = log.Cups("2026-10-19");
        Assert.Equal((2, 1, 67), (c.Reusable, c.Disposable, c.Rate));
        c = log.Cups("2026-10-12");
        Assert.Equal((0, 1, 0), (c.Reusable, c.Disposable, c.Rate));
        Assert.Equal("filter-coffee", log.Undo()!.Drink);
        Assert.Equal(201, MugDay.FromJson(log.ToJson()).Day("2026-10-19").Total);
        Assert.Throws<ArgumentOutOfRangeException>(() => log.Add("espresso", "2026-13-01T08:00"));
    }
}
