#!/usr/bin/env python3
"""Add existing attachment compatibility to the published MWF arsenal, without rebuilding assets.

Only gun acceptedAttachments, matching per-gun render mounts and release metadata change.
No texture, OBJ, glTF, ammo, damage, animation, optic effect or special mechanic is modified.
Mounts reuse the existing OBJ meshes; their visual fit/ADS still needs Minecraft validation.
"""
import argparse
import hashlib
import json
import math
import os
from pathlib import Path
import statistics
import struct
import base64
import zipfile

ROOT = Path(__file__).resolve().parents[1]
BASE_SHA256 = '0403f8fced602d38ce21300edcc2779bc1b79403a0a48fab1fb5662d9614b915'
OUTPUT = ROOT / 'downloads/Metro_Full_Arsenal_MWF_fix1_v2_compat.zip'
# Seven audited missing connections plus five AK-family connections for the extra AKK model.
ADDITIONS = {
    'metro_duplet': {'laser': ['metro_mod_laser']},
    'metro_velopal': {'laser': ['metro_mod_laser']},
    'metro_aksu': {'barrel': ['metro_mod_glushak']},
    'metro_saiga': {'barrel': ['metro_mod_glushak']},
    'metro_uboinik': {'barrel': ['metro_mod_uboinik_barrel'], 'sight': ['metro_mod_optica_small']},
    'metro_ventil': {'sight': ['metro_mod_optica']},
    'metro_akk': {
        'sight': ['metro_mod_optica', 'metro_mod_optica_small', 'metro_mod_kolimator'],
        'laser': ['metro_mod_laser'], 'barrel': ['metro_mod_glushak'],
    },
}
RELEASE_NOTE = '''METRO FULL ARSENAL / MWF fix1 / v2 — СОВМЕСТИМОСТЬ

Заменяет полный пак v1. Положить только этот ZIP в .minecraft/ModularWarfare/;
старый полный пак и отдельные паки Убойника/Дуплета убрать во избежание дублей ID.
На сервере нужен тот же ZIP. MWF JAR не меняется; дополнительных модов нет.
Сделайте резервную копию мира и полностью перезапустите игру/сервер.

Добавлены существующие обвесы (12 связей, 7 оружий):
* Дуплет и Велопал — metro_mod_laser.
* АКСУ и Сайга — metro_mod_glushak.
* Убойник — metro_mod_uboinik_barrel и metro_mod_optica_small (IRNV).
* Вентиль — metro_mod_optica.
* AKK — стандартные обвесы семейства АК: три прицела, ЛЦУ и глушитель.
  Это совместимость дополнительной модели пака, а не отдельный набор из Wiki.

Для каждой связи добавлено индивидуальное крепление OBJ в render-конфиге.
Использованы исходные модели и их собственные текстуры, без новых моделей,
перепаковки атласов, изменения UV или подмены текстур Убойника/Дуплета.
Посадки рассчитаны по геометрии; визуальная проверка и доводка ADS в Minecraft
НЕ выполнены. Это не восстановленные авторские сокеты из старого рендера.

ВАЖНО: совместимость с предметом «Оптика» не добавляет настоящий режим 4×,
а совместимость с IRNV не реализует ПНВ. Эффекты оптики — отдельная задача.
Калибры, магазины, вместимость, урон, режимы огня, позы рук и анимации сохранены.
Дуплет остаётся на 2 патрона, +2 ствола — ДЕКОР. Bridge/companion не добавлен.
Радиатор, клапаны, штык и прочие отсутствующие модели ждут отдельной поставки.
Механики пневматики, энергии, автоадаптеров и Gatling не изменялись.

Подробности: compatibilityUpdate в FULL_PACK_MANIFEST.json.
'''


def encode(value):
    return (json.dumps(value, ensure_ascii=False, indent=2, allow_nan=False) + '\n').encode('utf-8')


def xyz(values):
    return dict(zip('xyz', (round(float(v), 8) for v in values)))


def bounds(points):
    if not points:
        raise ValueError('Empty geometry')
    return ([min(p[i] for p in points) for i in range(3)],
            [max(p[i] for p in points) for i in range(3)])


def obj_points(files, attachment):
    render = json.loads(files[f'attachments/render/{attachment}.render.json'])
    text = files['obj/attachments/' + render['modelFileName']].decode('utf-8')
    return [tuple(map(float, line.split()[1:4])) for line in text.splitlines() if line.startswith('v ')]


