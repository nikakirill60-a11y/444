"""Source provenance / wiring checks, not substitutes for a real GL or multiplayer smoke test."""
import hashlib
import json
from pathlib import Path
import struct
import unittest
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/metroport'
JAVA=ROOT/'src/main/java/ru/metrocraft/port'

class ActionsAndPatchesTests(unittest.TestCase):
    def test_held_tools_are_byte_exact_source_models_and_atlases(self):
        manifest=json.loads((ROOT/'tools/mcmod-reference/source-files.json').read_text())['files']
        for key in ['machete','aptech']:
            for name,runtime in [('model.obj','models/legacy/mcmod_tools/'+key+'.obj'),('texture.png','textures/mcmod/tools/'+key+'.png')]:
                original=(ROOT/('tools/mcmod-reference/assets/tools/'+key+'/'+name)).read_bytes()
                self.assertEqual(original,(ASSETS/runtime).read_bytes())
                record=manifest['assets/tools/'+key+'/'+name]
                self.assertEqual(hashlib.sha256(original).hexdigest(),record['sha256']);self.assertTrue(record['driveId'])

    def test_four_patches_use_real_atlas_emblems_with_explicit_derivation(self):
        catalog=json.loads((ASSETS/'faction_patches.json').read_text())
        self.assertIn('New cosmetic patch items',catalog['provenance'])
        expected={'satanists':'satanarmor','contrabandists':'kontrabody','legion':'legionarmor','mgu':'nvgdbody'}
        self.assertEqual(set(expected),{e['faction'] for e in catalog['patches']})
        for e in catalog['patches']:
            self.assertEqual('textures/legacy/'+expected[e['faction']]+'.png',e['sourceAtlas'])
            self.assertEqual(hashlib.sha256((ASSETS/e['sourceAtlas']).read_bytes()).hexdigest(),e['sourceSha256'])
            image=(ASSETS/('textures/patches/'+e['faction']+'.png')).read_bytes()
            self.assertEqual(hashlib.sha256(image).hexdigest(),e['patchSha256']);self.assertEqual((128,128),struct.unpack('>II',image[16:24]))
            model=json.loads((ASSETS/('models/item/'+e['id']+'.json')).read_text())
            self.assertEqual('metroport:patches/'+e['faction'],model['textures']['layer0'])
            for locale,field in [('ru_ru','nameRu'),('en_us','nameEn')]:
                self.assertIn('item.metroport.'+e['id']+'.name='+e[field]+'\n',(ASSETS/('lang/'+locale+'.lang')).read_text())

    def test_animations_are_client_only_and_do_not_send_extra_hits(self):
        renderer=(JAVA/'client/McmodToolRenderer.java').read_text()
        self.assertIn('value=Side.CLIENT',renderer);self.assertIn('RenderSpecificHandEvent',renderer)
        self.assertIn('player.getItemInUseCount()',renderer);self.assertIn('event.getSwingProgress()',renderer)
        self.assertIn('GunHandPose.usesSupport',renderer);self.assertIn('player.getLocationSkin()',renderer)
        for side_effect in ['sendToServer','attackEntity','setHealth','damageItem','spend(']:self.assertNotIn(side_effect,renderer)
        events=(JAVA/'ClientEvents.java').read_text();self.assertIn('McmodToolRenderer.INSTANCE',events);self.assertIn('FactionPatchLayer(playerRenderer)',events)

    def test_medkit_heals_only_in_native_finish_callback(self):
        source=(JAVA/'mcmod/McmodMedkitItem.java').read_text()
        click=source.split('onItemRightClick(',1)[1].split('@Override',1)[0]
        self.assertIn('player.setActiveHand(hand)',click);self.assertNotIn('setHealth',click);self.assertNotIn('spend(',click)
        finish=source.split('onItemUseFinish(',1)[1].split('@Override',1)[0]
        self.assertIn('!world.isRemote',finish);self.assertIn('canUse(',finish);self.assertIn('spend(',finish)

    def test_gate_has_no_manual_source_and_samples_live_redstone_before_moving(self):
        source=(JAVA/'gates/GateTile.java').read_text()
        self.assertNotIn('manualTarget',source);self.assertNotIn('void toggle',source)
        self.assertNotIn('getFloat("Manual")',source);self.assertNotIn('getFloat("Target")',source)
        update=source.split('public void update()',1)[1].split('public boolean blockedAt',1)[0]
        self.assertIn('world.isBlockPowered(cell)',update);self.assertIn('world.isBlockPowered(pos)',update)
        self.assertLess(update.index('setTarget(redstoneTarget(signal))'),update.index('float next ='))
        self.assertIn('!world.isBlockLoaded(cell)',update);self.assertIn('blockedAt(next)',update)
        self.assertNotIn('tile.toggle()', (JAVA/'gates/GateBlock.java').read_text())
