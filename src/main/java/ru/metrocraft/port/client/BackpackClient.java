package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.inventory.Backpacks;

public final class BackpackClient implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer renderer;
    public BackpackClient(RenderPlayer renderer){this.renderer=renderer;}
    public static void receive(int entityId,ItemStack stack) {
        Minecraft.getMinecraft().addScheduledTask(()->{
            if(Minecraft.getMinecraft().world==null)return;
            Entity entity=Minecraft.getMinecraft().world.getEntityByID(entityId);
            if(entity instanceof EntityPlayer)Backpacks.set((EntityPlayer)entity,stack);
        });
    }
    @Override
    public void doRenderLayer(AbstractClientPlayer player,float swing,float amount,float ticks,float age,float yaw,float pitch,float scale) {
        ItemStack stack=Backpacks.get(player);
        if(stack.isEmpty() || player.isInvisible())return;
        String id=stack.getItem().getRegistryName().getPath();
        if(Backpacks.rows(stack)==0)return;
        GlStateManager.pushMatrix();
        renderer.getMainModel().bipedBody.postRender(0.0625F);
        GlStateManager.rotate(180,1,0,0);
        GlStateManager.scale(0.9,0.9,0.9);
        GlStateManager.translate(0,0,0.025);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("metroport:textures/backpacks/"+id+".png"));
        LegacyObjModel.get("backpacks/"+id).render();
        GlStateManager.popMatrix();
    }
    @Override public boolean shouldCombineTextures(){return false;}
}
