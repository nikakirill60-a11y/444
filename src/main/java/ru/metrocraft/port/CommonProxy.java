package ru.metrocraft.port;

/** Dedicated servers must never initialize Minecraft client renderers. */
public class CommonProxy {
    public void preInit() { }
    public void shotFeedback(String gun,float pitch,float yaw) {}
    public void survival(int dimension,int entity,float water,float energy,boolean lamp){}
    public void init() { }
}
