# frozen_string_literal: true

require_relative "packlist/version"

module GoPromotional
  # Packing checklists with templates, per-night quantities and a round-trip Markdown format.
  module Packlist
    TemplateItem = Struct.new(:name, :category, :per_night) do
      def to_h = { name: name, category: category, per_night: per_night }
    end

    def self.t(rows) = rows.map { |n, c, p| TemplateItem.new(n, c, p || false).freeze }.freeze

    TEMPLATES = {
      "work-day" => t([
        ["Laptop", "Work"], ["Laptop charger", "Work"], ["Phone charger", "Work"], ["Badge / ID", "Work"],
        ["Notebook", "Work"], ["Pen", "Work"], ["Headphones", "Work"], ["Water bottle", "Work"],
        ["Reusable coffee cup", "Work"], ["Lunch / snacks", "Work"]
      ]),
      "travel" => t([
        ["Passport / ID", "Documents"], ["Tickets / boarding pass", "Documents"], ["Bank cards", "Documents"],
        ["T-shirts", "Clothes", true], ["Underwear", "Clothes", true], ["Socks", "Clothes", true],
        ["Trousers / skirt", "Clothes"], ["Jumper", "Clothes"], ["Sleepwear", "Clothes"],
        ["Toothbrush", "Toiletries"], ["Toothpaste", "Toiletries"], ["Deodorant", "Toiletries"], ["Medications", "Toiletries"],
        ["Phone charger", "Tech"], ["Power bank", "Tech"], ["Plug adapter", "Tech"], ["Headphones", "Tech"],
        ["Reusable water bottle", "Other"], ["Tote bag", "Other"]
      ]),
      "trade-show" => t([
        ["Pop-up banner / stand", "Stand"], ["Table cloth", "Stand"], ["Branded giveaways", "Stand"],
        ["Business cards", "Stand"], ["Brochures / flyers", "Stand"], ["Lead capture sheet or scanner", "Stand"],
        ["Extension lead", "Kit"], ["Laptop / tablet", "Kit"], ["Phone charger", "Kit"], ["Gaffer tape", "Kit"],
        ["Scissors", "Kit"], ["Cable ties", "Kit"], ["Pens", "Kit"],
        ["Water bottles", "Team"], ["Snacks", "Team"], ["First-aid kit", "Team"]
      ]),
      "gym" => t([
        ["Gym clothes", "Gym"], ["Trainers", "Gym"], ["Towel", "Gym"], ["Water bottle", "Gym"], ["Padlock", "Gym"],
        ["Headphones", "Gym"], ["Shower gel", "Gym"], ["Deodorant", "Gym"], ["Snack", "Gym"]
      ])
    }.freeze

    Item = Struct.new(:name, :category, :qty, :packed) do
      def initialize(name, category, qty = 1, packed = false) = super
      def label = qty > 1 ? "#{qty} × #{name}" : name
      def to_h = { name: name, category: category, qty: qty, packed: packed }
    end

    module_function

    def key(s) = s.to_s.strip.downcase

    # Build a checklist. Per-night items get nights + 1 (one spare); duplicate names are merged (first wins).
    def build_list(templates: ["travel"], nights: 0, extras: [])
      raise ArgumentError, "nights must be an integer from 0 to 365" unless nights.is_a?(Integer) && (0..365).cover?(nights)
      seen = {}
      items = []
      push = lambda do |it|
        k = key(it.name)
        next if k.empty? || seen[k]
        seen[k] = true
        items << it
      end
      templates.each do |t|
        tpl = TEMPLATES[t] or raise ArgumentError, %(Unknown template "#{t}". Choose from: #{TEMPLATES.keys.join(', ')})
        tpl.each { |r| push.call(Item.new(r.name, r.category, r.per_night && nights.positive? ? nights + 1 : 1)) }
      end
      extras.each { |e| push.call(Item.new(e.to_s.strip, "Extras")) }
      items
    end

    def find(items, name)
      k = key(name)
      exact = items.find { |i| key(i.name) == k }
      return exact if exact
      partial = items.select { |i| key(i.name).include?(k) }
      return partial.first if partial.size == 1
      raise ArgumentError, %("#{name}" matches #{partial.size} items: #{partial.map(&:name).join(', ')}) if partial.any?
      raise ArgumentError, %(No item matches "#{name}")
    end

    # Mark an item packed (toggle when packed is nil). Exact name first, then a unique substring.
    def mark(items, name, packed = nil)
      it = find(items, name)
      it.packed = packed.nil? ? !it.packed : packed ? true : false
      it
    end

    def progress(items)
      packed = items.count(&:packed)
      { packed: packed, total: items.size, percent: items.empty? ? 0 : (packed * 100.0 / items.size + 0.5).floor }
    end

    def remaining(items) = items.reject(&:packed)

    def filter_items(items, query)
      q = key(query)
      items.select { |i| key(i.name).include?(q) || key(i.category).include?(q) }
    end

    def categories(items) = items.map(&:category).uniq

    # Markdown checkboxes grouped by category; parse_markdown reads it back.
    def to_markdown(items, title = "Packing list")
      lines = ["# #{title}", ""]
      categories(items).each do |c|
        lines << "## #{c}"
        items.select { |i| i.category == c }.each { |i| lines << "- [#{i.packed ? 'x' : ' '}] #{i.label}" }
        lines << ""
      end
      lines.join("\n")
    end

    def to_text(items)
      categories(items).map do |c|
        ([c] + items.select { |i| i.category == c }.map { |i| "  #{i.packed ? '[x]' : '[ ]'} #{i.label}" }).join("\n")
      end.join("\n")
    end

    # Returns [title, items].
    def parse_markdown(md)
      title = "Packing list"
      category = "Items"
      items = []
      md.to_s.each_line do |raw|
        line = raw.strip
        if (m = line.match(/\A#\s+(.+)\z/))
          title = m[1].strip
        elsif (m = line.match(/\A##\s+(.+)\z/))
          category = m[1].strip
        elsif (m = line.match(/\A[-*]\s+\[([ xX])\]\s+(.+)\z/))
          name = m[2].strip
          qty = 1
          if (q = name.match(/\A(\d+)\s*[×x]\s+(.+)\z/))
            qty = q[1].to_i
            name = q[2].strip
          end
          items << Item.new(name, category, qty, m[1] != " ")
        end
      end
      [title, items]
    end
  end
end
