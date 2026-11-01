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
 *  mods.provoda.until.ExtendetPlayer
 *  mp
 *  net.minecraft.client.Minecraft
 *  ng
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
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
import org.lwjgl.input.Mouse;

public class Ubludok
extends wk {
    float timer;
    int damage;
    int modifi_reload;
    public static int otdachaTimer;
    private int motionPause = 0;
    private float motionYaw = 0.0f;
    private float motionPitch = 0.0f;
    private int ticksToChangeMotion = 0;
    float current_bullet;

    public Ubludok(int par1, int ammo, int damage) {
        super(par1);
        this.a(ModBase.MetroTabGuns);
        this.e(0);
        this.d(1);
        this.damage = damage;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:ubludok");
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
            wm ammo;
            sq p = (sq)e;
            ExtendetPlayer ep = ExtendetPlayer.get((sq)p);
            if (!p.ag() && i.q().e("UnloadingTimer") > 0 && !i.q().n("Modifi")) {
                i.q().a("UnloadingTimer", i.q().e("UnloadingTimer") - 1);
                if (i.q().e("UnloadingTimer") == 2) {
                    int slot;
                    for (slot = 0; slot < 8; ++slot) {
                        if (p.bK.a[slot] != null) continue;
                        ammo = new wm(ModBase.UbludokAmmo);
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
                            ammo = new wm(ModBase.UbludokAmmo);
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
                        wm var13 = new wm(ModBase.UbludokAmmo);
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
            if (i.q().e("Bullet_Left") > 0 && ep.getShootKeyPressed() && i.q().e("LoadingTimer") == 0 && i.q().e("UnloadingTimer") == 0 && !i.q().n("Modifi") && ep.getST() <= 0 && !p.ah()) {
                if (w.I && otdachaTimer <= 0) {
                    otdachaTimer = 3;
                    p.B = revinv.a(3) != null ? (revinv.a(3).b() == ModBase.UbludokButt ? (p.B -= 0.4f) : (p.B -= 0.6f)) : (p.B -= 0.6f);
                }
                if (!w.I) {
                    if (w.a((int)p.u, (int)p.v + 1, (int)p.w) == 0) {
                        w.c((int)p.u, (int)p.v + 1, (int)p.w, 4016);
                    }
                    i.q().a("Shoot", true);
                    ep.upST(3);
                    if (revinv.a(2) != null) {
                        if (revinv.a(2).b() == ModBase.Glushak) {
                            w.a((mp)p, "vsv_shootclose", 1.0f, 1.0f);
                        } else {
                            w.a((mp)p, "ubludok_shoot", 1.0f, 1.1f / (wk.e.nextFloat() * 0.4f + 0.8f));
                        }
                    } else {
                        w.a((mp)p, "ubludok_shoot", 1.0f, 1.1f / (wk.e.nextFloat() * 0.4f + 0.8f));
                    }
                    EntityBullet var14 = new EntityBullet((mp)p, this.damage, 15, 10.0f, p.A, p.B);
                    w.d((mp)var14);
                    i.q().a("Bullet_Left", i.q().e("Bullet_Left") - 1);
                }
            } else if (ep.getST() <= 0 && ep.getShootKeyPressed() && !w.I) {
                w.a((mp)p, "empty", 1.0f, 1.0f);
                ep.upST(7);
            }
            if (!p.ag() && ep.getReloadKeyPressed() && i.q().e("UnloadingTimer") == 0 && i.q().e("LoadingTimer") == 0 && !p.ah() && !i.q().n("Modifi")) {
                if (i.q().n("HasAmmo")) {
                    i.q().a("UnloadingTimer", 22);
                    w.a((mp)p, "ubludok_unload", 1.0f, 1.0f);
                } else {
                    int i1;
                    for (i1 = 16; i1 < 21; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].b().cp != ModBase.UbludokAmmo.cp || p.bK.a[i1].q().e("Ammo") < 1) continue;
                        w.a((mp)p, "ubludok_reload", 1.0f, 1.0f);
                        int s = p.bK.a[i1].q().e("Ammo");
                        i.q().a("Bullet_Left", s);
                        i.q().a("HasAmmo", true);
                        p.bK.a[i1] = null;
                        i.q().a("LoadingTimer", 40);
                        break;
                    }
                    for (i1 = 0; i1 < 8; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].c != ModBase.UbludokAmmo.cp || p.bK.a[i1].q().e("Ammo") < 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "ubludok_reload", 1.0f, 1.0f);
                        ammo = p.bK.a[i1];
                        int s = ammo.q().e("Ammo");
                        i.q().a("Bullet_Left", s);
                        i.q().a("HasAmmo", true);
                        p.bK.a[i1] = null;
                        i.q().a("LoadingTimer", 40);
                        break;
                    }
                    for (i1 = 16; i1 < 21; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].b().cp != ModBase.UbludokAmmo.cp || p.bK.a[i1].q().e("Ammo") >= 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "ubludok_reload", 1.0f, 1.0f);
                        int s = p.bK.a[i1].q().e("Ammo");
                        i.q().a("Bullet_Left", s);
                        i.q().a("HasAmmo", true);
                        p.bK.a[i1] = null;
                        i.q().a("LoadingTimer", 40);
                        break;
                    }
                    for (i1 = 0; i1 < 8; ++i1) {
                        if (p.bK.a[i1] == null || p.bK.a[i1].c != ModBase.UbludokAmmo.cp || p.bK.a[i1].q().e("Ammo") >= 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "ubludok_reload", 1.0f, 1.0f);
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
            if (w.I && otdachaTimer > 0) {
                --otdachaTimer;
            }
            if (!w.I) {
                if (ep.getST() == 1) {
                    i.q().a("Shoot", false);
                }
                ep.consumeST();
            }
            if (w.I && Mouse.isButtonDown((int)1) && !p.ah() && Minecraft.x().s == null && i.q().e("UnloadingTimer") == 0 && i.q().e("LoadingTimer") == 0 && !i.q().n("Modifi") && !i.q().n("Air_Reload")) {
                if (--this.ticksToChangeMotion <= 0) {
                    this.ticksToChangeMotion = 5 + (int)(Math.random() * 15.0);
                    this.motionPitch = ((float)Math.random() - 0.5f) * 0.5f;
                    this.motionYaw = ((float)Math.random() - 0.5f) * 0.5f;
                    this.motionPause = (int)(Math.random() * 5.0);
                }
                if (--this.motionPause <= 0) {
                    Minecraft.x().g.c(this.motionYaw, this.motionPitch);
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
