/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  a
 *  aab
 *  ly
 *  mp
 *  sq
 *  wk
 *  wm
 */
package mods.provoda.items;

import java.util.List;
import mods.provoda.ModBase;

public class Lamp
extends wk {
    public static boolean Lamp = false;

    public Lamp(int var1) {
        super(var1);
        this.d(1);
        this.a(ModBase.MetroTabAtt);
    }

    public void a(wm i, aab w, mp e, int par4, boolean par5) {
    }

    public void a(ly var1) {
        this.ct = var1.a("provoda:flashlight");
    }

    public void a(wm is, sq player, List list, boolean flag) {
        list.add(a.h + "\u0424\u043e\u043d\u0430\u0440\u0438\u043a \u043f\u043e\u043c\u043e\u0436\u0435\u0442 \u0432\u0430\u043c \u0432 \u0442\u0435\u043c\u043d\u044b\u0445 \u043c\u0435\u0441\u0442\u0430\u0445");
    }
}
