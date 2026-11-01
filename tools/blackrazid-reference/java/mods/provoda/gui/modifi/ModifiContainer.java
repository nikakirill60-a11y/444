/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lt
 *  mods.provoda.gui.backpack.SlotCustom
 *  mods.provoda.gui.guns.railgun.OpticaSlot
 *  mods.provoda.gui.guns.revolver.BarrelSlot
 *  mods.provoda.gui.guns.revolver.ButtSlot
 *  so
 *  sq
 *  tj
 *  ul
 *  wm
 */
package mods.provoda.gui.modifi;

import mods.provoda.ModBase;
import mods.provoda.gui.backpack.SlotCustom;
import mods.provoda.gui.guns.railgun.OpticaSlot;
import mods.provoda.gui.guns.revolver.BarrelSlot;
import mods.provoda.gui.guns.revolver.ButtSlot;
import mods.provoda.gui.modifi.GlushakSlot;
import mods.provoda.gui.modifi.LaserSlot;
import mods.provoda.gui.modifi.ModifiSlot;

public class ModifiContainer
extends tj {
    public final lt inventory;

    public ModifiContainer(sq var1, so var2, lt var3) {
        this.inventory = var3;
        if (var1.bK.h() != null) {
            if (var1.bK.h().b() == ModBase.Rpk) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                }
            } else if (var1.bK.h().b() == ModBase.Saiga) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 35, 1));
                }
            } else if (var1.bK.h().b() == ModBase.Abzac) {
                for (int j = 0; j < 1; ++j) {
                    this.a(new ModifiSlot(this.inventory, j, 102, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 53, 1));
                }
            } else if (var1.bK.h().b() == ModBase.Uboinik) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a((ul)new BarrelSlot(this.inventory, j + 2, 53, 1));
                }
            } else if (var1.bK.h().b() == ModBase.Ventil) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 53, 1));
                }
            } else if (var1.bK.h().b() == ModBase.Vsv) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 53, 1));
                }
            } else if (var1.bK.h().b() == ModBase.Vyhlop) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                }
            } else if (var1.bK.h().b() == ModBase.Ashot) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 35, 1));
                    this.a((ul)new ButtSlot(this.inventory, j + 3, 140, 72));
                }
            } else if (var1.bK.h().b() == ModBase.Ubludok) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 53, 1));
                    this.a((ul)new ButtSlot(this.inventory, j + 3, 140, 72));
                }
            } else if (var1.bK.h().b() == ModBase.AK2012) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 35, 1));
                }
            } else if (var1.bK.h().b() == ModBase.AK) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 35, 1));
                }
            } else if (var1.bK.h().b() == ModBase.AKSU) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                }
            } else if (var1.bK.h().b() == ModBase.Duplet) {
                for (int j = 0; j < 1; ++j) {
                    this.a(new LaserSlot(this.inventory, j, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 1, 35, 1));
                    this.a((ul)new ButtSlot(this.inventory, j + 2, 137, 1));
                    this.a((ul)new ButtSlot(this.inventory, j + 3, 156, 1));
                }
            } else if (var1.bK.h().b() == ModBase.Gatling) {
                for (int j = 0; j < 1; ++j) {
                    this.a(new GlushakSlot(this.inventory, j, 8, 49));
                    this.a((ul)new ButtSlot(this.inventory, j + 1, 130, 64));
                    this.a(new ModifiSlot(this.inventory, j + 2, 152, 1));
                }
            } else if (var1.bK.h().b() == ModBase.Helsing) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                }
            } else if (var1.bK.h().b() == ModBase.Tihar) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                }
            } else if (var1.bK.h().b() == ModBase.Podonok) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 35, 1));
                    this.a((ul)new ButtSlot(this.inventory, j + 3, 140, 75));
                    this.a(new ModifiSlot(this.inventory, j + 4, 90, 1));
                }
            } else if (var1.bK.h().b() == ModBase.Preved) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 35, 1));
                }
            } else if (var1.bK.h().b() == ModBase.Railgun) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new ModifiSlot(this.inventory, j + 2, 104, 49));
                }
            } else if (var1.bK.h().b() == ModBase.Revolver) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 140, 1));
                    this.a(new LaserSlot(this.inventory, j + 1, 8, 49));
                    this.a(new GlushakSlot(this.inventory, j + 2, 78, 1));
                    this.a((ul)new ButtSlot(this.inventory, j + 3, 140, 72));
                }
            } else if (var1.bK.h().b() == ModBase.AKK) {
                for (int j = 0; j < 1; ++j) {
                    this.a((ul)new OpticaSlot(this.inventory, j, 99, 10));
                    this.a(new LaserSlot(this.inventory, j + 1, 59, 40));
                    this.a(new GlushakSlot(this.inventory, j + 2, 7, 25));
                }
            }
        }
        this.a(new ul((lt)var2, 0, 62, 135));
        this.a((ul)new SlotCustom((lt)var2, 1, 80, 135));
        this.a((ul)new SlotCustom((lt)var2, 2, 98, 135));
        this.a((ul)new SlotCustom((lt)var2, 3, 62, 117));
        this.a((ul)new SlotCustom((lt)var2, 4, 80, 117));
        this.a((ul)new SlotCustom((lt)var2, 5, 98, 117));
        this.a((ul)new SlotCustom((lt)var2, 6, 62, 99));
        this.a((ul)new SlotCustom((lt)var2, 7, 80, 99));
        this.a((ul)new SlotCustom((lt)var2, 8, 98, 99));
    }

    public wm b(sq par1EntityPlayer, int par2) {
        wm var3 = null;
        ul var4 = (ul)this.c.get(par2);
        if (var4 != null && var4.d()) {
            wm var5 = var4.c();
            var3 = var5.m();
            if (par2 >= 0 && par2 <= 14) {
                return null;
            }
            if (var5.a == 0) {
                var4.c((wm)null);
            } else {
                var4.e();
            }
            if (var5.a == var3.a) {
                return null;
            }
            var4.a(par1EntityPlayer, var5);
        }
        return var3;
    }

    public boolean a(sq var1) {
        return true;
    }
}
