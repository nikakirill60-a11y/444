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
 *  mods.provoda.entity.EntityPassiveFire
 *  mods.provoda.until.ExtendetPlayer
 *  mp
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
import mods.provoda.entity.EntityFlameBall;
import mods.provoda.entity.EntityPassiveFire;
import mods.provoda.until.ExtendetPlayer;
import org.lwjgl.input.Keyboard;

public class Ognemet
extends wk {
    int damage;
    int modifi_reload;
    float current_bullet;

    public Ognemet(int par1, int ammo, int damage) {
        super(par1);
        this.a(ModBase.MetroTabGuns);
        this.e(0);
        this.d(1);
        this.damage = damage;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:ognemet");
    }

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }

    public void a(wm i, aab w, mp e, int par4, boolean par5) {
        if (i.q() == null) {
            i.d(new bs());
        }
        if (e instanceof sq && ((sq)e).bK.h() == i) {
            wm ammo;
            sq p = (sq)e;
            ExtendetPlayer ep = ExtendetPlayer.get((sq)p);
            if (!p.ag() && i.q().e("UnloadingTimer") > 0 && !i.q().n("Modifi")) {
                i.q().a("UnloadingTimer", i.q().e("UnloadingTimer") - 1);
                if (i.q().e("UnloadingTimer") == 2) {
                    int slot;
                    for (slot = 0; slot < 8; ++slot) {
                        if (p.bK.a[slot] != null) continue;
                        ammo = new wm(ModBase.OgnemetAmmo);
                        ammo.d(new bs());
                        ammo.q().a("Ammo", i.q().e("Bullet_Left") - 200);
                        p.bK.a[slot] = ammo;
                        i.q().a("HasAmmo", false);
                        i.q().a("Bullet_Left", 0);
                        i.q().a("UnloadingTimer", 0);
                        break;
                    }
                    if (i.q().n("HasAmmo")) {
                        for (slot = 16; slot < 21; ++slot) {
                            if (p.bK.a[slot] != null) continue;
                            ammo = new wm(ModBase.OgnemetAmmo);
                            ammo.d(new bs());
                            ammo.q().a("Ammo", i.q().e("Bullet_Left") - 200);
                            p.bK.a[slot] = ammo;
                            i.q().a("HasAmmo", false);
                            i.q().a("Bullet_Left", 0);
                            i.q().a("UnloadingTimer", 0);
                            break;
                        }
                    }
                    if (i.q().n("HasAmmo")) {
                        wm var12 = new wm(ModBase.OgnemetAmmo);
                        var12.d(new bs());
                        var12.q().a("Ammo", i.q().e("Bullet_Left") - 200);
                        if (!w.I) {
                            w.d((mp)new rh(w, p.u, p.v, p.w, var12));
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
            if (i.q().e("Bullet_Left") > 0 && ep.getShootKeyPressed() && i.q().e("LoadingTimer") == 0 && i.q().e("UnloadingTimer") == 0 && !i.q().n("Modifi") && ep.getST() <= 0 && !p.ah() && i.q().e("Air") > 0 && !i.q().n("Air_Reload")) {
                if (!w.I) {
                    if (w.a((int)p.u, (int)p.v + 1, (int)p.w) == 0) {
                        w.c((int)p.u, (int)p.v + 1, (int)p.w, 4016);
                    }
                    EntityFlameBall var13 = new EntityFlameBall(p.q, (ng)p, 0.5f);
                    if (i.q().e("Sound") <= 0) {
                        w.a((mp)p, "ognemet_shoot", 1.0f, 1.0f);
                        i.q().a("Sound", 23);
                    }
                    w.d((mp)var13);
                    var13.b(var13.u, var13.v - 0.1, var13.w);
                    i.q().a("Bullet_Left", i.q().e("Bullet_Left") - 1);
                    i.q().a("Air", i.q().e("Air") - 2);
                    EntityPassiveFire var14 = new EntityPassiveFire(w, (ng)e, 0.1f);
                    ep.upST(3);
                    if (w.s.nextInt(10) == 0) {
                        w.d((mp)var14);
                    }
                }
            } else if (ep.getST() <= 0 && ep.getShootKeyPressed() && !w.I && !i.q().n("Air_Reload")) {
                w.a((mp)p, "ognemet_empty", 1.0f, 1.0f);
                ep.upST(7);
            }
            if (!p.ag() && ep.getReloadKeyPressed() && i.q().e("UnloadingTimer") == 0 && i.q().e("LoadingTimer") == 0 && !p.ah() && !i.q().n("Modifi")) {
                if (i.q().n("HasAmmo")) {
                    i.q().a("UnloadingTimer", 20);
                    w.a((mp)p, "ognemet_unload", 1.0f, 1.0f);
                } else {
                    int i1;
                    for (i1 = 16; i1 < 21; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].b().cp != ModBase.OgnemetAmmo.cp || p.bK.a[i1].q().e("Ammo") < 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "ognemet_reload", 1.0f, 1.0f);
                        int s = p.bK.a[i1].q().e("Ammo");
                        i.q().a("Bullet_Left", s + 200);
                        i.q().a("HasAmmo", true);
                        i.q().a("BigAmmo", false);
                        p.bK.a[i1] = null;
                        i.q().a("LoadingTimer", 40);
                        break;
                    }
                    for (i1 = 0; i1 < 8; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].c != ModBase.OgnemetAmmo.cp || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "ognemet_reload", 1.0f, 1.0f);
                        ammo = p.bK.a[i1];
                        int s = ammo.q().e("Ammo");
                        i.q().a("Bullet_Left", s + 200);
                        i.q().a("HasAmmo", true);
                        i.q().a("BigAmmo", false);
                        p.bK.a[i1] = null;
                        i.q().a("LoadingTimer", 25);
                        break;
                    }
                }
            }
            if (w.I && Keyboard.isKeyDown((int)45) && this.modifi_reload <= 0 && !p.ag() && !p.ah() && i.q().e("UnloadingTimer") == 0 && i.q().e("LoadingTimer") == 0) {
                i.q().a("Modifi", true);
                this.modifi_reload = 10;
            }
            if (w.I && (Keyboard.isKeyDown((int)1) || Keyboard.isKeyDown((int)18)) && i.q().n("Modifi")) {
                i.q().a("Modifi", false);
                this.modifi_reload = 10;
            }
            if (w.I && this.modifi_reload > 0) {
                --this.modifi_reload;
            }
            if (!w.I && ep.getReloadKeyPressed() && p.ag() && !p.ah() && i.q().e("UnloadingTimer") == 0 && i.q().e("LoadingTimer") == 0 && i.q().e("AT") <= 0) {
                if (i.q().n("Air_Reload")) {
                    i.q().a("Air_Reload", false);
                    i.q().a("AT", 10);
                } else {
                    i.q().a("Air_Reload", true);
                    i.q().a("AT", 10);
                }
            }
            if (ep.getShootKeyPressed() && i.q().e("AT") <= 0 && i.q().n("Air_Reload") && i.q().e("Air") < 180 && !w.I) {
                i.q().a("Charge", true);
                i.q().a("Air", i.q().e("Air") + 45);
                i.q().a("AT", 20);
                w.a((mp)p, "helsing_air", 1.0f, 1.0f);
            }
            if (i.q().e("AT") == 10) {
                i.q().a("Charge", false);
            }
            if (i.q().e("Sound") > 0) {
                i.q().a("Sound", i.q().e("Sound") - 1);
            }
            if (i.q().e("AT") > 0) {
                i.q().a("AT", i.q().e("AT") - 1);
            }
            if (!w.I) {
                ep.consumeST();
            }
        }
    }

    public boolean a(wm var1, aab var2, int var3, int var4, int var5, int var6, ng var7) {
        return true;
    }

    public boolean onLeftClickEntity(wm stack, sq player, mp entity) {
        return true;
    }

    public boolean onBlockStartBreak(wm var1, int var2, int var3, int var4, sq var5) {
        return true;
    }

    public void a(wm is, sq player, List list, boolean flag) {
        if (is.q() == null) {
            is.d(new bs());
        }
        float math = Math.abs(20);
        float max_bullet = is.q().e("Bullet_Left");
        boolean min_bullet = false;
        if (this.current_bullet > max_bullet) {
            this.current_bullet = (int)max_bullet;
        }
        if (this.current_bullet < 0.0f) {
            this.current_bullet = 0.0f;
        }
        if (!Keyboard.isKeyDown((int)42)) {
            list.add("<\u041d\u0430\u0436\u043c\u0438\u0442\u0435 " + a.m + "Shift" + a.h + ">");
            this.current_bullet = 0.0f;
        } else {
            if (this.current_bullet < max_bullet) {
                this.current_bullet += 1.0f / math;
            }
            if (is.q().n("HasAmmo")) {
                if (is.q().e("Bullet_Left") != 0) {
                    list.add("\u041f\u0430\u0442\u0440\u043e\u043d\u043e\u0432: " + (int)this.current_bullet);
                } else {
                    list.add("\u041f\u0443\u0441\u0442\u043e");
                }
            } else {
                list.add("\u0412\u0441\u0442\u0430\u0432\u044c\u0442\u0435 \u043c\u0430\u0433\u0430\u0437\u0438\u043d");
            }
        }
    }
}
