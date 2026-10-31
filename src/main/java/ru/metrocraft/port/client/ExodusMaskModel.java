package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import java.util.HashMap;
import java.util.Map;

/** Same head placement as vanilla LayerCustomHead, then the source model's own HEAD transform. */
public final class ExodusMaskModel extends LegacyNpcArmorPose {
    private static final Map<String, ExodusMaskModel> CACHE = new HashMap<>();
    private ExodusMaskModel(String id) { super(0); }
    public static ExodusMaskModel get(String id) { return CACHE.computeIfAbsent(id, ExodusMaskModel::new); }
    @Override public void render(Entity entity, float swing, float amount, float age, float yaw, float pitch, float scale) {
        if (!(entity instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase) entity;
        ItemStack stack = living.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (stack.isEmpty()) return;
        setRotationAngles(swing, amount, age, yaw, pitch, scale, entity);
        npcPose(entity, swing, amount, age, yaw, pitch, scale);
        GlStateManager.pushMatrix();
        if (isSneak) GlStateManager.translate(0, .2F, 0);
        if (isChild) {
            GlStateManager.translate(0, .5F * scale, 0);
            GlStateManager.scale(.7F, .7F, .7F);
            GlStateManager.translate(0, 16 * scale, 0);
        }
        bipedHead.postRender(scale);
        GlStateManager.translate(0, -.25F, 0);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.scale(.625F, -.625F, -.625F);
        Minecraft.getMinecraft().getItemRenderer().renderItem(living, stack, ItemCameraTransforms.TransformType.HEAD);
        GlStateManager.popMatrix();
    }
}
