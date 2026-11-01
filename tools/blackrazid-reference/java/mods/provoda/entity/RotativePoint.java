/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mp
 */
package mods.provoda.entity;

import mods.provoda.entity.EntityDrezinaStock;

public class RotativePoint {
    mp carrier;
    float relX;
    float relY;
    float relZ;
    float posX;
    float posY;
    float posZ;
    float absX;
    float absY;
    float absZ;
    float azimuth;
    float zenith;
    boolean followZenith;
    public boolean followRenderAngle;

    public RotativePoint(mp entity, float relativeX, float relativeY, float relativeZ) {
        if (entity != null) {
            this.carrier = entity;
        }
        this.relX = relativeX;
        this.relY = relativeY;
        this.relZ = relativeZ;
        this.azimuth = 0.0f;
        this.zenith = 0.0f;
        this.followZenith = false;
        this.followRenderAngle = false;
    }

    public void onUpdate() {
        float radius = (float)Math.sqrt(this.relX * this.relX + this.relY * this.relY + this.relZ * this.relZ);
        this.azimuth = (float)Math.atan2(this.relZ, this.relX);
        this.zenith = (float)Math.atan2(this.relY, Math.sqrt(this.relX * this.relX + this.relZ * this.relZ));
        if (this.carrier != null) {
            this.posX = (float)this.carrier.u;
            this.posY = (float)(this.carrier.v + this.carrier.W() - this.carrier.V());
            this.posZ = (float)this.carrier.w;
            this.azimuth = (float)(Math.atan2(this.relZ, this.relX) + Math.toRadians(this.carrier.A));
            if (this.carrier instanceof EntityDrezinaStock && this.followRenderAngle) {
                this.azimuth = (float)(Math.atan2(this.relZ, this.relX) + Math.toRadians(((EntityDrezinaStock)this.carrier).bogieAngle));
            }
            if (this.followZenith) {
                this.zenith = (float)(Math.atan2(this.relY, Math.sqrt(this.relX * this.relX + this.relZ * this.relZ)) + Math.toRadians(this.carrier.B));
            }
        }
        this.absX = (float)((double)this.posX + (double)radius * Math.cos(this.azimuth) * Math.cos(this.zenith));
        this.absY = (float)((double)this.posY + (double)radius * Math.sin(this.zenith));
        this.absZ = (float)((double)this.posZ + (double)radius * Math.sin(this.azimuth) * Math.cos(this.zenith));
    }

    public void setPosition(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public float getX() {
        return this.absX;
    }

    public float getY() {
        return this.absY;
    }

    public float getZ() {
        return this.absZ;
    }

    public float getRelX() {
        return this.relX;
    }

    public float getRelY() {
        return this.relY;
    }

    public float getRelZ() {
        return this.relZ;
    }

    public void setRelX(float rX) {
        this.relX = rX;
    }

    public void setRelY(float rY) {
        this.relY = rY;
    }

    public void setRelZ(float rZ) {
        this.relZ = rZ;
    }

    public void setFollowZenith(boolean bool) {
        this.followZenith = bool;
    }

    public void setFollowRender(boolean bool) {
        this.followRenderAngle = bool;
    }
}
