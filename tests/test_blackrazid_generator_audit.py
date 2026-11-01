import json,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT/'tools'))
from audit_blackrazid_generators import report
class NativeGeneratorAuditTests(unittest.TestCase):
    def test_exact_source_behavior_is_not_replaced_with_invented_tunnel_generation(self):
        data=report();self.assertEqual(data,json.loads((ROOT/'docs/blackrazid-generator-audit.json').read_text()));self.assertEqual(6,len(data['helpers']))
        self.assertTrue(all(not row['registered'] and not row['generatesTunnel'] for row in data['helpers']));self.assertEqual({3002,3004,3005,3006,3007,3008},{r['targetLegacyId'] for r in data['helpers']})
        self.assertEqual({'BlockGermo1','BlockShkaf1','BlockShkaf2','BlockChair2','BlockLavka1','BlockPolka1'},{r['targetClass'] for r in data['helpers']});self.assertEqual({5000},{r['nativeRayDistance'] for r in data['helpers']})
