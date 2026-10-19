# frozen_string_literal: true

require "date"
require "json"
require_relative "siplog/version"

module GoPromotional
  # Hydration helpers: daily water goal estimate, amount parsing and a day-by-day water log.
  module Siplog
    ML_PER_US_FL_OZ = 29.5735
    ML_PER_ACTIVE_MINUTE = 12
    HOT_WEATHER_ML = 500
    FACTORS = { "ml" => 1, "cl" => 10, "l" => 1000, "oz" => ML_PER_US_FL_OZ }.freeze
    AMOUNT = /\A\s*(\d+(?:\.\d+)?)\s*(ml|cl|l|oz)?\s*\z/i

    module_function

    def round_half_up(x)
      (x + 0.5).floor
    end

    def iso(value)
      return Date.today.iso8601 if value.nil?
      return value.iso8601 if value.is_a?(Date)
      s = value.to_s.strip
      raise ArgumentError, %(Invalid date "#{value}" (expected YYYY-MM-DD)) unless s.match?(/\A\d{4}-\d{2}-\d{2}\z/)
      Date.iso8601(s).iso8601
    rescue Date::Error
      raise ArgumentError, %(Invalid date "#{value}" (expected YYYY-MM-DD))
    end

    # Rough daily water goal in ml (a general guide, not medical advice):
    # weight x ml_per_kg + active minutes x 12 ml (+500 ml in hot weather), to the nearest 50 ml.
    def daily_goal(weight_kg:, activity_minutes: 0, hot: false, ml_per_kg: 33)
      raise ArgumentError, "weight_kg must be between 0 and 400" unless weight_kg.is_a?(Numeric) && weight_kg.positive? && weight_kg <= 400
      raise ArgumentError, "activity_minutes must be >= 0" if activity_minutes.negative?
      raw = weight_kg * ml_per_kg + activity_minutes * ML_PER_ACTIVE_MINUTE + (hot ? HOT_WEATHER_ML : 0)
      (raw / 50.0 + 0.5).floor * 50
    end

    # Parse 250, "500ml", "0.5l", "33cl" or "16oz" into whole millilitres.
    def parse_amount(value)
      if value.is_a?(Numeric)
        raise ArgumentError, "amount must be positive" unless value.positive?
        return round_half_up(value)
      end
      m = AMOUNT.match(value.to_s)
      raise ArgumentError, %(Cannot read amount "#{value}" (try 250, 500ml, 0.5l, 33cl or 16oz)) unless m
      ml = round_half_up(m[1].to_f * FACTORS[(m[2] || "ml").downcase])
      raise ArgumentError, "amount must be positive" unless ml.positive?
      ml
    end

    Entry = Struct.new(:date, :ml) do
      def to_h = { "date" => date, "ml" => ml }
    end

    # A day-by-day water log. The JSON file format ({goal, entries:[{date, ml}]}) is shared with the
    # npm, PyPI and crates.io versions.
    class HydrationLog
      attr_accessor :goal
      attr_reader :entries

      def initialize(goal: 2000, entries: [])
        @goal = goal
        @entries = entries.map { |e| e.is_a?(Entry) ? e : Entry.new(Siplog.iso(e["date"] || e[:date]), Siplog.parse_amount(e["ml"] || e[:ml])) }
      end

      def self.from_json(data)
        d = data.is_a?(String) ? JSON.parse(data) : (data || {})
        new(goal: d.fetch("goal", 2000), entries: d.fetch("entries", []))
      end

      def to_h = { "goal" => goal, "entries" => entries.map(&:to_h) }
      def to_json(*_args) = JSON.pretty_generate(to_h)

      def add(amount, day = nil)
        e = Entry.new(Siplog.iso(day), Siplog.parse_amount(amount))
        @entries << e
        e
      end

      # Remove the most recent entry for a day.
      def undo(day = nil)
        d = Siplog.iso(day)
        i = @entries.rindex { |e| e.date == d }
        i && @entries.delete_at(i)
      end

      def clear_day(day = nil)
        d = Siplog.iso(day)
        before = @entries.size
        @entries.reject! { |e| e.date == d }
        before - @entries.size
      end

      def total(day = nil)
        d = Siplog.iso(day)
        @entries.sum { |e| e.date == d ? e.ml : 0 }
      end

      def progress(day = nil, goal: nil)
        g = goal || @goal
        d = Siplog.iso(day)
        t = total(d)
        { date: d, total: t, goal: g, percent: g.positive? ? (t * 100.0 / g + 0.5).floor : 0,
          remaining: [0, g - t].max, reached: t >= g }
      end

      # Totals for the 7 days ending on end_day, newest first.
      def week(end_day = nil)
        e = Date.iso8601(Siplog.iso(end_day))
        (0..6).map { |i| { date: (e - i).iso8601, total: total(e - i) } }
      end

      # Consecutive days meeting the goal, ending today (or yesterday if today is not done yet).
      def streak(end_day = nil, goal: nil)
        g = goal || @goal
        d = Date.iso8601(Siplog.iso(end_day))
        d -= 1 if total(d) < g
        n = 0
        while n < 3660 && total(d) >= g
          n += 1
          d -= 1
        end
        n
      end

      def average(end_day = nil, days: 7)
        e = Date.iso8601(Siplog.iso(end_day))
        Siplog.round_half_up((0...days).sum { |i| total(e - i) } / days.to_f)
      end
    end
  end
end
