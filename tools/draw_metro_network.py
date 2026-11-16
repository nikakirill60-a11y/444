#!/usr/bin/env python3
"""Companion diagram, not an in-game screenshot. No external assets."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont


def draw(path):
    im = Image.new('RGB', (1440, 1080), '#101a22')
    d = ImageDraw.Draw(im)
    font_path = '/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf'
    bold_path = '/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf'
    def text(xy, value, size=22, color='#dce6e9', bold=False, anchor=None):
        d.text(xy, value, font=ImageFont.truetype(bold_path if bold else font_path, size), fill=color, anchor=anchor)
    text((64, 38), 'MinETRO', 22, '#e6bc70', True)
    text((64, 82), 'КОЛЬЦЕВАЯ ЛИНИЯ', 44, bold=True)
    text((64, 147), '4 станции  /  2 непрерывных пути  /  Minecraft 1.12.2', 22, '#8fa4b0')
    d.line((64, 198, 1376, 198), fill='#31424e', width=2)
    cx, cz, scale = 468, 610, 3.1
    def pt(x, z):
        return round(cx+x*scale), round(cz+z*scale)
    for k in range(-96, 97, 16):
        d.line((*pt(k, -96), *pt(k, 96)), fill='#1b2b35')
        d.line((*pt(-96, k), *pt(96, k)), fill='#1b2b35')
    d.rectangle((*pt(-71,-71), *pt(71,71)), fill='#263744')
    d.rectangle((*pt(-57,-57), *pt(57,57)), fill='#101a22')
    for radius in [60, 68]:
        d.rectangle((*pt(-radius,-radius), *pt(radius,radius)), outline='#d6bd88', width=4)
    d.rectangle((*pt(-64,-64), *pt(64,64)), outline='#54899b', width=2)
    stations = [('СЕВЕРНАЯ',0,-64,'#6da4f2',0), ('ВОСТОЧНАЯ',64,0,'#76bc9b',1),
                ('ЮЖНАЯ',0,64,'#e77b73',2), ('ЗАПАДНАЯ',-64,0,'#e4c76b',3)]
    for name, x,z,color,rot in stations:
        dx, dz = (24,11) if rot%2==0 else (11,24)
        d.rounded_rectangle((*pt(x-dx,z-dz), *pt(x+dx,z+dz)), radius=9, fill='#344653', outline=color, width=3)
        if rot%2==0:
            for side in [-4,4]: d.line((*pt(x-24,z+side), *pt(x+24,z+side)), fill='#d6bd88', width=4)
            d.rectangle((*pt(x-22,z-2),*pt(x+22,z+2)),fill=color)
        else:
            for side in [-4,4]: d.line((*pt(x+side,z-24), *pt(x+side,z+24)), fill='#d6bd88', width=4)
            d.rectangle((*pt(x-2,z-22),*pt(x+2,z+22)),fill=color)
        # Outside service-room footprint rotated from station-local geometry.
        corners=[]
        for lx,lz in [(8,-23),(22,-23),(22,-11),(8,-11)]:
            rx,rz=[(lx,lz),(-lz,lx),(-lx,-lz),(lz,-lx)][rot]
            corners.append(pt(x+rx,z+rz))
        d.polygon(corners,fill='#263f4c',outline=color)
    text(pt(0,-103), 'СЕВЕРНАЯ', 24, '#6da4f2', True, 'mm')
    text(pt(0,95), 'ЮЖНАЯ', 24, '#e77b73', True, 'mm')
    text(pt(-64,0), 'З', 22, '#101a22', True, 'mm')
    text(pt(64,0), 'В', 22, '#101a22', True, 'mm')
    text(pt(0,-5), 'КОЛЬЦО', 28, '#dce6e9', True, 'mm')
    text(pt(0,8), '1 024 рельсовых блока', 18, '#8fa4b0', anchor='mm')
    ex,ez=pt(16,-84)
    d.ellipse((ex-7,ez-7,ex+7,ez+7),fill='#e6bc70')
    text((835, 260), 'ЧТО ВНУТРИ', 24, '#e6bc70', True)
    lines=[('01','Четыре станции','Платформы длиной 49 блоков'),
           ('02','Два кольцевых пути','Энергорельсы и питание под путями'),
           ('03','Пешеходные мосты','Над рельсами на каждой станции'),
           ('04','Служебные комнаты','Мастерская, пост, склад, щитовая'),
           ('05','Наземный вестибюль','Спуск по лестнице на Северную')]
    for i,(num,title,desc) in enumerate(lines):
        y=324+i*96
        text((835,y),num,20,'#637f90',True)
        text((884,y-2),title,22,bold=True)
        text((884,y+33),desc,16,'#8fa4b0')
    text((835,844),'РАЗМЕЩЕНИЕ В МОДЕ',20,'#e6bc70',True)
    text((835,883),'/metronetwork plan',23,bold=True)
    text((835,924),'Затем: build <код из чата>',19,'#8fa4b0')
    d.line((64,982,1376,982),fill='#31424e',width=2)
    text((64,1008),'СХЕМА • НЕ ИГРОВОЙ СКРИНШОТ',16,'#637f90',True)
    text((1376,1008),'Ванильные блоки • без MWF и WorldEdit в моде',16,'#8fa4b0',anchor='ra')
    Path(path).parent.mkdir(parents=True, exist_ok=True)
    im.save(path, optimize=True)


if __name__ == '__main__':
    import sys
    draw(sys.argv[1] if len(sys.argv)>1 else 'downloads/MinETRO_metro_network.png')
