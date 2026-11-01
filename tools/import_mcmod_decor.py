#!/usr/bin/env python3
"""All registered MCMOD decorative objects. Runtime OBJ/PNG stay byte-exact originals."""
from pathlib import Path
import hashlib,json,re,shutil
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/metroport'
SOURCE=ROOT/'tools/mcmod-reference'
# Verified individual Drive downloads, OBJ then PNG. The completion manifest adds the remaining 39.
FILES={
 'barrel_01':('1sgseiMrCMoDFK6S8dtPofAmX87oX6YsM','1QyFkdQi2zWII_aSVgz9CLbvPd4Pr9zUv','Fire barrel'),
 'barrel_01_close':('1LbpH9Al1sbleLl-dCGJ3sDFTT05Ty4pv','179CQ-8-Y5D5hXA9EukCW2Gl_jRHCtnfL','Closed barrel'),
 'barrel_03_garbage':('1iQbfEFL25cSnnBVCJqMqC8yyPhWve67i','1AjMjatMAxDkQwCvTVrKkNyCznV4Ngvz-','Garbage barrel'),
 'barrel_04_broken':('11Sv2QKN_PZQfIwiHcM4M8sn5Yu2yEzNd','14ky5bCg5F05UHZyVLF8QsEE710PXdr5i','Dented barrel'),
 'bench_2':('14AyIIdxz1syXGmh6P9eeV_Re3A6W--et','1tS88nObBaL4TNoQKjN0n212W8Gm1az8H','Bench'),
 'bench_6':('1f3qYYzvjpjV4mmJYsEmZYBh-Q6yBsaH4','1EJ8P_ndGTSYZo3psaWyO1SA3s49xHL6v','Broken bench'),
 'cafe_chair':('1MmUeqkcZdOhZOdLojVv9AGb5BYu8OWsM','1WYC28Tb7ry1-1tWvR_jvzEYs6lZWswPs','Bar stool'),
 'chair':('1b0EnjTPlL2PqPN4NqXbIVuuOdyJW4nup','1Bxtmc2K6g49P6H2zjX8RwgSQ33KPXWM4','Kitchen chair'),
 'chair_01':('17W6_a9O58QsQE8VsyDek_-3CMw1oRlhb','1qkpMDLgZCc19ggADiazIqJEoG3hRojFx','Chair'),
 'chair_cabinet':('1VYDtjlXUsmOe3Yj05pCuEyZkkfV4dshz','1Y2_SxxcE8HsBuut-4fnDbjhD5mET2Ugt','Office chair'),
 'table':('1hQQq3MXvTjPyStvKNn6Y3_BRQCoqM2Rb','1tn9pm5Xz1mHl-NllfHETBAkysHdIuVIz','Table'),
 'table_benches':('12ip76N16sxBWR7zHUmR0yYsPoqDBMCEy','1kI8EiVegjM4E7iEnMz1iSumZ5zR1q9T7','Table and benches'),
 'table_cafe01':('1iGRlQYUBlQTBQw8S3Ydt3lkVpVorYKRk','1grgEFq1k7E7wzJp3_I_zXpWJLQ4Gdd3J','Cafe table'),
 'table_cafe_tkan':('1EUqNeE4lc3Z4EDT1w6dQhJy02Kh9be_3','1FJWd7F54qFWZnmmzw8_vBp7J-ERTi6jS','Table with tablecloth'),
 'plastic_table_01':('1vXhvCCwMa1aXN6hPHIN0ugQwjzqbZsC_','1d4WkpAyF7-uhcACko8r41E33PQ6ahlCN','Plastic table'),
 'table_nastil_1':('14d-NBMWZ7yc5QfJtvDczQXYXN7DUfvAN','1Nh_-eyBB_wg6nnsZJ1s_Aj0koa9T68fy','Plank table'),
 'table_selfmade':('1zr6_Ue3Rcos7JjS-I1deavpbtjj3zll6','12inU121jAliZVHTQvdxrp0Hm0dVhXw7P','Makeshift table'),
 'divan':('16r6wrXJfQGcDxPm9eNJ25BHYDaZkkIBV','1hmIq9EgMc0CeiBJ6zyzsiVqcrlvsQNJj','Sofa'),
 'bag_line':('1mhJ_nF0vNnha1e7vgRwTbwzP3twUjRZQ','1qv00XbSvuE0D2Ze3ghSwTXpNbB-rpqYe','Sandbags — long row'),
 'bag_line_short':('11Oqxv1zsY1oHVSDqxx0qQKtmd82zk69t','1szFbFyISnF3P5b0qMW8Cu1A6nkHFu4wy','Sandbags — short row'),
}
FILES.update(json.loads((ROOT/'tools/mcmod-decor-completion.json').read_text()))
def write(path,data):
 path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
