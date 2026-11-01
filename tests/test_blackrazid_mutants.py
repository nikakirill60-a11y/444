import hashlib,json,re,unittest,subprocess,sys
from pathlib import Path
from importlib import util
ROOT=Path(__file__).resolve().parents[1]
S=ROOT/'tools/blackrazid-reference'
A=ROOT/'src/main/resources/assets/metroport'

class BlackrazidMutantsTest(unittest.TestCase):
    def test_provenance_matches_every_recovered_original(self):
        rows=json.loads((S/'mutants-source-files.json').read_text())['files']
        self.assertEqual(150,len(rows));self.assertEqual(len(rows),len({e['path'] for e in rows}))
        for e in rows:
            data=(S/e['path']).read_bytes();self.assertEqual(e['bytes'],len(data));self.assertEqual(e['sha256'],hashlib.sha256(data).hexdigest());self.assertTrue(e['driveId'])
    def test_all_twelve_native_profiles_are_separate(self):
        rows=json.loads((A/'blackrazid_mutants.json').read_text());self.assertEqual({'Rat','Spider','Biblio','Kikimora','Strazh','Nosach','Dark','Bear','Mobus','Aqua_female','Aqua_male','Demon'},{e['sourceId'] for e in rows})
        self.assertEqual([15,80,200],[e['health'] for e in rows[:3]])
        code=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/mutants/BlackrazidMutants.java').read_text()
        self.assertEqual(23,len(re.findall(r'add\([^;]+\.class,',code)));self.assertNotIn('registerGlobalEntityID',code)
    def test_original_models_and_meshes_not_stock_substitutes(self):
        for entry in json.loads((A/'blackrazid_mutants.json').read_text()):
            model=entry['model'];source=(S/'original-models'/(model+'.java')).read_text()
            runtime=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/mutants'/(model+'.java')).read_text()
            self.assertEqual(source.count('.render();'),runtime.count('.render();'))
            runtime=runtime.replace('net.minecraft.client.renderer.GlStateManager.disableCull()', 'GL11.glDisable(2884)').replace('net.minecraft.client.renderer.GlStateManager.enableCull()', 'GL11.glEnable(2884)')
            self.assertEqual(re.findall(r'GL11\.\w+\([^;]+;',source),re.findall(r'GL11\.\w+\([^;]+;',runtime))
            for obj in re.findall(r'new GLModel\("/mods/provoda/mobs/models/([^"\n]+)',source):
                self.assertEqual((S/('assets/mutants/'+obj)).read_bytes(),(A/('models/blackrazid/mutants/'+obj.lower())).read_bytes())
            self.assertEqual((S/('assets/mutants/'+entry['folder']+'/'+entry['texture'])).read_bytes(),(A/('textures/blackrazid/mutants/'+entry['folder']+'.png')).read_bytes())
    def test_original_sounds_and_translations_exist(self):
        events=json.loads((A/'sounds.json').read_text())
        for name in ['rat','spider','biblio']:
            for kind in ['alive','hit','death']:
                key='blackrazid_'+name+'_'+kind;self.assertIn(key,events)
                self.assertEqual((S/('assets/sounds/'+name+'_'+kind+'.ogg')).read_bytes(),(A/('sounds/'+key+'.ogg')).read_bytes())
            for locale in ['en_us','ru_ru']:
                lines=(A/('lang/'+locale+'.lang')).read_text();self.assertEqual(1,lines.count('entity.metroport.blackrazid_'+name+'.name='))
    def test_general_handlers_and_world_destruction_not_imported(self):
        paths=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/mutants').glob('*.java')
        code='\n'.join(p.read_text() for p in paths)
        self.assertNotIn('import mods.provoda',code);self.assertNotIn('ExtendetPlayer.get',code)
        for banned in ['setBlockState(', 'createExplosion(', 'executeCommand(', 'addSpawn(']:self.assertNotIn(banned,code)
        spawner=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/mutants/MutantSpawner.java').read_text()
        self.assertIn('isAreaLoaded(',spawner);self.assertIn('NEARBY_CAP=16',spawner);self.assertIn('doMobSpawning',spawner)

    def test_mutant_importer_is_reproducible(self):
        paths=[A/'blackrazid_mutants.json',A/'blackrazid_spawners.json',A/'blackrazid_mutant_meshes.json',A/'blackrazid_mutant_missing_sounds.json',A/'sounds.json',A/'lang/ru_ru.lang',A/'lang/en_us.lang']
        paths+=list((ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/mutants').glob('Model*.java'))
        before=[p.read_bytes() for p in paths]
        subprocess.run([sys.executable,'tools/import_blackrazid_mutants.py'],cwd=ROOT,check=True,capture_output=True)
        self.assertEqual(before,[p.read_bytes() for p in paths])

    def test_all_source_profiles_and_spawner_batches_match(self):
        mobs=json.loads((A/'blackrazid_mutants.json').read_text())
        for mob in mobs:
            source=(S/('java/mods/provoda/mobs/mobs/'+mob['sourceId']+'.java')).read_text()
            self.assertEqual(mob['health'],int(re.search(r'int aW\(\)\s*{\s*return (\d+)',source)[1]))
            self.assertEqual(mob['damage'],int(re.search(r'int c\(mp var1\)\s*{\s*return (\d+)',source)[1]))
        spawners=json.loads((A/'blackrazid_spawners.json').read_text());self.assertEqual(11,len(spawners))
        for spawner in spawners:
            source=(S/('java/mods/provoda/entity/EntitySpawner'+spawner['sourceId']+'.java')).read_text()
            self.assertEqual(spawner['period'],int(re.search(r'this.timer = (\d+)',source)[1]))
            self.assertEqual(spawner['batch'],len(re.findall(r'new \w+\(this.q\)',source)))
            for locale in ['ru_ru','en_us']:
                self.assertEqual(1,(A/('lang/'+locale+'.lang')).read_text().count('entity.metroport.'+spawner['id']+'.name='))

    def test_missing_recordings_remain_explicit_and_not_invented(self):
        missing=json.loads((A/'blackrazid_mutant_missing_sounds.json').read_text())
        self.assertEqual(['dark_hit','dark_death','bear_hit'],missing)
        sounds=json.loads((A/'sounds.json').read_text())
        for family in ['kikimora','strazh','nosach','dark','bear','aquaf','aquam','demon']:
            for kind in ['alive','hit','death']:
                name=family+'_'+kind;key='blackrazid_'+name
                if name in missing:self.assertNotIn(key,sounds);continue
                self.assertEqual((S/('assets/sounds/'+name+'.ogg')).read_bytes(),(A/('sounds/'+key+'.ogg')).read_bytes())
        root=ROOT/'src/main/java/ru/metrocraft/port/blackrazid/mutants'
        self.assertIn('return "bear"',(root/'MobusEntity.java').read_text())
        self.assertNotEqual((S/'assets/mutants/bear/bear.obj').read_bytes(),(S/'assets/mutants/mobus/bear.obj').read_bytes())

    def test_kikimora_does_not_draw_from_pose_hook_twice(self):
        code=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/mutants/ModelKikimora.java').read_text()
        self.assertNotIn('void setRotationAngles(',code);self.assertIn('private void renderParts(',code)
        self.assertEqual(1,code.count('this.renderParts('));self.assertNotIn('worldObj',code);self.assertNotIn('renderEngine',code)
        meshes=json.loads((A/'blackrazid_mutant_meshes.json').read_text());self.assertEqual(68,len(meshes));self.assertEqual(68,len(set(meshes)))
        for mesh in meshes:self.assertTrue((A/('models/blackrazid/mutants/'+mesh)).exists())

    def test_native_cull_changes_use_minecraft_cache_and_restore_on_failure(self):
        root=ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/mutants'
        dark=(root/'ModelDark.java').read_text()
        self.assertIn('GlStateManager.disableCull()',dark);self.assertIn('GlStateManager.enableCull()',dark)
        self.assertNotIn('GL11.glDisable',dark);self.assertNotIn('GL11.glEnable',dark)
        renderer=(root/'MutantRenderers.java').read_text()
        self.assertEqual(2,renderer.count('boolean cull=GL11.glIsEnabled(GL11.GL_CULL_FACE)'))
        self.assertEqual(2,renderer.count('finally{GL11.glShadeModel(shade);restoreCull(cull);'))

    def test_demon_has_both_source_atlases_and_preserves_material_groups(self):
        self.assertEqual((S/'assets/mutants/demon/act34_demon_part02.512.png').read_bytes(),(A/'textures/blackrazid/mutants/demon_part02.png').read_bytes())
        mtl=(S/'assets/mutants/demon/leftkrilo1.mtl').read_text()
        self.assertIn('map_Kd act34_demon_part01.512c.png',mtl);self.assertIn('map_Kd act34_demon_part02.512.png',mtl)
        for part in ['leftkrilo1','rightkrilo1']:
            obj=(S/('assets/mutants/demon/'+part+'.obj')).read_text()
            self.assertIn('g act34_demon_part01',obj);self.assertIn('g act34_demon_part02',obj)
        code=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/mutants/MutantObjPart.java').read_text()
        self.assertIn('group.getKey().equals("act34_demon_part02")?membrane:primary',code)
        self.assertIn('finally{GlStateManager.bindTexture(texture);}',code)
        registry=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/mutants/BlackrazidMutants.java').read_text()
        ids=re.findall(r'add\([^;]+\.class,"([^"]+)",(\d+),',registry)
        self.assertEqual(23,len(ids));self.assertEqual(set(range(80,103)),{int(i) for name,i in ids})
