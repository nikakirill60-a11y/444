#!/usr/bin/env python3
"""Generate a standalone source collector, not a mod or model conversion."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
KINDS={'Germo','Lever','Door','Light','Inventory'}
HEADER='''#!/usr/bin/env python3
r"""Collect direct model dependencies of donor functional decoration only.

Python 3, no dependencies. Pass a LOCAL Metro2033Models.jar:
  py -3 collect_metromod_decor.py "C:\\Metro\\content\\Metro2033Models.jar"

Creates NEW metro_decor_parts folder, ZIP parts <=90 MiB and SHA256 inventory.
Selects the 112 catalogued Germo/Lever/Door/Light/Inventory entries and their
190 direct model references. Does not execute Java, download a server, install
a mod, collect vehicles/weapons or infer missing textures inside model files.
Original encrypted bytes, if present, are retained. NOT a playable decor port.
"""
import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import stat
import zipfile

'''

def generate(report):
    refs=tuple(sorted({p for e in report['entries'] if e['kind'] in KINDS for p in e['direct_model_references']}))
    # Share the already-tested streaming/size/CRC safeguards without requiring
    # users to install any repository modules. Generated file is standalone.
    source=(ROOT/'downloads/collect_metromod_menu.py').read_text()
    body=source[source.index('LIMIT = 90 * 1024 * 1024'):]
    body=body.replace('metro_menu_models_','metro_decor_models_').replace('menu_model_inventory.json','decor_model_inventory.json')
    body=body.replace('metro_menu_parts','metro_decor_parts').replace('original source models only; internal texture dependencies not yet audited','original functional-decor source models only; internal dependencies not yet audited; not a runtime port')
    return HEADER+'MODEL_REFERENCES = '+repr(refs)+'\n\n'+body

if __name__=='__main__':
    report=json.loads((ROOT/'reports/METRO_MOD/functional_decor_audit.json').read_text())
    (ROOT/'downloads/collect_metromod_decor.py').write_text(generate(report),encoding='utf-8')
