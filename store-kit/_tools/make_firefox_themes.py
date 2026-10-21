# Convert each Chrome theme (colours + NTP art) into an optional Firefox static theme
import json, os
from PIL import Image
REPO='/workspace/softrank/GoPromotional'
T={'homepage':('harbor-dusk',0.52),'water-bottles':('alpine-mist',0.36),'tote-bags':('canvas-trail',0.42),'travel-mugs':('ember-steam',0.40)}
def rgb(c): return f'rgb({c[0]}, {c[1]}, {c[2]})'
for g,(slug,cy) in T.items():
    cm=json.load(open(f'{REPO}/{g}/chrome-theme/manifest.json')); c=cm['theme']['colors']
    out=f'{REPO}/{g}/firefox-theme'; os.makedirs(f'{out}/images',exist_ok=True)
    ntp=Image.open(f'{REPO}/{g}/chrome-theme/images/theme_ntp_background.png').convert('RGB')
    W,H=1920,200; y0=max(0,min(ntp.height-H,round(ntp.height*cy-H/2)))
    band=ntp.crop((0,y0,W,y0+H)).convert('RGBA')
    # fade the left side into the frame colour so tab text keeps the Chrome theme contrast
    frame=Image.new('RGBA',(W,H),tuple(c['frame'])+(255,))
    mask=Image.new('L',(W,H)); px=mask.load()
    for x in range(W):
        t=0 if x<0.40*W else (1 if x>0.80*W else (x-0.40*W)/(0.40*W))
        for y in range(H): px[x,y]=round(255*t)
    hdr=Image.composite(band,frame,mask).convert('RGB')
    hdr.save(f'{out}/images/header.png',optimize=True)
    m={
      "manifest_version":2,
      "name":cm['name'],
      "version":cm['version'],
      "description":cm['description'],
      "browser_specific_settings":{"gecko":{"id":f"{slug}-theme@gopromotional.co.uk","strict_min_version":"140.0",
          "data_collection_permissions":{"required":["none"]}},
        "gecko_android":{"strict_min_version":"142.0"}},
      "theme":{
        "images":{"theme_frame":"images/header.png"},
        "colors":{
          "frame":rgb(c['frame']),"frame_inactive":rgb(c['frame_inactive']),
          "tab_background_text":rgb(c['tab_background_text']),"tab_text":rgb(c['tab_text']),
          "tab_selected":rgb(c['toolbar']),"tab_line":rgb(c['bookmark_text']),"tab_loading":rgb(c['bookmark_text']),
          "toolbar":rgb(c['toolbar']),"toolbar_text":rgb(c['tab_text']),"icons":rgb(c['tab_text']),
          "bookmark_text":rgb(c['bookmark_text']),
          "toolbar_field":rgb(c['omnibox_background']),"toolbar_field_text":rgb(c['omnibox_text']),
          "toolbar_field_border":rgb(c['frame']),
          "toolbar_field_focus":rgb(c['omnibox_background']),"toolbar_field_text_focus":rgb(c['omnibox_text']),
          "popup":rgb(c['toolbar']),"popup_text":rgb(c['tab_text']),
          "ntp_background":rgb(c['ntp_background']),"ntp_text":rgb(c['ntp_text'])
        }
      }
    }
    # theme_frame is aligned right-top by default in Firefox, so no properties block is needed
    open(f'{out}/manifest.json','w').write(json.dumps(m,indent=2)+'\n')
    open(f'{out}/README.md','w').write(f"""# {cm['name']} — Firefox theme (optional)

Static Firefox theme generated from `../chrome-theme/manifest.json` colours and a 1920×200 header band cut from
`../chrome-theme/images/theme_ntp_background.png`. The left 40% of the header fades into the Chrome `frame` colour so tab
text keeps the same contrast as the Chrome theme. Firefox themes cannot set a new-tab background image, so only
`ntp_background`/`ntp_text` colours carry over.

- gecko id: `{slug}-theme@gopromotional.co.uk`
- Build: `cd {g}/firefox-theme && rm -f firefox-theme.zip && zip -r -X firefox-theme.zip manifest.json images`
- Test: `about:debugging#/runtime/this-firefox` → **Load Temporary Add-on…** → `manifest.json`
- Regenerate: `python3 /workspace/softrank/tools/make_firefox_themes.py`
""")
    print(out, y0)
