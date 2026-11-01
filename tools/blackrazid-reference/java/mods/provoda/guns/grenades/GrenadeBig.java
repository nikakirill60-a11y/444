/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 *  mp
 *  sq
 *  wk
 *  wm
 */
package mods.provoda.guns.grenades;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;
import mods.provoda.entity.EntityGrenadeBig;

public class GrenadeBig
extends wk {
    public GrenadeBig(int i) {
        super(i);
        this.cq = 5;
        this.a(ModBase.MetroTabGuns);
    }

    public wm a(wm var1, aab var2, sq var3) {
        var3.a(var1, this.c_(var1));
        return var1;
    }

    public int c_(wm var1) {
        return 72000;
    }

    public void a(wm var1, aab var2, sq var3, int var4) {
        if (!var2.I) {
            var2.d((mp)new EntityGrenadeBig(var2, var3, 1.3499999f, 20, 15));
        }
        if (!var3.ce.d) {
            var3.bK.a[var3.bK.c] = null;
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:grenade_low");
    }
}
