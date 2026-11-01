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

public class Velopal
extends wk {
    float timer;
    public static int shootTimer;
    int damage;
    int modifi_reload;
    float current_bullet;

    public Velopal(int par1, int ammo, int damage) {
        super(par1);
        this.a(ModBase.MetroTabGuns);
        this.e(0);
        this.d(1);
        this.damage = damage;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(ly par1IconRegister) {
        this.ct = par1IconRegister.a("provoda:vyhlop");
    }

    public boolean onEntitySwing(ng entityLiving, wm stack) {
        return true;
    }

    public void a(wm i, aab w, mp e, int par4, boolean par5) {
        ModifiInventory revinv;
        if (i.q() == null) {
            i.d(new bs());
        }
        if ((revinv = ModifiInventory.theMinecraft) == null) {
            new ModifiInventory(i);
        }
        if (e instanceof sq && ((sq)e).bK.h() == i) {
            sq p = (sq)e;
            ExtendetPlayer ep = ExtendetPlayer.get(p);
            if (!p.ag() && i.q().e("UnloadingTimer") > 0 && !i.q().n("Modifi")) {
                i.q().a("UnloadingTimer", i.q().e("UnloadingTimer") - 1);
                if (i.q().e("UnloadingTimer") == 2) {
                    new wm(ModBase.VentilAmmo);
                    wm slot = i.q().n("BigAmmo") ? new wm(ModBase.VentilAmmoBig) : new wm(ModBase.VentilAmmo);
                    if (i.q().e("Bullet_Left") > 0) {
                        slot.d(new bs());
                        slot.q().a("Ammo", i.q().e("Bullet_Left") - 1);
                    } else {
                        slot.d(new bs());
                        slot.q().a("Ammo", i.q().e("Bullet_Left"));
                    }
                    if (!w.I) {
                        p.bK.a(slot);
                    }
                    i.q().a("HasAmmo", false);
                    if (i.q().e("Bullet_Left") > 0) {
                        i.q().a("Bullet_Left", 1);
                    } else {
                        i.q().a("Bullet_Left", 0);
                    }
                    i.q().a("UnloadingTimer", 0);
                }
            }
            if (!p.ag() && i.q().e("LoadingTimer") > 0) {
                i.q().a("LoadingTimer", i.q().e("LoadingTimer") - 1);
            }
            if (i.q().e("Bullet_Left") > 0 && i.q().e("LoadingTimer") == 0 && i.q().e("UnloadingTimer") == 0 && !i.q().n("Modifi") && shootTimer <= 0 && !p.ah()) {
                if (!w.I) {
                    w.a((mp)p, "vyhlop_shoot", 1.0f, 1.0f);
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    w.d((mp)new EntityBullet((mp)p, this.damage, 30, 5.0f, p.A, p.B));
                    i.q().a("Bullet_Left", i.q().e("Bullet_Left") - 1);
                    shootTimer = 35;
                }
            } else if (shootTimer == 0 && !w.I) {
                w.a((mp)p, "empty", 1.0f, 1.0f);
                shootTimer = 7;
            }
            if (!p.ag() && ep.getReloadKeyPressed() && i.q().e("UnloadingTimer") == 0 && i.q().e("LoadingTimer") == 0 && !p.ah() && !i.q().n("Modifi")) {
                if (i.q().n("HasAmmo")) {
                    i.q().a("UnloadingTimer", 20);
                    w.a((mp)p, "vyhlop_unload", 1.0f, 1.0f);
                } else {
                    int s;
                    wm ammo;
                    int i1;
                    for (i1 = 0; i1 < p.bK.j_(); ++i1) {
                        if (p.bK.a(i1) == null || p.bK.a((int)i1).c != ModBase.VyhlopAmmo.cp || p.bK.a(i1).q().e("Ammo") < 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "vyhlop_reload", 1.0f, 1.0f);
                        ammo = p.bK.a(i1);
                        s = ammo.q().e("Ammo");
                        i.q().a("Bullet_Left", s);
                        i.q().a("HasAmmo", true);
                        i.q().a("BigAmmo", false);
                        p.bK.a(i1, (wm)null);
                        i.q().a("LoadingTimer", 40);
                        break;
                    }
                    for (i1 = 0; i1 < p.bK.j_(); ++i1) {
                        if (p.bK.a(i1) == null || p.bK.a((int)i1).c != ModBase.VyhlopAmmo.cp || p.bK.a(i1).q().e("Ammo") >= 1 || i.q().n("HasAmmo")) continue;
                        w.a((mp)p, "vyhlop_reload", 1.0f, 1.0f);
                        ammo = p.bK.a(i1);
                        s = ammo.q().e("Ammo");
                        i.q().a("Bullet_Left", s);
                        i.q().a("HasAmmo", true);
                        i.q().a("BigAmmo", false);
                        p.bK.a(i1, (wm)null);
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
            if (shootTimer == 20 && !w.I && i.q().e("Bullet_Left") > 0) {
                w.a((mp)p, "ventil_preload", 1.0f, 1.0f);
            }
            if (shootTimer > 0 && !w.I) {
                --shootTimer;
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
                list.add("\u0412\u0441\u0442\u0430\u0432\u044c\u0442\u0435 \u043e\u0431\u043e\u0439\u043c\u0443");
            }
        }
    }
}
