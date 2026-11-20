"""User weapon.zip source fidelity, distinct registries and optional full-pack regressions."""
import hashlib,json,sys,unittest,zipfile,subprocess
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'
sys.path.insert(0,str(ROOT/'tools'))
class WeaponUpdateV13(unittest.TestCase):
    def test_source_archive_is_the_supplied_weapon_zip(self):
        self.assertEqual('72ff0e0aa6907d5eff3826903ef3a574ec0a675afdecd8f77855a7d39f4bb0ee',hashlib.sha256((ROOT/'tools/weapon3/sources.zip').read_bytes()).hexdigest())
    def test_three_melee_original_meshes_textures_names_and_independent_icons(self):
        z=zipfile.ZipFile(ROOT/'tools/weapon3/sources.zip');entries=json.loads((A/'melee_weapons.json').read_text())
        self.assertEqual({'hammer':'rare','crowbar':'epic','axe':'legendary'},{e['id']:e['rarity'] for e in entries})
        from build_uboinik_pack import obj_read
        icons=[]
        for e in entries:
            self.assertEqual(z.read(e['source']),(A/f'models/legacy/mcmod_tools/{e["id"]}.obj').read_bytes())
            self.assertEqual(z.read(e['source'].rsplit('/',1)[0]+'/props_instru.png'),(A/f'textures/mcmod/tools/{e["id"]}.png').read_bytes())
            self.assertEqual(e['triangles'],sum(map(len,obj_read(z.read(e['source'])).values())))
            for loc,key in [('ru_ru','nameRu'),('en_us','nameEn')]:self.assertIn('item.metroport.'+e['id']+'.name='+e[key],(A/f'lang/{loc}.lang').read_text())
            icons.append((A/f'textures/items/{e["id"]}.png').read_bytes())
        self.assertEqual(3,len(set(icons)))
    def test_import_is_deterministic(self):
        paths=[A/'melee_weapons.json']+[p for directory in ['models/item','models/legacy/mcmod_tools','textures/items','textures/mcmod/tools'] for p in (A/directory).glob('*') if p.stem in {'axe','hammer','crowbar'}]
        before={p:p.read_bytes() for p in paths};subprocess.run([sys.executable,str(ROOT/'tools/import_weapon3.py')],check=True)
        self.assertEqual(before,{p:p.read_bytes() for p in paths})
    def test_active_wiki_and_balance_remove_akk_not_other_kalashes(self):
        w=json.loads((ROOT/'website/wiki-data.json').read_text());byid={e['id']:e for e in w['entries']};b=json.loads((A/'inventory_balance.json').read_text())['items']
        for id in ['modularwarfare:metro_akk','modularwarfare:metro_akk_ammo']:self.assertNotIn(id,byid);self.assertNotIn(id,b)
        for id,tier in {'metro_ak':'rare','metro_army_ak':'legendary','metro_ak2012':'mythic'}.items():self.assertEqual(tier,byid['modularwarfare:'+id]['rarity'])
        for id,tier in {'hammer':'rare','crowbar':'epic','axe':'legendary'}.items():self.assertEqual(tier,byid['metroport:'+id]['rarity']);self.assertEqual('weapons',byid['metroport:'+id]['category'])
    def test_full_pack_is_exact_reproducible_v12_delta(self):
        from build_mwf_v13 import prepare,PACK
        from update_mwf_v10 import readzip
        path=ROOT/'.cache/output-v12/Metro_Full_Arsenal_MWF_fix1_v12_separate_textures.zip'
        if not path.exists():self.skipTest('Full v12 baseline generated in pack CI, not Forge-only build')
        files=readzip(path);report=prepare(files,readzip(ROOT/'tools/weapon3/sources.zip'))
        self.assertEqual(28,report['verifiedGunCount']);self.assertTrue(report['unrelatedWeaponsByteIdentical'])
        built=ROOT/'.cache/output-v13'/PACK
        if built.exists():self.assertEqual(files,readzip(built))
        from build_mwf_v11 import model
        from update_mwf_v10 import accessor
        d=model(files,'metro_ognemet_mini');node=next(n for n in d['nodes'] if n['name']=='gunModel');mesh=d['meshes'][node['mesh']]
        self.assertEqual(17418,sum(len(accessor(d,p['indices']))//3 for p in mesh['primitives']))
        z=zipfile.ZipFile(ROOT/'tools/weapon3/sources.zip')
        self.assertEqual(z.read('ognemet_mini/wpn34_ognemet_b.png'),files['assets/modularwarfare/skins/guns/metro_ognemet_mini.png'])
