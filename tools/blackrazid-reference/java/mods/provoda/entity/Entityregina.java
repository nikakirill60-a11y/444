/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  mods.provoda.entity.EntityDrezinaStock$CouplerType
 */
package mods.provoda.entity;

import mods.provoda.entity.EntityDrezinaBase;
import mods.provoda.entity.EntityDrezinaStock;
import mods.provoda.entity.RotativePoint;

public class Entityregina
extends EntityDrezinaBase {
    public Entityregina(aab par1World) {
        super(par1World);
        this.a(3.0f, 2.0f);
        this.mass = 2.4f;
        this.maxForce = 15.0f;
        this.maxSpeed = 0.3f;
        this.aa = 1.0f;
        this.maxFuel = 6500;
        this.riderPos = new RotativePoint(this, 0.0f, 0.0f, -1.75f);
        this.couplerFront = new RotativePoint(this, 0.0f, 0.0f, 2.375f);
        this.couplerRear = new RotativePoint(this, 0.0f, 0.0f, -2.5f);
        this.walkableMinX = 0.0f;
        this.walkableMinZ = -1.75f;
        this.walkableMaxX = 0.0f;
        this.walkableMaxZ = -1.75f;
        this.hornSound = "whistle_cher";
        this.pistonSound = "piston_cher";
        this.wheelRadius = new float[]{0.3f, 0.5f};
        this.wheelAngle = new float[]{0.0f, 0.0f};
        this.coupler = EntityDrezinaStock.CouplerType.CHER;
        this.a();
    }

    @Override
    public void spawnFx() {
    }

    @Override
    public double W() {
        return 1.75;
    }
}
