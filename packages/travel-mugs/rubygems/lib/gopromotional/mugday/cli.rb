# frozen_string_literal: true

require "optparse"
require_relative "../mugday"

module GoPromotional
  module Mugday
    # Command line interface: drinks | add | today | in-system | sleep-ready | cups | undo
    module CLI
      USAGE = <<~TXT
        Usage: gopromotional-mugday <command> [options]

        Commands:
          drinks                                typical caffeine per drink
          add <drink> [--time T] [--mg N] [--reusable]
          today [--date D] [--limit N]          total for a day
          in-system [--at T]                    caffeine still in your system
          sleep-ready [--threshold 50]          when caffeine drops below a threshold
          cups [--date D]                       reusable vs disposable cups, last 7 days
          undo                                  remove the last drink

        Options: --file PATH (default $GOPROMOTIONAL_MUGDAY_FILE or ~/.gopromotional-mugday.json), --json
        Times are YYYY-MM-DDTHH:MM. Caffeine values are typical figures; not medical advice.
      TXT

      module_function

      def default_file
        env = ENV.fetch("GOPROMOTIONAL_MUGDAY_FILE", "")
        env.empty? ? File.join(Dir.home, ".gopromotional-mugday.json") : env
      end

      def run(argv, out: $stdout, err: $stderr)
        o = { json: false, reusable: false, threshold: 50.0 }
        parser = OptionParser.new do |p|
          p.banner = USAGE
          p.on("--json") { o[:json] = true }
          p.on("--file PATH") { |v| o[:file] = v }
          p.on("--time T") { |v| o[:time] = v }
          p.on("--mg N", Float) { |v| o[:mg] = v }
          p.on("--reusable") { o[:reusable] = true }
          p.on("--date D") { |v| o[:date] = v }
          p.on("--limit N", Float) { |v| o[:limit] = v }
          p.on("--at T") { |v| o[:at] = v }
          p.on("--threshold N", Float) { |v| o[:threshold] = v }
          p.on("-v", "--version") { out.puts VERSION; return 0 }
          p.on("-h", "--help") { out.puts USAGE; return 0 }
        end
        args = parser.parse(argv)
        cmd = args.shift
        n = ->(x) { Mugday.num(x) }
        case cmd
        when nil
          out.puts USAGE
          return 0
        when "drinks"
          out.puts(o[:json] ? JSON.pretty_generate(DRINKS) :
            DRINKS.map { |k, v| "#{k.ljust(20)} #{v[:mg].to_s.rjust(4)} mg#{v[:ml].positive? ? "  (#{v[:ml]} ml)" : ''}" })
          return 0
        end
        path = o[:file] || default_file
        log = File.exist?(path) ? CaffeineLog.from_json(File.read(path, encoding: "utf-8")) : CaffeineLog.new
        save = -> { File.write(path, "#{log.to_json}\n") }
        case cmd
        when "add"
          raise ArgumentError, "add needs a drink name (see `drinks`)" if args.empty?
          e = log.add(args.join(" "), time: o[:time] || Mugday.now_local, mg: o[:mg] && n.(o[:mg]), reusable: o[:reusable])
          save.call
          d = log.day(e.time[0, 10])
          r = e.to_h
          text = "Logged #{e.drink} (#{n.(e.mg)} mg#{e.reusable ? ', reusable cup' : ''}). Today: #{n.(d[:total])}/#{n.(d[:limit])} mg."
        when "today"
          r = log.day(o[:date], limit: o[:limit] && n.(o[:limit]))
          text = "#{r[:date]}: #{n.(r[:total])} mg from #{r[:drinks]} drink(s); " +
                 (r[:over].positive? ? "#{n.(r[:over])} mg over the #{n.(r[:limit])} mg limit." : "#{n.(r[:remaining])} mg left of #{n.(r[:limit])} mg.")
        when "in-system"
          at = o[:at] || Mugday.now_local
          r = { at: at, mg: log.in_system(at) }
          text = "About #{r[:mg]} mg of caffeine still in your system at #{at} (half-life #{n.(log.half_life_hours)} h)."
        when "sleep-ready"
          raise ArgumentError, "--threshold must be above 0" unless o[:threshold].positive?
          t = log.sleep_ready(o[:threshold])
          r = { threshold: n.(o[:threshold]), time: t }
          text = t ? "Below #{n.(o[:threshold])} mg from about #{t.tr('T', ' ')}." : "No drinks logged."
        when "cups"
          r = log.cups(o[:date])
          text = "Last 7 days: #{r[:reusable]} reusable, #{r[:disposable]} disposable (#{r[:rate]}% reusable)."
        when "undo"
          e = log.undo
          save.call
          r = e&.to_h
          text = e ? "Removed #{e.drink} at #{e.time}." : "Nothing to undo."
        else
          raise ArgumentError, %(Unknown command "#{cmd}". Run with --help.)
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
