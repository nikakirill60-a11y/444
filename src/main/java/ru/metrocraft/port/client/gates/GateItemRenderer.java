package ru.metrocraft.port.client.gates;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.item.ItemStack;
import ru.metrocraft.port.gates.*;

/** Show the recovered gate, not the iron-block transform donor, in the creative tab and hand. */
public final class GateItemRenderer extends TileEntityItemStackRenderer {
    public static final GateItemRenderer INSTANCE = new GateItemRenderer();
    @Override public void renderByItem(ItemStack stack, float partial) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof GateBlock)) return;
        GateTile tile = new GateTile(); tile.type = ((GateBlock) block).type;
        TileEntitySpecialRenderer<GateTile> renderer = TileEntityRendererDispatcher.instance.getRenderer(tile);
        if (renderer == null) return;
        GlStateManager.pushMatrix();
        try {
            // RenderItem offsets TEISR by -0.5; cancel it before centring/scaling the native mesh.
            GlStateManager.translate(.5, .5, .5); GlStateManager.scale(.24, .24, .24);
            GlStateManager.translate(0, -2, 0);
            renderer.render(tile, -.5, 0, -.5, 0, -1, 1);
        } finally { GlStateManager.popMatrix(); }
    }
}
