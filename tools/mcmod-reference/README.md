# Original MCMOD migration reference

All 33 registered armor OBJ/atlas/icon triples and six original Java class files downloaded individually from the user's unpacked `MCMod.jar` folder on Google Drive. The full JAR is 146,057,952 bytes; it is not needed in this checkout. All raw retained files have sizes and SHA-256 in `source-files.json`.

`java/` was recovered with CFR 0.152 by `.github/workflows/inspect-mcmod.yml`. This is reference material, NOT compilable 1.12.2 code. Neither these class files nor decompiled sources are copied into the runtime JAR. Original authorship/rights remain unchanged. No general MCMOD license was recovered; see `../../THIRD_PARTY.md` before redistribution.

The downloaded `mcmod.info` was all zero bytes and is intentionally not retained as authoritative metadata. Other files were independently verified (CAFEBABE headers, valid PNG chunks, OBJ index/group checks).

Implemented subset, adaptations and outstanding work: `../../docs/MCMOD.md` and `../../docs/mcmod-report.json`.
