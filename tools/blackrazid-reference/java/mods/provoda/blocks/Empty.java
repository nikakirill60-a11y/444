/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aif
 *  apa
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 */
package mods.provoda.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;

public class Empty
extends apa {
    public Empty(int par1, aif par2Material) {
        super(par1, aif.e);
        this.a(ModBase.MetroTab);
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.cQ = par1IconRegister.a("provoda:empty");
    }

    public boolean b() {
        return false;
    }

    public boolean c() {
        return false;
    }
}
