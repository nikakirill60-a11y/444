# METRO_MOD selected systems — first integration (dev.17)

Scope: gas masks/filters, watches, devices, consumables, six redstone hermetic gates, Kikimora/Nosach, graphics. **No donor weapons.** This release is only the first mask/watch integration, not completion of all selected systems. MWF and the user's tuned packv4 are unchanged.

## Included

- Filter right click uses the standard 60-tick held-use lifecycle (3 seconds at 20 TPS). No filter is spent and no mask is refilled before server completion. Releasing use cancels without effects.
- Completion revalidates a live, non-spectator player and a suitable HEAD/CHEST mask, preserving the port's `MetroFilterTicks=6000` five-minute capacity and unrelated NBT. Creative does not consume a filter. Missing/full masks, empty/foreign filter stacks and client-only completion cannot refill.
- McmodOrdinaryContent's MaskFilter inherits the same behavior; FilterItem stays non-final.
- Existing watch-dosimeter HUD gains remaining filter time, rounded up to seconds. Red warning at <=60 seconds. One alert per low-filter episode, including an already-exhausted worn mask. Refill or removal rearms the alert. No compatible mask means no countdown.
- BlackRazid watches remain time-only; no filter display or alarm. Existing watch slot, transactions, death retention and radiation readings are unchanged.
- If both head/chest contain valid masks, telemetry reports their maximum remaining time, matching existing HEAD OR CHEST radiation protection.
- New owner-only S2C packet ID 13 (three ints, 12 bytes) sends at first tick and once per second. No client refill packet, no equipment-item writes, no watch packet ABI change. Client discards queued packets for replaced worlds/players and hides stale telemetry after 60 client ticks.
- Countdown is a server snapshot, not a speculative client-side refill/decay model. Existing filter wear outside contaminated areas is deliberately unchanged.
- Alert currently uses vanilla BLOCK_NOTE_PLING. No claim that donor audio/hand animations have been decoded/imported.

## Donor evidence

Client SHA256 `66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc`, 8,188,088 bytes; full ZIP CRC passed. Only static inspection, no Java execution or login/config file access. Repeat with `tools/inspect_metro_selected.py` + jawa 2.2.0. Evidence at `reports/METRO_MOD/selected_bytecode/` includes class/dump hashes.

- `filter_item`: right click sets `timer=60`, update decrements it, completion writes `Time_Left=300` and removes the filter. Donor also has a used-filter branch; a new waste-filter item is NOT included here.
- `mask_item`: tooltip labels `Time_Left` as seconds; its protection method decrements the field behind a server/player-state condition. Exact world scheduling is not inferred from the tooltip.
- `watch_controller`: <=60 check, watch predicate, alarm sound and latch against repeated play.
- `flask_item`: right click simply returns its stack; checked update does not restore thirst despite a tooltip claiming it does. External handlers still need tracing. Existing functional BlackRazid flask/charger are retained instead of replaced with an inert donor item.
- Kikimora/Nosach constructors have actual AI task registrations and numeric attribute assignments, not just model names. Attribute/AI names are obfuscated and need further mapping before porting.
- Gates, full donor models and graphical pipeline are not imported in this increment. Large model archives exceed the connector limit, custom resources remain encoded. Six existing redstone gates remain unchanged.

## Validation and installation

Build on the current published branch base `7291973fc66482be44923130bf55555915a82f64`, not the old loose 1.5.1 source snapshot. That snapshot lacks later inventory/watch/radiation code. A targeted patch is retained at `reports/METRO_MOD/dev17_against_published.patch` for traceability. Changes are published to the same `arena/dcab859f-444` branch; unrelated old local work is not overwritten.

180 existing Python resource/source-contract tests passed locally with Pillow 13.0.0. Added 18 Java tests exercise actual ItemStacks/NBT, mocked player/world use hooks, timer formatting, alarm latching, packet bounds and head/chest readings. These are not a full Minecraft/network/render acceptance run. Forge build/JUnit results are recorded separately after CI.

For the dev.17 test build, replace **only MetroPort JAR**, on client and server together. Do not install two MetroPort JARs. Do not replace MWF metro4 or the user's packv4. Packet ID 13 requires matching MetroPort versions on both ends.

### In-game acceptance still required

1. Dedicated server and integrated server; two independent players.
2. Empty and partial mask, HEAD and sealed CHEST suit, MetroPort and MCMOD filters.
3. Hold 59 ticks, release: unchanged mask/filter. Hold full 60 ticks: one consumed, five minutes loaded (minus normal wear).
4. Remove mask, switch item/hand, die or enter spectator mid-use; no partial refill or duplication.
5. Full mask rejects; creative refills without spending; existing NBT/damage/NoDrop preserved.
6. Dosimeter in slot/main/offhand shows server time remaining; at <=60 seconds one alert only. Refill rearms. No mask hides timer. Expired mask reads 00:00.
7. BlackRazid time-only watch never gets filter HUD/alarm, including when worn while holding a detector watch.
8. Open/close inventory while telemetry packets arrive: taking out a watch must not restore/duplicate it.
9. Death/respawn, reconnect, dimension change, pause/unpause and packet delay: no stale timer or repeated alarm spam.
10. MWF firing/reload and tuned packv4 hands unchanged. New donor-specific first-person filter animation is not supplied.
