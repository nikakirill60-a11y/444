/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lt
 *  ul
 *  wk
 *  wm
 */
package mods.provoda.gui.guns.revolver;

import mods.provoda.items.att.Barrel;
import mods.provoda.items.att.Glushak;
import mods.provoda.items.att.UboinikBarrel;

public class BarrelSlot
extends ul {
    public BarrelSlot(lt var1, int var2, int var3, int var4) {
        super(var1, var2, var3, var4);
    }

    public int a() {
        return 1;
    }

    public boolean a(wm var1) {
        wk var2 = var1 == null ? null : var1.b();
        return var2 != null && (var2 instanceof Barrel || var2 instanceof UboinikBarrel || var2 instanceof Glushak);
    }
}
