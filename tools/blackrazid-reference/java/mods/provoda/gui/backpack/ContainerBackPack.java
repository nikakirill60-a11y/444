/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lt
 *  mods.provoda.gui.backpack.SlotBackPack
 *  mods.provoda.gui.backpack.SlotCustom
 *  so
 *  sq
 *  tj
 *  ul
 *  wm
 */
package mods.provoda.gui.backpack;

import mods.provoda.ModBase;
import mods.provoda.gui.backpack.SlotBackPack;
import mods.provoda.gui.backpack.SlotCustom;

public class ContainerBackPack
extends tj {
    public final lt inventory;
    private static final int INV_START = 1;
    private static final int INV_END = 27;
    private static final int HOTBAR_START = 28;
    private static final int HOTBAR_END = 36;

    public ContainerBackPack(sq var1, so var2, lt var3) {
        this.inventory = var3;
        if (var1.bK.h() != null) {
            if (var1.bK.h().b() == ModBase.BackPackSmall) {
                for (int j = 0; j < 5; ++j) {
                    this.a((ul)new SlotBackPack(var3, j, 44 + j * 18, 63));
                }
            } else if (var1.bK.h().b() == ModBase.BackPackMedium) {
                int j;
                for (j = 0; j < 5; ++j) {
                    this.a((ul)new SlotBackPack(var3, j, 44 + j * 18, 63));
                }
                for (j = 0; j < 5; ++j) {
                    this.a((ul)new SlotBackPack(var3, j + 5, 44 + j * 18, 45));
                }
            } else if (var1.bK.h().b() == ModBase.BackPackLarge) {
                int j;
                for (j = 0; j < 5; ++j) {
                    this.a((ul)new SlotBackPack(var3, j, 44 + j * 18, 63));
                }
                for (j = 0; j < 5; ++j) {
                    this.a((ul)new SlotBackPack(var3, j + 5, 44 + j * 18, 45));
                }
                for (j = 0; j < 5; ++j) {
                    this.a((ul)new SlotBackPack(var3, j + 10, 44 + j * 18, 27));
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
