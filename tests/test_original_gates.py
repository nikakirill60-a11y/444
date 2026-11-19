"""Packaged original gates, dependencies, collision fidelity and live integration guards."""
import gzip
import hashlib
import json
from pathlib import Path
import unittest
from tools.import_metromod_gates import collision, overlap
from tools.inspect_metromod_decor_models import obj_metadata

ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources/assets/metroport'
JAVA=ROOT/'src/main/java/ru/metrocraft/port'
PROFILES=json.loads((RES/'metromod_gates.json').read_text())


class OriginalGatesTest(unittest.TestCase):
    def test_exact_six_new_ids_no_replacement_old_ids(self):
        self.assertEqual([1,2,4,5,6,9],[int(p['id'].rsplit('_',1)[1]) for p in PROFILES])
        self.assertEqual(12,sum(len(p['parts']) for p in PROFILES))
        self.assertNotIn('ru.metrocraft.port.gates.Gates.initialize();',(JAVA/'MetroPort.java').read_text())
        self.assertIn('OriginalGates.initialize();',(JAVA/'MetroPort.java').read_text())

    def test_all_mesh_materials_textures_and_item_models_exist(self):
        for p in PROFILES:
            self.assertTrue((RES/('models/item/'+p['id']+'.json')).exists())
            for part in p['parts']:
                mesh=RES/part['mesh'].split(':',1)[1]
                data=gzip.decompress(mesh.read_bytes())
                meta=obj_metadata(data,'assets/metro2033server/models/obj/germo/a.m2033model')
                self.assertEqual(set(meta['usemtl']),set(part['materials']))
                for tex in part['materials'].values():self.assertTrue((RES/tex.split(':',1)[1]).read_bytes().startswith(b'\x89PNG'))

    def test_geometry_matches_verified_donor_bytes_after_text_normalization(self):
        given=json.loads((ROOT/'reports/METRO_MOD/decor_geometry_verified.json').read_text())
        models={Path(m['decoded_path']).stem:m for m in given['models']}
        for p in PROFILES:
            for part in p['parts']:
                mesh=RES/part['mesh'].split(':',1)[1];data=gzip.decompress(mesh.read_bytes())
                original=models[mesh.name.split('.')[0]]
                if original['encoding']=='utf-8':self.assertEqual(original['decoded_sha256'],hashlib.sha256(data).hexdigest())

    def test_collision_fits_mesh_with_at_most_one_quarter_block_margin(self):
        for p in PROFILES:
            for part in p['parts']:
                mesh=RES/part['mesh'].split(':',1)[1]
                m=obj_metadata(gzip.decompress(mesh.read_bytes()),'assets/metro2033server/models/obj/germo/a.m2033model')
                for i in range(3):
                    lo=min(b[i] for b in part['boxes']);hi=max(b[i+3] for b in part['boxes'])
                    self.assertLessEqual(lo,m['bounds']['min'][i]);self.assertLessEqual(m['bounds']['min'][i]-lo,.251)
                    self.assertGreaterEqual(hi,m['bounds']['max'][i]);self.assertLessEqual(hi-m['bounds']['max'][i],.251)

    def test_actual_registration_server_code_and_client_reload_are_connected(self):
        reg=(JAVA/'metromod/decor/OriginalGates.java').read_text()
        self.assertIn('@Mod.EventBusSubscriber',reg);self.assertIn('new OriginalGateItem(block)',reg)
        proxy=(JAVA/'client/ClientProxy.java').read_text();self.assertIn('OriginalGateTile.class',proxy);self.assertIn('OriginalGateRenderer.reload()',proxy)
        self.assertIn('void originalGateModels',(JAVA/'ClientEvents.java').read_text())
        server='\n'.join(p.read_text() for p in (JAVA/'metromod/decor').glob('*.java'))
        self.assertNotIn('net.minecraft.client',server);self.assertNotIn('org.lwjgl',server)

    def test_language_generation_is_idempotent_for_new_blocks(self):
        from tools.mcmod_languages import update
        update();before={p:p.read_bytes() for p in (RES/'lang').glob('*.lang')};update()
        for p,data in before.items():
            self.assertEqual(data,p.read_bytes())
            for profile in PROFILES:self.assertEqual(1,p.read_text().count('tile.metroport.'+profile['id']+'.name='))

    def test_triangle_voxelization_does_not_fill_triangle_bounding_box_void(self):
        tri=[(0,0,0),(2,0,0),(0,2,0)]
        self.assertTrue(overlap(tri,(1,1,0)));self.assertFalse(overlap(tri,(7,7,0)))
        text='v 0 0 0\nv 2 0 0\nv 0 2 0\nf 1 2 3\n'
        boxes=collision(text)
        self.assertFalse(any(b[0]<1.9<b[3] and b[1]<1.9<b[4] for b in boxes))

    def test_import_provenance_is_only_allowlisted_gate_resources(self):
        evidence=json.loads((RES/'metromod_gates_provenance.json').read_text())
        self.assertFalse(evidence['all_functional_decor_ported'])
        self.assertEqual([3,7,8],evidence['remaining_germo_models'])
        self.assertTrue(all('/germo/' in n or '/textures/obj/' in n for n in evidence['source_files']))
        scope=json.loads((RES/'reduced_scope.json').read_text())
        self.assertTrue(all(p['id'] in scope['removedIds'] for p in PROFILES))


if __name__=='__main__':unittest.main()
