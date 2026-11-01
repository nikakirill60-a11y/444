/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  ly
 *  mp
 *  sq
 *  wk
 *  wm
 */
package mods.provoda.items;

import mods.provoda.entity.Entityregina;

public class ItemRegina
extends wk {
    public ItemRegina(int par1) {
        super(par1);
        this.cq = 1;
        this.b("itemRegina");
    }

    public void a(ly iconRegister) {
        this.ct = iconRegister.a("provoda:regina");
    }

    public boolean a(wm item, sq player, aab world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffSet) {
        if (!player.ce.d) {
            --item.a;
        }
        world.a((mp)player, "random.bow", 0.5f, 0.4f / (wk.e.nextFloat() * 0.4f + 0.8f));
        if (!world.I) {
            Entityregina ent = new Entityregina(world);
            float dir = player.A + 180.0f;
            ent.b((double)x + 0.5, (double)y + 1.5, (double)z + 0.5, dir, 0.0f);
            ent.a();
            world.d((mp)ent);
            return true;
        }
        --item.a;
        return true;
    }
}
