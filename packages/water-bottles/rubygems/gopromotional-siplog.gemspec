# frozen_string_literal: true

require_relative "lib/gopromotional/siplog/version"

Gem::Specification.new do |spec|
  spec.name = "gopromotional-siplog"
  spec.version = GoPromotional::Siplog::VERSION
  spec.authors = ["GoPromotional"]
  spec.summary = "Daily water goal estimate and a simple hydration log with streaks."
  spec.description = "A small hydration tracker: estimate a daily water goal from weight, exercise and weather, log drinks in ml, cl, litres or US fl oz, and see today's progress, the last 7 days, your average and your streak. The log is a plain JSON file."
  spec.homepage = "https://www.gopromotional.co.uk/branded-products/branded-water-bottles"
  spec.license = "MIT"
  spec.required_ruby_version = ">= 3.0"

  spec.metadata = {
    "homepage_uri" => "https://www.gopromotional.co.uk/branded-products/branded-water-bottles",
    "source_code_uri" => "https://github.com/nbkdevs/GoPromotional/tree/main/packages/water-bottles/rubygems",
    "bug_tracker_uri" => "https://github.com/nbkdevs/GoPromotional/issues",
    "rubygems_mfa_required" => "true"
  }

  spec.files = Dir["lib/**/*.rb", "exe/*", "README.md", "LICENSE"]
  spec.bindir = "exe"
  spec.executables = ["gopromotional-siplog"]
  spec.require_paths = ["lib"]
end
