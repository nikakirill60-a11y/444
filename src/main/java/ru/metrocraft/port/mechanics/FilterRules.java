package ru.metrocraft.port.mechanics;

/** Donor replacement takes 60 ticks; keep the port's five-minute tick-based NBT. */
public final class FilterRules {
    public static final int REPLACEMENT_TICKS = 60;
    public static final int CAPACITY_TICKS = 6000;
    public static final int WARNING_TICKS = 60 * 20;
    private FilterRules() {}
    public static boolean canReplace(int remaining) { return remaining < CAPACITY_TICKS; }
    /** -1 means no compatible mask. Zero means a worn mask with an exhausted filter. */
    public static int sanitize(int ticks) { return Math.max(-1, Math.min(CAPACITY_TICKS, ticks)); }
    public static int seconds(int ticks) { return (Math.max(0, sanitize(ticks)) + 19) / 20; }
    public static String time(int ticks) {
        int seconds = seconds(ticks);
        return String.format(java.util.Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60);
    }
    public static boolean low(int ticks) { return ticks >= 0 && ticks <= WARNING_TICKS; }
}
