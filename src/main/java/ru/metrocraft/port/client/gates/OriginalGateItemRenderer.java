package ru.metrocraft.port.client.gates;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import ru.metrocraft.port.metromod.decor.*;

public final class OriginalGateItemRenderer extends TileEntityItemStackRenderer {
    public static final OriginalGateItemRenderer INSTANCE=new OriginalGateItemRenderer();
    @Override public void renderByItem(ItemStack stack,float partial) {
        Block block=Block.getBlockFromItem(stack.getItem());if(!(block instanceof OriginalGateBlock))return;
        OriginalGateProfile profile=((OriginalGateBlock)block).profile;
        AxisAlignedBB bounds=null;
        for(AxisAlignedBB box:profile.boxes(BlockPos.ORIGIN,EnumFacing.SOUTH,0,false))bounds=bounds==null?box:bounds.union(box);
        double scale=.85/Math.max(bounds.maxX-bounds.minX,Math.max(bounds.maxY-bounds.minY,bounds.maxZ-bounds.minZ));
        GlStateManager.pushMatrix();GlStateManager.disableCull();GlStateManager.enableRescaleNormal();
        try {
            GlStateManager.translate(.5,.5,.5);GlStateManager.scale(scale,scale,scale);
            GlStateManager.translate(.5-(bounds.minX+bounds.maxX)/2,-(bounds.minY+bounds.maxY)/2,.5-(bounds.minZ+bounds.maxZ)/2);
            GlStateManager.color(1,1,1,1);OriginalGateRenderer.draw(profile,0);
        } finally {GlStateManager.color(1,1,1,1);GlStateManager.disableRescaleNormal();GlStateManager.enableCull();GlStateManager.popMatrix();}
    }
}
