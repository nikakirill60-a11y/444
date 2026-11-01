/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  a
 *  aab
 *  bbz
 *  bs
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 *  mp
 *  ng
 *  org.lwjgl.input.Keyboard
 *  sq
 *  uo
 *  uq
 *  wm
 */
package mods.provoda.items.armor.hats;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mods.provoda.ModBase;
import org.lwjgl.input.Keyboard;

public class HeavyHat
extends uo {
    public HeavyHat(int var1, uq var2, int var3, int var4) {
        super(var1, var2, var3, var4);
        this.e(200);
        this.cq = 1;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:hat1");
    }

    public wm a(wm var1, aab var2, sq var3) {
        if (var1.q() == null) {
            var1.d(new bs());
        }
        var1.q().a("isGasMask", true);
        int var4 = ng.b((wm)var1) - 1;
        wm var5 = var3.q(var4);
        if (var5 == null) {
            var3.c(var4 + 1, var1.m());
            var1.a = 0;
        }
        return var1;
    }

    public void a(wm var1, aab var2, mp var3, int var4, boolean var5) {
        if (var1.q() == null) {
            var1.d(new bs());
        }
    }

    public void a(wm var1, sq var2, List var3, boolean var4) {
        if (var1.q() == null) {
            var1.d(new bs());
        }
        if (!Keyboard.isKeyDown((int)42)) {
            var3.add("<Press " + a.m + "Shift" + a.h + ">");
        } else {
            var3.add("Time left " + var1.q().e("Time_Left"));
        }
    }

    public String getArmorTexture(wm var1, mp var2, int var3, int var4) {
        return var1.c == ModBase.HeavyHat.cp ? "/mods/provoda/textures/armor/hat1.png" : null;
    }

    @SideOnly(value=Side.CLIENT)
    public bbz getArmorModel(ng var1, wm var2, int var3) {
        bbz var4 = new bbz();
        if (var2 != null) {
            int var5;
            if (var2.b() instanceof HeavyHat && (var5 = ((uo)var2.b()).b) == 0) {
                var4 = ModBase.proxy.getHeavyHat(0);
            }
            if (var4 != null) {
                var4.c.j = var3 == 0;
                var4.d.j = var3 == 0;
                var4.e.j = var3 == 1 || var3 == 2;
                var4.f.j = var3 == 1;
                var4.g.j = var3 == 1;
                var4.h.j = var3 == 2 || var3 == 3;
                var4.i.j = var3 == 2 || var3 == 3;
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

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }
}
