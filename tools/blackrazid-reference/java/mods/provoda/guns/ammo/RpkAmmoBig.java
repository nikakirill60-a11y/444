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
package mods.provoda.guns.ammo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mods.provoda.ModBase;
import org.lwjgl.input.Keyboard;

public class RpkAmmoBig
extends wk {
    int bullet;
    String desk;

    public RpkAmmoBig(int par1, int par2, int par3, String desk) {
        super(par1);
        this.a(ModBase.MetroTabGuns);
        this.e(par3);
        this.d(1);
        this.desk = desk;
        this.bullet = par2;
    }

    public wm onFoodEaten(wm itemstack, aab world, sq entityplayer) {
        return itemstack;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:rpk_ammobig");
    }

    public wm a(wm i, aab w, sq p) {
        boolean yaaay = false;
        if (i.q() == null) {
            i.d(new bs());
        }
        if (i.q().e("Ammo") != this.n()) {
            int j;
            for (j = 0; j < 8; ++j) {
                if (p.bK.a[j] == null || p.bK.a[j].c != this.bullet) continue;
                p.bK.d(this.bullet);
                i.q().a("Ammo", i.q().e("Ammo") + 1);
                yaaay = true;
                break;
            }
            for (j = 16; j < 21; ++j) {
                if (p.bK.a[j] == null || p.bK.a[j].c != this.bullet || yaaay) continue;
                p.bK.d(this.bullet);
                i.q().a("Ammo", i.q().e("Ammo") + 1);
                break;
            }
        }
        return i;
    }

    public void a(wm i, aab w, mp e, int par4, boolean par5) {
        if (i.q() == null) {
            i.d(new bs());
            i.q().a("Ammo", 0);
        }
    }

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }

    public void a(wm is, sq player, List list, boolean flag) {
        if (is.q() == null) {
            is.d(new bs());
        }
        if (!Keyboard.isKeyDown((int)42)) {
            list.add("<\u041d\u0430\u0436\u043c\u0438\u0442\u0435 " + a.m + "Shift" + a.h + ">");
        } else {
            list.add(this.desk);
            list.add("\u041f\u0430\u0442\u0440\u043e\u043d\u043e\u0432: " + is.q().e("Ammo"));
        }
    }
}
