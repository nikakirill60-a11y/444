/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aqp
 *  bje
 *  mods.provoda.GLLoader.GLModel
 *  mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonProvoda
 *  org.lwjgl.opengl.GL11
 */
package mods.provoda.renderers.tunnels;

import mods.provoda.GLLoader.GLModel;
import mods.provoda.tileentitys.tunnels.TileEntityTunnelBetonProvoda;
import org.lwjgl.opengl.GL11;

public class RenderTunnelBetonProvoda
extends bje {
    private static GLModel ModelBar = new GLModel("/mods/provoda/obj/tunnels/tunnel_beton_provoda.obj");

    public void a(aqp tileEntity, double d1, double d2, double d3, float f) {
        this.renderTileEntity((TileEntityTunnelBetonProvoda)tileEntity, d1, d2, d3, f);
    }

    public void renderTileEntity(TileEntityTunnelBetonProvoda tileEntity, double d1, double d2, double d3, float f) {
        float trans = 0.365f;
        int rotation = 180;
        if (tileEntity != null && tileEntity.k != null) {
            int md = tileEntity.k.h(tileEntity.l, tileEntity.m, tileEntity.n);
            if (md == 0) {
                rotation = 90;
                trans = -0.38f;
            }
            if (md == 1) {
                rotation = 0;
            }
            if (md == 2) {
                rotation = 270;
            }
        }
        GL11.glPushMatrix();
        GL11.glShadeModel((int)7425);
        GL11.glTranslatef((float)((float)d1 + 0.5f), (float)((float)d2 + 1.5f), (float)((float)d3 + 0.5f));
        if (rotation == 90) {
            GL11.glTranslatef((float)-2.0f, (float)-0.5f, (float)-0.38f);
        } else if (rotation == 270) {
            GL11.glTranslatef((float)2.0f, (float)-0.5f, (float)0.365f);
        } else if (rotation == 180) {
            GL11.glTranslatef((float)-0.365f, (float)-0.5f, (float)2.0f);
        } else {
            GL11.glTranslatef((float)0.365f, (float)-0.5f, (float)-2.0f);
        }
        GL11.glRotatef((float)((float)rotation + 180.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glScalef((float)1.0f, (float)1.0f, (float)0.55f);
        ModelBar.render();
        GL11.glPopMatrix();
    }
}
