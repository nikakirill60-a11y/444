from pathlib import Path
import os
os.chdir(Path(__file__).resolve().parents[1])
import re,zipfile,json
root=Path('tools/legacy-reference/ru/metro/models'); dest=Path('src/main/java/ru/metrocraft/port/client/mobs');dest.mkdir(parents=True,exist_ok=True)
mappings={'field_78089_u':'textureHeight','field_78090_t':'textureWidth','field_78795_f':'rotateAngleX','field_78796_g':'rotateAngleY','field_78808_h':'rotateAngleZ','field_78809_i':'mirror','func_78087_a':'setRotationAngles','func_78088_a':'render','func_78785_a':'render','func_78787_b':'setTextureSize','func_78789_a':'addBox','func_78793_a':'setRotationPoint'}
models=['Jery','Komp','Kris','ModelGrib','Murzik','Trup1','Trup2','Trup3','Trup4','Tuzic']
for name in models:
 text=(root/(name+'.java')).read_text().replace('package ru.metro.models;', 'package ru.metrocraft.port.client.mobs;')
 for a,b in mappings.items():text=text.replace(a,b)
 (dest/(name+'.java')).write_text(text)
assets=Path('src/main/resources/assets/metroport');z=zipfile.ZipFile('metrocraft.jar');paths={n.lower():n for n in z.namelist()}; names=['jery','komp','kris','grib','murzik','trup1','trup2','trup3','trup4','tuzic']
for name,model in zip(names,models):
 renderer=Path('tools/legacy-reference/ru/metro/mod')/('Render'+('Grib' if name=='grib' else model)+'Entity.java')
 src=re.search(r'new ResourceLocation\("([^"]+)"',renderer.read_text()).group(1).replace('Metrocraft:','assets/metrocraft/').lower()
 target=assets/('textures/entity/'+name+'.png');target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(z.read(paths[src]))
print('Imported 10 original entity models and textures')
entity_dest=Path('src/main/java/ru/metrocraft/port/entity');entity_dest.mkdir(parents=True,exist_ok=True)
entity_map={'func_70105_a':'setSize','func_110147_ax':'applyEntityAttributes','func_110148_a':'getEntityAttribute','func_111128_a':'setBaseValue',
            'field_111267_a':'MAX_HEALTH','field_111265_b':'FOLLOW_RANGE','field_111266_c':'KNOCKBACK_RESISTANCE','field_111263_d':'MOVEMENT_SPEED','field_111264_e':'ATTACK_DAMAGE'}
for name in ['Kris','Murzik','Tuzic','Jery','Trup1','Trup2','Trup3','Trup4','Komp','Grib']:
    text=(Path('tools/legacy-reference/ru/metro/mod')/(name+'Entity.java')).read_text().replace('package ru.metro.mod;', 'package ru.metrocraft.port.entity;')
    for a,b in entity_map.items():text=text.replace(a,b)
    (entity_dest/(name+'Entity.java')).write_text(text)
