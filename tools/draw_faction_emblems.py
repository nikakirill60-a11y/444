"""New port-drawn game faction badges; deliberately not labelled as recovered original emblems."""
import math
from PIL import Image,ImageDraw
FACTIONS=[
 ('bandits','Нашивка бандитов','Bandit patch'),
 ('reich','Нашивка Рейха','Reich patch'),
 ('worm_cult','Нашивка червепоклонников','Worm Cult patch'),
 ('red_line','Нашивка Красной линии','Red Line patch'),
 ('polis','Нашивка Полиса','Polis patch'),
 ('stalkers','Нашивка сталкеров','Stalker patch'),
 ('hansa','Нашивка Ганзы','Hansa patch'),
]
def badge(faction):
 im=Image.new('RGBA',(512,512));d=ImageDraw.Draw(im)
 def xy(points):return [(int(x*4),int(y*4)) for x,y in points]
 def line(points,fill,width=3):d.line(xy(points),fill=fill,width=width*4,joint='curve')
 def ellipse(box,fill=None,outline=None,width=2):d.ellipse(tuple(int(x*4) for x in box),fill=fill,outline=outline,width=width*4)
 def rect(box,fill,outline=None,width=2):d.rectangle(tuple(int(x*4) for x in box),fill=fill,outline=outline,width=width*4)
 def poly(points,fill):d.polygon(xy(points),fill=fill)
 d.rounded_rectangle((20,20,488,488),radius=48,fill='#252822',outline='#0f110e',width=12)
 for x in range(16,113,9):
  for y in [11,116]:line([(x,y),(x+4,y)],'#7d775f',1)
 for y in range(16,113,9):
  for x in [11,116]:line([(x,y),(x,y+4)],'#7d775f',1)
 ivory='#e5dfca';red='#bf2824';yellow='#e1ae40'
 if faction=='bandits':
  for sign in [-1,1]:
   line([(64+sign*36,96),(64-sign*30,35)],ivory,5)
   line([(64+sign*36,96),(64+sign*30,88)],'#897d65',7)
  ellipse((37,27,91,79),ivory);rect((49,66,79,88),ivory)
  ellipse((44,47,58,61),'#171a16');ellipse((70,47,84,61),'#171a16')
  poly([(64,59),(59,70),(69,70)],'#171a16')
  for x in [54,61,68,75]:line([(x,78),(x,88)],'#252822',2)
 elif faction=='reich':
  # Fictional Metro-style angular three-pronged insignia, not a real-world party flag.
  poly([(64,20),(91,38),(91,82),(64,105),(37,82),(37,38)],ivory)
  poly([(64,29),(82,42),(82,77),(64,94),(46,77),(46,42)],'#252822')
  for x in [54,64,74]:rect((x-2,43,x+2,72),red)
  poly([(48,57),(64,78),(80,57),(80,68),(64,88),(48,68)],red)
 elif faction=='worm_cult':
  ellipse((24,24,104,104),outline='#918550',width=3)
  for shift in [-19,0,19]:
   points=[(64+shift+6*math.sin(t*.25),32+t) for t in range(61)]
   line(points,'#c0b07b',5);ellipse((59+shift,28,69+shift,40),'#c0b07b')
 elif faction=='red_line':
  points=[]
  for i in range(10):
   angle=-math.pi/2+i*math.pi/5;r=44 if i%2==0 else 19;points.append((64+r*math.cos(angle),64+r*math.sin(angle)))
  poly(points,red);line([(51,79),(76,51)],yellow,5)
  line([(49,54),(58,45),(67,55)],yellow,8)
  points=[(66+17*math.cos(t),59+21*math.sin(t)) for t in [i*.09 for i in range(38)]]
  line(points,yellow,4)
 elif faction=='polis':
  poly([(25,44),(64,22),(103,44)],'#99bbce');rect((27,47,101,52),'#99bbce')
  for x in [36,53,70,87]:rect((x,56,x+6,90),ivory)
  rect((25,94,103,100),'#99bbce');line([(24,103),(104,103)],ivory,2)
 elif faction=='stalkers':
  ellipse((25,25,103,103),outline='#a8a27a',width=3)
  poly([(40,36),(88,36),(94,67),(80,91),(48,91),(34,67)],'#adb09b')
  for x in [42,70]:ellipse((x,44,x+17,62),'#162323',outline=ivory,width=2)
  ellipse((53,66,75,87),'#303b30',outline=ivory,width=2)
  for x in [33,79]:rect((x,68,x+16,84),'#73795c',ivory,2)
 elif faction=='hansa':
  points=[]
  for i in range(48):
   t=i*math.pi/24;r=44 if i%4 in [0,3] else 39;points.append((64+r*math.cos(t),64+r*math.sin(t)))
  poly(points,'#bba762');ellipse((31,31,97,97),'#252822',ivory,2)
  # Ring stations + a connected trade route, a clearly new port-drawn emblem.
  for x,y in [(64,38),(87,64),(64,90),(41,64)]:ellipse((x-4,y-4,x+4,y+4),ivory)
  line([(41,64),(64,38),(87,64),(64,90),(41,64)],'#bba762',2)
  rect((54,49,58,79),ivory);rect((70,49,74,79),ivory);rect((55,61,73,67),ivory)
 return im.resize((128,128),Image.Resampling.LANCZOS)
