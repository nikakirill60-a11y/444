"""Static wiki integration checks; interactive behavior has a real browser test."""
from html.parser import HTMLParser
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
SITE = ROOT / 'website'


class Elements(HTMLParser):
    def __init__(self, text):
        super().__init__(); self.nodes = []; self.feed(text)
    def handle_starttag(self, tag, attrs):
        self.nodes.append((tag, dict(attrs)))


class WebsiteWiki(unittest.TestCase):
    def setUp(self):
        self.text = (SITE / 'wiki.html').read_text()
        self.nodes = Elements(self.text).nodes

    def test_main_navigation_links_to_wiki_and_marks_active_page(self):
        self.assertIn('href="/wiki.html">Вики</a>', (SITE/'index.html').read_text())
        self.assertIn('href="/wiki.html" aria-current="page">Вики</a>', self.text)
        for target in ('/#project', '/#start', '/account.html'):
            self.assertIn('href="'+target+'"', self.text)

    def test_exact_four_categories_with_real_section_targets(self):
        expected = ('weapons', 'armor', 'mutants', 'items')
        for name in expected:
            self.assertIn('id="tab-'+name+'" href="#'+name+'"', self.text)
            self.assertTrue(any(tag == 'section' and a.get('id') == name for tag, a in self.nodes))
        tabs = [a for _, a in self.nodes if a.get('id', '').startswith('tab-')]
        self.assertEqual(len(tabs), 4)
        for label in ('Оружие', 'Броня', 'Мутанты', 'Предметы'):
            self.assertIn('>'+label+'<', self.text)
        self.assertNotIn('Придметы', self.text)

    def test_ids_links_and_local_assets_resolve(self):
        ids = [a['id'] for _, a in self.nodes if 'id' in a]
        self.assertEqual(len(ids), len(set(ids)))
        for tag, attrs in self.nodes:
            if tag == 'a':
                href = attrs.get('href', '')
                if href.startswith('#'): self.assertIn(href[1:], ids)
            if tag in ('link', 'script', 'img'):
                asset = attrs.get('src', attrs.get('href', ''))
                self.assertTrue(asset.startswith('/'))
                self.assertTrue((SITE/asset.lstrip('/')).is_file(), asset)
        self.assertNotIn('<style', self.text)
        self.assertNotRegex(self.text, r'<script(?![^>]*\bsrc=)')

    def test_progressive_content_without_javascript_and_no_invented_articles(self):
        panels = [a for tag, a in self.nodes if tag == 'section' and a.get('class') == 'wiki-panel']
        self.assertEqual(len(panels), 4)
        self.assertTrue(all('hidden' not in a for a in panels))
        self.assertNotIn('МАТЕРИАЛЫ ГОТОВЯТСЯ', self.text)
        self.assertEqual(sum(tag == 'article' and 'data-entry' in a for tag, a in self.nodes), 268)
        self.assertIn('MinETRO dev.43 + MWF v13', self.text)
        self.assertNotIn('<form', self.text)

    def test_script_has_keyboard_history_and_aria_without_network_or_storage(self):
        script = (SITE/'wiki.js').read_text()
        for required in ('ArrowRight', 'ArrowLeft', 'Home', 'End', 'aria-selected', 'aria-controls',
                         'tabpanel', 'tablist', 'hashchange', 'popstate', 'pushState', '.hidden', 'tabIndex'):
            self.assertIn(required, script)
        for dangerous in ('innerHTML', 'insertAdjacentHTML', 'fetch(', 'localStorage', 'sessionStorage', 'eval('):
            self.assertNotIn(dangerous, script)


if __name__ == '__main__':
    unittest.main()
