/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  bs
 *  com.google.common.io.ByteArrayDataInput
 *  com.google.common.io.ByteArrayDataOutput
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  mp
 *  sq
 */
package mods.provoda.entity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import mods.provoda.entity.EntityObject;
import mods.provoda.mobs.mobs.Spider;

public class EntitySpawnerSpider
extends EntityObject
implements IEntityAdditionalSpawnData {
    private double xPos;
    private double zPos;
    private double yaw;
    public boolean field_70885_d = false;
    public float field_70886_e = 0.0f;
    public float destPos = 0.0f;
    public float field_70884_g;
    public float field_70888_h;
    public float field_70889_i = 1.0f;
    public int timer;

    public EntitySpawnerSpider(aab var1) {
        super(var1);
        this.timer = 4000;
    }

    public EntitySpawnerSpider(aab var1, double var2, double var4, double var6, int var8) {
        super(var1);
        sq var9 = this.findPlayer();
        if (var9 != null) {
            this.a((mp)var9, 180.0f, 180.0f);
            this.A -= 90.0f;
            this.ay = this.A;
            this.owner = var9;
        }
        this.xPos = var2;
        this.zPos = var6;
        this.yaw = this.A;
    }

    public sq findPlayer() {
        sq var1 = this.q.a((mp)this, 16.0);
        return var1 != null && this.n((mp)var1) ? var1 : null;
    }

    public void b(bs var1) {
        super.b(var1);
        var1.a("xPos", this.xPos);
        var1.a("zPos", this.zPos);
        var1.a("Yaw", this.yaw);
    }

    public void a(bs var1) {
        super.a(var1);
        this.xPos = var1.h("xPos");
        this.zPos = var1.h("zPos");
        this.yaw = var1.h("Yaw");
    }

    public void writeSpawnData(ByteArrayDataOutput var1) {
        var1.writeDouble(this.xPos);
        var1.writeDouble(this.zPos);
        var1.writeDouble(this.yaw);
    }

    public void readSpawnData(ByteArrayDataInput var1) {
        this.xPos = var1.readDouble();
        this.zPos = var1.readDouble();
        this.yaw = var1.readDouble();
    }

    @Override
    public void c() {
        super.c();
        this.field_70888_h = this.field_70886_e;
        this.field_70884_g = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.F ? -1 : 4) * 0.3);
        if (this.destPos < 0.0f) {
            this.destPos = 0.0f;
        }
        if (this.destPos > 1.0f) {
            this.destPos = 1.0f;
        }
        if (!this.F && this.field_70889_i < 1.0f) {
            this.field_70889_i = 1.0f;
        }
        this.field_70889_i = (float)((double)this.field_70889_i * 0.9);
        if (!this.F && this.y < 0.0) {
            this.y *= 0.6;
        }
        this.field_70886_e += this.field_70889_i * 2.0f;
        if (!this.h_() && !this.q.I && --this.timer <= 0) {
            Spider var1 = new Spider(this.q);
            var1.b(this.u, this.v + 1.0, this.w, this.A, 0.0f);
            this.q.d((mp)var1);
            Spider var2 = new Spider(this.q);
            var2.b(this.u, this.v + 1.0, this.w, this.A, 0.0f);
            this.q.d((mp)var2);
            this.timer = 4000;
        }
    }
}
