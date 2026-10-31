package ru.metrocraft.port.mcmod;

import javax.vecmath.Matrix4f;

/** Shared by renderer and CPU regression tests. Column-vector transforms match recovered GL calls. */
public final class McmodArmorPose {
    private McmodArmorPose() {}
    private static Matrix4f identity() { Matrix4f m=new Matrix4f(); m.setIdentity(); return m; }
    private static Matrix4f translation(float x,float y,float z) { Matrix4f m=identity(); m.m03=x; m.m13=y; m.m23=z; return m; }
    public static Matrix4f local(String group) {
        float x=0,y,sy=1.05F;
        switch(group) {
            case "head": y=-1.5F; break;
            case "body": y=-1.45F; break;
            case "rightarm": x=.32F; y=-1.34F; sy=1.1F; break;
            case "leftarm": x=-.31F; y=-1.34F; sy=1.1F; break;
            case "rightleg": x=.125F; y=-.7F; break;
            case "leftleg": x=-.12F; y=-.7F; break;
            default: throw new IllegalArgumentException("Unknown armor group: "+group);
        }
        Matrix4f result=identity(); result.rotX((float)Math.PI);
        Matrix4f scale=identity(); scale.m00=1.05F; scale.m11=sy; scale.m22=1.05F;
        result.mul(scale); result.mul(translation(x,y,0));
        Matrix4f rotation=identity(); rotation.rotY(-(float)Math.PI/2); result.mul(rotation);
        return result;
    }
    /** Do not use ModelRenderer.postRender: CHEST hides HEAD/LEGS, silently dropping their joint transforms. */
    public static Matrix4f posed(String group, float scale, float px,float py,float pz,
                                 float rx,float ry,float rz,float ox,float oy,float oz) {
        Matrix4f result=translation(ox+px*scale,oy+py*scale,oz+pz*scale), rotation=identity();
        rotation.rotZ(rz); result.mul(rotation);
        rotation.rotY(ry); result.mul(rotation);
        rotation.rotX(rx); result.mul(rotation);
        result.mul(local(group));
        return result;
    }
}
