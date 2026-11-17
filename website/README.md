# MinETRO beta website

Статический русский сайт без сборщика; account.html обращается напрямую к Supabase Auth и owner-only профилю.
Запуск: `python -m http.server 3000 --bind 0.0.0.0 --directory website` из корня репозитория.

Vercel: проект `minetro-beta`, Root Directory `website`, Framework `Other`, Output Directory `.`.
Конфигурация заголовков безопасности находится в `vercel.json`. Автоматические Git-деплои
при создании проекта были выключены; публикация выполняется отдельно через Vercel.

- В `index.html` оставлена только загрузка Windows-лаунчера. Отдельный JAR на сайте больше не предлагается.
- Не публикуйте ссылку на ещё не выпущенный релиз. JAR — не полная сборка.
- Блок «Перед поездкой» и копирование локального адреса удалены.
- `assets/tunnel.jpg` — атмосферный AI-концепт, не игровой скриншот.
- `assets/mark.svg` — существующая иконка MinETRO из Windows-лаунчера.
- Регистрация/вход: `/account.html`, тот же проект и public key, что у лаунчера. Скин пока меняется в лаунчере.
- Проверки: `python -m unittest discover -s tests -p test_beta_website.py`.

Локальный шрифт Oswald (Cyrillic + Latin) поставляется через
`@fontsource-variable/oswald`; лицензия SIL OFL 1.1 — `assets/Oswald-LICENSE.txt`.
Внешние шрифтовые сервисы и аналитика не подключены.

Браузерный smoke-test: установите Playwright во временную папку, затем
запустите `node tools/test_beta_website_browser.cjs` из корня (NODE_PATH должен
указывать на установленный node_modules). Можно передать CHROMIUM_EXECUTABLE_PATH
для своего Chromium и SITE_URL для адреса тестового сайта. Проверяются ширины
320/390/768/1440, отсутствие горизонтальной прокрутки/ошибок ресурсов, FAQ,
мобильное меню/Escape, три шага начала игры и единственная загрузка лаунчера.
Эта проверка не подтверждает совместимость сайта со всеми браузерами.


## Аккаунты

Email + пароль + ник (3–16 ASCII букв/цифр/подчёркиваний); пароль 12–128 символов.
Подтверждение email обязательно. После ссылки нужен явный вход с паролем;
страница удаляет параметры URL и не принимает присланный в URL bearer token.
Сессия только в памяти вкладки: без localStorage/cookie/refresh token, очищается
при выходе, pagehide и истечении. Профиль читается только по auth.uid, RLS остаётся включён.
Публичный publishable key не является секретом; service-role ключей на сайте нет.
Пароли передаются только HTTPS непосредственно Supabase, не Vercel/GitHub.
CSP блокирует нативную отправку формы; без JavaScript регистрация недоступна.

В Supabase разрешён точный redirect `https://minetro-beta.vercel.app/account.html`.
Старый site_url функции `account-confirmed` оставлен для лаунчера.
Собственный SMTP не подключён: встроенная отправка имеет строгие ограничения
и может не отправлять письма внешним адресатам. Для публичной регистрации нужен
SMTP и проверка реальной доставки; mock-тесты ниже этого не подтверждают.
Тестовые аккаунты в production автоматически не создаются.

- `node --test tests/website_accounts.test.mjs` — API/валидация/безопасность (mock).
- `node tools/test_website_accounts_browser.cjs` — UI 320/390/768/1440 (mock Auth),
  предварительно запустить сайт на порту 3000. Требуется Playwright, как выше.
