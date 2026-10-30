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

public class ModelGermo1
extends ModelBase {
    public static ModelRenderer Shape1;
    public static ModelRenderer Shape2;
    public static ModelRenderer Shape6;
    public static ModelRenderer Shape7;
    public static ModelRenderer Shape8;
    public static ModelRenderer Shape9;
    public static ModelRenderer Shape10;
    public static ModelRenderer Shape11;
    public static ModelRenderer Shape12;
    public static ModelRenderer Shape13;
    public static ModelRenderer Shape14;
    public static ModelRenderer Shape15;

    public ModelGermo1() {
        this.textureWidth = 512;
        this.textureHeight = 256;
        Shape1 = new ModelRenderer((ModelBase)this, 168, 176);
        Shape1.addBox(0.0f, 0.0f, 0.0f, 10, 2, 78);
        Shape1.setRotationPoint(-5.0f, 5.0f, -39.0f);
        Shape1.setTextureSize(512, 256);
        ModelGermo1.Shape1.mirror = true;
        this.setRotation(Shape1, 0.0f, 0.0f, 0.0f);
        Shape2 = new ModelRenderer((ModelBase)this, 344, 115);
        Shape2.addBox(0.0f, 0.0f, 1.0f, 6, 63, 78);
        Shape2.setRotationPoint(-3.0f, -39.0f, -40.0f);
        Shape2.setTextureSize(512, 256);
        ModelGermo1.Shape2.mirror = true;
        this.setRotation(Shape2, 0.0f, 0.0f, 0.0f);
        Shape6 = new ModelRenderer((ModelBase)this, 0, 115);
        Shape6.addBox(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape6.setRotationPoint(-4.0f, -4.0f, 27.0f);
        Shape6.setTextureSize(512, 256);
        ModelGermo1.Shape6.mirror = true;
        this.setRotation(Shape6, 0.0f, 0.0f, 0.0f);
        Shape7 = new ModelRenderer((ModelBase)this, 31, 115);
        Shape7.addBox(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape7.setRotationPoint(-4.0f, -4.0f, 15.0f);
        Shape7.setTextureSize(512, 256);
        ModelGermo1.Shape7.mirror = true;
        this.setRotation(Shape7, 0.0f, 0.0f, 0.0f);
        Shape8 = new ModelRenderer((ModelBase)this, 61, 115);
        Shape8.addBox(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape8.setRotationPoint(-4.0f, -4.0f, 3.0f);
        Shape8.setTextureSize(512, 256);
        ModelGermo1.Shape8.mirror = true;
        this.setRotation(Shape8, 0.0f, 0.0f, 0.0f);
        Shape9 = new ModelRenderer((ModelBase)this, 91, 115);
        Shape9.addBox(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape9.setRotationPoint(-4.0f, -4.0f, -10.0f);
        Shape9.setTextureSize(512, 256);
        ModelGermo1.Shape9.mirror = true;
        this.setRotation(Shape9, 0.0f, 0.0f, 0.0f);
        Shape10 = new ModelRenderer((ModelBase)this, 121, 115);
        Shape10.addBox(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape10.setRotationPoint(-4.0f, -4.0f, -22.0f);
        Shape10.setTextureSize(512, 256);
        ModelGermo1.Shape10.mirror = true;
        this.setRotation(Shape10, 0.0f, 0.0f, 0.0f);
        Shape11 = new ModelRenderer((ModelBase)this, 340, 27);
        Shape11.addBox(0.0f, 0.0f, 0.0f, 8, 10, 78);
        Shape11.setRotationPoint(-4.0f, -14.0f, -39.0f);
        Shape11.setTextureSize(512, 256);
        ModelGermo1.Shape11.mirror = true;
        this.setRotation(Shape11, 0.0f, 0.0f, 0.0f);
        Shape12 = new ModelRenderer((ModelBase)this, 30, 87);
        Shape12.addBox(0.0f, 0.0f, 0.0f, 8, 20, 8);
        Shape12.setRotationPoint(-4.0f, -34.0f, -4.0f);
        Shape12.setTextureSize(512, 256);
        ModelGermo1.Shape12.mirror = true;
        this.setRotation(Shape12, 0.0f, 0.0f, 0.0f);
        Shape13 = new ModelRenderer((ModelBase)this, 151, 115);
        Shape13.addBox(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape13.setRotationPoint(-4.0f, -4.0f, -34.0f);
        Shape13.setTextureSize(512, 256);
        ModelGermo1.Shape13.mirror = true;
        this.setRotation(Shape13, 0.0f, 0.0f, 0.0f);
        Shape14 = new ModelRenderer((ModelBase)this, 0, 88);
        Shape14.addBox(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape14.setRotationPoint(-4.0f, -34.0f, 21.0f);
        Shape14.setTextureSize(512, 256);
        ModelGermo1.Shape14.mirror = true;
        this.setRotation(Shape14, 0.0f, 0.0f, 0.0f);
        Shape15 = new ModelRenderer((ModelBase)this, 62, 88);
        Shape15.addBox(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape15.setRotationPoint(-4.0f, -34.0f, -28.0f);
        Shape15.setTextureSize(512, 256);
        ModelGermo1.Shape15.mirror = true;
        this.setRotation(Shape15, 0.0f, 0.0f, 0.0f);
    }



    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }



    public void render() {
        Shape1.render(0.0625f);
        Shape2.render(0.0625f);
        Shape6.render(0.0625f);
        Shape7.render(0.0625f);
        Shape8.render(0.0625f);
        Shape9.render(0.0625f);
        Shape10.render(0.0625f);
        Shape11.render(0.0625f);
        Shape12.render(0.0625f);
        Shape13.render(0.0625f);
        Shape14.render(0.0625f);
        Shape15.render(0.0625f);
    }
}
