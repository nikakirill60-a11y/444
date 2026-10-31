/*
 * Decompiled with CFR 0.152.
 */
package com.flansmod.common.guns.raytracing;

import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.driveables.EnumDriveablePart;
import com.flansmod.common.guns.raytracing.BulletHit;

public class DriveableHit
extends BulletHit {
    public EntityDriveable driveable;
    public EnumDriveablePart part;

    public DriveableHit(EntityDriveable d, EnumDriveablePart p, float f) {
        super(f);
        this.part = p;
        this.driveable = d;
    }
}

