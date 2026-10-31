/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.EntityLivingBase
 *  org.lwjgl.opengl.GL11
 */
package metro.armor_utill;

import metro.common.Main;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public abstract class ArmorCustomModel {
    public final PartModel partHead = new PartModel();
    public final PartModel partBody = new PartModel();
    public final PartModel partRightArm = new PartModel();
    public final PartModel partLeftArm = new PartModel();
    public final PartModel partRightLeg = new PartModel();
    public final PartModel partLeftLeg = new PartModel();
    public int color = -1;

    public abstract void pre();

    public abstract void post();

    public abstract void partHead();

    public abstract void partBody();

    public abstract void partRightArm();

    public abstract void partLeftArm();

    public abstract void partRightLeg();

    public abstract void partLeftLeg();

    public void render(EntityLivingBase entity) {
        if (!Main.isServer) {
            GL11.glPushMatrix();
            if (this.color != -1) {
                float red = (float)(this.color >> 16 & 0xFF) / 255.0f;
                float blue = (float)(this.color >> 8 & 0xFF) / 255.0f;
                float green = (float)(this.color & 0xFF) / 255.0f;
                GL11.glColor3f((float)red, (float)blue, (float)green);
            }
            this.pre();
            float parTicks = 0.0625f;
            float f6 = 2.0f;
            if (this.partHead.showModel) {
                GL11.glPushMatrix();
                if (entity.func_70631_g_()) {
                    GL11.glScalef((float)(1.5f / f6), (float)(1.5f / f6), (float)(1.5f / f6));
                    GL11.glTranslatef((float)0.0f, (float)(16.0f * parTicks), (float)0.0f);
                }
                this.blank(this.partHead, parTicks);
                this.partHead();
                GL11.glPopMatrix();
            }
            if (entity.func_70631_g_()) {
                GL11.glPushMatrix();
                GL11.glScalef((float)(1.0f / f6), (float)(1.0f / f6), (float)(1.0f / f6));
                GL11.glTranslatef((float)0.0f, (float)(24.0f * parTicks), (float)0.0f);
            }
            if (this.partBody.showModel) {
                GL11.glPushMatrix();
                this.blank(this.partBody, parTicks);
                this.partBody();
                GL11.glPopMatrix();
            }
            if (this.partRightArm.showModel) {
                GL11.glPushMatrix();
                this.blank(this.partRightArm, parTicks);
                this.partRightArm();
                GL11.glPopMatrix();
            }
            if (this.partLeftArm.showModel) {
                GL11.glPushMatrix();
                this.blank(this.partLeftArm, parTicks);
                this.partLeftArm();
                GL11.glPopMatrix();
            }
            if (this.partRightLeg.showModel) {
                GL11.glPushMatrix();
                this.blank(this.partRightLeg, parTicks);
                this.partRightLeg();
                GL11.glPopMatrix();
            }
            if (this.partLeftLeg.showModel) {
                GL11.glPushMatrix();
                this.blank(this.partLeftLeg, parTicks);
                this.partLeftLeg();
                GL11.glPopMatrix();
            }
            if (entity.func_70631_g_()) {
                GL11.glPopMatrix();
            }
            this.post();
            GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glPopMatrix();
        }
    }

    private void blank(PartModel mr, float parTicks) {
        if (!Main.isServer) {
            GL11.glTranslatef((float)mr.offsetX, (float)mr.offsetY, (float)mr.offsetZ);
            if (mr.rotationPointX != 0.0f || mr.rotationPointY != 0.0f || mr.rotationPointZ != 0.0f) {
                GL11.glTranslatef((float)(mr.rotationPointX * parTicks), (float)(mr.rotationPointY * parTicks), (float)(mr.rotationPointZ * parTicks));
            }
            if (mr.rotateAngleZ != 0.0f) {
                GL11.glRotatef((float)(mr.rotateAngleZ * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            if (mr.rotateAngleY != 0.0f) {
                GL11.glRotatef((float)(mr.rotateAngleY * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (mr.rotateAngleX != 0.0f) {
                GL11.glRotatef((float)(mr.rotateAngleX * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            GL11.glRotatef((float)180.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        }
    }

    public static class PartModel {
        public float rotationPointX;
        public float rotationPointY;
        public float rotationPointZ;
        public float rotateAngleX;
        public float rotateAngleY;
        public float rotateAngleZ;
        public float offsetX;
        public float offsetY;
        public float offsetZ;
        public boolean showModel;

        public void set(ModelRenderer mr) {
            this.rotateAngleX = mr.field_78795_f;
            this.rotateAngleY = mr.field_78796_g;
            this.rotateAngleZ = mr.field_78808_h;
            this.rotationPointX = mr.field_78800_c;
            this.rotationPointY = mr.field_78797_d;
            this.rotationPointZ = mr.field_78798_e;
            this.offsetX = mr.field_82906_o;
            this.offsetY = mr.field_82908_p;
            this.offsetZ = mr.field_82907_q;
            this.showModel = mr.field_78806_j;
        }
    }
}
