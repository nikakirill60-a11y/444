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

import mods.provoda.items.att.AshotButt;
import mods.provoda.items.att.Butt;
import mods.provoda.items.att.DupletButt;
import mods.provoda.items.att.DupletPriklad;
import mods.provoda.items.att.GatlingButt;
import mods.provoda.items.att.PodonokButt;
import mods.provoda.items.att.UbludokButt;

public class ButtSlot
extends ul {
    public ButtSlot(lt var1, int var2, int var3, int var4) {
        super(var1, var2, var3, var4);
    }

    public int a() {
        return 1;
    }

    public boolean a(wm var1) {
        wk var2 = var1 == null ? null : var1.b();
        return var2 != null && (var2 instanceof Butt || var2 instanceof UbludokButt || var2 instanceof AshotButt || var2 instanceof DupletButt || var2 instanceof DupletPriklad || var2 instanceof GatlingButt || var2 instanceof PodonokButt);
    }
}
