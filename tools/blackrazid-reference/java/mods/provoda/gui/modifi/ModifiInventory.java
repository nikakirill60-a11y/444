/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bs
 *  ca
 *  cf
 *  lt
 *  sq
 *  vb
 *  wm
 */
package mods.provoda.gui.modifi;

public class ModifiInventory
implements lt {
    public static ModifiInventory theMinecraft;
    private String name = "Inventory Item";
    private final wm stack;
    public static final int INV_SIZE = 1;
    wm[] inventory = new wm[5];

    public ModifiInventory(wm var1) {
        theMinecraft = this;
        this.stack = var1;
        if (!this.stack.p()) {
            this.stack.d(new bs());
        }
        this.readFromNBT(this.stack.q());
    }

    public int j_() {
        return this.inventory.length;
    }

    public wm a(int var1) {
        return this.inventory[var1];
    }

    public wm a(int var1, int var2) {
        wm var3 = this.a(var1);
        if (var3 != null) {
            if (var3.a > var2) {
                var3 = var3.a(var2);
                this.k_();
            } else {
                this.a(var1, null);
            }
        }
        return var3;
    }

    public wm b(int var1) {
        wm var2 = this.a(var1);
        this.a(var1, null);
        return var2;
    }

    public void a(int var1, wm var2) {
        this.inventory[var1] = var2;
        if (var2 != null && var2.a > this.d()) {
            var2.a = this.d();
        }
        this.k_();
    }

    public String b() {
        return this.name;
    }

    public boolean c() {
        return this.name.length() > 0;
    }

    public int d() {
        return 64;
    }

    public void k_() {
        for (int var1 = 0; var1 < this.j_(); ++var1) {
            if (this.a(var1) == null || this.a((int)var1).a != 0) continue;
            this.a(var1, null);
        }
        this.writeToNBT(this.stack.q());
    }

    public boolean a(sq var1) {
        return true;
    }

    public void f() {
    }

    public void g() {
    }

    public void readFromNBT(bs var1) {
        ca var2 = var1.m("ItemInventory");
        for (int var3 = 0; var3 < var2.c(); ++var3) {
            bs var4 = (bs)var2.b(var3);
            int var5 = var4.e("Slot");
            if (var5 < 0 || var5 >= this.j_()) continue;
            this.a(var5, wm.a((bs)var4));
        }
    }

    public void writeToNBT(bs var1) {
        ca var2 = new ca();
        for (int var3 = 0; var3 < this.j_(); ++var3) {
            if (this.a(var3) == null) continue;
            bs var4 = new bs();
            var4.a("Slot", var3);
            this.a(var3).b(var4);
            var2.a((cf)var4);
        }
        var1.a("ItemInventory", (cf)var2);
    }

    public boolean b(int var1, wm var2) {
        return !(var2.b() instanceof vb);
    }
}
