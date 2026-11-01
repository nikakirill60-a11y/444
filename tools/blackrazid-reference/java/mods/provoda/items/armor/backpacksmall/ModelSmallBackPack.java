/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bbz
 *  bcd
 *  bdi
 *  mp
 */
package mods.provoda.items.armor.backpacksmall;

public class ModelSmallBackPack
extends bbz {
    bdi Shape1 = new bdi((bcd)this, 20, 58);
    bdi Shape2;
    bdi Shape3;
    bdi Shape4;
    bdi Shape5;
    bdi Shape6;
    bdi Shape7;
    bdi Shape8;
    bdi Shape9;
    bdi Shape10;
    bdi Shape11;
    bdi Shape12;

    public ModelSmallBackPack(float var1) {
        super(var1, 0.0f, 64, 64);
        this.Shape1.a(0.0f, 0.0f, 0.0f, 4, 3, 2);
        this.Shape1.a(-2.0f, 1.0f, 1.9f);
        this.Shape1.b(64, 64);
        this.Shape1.i = true;
        this.setRotation(this.Shape1, 0.0f, 0.0f, 0.0f);
        this.Shape2 = new bdi((bcd)this, 1, 53);
        this.Shape2.a(0.0f, 0.0f, 0.0f, 4, 3, 1);
        this.Shape2.a(-2.0f, 1.1f, 3.0f);
        this.Shape2.b(64, 64);
        this.Shape2.i = true;
        this.setRotation(this.Shape2, 0.0f, 0.0f, 0.0f);
        this.Shape3 = new bdi((bcd)this, 1, 42);
        this.Shape3.a(0.0f, 0.0f, 0.0f, 4, 4, 2);
        this.Shape3.a(-2.0f, 5.0f, 2.0f);
        this.Shape3.b(64, 64);
        this.Shape3.i = true;
        this.setRotation(this.Shape3, 0.0f, 0.0f, 0.0f);
        this.Shape4 = new bdi((bcd)this, 1, 49);
        this.Shape4.a(0.0f, 0.0f, 0.0f, 4, 1, 2);
        this.Shape4.a(-2.0f, 3.2f, 1.9f);
        this.Shape4.b(64, 64);
        this.Shape4.i = true;
        this.setRotation(this.Shape4, 0.0f, 0.0f, 0.0f);
        this.Shape5 = new bdi((bcd)this, 1, 49);
        this.Shape5.a(0.0f, 0.0f, 0.0f, 4, 1, 2);
        this.Shape5.a(-2.0f, 4.9f, 1.9f);
        this.Shape5.b(64, 64);
        this.Shape5.i = true;
        this.setRotation(this.Shape5, 0.0f, 0.0f, 0.0f);
        this.Shape6 = new bdi((bcd)this, 1, 49);
        this.Shape6.a(0.0f, 0.0f, 0.0f, 4, 1, 2);
        this.Shape6.a(-2.0f, 8.1f, 1.9f);
        this.Shape6.b(64, 64);
        this.Shape6.i = true;
        this.setRotation(this.Shape6, 0.0f, 0.0f, 0.0f);
        this.Shape7 = new bdi((bcd)this, 13, 57);
        this.Shape7.a(0.0f, 0.0f, 0.0f, 1, 4, 2);
        this.Shape7.a(-2.1f, 5.0f, 1.9f);
        this.Shape7.b(64, 64);
        this.Shape7.i = false;
        this.setRotation(this.Shape7, 0.0f, 0.0f, 0.0f);
        this.Shape8 = new bdi((bcd)this, 13, 57);
        this.Shape8.a(0.0f, 0.0f, 0.0f, 1, 4, 2);
        this.Shape8.a(1.1f, 5.0f, 1.9f);
        this.Shape8.b(64, 64);
        this.Shape8.i = true;
        this.setRotation(this.Shape8, 0.0f, 0.0f, 0.0f);
        this.Shape9 = new bdi((bcd)this, 1, 58);
        this.Shape9.a(0.0f, 0.0f, 0.0f, 1, 3, 2);
        this.Shape9.a(-2.1f, 1.1f, 1.9f);
        this.Shape9.b(64, 64);
        this.Shape9.i = false;
        this.setRotation(this.Shape9, 0.0f, 0.0f, 0.0f);
        this.Shape10 = new bdi((bcd)this, 1, 58);
        this.Shape10.a(0.0f, 0.0f, 0.0f, 1, 3, 2);
        this.Shape10.a(1.1f, 1.1f, 1.9f);
        this.Shape10.b(64, 64);
        this.Shape10.i = true;
        this.setRotation(this.Shape10, 0.0f, 0.0f, 0.0f);
        this.Shape11 = new bdi((bcd)this, 8, 61);
        this.Shape11.a(0.0f, 0.0f, 0.0f, 1, 1, 1);
        this.Shape11.a(-2.0f, 4.1f, 1.2f);
        this.Shape11.b(64, 64);
        this.Shape11.i = false;
        this.setRotation(this.Shape11, 0.0f, 0.0f, 0.0f);
        this.Shape12 = new bdi((bcd)this, 8, 61);
        this.Shape12.a(0.0f, 0.0f, 0.0f, 1, 1, 1);
        this.Shape12.a(1.0f, 4.1f, 1.2f);
        this.Shape12.b(64, 64);
        this.Shape12.i = true;
        this.setRotation(this.Shape12, 0.0f, 0.0f, 0.0f);
        this.e.a(this.Shape1);
        this.e.a(this.Shape2);
        this.e.a(this.Shape3);
        this.e.a(this.Shape4);
        this.e.a(this.Shape5);
        this.e.a(this.Shape6);
        this.e.a(this.Shape7);
        this.e.a(this.Shape8);
        this.e.a(this.Shape9);
        this.e.a(this.Shape10);
        this.e.a(this.Shape11);
        this.e.a(this.Shape12);
    }

    public void a(mp var1, float var2, float var3, float var4, float var5, float var6, float var7) {
        super.a(var1, var2, var3, var4, var5, var6, var7);
        this.a(var2, var3, var4, var5, var6, var7, var1);
    }

    private void setRotation(bdi model, float x, float y, float z) {
        model.f = x;
        model.g = y;
        model.h = z;
    }

    public void a(float var1, float var2, float var3, float var4, float var5, float var6, mp var7) {
        super.a(var1, var2, var3, var4, var5, var6, var7);
    }
}
