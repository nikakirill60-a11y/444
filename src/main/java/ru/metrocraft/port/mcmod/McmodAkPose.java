package ru.metrocraft.port.mcmod;
/** Original millimetre OBJ -> existing, tested AK native hand space (+X points to muzzle). */
public final class McmodAkPose {
    public static final float SCALE=.0005F, X=.175F,Y=.38F,Z=-.175F;
    private McmodAkPose(){}
    public static float[] point(float x,float y,float z){return new float[]{X-z*SCALE,Y+y*SCALE,Z+x*SCALE};}
}
