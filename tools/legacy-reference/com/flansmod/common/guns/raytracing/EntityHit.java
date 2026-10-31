/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package com.flansmod.common.guns.raytracing;

import com.flansmod.common.guns.raytracing.BulletHit;
import net.minecraft.entity.Entity;

public class EntityHit
extends BulletHit {
    public Entity entity;

    public EntityHit(Entity e, float f) {
        super(f);
        this.entity = e;
    }
}

