/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aqp
 *  aqx
 */
package mods.provoda.tileentitys;

public class TileEntityTunnel
extends aqp {
    public aqx getRenderBoundingBox() {
        aqx bb = INFINITE_EXTENT_AABB;
        bb = aqx.a().a((double)(this.l - 4), (double)(this.m - 6), (double)(this.n - 4), (double)(this.l + 4), (double)this.m, (double)(this.n + 4));
        return bb;
    }
}
