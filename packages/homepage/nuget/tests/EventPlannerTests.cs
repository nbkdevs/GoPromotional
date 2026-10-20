using System;
using System.Linq;
using GoPromotional;
using Xunit;

public class EventPlannerTests
{
    [Fact]
    public void CountdownSplitsWeeksAndDays()
    {
        var c = EventPlanner.Countdown("2026-12-03", "2026-10-19");
        Assert.Equal((45, 6, 3, "upcoming"), (c.Days, c.Weeks, c.RemainderDays, c.Status));
        Assert.Equal("today", EventPlanner.Countdown("2026-10-19", "2026-10-19").Status);
        var past = EventPlanner.Countdown("2026-10-10", "2026-10-19");
        Assert.Equal((-9, "past"), (past.Days, past.Status));
    }

    [Fact]
    public void RejectsInvalidDates()
    {
        Assert.Throws<ArgumentOutOfRangeException>(() => EventPlanner.ToDay("2026-02-30"));
        Assert.Throws<FormatException>(() => EventPlanner.ToDay("19/10/2026"));
    }

    [Fact]
    public void WeekdayIsIso()
    {
        Assert.Equal(1, EventPlanner.Weekday(EventPlanner.ToDay("2026-10-19")));
        Assert.Equal(4, EventPlanner.Weekday(EventPlanner.ToDay("2026-12-03")));
        Assert.Equal(7, EventPlanner.Weekday(EventPlanner.ToDay("2026-10-25")));
    }

    [Fact]
    public void TimelineDatesAndOverdue()
    {
        var t = EventPlanner.Timeline("2026-12-03", "2026-10-19");
        Assert.Equal(12, t.Count);
        Assert.Equal("2026-10-08", t[0].Due);
        Assert.True(t[0].Overdue);
        var merch = t.First(r => r.Task.StartsWith("Order branded"));
        Assert.Equal(("2026-10-22", 3, false), (merch.Due, merch.DaysLeft!.Value, merch.Overdue!.Value));
        Assert.Equal("2026-12-05", t[^1].Due);
    }

    [Fact]
    public void GiveawaysRoundUp()
    {
        var q = EventPlanner.GiveawayQuantity(500, days: 2, uptake: 0.6, buffer: 0.1);
        Assert.Equal((600, 60, 660), (q.Base, q.Buffer, q.Total));
        q = EventPlanner.GiveawayQuantity(250, uptake: 0.35);
        Assert.Equal((88, 9, 97), (q.Base, q.Buffer, q.Total));
        Assert.Equal(0, EventPlanner.GiveawayQuantity(0).Total);
        Assert.Throws<ArgumentOutOfRangeException>(() => EventPlanner.GiveawayQuantity(10, uptake: 1.5));
        Assert.Throws<ArgumentOutOfRangeException>(() => EventPlanner.GiveawayQuantity(-1));
    }

    [Fact]
    public void OrderBySkipsWeekends()
    {
        var o = EventPlanner.OrderByDate("2026-12-03", today: "2026-10-19");
        Assert.Equal(("2026-11-12", 15, 24, false), (o.OrderByDate, o.WorkingDays, o.DaysFromToday!.Value, o.Late!.Value));
        Assert.Equal("2026-10-23", EventPlanner.OrderByDate("2026-10-26", 1, 0, 0).OrderByDate);
        Assert.True(EventPlanner.OrderByDate("2026-10-21", today: "2026-10-19").Late);
    }
}
