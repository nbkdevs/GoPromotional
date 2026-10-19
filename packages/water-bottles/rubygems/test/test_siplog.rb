# frozen_string_literal: true

require "minitest/autorun"
require "stringio"
require "tmpdir"
require_relative "../lib/gopromotional/siplog/cli"

class TestSiplog < Minitest::Test
  S = GoPromotional::Siplog

  def test_daily_goal
    assert_equal 2300, S.daily_goal(weight_kg: 70)
    assert_equal 3150, S.daily_goal(weight_kg: 70, activity_minutes: 30, hot: true)
    assert_equal 3250, S.daily_goal(weight_kg: 82.5, activity_minutes: 45)
    assert_raises(ArgumentError) { S.daily_goal(weight_kg: 0) }
  end

  def test_parse_amount
    assert_equal [250, 500, 500, 330, 473], [250, "500ml", "0.5l", "33cl", "16oz"].map { |a| S.parse_amount(a) }
    assert_raises(ArgumentError) { S.parse_amount("a glass") }
    assert_raises(ArgumentError) { S.parse_amount("0") }
  end

  def test_log_totals_undo_progress
    log = S::HydrationLog.new(goal: 2000)
    log.add(250, "2026-10-19"); log.add("500ml", "2026-10-19"); log.add("33cl", "2026-10-19")
    log.add(900, "2026-10-18")
    assert_equal 1080, log.total("2026-10-19")
    assert_equal ["2026-10-19", 330], log.undo("2026-10-19").to_a
    assert_equal({ date: "2026-10-19", total: 750, goal: 2000, percent: 38, remaining: 1250, reached: false },
                 log.progress("2026-10-19"))
    assert_nil log.undo("2026-10-01")
    assert_equal 1, log.clear_day("2026-10-18")
  end

  def test_week_average_streak
    log = S::HydrationLog.new(goal: 2000)
    %w[2026-10-16 2026-10-17 2026-10-18].each { |d| log.add(2100, d) }
    log.add(500, "2026-10-19")
    w = log.week("2026-10-19")
    assert_equal 7, w.size
    assert_equal({ date: "2026-10-19", total: 500 }, w[0])
    assert_equal({ date: "2026-10-16", total: 2100 }, w[3])
    assert_equal 3, log.streak("2026-10-19")
    log.add(1500, "2026-10-19")
    assert_equal 4, log.streak("2026-10-19")
    assert_equal 1186, log.average("2026-10-19")
  end

  def test_json_round_trip
    log = S::HydrationLog.new(goal: 2500)
    log.add(400, "2026-10-19")
    back = S::HydrationLog.from_json(log.to_json)
    assert_equal 2500, back.goal
    assert_equal log.entries, back.entries
    assert_equal({ "goal" => 2500, "entries" => [{ "date" => "2026-10-19", "ml" => 400 }] }, JSON.parse(log.to_json))
  end

  def test_cli
    Dir.mktmpdir do |dir|
      f = File.join(dir, "log.json")
      run = lambda do |*a|
        out, err = StringIO.new, StringIO.new
        [S::CLI.run(a + ["--file", f, "--date", "2026-10-19"], out: out, err: err), out.string.strip, err.string.strip]
      end
      assert_equal "Suggested daily water goal: 2300 ml (a general guide, not medical advice).", run.call("goal", "--weight", "70")[1]
      assert_equal "Added 500 ml. 500/2000 ml (25%).", run.call("add", "500ml")[1]
      assert_equal "Daily goal set to 2500 ml.", run.call("set-goal", "2.5l")[1]
      assert_match(/500\/2500 ml \[####\.+\] 20% - 2000 ml to go/, run.call("status")[1])
      assert_equal 2, run.call("add", "lots")[0]
      assert_equal 2500, JSON.parse(File.read(f))["goal"]
    end
  end
end
