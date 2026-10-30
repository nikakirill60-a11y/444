/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package ru.metrocraft.port.client.gates;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelGermoRamka
extends ModelBase {
    ModelRenderer head;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;

    public ModelGermoRamka() {
        this.textureWidth = 512;
        this.textureHeight = 256;
        this.Shape3 = new ModelRenderer((ModelBase)this, 34, 0);
        this.Shape3.addBox(0.0f, 0.0f, 1.0f, 16, 64, 1);
        this.Shape3.setRotationPoint(-8.0f, -40.0f, 38.0f);
        this.Shape3.setTextureSize(512, 256);
        this.Shape3.mirror = true;
        this.setRotation(this.Shape3, 0.0f, 0.0f, 0.0f);
        this.Shape4 = new ModelRenderer((ModelBase)this, 0, 0);
        this.Shape4.addBox(0.0f, 0.0f, 1.0f, 16, 64, 1);
        this.Shape4.setRotationPoint(-8.0f, -40.0f, -41.0f);
        this.Shape4.setTextureSize(512, 256);
        this.Shape4.mirror = true;
        this.setRotation(this.Shape4, 0.0f, 0.0f, 0.0f);
        this.Shape5 = new ModelRenderer((ModelBase)this, 68, 0);
        this.Shape5.addBox(0.0f, 0.0f, 1.0f, 16, 1, 78);
        this.Shape5.setRotationPoint(-8.0f, -40.0f, -40.0f);
        this.Shape5.setTextureSize(512, 256);
        this.Shape5.mirror = true;
        this.setRotation(this.Shape5, 0.0f, 0.0f, 0.0f);
    }



    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }



    public void render() {
        this.Shape3.render(0.0625f);
        this.Shape4.render(0.0625f);
        this.Shape5.render(0.0625f);
    }
}

