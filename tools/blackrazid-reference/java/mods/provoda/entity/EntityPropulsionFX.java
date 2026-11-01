/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  ara
 *  arc
 *  bs
 *  tb
 */
package mods.provoda.entity;

public class EntityPropulsionFX
extends tb {
    private int ticksInAir = 0;

    public EntityPropulsionFX(aab var1) {
        super(var1);
        this.a(0.1f, 0.1f);
    }

    public EntityPropulsionFX(aab var1, double var2, double var4, double var6) {
        super(var1);
        this.a(0.5f, 0.5f);
        this.b(var2, var4 += -0.2, var6);
        this.N = 0.0f;
    }

    public EntityPropulsionFX(aab var1, double var2, double var4, double var6, double var8, double var10, double var12) {
        super(var1);
        this.a(0.5f, 0.5f);
        this.b(var2, var4, var6);
        this.h(var8, var10, var12);
        this.N = 0.0f;
    }

    public void h(double var1, double var3, double var5) {
        this.x = var1;
        this.y = var3;
        this.z = var5;
    }

    public int b(float var1) {
        return 1000;
    }

    public float c(float var1) {
        return 1000.0f;
    }

    public boolean a(arc var1) {
        return true;
    }

    public boolean a(double var1) {
        return true;
    }

    public void l_() {
        super.l_();
        ++this.ticksInAir;
        if (this.ticksInAir >= 8 && this.q.s.nextInt(2) == 1 || this.ae) {
            this.w();
        }
        this.v += 0.005;
        this.u += (this.q.s.nextDouble() - 0.5) * 0.07;
        this.v += (this.q.s.nextDouble() - 0.5) * 0.07;
        this.w += (this.q.s.nextDouble() - 0.5) * 0.07;
        this.u += this.x;
        this.v += this.y;
        this.w += this.z;
        this.b(this.u, this.v, this.w);
    }

    public void a(bs var1) {
    }

    public void b(bs var1) {
    }

    protected float g() {
        return 0.0f;
    }

    protected void a(ara var1) {
    }
}
