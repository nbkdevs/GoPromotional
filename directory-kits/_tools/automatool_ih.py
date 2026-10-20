#!/usr/bin/env python3
"""Add Automatool section + short IH tagline/username to each listing.md; write automatool-ih.json."""
import json, re, pathlib
ROOT = pathlib.Path(__file__).resolve().parents[1]
B = "https://www.gopromotional.co.uk"
USERNAME = "nbkdevs"
P = {
 "homepage": dict(url=f"{B}/", anchor="branded merchandise", name="GoPromotional",
  category="Advertising & Paid Media",
  tagline="50,000+ UK promotional products, printed with your logo",
  short="UK supplier of 50,000+ promotional products printed with your logo, with a dedicated account manager, free PDF artwork proofs and express 48 or 72-hour delivery on selected items.",
  full="""GoPromotional is a UK promotional products supplier, founded in 2010 by Gareth Parkin and based near Sheffield, with offices in London and Dublin. Its catalogue covers more than 50,000 items that can be printed, engraved, embossed or embroidered with a company logo: pens, notebooks, bags, mugs, water bottles, clothing, tech and event giveaways.

## How ordering works

GoPromotional is not a DIY ordering tool. Every enquiry goes to a dedicated account manager who compares products, quantities, branding methods, prices and deadlines with you:

1. **Choose**: explain what the campaign needs to achieve and get recommendations.
2. **Artwork**: the in-house studio checks your logo and emails a PDF proof.
3. **Approve**: nothing is printed until you sign off the final layout.
4. **Deliver**: most items are branded within 5-7 working days, with 48 or 72-hour express options from the London warehouse.

## Why teams use it

- More than 3,500 eco-friendly lines and a range of UK-made products
- A price match if you find the same product cheaper elsewhere
- Rated 4.8 from 5,000+ verified reviews on Reviews.co.uk
- Buyers include schools, universities, charities, sports clubs, NHS teams, local authorities and agencies

The homepage also summarises the research behind promotional items, including the ASI Global Ad Impressions Study and the mere-exposure effect, so marketers can see why useful [{a}]({u}) keeps a logo in view long after an event."""),
 "water-bottles": dict(url=f"{B}/branded-products/branded-water-bottles", anchor="branded water bottles for event marketing",
  name="GoPromotional Water Bottles", category="Events & Webinars",
  tagline="Logo-printed water bottles for events, schools and gyms",
  short="Hundreds of reusable water bottles printed or laser engraved with your logo, from budget plastic sports bottles to Tritan, glass and vacuum-insulated steel, with a free artwork proof and minimums from 10.",
  full="""GoPromotional's water bottle page is a buying resource as much as a catalogue. It lists hundreds of bottles that can carry a company logo, grouped into budget drinks bottles, metal, Tritan, glass, recycled, protein shakers and fruit infusers, in 500ml, 650ml, 750ml and one-litre sizes.

## Choosing a branding method

- **Screen printing** for simple spot-colour logos
- **UltraHD** for full-colour or finely detailed artwork
- **Laser engraving** on metal and stainless steel, which reveals the material instead of adding colour

Not every bottle offers every method, so the studio checks each logo and emails a free proof, usually on the same day.

## What the order data shows

The page publishes figures from 673,883 bottles across 2,797 UK orders (2011 to July 2026). Plastic lines average about 305 units and metal about 158, and 100 is the most common quantity. Schools, universities and colleges (263 buyers) and sports and leisure (260) buy the most.

## Straight answers on specs

It separates reusable, recycled, recyclable and biodegradable claims, and points out that a dishwasher-safe bottle doesn't mean the print should be machine washed. Minimum orders start at 10, with most lines at 25 or 50.

A practical reference for anyone comparing [{a}]({u}), school programmes or sports club kit."""),
 "tote-bags": dict(url=f"{B}/branded-products/tote-bags", anchor="branded tote bags for event marketing",
  name="GoPromotional Tote Bags", category="Events & Webinars",
  tagline="Logo-printed cotton, canvas, jute and non-woven tote bags",
  short="Logo-printed cotton, non-woven, canvas, jute and foldable tote bags from 50 units, with a clear guide to fabric weights, print methods and when a cotton tote is genuinely the greener choice.",
  full="""GoPromotional's tote bag page covers thousands of UK printed bags and explains how to choose between them, not just what they cost.

## Materials at a glance

- **Cotton**: the default; soft, prints cleanly, widest colour choice
- **Non-woven**: the lowest cost per bag and the largest order volumes
- **Canvas**: a heavier, close-woven fabric for retail and premium gifts
- **Jute**: natural texture, picked when the bag is part of the gift

## Fabric weight explained

The ounce figure is the fabric weight per square yard. The range runs from 3oz to 27oz, with 5oz the most common, and at 10oz and above a bag stands up on its own.

## Printing

Screen printing handles most runs. UltraHD transfer and digital printing cover photographic designs, and the page notes that dye-sublimation does not bond to cotton. Both sides can usually be printed.

## Order patterns and eco claims

Average runs are around 400 bags, rising to 700 for non-woven, and more than 400,000 of the 5oz long-handled natural cotton shopper have been printed. The page cites the Danish EPA finding that an organic cotton bag needs about 149 uses to match a single-use plastic bag on climate impact, and says plainly that non-woven polypropylene is not an eco choice.

Minimums start at 50 on most bags, with a five-day standard turnaround and free proofs. Useful for anyone planning [{a}]({u})."""),
 "travel-mugs": dict(url=f"{B}/branded-products/travel-mugs", anchor="branded travel mugs for event marketing",
  name="GoPromotional Travel Mugs", category="HR & People Ops",
  tagline="Logo-printed travel mugs, tumblers and coffee cups",
  short="Branded travel mugs, tumblers and reusable coffee cups, screen printed, pad printed, UltraHD printed or laser engraved with your logo, plus a clear guide to lids, insulation and dishwasher care.",
  full="""GoPromotional's travel mug page brings together hundreds of plastic and metal travel mugs, tumblers and reusable coffee cups that can carry a company logo, and helps buyers pick by use rather than looks.

## Start with the lid and the insulation

- Push-on lids and thumb sliders suit a desk or a short walk; screw tops are safer for commuting
- Spill-resistant is not the same as leak-proof
- Double-walled and vacuum-insulated are different constructions, and copper linings cut radiant heat transfer

## Branding options

Screen printing and UltraHD full colour are available on selected plastic mugs, pad printing suits solid-colour designs, and laser engraving on metal exposes the steel beneath a powder coating rather than printing a colour.

## Care and eco claims

The page separates care for the mug from care for the logo: a dishwasher-safe body can still wear a printed mark, while engraving can't peel away. It also explains why recycled material and end-of-life recyclability are separate claims.

## What 1,100 customers ordered

Across 325,000 mugs, 250ml take-out mugs average around 450 units per order and 470ml Ontario steel tumblers about 100. The Americano family alone has sold more than 120,000 units to over 350 buyers.

Minimums start at 10, most items dispatch in five or seven working days, and plain samples are mostly free. A sensible first stop for [{a}]({u}) or staff gifts."""),
}
ALT = {"homepage": "Marketing Automation", "water-bottles": "Healthcare & Wellness",
       "tote-bags": "E-commerce", "travel-mugs": "Events & Webinars"}
