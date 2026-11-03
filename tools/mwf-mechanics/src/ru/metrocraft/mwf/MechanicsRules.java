package ru.metrocraft.mwf;

/** Pure, deterministic rules; no Minecraft/client classes and no mutable GunType state. */
public final class MechanicsRules {
    private MechanicsRules() {}
    public static boolean energyGun(String id) {
        return "metro_tihar".equals(id) || "metro_helsing".equals(id) || "metro_railgun".equals(id);
    }
    public static int maximum(String id, boolean battery) { return "metro_railgun".equals(id) ? (battery ? 150 : 100) : 120; }
    public static int cost(String id) { return "metro_helsing".equals(id) ? 18 : 8; }
    public static int clamp(int value, int maximum) { return Math.max(0, Math.min(value, maximum)); }
    public static int pump(int energy, int maximum) { return Math.min(maximum, clamp(energy, maximum) + 20); }
    public static int afterShot(String id, int energy, int maximum) { return Math.max(0, clamp(energy, maximum) - cost(id)); }
    public static float damage(String id, int energy, long ticksSinceRailShot, boolean capacitor) {
        float level = clamp(energy, 150) / 100.0f;
        float result = .35f + .65f * Math.min(1.0f, level);
        if (!"metro_railgun".equals(id)) return result + Math.max(0, Math.min(.2f, level - 1));
        int fullChargeTicks = capacitor ? 30 : 40; // capacitor: exactly 25% less accumulation time
        return result * Math.max(.25f, Math.min(1f, ticksSinceRailShot / (float) fullChargeTicks));
    }
    public static int leak(int energy, long elapsedTicks) {
        return energy > 100 ? Math.max(100, energy - (int)Math.min(energy - 100, Math.max(0, elapsedTicks) / 20)) : energy;
    }
    public static int capacity(String id, boolean extraBarrels, int baseline) {
        return "metro_duplet".equals(id) ? (extraBarrels ? 4 : 2) : baseline;
    }
    public static int fireInterval(int roundsPerMinute) { return Math.max(1, (int)Math.ceil(1200.0 / Math.max(1, roundsPerMinute))); }
}
