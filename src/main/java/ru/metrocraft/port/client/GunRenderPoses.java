package ru.metrocraft.port.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.util.ResourceLocation;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/** Native gun-space -> hand-space. No item/generated camera transforms. */
public final class GunRenderPoses {
    private static final class Pose { float[] translation, rotation; float scale; }
    private static final class Profile { float[] grip, trigger, support, aimSight; Pose firstPerson, thirdPerson, ground, fixed; }
    private static Map<String, Profile> profiles;

    public static void reload(){profiles=null;}
    private static Profile profile(String id) {
        if (profiles == null) {
            try (InputStreamReader reader = new InputStreamReader(Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("metroport:geometry/gun_poses.json")).getInputStream(), StandardCharsets.UTF_8)) {
                profiles = new Gson().fromJson(reader, new TypeToken<Map<String, Profile>>() {}.getType());
            } catch (Exception e) { throw new IllegalStateException("Cannot load gun poses", e); }
        }
        if(id.startsWith("blackrazid_") && !profiles.containsKey(id)){
            try(InputStreamReader reader=new InputStreamReader(Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("metroport:geometry/blackrazid_weapon_poses.json")).getInputStream(),StandardCharsets.UTF_8)){
                Map<String,Profile> nativeProfiles=new Gson().fromJson(reader,new TypeToken<Map<String,Profile>>(){}.getType());
                for(Map.Entry<String,Profile> entry:nativeProfiles.entrySet()){
                    if(profiles.containsKey(entry.getKey()))throw new IllegalStateException("Native pose cannot overwrite existing pose: "+entry.getKey());
                    profiles.put(entry.getKey(),entry.getValue());
                }
            }catch(Exception e){throw new IllegalStateException("Cannot load native gun poses",e);}
        }
        Profile profile = profiles.get(id);
        if (profile == null) throw new IllegalStateException("Missing gun pose: " + id);
        return profile;
    }
    public static float[] handAnchor(String id, boolean support) {
        Profile p = profile(id);
        return (support ? p.support : p.trigger).clone();
    }
    public static Matrix4f matrix(String id, TransformType type) {
        Profile profile = profile(id);
        boolean left = type == TransformType.FIRST_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND;
        Pose pose;
        switch (type) {
            case FIRST_PERSON_LEFT_HAND: case FIRST_PERSON_RIGHT_HAND: pose = profile.firstPerson; break;
            case THIRD_PERSON_LEFT_HAND: case THIRD_PERSON_RIGHT_HAND: pose = profile.thirdPerson; break;
            case GROUND: pose = profile.ground; break;
            default: pose = profile.fixed;
        }
        float[] translation=pose.translation.clone(),rotation=pose.rotation.clone();
        if(type==TransformType.FIRST_PERSON_LEFT_HAND || type==TransformType.FIRST_PERSON_RIGHT_HAND){
            float aim=GunClient.aim;
            float[] target=profile.aimSight==null?new float[]{-.56F,.42F,translation[2]}:
                    aimTranslation(profile.grip,profile.aimSight,pose.scale,left,translation[2]);
            translation[0]+=(target[0]-translation[0])*aim;
            translation[1]+=(target[1]-translation[1])*aim;
            rotation[1]+=(90-rotation[1])*aim;
        }
        return compose(profile.grip, translation, rotation, pose.scale,
                type == TransformType.FIRST_PERSON_LEFT_HAND || type == TransformType.FIRST_PERSON_RIGHT_HAND, left);
    }
    /** Align native iron sights after vanilla's (+/-.56,-.52,-.72) hand parent.
     * Left-hand translation is pre-compensated for compose's tx inversion.
     * Old profiles omit aimSight and retain their approved .42 target unchanged.
     */
    static float[] aimTranslation(float[] grip,float[] sight,float scale,boolean left,float z){
        return new float[]{-.56F+(left?1:-1)*scale*(sight[2]-grip[2]),
                .52F-scale*(sight[1]-grip[1]),z};
    }
    /** Pure math entry point shared with JVM tests; no Minecraft/OpenGL initialization. */
    static Matrix4f compose(float[] grip, float[] translation, float[] rotation, float scale, boolean firstPerson, boolean left) {
        float tx = translation[0], ry = rotation[1];
        if (firstPerson && left) { tx = -tx; ry = 180 - ry; }
        Matrix4f result = identity(), step = new Matrix4f();
        result.setTranslation(new Vector3f(tx, translation[1], translation[2]));
        step.rotZ(radians(rotation[2])); result.mul(step);
        step.rotY(radians(ry)); result.mul(step);
        step.rotX(radians(rotation[0])); result.mul(step);
        step.setIdentity(); step.setScale(scale); result.mul(step);
        step.setIdentity();
        step.setTranslation(new Vector3f(-grip[0], -grip[1], -grip[2]));
        result.mul(step);
        if (left) {
            // Forge 1.12 conjugates left-hand matrices by flipX. Compensate that
            // here: otherwise a +X native barrel points BACK towards the camera.
            Matrix4f flipX = identity(); flipX.m00 = -1;
            result.mul(flipX, result); result.mul(flipX);
        }
        return result;
    }
    private static float radians(float angle) { return (float) Math.toRadians(angle); }
    private static Matrix4f identity() { Matrix4f m = new Matrix4f(); m.setIdentity(); return m; }
}
