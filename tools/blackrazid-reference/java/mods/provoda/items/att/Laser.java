/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ly
 *  wk
 */
package mods.provoda.items.att;

import mods.provoda.ModBase;

public class Laser
extends wk {
    public Laser(int par1) {
        super(par1);
        this.a(ModBase.MetroTabAtt);
    }

    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:laser");
    }
}
