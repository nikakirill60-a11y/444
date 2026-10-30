/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 */
package sub_mod.techne_block;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import sub_mod.techne_block.RenderTileEntityTechne;

public class RenderItemBlockTechne
implements IItemRenderer {
    public boolean handleRenderType(ItemStack is, IItemRenderer.ItemRenderType type) {
        return true;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack is, IItemRenderer.ItemRendererHelper helper) {
        return true;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack is, Object ... data) {
        GL11.glPushMatrix();
        GL11.glScalef((float)0.2f, (float)0.2f, (float)0.2f);
        GL11.glTranslatef((float)3.9f, (float)1.5f, (float)2.9f);
        GL11.glRotatef((float)190.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)45.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        Minecraft.func_71410_x().field_71446_o.func_110577_a(RenderTileEntityTechne.texture);
        RenderTileEntityTechne.model.render();
        RenderTileEntityTechne.model1.render();
        GL11.glPopMatrix();
    }
}

