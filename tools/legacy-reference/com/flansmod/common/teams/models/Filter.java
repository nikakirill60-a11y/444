/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package com.flansmod.common.teams.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class Filter
extends ModelBase {
    public ModelRenderer Shape9;

    public Filter() {
        this.field_78090_t = 64;
        this.field_78089_u = 64;
        this.Shape9 = new ModelRenderer((ModelBase)this, 24, 54);
        this.Shape9.func_78789_a(0.0f, -1.5f, 0.0f, 3, 3, 2);
        this.Shape9.func_78793_a(-2.0f, 24.0f, 0.0f);
        this.Shape9.func_78787_b(64, 64);
        this.Shape9.field_78809_i = true;
        this.setRotation(this.Shape9, 1.5707964f, 0.0f, 0.0f);
    }

    public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
        super.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
        this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
        this.Shape9.func_78785_a(var7);
    }

    private void setRotation(ModelRenderer var1, float var2, float var3, float var4) {
        var1.field_78795_f = var2;
        var1.field_78796_g = var3;
        var1.field_78808_h = var4;
    }

    public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
        super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
    }
}

