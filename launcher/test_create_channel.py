import hashlib,json,tempfile,unittest,zipfile
from pathlib import Path
from create_channel import create
class ChannelAuthorTests(unittest.TestCase):
    def pack(self,path,files):
        with zipfile.ZipFile(path,'w') as z:
            for name,data in files:z.writestr(name,data)
    def test_complete_zip_with_custom_root_pins_all_mod_hashes(self):
        with tempfile.TemporaryDirectory() as root:
            p=Path(root)/'full.zip';self.pack(p,[('pack/.minecraft/mods/metrocraft-port-test.jar',b'minetro'),('pack/.minecraft/mods/mwf.jar',b'mwf'),('pack/.minecraft/ModularWarfare/packv4.json',b'custom hands')])
            result=create(p,'https://example.test/full.zip','test-01','pack/.minecraft')
            self.assertEqual(2,len(result['requiredFiles']));self.assertEqual(p.stat().st_size,result['archive']['bytes'])
            self.assertEqual(hashlib.sha256(p.read_bytes()).hexdigest(),result['archive']['sha256'])
            self.assertEqual('1.12.2',result['minecraft']);self.assertEqual('11.1.1',result['prismVersion'])
    def test_one_mod_is_not_called_a_full_pack(self):
        with tempfile.TemporaryDirectory() as root:
            p=Path(root)/'one.zip';self.pack(p,[('mods/metrocraft-port.jar',b'mod')])
            with self.assertRaises(ValueError):create(p,'https://example.test/one.zip','test-01')
    def test_unsafe_url_version_and_root_rejected(self):
        for url,ver,root in [('http://example.test/a','test-01',''),('https://u:p@example.test/a','test-01',''),('https://example.test/a','../bad',''),('https://example.test/a','test-01','../root')]:
            with self.assertRaises(ValueError):create(Path('unused'),url,ver,root)
    def test_default_channel_has_no_fabricated_pack(self):
        m=json.loads((Path(__file__).parent/'channel.test.json').read_text());self.assertIsNone(m['archive']);self.assertEqual([],m['requiredFiles'])
if __name__=='__main__':unittest.main()
