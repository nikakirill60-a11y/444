package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.inventory.NvgEquipment;

/** Original PNV head mesh now layers over a separate helmet or a suit's built-in mask. */
public final class NvgLayer implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer renderer;
    public NvgLayer(RenderPlayer renderer) {this.renderer=renderer;}
    public static void receive(int entityId,ItemStack stack) {
        Minecraft.getMinecraft().addScheduledTask(()->{
            if(Minecraft.getMinecraft().world==null)return;
            Entity entity=Minecraft.getMinecraft().world.getEntityByID(entityId);
            if(entity instanceof EntityPlayer)NvgEquipment.receive((EntityPlayer)entity,stack);
        });
    }
    @Override public void doRenderLayer(AbstractClientPlayer player,float swing,float amount,float partial,float age,float yaw,float pitch,float scale) {
        ItemStack stack=NvgEquipment.get(player);
        if(stack.isEmpty() || player.isInvisible() || player.isSpectator())return;
        String id=stack.getItem().getRegistryName().getPath();
        LegacyItemRenderer.Info info=LegacyItemRenderer.info(id);
        LegacyGeometry mesh=LegacyGeometry.get(id);
        java.util.List<ru.metrocraft.port.client.tmt.ModelRendererTurbo> parts=mesh.groups.get("headModel");
        if(parts==null)return;
        GlStateManager.pushMatrix();
        try {
            if(player.isSneaking())GlStateManager.translate(0,.2,0);
            renderer.getMainModel().bipedHead.postRender(1/16F);
            GlStateManager.scale(info.scale,info.scale,info.scale);
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(info.texture));
            GlStateManager.color(1,1,1,1);
            for(ru.metrocraft.port.client.tmt.ModelRendererTurbo part:parts) {
                part.setRotationPoint(0,0,0);part.rotateAngleX=part.rotateAngleY=part.rotateAngleZ=0;part.render(1/16F);
            }
        } finally {GlStateManager.popMatrix();}
    }
    @Override public boolean shouldCombineTextures() {return false;}
}
