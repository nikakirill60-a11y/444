"""Test geometry validation and the dependency-only local collector; not a game run."""
import importlib.util
import hashlib
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch
import zipfile
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
from tools import inspect_metromod_decor_models as I
from tools import decor_finish_template as F
from tools.prepare_decor_finish import generate
ROOT=Path(__file__).resolve().parents[1]
OBJ=b'mtllib a.mtl\nv 0 0 0\nv 1 0 0\nv 0 1 0\nvt 0 0\nvt 1 0\nvt 0 1\nvn 0 0 1\nusemtl Metal\nf 1/1/1 2/2/1 3/3/1\n'
PATH=I.PREFIX+'door/a.m2033model'
def envelope(data):
    key=bytes(range(16));return key+AES.new(key,AES.MODE_ECB).encrypt(pad(data,16))
class GeometryInspectionTest(unittest.TestCase):
    def test_valid_obj_counts_bounds_and_actual_material_path(self):
        m=I.obj_metadata(OBJ,PATH);self.assertEqual({'v':3,'vt':3,'vn':1,'f':1},m['counts']);self.assertEqual(1,m['triangles']);self.assertEqual([0.,0.,0.],m['bounds']['min']);self.assertEqual([I.PREFIX+'door/a.m2033gl4'],m['material_libraries'])
    def test_negative_indices_are_validated_relative_to_current_vertices(self):
        self.assertEqual(1,I.obj_metadata(OBJ.replace(b'f 1/1/1 2/2/1 3/3/1',b'f -3/-3/-1 -2/-2/-1 -1/-1/-1'),PATH)['triangles'])
        with self.assertRaisesRegex(ValueError,'relative'):I.obj_metadata(OBJ.replace(b'f 1/1/1',b'f -4/1/1'),PATH)
    def test_nonfinite_extreme_and_invalid_faces_are_refused(self):
        for b in [OBJ.replace(b'v 0 0 0',b'v nan 0 0'),OBJ.replace(b'v 0 0 0',b'v 1e20 0 0'),OBJ.replace(b'f 1/1/1',b'f 0/1/1'),OBJ.replace(b'f 1/1/1',b'f 4/1/1')]:
            with self.assertRaises(ValueError):I.obj_metadata(b,PATH)
    def test_material_traversal_absolute_and_backslash_paths_are_refused(self):
        for ref in [b'../outside.mtl',b'/outside.mtl',b'c:\\outside.mtl',b'x//a.mtl']:
            with self.assertRaises(ValueError):I.obj_metadata(OBJ.replace(b'a.mtl',ref),PATH)
    def test_unknown_commands_are_not_executed(self):
        with self.assertRaisesRegex(ValueError,'Unsupported'):I.obj_metadata(OBJ+b'call command.py\n',PATH)
    def test_decode_and_provenance_roundtrip(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d);p=root/'models.zip';b=envelope(OBJ)
            with zipfile.ZipFile(p,'w') as z:z.writestr(PATH,b)
            inv={'source_sha256':'original','parts':[{'name':p.name,'bytes':p.stat().st_size,'sha256':I.sha(p.read_bytes())}],
                 'files':[{'part':p.name,'path':PATH,'reference':'door/a.m2033model','bytes':len(b),'sha256':I.sha(b)}],'missing':[]}
            inventory=root/'inventory.json';inventory.write_text(json.dumps(inv))
            r=I.inspect(p,inventory,root/'report.json',root/'decoded.zip');self.assertEqual(1,r['validated_models']);self.assertFalse(r['runtime_port_implemented'])
            with zipfile.ZipFile(root/'decoded.zip') as z:self.assertEqual(OBJ,z.read(I.PREFIX+'door/a.obj'))
            with self.assertRaises(FileExistsError):I.inspect(p,inventory,root/'report.json')
    def test_modified_archive_hash_is_refused(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d);p=root/'models.zip';p.write_bytes(b'no');inv=root/'inv.json';inv.write_text(json.dumps({'parts':[{'name':p.name,'bytes':2,'sha256':'wrong'}]}))
            with self.assertRaisesRegex(ValueError,'hash'):I.inspect(p,inv,root/'report.json')
    def test_uploaded_evidence_matches_187_models_and_corrects_suffixes(self):
        r=json.loads((ROOT/'reports/METRO_MOD/decor_geometry_verified.json').read_text());self.assertEqual(187,len(r['models']));self.assertEqual(187,len(r['material_paths']));self.assertEqual(187389,r['geometry_totals']['f']);self.assertFalse(r['runtime_port_implemented']);self.assertEqual(4,len(r['dynamic_suffix_correction']['resolved_models']))
