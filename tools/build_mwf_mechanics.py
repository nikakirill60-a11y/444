#!/usr/bin/env python3
"""Reproducible integrated patch for the exact user-supplied MWF binary and v2 arsenal.
Requires JDK 17 and imageio-ffmpeg==0.6.0. No upstream version substitution or Forge download.
Compile/test facades are NEVER included in the deliverable JAR.
"""
import argparse
import base64
import copy
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'tools/mwf-mechanics'
BASE_JAR_SHA = 'f91a5419cbc9e3cbd18f831e37c17aa2833d577142d324fed1ca14846ce95183'
BASE_PACK_SHA = '87a1c12a14543b022810da233d8db26548e7587d7b9cd804bd155712e624f6ec'
JAR_NAME = 'modularwarfare-shining-2024.2.4.6f-fix1-metro3.jar'
PACK_NAME = 'Metro_Full_Arsenal_MWF_fix1_v5_mechanics.zip'
EXPORTS = [f'--add-exports=java.base/jdk.internal.org.objectweb.asm{s}=ALL-UNNAMED'
           for s in ('', '.tree', '.tree.analysis')]
README = '''METRO MWF MECHANICS 1.2 / Minecraft 1.12.2

ОБЯЗАТЕЛЬНАЯ УСТАНОВКА ОБОИХ ФАЙЛОВ, НА КЛИЕНТЕ И СЕРВЕРЕ
1. Резервная копия мира. Полностью закрыть игру/сервер.
2. Удалить прежний MWF JAR из mods — исходный fix1 или уже установленный metro1/metro2.
   Вместо него поставить modularwarfare-shining-2024.2.4.6f-fix1-metro3.jar.
   Это доработанная копия именно предоставленного закрытого MWF, не другой upstream.
   ДВА MWF JAR одновременно ставить НЕЛЬЗЯ. Старый metro-duplet-bridge тоже удалить.
3. Заменить полный контент-пак на Metro_Full_Arsenal_MWF_fix1_v5_mechanics.zip
   в .minecraft/ModularWarfare/. Не оставлять v1/v2/v3/v4 или отдельные паки Убойника/Дуплета.
4. Такие же JAR и ZIP должны стоять у всех игроков и на сервере.
   Версия сетевого мода отличается: старые клиенты намеренно не допускаются.

НА КЛАВИШУ P (переназначается в Управление → Metro MWF)
Удерживать P: Тихарь/Хельсинг накачиваются, Рельса заряжается.
Каждые 10 серверных тиков (0.5 сек при 20 TPS) прибавляется 20 единиц.
С нулевым/недостаточным давлением/энергией выстрел блокируется без расхода патрона.
Во время накачки стрелять нельзя. Заряжание патронов и накачка — разные действия.
Смена магазина не восстанавливает воздух или электрическую энергию.

Тихарь: 0–120, 8 на выстрел; Хельсинг: 0–120, 18 на выстрел.
Нормальное давление — 100. Избыточное постепенно уходит: 1 единица/сек до 100.
Низкое давление снижает урон, избыточное повышает его до 20%.
Накачка/расход/ограничения проверяются сервером, состояние хранится в NBT оружия.
Новые и ранее не накачанные экземпляры начинают с нуля.

Рельса: батарея 0–100, 8 на выстрел. Увеличенная батарея даёт максимум 150.
Конденсатор сокращает накопление полного выстрела с 40 до 30 тиков.
Быстрая повторная стрельба даёт неполный заряд и меньший урон.
Электрическая батарея сама по себе не разряжается во время хранения.
Это игровая адаптация правил, а не заявление о точном воспроизведении формул Metro.

ДУПЛЕТ
Новый Дуплет появляется ПУСТЫМ. Его нужно зарядить патронами через R.
Старые экземпляры сохраняют уже имеющиеся патроны: массового сброса NBT нет.
При стрельбе используется только внутренний ammocount, даже если в старом NBT
остался тег съёмного магазина ammo. Один логический выстрел тратит один патрон.
Без дополнительных стволов — 2 патрона; с metro_duplet_extra_barrels — 4.
Работают вместимость, загрузка четырёх патронов, HUD и расход на сервере.
Один выстрел тратит один патрон. Отдельные залпы 2/4 и новое действие ПКМ НЕ добавлены.
Нет второго предмета metro_duplet_4 и нет дополнительного bridge-мода.
Для смены обвесов разряжать Дуплет НЕ нужно. Дождаться окончания перезарядки.
При снятии +2 стволов до двух патронов остаются в оружии, лишние возвращаются
в инвентарь тем же типом патронов с сохранением их NBT.
Если нет места для обвеса или лишних патронов, вся операция отменяется без потерь.
Если снятый обвес не помещается в инвентарь, операция отменяется без потери вещей.

ИСПРАВЛЕНИЯ НАЧИНАЯ С METRO3
Исправлен сетевой ABI накачки: однобайтовое чтение использует настоящий readByte()B.
Прежняя тестовая сигнатура readUnsignedByte()I была ошибочной: в Netty она ()S.
Пакет кодируется/декодируется с проверкой диапазона слота на сервере.
Ошибки накачки/HUD теперь логируются и отключают неисправный обработчик до
смены мира, а не выбрасывают исключение из клиентского тика.

ИСПРАВЛЕНИЕ ПРЕЖНЕГО КРАША
Окно модификаций больше не индексирует modelSkins вне границ при старом skinId.
Неверный индекс получает первую доступную текстуру без изменения NBT на клиенте.

ГЛУШИТЕЛИ
Подключены отдельные OGG, отличающиеся от обычного выстрела, и меньшая дистанция звука.
Это обработанные исходные записи пака: фильтрация, компрессия и короткий хвост;
не выдаются за оригинальные игровые записи выстрелов с глушителем Metro.
Маршрутизация — штатный isSuppressor MWF, локально и для других игроков.
Дульный тормоз Абзаца и пламегаситель Вентиля/Преведа не считаются звуковыми глушителями.

ЧЕГО ЭТА ВЕРСИЯ НЕ ДЕЛАЕТ
Нет полноценной струи огнемёта, вращения Gatling, автоадаптеров и новых моделей.
Нет новой анимации работы насосом/отдельными деталями четырёх стволов.
Модели, руки, UV, текстуры и обычные выстрелы сохранены.
Кратность оптики и ПНВ не дорабатывались.
Механики выстрела рассчитаны на Enhanced-оружие этого пака.
Это не полная замена сетевой валидации и античита исходного MWF.
Проверки Java/байткода/ресурсов не заменяют запуск Minecraft.
Запуск Minecraft/Forge/OpenGL и проверка в реальной сетевой игре НЕ выполнены.
'''


