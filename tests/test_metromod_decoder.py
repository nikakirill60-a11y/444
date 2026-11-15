"""Known donor fixtures, fail-closed extraction, and complete decoded output integrity."""
import hashlib
import io
import json
from pathlib import Path
import stat
import sys
import tempfile
import unittest
import zipfile
sys.path.insert(0,str(Path(__file__).resolve().parents[1]/'tools'))
from decode_metromod_content import decode,validate,safe_name,extract,EXTENSIONS
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
from import_metromod_icons import run as import_icons

ROOT=Path(__file__).resolve().parents[1]
F=ROOT/'tests/fixtures/metromod'
PREFIX='assets/metro2033server/'


def envelope(data):
    key=bytes(range(16));return key+AES.new(key,AES.MODE_ECB).encrypt(pad(data,16))


class MetroDecoderTest(unittest.TestCase):
    def test_known_original_png_decodes_and_loads(self):
        data=decode((F/'konservi.m2033texture').read_bytes());self.assertEqual({'format':'PNG','width':32,'height':32,'mode':'RGBA'},validate(data,'.png'))
        self.assertEqual(data,(ROOT/'src/main/resources/assets/metroport/textures/metromod/items/konservi.png').read_bytes())
    def test_known_original_sound_decodes_fully_as_vorbis(self):
        details=validate(decode((F/'geiger_1.m2033sound').read_bytes()),'.ogg')
        self.assertEqual('OGG/VORBIS',details['format']);self.assertGreater(details['frames'],0);self.assertEqual(48000,details['samplerate'])
    def test_known_original_shader_is_readable_source(self):
        data=decode((F/'nightVision.m2033shaderfs').read_bytes());self.assertEqual('GLSL source',validate(data,'.frag')['format']);self.assertIn(b'#version 330 core',data)
    def test_invalid_envelope_lengths_fail(self):
        for data in [b'',b'0'*16,b'0'*31,b'0'*33]:
            with self.assertRaises(ValueError):decode(data)
    def test_bad_padding_is_not_silently_accepted(self):
        with self.assertRaises(ValueError):decode(bytes(range(16))+AES.new(bytes(range(16)),AES.MODE_ECB).encrypt(b'X'*16))
    def test_plaintext_disguised_as_png_is_rejected(self):
        with self.assertRaises(ValueError):validate(b'not an image','.png')
    def test_plaintext_disguised_as_audio_is_rejected(self):
        with self.assertRaises(ValueError):validate(b'not a sound','.ogg')
    def test_shader_must_be_utf8_source_with_main(self):
        for data in [b'\xff\xfe',b'just text',b'void main\x00']:
            with self.assertRaises((ValueError,UnicodeDecodeError)):validate(data,'.frag')
    def test_path_traversal_and_unexpected_namespace_fail(self):
        for name in ['../bad','/tmp/bad',PREFIX+'../bad',PREFIX+'a\\b','assets/other/test',PREFIX+'a//b',PREFIX+'a\x00b']:
            with self.assertRaises(ValueError):safe_name(name)
    def test_symlink_entry_is_rejected(self):
        with tempfile.TemporaryDirectory() as d:
            p=Path(d);info=zipfile.ZipInfo(PREFIX+'link');info.external_attr=(stat.S_IFLNK|0o777)<<16
            with zipfile.ZipFile(p/'in.zip','w') as z:z.writestr(info,'/tmp/secret')
            with self.assertRaises(ValueError):extract(p/'in.zip',p/'out.zip',p/'report.json')
            self.assertFalse((p/'out.zip.partial').exists());self.assertFalse((p/'out.zip').exists())
    def test_colliding_decoded_and_plain_names_are_rejected(self):
        with tempfile.TemporaryDirectory() as d:
            p=Path(d)
            with zipfile.ZipFile(p/'in.zip','w') as z:
                z.writestr(PREFIX+'x.png',b'plain');z.writestr(PREFIX+'x.m2033texture',(F/'konservi.m2033texture').read_bytes())
            with self.assertRaises(ValueError):extract(p/'in.zip',p/'out.zip',p/'report.json')
    def test_complete_fixture_extraction_is_deterministic_and_skips_only_known_marker(self):
        with tempfile.TemporaryDirectory() as d:
            p=Path(d)
            with zipfile.ZipFile(p/'in.zip','w') as z:
                z.writestr(PREFIX+'textures/weapon',b'');z.writestr(PREFIX+'lang/ru_RU.lang','example=пример')
                z.writestr(PREFIX+'x.m2033texture',(F/'konservi.m2033texture').read_bytes())
            a=extract(p/'in.zip',p/'a.zip',p/'a.json');b=extract(p/'in.zip',p/'b.zip',p/'b.json')
            self.assertEqual(a['output_sha256'],b['output_sha256']);self.assertEqual(2,a['files']);self.assertEqual([PREFIX+'textures/weapon'],a['skipped_empty_markers'])
            with zipfile.ZipFile(p/'a.zip') as z:self.assertEqual('example=пример',z.read(PREFIX+'lang/ru_RU.lang').decode())
    def test_output_must_not_overwrite_source(self):
        with self.assertRaises(ValueError):extract('same.zip','same.zip','report.json')
    def test_entire_published_extraction_has_matching_hashes_and_counts(self):
        report=json.loads((ROOT/'reports/METRO_MOD/content_decoded_manifest.json').read_text());path=ROOT/'downloads'/report['output']
        self.assertEqual(report['output_sha256'],hashlib.sha256(path.read_bytes()).hexdigest())
        self.assertEqual({'PNG':1297,'OGG/VORBIS':115,'GLSL source':27,'copied unchanged':4},report['counts'])
        self.assertEqual(1443,report['files'])
        with zipfile.ZipFile(path) as z:
            self.assertIsNone(z.testzip());self.assertEqual(1443,len(z.namelist()))
            for record in report['records']:self.assertEqual(record['sha256'],hashlib.sha256(z.read(record['output'])).hexdigest())
    def test_selected_icon_import_is_reproducible(self):
        assets=ROOT/'src/main/resources/assets/metroport';paths=[assets/'metromod_consumables.json']
        paths+=list((assets/'textures/metromod/items').glob('*.png'));paths+=list((assets/'models/item').glob('metromod_*.json'))
        before={str(p):p.read_bytes() for p in paths};import_icons();self.assertEqual(before,{str(p):p.read_bytes() for p in paths})
