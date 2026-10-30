/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package ru.metrocraft.port.client.mobs;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class Komp
extends ModelBase {
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;

    public Komp() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Shape1 = new ModelRenderer((ModelBase)this, 36, 24);
        this.Shape1.addBox(-2.0f, 11.0f, 3.0f, 12, 6, 1);
        this.Shape1.setRotationPoint(-4.0f, 20.0f, 11.0f);
        this.Shape1.setTextureSize(64, 32);
        this.Shape1.mirror = true;
        this.setRotation(this.Shape1, -1.570796f, 0.0f, 0.0f);
        this.Shape2 = new ModelRenderer((ModelBase)this, 0, 18);
        this.Shape2.addBox(0.0f, 0.0f, 0.0f, 4, 1, 4);
        this.Shape2.setRotationPoint(-2.0f, 23.0f, 2.0f);
        this.Shape2.setTextureSize(64, 32);
        this.Shape2.mirror = true;
        this.setRotation(this.Shape2, 0.0f, 0.0f, 0.0f);
        this.Shape3 = new ModelRenderer((ModelBase)this, 0, 0);
        this.Shape3.addBox(0.0f, 0.0f, 0.0f, 2, 3, 2);
        this.Shape3.setRotationPoint(-1.0f, 20.0f, 3.0f);
        this.Shape3.setTextureSize(64, 32);
        this.Shape3.mirror = true;
        this.setRotation(this.Shape3, 0.0f, 0.0f, 0.0f);
        this.Shape4 = new ModelRenderer((ModelBase)this, 36, 0);
        this.Shape4.addBox(0.0f, 0.0f, 0.0f, 12, 8, 1);
        this.Shape4.setRotationPoint(-6.0f, 12.0f, 4.0f);
        this.Shape4.setTextureSize(64, 32);
        this.Shape4.mirror = true;
        this.setRotation(this.Shape4, -0.1115358f, -0.0174533f, 0.0f);
        this.Shape5 = new ModelRenderer((ModelBase)this, 23, 26);
        this.Shape5.addBox(0.0f, 0.0f, 0.0f, 2, 3, 2);
        this.Shape5.setRotationPoint(5.0f, 21.0f, 4.0f);
        this.Shape5.setTextureSize(64, 32);
        this.Shape5.mirror = true;
        this.setRotation(this.Shape5, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.Shape1.render(f5);
        this.Shape2.render(f5);
        this.Shape3.render(f5);
        this.Shape4.render(f5);
        this.Shape5.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity en) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, en);
    }
}

