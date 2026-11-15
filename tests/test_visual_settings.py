"""Build-safety checks complement executed Java transaction tests; not an in-game certification."""
from pathlib import Path
import json
import unittest
ROOT=Path(__file__).resolve().parents[1]
J=ROOT/'src/main/java/ru/metrocraft/port'
A=ROOT/'src/main/resources/assets/metroport'
class VisualSettingsTest(unittest.TestCase):
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
