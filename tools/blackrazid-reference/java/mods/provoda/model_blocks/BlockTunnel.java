/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  aif
 *  akz
 *  aqp
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  kx
 *  ly
 *  mods.provoda.ModBase
 *  mods.provoda.tileentitys.TileEntityTunnel
 *  ng
 *  wm
 */
package mods.provoda.model_blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;
import mods.provoda.tileentitys.TileEntityTunnel;

public class BlockTunnel
extends akz {
    public BlockTunnel(int var1) {
        super(var1, aif.c);
        this.a(ModBase.MetroTab);
        this.c("Tunnel");
    }

    public int d() {
        return ModBase.renderModelTunnel;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.cQ = par1IconRegister.a("provoda:tunnel");
    }

    public boolean c() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public void a(aab var1, int var2, int var3, int var4, ng var5, wm var6) {
        int var7 = kx.c((double)((double)(var5.A * 4.0f / 360.0f) + 0.5)) & 3;
        int var8 = var1.h(var2, var3, var4) >> 2;
        ++var7;
        if ((var7 %= 4) == 0) {
            var1.b(var2, var3, var4, 2 | var8 << 2, 2);
            var1.c(var2, var3 - 6, var4 + 3, ModBase.Empty.cz);
            var1.c(var2, var3 - 5, var4 + 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 4, var4 + 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 3, var4 + 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 2, var4 + 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 1, var4 + 3, ModBase.Empty.cz);
            var1.c(var2, var3, var4 + 2, ModBase.Empty.cz);
            var1.c(var2, var3, var4 + 1, ModBase.Empty.cz);
            var1.c(var2, var3, var4 - 1, ModBase.Empty.cz);
            var1.c(var2, var3, var4 - 2, ModBase.Empty.cz);
            var1.c(var2, var3 - 1, var4 - 3, ModBase.Empty.cz);
            var1.c(var2, var3 - 2, var4 - 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 3, var4 - 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 4, var4 - 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 5, var4 - 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 6, var4 - 3, ModBase.Empty.cz);
            var1.c(var2, var3 - 6, var4 - 2, ModBase.Empty.cz);
        }
        if (var7 == 1) {
            var1.b(var2, var3, var4, 3 | var8 << 2, 2);
            var1.c(var2 - 3, var3 - 6, var4, ModBase.Empty.cz);
            var1.c(var2 - 4, var3 - 5, var4, ModBase.Empty.cz);
            var1.c(var2 - 4, var3 - 4, var4, ModBase.Empty.cz);
            var1.c(var2 - 4, var3 - 3, var4, ModBase.Empty.cz);
            var1.c(var2 - 4, var3 - 2, var4, ModBase.Empty.cz);
            var1.c(var2 - 3, var3 - 1, var4, ModBase.Empty.cz);
            var1.c(var2 - 2, var3, var4, ModBase.Empty.cz);
            var1.c(var2 - 1, var3, var4, ModBase.Empty.cz);
            var1.c(var2 + 1, var3, var4, ModBase.Empty.cz);
            var1.c(var2 + 2, var3, var4, ModBase.Empty.cz);
            var1.c(var2 + 3, var3 - 1, var4, ModBase.Empty.cz);
            var1.c(var2 + 4, var3 - 2, var4, ModBase.Empty.cz);
            var1.c(var2 + 4, var3 - 3, var4, ModBase.Empty.cz);
            var1.c(var2 + 4, var3 - 4, var4, ModBase.Empty.cz);
            var1.c(var2 + 4, var3 - 5, var4, ModBase.Empty.cz);
            var1.c(var2 + 3, var3 - 6, var4, ModBase.Empty.cz);
            var1.c(var2 + 2, var3 - 6, var4, ModBase.Empty.cz);
        }
        if (var7 == 2) {
            var1.b(var2, var3, var4, 0 | var8 << 2, 2);
            var1.c(var2, var3 - 6, var4 - 3, ModBase.Empty.cz);
            var1.c(var2, var3 - 5, var4 - 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 4, var4 - 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 3, var4 - 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 2, var4 - 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 1, var4 - 3, ModBase.Empty.cz);
            var1.c(var2, var3, var4 - 2, ModBase.Empty.cz);
            var1.c(var2, var3, var4 - 1, ModBase.Empty.cz);
            var1.c(var2, var3, var4 + 1, ModBase.Empty.cz);
            var1.c(var2, var3, var4 + 2, ModBase.Empty.cz);
            var1.c(var2, var3 - 1, var4 + 3, ModBase.Empty.cz);
            var1.c(var2, var3 - 2, var4 + 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 3, var4 + 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 4, var4 + 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 5, var4 + 4, ModBase.Empty.cz);
            var1.c(var2, var3 - 6, var4 + 3, ModBase.Empty.cz);
            var1.c(var2, var3 - 6, var4 + 2, ModBase.Empty.cz);
        }
        if (var7 == 3) {
            var1.b(var2, var3, var4, 1 | var8 << 2, 2);
            var1.c(var2 + 3, var3 - 6, var4, ModBase.Empty.cz);
            var1.c(var2 + 4, var3 - 5, var4, ModBase.Empty.cz);
            var1.c(var2 + 4, var3 - 4, var4, ModBase.Empty.cz);
            var1.c(var2 + 4, var3 - 3, var4, ModBase.Empty.cz);
            var1.c(var2 + 4, var3 - 2, var4, ModBase.Empty.cz);
            var1.c(var2 + 3, var3 - 1, var4, ModBase.Empty.cz);
            var1.c(var2 + 2, var3, var4, ModBase.Empty.cz);
            var1.c(var2 + 1, var3, var4, ModBase.Empty.cz);
            var1.c(var2 - 1, var3, var4, ModBase.Empty.cz);
            var1.c(var2 - 2, var3, var4, ModBase.Empty.cz);
            var1.c(var2 - 3, var3 - 1, var4, ModBase.Empty.cz);
            var1.c(var2 - 4, var3 - 2, var4, ModBase.Empty.cz);
            var1.c(var2 - 4, var3 - 3, var4, ModBase.Empty.cz);
            var1.c(var2 - 4, var3 - 4, var4, ModBase.Empty.cz);
            var1.c(var2 - 4, var3 - 5, var4, ModBase.Empty.cz);
            var1.c(var2 - 3, var3 - 6, var4, ModBase.Empty.cz);
            var1.c(var2 - 2, var3 - 6, var4, ModBase.Empty.cz);
        }
    }

    public int a(aab var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
        return var9;
    }

    public void g(aab var1, int var2, int var3, int var4, int var5) {
        if (var5 == 0) {
            var1.i(var2, var3 - 6, var4 - 3);
            var1.i(var2, var3 - 5, var4 - 4);
            var1.i(var2, var3 - 4, var4 - 4);
            var1.i(var2, var3 - 3, var4 - 4);
            var1.i(var2, var3 - 2, var4 - 4);
            var1.i(var2, var3 - 1, var4 - 3);
            var1.i(var2, var3, var4 - 2);
            var1.i(var2, var3, var4 - 1);
            var1.i(var2, var3, var4 + 1);
            var1.i(var2, var3, var4 + 2);
            var1.i(var2, var3 - 1, var4 + 3);
            var1.i(var2, var3 - 2, var4 + 4);
            var1.i(var2, var3 - 3, var4 + 4);
            var1.i(var2, var3 - 4, var4 + 4);
            var1.i(var2, var3 - 5, var4 + 4);
            var1.i(var2, var3 - 6, var4 + 3);
            var1.i(var2, var3 - 6, var4 + 2);
        }
        if (var5 == 1) {
            var1.i(var2 + 3, var3 - 6, var4);
            var1.i(var2 + 4, var3 - 5, var4);
            var1.i(var2 + 4, var3 - 4, var4);
            var1.i(var2 + 4, var3 - 3, var4);
            var1.i(var2 + 4, var3 - 2, var4);
            var1.i(var2 + 3, var3 - 1, var4);
            var1.i(var2 + 2, var3, var4);
            var1.i(var2 + 1, var3, var4);
            var1.i(var2 - 1, var3, var4);
            var1.i(var2 - 2, var3, var4);
            var1.i(var2 - 3, var3 - 1, var4);
            var1.i(var2 - 4, var3 - 2, var4);
            var1.i(var2 - 4, var3 - 3, var4);
            var1.i(var2 - 4, var3 - 4, var4);
            var1.i(var2 - 4, var3 - 5, var4);
            var1.i(var2 - 3, var3 - 6, var4);
            var1.i(var2 - 2, var3 - 6, var4);
        }
        if (var5 == 2) {
            var1.i(var2, var3 - 6, var4 + 3);
            var1.i(var2, var3 - 5, var4 + 4);
            var1.i(var2, var3 - 4, var4 + 4);
            var1.i(var2, var3 - 3, var4 + 4);
            var1.i(var2, var3 - 2, var4 + 4);
            var1.i(var2, var3 - 1, var4 + 3);
            var1.i(var2, var3, var4 + 2);
            var1.i(var2, var3, var4 + 1);
            var1.i(var2, var3, var4 - 1);
            var1.i(var2, var3, var4 - 2);
            var1.i(var2, var3 - 1, var4 - 3);
            var1.i(var2, var3 - 2, var4 - 4);
            var1.i(var2, var3 - 3, var4 - 4);
            var1.i(var2, var3 - 4, var4 - 4);
            var1.i(var2, var3 - 5, var4 - 4);
            var1.i(var2, var3 - 6, var4 - 3);
            var1.i(var2, var3 - 6, var4 - 2);
        }
        if (var5 == 3) {
            var1.i(var2 - 3, var3 - 6, var4);
            var1.i(var2 - 4, var3 - 5, var4);
            var1.i(var2 - 4, var3 - 4, var4);
            var1.i(var2 - 4, var3 - 3, var4);
            var1.i(var2 - 4, var3 - 2, var4);
            var1.i(var2 - 3, var3 - 1, var4);
            var1.i(var2 - 2, var3, var4);
            var1.i(var2 - 1, var3, var4);
            var1.i(var2 + 1, var3, var4);
            var1.i(var2 + 2, var3, var4);
            var1.i(var2 + 3, var3 - 1, var4);
            var1.i(var2 + 4, var3 - 2, var4);
            var1.i(var2 + 4, var3 - 3, var4);
            var1.i(var2 + 4, var3 - 4, var4);
            var1.i(var2 + 4, var3 - 5, var4);
            var1.i(var2 + 3, var3 - 6, var4);
            var1.i(var2 + 2, var3 - 6, var4);
        }
    }

    public aqp b(aab var1) {
        return new TileEntityTunnel();
    }
}
