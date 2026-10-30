/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.MathHelper
 *  noppes.npcs.constants.EnumAnimation
 *  noppes.npcs.entity.EntityCustomNpc
 *  org.lwjgl.opengl.GL11
 */
package com.flansmod.client.model;

import com.flansmod.client.tmt.ModelRendererTurbo;
import com.flansmod.common.teams.ArmourType;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import noppes.npcs.constants.EnumAnimation;
import noppes.npcs.entity.EntityCustomNpc;
import org.lwjgl.opengl.GL11;

public class ModelCustomArmour
extends ModelBiped {
    public ArmourType type;
    public ModelRendererTurbo[] headModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] bodyModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] leftArmModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] rightArmModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] leftLegModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] rightLegModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] skirtFrontModel = new ModelRendererTurbo[0];
    public ModelRendererTurbo[] skirtRearModel = new ModelRendererTurbo[0];

    public void setRotationAnglesNPC(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        this.field_78116_c.field_78796_g = par4 / 57.295776f;
        this.field_78116_c.field_78795_f = par5 / 57.295776f;
        this.field_78116_c.field_78808_h = 0.0f;
        this.field_78123_h.field_78795_f = MathHelper.func_76134_b((float)(par1 * 0.6662f)) * 1.4f * par2;
        this.field_78124_i.field_78795_f = MathHelper.func_76134_b((float)(par1 * 0.6662f + (float)Math.PI)) * 1.4f * par2;
        this.field_78123_h.field_78796_g = 0.0f;
        this.field_78124_i.field_78796_g = 0.0f;
        this.field_78113_g.field_78795_f = 0.0f;
        this.field_78113_g.field_78796_g = 0.0f;
        this.field_78113_g.field_78808_h = 0.0f;
        this.field_78113_g.field_78797_d = 2.0f;
        this.field_78112_f.field_78795_f = 0.0f;
        this.field_78112_f.field_78796_g = 0.0f;
        this.field_78112_f.field_78808_h = 0.0f;
        this.field_78112_f.field_78797_d = 2.0f;
        this.field_78115_e.field_78796_g = 0.0f;
        this.field_78115_e.field_78795_f = 0.0f;
        this.field_78115_e.field_78808_h = 0.0f;
        this.field_78124_i.field_78808_h = 0.0f;
        this.field_78124_i.field_78798_e = 0.0f;
        this.field_78123_h.field_78808_h = 0.0f;
        this.field_78123_h.field_78798_e = 0.0f;
        this.field_78123_h.field_78797_d = 12.0f;
        this.field_78124_i.field_78797_d = 12.0f;
    }

    public void setRotationAnglesHUG(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        float f6 = MathHelper.func_76126_a((float)(this.field_78095_p * 3.141593f));
        float f7 = MathHelper.func_76126_a((float)((1.0f - (1.0f - this.field_78095_p) * (1.0f - this.field_78095_p)) * 3.141593f));
        this.field_78112_f.field_78808_h = 0.0f;
        this.field_78113_g.field_78808_h = 0.0f;
        this.field_78112_f.field_78796_g = -(0.1f - f6 * 0.6f);
        this.field_78113_g.field_78796_g = 0.1f;
        this.field_78112_f.field_78795_f = -1.570796f;
        this.field_78113_g.field_78795_f = -1.570796f;
        this.field_78112_f.field_78795_f -= f6 * 1.2f - f7 * 0.4f;
        this.field_78112_f.field_78808_h += MathHelper.func_76134_b((float)(par3 * 0.09f)) * 0.05f + 0.05f;
        this.field_78113_g.field_78808_h -= MathHelper.func_76134_b((float)(par3 * 0.09f)) * 0.05f + 0.05f;
        this.field_78112_f.field_78795_f += MathHelper.func_76126_a((float)(par3 * 0.067f)) * 0.05f;
        this.field_78113_g.field_78795_f -= MathHelper.func_76126_a((float)(par3 * 0.067f)) * 0.05f;
        this.field_78124_i.field_78808_h = 0.0f;
        this.field_78124_i.field_78798_e = 0.0f;
        this.field_78123_h.field_78808_h = 0.0f;
        this.field_78123_h.field_78798_e = 0.0f;
        this.field_78123_h.field_78797_d = 12.0f;
        this.field_78124_i.field_78797_d = 12.0f;
    }

    public void setRotationAnglesAIMING(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        float f6 = 0.0f;
        float f7 = 0.0f;
        this.field_78112_f.field_78808_h = 0.0f;
        this.field_78113_g.field_78808_h = 0.0f;
        this.field_78112_f.field_78796_g = -(0.1f - f6 * 0.6f) + this.field_78116_c.field_78796_g;
        this.field_78113_g.field_78796_g = 0.1f - f6 * 0.6f + this.field_78116_c.field_78796_g + 0.4f;
        this.field_78112_f.field_78795_f = -1.5707964f + this.field_78116_c.field_78795_f;
        this.field_78113_g.field_78795_f = -1.5707964f + this.field_78116_c.field_78795_f;
        this.field_78112_f.field_78795_f -= f6 * 1.2f - f7 * 0.4f;
        this.field_78113_g.field_78795_f -= f6 * 1.2f - f7 * 0.4f;
        this.field_78112_f.field_78808_h += MathHelper.func_76134_b((float)(par3 * 0.09f)) * 0.05f + 0.05f;
        this.field_78113_g.field_78808_h -= MathHelper.func_76134_b((float)(par3 * 0.09f)) * 0.05f + 0.05f;
        this.field_78112_f.field_78795_f += MathHelper.func_76126_a((float)(par3 * 0.067f)) * 0.05f;
        this.field_78113_g.field_78795_f -= MathHelper.func_76126_a((float)(par3 * 0.067f)) * 0.05f;
        this.field_78124_i.field_78808_h = 0.0f;
        this.field_78124_i.field_78798_e = 0.0f;
        this.field_78123_h.field_78808_h = 0.0f;
        this.field_78123_h.field_78798_e = 0.0f;
        this.field_78123_h.field_78797_d = 12.0f;
        this.field_78124_i.field_78797_d = 12.0f;
    }

    public void setRotationAnglesCRAWLING(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        this.field_78116_c.field_78808_h = -par4 / 57.295776f;
        this.field_78116_c.field_78796_g = 0.0f;
        this.field_78116_c.field_78795_f = -0.95993114f;
        if ((double)par2 > 0.25) {
            par2 = 0.25f;
        }
        float movement = MathHelper.func_76134_b((float)(par1 * 0.8f + (float)Math.PI)) * par2;
        this.field_78113_g.field_78795_f = (float)Math.PI - movement * 0.25f;
        this.field_78113_g.field_78796_g = movement * -0.46f;
        this.field_78113_g.field_78808_h = movement * -0.2f;
        this.field_78113_g.field_78797_d = 2.0f - movement * 9.0f;
        this.field_78112_f.field_78795_f = (float)Math.PI + movement * 0.25f;
        this.field_78112_f.field_78796_g = movement * -0.4f;
        this.field_78112_f.field_78808_h = movement * -0.2f;
        this.field_78112_f.field_78797_d = 2.0f + movement * 9.0f;
        this.field_78115_e.field_78796_g = movement * 0.1f;
        this.field_78115_e.field_78795_f = 0.0f;
        this.field_78115_e.field_78808_h = movement * 0.1f;
        this.field_78124_i.field_78795_f = movement * 0.1f;
        this.field_78124_i.field_78796_g = movement * 0.1f;
        this.field_78124_i.field_78808_h = -0.122173056f - movement * 0.25f;
        this.field_78124_i.field_78797_d = 10.4f + movement * 9.0f;
        this.field_78124_i.field_78798_e = movement * 0.6f - 0.01f;
        this.field_78123_h.field_78795_f = movement * -0.1f;
        this.field_78123_h.field_78796_g = movement * 0.1f;
        this.field_78123_h.field_78808_h = 0.122173056f - movement * 0.25f;
        this.field_78123_h.field_78797_d = 10.4f - movement * 9.0f;
        this.field_78123_h.field_78798_e = movement * -0.6f - 0.01f;
    }

    public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glPushMatrix();
        GL11.glScalef((float)this.type.modelScale, (float)this.type.modelScale, (float)this.type.modelScale);
        this.field_78117_n = entity.func_70093_af();
        ItemStack itemstack = ((EntityLivingBase)entity).func_71124_b(0);
        this.field_78120_m = itemstack != null ? 1 : 0;
        this.field_78118_o = false;
        if (itemstack != null && entity instanceof EntityPlayer && ((EntityPlayer)entity).func_71052_bv() > 0) {
            EnumAction enumaction = itemstack.func_77975_n();
            if (enumaction == EnumAction.block) {
                this.field_78120_m = 3;
            } else if (enumaction == EnumAction.bow) {
                this.field_78118_o = true;
            }
        }
        if (entity instanceof EntityCustomNpc) {
            EntityCustomNpc npc = (EntityCustomNpc)entity;
            this.setRotationAnglesNPC(f, f1, f2, f3, f4, f5, entity);
            if (npc.currentAnimation == EnumAnimation.NONE || npc.currentAnimation == EnumAnimation.LYING || npc.currentAnimation == EnumAnimation.SNEAKING) {
                this.field_78119_l = 0;
                if (npc.getOffHand() != null) {
                    this.field_78119_l = 1;
                }
                this.field_78113_g.field_78795_f = this.field_78119_l != 0 ? this.field_78113_g.field_78795_f * 0.5f - 0.31415927f * (float)this.field_78119_l : 0.0f;
            }
            if (npc.currentAnimation == EnumAnimation.HUG) {
                this.setRotationAnglesHUG(f, f1, f2, f3, f4, f5, entity);
            } else if (npc.currentAnimation == EnumAnimation.AIMING) {
                this.setRotationAnglesAIMING(f, f1, f2, f3, f4, f5, entity);
            } else if (npc.currentAnimation == EnumAnimation.CRAWLING) {
                this.setRotationAnglesCRAWLING(f, f1, f2, f3, f4, f5, entity);
            } else {
                this.func_78087_a(f, f1, f2, f3, f4, f5, entity);
            }
        } else {
            this.func_78087_a(f, f1, f2, f3, f4, f5, entity);
        }
        this.render(this.headModel, this.field_78116_c, f5, this.type.modelScale);
        this.render(this.bodyModel, this.field_78115_e, f5, this.type.modelScale);
        this.render(this.leftArmModel, this.field_78113_g, f5, this.type.modelScale);
        this.render(this.rightArmModel, this.field_78112_f, f5, this.type.modelScale);
        this.render(this.leftLegModel, this.field_78124_i, f5, this.type.modelScale);
        this.render(this.rightLegModel, this.field_78123_h, f5, this.type.modelScale);
        for (ModelRendererTurbo mod : this.skirtFrontModel) {
            mod.field_78800_c = (this.field_78124_i.field_78800_c + this.field_78123_h.field_78800_c) / 2.0f / this.type.modelScale;
            mod.field_78797_d = (this.field_78124_i.field_78797_d + this.field_78123_h.field_78797_d) / 2.0f / this.type.modelScale;
            mod.field_78798_e = (this.field_78124_i.field_78798_e + this.field_78123_h.field_78798_e) / 2.0f / this.type.modelScale;
            mod.field_78795_f = Math.min(this.field_78124_i.field_78795_f, this.field_78123_h.field_78795_f);
            mod.field_78796_g = this.field_78124_i.field_78796_g;
            mod.field_78808_h = this.field_78124_i.field_78808_h;
            mod.func_78785_a(f5);
        }
        for (ModelRendererTurbo mod : this.skirtRearModel) {
            mod.field_78800_c = (this.field_78124_i.field_78800_c + this.field_78123_h.field_78800_c) / 2.0f / this.type.modelScale;
            mod.field_78797_d = (this.field_78124_i.field_78797_d + this.field_78123_h.field_78797_d) / 2.0f / this.type.modelScale;
            mod.field_78798_e = (this.field_78124_i.field_78798_e + this.field_78123_h.field_78798_e) / 2.0f / this.type.modelScale;
            mod.field_78795_f = Math.max(this.field_78124_i.field_78795_f, this.field_78123_h.field_78795_f);
            mod.field_78796_g = this.field_78124_i.field_78796_g;
            mod.field_78808_h = this.field_78124_i.field_78808_h;
            mod.func_78785_a(f5);
        }
        GL11.glPopMatrix();
    }

    public void render(ModelRendererTurbo[] models, ModelRenderer bodyPart, float f5, float scale) {
        this.setBodyPart(models, bodyPart, scale);
        for (ModelRendererTurbo mod : models) {
            mod.field_78795_f = bodyPart.field_78795_f;
            mod.field_78796_g = bodyPart.field_78796_g;
            mod.field_78808_h = bodyPart.field_78808_h;
            mod.func_78785_a(f5);
        }
    }

    public void setBodyPart(ModelRendererTurbo[] models, ModelRenderer bodyPart, float scale) {
        for (ModelRendererTurbo mod : models) {
            mod.field_78800_c = bodyPart.field_78800_c / scale;
            mod.field_78797_d = bodyPart.field_78797_d / scale;
            mod.field_78798_e = bodyPart.field_78798_e / scale;
        }
    }
}

