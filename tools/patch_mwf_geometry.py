#!/usr/bin/env python3
"""Surgical v6 -> v7 geometry fix. Never rebuild from the pre-mechanics pack.
Requires the tracked original BlackRazid assets, Pillow and NumPy.
"""
import argparse, base64, copy, hashlib, io, json, zipfile
from pathlib import Path
import numpy as np
from PIL import Image, ImageChops
from build_full_mwf_pack import PackGltf, parse_obj, wrap_triangle

ROOT = Path(__file__).resolve().parents[1]
PREFIX = 'assets/modularwarfare/'
BASE_SHA = 'cf3689482eddbf35eb864cdb555e1ca6cb2428f86fd0a4b8e9105e33ee666c1c'


def encoded(value):
    return (json.dumps(value, ensure_ascii=False, indent=2) + '\n').encode()


def append_mesh(doc, name, triangles, trs):
    """Append a separate embedded buffer without touching original animation accessors."""
    part = PackGltf()
    part.mesh(name, triangles)
    part.doc['buffers'] = [{'byteLength': len(part.data), 'uri': 'data:application/octet-stream;base64,' + base64.b64encode(part.data).decode()}]
    bo, vo, ao, mo = (len(doc[k]) for k in ('buffers', 'bufferViews', 'accessors', 'meshes'))
    doc['buffers'].extend(part.doc['buffers'])
    for view in part.doc['bufferViews']:
        view['buffer'] += bo
        doc['bufferViews'].append(view)
    for accessor in part.doc['accessors']:
        accessor['bufferView'] += vo
        doc['accessors'].append(accessor)
    mesh = part.doc['meshes'][0]
    for primitive in mesh['primitives']:
        primitive['attributes'] = {k: v + ao for k, v in primitive['attributes'].items()}
        primitive['indices'] += ao
    doc['meshes'].append(mesh)
    node = {'name': name, 'mesh': mo, **copy.deepcopy(trs)}
    index = len(doc['nodes'])
    doc['nodes'].append(node)
    doc['nodes'][0]['children'].append(index)
    return index


def texture_tile(atlas, original):
    """Locate the exact existing atlas tile, not a guessed/new texture substitute."""
    original = original.convert('RGBA')
    original.thumbnail((508, 508), Image.Resampling.LANCZOS)
    w, h = original.size
    matches = []
    for y in range(4, atlas.height, 512):
        for x in range(4, atlas.width, 512):
            tile = atlas.crop((x, y, x + w, y + h)).convert('RGBA')
            if ImageChops.difference(tile, original).convert('RGB').getbbox() is None:
                matches.append((x, y, w, h))
    if len(matches) != 1:
        raise ValueError('Expected exactly one original cylinder texture tile: ' + str(matches))
    return matches[0]


def patch(files, base_sha=BASE_SHA):
    report = {'baseSha256': base_sha, 'minecraftVerified': False, 'fixes': {}}
    # The old exporter copied only translation from the native draw matrix.
    # Native AK2012 mag has S(.1,.1,.107), so omission made it 10x oversized.
    path = 'guns/render/metro_ak2012.render.json'
    config = json.loads(files[path])
    config['attachment']['metro_ak2012_ammo']['scale'] = dict(x=.1, y=.1, z=.107)
    files[path] = encoded(config)
    report['fixes']['ak2012Magazine'] = {'source': 'tools/blackrazid-reference/java/mods/provoda/guns/renderers/Render_AK2012.java', 'scale': [.1, .1, .107], 'translationPreserved': True, 'posePreserved': True}

    # `baraban` was misclassified as a detachable magazine and filtered out,
    # although this gun uses loose ammunition. Restore at its native resting pivot.
    path = PREFIX + 'gltf/guns/metro_revolver.gltf'
    doc = json.loads(files[path])
    gun = next(n for n in doc['nodes'] if n['name'] == 'gunModel')
    anchor = next(n for n in doc['nodes'] if n['name'] == 'ammoModel')
    size = gun['scale'][0]
    factor = anchor['scale'][0] / size
    shift = (np.array(anchor['translation']) - gun['translation']) / size
    rotation = np.array([[0, 0, 1], [0, 1, 0], [-1, 0, 0]], dtype=float)
    source = ROOT / 'tools/blackrazid-reference/assets/weapons/revolver/baraban.obj'
    texture = ROOT / 'tools/blackrazid-reference/assets/weapons/textures/wpn_revolver.2048.png'
    atlas = Image.open(io.BytesIO(files[PREFIX + 'skins/guns/metro_native_atlas.png'])).convert('RGBA')
    x, y, w, h = texture_tile(atlas, Image.open(texture))
    triangles = []
    for material, tri in parse_obj(source.read_bytes()):
        for clipped in wrap_triangle(tri):
            out = []
            for point, uv, normal in clipped:
                p = factor * rotation @ (np.array(point) + [0, .085, 0]) + shift
                n = rotation @ normal
                out.append((tuple(p), ((x + uv[0] * w) / atlas.width, (y + (1 - uv[1]) * h) / atlas.height), tuple(n)))
            triangles.append(out)
    trs = {k: v for k, v in gun.items() if k in ('translation', 'rotation', 'scale')}
    append_mesh(doc, 'revolverCylinderModel', triangles, trs)
    files[path] = encoded(doc)
    report['fixes']['revolverCylinder'] = {'sourceSha256': hashlib.sha256(source.read_bytes()).hexdigest(), 'pivot': [0, .085, 0], 'triangles': len(triangles), 'atlasTile': [x, y, w, h], 'posePreserved': True, 'roundVisibilityUnchanged': True}
    files['GEOMETRY_UPDATE_MANIFEST.json'] = encoded(report)
    manifest = json.loads(files['FULL_PACK_MANIFEST.json'])
    manifest['geometryUpdate'] = report
    files['FULL_PACK_MANIFEST.json'] = encoded(manifest)
    return files


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--input', required=True)
    parser.add_argument('--output', required=True)
    ARGS = parser.parse_args()
    if hashlib.sha256(Path(ARGS.input).read_bytes()).hexdigest() != BASE_SHA:
        raise ValueError('Unexpected pack baseline; refusing to discard a newer pack')
    with zipfile.ZipFile(ARGS.input) as archive:
        files = {name: archive.read(name) for name in archive.namelist()}
    files = patch(files)
    Path(ARGS.output).parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(ARGS.output, 'w', zipfile.ZIP_DEFLATED, compresslevel=9) as archive:
        for name, data in sorted(files.items()):
            info = zipfile.ZipInfo(name, (2026, 10, 30, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            archive.writestr(info, data)
    print(ARGS.output, Path(ARGS.output).stat().st_size, hashlib.sha256(Path(ARGS.output).read_bytes()).hexdigest())
