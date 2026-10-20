import re, pathlib
ROOT = pathlib.Path("/workspace/softrank/GoPromotional/directory-kits")
K = {}
K["homepage"] = dict(
 url="https://www.gopromotional.co.uk/", anchor="branded merchandise",
 ph_name="GoPromotional",
 ph_tagline="50,000+ promotional products printed with your logo, with expert help",
 ph_desc=("GoPromotional is a UK supplier of promotional products and branded merchandise. Choose from 50,000+ items, "
  "from pens, notebooks and mugs to bags, clothing, tech and trade show giveaways, printed, engraved or embroidered with your logo. "
  "A dedicated account manager helps with product choice, quantities and deadlines, the artwork team sends a PDF proof before anything "
  "is printed, and most samples are free. Price match promise, plus 48/72-hour express options."),
 ph_tags=["Marketing", "E-Commerce", "Branding"], ph_fallback=["Sales", "Events"],
 ph_comment=("Hi Product Hunt! GoPromotional helps organisations put their logo on things people actually keep and use. "
  "The range covers 50,000+ products, from pens and mugs to bags, clothing and tech."
  "\n\nWhat makes us different is that we're not a DIY ordering site. Every order gets a dedicated account manager "
  "who compares products, prices and deadlines with you. Our artwork team checks your logo and emails a "
  "PDF proof, and nothing is printed until you approve it. Most samples are free, and selected products can be delivered in 48 or 72 "
  "hours from our London warehouse.\n\nWe work with schools, charities, sports clubs, NHS teams and businesses of every size. "
  "If you're planning an event, onboarding new staff or looking for client gifts, we'd love to help you choose "
  "branded merchandise that fits your audience, budget and deadline. Questions welcome below!"),
 dn_title="GoPromotional: Promotional Products & Branded Merchandise",
 dn_cat="E-Commerce (alt: Business / Marketing)",
 dn_desc=("GoPromotional is a UK supplier of custom-printed promotional products and promotional gifts, founded in 2010 by Gareth Parkin. "
  "The head office is near Sheffield in South Yorkshire, with further offices in London and Dublin.\n\n"
  "The website brings together more than 50,000 products that can be personalised with a company logo, grouped into clear categories: "
  "pens and pencils, notes and paper products, bags and backpacks, travel mugs, technology and gadgets, wearables, outdoor and leisure, "
  "lifestyle, notebooks, drinkware, toys and novelties, and trade show and event items. Each product page shows prices and minimum "
  "quantities, and a fast quote form sits next to every item.\n\n"
  "The service behind the site is built around people rather than a self-service checkout. A dedicated account manager helps buyers "
  "compare products and deadlines, the in-house artwork team checks every logo and sends a PDF proof, and production only starts once "
  "the proof is approved. Branding methods include screen printing, UltraHD full colour printing, laser engraving, embroidery, foil "
  "printing and embossing.\n\n"
  "Other features include free samples on most items, a price match promise on like-for-like quotes, over 3,500 products with "
  "environmental credentials, a UK-made range, and express 48 or 72-hour delivery on selected lines. For anyone looking for "
  "branded merchandise for events, staff or clients, the homepage is a good starting point for exploring the full range."),
 dn_tags=["promotional products", "promotional gifts", "corporate gifts", "custom printing", "logo printing", "trade show giveaways", "UK", "e-commerce"],
 ih_name="GoPromotional",
 ih_tagline="UK promotional products, printed with your logo and managed by a real account manager",
 ih_desc=("GoPromotional is a UK business supplying promotional products to schools, charities, sports clubs, NHS teams, agencies and "
  "companies of every size. Founded in 2010 by Gareth Parkin, it now lists more than 50,000 items on its website, from pens and notebooks "
  "to bags, mugs, clothing and tech.\n\nThe model is deliberately not DIY: each customer gets a dedicated account manager, and an "
  "in-house artwork team checks every logo and emails a PDF proof before production. Free samples on most items, a price match "
  "promise and express 48 or 72-hour delivery on selected lines from the London warehouse help buyers order with confidence. "
  "If you need branded merchandise for an event, a staff welcome pack or client gifts, the site makes it easy to compare options "
  "and request a quote."),
)
K["water-bottles"] = dict(
 url="https://www.gopromotional.co.uk/branded-products/branded-water-bottles", anchor="branded water bottles for event marketing",
 ph_name="GoPromotional Water Bottles",
 ph_tagline="Logo-printed water bottles, from budget sports bottles to insulated steel",
 ph_desc=("A UK range of custom water bottles printed or engraved with your logo: budget plastic sports bottles for big giveaways, "
  "Tritan, glass and aluminium bottles, and stainless steel and copper vacuum-insulated bottles for corporate gifts. BPA-free, "
  "biodegradable and recycled-material options are included. Branding by screen print, UltraHD full colour or laser engraving, with "
  "a free artwork proof before production. Minimum orders start at 10 depending on the bottle."),
 ph_tags=["Marketing", "Branding", "Health & Fitness"], ph_fallback=["E-Commerce", "Sustainability"],
 ph_comment=("Hello Product Hunt! This is GoPromotional's water bottle range: reusable bottles that companies, schools, gyms and "
  "charities can print with their logo and hand out at events or give to staff and clients.\n\nThe page is designed to help you choose "
  "rather than just scroll. It explains when a budget plastic sports bottle makes sense for a large hand-out and when stainless steel, "
  "glass or a copper vacuum-insulated bottle is better for corporate gifting. It also covers the difference between screen printing, "
  "UltraHD full colour and laser engraving, and it is honest that reusable, recycled, recyclable and biodegradable are different claims.\n\n"
  "We've shared what our UK order records show too: 100 units is the most common order, and schools and sports clubs are the biggest "
  "buyers. If you're sourcing branded water bottles for event marketing, staff wellbeing or a fundraiser, we'd love your feedback!"),
 dn_title="Branded Water Bottles by GoPromotional",
 dn_cat="E-Commerce (alt: Marketing / Health & Fitness)",
 dn_desc=("This page from GoPromotional, a UK promotional merchandise supplier, presents its full range of custom water bottles that "
  "can be printed or engraved with a company logo. The range runs from low-cost plastic sports bottles for large promotions to "
  "aluminium, stainless steel, glass and insulated metal bottles for corporate gifts, with BPA-free, biodegradable and recycled-material "
  "options for eco-conscious campaigns.\n\n"
  "Visitors can browse by type using quick category tiles (budget, plastic, metal, eco, Tritan, glass and fruit infusers) and filter the "
  "product grid by capacity, material and imprint method. Every product card shows the starting price, minimum order quantity and "
  "typical dispatch time.\n\n"
  "Below the grid, a practical buying guide explains how to match a bottle to the audience, quantity and deadline, how screen printing, "
  "UltraHD full colour and laser engraving differ, and what to check for leak resistance, dishwasher suitability and recycled claims. "
  "The page also shares insight from GoPromotional's own UK order records since 2011, including the most common order size and the "
  "sectors that buy the most bottles.\n\n"
  "An FAQ covers minimum orders, artwork formats, samples and free proofs. It's a useful resource for anyone sourcing "
  "branded water bottles for event marketing, school and university programmes, sports clubs or charity fundraisers."),
 dn_tags=["water bottles", "promotional products", "custom printing", "reusable bottles", "eco-friendly", "corporate gifts", "events", "UK"],
 ih_name="GoPromotional Water Bottles",
 ih_tagline="Custom logo water bottles for events, schools, gyms and corporate gifts",
 ih_desc=("GoPromotional's water bottle page gathers hundreds of reusable bottles that can be printed or engraved with a logo, "
  "from budget plastic sports bottles for large giveaways to stainless steel, glass and copper vacuum-insulated bottles for client gifts. "
  "Filters for capacity, material and imprint method make it quick to narrow down, and each product shows the starting price and "
  "minimum order.\n\nThe buying guide is written for real decisions: which bottle suits your audience and order size, how screen "
  "printing, UltraHD and laser engraving compare, and why recycled and recyclable are different claims. It also shares data from the "
  "company's UK order records. A good fit for anyone choosing branded water bottles for event marketing, staff wellbeing or fundraising."),
)
K["tote-bags"] = dict(
 url="https://www.gopromotional.co.uk/branded-products/tote-bags", anchor="branded tote bags for event marketing",
 ph_name="GoPromotional Tote Bags",
 ph_tagline="Custom printed cotton, jute, canvas and non-woven tote bags for your brand",
 ph_desc=("Reusable tote bags printed with your logo, from GoPromotional in the UK. Choose cotton shoppers, non-woven convention bags, "
  "canvas, jute, foldable and organic or recycled cotton totes, with screen printing for solid colours and UltraHD transfer or digital "
  "printing for full-colour designs, on one or both sides. Most bags start at 50 units, standard turnaround is five working days from "
  "artwork approval, and selected bags can be dispatched within 48 hours."),
 ph_tags=["Marketing", "Branding", "Sustainability"], ph_fallback=["E-Commerce", "Events"],
 ph_comment=("Hi everyone! GoPromotional prints tote bags for trade shows, conferences, store openings, charities and retailers, "
  "and this page is our complete tote range in one place.\n\nWe built it to answer the questions buyers actually ask. What's the "
  "difference between cotton, canvas, jute and non-woven? What do 5oz, 7oz and 10oz mean? (It's fabric weight per square yard, not the "
  "bag's weight.) Why can't you dye-sublimate onto cotton? We're also straight about the environment: a cotton tote only beats a "
  "single-use bag if it gets reused many times, so we'd rather help you pick one people will keep.\n\nYou'll get a free proof, usually "
  "the same day, and nothing is printed until you approve it. Samples are free on most bags. If you're planning branded tote bags for "
  "event marketing or delegate packs, I'd love to hear what you think of the guide!"),
 dn_title="Branded Tote Bags by GoPromotional",
 dn_cat="E-Commerce (alt: Marketing / Sustainability)",
 dn_desc=("GoPromotional's tote bags page is a complete catalogue of reusable bags that can be custom printed with a company logo, "
  "from a UK promotional merchandise supplier. The range includes natural and coloured cotton shoppers, non-woven convention bags, "
  "heavier canvas totes, jute bags, foldable shoppers and organic or recycled cotton options, with long and short handle styles.\n\n"
  "Category tiles at the top split the range into cotton, jute, canvas, non-woven and foldable bags, and the product grid can be "
  "filtered by handle length, fabric weight, imprint method and number of print colours. Each product card lists the starting price "
  "and minimum order.\n\n"
  "The written guide is a highlight. It explains how the materials differ, what 5oz, 7oz and 10oz fabric weights actually mean, which "
  "printing method suits which design (screen printing for solid colours, UltraHD transfer or digital printing for full-colour artwork), "
  "and why dye-sublimation does not work on cotton. It also takes an honest look at the environmental case for cotton totes, citing the "
  "Danish EPA life cycle assessment, and shares what GoPromotional's own order records show about demand.\n\n"
  "An FAQ covers minimum orders, turnaround, proofs, samples and set-up charges. It is a practical resource for anyone choosing "
  "branded tote bags for event marketing, delegate packs, retail or charity campaigns."),
 dn_tags=["tote bags", "cotton bags", "promotional products", "custom printing", "reusable bags", "sustainability", "trade shows", "UK"],
 ih_name="GoPromotional Tote Bags",
 ih_tagline="Logo-printed cotton, canvas, jute and non-woven tote bags, with an honest buying guide",
 ih_desc=("This GoPromotional page brings together a large range of reusable tote bags that can be printed with your logo: cotton "
  "shoppers, non-woven convention bags, canvas, jute, foldable and organic or recycled cotton totes. Filters for fabric weight, handle "
  "length and imprint method help you narrow it down quickly.\n\nWhat sets it apart is the guide. It explains what 5oz, 7oz and 10oz "
  "fabric weights mean, which printing method suits your artwork, and why dye-sublimation doesn't bond to cotton, and it is candid that "
  "a cotton tote only pays off environmentally when it is reused. Most bags start at 50 units, with a free proof and samples on most "
  "items. Ideal if you're ordering branded tote bags for event marketing, conferences or retail."),
)
K["travel-mugs"] = dict(
 url="https://www.gopromotional.co.uk/branded-products/travel-mugs", anchor="branded travel mugs for event marketing",
 ph_name="GoPromotional Travel Mugs",
 ph_tagline="Branded travel mugs, tumblers and reusable coffee cups with your logo",
 ph_desc=("Custom travel mugs from GoPromotional in the UK: plastic travel mugs, stainless steel and copper vacuum-insulated tumblers, "
  "reusable coffee cups, enamel mugs and big-name brands such as Stanley and Contigo, all printed or engraved with your logo. Branding "
  "options include screen and pad printing, UltraHD full colour and laser engraving on metal. Minimum orders start at 10 on selected "
  "lines, most products dispatch in five or seven working days, and a free artwork proof comes first."),
 ph_tags=["Marketing", "Branding", "Travel"], ph_fallback=["E-Commerce", "Sustainability"],
 ph_comment=("Hey Product Hunt! This is GoPromotional's travel mug collection: hundreds of travel mugs, tumblers and reusable coffee "
  "cups that businesses, schools, food and drink venues and charities can brand with their logo.\n\nWe wanted the page to help with the details that "
  "matter once a mug is in someone's bag. It explains how to choose a lid (push-on, thumb slider or screw top), why spill-resistant and "
  "leak-proof aren't the same, and how double-walled and vacuum-insulated construction differ. It also covers when laser engraving beats "
  "printing, and how to wash a printed mug so the logo lasts.\n\nOur most popular family is the Americano range, which spans compact, "
  "insulated grip and spill-proof lid options. Plain samples and a free artwork proof are available before you order. If you're "
  "choosing branded travel mugs for event marketing or employee gifts, we'd love your thoughts!"),
 dn_title="Branded Travel Mugs by GoPromotional",
 dn_cat="E-Commerce (alt: Marketing / Food & Drink)",
 dn_desc=("This page from UK promotional merchandise supplier GoPromotional showcases its collection of travel mugs that can be "
  "printed or engraved with a company logo. It covers plastic travel mugs, stainless steel and aluminium mugs, copper vacuum-insulated "
  "tumblers, reusable coffee cups, enamel mugs and thermos flasks, alongside well-known brands such as Stanley and Contigo.\n\n"
  "Visual category tiles lead into plastic, metal, tumbler, reusable cup, enamel and vacuum flask ranges, and the product grid can be "
  "filtered by capacity, material and imprint method. Product cards show starting prices, minimum quantities and dispatch times.\n\n"
  "The buying guide is detailed and practical. It explains how to choose by material and use, how push-on, thumb-slider and screw-top "
  "lids suit different journeys, why spill-resistant and leak-proof are not the same, and how double-walled and vacuum-insulated mugs "
  "differ. It compares screen printing, pad printing, UltraHD full colour and laser engraving, gives clear care advice for printed logos, "
  "and explains the difference between recycled and recyclable materials. Insight from GoPromotional's own order records shows which "
  "ranges and sectors buy most.\n\n"
  "An FAQ answers questions on minimums, dispatch, samples, Pantone matching and artwork. A strong resource for anyone ordering "
  "branded travel mugs for event marketing, employee gifts or food and drink promotions."),
 dn_tags=["travel mugs", "tumblers", "reusable coffee cups", "promotional products", "custom printing", "laser engraving", "corporate gifts", "UK"],
 ih_name="GoPromotional Travel Mugs",
 ih_tagline="Custom printed and engraved travel mugs, tumblers and reusable coffee cups",
 ih_desc=("GoPromotional's travel mug page lists hundreds of mugs, tumblers and reusable coffee cups that can be branded with a logo: "
  "budget plastic travel mugs for volume giveaways, stainless steel and copper vacuum-insulated tumblers for client and staff gifts, "
  "plus enamel mugs and brands such as Stanley and Contigo.\n\nThe guide is genuinely useful. It explains how to pick a lid, why "
  "spill-resistant isn't leak-proof, how double-walled and vacuum-insulated mugs differ, and when laser engraving is a better choice "
  "than print. Filters for capacity, material and imprint method keep the range manageable, and free artwork proofs and plain samples "
  "are available. A practical starting point for anyone sourcing branded travel mugs for event marketing or employee gifts."),
)

