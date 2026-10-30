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

public class ModelGermo
extends ModelBase {
    ModelRenderer head;
    ModelRenderer Shape2;
    ModelRenderer Shape1;
    ModelRenderer Shape16;
    ModelRenderer Shape17;
    ModelRenderer Shape13;
    ModelRenderer Shape15;
    ModelRenderer Shape8;
    ModelRenderer Shape7;

    public ModelGermo() {
        this.textureWidth = 512;
        this.textureHeight = 256;
        this.Shape2 = new ModelRenderer((ModelBase)this, 366, 145);
        this.Shape2.addBox(0.0f, 0.0f, 1.0f, 7, 45, 66);
        this.Shape2.setRotationPoint(-3.0f, -33.0f, -34.0f);
        this.Shape2.setTextureSize(512, 256);
        this.Shape2.mirror = true;
        this.setRotation(this.Shape2, 0.0f, 0.0f, 0.0f);
        this.Shape1 = new ModelRenderer((ModelBase)this, 338, 0);
        this.Shape1.addBox(0.0f, 0.0f, 1.0f, 9, 6, 78);
        this.Shape1.setRotationPoint(-4.0f, -39.0f, -40.0f);
        this.Shape1.setTextureSize(512, 256);
        this.Shape1.mirror = true;
        this.setRotation(this.Shape1, 0.0f, 0.0f, 0.0f);
        this.Shape16 = new ModelRenderer((ModelBase)this, 300, 51);
        this.Shape16.addBox(0.0f, 0.0f, 1.0f, 9, 20, 6);
        this.Shape16.setRotationPoint(-4.0f, -33.0f, -4.0f);
        this.Shape16.setTextureSize(512, 256);
        this.Shape16.mirror = true;
        this.setRotation(this.Shape16, 0.0f, 0.0f, 0.0f);
        this.Shape17 = new ModelRenderer((ModelBase)this, 300, 77);
        this.Shape17.addBox(0.0f, 0.0f, 1.0f, 9, 20, 6);
        this.Shape17.setRotationPoint(-4.0f, -7.0f, -4.0f);
        this.Shape17.setTextureSize(512, 256);
        this.Shape17.mirror = true;
        this.setRotation(this.Shape17, 0.0f, 0.0f, 0.0f);
        this.Shape13 = new ModelRenderer((ModelBase)this, 193, 184);
        this.Shape13.addBox(0.0f, 0.0f, 1.0f, 9, 6, 66);
        this.Shape13.setRotationPoint(-4.0f, -13.0f, -34.0f);
        this.Shape13.setTextureSize(512, 256);
        this.Shape13.mirror = true;
        this.setRotation(this.Shape13, 0.0f, 0.0f, 0.0f);
        this.Shape15 = new ModelRenderer((ModelBase)this, 300, 0);
        this.Shape15.addBox(0.0f, 0.0f, 1.0f, 9, 45, 6);
        this.Shape15.setRotationPoint(-4.0f, -33.0f, -40.0f);
        this.Shape15.setTextureSize(512, 256);
        this.Shape15.mirror = true;
        this.setRotation(this.Shape15, 0.0f, 0.0f, 0.0f);
        this.Shape15.mirror = false;
        this.Shape8 = new ModelRenderer((ModelBase)this, 0, 166);
        this.Shape8.addBox(0.0f, 0.0f, 1.0f, 9, 12, 78);
        this.Shape8.setRotationPoint(-4.0f, 12.0f, -40.0f);
        this.Shape8.setTextureSize(512, 256);
        this.Shape8.mirror = true;
        this.setRotation(this.Shape8, 0.0f, 0.0f, 0.0f);
        this.Shape7 = new ModelRenderer((ModelBase)this, 270, 0);
        this.Shape7.addBox(0.0f, 0.0f, 1.0f, 9, 45, 6);
        this.Shape7.setRotationPoint(-4.0f, -33.0f, 32.0f);
        this.Shape7.setTextureSize(512, 256);
        this.Shape7.mirror = true;
        this.setRotation(this.Shape7, 0.0f, 0.0f, 0.0f);
    }



    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }



    public void render() {
        this.Shape1.render(0.0625f);
        this.Shape2.render(0.0625f);
        this.Shape7.render(0.0625f);
        this.Shape8.render(0.0625f);
        this.Shape13.render(0.0625f);
        this.Shape17.render(0.0625f);
        this.Shape15.render(0.0625f);
        this.Shape16.render(0.0625f);
    }
}
