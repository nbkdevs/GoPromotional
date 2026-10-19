# frozen_string_literal: true

require "minitest/autorun"
require "stringio"
require_relative "../lib/gopromotional/event_planner/cli"

class TestEventPlanner < Minitest::Test
  EP = GoPromotional::EventPlanner
  TODAY = "2026-10-19"

  def test_countdown
    r = EP.countdown("2026-12-03", today: TODAY)
    assert_equal [45, 6, 3, "upcoming"], r.values_at(:days, :weeks, :remainder_days, :status)
    assert_equal "today", EP.countdown(TODAY, today: TODAY)[:status]
    assert_equal "past", EP.countdown("2026-10-01", today: TODAY)[:status]
    assert_raises(ArgumentError) { EP.countdown("2026-02-30", today: TODAY) }
  end

  def test_timeline
    rows = EP.timeline("2026-12-03", today: TODAY)
    assert_equal 12, rows.size
    assert_equal "2026-10-08", rows.first[:due]
    assert rows.first[:overdue]
    order = rows.find { |r| r[:task].start_with?("Order branded") }
    assert_equal ["2026-10-22", 3, false], order.values_at(:due, :days_left, :overdue)
    assert_equal "2026-12-05", rows.last[:due]
    refute EP.timeline("2026-12-03").first.key?(:days_left)
  end

  def test_giveaways
    assert_equal({ base: 600, buffer: 60, total: 660 }, EP.giveaway_quantity(attendees: 500, days: 2, uptake: 0.6))
    assert_equal({ base: 88, buffer: 9, total: 97 }, EP.giveaway_quantity(attendees: 250, uptake: 0.35))
    assert_raises(ArgumentError) { EP.giveaway_quantity(attendees: 10, uptake: 1.5) }
    assert_raises(ArgumentError) { EP.giveaway_quantity(attendees: -1) }
  end

  def test_order_by
    r = EP.order_by_date("2026-12-03", today: TODAY)
    assert_equal({ order_by: "2026-11-12", working_days: 15, days_from_today: 24, late: false }, r)
    assert_equal "2026-10-23", EP.order_by_date("2026-10-26", production_days: 1, delivery_days: 0, safety_days: 0)[:order_by]
    assert EP.order_by_date("2026-10-26", today: TODAY)[:late]
  end

  def test_working_days_skip_weekends
    assert_equal Date.new(2026, 10, 16), EP.subtract_working_days("2026-10-19", 1)
  end

  def cli(*args)
    out, err = StringIO.new, StringIO.new
    code = EP::CLI.run(args, out: out, err: err)
    [code, out.string.strip, err.string.strip]
  end

  def test_cli
    assert_equal [0, "45 days to go (6 weeks 3 days) until 2026-12-03.", ""], cli("countdown", "2026-12-03", "--today", TODAY)
    assert_equal "Order 660 items (600 expected + 60 buffer).", cli("giveaways", "--attendees", "500", "--days", "2")[1]
    assert_equal "Order by 2026-11-12 (15 working days before the event), 24 days from today.",
                 cli("order-by", "2026-12-03", "--today", TODAY)[1]
    assert_equal 24, JSON.parse(cli("order-by", "2026-12-03", "--today", TODAY, "--json")[1])["days_from_today"]
    code, _, err = cli("countdown", "nope")
    assert_equal 2, code
    assert_match(/Invalid date/, err)
  end
end
