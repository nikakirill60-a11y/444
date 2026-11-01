/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aqp
 *  bs
 *  cg
 *  ei
 *  fn
 */
package mods.provoda.tileentitys.tunnels;

public class TileEntityGag
extends aqp {
    public int primary_x;
    public int primary_y;
    public int primary_z;
    public float meta = 0.0f;

    public void b(bs par1NBTTagCompound) {
        super.b(par1NBTTagCompound);
        par1NBTTagCompound.a("primary_x", this.primary_x);
        par1NBTTagCompound.a("primary_y", this.primary_y);
        par1NBTTagCompound.a("primary_z", this.primary_z);
        par1NBTTagCompound.a("meta", this.meta);
    }

    public void a(bs par1NBTTagCompound) {
        super.a(par1NBTTagCompound);
        this.primary_x = par1NBTTagCompound.e("primary_x");
        this.primary_y = par1NBTTagCompound.e("primary_y");
        this.primary_z = par1NBTTagCompound.e("primary_z");
        this.meta = par1NBTTagCompound.g("meta");
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
