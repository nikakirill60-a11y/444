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

public class Flyashka
extends wk {
    int timer;

    public Flyashka(int var1) {
        super(var1);
        this.d(1);
        this.a(ModBase.MetroTabAtt);
    }

    public wm a(wm var1, aab var2, sq var3) {
        ExtendetPlayer ep;
        int water;
        if (var1.q().e("Timer") <= 0 && var1.q().e("Water") > -10 && (water = (ep = ExtendetPlayer.get(var3)).thirst()) < 100) {
            var1.q().a("Charge", true);
            var2.a((mp)var3, "flyashka", 1.0f, 1.0f);
            var1.q().a("Timer", 40);
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
            if (par1ItemStack.q().e("Timer") == 10) {
                par1ItemStack.q().a("Water", par1ItemStack.q().e("Water") - 1);
                par1ItemStack.q().a("Charge", false);
                ep.thirstUp();
            }
            if (par1ItemStack.q().e("Timer") >= 0) {
                par1ItemStack.q().a("Timer", par1ItemStack.q().e("Timer") - 1);
            }
            if (par2World.I && par1ItemStack.q().n("Charge")) {
                p.B -= 1.0f;
            }
        }
    }

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly var1) {
        this.ct = var1.a("provoda:flyashka");
    }

    public void a(wm is, sq player, List list, boolean flag) {
        if (is.q() == null) {
            is.d(new bs());
        }
        list.add(a.h + "\u0412\u043e\u0441\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u0435\u0442 \u0436\u0430\u0436\u0434\u0443");
        if (!Keyboard.isKeyDown((int)42)) {
            list.add("<\u041d\u0430\u0436\u043c\u0438\u0442\u0435 " + a.m + "Shift" + a.h + ">");
        } else {
            list.add("\u0412\u043e\u0434\u044b \u043e\u0441\u0442\u0430\u043b\u043e\u0441\u044c: " + (10 + is.q().e("Water")));
        }
    }
}
