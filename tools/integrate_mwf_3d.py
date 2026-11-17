#!/usr/bin/env python3
"""Deterministic v7 -> v8 data-only update from the user's 3d.rar geometry.
Source-owned assets are preserved separately in tools/mwf-3d/sources.zip.
No change to the pinned fix1-metro4 runtime or pre-existing hand/weapon poses.
"""
import argparse
import base64
import copy
import hashlib
import io
import json
import math
from pathlib import Path
import zipfile

import numpy as np
from PIL import Image
from build_full_mwf_pack import obj_bytes, wrap_triangle
from build_uboinik_pack import obj_read, icon
from patch_mwf_geometry import append_mesh, encoded

ROOT = Path(__file__).resolve().parents[1]
P = 'assets/modularwarfare/'
BASE_SHA = '02871b5b661e7c5747ef77e41308c1621869111487abd1d259132455fb0c91ef'
RETIRED = {
    'metro_uboinik_laser': 'metro_mod_laser',
    'metro_uboinik_suppressor': 'metro_mod_glushak',
    'metro_mod_uboinik_barrel': 'metro_mod_barrel',
    'metro_uboinik_collimator': 'metro_mod_kolimator',
}


def digest(data):
    return hashlib.sha256(data).hexdigest()


def png(image):
    stream = io.BytesIO()
    image.save(stream, format='PNG', optimize=True, compress_level=9)
    return stream.getvalue()


def xyz(v):
    return dict(zip('xyz', map(float, v)))


def triangles(data):
    return [t for group in obj_read(data).values() for t in group]


def bounds(tris):
    points = np.array([v[0] for t in tris for v in t])
    return points.min(axis=0), points.max(axis=0)


def transformed(tris, scale=1, translation=(0, 0, 0), rotation=None, gltf=False):
    rotation = np.eye(3) if rotation is None else np.asarray(rotation)
    return [[(tuple(scale * rotation @ np.array(p) + translation), (uv[0], 1 - uv[1]) if gltf else uv, tuple(rotation @ n)) for p, uv, n in t] for t in tris]


def fit(tris, target, rotation=None):
    """Uniform scale, retain proportions; fit longitudinal X extent and bbox centre.
    Deliberate socket adaptation, recorded in the manifest, not a claim of live alignment.
    """
    lo, hi = bounds(transformed(tris, rotation=rotation))
    a, b = map(np.array, target)
    if hi[0] - lo[0] < 1e-8:
        raise ValueError('No longitudinal extent')
    scale = float((b[0] - a[0]) / (hi[0] - lo[0]))
    shift = (a + b) / 2 - scale * (lo + hi) / 2
    return scale, shift


def render_matrix(t):
    p = np.array([t.get('translate', {}).get(k, 0) for k in 'xyz'])
    s = np.diag([t.get('scale', {}).get(k, 1) for k in 'xyz'])
    x, y, z = [math.radians(t.get('rotate', {}).get(k, 0)) for k in 'xyz']
    rx = np.array([[1, 0, 0], [0, math.cos(x), -math.sin(x)], [0, math.sin(x), math.cos(x)]])
    ry = np.array([[math.cos(y), 0, math.sin(y)], [0, 1, 0], [-math.sin(y), 0, math.cos(y)]])
    rz = np.array([[math.cos(z), -math.sin(z), 0], [math.sin(z), math.cos(z), 0], [0, 0, 1]])
    m = np.eye(4)
    m[:3, :3] = s @ ry @ rx @ rz
    m[:3, 3] = p
    return m


def mesh_bounds(doc, names):
    result = []
    gun = next(n for n in doc['nodes'] if n['name'] == 'gunModel')
    for node in doc['nodes']:
        if node['name'] not in names or 'mesh' not in node:
            continue
        # Existing baked native socket parts share the body TRS.
        for k in ('translation', 'rotation', 'scale'):
            if node.get(k) != gun.get(k):
                raise ValueError('Socket needs explicit relative transform: ' + node['name'])
        for primitive in doc['meshes'][node['mesh']]['primitives']:
            a = doc['accessors'][primitive['attributes']['POSITION']]
            result.extend((a['min'], a['max']))
    if not result:
        raise ValueError('Missing baked socket: ' + str(names))
    return np.min(result, axis=0), np.max(result, axis=0)


