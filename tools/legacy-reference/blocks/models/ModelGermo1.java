/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package blocks.models;

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
        this.field_78090_t = 512;
        this.field_78089_u = 256;
        Shape1 = new ModelRenderer((ModelBase)this, 168, 176);
        Shape1.func_78789_a(0.0f, 0.0f, 0.0f, 10, 2, 78);
        Shape1.func_78793_a(-5.0f, 5.0f, -39.0f);
        Shape1.func_78787_b(512, 256);
        ModelGermo1.Shape1.field_78809_i = true;
        this.setRotation(Shape1, 0.0f, 0.0f, 0.0f);
        Shape2 = new ModelRenderer((ModelBase)this, 344, 115);
        Shape2.func_78789_a(0.0f, 0.0f, 1.0f, 6, 63, 78);
        Shape2.func_78793_a(-3.0f, -39.0f, -40.0f);
        Shape2.func_78787_b(512, 256);
        ModelGermo1.Shape2.field_78809_i = true;
        this.setRotation(Shape2, 0.0f, 0.0f, 0.0f);
        Shape6 = new ModelRenderer((ModelBase)this, 0, 115);
        Shape6.func_78789_a(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape6.func_78793_a(-4.0f, -4.0f, 27.0f);
        Shape6.func_78787_b(512, 256);
        ModelGermo1.Shape6.field_78809_i = true;
        this.setRotation(Shape6, 0.0f, 0.0f, 0.0f);
        Shape7 = new ModelRenderer((ModelBase)this, 31, 115);
        Shape7.func_78789_a(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape7.func_78793_a(-4.0f, -4.0f, 15.0f);
        Shape7.func_78787_b(512, 256);
        ModelGermo1.Shape7.field_78809_i = true;
        this.setRotation(Shape7, 0.0f, 0.0f, 0.0f);
        Shape8 = new ModelRenderer((ModelBase)this, 61, 115);
        Shape8.func_78789_a(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape8.func_78793_a(-4.0f, -4.0f, 3.0f);
        Shape8.func_78787_b(512, 256);
        ModelGermo1.Shape8.field_78809_i = true;
        this.setRotation(Shape8, 0.0f, 0.0f, 0.0f);
        Shape9 = new ModelRenderer((ModelBase)this, 91, 115);
        Shape9.func_78789_a(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape9.func_78793_a(-4.0f, -4.0f, -10.0f);
        Shape9.func_78787_b(512, 256);
        ModelGermo1.Shape9.field_78809_i = true;
        this.setRotation(Shape9, 0.0f, 0.0f, 0.0f);
        Shape10 = new ModelRenderer((ModelBase)this, 121, 115);
        Shape10.func_78789_a(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape10.func_78793_a(-4.0f, -4.0f, -22.0f);
        Shape10.func_78787_b(512, 256);
        ModelGermo1.Shape10.field_78809_i = true;
        this.setRotation(Shape10, 0.0f, 0.0f, 0.0f);
        Shape11 = new ModelRenderer((ModelBase)this, 340, 27);
        Shape11.func_78789_a(0.0f, 0.0f, 0.0f, 8, 10, 78);
        Shape11.func_78793_a(-4.0f, -14.0f, -39.0f);
        Shape11.func_78787_b(512, 256);
        ModelGermo1.Shape11.field_78809_i = true;
        this.setRotation(Shape11, 0.0f, 0.0f, 0.0f);
        Shape12 = new ModelRenderer((ModelBase)this, 30, 87);
        Shape12.func_78789_a(0.0f, 0.0f, 0.0f, 8, 20, 8);
        Shape12.func_78793_a(-4.0f, -34.0f, -4.0f);
        Shape12.func_78787_b(512, 256);
        ModelGermo1.Shape12.field_78809_i = true;
        this.setRotation(Shape12, 0.0f, 0.0f, 0.0f);
        Shape13 = new ModelRenderer((ModelBase)this, 151, 115);
        Shape13.func_78789_a(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape13.func_78793_a(-4.0f, -4.0f, -34.0f);
        Shape13.func_78787_b(512, 256);
        ModelGermo1.Shape13.field_78809_i = true;
        this.setRotation(Shape13, 0.0f, 0.0f, 0.0f);
        Shape14 = new ModelRenderer((ModelBase)this, 0, 88);
        Shape14.func_78789_a(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape14.func_78793_a(-4.0f, -34.0f, 21.0f);
        Shape14.func_78787_b(512, 256);
        ModelGermo1.Shape14.field_78809_i = true;
        this.setRotation(Shape14, 0.0f, 0.0f, 0.0f);
        Shape15 = new ModelRenderer((ModelBase)this, 62, 88);
        Shape15.func_78789_a(0.0f, 0.0f, 0.0f, 8, 20, 7);
        Shape15.func_78793_a(-4.0f, -34.0f, -28.0f);
        Shape15.func_78787_b(512, 256);
        ModelGermo1.Shape15.field_78809_i = true;
        this.setRotation(Shape15, 0.0f, 0.0f, 0.0f);
    }

    public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.func_78088_a(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5);
        Shape1.func_78785_a(f5);
        Shape2.func_78785_a(f5);
        Shape6.func_78785_a(f5);
        Shape7.func_78785_a(f5);
        Shape8.func_78785_a(f5);
        Shape9.func_78785_a(f5);
        Shape10.func_78785_a(f5);
        Shape11.func_78785_a(f5);
        Shape12.func_78785_a(f5);
        Shape13.func_78785_a(f5);
        Shape14.func_78785_a(f5);
        Shape15.func_78785_a(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.field_78795_f = x;
        model.field_78796_g = y;
        model.field_78808_h = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5);
    }

    public void render() {
        Shape1.func_78785_a(0.0625f);
        Shape2.func_78785_a(0.0625f);
        Shape6.func_78785_a(0.0625f);
        Shape7.func_78785_a(0.0625f);
        Shape8.func_78785_a(0.0625f);
        Shape9.func_78785_a(0.0625f);
        Shape10.func_78785_a(0.0625f);
        Shape11.func_78785_a(0.0625f);
        Shape12.func_78785_a(0.0625f);
        Shape13.func_78785_a(0.0625f);
        Shape14.func_78785_a(0.0625f);
        Shape15.func_78785_a(0.0625f);
    }
}

