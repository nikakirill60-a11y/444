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
 *  mods.provoda.gui.modifi.ModifiGui
 *  mods.provoda.gui.modifi.ModifiInventory
 *  mp
 *  net.minecraft.client.Minecraft
 *  ng
 *  org.lwjgl.input.Keyboard
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

public class Duplet
extends wk {
    float timer;
    int damage;
    int modifi_reload;
    public static int otdachaTimer;
    float current_bullet;

    public Duplet(int par1, int ammo, int damage) {
        super(par1);
        this.a(ModBase.MetroTabGuns);
        this.e(0);
        this.d(1);
        this.damage = damage;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:duplet");
    }

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }

    public void a(wm i, aab w, mp e, int par4, boolean par5) {
        if (i.q() == null) {
            i.d(new bs());
        }
        ModifiInventory revinv = new ModifiInventory(i);
        if (e instanceof sq && ((sq)e).bK.h() == i) {
            sq p = (sq)e;
            ExtendetPlayer ep = ExtendetPlayer.get(p);
            if ((revinv.a(1) == null || revinv.a(1) != null && revinv.a(1).b() != ModBase.QuadBarrel) && i.q().e("Bullet_Left") > 2) {
                i.q().a("Bullet_Left", 2);
            }
            if (i.q().e("Bullet_Left") > 0 && ep.getShootKeyPressed() && i.q().e("LoadingTimer") == 0 && i.q().e("UnloadingTimer") == 0 && !i.q().n("Modifi") && ep.getST() <= 0 && !p.ah()) {
                if (w.I && otdachaTimer <= 0) {
                    otdachaTimer = 3;
                    if (revinv.a(2) != null) {
                        if (revinv.a(2).b() == ModBase.DupletButt && revinv.a(3) != null && revinv.a(3).b() == ModBase.DupletPriklad) {
                            p.B -= 1.0f;
                        } else if (revinv.a(2).b() == ModBase.DupletButt) {
                            p.B -= 1.0f;
                        }
                    } else {
                        p.B = revinv.a(3) != null && revinv.a(3).b() == ModBase.DupletPriklad ? (p.B -= 1.0f) : (p.B -= 2.0f);
                    }
                }
                if (!w.I) {
                    if (revinv.a(1) != null) {
                        if (revinv.a(1).b() == ModBase.DupletGlushak) {
                            w.a((mp)p, "saiga_closeshoot", 0.5f, 1.0f);
                        } else {
                            w.a((mp)p, "ashot_shoot", 1.0f, 1.0f);
                            if (w.a((int)p.u, (int)p.v + 1, (int)p.w) == 0) {
                                w.c((int)p.u, (int)p.v + 1, (int)p.w, 4016);
                            }
                            i.q().a("Shoot", true);
                        }
                    } else {
                        w.a((mp)p, "ashot_shoot", 1.0f, 1.0f);
                        if (w.a((int)p.u, (int)p.v + 1, (int)p.w) == 0) {
                            w.c((int)p.u, (int)p.v + 1, (int)p.w, 4016);
                        }
                        i.q().a("Shoot", true);
                    }
                    if (revinv.a(1) != null) {
                        if (revinv.a(1).b() == ModBase.QuadBarrel) {
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            i.q().a("Bullet_Left", i.q().e("Bullet_Left") - 1);
                        } else if (revinv.a(1).b() == ModBase.DupletBarrel) {
                            w.d((mp)new EntityBullet((mp)p, this.damage, 70, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 70, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 70, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 70, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 70, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 70, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 70, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 70, 4.0f, p.A, p.B));
                        } else {
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                            w.d((mp)new EntityBullet((mp)p, this.damage, 80, 4.0f, p.A, p.B));
                        }
                    } else {
                        w.d((mp)new EntityBullet((mp)p, this.damage, 80, 5.0f, p.A, p.B));
                        w.d((mp)new EntityBullet((mp)p, this.damage, 80, 5.0f, p.A, p.B));
                        w.d((mp)new EntityBullet((mp)p, this.damage, 80, 5.0f, p.A, p.B));
                        w.d((mp)new EntityBullet((mp)p, this.damage, 80, 5.0f, p.A, p.B));
                        w.d((mp)new EntityBullet((mp)p, this.damage, 80, 5.0f, p.A, p.B));
                        w.d((mp)new EntityBullet((mp)p, this.damage, 80, 5.0f, p.A, p.B));
                        w.d((mp)new EntityBullet((mp)p, this.damage, 80, 5.0f, p.A, p.B));
                        w.d((mp)new EntityBullet((mp)p, this.damage, 80, 5.0f, p.A, p.B));
                    }
                    i.q().a("Bullet_Left", i.q().e("Bullet_Left") - 1);
                    i.q().a("UnloadingTimer", 0);
                    ep.upST(3);
                }
            } else if (ep.getST() <= 0 && ep.getShootKeyPressed() && !w.I) {
                w.a((mp)p, "empty", 1.0f, 1.0f);
                ep.upST(7);
            }
            if (i.q().e("LoadingTimer") > 0) {
                i.q().a("LoadingTimer", i.q().e("LoadingTimer") - 1);
            }
            if (!p.ag() && ep.getReloadKeyPressed() && i.q().e("UnloadingTimer") == 0 && i.q().e("LoadingTimer") == 0 && !p.ah() && !i.q().n("Modifi") && i.q().e("Bullet_Left") <= 0) {
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.QuadBarrel) {
                    int x;
                    int stack;
                    int i1;
                    for (i1 = 0; i1 < 13; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].b().cp != ModBase.Bullet12x70.cp) continue;
                        stack = p.bK.a[i1].a;
                        if (stack > 1) {
                            for (x = 0; x < 4; ++x) {
                                if (i.q().e("Bullet_Left") == 4 || p.bK.a[i1] == null || p.bK.a[i1].b().cp != ModBase.Bullet12x70.cp) continue;
                                if (p.bK.a[i1].a <= 1) {
                                    p.bK.a[i1] = null;
                                    i.q().a("Bullet_Left", i.q().e("Bullet_Left") + 1);
                                    continue;
                                }
                                --p.bK.a[i1].a;
                                i.q().a("Bullet_Left", i.q().e("Bullet_Left") + 1);
                            }
                        } else if (i.q().e("Bullet_Left") != 4) {
                            i.q().a("Bullet_Left", i.q().e("Bullet_Left") + 1);
                            p.bK.a[i1] = null;
                        }
                        w.a((mp)p, "ashot_reload", 1.0f, 1.0f);
                        i.q().a("LoadingTimer", 30);
                        break;
                    }
                    for (i1 = 0; i1 < p.bK.j_(); ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].c != ModBase.Bullet12x70.cp || i.q().e("Bullet_Left") == 2) continue;
                        stack = p.bK.a[i1].a;
                        if (stack > 1) {
                            for (x = 0; x < 4; ++x) {
                                if (i.q().e("Bullet_Left") == 4 || p.bK.a[i1] == null || p.bK.a[i1].c != ModBase.Bullet12x70.cp) continue;
                                i.q().a("Bullet_Left", i.q().e("Bullet_Left") + 1);
                                p.bK.d(ModBase.Bullet12x70.cp);
                            }
                        } else if (i.q().e("Bullet_Left") != 4 && p.bK.a[i1] != null && p.bK.a[i1].c == ModBase.Bullet12x70.cp) {
                            i.q().a("Bullet_Left", i.q().e("Bullet_Left") + 1);
                            p.bK.d(ModBase.Bullet12x70.cp);
                        }
                        w.a((mp)p, "ashot_reload", 1.0f, 1.0f);
                        i.q().a("LoadingTimer", 30);
                        break;
                    }
                } else {
                    int x;
                    int stack;
                    int i1;
                    for (i1 = 16; i1 < 21; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].b().cp != ModBase.Bullet12x70.cp) continue;
                        stack = p.bK.a[i1].a;
                        if (stack > 1) {
                            for (x = 0; x < 2; ++x) {
                                if (i.q().e("Bullet_Left") == 2) continue;
                                i.q().a("Bullet_Left", i.q().e("Bullet_Left") + 1);
                                if (p.bK.a[i1].a <= 1) {
                                    p.bK.a[i1] = null;
                                    continue;
                                }
                                --p.bK.a[i1].a;
                            }
                        } else if (i.q().e("Bullet_Left") != 2) {
                            i.q().a("Bullet_Left", i.q().e("Bullet_Left") + 1);
                            p.bK.a[i1] = null;
                        }
                        w.a((mp)p, "ashot_reload", 1.0f, 1.0f);
                        i.q().a("LoadingTimer", 30);
                        break;
                    }
                    for (i1 = 0; i1 < 8; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].c != ModBase.Bullet12x70.cp || i.q().e("Bullet_Left") == 2) continue;
                        stack = p.bK.a[i1].a;
                        if (stack > 1) {
                            for (x = 0; x < 2; ++x) {
                                if (i.q().e("Bullet_Left") == 2) continue;
                                i.q().a("Bullet_Left", i.q().e("Bullet_Left") + 1);
                                p.bK.d(ModBase.Bullet12x70.cp);
                            }
                        } else if (i.q().e("Bullet_Left") != 2) {
                            i.q().a("Bullet_Left", i.q().e("Bullet_Left") + 1);
                            p.bK.d(ModBase.Bullet12x70.cp);
                        }
                        w.a((mp)p, "ashot_reload", 1.0f, 1.0f);
                        i.q().a("LoadingTimer", 30);
                        break;
                    }
                }
            }
            if (w.I && Minecraft.x().s instanceof ModifiGui) {
                i.q().a("Modifi", true);
            } else if (w.I) {
                i.q().a("Modifi", false);
            }
            if (w.I && otdachaTimer > 0) {
                --otdachaTimer;
            }
            if (!w.I) {
                ep.consumeST();
                if (ep.getST() == 1) {
                    i.q().a("Shoot", false);
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
        } else if (is.q().e("Bullet_Left") != 0) {
            list.add("\u041f\u0430\u0442\u0440\u043e\u043d\u043e\u0432: " + (int)max_bullet);
        } else {
            list.add("\u041f\u0443\u0441\u0442\u043e");
        }
    }
}