def gun_points(files, gun, node_name='gunModel'):
    doc = json.loads(files[f'assets/modularwarfare/gltf/guns/{gun}.gltf'])
    node = next(n for n in doc['nodes'] if n['name'] == node_name)
    points = []
    for primitive in doc['meshes'][node['mesh']]['primitives']:
        accessor = doc['accessors'][primitive['attributes']['POSITION']]
        view = doc['bufferViews'][accessor['bufferView']]
        assert accessor['componentType'] == 5126 and accessor['type'] == 'VEC3'
        data = base64.b64decode(doc['buffers'][view['buffer']]['uri'].split(',', 1)[1], validate=True)
        offset = view.get('byteOffset', 0) + accessor.get('byteOffset', 0)
        stride = view.get('byteStride', 12)
        points.extend(struct.unpack_from('<fff', data, offset + i * stride) for i in range(accessor['count']))
    return points


def fit_mount(files, gun, attachment, slot):
    # OBJ modules use +Z forward; the fitted gun mesh uses +X forward.
    rotated = [(z, y, -x) for x, y, z in obj_points(files, attachment)]
    lo, hi = bounds(rotated)
    body = gun_points(files, gun)
    blo, bhi = bounds(body)
    scale = 1.0
    if slot == 'barrel':
        # The rear of the reused module overlaps the muzzle by 12 mm.
        front = [p for p in body if p[0] >= bhi[0] - .012]
        center_y = statistics.median(p[1] for p in front)
        center_z = statistics.median(p[2] for p in front)
        translate = [bhi[0] - .012 - lo[0], center_y - (lo[1] + hi[1]) / 2,
                     center_z - (lo[2] + hi[2]) / 2]
    elif slot == 'laser':
        # Side mount near the front of the fore-end, sized to the shorter Duplet.
        center_x = .17 if gun == 'metro_duplet' else .20
        section = [p for p in body if abs(p[0] - center_x) < .025]
        if not section:
            raise ValueError(f'No fore-end geometry for {gun}')
        slo, shi = bounds(section)
        scale = .85 if gun == 'metro_duplet' else 1.0
        center_y = (slo[1] + shi[1]) / 2
        # Four mm overlap of the clamp with the side of the fore-end.
        translate = [center_x - scale * (lo[0] + hi[0]) / 2,
                     center_y - scale * (lo[1] + hi[1]) / 2,
                     slo[2] + .004 - scale * hi[2]]
    elif gun == 'metro_uboinik':
        # Match the current uploaded collimator's receiver mount, not the old native Ubo model.
        clo, chi = bounds(obj_points(files, 'metro_uboinik_collimator'))
        translate = [(clo[0] + chi[0] - lo[0] - hi[0]) / 2,
                     clo[1] - lo[1], (clo[2] + chi[2] - lo[2] - hi[2]) / 2]
    elif gun == 'metro_ventil':
        # Reuse the existing IR optic's attachment location/physical scale.
        nlo, nhi = bounds(gun_points(files, gun, 'mod_OpticaSmall_0'))
        ir_rotated = [(z, y, -x) for x, y, z in obj_points(files, 'metro_mod_optica_small')]
        ilo, ihi = bounds(ir_rotated)
        scale = (nhi[0] - nlo[0]) / (ihi[0] - ilo[0])
        translate = [(nlo[0] + nhi[0] - scale * (lo[0] + hi[0])) / 2,
                     nlo[1] - scale * lo[1], (nlo[2] + nhi[2] - scale * (lo[2] + hi[2])) / 2]
    else:
        assert gun == 'metro_akk' and slot == 'sight'
        section = [p for p in body if abs(p[0] + .20) < .025]
        slo, shi = bounds(section)
        translate = [-.20 - (lo[0] + hi[0]) / 2, shi[1] + .001 - lo[1],
                     (slo[2] + shi[2] - lo[2] - hi[2]) / 2]
    values = translate + [scale]
    assert all(math.isfinite(x) for x in values) and .1 < scale < 4
    return {'binding': 'gunModel', 'translate': xyz(translate), 'rotate': xyz([0, 90, 0]),
            'scale': xyz([scale] * 3), 'sightAimPosOffset': xyz([0, 0, 0])}


