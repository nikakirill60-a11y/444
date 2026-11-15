#!/usr/bin/env python3
"""Generate a dependency collector for the actually verified uploaded geometry."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]

def generate(report):
    source=(ROOT/'tools/decor_finish_template.py').read_text()
    models=report['dynamic_suffix_correction']['resolved_models']+['structure/mls_vorota_l06_add2.m2033model']
    return source.replace('MODEL_MATERIALS = ()','MODEL_MATERIALS = '+repr(tuple(report['material_paths']))).replace('EXTRA_MODELS = ()','EXTRA_MODELS = '+repr(tuple(models))).replace("EXPECTED_MODELS_SHA = ''",'EXPECTED_MODELS_SHA = '+repr(report['original_models_jar_sha256']))

if __name__=='__main__':
    report=json.loads((ROOT/'reports/METRO_MOD/decor_geometry_verified.json').read_text())
    (ROOT/'downloads/finishdecor.py').write_text(generate(report),encoding='utf-8')
