#!/usr/bin/env python3
"""Selected non-weapon audio, mask/NVG textures and three medical material icons."""
import hashlib
import json
from pathlib import Path
import zipfile
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'
SOUNDS={
 'metromod_flask':'items/flyashka', 'metromod_medkit':'items/med',
 'metromod_filter':'mask/change_filter.vba',
 'metromod_watch_alarm_1':'watch/watch_timer_alarm_1.vba',
 'metromod_watch_alarm_2':'watch/watch_timer_alarm_2.vba',
 'metromod_watch_alarm_3':'watch/watch_timer_alarm_3.vba',
 'metromod_gate_open':'germo/right_open','metromod_gate_close':'germo/right_close',
}
TEXTURES={
 'mask_clean':'ui/mask/ui_maska_d.1024c.png',
 'nvg_goggles':'ui/google_tex.png','nvg_noise':'ui/noise_tex.png',
}
MATERIALS=[('bint',8,'Бинт','Bandage'),('shpric',24,'Шприц','Syringe'),('medspirt',8,'Медицинский спирт','Medical alcohol')]


def run():
    archive=ROOT/'downloads/METRO_MOD_Content_decoded.zip'
    if hashlib.sha256(archive.read_bytes()).hexdigest()!='79aff5c64d12778fc826a53a4ee24e80a35934830812d08cdaad6ad9ba62d3ba':raise ValueError('Unexpected source')
    manifest=json.loads((ROOT/'reports/METRO_MOD/content_decoded_manifest.json').read_text())
    records={r['output']:r for r in manifest['records']};copied=[]
    with zipfile.ZipFile(archive) as z:
        def copy(source,target):
            data=z.read(source);sha=hashlib.sha256(data).hexdigest()
            if records[source]['sha256']!=sha:raise ValueError('Source digest mismatch')
            p=A/target;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data)
            copied.append(dict(source=source,target=target,sha256=sha,bytes=len(data)))
        soundjson=json.loads((A/'sounds.json').read_text())
        for key,source in SOUNDS.items():
            copy('assets/metro2033server/sounds/'+source+'.ogg','sounds/'+key+'.ogg')
            soundjson[key]={'sounds':['metroport:'+key]}
        (A/'sounds.json').write_text(json.dumps(dict(sorted(soundjson.items())),ensure_ascii=False,indent=2)+'\n')
        for name,source in TEXTURES.items():copy('assets/metro2033server/textures/'+source,'textures/metromod/ui/'+name+'.png')
        materials=[]
        for name,size,ru,en in MATERIALS:
            id='metromod_'+name
            copy('assets/metro2033server/textures/items/512/'+name+'.png','textures/metromod/items/'+name+'.png')
            (A/'models/item'/ (id+'.json')).write_text(json.dumps(dict(parent='minecraft:item/generated',textures=dict(layer0='metroport:metromod/items/'+name)),indent=2)+'\n')
            materials.append(dict(id=id,sourceId=name,stackSize=size,nameRu=ru,nameEn=en,kind='material',healingEffect=None))
        (A/'metromod_medical_materials.json').write_text(json.dumps(materials,ensure_ascii=False,indent=2)+'\n')
    (A/'metromod_selected_sources.json').write_text(json.dumps(copied,ensure_ascii=False,indent=2)+'\n')
    from mcmod_languages import update
    update()


if __name__=='__main__':run()
