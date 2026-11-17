"""Static closure/reproducibility of the mesh scene adapter; not a visual test."""
import json
from pathlib import Path
import re
import unittest

from tools.generate_metromod_menu_scene import generate, OUTPUT, REFERENCE, SHA

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / 'src/main/java/ru/metrocraft/port/client/menu'


class OriginalMenuSceneTest(unittest.TestCase):
    def test_generated_mesh_transforms_are_reproducible(self):
        self.assertEqual(generate(), OUTPUT.read_text(encoding='utf-8'))
        self.assertEqual(generate(), generate())
        self.assertIn(SHA, OUTPUT.read_text())

    def test_all_35_donor_meshes_have_a_scene_draw_not_just_packaged_assets(self):
        scene = OUTPUT.read_text()
        drawn = set(re.findall(r'out\.mesh\("([^"]+)"\)', scene))
        manifest = json.loads((ROOT / 'src/main/resources/assets/metroport/metromod_menu.json').read_text())
        self.assertEqual(35, len(drawn))
        self.assertEqual({row['source_reference'] for row in manifest['models']}, drawn)

    def test_extraction_does_not_carry_donor_network_auth_or_weapon_code(self):
        for source in JAVA.glob('*.java'):
            text = source.read_text()
            for forbidden in ('metro2033server.ru', 'Desktop.', 'java.net', 'URLConnection',
                              'GunHands', 'packv4', 'System.exit', 'getSession()'):
                self.assertNotIn(forbidden, text, source.name)
        self.assertNotIn('net.minecraft', OUTPUT.read_text())
        self.assertNotIn('org.lwjgl', OUTPUT.read_text())

    def test_matrix_pushes_have_failure_unwinding(self):
        scene = OUTPUT.read_text()
        self.assertEqual(scene.count('out.push();'), scene.count('finally { out.pop(); }'))
        self.assertGreater(scene.count('finally { out.pop(); }'), 25)
        self.assertIn('GLAllocation.deleteDisplayLists(group.list)', (JAVA / 'OriginalMenuMeshes.java').read_text())

    def test_cpu_check_is_wired_into_ci_without_a_minecraft_context(self):
        workflow = (ROOT / '.github/workflows/build.yml').read_text()
        self.assertIn('OriginalMenuSceneCheck.java', workflow)
        self.assertIn('java -cp .cache/menu-scene-check ru.metrocraft.port.client.menu.OriginalMenuSceneCheck', workflow)
        self.assertTrue(REFERENCE.is_file())
