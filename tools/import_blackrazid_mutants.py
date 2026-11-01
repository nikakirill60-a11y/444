#!/usr/bin/env python3
"""Copy only recovered assets and adapt original model Java API, never fabricate geometry."""
import json,re
from import_content import ROOT,ASSETS,put,write_json
from mcmod_languages import update
S=ROOT/'tools/blackrazid-reference'
# Demon uses its own flight implementation, not the ground profile loop.
MOBS=[dict(id='blackrazid_rat',sourceId='Rat',model='ModelRat',folder='rat',texture='act_rat_1.512c.png',nameRu='Крыса',nameEn='Rat',health=15,damage=4,speed=.45),
      dict(id='blackrazid_spider',sourceId='Spider',model='ModelSpider',folder='spider',texture='spider.png',nameRu='Паук',nameEn='Spider',health=80,damage=6,speed=.45),
      dict(id='blackrazid_biblio',sourceId='Biblio',model='ModelBiblio',folder='bibliotecar',texture='biblio.png',nameRu='Библиотекарь',nameEn='Librarian',health=200,damage=10,speed=.46)]
# Class, original source, model, folder, atlas, localized names, HP/damage/speed/ranges/air.
GROUND=[
    ('KikimoraEntity','Kikimora','ModelKikimora','kikimora','kikimora.png','Кикимора','Kikimora',40,4,.45,32,8,True),
    ('StrazhEntity','Strazh','ModelStrazh','strazh','strazh.png','Страж','Watcher',100,8,.48,32,16,True),
    ('NosachEntity','Nosach','ModelNosach','nosach','nosach.png','Носач','Nosach',120,6,.46,32,8,True),
    ('DarkEntity','Dark','ModelDark','dark','dark.png','Тёмный','Dark One',200,12,.47,16,8,True),
    ('BearEntity','Bear','ModelBear','bear','act34_bear_kub.512c.png','Медведь','Bear',140,8,.45,64,8,True),
    ('MobusEntity','Mobus','ModelMobus','mobus','act34_bear_kub.512c.png','Мобус','Mobus',140,8,.45,64,8,True),
    ('AquaFemaleEntity','Aqua_female','ModelAquaf','aqua_female','act34_aqua_fem.512c.png','Водяная (самка)','Water mutant (female)',90,6,.45,32,8,False),
    ('AquaMaleEntity','Aqua_male','ModelAquam','aqua_male','act_aqua_male.512c.png','Водяной (самец)','Water mutant (male)',250,10,.45,32,8,False),
]
for cls,source,model,folder,texture,ru,en,hp,damage,speed,follow,watch,air in GROUND:
    MOBS.append(dict(id='blackrazid_'+folder,sourceId=source,model=model,folder=folder,texture=texture,nameRu=ru,nameEn=en,
        health=hp,damage=damage,speed=speed,followRange=follow,watchRange=watch,waterBreathing=air))
MOBS.append(dict(id='blackrazid_demon',sourceId='Demon',model='ModelDemon',folder='demon',texture='act34_demon_part01.512c.png',
    nameRu='Демон',nameEn='Demon',health=800,damage=12,speed=None,flight=True,headContactDamage=10,headSize=[6,6],size=[8,8],
    extraTextures={'act34_demon_part02':'act34_demon_part02.512.png'}))
# Source aliases are deliberate: Mobus really uses bear sounds, Aqua has abbreviated filenames.
SOUND_FAMILIES=['rat','spider','biblio','kikimora','strazh','nosach','dark','bear','aquaf','aquam','demon']
SPAWNERS=[dict(id='blackrazid_spawner_'+name,sourceId=source,period=period,batch=batch,nameRu=ru,nameEn=en) for name,source,period,batch,ru,en in [
    ('spider','Spider',4000,2,'Паучье гнездо','Spider nest'),('biblio','Biblio',10000,1,'Спавнер библиотекаря','Librarian spawner'),
    ('kikimora','Kikimora',4000,3,'Спавнер кикимор','Kikimora spawner'),('strazh','Strazh',4000,3,'Спавнер стражей','Watcher spawner'),
    ('nosach','Nosach',6000,2,'Спавнер носачей','Nosach spawner'),('dark','Dark',8000,2,'Спавнер тёмных','Dark One spawner'),
    ('bear','Bear',8000,2,'Спавнер медведей','Bear spawner'),('mobus','Mobus',8000,2,'Спавнер Мобусов','Mobus spawner'),
    ('aqua_female','Aquaf',6000,2,'Спавнер водяных (самки)','Female water mutant spawner'),
    ('aqua_male','Aquam',10000,1,'Спавнер водяного (самец)','Male water mutant spawner'),
    ('demon','Demon',30000,1,'Спавнер демона','Demon spawner')]]

