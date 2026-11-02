# Metro Full Arsenal / Minecraft 1.12.2 / closed MWF fix1 — v1

[Download the single content ZIP](https://github.com/nikakirill60-a11y/444/raw/refs/heads/arena/dcab859f-444/downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip)

**Inventory-complete adaptation, not a complete recreation of the old mod's special mechanics.**
No companion JAR. Nothing from the recovered legacy sources is restored into MetroPort runtime directories.

## Delivery

- `downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip` — **83,633,123 bytes**.
- SHA-256: `0403f8fced602d38ce21300edcc2779bc1b79403a0a48fab1fb5662d9614b915`.
- Exact target JAR SHA-256: `f91a5419cbc9e3cbd18f831e37c17aa2833d577142d324fed1ca14846ce95183`.
- Install the ZIP intact in `.minecraft/ModularWarfare/` on client and server; remove earlier overlapping Uboinik/Duplet content ZIPs.
- Back up the world. Before updating, unload old four-round Duplets and replace any old `metro_duplet_4` items with `metro_duplet` plus the cosmetic attachment. The duplicate gun registry ID is intentionally removed in this full pack.
- Detailed Russian instructions: `README_FULL_PACK_RU.txt` inside the ZIP.

## Inventory and preservation

23 distinct weapons, 31 attachments, 21 magazine/canister items, 9 loose ammunition types. The 28 original source modules are all represented: five map to the existing Duplet attachments, 23 receive their own native module entries. The three unique current Uboinik attachments are retained, resulting in 31 attachments, not 28 duplicated variants.

Roster: Ubludok, AK2012, AK, AKSU, Rpk, Vsv, Ashot, Duplet, Gatling, Helsing, Tihar, Podonok, Preved, Railgun, Revolver, Saiga, Abzac, Uboinik, Ventil, Vyhlop, Velopal, Ognemet, AKK.

AKK was omitted by the earlier MetroPort inventory importer, but **is separately constructed in the original `ModBase`** (`AKK = new AKK(2281, 30, 5)`). Its distinct native model is included; handling/balance use explicitly provisional AK-family settings and no unproven sockets are guessed.

The current Uboinik v5 and Duplet v2 model, pose, gun definition, render configuration, skins, and attachments remain byte-identical to `Metro_Uboinik_Duplet_MWF_fix1_v2.zip`. The old cosmetic *gun duplicate* is omitted, not the actual extra-barrels attachment. Duplet remains **2 rounds**, with the extra barrel pair purely decorative.

The original 12/70 item is mapped to the retained `metro_12g_buckshot` rather than introducing two interchangeable copies.

## Native resources and rendering

- Pinned historical source `10a6aa2`, recovered only under `.cache/full-pack/build/legacy/` through `git archive`.
- Additional native multipart OBJ/MTL/PNG from the user's unpacked Drive `Mod`, archived with 90 per-file SHA-256 records under `tools/mwf-full-pack/native-sources.*`.
- Strict compiler converts resting source Java scene branches into inert geometry data. No old Java is executed or packaged.
- MWF hide/show geometry preserves gun-specific source module positions and replacement parts; the inventory OBJ remains a real native model. The external on-gun inventory mesh is collapsed to avoid double rendering its baked native counterpart.
- Simultaneous multi-material PNGs are baked into one common atlas, **not selectable alternate skins**. Railgun keeps all four primary materials and Podonok keeps both body/addon materials. Repeated source UVs are handled before atlasing, not incorrectly modulo-wrapped per vertex.
- The shared atlas has 60 deduplicated image/solid materials at 4096×4096, with source image tiles capped at 508 px. Separate MWF skin categories have identical copies to support the engine's gun/ammo/attachment resource lookups. There are not 21 duplicate full-sized gun atlases.
- Native first-person Ashot/Revolver round positions are recovered in addition to resting equipped geometry. Detachable magazine rounds live in `bulletModel_N` OBJ groups handled by MWF's remaining-ammo loop. 234 native visible magazine round surfaces are carried across magazine types. Some magazines only expose a fraction of their total rounds; no fictional geometry is invented to fill hidden capacities.
- Original icons and sounds are included; current Duplet sound reuse from Uboinik is preserved.

## Explicit limits

- Pressure, manual pumping, rail charge, flame stream, original bolt/ball projectile physics, and rotor motion are **not implemented**. Ognemet is clearly labelled a normal-MWF bullet adaptation, not a functional flamethrower.
- `AutoFire` is a decorative attachment; it does not alter a gun's allowed fire modes.
- MWF ammo does not enforce the old flamethrower fuel-only refill policy.
- Empty source fitting branches are not presented as functioning sockets. `omittedSockets` lists each missing source rendering branch; every one of the 28 source module items is nevertheless installable on at least one supported gun.
- New hold/ADS/reload configurations are provisional adaptations fitted to the native geometry. Existing approved Uboinik/Duplet poses are unchanged. **No Minecraft/OpenGL run was performed.**
- The merged current Duplet has no separate cartridges or hinge; no fake round disappearance or break-action animation is claimed for it.
- The source Velopal has no separately rendered magazine: its ammo item remains functional but no unrelated floating magazine is drawn.
- Existing source ownership applies to assets. The tool code does not relicense those models, textures, or sounds.

`FULL_PACK_MANIFEST.json` inside the ZIP gives the roster, module mapping, source parts, magazine capacities, material provenance and limitations. The two original source manifests document the baseline packs, not the new roster; the full manifest supersedes their historical duplicate-gun alias.

## Rebuild and verification

```sh
python -m pip install -r tools/mwf-full-pack/requirements.txt
python tools/mwf-full-pack/test_full_pack.py -v
python tools/build_full_mwf_pack.py
python tools/verify_full_mwf_pack.py downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip
# Optional: use the actual selected JAR and a Java runtime with JPype1:
python tools/verify_full_mwf_pack.py downloads/Metro_Full_Arsenal_MWF_fix1_v1.zip \
  --mwf-jar /path/to/selected.jar --jvm /path/to/libjvm.so
```

Use a fresh `--output` directory for each rebuild. Full repository history is required for the pinned source snapshot. The builder refuses to mix an existing output tree with newly generated files.

Local results:
- **12 unit tests PASS**: source hashes, strict conditions, OBJ/material boundaries, UV clipping, source transforms, all supported independent attachment combinations, Gatling replacement rather than stacking, simultaneous Railgun/Podonok images, no runtime-source restoration.
- Full pack validator **PASS**: 487 files; all registries, icons, skins, sounds, localizations, magazine and bullet dependencies, geometry hide/show names, embedded buffers, uint16 primitives and finite animation data.
- **Actual closed-JAR schema/enum validation PASS** for all definitions and nested configurations.
- **Actual closed-JAR glTF loader PASS for all 23 gun models**, 281,374 total triangles across model assets and variants.
- Byte-identical baseline checks **PASS**.

These tests are not evidence of successful Minecraft startup or visually approved new poses.
