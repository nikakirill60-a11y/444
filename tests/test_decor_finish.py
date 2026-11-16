"""Finish collection verification tests; no Minecraft/OpenGL claims."""
import io
import json
from pathlib import Path
import tempfile
import unittest
import zipfile
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
from PIL import Image
from tools import decor_finish_template as F
from tools import inspect_metromod_decor_finish as V


def envelope(data, key=bytes(range(16))):
    return key+AES.new(key,AES.MODE_ECB).encrypt(pad(data,16))


class MaterialTest(unittest.TestCase):
    def test_encrypted_key_starting_hash_is_not_plaintext(self):
        text=b'newmtl Metal\nmap_Kd textures/metal.dds\n'
        key=b'#\x08\x9dQ\xeb\x1d\x13\xf8\x06\x02\x16W\x0c\xa1\xa8\x11'
        self.assertEqual(text.decode(), F.material_text(envelope(text,key)))

    def test_plain_utf8_and_cp1251_remain_supported(self):
        for encoding in ('utf-8','cp1251'):
            text='newmtl Металл\nmap_Kd C:\\textures\\metal.dds\n'
            self.assertEqual(text,F.material_text(text.encode(encoding)))

    def test_binary_header_is_not_accepted_as_plaintext(self):
        with self.assertRaises(ValueError):F.material_text(b'#\x01newmtl x\n')

    def test_bindings_preserve_labels_and_donor_basename_rule(self):
        self.assertEqual({'Металл':'a','Plain':None},V.material_bindings('newmtl Металл\nmap_Kd C:\\textures\\a.extra.dds\nnewmtl Plain\n'))

    def test_duplicate_labels_and_options_are_rejected(self):
        for text in ('newmtl x\nnewmtl x\n','newmtl x\nmap_Kd -s 1 1 x.dds\n'):
            with self.assertRaises(ValueError):V.material_bindings(text)


class FinishInspectionTest(unittest.TestCase):
    def fixture(self, root, missing=False):
        self.source=root/'finish.zip'; self.inventory=root/'inventory.json'
        self.geometry=root/'geometry.json'; self.report=root/'report.json'; self.output=root/'decoded.zip'
        m=V.PREFIX+'door/a.m2033gl4'; t=V.TEXTURES+'metal.512.m2033texture'
        buffer=io.BytesIO();Image.new('RGBA',(2,2),(1,2,3,255)).save(buffer,format='DDS')
        self.files={m:envelope(b'newmtl Metal\nmap_Kd metal.dds\n')}
        if not missing:self.files[t]=envelope(buffer.getvalue())
        with zipfile.ZipFile(self.source,'w') as z:
            for name,data in self.files.items():z.writestr(name,data)
        self.given={'parts':[{'name':self.source.name,'bytes':self.source.stat().st_size,'sha256':V.sha(self.source.read_bytes())}],
                    'files':[{'part':self.source.name,'path':n,'bytes':len(b),'sha256':V.sha(b)} for n,b in self.files.items()],
                    'missing_models':['structure/missing.m2033model'],'material_errors':[]}
        self.inventory.write_text(json.dumps(self.given))
        self.geometry.write_text(json.dumps({'models':[{'source_path':V.PREFIX+'door/a.m2033model','material_libraries':[m],'usemtl':['Metal']}]}))

    def inspect(self):
        return V.inspect(self.source,self.inventory,self.geometry,self.report,self.output)

    def test_hashes_dds_decode_png_conversion_and_closure(self):
        with tempfile.TemporaryDirectory() as d:
            self.fixture(Path(d));r=self.inspect()
            self.assertEqual(2,r['validated_files']);self.assertEqual([],r['missing_diffuse_textures'])
            self.assertEqual([],r['missing_material_labels']);self.assertFalse(r['runtime_port_implemented'])
            self.assertFalse(r['complete_pack_verified']);self.assertEqual(1,len(r['original_missing_models']))
            with zipfile.ZipFile(self.output) as z:
                png=z.read(V.TEXTURES+'metal.512.png')
                with Image.open(io.BytesIO(png)) as im:self.assertEqual((1,2,3,255),im.getpixel((0,0)))

    def test_member_hash_mismatch_rejected(self):
        with tempfile.TemporaryDirectory() as d:
            self.fixture(Path(d));self.given['files'][0]['sha256']='0'*64
            self.inventory.write_text(json.dumps(self.given))
            with self.assertRaisesRegex(ValueError,'Member hash'):self.inspect()
            self.assertFalse(self.report.exists())

    def test_part_hash_mismatch_rejected(self):
        with tempfile.TemporaryDirectory() as d:
            self.fixture(Path(d));self.given['parts'][0]['sha256']='0'*64
            self.inventory.write_text(json.dumps(self.given))
            with self.assertRaisesRegex(ValueError,'Part hash'):self.inspect()

    def test_missing_diffuse_is_reported_not_hidden(self):
        with tempfile.TemporaryDirectory() as d:
            self.fixture(Path(d),missing=True);r=self.inspect()
            self.assertEqual('metal',r['missing_diffuse_textures'][0]['root'])

    def test_missing_label_is_reported(self):
        with tempfile.TemporaryDirectory() as d:
            self.fixture(Path(d));g=json.loads(self.geometry.read_text());g['models'][0]['usemtl']=['Other']
            self.geometry.write_text(json.dumps(g));r=self.inspect()
            self.assertEqual('Other',r['missing_material_labels'][0]['label'])

    def test_duplicate_inventory_and_output_alias_rejected(self):
        with tempfile.TemporaryDirectory() as d:
            self.fixture(Path(d));self.given['files'].append(self.given['files'][0]);self.inventory.write_text(json.dumps(self.given))
            with self.assertRaisesRegex(ValueError,'Duplicate'):self.inspect()
            with self.assertRaisesRegex(ValueError,'distinct'):
                V.inspect(self.source,self.inventory,self.geometry,self.source)

    def test_existing_output_is_not_overwritten(self):
        with tempfile.TemporaryDirectory() as d:
            self.fixture(Path(d));self.report.write_text('keep')
            with self.assertRaises(FileExistsError):self.inspect()
            self.assertEqual('keep',self.report.read_text())


if __name__=='__main__':unittest.main()
