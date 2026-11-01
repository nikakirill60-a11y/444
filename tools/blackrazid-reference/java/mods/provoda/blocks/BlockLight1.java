/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  aak
 *  aif
 *  akz
 *  aqp
 *  aqx
 *  ly
 *  mods.provoda.until.BlockLightTile
 */
package mods.provoda.blocks;

import java.util.Random;
import mods.provoda.until.BlockLightTile;

public class BlockLight1
extends akz {
    public BlockLight1(int par1) {
        super(par1, aif.a);
        this.a(0.6f);
        this.r();
        this.b(true);
    }

    public void a(aab w, int x, int y, int z, Random par5Random) {
        w.i(x, y, z);
    }

    public boolean c() {
        return false;
    }

    public boolean isAirBlock(aab world, int x, int y, int z) {
        return true;
    }

    public boolean b(aak par1IBlockAccess, int par2, int par3, int par4) {
        return false;
    }

    public boolean b() {
        return false;
    }

    public aqx b(aab par1World, int par2, int par3, int par4) {
        return null;
    }

    public boolean a(int par1, boolean par2) {
        return par2 && par1 == 0;
    }

    public boolean a_(aak par1IBlockAccess, int par2, int par3, int par4, int par5) {
        aif material = par1IBlockAccess.g(par2, par3, par4);
        return material == this.cO ? false : (par5 == 1 ? true : (material == aif.v ? false : super.a_(par1IBlockAccess, par2, par3, par4, par5)));
    }

    public void a(ly par1IconRegister) {
        this.cQ = par1IconRegister.a("provoda:empty");
    }

    public aqp b(aab world) {
        return new BlockLightTile();
    }
}
