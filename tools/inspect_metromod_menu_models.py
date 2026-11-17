#!/usr/bin/env python3
"""Verify the uploaded original menu geometry without loading donor Java."""
import argparse
import json
from pathlib import Path
import tempfile

from tools.inspect_metromod_decor_models import inspect as inspect_geometry
from tools.inspect_metromod_decor_models import PREFIX

ROOT = Path(__file__).resolve().parents[1]
MODELS_SHA = 'e768edb709880a8d81e512534af22b2a3e306dbace709bc27ded6cc4e6d1e4f8'


def check_inventory(given, references):
    if given.get('source_sha256') != MODELS_SHA:
        raise ValueError('Unexpected original Models.jar SHA256')
    expected = set(references)
    files = given.get('files', [])
    actual = [r['reference'] for r in files]
    if len(actual) != len(expected) or set(actual) != expected:
        raise ValueError('Menu references missing, duplicated or unexpected')
    if given.get('missing') or given.get('matched') != len(expected) or given.get('expected') != len(expected):
        raise ValueError('Incomplete menu model inventory')
    if any(r['path'] != PREFIX + r['reference'] for r in files):
        raise ValueError('Noncanonical menu model path')


def inspect(source, inventory, output, decoded_zip=None):
    output = Path(output)
    if output.exists():
        raise FileExistsError('Report already exists')
    paths = [Path(source), Path(inventory), output] + ([Path(decoded_zip)] if decoded_zip else [])
    if len({p.resolve() for p in paths}) != len(paths):
        raise ValueError('Input and output paths must be distinct')
    if Path(inventory).stat().st_size > 2 * 1024 * 1024:
        raise ValueError('Inventory too large')
    given = json.loads(Path(inventory).read_text(encoding='utf-8-sig'))
    references = json.loads((ROOT / 'reports/METRO_MOD/menu_audit.json').read_text())['model_references']
    check_inventory(given, references)
    # Reuse the bounded/hash/CRC/AES/OBJ validator. Its legacy decor-specific
    # suffix annotation does NOT apply to this menu and is removed below.
    with tempfile.TemporaryDirectory() as temp:
        report = inspect_geometry(source, inventory, Path(temp) / 'geometry.json', decoded_zip)
    report.pop('dynamic_suffix_correction')
    report['scope'] = '35 direct scene models referenced by the original METRO_MOD 3D menu'
    report['direct_menu_reference_set_verified'] = True
    report['geometry_totals']['triangles'] = sum(r['triangles'] for r in report['models'])
    report['status'] = 'All 35 direct menu meshes verified; material and texture closure not yet verified; GUI not implemented'
    with output.open('x', encoding='utf-8') as handle:
        handle.write(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    return report


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('source'); parser.add_argument('inventory'); parser.add_argument('report')
    parser.add_argument('--decoded-zip')
    args = parser.parse_args()
    report = inspect(args.source, args.inventory, args.report, args.decoded_zip)
    print(report['status'])
    print('Required material files:', len(report['material_paths']))
