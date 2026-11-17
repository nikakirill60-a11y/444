#!/usr/bin/env python3
"""Import verified original menu meshes and every collected texture channel.

No invented geometry, UV edits, guessed atlases, source Java execution or GUI hook.
The manifest is preparation for the original scene port, not a finished menu.
"""
import argparse
import gzip
import hashlib
import io
import json
from pathlib import Path
import re
import zipfile
from PIL import Image

from tools.decode_metromod_content import decode
from tools.inspect_metromod_decor_models import obj_metadata, PREFIX
from tools.inspect_metromod_decor_finish import material_bindings
from tools.decor_finish_template import material_text

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'src/main/resources/assets/metroport'
TEXTURES = 'assets/metro2033server/textures/obj/'


def sha(data):
    return hashlib.sha256(data).hexdigest()


def report(name):
    return json.loads((ROOT / 'reports/METRO_MOD' / name).read_text(encoding='utf-8'))


def verify_closure(geometry, finish):
    if geometry.get('validated_models') != 35 or not geometry.get('direct_menu_reference_set_verified'):
        raise ValueError('Original menu geometry must be verified first')
    if finish.get('material_count') != 35 or finish.get('total_geometry_count') != 35:
        raise ValueError('Unexpected material/mesh inventory')
    for key in ('missing_materials', 'missing_material_labels', 'missing_diffuse_textures',
                'original_missing_models', 'resolved_collector_material_errors'):
        if key not in finish or finish[key]:
            raise ValueError('Incomplete original menu material closure: ' + key)


def prepare(models, materials):
    geometry = report('menu_geometry_verified.json')
    finish = report('menu_finish_verified.json')
    verify_closure(geometry, finish)
    if sha(models) != geometry['source_zip_sha256'] or sha(materials) != finish['source_zip_sha256']:
        raise ValueError('Unexpected source ZIP SHA256')
    assets = {}; textures = {}; records = []; meshes = []; sources = {}
    with zipfile.ZipFile(io.BytesIO(models)) as mz, zipfile.ZipFile(io.BytesIO(materials)) as fz:
        for archive, rows in ((mz, geometry['models']), (fz, finish['files'])):
            names = archive.namelist()
            if len(set(names)) != len(names) or set(names) != {r['source_path'] for r in rows}:
                raise ValueError('Unexpected/duplicate source ZIP membership')
            for row in rows:
                name = row['source_path']; entry = archive.getinfo(name)
                if entry.file_size != row['source_bytes']:
                    raise ValueError('Unexpected source member size: ' + name)
                data = archive.read(entry)  # Includes ZIP CRC validation.
                if sha(data) != row['source_sha256']:
                    raise ValueError('Unexpected source member SHA256: ' + name)
                sources[name] = data
        for row in finish['files']:
            if row['type'] != 'texture':
                continue
            name = row['source_path']; plain = decode(sources[name])
            if not plain.startswith(b'DDS '):
                raise ValueError('Expected donor DDS texture')
            relative = name.removeprefix(TEXTURES).removesuffix('.m2033texture') + '.png'
            if not re.fullmatch(r'[a-z0-9_.-]+\.png', relative):
                raise ValueError('Unsafe/noncanonical texture resource path')
            target = 'textures/metromod/menu/' + relative
            with Image.open(io.BytesIO(plain)) as image:
                if image.size != (row['width'], row['height']):
                    raise ValueError('Verified texture dimensions differ')
                image.load(); out = io.BytesIO(); image.convert('RGBA').save(out, format='PNG')
            data = out.getvalue()
            if sha(data) != row['decoded_sha256']:
                raise ValueError('Verified texture decoding differs')
            assets[target] = data
            textures[name] = 'metroport:' + target
        for row in geometry['models']:
            name = row['source_path']; plain = decode(sources[name])
            if sha(plain) != row['decoded_sha256']:
                raise ValueError('Verified geometry decoding differs')
            metadata = obj_metadata(plain, name)
            if metadata['counts'] != row['counts']:
                raise ValueError('Verified geometry counts differ')
            bindings = {}
            for library in row['material_libraries']:
                for label, root in material_bindings(material_text(sources[library])).items():
                    if label in bindings and bindings[label] != root:
                        raise ValueError('Ambiguous original material label')
                    bindings[label] = root
            maps = {}
            for label in row['usemtl']:
                root = bindings[label]
                if not root:
                    raise ValueError('Untextured original menu material')
                channels = {}
                for channel, suffix in (('diffuse', ''), ('normal', '_n'), ('specular', '_s'), ('emission', '_e')):
                    candidates = [value for path, value in textures.items()
                                  if re.fullmatch(re.escape(TEXTURES + root + suffix) + r'\.[0-9]+\.m2033texture', path)]
                    if len(candidates) > 1:
                        raise ValueError('Ambiguous collected texture resolution')
                    if candidates:
                        channels[channel] = candidates[0]
                if 'diffuse' not in channels:
                    raise ValueError('Missing exact diffuse texture')
                maps[label] = channels
            reference = row['reference']
            target = 'models/metromod/menu/' + reference.removesuffix('.m2033model') + '.obj.gz'
            # No normalization of OBJ positions, normals, groups, winding or UV.
            assets[target] = gzip.compress(plain, mtime=0)
            meshes.append({'source_reference': reference, 'mesh': 'metroport:' + target,
                           'materials': maps, 'bounds': row['bounds'], 'triangles': row['triangles'],
                           'decoded_obj_sha256': row['decoded_sha256']})
    for name, data in sorted(assets.items()):
        records.append({'path': name, 'bytes': len(data), 'sha256': sha(data)})
    manifest = {'schema': 1, 'source': 'original METRO_MOD 1.7.10 3D menu',
                'models_zip_sha256': geometry['source_zip_sha256'],
                'materials_zip_sha256': finish['source_zip_sha256'],
                'model_count': len(meshes), 'texture_count': len(textures),
                'models': meshes, 'assets': records,
                'gui_implemented': False, 'in_game_verified': False,
                'notes': ['Original OBJ bytes inside deterministic gzip, without UV or geometry edits.',
                          'All collected diffuse/normal/specular/emission textures retained as decoded PNG.',
                          'Material channel availability is not a claim of donor shader parity.',
                          'No main-menu replacement or automatic audio/network activity enabled.']}
    return assets, manifest


def install(models, materials, destination=RES):
    assets, manifest = prepare(Path(models).read_bytes(), Path(materials).read_bytes())
    destination = Path(destination)
    for name, data in assets.items():
        path = destination / name
        if path.exists() and path.read_bytes() != data:
            raise ValueError('Refusing to replace different prepared menu resource: ' + name)
    for name, data in assets.items():
        path = destination / name; path.parent.mkdir(parents=True, exist_ok=True); path.write_bytes(data)
    (destination / 'metromod_menu.json').write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    return manifest


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('models_zip'); parser.add_argument('materials_zip')
    args = parser.parse_args()
    result = install(args.models_zip, args.materials_zip)
    print('Prepared', result['model_count'], 'original menu meshes and', result['texture_count'], 'textures. GUI not enabled.')
