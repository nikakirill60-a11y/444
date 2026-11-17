"""Import only the pinned donor menu's two effect images and original quad.
No donor execution; the entire original Content archive is SHA256 pinned.
"""
import hashlib
import io
import json
from pathlib import Path
import zipfile
from PIL import Image
from tools.decode_metromod_content import decode

ROOT = Path(__file__).resolve().parents[1]
CONTENT_SHA = '318cdcf0a48f076571300e4f8336651c702601cd9beafbc0a72cf028d29af9ab'
RES = ROOT / 'src/main/resources/assets/metroport'

def prepare(raw):
    if hashlib.sha256(raw).hexdigest() != CONTENT_SHA:
        raise ValueError('Wrong original Content SHA256')
    files = {}; rows = []
    with zipfile.ZipFile(io.BytesIO(raw)) as archive:
        for name in ('ambient/tunnel_kapli', 'fx/fx_glowlamp_small'):
            source = 'assets/metro2033server/textures/512/' + name + '.m2033texture'
            encrypted = archive.read(source); plain = decode(encrypted)
            with Image.open(io.BytesIO(plain)) as image:
                image.load(); converted = image.convert('RGBA'); out = io.BytesIO()
                converted.save(out, format='PNG', optimize=False)
                dimensions = list(image.size)
            target = 'textures/metromod/menu_effects/' + name.split('/')[-1] + '.png'
            files[target] = out.getvalue()
            rows.append({'source': source, 'source_sha256': hashlib.sha256(encrypted).hexdigest(),
                         'decoded_sha256': hashlib.sha256(plain).hexdigest(), 'path': target,
                         'sha256': hashlib.sha256(files[target]).hexdigest(), 'dimensions': dimensions})
        source = 'assets/metro2033server/models/obj/primitives/quad.obj'
        files['models/metromod/menu_effects/quad.obj'] = archive.read(source)
        rows.append({'source': source, 'path': 'models/metromod/menu_effects/quad.obj',
                     'sha256': hashlib.sha256(archive.read(source)).hexdigest()})
    return files, {'schema': 1, 'content_sha256': CONTENT_SHA, 'files': rows,
                   'donor_java_executed': False, 'in_game_verified': False}

if __name__ == '__main__':
    import argparse
    parser = argparse.ArgumentParser(description=__doc__); parser.add_argument('content')
    args = parser.parse_args(); files, report = prepare(Path(args.content).read_bytes())
    for name, data in files.items():
        p = RES / name
        if p.exists() and p.read_bytes() != data: raise ValueError('Conflicting output ' + name)
    for name, data in files.items():
        p = RES / name; p.parent.mkdir(parents=True, exist_ok=True); p.write_bytes(data)
    (ROOT / 'reports/METRO_MOD/menu_effects_verified.json').write_text(json.dumps(report, indent=2)+'\n')
