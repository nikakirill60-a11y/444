/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  bs
 *  kx
 *  mp
 *  ng
 */
package mods.provoda.entity;

public class EntityPassiveFire
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

    public EntityPassiveFire(aab var1) {
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

    public EntityPassiveFire(aab var1, double var2, double var4, double var6) {
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
        this.a(0.1f, 0.1f);
        this.b(var2, var4, var6);
        this.N = 0.0f;
    }

    public EntityPassiveFire(aab var1, ng var2, ng var3, float var4, float var5) {
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

    public EntityPassiveFire(aab var1, ng var2, float var3) {
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
        this.a(0.1f, 0.1f);
        this.b(var2.u, var2.v + (double)var2.e(), var2.w, var2.A, var2.B);
        this.A = (this.A + 25.0f) % 360.0f;
        this.u -= (double)(kx.b((float)(this.A / 180.0f * (float)Math.PI)) * 0.16f);
        this.v -= 0.2;
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

    public boolean a(double var1) {
        return var1 <= 16.0;
    }

    public EntityPassiveFire(aab var1, int var2, int var3, int var4, int var5, int var6, int var7) {
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
        this.a(0.3f, 0.3f);
        this.b(var2, var3, var4);
        this.x = var5;
        this.y = var6;
        this.z = var7;
        this.N = 0.0f;
    }

    protected void a() {
    }

    public void h(double var1, double var3, double var5) {
        this.x = var1 + ((double)this.q.s.nextFloat() - 0.5) / 50.0;
        this.y = var3 + ((double)this.q.s.nextFloat() - 0.5) / 50.0;
        this.z = var5 + ((double)this.q.s.nextFloat() - 0.5) / 50.0;
    }

    public void l_() {
        this.U = this.u;
        this.V = this.v;
        this.W = this.w;
        super.l_();
        if (this.ticksInAir > 7) {
            this.w();
        }
        this.u += this.x;
        this.v += this.y;
        this.w += this.z;
        float var1 = 0.4f;
        float var2 = -0.02f;
        if (this.G()) {
            this.w();
        }
        this.x *= (double)var1;
        this.y *= (double)var1;
        this.z *= (double)var1;
        this.y -= (double)var2;
        this.b(this.u, this.v, this.w);
        ++this.ticksInAir;
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
}
