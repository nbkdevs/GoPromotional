using System;
using GoPromotional;
using Xunit;

public class SipLogTests
{
    [Fact]
    public void DailyGoalRoundsTo50()
    {
        Assert.Equal(2300, SipLog.DailyGoal(70));
        Assert.Equal(3150, SipLog.DailyGoal(70, 30, true));
        Assert.Equal(3250, SipLog.DailyGoal(82.5, 45));
        Assert.Throws<ArgumentOutOfRangeException>(() => SipLog.DailyGoal(0));
    }

    [Theory]
    [InlineData("500ml", 500)] [InlineData("0.5l", 500)] [InlineData("1.5 L", 1500)]
    [InlineData("33cl", 330)] [InlineData("330", 330)] [InlineData("16oz", 473)]
    public void ParsesAmounts(string input, int ml) => Assert.Equal(ml, SipLog.ParseAmount(input));

    [Fact]
    public void RejectsBadAmounts()
    {
        Assert.Throws<FormatException>(() => SipLog.ParseAmount("a glass"));
        Assert.Throws<ArgumentOutOfRangeException>(() => SipLog.ParseAmount("0"));
    }

    [Fact]
    public void TotalsUndoProgress()
    {
        var log = new SipLog(2000);
        log.Add(250, "2026-10-19"); log.Add("500ml", "2026-10-19"); log.Add("33cl", "2026-10-19"); log.Add(900, "2026-10-18");
        Assert.Equal(1080, log.Total("2026-10-19"));
        var u = log.Undo("2026-10-19")!;
        Assert.Equal(("2026-10-19", 330), (u.Date, u.Ml));
        var p = log.Progress("2026-10-19");
        Assert.Equal((750, 2000, 38, 1250, false), (p.Total, p.Goal, p.Percent, p.Remaining, p.Reached));
        Assert.Null(log.Undo("2026-10-01"));
        Assert.Equal(1, log.ClearDay("2026-10-18"));
    }

    [Fact]
    public void WeekAverageStreak()
    {
        var log = new SipLog(2000);
        foreach (var d in new[] { "2026-10-16", "2026-10-17", "2026-10-18" }) log.Add(2100, d);
        log.Add(500, "2026-10-19");
        var w = log.Week("2026-10-19");
        Assert.Equal(7, w.Count);
        Assert.Equal(("2026-10-19", 500), w[0]);
        Assert.Equal(("2026-10-16", 2100), w[3]);
        Assert.Equal(3, log.Streak("2026-10-19"));
        log.Add(1500, "2026-10-19");
        Assert.Equal(4, log.Streak("2026-10-19"));
        Assert.Equal(1186, log.Average("2026-10-19"));
    }

    [Fact]
    public void JsonRoundTripAndCrossLanguageFile()
    {
        var log = new SipLog(2500);
        log.Add(400, "2026-10-19");
        var back = SipLog.FromJson(log.ToJson());
        Assert.Equal((2500, 400), (back.Goal, back.Total("2026-10-19")));
        var other = SipLog.FromJson("{\"goal\":2000,\"entries\":[{\"date\":\"2026-10-19\",\"ml\":500},{\"date\":\"2026-10-19\",\"ml\":250}]}");
        Assert.Equal(750, other.Total("2026-10-19"));
        Assert.Contains("\"entries\"", log.ToJson());
    }
}
