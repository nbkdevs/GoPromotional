# GoPromotional.PackList

Build a packing checklist from templates (work day, travel, trade show, gym), scale clothes to the number of nights, add your own items, tick things off and see what is left. Lists are stored as plain Markdown checklists you can also edit by hand.

- **4 templates**: work day (10 items), travel (19), trade show (16), gym (9). Combine them; duplicates are merged.
- **Per-night quantities**: T-shirts, underwear and socks scale to nights + 1.
- **Tick off** items by a unique part of their name; see **progress** and what is **still to pack**.
- **Markdown storage**: `- [x] 3 × T-shirts` round-trips, so the list works in any editor, GitHub or Obsidian.

## Install

```sh
dotnet add package GoPromotional.PackList
```

## Usage

```csharp
using GoPromotional;

var items = PackList.BuildList(new[] { "trade-show" }, extras: new[] { "Tote bags" });
PackList.Mark(items, "gaffer");                       // unique substring match
var p = PackList.Progress(items);                     // Packed 1, Total 17, Percent 6
File.WriteAllText("expo.md", PackList.ToMarkdown(items, "Expo"));

var (title, back) = PackList.ParseMarkdown(File.ReadAllText("expo.md"));
```

Targets .NET Standard 2.0 and .NET 8. Run the tests with `dotnet test tests`.

## Links

- Website: [branded tote bags for event marketing](https://www.gopromotional.co.uk/branded-products/tote-bags)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/tote-bags/nuget`)
- Also available on npm, PyPI, crates.io and RubyGems as `gopromotional-packlist`, and as the Docker image `iamnazzal/gopromotional-packlist`
- Licence: MIT
