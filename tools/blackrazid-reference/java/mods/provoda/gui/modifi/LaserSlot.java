/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lt
 *  mods.provoda.items.att.Laser
 *  ul
 *  wk
 *  wm
 */
package mods.provoda.gui.modifi;

import mods.provoda.items.att.Laser;

public class LaserSlot
extends ul {
    public LaserSlot(lt var1, int var2, int var3, int var4) {
        super(var1, var2, var3, var4);
    }

    public int a() {
        return 1;
    }

    public boolean a(wm var1) {
        wk var2 = var1 == null ? null : var1.b();
        return var2 != null && var2 instanceof Laser;
    }
}
