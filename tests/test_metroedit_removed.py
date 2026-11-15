"""Keep the explicitly cancelled editor out of current source and published builds."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
J=ROOT/'src/main/java/ru/metrocraft/port'
class MetroEditRemovedTest(unittest.TestCase):
    def test_editor_implementation_and_preview_renderer_are_removed(self):
        self.assertFalse((J/'editor').exists())
        self.assertFalse((J/'client/EditorPreviewRenderer.java').exists())
    def test_no_editor_dependencies_or_command_in_current_java(self):
        for p in J.rglob('*.java'):
            s=p.read_text()
            for token in ['ru.metrocraft.port.editor','EditorPreviewRenderer','EditorPreviewPacket','"metroedit"','metroport-blueprints']:
                self.assertNotIn(token,s,str(p))
    def test_packet16_is_retired_not_reused(self):
        import re
        s=(J/'MetroPort.java').read_text()
        self.assertIn('Packet ID 16 retired',s)
        self.assertNotRegex(s,r'registerMessage\([^;]*,\s*16\s*,')
    def test_visual_settings_and_other_server_command_remain(self):
        s=(J/'MetroPort.java').read_text()
        self.assertIn('ru.metrocraft.port.client.MetroGuiFactory',s)
        self.assertIn('new ru.metrocraft.port.mechanics.GiveNoDrop()',s)
        self.assertTrue((J/'client/VisualSettingsScreen.java').exists())
        self.assertTrue((J/'metromod/VisualOptions.java').exists())
    def test_release_verifier_forbids_removed_classes(self):
        s=(ROOT/'tools/verify_metro_selected_build.py').read_text()
        self.assertIn("n.startswith('ru/metrocraft/port/editor/')",s)
        self.assertIn('Removed MetroEdit classes remain in JAR',s)
    def test_no_world_or_blueprint_deletion_migration(self):
        s=(J/'MetroPort.java').read_text()
        self.assertNotIn('deleteDirectory',s)
        self.assertNotIn('deleteIfExists',s)
        self.assertNotIn('metroport-blueprints',s)
        self.assertIn('НЕ удаляются',(ROOT/'docs/METRO_SELECTED_INSTALL_RU.txt').read_text())
