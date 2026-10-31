package ru.metrocraft.port.ballistics;

import ru.metrocraft.port.math.RotatedAxes;
import ru.metrocraft.port.math.vector.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.*;

/** Original four player bones, their animation math and oriented-box ray intersections. */
public final class LagSnapshot {
    public enum Part {BODY,HEAD,LEFTARM,RIGHTARM}
    public static final class Hit {
        public final Part part; public final float fraction;
        Hit(Part part,float fraction){this.part=part;this.fraction=fraction;}
        public float damageMultiplier(){return part==Part.HEAD?2F:part==Part.BODY?1F:.6F;}
    }
    private final Vector3f pos;
    private final List<BoneBox> hitboxes=new ArrayList<>();
    public LagSnapshot(double x,double y,double z,float bodyYaw,float headYaw,float pitch,int age){
        pos=new Vector3f(x,y,z);
        RotatedAxes bodyAxes = new RotatedAxes(bodyYaw, 0.0f, 0.0f);
        RotatedAxes headAxes = new RotatedAxes(headYaw - bodyYaw, 0.0f, -pitch);
        this.hitboxes.add(new BoneBox(bodyAxes, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(-0.25f, 0.0f, -0.15f), new Vector3f(0.5f, 1.4f, 0.3f), Part.BODY));
        this.hitboxes.add(new BoneBox(bodyAxes.findLocalAxesGlobally(headAxes), new Vector3f(0.0f, 1.4f, 0.0f), new Vector3f(-0.25f, 0.0f, -0.25f), new Vector3f(0.5f, 0.5f, 0.5f), Part.HEAD));
        float yHead = (headYaw - bodyYaw) / 57.295776f;
        float xHead = pitch / 57.295776f;
        float zRight = 0.0f;
        float zLeft = 0.0f;
        float yRight = -0.1f + yHead - 1.5707964f;
        float yLeft = 0.1f + yHead + 0.4f - 1.5707964f;
        float xRight = -1.5707964f + xHead;
        float xLeft = -1.5707964f + xHead;
        RotatedAxes leftArmAxes = new RotatedAxes().rotateGlobalPitchInRads(xLeft -= MathHelper.sin((float)((float)age * 0.067f)) * 0.05f).rotateGlobalYawInRads((float)Math.PI + yLeft).rotateGlobalRollInRads(-(zLeft -= MathHelper.cos((float)((float)age * 0.09f)) * 0.05f + 0.05f));
        RotatedAxes rightArmAxes = new RotatedAxes().rotateGlobalPitchInRads(xRight += MathHelper.sin((float)((float)age * 0.067f)) * 0.05f).rotateGlobalYawInRads((float)Math.PI + yRight).rotateGlobalRollInRads(-(zRight += MathHelper.cos((float)((float)age * 0.09f)) * 0.05f + 0.05f));
        float originZRight = MathHelper.sin((float)(-bodyYaw * (float)Math.PI / 180.0f)) * 5.0f / 16.0f;
        float originXRight = -MathHelper.cos((float)(-bodyYaw * (float)Math.PI / 180.0f)) * 5.0f / 16.0f;
        float originZLeft = -MathHelper.sin((float)(-bodyYaw * (float)Math.PI / 180.0f)) * 5.0f / 16.0f;
        float originXLeft = MathHelper.cos((float)(-bodyYaw * (float)Math.PI / 180.0f)) * 5.0f / 16.0f;
        this.hitboxes.add(new BoneBox(bodyAxes.findLocalAxesGlobally(leftArmAxes), new Vector3f(originXLeft, 1.3f, originZLeft), new Vector3f(-0.125f, -0.6f, -0.125f), new Vector3f(0.25f, 0.7f, 0.25f), Part.LEFTARM));
        this.hitboxes.add(new BoneBox(bodyAxes.findLocalAxesGlobally(rightArmAxes), new Vector3f(originXRight, 1.3f, originZRight), new Vector3f(-0.125f, -0.6f, -0.125f), new Vector3f(0.25f, 0.7f, 0.25f), Part.RIGHTARM));
    }
    public Hit trace(Vec3d start,Vec3d end){
        Vector3f origin=Vector3f.sub(new Vector3f(start),pos,null);
        Vector3f motion=Vector3f.sub(new Vector3f(end),new Vector3f(start),null);
        Hit nearest=null;
        for(BoneBox box:hitboxes){
            Hit hit=box.raytrace(origin,motion);
            if(hit!=null && hit.fraction>=0 && hit.fraction<=1 && (nearest==null||hit.fraction<nearest.fraction))nearest=hit;
        }
        return nearest;
    }
    private static final class BoneBox {
        final RotatedAxes axes;final Vector3f point,origin,dimensions;final Part type;
        BoneBox(RotatedAxes axes,Vector3f point,Vector3f origin,Vector3f dimensions,Part type){
            this.axes=axes;this.point=point;this.origin=origin;this.dimensions=dimensions;this.type=type;
        }
    public Hit raytrace(Vector3f origin, Vector3f motion) {
        float intersectX;
        float intersectZ;
        float intersectTime;
        origin = Vector3f.sub(origin, this.point, null);
        origin = this.axes.findGlobalVectorLocally(origin);
        motion = this.axes.findGlobalVectorLocally(motion);
        if (motion.x != 0.0f) {
            float intersectY;
            if (origin.x < this.origin.x) {
                intersectTime = (this.origin.x - origin.x) / motion.x;
                intersectY = origin.y + motion.y * intersectTime;
                intersectZ = origin.z + motion.z * intersectTime;
                if (intersectY >= this.origin.y && intersectY <= this.origin.y + this.dimensions.y && intersectZ >= this.origin.z && intersectZ <= this.origin.z + this.dimensions.z) {
                    return new Hit(this.type, intersectTime);
                }
            } else if (origin.x > this.origin.x + this.dimensions.x) {
                intersectTime = (this.origin.x + this.dimensions.x - origin.x) / motion.x;
                intersectY = origin.y + motion.y * intersectTime;
                intersectZ = origin.z + motion.z * intersectTime;
                if (intersectY >= this.origin.y && intersectY <= this.origin.y + this.dimensions.y && intersectZ >= this.origin.z && intersectZ <= this.origin.z + this.dimensions.z) {
                    return new Hit(this.type, intersectTime);
                }
            }
        }
        if (motion.z != 0.0f) {
            float intersectY;
            if (origin.z < this.origin.z) {
                intersectTime = (this.origin.z - origin.z) / motion.z;
                intersectX = origin.x + motion.x * intersectTime;
                intersectY = origin.y + motion.y * intersectTime;
                if (intersectX >= this.origin.x && intersectX <= this.origin.x + this.dimensions.x && intersectY >= this.origin.y && intersectY <= this.origin.y + this.dimensions.y) {
                    return new Hit(this.type, intersectTime);
                }
            } else if (origin.z > this.origin.z + this.dimensions.z) {
                intersectTime = (this.origin.z + this.dimensions.z - origin.z) / motion.z;
                intersectX = origin.x + motion.x * intersectTime;
                intersectY = origin.y + motion.y * intersectTime;
                if (intersectX >= this.origin.x && intersectX <= this.origin.x + this.dimensions.x && intersectY >= this.origin.y && intersectY <= this.origin.y + this.dimensions.y) {
                    return new Hit(this.type, intersectTime);
                }
            }
        }
        if (motion.y != 0.0f) {
            if (origin.y < this.origin.y) {
                intersectTime = (this.origin.y - origin.y) / motion.y;
                intersectX = origin.x + motion.x * intersectTime;
                intersectZ = origin.z + motion.z * intersectTime;
                if (intersectX >= this.origin.x && intersectX <= this.origin.x + this.dimensions.x && intersectZ >= this.origin.z && intersectZ <= this.origin.z + this.dimensions.z) {
                    return new Hit(this.type, intersectTime);
                }
            } else if (origin.y > this.origin.y + this.dimensions.y) {
                intersectTime = (this.origin.y + this.dimensions.y - origin.y) / motion.y;
                intersectX = origin.x + motion.x * intersectTime;
                intersectZ = origin.z + motion.z * intersectTime;
                if (intersectX >= this.origin.x && intersectX <= this.origin.x + this.dimensions.x && intersectZ >= this.origin.z && intersectZ <= this.origin.z + this.dimensions.z) {
                    return new Hit(this.type, intersectTime);
                }
            }
        }
        return null;
    }

    }
}
