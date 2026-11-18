"""Content completeness, source fidelity and static safety of the generated wiki."""
import hashlib
import json
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
SITE = ROOT/'website'
ASSETS = ROOT/'src/main/resources/assets/metroport'

def load(name): return json.loads((ASSETS/(name+'.json')).read_text())

class WikiCatalogTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.data = json.loads((SITE/'wiki-data.json').read_text())
        cls.entries = cls.data['entries']
        cls.byid = {e['id']: e for e in cls.entries}
        cls.html = (SITE/'wiki.html').read_text()

    def test_counts_and_unique_full_ids(self):
        self.assertEqual(self.data['counts'], {'weapons': 28, 'items': 271, 'armor': 110, 'mutants': 18})
        self.assertEqual(len(self.entries), 427)
        self.assertEqual(len(self.byid), len(self.entries))
        self.assertEqual(sum(e['id'].startswith('modularwarfare:') for e in self.entries), 84)
        self.assertEqual(sum(e['category']=='weapons' and e['id'].startswith('modularwarfare:') for e in self.entries), 25)

    def test_all_active_json_registries_are_included(self):
        for filename in ('mcmod_armor', 'blackrazid_equipment', 'mcmod_items', 'blackrazid_survival',
                         'blackrazid_mutants', 'blackrazid_spawners', 'metromod_gates',
                         'metromod_interactive', 'metromod_consumables', 'metromod_medical_materials'):
            for row in load(filename): self.assertIn('metroport:'+row['id'], self.byid, filename)
        for d in load('content'):
            active = d['kind'] not in ('gun','ammo','attachment') and not d.get('repairGun')
            self.assertEqual('metroport:'+d['id'] in self.byid, active, d['id'])
        for i in range(58): self.assertIn('metroport:block'+str(i), self.byid)
        self.assertEqual(len([e for e in self.entries if e['group']=='Рюкзаки']), 6)
        self.assertEqual(len([e for e in self.entries if e['group']=='Нашивки']), 11)

    def test_removed_content_does_not_return(self):
        for id in load('reduced_scope')['removedIds']:
            self.assertNotIn('metroport:'+id, self.byid)
        for id in ('exodus_gasmask_gold', 'exodus_gasmask_diamond', 'metropickaxe'):
            self.assertNotIn('metroport:'+id, self.byid)
        self.assertFalse(any(e['id'].startswith('metroport:mcmod_decor_') for e in self.entries))

    def test_weapon_corrections_and_cross_category_compatibility(self):
        for id in ('metro_medved','metro_velopal'):
            e = self.byid['modularwarfare:'+id]
            self.assertEqual(dict(e['stats'])['Внутренняя ёмкость'], '6')
            self.assertIn('modularwarfare:metro_12g_buckshot', e['links'])
            self.assertNotIn('modularwarfare:metro_vyhlop_ammo', e['links'])
        self.assertIn('собственный баллон', self.byid['modularwarfare:metro_ognemet_mini']['description'])
        self.assertIn('Не установлен по умолчанию', self.byid['modularwarfare:metro_mod_ubludok_radiator']['description'])
        self.assertEqual(dict(self.byid['modularwarfare:metro_duplet']['stats'])['Внутренняя ёмкость'], '2')
        self.assertIn('до четырёх', self.byid['modularwarfare:metro_duplet']['description'])
        for e in self.entries:
            for target in e['links']: self.assertIn(target, self.byid)

    def test_armor_stats_not_donor_set_protection(self):
        for row in load('mcmod_armor') + load('blackrazid_equipment'):
            e = self.byid['metroport:'+row['id']]
            if row['mode']=='backpack': continue
            stats = dict(e['stats'])
            self.assertAlmostEqual(float(stats['Защита, коэффициент']), row['defence'], places=4)
            self.assertEqual(int(stats['Прочность']), row['condition'])
        self.assertIn('отдельным шлемом', self.byid['metroport:mcmod_officer_ozk']['description'])
        self.assertIn('собственный слот ПНВ', self.byid['metroport:pnv']['description'])

    def test_mutants_do_not_invent_rat_attack_or_demon_damage(self):
        self.assertEqual(dict(self.byid['metroport:blackrazid_rat']['stats'])['Урон'], 'Не атакует')
        self.assertEqual(dict(self.byid['metroport:blackrazid_demon']['stats'])['Урон'], '10')
        self.assertIn('наземный ИИ', self.byid['metroport:blackrazid_aqua_male']['description'])
        self.assertIn('адаптации', self.byid['metroport:exodus_guard']['description'])

    def test_every_article_and_local_icon_is_published_with_provenance(self):
        manifest = json.loads((SITE/'wiki-manifest.json').read_text())
        for e in self.entries:
            self.assertIn('id="entry-'+e['id'].replace(':','-')+'"', self.html)
            self.assertTrue(e['source'])
            if not e['source'].startswith('MWF v10:'): self.assertTrue((ROOT/e['source']).is_file(), e['source'])
            if e['image']:
                file = SITE/e['image'].lstrip('/')
                self.assertTrue(file.is_file())
                self.assertEqual(hashlib.sha256(file.read_bytes()).hexdigest(), manifest['icons'][e['image']])
        self.assertEqual(len(manifest['icons']), 301)
        self.assertNotIn('МАТЕРИАЛЫ ГОТОВЯТСЯ', self.html)
        self.assertIn('Без иллюстрации', self.html)

if __name__=='__main__': unittest.main()
