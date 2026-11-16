# MinETRO dev26 — оригинальный интерактивный декор

Расширение dev25: **72 новых объекта**, вместе с шестью прежними оригинальными
воротами — **78 из 112** записей выбранного функционального каталога:
14 ворот, 27 дверей, 6 рубильников, 31 источник света.
**Не весь перенос завершён:** 34 записи намеренно не зарегистрированы (ниже).

## Работа в Forge 1.12.2

- Оригинальные OBJ/UV и diffuse-материалы; не заменены кубами/другими моделями.
- Ворота управляются редстоуном на любой занятой ячейке, без ручного переключения.
- Двери: ПКМ переключает ручное открытие, сигнал редстоуна также открывает.
  При включённом ручном открытии снятие сигнала не закрывает — выключите ПКМ.
- Рубильники: ПКМ переключает выход 0/15 на соседние блоки от корпуса и его частей.
  Внешний сигнал не переключает рубильник; собственный выход не зацикливает вход.
- Светильники: ПКМ вкл/выкл; редстоун тоже включает. Свет уровня 15 от занятой
  структуры, обновление штатного светового движка. Это адаптация освещения Forge,
  не восстановление донорского deferred/GLSL-освещения, топлива или электросети.
- Установка/поворот/сохранение/удаление/защита от зажатия сущностей используют
  транзакционную многоблочную систему dev25. Требуется воздух во всём кармане движения.
- Сохраняются направление, UUID структуры, положение деталей, ручной режим и сигнал.
- Из исходных render-методов статически извлечены 65 положений каждой детали.
  Одинаковая линейная интерполяция матриц используется клиентом и сервером.
  Вращения/составные детали/зеркальные масштабы поддержаны; точное донорское
  время анимации не заявляется. Между отсчётами возможна малая деформация матрицы.
- Коллизия консервативная: четвертьблочные поверхностные воксели исходных треугольников,
  после вращения — ограничивающие AABB отдельных кубоидов. Это не точная triangle-mesh
  физика; около наклонных деталей возможен дополнительный зазор. Весь размах
  анимации зарезервирован, включая промежуточные положения, не только концы.

## Что не выдумывалось

30 меток **Inventory** в клиентском каталоге сами по себе не доказывают наличие
серверных слотов, их размер или правила подбора. Поэтому эти объекты не превращены
в самодельные сундуки, не возвращены оружие/боеприпасы, не заявлен их перенос.

Для germo7/germo8/l07_vorota не восстановлены управляющие векторы, которые
исходный клиент получает вне render-конструктора. Не подставлены угаданные числа.
Для mls_vorota_l06 отсутствует add2; даже доступный двухдетальный preview не объявлен
полностью перенесённой моделью. Дополнительно для wpn_14_5x5_ammo отсутствует diffuse
wpn_dshk, но весь Inventory уже исключён из-за неподтверждённой серверной функции.

Мебель без подтверждённых действий, оружие METRO_MOD, MetroEdit и донорский сервер
не добавлялись. Старые мировые ID/шесть прежних ворот и шесть dev25 сохранены.
MWF, packv4, пользовательские руки, маски/ПНВ и их настройки не изменялись.

## Проверка и ограничения

Добавлены 11 JUnit-проверок поведения адаптеров, коллизии вращения и сохранения,
9 Python-проверок геометрии/ресурсов/матриц/полноты каталога. Старые проверки сохранены.
CI и ZIP-проверка сборки обязательны перед публикацией. **Запуск в Minecraft,
OpenGL, реальный сервер и визуальная проверка не выполнены.** Тесты с mock-world
не означают, что игровой прогон уже состоялся.

Источники и SHA: `metromod_interactive_provenance.json`,
`reports/METRO_MOD/interactive_coverage.json`, исходные два verified-отчёта.
`tools/metromod_pose_reader.py` — ограниченный интерпретатор арифметики/ветвлений
render-методов, без JVM, вызовов OpenGL, сети или запуска донорского клиента.
`tools/import_metromod_interactive.py` проверяет SHA клиента и каждого исходного
ресурса, отказывает на неизвестных операциях/полях, формирует отчёт отказов.

## Полный список выбранного каталога (112 записей)

