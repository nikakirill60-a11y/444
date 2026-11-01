/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bbz
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
package mods.provoda.items.armor.normal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;

public class Normal
extends uo {
    public Normal(int var1, uq var2, int var3, int var4) {
        super(var1, var2, var3, var4);
        this.e(500);
        this.cq = 1;
    }

    public String getArmorTexture(wm var1, mp var2, int var3, int var4) {
        return var1.c == ModBase.Normal.cp ? "/mods/provoda/textures/armor/normal.png" : null;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:normal");
    }

    @SideOnly(value=Side.CLIENT)
    public bbz getArmorModel(ng var1, wm var2, int var3) {
        bbz var4 = new bbz();
        if (var2 != null) {
            int var5;
            if (var2.b() instanceof Normal && (var5 = ((uo)var2.b()).b) == 1) {
                var4 = ModBase.proxy.getNormalModel(1);
            }
            if (var4 != null) {
                var4.c.j = var3 == 1;
                var4.d.j = var3 == 1;
                var4.e.j = var3 == 1 || var3 == 1;
                var4.f.j = var3 == 1;
                var4.g.j = var3 == 1;
                var4.h.j = var3 == 1;
                var4.i.j = var3 == 1;
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
