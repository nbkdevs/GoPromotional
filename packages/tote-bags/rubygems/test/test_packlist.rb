# frozen_string_literal: true

require "minitest/autorun"
require "stringio"
require "tmpdir"
require_relative "../lib/gopromotional/packlist/cli"

class TestPacklist < Minitest::Test
  P = GoPromotional::Packlist

  def test_templates
    assert_equal({ "work-day" => 10, "travel" => 19, "trade-show" => 16, "gym" => 9 }, P::TEMPLATES.transform_values(&:size))
  end

  def test_build_list_per_night_and_merge
    items = P.build_list(templates: ["travel"], nights: 3)
    assert_equal 4, P.find(items, "T-shirts").qty
    assert_equal 1, P.find(items, "Jumper").qty
    both = P.build_list(templates: %w[work-day travel], extras: ["Umbrella", "laptop"])
    assert_equal 28, both.size
    assert_equal 1, both.count { |i| i.name == "Phone charger" }
    assert_equal "Work", P.find(both, "Phone charger").category
    assert_equal P::Item.new("Umbrella", "Extras"), both.last
    assert_raises(ArgumentError) { P.build_list(templates: ["moon"]) }
  end

  def test_mark_progress_filter
    items = P.build_list(templates: ["gym"])
    P.mark(items, "towel", true)
    P.mark(items, "Trainers")
    assert_equal({ packed: 2, total: 9, percent: 22 }, P.progress(items))
    assert_equal 7, P.remaining(items).size
    assert_equal ["Shower gel"], P.filter_items(items, "sh").map(&:name)
    assert_match(/matches/, assert_raises(ArgumentError) { P.mark(items, "e") }.message)
    assert_match(/No item/, assert_raises(ArgumentError) { P.mark(items, "kayak") }.message)
  end

  def test_markdown_round_trip
    items = P.build_list(templates: ["travel"], nights: 2)
    P.mark(items, "Passport / ID", true)
    md = P.to_markdown(items, "Lisbon")
    assert md.start_with?("# Lisbon\n\n## Documents\n- [x] Passport / ID\n")
    assert_includes md, "- [ ] 3 × T-shirts"
    title, back = P.parse_markdown(md)
    assert_equal "Lisbon", title
    assert_equal items, back
  end

  def test_cli
    Dir.mktmpdir do |dir|
      f = File.join(dir, "show.md")
      run = lambda do |*a|
        out, err = StringIO.new, StringIO.new
        [P::CLI.run(a, out: out, err: err), out.string.strip, err.string.strip]
      end
      assert_equal "Wrote 17 items to #{f}", run.call("new", "--template", "trade-show", "--extra", "Tote bags", "--title", "Expo", "--out", f)[1]
      assert_equal "1/17 packed (6%)", run.call("check", f, "gaffer")[1]
      assert_equal 16, JSON.parse(run.call("status", f, "--json")[1])["remaining"].size
      assert_includes File.read(f), "- [x] Gaffer tape"
      assert_equal 2, run.call("add", f, "Gaffer tape")[0]
    end
  end
end
