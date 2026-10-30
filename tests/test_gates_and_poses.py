"""Asset and transform-math checks. These are NOT Minecraft gameplay tests."""
import json
import math
from pathlib import Path
import re
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/metroport'


def rotate_y(p, angle):
    x, y, z = p
    s, c = math.sin(math.radians(angle)), math.cos(math.radians(angle))
    return (c*x + s*z, y, -s*x + c*z)


class GateAndPoseTests(unittest.TestCase):
    def test_all_16_guns_have_dedicated_finite_poses(self):
        catalogue = json.loads((ASSETS / 'geometry/catalogue.json').read_text())
        poses = json.loads((ASSETS / 'geometry/gun_poses.json').read_text())
        self.assertEqual(set(poses), {k for k, v in catalogue.items() if v['kind'] == 'gun'})
        for name, profile in poses.items():
            self.assertEqual(len(profile['grip']), 3, name)
            for key in ['firstPerson', 'thirdPerson', 'ground', 'fixed']:
                pose = profile[key]
                self.assertEqual(len(pose['translation']), 3)
                self.assertEqual(len(pose['rotation']), 3)
                self.assertTrue(0 < pose['scale'] <= 1)
                self.assertTrue(all(math.isfinite(v) for v in pose['translation'] + pose['rotation'] + profile['grip']))

    def test_muzzles_point_away_from_camera_in_both_hands(self):
        poses = json.loads((ASSETS / 'geometry/gun_poses.json').read_text())
        for name, profile in poses.items():
            angle = profile['firstPerson']['rotation'][1]
            right = rotate_y((1, 0, 0), angle)
            left = rotate_y((1, 0, 0), 180 - angle)
            self.assertLess(right[2], -.95, name)
            self.assertLess(left[2], -.95, name)
            self.assertAlmostEqual(right[0], -left[0])
            # Actual Forge conjugation/anchor matrices are also covered by JVM tests.

    def test_third_person_respects_vanilla_arm_parent_rotation(self):
        poses = json.loads((ASSETS / 'geometry/gun_poses.json').read_text())
        for profile in poses.values():
            self.assertEqual(profile['thirdPerson']['rotation'], [0, 0, 90])
        # +X -> +Y in the item pose; vanilla Y180 leaves +Y unchanged;
        # vanilla X-90 then turns it into player-forward -Z.
        arm_forward = (0, math.cos(math.radians(-90)), math.sin(math.radians(-90)))
        self.assertAlmostEqual(arm_forward[2], -1)

    def test_gate_meshes_and_atlases_are_original_bytes(self):
        with zipfile.ZipFile(ROOT / '(Metro) Flans-v1.1.jar') as original:
            for name in ['Germa', 'GermaRamka']:
                self.assertEqual((ASSETS / 'models/legacy' / (name.lower()+'.obj')).read_bytes(),
                                 original.read('assets/sub_mod_techne_block/models/'+name+'.obj'))
            for name in ['model_techne', 'ModelGermo1', 'ModelGermoLeft', 'Germa']:
                self.assertEqual((ASSETS / 'textures/gates' / (name.lower()+'.png')).read_bytes(),
                                 original.read('assets/sub_mod_techne_block/textures/blocks/'+name+'.png'))

    def test_gate_models_preserve_all_boxes_without_recursive_adapter(self):
        for name in ['ModelGermo', 'ModelGermo1', 'ModelGermoLeft', 'ModelGermoRamka']:
            source = (ROOT / 'tools/legacy-reference/blocks/models' / (name+'.java')).read_text()
            port = (ROOT / 'src/main/java/ru/metrocraft/port/client/gates' / (name+'.java')).read_text()
            self.assertEqual(source.count('.func_78789_a('), port.count('.addBox('), name)
            self.assertNotIn('this.setRotationAngles(', port)
            self.assertNotRegex(port, r'\b(?:func_|field_)\d')
            self.assertIn('public void render()', port)

    def test_six_variants_have_items_and_translations(self):
        source = (ROOT / 'src/main/java/ru/metrocraft/port/gates/GateType.java').read_text()
        names = re.findall(r'\("(germo[^"]*)"', source)
        self.assertEqual(len(names), 6)
        for name in names:
            self.assertTrue((ASSETS / 'models/item' / (name+'.json')).is_file())
            for locale in ['ru_ru', 'en_us']:
                self.assertIn('tile.metroport.'+name+'.name=', (ASSETS / 'lang' / (locale+'.lang')).read_text())
        self.assertFalse((ASSETS / 'models/item/gate_part.json').exists())

    def test_swept_gate_bounds_fit_reserved_cells(self):
        # Same native coordinates/pivots as GateRenderer and GateTile.
        for obj in [False, True]:
            x0, x1 = (-.631234, 0) if obj else (-.56, .56)
            z0, z1 = (-.667083, 3.388717) if obj else (-2.5, 2.5)
            pivot, wshift, dshift = (0, -1.638717, .426667) if obj else (-2.5, 0, 0)
            for angle in range(91):
                s, c = math.sin(math.radians(angle)), math.cos(math.radians(angle))
                for x in [x0, x1]:
                    for z in [z0, z1]:
                        w = pivot + (z-pivot)*c - x*s + wshift
                        d = (z-pivot)*s + x*c + dshift
                        self.assertTrue(-3.5 <= w <= 3.5, (obj, angle, w))
                        self.assertTrue(-1.5 <= d <= 5.5, (obj, angle, d))
        for p in [0, .1, .5, .99, 1]:
            self.assertTrue(0 <= 4*p and 4+4*p <= 8)

    def test_orientation_matches_each_horizontal_facing(self):
        # Native +X points toward the gate normal after Y(90-angle) * Z180.
        # Native +Z spans width. This catches the old sign bug for EAST/WEST.
        for angle, facing, right in [(0,(0,0,1),(1,0,0)), (90,(-1,0,0),(0,0,1)),
                                     (180,(0,0,-1),(-1,0,0)), (270,(1,0,0),(0,0,-1))]:
            normal = rotate_y((-1,0,0), 90-angle)
            width = rotate_y((0,0,1), 90-angle)
            for actual, expected in zip(normal+width, facing+right):
                self.assertAlmostEqual(actual, expected)


if __name__ == '__main__':
    unittest.main()
