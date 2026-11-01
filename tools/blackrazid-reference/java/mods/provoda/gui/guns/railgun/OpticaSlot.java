/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lt
 *  ul
 *  wk
 *  wm
 */
package mods.provoda.gui.guns.railgun;

import mods.provoda.items.att.Kolimator;
import mods.provoda.items.att.Optica;
import mods.provoda.items.att.OpticaSmall;
import mods.provoda.items.att.TiharOptica;

public class OpticaSlot
extends ul {
    public OpticaSlot(lt var1, int var2, int var3, int var4) {
        super(var1, var2, var3, var4);
    }

    public int a() {
        return 1;
    }

    public boolean a(wm var1) {
        wk var2 = var1 == null ? null : var1.b();
        return var2 != null && (var2 instanceof Optica || var2 instanceof Kolimator || var2 instanceof OpticaSmall || var2 instanceof TiharOptica);
    }
}
