# Подключение полной сборки к тестовому лаунчеру

Условие: согласованный архив **полной** Forge 1.12.2 сборки, включая MWF и
пользовательский packv4. Хранилища пользователя пока выключены; доступа к актуальному
архиву в этом этапе нет. Не заменять его одним опубликованным JAR MinETRO.

Архив должен содержать игровые данные, а не Windows-установщик:
`mods/`, `config/`, `ModularWarfare/` (или реальные каталоги MWF-паков),
необходимые ресурсы и настройки. Без логинов, tokens, `accounts.json`, лаунчеров,
нативных EXE/DLL/BAT/PS1. ZIP может иметь внешний каталог: задайте `--root`.
Пароли и токены Microsoft не извлекать и не включать в дистрибутив.

После проверки состава разместить ZIP по прямой доступной HTTPS-ссылке.
Google Drive/Dropbox web-страница предпросмотра — не прямая ссылка на ZIP;
HTML/неверный размер будут отклонены, а не распакованы как сборка.
Не выполнять upload без согласования прав на распространение закрытых модов.

```sh
python launcher/create_channel.py path/to/full-pack.zip \
  --url 'https://host.example/full-pack.zip' \
  --version test-02 --root 'pack/.minecraft' --out channel.test-02.json
```

Генератор вычисляет SHA256/размер ZIP и SHA256 JAR модов (включая MinETRO).
Проверьте, что в ZIP действительно актуальный MinETRO, нужный MWF и нет дубликатов.
Проверка имён генератором не подтверждает совместимость/безопасность Java-кода.
Подключить JSON в приложении: файл «JSON…» -> «Подключить» или HTTPS-ссылка.
Подтверждённый snapshot сохраняется локально; новый канал не применяется без согласия.
SHA256 — целостность. Криптографической подписи канала в прототипе нет.

Новая `version` / новый SHA ZIP создают новый экземпляр Prism. Существующая версия
не переписывается даже при повторной установке. Миры остаются в старом экземпляре;
переносить только после резервной копии. Память существующего экземпляра менять в Prism.

## Воспроизведение проверок

```sh
python -m unittest discover -s launcher -p 'test_*.py'
dotnet run --project launcher/MinETRO.Tests -c Release -- launcher/channel.test.json
# Дополнительно реальная загрузка/проверка официального portable Prism:
dotnet run --project launcher/MinETRO.Tests -c Release -- launcher/channel.test.json --verify-prism
# Публикация Windows x64, self-contained (на Windows):
dotnet publish launcher/MinETRO.Launcher -c Release -r win-x64 --self-contained true \
  -p:PublishSingleFile=true -p:EnableCompressionInSingleFile=true \
  -p:IncludeNativeLibrariesForSelfExtract=true -o launcher/publish
```

Исходный CLI Prism проверен в `launcher/Application.cpp`: `--dir` и `--launch`.
Microsoft-авторизация, загрузка Minecraft/Forge/Java — штатные функции Prism.
Оболочка не запрашивает/не читает учётные данные и не обходит проверку лицензии.
Prism 11.1.1 ZIP зафиксирован по SHA256 из GitHub release asset `596582304`.
В CI проверяется реальная загрузка/распаковка этого ZIP, не только HTTP-мок.
GUI smoke открывает форму на Windows и сохраняет реальный `PREVIEW.png`.
Это **не** end-to-end запуск Minecraft, модпака или реальный Microsoft login.
