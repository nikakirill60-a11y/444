package ru.metrocraft.port.mcmod;

/** Source statistics + explicitly documented 1.12.2 slot adaptation. */
public final class McmodArmorEntry {
    public String id, model, mode, nameRu, nameEn;
    public int condition, sourceProtect, sourceType;
    public double defence;
    public boolean radiation;
    public boolean helmet() { return "helmet".equals(mode); }
    public boolean whole() { return "whole".equals(mode); }
}
