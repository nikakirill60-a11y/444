> Текущий этап: **17 Germo подключены в исходниках к World-адаптеру**.
> См. [GERMO_WORLD_ADAPTER_RU.md](GERMO_WORLD_ADAPTER_RU.md).
> Forge-сборка/JUnit и gameplay ещё требуют проверки; ниже — история этапов.

# Коллизии из client.jar: состояние переноса

Донор: `client.jar`, SHA256 `66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc`.
Исследование статическое; клиентский код донора не запускался.

## Что изменено в этом этапе

Для **27 объектов категории Door** перенесён контракт **model/AABB mode**:

* bounds provider берёт точные float min/max модели, а не четвертьблочные
  поверхностные воксели;
* две реализации поведения объединяют границы основного и вспомогательного
  объектов; соответствующие overrides расширяют AABB на `(1,0,1)`;
* очень тонкая ось расширяется на `.005F` с каждой стороны, без округления к сетке;
* при `isActivated=true` физическая AABB двери отсутствует **сразу**, независимо
  от `animX`; при false используется сохранённая AABB. Render-позы не являются
  физическими коллизиями;
* сохранены имена NBT `aabb_minx/miny/minz/maxx/maxy/maxz` для явного переопределения
  локальных границ. Некорректные/неполные значения не принимаются;
* блоки выбора открытой двери заменяемы; их замена не уничтожает контроллер.
  Отсутствие/задержка пакета proxy не отключает коллизии контроллера;
* миграция LayoutVersion 2→3 убирает принадлежащие двери прежние блоки выбора
  из кармана движения. Чужие блоки/UUID не присваиваются и не удаляются.

**Адаптация, а не данные оригинальной карты:** используются текущие позиция,
unit scale, facing, ground и offset MinETRO. Масштабы/повороты/индивидуальные AABB
объектов конкретной карты хранятся в её сохранении, а не в client.jar. Они
не восстановлены и не выдаются за известные. Крупные default AABB некоторых
двухстворчатых дверей действительно предусмотрены поведением донора; без
настроек карты они не обязательно совпадут с желаемым проёмом.

Безопасность Forge-адаптера: закрытие не разрешается, пока в закрытой AABB есть
реальный блок или сущность. Косметическая анимация не блокируется стенами по
размаху render-модели. Это отдельная защита, а не заявление о переносе сервера.

## Воспроизведение

```
PYTHONPATH=. python -m tools.import_metromod_door_collision /path/to/client.jar
python -m unittest discover -s tests
./gradlew --no-daemon build
```

Нужны зависимости `tools/requirements.txt`; экспортёр проверяет SHA256 донора,
27 записей и allowlist из восьми просмотренных классов поведения. Hash каждого
исследованного class и каждой используемой упакованной модели записан в
`DONOR_DOOR_COLLISION.json`. Runtime-данные —
`assets/metroport/metromod_door_collision.json`.

Цепочка оригинала (все классы имеют префикс `null/`):

| Роль | Класс / метод |
|---|---|
| Состояние, физика | `llIjIlllliiiIiililjjjIjiIijijiiI.IjiIliIlIllillljIIIiIjiljlIljlIl(III)` |
| Стандартное поведение | `lIjijlilIlIIIiIiIlIIIIlIijljllij`, bounds `(Vector3f,Vector3f)` и `(STATE)` |
| Bounds provider | `ijijjIlllljIIjijjjlljIIljiiliiiI` |
| Математика AABB / grow | `iIIijIiiiiIlIIjjIjlIjliijIIjIill.jjlliijIIIiIjIjiiIjijjIjIljjiiII(DDD)` |
| Вызов от мира | `jjjjIiijiiIlljllilIjliijlijIIIiI`, collector: state mode либо block mode |

## Что НЕ закончено

**17 Germo пока остаются на прежней реализации.** Оригинальный block-mode
использует другой путь: компоненты по порогам анимации ставят/удаляют специальные
блоки в мире, координаты округляются после поворота. Это stateful-логика,
не AABB движущегося меша. Обнаружены базовый helper
`liIjIjijllIIiIilIiIllIiiljjIiili` и конкретные компоненты, например
`llIljIIjljlIilIilIjijlIjllIjijII` (germo1),
`liIIjjiiljIIllllIiijliIIjIIiIlij` (germo4),
`jljIiillijjjlIjjIllllljIjiiiiljI` (germo9).
Их перенос нельзя подменять 27 Door-профилями.

Lever и Light на этом этапе не менялись. Это **не завершение переноса коллизий
всего декора**. Полноценный запуск Minecraft и проверка игроком не проводились;
JUnit-проверки World/Entity не обозначаются как gameplay validation.

## Результат проверки этого этапа

CI [43918095385](https://github.com/nikakirill60-a11y/444/actions/runs/43918095385):
**294 Python-теста PASS**, но компиляция/JUnit не начались — Forge Maven
завершил загрузку client-extra/mergetool/Scala зависимостей таймаутами.
Предварительная загрузка userdev3/universal/sources через curl с проверкой SHA1
удалась, но не устранила недоступность остальных зависимостей.

**Новый JAR dev.30 не опубликован. Java-тесты добавлены, но ещё не выполнены.**
Не считать этот этап подтверждённым исправлением игры.
Машиночитаемый результат: `DONOR_COLLISION_VALIDATION.json`.


### Повторная проверка

CI [43924978159](https://github.com/nikakirill60-a11y/444/actions/runs/43924978159):
**299 Python-тестов PASS**. Добавлен только CI-транспорт: локальный кеш перед
официальными Forge Maven endpoints, ограниченные повторы curl, сохранение
скачанных зависимостей даже при ошибке сборки. Координаты и версии не менялись,
в Minecraft этот helper не запускается. Но universal JAR Forge по-прежнему
не скачался с обоих официальных endpoints (`could not resolve universal: null`).
Компиляция/JUnit ещё не выполнены, публикации dev.30 нет.

Доступ к донору восстановлен: `client.jar` скачан повторно, SHA256 совпал.
Для всех 17 Germo извлечены оригинальные stateful callback и методы анимации
в отдельный Java-модуль. 2448 сравнений с независимым анализатором байткода PASS.
**Модуль ещё не подключён к игровому World**; подробности и оставшиеся шаги —
[GERMO_BLOCK_RULES_RU.md](GERMO_BLOCK_RULES_RU.md). Нового JAR пока нет.

Последний CI [43928176897](https://github.com/nikakirill60-a11y/444/actions/runs/43928176897):
307 Python PASS, отдельные Germo javac Java 8 / сравнения с байткодом PASS.
Forge-сборка снова остановилась на разрешении зависимостей, теперь legacydev
и обработке client.jar/POM; до компиляции игрового кода/JUnit не дошла.
