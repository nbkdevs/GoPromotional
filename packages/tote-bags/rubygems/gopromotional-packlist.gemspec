# frozen_string_literal: true

require_relative "lib/gopromotional/packlist/version"

Gem::Specification.new do |spec|
  spec.name = "gopromotional-packlist"
  spec.version = GoPromotional::Packlist::VERSION
  spec.authors = ["GoPromotional"]
  spec.summary = "Packing checklists with templates, per-night quantities and a Markdown format."
  spec.description = "Build a packing checklist from templates (work day, travel, trade show, gym), scale clothes to the number of nights, add your own items, tick things off and see what is left. Lists are stored as plain Markdown checklists you can also edit by hand."
  spec.homepage = "https://www.gopromotional.co.uk/branded-products/tote-bags"
  spec.license = "MIT"
  spec.required_ruby_version = ">= 3.0"

  spec.metadata = {
    "homepage_uri" => "https://www.gopromotional.co.uk/branded-products/tote-bags",
    "source_code_uri" => "https://github.com/nbkdevs/GoPromotional/tree/main/packages/tote-bags/rubygems",
    "bug_tracker_uri" => "https://github.com/nbkdevs/GoPromotional/issues",
    "rubygems_mfa_required" => "true"
  }

  spec.files = Dir["lib/**/*.rb", "exe/*", "README.md", "LICENSE"]
  spec.bindir = "exe"
  spec.executables = ["gopromotional-packlist"]
  spec.require_paths = ["lib"]
end
