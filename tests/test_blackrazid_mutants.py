import hashlib,json,re,unittest
from pathlib import Path
from importlib import util
ROOT=Path(__file__).resolve().parents[1]
S=ROOT/'tools/blackrazid-reference'
A=ROOT/'src/main/resources/assets/metroport'

class BlackrazidMutantsTest(unittest.TestCase):
    def test_provenance_matches_every_recovered_original(self):
        rows=json.loads((S/'mutants-source-files.json').read_text())['files']
        self.assertEqual(62,len(rows));self.assertEqual(len(rows),len({e['path'] for e in rows}))
        for e in rows:
            data=(S/e['path']).read_bytes();self.assertEqual(e['bytes'],len(data));self.assertEqual(e['sha256'],hashlib.sha256(data).hexdigest());self.assertTrue(e['driveId'])
    def test_only_three_completed_profiles_are_claimed(self):
        rows=json.loads((A/'blackrazid_mutants.json').read_text());self.assertEqual({'Rat','Spider','Biblio'},{e['sourceId'] for e in rows})
        self.assertEqual([15,80,200],[e['health'] for e in rows])
        code=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/mutants/BlackrazidMutants.java').read_text()
        self.assertEqual(5,len(re.findall(r'add\([^;]+\.class,',code)));self.assertNotIn('registerGlobalEntityID',code)
    def test_original_models_and_meshes_not_stock_substitutes(self):
        for entry in json.loads((A/'blackrazid_mutants.json').read_text()):
            model=entry['model'];source=(S/'original-models'/(model+'.java')).read_text()
            runtime=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/mutants'/(model+'.java')).read_text()
            self.assertEqual(source.count('.render();'),runtime.count('.render();'))
            self.assertEqual(re.findall(r'GL11\.\w+\([^;]+;',source),re.findall(r'GL11\.\w+\([^;]+;',runtime))
            for obj in re.findall(r'new GLModel\("/mods/provoda/mobs/models/([^"\n]+)',source):
                self.assertEqual((S/('assets/mutants/'+obj)).read_bytes(),(A/('models/blackrazid/mutants/'+obj)).read_bytes())
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
