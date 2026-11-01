/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  a
 *  aab
 *  bs
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 *  mp
 *  ng
 *  org.lwjgl.input.Keyboard
 *  sq
 *  wk
 *  wm
 */
package mods.provoda.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mods.provoda.ModBase;
import mods.provoda.until.ExtendetPlayer;
import org.lwjgl.input.Keyboard;

public class Med
extends wk {
    int timer;

    public Med(int var1) {
        super(var1);
        this.d(1);
        this.a(ModBase.MetroTabAtt);
    }

    public wm a(wm var1, aab var2, sq var3) {
        ExtendetPlayer ep;
        if (!var2.I && (ep = ExtendetPlayer.get(var3)).medTimer() <= 0 && var1.q().e("Heal_Left") > -5) {
            var1.q().a("Charge", true);
            var2.a((mp)var3, "med", 1.0f, 1.0f);
            var1.q().a("Heal_Left1", var1.q().e("Heal_Left1") - 1);
            ep.setMedTimer();
        }
        return var1;
    }

    public void a(wm par1ItemStack, aab par2World, mp par3Entity, int par4, boolean par5) {
        if (par1ItemStack.q() == null) {
            par1ItemStack.d(new bs());
        }
        if (par3Entity instanceof sq && ((sq)par3Entity).bK.h() == par1ItemStack) {
            sq p = (sq)par3Entity;
            ExtendetPlayer ep = ExtendetPlayer.get(p);
            if (!par2World.I && ep.medTimer() == 10) {
                p.j(15);
                par1ItemStack.q().a("Charge", false);
                par1ItemStack.q().a("Heal_Left", par1ItemStack.q().e("Heal_Left") - 1);
                if (p.bK.a[13] != null) {
                    wm apt = p.bK.a[13].m();
                    int cur = p.bK.c;
                    if (p.bK.h() != null) {
                        wm itemcur = p.bK.h().m();
                        --p.bK.a[13].a;
                        p.bK.a[13] = itemcur;
                    }
                    p.bK.a[cur] = apt;
                }
            }
            if (!par2World.I) {
                ep.consumeMedTimer();
            }
        }
    }

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly var1) {
        this.ct = var1.a("provoda:med");
    }

    public void a(wm is, sq player, List list, boolean flag) {
        if (is.q() == null) {
            is.d(new bs());
        }
        if (!Keyboard.isKeyDown((int)42)) {
            list.add("<\u041d\u0430\u0436\u043c\u0438\u0442\u0435 " + a.m + "Shift" + a.h + ">");
        } else {
            list.add("\u0428\u043f\u0440\u0438\u0446\u043e\u0432: " + (5 + is.q().e("Heal_Left")));
        }
    }
}