1. `box/box_metal.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
2. `box/box_metal1.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
3. `box/box_metal2.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
4. `box/box_metal3.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
5. `box/box_metal4.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
6. `box/box_metal5.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
7. `box/box_books.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
8. `box/box_books_broken.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
9. `box/box_metal_4.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
10. `box/box_metal_5.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
11. `box/test_instrumentbox_01.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
12. `arsenal/box_patron.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
13. `arsenal/ammo_box.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
14. `arsenal/armorset_soldier_01.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
15. `arsenal/armorset_soldier_02.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
16. `arsenal/box_ak74.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
17. `arsenal/gunparts_01.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
18. `arsenal/rukzak.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
19. `arsenal/shotgun_parts.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
20. `arsenal/wpn_support.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
21. `columnes/round_column_svetilnik.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_columnes_round_column_svetilnik`.
22. `constr_metal/grate1_door.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_constr_metal_grate1_door`.
23. `germo/germo1.m2033model` — Germo: **работает с dev25**; `metroport:metromod_germo_1`.
24. `germo/germo2.m2033model` — Germo: **работает с dev25**; `metroport:metromod_germo_2`.
25. `germo/germo3.m2033model` — Germo: **добавлено dev26**; `metroport:metromod_germo_3`.
26. `germo/germo4.m2033model` — Germo: **работает с dev25**; `metroport:metromod_germo_4`.
27. `germo/germo5.m2033model` — Germo: **работает с dev25**; `metroport:metromod_germo_5`.
28. `germo/germo6.m2033model` — Germo: **работает с dev25**; `metroport:metromod_germo_6`.
29. `germo/germo7.m2033model` — Germo: **НЕ перенесено**. Причина: Uninitialized field null/lIjijlilIlIIIiIiIlIIIIlIijljllij.jjlliijIIIiIjIjiiIjijjIjIljjiiII on null/ljjijiliiljIljlliiiIiljiIliIIjIj.
30. `germo/germo8.m2033model` — Germo: **НЕ перенесено**. Причина: Uninitialized field null/lIjijlilIlIIIiIiIlIIIIlIijljllij.jjlliijIIIiIjIjiiIjijjIjIljjiiII on null/llliljiiijjjIljjjIjIiIjljliljjji.
31. `items/baggage/rukzak.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
32. `decor_metro/fonar_01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_fonar_01`.
33. `decor_metro/karbid_lamp_01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_karbid_lamp_01`.
34. `decor_metro/karbid_lamp_02.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_karbid_lamp_02`.
35. `decor_metro/l07_vorota.m2033model` — Germo: **НЕ перенесено**. Причина: Uninitialized field null/lIjijlilIlIIIiIiIlIIIIlIijljllij.jjlliijIIIiIjIjiiIjijjIjIljjiiII on null/jiIilIjjiIIjiliIiijiljjjlliilIIi.
36. `decor_metro/lamp_abajur_2.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lamp_abajur_2`.
37. `decor_metro/lamp_halogen_1.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lamp_halogen_1`.
38. `decor_metro/lamp_industrt01_01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lamp_industrt01_01`.
39. `decor_metro/lamp_kerosinka_06_nice.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lamp_kerosinka_06_nice`.
40. `decor_metro/lamp_kerosinka_07_nice.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lamp_kerosinka_07_nice`.
41. `decor_metro/lamp_na_stenu_01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lamp_na_stenu_01`.
42. `decor_metro/lamp_nastolny_01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lamp_nastolny_01`.
43. `decor_metro/lamp_projector_no_lp.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lamp_projector_no_lp`.
44. `decor_metro/lamp_small.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lamp_small`.
45. `decor_metro/lampa.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_lampa`.
46. `decor_metro/wpn_proj_01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_wpn_proj_01`.
47. `decor_metro/wpn_proj_02.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_decor_metro_wpn_proj_02`.
48. `door/d6_gate_platform_.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_d6_gate_platform_`.
49. `door/door_d06_l02_01.m2033model` — Germo: **добавлено dev26**; `metroport:metromod_germo_door_door_d06_l02_01`.
50. `door/door_hran.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_door_hran`.
51. `door/door_l00_hospital.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_door_l00_hospital`.
52. `door/l04_gate02_.m2033model` — Germo: **добавлено dev26**; `metroport:metromod_germo_door_l04_gate02_`.
53. `door/l04_t_zatvor.m2033model` — Germo: **добавлено dev26**; `metroport:metromod_germo_door_l04_t_zatvor`.
54. `door/l13_vorota_test.m2033model` — Germo: **добавлено dev26**; `metroport:metromod_germo_door_l13_vorota_test`.
55. `door/l17_door_closed.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_l17_door_closed`.
56. `door/l17_door_gate.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_l17_door_gate`.
57. `door/l21_clap_door.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_l21_clap_door`.
58. `door/l21_level_doors02.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_l21_level_doors02`.
59. `door/l21_level_doors03.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_l21_level_doors03`.
60. `door/l21_level_doors04.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_l21_level_doors04`.
61. `door/ostankino_door.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_ostankino_door`.
62. `door/vorota.m2033model` — Germo: **добавлено dev26**; `metroport:metromod_germo_door_vorota`.
63. `electro/props_rubilnik_01.m2033model` — Lever: **добавлено dev26**; `metroport:metromod_lever_electro_props_rubilnik_01`.
64. `electro/rubilnik_4.m2033model` — Lever: **добавлено dev26**; `metroport:metromod_lever_electro_rubilnik_4`.
65. `fence/kalitka_01.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_fence_kalitka_01`.
66. `fireplace/barrel_01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_fireplace_barrel_01`.
67. `fireplace/candle_church01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_fireplace_candle_church01`.
68. `fireplace/candle_church02.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_fireplace_candle_church02`.
69. `fireplace/fireplace_01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_fireplace_fireplace_01`.
70. `germo/germo9.m2033model` — Germo: **работает с dev25**; `metroport:metromod_germo_9`.
71. `house/small_door.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_house_small_door`.
72. `items/cyan_light_stick.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_items_cyan_light_stick`.
73. `items/green_light_stick.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_items_green_light_stick`.
74. `items/filter.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
75. `lamp/kerosinka_02.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_lamp_kerosinka_02`.
76. `lamp/kerosinka_03.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_lamp_kerosinka_03`.
77. `lamp/kerosinka_05.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_lamp_kerosinka_05`.
78. `lamp/l04_lampion01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_lamp_l04_lampion01`.
79. `lamp/l04_lampion02.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_lamp_l04_lampion02`.
80. `lamp/light_tripple1.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_lamp_light_tripple1`.
81. `lamp/svetilnik.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_lamp_svetilnik`.
82. `parom/ubl_ammo_01.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
83. `parom/ubl_ammo_02.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
84. `patron/ammo_01.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
85. `patron/mnogo_ammo_01.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
86. `patron/wpn_7_62_ammo.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
87. `patron/wpn_14_5x5_ammo.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
88. `props_library/wood_door.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_props_library_wood_door`.
89. `door/biblio_door_chit_zal_left.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_biblio_door_chit_zal_left`.
90. `door/biblio_door_chit_zal_right.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_biblio_door_chit_zal_right`.
91. `door/biblio_door_hall_razlom_left.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_biblio_door_hall_razlom_left`.
92. `door/biblio_door_hall_razlom_right.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_biblio_door_hall_razlom_right`.
93. `door/biblio_door_razlom.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_biblio_door_razlom`.
94. `door/door_2_left.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_door_2_left`.
95. `door/door_2_right.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_door_door_2_right`.
96. `station_props/lightbox.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_station_props_lightbox`.
97. `structure/d6_door_02.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_structure_d6_door_02`.
98. `structure/gates_01.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_structure_gates_01`.
99. `structure/l13_reshetka_skin.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_structure_l13_reshetka_skin`.
100. `structure/l19_polis_door.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_structure_l19_polis_door`.
101. `structure/mls_vorota_l06.m2033model` — Germo: **НЕ перенесено**. Причина: Required donor dependency structure/mls_vorota_l06_add2.m2033model is missing; partial preview is not a complete port.
102. `structure/reshetka_02.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_structure_reshetka_02`.
103. `structure/rubilnik_01.m2033model` — Lever: **добавлено dev26**; `metroport:metromod_lever_structure_rubilnik_01`.
104. `structure/rubilnik_02.m2033model` — Lever: **добавлено dev26**; `metroport:metromod_lever_structure_rubilnik_02`.
105. `structure/rubilnik_lift.m2033model` — Lever: **добавлено dev26**; `metroport:metromod_lever_structure_rubilnik_lift`.
106. `structure/shitovaya_01.m2033model` — Lever: **добавлено dev26**; `metroport:metromod_lever_structure_shitovaya_01`.
107. `structure/vorota_l03.m2033model` — Door: **добавлено dev26**; `metroport:metromod_door_structure_vorota_l03`.
108. `structure/vorota_resh_tower.m2033model` — Germo: **добавлено dev26**; `metroport:metromod_germo_structure_vorota_resh_tower`.
109. `structure/zatvor_01.m2033model` — Germo: **добавлено dev26**; `metroport:metromod_germo_structure_zatvor_01`.
110. `tunnel_props/tonnels_props_lamp_germ_01.m2033model` — Light: **добавлено dev26**; `metroport:metromod_light_tunnel_props_tonnels_props_lamp_germ_01`.
111. `wpn/predohranitel_bomb_sticky.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
112. `wpn/wpn_bomb_predohranitel.m2033model` — Inventory: **НЕ перенесено**. Причина: Client Inventory tag does not establish server storage slots or pickup rules; no invented container.
