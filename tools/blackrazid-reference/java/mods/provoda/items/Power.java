/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  bs
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 *  mods.provoda.until.ExtendetPlayer
 *  mp
 *  ng
 *  sq
 *  wk
 *  wm
 */
package mods.provoda.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;
import mods.provoda.until.ExtendetPlayer;

public class Power
extends wk {
    public Power(int var1) {
        super(var1);
        this.d(1);
        this.a(ModBase.MetroTabAtt);
    }

    public void a(wm par1ItemStack, aab par2World, mp par3Entity, int par4, boolean par5) {
        if (par1ItemStack.q() == null) {
            par1ItemStack.d(new bs());
        }
        if (par3Entity instanceof sq && ((sq)par3Entity).bK.h() == par1ItemStack) {
            sq p = (sq)par3Entity;
            ExtendetPlayer ep = ExtendetPlayer.get((sq)p);
            if (!par2World.I) {
                float en = ep.energyGet();
                par1ItemStack.q().a("Energy", en);
                if (par1ItemStack.q().e("Timer") <= 0 && ep.getShootKeyPressed()) {
                    par1ItemStack.q().a("Charge", true);
                    par2World.a((mp)p, "railgun_air", 1.0f, 1.0f);
                    par1ItemStack.q().a("Timer", 15);
                    ep.energyUp();
                }
                if (par1ItemStack.q().e("Timer") == 10) {
                    par1ItemStack.q().a("Charge", false);
                }
                if (par1ItemStack.q().e("Timer") >= 0) {
                    par1ItemStack.q().a("Timer", par1ItemStack.q().e("Timer") - 1);
                }
            }
        }
    }

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly var1) {
        this.ct = var1.a("provoda:power");
    }
}
