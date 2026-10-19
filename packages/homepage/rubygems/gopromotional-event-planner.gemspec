# frozen_string_literal: true

require_relative "lib/gopromotional/event_planner/version"

Gem::Specification.new do |spec|
  spec.name = "gopromotional-event-planner"
  spec.version = GoPromotional::EventPlanner::VERSION
  spec.authors = ["GoPromotional"]
  spec.summary = "Event countdown, planning timeline, giveaway quantities and print order-by dates."
  spec.description = "Plan an event or trade show from the command line or your code: a countdown, a 12-step planning timeline with due dates, an estimate of how many giveaway items to order, and the latest order date for printed merchandise in working days."
  spec.homepage = "https://www.gopromotional.co.uk/"
  spec.license = "MIT"
  spec.required_ruby_version = ">= 3.0"

  spec.metadata = {
    "homepage_uri" => "https://www.gopromotional.co.uk/",
    "source_code_uri" => "https://github.com/nbkdevs/GoPromotional/tree/main/packages/homepage/rubygems",
    "bug_tracker_uri" => "https://github.com/nbkdevs/GoPromotional/issues",
    "rubygems_mfa_required" => "true"
  }

  spec.files = Dir["lib/**/*.rb", "exe/*", "README.md", "LICENSE"]
  spec.bindir = "exe"
  spec.executables = ["gopromotional-event-planner"]
  spec.require_paths = ["lib"]
end
