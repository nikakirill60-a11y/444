/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ModLoader
 *  aqp
 *  bje
 *  net.minecraft.client.Minecraft
 *  org.lwjgl.opengl.GL11
 */
package mods.provoda.renderers;

import mods.provoda.models.ModelTunnel;
import mods.provoda.tileentitys.TileEntityTunnel;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class RendererTunnel
extends bje {
    Minecraft mc = Minecraft.x();
    private static ModelTunnel model;

    public RendererTunnel() {
        model = new ModelTunnel();
    }

    public void a(aqp var1, double var2, double var4, double var6, float var8) {
        this.renderTileEntity((TileEntityTunnel)var1, var2, var4, var6, var8);
    }

    public void renderTileEntity(TileEntityTunnel var1, double var2, double var4, double var6, float var8) {
        int var19;
        int var9 = 0;
        if (var1 != null && var1.k != null) {
            var19 = var1.k.h(var1.l, var1.m, var1.n);
            if (var19 == 0) {
                var9 = 90;
            }
            if (var19 == 1) {
                var9 = 180;
            }
            if (var19 == 2) {
                var9 = 270;
            }
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)var2 + 0.5f), (float)((float)var4 + 1.5f), (float)((float)var6 + 0.5f));
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)var9, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glScalef((float)1.4f, (float)1.4f, (float)1.0f);
        GL11.glTranslatef((float)0.3f, (float)3.9f, (float)0.0f);
        var19 = ModLoader.getMinecraftInstance().p.f("/mods/provoda/textures/tunnel.png");
        GL11.glBindTexture((int)3553, (int)var19);
        model.a(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }
}