def sha(data):
    return hashlib.sha256(data).hexdigest()


def js(value):
    return (json.dumps(value, ensure_ascii=False, indent=2, allow_nan=False) + '\n').encode()


def run(args, **kwargs):
    result = subprocess.run([str(a) for a in args], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, **kwargs)
    print(result.stdout, end='', flush=True)
    if result.returncode:
        message = result.stdout[-6500:].replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
        print('::error title=MWF mechanics build failed::' + message, flush=True)
        raise subprocess.CalledProcessError(result.returncode, args, result.stdout)
    return result.stdout


def patch_pack(source, output, patched_jar, work):
    import imageio_ffmpeg
    ffmpeg = imageio_ffmpeg.get_ffmpeg_exe()
    with zipfile.ZipFile(source) as z:
        assert z.testzip() is None
        files = {n: z.read(n) for n in z.namelist()}
        metadata = {i.filename: copy.copy(i) for i in z.infolist()}
    original = dict(files)
    sounds_path = 'assets/modularwarfare/sounds.json'
    sounds = json.loads(files[sounds_path])
    sounds_before = copy.deepcopy(sounds)
    conversions = {}
    audio = work / 'audio'
    audio.mkdir(parents=True, exist_ok=True)

    def convert(data, mode):
        key = sha(data + mode.encode())[:16]
        name = 'metro_mechanics_' + key
        if name in conversions:
            return name
        src, dst = audio / (key + '.source.ogg'), audio / (key + '.ogg')
        src.write_bytes(data)
        filters = ('highpass=f=80,lowpass=f=1800,acompressor=threshold=0.12:ratio=3:attack=1:release=60,'
                   'volume=0.45,afade=t=out:st=0.22:d=0.18,atrim=duration=0.4') if mode == 'suppressed' else (
                   'silenceremove=start_periods=1:start_duration=0.005:start_threshold=-50dB,'
                   'atrim=duration=0.32,afade=t=in:d=0.008,afade=t=out:st=0.22:d=0.10,volume=0.7')
        run([ffmpeg, '-hide_banner', '-loglevel', 'error', '-y', '-i', src, '-af', filters,
             '-ac', '1', '-ar', '44100', '-c:a', 'libvorbis', '-q:a', '4', '-fflags', '+bitexact', '-flags:a', '+bitexact', dst])
        content = dst.read_bytes()
        assert content.startswith(b'OggS') and len(content) > 500
        files['assets/modularwarfare/sounds/' + name + '.ogg'] = content
        conversions[name] = {'mode': mode, 'sourceSha256': sha(data), 'sha256': sha(content)}
        return name

    def sound_names(event):
        return [s if isinstance(s, str) else s['name'] for s in sounds_before[event]['sounds']]

    # Correct the known compensator misclassification before selecting suppressible weapons.
    brake_path = 'attachments/metro_mod_abzac_glushak.json'
    brake = json.loads(files[brake_path]); brake['barrel']['isSuppressor'] = False
    brake['barrel']['hideFlash'] = True; brake['displayName'] = 'Дульный тормоз Абзаца'
    files[brake_path] = js(brake)
    flash_path = 'attachments/metro_mod_ventil_glushak.json'
    flash = json.loads(files[flash_path]); flash['barrel']['isSuppressor'] = False
    files[flash_path] = js(flash)
    modules = {json.loads(v)['internalName']: json.loads(v) for n, v in files.items()
               if n.startswith('attachments/') and n.count('/') == 1 and n.endswith('.json')}
    suppressed = []
    for path in sorted(files):
        if not (path.startswith('guns/') and path.count('/') == 1 and path.endswith('.json')):
            continue
        gun = json.loads(files[path])
        if not any(modules[m].get('barrel', {}).get('isSuppressor') for m in gun.get('acceptedAttachments', {}).get('barrel', [])):
            continue
        entries = copy.deepcopy(gun['weaponSoundMap']['weaponFire'])
        for index, entry in enumerate(entries):
            event = entry['soundName']
            new_event = gun['internalName'] + '_suppressed_' + str(index)
            clips = []
            for old in sound_names(event):
                resource = old.split(':', 1)[-1]
                source_audio = files['assets/modularwarfare/sounds/' + resource + '.ogg']
                converted = convert(source_audio, 'suppressed')
                clips.append({'name': 'modularwarfare:' + converted, 'stream': False})
            sounds[new_event] = {'category': 'player', 'sounds': clips}
            entry.update(soundEvent='weaponFireSuppressed', soundName=new_event,
                         soundRange=24 if gun['weaponType'] == 'shotgun' else 16)
        gun['weaponSoundMap']['weaponFireSuppressed'] = entries
        files[path] = js(gun); suppressed.append(gun['internalName'])
    for kind, original_event in [('pump', 'metro_native_tihar_reload'), ('charge', 'metro_native_railgun_reload')]:
        resource = sound_names(original_event)[0].split(':', 1)[-1]
        converted = convert(files['assets/modularwarfare/sounds/' + resource + '.ogg'], kind)
        sounds['metro_' + kind] = {'category': 'player', 'sounds': [{'name': 'modularwarfare:' + converted, 'stream': False}]}
    files[sounds_path] = js(sounds)
    quad_path = 'attachments/metro_duplet_extra_barrels.json'
    quad = json.loads(files[quad_path]); quad['displayName'] = 'Дуплет: +2 ствола (+2 патрона)'
    files[quad_path] = js(quad)
    for locale in ('ru_ru', 'ru_RU', 'en_us', 'en_US'):
        path = 'assets/modularwarfare/lang/' + locale + '.lang'
        lines = files[path].decode().splitlines()
        labels = {'metro_duplet_extra_barrels': quad['displayName'] if locale.lower().startswith('ru') else 'Duplet: extra barrels (+2 rounds)',
                  'metro_mod_abzac_glushak': 'Дульный тормоз Абзаца' if locale.lower().startswith('ru') else 'Abzats muzzle brake'}
        lines = [line for line in lines if not any(line.startswith('item.' + n + '.name=') for n in labels)]
        lines.extend('item.' + name + '.name=' + label for name, label in labels.items())
        lines.extend(['key.metro.pump=' + ('Накачать / зарядить оружие' if locale.lower().startswith('ru') else 'Pump / charge weapon'),
                      'key.categories.metro_mechanics=Metro MWF'])
        files[path] = ('\n'.join(lines) + '\n').encode()
    manifest = json.loads(files['FULL_PACK_MANIFEST.json'])
    manifest.update(dupletCapacityWithExtraBarrels=4, dupletCapacityDynamic=True, integratedMechanicsVersion='1.2.0')
    manifest['implementedMechanics'] = ['pneumatic_pressure', 'railgun_energy', 'dynamic_duplet_capacity', 'suppressed_audio', 'loaded_duplet_attachment_transactions', 'safe_skin_rendering', 'empty_new_duplet', 'duplet_loose_ammo_storage', 'pump_packet_abi_fix']
    manifest['mechanicsRequiresJarSha256'] = sha(patched_jar.read_bytes())
    manifest['limitations'] = [s for s in manifest['limitations'] if not s.startswith('Pressure, pumping, rail charging,')]
    manifest['limitations'].extend(['Flame stream, native projectiles, Gatling rotor and automatic adapters remain unimplemented.',
                                    'Duplet uses four sequential shots, not the original Metro two/four-shell salvo controls.',
                                    'Pumping has HUD/audio and gameplay state, but no new pump-handle animation.',
                                    'No Minecraft/Forge/OpenGL runtime validation.'])
    manifest['mechanicsAudio'] = {'suppressedWeapons': suppressed, 'conversions': conversions,
                                  'provenance': 'Filtered source pack recordings, NOT original Metro suppressed recordings.'}
    files['FULL_PACK_MANIFEST.json'] = js(manifest)
    files['README_MECHANICS_V5_RU.txt'] = README.encode()
    files['README_FULL_PACK_RU.txt'] = README.encode()
    files['README_COMPATIBILITY_V2_RU.txt'] = ('ИСТОРИЯ v2; в v5 дополнительные стволы рабочие и смена обвесов без разрядки. См. README_MECHANICS_V5_RU.txt.\n\n').encode() + files['README_COMPATIBILITY_V2_RU.txt']
    # Binary visual assets and first-person holding/animation configurations must not drift.
    for name, data in original.items():
        if name.startswith(('obj/', 'assets/modularwarfare/gltf/', 'assets/modularwarfare/skins/', 'guns/render/', 'ammo/', 'bullets/')):
            assert files[name] == data, 'Unexpected visual/balance asset change: ' + name
        if name.startswith('guns/') and name.count('/') == 1:
            before, after = json.loads(data), json.loads(files[name]); before.pop('weaponSoundMap'); after.pop('weaponSoundMap')
            assert before == after, 'Gun balance/capacity changed instead of runtime logic: ' + name
    output.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(output, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=9) as z:
        for name in sorted(files):
            info = metadata.get(name) or zipfile.ZipInfo(name, (2026, 11, 3, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            z.writestr(info, files[name], compresslevel=9)
    return {'files': len(files), 'suppressedWeapons': suppressed, 'newAudioFiles': len(conversions),
            'changedEntries': sorted(n for n in files if files[n] != original.get(n))}


def validate_jar(base, patched, classes):
    with zipfile.ZipFile(base) as b, zipfile.ZipFile(patched) as z:
        assert z.testzip() is None and len(z.namelist()) == len(set(z.namelist()))
        names = set(z.namelist())
        import re
        original_names = {n for n in b.namelist() if not re.fullmatch(r'META-INF/[^/]+\.(?:SF|RSA|DSA)', n, re.IGNORECASE)}
        assert original_names <= names, 'Original JAR entries, including resource directories, were removed'
        helpers = {n for n in names if n.startswith('ru/metrocraft/mwf/')}
        assert helpers and all(n.endswith('.class') and 'Test' not in n for n in helpers)
        assert not any(n.startswith(('net/', 'io/')) and n not in b.namelist() for n in names), 'Compile facades leaked into JAR'
        changed = [n for n in names if n in b.namelist() and z.read(n) != b.read(n)]
        assert changed and all(n.startswith('com/modularwarfare/') and n.endswith('.class') for n in changed)
        for n in helpers:
            data = z.read(n); assert data[:4] == b'\xca\xfe\xba\xbe' and int.from_bytes(data[6:8], 'big') == 52, n
        assert not any('metro_duplet_bridge' in n for n in names)
        assert b'metro3' in z.read('com/modularwarfare/ModularWarfare.class')
        return {'changedClasses': sorted(changed), 'helperClasses': sorted(helpers), 'compileFacadesDistributed': False}


def build(args):
    assert sha(args.mwf_jar.read_bytes()) == BASE_JAR_SHA, 'Wrong selected MWF build'
    assert sha(args.pack.read_bytes()) == BASE_PACK_SHA, 'Wrong v2 content pack'
    args.work.mkdir(parents=True, exist_ok=True); args.output_dir.mkdir(parents=True, exist_ok=True)
    support = importlib.util.spec_from_file_location('mechanics_test_support', SOURCE / 'test_support.py')
    module = importlib.util.module_from_spec(support); support.loader.exec_module(module)
    stubs, backend = args.work / 'stubs', args.work / 'backend'
    stub_classes, helper_classes, test_classes, tool_classes = [args.work / p for p in ('stub-classes', 'helpers', 'test-classes', 'patch-tool')]
    for p in (stub_classes, helper_classes, test_classes, tool_classes):
        if p.exists(): shutil.rmtree(p)
        p.mkdir(parents=True)
    javac, java = args.javac, args.java
    # The supplied fat JAR contains Mixin annotation processors. We are not compiling mixins
    # and must not auto-run those processors (nor substitute an upstream dependency graph).
    run([javac, '-proc:none', '--release', '8', '-encoding', 'UTF-8', '-d', stub_classes, *module.materialize(stubs)])
    classpath = os.pathsep.join(map(str, [stub_classes, args.mwf_jar]))
    run([javac, '-proc:none', '--release', '8', '-encoding', 'UTF-8', '-cp', classpath, '-d', helper_classes, *sorted((SOURCE / 'src').rglob('*.java'))])
    testpath = os.pathsep.join(map(str, [helper_classes, stub_classes, args.mwf_jar]))
    run([javac, '-proc:none', '--release', '8', '-encoding', 'UTF-8', '-cp', testpath, '-d', test_classes,
         *module.materialize(backend, True), *sorted((SOURCE / 'tests').rglob('*.java'))])
    run([javac, '-proc:none', *EXPORTS, '-encoding', 'UTF-8', '-d', tool_classes, SOURCE / 'PatchMwf.java'])
    native_testlog = run([java, *EXPORTS, '-cp', tool_classes, 'PatchMwf', '--native-ammo-tests', args.mwf_jar, test_classes])
    testlog = run([java, '-Xverify:all', '-cp', os.pathsep.join(map(str, [test_classes, helper_classes, stub_classes, args.mwf_jar])), 'ru.metrocraft.mwf.MechanicsTest'])
    patched = args.output_dir / JAR_NAME
    patchlog = run([java, *EXPORTS, '-cp', tool_classes, 'PatchMwf', args.mwf_jar, patched, helper_classes])
    jar_report = validate_jar(args.mwf_jar, patched, helper_classes)
    pack = args.output_dir / PACK_NAME
    pack_report = patch_pack(args.pack, pack, patched, args.work)
    report = {'version': '1.2.0', 'baseJarSha256': BASE_JAR_SHA, 'basePackSha256': BASE_PACK_SHA,
              'jar': {'name': JAR_NAME, 'bytes': patched.stat().st_size, 'sha256': sha(patched.read_bytes()), **jar_report},
              'pack': {'name': PACK_NAME, 'bytes': pack.stat().st_size, 'sha256': sha(pack.read_bytes()), **pack_report},
              'isolatedTests': testlog.strip(), 'nativeAmmoBoundary': native_testlog.strip(), 'patchVerification': patchlog.strip(),
              'minecraftRuntimeTested': False, 'forgeLaunchTested': False, 'multiplayerTested': False}
    (args.output_dir / 'MWF_Mechanics_v3_report.json').write_bytes(js(report))
    (args.output_dir / 'MWF_Mechanics_v3_README_RU.txt').write_text(README, encoding='utf-8')
    print(json.dumps(report, ensure_ascii=False, indent=2))
    return report


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--mwf-jar', type=Path, required=True)
    parser.add_argument('--pack', type=Path, default=ROOT / 'downloads/Metro_Full_Arsenal_MWF_fix1_v2_compat.zip')
    parser.add_argument('--output-dir', type=Path, default=ROOT / 'downloads')
    parser.add_argument('--work', type=Path, default=ROOT / '.cache/mwf-mechanics/build')
    parser.add_argument('--java', default='java'); parser.add_argument('--javac', default='javac')
    build(parser.parse_args())
