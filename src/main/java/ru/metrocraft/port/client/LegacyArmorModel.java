package ru.metrocraft.port.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import ru.metrocraft.port.client.tmt.ModelRendererTurbo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LegacyArmorModel extends ModelBiped {
    private static final Map<String, LegacyArmorModel> CACHE = new HashMap<>();
    private final String id;
    private LegacyArmorModel(String id) { super(0); this.id = id; }
    public static LegacyArmorModel get(String id) { return CACHE.computeIfAbsent(id, LegacyArmorModel::new); }
    @Override
    public void render(Entity entity, float swing, float amount, float age, float yaw, float pitch, float scale) {
        setRotationAngles(swing, amount, age, yaw, pitch, scale, entity);
        float factor = LegacyItemRenderer.info(id).scale;
        LegacyGeometry geometry = LegacyGeometry.get(id);
        GlStateManager.pushMatrix();
        if (isChild) { GlStateManager.scale(0.5,0.5,0.5); GlStateManager.translate(0,24 * scale,0); }
        if (isSneak) GlStateManager.translate(0, 0.2, 0);
        GlStateManager.scale(factor, factor, factor);
        limb(geometry,"headModel",bipedHead,scale,factor);
        limb(geometry,"bodyModel",bipedBody,scale,factor);
        limb(geometry,"leftArmModel",bipedLeftArm,scale,factor);
        limb(geometry,"rightArmModel",bipedRightArm,scale,factor);
        limb(geometry,"leftLegModel",bipedLeftLeg,scale,factor);
        limb(geometry,"rightLegModel",bipedRightLeg,scale,factor);
        limb(geometry,"skirtFrontModel",bipedLeftLeg,scale,factor);
        limb(geometry,"skirtRearModel",bipedRightLeg,scale,factor);
        GlStateManager.popMatrix();
    }
    private void limb(LegacyGeometry geometry, String group, ModelRenderer body, float scale, float factor) {
        List<ModelRendererTurbo> parts = geometry.groups.get(group);
        if (parts == null) return;
        for (ModelRendererTurbo p : parts) {
            p.rotationPointX = body.rotationPointX / factor;
            p.rotationPointY = body.rotationPointY / factor;
            p.rotationPointZ = body.rotationPointZ / factor;
            p.rotateAngleX = body.rotateAngleX;
            p.rotateAngleY = body.rotateAngleY;
            p.rotateAngleZ = body.rotateAngleZ;
            p.render(scale);
        }
    }
}
