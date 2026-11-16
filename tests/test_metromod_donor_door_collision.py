import gzip
import json
import unittest
from pathlib import Path
from tools.import_metromod_door_collision import extrema, RULES

ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources/assets/metroport'

class DonorDoorCollisionTest(unittest.TestCase):
    def test_every_door_has_reviewed_collision_and_exact_asset_extrema(self):
        profiles=json.loads((RES/'metromod_interactive.json').read_text())
        collision=json.loads((RES/'metromod_door_collision.json').read_text())
        doors=[p for p in profiles if p['kind']=='Door']
        self.assertEqual(27,len(doors));self.assertEqual({p['id'] for p in doors},set(collision))
        for p in doors:
            rule=collision[p['id']];union,grow=RULES[rule['behavior'].split('/')[-1]]
            parts=p['parts'] if union else p['parts'][:1]
            expected=[extrema(gzip.decompress((RES/part['mesh'].split(':',1)[1]).read_bytes()).decode()) for part in parts]
            self.assertEqual(expected,rule['models'],p['id'])
            self.assertEqual([1,0,1] if grow else [0,0,0],rule['grow'])
    def test_provider_inputs_are_not_quarter_block_voxels_or_render_poses(self):
        rules=json.loads((RES/'metromod_door_collision.json').read_text())
        self.assertTrue(any(abs(v*4-round(v*4))>1e-5 for r in rules.values() for b in r['models'] for v in b))
        self.assertTrue(all('frames' not in r and 'boxes' not in r for r in rules.values()))
    def test_limits_are_explicit_not_claimed_as_full_server_or_germo_port(self):
        report=json.loads((ROOT/'reports/METRO_MOD/DONOR_DOOR_COLLISION.json').read_text())
        self.assertFalse(report['map_specific_custom_aabbs_available'])
        self.assertFalse(report['germo_block_mode_ported'])
        self.assertFalse(report['minecraft_gameplay_verified'])
        self.assertEqual(14,len(report['class_sha256']))
