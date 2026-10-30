/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package sub_mod.techne_block.renders;

import blocks.models.ModelGermoLeft;
import blocks.models.ModelGermoRamka;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import sub_mod.techne_block.tiles.TileGermoLeft;

public class RenderGermoLeft
extends TileEntitySpecialRenderer {
    public static final ModelGermoRamka model1 = new ModelGermoRamka();
    public static final ModelGermoLeft model4 = new ModelGermoLeft();
    public static final ResourceLocation texture1 = new ResourceLocation("sub_mod_techne_block", "textures/blocks/ModelGermoLeft.png");

    public void func_147500_a(TileEntity tile, double x, double y, double z, float f) {
        this.render((TileGermoLeft)tile, x, y, z, f);
    }

    private void render(TileGermoLeft tile, double x, double y, double z, float f) {
        int i = tile.func_145831_w().func_72805_g(tile.field_145851_c, tile.field_145848_d, tile.field_145849_e);
        float runSwitch = tile.lastrunProgress + (tile.runProgress - tile.lastrunProgress);
        float runSwitch2 = tile.lastrunProgress + (tile.runProgress - tile.lastrunProgress) * 2.0f;
        GL11.glPushMatrix();
        GL11.glTranslated((double)x, (double)y, (double)z);
        GL11.glTranslatef((float)0.5f, (float)1.5f, (float)0.5f);
        if (i == 0) {
            GL11.glTranslatef((float)0.0f, (float)-1.0f, (float)2.0f);
        } else if (i == 1) {
            GL11.glTranslatef((float)-2.0f, (float)-1.0f, (float)0.0f);
        } else if (i == 2) {
            GL11.glTranslatef((float)0.0f, (float)-1.0f, (float)-2.0f);
        } else if (i == 3) {
            GL11.glTranslatef((float)2.0f, (float)-1.0f, (float)0.0f);
        }
        GL11.glRotatef((float)(90.0f * (float)(-i)), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        this.func_147499_a(texture1);
        model4.render();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glPopMatrix();
    }
}

