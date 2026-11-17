#!/usr/bin/env python3
"""Repack the pinned decor export into independently movable object folders.
Every OBJ, MTL, referenced PNG and animation.json lives in the SAME directory.
No geometry/UV/animation samples or image pixels are regenerated.
"""
import argparse
import copy
import hashlib
import json
from pathlib import Path
import posixpath
import zipfile

ROOT = Path(__file__).resolve().parents[1]
BASE_SHA = '64c98936da0ec6f8b1f4c133deaf3c2269f0538dc6805a441f54a30b6f7babae'
OUTPUT = ROOT / 'downloads/MinETRO_Decor_Germo_Doors_Lamps_v2_local_textures.zip'


def sha(data):
    return hashlib.sha256(data).hexdigest()


def js(value):
    return (json.dumps(value, ensure_ascii=False, indent=2, allow_nan=False) + '\n').encode('utf-8')


README = '''# MinETRO — гермоворота, двери, лампы / v2

ИСПРАВЛЕНО РАСПОЛОЖЕНИЕ ТЕКСТУР:
у КАЖДОГО объекта текстуры PNG лежат прямо рядом с OBJ и MTL,
а не в общей внешней папке. В MTL указаны только имена PNG без ../.
Папку одного объекта можно скопировать отдельно от всего архива.

objects/01_Germo_Gates/ — 17 ворот/гермоворот.
objects/02_Doors/       — 27 обычных дверей.
objects/03_Lamps/       — 31 светильник.

Пример папки объекта:
  part_00.obj
  part_00.mtl
  metal34_balka_02.png
  metal_trub_tonnel.png
  animation.json

У составных объектов несколько part_XX.obj/mtl в той же папке.
Они используют лежащие тут же PNG. Сохраняйте всю папку объекта,
не один OBJ. Общие текстуры намеренно скопированы в каждый объект,
чтобы он не зависел от других папок. При конфликте имён PNG добавлен
короткий SHA-суффикс; сами файлы PNG сохранены побайтно.

## Модели и анимация

Модели — обычные Wavefront OBJ, материалы — MTL, текстуры — PNG.
Геометрия, UV, нормали и изображения взяты из предыдущего экспорта,
не переделаны. Движение лежит в animation.json рядом с моделью.
OBJ не хранит анимацию: при простом открытии OBJ воспроизведения нет.
Для анимации нужно импортировать матрицы из animation.json.

Матрицы ROW-MAJOR, используются столбцы-векторы [x,y,z,1].
Перенос — элементы 3,7,11. На каждую деталь 65 матриц progress от 0 до 1.
Для p: frame=p*64; линейно смешать соседние матрицы поэлементно.
Полный локальный transform = T(root_translation) * part_matrix(p).
Мировое положение и поворот блока задаются приложением.
Названия OBJ в animation.json локальные, все детали находятся рядом.
Донорские тайминги Metro этим экспортом не подтверждаются.

Движение геометрии есть у 44 объектов (17 ворот + 27 дверей).
У 31 лампы геометрия статична: в Minecraft переключается освещение,
а не механическая анимация. GLSL-эффекты/топливо/электросеть не восстановлены.

CATALOG_RU.csv — нумерованный список всех 75 объектов и их папок.
MANIFEST.json — SHA-256 всех файлов и соответствия исходному v1.
reference/java/ — прежний справочный код движения/рендера/освещения.
Это НЕ полный компилируемый проект и НЕ готовый Minecraft-мод/ресурспак.
Папка общего runtime-атласа assets из v1 не нужна для этих OBJ и не включена:
все необходимые изображения уже находятся рядом с каждой моделью.

Не включены оружие, мебель, ящики, рубильники и неполные ворота
structure/mls_vorota_l06 (нет обязательной детали add2).
Права и авторство исходных материалов не меняются: ATTRIBUTION_RU.txt.
Minecraft/OpenGL здесь не запускались.
'''


def build(source, output=OUTPUT):
    assert sha(Path(source).read_bytes()) == BASE_SHA, 'Expected exact v1 decor ZIP'
    with zipfile.ZipFile(source) as z:
        assert z.testzip() is None
        old = {n: z.read(n) for n in z.namelist() if not n.endswith('/')}
    manifest = json.loads(old['MANIFEST.json'])
    files = {n: data for n, data in old.items() if n.startswith('reference/java/')}
    files['ATTRIBUTION_RU.txt'] = old['ATTRIBUTION_RU.txt']
    files['CATALOG_RU.csv'] = old['CATALOG_RU.csv']
    files['README_RU.txt'] = README.encode('utf-8')
    sources = {}
    for row in manifest['objects']:
        folder = row['folder']
        owned = {n: data for n, data in old.items() if n.startswith(folder + '/')}
        material_names = sorted(n for n in owned if n.endswith('.mtl'))
        refs = set()
        for name in material_names:
            for line in owned[name].decode('utf-8').splitlines():
                if line.startswith('map_Kd '):
                    ref = posixpath.normpath(folder + '/' + line.split(None, 1)[1])
                    assert ref in old and ref.endswith('.png') and ref.startswith('assets/metroport/textures/')
                    refs.add(ref)
        local = {}
        for ref in sorted(refs):
            name = posixpath.basename(ref)
            dest = folder + '/' + name
            if dest in files and files[dest] != old[ref]:
                name = name[:-4] + '_' + sha(old[ref])[:12] + '.png'
                dest = folder + '/' + name
            assert dest not in files or files[dest] == old[ref], 'Texture-name collision'
            files[dest] = old[ref]
            local[ref] = name
        for name, data in owned.items():
            if name.endswith('.mtl'):
                lines = []
                for line in data.decode('utf-8').splitlines():
                    if line.startswith('map_Kd '):
                        ref = posixpath.normpath(folder + '/' + line.split(None, 1)[1])
                        line = 'map_Kd ' + local[ref]
                    lines.append(line)
                data = ('\n'.join(lines) + '\n').encode('utf-8')
            elif name.endswith('/animation.json'):
                animation = json.loads(data)
                # The old native_mesh link was an alternate Forge copy, not the
                # portable animation dependency. Keep provenance in the manifest.
                originals = [p.pop('native_mesh') for p in animation['parts']]
                sources[row['id']] = {'native_meshes_in_v1': originals, 'textures': local}
                data = js(animation)
            files[name] = data
    result = {'format': 2, 'source_archive_sha256': BASE_SHA, 'standalone_mod': False,
              'minecraft_tested': False, 'texture_layout': 'same directory as each OBJ/MTL',
              'counts': manifest['counts'], 'animated_geometry_objects': manifest['animated_geometry_objects'],
              'objects': copy.deepcopy(manifest['objects']), 'source_mapping': sources,
              'files': {n: {'bytes': len(b), 'sha256': sha(b)} for n, b in sorted(files.items())}}
    files['MANIFEST.json'] = js(result)
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
                      'sha256': sha(output.read_bytes()), 'counts': result['counts'],
                      'files': len(files)}, ensure_ascii=False, indent=2))
    return output


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--input', type=Path, required=True)
    parser.add_argument('--output', type=Path, default=OUTPUT)
    args = parser.parse_args()
    build(args.input, args.output)
