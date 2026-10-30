package ru.metrocraft.port;

/** Dedicated servers must never initialize Minecraft client renderers. */
public class CommonProxy {
    public void preInit() { }
    public void init() { }
}
