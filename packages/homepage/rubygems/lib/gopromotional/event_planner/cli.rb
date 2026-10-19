# frozen_string_literal: true

require "json"
require "optparse"
require_relative "../event_planner"

module GoPromotional
  module EventPlanner
    # Command line interface: countdown | timeline | giveaways | order-by
    module CLI
      USAGE = <<~TXT
        Usage: gopromotional-event-planner <command> [options]

        Commands:
          countdown <date>                 days until the event
          timeline <date>                  planning checklist with due dates
          giveaways --attendees N [--days N] [--uptake 0.6] [--per-person 1] [--buffer 0.1]
          order-by <date> [--production 10] [--delivery 3] [--safety 2]

        Options: --json, --today YYYY-MM-DD, -v/--version, -h/--help
      TXT

      module_function

      def run(argv, out: $stdout, err: $stderr)
        o = { json: false, today: Date.today.iso8601, days: 1.0, uptake: 0.6, per_person: 1.0, buffer: 0.1,
              production: 10, delivery: 3, safety: 2 }
        parser = OptionParser.new do |p|
          p.banner = USAGE
          p.on("--json") { o[:json] = true }
          p.on("--today DATE") { |v| o[:today] = v }
          p.on("--attendees N", Float) { |v| o[:attendees] = v }
          p.on("--days N", Float) { |v| o[:days] = v }
          p.on("--uptake N", Float) { |v| o[:uptake] = v }
          p.on("--per-person N", Float) { |v| o[:per_person] = v }
          p.on("--buffer N", Float) { |v| o[:buffer] = v }
          p.on("--production N", Integer) { |v| o[:production] = v }
          p.on("--delivery N", Integer) { |v| o[:delivery] = v }
          p.on("--safety N", Integer) { |v| o[:safety] = v }
          p.on("-v", "--version") { out.puts VERSION; return 0 }
          p.on("-h", "--help") { out.puts USAGE; return 0 }
        end
        args = parser.parse(argv)
        cmd = args.shift
        need_date = -> { args.first or raise ArgumentError, "#{cmd} needs an event date (YYYY-MM-DD)" }
        case cmd
        when "countdown"
          r = EventPlanner.countdown(need_date.call, today: o[:today])
          text = case r[:status]
                 when "today" then "#{r[:event_date]} is today."
                 when "upcoming" then "#{r[:days]} days to go (#{r[:weeks]} weeks #{r[:remainder_days]} days) until #{r[:event_date]}."
                 else "#{r[:event_date]} was #{-r[:days]} days ago."
                 end
        when "timeline"
          r = EventPlanner.timeline(need_date.call, today: o[:today])
          text = r.map { |x| "#{x[:overdue] ? '!' : ' '} #{x[:due]}  #{x[:task]}#{x[:overdue] ? '  (overdue)' : ''}" }.join("\n")
        when "giveaways"
          raise ArgumentError, "giveaways needs --attendees N" unless o[:attendees]
          r = EventPlanner.giveaway_quantity(attendees: o[:attendees], days: o[:days], uptake: o[:uptake],
                                             per_person: o[:per_person], buffer: o[:buffer])
          text = "Order #{r[:total]} items (#{r[:base]} expected + #{r[:buffer]} buffer)."
        when "order-by"
          r = EventPlanner.order_by_date(need_date.call, production_days: o[:production], delivery_days: o[:delivery],
                                                         safety_days: o[:safety], today: o[:today])
          text = "Order by #{r[:order_by]} (#{r[:working_days]} working days before the event)"
          text += r[:late] ? " - that is #{-r[:days_from_today]} days ago, so ask about express production." : ", #{r[:days_from_today]} days from today."
        when nil
          out.puts USAGE
          return 0
        else
          raise ArgumentError, %(Unknown command "#{cmd}". Run with --help.)
        end
        out.puts(o[:json] ? JSON.pretty_generate(r) : text)
        0
      rescue ArgumentError, OptionParser::ParseError => e
        err.puts "Error: #{e.message}"
        2
      end
    end
  end
end
