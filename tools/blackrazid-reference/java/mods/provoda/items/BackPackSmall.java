/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  bbz
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 *  mods.provoda.until.PacketHandler
 *  mp
 *  ng
 *  sq
 *  uo
 *  uq
 *  wm
 */
package mods.provoda.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;
import mods.provoda.until.PacketHandler;

public class BackPackSmall
extends uo {
    public BackPackSmall(int var1, uq enummaterial, int var3, int var4) {
        super(var1, enummaterial, var3, var4);
        this.e(150);
        this.cq = 1;
    }

    public wm a(wm par1ItemStack, aab par2World, sq par3EntityPlayer) {
        if (par2World.I) {
            PacketHandler.sendOpenGuiPacket3((int)10);
        }
        return par1ItemStack;
    }

    @SideOnly(value=Side.CLIENT)
    public String getArmorTexture(wm var1, mp var2, int var3, int var4) {
        return var1.c == ModBase.BackPackSmall.cp ? "/mods/provoda/textures/armor/small_back.png" : null;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:back_small");
    }

    @SideOnly(value=Side.CLIENT)
    public bbz getArmorModel(ng var1, wm var2, int var3) {
        bbz var4 = new bbz();
        if (var2 != null) {
            int var5;
            if (var2.b() instanceof BackPackSmall && (var5 = ((uo)var2.b()).b) == 2) {
                var4 = ModBase.proxy.getSmallBackModel(2);
            }
            if (var4 != null) {
                var4.c.j = var3 == 2;
                var4.d.j = var3 == 2;
                var4.e.j = var3 == 2 || var3 == 2;
                var4.f.j = var3 == 2;
                var4.g.j = var3 == 2;
                var4.h.j = var3 == 2;
                var4.i.j = var3 == 2;
                var4.n = var1.ag();
                var4.q = var1.af();
                var4.s = var1.h_();
                int n = var4.m = var1.p(0) != null ? 1 : 0;
                if (var1 instanceof sq) {
                    var4.o = ((sq)var1).bY() > 2;
                }
                return var4;
            }
        }
        return null;
    }
}
