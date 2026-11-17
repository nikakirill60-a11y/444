"""Static beta-site safety/navigation checks; not a deployed HTTP or visual test."""
import json
import re
import unittest
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import urlparse

ROOT = Path(__file__).resolve().parents[1]
SITE = ROOT / 'website'

class Page(HTMLParser):
    def __init__(self, text):
        super().__init__()
        self.ids, self.links, self.assets = [], [], []
        self.feed(text)

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if 'id' in attrs:
            self.ids.append(attrs['id'])
        if tag == 'a':
            self.links.append(attrs.get('href', ''))
            if attrs.get('target') == '_blank':
                assert 'noopener' in attrs.get('rel', '')
        if tag in ('script', 'img', 'link'):
            self.assets.append(attrs.get('src', attrs.get('href', '')))

class BetaWebsiteTest(unittest.TestCase):
    def test_all_anchor_targets_and_local_assets_exist(self):
        page = Page((SITE / 'index.html').read_text())
        self.assertEqual(len(page.ids), len(set(page.ids)))
        for link in page.links:
            if link.startswith('#') and len(link) > 1:
                self.assertIn(link[1:], page.ids)
        for asset in page.assets:
            self.assertTrue(asset.startswith('/'), asset)
            self.assertTrue((SITE / asset.lstrip('/')).is_file(), asset)
        for asset in re.findall(r"url\(['\"]?(/[^)'\"]+)", (SITE / 'styles.css').read_text()):
            self.assertTrue((SITE / asset.lstrip('/')).is_file(), asset)

    def test_downloads_are_github_not_fake_modpack_or_server(self):
        text = (SITE / 'index.html').read_text()
        page = Page(text)
        downloads = [link for link in page.links if link.endswith(('.zip', '.jar'))]
        self.assertEqual(1, len(downloads))
        self.assertTrue(downloads[0].endswith('MinETRO-Launcher-0.4-win-x64.zip'))
        for link in downloads:
            self.assertEqual('github.com', urlparse(link).netloc)
        for required in ('не кадр из игры', 'Полный архив пока не опубликован',
                         'не общий онлайн-сервер', 'не является игровым скриншотом',
                         'Добро пожаловать на MinETRO', 'F8'):
            self.assertIn(required, text)
        self.assertNotIn('<form', text)
        self.assertNotIn('http://localhost', text)

    def test_vercel_is_static_and_csp_disallows_third_party_scripts(self):
        config = json.loads((SITE / 'vercel.json').read_text())
        self.assertIsNone(config['framework'])
        self.assertEqual('.', config['outputDirectory'])
        headers = {h['key']: h['value'] for h in config['headers'][0]['headers']}
        self.assertIn("script-src 'self'", headers['Content-Security-Policy'])
        self.assertNotIn('unsafe-inline', headers['Content-Security-Policy'])
        self.assertEqual('nosniff', headers['X-Content-Type-Options'])
        script = (SITE / 'app.js').read_text()
        self.assertNotIn('clipboard', script)
        self.assertNotIn('#copy-server', script)
        self.assertIn("aria-expanded", script)
        self.assertNotIn('fetch(', script)

    def test_requested_project_text_and_empty_third_card(self):
        text = (SITE / 'index.html').read_text()
        normalized = re.sub('<[^>]+>', ' ', text)
        normalized = ' '.join(normalized.split())
        for required in ('В метро не живут там выживают',
                         'Вылазь наружу лутай здания и возвращайся в метро и разбогатей на этом',
                         'Воюй за станции с другими', '02 — КАК НАЧАТЬ ИГРАТЬ?',
                         'Скачай лаунчер', 'Запусти и зарегистрируйся или войди',
                         'Нажми кнопку «Играть»'):
            self.assertIn(required, normalized)
        empty = re.search(r'<article class="feature feature-empty".*?</article>', text).group()
        self.assertNotIn('<h3', empty)
        self.assertNotIn('<p', empty)
        self.assertIn('/ 03', empty)

    def test_removed_sections_and_links_do_not_remain(self):
        text = (SITE / 'index.html').read_text()
        for removed in ('Не просто сборка', 'Своя подземка', 'ДНЕВНИК РАЗРАБОТКИ',
                        'Это бета. Здесь виден реальный прогресс', 'Все версии на GitHub',
                        'Мод с 3D-меню', '.jar"', 'ПЕРЕД ПОЕЗДКОЙ', 'id="guide"',
                        'id="copy-server"', 'href="#guide"', 'href="#development"'):
            self.assertNotIn(removed, text)
