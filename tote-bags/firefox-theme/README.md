# Canvas Trail — Firefox theme (optional)

Static Firefox theme generated from `../chrome-theme/manifest.json` colours and a 1920×200 header band cut from
`../chrome-theme/images/theme_ntp_background.png`. The left 40% of the header fades into the Chrome `frame` colour so tab
text keeps the same contrast as the Chrome theme. Firefox themes cannot set a new-tab background image, so only
`ntp_background`/`ntp_text` colours carry over.

- gecko id: `canvas-trail-theme@gopromotional.co.uk`
- Build: `cd tote-bags/firefox-theme && rm -f firefox-theme.zip && zip -r -X firefox-theme.zip manifest.json images`
- Test: `about:debugging#/runtime/this-firefox` → **Load Temporary Add-on…** → `manifest.json`
- Regenerate: `python3 /workspace/softrank/tools/make_firefox_themes.py`
