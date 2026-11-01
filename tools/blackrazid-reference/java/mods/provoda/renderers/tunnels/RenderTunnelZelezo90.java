/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aqp
 *  bje
 *  mods.provoda.GLLoader.GLModel
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezo90
 *  org.lwjgl.opengl.GL11
 */
package mods.provoda.renderers.tunnels;

import mods.provoda.GLLoader.GLModel;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelZelezo90;
import org.lwjgl.opengl.GL11;

public class RenderTunnelZelezo90
extends bje {
    private static GLModel ModelBar = new GLModel("/mods/provoda/obj/tunnels/tunnel_metal90.obj");

    public void a(aqp tileEntity, double d1, double d2, double d3, float f) {
        this.renderTileEntity((TileEntityTunnelZelezo90)tileEntity, d1, d2, d3, f);
    }

    public void renderTileEntity(TileEntityTunnelZelezo90 tileEntity, double d1, double d2, double d3, float f) {
        int rotation = 90;
        int md1 = 0;
        if (tileEntity != null && tileEntity.k != null) {
            int md = tileEntity.k.h(tileEntity.l, tileEntity.m, tileEntity.n);
            if (md == 0) {
                rotation = 0;
                md1 = 3;
            }
            if (md == 1) {
                rotation = -90;
                md1 = 1;
            }
            if (md == 2) {
                rotation = 180;
                md1 = 2;
            }
        }
        GL11.glPushMatrix();
        GL11.glShadeModel((int)7425);
        GL11.glTranslatef((float)((float)d1 + 0.5f), (float)((float)d2 + 1.5f), (float)((float)d3 + 0.5f));
        if (md1 == 0) {
            GL11.glTranslatef((float)-0.01f, (float)-0.5f, (float)0.38f);
            GL11.glTranslatef((float)17.15f, (float)-0.0f, (float)-0.01f);
            GL11.glScalef((float)0.988f, (float)1.0f, (float)0.991f);
            GL11.glTranslatef((float)-5.8f, (float)-0.005f, (float)-4.8f);
        } else if (md1 == 2) {
            GL11.glTranslatef((float)-0.01f, (float)-0.5f, (float)0.38f);
            GL11.glTranslatef((float)-0.05f, (float)-0.0f, (float)-17.15f);
            GL11.glScalef((float)0.991f, (float)1.0f, (float)0.988f);
            GL11.glTranslatef((float)-4.35f, (float)-0.005f, (float)5.42f);
        } else if (md1 == 1) {
            GL11.glTranslatef((float)0.01f, (float)-0.5f, (float)-0.38f);
            GL11.glTranslatef((float)-17.15f, (float)-0.0f, (float)0.0f);
            GL11.glScalef((float)0.988f, (float)1.0f, (float)0.991f);
            GL11.glTranslatef((float)5.8f, (float)-0.005f, (float)4.8f);
        } else {
            GL11.glTranslatef((float)0.03f, (float)-0.5f, (float)-0.44f);
            GL11.glTranslatef((float)0.05f, (float)-0.0f, (float)17.15f);
            GL11.glScalef((float)0.991f, (float)1.0f, (float)0.988f);
            GL11.glTranslatef((float)4.33f, (float)-0.005f, (float)-5.38f);
        }
        GL11.glRotatef((float)((float)rotation + 180.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        ModelBar.render();
        GL11.glPopMatrix();
    }
}
