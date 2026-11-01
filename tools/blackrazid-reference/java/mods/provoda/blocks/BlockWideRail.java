/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  aak
 *  aif
 *  akz
 *  aqp
 *  ara
 *  mods.provoda.tileentitys.tunnels.TileEntityWideRail
 *  wm
 */
package mods.provoda.blocks;

import java.util.Random;
import mods.provoda.ModBase;
import mods.provoda.tileentitys.tunnels.TileEntityWideRail;

public class BlockWideRail
extends akz {
    public BlockWideRail(int id, aif material) {
        super(id, material);
        this.a(0.0f, 0.0f, 0.0f, 1.0f, 0.05f, 1.0f);
        this.c("blockWideRails");
    }

    public void a(aak par1, int par2, int par3, int par4) {
        TileEntityWideRail tile = (TileEntityWideRail)par1.r(par2, par3, par4);
        if (tile.mId == 2) {
            this.a(0.0f, 0.0f, 0.0f, 1.0f, 1.375f, 1.0f);
        } else {
            this.a(0.0f, 0.0f, 0.0f, 1.0f, 0.05f, 1.0f);
        }
    }

    public wm getPickBlock(ara target, aab world, int x, int y, int z) {
        return new wm(ModBase.itemRails.cp, 1, 0);
    }

    public void a(aab world, int i, int j, int k, int par5, int par6) {
        world.s(i, j, k);
    }

    public int a(int par1, Random par2Random, int par3) {
        return 0;
    }

    public boolean b() {
        return false;
    }

    public int d() {
        return -1;
    }

    public boolean a(aak iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    public boolean c() {
        return false;
    }

    public aqp b(aab world) {
        return new TileEntityWideRail();
    }
}
