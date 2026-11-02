#!/usr/bin/env python3
"""Wrap the published, verified Duplet v1 outputs in one installation ZIP.
Does not rebuild/change the pack or bridge and never includes the closed MWF JAR.
"""
import hashlib
import io
from pathlib import Path
import zipfile
ROOT=Path(__file__).resolve().parents[1]
INPUTS={
 'ModularWarfare/Metro_Uboinik_Duplet_MWF_fix1_v1.zip':('Metro_Uboinik_Duplet_MWF_fix1_v1.zip','535275729cc3d4467a7d0ab9b4e9faf0621837a45f8026fbd7d9a24b8a7fbe68'),
 'mods/metro-duplet-bridge-1.0.0.jar':('metro-duplet-bridge-1.0.0.jar','ff8e758ab3c585e6221d19c8f32fbcc88f4e9cd8ef8671bb8245dad41f0d00f8'),
}
README='''ДУПЛЕТ — ЕДИНЫЙ АРХИВ УСТАНОВКИ v1
Minecraft 1.12.2 + Forge + выбранный MWF-Shining 2024.2.4.6f-fix1

Это та же тестовая сборка Дуплета v1, только в одном архиве.
Закрытый MWF JAR не включён и не изменяется. Редактор рук здесь не включён.

УСТАНОВКА
1. Полностью закрой Minecraft. Сделай резервную копию мира и старого пака
   вне папки ModularWarfare.
2. Распакуй ЭТОТ архив в отдельную временную папку.
3. Убери прежние ZIP Убойника/Дуплета и распакованные копии с теми же
   предметами из .minecraft/ModularWarfare/. Общий новый пак УЖЕ содержит
   Убойник v5, поэтому старый ZIP рядом оставлять нельзя.
4. Из распакованного архива:
   - файл ModularWarfare/Metro_Uboinik_Duplet_MWF_fix1_v1.zip положи
     ЦЕЛИКОМ в .minecraft/ModularWarfare/;
   - файл mods/metro-duplet-bridge-1.0.0.jar положи в .minecraft/mods/.
   Внутренний ZIP контент-пака НЕ распаковывай. JAR не запускай двойным щелчком.
   Сам Metro_Duplet_Install_v1.zip класть в mods/ или ModularWarfare/ не нужно.
5. На выделенном сервере установи те же два файла в соответствующие папки.
6. Запусти игру заново. В креативе ищи «Дуплет».

ВОЗМОЖНОСТИ
Дуплет — 2 патрона; с установленным дополнением «+2 ствола» — 4.
Есть глушитель, удлинитель, рукоять, приклад. Патроны общие с Убойником.
+2, глушитель и удлинитель занимают один слот barrel: вместе не ставятся.

Смена любого обвеса Дуплета разрешена ТОЛЬКО после полной выгрузки патронов
и завершения перезарядки/выгрузки. При нехватке места для снятых деталей
операция отменяется. За ёмкость 2/4 отвечает отдельный мод-переходник в mods/.

ОГРАНИЧЕНИЯ
Полного запуска Minecraft при подготовке не было. Это тестовая версия:
проверки структуры, выбранного JAR, модульной логики и браузерного
предпросмотра не гарантируют совместимость со всем модпаком.
Полноценная анимация переломки и извлечение гильз пока не реализованы.
Звуки Дуплета временно взяты от Убойника. OpenGL 1281 не исправляется.
Подробные инструкции находятся внутри самого контент-пака и JAR переходника.

Исходники и документация: репозиторий nikakirill60-a11y/444,
ветка arena/dcab859f-444, docs/MWF_DUPLET_PACK.md.
Авторство исходных моделей/текстур/звуков не меняется.
'''
def build(destination=ROOT/'downloads/Metro_Duplet_Install_v1.zip'):
 entries={}
 for name,(filename,expected) in INPUTS.items():
  data=(ROOT/'downloads'/filename).read_bytes()
  if hashlib.sha256(data).hexdigest()!=expected:raise ValueError('Unexpected input hash: '+filename)
  with zipfile.ZipFile(io.BytesIO(data)) as z:
   if z.testzip() is not None:raise ValueError('Invalid input archive: '+filename)
  entries[name]=data
 entries['README_RU.txt']=README.encode('utf-8')
 destination.parent.mkdir(parents=True,exist_ok=True)
 with zipfile.ZipFile(destination,'w') as z:
  for name,data in sorted(entries.items()):
   info=zipfile.ZipInfo(name,(2026,11,2,0,0,0));info.external_attr=0o644<<16
   info.compress_type=zipfile.ZIP_DEFLATED if name.endswith('.txt') else zipfile.ZIP_STORED
   z.writestr(info,data)
 with zipfile.ZipFile(destination) as z:
  if z.testzip() is not None:raise ValueError('Invalid installation ZIP')
  for name,(filename,_) in INPUTS.items():assert z.read(name)==(ROOT/'downloads'/filename).read_bytes()
 print(destination.name,destination.stat().st_size,hashlib.sha256(destination.read_bytes()).hexdigest())
 return destination
if __name__=='__main__':build()
