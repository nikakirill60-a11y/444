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
    private static final class Profile { float[] grip; Pose firstPerson, thirdPerson, ground, fixed; }
    private static Map<String, Profile> profiles;

    public static Matrix4f matrix(String id, TransformType type) {
        if (profiles == null) {
            try (InputStreamReader reader = new InputStreamReader(Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("metroport:geometry/gun_poses.json")).getInputStream(), StandardCharsets.UTF_8)) {
                profiles = new Gson().fromJson(reader, new TypeToken<Map<String, Profile>>() {}.getType());
            } catch (Exception e) { throw new IllegalStateException("Cannot load gun poses", e); }
        }
        Profile profile = profiles.get(id);
        if (profile == null) throw new IllegalStateException("Missing gun pose: " + id);
        boolean left = type == TransformType.FIRST_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND;
        Pose pose;
        switch (type) {
            case FIRST_PERSON_LEFT_HAND: case FIRST_PERSON_RIGHT_HAND: pose = profile.firstPerson; break;
            case THIRD_PERSON_LEFT_HAND: case THIRD_PERSON_RIGHT_HAND: pose = profile.thirdPerson; break;
            case GROUND: pose = profile.ground; break;
            default: pose = profile.fixed;
        }
        float tx = pose.translation[0], ry = pose.rotation[1];
        if (type == TransformType.FIRST_PERSON_LEFT_HAND) { tx = -tx; ry = 180 - ry; }
        Matrix4f result = identity(), step = new Matrix4f();
        result.setTranslation(new Vector3f(tx, pose.translation[1], pose.translation[2]));
        step.rotZ(radians(pose.rotation[2])); result.mul(step);
        step.rotY(radians(ry)); result.mul(step);
        step.rotX(radians(pose.rotation[0])); result.mul(step);
        step.setIdentity(); step.setScale(pose.scale); result.mul(step);
        step.setIdentity();
        step.setTranslation(new Vector3f(-profile.grip[0], -profile.grip[1], -profile.grip[2]));
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
