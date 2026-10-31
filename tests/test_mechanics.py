"""Imported mechanical parameters and packaged resource checks, not game simulations."""
import io
import json
from pathlib import Path
import unittest
import re
import zipfile
from tools.import_content import parse,decode
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/metroport'

class MechanicsTests(unittest.TestCase):
    def test_parameters_match_original_definitions(self):
        with zipfile.ZipFile(ROOT/'Flan-20261030T152711Z-1-001.zip') as outer:
            with zipfile.ZipFile(io.BytesIO(outer.read('Flan/MetroCraft -Pack-1.7.10-4.10.0.jar'))) as pack:
                for item in json.loads((ASSETS/'content.json').read_text()):
                    source=parse(decode(pack.read(item['source'])))
                    if item['kind']=='gun':
                        self.assertEqual(item['bulletSpeed'],float(source['BulletSpeed']))
                        self.assertEqual(item['recoil'],float(source['Recoil']))
                    if item['kind']=='armor':
                        self.assertEqual(item['knockbackModifier'],float(source.get('KnockbackModifier','.2')))
                        self.assertEqual(item['moveSpeedModifier'],float(source.get('MoveSpeedModifier','1')))
                    if item['kind']=='ammo':self.assertEqual(item['fallSpeed'],float(source['FallSpeed']))
    def test_native_rig_preserved_for_all_guns(self):
        catalogue=json.loads((ASSETS/'geometry/catalogue.json').read_text())
        for name,info in catalogue.items():
            rig=json.loads((ASSETS/('geometry/'+name+'.json')).read_text())['rig']
            if info['kind']=='gun':
                self.assertEqual(rig['animationType'],'BOTTOM_CLIP')
                source=(ROOT/'tools/legacy-reference/com/flansmod/client/model/MetroCraft'/info['sourceModel']).read_text()
                native=re.findall(r'this\.downReload = (-?[0-9.]+)f',source)[-1]
                self.assertEqual(rig['downReload'],float(native))
                self.assertEqual(len(rig['barrelAttachPoint']),3)
    def test_packaged_original_scope_textures(self):
        with zipfile.ZipFile(ROOT/'Flan-20261030T152711Z-1-001.zip') as outer:
            with zipfile.ZipFile(io.BytesIO(outer.read('Flan/MetroCraft -Pack-1.7.10-4.10.0.jar'))) as pack:
                for name in ['aim1','svd']:
                    self.assertEqual((ASSETS/('textures/scopes/'+name+'.png')).read_bytes(),pack.read('assets/flansmod/gui/'+name+'.png'))
    def test_only_medkit_has_a_recipe_with_external_fiberglass(self):
        with zipfile.ZipFile(ROOT/'Flan-20261030T152711Z-1-001.zip') as outer:
            with zipfile.ZipFile(io.BytesIO(outer.read('Flan/MetroCraft -Pack-1.7.10-4.10.0.jar'))) as pack:
                for item in json.loads((ASSETS/'content.json').read_text()):
                    text=decode(pack.read(item['source']))
                    recipes=[line.strip() for line in text.splitlines() if line.strip().startswith(('Recipe ','ShapelessRecipe '))]
                    if item['id']=='medkit':self.assertEqual(recipes,['Recipe F fiberglass P potion.8229'])
                    else:self.assertEqual(recipes,[])
