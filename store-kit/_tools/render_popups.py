# Render real extension popups headlessly (with a chrome.storage shim seeded with demo data)
import json, shutil, subprocess, os, datetime as dt
REPO='/workspace/softrank/GoPromotional'
OUT='/tmp/render'
today=dt.date.today()
def d(i): return (today-dt.timedelta(days=i)).isoformat()
def d_future(i): return (today+dt.timedelta(days=i)).isoformat()
states={
 'homepage':[
   {'event_snapshot_v1':{'eventName':'Product launch meetup','eventDate':d_future(14),'eventTime':'18:30','venue':'Harbour Studio, Bristol','notes':'Welcome drinks 18:00\nTalks 18:45–20:00\nNetworking afterwards','tasks':[
     {'title':'Confirm venue','priority':'high','done':True},
     {'title':'Send invitations','priority':'high','done':True},
     {'title':'Lock headcount','priority':'normal','done':False},
     {'title':'Prepare agenda','priority':'normal','done':False},
     {'title':'Check AV / equipment','priority':'normal','done':False},
     {'title':'Plan guest arrival flow','priority':'normal','done':False},
   ]}},
   {'event_snapshot_v1':{'eventName':'Team offsite','eventDate':d_future(3),'eventTime':'09:00','venue':'Lakeside Hub','notes':'Bring printed name badges','tasks':[
     {'title':'Confirm venue','priority':'high','done':True},
     {'title':'Send invitations','priority':'high','done':True},
     {'title':'Lock headcount','priority':'normal','done':True},
     {'title':'Prepare agenda','priority':'normal','done':True},
     {'title':'Check AV / equipment','priority':'normal','done':False},
     {'title':'Order catering','priority':'high','done':False},
   ]}},
 ],
 'water-bottles':[
   {'sip_quick_v1':{'goal':2000,'days':{d(0):1330,d(1):2150,d(2):1800,d(3):2400,d(4):1650,d(5):2000,d(6):900},'history':{d(0):[250,500,330,250]}}},
   {'sip_quick_v1':{'goal':2500,'days':{d(0):2580,d(1):2500,d(2):2100,d(3):2650,d(4):1900,d(5):2300,d(6):2500},'history':{d(0):[500,500,330,250,500,500]}}},
 ],
 'tote-bags':[
   {'pack_quick_v2':{'activeId':'trade','lists':[
     {'id':'trade','name':'Trade show stand','items':[{'title':'Banner stand','done':True},{'title':'Tote bags for giveaways','done':True},{'title':'Business cards','done':True},{'title':'Laptop + charger','done':False},{'title':'Extension lead','done':False},{'title':'Visitor sign-up sheet','done':False},{'title':'Water bottle','done':False}]},
     {'id':'travel','name':'Weekend trip','items':[]}]}},
   {'pack_quick_v2':{'activeId':'travel','lists':[
     {'id':'trade','name':'Trade show stand','items':[]},
     {'id':'travel','name':'Weekend trip','items':[{'title':t,'done':i<3} for i,t in enumerate(['Passport / ID','Tickets','Phone charger','Toothbrush','Medications','Clothes','Headphones','Snacks'])]}]}},
 ],
 'travel-mugs':[
   {'mug_tap_v2':{'logs':[{'reusable':r,'day':d(i),'at':0,'note':n} for i,r,n in [(5,True,'coffee'),(4,True,'tea'),(4,False,''),(3,True,'flat white'),(2,True,'coffee'),(1,True,'tea'),(1,False,'station kiosk'),(0,True,'coffee'),(0,True,'')]]}},
   {'mug_tap_v2':{'logs':[{'reusable':True,'day':d(i),'at':0,'note':n} for i,n in [(6,'coffee'),(5,'tea'),(4,'coffee'),(3,'latte'),(2,'coffee'),(1,'tea'),(0,'coffee')]]}},
 ],
}
for g,sts in states.items():
  for k,st in enumerate(sts,1):
    src=f'{REPO}/{g}/edge-extension'; dst=f'{OUT}/{g}-{k}'
    shutil.rmtree(dst,ignore_errors=True); shutil.copytree(src,dst)
    shim='const __S=%s;window.chrome=window.chrome||{};chrome.storage={local:{get:async k=>({[k]:__S[k]}),set:async o=>{Object.assign(__S,o)}}};'%json.dumps(st)
    open(f'{dst}/shim.js','w').write(shim)
    h=open(f'{dst}/popup.html').read().replace('<script src="popup.js"></script>','<script src="shim.js"></script>\n  <script src="popup.js"></script>')
    h=h.replace('</head>','<style>html,body{max-height:none!important;overflow:visible!important}</style></head>')
    open(f'{dst}/popup.html','w').write(h)
    png=f'{OUT}/{g}-{k}.png'
    subprocess.run(['google-chrome','--headless=new','--no-sandbox','--disable-gpu','--hide-scrollbars','--force-device-scale-factor=2',
      '--window-size=340,1200','--virtual-time-budget=2000',f'--screenshot={png}',f'file://{dst}/popup.html'],check=True,capture_output=True)
    print(png)
