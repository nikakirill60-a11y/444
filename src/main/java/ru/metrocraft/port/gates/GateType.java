package ru.metrocraft.port.gates;

public enum GateType {
    MAIN("germo", false, 0), OPEN("germo_open", false, 1), RIGHT("germo_right", false, 0),
    LEFT("germo_left", true, 0), LEFT45("germo_left45", true, .5F), HINGED("germo_half_open", true, .5F);
    public final String id;
    public final boolean swing;
    public final float initial;
    GateType(String id, boolean swing, float initial) { this.id = id; this.swing = swing; this.initial = initial; }
}
