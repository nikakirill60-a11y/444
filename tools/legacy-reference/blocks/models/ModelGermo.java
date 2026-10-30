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
        this.field_78090_t = 512;
        this.field_78089_u = 256;
        this.Shape2 = new ModelRenderer((ModelBase)this, 366, 145);
        this.Shape2.func_78789_a(0.0f, 0.0f, 1.0f, 7, 45, 66);
        this.Shape2.func_78793_a(-3.0f, -33.0f, -34.0f);
        this.Shape2.func_78787_b(512, 256);
        this.Shape2.field_78809_i = true;
        this.setRotation(this.Shape2, 0.0f, 0.0f, 0.0f);
        this.Shape1 = new ModelRenderer((ModelBase)this, 338, 0);
        this.Shape1.func_78789_a(0.0f, 0.0f, 1.0f, 9, 6, 78);
        this.Shape1.func_78793_a(-4.0f, -39.0f, -40.0f);
        this.Shape1.func_78787_b(512, 256);
        this.Shape1.field_78809_i = true;
        this.setRotation(this.Shape1, 0.0f, 0.0f, 0.0f);
        this.Shape16 = new ModelRenderer((ModelBase)this, 300, 51);
        this.Shape16.func_78789_a(0.0f, 0.0f, 1.0f, 9, 20, 6);
        this.Shape16.func_78793_a(-4.0f, -33.0f, -4.0f);
        this.Shape16.func_78787_b(512, 256);
        this.Shape16.field_78809_i = true;
        this.setRotation(this.Shape16, 0.0f, 0.0f, 0.0f);
        this.Shape17 = new ModelRenderer((ModelBase)this, 300, 77);
        this.Shape17.func_78789_a(0.0f, 0.0f, 1.0f, 9, 20, 6);
        this.Shape17.func_78793_a(-4.0f, -7.0f, -4.0f);
        this.Shape17.func_78787_b(512, 256);
        this.Shape17.field_78809_i = true;
        this.setRotation(this.Shape17, 0.0f, 0.0f, 0.0f);
        this.Shape13 = new ModelRenderer((ModelBase)this, 193, 184);
        this.Shape13.func_78789_a(0.0f, 0.0f, 1.0f, 9, 6, 66);
        this.Shape13.func_78793_a(-4.0f, -13.0f, -34.0f);
        this.Shape13.func_78787_b(512, 256);
        this.Shape13.field_78809_i = true;
        this.setRotation(this.Shape13, 0.0f, 0.0f, 0.0f);
        this.Shape15 = new ModelRenderer((ModelBase)this, 300, 0);
        this.Shape15.func_78789_a(0.0f, 0.0f, 1.0f, 9, 45, 6);
        this.Shape15.func_78793_a(-4.0f, -33.0f, -40.0f);
        this.Shape15.func_78787_b(512, 256);
        this.Shape15.field_78809_i = true;
        this.setRotation(this.Shape15, 0.0f, 0.0f, 0.0f);
        this.Shape15.field_78809_i = false;
        this.Shape8 = new ModelRenderer((ModelBase)this, 0, 166);
        this.Shape8.func_78789_a(0.0f, 0.0f, 1.0f, 9, 12, 78);
        this.Shape8.func_78793_a(-4.0f, 12.0f, -40.0f);
        this.Shape8.func_78787_b(512, 256);
        this.Shape8.field_78809_i = true;
        this.setRotation(this.Shape8, 0.0f, 0.0f, 0.0f);
        this.Shape7 = new ModelRenderer((ModelBase)this, 270, 0);
        this.Shape7.func_78789_a(0.0f, 0.0f, 1.0f, 9, 45, 6);
        this.Shape7.func_78793_a(-4.0f, -33.0f, 32.0f);
        this.Shape7.func_78787_b(512, 256);
        this.Shape7.field_78809_i = true;
        this.setRotation(this.Shape7, 0.0f, 0.0f, 0.0f);
    }

    public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.func_78088_a(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5);
        this.Shape2.func_78785_a(f5);
        this.Shape1.func_78785_a(f5);
        this.Shape16.func_78785_a(f5);
        this.Shape17.func_78785_a(f5);
        this.Shape13.func_78785_a(f5);
        this.Shape15.func_78785_a(f5);
        this.Shape8.func_78785_a(f5);
        this.Shape7.func_78785_a(f5);
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
        this.Shape1.func_78785_a(0.0625f);
        this.Shape2.func_78785_a(0.0625f);
        this.Shape7.func_78785_a(0.0625f);
        this.Shape8.func_78785_a(0.0625f);
        this.Shape13.func_78785_a(0.0625f);
        this.Shape17.func_78785_a(0.0625f);
        this.Shape15.func_78785_a(0.0625f);
        this.Shape16.func_78785_a(0.0625f);
    }
}

