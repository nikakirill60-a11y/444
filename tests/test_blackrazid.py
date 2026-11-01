import hashlib
import importlib.util
import json
from pathlib import Path
import sys
import unittest
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'tools'))
from import_blackrazid import geometry, MODELS, JAVA
A=ROOT/'src/main/resources/assets/metroport'
E=json.loads((A/'blackrazid_equipment.json').read_text())

class BlackrazidTests(unittest.TestCase):
    def test_exact_registered_inventory(self):
        self.assertEqual(len(E),18)
        self.assertEqual(sum(e['mode']=='whole' for e in E),11)
        self.assertEqual(sum(e['mode']=='helmet' for e in E),4)
        self.assertEqual(sum(e['mode']=='backpack' for e in E),3)
        self.assertEqual(set(e['sourceId'] for e in E),set(MODELS))
        self.assertEqual(len(set(e['id'] for e in E)),18)
    def test_all_original_sources_verified(self):
        s=ROOT/'tools/blackrazid-reference'
        files=json.loads((s/'source-files.json').read_text())['files']
        self.assertEqual(len(files),78)
        for e in files:
            b=(s/e['path']).read_bytes()
            self.assertTrue(e['driveId']);self.assertEqual(len(b),e['bytes']);self.assertEqual(hashlib.sha256(b).hexdigest(),e['sha256'])
    def test_exact_constructor_and_joint_data_reproduced(self):
        for e in E:
            self.assertEqual(json.loads((A/('geometry/blackrazid/'+e['id']+'.json')).read_text()),geometry(JAVA/e['sourceModel']))
    def test_not_using_unregistered_separate_him_parts(self):
        entry=next(e for e in E if e['sourceId']=='Himza')
        self.assertEqual(entry['sourceModel'],'items/armor/him/models/HimCheest.java')
        self.assertFalse(any('Boots' in e['sourceModel'] or 'Helmet' in e['sourceModel'] or 'HimLeg' in e['sourceModel'] for e in E))
    def test_assets_and_original_uv_size(self):
        from PIL import Image
        for e in E:
            d=json.loads((A/('geometry/blackrazid/'+e['id']+'.json')).read_text())
            self.assertTrue(d['parts']);self.assertEqual(d['inflation'],1)
            self.assertIn(d['textureSize'],[[64,64],[128,128]])
            for p in d['parts']:
                self.assertTrue(p['boxes'])
                for parent in p['parents']:
                    self.assertIn(parent,['head','headwear','body','rightarm','leftarm','rightleg','leftleg']+[x['name'] for x in d['parts']])
            with Image.open(A/('textures/blackrazid/armor/'+e['texture']+'.png')) as image:self.assertGreater(image.width,0)
            icon=A/('textures/blackrazid/icons/'+e['icon']+'.png')
            with Image.open(icon) as image:self.assertTrue(image.convert('RGBA').getbbox())
            model=json.loads((A/('models/item/'+e['id']+'.json')).read_text())
            self.assertTrue(model['textures']['layer0'].endswith('/'+e['icon']))
    def test_backpacks_preserve_native_portable_capacities(self):
        self.assertEqual([e['storageSlots'] for e in E if e['mode']=='backpack'],[5,10,15])
        source=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/BlackrazidBackpackItem.java').read_text()
        self.assertIn('super(0)',source) # No second 9/18/27 player-inventory unlock.
        self.assertIn('player.openGui',source)
    def test_filters_and_safe_armor_adaptation(self):
        self.assertEqual({e['sourceId'] for e in E if e['gasMask']},{'Himza','Clean','HeavyHat','NaziHat','gas_mask'})
        self.assertTrue(all(0<=e['defence']<=.8 and e['condition']>0 for e in E))
    def test_armed_shoulder_offsets_and_unused_shapes(self):
        d=json.loads((A/'geometry/blackrazid/blackrazid_easy.json').read_text())
        self.assertNotEqual(d['rest']['Shape59'],d['armed']['Shape59'])
        for e in E:
            d=json.loads((A/('geometry/blackrazid/'+e['id']+'.json')).read_text())
            for p in d['parts']:
                if p.get('unused'):self.assertEqual(p['parents'],[])
                self.assertEqual(len(p['parents']),len(set(p['parents'])))
    def test_translations_and_tab_registration(self):
        for locale in ['ru_ru','en_us']:
            lines=(A/('lang/'+locale+'.lang')).read_text().splitlines()
            for e in E:self.assertEqual(sum(l.startswith('item.metroport.'+e['id']+'.name=') for l in lines),1)
        self.assertIn('setCreativeTab(MetroPort.ARMOR)',(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/BlackrazidContent.java').read_text())
    def test_approved_revolver_not_modified(self):
        poses=json.loads((A/'geometry/gun_poses.json').read_text())
        self.assertEqual(poses['revolver']['firstPerson']['translation'],[-.2,.08,-.25])
        self.assertEqual(poses['revolver']['firstPerson']['scale'],.8)
