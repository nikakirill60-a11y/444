"""Integration point for a verified Metro: Last Light Redux game build.

The UI sends local catalog keys, NOT in-game entity IDs. A future adapter must map
keys to verified game items, check the running game's build, and provide safeguards
for save files and level-specific inventory restrictions.
"""


class SpawnUnavailable(RuntimeError):
    pass


def spawn(item_key: str, quantity: int) -> None:
    """Give an item only after a version-specific adapter is implemented.

    Never report success without changing the game; never guess memory addresses.
    """
    raise SpawnUnavailable(
        "Нет проверенного адаптера для этой сборки игры. "
        "Меню готово, но предмет НЕ выдан."
    )
