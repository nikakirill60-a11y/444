import json
from pathlib import Path
from types import SimpleNamespace as NS
import unittest

from tools.audit_metromod_menu import code_inventory

ROOT = Path(__file__).resolve().parents[1]


def operand(kind, value):
    return NS(op_type=NS(name=kind), value=value)


def instruction(pos, mnemonic, operands=()):
    return NS(pos=pos, mnemonic=mnemonic, operands=operands)


def method(desc, instructions):
    return NS(name=NS(value='overloaded'), descriptor=NS(value=desc),
              code=NS(_code=b'fixture', disassemble=lambda: iter(instructions)))


class MenuCodeAuditTest(unittest.TestCase):
    def test_original_method_inventory_and_all_switches(self):
        report = json.loads((ROOT / 'reports/METRO_MOD/menu_audit.json').read_text())
        code = report['code_inventory']
        self.assertFalse(code['semantics_inferred'])
        self.assertFalse(report['target_implemented'])
        self.assertEqual(29, len(code['methods']))
        self.assertEqual(3, sum(len(m['switches']) for m in code['methods']))
        identities = [(m['name'], m['descriptor']) for m in code['methods']]
        self.assertEqual(len(identities), len(set(identities)))
        constructors = [m for m in code['methods'] if m['name'] == '<init>']
        self.assertEqual(['(Z)V'], [m['descriptor'] for m in constructors])
        literals = [r['value'] for m in constructors for r in m['literals']]
        self.assertEqual(35, sum(s.endswith('.m2033model') for s in literals))
        self.assertEqual(6, sum(s.startswith('metro2033server:menu.') for s in literals))

    def test_lookup_and_table_offsets_are_relative_and_overloads_preserved(self):
        methods = [
            method('(I)V', [instruction(4, 'lookupswitch', [{0: 28, 1: 53}, operand('BRANCH', 76)]),
                             instruction(32, 'return'), instruction(57, 'return'), instruction(80, 'return')]),
            method('(F)V', [instruction(11, 'tableswitch', [operand('BRANCH', 52), operand('LITERAL', 0),
                                                           operand('LITERAL', 1), operand('BRANCH', 25), operand('BRANCH', 35)]),
                             instruction(36, 'return'), instruction(46, 'return'), instruction(63, 'return')])]
        result = code_inventory(NS(methods=methods))['methods']
        self.assertEqual({'0': 32, '1': 57}, result[0]['switches'][0]['cases'])
        self.assertEqual(80, result[0]['switches'][0]['default'])
        self.assertEqual({'0': 36, '1': 46}, result[1]['switches'][0]['cases'])
        self.assertNotEqual(result[0]['descriptor'], result[1]['descriptor'])

    def test_bad_switch_target_is_not_silently_accepted(self):
        m = method('(I)V', [instruction(0, 'lookupswitch', [{0: 99}, operand('BRANCH', 0)])])
        with self.assertRaisesRegex(ValueError, 'not an instruction'):
            code_inventory(NS(methods=[m]))
