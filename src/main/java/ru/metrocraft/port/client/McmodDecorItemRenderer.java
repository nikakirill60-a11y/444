package ru.metrocraft.port.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.*;
import ru.metrocraft.port.mcmod.decor.*;

public final class McmodDecorItemRenderer extends TileEntityItemStackRenderer {
    public static final McmodDecorItemRenderer INSTANCE=new McmodDecorItemRenderer();
    @Override public void renderByItem(ItemStack stack,float partial) {
        if(!(stack.getItem() instanceof ItemBlock) || !(((ItemBlock)stack.getItem()).getBlock() instanceof McmodDecorBlock))return;
        McmodDecorContent.Entry e=((McmodDecorBlock)((ItemBlock)stack.getItem()).getBlock()).entry;
        GlStateManager.pushMatrix();GlStateManager.disableCull();GlStateManager.enableRescaleNormal();
        try {
            GlStateManager.translate(.5,.5,.5);float fit=DecorPose.itemScale(e);GlStateManager.scale(fit,fit,fit);
            GlStateManager.translate(-(e.bounds[0]+e.bounds[3])/2,-(e.bounds[1]+e.bounds[4])/2,-(e.bounds[2]+e.bounds[5])/2);
            GlStateManager.color(1,1,1,1);McmodDecorRenderer.mesh(e);
        } finally {GlStateManager.color(1,1,1,1);GlStateManager.enableCull();GlStateManager.disableRescaleNormal();GlStateManager.popMatrix();}
    }
}
