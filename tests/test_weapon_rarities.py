"""The user's firearm tiers, and a guard against unrelated balance changes."""
import hashlib,json,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
BALANCE=ROOT/'src/main/resources/assets/metroport/inventory_balance.json'
GROUPS={'common': ['duplet', 'revolver', 'ubludok'],
 'uncommon': ['ashot', 'aksu'],
 'rare': ['ak', 'podonok', 'uboinik'],
 'epic': ['tihar', 'ventil', 'rpk', 'velopal', 'helsing'],
 'legendary': ['saiga', 'vsv', 'gatling', 'preved'],
 'mythic': ['medved',
            'ognemet',
            'ognemet_mini',
            'abzac',
            'ak2012',
            'vyhlop',
            'railgun']}
class WeaponRarities(unittest.TestCase):
    def test_exact_requested_tiers_for_all_twenty_four_named_firearms(self):
        items=json.loads(BALANCE.read_text())['items']
        expected={gun:tier for tier,guns in GROUPS.items() for gun in guns}
        self.assertEqual(24,len(expected))
        for gun,tier in expected.items():
            with self.subTest(gun=gun):self.assertEqual(tier,items['modularwarfare:metro_'+gun]['rarity'])
    def test_unlisted_akk_keeps_rare(self):
        self.assertEqual('rare',json.loads(BALANCE.read_text())['items']['modularwarfare:metro_akk']['rarity'])
    def test_approved_dev39_balance_and_unchanged_armor_dimensions_capacity(self):
        data=json.loads(BALANCE.read_text())
        for id,profile in data['items'].items():
            if id.startswith('modularwarfare:metro_'):profile.pop('rarity',None)
        actual=hashlib.sha256(json.dumps(data,sort_keys=True,separators=(',',':')).encode()).hexdigest()
        self.assertEqual('5bee48cf5d0463952bd11e7630e03ab5d7b9baf61dc5c4fa654d2686956cebaa',actual)
