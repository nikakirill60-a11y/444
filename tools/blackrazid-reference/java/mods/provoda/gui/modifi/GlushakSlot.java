/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lt
 *  mods.provoda.items.att.AbzacGlushak
 *  mods.provoda.items.att.Barrel
 *  mods.provoda.items.att.DupletBarrel
 *  mods.provoda.items.att.DupletGlushak
 *  mods.provoda.items.att.GatlingBarrel
 *  mods.provoda.items.att.Glushak
 *  mods.provoda.items.att.QuadBarrel
 *  mods.provoda.items.att.UboinikBarrel
 *  mods.provoda.items.att.VentilGlushak
 *  mods.provoda.items.att.VsvGlushak
 *  ul
 *  wk
 *  wm
 */
package mods.provoda.gui.modifi;

import mods.provoda.items.att.AbzacGlushak;
import mods.provoda.items.att.Barrel;
import mods.provoda.items.att.DupletBarrel;
import mods.provoda.items.att.DupletGlushak;
import mods.provoda.items.att.GatlingBarrel;
import mods.provoda.items.att.Glushak;
import mods.provoda.items.att.QuadBarrel;
import mods.provoda.items.att.UboinikBarrel;
import mods.provoda.items.att.VentilGlushak;
import mods.provoda.items.att.VsvGlushak;

public class GlushakSlot
extends ul {
    public GlushakSlot(lt var1, int var2, int var3, int var4) {
        super(var1, var2, var3, var4);
    }

    public int a() {
        return 1;
    }

    public boolean a(wm var1) {
        wk var2 = var1 == null ? null : var1.b();
        return var2 != null && (var2 instanceof Glushak || var2 instanceof AbzacGlushak || var2 instanceof VentilGlushak || var2 instanceof VsvGlushak || var2 instanceof UboinikBarrel || var2 instanceof DupletGlushak || var2 instanceof QuadBarrel || var2 instanceof DupletBarrel || var2 instanceof GatlingBarrel || var2 instanceof Barrel);
    }
}
