/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  arc
 *  kx
 *  mg
 *  mp
 *  ng
 *  qw
 *  qx
 *  sb
 */
package mods.provoda.mobs.mobs;

import java.util.List;
import java.util.Random;

public class Demon
extends sb
implements qw {
    public double targetX;
    public double targetY;
    public double targetZ;
    public qx dragonPartHead = new qx((qw)this, "head", 6.0f, 6.0f);
    public double[][] ringBuffer = new double[64][3];
    public int ringBufferIndex = -1;
    public float prevAnimTime = 0.0f;
    public float animTime = 0.0f;
    public boolean forceNewTarget = false;
    public boolean slowed = false;
    private mp target;
    public int deathTicks = 0;

    public Demon(aab var1) {
        super(var1);
        this.b(this.aW());
        this.a(8.0f, 8.0f);
        this.Z = false;
        this.ag = true;
        this.targetY = 100.0;
        this.am = true;
    }

    public int aW() {
        return 800;
    }

    public double[] getMovementOffsets(int par1, float par2) {
        if (this.aS <= 0) {
            par2 = 0.0f;
        }
        par2 = 1.0f - par2;
        int j = this.ringBufferIndex - par1 * 1 & 0x3F;
        int k = this.ringBufferIndex - par1 * 1 - 1 & 0x3F;
        double[] adouble = new double[3];
        double d0 = this.ringBuffer[j][0];
        double d1 = kx.g((double)(this.ringBuffer[k][0] - d0));
        adouble[0] = d0 + d1 * (double)par2;
        d0 = this.ringBuffer[j][1];
        d1 = this.ringBuffer[k][1] - d0;
        adouble[1] = d0 + d1 * (double)par2;
        adouble[2] = this.ringBuffer[j][2] + (this.ringBuffer[k][2] - this.ringBuffer[j][2]) * (double)par2;
        return adouble;
    }

    public void c() {
        float f = kx.b((float)(this.animTime * (float)Math.PI * 2.0f));
        float f1 = kx.b((float)(this.prevAnimTime * (float)Math.PI * 2.0f));
        if (f1 <= -0.3f && f >= -0.3f) {
            this.q.a(this.u, this.v, this.w, "mob.enderdragon.wings", 5.0f, 0.8f + this.ab.nextFloat() * 0.3f, false);
        }
        this.prevAnimTime = this.animTime;
        if (this.aS <= 0) {
            f = (this.ab.nextFloat() - 0.5f) * 8.0f;
            f1 = (this.ab.nextFloat() - 0.5f) * 4.0f;
            float f2 = (this.ab.nextFloat() - 0.5f) * 8.0f;
        } else {
            float f18;
            float f16;
            float f15;
            float var33;
            float f3;
            double d2;
            double var27;
            double d3;
            f = 0.2f / (kx.a((double)(this.x * this.x + this.z * this.z)) * 10.0f + 1.0f);
            this.animTime = this.slowed ? (this.animTime += f * 0.5f) : (this.animTime += (f *= (float)Math.pow(2.0, this.y)));
            this.A = kx.g((float)this.A);
            if (this.ringBufferIndex < 0) {
                for (int d0 = 0; d0 < this.ringBuffer.length; ++d0) {
                    this.ringBuffer[d0][0] = this.A;
                    this.ringBuffer[d0][1] = this.v;
                }
            }
            if (++this.ringBufferIndex == this.ringBuffer.length) {
                this.ringBufferIndex = 0;
            }
            this.ringBuffer[this.ringBufferIndex][0] = this.A;
            this.ringBuffer[this.ringBufferIndex][1] = this.v;
            if (this.q.I) {
                if (this.bu > 0) {
                    d3 = this.u + (this.bv - this.u) / (double)this.bu;
                    var27 = this.v + (this.bw - this.v) / (double)this.bu;
                    double d1 = this.w + (this.bx - this.w) / (double)this.bu;
                    d2 = kx.g((double)(this.by - (double)this.A));
                    this.A = (float)((double)this.A + d2 / (double)this.bu);
                    this.B = (float)((double)this.B + (this.bz - (double)this.B) / (double)this.bu);
                    --this.bu;
                    this.b(d3, var27, d1);
                    this.b(this.A, this.B);
                }
            } else {
                double f11;
                double f9;
                d3 = this.targetX - this.u;
                var27 = this.targetY - this.v;
                double d1 = this.targetZ - this.w;
                d2 = d3 * d3 + var27 * var27 + d1 * d1;
                if (this.target != null) {
                    this.targetX = this.target.u;
                    this.targetZ = this.target.w;
                    f9 = this.targetX - this.u;
                    f11 = this.targetZ - this.w;
                    double adouble = Math.sqrt(f9 * f9 + f11 * f11);
                    double f13 = (double)0.4f + adouble / 80.0 - 1.0;
                    if (f13 > 10.0) {
                        f13 = 10.0;
                    }
                    this.targetY = this.target.E.b + f13;
                } else {
                    this.targetX += this.ab.nextGaussian() * 2.0;
                    this.targetZ += this.ab.nextGaussian() * 2.0;
                }
                if (this.forceNewTarget || d2 < 100.0 || d2 > 22500.0 || this.G || this.H) {
                    this.setNewTarget();
                }
                if ((var27 /= (double)kx.a((double)(d3 * d3 + d1 * d1))) < (double)(-(f3 = 0.6f))) {
                    var27 = -f3;
                }
                if (var27 > (double)f3) {
                    var27 = f3;
                }
                this.y += var27 * (double)0.1f;
                this.A = kx.g((float)this.A);
                f9 = 180.0 - Math.atan2(d3, d1) * 180.0 / Math.PI;
                f11 = kx.g((double)(f9 - (double)this.A));
                if (f11 > 50.0) {
                    f11 = 50.0;
                }
                if (f11 < -50.0) {
                    f11 = -50.0;
                }
                arc var30 = this.q.U().a(this.targetX - this.u, this.targetY - this.v, this.targetZ - this.w).a();
                arc adouble1 = this.q.U().a((double)kx.a((float)(this.A * (float)Math.PI / 180.0f)), this.y, (double)(-kx.b((float)(this.A * (float)Math.PI / 180.0f)))).a();
                var33 = (float)(adouble1.b(var30) + 0.5) / 1.5f;
                if (var33 < 0.0f) {
                    var33 = 0.0f;
                }
                this.bF *= 0.8f;
                float j = kx.a((double)(this.x * this.x + this.z * this.z)) * 1.0f + 1.0f;
                double adouble2 = Math.sqrt(this.x * this.x + this.z * this.z) * 1.0 + 1.0;
                if (adouble2 > 40.0) {
                    adouble2 = 40.0;
                }
                this.bF = (float)((double)this.bF + f11 * ((double)0.7f / adouble2 / (double)j));
                this.A += this.bF * 0.1f;
                f15 = (float)(2.0 / (adouble2 + 1.0));
                f16 = 0.06f;
                this.a(0.0f, -1.0f, f16 * (var33 * f15 + (1.0f - f15)));
                if (this.slowed) {
                    this.d(this.x * (double)0.8f, this.y * (double)0.8f, this.z * (double)0.8f);
                } else {
                    this.d(this.x, this.y, this.z);
                }
                arc f17 = this.q.U().a(this.x, this.y, this.z).a();
                f18 = (float)(f17.b(adouble1) + 1.0) / 2.0f;
                f18 = 0.8f + 0.15f * f18;
                this.x *= (double)f18;
                this.z *= (double)f18;
                this.y *= (double)0.91f;
            }
            this.ay = this.A;
            f1 = (float)(this.getMovementOffsets(5, 1.0f)[1] - this.getMovementOffsets(10, 1.0f)[1]) * 10.0f / 180.0f * (float)Math.PI;
            float f2 = kx.b((float)f1);
            float var28 = -kx.a((float)f1);
            float f10 = this.A * (float)Math.PI / 180.0f;
            float var29 = kx.a((float)f10);
            float f12 = kx.b((float)f10);
            if (!this.q.I && this.aW == 0) {
                this.aW = 20;
                this.attackEntitiesInList(this.q.b((mp)this, this.dragonPartHead.E.b(1.0, 1.0, 1.0)));
            }
            double[] var31 = this.getMovementOffsets(5, 1.0f);
            double[] var32 = this.getMovementOffsets(0, 1.0f);
            f3 = kx.a((float)(this.A * (float)Math.PI / 180.0f - this.bF * 0.01f));
            var33 = kx.b((float)(this.A * (float)Math.PI / 180.0f - this.bF * 0.01f));
            this.dragonPartHead.l_();
            this.dragonPartHead.b(this.u + (double)(f3 * 5.5f * f2), this.v + (var32[1] - var31[1]) * 1.0 + (double)(var28 * 5.5f), this.w - (double)(var33 * 5.5f * f2), 0.0f, 0.0f);
            for (int var34 = 0; var34 < 3; ++var34) {
                double[] var35 = this.getMovementOffsets(12 + var34 * 2, 1.0f);
                float f14 = this.A * (float)Math.PI / 180.0f + this.simplifyAngle(var35[0] - var31[0]) * (float)Math.PI / 180.0f * 1.0f;
                f15 = kx.a((float)f14);
                f16 = kx.b((float)f14);
                float var36 = 1.5f;
                f18 = (float)(var34 + 1) * 2.0f;
            }
        }
    }

    private void attackEntitiesInList(List par1List) {
        for (int i = 0; i < par1List.size(); ++i) {
            mp entity = (mp)par1List.get(i);
            if (!(entity instanceof ng) || this.ba > 0) continue;
            this.ba = 20;
            entity.a(mg.a((ng)this), 10);
        }
    }

    private void setNewTarget() {
        this.forceNewTarget = false;
        if (this.ab.nextInt(2) == 0 && !this.q.h.isEmpty()) {
            this.target = (mp)this.q.h.get(this.ab.nextInt(this.q.h.size()));
        } else {
            double d2;
            double d1;
            double d0;
            boolean flag = false;
            do {
                this.targetX = 0.0;
                this.targetY = 70.0f + this.ab.nextFloat() * 50.0f;
                this.targetZ = 0.0;
                this.targetX += (double)(this.ab.nextFloat() * 120.0f - 60.0f);
                this.targetZ += (double)(this.ab.nextFloat() * 120.0f - 60.0f);
            } while (!(flag = (d0 = this.u - this.targetX) * d0 + (d1 = this.v - this.targetY) * d1 + (d2 = this.w - this.targetZ) * d2 > 100.0));
            this.target = null;
        }
    }

    private float simplifyAngle(double par1) {
        return (float)kx.g((double)par1);
    }

    protected float ba() {
        return 2.0f;
    }

    public boolean bh() {
        return true;
    }

    public int c(mp var1) {
        return 12;
    }

    protected void bo() {
        super.bo();
    }

    protected String bb() {
        return "demon_alive";
    }

    protected String bc() {
        Random rand = new Random();
        int i = rand.nextInt(4);
        return i == 0 ? "demon_hit" : "demon_hit1";
    }

    protected String bd() {
        return "demon_death";
    }

    public void x() {
        super.x();
        if (this.ae) {
            this.g(300);
        }
    }

    public aab d() {
        return this.q;
    }

    public boolean a(qx entitydragonpart, mg damagesource, int i) {
        return false;
    }
}
