# frozen_string_literal: true

require "date"
require_relative "event_planner/version"

module GoPromotional
  # Event planning helpers: countdown, planning timeline, giveaway quantities and order-by dates.
  module EventPlanner
    TimelineItem = Struct.new(:offset_days, :category, :task, keyword_init: true)

    TIMELINE_TEMPLATE = [
      [56, "planning", "Confirm budget and attendee estimate"],
      [49, "venue", "Book the venue"],
      [42, "merchandise", "Order branded merchandise and giveaways"],
      [35, "comms", "Send invitations or open registration"],
      [28, "programme", "Confirm speakers and agenda"],
      [21, "venue", "Arrange catering"],
      [14, "merchandise", "Approve print proofs and signage"],
      [7, "comms", "Send a reminder to attendees"],
      [3, "logistics", "Confirm deliveries and the staffing rota"],
      [1, "logistics", "Pack the event kit and giveaways"],
      [0, "logistics", "Event day: set up the stand and registration"],
      [-2, "comms", "Send thank-you and follow-up messages"]
    ].map { |o, c, t| TimelineItem.new(offset_days: o, category: c, task: t).freeze }.freeze

    module_function

    # Accept a Date or an ISO YYYY-MM-DD string.
    def to_date(value)
      return value if value.is_a?(Date)
      s = value.to_s.strip
      raise ArgumentError, %(Invalid date "#{value}" (expected YYYY-MM-DD)) unless s.match?(/\A\d{4}-\d{2}-\d{2}\z/)
      begin
        Date.iso8601(s)
      rescue Date::Error
        raise ArgumentError, %(Invalid calendar date "#{s}")
      end
    end

    # Days until an event, split into weeks and days.
    def countdown(event_date, today: Date.today)
      ev = to_date(event_date)
      now = to_date(today)
      days = (ev - now).to_i
      {
        event_date: ev.iso8601, today: now.iso8601, days: days,
        weeks: days.abs / 7, remainder_days: days.abs % 7,
        status: days.positive? ? "upcoming" : (days.zero? ? "today" : "past")
      }
    end

    # Planning tasks with due dates, sorted by date. With today: each row gets :days_left and :overdue.
    def timeline(event_date, today: nil, template: TIMELINE_TEMPLATE)
      ev = to_date(event_date)
      now = today && to_date(today)
      template.map do |item|
        due = ev - item.offset_days
        row = { task: item.task, category: item.category, offset_days: item.offset_days, due: due.iso8601 }
        if now
          row[:days_left] = (due - now).to_i
          row[:overdue] = due < now
        end
        row
      end.sort_by { |r| r[:due] }
    end

    def ceil_clean(x)
      ((x * 1e6).round / 1e6).ceil
    end

    # Giveaway items: attendees x days x uptake x per_person, plus a buffer (both rounded up).
    def giveaway_quantity(attendees:, days: 1, uptake: 0.6, per_person: 1, buffer: 0.1)
      { attendees: attendees, days: days, uptake: uptake, per_person: per_person, buffer: buffer }.each do |name, v|
        unless v.is_a?(Numeric) && v.finite? && v >= 0
          raise ArgumentError, "#{name} must be a non-negative number"
        end
      end
      raise ArgumentError, "uptake must be between 0 and 1" if uptake > 1
      base = ceil_clean(attendees * days * uptake * per_person)
      extra = ceil_clean(base * buffer)
      { base: base, buffer: extra, total: base + extra }
    end

    # Step back n working days (Monday to Friday).
    def subtract_working_days(day, n)
      d = to_date(day)
      left = n
      while left.positive?
        d -= 1
        left -= 1 if d.cwday <= 5
      end
      d
    end

    # Latest order date for printed merchandise: production + delivery + safety, in working days.
    def order_by_date(event_date, production_days: 10, delivery_days: 3, safety_days: 2, today: nil)
      { production_days: production_days, delivery_days: delivery_days, safety_days: safety_days }.each do |name, v|
        raise ArgumentError, "#{name} must be a non-negative integer" unless v.is_a?(Integer) && v >= 0
      end
      working = production_days + delivery_days + safety_days
      by = subtract_working_days(event_date, working)
      out = { order_by: by.iso8601, working_days: working }
      if today
        out[:days_from_today] = (by - to_date(today)).to_i
        out[:late] = out[:days_from_today].negative?
      end
      out
    end
  end
end