class CollectorCryptoTest(unittest.TestCase):
    def test_fips197_aes128_vector(self):
        key=bytes.fromhex('000102030405060708090a0b0c0d0e0f');cipher=bytes.fromhex('69c4e0d86a7b0430d8cdb78070b4c55a');plain=bytes.fromhex('00112233445566778899aabbccddeeff');self.assertEqual(plain,F.decrypt_blocks(key,cipher))
    def test_metadata_decoder_matches_reference_cipher_varied_lengths(self):
        for size in [0,1,15,16,17,127,2048]:
            data=bytes(i%256 for i in range(size));self.assertEqual(data,F.decode_envelope(envelope(data)))
    def test_bad_key_block_envelope_and_padding_are_refused(self):
        for key,data in [(b'a',bytes(16)),(bytes(16),b'a')]:
            with self.assertRaises(ValueError):F.decrypt_blocks(key,data)
        for data in [b'',bytes(17),bytes(range(16))+AES.new(bytes(range(16)),AES.MODE_ECB).encrypt(bytes(16))]:
            with self.assertRaises(ValueError):F.decode_envelope(data)
    def test_materials_resolve_only_basenames_and_first_extension_like_donor(self):
        text='newmtl Metal\nmap_Kd C:\\export\\metal.a.dds\n';self.assertEqual({'metal'},F.texture_roots(F.material_text(envelope(text.encode()))))
        self.assertEqual({'metal'},F.texture_roots(text))
        for text in ['map_Kd foo.png','newmtl a\nmap_Kd -s 1 1 1 foo.png']:
            with self.assertRaises(ValueError):F.texture_roots(text)
    def test_texture_resolution_and_optional_maps_are_explicit(self):
        names=[F.TEXTURE_PREFIX+x for x in ['metal.256.m2033texture','metal.512.m2033texture','metal.1024.m2033texture','metal_n.256.m2033texture','metal_e.1024.m2033texture','unrelated.512.m2033texture']]
        paths,missing,choices=F.choose_textures(dict.fromkeys(names),{'metal','absent'},512)
        self.assertEqual(['absent'],missing);self.assertEqual(3,len(paths));self.assertIn(F.TEXTURE_PREFIX+'metal.512.m2033texture',paths);self.assertTrue(any(c['above_requested_size'] for c in choices))
    def test_standalone_script_is_reproducible_without_external_crypto_dependency(self):
        r=json.loads((ROOT/'reports/METRO_MOD/decor_geometry_verified.json').read_text());s=(ROOT/'downloads/finishdecor.py').read_text();self.assertEqual(s,generate(r))
        for t in ['subprocess','from Crypto','import requests','os.system','exec(']:self.assertNotIn(t,s)
class CollectorIntegrationTest(unittest.TestCase):
    def test_combined_collection_includes_materials_textures_and_missing_part_header(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d);mat=F.MODEL_PREFIX+'door/a.m2033gl4';extra=F.MODEL_PREFIX+'door/x_add1.m2033model';extramat=F.MODEL_PREFIX+'door/x_add1.m2033gl4';tex=F.TEXTURE_PREFIX+'metal.512.m2033texture'
            with zipfile.ZipFile(root/'Metro2033Models.jar','w') as z:
                z.writestr(mat,envelope(b'newmtl Metal\nmap_Kd C:\\art\\metal.dds\n'));z.writestr(extra,envelope(OBJ.replace(b'a.mtl',b'x_add1.mtl')));z.writestr(extramat,envelope(b'newmtl Untextured\n'));z.writestr('loginpass.txt','PRIVATE')
            with zipfile.ZipFile(root/'Metro2033Textures0.jar','w') as z:z.writestr(tex,b'original encrypted texture');z.writestr(F.TEXTURE_PREFIX+'unrelated.512.m2033texture',b'NO')
            with patch.object(F,'MODEL_MATERIALS',(mat,)),patch.object(F,'EXTRA_MODELS',('door/x_add1.m2033model','missing.m2033model')):
                r=F.collect(root,expected_model_sha=None,limit=1600)
            self.assertEqual(['missing.m2033model'],r['missing_models']);self.assertFalse(r['missing_materials']);self.assertFalse(r['material_errors']);self.assertEqual(4,len(r['files']));self.assertFalse(r['runtime_port_implemented'])
            for p in r['parts']:
                path=root/'metro_decor_finish_parts'/p['name'];self.assertLessEqual(path.stat().st_size,1600);self.assertEqual(p['sha256'],F.sha_file(path))
            with self.assertRaises(FileExistsError):F.collect(root,expected_model_sha=None)
    def test_duplicate_selected_resource_is_refused(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d);mat=F.MODEL_PREFIX+'door/a.m2033gl4'
            for name in ['Metro2033Models.jar','Metro2033Textures0.jar']:
                with zipfile.ZipFile(root/name,'w') as z:z.writestr(mat,b'newmtl a\n')
            with patch.object(F,'MODEL_MATERIALS',(mat,)),patch.object(F,'EXTRA_MODELS',()):
                with self.assertRaisesRegex(ValueError,'duplicate'):F.collect(root,expected_model_sha=None)
            self.assertFalse((root/'metro_decor_finish_parts').exists())
    def test_wrong_original_source_hash_and_invalid_resolution_are_refused(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d);(root/'Metro2033Models.jar').write_bytes(b'not original')
            with self.assertRaisesRegex(ValueError,'SHA256'):F.collect(root,expected_model_sha='wrong')
            with self.assertRaises(ValueError):F.collect(root,size=33,expected_model_sha=None)
    def test_unsafe_archive_resource_path_is_not_allowed(self):
        for name in ['../x','assets/x//a','assets/x/../a','C:\\x','/a']:
            with self.assertRaises(ValueError):F.safe_path(name)