def main():
    write_json(ASSETS/'blackrazid_mutants.json',MOBS)
    write_json(ASSETS/'blackrazid_spawners.json',SPAWNERS)
    dst=ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/mutants';dst.mkdir(parents=True,exist_ok=True)
    meshes=[]
    for m in MOBS:
        text=(S/'original-models'/(m['model']+'.java')).read_text()
        source_meshes={name.lower():name for name in re.findall(r'new GLModel\("/mods/provoda/mobs/models/([^"\n]+)',text)}
        text=re.sub(r'package [^;]+;', 'package ru.metrocraft.port.client.blackrazid.mutants;',text)
        text=text.replace('import mods.provoda.GLLoader.GLModel;', '').replace('net.minecraft.util.MathHelper','net.minecraft.util.math.MathHelper')
        if m['model']!='ModelKikimora':text=re.sub(r'\s*EntityLiving entity1 = \(EntityLiving\)entity;', '',text)
        text=text.replace('entity.worldObj','entity.world')
        if m['model']=='ModelKikimora':
            # Its old setRotationAngles draws geometry. RenderLiving calls that hook before render;
            # rename to a private draw pass so the six parts are not rendered twice in 1.12.
            text=text.replace('public void setRotationAngles(', 'private void renderParts(').replace('this.setRotationAngles(', 'this.renderParts(')
            text=text.replace('Minecraft.getMinecraft().renderEngine.bindTexture("/mods/provoda/textures/mobs/kikimora.png")',
                'Minecraft.getMinecraft().getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("metroport:textures/blackrazid/mutants/kikimora.png"))')
        text=text.replace('GL11.glDisable(2884)', 'net.minecraft.client.renderer.GlStateManager.disableCull()').replace('GL11.glEnable(2884)', 'net.minecraft.client.renderer.GlStateManager.enableCull()')
        text=text.replace('GLModel','MutantObjPart').replace('"/mods/provoda/mobs/models/', '"metroport:models/blackrazid/mutants/')
        text=re.sub(r'"metroport:models/blackrazid/mutants/([^"]+)"',lambda match:'"metroport:models/blackrazid/mutants/'+match[1].lower()+'"',text)
        if m['sourceId']=='Demon':
            text=re.sub(r'new MutantObjPart\(("[^"\n]+")\)',r'new MutantObjPart(\1,"metroport:textures/blackrazid/mutants/demon.png","metroport:textures/blackrazid/mutants/demon_part02.png")',text)
            put(ASSETS/'textures/blackrazid/mutants/demon_part02.png',(S/'assets/mutants/demon/act34_demon_part02.512.png').read_bytes())
        text='// Generated from the user-supplied original model by import_blackrazid_mutants.py.\n'+text
        (dst/(m['model']+'.java')).write_text(text)
        for name in re.findall(r'new MutantObjPart\("metroport:models/blackrazid/mutants/([^"\n]+)',text):
            meshes.append(name)
            put(ASSETS/('models/blackrazid/mutants/'+name),(S/('assets/mutants/'+source_meshes[name])).read_bytes())
        put(ASSETS/('textures/blackrazid/mutants/'+m['folder']+'.png'),(S/('assets/mutants/'+m['folder']+'/'+m['texture'])).read_bytes())
    write_json(ASSETS/'blackrazid_mutant_meshes.json',sorted(meshes))
    sounds=json.loads((ASSETS/'sounds.json').read_text())
    missing=[]
    for name in SOUND_FAMILIES:
        for suffix in ['alive','hit','death']:
            key='blackrazid_'+name+'_'+suffix;source=S/('assets/sounds/'+name+'_'+suffix+'.ogg')
            if not source.exists():
                missing.append(name+'_'+suffix);continue # Missing source stays silent, not substituted with another creature.
            put(ASSETS/('sounds/'+key+'.ogg'),source.read_bytes())
            sounds[key]=dict(sounds=['metroport:'+key])
    write_json(ASSETS/'blackrazid_mutant_missing_sounds.json',missing)
    write_json(ASSETS/'sounds.json',dict(sorted(sounds.items())))
    update();print('Imported',len(MOBS),'native mutant models and',len(SPAWNERS),'spawners; game acceptance remains pending.')
if __name__=='__main__':main()
