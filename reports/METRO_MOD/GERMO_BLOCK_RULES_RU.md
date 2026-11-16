# Оригинальные Germo: извлечены 17 stateful-правил

## Результат

`client.jar` повторно получен из уже предоставленного Google Drive. Его SHA256
совпадает: `66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc`.
Клиент/лаунчер не запускался. Использованы статический разбор class и Fernflower
из npm `fernflower@1.1.0`.

В `DonorGermoBlockRules.java` перенесены **17 компонентов Germo и их методы
изменения анимации**. Это отдельный Java-модуль без Forge, Minecraft, OpenGL,
моделей и вокселизации. Генератор переводит просмотренные методы, меняет имена
состояния и заменяет записи мира упорядоченными вызовами `Sink`. Звуковые вызовы
исключены; выбрана серверная ветка оригинальных методов. Список исходных классов,
их SHA256, хеши декомпиляции и аргументы каталога сохранены в
`DONOR_GERMO_BLOCK_RULES.json`. Изменённая декомпиляция отклоняется экспортёром.

Названия callback в адаптере отражают направление: `towardClosed` — исходный
`jIIlljIIIjijjlljiIiIiljjjiIIlIIl`, `towardOpen` — исходный
`ljIillIiijijlIIijlilliljllijljlI`. Отдельные callback могут и удалять, и добавлять
блоки. Нельзя превращать результат в статическую AABB только по `progress`.

Подтверждены исходные особенности:

* `float`-арифметика, строгие пороги, повороты и **Math.round после поворота**;
* различные пороги установки/снятия и флаги пройденных сегментов;
* поворотные компоненты снимают старые клетки, затем ставят новые;
* `zatvor_01` меняет два компонента анимации и использует предыдущее состояние;
* базовый helper ставит специальный блок только в **air**, удаляет только
  совпадающий тип. Для MinETRO потребуется дополнительно проверять UUID;
* специальный блок `empty`/`empty_red_micro` — класс
  `jIiiIlljjiIliijjiiIlljIjiiiIIlIj`, регистрация
  `jlIjiliIjjiIiillillilijjjjiIljjI.ljIillIiijijlIIijlilliljllijljlI`.
  Его наследование не заменяет физику лучевой выборкой: клиентский raytrace
  переключается отдельно от унаследованной блочной коллизии.

## Проверка независимо от декомпилятора

Ожидаемые результаты взяты **не из сгенерированного Java**, а отдельным
ограниченным анализатором class-байткода `tools/metromod_germo_reader.py`:
**2448 сравнений**, 17 профилей × 2 направления × 4 поворота × 18 значений
анимации. Сравнивается порядок всех операций и конечные float-биты animation /
previous, включая побочные изменения callback. Неизвестные opcodes / вызовы
отклоняются; произвольный код донора не запускается.

Java-модуль и standalone-проверка локально скомпилированы ECJ из Processing4
(`org.eclipse.jdt.core.jar`, Git blob `b749e518b8a065856cc948363db71253fb855f84`)
с target 1.8, запущены на JRE 17:

**PASS: 2448 bytecode differential fixtures; 4904 assertions; 17 profiles.**

Дополнительно проверены строгая граница `-.75F`, её float-сосед, удержание флага
сегмента, разворот, clamp и отказ от неизвестного профиля. В CI добавлена
самостоятельная компиляция через **javac Java 8 до запуска ForgeGradle**: сбой
Forge Maven теперь не должен скрывать результат проверки этого модуля.

CI [43928176897](https://github.com/nikakirill60-a11y/444/actions/runs/43928176897):
этапы `Validate imported content` (**307 Python**) и `Compile and verify donor
Germo rules independently of Forge` (**javac Java 8 + standalone**) успешно
завершились. Полная Forge-сборка на момент записи ещё выполнялась; её успех
этим не заявляется.

Воспроизведение после статической декомпиляции:

```sh
PYTHONPATH=. python -m tools.import_metromod_germo_collision client.jar decompiled
PYTHONPATH=. python -m tools.metromod_germo_reader client.jar src/test/resources/metromod_germo_callbacks.tsv
mkdir -p .cache/germo-check
javac -source 8 -target 8 -d .cache/germo-check \
  src/main/java/ru/metrocraft/port/metromod/decor/DonorGermoBlockRules.java \
  src/test/java/ru/metrocraft/port/metromod/decor/DonorGermoStandaloneCheck.java
java -cp .cache/germo-check ru.metrocraft.port.metromod.decor.DonorGermoStandaloneCheck \
  src/test/resources/metromod_germo_callbacks.tsv
```

## Что ещё не готово

**Модуль пока НЕ подключён к OriginalGateTile. В игре 17 Germo всё ещё используют
прежнюю реализацию. Это не готовое исправление и не новый JAR.**

Для безопасного подключения ещё нужны:

1. проверенный координатный адаптер между исходными блочными клетками и текущими
   unit-scale / ground / facing MinETRO. Каталожные смещения, например
   `(3.99,-.75,0)` у germo1, нельзя просто отбросить: клетки исходного компонента
   находятся на x=2..6, а не относительно центра меша;
2. сохранение / синхронизация animation, previous и флагов сегментов, включая
   смену направления и перезагрузку чанка. Исходный lifecycle присваивает только
   animation.x; неизвестные поля сохранения карты нельзя выдумывать;
3. транзакционные операции блоков: air-only, UUID, откат вместе с состоянием,
   безопасное закрытие, миграция прежних proxy без удаления чужих блоков;
4. проверка реальным World/Entity и Minecraft. Standalone-проверка не доказывает
   коллизию игрока или возможность строительства в настоящем мире.

Полная сборка Door-этапа dev.30 ранее блокировалась скачиванием зависимостей
Forge. Проверка этого изолированного Java-модуля **не означает**, что весь мод
или ранее добавленные Forge/JUnit-тесты были скомпилированы.
