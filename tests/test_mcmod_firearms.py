import gzip,hashlib,json,subprocess,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/metroport'
class FirearmTests(unittest.TestCase):
    def test_every_recovered_asset_matches_original_bytes_after_decompression(self):
        manifest=json.loads((ROOT/'tools/mcmod-firearm-sources.json').read_text());self.assertEqual(84,len(manifest))
        for rel,source in manifest.items():
            b=(A/rel).read_bytes();b=gzip.decompress(b) if source['gzip'] else b
            self.assertEqual(source['sourceBytes'],len(b),rel);self.assertEqual(source['sourceSha256'],hashlib.sha256(b).hexdigest(),rel)
    def test_registered_guns_ammo_and_sounds_are_complete(self):
        entries=sum([json.loads((A/n).read_text()) for n in ['mcmod_guns.json','mcmod_firearms.json','mcmod_support.json']],[])
        guns=[d for d in entries if d['kind']=='gun'];self.assertEqual(15,len(guns));self.assertEqual(7,len([d for d in entries if d['kind']=='ammo']))
        ammo={d['id'] for d in entries if d['kind']=='ammo'};sounds=json.loads((A/'sounds.json').read_text())
        for d in guns:
            self.assertIn(d['ammo'],ammo);self.assertIn(d['shootSound'],sounds);self.assertIn(d['reloadSound'],sounds)
            self.assertEqual(20,d['bulletSpeed']);self.assertGreater(d['durability'],0)
        self.assertEqual(3,len([d for d in guns if d.get('mcmodAir')]))
    def test_firearm_generator_is_reproducible(self):
        paths=[A/'mcmod_firearms.json',A/'mcmod_guns.json',A/'sounds.json',A/'lang/ru_ru.lang',A/'lang/en_us.lang',ROOT/'docs/mcmod-firearms-report.json']
        before=[p.read_bytes() for p in paths];subprocess.run([sys.executable,'tools/import_mcmod_firearms.py'],cwd=ROOT,check=True,capture_output=True);self.assertEqual(before,[p.read_bytes() for p in paths])
