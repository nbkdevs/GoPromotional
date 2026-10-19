# frozen_string_literal: true

require "minitest/autorun"
require "stringio"
require "tmpdir"
require_relative "../lib/gopromotional/mugday/cli"

class TestMugday < Minitest::Test
  M = GoPromotional::Mugday

  def sample
    log = M::CaffeineLog.new
    log.add("flat-white", time: "2026-10-19T08:00", reusable: true)
    log.add("filter coffee", time: "2026-10-19T14:00")
    log.add("Tea", time: "2026-10-19T11:00", reusable: true)
    log
  end

  def test_drinks_and_times
    assert_equal 16, M::DRINKS.size
    assert_equal({ drink: "double-espresso", mg: 126, ml: 60 }, M.lookup_drink("Double Espresso"))
    assert_raises(ArgumentError) { M.lookup_drink("mocha-frappe") }
    assert_equal "2026-10-19T08:05", M.from_minutes(M.to_minutes("2026-10-19 08:05"))
    assert_raises(ArgumentError) { M.to_minutes("2026-13-01T08:00") }
    assert_raises(ArgumentError) { M.to_minutes("2026-10-19T24:00") }
  end

  def test_day_totals
    log = sample
    assert_equal %w[flat-white tea filter-coffee], log.entries.map(&:drink)
    d = log.day("2026-10-19")
    assert_equal [341, 59, 0], d.values_at(:total, :remaining, :over)
    assert_equal 141, log.day("2026-10-19", limit: M::LIMITS[:pregnant])[:over]
  end

  def test_half_life_and_sleep
    log = sample
    assert_equal [0, 244, 81], %w[07:59 14:00 22:00].map { |t| log.in_system("2026-10-19T#{t}") }
    assert_equal "2026-10-20T01:30", log.sleep_ready(50)
    assert_equal "2026-10-19T20:30", log.sleep_ready(100)
    assert_equal "2026-10-19T14:00", log.sleep_ready(500)
    assert_nil M::CaffeineLog.new.sleep_ready
  end

  def test_cups_and_json
    log = sample
    log.add("latte", time: "2026-10-12T09:00", reusable: true)
    assert_equal({ reusable: 2, disposable: 1, rate: 67 }, log.cups("2026-10-19"))
    back = M::CaffeineLog.from_json(log.to_json)
    assert_equal log.entries, back.entries
    assert_equal 5, JSON.parse(log.to_json)["halfLifeHours"]
    assert_equal "filter-coffee", log.undo.drink
  end

  def test_cli
    Dir.mktmpdir do |dir|
      f = File.join(dir, "mug.json")
      run = lambda do |*a|
        out, err = StringIO.new, StringIO.new
        [M::CLI.run(a + ["--file", f], out: out, err: err), out.string.strip, err.string.strip]
      end
      assert_equal "Logged flat-white (126 mg, reusable cup). Today: 126/400 mg.",
                   run.call("add", "flat", "white", "--time", "2026-10-19T08:00", "--reusable")[1]
      run.call("add", "tea", "--time", "2026-10-19T11:00", "--reusable")
      run.call("add", "filter-coffee", "--time", "2026-10-19T14:00")
      assert_equal "2026-10-19: 341 mg from 3 drink(s); 59 mg left of 400 mg.", run.call("today", "--date", "2026-10-19")[1]
      assert_equal "Below 50 mg from about 2026-10-20 01:30.", run.call("sleep-ready")[1]
      assert_equal "Last 7 days: 2 reusable, 1 disposable (67% reusable).", run.call("cups", "--date", "2026-10-19")[1]
      assert_equal 2, run.call("add", "unicorn latte")[0]
    end
  end
end
