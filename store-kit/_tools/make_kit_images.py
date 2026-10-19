# Build store-kit images from real popup renders + existing branding art (PIL only)
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import os
REPO='/workspace/softrank/GoPromotional'
R='/tmp/render'
INTER='/usr/share/fonts/truetype/sand-box/google/Inter/Inter-VariableFont_opsz,wght.ttf'
def font(sz,w=700):
    f=ImageFont.truetype(INTER,sz)
    try: f.set_variation_by_axes([14 if sz<20 else 32, w])
    except Exception:
        try: f.set_variation_by_axes([w])
        except Exception: pass
    return f
def hexrgb(h): h=h.lstrip('#'); return tuple(int(h[i:i+2],16) for i in (0,2,4))
def mix(a,b,t): return tuple(round(a[i]*(1-t)+b[i]*t) for i in range(3))
X={
 'water-bottles':dict(slug='sip-quick',name='Sip Quick',accent='#0E7490',
   tag='Water intake tracker for your toolbar',
   shots=[('Log a glass in one click',['Quick +150 / +250 / +330 / +500 ml buttons','Custom amounts and one-step undo','Progress bar towards your daily goal']),
          ('Review the last 7 days',['Daily totals for the past week','Set your own daily goal','Copy a short stats summary'])]),
 'tote-bags':dict(slug='pack-quick',name='Pack Quick',accent='#92400E',
   tag='Packing lists in your toolbar',
   shots=[('Keep a list for every bag',['Multiple named lists','Tick items off as you pack','Filter long lists instantly']),
          ('Start from a template',['Work day and Travel starter items','Clear packed items in one click','Copy a list to share it'])]),
 'travel-mugs':dict(slug='mug-tap',name='Mug Tap',accent='#7C2D12',
   tag='Reusable cup habit tracker',
   shots=[('Count reusable vs disposable cups',['One tap per drink, optional note','Weekly reusable rate','Undo the last entry']),
          ('Build a streak',['Daily streak of reusable-cup days','Recent drinks log','Copy a weekly summary'])]),
}
made=[]
for g,c in X.items():
    out=f'{REPO}/store-kit/{g}/images'; os.makedirs(out,exist_ok=True)
    acc=hexrgb(c['accent']); light=mix(acc,(255,255,255),0.90); lighter=mix(acc,(255,255,255),0.96)
    icon=Image.open(f'{REPO}/{g}/branding/extension/icon-512.png').convert('RGBA')
    for k,(head,bullets) in enumerate(c['shots'],1):
        W,H=1280,800
        im=Image.new('RGB',(W,H),lighter); d=ImageDraw.Draw(im)
        # soft diagonal band
        for y in range(H):
            d.line([(0,y),(W,y)],fill=mix(lighter,light,y/H))
        ic=icon.resize((96,96),Image.LANCZOS); im.paste(ic,(90,120),ic)
        d.text((206,128),c['name'],font=font(44,750),fill=(28,36,48))
        d.text((208,184),c['tag'],font=font(20,500),fill=(95,107,122))
        d.text((90,280),head,font=font(34,700),fill=acc)
        y=350
        for b in bullets:
            d.ellipse((92,y+9,104,y+21),fill=acc)
            d.text((120,y),b,font=font(22,500),fill=(40,48,60)); y+=48
        d.text((90,700),'Runs locally · no account · no tracking',font=font(18,500),fill=(95,107,122))
        # popup render, scaled to fit
        pop=Image.open(f'{R}/{g}-{k}-crop.png').convert('RGB')
        maxh=720; s=min(maxh/pop.height, 400/pop.width); pw,ph=round(pop.width*s),round(pop.height*s)
        pop=pop.resize((pw,ph),Image.LANCZOS)
        px,py=W-90-pw,(H-ph)//2
        sh=Image.new('RGBA',(pw+60,ph+60),(0,0,0,0)); ImageDraw.Draw(sh).rounded_rectangle((30,34,30+pw,34+ph),12,fill=(0,0,0,70))
        sh=sh.filter(ImageFilter.GaussianBlur(12)); im.paste(sh,(px-30,py-30),sh)
        mask=Image.new('L',(pw,ph),0); ImageDraw.Draw(mask).rounded_rectangle((0,0,pw-1,ph-1),10,fill=255)
        im.paste(pop,(px,py),mask)
        p=f'{out}/{c["slug"]}-screenshot-0{k}-1280x800.png'; im.save(p,optimize=True); made.append(p)
    # 440x280 small promo tile
    W,H=440,280; im=Image.new('RGB',(W,H),acc); d=ImageDraw.Draw(im)
    for y in range(H): d.line([(0,y),(W,y)],fill=mix(acc,mix(acc,(0,0,0),0.25),y/H))
    ic=icon.resize((120,120),Image.LANCZOS); im.paste(ic,((W-120)//2,46),ic)
    f=font(34,750); tw=d.textlength(c['name'],font=f); d.text(((W-tw)/2,182),c['name'],font=f,fill='white')
    f2=font(15,500); tw=d.textlength(c['tag'],font=f2); d.text(((W-tw)/2,230),c['tag'],font=f2,fill=(235,240,245))
    p=f'{out}/{c["slug"]}-promo-440x280.png'; im.save(p,optimize=True); made.append(p)
    # 300x300 logo (transparent corners) + 128 icon copy
    for sz in (300,128):
        p=f'{out}/{c["slug"]}-logo-{sz}x{sz}.png'; icon.resize((sz,sz),Image.LANCZOS).save(p,optimize=True); made.append(p)
for p in made: print(Image.open(p).size, Image.open(p).mode, p)
