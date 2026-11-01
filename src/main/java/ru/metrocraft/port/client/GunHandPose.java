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
        if(approved){
            step.rotZ((float)Math.toRadians(support?-55:-65));result.mul(step);
            step.rotX((float)Math.toRadians(-(left?-1:1)*(support?30:18)));result.mul(step);
        }else{
            // Aim each forearm from its own outboard elbow to the palm. The former
            // coplanar Z/X rotations put BOTH elbows behind the stock on screen.
            Vector3f along=new Vector3f(support?.32F:.26F,support?.28F:.32F,left?.34F:-.34F);along.normalize();
            Vector3f across=new Vector3f();across.cross(new Vector3f(0,1,0),along);across.normalize();
            Vector3f depth=new Vector3f();depth.cross(across,along);depth.normalize();
            across.scale(.85F);along.scale(.85F);depth.scale(.85F);step.setIdentity();
            step.setColumn(0,across.x,across.y,across.z,0);step.setColumn(1,along.x,along.y,along.z,0);step.setColumn(2,depth.x,depth.y,depth.z,0);result.mul(step);
        }
        float center = (left ? 1 : -1)*(slim ? .5F : 1);
        step.setIdentity();
        // Anchor the palm CENTRE (y=9), not a shoulder/camera pivot or the end face at y=10.
        step.setTranslation(new Vector3f(-center/16F, -9/16F, 0)); result.mul(step);
        return result;
    }

}
