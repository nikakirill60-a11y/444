/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  apa
 *  aqx
 *  ara
 *  arb
 *  arc
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  mp
 *  ng
 *  sq
 *  tb
 */
package mods.provoda.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;

public abstract class EntityGrenade
extends tb {
    private static final float MOTION_FACTOR = 0.999f;
    private static final float ROTATION_FACTOR = 0.999f;
    private static final float ENTITY_SIZE = 0.25f;
    public float xRotation = 0.0f;
    public float yRotation = 0.0f;
    public float zRotation = 0.0f;
    public float xRotationSpeed = 0.0f;
    public float yRotationSpeed = 0.0f;
    public float zRotationSpeed = 0.0f;
    protected boolean collided = false;
    protected double prevY;
    protected int block_explode_radius = 2;
    protected double entity_explode_radius = 3.0;
    private int damage;
    public sq shooter;

    public EntityGrenade(aab var1) {
        super(var1);
        this.xRotationSpeed = ((float)Math.random() - 0.5f) * 10.0f;
        this.yRotationSpeed = (float)Math.random() * 10.0f + 10.0f;
        this.zRotationSpeed = ((float)Math.random() - 0.5f) * 10.0f;
        this.a(0.25f, 0.25f);
        this.P = 0.25f;
    }

    public EntityGrenade(aab var1, sq var2, float var3, int var4) {
        super(var1, (ng)var2);
        this.damage = var4;
        this.shooter = var2;
        this.x *= (double)var3 / 1.5;
        this.y *= (double)var3 / 1.5;
        this.z *= (double)var3 / 1.5;
    }

    protected float g() {
        return 0.05f;
    }

    public void l_() {
        super.l_();
        if (this.q.I) {
            float var1 = this.v == this.prevY ? 0.94905f : 0.999f;
            this.xRotationSpeed *= var1;
            this.yRotationSpeed *= var1;
            this.zRotationSpeed *= var1;
            this.xRotation = (this.xRotation + this.xRotationSpeed) % 360.0f;
            this.yRotation = (this.yRotation + this.yRotationSpeed) % 360.0f;
            this.zRotation = (this.zRotation + this.zRotationSpeed) % 360.0f;
        }
        this.prevY = this.v;
        if (this.ac > 100) {
            this.w();
        }
    }

    protected void explode() {
    }

    public void w() {
        if (!this.M) {
            this.explode();
        }
        super.w();
    }

    protected void a(ara var1) {
        if (var1.a.ordinal() == 0) {
            this.pushOff(var1.b, var1.c, var1.d, var1.e);
        } else {
            this.hitEntity(var1.g);
        }
    }

    protected void hitEntity(mp var1) {
        this.x *= -0.5;
        this.y *= -0.5;
        this.z *= -0.5;
        this.xRotationSpeed *= 0.5f;
        this.yRotationSpeed *= 0.5f;
        this.zRotationSpeed *= 0.5f;
        this.calculateNewImpact();
    }

    protected void pushOff(int var1, int var2, int var3, int var4) {
        boolean var5;
        boolean bl = var5 = Math.abs(this.y) > (double)0.1f;
        if (var4 != 0 && var4 != 1) {
            if (var4 != 2 && var4 != 3) {
                this.x *= -0.5;
                this.y *= (double)0.8f;
                this.z *= 0.5;
            } else {
                this.x *= 0.5;
                this.y *= (double)0.8f;
                this.z *= -0.5;
            }
        } else if (var5) {
            this.x *= (double)0.8f;
            this.y *= -0.5;
            this.z *= (double)0.8f;
        } else {
            this.x *= (double)0.95f;
            this.y = 0.0;
            this.z *= (double)0.95f;
        }
        if (var4 == 1) {
            // empty if block
        }
        this.calculateNewImpact();
    }

    protected void calculateNewImpact() {
        arc var1 = this.q.U().a(this.u, this.v, this.w);
        arc var2 = this.q.U().a(this.u + this.x, this.v + this.y, this.w + this.z);
        ara var3 = this.q.a(var1, var2);
        var1 = this.q.U().a(this.u, this.v, this.w);
        var2 = this.q.U().a(this.u + this.x, this.v + this.y, this.w + this.z);
        if (var3 != null) {
            var2 = this.q.U().a(var3.f.c, var3.f.d, var3.f.e);
        }
        if (!this.q.I) {
            mp var4 = null;
            List var5 = this.q.b((mp)this, this.E.a(this.x, this.y, this.z).b(1.0, 1.0, 1.0));
            double var6 = 0.0;
            ng var8 = this.h();
            for (int var9 = 0; var9 < var5.size(); ++var9) {
                double var14;
                float var11;
                aqx var12;
                ara var13;
                mp var10 = (mp)var5.get(var9);
                if (!var10.K() || var10 == var8 && this.ac < 5 || (var13 = (var12 = var10.E.b((double)(var11 = 0.3f), (double)var11, (double)var11)).a(var1, var2)) == null || !((var14 = var1.d(var13.f)) < var6) && var6 != 0.0) continue;
                var4 = var10;
                var6 = var14;
            }
            if (var4 != null) {
                var3 = new ara(var4);
            }
        }
        if (var3 != null) {
            if (var3.a == arb.a && this.q.a(var3.b, var3.c, var3.d) == apa.bi.cz) {
                this.Z();
            } else {
                this.a(var3);
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void a(double var1, double var3, double var5, float var7, float var8, int var9) {
    }
}
