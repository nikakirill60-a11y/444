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
package mods.provoda.items.armor.him;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mods.provoda.ModBase;
import org.lwjgl.input.Keyboard;

public class HimArmor
extends uo {
    public HimArmor(int var1, uq var2, int var3, int var4) {
        super(var1, var2, var3, var4);
        this.e(300);
        this.cq = 1;
    }

    public wm a(wm var1, aab var2, sq var3) {
        if (var1.q() == null) {
            var1.d(new bs());
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
            var3.add("\u0424\u0438\u043b\u044c\u0442\u0440\u0430 \u0445\u0432\u0430\u0442\u0438\u0442 \u043d\u0430 " + var1.q().e("Time_Left") + " \u0441\u0435\u043a\u0443\u043d\u0434");
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:himza");
    }

    public String getArmorTexture(wm var1, mp var2, int var3, int var4) {
        return var1.c == ModBase.Himza.cp ? "/mods/provoda/textures/armor/himza.png" : null;
    }

    @SideOnly(value=Side.CLIENT)
    public bbz getArmorModel(ng var1, wm var2, int var3) {
        bbz var4 = new bbz();
        if (var2 != null) {
            if (var2.b() instanceof HimArmor) {
                int var5 = ((uo)var2.b()).b;
                if (var5 == 0) {
                    var4 = ModBase.proxy.getHimModel(0);
                }
                if (var5 == 1) {
                    var4 = ModBase.proxy.getHimModel(1);
                }
                if (var5 == 2) {
                    var4 = ModBase.proxy.getHimModel(2);
                }
                if (var5 == 3) {
                    var4 = ModBase.proxy.getHimModel(3);
                }
            }
            if (var4 != null) {
                var4.c.j = var3 == 1;
                var4.d.j = var3 == 1;
                var4.e.j = var3 == 1 || var3 == 2;
                var4.f.j = var3 == 1;
                var4.g.j = var3 == 1;
                var4.h.j = var3 == 1 || var3 == 3;
                var4.i.j = var3 == 1 || var3 == 3;
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