words = lambda t: len(re.findall(r"[A-Za-z0-9][A-Za-z0-9'’.,%&+\-]*", re.sub(r"\]\([^)]*\)", "]", t)))
out = {}
for page, d in P.items():
    full = d["full"].replace("{a}", d["anchor"]).replace("{u}", d["url"])
    links = re.findall(r"\[([^\]]+)\]\(([^)]+)\)", full)
    assert links == [(d["anchor"], d["url"])], (page, links)
    assert full.count(d["anchor"]) == 1, page
    assert len(d["short"]) <= 280 and d["short"].count(d["anchor"]) == 0
    wc = words(full); assert 150 <= wc <= 250, (page, wc)
    assert len(d["tagline"]) <= 60 and len(USERNAME) <= 20
    f = ROOT / page / "listing.md"; md = f.read_text()
    ih_desc = re.search(r"## Indie Hackers.*?### Description[^\n]*\n\n(.*?)\n\n---", md, re.S).group(1).strip()
    if "## Automatool" not in md:
        # replace only the IH table tagline row (the one with "(NN characters)")
        md = re.sub(r"\| Tagline \| ([^|]*?) \((\d+) characters\) \|",
                    lambda m: f"| Tagline (use this; limit 60) | {d['tagline']} ({len(d['tagline'])} / 60) |\n| Old tagline (too long, do not use) | {m.group(1)} ({m.group(2)} characters) |",
                    md, count=1)
        md = md.replace(f"| URL | {d['url']} |\n\n### Description",
                        f"| URL | {d['url']} |\n| Account username (suggestion) | {USERNAME} ({len(USERNAME)} / 20); one account for all 4 products |\n| Logo | `thumb-240.png` (square, at least 200x200) |\n\n### Description", 1)
        sec = f"""## Automatool (https://automatool.app/submit)

| Field | Value | Limit |
|---|---|---|
| Tool Name | {d['name']} | {len(d['name'])} / 128 |
| Category | {d['category']} (alt: {ALT[page]}) | one of the 83 options |
| Short Description | see below | {len(d['short'])} / 280 |
| Website URL | {d['url']} | (URL field) |
| Logo | `thumb-240.png` (square PNG, under 2 MB) | optional |

### Short Description ({len(d['short'])} / 280 characters)

{d['short']}

### Full Description (Markdown, {wc} words; target 150-250)

{full}

"""
        md = md.replace("\n---\nAnchor check:", "\n" + sec + "---\nAnchor check:", 1)
        md = md.replace("and the Indie Hackers description", "the Indie Hackers description and (as the only Markdown link) the Automatool full description", 1)
        assert "## Automatool" in md
        f.write_text(md)
    out[page] = {"page_url": d["url"], "anchor": d["anchor"],
        "automatool": {"name": d["name"], "category": d["category"], "short": d["short"], "full": full,
                       "website_url": d["url"], "logo": f"directory-kits/{page}/thumb-240.png"},
        "ih": {"name": d["name"], "tagline": d["tagline"], "url": d["url"], "description": ih_desc,
               "username_suggestion": USERNAME, "logo": f"directory-kits/{page}/thumb-240.png"}}
    print(f"{page}: short {len(d['short'])}, full {wc}w, tagline {len(d['tagline'])}, cat {d['category']}")
(ROOT / "automatool-ih.json").write_text(json.dumps(out, indent=2, ensure_ascii=False) + "\n")
