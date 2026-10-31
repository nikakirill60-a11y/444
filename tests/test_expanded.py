"""Migration integrity checks. Geometry/data checks, NOT screenshots or game simulation."""
import hashlib
import json
import math
from pathlib import Path
import re
import subprocess
import sys
import unittest

ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/metroport'
CATALOGUE=json.loads((ASSETS/'geometry/catalogue.json').read_text())
ENTRIES=json.loads((ASSETS/'content.json').read_text())


class ExpandedTests(unittest.TestCase):
    def test_all_57_models_have_geometry_and_original_atlas(self):
        self.assertEqual(len(CATALOGUE),57)
        self.assertEqual(len(list((ASSETS/'models/item').glob('*.json'))),175)
        for key,entry in CATALOGUE.items():
            self.assertTrue((ASSETS/('geometry/'+key+'.json')).is_file())
            self.assertTrue((ASSETS/entry['texture'].split(':',1)[1]).read_bytes().startswith(b'\x89PNG\r\n\x1a\n'))
            self.assertGreater(entry['scale'],0)

    def test_geometry_preserves_every_original_box(self):
        total=0
        for key,entry in CATALOGUE.items():
            source=(ROOT/'tools/legacy-reference/com/flansmod/client/model/MetroCraft'/entry['sourceModel']).read_text()
            expected=len(re.findall(r'\.(?:addShapeBox|func_78790_a)\(',source))
            geometry=json.loads((ASSETS/('geometry/'+key+'.json')).read_text())
            actual=sum(op['op'] in ('box','shape') for part in geometry['parts'] for op in part['ops'])
            self.assertEqual(actual,expected,key)
            total+=actual
        self.assertEqual(total,12213)

    def test_geometry_parameter_shapes(self):
        sizes={'point':3,'box':7,'shape':31,'rx':1,'ry':1,'rz':1,'px':1,'py':1,'pz':1}
        for key in CATALOGUE:
            data=json.loads((ASSETS/('geometry/'+key+'.json')).read_text())
            self.assertGreater(len(data['parts']),0)
            for part in data['parts']:
                self.assertEqual(len(part['uv']),4)
                self.assertGreater(part['uv'][2],0)
                self.assertGreater(part['uv'][3],0)
                for op in part['ops']:
                    self.assertIn(op['op'],sizes)
                    self.assertEqual(len(op['values']),sizes[op['op']],(key,op))
                    self.assertTrue(all(math.isfinite(v) for v in op['values']))
            for transform in data['transforms']:
                self.assertIn(transform['op'],('flipAll','translateAll','translateWeapon','translateShoot'))
                self.assertEqual(len(transform['values']),0 if transform['op']=='flipAll' else 3)

    def test_58_blocks_match_original_registrations(self):
        text=(ROOT/'tools/legacy-reference/ru/metrocraft/stilian/Main.java').read_text()
        self.assertEqual(len(re.findall(r'new BlockTut\(',text)),58)
        for index in range(58):
            key='block'+str(index)
            state=json.loads((ASSETS/('blockstates/'+key+'.json')).read_text())
            self.assertEqual(state['variants']['normal']['model'],'metroport:'+key)
            item=json.loads((ASSETS/('models/item/'+key+'.json')).read_text())
            self.assertEqual(item['parent'],'metroport:block/'+key)
            self.assertTrue((ASSETS/('textures/blocks/'+key+'.png')).is_file())

    def test_decor_tab_separates_blocks_from_supplies(self):
        main = (ROOT/'src/main/java/ru/metrocraft/port/MetroPort.java').read_text()
        expanded = (ROOT/'src/main/java/ru/metrocraft/port/ExpandedContent.java').read_text()
        gate = (ROOT/'src/main/java/ru/metrocraft/port/gates/GateBlock.java').read_text()
        self.assertIn('DECOR = tab("decor", "block0", Items.PAINTING)', main)
        self.assertIn('setCreativeTab(MetroPort.DECOR).setRegistryName(MetroPort.ID, "block" + i)', expanded)
        self.assertIn('item instanceof ItemBlock && !id.equals("radiation")', expanded)
        self.assertIn('? MetroPort.DECOR : MetroPort.THINGS', expanded)
        self.assertIn('setCreativeTab(MetroPort.DECOR)', gate)
        self.assertIn('setTranslationKey("metroport.radiation").setCreativeTab(MetroPort.THINGS)', expanded)

    def test_attachment_whitelists_and_modifiers(self):
        attachments={e['id']:e for e in ENTRIES if e['kind']=='attachment'}
        for e in ENTRIES:
            if e['kind']=='gun':
                for key in e['allowedAttachments']:
                    self.assertIn(key,attachments)
            if e['kind']=='attachment':
                self.assertIn(e['attachmentSlot'],('scope','barrel','stock','grip','generic'))
                for key in ('damageMultiplier','spreadMultiplier','reloadMultiplier','zoom'):
                    self.assertGreater(e[key],0)
        ak=next(e for e in ENTRIES if e['id']=='ak74')
        self.assertIn('pso',ak['allowedAttachments'])

    def test_all_10_entities_have_original_models_and_textures(self):
        for name,model in {'kris':'Kris','murzik':'Murzik','tuzic':'Tuzic','jery':'Jery','trup1':'Trup1',
                           'trup2':'Trup2','trup3':'Trup3','trup4':'Trup4','komp':'Komp','grib':'ModelGrib'}.items():
            code=(ROOT/('src/main/java/ru/metrocraft/port/entity/'+('Grib' if name=='grib' else name[0].upper()+name[1:])+'Entity.java')).read_text()
            self.assertNotRegex(code,r'func_\d+_|field_\d+_')
            self.assertTrue((ASSETS/('textures/entity/'+name+'.png')).is_file())
            self.assertTrue((ROOT/('src/main/java/ru/metrocraft/port/client/mobs/'+model+'.java')).is_file())

    def test_all_3_backpack_meshes_and_resources(self):
        for size in ('small','medium','big'):
            key=size+'rucksack'
            mesh=(ASSETS/('models/legacy/backpacks/'+key+'.obj')).read_text()
            vertices=[line for line in mesh.splitlines() if line.startswith('v ')]
            texture=[line for line in mesh.splitlines() if line.startswith('vt ')]
            faces=[line for line in mesh.splitlines() if line.startswith('f ')]
            self.assertGreater(len(vertices),0)
            self.assertGreater(len(faces),0)
            for face in faces:
                for field in face.split()[1:]:
                    v,t=map(int,field.split('/')[:2])
                    self.assertTrue(1<=v<=len(vertices))
                    self.assertTrue(1<=t<=len(texture))
            self.assertTrue((ASSETS/('textures/backpacks/'+key+'.png')).is_file())
            self.assertTrue((ASSETS/('models/item/'+key+'.json')).is_file())

    def test_all_expanded_resources_are_lowercase(self):
        for path in ASSETS.rglob('*'):
            relative=path.relative_to(ASSETS).as_posix()
            self.assertEqual(relative,relative.lower())
            self.assertNotIn(' ',relative)

    def test_extended_imports_are_reproducible(self):
        def digest():
            return {str(p.relative_to(ROOT)):hashlib.sha256(p.read_bytes()).hexdigest()
                    for root in (ROOT/'src',ROOT/'docs') for p in root.rglob('*') if p.is_file()}
        before=digest()
        for tool in ('import_legacy.py','import_entities.py','import_gates.py','create_gun_poses.py'):
            subprocess.run([sys.executable,str(ROOT/'tools'/tool)],cwd=ROOT,check=True,capture_output=True)
        self.assertEqual(before,digest())


if __name__=='__main__': unittest.main()
