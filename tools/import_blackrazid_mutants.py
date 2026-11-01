#!/usr/bin/env python3
"""Copy only recovered assets and adapt original model Java API, never fabricate geometry."""
import json,re
from import_content import ROOT,ASSETS,put,write_json
from mcmod_languages import update
S=ROOT/'tools/blackrazid-reference'
# Other nine source/model profiles are recovered references, not registered runtime entities yet.
MOBS=[dict(id='blackrazid_rat',sourceId='Rat',model='ModelRat',folder='rat',texture='act_rat_1.512c.png',nameRu='Крыса',nameEn='Rat',health=15,damage=4,speed=.45),
      dict(id='blackrazid_spider',sourceId='Spider',model='ModelSpider',folder='spider',texture='spider.png',nameRu='Паук',nameEn='Spider',health=80,damage=6,speed=.45),
      dict(id='blackrazid_biblio',sourceId='Biblio',model='ModelBiblio',folder='bibliotecar',texture='biblio.png',nameRu='Библиотекарь',nameEn='Librarian',health=200,damage=10,speed=.46)]
def main():
    write_json(ASSETS/'blackrazid_mutants.json',MOBS)
    dst=ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/mutants';dst.mkdir(parents=True,exist_ok=True)
    for m in MOBS:
        text=(S/'original-models'/(m['model']+'.java')).read_text()
        text=re.sub(r'package [^;]+;', 'package ru.metrocraft.port.client.blackrazid.mutants;',text)
        text=text.replace('import mods.provoda.GLLoader.GLModel;', '').replace('net.minecraft.util.MathHelper','net.minecraft.util.math.MathHelper')
        text=re.sub(r'\s*EntityLiving entity1 = \(EntityLiving\)entity;', '',text)
        text=text.replace('GLModel','MutantObjPart').replace('"/mods/provoda/mobs/models/', '"metroport:models/blackrazid/mutants/')
        text='// Generated from the user-supplied original model by import_blackrazid_mutants.py.\n'+text
        (dst/(m['model']+'.java')).write_text(text)
        for name in re.findall(r'new MutantObjPart\("metroport:models/blackrazid/mutants/([^"\n]+)',text):
            put(ASSETS/('models/blackrazid/mutants/'+name),(S/('assets/mutants/'+name)).read_bytes())
        put(ASSETS/('textures/blackrazid/mutants/'+m['folder']+'.png'),(S/('assets/mutants/'+m['folder']+'/'+m['texture'])).read_bytes())
    sounds=json.loads((ASSETS/'sounds.json').read_text())
    for m in MOBS:
        name=m['id'][len('blackrazid_'):]
        for suffix in ['alive','hit','death']:
            key='blackrazid_'+name+'_'+suffix
            put(ASSETS/('sounds/'+key+'.ogg'),(S/('assets/sounds/'+name+'_'+suffix+'.ogg')).read_bytes())
            sounds[key]=dict(sounds=['metroport:'+key])
    write_json(ASSETS/'sounds.json',dict(sorted(sounds.items())))
    update();print('Imported 3 original mutant models, atlases and 9 sounds; remaining mutants not claimed complete.')
if __name__=='__main__':main()