def attachment_tris(files, id):
    r = json.loads(files['attachments/render/' + id + '.render.json'])
    return triangles(files['obj/attachments/' + r['modelFileName']])


def dynamic_bounds(files, id, binding):
    tri = attachment_tris(files, id)
    m = render_matrix(binding)
    pts = np.array([(m @ np.array([*v[0], 1]))[:3] for t in tri for v in t])
    return pts.min(0), pts.max(0)


def set_binding(tris, target, rotation=None, old=None):
    scale, p = fit(tris, target, rotation)
    result = {'binding': 'gunModel', 'translate': xyz(p), 'scale': xyz([scale] * 3)}
    if rotation is not None:
        # Only the shared barrel/suppressor need the native Z -> model X turn.
        result['rotate'] = xyz([0, 90, 0])
    for k in ('sightAimPosOffset', 'sightAimRotOffset', 'renderInsideGunOffset'):
        if old and k in old:
            result[k] = copy.deepcopy(old[k])
    return result


def skin(id):
    return [{'internalName': id, 'skinAsset': id, 'displayName': 'Оригинал'}]


def update(files, source):
    old = copy.deepcopy(files)
    report = {
        'baseSha256': BASE_SHA, 'sourceArchive': 'User 3d.rar / Google Drive 1fEvcBDNUsWWHMG3gmyTWg4bfwbn7lxIe',
        'sourceFiles': {n: digest(b) for n, b in sorted(source.items())},
        'minecraftVerified': False, 'socketAdaptation': 'Uniform longitudinal fit to old socket bounds; body and hands unchanged',
        'changedSockets': {}, 'retiredAttachments': RETIRED, 'newWeapons': {},
        'limitations': [
            'No live Minecraft verification; attachments need visual confirmation in the pinned runtime.',
            'Mini flamethrower uses the existing MWF bullet-based approximation, NOT a flame stream.',
            'The supplied mini model is static; no invented separate canister/lever animation.',
            'Medved gameplay and hand/reload animation are an explicit initial Uboinik-profile adaptation; no claim of original Metro balance or pneumatic mechanics.',
            'Source radiator is fitted to the original Ubludok muzzle axis without changing its body/hand pose.',
        ],
    }
    # All of the supplied laser materials use the same diffuse texture, not skins.
    laser = triangles(source['att_lazer.obj'])
    collimator = attachment_tris(old, 'metro_uboinik_collimator')
    # Canonical dynamic shared models. Use existing Uboinik collimator geometry byte-for-byte.
    files['obj/attachments/metro_mod_laser.obj'] = obj_bytes([('attachmentModel', laser)])
    files['obj/attachments/metro_mod_kolimator.obj'] = old['obj/attachments/metro_uboinik_collimator.obj']
    laser_image = Image.open(io.BytesIO(source['wpn_obves.png'])).convert('RGBA')
    laser_image.thumbnail((1024, 1024), Image.Resampling.LANCZOS)
    files[P + 'skins/attachments/metro_mod_laser.png'] = png(laser_image)
    files[P + 'skins/attachments/metro_mod_kolimator.png'] = old[P + 'skins/attachments/metro_uboinik_collimator.png']
    for id in ('metro_mod_laser', 'metro_mod_kolimator'):
        t = json.loads(files['attachments/' + id + '.json'])
        t['modelSkins'] = skin(id)
        files['attachments/' + id + '.json'] = encoded(t)
    # Preserve native laser feature config and collimator sight rendering (including its reticle).
    for id, previous in [('metro_mod_laser', 'metro_uboinik_laser'), ('metro_mod_kolimator', 'metro_uboinik_collimator')]:
        cfg = json.loads(old['attachments/render/' + previous + '.render.json'])
        cfg['modelFileName'] = id + '.obj'
        files['attachments/render/' + id + '.render.json'] = encoded(cfg)
    files[P + 'textures/items/metro_mod_kolimator.png'] = old[P + 'textures/items/metro_uboinik_collimator.png']
    files[P + 'textures/items/metro_mod_laser.png'] = png(icon(laser, laser_image))
    for path in sorted(n for n in old if n.startswith('guns/') and n.count('/') == 1):
        g = json.loads(old[path]); id = g['internalName']
        rp = 'guns/render/' + id + '.render.json'
        r = json.loads(old[rp]); doc = json.loads(old[P + 'gltf/guns/' + r['modelFileName']])
        changed = []
        for common, raw in [('metro_mod_laser', laser), ('metro_mod_kolimator', collimator)]:
            binding = r.get('attachment', {}).get(common)
            if binding is None:
                continue
            target = mesh_bounds(doc, binding['showPart']) if binding.get('showPart') else dynamic_bounds(old, common, binding)
            r['attachment'][common] = set_binding(raw, target, old=binding)
            # Old baked parts stay hidden; the actual shared item now renders the geometry.
            for name in binding.get('showPart', []):
                if name not in r.setdefault('defaultHidePart', []):
                    r['defaultHidePart'].append(name)
            changed.append(common)
        if id == 'metro_uboinik':
            native_to_x = np.array([[0, 0, 1], [0, 1, 0], [-1, 0, 0]])
            for retired, common in RETIRED.items():
                previous = r['attachment'].pop(retired)
                target = dynamic_bounds(old, retired, previous)
                raw = laser if common == 'metro_mod_laser' else collimator if common == 'metro_mod_kolimator' else attachment_tris(old, common)
                rotation = native_to_x if common in ('metro_mod_barrel', 'metro_mod_glushak') else None
                r['attachment'][common] = set_binding(raw, target, rotation, previous)
                changed.append(common)
            g['acceptedAttachments'] = {
                'barrel': ['metro_mod_barrel', 'metro_mod_glushak'],
                'sight': ['metro_mod_kolimator', 'metro_mod_optica_small'], 'laser': ['metro_mod_laser'],
            }
            if 'defaultAttachments' in g:
                g['defaultAttachments'] = {slot: RETIRED.get(v, v) for slot, v in g['defaultAttachments'].items()}
            files[path] = encoded(g)
        # The old Uboinik barrel item was also accepted by Ashot. Migrate that
        # socket too; removing its item must never leave another gun dangling.
        for retired, common in RETIRED.items():
            if retired not in r.get('attachment', {}):
                continue
            binding = r['attachment'].pop(retired)
            target = mesh_bounds(doc, binding['showPart']) if binding.get('showPart') else dynamic_bounds(old, retired, binding)
            rotation = np.array([[0, 0, 1], [0, 1, 0], [-1, 0, 0]]) if common in ('metro_mod_barrel', 'metro_mod_glushak') else None
            raw = laser if common == 'metro_mod_laser' else collimator if common == 'metro_mod_kolimator' else attachment_tris(old, common)
            r['attachment'][common] = set_binding(raw, target, rotation, binding)
            for part in binding.get('showPart', []):
                if part not in r.setdefault('defaultHidePart', []):
                    r['defaultHidePart'].append(part)
            g['acceptedAttachments'] = {slot: list(dict.fromkeys(RETIRED.get(v, v) for v in ids)) for slot, ids in g['acceptedAttachments'].items()}
            files[path] = encoded(g)
            changed.append(common)
        if changed:
            report['changedSockets'][id] = changed
            files[rp] = encoded(r)
    # New Uboinik diffuse goes in the BODY HALF only; preserve its separate shell texture.
    original = Image.open(io.BytesIO(old[P + 'skins/guns/metro_uboinik.png'])).convert('RGBA')
    body = Image.open(io.BytesIO(source['wpn34_bigun.png'])).convert('RGBA')
    atlas = Image.new('RGBA', (4096, 2048))
    atlas.paste(body.resize((2048, 2048), Image.Resampling.LANCZOS), (0, 0))
    atlas.paste(original.crop((1024, 0, 2048, 1024)).resize((2048, 2048), Image.Resampling.NEAREST), (2048, 0))
    files[P + 'skins/guns/metro_uboinik.png'] = png(atlas)
    report['uboinikTexture'] = {'body': 'wpn34_bigun.png', 'atlas': [4096, 2048], 'bodyUVUnchanged': True, 'shellTexturePreserved': True}
    # New models are separate gun IDs, never additional modelSkins on existing weapons.
    flame_texture_path = ROOT / 'tools/blackrazid-reference/assets/weapons/ognemet/wpn34_ognemet_01.2048.png'
    flame_image = Image.open(flame_texture_path).convert('RGBA')
    report['miniTextureRecovery'] = {'path': str(flame_texture_path.relative_to(ROOT)), 'sha256': digest(flame_texture_path.read_bytes()), 'reason': 'Same named original texture is missing from user archive'}
    for id, name, template, model, image in [
        ('metro_medved', 'Медведь', 'metro_uboinik', 'medved.obj', Image.open(io.BytesIO(source['wpn34_medved.png'])).convert('RGBA')),
        ('metro_ognemet_mini', 'Мини-огнемёт (адаптация MWF)', 'metro_ognemet', 'ognemet_mini.obj', flame_image),
    ]:
        g = json.loads(old['guns/' + template + '.json'])
        r = json.loads(old['guns/render/' + template + '.render.json'])
        doc = json.loads(old[P + 'gltf/guns/' + r['modelFileName']])
        raw = triangles(source[model])
        target = mesh_bounds(doc, ['gunModel'])
        scale, offset = fit(raw, target)
        wrapped = [piece for tri in raw for piece in wrap_triangle(tri)]
        tris = transformed(wrapped, scale, offset, gltf=True)
        gun_node = next(n for n in doc['nodes'] if n['name'] == 'gunModel')
        trs = {k: copy.deepcopy(v) for k, v in gun_node.items() if k in ('translation', 'rotation', 'scale')}
        # Do not retain template geometry, baked modules or loose cartridges floating on the new body.
        # Preserve the exact four hand nodes and all their animation data.
        for n in doc['nodes']:
            if n['name'] != 'root' and 'Arm' not in n['name']:
                n.pop('mesh', None)
                if n['name'].startswith(('ammoModel', 'bulletModel', 'shellModel')):
                    n['scale'] = [0, 0, 0]
        index = append_mesh(doc, 'newWeaponBody', tris, trs)
        gun_node['mesh'] = doc['nodes'][index]['mesh']
        # The helper node must not duplicate rendering of the same mesh.
        doc['nodes'][index].pop('mesh')
        r['modelFileName'] = id + '.gltf'; r['attachment'] = {}; r['defaultHidePart'] = []
        g.update(internalName=id, displayName=name, modelSkins=skin(id), acceptedAttachments={})
        g.pop('defaultAttachments', None)
        if g.get('acceptedAmmo'):
            previous = g['acceptedAmmo'][0]; ammo = id + '_ammo'
            a = json.loads(old['ammo/' + previous + '.json'])
            a.update(internalName=ammo, displayName='Баллон мини-огнемёта')
            # Model/config are shared but item ID and storage are independent.
            files['ammo/' + ammo + '.json'] = encoded(a)
            files['ammo/render/' + ammo + '.render.json'] = old['ammo/render/' + previous + '.render.json']
            for section in ('models/item/', 'textures/items/'):
                suffix = '.json' if section.startswith('models') else '.png'
                src = P + section + previous + suffix
                if src in old:
                    files[P + section + ammo + suffix] = old[src] if suffix == '.png' else encoded({'parent': 'item/generated', 'textures': {'layer0': 'modularwarfare:items/' + ammo}})
            g['acceptedAmmo'] = [ammo]; g['defaultAmmo'] = ammo
            r['attachment'][ammo] = {'scale': xyz([0, 0, 0])}
        files['guns/' + id + '.json'] = encoded(g)
        files['guns/render/' + id + '.render.json'] = encoded(r)
        files[P + 'gltf/guns/' + id + '.gltf'] = encoded(doc)
        files[P + 'skins/guns/' + id + '.png'] = png(image)
        files[P + 'textures/items/' + id + '.png'] = png(icon(raw, image))
        files[P + 'models/item/' + id + '.json'] = encoded({'parent': 'item/generated', 'textures': {'layer0': 'modularwarfare:items/' + id}, 'display': {'thirdperson_righthand': {'scale': [0, 0, 0]}, 'thirdperson_lefthand': {'scale': [0, 0, 0]}}})
        report['newWeapons'][id] = {'model': model, 'template': template, 'scale': scale, 'translation': list(map(float, offset)), 'statsInherited': True, 'handAndAnimationTRSPreserved': True, 'staticSourceModel': True}
    # Radiator: reuse a demonstrably empty tile; no rescaling/repacking any old UVs.
    id = 'metro_ubludok'
    g = json.loads(files['guns/' + id + '.json']); rp = 'guns/render/' + id + '.render.json'
    r = json.loads(files[rp]); gp = P + 'gltf/guns/' + r['modelFileName']; doc = json.loads(files[gp])
    atlas = Image.open(io.BytesIO(old[P + 'skins/guns/metro_native_atlas.png'])).convert('RGBA')
    free = [(x, y) for y in range(0, atlas.height, 512) for x in range(0, atlas.width, 512) if atlas.crop((x, y, x + 512, y + 512)).getbbox() is None]
    if not free:
        raise ValueError('No free texture tile for radiator')
    tx, ty = free[0]
    radiator_image = Image.open(io.BytesIO(source['wpn34_4knifes.png'])).convert('RGBA').resize((508, 508), Image.Resampling.LANCZOS)
    tile = Image.new('RGBA', (512, 512)); tile.paste(radiator_image, (2, 2))
    # Two-pixel edge padding avoids linear-sampling seams.
    tile.paste(radiator_image.crop((0, 0, 1, 508)).resize((2, 508)), (0, 2)); tile.paste(radiator_image.crop((507, 0, 508, 508)).resize((2, 508)), (510, 2))
    tile.paste(tile.crop((0, 2, 512, 3)).resize((512, 2)), (0, 0)); tile.paste(tile.crop((0, 509, 512, 510)).resize((512, 2)), (0, 510))
    atlas.paste(tile, (tx, ty))
    raw = triangles(source['ubludok_radiator.obj']); lo, hi = bounds(raw)
    bodylo, bodyhi = mesh_bounds(doc, ['gunModel'])
    muzzlelo, muzzlehi = mesh_bounds(doc, ['mod_Glushak_0']); centre = (muzzlelo + muzzlehi) / 2
    gun_node = next(n for n in doc['nodes'] if n['name'] == 'gunModel'); anchor = next(n for n in doc['nodes'] if n['name'] == 'ammoModel')
    scale = anchor['scale'][0] / gun_node['scale'][0]
    offset = np.array([bodyhi[0] - scale * hi[0], centre[1] - scale * (lo[1] + hi[1]) / 2, centre[2] - scale * (lo[2] + hi[2]) / 2])
    part = transformed([piece for tri in raw for piece in wrap_triangle(tri)], scale, offset)
    part = [[(p, ((tx + 2 + uv[0] * 508) / atlas.width, (ty + 2 + (1 - uv[1]) * 508) / atlas.height), n) for p, uv, n in t] for t in part]
    append_mesh(doc, 'ubludokRadiatorModel', part, {k: gun_node[k] for k in ('translation', 'scale')})
    # One previously transparent tile changes; every existing gun UV and atlas pixel
    # outside that tile remains identical. Avoid duplicating a 20 MB atlas per weapon.
    files[gp] = encoded(doc)
    files[P + 'skins/guns/metro_native_atlas.png'] = png(atlas)
    report['radiator'] = {'node': 'ubludokRadiatorModel', 'textureTile': [tx, ty, 512, 512], 'scale': float(scale), 'translation': list(map(float, offset)), 'bodyAndHandTRSPreserved': True}
    # Dedicated Uboinik items are no longer present/accepted. Back up/migrate stacks BEFORE updating.
    for id in RETIRED:
        for path in list(files):
            if Path(path).name in (id + '.json', id + '.render.json', id + '.obj', id + '.png'):
                del files[path]
    names = {'metro_medved': ('Медведь', 'Medved'), 'metro_ognemet_mini': ('Мини-огнемёт (адаптация MWF)', 'Mini flamethrower (MWF adaptation)'), 'metro_ognemet_mini_ammo': ('Баллон мини-огнемёта', 'Mini flamethrower canister')}
    for path in [p for p in files if p.startswith(P + 'lang/') and p.endswith('.lang')]:
        lines = [l for l in files[path].decode().splitlines() if not any(id in l.split('=', 1)[0] for id in RETIRED)]
        russian = '/ru_' in path.lower()
        lines.extend('item.' + id + '.name=' + display[0 if russian else 1] for id, display in names.items())
        files[path] = ('\n'.join(lines) + '\n').encode()
    manifest = json.loads(files['FULL_PACK_MANIFEST.json'])
    manifest['archive3dUpdate'] = report
    manifest['counts'] = {c: len([n for n in files if n.startswith(c + '/') and n.count('/') == 1 and n.endswith('.json')]) for c in ('guns', 'ammo', 'attachments', 'bullets')}
    # Historical manifests describe their base; don't silently claim hashes cover new geometry.
    files['FULL_PACK_MANIFEST.json'] = encoded(manifest)
    files['ARCHIVE_3D_UPDATE_MANIFEST.json'] = encoded(report)
    files['README_V8_RU.txt'] = ('Metro Full Arsenal v8 — 25 отдельных оружий.\nЗаменить прежний пак, не ставить рядом. MWF fix1-metro4 на клиенте и сервере.\nПеред обновлением заменить личные обвесы Убойника на общие и сохранить резервную копию мира:\n' + '\n'.join(a + ' -> ' + b for a, b in RETIRED.items()) + '\nМини-огнемёт — отдельный ID, пока bullet-based MWF адаптация, НЕ поток огня.\nМедведь: начальный профиль Убойника; исходные пневматические механики не добавлены.\nМодели, руки и крепления требуют проверки в игре. Minecraft здесь не запускался.\n').encode()
    return files


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--input', required=True); parser.add_argument('--source', default=str(ROOT / 'tools/mwf-3d/sources.zip')); parser.add_argument('--output', required=True)
    args = parser.parse_args()
    data = Path(args.input).read_bytes()
    if digest(data) != BASE_SHA:
        raise ValueError('Expected exact v7 pack; refusing to replace a different/newer baseline')
    with zipfile.ZipFile(io.BytesIO(data)) as z:
        files = {n: z.read(n) for n in z.namelist()}
    with zipfile.ZipFile(args.source) as z:
        if any('/' in n or '\\' in n or ':' in n or n.startswith('.') for n in z.namelist()) or sum(i.file_size for i in z.infolist()) > 100_000_000:
            raise ValueError('Unsafe source archive')
        source = {n: z.read(n) for n in z.namelist()}
    files = update(files, source)
    dest = Path(args.output); dest.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(dest, 'w', zipfile.ZIP_DEFLATED, compresslevel=9) as z:
        for name, value in sorted(files.items()):
            info = zipfile.ZipInfo(name, (2026, 10, 30, 0, 0, 0)); info.compress_type = zipfile.ZIP_DEFLATED
            z.writestr(info, value)
    print(dest, dest.stat().st_size, digest(dest.read_bytes()))


if __name__ == '__main__':
    main()
