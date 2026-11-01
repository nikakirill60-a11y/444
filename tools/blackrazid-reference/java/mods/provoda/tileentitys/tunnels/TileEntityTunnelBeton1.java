/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aqp
 *  aqx
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 */
package mods.provoda.tileentitys.tunnels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;

public class TileEntityTunnelBeton1
extends aqp {
    @SideOnly(value=Side.CLIENT)
    public double n() {
        return ModBase.clientType == 0 ? 2048.0 : (ModBase.clientType == 1 ? 1024.0 : 512.0);
    }

    public aqx getRenderBoundingBox() {
        aqx bb = INFINITE_EXTENT_AABB;
        bb = aqx.a().a((double)(this.l - 4), (double)this.m, (double)(this.n - 4), (double)(this.l + 4), (double)(this.m + 8), (double)(this.n + 4));
        return bb;
    }
}
