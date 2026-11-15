"""Original asset provenance and integration. GL output still requires in-game verification."""
import hashlib
import json
from pathlib import Path
import subprocess
import sys
import unittest
import zipfile
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'
J=ROOT/'src/main/java/ru/metrocraft/port'

class MetroSelectedTest(unittest.TestCase):
    def test_all_14_selected_assets_match_the_decoded_original_bytes(self):
        rows=json.loads((A/'metromod_selected_sources.json').read_text());self.assertEqual(14,len(rows))
        with zipfile.ZipFile(ROOT/'downloads/METRO_MOD_Content_decoded.zip') as z:
            for row in rows:
                data=(A/row['target']).read_bytes();self.assertEqual(data,z.read(row['source']))
                self.assertEqual(hashlib.sha256(data).hexdigest(),row['sha256']);self.assertNotIn('/weapon/',row['source'])
    def test_medical_materials_are_not_disguised_healing_items(self):
        rows=json.loads((A/'metromod_medical_materials.json').read_text());audit=json.loads((ROOT/'reports/METRO_MOD/dev21_medicine_audit.json').read_text())
        self.assertEqual(['bint','shpric','medspirt'],[r['sourceId'] for r in rows]);self.assertEqual([8,24,8],[r['stackSize'] for r in rows])
        for row in rows:
            self.assertIsNone(row['healingEffect']);self.assertEqual('material',row['kind'])
            self.assertEqual(row['stackSize'],audit['items'][row['sourceId']]['stack_size']);self.assertEqual([],audit['items'][row['sourceId']]['external_field_references'])
        src=(J/'metromod/MedicalMaterialItem.java').read_text();self.assertNotIn('onItemRightClick',src);self.assertNotIn('onItemUseFinish',src);self.assertNotIn('.heal(',src)
    def test_every_new_sound_has_an_independent_registered_event(self):
        sounds=json.loads((A/'sounds.json').read_text());src=(J/'metromod/MetroAudio.java').read_text()
        rows=[r for r in json.loads((A/'metromod_selected_sources.json').read_text()) if r['target'].startswith('sounds/')];self.assertEqual(8,len(rows))
        for row in rows:
            key=Path(row['target']).stem;self.assertEqual({'sounds':['metroport:'+key]},sounds[key]);self.assertIn('"'+key+'"',src)
    def test_original_alarm_stays_under_existing_detector_and_episode_guards(self):
        src=(J/'client/FilterWatchClient.java').read_text();self.assertIn('alarm.update(ticks, detector(displayedWatch(mc.player)))',src)
        self.assertIn('MetroAudio.alarm',src);self.assertNotIn('BLOCK_NOTE_PLING',src)
    def test_overlay_is_pre_hud_first_person_and_never_loads_global_shaders(self):
        src=(J/'client/MetroOverlays.java').read_text();self.assertIn('RenderGameOverlayEvent.Pre',src);self.assertIn('thirdPersonView==0',src)
        self.assertIn('OverlayRules.mask',src);self.assertIn('NvgEffects.visualActive()',src);self.assertIn('finally{',src)
        self.assertIn('GlStateBuffers.colorQuery()',src);self.assertIn('GlStateManager.bindTexture(binding)',src)
        self.assertIn('GL11.GL_ZERO,GL11.GL_SRC_COLOR',src)
        for forbidden in ['loadShader(','.gammaSetting','RenderSpecificHandEvent','RenderPlayerEvent','setItemStackToSlot','setHealth(']:self.assertNotIn(forbidden,src)
    def test_configs_can_disable_each_new_visual_component(self):
        src=(J/'client/MetroOverlays.java').read_text()
        for key in ['maskOverlay','nvgTextureOverlay','nvgNoise']:self.assertIn('"'+key+'"',src)
        self.assertIn('MetroOverlays.configure(config,tint)',(J/'client/NvgEffects.java').read_text())
    def test_localization_and_models_for_three_medical_materials(self):
        for row in json.loads((A/'metromod_medical_materials.json').read_text()):
            model=json.loads((A/'models/item'/ (row['id']+'.json')).read_text());self.assertEqual('metroport:metromod/items/'+row['sourceId'],model['textures']['layer0'])
            for locale in ['ru_ru','en_us']:
                lines=(A/'lang'/ (locale+'.lang')).read_text().splitlines();self.assertEqual(1,sum(l.startswith('item.metroport.'+row['id']+'.name=') for l in lines))
    def test_selected_importer_is_byte_reproducible(self):
        paths=[A/r['target'] for r in json.loads((A/'metromod_selected_sources.json').read_text())]
        paths += [A/'sounds.json',A/'metromod_selected_sources.json',A/'metromod_medical_materials.json',A/'lang/ru_ru.lang',A/'lang/en_us.lang']
        before=[p.read_bytes() for p in paths]
        subprocess.run([sys.executable,'tools/import_metromod_selected.py'],cwd=ROOT,check=True)
        self.assertEqual(before,[p.read_bytes() for p in paths])
