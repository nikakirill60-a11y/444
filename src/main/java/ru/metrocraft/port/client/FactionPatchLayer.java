package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.patches.*;

/** Left upper-arm cloth badge, following the vanilla limb (walking, sneaking, swimming, handedness). */
public final class FactionPatchLayer implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer renderer;
    private static final java.util.Map<String,Double> SURFACES=new java.util.HashMap<>();
    private static double surface(ItemStack armor) {
        String id=armor.getItem().getRegistryName().toString();
        return SURFACES.computeIfAbsent(id,key->{
            if(armor.getItem() instanceof ru.metrocraft.port.mcmod.McmodArmorItem)
                return McmodArmorModel.patchSurface(((ru.metrocraft.port.mcmod.McmodArmorItem)armor.getItem()).entry);
            double x=.257; // Vanilla chest armor is inflated by one pixel, even for Alex skins.
            if(armor.getItem() instanceof ru.metrocraft.port.MetroArmor) {
                String model=armor.getItem().getRegistryName().getPath();
                float factor=LegacyItemRenderer.info(model).scale/16F;
                java.util.List<ru.metrocraft.port.client.tmt.ModelRendererTurbo> parts=LegacyGeometry.get(model).groups.get("leftArmModel");
                if(parts!=null)for(ru.metrocraft.port.client.tmt.ModelRendererTurbo part:parts)
                    x=Math.max(x,part.outerX(-.05/factor,.24/factor,.15/factor)*factor+.008);
            }
            return x;
        });
    }
    public FactionPatchLayer(RenderPlayer renderer) { this.renderer=renderer; }
    @Override public void doRenderLayer(AbstractClientPlayer player,float swing,float amount,float partial,float age,float yaw,float pitch,float scale) {
        ItemStack armor=player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        ItemStack patch=FactionPatches.get(armor);
        if(patch.isEmpty() || player.isInvisible() || player.isSpectator())return;
        String faction=((FactionPatchItem)patch.getItem()).faction;
        GlStateManager.pushMatrix();
        try {
            if(player.isSneaking())GlStateManager.translate(0,.2,0);
            renderer.getMainModel().bipedLeftArm.postRender(1/16F);
            // Slightly outside the chest armor's upper-arm surface, including a cloth backing.
            double x=surface(armor);
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("metroport:textures/patches/"+faction+".png"));
            GlStateManager.color(1,1,1,1); GlStateManager.disableCull();
            GlStateManager.enableBlend(); GlStateManager.tryBlendFuncSeparate(770,771,1,0);
            BufferBuilder b=Tessellator.getInstance().getBuffer();
            b.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_TEX_NORMAL);
            b.pos(x,0,-.105).tex(0,0).normal(1,0,0).endVertex();
            b.pos(x,.21,-.105).tex(0,1).normal(1,0,0).endVertex();
            b.pos(x,.21,.105).tex(1,1).normal(1,0,0).endVertex();
            b.pos(x,0,.105).tex(1,0).normal(1,0,0).endVertex();
            Tessellator.getInstance().draw();
        } finally {
            GlStateManager.disableBlend(); GlStateManager.enableCull(); GlStateManager.color(1,1,1,1); GlStateManager.popMatrix();
        }
    }
    @Override public boolean shouldCombineTextures() { return false; }
}
