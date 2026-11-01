/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aqp
 *  bje
 *  mods.provoda.GLLoader.GLModel
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonDouble1
 *  org.lwjgl.opengl.GL11
 */
package mods.provoda.renderers.tunnels;

import mods.provoda.GLLoader.GLModel;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonDouble1;
import org.lwjgl.opengl.GL11;

public class RenderTunnelBetonDouble1
extends bje {
    private static GLModel ModelBar = new GLModel("/mods/provoda/obj/tunnels/tunnel_beton_double.obj");

    public void a(aqp tileEntity, double d1, double d2, double d3, float f) {
        this.renderTileEntity((TileEntityTunnelBetonDouble1)tileEntity, d1, d2, d3, f);
    }

    public void renderTileEntity(TileEntityTunnelBetonDouble1 tileEntity, double d1, double d2, double d3, float f) {
        int rotation = 0;
        int md1 = 0;
        if (tileEntity != null && tileEntity.k != null) {
            int md = tileEntity.k.h(tileEntity.l, tileEntity.m, tileEntity.n);
            if (md == 0) {
                rotation = -90;
                md1 = 3;
            }
            if (md == 1) {
                rotation = 180;
                md1 = 1;
            }
            if (md == 2) {
                rotation = 90;
                md1 = 2;
            }
        }
        GL11.glPushMatrix();
        GL11.glShadeModel((int)7425);
        GL11.glTranslatef((float)((float)d1 + 0.5f), (float)((float)d2 + 1.5f), (float)((float)d3 + 0.5f));
        if (md1 == 0) {
            GL11.glTranslatef((float)0.38f, (float)-0.5f, (float)0.01f);
            GL11.glTranslatef((float)-0.05f, (float)-0.0f, (float)17.15f);
            GL11.glScalef((float)0.993f, (float)1.0f, (float)0.991f);
            GL11.glTranslatef((float)0.05f, (float)-0.005f, (float)-0.74f);
        } else if (md1 == 2) {
            GL11.glTranslatef((float)0.01f, (float)-0.5f, (float)-0.38f);
            GL11.glTranslatef((float)17.15f, (float)-0.0f, (float)0.05f);
            GL11.glScalef((float)0.991f, (float)1.0f, (float)0.993f);
            GL11.glTranslatef((float)-0.74f, (float)-0.005f, (float)-0.05f);
        } else if (md1 == 1) {
            GL11.glTranslatef((float)-0.38f, (float)-0.5f, (float)-0.01f);
            GL11.glTranslatef((float)0.05f, (float)-0.0f, (float)-17.15f);
            GL11.glScalef((float)0.993f, (float)1.0f, (float)0.991f);
            GL11.glTranslatef((float)-0.05f, (float)-0.005f, (float)0.74f);
        } else {
            GL11.glTranslatef((float)-0.01f, (float)-0.5f, (float)0.38f);
            GL11.glTranslatef((float)-17.15f, (float)-0.0f, (float)-0.05f);
            GL11.glScalef((float)0.991f, (float)1.0f, (float)0.993f);
            GL11.glTranslatef((float)0.74f, (float)-0.005f, (float)0.05f);
        }
        GL11.glRotatef((float)((float)rotation + 180.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        ModelBar.render();
        GL11.glPopMatrix();
    }
}
