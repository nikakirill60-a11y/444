/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mods.provoda.GLLoader.GLModel
 *  mods.provoda.items.att.renderers.RenderButt$NamelessClass722938841
 *  mods.provoda.until.Hand
 *  mp
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 *  wm
 */
package mods.provoda.items.att.renderers;

import mods.provoda.GLLoader.GLModel;
import mods.provoda.ModBase;
import mods.provoda.items.att.renderers.RenderButt;
import mods.provoda.until.Hand;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderButt
implements IItemRenderer {
    Hand hand = new Hand();
    GLModel model = new GLModel("/mods/provoda/guns/models/revolver/butt.obj");

    public boolean handleRenderType(wm item, IItemRenderer.ItemRenderType type) {
        switch (NamelessClass722938841.$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRenderType[type.ordinal()]) {
            case 1: {
                if (ModBase.clientType == 0) {
                    return true;
                }
                return ModBase.clientType == 1;
            }
            case 2: {
                if (ModBase.clientType == 0) {
                    return true;
                }
                return ModBase.clientType == 1;
            }
            case 3: {
                return true;
            }
        }
        return false;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, wm item, IItemRenderer.ItemRendererHelper helper) {
        return false;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, wm item, Object ... data) {
        Minecraft mc = Minecraft.x();
        int i1 = mc.p.a(mc.g.cv, (String)null);
        if (mc.p.a(mc.g.cv, (String)null) == -1) {
            i1 = mc.p.f("/mob/char.png");
        }
        switch (NamelessClass722938841.$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRenderType[type.ordinal()]) {
            case 1: {
                GL11.glShadeModel((int)7425);
                GL11.glDisable((int)2884);
                GL11.glPopMatrix();
                GL11.glScaled((double)4.0, (double)4.0, (double)4.0);
                GL11.glTranslatef((float)-0.1f, (float)0.07f, (float)0.1f);
                GL11.glRotatef((float)-0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)-10.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)-20.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.model.render();
                GL11.glPushMatrix();
                break;
            }
            case 2: {
                GL11.glShadeModel((int)7425);
                GL11.glDisable((int)2884);
                GL11.glPushMatrix();
                GL11.glScaled((double)3.5, (double)3.5, (double)3.5);
                GL11.glTranslatef((float)-0.0f, (float)-0.03f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.model.render();
                GL11.glPopMatrix();
                break;
            }
            case 3: {
                GL11.glShadeModel((int)7425);
                GL11.glDisable((int)2884);
                GL11.glPopMatrix();
                GL11.glRotatef((float)200.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glTranslatef((float)0.4f, (float)0.64f, (float)0.8f);
                GL11.glScaled((double)5.0, (double)5.0, (double)5.0);
                GL11.glRotatef((float)-0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.model.render();
                GL11.glPopMatrix();
                GL11.glTranslatef((float)-0.9f, (float)0.5f, (float)-0.2f);
                GL11.glScaled((double)2.0, (double)2.0, (double)2.0);
                GL11.glRotatef((float)5.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)80.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)-95.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glBindTexture((int)3553, (int)i1);
                this.hand.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glPushMatrix();
                GL11.glPushMatrix();
            }
        }
    }
}
