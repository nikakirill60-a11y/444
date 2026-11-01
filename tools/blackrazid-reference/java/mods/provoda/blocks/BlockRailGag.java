/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  aak
 *  aif
 *  aqp
 *  aqx
 *  ara
 *  mods.provoda.blocks.BlockGag
 *  mp
 *  wm
 */
package mods.provoda.blocks;

import java.util.List;
import java.util.Random;
import mods.provoda.ModBase;
import mods.provoda.blocks.BlockGag;
import mods.provoda.tileentitys.tunnels.TileEntityGag;

public class BlockRailGag
extends BlockGag {
    public BlockRailGag(int id, aif material) {
        super(id, material);
        this.a(0.0f, 0.0f, 0.0f, 1.0f, 0.05f, 1.0f);
        this.c("blockRailGag");
    }

    public void a(aab par1World, int par2, int par3, int par4, aqx par5AxisAlignedBB, List par6List, mp par7Entity) {
        this.a((aak)par1World, par2, par3, par4);
        super.a(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
    }

    public wm getPickBlock(ara target, aab world, int x, int y, int z) {
        return new wm(ModBase.itemRails.cp, 1, 0);
    }

    public boolean c() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public int d() {
        return -1;
    }

    public int a(int par1, Random par2Random, int par3) {
        return 0;
    }

    public aqp b(aab world) {
        return new TileEntityGag();
    }
}