def cnt(s): return len(s)
def words(s): return len(re.findall(r"\b[\w'£,./-]+\b", s))
def once(s, a): return len(re.findall(re.escape(a), s, re.I))
errs = []
for page, d in K.items():
    a = d["anchor"]
    checks = [("ph_name", cnt(d["ph_name"]) <= 40), ("ph_tagline", cnt(d["ph_tagline"]) <= 80), ("ph_desc", cnt(d["ph_desc"]) <= 500),
              ("ph_desc anchor<=1", once(d["ph_desc"], a) <= 1), ("ph_comment len", 600 <= cnt(d["ph_comment"]) <= 900), ("ph_comment anchor", once(d["ph_comment"], a) == 1),
              ("dn words", 150 <= words(d["dn_desc"]) <= 300), ("dn anchor", once(d["dn_desc"], a) == 1),
              ("ih words", 100 <= words(d["ih_desc"]) <= 200), ("ih anchor", once(d["ih_desc"], a) == 1)]
    for n, ok in checks:
        if not ok: errs.append((page, n, cnt(d.get(n.split()[0], "")) if n.split()[0] in d else ""))
    print(page, {k: cnt(d[k]) for k in ["ph_name", "ph_tagline", "ph_desc", "ph_comment"]}, "dn", words(d["dn_desc"]), "ih", words(d["ih_desc"]),
          "anchors ph/c/dn/ih", once(d["ph_desc"], a), once(d["ph_comment"], a), once(d["dn_desc"], a), once(d["ih_desc"], a))
