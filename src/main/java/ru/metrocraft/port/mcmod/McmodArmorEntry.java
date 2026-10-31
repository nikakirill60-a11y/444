package ru.metrocraft.port.mcmod;

/** Source statistics + explicitly documented 1.12.2 slot adaptation. */
public final class McmodArmorEntry {
    public String id, model, visualModel, sourceId, mode, nameRu, nameEn;
    public int condition, sourceProtect, sourceType;
    public double defence;
    public boolean radiation;
    /** Slot partition is shared with regression tests; every original group is rendered exactly once. */
    public boolean rendersGroup(String group, boolean separateHead) {
        if (helmet()) return "head".equals(group);
        if ("head".equals(group)) return whole() && !separateHead;
        return true;
    }
    /** Corrected visual association; model/sourceId still identify the item's original statistics and icon. */
    public String geometryModel() { return visualModel == null || visualModel.isEmpty() ? model : visualModel; }
    public boolean helmet() { return "helmet".equals(mode); }
    public boolean whole() { return "whole".equals(mode); }
}
