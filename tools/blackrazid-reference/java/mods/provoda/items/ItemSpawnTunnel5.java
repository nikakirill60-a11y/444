/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  ara
 *  arb
 *  arc
 *  kx
 *  ly
 *  sq
 *  wk
 *  wm
 */
package mods.provoda.items;

import mods.provoda.ModBase;

public class ItemSpawnTunnel5
extends wk {
    public ItemSpawnTunnel5(int i) {
        super(i);
        this.cq = 1;
        this.a(ModBase.MetroTab);
    }

    public wm a(wm itemstack, aab world, sq entityplayer) {
        float f9;
        float f6;
        double d3;
        float f5;
        float f = 1.0f;
        float f1 = entityplayer.D + (entityplayer.B - entityplayer.D) * f;
        float f2 = entityplayer.C + (entityplayer.A - entityplayer.C) * f;
        double d = entityplayer.r + (entityplayer.u - entityplayer.r) * (double)f;
        double d1 = entityplayer.s + (entityplayer.v - entityplayer.s) * (double)f + 1.62 - (double)entityplayer.N;
        double d2 = entityplayer.t + (entityplayer.w - entityplayer.t) * (double)f;
        arc vec3d = arc.a((double)d, (double)d1, (double)d2);
        float f3 = kx.b((float)(-f2 * 0.01745329f - 3.141593f));
        float f4 = kx.a((float)(-f2 * 0.01745329f - 3.141593f));
        float f7 = f4 * (f5 = -kx.b((float)(-f1 * 0.01745329f)));
        arc vec3d1 = vec3d.c((double)f7 * (d3 = 5000.0), (double)(f6 = kx.a((float)(-f1 * 0.01745329f))) * d3, (double)(f9 = f3 * f5) * d3);
        ara movingobjectposition = world.a(vec3d, vec3d1, false, true);
        if (movingobjectposition == null) {
            return itemstack;
        }
        if (movingobjectposition.a == arb.a) {
            int i = movingobjectposition.b;
            int j = movingobjectposition.c;
            int k = movingobjectposition.d;
            world.c(i, j + 1, k, 3007);
        }
        return itemstack;
    }

    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:tunnel");
    }
}
