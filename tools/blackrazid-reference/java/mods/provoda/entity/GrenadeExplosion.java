/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  aat
 *  apa
 *  aqx
 *  arc
 *  kx
 *  mg
 *  mods.provoda.until.ExtendetPlayer
 *  mp
 *  sq
 *  zl
 *  zw
 */
package mods.provoda.entity;

import java.util.HashSet;
import java.util.List;
import mods.provoda.until.ExtendetPlayer;

public class GrenadeExplosion
extends zw {
    private aab worldObj;
    private sq damager;
    private int damage;
    private int field_77289_h = 16;

    public GrenadeExplosion(aab var1, mp var2, double var3, double var5, double var7, float var9, int var10, sq var11) {
        super(var1, var2, var3, var5, var7, var9);
        this.worldObj = var1;
        this.damage = var10;
        this.damager = var11;
    }

    public void a() {
        double var10;
        double var8;
        double var6;
        int var5;
        int var4;
        int var3;
        float var1 = this.g;
        HashSet<aat> var2 = new HashSet<aat>();
        for (var3 = 0; var3 < this.field_77289_h; ++var3) {
            for (var4 = 0; var4 < this.field_77289_h; ++var4) {
                for (var5 = 0; var5 < this.field_77289_h; ++var5) {
                    if (var3 != 0 && var3 != this.field_77289_h - 1 && var4 != 0 && var4 != this.field_77289_h - 1 && var5 != 0 && var5 != this.field_77289_h - 1) continue;
                    double var30 = (float)var3 / ((float)this.field_77289_h - 1.0f) * 2.0f - 1.0f;
                    double var31 = (float)var4 / ((float)this.field_77289_h - 1.0f) * 2.0f - 1.0f;
                    double var32 = (float)var5 / ((float)this.field_77289_h - 1.0f) * 2.0f - 1.0f;
                    double var33 = Math.sqrt(var30 * var30 + var31 * var31 + var32 * var32);
                    var30 /= var33;
                    var31 /= var33;
                    var32 /= var33;
                    var6 = this.c;
                    var8 = this.d;
                    var10 = this.e;
                    float var21 = 0.3f;
                    for (float var35 = this.g * (0.7f + this.worldObj.s.nextFloat() * 0.6f); var35 > 0.0f; var35 -= var21 * 0.75f) {
                        int var36;
                        int var23;
                        int var34 = kx.c((double)var6);
                        int var25 = this.worldObj.a(var34, var23 = kx.c((double)var8), var36 = kx.c((double)var10));
                        if (var25 > 0) {
                            apa var37 = apa.r[var25];
                            float var27 = this.f != null ? this.f.a((zw)this, this.worldObj, var34, var23, var36, var37) : var37.getExplosionResistance(this.f, this.worldObj, var34, var23, var36, this.c, this.d, this.e);
                            var35 -= (var27 + 0.3f) * var21;
                        }
                        if (var35 > 0.0f && (this.f == null || this.f.a((zw)this, this.worldObj, var34, var23, var36, var25, var35))) {
                            var2.add(new aat(var34, var23, var36));
                        }
                        var6 += var30 * (double)var21;
                        var8 += var31 * (double)var21;
                        var10 += var32 * (double)var21;
                    }
                }
            }
        }
        this.h.addAll(var2);
        this.g *= 2.0f;
        var3 = kx.c((double)(this.c - (double)this.g - 1.0));
        var4 = kx.c((double)(this.c + (double)this.g + 1.0));
        var5 = kx.c((double)(this.d - (double)this.g - 1.0));
        int var301 = kx.c((double)(this.d + (double)this.g + 1.0));
        int var13 = kx.c((double)(this.e - (double)this.g - 1.0));
        int var311 = kx.c((double)(this.e + (double)this.g + 1.0));
        List var15 = this.worldObj.b(this.f, aqx.a().a((double)var3, (double)var5, (double)var13, (double)var4, (double)var301, (double)var311));
        arc var321 = this.worldObj.U().a(this.c, this.d, this.e);
        mg var17 = mg.a((sq)this.damager);
        for (int var331 = 0; var331 < var15.size(); ++var331) {
            double var351;
            mp var19 = (mp)var15.get(var331);
            double var341 = var19.f(this.c, this.d, this.e) / (double)this.g;
            if (!(var341 <= 1.0) || (var351 = (double)kx.a((double)((var6 = var19.u - this.c) * var6 + (var8 = var19.v + (double)var19.e() - this.d) * var8 + (var10 = var19.w - this.e) * var10))) == 0.0) continue;
            var6 /= var351;
            var8 /= var351;
            var10 /= var351;
            double var361 = this.worldObj.a(var321, var19.E);
            double var371 = (1.0 - var341) * var361;
            if (var19 instanceof sq) {
                int factionHitEntity;
                int var28 = ExtendetPlayer.get((sq)this.damager).getFaction();
                if (var28 != (factionHitEntity = ExtendetPlayer.get((sq)((sq)var19)).getFaction())) {
                    var19.a(var17, (int)(var371 * (double)this.damage));
                }
            } else {
                var19.a(var17, (int)(var371 * (double)this.damage));
            }
            double var38 = zl.a((mp)var19, (double)var371);
            var19.x += var6 * var38;
            var19.y += var8 * var38;
            var19.z += var10 * var38;
            if (!(var19 instanceof sq)) continue;
            this.b().put((sq)var19, this.worldObj.U().a(var6 * var371, var8 * var371, var10 * var371));
        }
        this.g = var1;
    }

    public void a(boolean var1) {
        this.worldObj.a(this.c, this.d, this.e, "random.explode", 4.0f, (1.0f + (this.worldObj.s.nextFloat() - this.worldObj.s.nextFloat()) * 0.2f) * 0.7f);
    }
}
