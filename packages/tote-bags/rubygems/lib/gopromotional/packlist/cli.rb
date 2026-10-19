# frozen_string_literal: true

require "json"
require "optparse"
require_relative "../packlist"

module GoPromotional
  module Packlist
    # Command line interface: templates | new | check | uncheck | add | status
    module CLI
      USAGE = <<~TXT
        Usage: gopromotional-packlist <command> [options]

        Commands:
          templates                         list built-in templates
          new [--template travel[,gym,...]] [--nights N] [--extra ITEM]... [--title T] [--out FILE]
          check <file> <item>               mark packed (unique partial names work)
          uncheck <file> <item>
          add <file> <item>
          status <file>                     progress and what is still left

        Options: --json, -v/--version, -h/--help
      TXT

      module_function

      def run(argv, out: $stdout, err: $stderr)
        o = { json: false, template: "travel", nights: 0, extras: [], title: "Packing list" }
        parser = OptionParser.new do |p|
          p.banner = USAGE
          p.on("--json") { o[:json] = true }
          p.on("--template NAMES") { |v| o[:template] = v }
          p.on("--nights N", Integer) { |v| o[:nights] = v }
          p.on("--extra ITEM") { |v| o[:extras] << v }
          p.on("--title TITLE") { |v| o[:title] = v }
          p.on("--out FILE") { |v| o[:out] = v }
          p.on("-v", "--version") { out.puts VERSION; return 0 }
          p.on("-h", "--help") { out.puts USAGE; return 0 }
        end
        args = parser.parse(argv)
        cmd = args.shift
        case cmd
        when nil
          out.puts USAGE
        when "templates"
          out.puts(o[:json] ? JSON.pretty_generate(TEMPLATES.transform_values { |v| v.map(&:to_h) }) : TEMPLATES.map { |k, v| "#{k} (#{v.size} items)" })
        when "new"
          names = o[:template].split(",").map(&:strip).reject(&:empty?)
          items = Packlist.build_list(templates: names, nights: o[:nights], extras: o[:extras])
          md = Packlist.to_markdown(items, o[:title])
          if o[:out]
            File.write(o[:out], md)
            out.puts "Wrote #{items.size} items to #{o[:out]}"
          else
            out.puts(o[:json] ? JSON.pretty_generate(items.map(&:to_h)) : md)
          end
        when "check", "uncheck", "add"
          file = args.shift
          name = args.join(" ")
          raise ArgumentError, "#{cmd} needs a file and an item name" if file.nil? || name.empty?
          title, items = Packlist.parse_markdown(File.read(file, encoding: "utf-8"))
          if cmd == "add"
            raise ArgumentError, %("#{name}" is already on the list) if items.any? { |i| i.name.downcase == name.downcase }
            items << Item.new(name, "Extras")
          else
            Packlist.mark(items, name, cmd == "check")
          end
          File.write(file, Packlist.to_markdown(items, title))
          pr = Packlist.progress(items)
          out.puts "#{pr[:packed]}/#{pr[:total]} packed (#{pr[:percent]}%)"
        when "status"
          file = args.shift or raise ArgumentError, "status needs a file"
          title, items = Packlist.parse_markdown(File.read(file, encoding: "utf-8"))
          pr = Packlist.progress(items)
          left = Packlist.remaining(items)
          if o[:json]
            out.puts JSON.pretty_generate(pr.merge(remaining: left.map(&:to_h)))
          else
            out.puts "#{title}: #{pr[:packed]}/#{pr[:total]} packed (#{pr[:percent]}%)"
            out.puts(left.empty? ? "All packed." : "Still to pack: #{left.map(&:name).join(', ')}")
          end
        else
          raise ArgumentError, %(Unknown command "#{cmd}". Run with --help.)
        end
        0
      rescue ArgumentError, OptionParser::ParseError, SystemCallError => e
        err.puts "Error: #{e.message}"
        2
      end
    end
  end
end
