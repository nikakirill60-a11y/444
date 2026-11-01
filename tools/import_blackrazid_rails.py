#!/usr/bin/env python3
"""Native rail footprints/materials. Missing vehicle meshes and empty old draw branches stay explicit."""
import re,json,ast
from import_content import ROOT,ASSETS,put,write_json
from mcmod_languages import update
S=ROOT/'tools/blackrazid-reference'
RUS=['Прямые широкие пути','Пересечение широких путей','Тупик широких путей','Правый поворот','Левый поворот','Правая стрелка','Левая стрелка','Z-образный путь','S-образный путь','Z-образная стрелка','S-образная стрелка','Y-образная стрелка']
def profiles():
    text=(S/'java/mods/provoda/items/ItemWideRails.java').read_text()
    names=re.findall(r'"([^"]+)"',re.search(r'String\[\] Names = new String\[\]\{([^;]+)',text)[1])
    raw=re.search(r'gagShift = (.+);\n',text)[1]
    raw=raw.replace('new int[0][]','[]');raw=re.sub(r'new int(?:\[\])+','',raw)
    offsets=ast.literal_eval(raw.replace('{','[').replace('}',']'))
    return [dict(id=i,nameEn=name,nameRu=RUS[i],offsets=offsets[i]) for i,name in enumerate(names)]
def main():
    write_json(ASSETS/'blackrazid_rails.json',profiles())
    materials={}
    for model in ['rails','rails90','railsX']:
        put(ASSETS/('models/blackrazid/rails/'+model.lower()+'.obj'),(S/('assets/rails/'+model+'.obj')).read_bytes())
        materials[model.lower()]={}
        text=(S/('assets/rails/'+model+'.mtl')).read_text()
        for material,body in re.findall(r'newmtl (\S+)\s+([\s\S]*?)(?=newmtl |\Z)',text):
            texture=re.search(r'map_Kd (\S+)',body)[1]
            put(ASSETS/('textures/blackrazid/rails/'+texture),(S/('assets/rails/'+texture)).read_bytes())
            materials[model.lower()][material]='metroport:textures/blackrazid/rails/'+texture
    write_json(ASSETS/'blackrazid_rail_materials.json',materials)
    write_json(ASSETS/'models/item/blackrazid_wide_rails.json',dict(parent='item/generated',textures=dict(layer0='metroport:items/blackrazid_wide_rails')))
    put(ASSETS/'textures/items/blackrazid_wide_rails.png',(S/'assets/items/rails.png').read_bytes())
    write_json(ASSETS/'blockstates/blackrazid_wide_rail.json',dict(variants={('direction='+str(i)):dict(model='metroport:blackrazid_rail_hidden') for i in range(4)}))
    write_json(ASSETS/'blockstates/blackrazid_rail_proxy.json',dict(variants={('direction='+str(i)):dict(model='metroport:blackrazid_rail_hidden') for i in range(4)}))
    write_json(ROOT/'docs/blackrazid-transport-source-gaps.json',dict(
        missingVehicleMeshes=['entity/drezina/regina/models/regina.obj','entity/drezina/regina/models/part.obj','entity/drezina/pass/drezina.obj','entity/drezina/pass/koleca.obj','entity/drezina/pass/richag.obj'],
        vehicleMeshSearch='Exact filenames and folders searched in supplied Drive; zero matches. MCMOD temp_regina is not a substitute.',
        missingRowSounds=['whistle_cher','piston_cher','brake'],
        emptyNativeDrawBranches=[2,5,6,7,8,9,10,11],
        adaptations=['Switch/composite tracks are displayed with repeated native rail segments; original renderer only transforms matrices in these branches.',
                     'Switch activated state is driven by server redstone in the port; recovered tiles only persist that flag and the vehicle reads it.',
                     'Dead end keeps its source collision but uses a native straight rail marker: no recovered buffer-stop mesh.',
                     'Placement validates ALL source footprint cells and rolls back on failure; native placement only prints most validation failures.',
                     'Straight and left-turn footprints also receive owner-linked proxies; the old item accidentally skipped their helper placement.'],
        status='Wide-rail runtime only; Regina/Drezina not registered as invisible or substitute vehicles.'))
    write_json(ASSETS/'models/block/blackrazid_rail_hidden.json',dict(textures=dict(particle='metroport:blackrazid/rails/wood_shpala_01.1024c'),elements=[]))
    update();print('Imported 12 native rail footprints, 3 OBJ and their 3 source material atlases.')
if __name__=='__main__':main()
