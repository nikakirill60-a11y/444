#!/usr/bin/env python3
"""Generate a standalone collector from the actually verified 3D-menu meshes."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def generate(report):
    if not report.get('direct_menu_reference_set_verified') or report.get('validated_models') != 35:
        raise ValueError('Verified original menu geometry required')
    source = (ROOT / 'tools/decor_finish_template.py').read_text(encoding='utf-8')
    source = source.replace('functional-decor materials, textures and model parts',
                            'original 3D-menu materials and their textures')
    source = source.replace('finishdecor.py', 'finishmenu.py')
    source = source.replace('metro_decor_finish', 'metro_menu_finish')
    source = source.replace('decor_finish_inventory.json', 'menu_finish_inventory.json')
    source = source.replace('MODEL_MATERIALS = ()', 'MODEL_MATERIALS = ' + repr(tuple(report['material_paths'])))
    # No decor gate add-ons, weapons or other guessed model dependencies.
    source = source.replace("EXPECTED_MODELS_SHA = ''",
                            'EXPECTED_MODELS_SHA = ' + repr(report['original_models_jar_sha256']))
    return source


if __name__ == '__main__':
    report = json.loads((ROOT / 'reports/METRO_MOD/menu_geometry_verified.json').read_text())
    (ROOT / 'downloads/finishmenu.py').write_text(generate(report), encoding='utf-8')
