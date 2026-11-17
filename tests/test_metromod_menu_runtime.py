"""Runtime wiring/asset provenance checks. Not a live Minecraft renderer test."""
import hashlib
import json
from pathlib import Path
import unittest
from PIL import Image
from tools.import_metromod_menu_effects import prepare, CONTENT_SHA

ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/'src/main/java/ru/metrocraft/port/client/menu'
RES=ROOT/'src/main/resources/assets/metroport'

class OriginalMenuRuntimeTest(unittest.TestCase):
    def test_effect_assets_have_pinned_original_provenance(self):
        report=json.loads((ROOT/'reports/METRO_MOD/menu_effects_verified.json').read_text())
        self.assertEqual(CONTENT_SHA,report['content_sha256'])
        self.assertFalse(report['donor_java_executed']);self.assertFalse(report['in_game_verified'])
        self.assertEqual(3,len(report['files']))
        for row in report['files']:
            path=RES/row['path'];self.assertEqual(row['sha256'],hashlib.sha256(path.read_bytes()).hexdigest())
            if path.suffix=='.png':
                with Image.open(path) as image:self.assertEqual(row['dimensions'],list(image.size));image.verify()
        with self.assertRaisesRegex(ValueError,'SHA256'):prepare(b'not the donor')

    def test_client_only_hook_replaces_only_vanilla_main_menu(self):
        client=(JAVA/'OriginalMenuClient.java').read_text()
        self.assertIn('getClass()==GuiMainMenu.class',client);self.assertIn('mc.world==null',client)
        self.assertIn('metroport.disableOriginalMenu',client);self.assertIn('bypass=true',client)
        self.assertIn('OriginalMenuClient.initialize()', (ROOT/'src/main/java/ru/metrocraft/port/client/ClientProxy.java').read_text())
        common=(ROOT/'src/main/java/ru/metrocraft/port/CommonProxy.java').read_text()
        self.assertNotIn('client.menu',common)

    def test_connect_is_only_an_explicit_action_and_always_loopback(self):
        screen=(JAVA/'OriginalMenuScreen.java').read_text()
        self.assertEqual(1,screen.count('new GuiConnecting('))
        self.assertIn('new GuiConnecting(',screen[screen.index('private void activate()'):])
        self.assertIn('public static final String SERVER="localhost:25565"',(JAVA/'OriginalMenuClient.java').read_text())
        for p in JAVA.glob('*.java'):
            for forbidden in ('metro2033server.ru','java.net','ThreadDownloadImageData','Desktop.getDesktop'):
                self.assertNotIn(forbidden,p.read_text())

    def test_exit_reload_error_and_focus_have_owned_cleanup(self):
        screen=(JAVA/'OriginalMenuScreen.java').read_text();audio=(JAVA/'OriginalMenuAudio.java').read_text()
        self.assertIn('audio.close()',screen);self.assertIn('meshes.close()',screen)
        self.assertIn('closed=true;releaseResources()',screen);self.assertIn('OriginalMenuClient.vanilla(failure)',screen)
        self.assertIn('if(!Display.isActive()) { grab(false)',screen)
        self.assertIn('active.releaseResources()', (JAVA/'OriginalMenuClient.java').read_text())
        self.assertNotIn('.stopSounds()',audio);self.assertIn('handler.stopSound(sound)',audio)
        self.assertNotIn('getMusicTicker()',screen)
        client=(JAVA/'OriginalMenuClient.java').read_text()
        self.assertIn('currentScreen instanceof OriginalMenuScreen',client)
        self.assertIn('"minecraft:music.menu".equals',client)
        self.assertIn('event.setResultSound(null)',client)
        self.assertIn('KEY_F8',screen)

    def test_gl_scope_restores_matrices_cached_flags_textures_and_shader(self):
        scope=(JAVA/'MenuGlScope.java').read_text()
        for token in ('GL_PROJECTION','GL_MODELVIEW','glPopAttrib','set(caps[i], enabled[i])',
                      'GlStateManager.bindTexture(texture)','GlStateManager.setActiveTexture(activeTexture)',
                      'GlStateManager.clearColor(clearColor.get(0)', 'OpenGlHelper.glUseProgram(program)',
                      'GlStateBuffers.colorQuery()'):
            self.assertIn(token,scope)
        screen=(JAVA/'OriginalMenuScreen.java').read_text()
        self.assertEqual(2,screen.count('try(MenuGlScope scope=new MenuGlScope())'))
        self.assertIn('GLU.gluPerspective(70',screen)

    def test_callbacks_draw_lights_lettering_drops_and_glow_inside_original_transforms(self):
        geometry=(JAVA/'OriginalMenuGeometry.java').read_text();scene=(JAVA/'OriginalMenuScene.java').read_text()
        self.assertEqual(1,geometry.count('out.dashboard();'))
        self.assertLess(geometry.index('out.mesh("menu/part2.m2033model")'),geometry.index('out.dashboard();'))
        self.assertLess(scene.index('out.cameraLights()'),scene.index('out.rotate(180, 0, 0, 1)'))
        self.assertIn('out.particles();',scene);self.assertIn('out.glows();',scene)
        overlay=(JAVA/'OriginalMenuOverlay.java').read_text()
        for coordinate in ('.295f,-.278f,.01f','.085f,-.265f,0','-.129f,-.294f,0','-.329f,-.266f,0'):
            self.assertIn(coordinate,overlay)

    def test_all_audio_events_registered_and_release_stays_honest(self):
        source=(ROOT/'src/main/java/ru/metrocraft/port/MetroPort.java').read_text()
        for name in ('menu_carnoise','menu_intro_loop','menu_railnoise','menu_wheel_knock_noise','regina_engine_idle','switch2_light'):
            self.assertIn('"'+name+'"',source)
        doc=(ROOT/'docs/METRO_MENU_DEV32_RU.txt').read_text()
        self.assertIn('не полная сборка',doc);self.assertIn('не подтверждено',doc)
        self.assertIn('F8',doc);self.assertIn('GuiOptions',doc)
        workflow=(ROOT/'.github/workflows/build.yml').read_text()
        self.assertIn('python -m tools.verify_metromod_menu_build',workflow)
        self.assertIn('--prerelease',workflow)

    def test_dev33_welcome_is_generated_and_names_minetro(self):
        shared=json.loads((ROOT/'tools/mcmod-localization.json').read_text())
        for locale,text in [('ru_ru','Добро пожаловать на MinETRO'),('en_us','Welcome to MinETRO')]:
            self.assertEqual(text,shared[locale]['metroport.menu.welcome'])
            lang=(RES/('lang/'+locale+'.lang')).read_text()
            for key in ('welcome','singleplayer','test_hint'):
                self.assertIn('metroport.menu.'+key+'='+shared[locale]['metroport.menu.'+key],lang)

    def test_singleplayer_is_separate_from_donor_hotspots_and_requires_user_action(self):
        screen=(JAVA/'OriginalMenuScreen.java').read_text()
        self.assertIn('singleplayerButton=new GuiButton(100,',screen)
        self.assertIn('buttonList.clear()',screen)
        self.assertIn('singleplayerButton.drawButton(',screen)
        self.assertIn('singleplayerButton.mousePressed(mc,x,y)',screen)
        self.assertIn('new GuiWorldSelection(this)',screen)
        self.assertIn('KEY_F6',screen)
        self.assertLess(screen.index('KEY_F6'),screen.index('if(state.lightR<=.1f)return;'))
        mouse=screen[screen.index('protected void mouseClicked'):screen.index('public void handleMouseInput')]
        self.assertLess(mouse.index('if(releasedByUser)'),mouse.index('singleplayerButton.mousePressed'))
        self.assertIn('activationDelay=2;return;',mouse)
        self.assertEqual(2,screen.count('openSingleplayer();'))
        overlay=(JAVA/'OriginalMenuOverlay.java').read_text()
        self.assertNotIn('singleplayer',overlay)  # no invented fifth donor switch
        self.assertNotIn('launchIntegratedServer',screen)

    def test_website_is_fixed_https_and_uses_vanilla_explicit_link_handling(self):
        screen=(JAVA/'OriginalMenuScreen.java').read_text()
        self.assertEqual(1,screen.count('handleComponentClick('))
        self.assertIn('handleComponentClick(',screen[screen.index('case 2:'):screen.index('case 3:')])
        self.assertIn('ClickEvent.Action.OPEN_URL',screen)
        self.assertIn('WEBSITE="https://minetro-beta.vercel.app/"',(JAVA/'OriginalMenuClient.java').read_text())
        self.assertNotIn('site_unset',screen)
