/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  bbz
 *  bs
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 *  mp
 *  ng
 *  sq
 *  uo
 *  uq
 *  wm
 */
package mods.provoda.items.armor.heavy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;

public class HeavyArmor
extends uo {
    public HeavyArmor(int var1, uq var2, int var3, int var4) {
        super(var1, var2, var3, var4);
        this.e(600);
        this.cq = 1;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:heavy1");
    }

    public wm a(wm var1, aab var2, sq var3) {
        int var4;
        wm var5;
        if (var1.q() == null) {
            var1.d(new bs());
        }
        if (!var3.ag() && (var5 = var3.q(var4 = ng.b((wm)var1) - 1)) == null) {
            var3.c(var4 + 1, var1.m());
            var1.a = 0;
        }
        return var1;
    }

    public String getArmorTexture(wm var1, mp var2, int var3, int var4) {
        return var1.c == ModBase.Heavy.cp ? "/mods/provoda/textures/armor/heavy.png" : null;
    }

    @SideOnly(value=Side.CLIENT)
    public bbz getArmorModel(ng var1, wm var2, int var3) {
        bbz var4 = new bbz();
        if (var2 != null) {
            if (var2.b() instanceof HeavyArmor) {
                int var5 = ((uo)var2.b()).b;
                if (var5 == 0) {
                    var4 = ModBase.proxy.getHeavyModel(0);
                }
                if (var5 == 1) {
                    var4 = ModBase.proxy.getHeavyModel(1);
                }
                if (var5 == 2) {
                    var4 = ModBase.proxy.getHeavyModel(2);
                }
                if (var5 == 3) {
                    var4 = ModBase.proxy.getHeavyModel(3);
                }
            }
            if (var4 != null) {
                var4.c.j = var3 == 1;
                var4.d.j = var3 == 1;
                var4.e.j = var3 == 1 || var3 == 1;
                var4.f.j = var3 == 1;
                var4.g.j = var3 == 1;
                var4.h.j = var3 == 1 || var3 == 1;
                var4.i.j = var3 == 1 || var3 == 1;
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
