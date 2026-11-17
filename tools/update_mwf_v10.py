#!/usr/bin/env python3
"""Pinned v9/metro5 -> v10/metro6: inventory icons, Velopal, Ashot stock, radiator.
Medved stays buckshot following the user's explicit clarification.
"""
import argparse
import base64
import copy
import hashlib
import importlib.util
import io
import json
import math
import os
from pathlib import Path
import shutil
import subprocess
import zipfile

import numpy as np
from PIL import Image, ImageDraw
from build_uboinik_pack import Gltf, icon
from patch_mwf_geometry import texture_tile
from build_mwf_motion import wrap, rotation

ROOT = Path(__file__).resolve().parents[1]
P = 'assets/modularwarfare/'
PACK_SHA = '3eeb9871c846b6c6769affa0dbdf2cdf94ffbaed7417b40b1a1f38bd497aebb5'
JAR_SHA = '5c413c6c28ce80f93715ccbb11f156d825ea0a846e81bc2c8134aa6a9416bf9b'
V7_SHA = '02871b5b661e7c5747ef77e41308c1621869111487abd1d259132455fb0c91ef'
JAR = 'modularwarfare-shining-2024.2.4.6f-fix1-metro6.jar'
PACK = 'Metro_Full_Arsenal_MWF_fix1_v10_icons.zip'


def sha(data): return hashlib.sha256(data).hexdigest()
def js(data): return (json.dumps(data, ensure_ascii=False, indent=2, allow_nan=False) + '\n').encode()
def readzip(path):
    with zipfile.ZipFile(path) as z: return {n: z.read(n) for n in z.namelist() if not n.endswith('/')}
def writezip(path, files):
    with zipfile.ZipFile(path, 'w', zipfile.ZIP_DEFLATED, compresslevel=9) as z:
        for n, b in sorted(files.items()):
            i = zipfile.ZipInfo(n, (2026, 10, 30, 0, 0, 0)); i.compress_type = zipfile.ZIP_DEFLATED
            z.writestr(i, b)
def png(image):
    out = io.BytesIO(); image.save(out, format='PNG', optimize=True, compress_level=9); return out.getvalue()
