package ru.metrocraft.port.client;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

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
                anchor[1] - (support ? .22F*p : 0),
                anchor[2] + side*(support ? -.025F : .025F)};
    }
    /** The very same matrix is used by OpenGL and by the palm-contact JVM regression tests. */
    public static Matrix4f armMatrix(float[] contact, boolean left, boolean support, boolean slim) {
        return armMatrix("revolver",contact,left,support,slim);
    }
    /** Revolver retains the approved pose; rifles use a slimmer, less rolled underhand grip. */
    public static Matrix4f armMatrix(String id,float[] contact,boolean left,boolean support,boolean slim) {
        boolean approved="revolver".equals(id);
        Matrix4f result = new Matrix4f(), step = new Matrix4f();
        result.setIdentity(); result.setTranslation(new Vector3f(contact));
        step.rotZ((float)Math.toRadians(approved ? (support ? -55 : -65) : (support ? -42 : -48))); result.mul(step);
        step.rotX((float)Math.toRadians(-(left ? -1 : 1)*(approved ? (support ? 30 : 18) : (support ? 10 : 12)))); result.mul(step);
        if(!approved){step.setIdentity();step.setScale(.85F);result.mul(step);}
        float center = (left ? 1 : -1)*(slim ? .5F : 1);
        step.setIdentity();
        // Anchor the palm CENTRE (y=9), not a shoulder/camera pivot or the end face at y=10.
        step.setTranslation(new Vector3f(-center/16F, -9/16F, 0)); result.mul(step);
        return result;
    }

}
