/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package ru.metro.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class Trup4
extends ModelBase {
    ModelRenderer head;
    ModelRenderer body;
    ModelRenderer rightarm;
    ModelRenderer leftarm;
    ModelRenderer rightleg;
    ModelRenderer leftleg;
    ModelRenderer Shape1;

    public Trup4() {
        this.field_78090_t = 64;
        this.field_78089_u = 32;
        this.head = new ModelRenderer((ModelBase)this, 0, 0);
        this.head.func_78789_a(-4.0f, -8.0f, -4.0f, 8, 8, 8);
        this.head.func_78793_a(0.0f, 11.0f, 6.0f);
        this.head.func_78787_b(64, 32);
        this.head.field_78809_i = true;
        this.setRotation(this.head, -0.2230717f, 0.1115358f, 0.3346075f);
        this.body = new ModelRenderer((ModelBase)this, 16, 16);
        this.body.func_78789_a(-4.0f, 0.0f, -2.0f, 8, 12, 4);
        this.body.func_78793_a(0.0f, 11.0f, 7.0f);
        this.body.func_78787_b(64, 32);
        this.body.field_78809_i = true;
        this.setRotation(this.body, -0.4461433f, 0.0f, 0.0f);
        this.rightarm = new ModelRenderer((ModelBase)this, 40, 16);
        this.rightarm.func_78789_a(-3.0f, -2.0f, -2.0f, 4, 6, 4);
        this.rightarm.func_78793_a(-5.0f, 13.0f, 6.0f);
        this.rightarm.func_78787_b(64, 32);
        this.rightarm.field_78809_i = true;
        this.setRotation(this.rightarm, -0.4461433f, 0.1115358f, 0.2974289f);
        this.leftarm = new ModelRenderer((ModelBase)this, 40, 16);
        this.leftarm.func_78789_a(-1.0f, 0.0f, -2.0f, 4, 7, 4);
        this.leftarm.func_78793_a(4.0f, 12.0f, 6.0f);
        this.leftarm.func_78787_b(64, 32);
        this.leftarm.field_78809_i = true;
        this.setRotation(this.leftarm, -0.5205006f, -0.5948578f, 0.0f);
        this.rightleg = new ModelRenderer((ModelBase)this, 0, 16);
        this.rightleg.func_78789_a(0.0f, 0.0f, -2.0f, 4, 12, 4);
        this.rightleg.func_78793_a(-4.0f, 22.0f, 4.0f);
        this.rightleg.func_78787_b(64, 32);
        this.rightleg.field_78809_i = true;
        this.setRotation(this.rightleg, -1.682332f, 0.1115358f, 0.3346075f);
        this.leftleg = new ModelRenderer((ModelBase)this, 0, 16);
        this.leftleg.func_78789_a(-2.0f, 0.0f, -2.0f, 4, 6, 4);
        this.leftleg.func_78793_a(2.0f, 22.0f, 3.0f);
        this.leftleg.func_78787_b(64, 32);
        this.leftleg.field_78809_i = true;
        this.setRotation(this.leftleg, -1.422082f, -0.4089647f, 0.1858931f);
        this.Shape1 = new ModelRenderer((ModelBase)this, 40, 20);
        this.Shape1.func_78789_a(1.0f, 0.0f, 0.0f, 4, 6, 4);
        this.Shape1.func_78793_a(5.0f, 16.0f, 1.0f);
        this.Shape1.func_78787_b(64, 32);
        this.Shape1.field_78809_i = true;
        this.setRotation(this.Shape1, -1.003822f, -0.5948578f, 0.1115358f);
    }

    public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.func_78088_a(entity, f, f1, f2, f3, f4, f5);
        this.func_78087_a(f, f1, f2, f3, f4, f5, entity);
        this.head.func_78785_a(f5);
        this.body.func_78785_a(f5);
        this.rightarm.func_78785_a(f5);
        this.leftarm.func_78785_a(f5);
        this.rightleg.func_78785_a(f5);
        this.leftleg.func_78785_a(f5);
        this.Shape1.func_78785_a(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.field_78795_f = x;
        model.field_78796_g = y;
        model.field_78808_h = z;
    }

    public void func_78087_a(float f, float f1, float f2, float f3, float f4, float f5, Entity en) {
        super.func_78087_a(f, f1, f2, f3, f4, f5, en);
    }
}

