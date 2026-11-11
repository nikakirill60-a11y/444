"""UI catalog, not engine item IDs. IDs here are local menu identifiers only."""
from dataclasses import dataclass


@dataclass(frozen=True)
class Item:
    category: str
    key: str
    name: str


ITEMS = (
    Item("Оружие", "revolver", "Револьвер"),
    Item("Оружие", "lolife", "Лолайф"),
    Item("Оружие", "bastard", "Ублюдок (Bastard)"),
    Item("Оружие", "kalash", "Калаш"),
    Item("Оружие", "kalash_2012", "Калаш 2012"),
    Item("Оружие", "aksu", "АКСУ"),
    Item("Оружие", "rpk", "РПК"),
    Item("Оружие", "vsv", "ВСВ"),
    Item("Оружие", "valve", "Вентиль (Valve)"),
    Item("Оружие", "preved", "Превед"),
    Item("Оружие", "clapper", "Клаппер"),
    Item("Оружие", "ashot", "Ашот"),
    Item("Оружие", "duplet", "Дуплет"),
    Item("Оружие", "shambler", "Шамблер"),
    Item("Оружие", "saiga", "Сайга"),
    Item("Оружие", "abzats", "Абзац"),
    Item("Оружие", "bigun", "Бигун"),
    Item("Оружие", "tikhar", "Тихарь"),
    Item("Оружие", "helsing", "Хельсинг"),
    Item("Оружие", "hellbreath", "Хеллбриз"),
    Item("Оружие", "flamethrower", "Огнемёт"),
    Item("Оружие", "gatling", "Гатлинг"),
    Item("Боеприпасы", "pistol_ammo", "Пистолетные патроны"),
    Item("Боеприпасы", "rifle_ammo", "Автоматные патроны"),
    Item("Боеприпасы", "shotgun_ammo", "Дробь"),
    Item("Боеприпасы", "sniper_ammo", "Снайперские патроны"),
    Item("Боеприпасы", "airgun_ammo", "Шарики для пневматики"),
    Item("Боеприпасы", "military_ammo", "Военные патроны / валюта"),
    Item("Снаряжение", "medkit", "Аптечка"),
    Item("Снаряжение", "filter", "Фильтр противогаза"),
    Item("Снаряжение", "grenade", "Граната"),
    Item("Снаряжение", "sticky_grenade", "Липучая граната"),
    Item("Снаряжение", "incendiary", "Зажигательная граната"),
    Item("Снаряжение", "throwing_knife", "Метательный нож"),
    Item("Снаряжение", "claymore", "Мина"),
)

CATEGORIES = ("Все", "Оружие", "Боеприпасы", "Снаряжение")


def search_items(category="Все", query=""):
    if category not in CATEGORIES:
        raise ValueError("Неизвестная категория")
    needle = query.casefold().strip()
    return tuple(item for item in ITEMS if
                 (category == "Все" or item.category == category) and
                 (not needle or needle in item.name.casefold() or needle in item.key))
