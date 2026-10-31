package ru.metrocraft.port.gates;

public enum GateType {
    MAIN("germo", false), OPEN("germo_open", false), RIGHT("germo_right", false),
    LEFT("germo_left", true), LEFT45("germo_left45", true), HINGED("germo_half_open", true);
    public final String id;
    public final boolean swing;
    GateType(String id, boolean swing) { this.id = id; this.swing = swing; }
}
