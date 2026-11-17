# MinETRO beta website

Статический русский сайт, без сборщика, зависимостей и серверного доступа к аккаунтам.
Запуск: `python -m http.server 3000 --bind 0.0.0.0 --directory website` из корня репозитория.

Vercel: проект `minetro-beta`, Root Directory `website`, Framework `Other`, Output Directory `.`.
Конфигурация заголовков безопасности находится в `vercel.json`. Автоматические Git-деплои
при создании проекта были выключены; публикация выполняется отдельно через Vercel.

- Ссылки на Windows-лаунчер и отдельный тестовый JAR находятся в `index.html`.
- Не публикуйте ссылку на ещё не выпущенный релиз. JAR — не полная сборка.
- `localhost:25565` — только локальный тест, не публичный сервер.
- `assets/tunnel.jpg` — атмосферный AI-концепт, не игровой скриншот.
- `assets/mark.svg` — существующая иконка MinETRO из Windows-лаунчера.
- На сайте нет регистрации/приёма паролей; аккаунт и скин остаются в лаунчере.
- Проверки: `python -m unittest discover -s tests -p test_beta_website.py`.
