# Directory listing kits

One folder per GoPromotional target page, for manual submission to Product Hunt, Design Nominees, Indie Hackers and Automatool. `automatool-ih.json` holds the Automatool and Indie Hackers fields for all 4 pages in one paste-ready file (regenerate with `python3 _tools/automatool_ih.py`).
Nothing here has been submitted.

Each folder holds:
- `listing.md` with all copy and character/word counts, and the exact anchor phrase used once per long field
- `thumb-240.png`, a 240x240 thumbnail (GoPromotional's 2026 favicon roundel, from the site, centred on white)
- `gallery-1.png` to `gallery-3.png`, 1270x760 screenshots of the live page (hero, then the product grid)
- `screenshot-1280x800.png`, an above-the-fold 1280x800 screenshot

`_tools/` has the scripts that produced them (headless Chrome via playwright-core, and the copy generator with its checks).
