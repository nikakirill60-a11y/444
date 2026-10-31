"""MCMOD item registration, generated icon provenance, translation and reproducibility."""
import hashlib
import json
from pathlib import Path
import struct
import subprocess
import sys
import unittest
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/metroport'

class McmodUiAndItemsTests(unittest.TestCase):
    def test_all_53_split_icons_use_real_groups_and_matching_atlases(self):
        icons=json.loads((ROOT/'docs/mcmod-icons.json').read_text())['icons']
        entries=json.loads((ASSETS/'mcmod_armor.json').read_text())
        self.assertEqual(53,len(icons))
        for entry in entries:
            if entry['mode']=='whole':continue
            record=icons[entry['id']]
            groups=['head'] if entry['mode']=='helmet' else ['body','leftarm','rightarm']
            self.assertEqual(groups,record['groups'])
            model=entry['visualModel']
            self.assertEqual(model,record['model'])
            for field,path in [('objSha256','models/mcmod/'+model+'/model.obj'),('atlasSha256','textures/mcmod/armor/'+model+'.png'),('iconSha256','textures/'+entry['icon']+'.png')]:
                data=(ASSETS/path).read_bytes();self.assertEqual(record[field],hashlib.sha256(data).hexdigest())
            data=(ASSETS/('textures/'+entry['icon']+'.png')).read_bytes()
            self.assertEqual((128,128),struct.unpack('>II',data[16:24]))
        for entry in entries:
            if entry['mode']=='helmet':
                vest=next((e for e in entries if e['model']==entry['model'] and e['mode']=='vest'),None)
                if vest:self.assertNotEqual(icons[entry['id']]['iconSha256'],icons[vest['id']]['iconSha256'])

    def test_armor_names_and_tooltips_are_localized_without_internal_ids(self):
        entries=json.loads((ASSETS/'mcmod_armor.json').read_text())
        for e in entries:
            for field in ['nameRu','nameEn']:
                self.assertNotIn('[MCMOD]',e[field]);self.assertNotIn('_',e[field])
        self.assertEqual('Шлем Ганзы',next(e['nameRu'] for e in entries if e['id']=='mcmod_hanza_helmet'))
        shared=json.loads((ROOT/'tools/mcmod-localization.json').read_text())
        self.assertEqual(set(shared['ru_ru']),set(shared['en_us']))
        for locale in shared:
            text=(ASSETS/('lang/'+locale+'.lang')).read_text()
            for key,value in shared[locale].items():self.assertIn(key+'='+value+'\n',text)
        java=(ROOT/'src/main/java/ru/metrocraft/port/mcmod/McmodArmorItem.java').read_text()
        self.assertIn('I18n.format("metroport.mcmod.armor.',java)
        self.assertNotIn('Защита предмета',java)

    def test_nine_ordinary_items_match_source_registration_and_original_icons(self):
        entries=json.loads((ASSETS/'mcmod_items.json').read_text())
        expected={'money','money5','money30','moneyj','res_metall','res_xim','filtr','aptech','machete'}
        self.assertEqual(expected,{e['sourceId'] for e in entries})
        source=(ROOT/'tools/mcmod-reference/java/metro/common/regstration/RegisterGuns.java').read_text()
        for e in entries:
            key=e['sourceId'];self.assertIn('GameRegistry.registerItem((Item)'+key+', (String)"'+key+'")',source)
            original=(ROOT/('tools/mcmod-reference/assets/items/'+key+'.png')).read_bytes()
            bundled=(ASSETS/('textures/mcmod/items/'+key+'.png')).read_bytes()
            self.assertEqual(original,bundled);self.assertEqual(e['iconSha256'],hashlib.sha256(bundled).hexdigest())
            model=json.loads((ASSETS/('models/item/'+e['id']+'.json')).read_text())
            self.assertEqual('metroport:mcmod/items/'+key,model['textures']['layer0'])
            self.assertEqual(1 if e['kind'] in ['medkit','machete'] else 64,e['stackSize'])
        self.assertIn('setCreativeTab(MetroPort.THINGS)',(ROOT/'src/main/java/ru/metrocraft/port/mcmod/McmodContent.java').read_text())

    def test_ordinary_import_is_reproducible_and_keeps_both_catalog_localizations(self):
        paths=[ASSETS/'mcmod_items.json',ROOT/'docs/mcmod-items-report.json']
        paths+=list((ASSETS/'textures/mcmod/items').glob('*.png'))
        paths+=[ASSETS/('lang/'+l+'.lang') for l in ['ru_ru','en_us']]
        before={p:p.read_bytes() for p in paths}
        subprocess.run([sys.executable,str(ROOT/'tools/import_mcmod_items.py')],check=True,cwd=ROOT,capture_output=True)
        self.assertEqual(before,{p:p.read_bytes() for p in paths})
