/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  aqx
 *  ara
 *  arc
 *  bs
 *  kx
 *  mg
 *  mods.provoda.entity.EntityPropulsionFX
 *  mp
 *  ng
 */
package mods.provoda.entity;

import java.util.Iterator;
import java.util.List;
import mods.provoda.entity.EntityPropulsionFX;

public class EntityFlameBall
extends mp {
    private int xTile;
    private int yTile;
    private int zTile;
    private aab world;
    private int timer = 0;
    private int inTile;
    private int inData;
    private boolean inGround;
    public mp shootingEntity;
    private int ticksInAir;
    private double damage;

    public EntityFlameBall(aab var1) {
        super(var1);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.world = var1;
        this.inTile = 0;
        this.inData = 0;
        this.inGround = false;
        this.ticksInAir = 0;
        this.damage = 2.0;
        this.a(0.1f, 0.1f);
    }

    public EntityFlameBall(aab var1, double var2, double var4, double var6) {
        super(var1);
        this.world = var1;
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.inTile = 0;
        this.inData = 0;
        this.inGround = false;
        this.ticksInAir = 0;
        this.damage = 2.0;
        this.a(0.5f, 0.5f);
        this.b(var2, var4, var6);
        this.N = 0.0f;
    }

    public EntityFlameBall(aab var1, ng var2, ng var3, float var4, float var5) {
        super(var1);
        this.world = var1;
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.inTile = 0;
        this.inData = 0;
        this.inGround = false;
        this.ticksInAir = 0;
        this.damage = 2.0;
        this.shootingEntity = var2;
        this.v = var2.v + (double)var2.e() - 0.1;
        double var6 = var3.u - var2.u;
        double var8 = var3.v + (double)var3.e() - 0.7 - this.v;
        double var10 = var3.w - var2.w;
        double var12 = kx.a((double)(var6 * var6 + var10 * var10));
        if (var12 >= 1.0E-7) {
            float var14 = (float)(Math.atan2(var10, var6) * 180.0 / Math.PI) - 90.0f;
            float var15 = (float)(-(Math.atan2(var8, var12) * 180.0 / Math.PI));
            double var16 = var6 / var12;
            double var18 = var10 / var12;
            this.b(var2.u + var16, this.v, var2.w + var18, var14, var15);
            this.N = 0.0f;
            float var20 = (float)var12 * 0.2f;
            this.h(var6, var8 + (double)var20, var10);
        }
    }

    public EntityFlameBall(aab var1, ng var2, float var3) {
        super(var1);
        this.world = var1;
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.inTile = 0;
        this.inData = 0;
        this.inGround = false;
        this.ticksInAir = 0;
        this.damage = 2.0;
        this.shootingEntity = var2;
        this.a(0.5f, 0.5f);
        this.b(var2.u, var2.v + (double)var2.e(), var2.w, var2.A, var2.B);
        this.u -= (double)(kx.b((float)(this.A / 180.0f * (float)Math.PI)) * 0.16f);
        this.v -= (double)0.1f;
        this.w -= (double)(kx.a((float)(this.A / 180.0f * (float)Math.PI)) * 0.16f);
        this.b(this.u, this.v, this.w);
        this.N = 0.0f;
        this.x = -kx.a((float)(this.A / 180.0f * (float)Math.PI)) * kx.b((float)(this.B / 180.0f * (float)Math.PI));
        this.z = kx.b((float)(this.A / 180.0f * (float)Math.PI)) * kx.b((float)(this.B / 180.0f * (float)Math.PI));
        this.y = -kx.a((float)(this.B / 180.0f * (float)Math.PI));
        this.h(this.x, this.y, this.z);
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

    public EntityFlameBall(aab var1, int var2, int var3, int var4, int var5, int var6, int var7) {
        super(var1);
        this.world = var1;
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.inTile = 0;
        this.inData = 0;
        this.inGround = false;
        this.ticksInAir = 0;
        this.damage = 2.0;
        this.a(0.5f, 0.5f);
        this.b(var2, var3, var4);
        this.x = var5;
        this.y = var6;
        this.z = var7;
        this.N = 0.0f;
    }

    protected void a() {
    }

    public void h(double var1, double var3, double var5) {
        this.x = var1;
        this.y = var3;
        this.z = var5;
        if (this.D == 0.0f && this.C == 0.0f) {
            float var7 = kx.a((double)(var1 * var1 + var5 * var5));
            this.C = this.A = (float)(Math.atan2(var1, var5) * 180.0 / Math.PI);
            this.D = this.B = (float)(Math.atan2(var3, var7) * 180.0 / Math.PI);
            this.D = this.B * 2.0f;
            this.C = this.A;
            this.b(this.u, this.v, this.w, this.A, this.B);
        }
    }

    public void l_() {
        float var10;
        int var1;
        this.U = this.u;
        this.V = this.v;
        this.W = this.w;
        super.l_();
        if (this.ticksInAir > 0 && !this.ae) {
            for (var1 = 0; var1 < 2; ++var1) {
                if (this.q.I) continue;
                EntityPropulsionFX var15 = new EntityPropulsionFX(this.q, this.u, this.v, this.w);
                this.q.d((mp)var15);
            }
        }
        if (this.ticksInAir > 8) {
            this.w();
        }
        if (this.inGround) {
            var1 = this.q.a(this.xTile, this.yTile, this.zTile);
            this.x *= (double)(this.ab.nextFloat() * 0.2f);
            this.y *= (double)(this.ab.nextFloat() * 0.2f);
            this.z *= (double)(this.ab.nextFloat() * 0.2f);
            this.ticksInAir = 0;
        } else {
            ++this.ticksInAir;
        }
        arc var161 = this.q.U().a(this.u, this.v, this.w);
        arc var16 = this.q.U().a(this.u + this.x, this.v + this.y, this.w + this.z);
        ara var3 = this.q.a(var161, var16);
        var161 = this.q.U().a(this.u, this.v, this.w);
        var16 = this.q.U().a(this.u + this.x, this.v + this.y, this.w + this.z);
        if (var3 != null) {
            var16 = this.q.U().a(var3.f.c, var3.f.d, var3.f.e);
        }
        mp var4 = null;
        List var5 = this.q.b((mp)this, this.E.a(this.x, this.y, this.z).b(1.0, 1.0, 1.0));
        double var6 = 0.0;
        Iterator var8 = var5.iterator();
        if (!this.q.I) {
            while (var8.hasNext()) {
                double var13;
                aqx var18;
                ara var12;
                mp var17 = (mp)var8.next();
                if (!var17.K() || var17 == this.shootingEntity && this.ticksInAir < 5 || (var12 = (var18 = var17.E.b((double)(var10 = 0.3f), (double)var10, (double)var10)).a(var161, var16)) == null || !((var13 = var161.d(var12.f)) < var6) && var6 != 0.0) continue;
                var4 = var17;
                var6 = var13;
            }
        }
        if (var4 != null) {
            var3 = new ara(var4);
        }
        if (var3 != null) {
            this.fire();
            this.w();
            if (var3.g != null) {
                var3.g.d(3);
                var3.g.a(mg.g, 4);
            }
        }
        this.u += this.x;
        this.v += this.y;
        this.w += this.z;
        float var171 = kx.a((double)(this.x * this.x + this.z * this.z));
        this.A = (float)(Math.atan2(this.x, this.z) * 180.0 / Math.PI);
        this.B = (float)(Math.atan2(this.y, var171) * 180.0 / Math.PI);
        while (this.B - this.D < -180.0f) {
            this.D -= 360.0f;
        }
        while (this.B - this.D >= 180.0f) {
            this.D += 360.0f;
        }
        while (this.A - this.C < -180.0f) {
            this.C -= 360.0f;
        }
        while (this.A - this.C >= 180.0f) {
            this.C += 360.0f;
        }
        this.B = this.D + (this.B - this.D) * 0.2f;
        this.A = this.C + (this.A - this.C) * 0.2f;
        var10 = 0.99f;
        float var181 = 0.01f;
        if (this.G()) {
            this.w();
        }
        this.x *= (double)var10;
        this.y *= (double)var10;
        this.z *= (double)var10;
        this.y -= (double)var181;
        this.b(this.u, this.v, this.w);
    }

    public void b(bs var1) {
        var1.a("xTile", (short)this.xTile);
        var1.a("yTile", (short)this.yTile);
        var1.a("zTile", (short)this.zTile);
        var1.a("inTile", (byte)this.inTile);
        var1.a("inData", (byte)this.inData);
        var1.a("inGround", (byte)(this.inGround ? 1 : 0));
        var1.a("damage", this.damage);
    }

    public void a(bs var1) {
        this.xTile = var1.d("xTile");
        this.yTile = var1.d("yTile");
        this.zTile = var1.d("zTile");
        this.inTile = var1.c("inTile") & 0xFF;
        this.inData = var1.c("inData") & 0xFF;
        boolean bl = this.inGround = var1.c("inGround") == 1;
        if (var1.b("damage")) {
            this.damage = var1.h("damage");
        }
    }

    public float Q() {
        return 0.0f;
    }

    public boolean ap() {
        return false;
    }

    private void fire() {
    }
}
