import unittest

from metro_spawner.catalog import ITEMS, search_items
from metro_spawner.game_backend import SpawnUnavailable, spawn


class MenuDataTests(unittest.TestCase):
    def test_unique_local_keys(self):
        self.assertEqual(len(ITEMS), len({item.key for item in ITEMS}))

    def test_case_insensitive_search(self):
        self.assertEqual([x.key for x in search_items(query="САЙГА")], ["saiga"])
        self.assertEqual([x.key for x in search_items(query="KALASH")], ["kalash", "kalash_2012"])

    def test_categories(self):
        self.assertTrue(all(x.category == "Боеприпасы" for x in search_items("Боеприпасы")))
        with self.assertRaises(ValueError):
            search_items("неизвестно")

    def test_no_adapter_never_claims_spawn(self):
        with self.assertRaises(SpawnUnavailable):
            spawn("kalash", 1)


if __name__ == "__main__":
    unittest.main()
