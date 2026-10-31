#!/usr/bin/env python3
"""Derive new cloth patches from original Flan atlas emblems (not original standalone items)."""
from pathlib import Path
import hashlib,json
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/metroport'
SPECS=[
 ('satanists','satanarmor',(1272,610,1604,942),'Нашивка сатанистов','Satanist patch'),
 ('contrabandists','kontrabody',(10,99,91,201),'Нашивка контрабандистов','Contrabandist patch'),
 ('legion','legionarmor',(44,212,103,259),'Нашивка «Легион»','Legion patch'),
 ('mgu','nvgdbody',(277,330,426,502),'Нашивка МГУ','MSU patch'),
]
def main():
 records=[]
 for key,source,bounds,ru,en in SPECS:
  path=ASSETS/('textures/legacy/'+source+'.png');original=path.read_bytes()
  emblem=Image.open(path).convert('RGBA').crop(bounds)
  if key=='satanists':
   mask=Image.new('L',emblem.size);ImageDraw.Draw(mask).ellipse((1,1,emblem.width-2,emblem.height-2),fill=255)
   from PIL import ImageChops
   emblem.putalpha(ImageChops.multiply(emblem.getchannel('A'),mask))
  emblem.thumbnail((92,92),Image.Resampling.LANCZOS)
  out=Image.new('RGBA',(128,128));draw=ImageDraw.Draw(out)
  draw.rounded_rectangle((5,5,122,122),radius=12,fill=(134,128,108,255) if key=='legion' else (37,40,34,255),outline=(15,17,14,255),width=3)
  for x in range(16,113,9):
   for y in (11,116):draw.line((x,y,x+4,y),fill=(125,119,95,255),width=1)
  for y in range(16,113,9):
   for x in (11,116):draw.line((x,y,x,y+4),fill=(125,119,95,255),width=1)
  out.alpha_composite(emblem,((128-emblem.width)//2,(128-emblem.height)//2))
  target=ASSETS/('textures/patches/'+key+'.png');target.parent.mkdir(exist_ok=True);out.save(target)
  item='patch_'+key
  (ASSETS/('models/item/'+item+'.json')).write_text(json.dumps({'parent':'item/generated','textures':{'layer0':'metroport:patches/'+key}},indent=2)+'\n')
  records.append({'id':item,'faction':key,'nameRu':ru,'nameEn':en,'sourceAtlas':'textures/legacy/'+source+'.png','sourceBounds':bounds,'sourceSha256':hashlib.sha256(original).hexdigest(),'patchSha256':hashlib.sha256(target.read_bytes()).hexdigest()})
 report={'provenance':'New cosmetic patch items derived from emblems embedded in the original Flan armor atlases. Cloth backing/stitching added by this port. No faction permissions or membership invented.','patches':records}
 (ASSETS/'faction_patches.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
 from mcmod_languages import update
 update()
if __name__=='__main__':main()
