"""Source audit and bounded collector checks, not Minecraft gameplay tests."""
import hashlib
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
import zipfile
ROOT=Path(__file__).resolve().parents[1]
def load(name,path):
    spec=importlib.util.spec_from_file_location(name,ROOT/path)
    m=importlib.util.module_from_spec(spec);spec.loader.exec_module(m);return m
A=load('decor_auditor','tools/audit_metromod_decor.py')
C=load('decor_collector','downloads/collect_metromod_decor.py')
G=load('decor_generator','tools/prepare_metromod_decor_collector.py')
R=json.loads((ROOT/'reports/METRO_MOD/functional_decor_audit.json').read_text())
class FunctionalDecorTest(unittest.TestCase):
    def rows(self,kind='Lever'):
        def row(op,**kw):return dict(op=op,values=[],offset=0,**kw)
        return [row('new',**{'class':A.PROFILE}),row('dup'),row('invokespecial',owner=A.PROFILE,name='<init>',desc='()V'),row('astore_2'),row('aload_2'),row('getstatic',owner=A.KINDS,name='kind',desc='L'+A.KINDS+';'),row('invokevirtual',owner=A.PROFILE,name='set',desc='(L'+A.KINDS+';)V'),row('ldc',literal='electro/a.m2033model'),row('ldc_w',literal='Рубильник'),row('aload_2'),row('iconst_0'),row('invokestatic',owner=A.CATALOG,name='add',desc=A.REGISTER),row('return')]
    def test_symbolic_registration_uses_enum_not_display_name(self):
        r=A.catalogue(self.rows(),{'kind':'Lever'});self.assertEqual('Lever',r[0]['kind']);self.assertEqual('Рубильник',r[0]['name_ru'])
        self.assertEqual('None',A.catalogue(self.rows(),{'kind':'None'})[0]['kind'])
    def test_unknown_opcode_is_a_hard_failure_not_partial_success(self):
        with self.assertRaisesRegex(ValueError,'Unsupported opcode'):A.catalogue([{'op':'goto','values':[1],'offset':0}],{})
    def test_unresolved_registration_is_refused(self):
        rows=self.rows();rows[7]['literal']='not-a-model'
        with self.assertRaisesRegex(ValueError,'Unresolved'):A.catalogue(rows,{'kind':'Lever'})
    def test_profile_reuse_keeps_kind_for_following_variant(self):
        rows=self.rows();rows=rows[:-1]+rows[7:-1]+rows[-1:]
        r=A.catalogue(rows,{'kind':'Lever'});self.assertEqual(2,len(r));self.assertEqual(['Lever','Lever'],[e['kind'] for e in r])
    def test_constructor_backreferences_do_not_recurse_forever(self):
        a={'args':[]};b={'args':[a,'part.m2033model']};a['args'].append(b)
        self.assertEqual(['part.m2033model'],list(A.all_strings(a)))
    def test_collector_generation_is_reproducible_and_exactly_scoped(self):
        self.assertEqual(G.generate(R),(ROOT/'downloads/collect_metromod_decor.py').read_text())
        expected={p for e in R['entries'] if e['kind'] in G.KINDS for p in e['direct_model_references']}
        self.assertEqual(expected,set(C.MODEL_REFERENCES));self.assertEqual(190,len(expected))
        self.assertFalse(any('/weapon/' in p or p.startswith(('cars/','veh/','military_veh/','drezina/')) for p in expected))
    def test_counts_are_catalogue_evidence_not_runtime_completion(self):
        self.assertEqual(2397,R['registered_models']);self.assertEqual(188,len(R['entries']));self.assertFalse(R['runtime_port_implemented'])
        self.assertEqual(112,sum(n for k,n in R['kind_counts'].items() if k in G.KINDS))
        for e in R['entries']:self.assertFalse(e['runtime_ported'])
    def test_all_nine_numbered_gates_and_six_levers_are_in_scope(self):
        refs=set(C.MODEL_REFERENCES)
        for i in range(1,10):self.assertIn(f'germo/germo{i}.m2033model',refs)
        levers=[e for e in R['entries'] if e['kind']=='Lever'];self.assertEqual(6,len(levers))
        for e in levers:self.assertIn(e['model'],refs)
    def test_copies_only_selected_models_and_preserves_bytes(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d);src=root/'models.jar';ref=C.MODEL_REFERENCES[0]
            with zipfile.ZipFile(src,'w') as z:z.writestr('assets/source/'+ref,b'original');z.writestr('loginpass.txt',b'PRIVATE');z.writestr('weapon/a.m2033model',b'NO')
            r=C.collect(src,root/'out');self.assertEqual(1,r['matched']);self.assertEqual(189,len(r['missing']))
            record=r['files'][0];self.assertEqual(hashlib.sha256(b'original').hexdigest(),record['sha256'])
            with zipfile.ZipFile(root/'out'/record['part']) as z:self.assertEqual(['assets/source/'+ref],z.namelist())
    def test_ambiguous_suffixes_are_refused(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d);src=root/'models.jar';ref=C.MODEL_REFERENCES[0]
            with zipfile.ZipFile(src,'w') as z:z.writestr('a/'+ref,b'1');z.writestr('b/'+ref,b'2')
            with self.assertRaisesRegex(ValueError,'Ambiguous'):C.collect(src,root/'out')
    def test_no_models_produces_missing_inventory_not_a_fake_port(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d);src=root/'none.jar'
            with zipfile.ZipFile(src,'w') as z:z.writestr('unrelated',b'no')
            r=C.collect(src,root/'out');self.assertEqual(0,r['matched']);self.assertEqual(190,len(r['missing']));self.assertEqual([],r['parts'])
    def test_existing_output_is_not_overwritten(self):
        with tempfile.TemporaryDirectory() as d:
            with self.assertRaises(FileExistsError):C.collect(Path(d)/'irrelevant.jar',Path(d))
    def test_source_hash_mismatch_is_refused_before_parsing(self):
        with tempfile.TemporaryDirectory() as d:
            p=Path(d)/'not-donor.jar';p.write_bytes(b'not-donor')
            with self.assertRaisesRegex(ValueError,'SHA256'):A.audit(p)