def patch(files):
    result = dict(files)
    records = []
    for gun, slots in ADDITIONS.items():
        gun_path, render_path = f'guns/{gun}.json', f'guns/render/{gun}.render.json'
        entry, render = json.loads(files[gun_path]), json.loads(files[render_path])
        for slot, attachments in slots.items():
            accepted = entry.setdefault('acceptedAttachments', {}).setdefault(slot, [])
            for attachment in attachments:
                module = json.loads(files[f'attachments/{attachment}.json'])
                assert module['attachmentType'] == slot and not module.get('sameTextureAsGun')
                if attachment not in accepted:
                    accepted.append(attachment)
                render.setdefault('attachment', {})[attachment] = fit_mount(files, gun, attachment, slot)
                records.append({'gun': gun, 'slot': slot, 'attachment': attachment})
        result[gun_path], result[render_path] = encode(entry), encode(render)
    manifest = json.loads(files['FULL_PACK_MANIFEST.json'])
    resolved = [s for s in manifest.get('omittedSockets', [])
                if (s['gun'], s['module']) in {('Saiga', 'Glushak'), ('Ventil', 'Optica')}]
    previous = manifest.get('compatibilityUpdate', {})
    manifest['omittedSockets'] = [s for s in manifest.get('omittedSockets', []) if s not in resolved]
    manifest['compatibilityUpdate'] = {
        'version': 'v2_compat', 'baselineSha256': BASE_SHA256, 'connections': records,
        'resolvedOriginalOmissions': resolved or previous.get('resolvedOriginalOmissions', []),
        'placement': 'Existing OBJ geometry, per-gun bounds-based mounts; Minecraft fit/ADS unverified.',
        'akkPolicy': 'Extra model receives AK-family attachment compatibility; not a separate Wiki weapon.',
        'newModels': False, 'newTextures': False, 'gameTested': False,
        'opticEffectsChanged': False, 'specialMechanicsChanged': False,
    }
    for gun, slots in ADDITIONS.items():
        manifest['guns'][gun]['compatibilityAdditions'] = [a for ids in slots.values() for a in ids]
    manifest['limitations'] = [
        'AKK uses AK-family compatibility with provisional bounds-based mounts, not recovered original sockets.'
        if s.startswith('AKK resting assembly is recovered,') else s for s in manifest['limitations']]
    result['FULL_PACK_MANIFEST.json'] = encode(manifest)
    readme = files['README_FULL_PACK_RU.txt'].decode('utf-8')
    marker = '\n\nОБНОВЛЕНИЕ v2 — СОВМЕСТИМОСТЬ\n'
    readme = readme.split(marker)[0]
    result['README_FULL_PACK_RU.txt'] = (readme + marker + RELEASE_NOTE).encode('utf-8')
    result['README_COMPATIBILITY_V2_RU.txt'] = RELEASE_NOTE.encode('utf-8')
    return result


def build(source, destination=OUTPUT):
    source, destination = Path(source), Path(destination)
    if source.resolve() == destination.resolve():
        raise ValueError('Do not overwrite the baseline pack')
    if hashlib.sha256(source.read_bytes()).hexdigest() != BASE_SHA256:
        raise ValueError('Expected the exact published v1 pack; input SHA-256 mismatch')
    with zipfile.ZipFile(source) as archive:
        if archive.testzip() or len(archive.namelist()) != len(set(archive.namelist())):
            raise ValueError('Corrupt or duplicate ZIP entries')
        info = {i.filename: i for i in archive.infolist()}
        files = {name: archive.read(name) for name in info}
    result = patch(files)
    destination.parent.mkdir(parents=True, exist_ok=True)
    temporary = destination.with_suffix('.zip.tmp')
    try:
        with zipfile.ZipFile(temporary, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=9) as archive:
            for name in sorted(result):
                metadata = info.get(name) or zipfile.ZipInfo(name, (2026, 11, 3, 0, 0, 0))
                metadata.compress_type = zipfile.ZIP_DEFLATED
                archive.writestr(metadata, result[name], compresslevel=9)
        os.replace(temporary, destination)
    finally:
        temporary.unlink(missing_ok=True)
    changed = [n for n in result if files.get(n) != result[n]]
    summary = {'output': str(destination), 'bytes': destination.stat().st_size,
               'sha256': hashlib.sha256(destination.read_bytes()).hexdigest(),
               'connections': sum(len(ids) for slots in ADDITIONS.values() for ids in slots.values()),
               'gunsUpdated': len(ADDITIONS), 'changedEntries': changed, 'gameTested': False}
    print(json.dumps(summary, ensure_ascii=False, indent=2))
    return summary


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--source', type=Path, default=ROOT / 'downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip')
    parser.add_argument('--output', type=Path, default=OUTPUT)
    args = parser.parse_args()
    build(args.source, args.output)
