package ru.metrocraft.port.client;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/** Pure shared animation math; no GL, world, damage, packets or use-state mutation. */
public final class ToolActionPose {
    private ToolActionPose() {}
    public static boolean animated(String id) { return "mcmod_machete".equals(id) || "mcmod_aptech".equals(id); }
    public static float unit(float p) { return Float.isFinite(p)?Math.max(0,Math.min(1,p)):0; }
    private static float smooth(float p) { p=unit(p); return p*p*(3-2*p); }
    public static float slash(float swing) { return (float)Math.sin(Math.PI*unit(swing)); }
    public static float treatment(float progress) { return (float)Math.sin(Math.PI*unit(progress)); }
    public static float extraction(float progress) {
        float p=unit(progress);
        return smooth(p/.25F)*(1-smooth((p-.72F)/.28F));
    }
    /** Mesh grip, not the diagonal handle of the flat inventory icon. Forge mirrors left matrices. */
    public static Matrix4f thirdPerson(String id,boolean left){
        boolean medical="mcmod_aptech".equals(id);
        return GunRenderPoses.compose(medical?new float[]{0,-.13F,.025F}:new float[]{0,0,0},
                new float[]{0,.05F,.10F},medical?new float[]{0,0,0}:new float[]{0,90,0},
                medical?1.25F:.72F,false,left);
    }
    /** Palm contacts in tool/camera metres; exactly the matrix submitted to OpenGL. */
    public static Matrix4f armMatrix(float[] contact,boolean skinLeft,boolean slim,boolean support) {
        Matrix4f result=new Matrix4f(), step=new Matrix4f();
        result.setIdentity(); result.setTranslation(new Vector3f(contact));
        step.rotZ((float)Math.toRadians(support?-35:10)); result.mul(step);
        step.rotX((float)Math.toRadians(support?-55:-65)); result.mul(step);
        float center=(skinLeft?1:-1)*(slim?.5F:1);
        step.setIdentity(); step.setTranslation(new Vector3f(-center/16F,-9/16F,0)); result.mul(step);
        return result;
    }
}