print("ERRORS", errs)
# distinctness
names = [d["ph_name"] for d in K.values()]; tags = [d["ph_tagline"] for d in K.values()]
assert len(set(names)) == 4 and len(set(tags)) == 4

def md(page, d):
    a = d["anchor"]
    return f"""# Directory listing kit: {page}

Target page: {d['url']}
Exact anchor phrase: "{a}"

All copy is based on the live page content (fetched 2026-10-20). Plain text only; no prices or stats beyond what the page states.

## Product Hunt

| Field | Value | Characters |
|---|---|---|
| Name | {d['ph_name']} | {cnt(d['ph_name'])} / 40 |
| Tagline | {d['ph_tagline']} | {cnt(d['ph_tagline'])} / 80 |
| Link | {d['url']} | (URL field) |
| Pricing | Paid | |
| Launch tags | {', '.join(d['ph_tags'])} | |
| Fallback tags | {', '.join(d['ph_fallback'])} | |

### Description ({cnt(d['ph_desc'])} / 500 characters)

{d['ph_desc']}

### First comment (maker comment, {cnt(d['ph_comment'])} characters; target 600-900)

{d['ph_comment']}

Media: `thumb-240.png` (thumbnail 240x240), `gallery-1.png`, `gallery-2.png`, `gallery-3.png` (1270x760).

## Design Nominees (website submission)

| Field | Value |
|---|---|
| Title | {d['dn_title']} ({cnt(d['dn_title'])} characters) |
| Category (guess) | {d['dn_cat']} |
| URL | {d['url']} |
| Tags | {', '.join(d['dn_tags'])} |
| Screenshot | `screenshot-1280x800.png` |

### Description ({words(d['dn_desc'])} words; target 150-300)

{d['dn_desc']}

## Indie Hackers (product listing)

| Field | Value |
|---|---|
| Product name | {d['ih_name']} ({cnt(d['ih_name'])} characters) |
| Tagline | {d['ih_tagline']} ({cnt(d['ih_tagline'])} characters) |
| URL | {d['url']} |

### Description ({words(d['ih_desc'])} words; target 100-200)

{d['ih_desc']}

---
Anchor check: the exact phrase "{a}" appears once each in the Product Hunt first comment, the Design Nominees description and the Indie Hackers description{', and once in the Product Hunt description' if once(d['ph_desc'], a) else ''}. If a form renders Markdown or HTML, link that phrase to {d['url']}; otherwise leave it as plain text and put the URL in the URL field.
"""
if not errs:
    for page, d in K.items():
        (ROOT / page / "listing.md").write_text(md(page, d))
    print("written")
