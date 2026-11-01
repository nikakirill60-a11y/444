/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bbz
 *  bcd
 *  bdi
 *  mp
 */
package mods.provoda.items.armor.him.models;

public class HimLeg
extends bbz {
    public bdi Shape1 = new bdi((bcd)this, 1, 52);
    public bdi Shape2;
    public bdi Shape3;
    public bdi Shape4;
    public bdi Shape5;
    public bdi Shape6;
    public bdi Shape7;
    public bdi Shape8;
    public bdi Shape9;
    public bdi Shape10;
    public bdi Shape11;
    public bdi Shape12;

    public HimLeg(float var1) {
        super(var1, 0.0f, 64, 64);
        this.Shape1.a(0.0f, 0.0f, 0.0f, 4, 10, 1);
        this.Shape1.a(-2.0f, 0.0f, -2.2f);
        this.Shape1.i = true;
        this.setRotation(this.Shape1, 0.0f, 0.0f, 0.0f);
        this.Shape2.a(0.0f, 0.0f, 0.0f, 1, 10, 4);
        this.Shape2.a(-2.2f, 0.0f, -2.0f);
        this.Shape2.i = true;
        this.setRotation(this.Shape2, 0.0f, 0.0f, 0.0f);
        this.Shape2.i = false;
        this.Shape3.a(0.0f, 0.0f, 0.0f, 4, 10, 1);
        this.Shape3.a(-2.0f, 0.0f, 1.2f);
        this.Shape3.i = true;
        this.setRotation(this.Shape3, 0.0f, 0.0f, 0.0f);
        this.Shape3.i = false;
        this.Shape4.a(0.0f, 0.0f, 0.0f, 4, 3, 1);
        this.Shape4.a(-2.2f, 3.0f, -2.5f);
        this.Shape4.i = true;
        this.setRotation(this.Shape4, 0.0f, 0.0f, 0.0f);
        this.Shape5.a(0.0f, 0.0f, 0.0f, 1, 3, 4);
        this.Shape5.a(-2.5f, 3.0f, -2.4f);
        this.Shape5.i = true;
        this.setRotation(this.Shape5, 0.0f, 0.0f, 0.0f);
        this.Shape6.a(0.0f, 0.0f, 0.0f, 4, 10, 1);
        this.Shape6.a(-2.0f, 0.0f, -2.2f);
        this.Shape6.i = true;
        this.setRotation(this.Shape6, 0.0f, 0.0f, 0.0f);
        this.Shape6.i = false;
        this.Shape7.a(0.0f, 0.0f, 0.0f, 1, 10, 4);
        this.Shape7.a(1.2f, 0.0f, -2.0f);
        this.Shape7.b(64, 64);
        this.Shape7.i = true;
        this.setRotation(this.Shape7, 0.0f, 0.0f, 0.0f);
        this.Shape8.a(0.0f, 0.0f, 0.0f, 4, 10, 1);
        this.Shape8.a(-2.0f, 0.0f, 1.2f);
        this.Shape8.b(64, 64);
        this.Shape8.i = true;
        this.setRotation(this.Shape8, 0.0f, 0.0f, 0.0f);
        this.Shape9.a(0.0f, 0.0f, 0.0f, 4, 3, 1);
        this.Shape9.a(-2.2f, 3.0f, -2.5f);
        this.Shape9.b(64, 64);
        this.Shape9.i = true;
        this.setRotation(this.Shape9, 0.0f, 0.0f, 0.0f);
        this.Shape10.a(0.0f, 0.0f, 0.0f, 1, 3, 4);
        this.Shape10.a(1.5f, 3.0f, -2.4f);
        this.Shape10.b(64, 64);
        this.Shape10.i = true;
        this.setRotation(this.Shape10, 0.0f, 0.0f, 0.0f);
        this.Shape11.a(0.0f, 0.0f, 0.0f, 1, 10, 4);
        this.Shape11.a(1.2f, 0.0f, -2.0f);
        this.Shape11.b(64, 64);
        this.Shape11.i = true;
        this.setRotation(this.Shape11, 0.0f, 0.0f, 0.0f);
        this.Shape12.a(0.0f, 0.0f, 0.0f, 1, 10, 4);
        this.Shape12.a(-2.2f, 0.0f, -2.0f);
        this.Shape12.b(64, 64);
        this.Shape12.i = true;
        this.setRotation(this.Shape12, 0.0f, 0.0f, 0.0f);
        this.Shape12.i = false;
        this.h.a(this.Shape1);
        this.h.a(this.Shape2);
        this.h.a(this.Shape3);
        this.h.a(this.Shape4);
        this.h.a(this.Shape5);
        this.h.a(this.Shape11);
        this.i.a(this.Shape6);
        this.i.a(this.Shape7);
        this.i.a(this.Shape8);
        this.i.a(this.Shape9);
        this.i.a(this.Shape10);
        this.i.a(this.Shape12);
    }

    public void a(mp var1, float var2, float var3, float var4, float var5, float var6, float var7) {
        super.a(var1, var2, var3, var4, var5, var6, var7);
        this.a(var2, var3, var4, var5, var6, var7, var1);
    }

    private void setRotation(bdi var1, float var2, float var3, float var4) {
        var1.f = var2;
        var1.g = var3;
        var1.h = var4;
    }

    public void a(float var1, float var2, float var3, float var4, float var5, float var6, mp var7) {
        super.a(var1, var2, var3, var4, var5, var6, var7);
    }
}
