# frozen_string_literal: true

require "optparse"
require_relative "../siplog"

module GoPromotional
  module Siplog
    # Command line interface: goal | add | status | week | undo | set-goal
    module CLI
      USAGE = <<~TXT
        Usage: gopromotional-siplog <command> [options]

        Commands:
          goal --weight KG [--activity MIN] [--hot]   suggest a daily water goal
          add <amount>          log a drink: 250, 500ml, 0.5l, 33cl, 16oz
          status                today's progress and streak
          week                  last 7 days and average
          undo                  remove the last entry for the day
          set-goal <ml>         set the daily goal

        Options: --file PATH (default $GOPROMOTIONAL_SIPLOG_FILE or ~/.gopromotional-siplog.json),
                 --date YYYY-MM-DD, --json, -v/--version, -h/--help
      TXT

      module_function

      def default_file
        env = ENV.fetch("GOPROMOTIONAL_SIPLOG_FILE", "")
        env.empty? ? File.join(Dir.home, ".gopromotional-siplog.json") : env
      end

      def bar(pct)
        ("#" * [20, (pct / 5.0 + 0.5).to_i].min).ljust(20, ".")
      end

      def run(argv, out: $stdout, err: $stderr)
        o = { json: false, activity: 0.0, hot: false }
        parser = OptionParser.new do |p|
          p.banner = USAGE
          p.on("--json") { o[:json] = true }
          p.on("--file PATH") { |v| o[:file] = v }
          p.on("--date DATE") { |v| o[:date] = v }
          p.on("--weight KG", Float) { |v| o[:weight] = v }
          p.on("--activity MIN", Float) { |v| o[:activity] = v }
          p.on("--hot") { o[:hot] = true }
          p.on("-v", "--version") { out.puts VERSION; return 0 }
          p.on("-h", "--help") { out.puts USAGE; return 0 }
        end
        args = parser.parse(argv)
        cmd = args.shift
        (out.puts USAGE; return 0) if cmd.nil?
        day = o[:date] || Date.today.iso8601
        path = o[:file] || default_file
        if cmd == "goal"
          raise ArgumentError, "goal needs --weight KG" unless o[:weight]
          r = { goal: Siplog.daily_goal(weight_kg: o[:weight], activity_minutes: o[:activity], hot: o[:hot]) }
          text = "Suggested daily water goal: #{r[:goal]} ml (a general guide, not medical advice)."
        else
          log = File.exist?(path) ? HydrationLog.from_json(File.read(path, encoding: "utf-8")) : HydrationLog.new
          save = -> { File.write(path, "#{log.to_json}\n") }
          case cmd
          when "add"
            raise ArgumentError, "add needs an amount, e.g. 250 or 500ml" unless args.first
            ml = log.add(args.join(" "), day).ml
            save.call
            r = log.progress(day)
            text = "Added #{ml} ml. #{r[:total]}/#{r[:goal]} ml (#{r[:percent]}%)."
          when "status"
            r = log.progress(day)
            text = "#{r[:date]}: #{r[:total]}/#{r[:goal]} ml [#{bar(r[:percent])}] #{r[:percent]}%" +
                   (r[:reached] ? " - goal reached" : " - #{r[:remaining]} ml to go") +
                   "\nStreak: #{log.streak(day)} day(s)"
          when "week"
            r = log.week(day)
            text = r.map { |d| "#{d[:date]}  #{d[:total].to_s.rjust(5)} ml  #{d[:total] >= log.goal ? 'goal' : ''}" }.join("\n")
            text += "\nAverage: #{log.average(day)} ml/day"
          when "undo"
            e = log.undo(day)
            save.call
            r = e&.to_h
            text = e ? "Removed #{e.ml} ml from #{e.date}." : "Nothing to undo."
          when "set-goal"
            g = Siplog.parse_amount(args.join(" "))
            raise ArgumentError, "goal must be between 500 and 6000 ml" unless (500..6000).cover?(g)
            log.goal = g
            save.call
            r = { goal: g }
            text = "Daily goal set to #{g} ml."
          else
            raise ArgumentError, %(Unknown command "#{cmd}". Run with --help.)
          end
        end
        out.puts(o[:json] ? JSON.pretty_generate(r) : text)
        0
      rescue ArgumentError, OptionParser::ParseError, SystemCallError, JSON::ParserError => e
        err.puts "Error: #{e.message}"
        2
      end
    end
  end
end
