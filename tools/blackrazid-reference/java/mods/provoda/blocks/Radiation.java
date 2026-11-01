/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  aak
 *  aif
 *  apa
 *  aqx
 *  ly
 *  mg
 *  mp
 *  sq
 */
package mods.provoda.blocks;

import mods.provoda.ModBase;

public class Radiation
extends apa {
    int i = 0;

    public Radiation(int var1) {
        super(var1, aif.c);
        this.r();
        this.a(ModBase.MetroTab);
        float var2 = 0.0f;
        float var3 = 0.0f;
        this.a(0.0f + var3, 0.0f + var2, 0.0f + var3, 1.0f + var3, 1.0f + var2, 1.0f + var3);
        this.c("Radiation");
    }

    public boolean c() {
        return false;
    }

    public boolean b(aak var1, int var2, int var3, int var4) {
        return this.cO != aif.i;
    }

    public boolean b() {
        return false;
    }

    public aqx b(aab var1, int var2, int var3, int var4) {
        return null;
    }

    public boolean a(int var1, boolean var2) {
        return ModBase.DrawBlockHighlight ? true : var2 && var1 == 0;
    }

    public boolean a_(aak var1, int var2, int var3, int var4, int var5) {
        aif var6 = var1.g(var2, var3, var4);
        return var6 == this.cO ? false : (var5 == 1 ? true : (var6 == aif.v ? false : super.a_(var1, var2, var3, var4, var5)));
    }

    public void a(aab var1, int var2, int var3, int var4, mp var5) {
        if (var5 instanceof sq) {
            sq var6 = (sq)var5;
            if (!var6.ce.d) {
                if (var6.bK.f(3) != null) {
                    if (var6.bK.f(3).q() != null) {
                        if (var6.bK.f(3).q().n("isGasMask")) {
                            if (var6.bK.f(3).q().e("Time_Left") <= 0) {
                                ++this.i;
                                if (this.i > 200) {
                                    var6.q.a((mp)var6, "in", 1.0f, 1.0f);
                                    var6.a(mg.f, 3);
                                    this.i = 0;
                                }
                            }
                        } else {
                            ++this.i;
                            if (this.i > 200) {
                                var6.q.a((mp)var6, "in", 1.0f, 1.0f);
                                var6.a(mg.f, 3);
                                this.i = 0;
                            }
                        }
                    } else {
                        ++this.i;
                        if (this.i > 200) {
                            var6.q.a((mp)var6, "in", 1.0f, 1.0f);
                            var6.a(mg.f, 3);
                            this.i = 0;
                        }
                    }
                } else if (var6.bK.f(2) != null) {
                    if (var6.bK.f(2).q() != null) {
                        if (var6.bK.f(2).q().n("isGasMask")) {
                            if (var6.bK.f(2).q().e("Time_Left") <= 0) {
                                ++this.i;
                                if (this.i > 200) {
                                    var6.q.a((mp)var6, "in", 1.0f, 1.0f);
                                    var6.a(mg.f, 3);
                                    this.i = 0;
                                }
                            }
                        } else {
                            ++this.i;
                            if (this.i > 200) {
                                var6.q.a((mp)var6, "in", 1.0f, 1.0f);
                                var6.a(mg.f, 3);
                                this.i = 0;
                            }
                        }
                    } else {
                        ++this.i;
                        if (this.i > 200) {
                            var6.q.a((mp)var6, "in", 1.0f, 1.0f);
                            var6.a(mg.f, 3);
                            this.i = 0;
                        }
                    }
                } else {
                    ++this.i;
                    if (this.i > 200) {
                        var6.q.a((mp)var6, "in", 1.0f, 1.0f);
                        var6.a(mg.f, 3);
                        this.i = 0;
                    }
                }
            }
        }
    }

    public void a(ly var1) {
        this.cQ = var1.a("provoda:empty");
    }
}
