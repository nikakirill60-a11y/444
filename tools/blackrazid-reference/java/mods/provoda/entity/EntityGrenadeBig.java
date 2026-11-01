/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  mp
 *  sq
 */
package mods.provoda.entity;

import mods.provoda.entity.EntityGrenade;
import mods.provoda.entity.GrenadeExplosion;

public class EntityGrenadeBig
extends EntityGrenade {
    int damage;

    public EntityGrenadeBig(aab var1) {
        super(var1);
    }

    public EntityGrenadeBig(aab var1, sq var2, float var3, int var4, int damage) {
        super(var1, var2, var3, var4);
        this.damage = damage;
    }

    @Override
    protected void explode() {
        if (!this.q.I) {
            GrenadeExplosion var1 = new GrenadeExplosion(this.q, (mp)this, this.u, this.v, this.w, 5.5f, this.damage, this.shooter);
            var1.b = true;
            var1.a();
            var1.a(true);
        } else {
            this.q.a("largeexplode", this.u, this.v, this.w, 1.0, 0.0, 0.0);
        }
    }
}
