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

public class ModelGermoRamka
extends ModelBase {
    ModelRenderer head;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;

    public ModelGermoRamka() {
        this.field_78090_t = 512;
        this.field_78089_u = 256;
        this.Shape3 = new ModelRenderer((ModelBase)this, 34, 0);
        this.Shape3.func_78789_a(0.0f, 0.0f, 1.0f, 16, 64, 1);
        this.Shape3.func_78793_a(-8.0f, -40.0f, 38.0f);
        this.Shape3.func_78787_b(512, 256);
        this.Shape3.field_78809_i = true;
        this.setRotation(this.Shape3, 0.0f, 0.0f, 0.0f);
        this.Shape4 = new ModelRenderer((ModelBase)this, 0, 0);
        this.Shape4.func_78789_a(0.0f, 0.0f, 1.0f, 16, 64, 1);
        this.Shape4.func_78793_a(-8.0f, -40.0f, -41.0f);
        this.Shape4.func_78787_b(512, 256);
        this.Shape4.field_78809_i = true;
        this.setRotation(this.Shape4, 0.0f, 0.0f, 0.0f);
        this.Shape5 = new ModelRenderer((ModelBase)this, 68, 0);
        this.Shape5.func_78789_a(0.0f, 0.0f, 1.0f, 16, 1, 78);
        this.Shape5.func_78793_a(-8.0f, -40.0f, -40.0f);
        this.Shape5.func_78787_b(512, 256);
        this.Shape5.field_78809_i = true;
        this.setRotation(this.Shape5, 0.0f, 0.0f, 0.0f);
    }

    public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.func_78088_a(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5);
        this.Shape3.func_78785_a(f5);
        this.Shape4.func_78785_a(f5);
        this.Shape5.func_78785_a(f5);
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
        this.Shape3.func_78785_a(0.0625f);
        this.Shape4.func_78785_a(0.0625f);
        this.Shape5.func_78785_a(0.0625f);
    }
}

