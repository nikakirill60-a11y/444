/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  apa
 *  aqp
 *  bje
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  mods.provoda.GLLoader.GLModel
 *  mods.provoda.ModBase
 *  mods.provoda.tileentitys.tunnels.TileEntityWideRail
 *  org.lwjgl.opengl.GL11
 */
package mods.provoda.renderers.tunnels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.GLLoader.GLModel;
import mods.provoda.ModBase;
import mods.provoda.tileentitys.tunnels.TileEntityWideRail;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class TileEntityWideRailRenderer
extends bje {
    private static GLModel rails90 = new GLModel("/mods/provoda/obj/tunnels/rails90.obj");
    private static GLModel rails = new GLModel("/mods/provoda/obj/tunnels/rails.obj");
    private static GLModel railsX = new GLModel("/mods/provoda/obj/tunnels/railsX.obj");

    public void a(aqp tileentity, double d, double d1, double d2, float f) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)d), (float)((float)d1), (float)((float)d2));
        TileEntityWideRail tile = (TileEntityWideRail)tileentity;
        if (tile != null) {
            this.renderBlockRail(tile, tile.k, tile.l, tile.m, tile.n, ModBase.rails);
        }
        GL11.glPopMatrix();
    }

    public void renderBlockRail(TileEntityWideRail tile, aab world, int i, int j, int k, apa block) {
        int dir = world.h(i, j, k);
        if (tile != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)0.5f, (float)0.0f, (float)0.5f);
            GL11.glRotatef((float)((float)dir * -90.0f), (float)0.0f, (float)1.0f, (float)0.0f);
            if (tile.mId == 0) {
                GL11.glTranslatef((float)-0.422f, (float)-0.0f, (float)-0.01f);
                GL11.glScalef((float)1.0f, (float)1.0f, (float)0.55f);
                rails.render();
            } else if (tile.mId == 1) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)-0.01f, (float)0.0f, (float)0.0f);
                GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                railsX.render();
                GL11.glPopMatrix();
            } else if (tile.mId == 2) {
                GL11.glPushMatrix();
                GL11.glRotatef((float)180.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glPopMatrix();
            } else if (tile.mId != 3 && tile.mId != 4) {
                if (tile.mId != 5 && tile.mId != 6) {
                    if (tile.mId >= 7 && tile.mId <= 12) {
                        int y1;
                        float y = tile.mId != 9 && tile.mId != 7 ? -1.0f : 1.0f;
                        GL11.glPushMatrix();
                        for (y1 = 0; y1 < (tile.mId != 11 && tile.mId != 12 ? 1 : 2); y1 = (int)((byte)(y1 + 1))) {
                            for (int var17 = -1; var17 < 2; var17 = (int)((byte)(var17 + 2))) {
                                GL11.glPushMatrix();
                                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-0.15f);
                                for (int e = 0; e < 13; e = (int)((byte)(e + 1))) {
                                    GL11.glTranslatef((float)(y * 12.95f), (float)0.0f, (float)0.0f);
                                    GL11.glRotatef((float)(y * 90.0f / 31.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                                    GL11.glTranslatef((float)(y * -12.95f), (float)0.0f, (float)0.0f);
                                }
                                GL11.glPopMatrix();
                                GL11.glTranslatef((float)(y * 5.0f), (float)0.0f, (float)15.0f);
                                GL11.glRotatef((float)180.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                            }
                            y *= -1.0f;
                        }
                        GL11.glPopMatrix();
                        if (tile.mId == 9 || tile.mId == 10 || tile.mId == 12) {
                            for (y1 = 0; y1 < 9; y1 = (int)((byte)(y1 + 1))) {
                                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)1.0f);
                            }
                        }
                    } else if (tile.mId == 13) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-0.5f);
                        GL11.glRotatef((float)1.145762f, (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.5f);
                        GL11.glScalef((float)1.0f, (float)1.0f, (float)1.001f);
                        for (int var17 = 0; var17 < 50; var17 = (int)((byte)(var17 + 1))) {
                            GL11.glTranslatef((float)0.0f, (float)0.0f, (float)1.0f);
                        }
                        GL11.glPopMatrix();
                    }
                } else {
                    int y1;
                    GL11.glPushMatrix();
                    float y = tile.mId == 5 ? 1.0f : -1.0f;
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-0.5f);
                    GL11.glTranslatef((float)(y * 15.5f), (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)(y * 1.25f), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glTranslatef((float)(y * -15.5f), (float)0.0f, (float)0.0f);
                    for (y1 = 0; y1 <= 35; y1 = (int)((byte)(y1 + 1))) {
                        GL11.glTranslatef((float)(y * 15.5f), (float)0.0f, (float)0.0f);
                        GL11.glRotatef((float)(y * 2.5f), (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glTranslatef((float)(y * -15.5f), (float)0.0f, (float)0.0f);
                    }
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    for (y1 = 0; y1 <= 10; y1 = (int)((byte)(y1 + 1))) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)1.0f);
                    }
                    GL11.glPopMatrix();
                }
            } else {
                GL11.glPushMatrix();
                float y = tile.mId == 3 ? 1.0f : -1.0f;
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-0.5f);
                GL11.glTranslatef((float)(y * 15.5f), (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)(y * 90.0f / 35.0f / 2.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glTranslatef((float)(y * -15.5f), (float)0.0f, (float)0.0f);
                if (tile.mId == 4) {
                    GL11.glTranslatef((float)-0.6f, (float)-0.001f, (float)-1.25f);
                    GL11.glScalef((float)0.98f, (float)1.0f, (float)1.015f);
                } else {
                    GL11.glTranslatef((float)-0.6f, (float)-0.001f, (float)-1.38f);
                    GL11.glScalef((float)1.015f, (float)1.0f, (float)0.98f);
                }
                rails90.render();
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
        }
    }
}