def run(args):
    result = subprocess.run(list(map(str, args)), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    print(result.stdout, end='', flush=True)
    if result.returncode: raise RuntimeError('Command failed: ' + str(args))
    return result.stdout


def accessor(doc, index):
    a = doc['accessors'][index]; v = doc['bufferViews'][a['bufferView']]
    data = base64.b64decode(doc['buffers'][v['buffer']]['uri'].split(',', 1)[1])
    size = {'SCALAR': 1, 'VEC2': 2, 'VEC3': 3, 'VEC4': 4}[a['type']]
    dtype = {5126: '<f4', 5123: '<u2', 5125: '<u4', 5121: 'u1'}[a['componentType']]
    start = v.get('byteOffset', 0) + a.get('byteOffset', 0)
    width = np.dtype(dtype).itemsize * size
    stride = v.get('byteStride', width)
    return np.ndarray((a['count'], size), dtype=dtype, buffer=data, offset=start, strides=(stride, np.dtype(dtype).itemsize)).copy()


def trs(node):
    if 'matrix' in node: return np.array(node['matrix']).reshape(4, 4).T
    x, y, z, w = node.get('rotation', [0, 0, 0, 1]); m = np.eye(4)
    m[:3, :3] = np.array([[1-2*(y*y+z*z), 2*(x*y-z*w), 2*(x*z+y*w)],
                          [2*(x*y+z*w), 1-2*(x*x+z*z), 2*(y*z-x*w)],
                          [2*(x*z-y*w), 2*(y*z+x*w), 1-2*(x*x+y*y)]]) @ np.diag(node.get('scale', [1, 1, 1]))
    m[:3, 3] = node.get('translation', [0, 0, 0]); return m


def world(doc, index):
    parent = next((i for i, n in enumerate(doc['nodes']) if index in n.get('children', [])), None)
    return (world(doc, parent) if parent is not None else np.eye(4)) @ trs(doc['nodes'][index])


def triangles(doc, config):
    """Rest pose without hands, muzzle flash or uninstalled baked attachments."""
    hidden = set(config.get('defaultHidePart', []))
    for i, node in enumerate(doc['nodes']):
        name = node['name']
        if 'mesh' not in node or 'Arm' in name or name in hidden or 'flash' in name.lower(): continue
        matrix = world(doc, i); normal = np.linalg.inv(matrix[:3, :3]).T
        for primitive in doc['meshes'][node['mesh']]['primitives']:
            attrs = primitive['attributes']; ps = accessor(doc, attrs['POSITION'])
            uvs = accessor(doc, attrs['TEXCOORD_0']); ns = accessor(doc, attrs['NORMAL'])
            indices = accessor(doc, primitive['indices']).flatten() if 'indices' in primitive else range(len(ps))
            for start in range(0, len(indices), 3):
                out = []
                for index in indices[start:start+3]:
                    p = (matrix @ [*ps[index], 1])[:3]; n = normal @ ns[index]; n /= np.linalg.norm(n) or 1
                    out.append((p, (float(uvs[index][0]), 1-float(uvs[index][1])), n))
                yield out


def ashot_profiles(doc):
    profiles = {}
    for side, delta, angles in [('left', [.07, -.02, 0], [0, -12, -9]), ('right', [.015, -.005, 0], [0, 0, 3])]:
        node = next(n for n in doc['nodes'] if n['name'] == side+'ArmModel')
        wrist = [.375 if side == 'left' else -.375, .75, 0, 1]
        pivot = (trs(node) @ wrist)[:3]; r = rotation(angles)
        matrix = np.eye(4); matrix[:3, :3] = r; matrix[:3, 3] = pivot + delta - r @ pivot
        name = 'metroMotionAshot' + side.capitalize()
        profile = {'gun': 'metro_ashot', 'attachment': 'metro_mod_ashot_butt', 'matrix': matrix.T.flatten().tolist()}
        indices = [i for i, n in enumerate(doc['nodes']) if n['name'] in (side+'ArmModel', side+'ArmSlimModel')]
        assert len(indices) == 2
        wrap(doc, indices, name, {}, profile); profiles[name] = profile
    return profiles


def prepare(files, v7, source):
    report = {'minecraftTested': False, 'basePackSha256': PACK_SHA, 'baseJarSha256': JAR_SHA,
              'medved': 'Unchanged buckshot, explicitly confirmed by user; no invented pneumatic conversion.',
              'limitations': ['No live Minecraft/Forge/Java8 test.', 'Ashot stock deltas overlay existing reload, not a new reload sequence.',
                              'Velopal original static geometry has no newly invented chamber/reload animation.',
                              'Radiator has no invented cooling/overheat stat; it is an optional barrel-slot geometry item.']}
    # The RAR diffuse belongs to the six-shot bicycle shotgun, NOT Uboinik.
    path = P+'gltf/guns/metro_velopal.gltf'; doc = json.loads(files[path])
    atlas = Image.open(io.BytesIO(files[P+'skins/guns/metro_native_atlas.png']))
    original = Image.open(ROOT/'tools/blackrazid-reference/assets/weapons/vyhlop/velo.png')
    x, y, w, h = texture_tile(atlas, original)
    body = next(n for n in doc['nodes'] if n['name'] == 'gunModel'); helper = Gltf()
    for primitive in doc['meshes'][body['mesh']]['primitives']:
        uv = accessor(doc, primitive['attributes']['TEXCOORD_0'])
        uv[:, 0] = (uv[:, 0]*atlas.width-x)/w; uv[:, 1] = (uv[:, 1]*atlas.height-y)/h
        assert uv.min() >= -1e-5 and uv.max() <= 1+1e-5
        index = helper.accessor(uv.tolist(), 2)
        primitive['attributes']['TEXCOORD_0'] = len(doc['accessors']) + index
    bo, vo = len(doc['buffers']), len(doc['bufferViews'])
    doc['buffers'].append({'byteLength': len(helper.data), 'uri': 'data:application/octet-stream;base64,'+base64.b64encode(helper.data).decode()})
    for view in helper.doc['bufferViews']: view['buffer'] += bo; doc['bufferViews'].append(view)
    for a in helper.doc['accessors']: a['bufferView'] += vo; doc['accessors'].append(a)
    files[path] = js(doc)
    files[P+'skins/guns/metro_velopal.png'] = source['wpn34_bigun.png']
    g = json.loads(files['guns/metro_velopal.json']); g['modelSkins'][0]['skinAsset'] = 'metro_velopal'
    g.pop('acceptedAmmo', None); g.pop('defaultAmmo', None)
    g.update(internalAmmoStorage=6, acceptedBullets=['metro_12g_buckshot'], defaultBullet='metro_12g_buckshot', weaponType='shotgun', numBullets=10)
    files['guns/metro_velopal.json'] = js(g)
    r = json.loads(files['guns/render/metro_velopal.render.json']); r['attachment'].pop('metro_vyhlop_ammo', None)
    files['guns/render/metro_velopal.render.json'] = js(r)
    files[P+'skins/guns/metro_uboinik.png'] = v7[P+'skins/guns/metro_uboinik.png']
    report['velopal'] = {'capacity': 6, 'ammo': 'metro_12g_buckshot', 'pellets': 10, 'texture': 'wpn34_bigun.png',
                         'textureSha256': sha(source['wpn34_bigun.png']), 'oldTile': [x, y, w, h], 'oldAtlasSize': list(atlas.size),
                         'geometryAndHandPosePreserved': True, 'uboinikTextureRestoredFromV7': True}
    path = P+'gltf/guns/metro_ashot.gltf'; doc = json.loads(files[path])
    profiles = json.loads(files[P+'metro_motion.json']); profiles.update(ashot_profiles(doc))
    files[path] = js(doc); files[P+'metro_motion.json'] = js(profiles)
    report['ashot'] = {'stock': 'metro_mod_ashot_butt', 'noStockUnchanged': True, 'bothArmsClassicSlim': True}
    # Keep the existing fitted radiator triangles/UV/socket, only change visibility.
    path = 'guns/render/metro_ubludok.render.json'; r = json.loads(files[path]); ident = 'metro_mod_ubludok_radiator'
    r['defaultHidePart'].append('ubludokRadiatorModel')
    r['attachment'][ident] = {'binding': 'gunModel', 'scale': dict(x=0, y=0, z=0), 'showPart': ['ubludokRadiatorModel'], 'hidePart': []}
    files[path] = js(r)
    path = 'guns/metro_ubludok.json'; g = json.loads(files[path]); g['acceptedAttachments']['barrel'].append(ident)
    files[path] = js(g)
    attachment = {'internalName': ident, 'displayName': 'Радиатор для Ублюдка', 'maxStackSize': 1, 'attachmentType': 'barrel',
                  'sameTextureAsGun': False, 'modelSkins': [{'internalName': ident, 'skinAsset': ident, 'displayName': 'Оригинал'}],
                  'barrel': {'isSuppressor': False, 'hideFlash': False, 'recoilPitchFactor': 1, 'recoilYawFactor': 1, 'accuracyFactor': 1}}
    files['attachments/'+ident+'.json'] = js(attachment)
    files['attachments/render/'+ident+'.render.json'] = js({'modelFileName': ident+'.obj', 'extra': {'modelScale': 1}})
    # Item OBJ for the attachment catalogue; the gun uses its already-fitted baked mesh.
    from integrate_mwf_3d import triangles as obj_triangles
    from build_full_mwf_pack import obj_bytes
    raw = obj_triangles(source['ubludok_radiator.obj'])
    files['obj/attachments/'+ident+'.obj'] = obj_bytes([('attachmentModel', raw)])
    files[P+'skins/attachments/'+ident+'.png'] = source['wpn34_4knifes.png']
    files[P+'textures/items/'+ident+'.png'] = png(icon(raw, Image.open(io.BytesIO(source['wpn34_4knifes.png'])), size=256))
    files[P+'models/item/'+ident+'.json'] = js({'parent': 'item/generated', 'textures': {'layer0': 'modularwarfare:items/'+ident}})
    for path in [n for n in files if n.startswith(P+'lang/') and n.endswith('.lang')]:
        name = 'Радиатор для Ублюдка' if '/ru_' in path.lower() else 'Ubludok radiator'
        files[path] = files[path].rstrip(b'\n') + ('\nitem.'+ident+'.name='+name+'\n').encode()
    report['radiator'] = {'id': ident, 'slot': 'barrel', 'defaultInstalled': False, 'exclusiveWithSuppressor': True, 'oldMeshPreserved': True}
    return report


def compile_client(args, profiles):
    work = args.work; work.mkdir(parents=True, exist_ok=True)
    spec = importlib.util.spec_from_file_location('support', ROOT/'tools/mwf-mechanics/test_support.py')
    support = importlib.util.module_from_spec(spec); spec.loader.exec_module(support)
    stubs, helpers, tests, tool = [work/n for n in ('stubs', 'helpers', 'tests', 'tool')]
    for p in (stubs, helpers, tests, tool):
        if p.exists(): shutil.rmtree(p)
        p.mkdir()
    sources = work/'stub-sources'; facades = support.materialize(sources)
    (sources/'io/netty/buffer/ByteBuf.java').write_text('''package io.netty.buffer; public class ByteBuf {
    private final java.nio.ByteBuffer b=java.nio.ByteBuffer.allocate(1024); private int read;
    public ByteBuf writeByte(int n){b.put((byte)n);return this;}public byte readByte(){return b.get(read++);}
    public short readUnsignedByte(){return(short)(readByte()&255);}public ByteBuf writeInt(int n){b.putInt(n);return this;}
    public int readInt(){int v=b.getInt(read);read+=4;return v;}}''')
    java8 = ['--release', '8'] if args.java8_mode == 'release' else ['-source', '8', '-target', '8']
    run([args.javac, '-proc:none', *java8, '-d', stubs, *facades])
    run([args.javac, '-proc:none', *java8, '-cp', os.pathsep.join(map(str, [stubs, args.jar])), '-d', helpers,
         ROOT/'tools/mwf-v10/src/ru/metrocraft/mwf/MotionClient.java'])
    jarfiles = readzip(args.jar)
    # Only the main helper changes; its three inner-class layouts are unchanged.
    jarfiles['ru/metrocraft/mwf/MotionClient.class'] = (helpers/'ru/metrocraft/mwf/MotionClient.class').read_bytes()
    version = 'com/modularwarfare/ModularWarfare.class'
    assert jarfiles[version].count(b'2024.2.4.6f-metro5') > 0
    jarfiles[version] = jarfiles[version].replace(b'2024.2.4.6f-metro5', b'2024.2.4.6f-metro6')
    jarfiles[P+'metro_motion.json'] = profiles
    jar = args.output/JAR; writezip(jar, jarfiles)
    backend = support.materialize(work/'backend', True)
    cp = os.pathsep.join(map(str, [tests, stubs, jar]))
    run([args.javac, '-proc:none', *java8, '-cp', cp, '-d', tests, *backend,
         ROOT/'tools/mwf-mechanics/tests/MechanicsTest.java', ROOT/'tools/mwf-motion/tests/MotionTest.java',
         ROOT/'tools/mwf-v10/AshotPoseTest.java'])
    run([args.javac, '-proc:none', '-cp', args.asm, '-d', tool, ROOT/'tools/mwf-motion/NativeTestMethods.java'])
    run([args.java, '-cp', os.pathsep.join(map(str, [tool, args.asm])), 'NativeTestMethods', jar, tests])
    log = ''
    for name in ('MechanicsTest', 'MotionTest', 'AshotPoseTest'):
        params = [] if name != 'AshotPoseTest' else [str(v) for side in ('Left', 'Right') for v in json.loads(profiles)['metroMotionAshot'+side]['matrix']]
        log += run([args.java, '-Xverify:all', '-cp', cp, 'ru.metrocraft.mwf.'+name, *params])
    # Keep machine-specific JVM warning paths out of deterministic archive metadata.
    return jar, '\n'.join(line for line in log.splitlines() if 'TESTS_OK ' in line) + '\n'


def make_icons(files, report, contact):
    icons = []; report['icons'] = {}
    for path in sorted(n for n in files if n.startswith('guns/') and n.count('/') == 1 and n.endswith('.json')):
        g = json.loads(files[path]); ident = g['internalName']
        cfg = json.loads(files['guns/render/'+ident+'.render.json']); doc = json.loads(files[P+'gltf/guns/'+cfg['modelFileName']])
        skin = g['modelSkins'][0]['skinAsset']; tex = Image.open(io.BytesIO(files[P+'skins/guns/'+skin+'.png']))
        tris = list(triangles(doc, cfg))
        # Detachable magazines are separate MWF OBJ, not embedded gun nodes.
        if g.get('acceptedAmmo'):
            from build_uboinik_pack import obj_read
            from integrate_mwf_3d import render_matrix
            ammo = g.get('defaultAmmo', g['acceptedAmmo'][0]); a = json.loads(files['ammo/'+ammo+'.json'])
            if a.get('isDynamicAmmo') and a['modelSkins'][0]['skinAsset'] == skin:
                ar = json.loads(files['ammo/render/'+ammo+'.render.json']); node = next(n for n in doc['nodes'] if n['name'] == 'ammoModel')
                index = doc['nodes'].index(node); matrix = world(doc, index) @ render_matrix(cfg['attachment'].get(ammo, {}))
                matrix[:3, :3] *= ar.get('extra', {}).get('modelScale', 1)
                for name, group in obj_read(files['obj/ammo/'+ar['modelFileName']]).items():
                    if name != 'ammoModel': continue
                    for tri in group:
                        tris.append([((matrix @ [*p, 1])[:3], uv, np.array(n)) for p, uv, n in tri])
        image = icon(tris, tex, size=384).resize((256, 256), Image.Resampling.LANCZOS)
        path = P+'textures/items/'+ident+'.png'; data = png(image); files[path] = data
        report['icons'][ident] = {'sha256': sha(data), 'size': [256, 256], 'triangles': len(tris), 'method': 'textured actual rest mesh, no hands/uninstalled modules; supersampled raster'}
        icons.append((g['displayName'], image)); print('ICON', ident, len(tris), flush=True)
    assert len(icons) == 25
    contact.parent.mkdir(parents=True, exist_ok=True)
    sheet = Image.new('RGB', (5*280, 5*180), '#333a3e'); draw = ImageDraw.Draw(sheet)
    for i, (name, image) in enumerate(icons):
        image = image.copy(); image.thumbnail((270, 140)); x, y = (i%5)*280+5, (i//5)*180+5
        sheet.paste(image, (x, y), image); draw.text((x+5, y+145), name, fill='white')
    sheet.save(contact)


def main(args):
    assert sha(args.pack.read_bytes()) == PACK_SHA
    assert sha(args.jar.read_bytes()) == JAR_SHA
    assert sha(args.v7.read_bytes()) == V7_SHA
    files = readzip(args.pack); old = copy.copy(files)
    source = readzip(ROOT/'tools/mwf-3d/sources.zip'); report = prepare(files, readzip(args.v7), source)
    args.output.mkdir(parents=True, exist_ok=True)
    jar, testlog = compile_client(args, files[P+'metro_motion.json'])
    make_icons(files, report, ROOT/'artifacts/mwf-v10/icons-contact.png')
    report['requiresJarSha256'] = sha(jar.read_bytes()); report['testLog'] = testlog
    manifest = json.loads(files['FULL_PACK_MANIFEST.json'])
    manifest['inventoryV10Update'] = report; manifest['mechanicsRequiresJarSha256'] = sha(jar.read_bytes()); manifest['integratedMechanicsVersion'] = '1.5.0'
    manifest['counts'] = {c: sum(n.startswith(c+'/') and n.count('/') == 1 and n.endswith('.json') for n in files) for c in ('guns', 'attachments', 'ammo', 'bullets')}
    # Historical provenance is retained; the effective motion dependency now points to metro6.
    for target in ('MOTION_UPDATE_MANIFEST.json',):
        m = json.loads(files[target]); m['requiresJarSha256'] = sha(jar.read_bytes()); m['profiles'] = json.loads(files[P+'metro_motion.json']); files[target] = js(m)
        manifest['motionUpdate'] = m
    files['FULL_PACK_MANIFEST.json'] = js(manifest)
    report['changedFiles'] = sorted(n for n in old if old[n] != files[n]); report['addedFiles'] = sorted(set(files)-set(old))
    files['INVENTORY_V10_UPDATE_MANIFEST.json'] = js(report)
    files['README_V10_RU.txt'] = ('v10: заменить прежний ZIP; поставить metro6 JAR вместо metro5 на клиент и сервер. Не держать старые рядом.\n'
        '25 новых иконок; Велопал — 6 дробовых патронов, текстура wpn34_bigun.png из пользовательского RAR.\n'
        'Ашот: приклад автоматически меняет хват двух рук, без приклада исходная поза.\n'
        'Радиатор Ублюдка — отдельный обвес в слоте barrel, по умолчанию снят.\n'
        'Медведь оставлен дробовиком по уточнению пользователя.\n'
        'Сделайте копию мира. Перед обновлением разгрузите старые Велопалы: магазин Выхлопа больше не принимается.\n'
        'Для старых NBT-стеков Велопала при необходимости возьмите новый предмет после возврата патронов.\n'
        'Автотесты не заменяют проверку в Minecraft; игра здесь не запускалась.\n').encode()
    writezip(args.output/PACK, files)
    report['jarSha256'] = sha(jar.read_bytes()); report['packSha256'] = sha((args.output/PACK).read_bytes())
    (args.output/'MWF_V10_report.json').write_bytes(js(report))
    print('OUTPUT', report['jarSha256'], report['packSha256'])


if __name__ == '__main__':
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('--pack', type=Path, required=True); p.add_argument('--jar', type=Path, required=True); p.add_argument('--v7', type=Path, required=True)
    p.add_argument('--asm', type=Path, required=True); p.add_argument('--javac', default='javac'); p.add_argument('--java', default='java')
    p.add_argument('--java8-mode', choices=['release', 'source-target'], default='release')
    p.add_argument('--work', type=Path, default=ROOT/'.cache/mwf-v10'); p.add_argument('--output', type=Path, default=ROOT/'downloads')
    main(p.parse_args())
