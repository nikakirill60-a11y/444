import hashlib
import json
import unittest
from collections import Counter
from pathlib import Path

from tools.import_metromod_germo_collision import OUTPUT, REPORT, STATE, WORLD, TICK, CLOSE, PUT, REMOVE, body, component, validate
from tools.metromod_germo_reader import GermoReader, VECTOR

ROOT=Path(__file__).resolve().parents[1]
FIXTURES=ROOT/'src/test/resources/metromod_germo_callbacks.tsv'


class GermoCollisionTests(unittest.TestCase):
    def test_complete_profile_coverage_and_provenance(self):
        report=json.loads(REPORT.read_text())
        profiles=[]
        for name in ('metromod_gates.json','metromod_interactive.json'):
            profiles+=json.loads((ROOT/'src/main/resources/assets/metroport'/name).read_text())
        self.assertEqual({p['id'] for p in profiles if p.get('kind','Germo')=='Germo'}, {p['id'] for p in report['profiles']})
        self.assertEqual(17,report['profile_count'])
        for p in report['profiles']:
            for field in ('behavior','component'):
                self.assertEqual(64,len(report['class_sha256'][p[field]+'.class']))
                self.assertEqual(64,len(report['decompiled_sha256'][p[field]+'.java']))
        self.assertEqual(hashlib.sha256(OUTPUT.read_bytes()).hexdigest(),report['generated_java_sha256'])
        self.assertTrue(report['runtime_connected'])
        self.assertFalse(report['minecraft_gameplay_verified'])

    def test_bytecode_fixture_coverage(self):
        rows=[line.split('\t') for line in FIXTURES.read_text().splitlines() if not line.startswith('#')]
        counts=Counter((r[0],r[1],r[2]) for r in rows)
        self.assertEqual(2448,len(rows))
        self.assertEqual({18},set(counts.values()))
        self.assertEqual(17*2*4,len(counts))
        self.assertEqual({'open','closed'},{r[1] for r in rows})
        for row in rows:self.assertRegex(row[-1],r'^[0-9a-f]{64}$')

    def test_method_extraction_balances_braces(self):
        text='public void '+TICK+'('+STATE+' var1, '+WORLD+' var2, int var3, int var4, int var5) { if(true) { int a=0; } }'
        self.assertEqual(' if(true) { int a=0; } ',body(text,TICK,[STATE,WORLD,'int','int','int']))
        with self.assertRaisesRegex(ValueError,'Missing/ambiguous'):body(text+text,TICK,[STATE,WORLD,'int','int','int'])

    def test_coordinate_rename_does_not_corrupt_longer_locals(self):
        self.assertEqual('x + var30 + y + var40 + z + var50 + var15',component('var3 + var30 + var4 + var40 + var5 + var50 + var15'))

    def test_unknown_source_symbols_rejected(self):
        with self.assertRaisesRegex(ValueError,'Unmapped'):validate('var1.unmappedWorldWrite();')
        with self.assertRaisesRegex(ValueError,'Unmapped'):validate('jIlIlIlIlIlIlIlIlIlIlIlIlIlIlIlI.call();')

    def test_oracle_refuses_arbitrary_calls_and_allocations(self):
        reader=GermoReader(None,'null/reviewed')
        with self.assertRaisesRegex(ValueError,'Unexpected callback'):reader.call('java/lang/Runtime','exec','()V',None,[])
        with self.assertRaisesRegex(ValueError,'Unexpected Math'):reader.call('java/lang/Math','random','()D',None,[])

    def test_oracle_float_math_round_and_vector_copy(self):
        reader=GermoReader(None,'null/reviewed')
        for value,expected in ((-.5,0),(-1.5,-1),(.5,1),(1.5,2)):
            self.assertEqual(expected,reader.call('java/lang/Math','round','(F)I',None,[value]))
        source=[1.,2.,3.];copy=[]
        reader.call(VECTOR,'<init>','(Lorg/lwjgl/util/vector/ReadableVector3f;)V',copy,[source])
        copy[0]=9;self.assertEqual([1.,2.,3.],source)

    def test_ci_checks_engine_before_forge_dependency_resolution(self):
        workflow=(ROOT/'.github/workflows/build.yml').read_text()
        self.assertLess(workflow.index('DonorGermoStandaloneCheck'),workflow.index('Build and reobfuscate for Forge'))

    def test_runtime_metadata_is_catalogue_not_voxel_data(self):
        report=json.loads(REPORT.read_text())
        metadata=json.loads((ROOT/'src/main/resources/assets/metroport/metromod_germo_collision.json').read_text())
        self.assertEqual(17,len(metadata))
        for row in report['profiles']:
            args=row['catalogue_args'];entry=metadata[row['id']]
            self.assertEqual(args[2],entry['offset'])
            self.assertEqual(args[3] if isinstance(args[3],list) else [1,1,1],entry['scale'])
            self.assertEqual({'offset','scale'},set(entry))

    def test_runtime_path_uses_snapshot_not_render_voxels(self):
        source=(ROOT/'src/main/java/ru/metrocraft/port/metromod/decor/OriginalGateTile.java').read_text()
        self.assertIn('if(profile.donorGermo!=null) {updateGermo();return;}',source)
        self.assertIn('DonorGermoCollision.save(germo())',source)
        self.assertIn('DonorGermoCollision.restore(profile.id,facing',source)
        self.assertIn('DonorGermoMotion candidate=current.next(signal);',source)
        self.assertLess(source.index('boolean stop=!canFitGermo'),source.index('germo=candidate;'))
        self.assertIn('profile.donorGermo.boxes(profile,germo(),pos,facing)',source)
