"""Build-safety checks complement executed Java transaction tests; not an in-game certification."""
from pathlib import Path
import json
import unittest
ROOT=Path(__file__).resolve().parents[1]
J=ROOT/'src/main/java/ru/metrocraft/port'
A=ROOT/'src/main/resources/assets/metroport'
class EditorSafetyTest(unittest.TestCase):
    def test_editor_registered_server_side_and_reset_at_shutdown(self):
        s=(J/'MetroPort.java').read_text();self.assertIn('registerServerCommand(new ru.metrocraft.port.editor.MetroEditCommand())',s);self.assertIn('FMLServerStoppedEvent',s)
    def test_no_remote_copy_or_client_imports_in_server_editor(self):
        for p in (J/'editor').glob('*.java'):
            s=p.read_text()
            for forbidden in ['java.net.','net.minecraft.client.','Runtime.getRuntime','ProcessBuilder','getChunkFromChunkCoords']:
                self.assertNotIn(forbidden,s,p.name)
            if p.name=='EditorPreviewPacket.java':
                self.assertEqual(['import io.netty.buffer.ByteBuf;'],[l for l in s.splitlines() if l.startswith('import io.netty.')])
            else:self.assertNotIn('io.netty.',s,p.name)
    def test_command_requires_creative_operator_and_loaded_local_area(self):
        s=(J/'editor/MetroEditCommand.java').read_text()
        for guard in ['canUseCommand(2','capabilities.isCreativeMode','world.isBlockLoaded','server.isBlockProtected','world.getTileEntity']:
            self.assertIn(guard.replace('world.','w.') if guard=='world.isBlockLoaded' else guard,s)
        self.assertIn('BreakEvent',s);self.assertIn('PlaceEvent',s)
    def test_visual_factory_is_only_named_in_common_entrypoint(self):
        s=(J/'MetroPort.java').read_text();self.assertIn('guiFactory = "ru.metrocraft.port.client.MetroGuiFactory"',s)
        self.assertNotIn('new VisualSettingsScreen',s)
    def test_each_visual_locale_key_occurs_once(self):
        for locale in ['ru_ru','en_us']:
            lines=(A/'lang'/ (locale+'.lang')).read_text().splitlines()
            keys=[l.split('=',1)[0] for l in lines if l.startswith('metroport.visual.')]
            self.assertEqual(8,len(keys));self.assertEqual(len(keys),len(set(keys)))
    def test_visual_gui_cancel_and_escape_do_not_write_config(self):
        s=(J/'client/VisualSettingsScreen.java').read_text();self.assertEqual(1,s.count('config.save()'))
        self.assertIn('case 8:mc.displayGuiScreen(parent);return;',s)
        self.assertIn('if(key==1)mc.displayGuiScreen(parent)',s)
    def test_no_new_firearm_or_pack_changes_in_editor(self):
        for p in (J/'editor').glob('*.java'):
            self.assertNotIn('registerMessage',p.read_text());self.assertNotIn('EntityItem(',p.read_text())
