package ru.metrocraft.port.mcmod;
/** Original millimetre OBJ -> existing, tested AK native hand space (+X points to muzzle). */
public final class McmodAkPose {
    public static final float SCALE=.0005F, X=.175F,Y=.38F,Z=-.175F;
    private McmodAkPose(){}
    // The OBJ foregrip is NOT at the Flan model's wrist: use its own underside contact.
    public static float[] handAnchor(boolean support){return support?new float[]{.30F,.25F,-.175F}:new float[]{-.115F,.115F,-.175F};}
    public static float[] point(float x,float y,float z){return new float[]{X-z*SCALE,Y+y*SCALE,Z+x*SCALE};}
}