def main():
 text=(SOURCE/'java/metro/common/regstration/RegisterBlocks.java').read_text()
 registered={m[0]:{'model':m[1],'texture':m[2],'scale':float(m[3])} for m in re.findall(r'(\w+) = new HudrantBlock\(.*?"([^"]+)", "([^"]+)", ([\d.]+)f\)',text)}
 names={k:json.loads('"'+v+'"') for k,v in re.findall(r'LanguageRegistry.addName\(\(Object\)(\w+), \(String\)"([^"\n]+)"\)',text)}
 manifest=json.loads((SOURCE/'source-files.json').read_text());entries=[]
 for key,(objid,pngid,en) in FILES.items():
  info=registered[key];assert info['model']==key and info['texture']==key
  hashes={}
  for ext,id in [('obj',objid),('png',pngid)]:
   rel='assets/decor/'+key+'.'+ext;data=(SOURCE/rel).read_bytes();sha=hashlib.sha256(data).hexdigest()
   manifest['files'][rel]={'driveId':id,'bytes':len(data),'sha256':sha};hashes[ext]=sha
   target=ASSETS/(('models/mcmod/decor/' if ext=='obj' else 'textures/mcmod/decor/')+key+'.'+ext);target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(data)
  vertices=[list(map(float,l.split()[1:4])) for l in (SOURCE/('assets/decor/'+key+'.obj')).read_text(errors='replace').splitlines() if l.startswith('v ')]
  bounds=[min(v[i] for v in vertices) for i in range(3)]+[max(v[i] for v in vertices) for i in range(3)]
  entry={'id':'mcmod_decor_'+key,'kind':'decor','sourceId':key,'nameRu':names[key],'nameEn':en,'scale':info['scale'],'bounds':bounds,'sha256':hashes}
  entries.append(entry)
  # Small shared vanilla particle sprite; original large PNG is bound directly by the 3D renderer.
  block='mcmod_decor/'+key
  write(ASSETS/('models/block/'+block+'.json'),{'parent':'block/block','textures':{'particle':'minecraft:blocks/iron_block'},'elements':[]})
  display={}
  for view,rot,scale in [('gui',[30,225,0],1),('ground',[0,0,0],.5),('fixed',[0,180,0],.7),('firstperson_righthand',[0,45,0],.6),('firstperson_lefthand',[0,225,0],.6),('thirdperson_righthand',[75,45,0],.6),('thirdperson_lefthand',[75,225,0],.6)]:
   display[view]={'rotation':rot,'translation':[0,0,0],'scale':[scale]*3}
  write(ASSETS/('models/item/'+entry['id']+'.json'),{'parent':'metroport:block/'+block,'display':display})
  write(ASSETS/('blockstates/'+entry['id']+'.json'),{'variants':{'facing='+f:{'model':'metroport:'+block} for f in ['north','east','south','west']}})
 write(ASSETS/'mcmod_decor.json',entries)
 write(ROOT/'docs/mcmod-decor-report.json',{'batch':2,'registeredDecor':registered,'imported':[e['sourceId'] for e in entries],'pending':sorted(set(registered)-set(FILES)),
  'technicalBlocksNotDecor':['collision','rad_block','air_block','reg_block'],
  'provenance':'Original HudrantBlock registrations, original per-block scale, recovered RenderTileEntityHudrant rotations [270,180,90,0]. Runtime OBJ/PNG byte-exact; old classes are references only.',
  'adaptations':['Four orientations; shared TESR and normalized 3D item views instead of 59 fixed renderer bindings.','Preserve original unbreakable map-decor behavior; no container, seating, fire, vehicle or door mechanics invented.','Collision is the rotated model bounding box clipped to the installation cell. Protruding furniture does not reserve/edit neighbouring cells.','Shared iron-block break particles avoid putting 59 large atlases into the item texture atlas. Actual visible geometry uses each original texture.']})
 write(SOURCE/'source-files.json',manifest)
 from mcmod_languages import update
 update()
 print('Imported',len(entries),'of',len(registered),'registered MCMOD decorations')
if __name__=='__main__':main()
