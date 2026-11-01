# BlackRazidMetro migration reference

Original legacy class files supplied by the user in Google Drive folder
`blackrazidmetro` (`1u3snbncRSvARWyceeoHUilIw2MTdOALv`).
CFR 0.152 recovers registration, armor mesh, pivot, texture and backpack behavior
as migration reference only. Nothing in this directory is compiled into the mod.
Original authorship applies; see ../../THIRD_PARTY.md.

Weapon references now cover all 22 selected gun classes. Asset provenance is split into
`weapons-source-files.json` (78 originals) and `weapons-special-source-files.json`
(59 originals, including the two visual-only flame helpers). Baseline runtime profiles
live in `../blackrazid-special-profiles.json`; native models, modules and exact FX are
not implemented merely because references have been recovered.
