# MinETRO Launcher TEST 0.1 — Windows x64

Тестовая Windows-оболочка установки и запуска через отдельный portable Prism.
**Архив полной сборки не подключён**: Google Drive/Dropbox выключены в разговоре,
актуальный архив MWF/packv4 не получен. Установка/игра в стандартном канале отключены,
а не имитируются скачиванием одного MinETRO.jar. Можно подготовить Prism и войти в него.

## Дистрибутив и проверки

* [ZIP для Windows 10/11 x64](https://github.com/nikakirill60-a11y/444/raw/7a9de73431e40d2c039cf945d617d04b547036ff/downloads/MinETRO-Launcher-Test-0.1-win-x64.zip).
* Размер 66 297 730 байт. SHA256:
  `3aa8403ec6e24f581a5f267e20a8169f5fd6aaca3b8fd6a94b233c8da833acfa`.
* Исходники сборки `c1af28baec3ad15f0416062b4e6b8179d25728f4`, публикация
  `7a9de73431e40d2c039cf945d617d04b547036ff`.
* [CI 44050942773](https://github.com/nikakirill60-a11y/444/actions/runs/44050942773): PASS.
* 15 C# сценариев PASS: SHA/размер/cache, HTTPS redirect policy, zip-slip,
  имена Windows, дубликаты, symlink/credentials, transactional install,
  отмена/повреждение, сохранение миров/packv4, изоляция версий, Prism args,
  сохранение подтверждённого канала; также **реальная** загрузка/проверка SHA256
  официального portable Prism 11.1.1.
* 4 Python проверки автора канала PASS; self-contained EXE build и реальный
  WinForms startup smoke PASS. В ZIP есть настоящий `PREVIEW.png` из Windows CI.
* Локально повторно проверены CRC всех ZIP entries, SHA256, размер, содержимое
  канала и совпадение Git blob SHA с GitHub Contents API.
* **Не проверены:** полный модпак, Minecraft gameplay, фактический Microsoft login.
  Учётные данные в оболочку не вводятся; авторизация только штатным Prism/Microsoft.

## Использование

Распаковать **весь** ZIP и открыть `MinETRO-Launcher-Test.exe` рядом с
`channel.test.json`. .NET встроен, администратор не нужен. Сертификата подписи EXE
нет; Windows может предупреждать. Не отключать защиту Windows; сверять SHA256.

Выбрать отдельную папку. «Подготовить Prism / войти в Microsoft» скачает официальный
движок с проверкой SHA256. Лаунчер не читает accounts.json и не перехватывает пароли.
После получения полного архива автор создаёт манифест по инструкции
[`launcher/MAINTAINER_RU.md`](../launcher/MAINTAINER_RU.md), пользователь подтверждает
его подключение. Канал сохраняется локально, не меняется удалённо без согласия.

Установка проходит в staging с последующим rename в отдельный экземпляр Prism.
Новая версия не стирает предыдущую. Миры автоматически не переносятся — для переноса
нужна резервная копия. Повторная проверка не перезаписывает настроенные руки/настройки.
Java 8 x64 и Microsoft-аккаунт с лицензией Minecraft Java настраиваются в Prism.
Полная инструкция и уведомления о лицензиях включены в ZIP.
