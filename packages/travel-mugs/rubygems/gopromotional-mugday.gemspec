# frozen_string_literal: true

require_relative "lib/gopromotional/mugday/version"

Gem::Specification.new do |spec|
  spec.name = "gopromotional-mugday"
  spec.version = GoPromotional::Mugday::VERSION
  spec.authors = ["GoPromotional"]
  spec.summary = "Caffeine tracker with half-life estimate, sleep-ready time and reusable-cup stats."
  spec.description = "Log coffee, tea and other drinks with typical caffeine values, compare the day's total with a limit (400 mg adult, 200 mg in pregnancy), estimate how much caffeine is still in your system, find out when it should drop below a sleep-friendly level, and track how often you used a reusable cup. Typical values only; not medical advice."
  spec.homepage = "https://www.gopromotional.co.uk/branded-products/travel-mugs"
  spec.license = "MIT"
  spec.required_ruby_version = ">= 3.0"

  spec.metadata = {
    "homepage_uri" => "https://www.gopromotional.co.uk/branded-products/travel-mugs",
    "source_code_uri" => "https://github.com/nbkdevs/GoPromotional/tree/main/packages/travel-mugs/rubygems",
    "bug_tracker_uri" => "https://github.com/nbkdevs/GoPromotional/issues",
    "rubygems_mfa_required" => "true"
  }

  spec.files = Dir["lib/**/*.rb", "exe/*", "README.md", "LICENSE"]
  spec.bindir = "exe"
  spec.executables = ["gopromotional-mugday"]
  spec.require_paths = ["lib"]
end
