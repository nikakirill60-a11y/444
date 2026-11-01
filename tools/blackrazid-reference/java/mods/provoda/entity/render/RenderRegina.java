/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aqx
 *  bgz
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  mods.provoda.GLLoader.GLModel
 *  mods.provoda.entity.Entityregina
 *  mp
 *  org.lwjgl.opengl.GL11
 */
package mods.provoda.entity.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.GLLoader.GLModel;
import mods.provoda.entity.Entityregina;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderRegina
extends bgz {
    GLModel model;
    GLModel part;

    public RenderRegina() {
        this.d = 0.0f;
        this.model = new GLModel("/mods/provoda/entity/drezina/regina/models/regina.obj");
        this.part = new GLModel("/mods/provoda/entity/drezina/regina/models/part.obj");
    }

    public void renderCherepanov(Entityregina par1, double par2, double par4, double par6, float par8, float par9) {
        GL11.glPushMatrix();
        GL11.glShadeModel((int)7425);
        GL11.glDisable((int)2884);
        float wl = par1.wheelAngle[0];
        float wd = par1.wheelAngle[1];
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glTranslated((double)par2, (double)par4, (double)par6);
        GL11.glRotatef((float)(0.0f - par8), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)par1.B, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glTranslated((double)0.0, (double)-0.26, (double)-1.7);
        GL11.glPushMatrix();
        this.model.render();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)-1.11f, (float)0.637f, (float)1.726f);
        GL11.glRotatef((float)(-wd / 2.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        this.part.render();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)1.11f, (float)0.637f, (float)1.726f);
        GL11.glRotatef((float)(-wd / 2.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        this.part.render();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)-1.11f, (float)0.637f, (float)-1.8f);
        GL11.glRotatef((float)(-wd / 2.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        this.part.render();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)1.11f, (float)0.637f, (float)-1.8f);
        GL11.glRotatef((float)(-wd / 2.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        this.part.render();
        GL11.glPopMatrix();
        GL11.glEnable((int)2884);
        bgz.a((aqx)par1.D());
        GL11.glPopMatrix();
    }

    public void a(mp par1, double par2, double par4, double par6, float par8, float par9) {
        this.renderCherepanov((Entityregina)par1, par2, par4, par6, par8, par9);
    }
}
