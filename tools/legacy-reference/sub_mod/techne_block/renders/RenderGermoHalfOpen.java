/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.model.AdvancedModelLoader
 *  net.minecraftforge.client.model.IModelCustom
 *  org.lwjgl.opengl.GL11
 */
package sub_mod.techne_block.renders;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import sub_mod.techne_block.tiles.TileGermoHalfOpen;

public class RenderGermoHalfOpen
extends TileEntitySpecialRenderer {
    public static final ResourceLocation texture = new ResourceLocation("sub_mod_techne_block", "textures/blocks/Germa.png");
    public static final ResourceLocation germa = new ResourceLocation("sub_mod_techne_block", "models/Germa.obj");
    public static final ResourceLocation germaramka = new ResourceLocation("sub_mod_techne_block", "models/GermaRamka.obj");
    private IModelCustom model = AdvancedModelLoader.loadModel((ResourceLocation)germa);
    private IModelCustom model1 = AdvancedModelLoader.loadModel((ResourceLocation)germaramka);

    public void func_147500_a(TileEntity tile, double x, double y, double z, float f) {
        this.render((TileGermoHalfOpen)tile, x, y, z, f);
    }

    private void render(TileGermoHalfOpen tile, double x, double y, double z, float f) {
        int i = tile.func_145831_w().func_72805_g(tile.field_145851_c, tile.field_145848_d, tile.field_145849_e);
        GL11.glPushMatrix();
        GL11.glTranslated((double)x, (double)y, (double)z);
        if (i == 0) {
            GL11.glTranslatef((float)0.3f, (float)0.0f, (float)0.1f);
        } else if (i == 1) {
            GL11.glTranslatef((float)0.9f, (float)0.0f, (float)0.3f);
        } else if (i == 2) {
            GL11.glTranslatef((float)0.7f, (float)0.0f, (float)0.9f);
        } else if (i == 3) {
            GL11.glTranslatef((float)0.1f, (float)0.0f, (float)0.7f);
        }
        GL11.glRotatef((float)(90.0f * (float)(-i)), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        this.func_147499_a(texture);
        GL11.glScalef((float)0.53f, (float)0.53f, (float)0.55f);
        this.model1.renderAll();
        if (tile.rotation == 90) {
            GL11.glRotatef((float)tile.animLocX, (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (tile.rotation == 0) {
            GL11.glRotatef((float)tile.animLocX1, (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (tile.rotation == 180) {
            GL11.glRotatef((float)tile.animLocX2, (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (tile.rotation == 270) {
            GL11.glRotatef((float)tile.animLocX3, (float)0.0f, (float)1.0f, (float)0.0f);
        }
        this.model.renderAll();
        GL11.glPopMatrix();
    }
}

