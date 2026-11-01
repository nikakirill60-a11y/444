/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ly
 *  wk
 */
package mods.provoda.items.att;

import mods.provoda.ModBase;

public class DupletBarrel
extends wk {
    public DupletBarrel(int par1) {
        super(par1);
        this.a(ModBase.MetroTabAtt);
    }

    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:duplet_barrel");
    }
}
