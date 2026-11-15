# METRO_MOD: ресурсы действительно извлечены

## Результат

Полностью декодирован доступный `Metro2033Content.jar`:

- **1297 настоящих PNG** — проверены PNG chunk CRC, затем полностью загружены Pillow.
- **115 настоящих OGG/Vorbis** — каждый целиком декодирован libsndfile в PCM-блоки,
  проверены формат, частота/каналы и полное число кадров. Прослушивание не проводилось.
- **27 исходников GLSL** — 13 `.vert`, 13 `.frag`, 1 `.geom`; проверены UTF-8,
  отсутствие NUL и наличие `void main`. Компиляция GLSL/OpenGL не проводилась.
- **4 обычных файла** скопированы без изменений: локализации, sounds.json, quad.obj.
- Итого **1443 файла**. Пропущен только известный пустой маркер `textures/weapon`,
  конфликтующий с каталогом; это не потерянная текстура.

Архив: `downloads/METRO_MOD_Content_decoded.zip`, **47 777 606 байт**.
SHA256: `79aff5c64d12778fc826a53a4ee24e80a35934830812d08cdaad6ad9ba62d3ba`.
Полный manifest с обоими хешами и результатами проверки каждого файла:
`content_decoded_manifest.json`.

Это отдельные исходные ресурсы, **не устанавливаемый мод/ресурспак**.
Полное извлечение включает исходные оружейные изображения/звуки из Content.jar
для сохранения архива, но они НЕ включаются в мод и НЕ означают перенос оружия.

## Как найден формат

Донорский Java-код не запускался. Статически разобран предоставленный `client.jar`
с SHA256 `66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc`.

1. Загрузчик `null/jIljilIlIjjjIIjijjjjilIlIijIlIil` переадресует PNG/OGG
   в `.m2033texture`/`.m2033sound` и общий resource loader.
2. `null/jjijllIljljjlijilijiIiiljjiIiiII` читает заголовок через
   `null/IiijIlIlillIliIilijlillliIljilil... (InputStream)[B`: ровно **16 байт**.
3. Он передаёт эти байты в SecretKeySpec и оставшееся содержимое в декодер.
4. Три коротких метода возвращают `A`, `E`, `S`; декодер
   `null/IIljIlIiIIjiiIIjjjiiiIiiIIjiijlj` использует Cipher("AES") и doFinal.
   Для Java это AES/ECB/PKCS5Padding (PKCS7 padding для 16-байтного блока).
5. Проверка этого пути дала PNG, OGG и GLSL с корректным содержимым, затем
   **все 1439 зашифрованных ресурсов** успешно декодировались и прошли проверки.

Это не поиск пароля и не чтение пользовательских учётных данных: ключ находится
в заголовке каждого ресурса и так же читается самим загрузчиком. loginpass не читался.
Простое переименование расширений не использовалось.

## Что применено в dev20

Оригинальные PNG `textures/items/512/{konservi,galeti,gribtea,waterbottle}.png`
(реальный размер каждого 32×32) скопированы байт-в-байт в пространство MetroPort.
Они заменили четыре временных vanilla-иконки; параметры предметов не изменились.
Восстановленные шейдеры также сохранены читаемыми в `decoded_shaders/` для адаптации,
но не включены в игровой pipeline. В частности, nightVision.frag использует
GLSL 330, framebuffer-текстуры screen/noise/google/bloom и набор uniforms донора:
наличие исходника ещё не означает совместимость с рендером Forge/MWF/OptiFine.

## Что остаётся недоступным

Папка content на Диске проверена заново: 8 JAR, частей до 100 МБ для Models нет.
`Metro2033Models.jar`: ID `1TC7ot75HsDE0FfBYKrG97QVoQn7Tz077`, **616 729 038 байт**.
Проверены права: только владелец, публичного доступа нет. Права не изменялись.
Подключение позволяет загрузку только до **104 857 600 байт**, не имеет параметров
range, извлечения ZIP на Диске или серверного разбиения. Поэтому моделям нужен
доступный файл частями до 100 МБ либо отдельно извлечённые нужные файлы. Ссылки
на `mob/kikimora/...m2033model` и `mob/nosach/nosach.m2033model` не выдаются за файлы.
Остальные большие Textures/ConnectedTextures/MinecraftContent тоже не объявляются
прочитанными. Ключи ресурсов их содержимое заменить не могут.

## Воспроизведение

```
python -m pip install -r tools/requirements.txt
python tools/decode_metromod_content.py /path/to/Metro2033Content.jar \
  downloads/METRO_MOD_Content_decoded.zip reports/METRO_MOD/content_decoded_manifest.json
python tools/import_metromod_icons.py
python -m unittest discover -s tests
```

15 новых проверок декодера: настоящие зашифрованные PNG/OGG/GLSL fixtures,
неверная длина/padding, ложные форматы, path traversal, symlink, конфликт имён,
запрет перезаписи источника, детерминированность, полные CRC/хеши архива,
повторяемость импорта иконок. Итог локально: **199 Python — PASS**.
Авторство/ограничения оригиналов не переоформляются; см. THIRD_PARTY.md.
