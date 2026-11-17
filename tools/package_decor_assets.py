#!/usr/bin/env python3
"""Bundle existing METRO_MOD gates/doors/lights, without re-authoring their animation.

Standard library only. Native resources are kept verbatim except the explicitly
filtered catalogues. Portable OBJ differs only in its mtllib declaration.
"""
import argparse
import copy
import csv
import gzip
import hashlib
import io
import json
import math
import posixpath
import struct
import subprocess
import zipfile
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'src/main/resources/assets/metroport'
KINDS = {'Germo': '01_Germo_Gates', 'Door': '02_Doors', 'Light': '03_Lamps'}
OUTPUT = ROOT / 'downloads/MinETRO_Decor_Germo_Doors_Lamps_v1.zip'
IDENTITY = [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1]


def js(value):
    return (json.dumps(value, ensure_ascii=False, indent=2, allow_nan=False) + '\n').encode('utf-8')


def sha(data):
    return hashlib.sha256(data).hexdigest()


def resource(ref):
    namespace, path = ref.split(':', 1)
    if namespace != 'metroport' or '..' in Path(path).parts or path.startswith('/'):
        raise ValueError('Unexpected resource ' + ref)
    return 'assets/metroport/' + path


def frames(part):
    if 'frames' in part:
        return part['frames']
    # Same translation fallback as OriginalGateProfile.Part.matrix(progress).
    result = []
    for i in range(65):
        m = IDENTITY[:]
        for axis in range(3):
            m[axis * 4 + 3] = part['move'][axis] * i / 64
        result.append(m)
    return result


README = '''# MinETRO — гермоворота, обычные двери и лампы

75 объектов из уже перенесённого каталога METRO_MOD:
17 гермоворот/ворот, 27 обычных дверей и 31 светильник.
Без оружия, мебели, ящиков и рубильников.

ЭТО АРХИВ РЕСУРСОВ И АНИМАЦИОННЫХ ДАННЫХ, НЕ ГОТОВЫЙ МОД.
ZIP не нужно класть в mods или устанавливать как обычный ресурспак.

## Где что лежит

objects/01_Germo_Gates/ — ворота, каждый объект в отдельной папке.
objects/02_Doors/       — обычные двери.
objects/03_Lamps/       — лампы и светильники.
В каждой папке: part_XX.obj, part_XX.mtl и animation.json.
OBJ можно открыть в Blender/другом редакторе с поддержкой Wavefront.
Распаковать ВЕСЬ ZIP: MTL ссылается относительным путём на общую папку
assets/metroport/textures, не копировать один OBJ отдельно.

assets/metroport/ — исходные OBJ.GZ, PNG, профили анимаций, посадка и коллизии
в формате существующего MinETRO Forge 1.12.2. OBJ.GZ сохранены побайтно.
reference/java/ — существующий код рендера/движения/переключения/коллизии
для справки при интеграции, НЕ самостоятельный компилируемый проект.
CATALOG_RU.csv — нумерованный список, имена и пути к объектам.
MANIFEST.json — полный список файлов, их SHA-256 и источник.

## Анимация

Wavefront OBJ сам по себе не хранит анимацию. Она лежит в animation.json:
матрицы каждой детали в 65 точках нормализованного progress от 0 до 1.
У обычных дверей/новых ворот это сохранённые исходные выборки рендера.
У шести прежних ворот move переведён в эквивалентные матрицы переноса.
Матрицы ROW-MAJOR, используются столбцы-векторы [x,y,z,1].
Перенос — элементы 3,7,11 (не 12,13,14).
При progress p: frame=p*64; линейно смешать соседние матрицы поэлементно.
Полный локальный transform = T(root_translation) * part_matrix(p).
root_translation учитывает ground/offset из актуальной посадки.
Дополнительный поворот блока и его мировое положение задаются приложением.
Шаг из profile — адаптированный шаг за тик, не доказанный тайминг Metro.
Для проигрывания в Blender требуется импорт этих матриц в ключи/скрипт:
автоматического воспроизведения от простого открытия OBJ не будет.

У 31 лампы НЕТ движения геометрии в имеющихся профилях: они статичны.
В Minecraft у них переключается освещение, а не выдуманная механическая
анимация. Это явно помечено в animation.json и CATALOG_RU.csv.
Реализация вкл/выкл светового уровня Forge есть в reference/java.
Донорские GLSL-эффекты, топливо и электросеть здесь не восстановлены.

Не вошла structure/mls_vorota_l06: отсутствует обязательная деталь add2;
неполная модель не выдаётся за готовые ворота. Старые дубли из других паков
и декоративные рубильники не добавлены. Текстуры — имеющиеся diffuse PNG;
отсутствующие normal/specular-карты не выдумывались.

Геометрия/UV/текстуры и старые матрицы в native-ресурсах не переделывались.
Проверены ZIP CRC, существование всех ссылок, PNG, OBJ-материалы, матрицы,
количество объектов, отсутствие лишних категорий и SHA-256 ресурсов.
Новая проверка в Minecraft/OpenGL не выполнялась; известные ограничения
посадки/коллизии существующего порта этим архивом не исправляются.

Авторство исходного Metro/METRO_MOD-контента остаётся за правообладателями.
Передано из предоставленных пользователем материалов, не новая авторская
коллекция и не переоформление лицензии. См. ATTRIBUTION_RU.txt.
'''


