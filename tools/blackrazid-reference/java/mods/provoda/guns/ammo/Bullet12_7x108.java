/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 *  wk
 */
package mods.provoda.guns.ammo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;

public class Bullet12_7x108
extends wk {
    public Bullet12_7x108(int par1) {
        super(par1);
        this.a(ModBase.MetroTabGuns);
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:12.7x108mm");
    }
}
