/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lt
 *  mods.provoda.items.att.AutoFire
 *  mods.provoda.items.att.GatlingGiro
 *  mods.provoda.items.att.GatlingMotor
 *  mods.provoda.items.att.PodonokGas
 *  mods.provoda.items.att.RailgunAkk
 *  mods.provoda.items.att.RailgunKon
 *  ul
 *  wk
 *  wm
 */
package mods.provoda.gui.modifi;

import mods.provoda.items.att.AutoFire;
import mods.provoda.items.att.GatlingGiro;
import mods.provoda.items.att.GatlingMotor;
import mods.provoda.items.att.PodonokGas;
import mods.provoda.items.att.RailgunAkk;
import mods.provoda.items.att.RailgunKon;

public class ModifiSlot
extends ul {
    public ModifiSlot(lt var1, int var2, int var3, int var4) {
        super(var1, var2, var3, var4);
    }

    public int a() {
        return 1;
    }

    public boolean a(wm var1) {
        wk var2 = var1 == null ? null : var1.b();
        return var2 != null && (var2 instanceof AutoFire || var2 instanceof GatlingGiro || var2 instanceof GatlingMotor || var2 instanceof PodonokGas || var2 instanceof RailgunKon || var2 instanceof RailgunAkk);
    }
}
