# frozen_string_literal: true

require "date"
require "json"
require_relative "mugday/version"

module GoPromotional
  # Caffeine and coffee intake tracker: typical caffeine per drink, daily totals against a limit, an
  # estimate of caffeine still in your system (half-life model), a "sleep-ready" time and reusable-cup
  # stats. Values are typical figures; real drinks vary. Not medical advice.
  module Mugday
    DRINKS = {
      "espresso" => { mg: 63, ml: 30 }, "double-espresso" => { mg: 126, ml: 60 },
      "americano" => { mg: 126, ml: 350 }, "latte" => { mg: 126, ml: 350 },
      "cappuccino" => { mg: 126, ml: 350 }, "flat-white" => { mg: 126, ml: 220 },
      "filter-coffee" => { mg: 140, ml: 300 }, "instant-coffee" => { mg: 100, ml: 300 },
      "cold-brew" => { mg: 155, ml: 350 }, "decaf-coffee" => { mg: 3, ml: 300 },
      "tea" => { mg: 75, ml: 300 }, "green-tea" => { mg: 30, ml: 300 },
      "cola" => { mg: 40, ml: 330 }, "energy-drink" => { mg: 80, ml: 250 },
      "hot-chocolate" => { mg: 5, ml: 300 }, "dark-chocolate-50g" => { mg: 25, ml: 0 }
    }.freeze
    LIMITS = { adult: 400, pregnant: 200, single_dose: 200 }.freeze
    DEFAULT_HALF_LIFE_HOURS = 5
    TIME = /\A(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})\z/
    EPOCH = Date.new(1970, 1, 1)

    module_function

    def round_half_up(x) = (x + 0.5).floor
    def norm(name) = name.to_s.strip.downcase.gsub(/[\s_]+/, "-")
    def num(x) = x.is_a?(Float) && x == x.floor ? x.to_i : x

    # "YYYY-MM-DDTHH:MM" (naive local time) to minutes since 1970-01-01.
    def to_minutes(value)
      m = TIME.match(value.to_s.strip) or raise ArgumentError, %(Invalid time "#{value}" (expected YYYY-MM-DDTHH:MM))
      y, mo, d, h, mi = m.captures.map(&:to_i)
      raise ArgumentError, %(Invalid time "#{value}") unless Date.valid_date?(y, mo, d) && h < 24 && mi < 60
      (Date.new(y, mo, d) - EPOCH).to_i * 1440 + h * 60 + mi
    end

    def from_minutes(minutes)
      day, rem = minutes.divmod(1440)
      format("%sT%02d:%02d", (EPOCH + day).iso8601, rem / 60, rem % 60)
    end

    def now_local = Time.now.strftime("%Y-%m-%dT%H:%M")

    def lookup_drink(name)
      k = norm(name)
      info = DRINKS[k] or raise ArgumentError, %(Unknown drink "#{name}". Known: #{DRINKS.keys.join(', ')} (or pass mg yourself))
      { drink: k, **info }
    end

    Drink = Struct.new(:drink, :mg, :time, :reusable) do
      def to_h = { "drink" => drink, "mg" => Mugday.num(mg), "time" => time, "reusable" => reusable }
    end

    # Caffeine log. The JSON file format ({limit, halfLifeHours, entries}) is shared with the npm,
    # PyPI and crates.io versions.
    class CaffeineLog
      attr_accessor :limit, :half_life_hours
      attr_reader :entries

      def initialize(limit: LIMITS[:adult], half_life_hours: DEFAULT_HALF_LIFE_HOURS, entries: [])
        @limit = limit
        @half_life_hours = half_life_hours
        @entries = []
        entries.each do |e|
          e = e.to_h.transform_keys(&:to_s)
          add(e["drink"], time: e["time"], mg: e["mg"], reusable: e["reusable"] || false)
        end
      end

      def self.from_json(data)
        d = data.is_a?(String) ? JSON.parse(data) : (data || {})
        new(limit: d.fetch("limit", LIMITS[:adult]),
            half_life_hours: d["halfLifeHours"] || d["half_life_hours"] || DEFAULT_HALF_LIFE_HOURS,
            entries: d.fetch("entries", []))
      end

      def to_h = { "limit" => Mugday.num(limit), "halfLifeHours" => Mugday.num(half_life_hours), "entries" => entries.map(&:to_h) }
      def to_json(*_args) = JSON.pretty_generate(to_h)

      # Log a drink. mg overrides the typical value (handy for unknown drinks).
      def add(drink, time: nil, mg: nil, reusable: false)
        name = Mugday.norm(drink)
        if mg.nil?
          info = Mugday.lookup_drink(drink)
          name = info[:drink]
          mg = info[:mg]
        end
        raise ArgumentError, "mg must be between 0 and 1000" unless mg.is_a?(Numeric) && mg >= 0 && mg <= 1000
        e = Drink.new(name, mg, Mugday.from_minutes(Mugday.to_minutes(time || Mugday.now_local)), reusable ? true : false)
        @entries << e
        @entries.sort_by!.with_index { |x, i| [Mugday.to_minutes(x.time), i] }
        e
      end

      def undo = @entries.pop
      def for_day(day) = @entries.select { |e| e.time[0, 10] == day }

      def day(day = nil, limit: nil)
        d = day || Mugday.now_local[0, 10]
        lim = limit || @limit
        items = for_day(d)
        total = items.sum(&:mg)
        { date: d, drinks: items.size, total: total, limit: lim, remaining: [0, lim - total].max, over: [0, total - lim].max }
      end

      def level(t)
        @entries.sum do |e|
          dt = t - Mugday.to_minutes(e.time)
          dt >= 0 ? e.mg * (0.5**(dt / 60.0 / @half_life_hours)) : 0
        end
      end

      # Estimated caffeine (mg) still in the body at `at`.
      def in_system(at = nil) = Mugday.round_half_up(level(Mugday.to_minutes(at || Mugday.now_local)))

      # Earliest time (rounded up to 15 min) caffeine falls below threshold, if nothing more is drunk.
      def sleep_ready(threshold = 50)
        return nil if @entries.empty?
        last = Mugday.to_minutes(@entries.last.time)
        lv = level(last)
        return Mugday.from_minutes(last) if lv <= threshold
        minutes = @half_life_hours * 60 * Math.log2(lv / threshold.to_f)
        Mugday.from_minutes(((last + minutes) / 15.0).ceil * 15)
      end

      # Reusable vs disposable cups over the 7 days ending on end_day.
      def cups(end_day = nil)
        e = Mugday.to_minutes("#{end_day || Mugday.now_local[0, 10]}T00:00") / 1440
        week = @entries.select { |x| d = Mugday.to_minutes("#{x.time[0, 10]}T00:00") / 1440; d > e - 7 && d <= e }
        reusable = week.count(&:reusable)
        { reusable: reusable, disposable: week.size - reusable,
          rate: week.empty? ? 0 : Mugday.round_half_up(reusable * 100.0 / week.size) }
      end
    end
  end
end
