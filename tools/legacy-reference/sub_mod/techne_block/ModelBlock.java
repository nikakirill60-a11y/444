/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package sub_mod.techne_block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBlock
extends ModelBase {
    ModelRenderer block;

    public ModelBlock() {
        this.field_78090_t = 64;
        this.field_78089_u = 32;
        this.block = new ModelRenderer((ModelBase)this, 0, 0);
        this.block.func_78789_a(0.0f, 0.0f, 0.0f, 16, 16, 16);
        this.block.func_78793_a(-8.0f, 8.0f, -8.0f);
        this.block.func_78787_b(64, 32);
        this.block.field_78809_i = true;
        this.setRotation(this.block, 0.0f, 0.0f, 0.0f);
    }

    public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.func_78088_a(entity, f, f1, f2, f3, f4, f5);
        this.func_78087_a(f, f1, f2, f3, f4, f5, entity);
        this.block.func_78785_a(f5);
    }

    public void render() {
        this.block.func_78785_a(0.0625f);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.field_78795_f = x;
        model.field_78796_g = y;
        model.field_78808_h = z;
    }

    public void func_78087_a(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.func_78087_a(f, f1, f2, f3, f4, f5, entity);
    }
}

