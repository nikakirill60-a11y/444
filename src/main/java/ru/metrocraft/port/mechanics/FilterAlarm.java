package ru.metrocraft.port.mechanics;

/** One alert per low-filter episode, not every packet/frame. Does not own equipment. */
public final class FilterAlarm {
    private boolean notified;
    public boolean update(int ticks, boolean detectorWatchVisible) {
        if (!FilterRules.low(ticks)) { notified = false; return false; }
        if (!detectorWatchVisible || notified) return false;
        notified = true;
        return true;
    }
}
