/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aqp
 *  aqx
 *  bs
 *  cg
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ei
 *  fn
 */
package mods.provoda.tileentitys.tunnels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;

public class TileEntityWideRail
extends aqp {
    public int mId;
    public boolean activated;

    public void b(bs par1NBTTagCompound) {
        super.b(par1NBTTagCompound);
        par1NBTTagCompound.a("mid", this.mId);
        par1NBTTagCompound.a("act", this.activated);
    }

    public void a(bs par1NBTTagCompound) {
        super.a(par1NBTTagCompound);
        this.mId = par1NBTTagCompound.e("mid");
        this.activated = par1NBTTagCompound.n("act");
    }

    @SideOnly(value=Side.CLIENT)
    public aqx getRenderBoundingBox() {
        aqx bb = INFINITE_EXTENT_AABB;
        return bb;
    }

    @SideOnly(value=Side.CLIENT)
    public double n() {
        return ModBase.clientType == 0 ? 2048.0 : (ModBase.clientType == 1 ? 1024.0 : 512.0);
    }

    public ei m() {
        bs nbtTag = new bs();
        this.b(nbtTag);
        return new fn(this.l, this.m, this.n, 1, nbtTag);
    }

    public void onDataPacket(cg net, fn packet) {
        this.a(packet.e);
    }
}
