/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 *  mg
 *  mods.provoda.until.ExtendetPlayer
 *  mp
 *  ng
 *  sq
 *  wk
 *  wl
 *  wm
 */
package mods.provoda.guns;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.provoda.ModBase;
import mods.provoda.until.ExtendetPlayer;

public class Machete
extends wk {
    private int weaponDamage;

    public Machete(int par1) {
        super(par1);
        this.cq = 1;
        this.e(wl.c.a());
        this.a(ModBase.MetroTabGuns);
        this.weaponDamage = 4;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:machete");
    }

    public boolean onLeftClickEntity(wm stack, sq player, mp entity) {
        mg var17 = mg.a((sq)player);
        if (entity instanceof sq) {
            int factionHitEntity;
            int factionDamager = ExtendetPlayer.get((sq)player).getFaction();
            if (factionDamager == (factionHitEntity = ExtendetPlayer.get((sq)((sq)entity)).getFaction())) {
                return false;
            }
            entity.a(var17, this.weaponDamage);
            return true;
        }
        entity.a(var17, this.weaponDamage);
        return true;
    }

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }

    public boolean a(wm var1, aab var2, int var3, int var4, int var5, int var6, ng var7) {
        return true;
    }

    public boolean onBlockStartBreak(wm var1, int var2, int var3, int var4, sq var5) {
        return true;
    }
}
