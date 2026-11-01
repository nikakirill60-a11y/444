/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  kx
 *  mg
 *  mp
 *  ng
 *  no
 *  rh
 *  sq
 *  wk
 *  wm
 */
package mods.provoda.entity;

public class EntityObject
extends ng {
    public sq owner;
    private double xPos;
    private double zPos;
    private double yaw;
    public int hl;
    protected int aS;

    public EntityObject(aab var1) {
        super(var1);
        ((ng)this).aS = this.aW();
        this.ag = true;
        this.hl = this.ab.nextInt(20) + 20;
    }

    public void a() {
        super.a();
    }

    public void a(mp var1, int var2, double var3, double var5) {
        this.an = true;
        float var7 = kx.a((double)(var3 * var3 + var5 * var5));
        float var8 = 0.225f;
        this.x /= 2.0;
        this.y /= 2.0;
        this.z /= 2.0;
        this.x -= var3 / (double)var7 * (double)var8;
        this.y += (double)var8;
        this.z -= var5 / (double)var7 * (double)var8;
        if (this.y > 0.4) {
            this.y = 0.4;
        }
    }

    public void bA() {
    }

    public boolean bf() {
        return true;
    }

    public boolean bm() {
        return false;
    }

    public void a(float var1) {
    }

    public void c() {
        this.a(0.54f, 0.09f);
        this.ay = this.A = (float)this.yaw;
        if (this.hl <= 0) {
            this.j(600);
        }
    }

    public boolean bD() {
        if (this.bF() == no.b) {
            // empty if block
        }
        return true;
    }

    protected float getGravityVelocity() {
        return 0.0f;
    }

    public rh a(int var1, int var2, float var3) {
        return this.a(new wm(var1, var2, 10), var3);
    }

    public rh b(int var1, int var2) {
        return this.a(var1, var2, 0.0f);
    }

    public int aW() {
        return 9999;
    }

    protected String bc() {
        return null;
    }

    protected String bd() {
        return null;
    }

    public boolean a(mg var1, int var2) {
        sq p;
        if (var1.i() != null && var1.i() instanceof sq && (p = (sq)var1.i()).cd() != null && p.cd().b() == wk.aE) {
            System.out.println("true");
            super.a(var1, 100000);
            return true;
        }
        if (this.aq()) {
            return false;
        }
        if ("outOfWorld".equals(var1.n())) {
            super.a(var1, 100000);
            return true;
        }
        return super.a(var1, var2);
    }
}
