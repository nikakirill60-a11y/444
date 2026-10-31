/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.model.AdvancedModelLoader
 *  org.lwjgl.opengl.GL11
 */
package metro.client.blocks.render;

import metro.common.ClientProxy;
import metro.common.blocks.HudrantBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import org.lwjgl.opengl.GL11;

public class RenderTileEntityHudrant
extends TileEntitySpecialRenderer {
    int list;

    public RenderTileEntityHudrant(HudrantBlock block) {
        ClientProxy.blockModels.put(block.model_name, AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/street/" + block.model_name + "/" + block.model_name + ".obj")));
        this.list = GL11.glGenLists((int)1);
        GL11.glNewList((int)this.list, (int)4864);
        ClientProxy.blockModelslist.put(block.model_name, this.list);
        ClientProxy.blockModels.get(block.model_name).renderAll();
        GL11.glEndList();
        System.out.print("List Ceated!");
    }

    public void func_147500_a(TileEntity tile, double x, double y, double z, float f) {
        if (tile.func_145831_w().func_147439_a(tile.field_145851_c, tile.field_145848_d, tile.field_145849_e) != Blocks.field_150350_a && tile.func_145831_w().func_147439_a(tile.field_145851_c, tile.field_145848_d, tile.field_145849_e) instanceof HudrantBlock) {
            HudrantBlock block = (HudrantBlock)tile.func_145831_w().func_147439_a(tile.field_145851_c, tile.field_145848_d, tile.field_145849_e);
            int metadata = tile.func_145831_w().func_72805_g(tile.field_145851_c, tile.field_145848_d, tile.field_145849_e);
            int rotationAngle = 0;
            if (metadata == 2) {
                rotationAngle = 90;
            }
            if (metadata == 1) {
                rotationAngle = 180;
            }
            if (metadata == 0) {
                rotationAngle = 270;
            }
            float scale = block.scale;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)((float)x + 0.5f), (float)((float)y), (float)((float)z + 0.5f));
            GL11.glRotatef((float)rotationAngle, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            Minecraft.func_71410_x().func_110434_K().func_110577_a(new ResourceLocation("metro", "models/street/" + block.model_name + "/" + block.model_name + ".png"));
            GL11.glCallList((int)ClientProxy.blockModelslist.get(block.model_name));
            GL11.glPopMatrix();
        }
    }
}
