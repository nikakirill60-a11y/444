package ru.metrocraft.port.blackrazid.grenades;
/** Source custom explosion used a 5.5 strength / 11-block entity radius, without terrain destruction. */
public final class GrenadeRules {
    public static final int FUSE=101;
    public static final double RADIUS=11;
    private GrenadeRules(){}
    public static int damage(boolean big,double distance,double exposure){
        if(!Double.isFinite(distance) || !Double.isFinite(exposure) || distance<0 || distance>=RADIUS)return 0;
        return (int)((1-distance/RADIUS)*Math.max(0,Math.min(1,exposure))*(big?15:10));
    }
    public static int age(int age){return Math.max(0,Math.min(FUSE,age));}
}
