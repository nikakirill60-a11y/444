# Исходные материалы и атрибуция

Это адаптация предоставленного пользователем MetroCraft-контента, а не новая авторская коллекция.

- `Flan-20261030T152711Z-1-001.zip` → `Flan/MetroCraft -Pack-1.7.10-4.10.0.jar`: определения 71 предмета, названия, иконки, атласы и используемые звуки. Авторство остаётся за первоначальными правообладателями. Имена авторов пака в его метаданных не указаны и здесь не выдуманы.
- `(Metro) Flans-v1.1.jar`: старый Flan's Mod с изменениями MetroCraft; источник геометрии и справочных механик, включая четыре Techne-модели ворот, два OBJ, четыре исходных атласа ворот и звук `germo`. Рекурсивные/неиспользуемые entity-адаптеры моделей удалены, имена полей/методов адаптированы к 1.12.2. Его лицензия Creative Commons Attribution–Noncommercial–ShareAlike 3.0 сохранена в `src/main/resources/LICENSE-original-Flans.txt` и включается в JAR.
- `Inventory(METRO)-v1.0.jar`: иконки/OBJ/текстуры рюкзаков, оформление инвентаря и справочная реализация вместимости.
- `metrocraft.jar`: оригинальные классы, модели и текстуры 10 сущностей. Методы и поля адаптированы к именам/API 1.12.2. Геометрия моделей получена из исходных классов, а не создана заново.
- `StilianMetro.jar`: 58 зарегистрированных декоративных блоков, их текстуры и кирка.
- Библиотека Turbo Model Thingy (`src/main/java/ru/metrocraft/port/client/tmt`) взята из [FlansMods/FlansMod, ветка 1.12.2](https://github.com/FlansMods/FlansMod/tree/1.12.2), commit `71ba7ed065d906d48f34ca471bbd0172b5192f6b`. Автор расширения ModelRendererTurbo — GaryCXJk, также вклад участников Flan's Mod. Изменены пространство имён и вызовы журналирования; остальные исходные комментарии сохранены. Лицензия репозитория включена как `LICENSE-Flans-1.12.2.txt`.
- `tools/legacy-reference`: справочные исходники, восстановленные CFR 0.152 из предоставленных JAR. Сохраняют авторство и ограничения оригиналов. **Не включаются в компиляцию** 1.12.2. Декомпиляция не создаёт новых прав на оригинальные ресурсы.
- Gradle Wrapper 4.10.3 взят из `gradle/gradle` (Apache License 2.0): `gradle/WRAPPER-LICENSE`.

Новые контейнеры, сетевые пакеты, импортёры и адаптеры механик написаны для этого порта. Старые бинарные `.class` не подмешиваются в итоговый JAR.

Наличие лицензии в одном архиве не доказывает, что она охватывает все сторонние звуки и изображения из сборки. Перед публичным распространением или коммерческим использованием проверьте разрешения авторов оригинального пака. Не выдавайте исходные ресурсы за свои и соблюдайте условия некоммерческого использования/атрибуции/share-alike применимых оригиналов.

- `math/RotatedAxes` и общая векторная математика адаптированы из той же ветки Flan 1.12.2. Векторная библиотека содержит исходные BSD-заголовки LWJGL; текст также включён в JAR как `LICENSE-LWJGL-vector.txt`. Убраны зависимость журналирования от FlansMod и неиспользуемый vehicle packet helper Matrix2f. `ballistics/LagSnapshot` сохраняет координаты/повороты четырёх костей и алгоритм пересечения из предоставленных PlayerSnapshot/PlayerHitbox 1.7.10.

- `ExodusRPV3.zip`, предоставленный пользователем в `main`: исходные JSON-модели/текстуры трёх противогазов, двух мутантов и 22 декораций. Геометрия, UV и display transforms сохранены, пути переведены в пространство `metroport`. В метаданных ресурспака нет подтверждённой общей лицензии: права не переоформляются. Происхождение скопированных PNG, SHA-256 архива и все замены отсутствующих текстур перечислены в `docs/exodus-report.json`. Ванильные fallback-текстуры не копируются из игры.

- `MCMod.jar` (распакованный и предоставленный пользователем через Google Drive): в партии 1 перенесены оригинальные OBJ/атласы/иконки шести комплектов брони. `tools/mcmod-reference` содержит оригиналы с SHA-256 и справочные исходники CFR 0.152 из шести классов регистрации/рендера; бинарные классы не включаются в JAR. Лицензия MCMOD не установлена (полученный `mcmod.info` состоит из нулей); наличие исходников/ассетов не создаёт разрешения на их публичное или коммерческое распространение. Авторство не переоформляется. См. `docs/MCMOD.md` и `docs/mcmod-report.json`.

- MCMOD 1.5.1: получены остальные 27 комплектов; теперь сохранены 33 исходных OBJ/атласа/иконки (99 ресурсов) и шесть справочных классов, SHA-256 в том же manifest. Оригинальные Windows-1251 метки материалов противогаза и особые имена атласов сохранены; ограничения прав/неустановленной лицензии остаются прежними.

- MCMOD 1.6.0: добавлены девять исходных иконок обычных предметов и справочные классы их регистрации/обработчиков. 53 отдельные иконки шлемов/нагрудников — новые растровые производные оригинальных OBJ и атласов, созданные `tools/create_mcmod_icons.py`; не выдаются за отдельные иконки, присутствовавшие в исходном JAR. Авторство и ограничения исходных ресурсов сохраняются.

## MCMOD unified firearm resources (1.11)

The remaining fourteen registered gun models, original textures/icons/sounds, shared attachment atlases and sixteen GuiMods images were recovered from the user-supplied unpacked MCMod resources. Runtime OBJ gzip is deterministic and lossless. Source file IDs, uncompressed sizes and SHA-256 are in `tools/mcmod-firearm-sources.json`. Reference bytecode/decompilation is for migration auditing only and is not included as executable legacy mod classes in the distributable. Native 1.12 renderer/GUI/transactions and animation curves are port implementations, not pixel-identical legacy code.

## BlackRazidMetro (1.12.0)

User-supplied Google Drive folder `blackrazidmetro`: original armor/backpack bytecode,
18 worn atlases and 17 item icons. 78 exact originals, Drive IDs, sizes and SHA-256 are
recorded in `tools/blackrazid-reference/source-files.json`. Registration/model sources
are recovered with CFR 0.152 for reference only and are not compiled into the mod.
Runtime box/UV data are derived from those original models; the PPM-88 inventory icon
is newly rasterized from its original geometry/atlas, not an additional recovered source icon.
Native 1.12 equipment, storage GUI, validation and networking are port implementations.
No license granting public/commercial redistribution was supplied; original authorship
and rights remain unchanged. User provision is not a license grant. See docs/BLACKRAZID.md.


### BlackRazid selected expansion — development, not a complete port

74 additional user-supplied originals are recorded with Drive IDs, lengths and SHA-256 in
`tools/blackrazid-reference/expansion-source-files.json`: behavior bytecode plus four icons and two OGG files.
Recovered Java under `tools/blackrazid-reference/java` is reference material, not compiled/packaged runtime code.
ExtendetPlayer and PacketHandler are **audit only**; their general player data, air, faction/event/command implementation is excluded.
Five new development items reuse the original flashlight/flask/power/grenade icons and flask/charging sounds.
Thirst, charging, projectiles and networking are safe 1.12.2 adaptations, described explicitly in `docs/BLACKRAZID_EXPANSION.md`.
Client lighting is a new adaptation because the recovered lamp event body is empty.
22 firearms, transport, 12 mutants/spawners and environment are not claimed as completed by merely recovering their sources.
Original rights/permissions remain as described above; user-provided files are not a grant of redistribution rights.

Additional selected BlackRazid ammunition/firearm development references are listed in
`tools/blackrazid-reference/weapons-source-files.json`: 29 original ammo classes, 35 native item PNGs,
and 14 original OGGs, with Drive IDs, byte lengths and SHA-256. CFR recovery is audit-only;
no original executable class is placed in runtime resources. The native six-gun baseline uses original
inventory icons, **not** a completed native 3D mesh/module port. At that stage the other sixteen guns were still pending; the following baseline adds their mechanics, not native weapon meshes/modules or the remaining requested expansion sections.

The next native baseline covers all 22 selected firearms but still does not claim complete models/modules/animations.
`weapons-special-source-files.json` records 59 additional originals (15 icons, 42 OGGs, two flame-helper classes).
`EntityPassiveFire` and `EntityPropulsionFX` are visual-only references; client FLAME particles are an explicit temporary adaptation.
`vyhlop_shoot.ogg` is absent from the supplied sound folder; the original BlackRazid ventil shot is a documented fallback,
not claimed to be a recovered Vyhlop recording. Native models, hands, modules, FX, transport, mutants and environment remain pending.
