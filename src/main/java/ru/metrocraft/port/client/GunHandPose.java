package ru.metrocraft.port.client;

/** Wrist contacts in native gun metres, before the shared gun/aim/recoil camera transform. */
public final class GunHandPose {
    private GunHandPose() {}
    public static boolean usesSupport(boolean renderLeft, boolean primaryLeft, boolean mainEmpty, boolean offEmpty) {
        // Never draw the same other arm a second time when it holds another item/gun.
        return renderLeft == primaryLeft ? offEmpty : mainEmpty;
    }
    public static float[] wrist(float[] anchor, boolean support, boolean left, float reload) {
        float side = left ? -1 : 1;
        float p = Float.isFinite(reload) ? Math.max(0, Math.min(1, reload)) : 0;
        return new float[]{anchor[0] - (support ? .12F*p : 0),
                anchor[1] - (support ? .07F + .22F*p : .10F),
                anchor[2] + side*(support ? -.08F : .055F)};
    }
}