def build(output=OUTPUT):
    files, originals = {}, {}

    def put(name, data):
        if name in files and files[name] != data:
            raise ValueError('Conflicting archive path ' + name)
        files[name] = data

    def original(path):
        path = Path(path)
        data = path.read_bytes()
        originals[path.relative_to(ROOT).as_posix()] = sha(data)
        return data

    def load(name):
        return json.loads(original(RES / name))

    legacy = load('metromod_gates.json')
    interactive = [p for p in load('metromod_interactive.json') if p['kind'] in KINDS]
    profiles = [dict(p, kind='Germo') for p in legacy] + interactive
    assert Counter(p['kind'] for p in profiles) == {'Germo': 17, 'Door': 27, 'Light': 31}
    ids = {p['id'] for p in profiles}
    assert len(ids) == 75
    layout, ground = load('metromod_layout.json'), load('metromod_ground.json')
    put('assets/metroport/metromod_gates.json', original(RES / 'metromod_gates.json'))
    put('assets/metroport/metromod_interactive.json', js(interactive))
    for name in ('metromod_layout.json', 'metromod_ground.json',
                 'metromod_door_collision.json', 'metromod_germo_collision.json'):
        put('assets/metroport/' + name, js({k: v for k, v in load(name).items() if k in ids}))

    catalog = []
    for p in profiles:
        folder = 'objects/' + KINDS[p['kind']] + '/' + p['id']
        offset = layout.get(p['id'], {}).get('offset', [0, 0, 0])
        root = [offset[0], ground.get(p['id'], p['ground']) + offset[1], offset[2]]
        animation = {'id': p['id'], 'name_ru': p['name_ru'], 'kind': p['kind'],
                     'source': p['source'], 'matrix_order': 'row-major',
                     'vectors': 'column', 'progress': [0, 1], 'step_per_tick': p['step'],
                     'root_translation': root, 'parts': []}
        moving = False
        for index, part in enumerate(p['parts']):
            native = resource(part['mesh'])
            data = original(ROOT / 'src/main/resources' / native)
            put(native, data)
            text = gzip.decompress(data).decode('utf-8')
            used = {line.split(None, 1)[1].strip() for line in text.splitlines() if line.startswith('usemtl ')}
            assert used and used <= part['materials'].keys(), (p['id'], used)
            stem = f'part_{index:02}'
            # Do not edit vertices, UVs, normals, groups or winding.
            obj = 'mtllib ' + stem + '.mtl\n' + '\n'.join(
                line for line in text.splitlines() if not line.startswith('mtllib ')) + '\n'
            put(folder + '/' + stem + '.obj', obj.encode('utf-8'))
            mtl = []
            for label, ref in sorted(part['materials'].items()):
                tex = resource(ref)
                payload = original(ROOT / 'src/main/resources' / tex)
                assert payload[:8] == b'\x89PNG\r\n\x1a\n' and all(struct.unpack('>II', payload[16:24]))
                put(tex, payload)
                mtl += ['newmtl ' + label, 'Kd 1 1 1', 'd 1', 'illum 1',
                        'map_Kd ' + posixpath.relpath(tex, folder), '']
            put(folder + '/' + stem + '.mtl', '\n'.join(mtl).encode('utf-8'))
            matrices = frames(part)
            assert len(matrices) == 65
            assert all(len(m) == 16 and all(math.isfinite(v) for v in m) and m[12:] == [0, 0, 0, 1] for m in matrices)
            animated = any(m != matrices[0] for m in matrices[1:])
            moving |= animated
            animation['parts'].append({'obj': stem + '.obj', 'native_mesh': part['mesh'],
                                       'animated': animated, 'frames': matrices})
        animation['geometry_animated'] = moving
        animation['state_behavior'] = ('light_on_off_no_geometry_motion' if p['kind'] == 'Light'
                                       else 'open_close')
        put(folder + '/animation.json', js(animation))
        catalog.append({'number': len(catalog) + 1, 'kind': p['kind'], 'id': p['id'],
                        'name_ru': p['name_ru'], 'parts': len(p['parts']),
                        'geometry_animated': moving, 'folder': folder})
        for sub in ('blockstates', 'models/item'):
            put('assets/metroport/' + sub + '/' + p['id'] + '.json', original(RES / sub / (p['id'] + '.json')))

    # Runtime source is deliberately labelled as an integration reference, not a mod.
    java = ROOT / 'src/main/java/ru/metrocraft/port'
    sources = list((java / 'metromod/decor').glob('*.java'))
    sources += [java / 'client/gates' / name for name in ('OriginalGateRenderer.java', 'OriginalGateItemRenderer.java')]
    sources += [java / 'mcmod/McmodObjMesh.java']
    for path in sources:
        put('reference/java/' + path.relative_to(ROOT / 'src/main/java').as_posix(), original(path))
    put('README_RU.txt', README.encode('utf-8'))
    put('ATTRIBUTION_RU.txt', original(ROOT / 'THIRD_PARTY.md'))
    table = io.StringIO(newline='')
    writer = csv.DictWriter(table, fieldnames=list(catalog[0]), delimiter=';')
    writer.writeheader()
    writer.writerows(catalog)
    put('CATALOG_RU.csv', b'\xef\xbb\xbf' + table.getvalue().encode('utf-8'))
    commit = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=ROOT, text=True).strip()
    manifest = {'format': 1, 'source_commit': commit, 'standalone_mod': False,
                'minecraft_tested': False, 'counts': dict(Counter(p['kind'] for p in profiles)),
                'animated_geometry_objects': sum(c['geometry_animated'] for c in catalog),
                'objects': catalog, 'source_files_sha256': originals,
                'files': {n: {'bytes': len(data), 'sha256': sha(data)} for n, data in sorted(files.items())}}
    put('MANIFEST.json', js(manifest))
    output = Path(output)
    output.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(output, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=9) as z:
        for name, data in sorted(files.items()):
            info = zipfile.ZipInfo(name, (2026, 10, 30, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o100644 << 16
            z.writestr(info, data)
    with zipfile.ZipFile(output) as z:
        assert z.testzip() is None
    print(json.dumps({'output': str(output), 'bytes': output.stat().st_size,
                      'sha256': sha(output.read_bytes()), 'objects': manifest['counts'],
                      'animated_geometry_objects': manifest['animated_geometry_objects'],
                      'files': len(files)}, ensure_ascii=False, indent=2))
    return output


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path, default=OUTPUT)
    build(parser.parse_args().output)
