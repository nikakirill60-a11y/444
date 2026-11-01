/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  a
 *  aab
 *  bs
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  ly
 *  mp
 *  net.minecraft.client.Minecraft
 *  ng
 *  org.lwjgl.input.Keyboard
 *  rh
 *  sq
 *  wk
 *  wm
 */
package mods.provoda.guns;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mods.provoda.ModBase;
import mods.provoda.entity.EntityBullet;
import mods.provoda.gui.modifi.ModifiGui;
import mods.provoda.gui.modifi.ModifiInventory;
import mods.provoda.until.ExtendetPlayer;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class Gatling
extends wk {
    float timer;
    int damage;
    int modifi_reload;
    public static int otdachaTimer;
    float current_bullet;

    public Gatling(int par1, int ammo, int damage) {
        super(par1);
        this.a(ModBase.MetroTabGuns);
        this.e(0);
        this.d(1);
        this.damage = damage;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:gatling");
    }

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }

    public wm a(wm i, aab w, sq p) {
        if (!w.I && i.q().e("Air") > 0) {
            w.a((mp)p, "gatling_rot", 1.0f, 1.0f);
        }
        return i;
    }

    public void a(wm i, aab w, mp e, int par4, boolean par5) {
        if (i.q() == null) {
            i.d(new bs());
        }
        ModifiInventory revinv = new ModifiInventory(i);
        if (e instanceof sq && ((sq)e).bK.h() == i) {
            wm ammo;
            sq p = (sq)e;
            ExtendetPlayer ep = ExtendetPlayer.get(p);
            if (!(p.ag() || i.q().e("UnloadingTimer") <= 0 || i.q().n("Modifi") || i.q().n("Air_Reload"))) {
                i.q().a("UnloadingTimer", i.q().e("UnloadingTimer") - 1);
                if (i.q().e("UnloadingTimer") == 2) {
                    int slot;
                    for (slot = 0; slot < 8; ++slot) {
                        if (p.bK.a[slot] != null) continue;
                        ammo = new wm(ModBase.GatlingAmmo);
                        ammo.d(new bs());
                        ammo.q().a("Ammo", i.q().e("Bullet_Left"));
                        p.bK.a[slot] = ammo;
                        i.q().a("HasAmmo", false);
                        i.q().a("Bullet_Left", 0);
                        i.q().a("UnloadingTimer", 0);
                        break;
                    }
                    if (i.q().n("HasAmmo")) {
                        for (slot = 16; slot < 21; ++slot) {
                            if (p.bK.a[slot] != null) continue;
                            ammo = new wm(ModBase.GatlingAmmo);
                            ammo.d(new bs());
                            ammo.q().a("Ammo", i.q().e("Bullet_Left"));
                            p.bK.a[slot] = ammo;
                            i.q().a("HasAmmo", false);
                            i.q().a("Bullet_Left", 0);
                            i.q().a("UnloadingTimer", 0);
                            break;
                        }
                    }
                    if (i.q().n("HasAmmo")) {
                        wm var13 = new wm(ModBase.GatlingAmmo);
                        var13.d(new bs());
                        var13.q().a("Ammo", i.q().e("Bullet_Left"));
                        if (!w.I) {
                            w.d((mp)new rh(w, p.u, p.v, p.w, var13));
                        }
                        i.q().a("HasAmmo", false);
                        i.q().a("Bullet_Left", 0);
                        i.q().a("UnloadingTimer", 0);
                    }
                }
            }
            if (i.q().e("LoadingTimer") > 0) {
                i.q().a("LoadingTimer", i.q().e("LoadingTimer") - 1);
            }
            if (i.q().e("Air") > 0 && i.q().e("Bullet_Left") > 0 && ep.getShootKeyPressed() && i.q().e("LoadingTimer") == 0 && i.q().e("UnloadingTimer") == 0 && !i.q().n("Modifi") && ep.getST() <= 0 && !p.ah() && !i.q().n("Air_Reload")) {
                if (w.I && otdachaTimer <= 0) {
                    if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                        if (i.q().e("Air") > 266) {
                            otdachaTimer = 2;
                        } else if (i.q().e("Air") > 133 && i.q().e("Air") < 267) {
                            otdachaTimer = 3;
                        } else if (i.q().e("Air") > 0 && i.q().e("Air") < 134) {
                            otdachaTimer = 4;
                        }
                    } else if (i.q().e("Air") > 266) {
                        otdachaTimer = 3;
                    } else if (i.q().e("Air") > 133 && i.q().e("Air") < 267) {
                        otdachaTimer = 4;
                    } else if (i.q().e("Air") > 0 && i.q().e("Air") < 134) {
                        otdachaTimer = 5;
                    }
                    p.B = revinv.a(1) != null && revinv.a(1).b() == ModBase.GatlingButt && revinv.a(2) != null && revinv.a(2).b() == ModBase.GatlingGiro ? (p.B -= 0.3f) : (revinv.a(2) != null && revinv.a(2).b() == ModBase.GatlingGiro ? (p.B -= 0.4f) : (revinv.a(1) != null && revinv.a(1).b() == ModBase.GatlingButt ? (p.B -= 0.5f) : (p.B -= 0.6f)));
                }
                if (!w.I) {
                    if (w.a((int)p.u, (int)p.v + 1, (int)p.w) == 0) {
                        w.c((int)p.u, (int)p.v + 1, (int)p.w, 4016);
                    }
                    i.q().a("Shoot", true);
                    if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                        if (i.q().e("Air") > 266) {
                            ep.upST(2);
                        } else if (i.q().e("Air") > 133 && i.q().e("Air") < 267) {
                            ep.upST(3);
                        } else if (i.q().e("Air") > 0 && i.q().e("Air") < 134) {
                            ep.upST(4);
                        }
                    } else if (i.q().e("Air") > 266) {
                        ep.upST(3);
                    } else if (i.q().e("Air") > 133 && i.q().e("Air") < 267) {
                        ep.upST(4);
                    } else if (i.q().e("Air") > 0 && i.q().e("Air") < 134) {
                        ep.upST(5);
                    }
                    if (revinv.a(2) == null || revinv.a(2).b() != ModBase.GatlingMotor) {
                        i.q().a("Air", i.q().e("Air") - 1);
                    }
                    w.a((mp)p, "gatling_shoot", 1.0f, 1.0f);
                    w.a((mp)p, "gatling_rot", 1.0f, 1.0f);
                    EntityBullet var14 = new EntityBullet((mp)p, this.damage, 25, 4.0f, p.A, p.B);
                    w.d((mp)var14);
                    i.q().a("Bullet_Left", i.q().e("Bullet_Left") - 1);
                }
            } else if (ep.getST() <= 0 && ep.getShootKeyPressed() && !w.I && !i.q().n("Air_Reload")) {
                w.a((mp)p, "empty", 1.0f, 1.0f);
                ep.upST(7);
            }
            if (ep.getShootKeyPressed() && i.q().e("Bullet_Left") > 0 && i.q().e("Air") > 0) {
                i.q().a("Rot", true);
            } else {
                i.q().a("Rot", false);
            }
            if (!(p.ag() || !ep.getReloadKeyPressed() || i.q().e("UnloadingTimer") != 0 || i.q().e("LoadingTimer") != 0 || p.ah() || i.q().n("Modifi") || i.q().n("Air_Reload"))) {
                if (i.q().n("HasAmmo")) {
                    i.q().a("UnloadingTimer", 30);
                    w.a((mp)p, "gatling_unload", 1.0f, 1.0f);
                } else {
                    int i1;
                    for (i1 = 16; i1 < 21; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].b().cp != ModBase.GatlingAmmo.cp || p.bK.a[i1].q().e("Ammo") < 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "gatling_reload", 1.0f, 1.0f);
                        int s = p.bK.a[i1].q().e("Ammo");
                        i.q().a("Bullet_Left", s);
                        i.q().a("HasAmmo", true);
                        p.bK.a[i1] = null;
                        i.q().a("LoadingTimer", 40);
                        break;
                    }
                    for (i1 = 0; i1 < 8; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].c != ModBase.GatlingAmmo.cp || p.bK.a[i1].q().e("Ammo") < 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "gatling_reload", 1.0f, 1.0f);
                        ammo = p.bK.a[i1];
                        int s = ammo.q().e("Ammo");
                        i.q().a("Bullet_Left", s);
                        i.q().a("HasAmmo", true);
                        p.bK.a[i1] = null;
                        i.q().a("LoadingTimer", 40);
                        break;
                    }
                    for (i1 = 16; i1 < 21; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].b().cp != ModBase.GatlingAmmo.cp || p.bK.a[i1].q().e("Ammo") >= 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "gatling_reload", 1.0f, 1.0f);
                        int s = p.bK.a[i1].q().e("Ammo");
                        i.q().a("Bullet_Left", s);
                        i.q().a("HasAmmo", true);
                        p.bK.a[i1] = null;
                        i.q().a("LoadingTimer", 40);
                        break;
                    }
                    for (i1 = 0; i1 < 8; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].c != ModBase.GatlingAmmo.cp || p.bK.a[i1].q().e("Ammo") >= 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "gatling_reload", 1.0f, 1.0f);
                        ammo = p.bK.a[i1];
                        int s = ammo.q().e("Ammo");
                        i.q().a("Bullet_Left", s);
                        i.q().a("HasAmmo", true);
                        p.bK.a[i1] = null;
                        i.q().a("LoadingTimer", 40);
                        break;
                    }
                }
            }
            if (w.I && Minecraft.x().s instanceof ModifiGui) {
                i.q().a("Modifi", true);
            } else if (w.I) {
                i.q().a("Modifi", false);
            }
            if (revinv.a(2) != null && revinv.a(2).b() == ModBase.GatlingMotor) {
                i.q().a("Air", 400);
            } else if (!w.I && i.q().e("AT") <= 0 && p.ag() && ep.getReloadKeyPressed() && !p.ah() && i.q().e("UnloadingTimer") == 0 && i.q().e("LoadingTimer") == 0) {
                if (i.q().n("Air_Reload")) {
                    i.q().a("Air_Reload", false);
                    i.q().a("AT", 15);
                } else {
                    i.q().a("Air_Reload", true);
                    i.q().a("AT", 15);
                }
            } else if (!w.I && (p.ah() || i.q().e("ST") > 0)) {
                i.q().a("Air_Reload", false);
            }
            if (!w.I && ep.getShootKeyPressed() && i.q().n("Air_Reload") && i.q().e("AT") <= 0 && i.q().e("Air") < 400) {
                w.a((mp)p, "gatling_air", 1.0f, 1.0f);
                i.q().a("Air", i.q().e("Air") + 100);
                i.q().a("Charge", true);
                i.q().a("AT", 15);
            }
            if (i.q().e("AT") > 0 && !w.I) {
                if (i.q().e("AT") == 5) {
                    i.q().a("Charge", false);
                }
                i.q().a("AT", i.q().e("AT") - 1);
            }
            if (w.I && otdachaTimer > 0) {
                --otdachaTimer;
            }
            if (!w.I) {
                ep.consumeST();
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                    if (i.q().e("Air") > 266) {
                        if (ep.getST() == 1) {
                            i.q().a("Shoot", false);
                        } else if (i.q().e("Air") > 133 && i.q().e("Air") < 267) {
                            if (ep.getST() == 1) {
                                i.q().a("Shoot", false);
                            } else if (i.q().e("Air") > 0 && i.q().e("Air") < 134 && ep.getST() == 2) {
                                i.q().a("Shoot", false);
                            }
                        }
                    }
                } else if (i.q().e("Air") > 266) {
                    if (ep.getST() == 1) {
                        i.q().a("Shoot", false);
                    } else if (i.q().e("Air") > 133 && i.q().e("Air") < 267) {
                        if (ep.getST() == 2) {
                            i.q().a("Shoot", false);
                        } else if (i.q().e("Air") > 0 && i.q().e("Air") < 134 && ep.getST() == 3) {
                            i.q().a("Shoot", false);
                        }
                    }
                }
            }
        }
    }

    public boolean a(wm var1, aab var2, int var3, int var4, int var5, int var6, ng var7) {
        return true;
    }

    public boolean onBlockStartBreak(wm var1, int var2, int var3, int var4, sq var5) {
        return true;
    }

    public void a(wm is, sq player, List list, boolean flag) {
        if (is.q() == null) {
            is.d(new bs());
        }
        float max_bullet = is.q().e("Bullet_Left");
        if (!Keyboard.isKeyDown((int)42)) {
            list.add("<\u041d\u0430\u0436\u043c\u0438\u0442\u0435 " + a.m + "Shift" + a.h + ">");
        } else if (is.q().n("HasAmmo")) {
            if (is.q().e("Bullet_Left") != 0) {
                list.add("\u041f\u0430\u0442\u0440\u043e\u043d\u043e\u0432: " + (int)max_bullet);
            } else {
                list.add("\u041f\u0443\u0441\u0442\u043e");
            }
        } else {
            list.add("\u0412\u0441\u0442\u0430\u0432\u044c\u0442\u0435 \u043c\u0430\u0433\u0430\u0437\u0438\u043d");
        }
    }
}
