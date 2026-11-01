/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  arc
 *  mp
 *  ng
 *  nr
 *  om
 *  or
 *  ou
 *  pf
 *  pg
 *  pq
 *  pr
 *  pz
 *  sb
 *  sq
 */
package mods.provoda.mobs.mobs;

import java.util.Random;
import mods.provoda.ModBase;
import mods.provoda.until.ExtendetPlayer;

public class Spider
extends sb {
    private boolean panic;
    private float panicTimer;

    public Spider(aab var1) {
        super(var1);
        this.bI = 0.45f;
        this.aC().a(false);
        this.bo.a(2, (om)new ou((ng)this, sq.class, this.bI, false));
        this.bo.a(4, (om)new pf((ng)this));
        this.bo.a(5, (om)new pg((nr)this, this.bI / 2.0f));
        this.bo.a(6, (om)new or((ng)this, sq.class, 8.0f));
        this.bp.a(1, (om)new pq((ng)this, true));
        this.bp.a(2, (om)new pr((ng)this, sq.class, 32.0f, 0, true));
    }

    public boolean bh() {
        return true;
    }

    public int c(mp var1) {
        return 6;
    }

    public int aW() {
        return 80;
    }

    protected void bo() {
        super.bo();
    }

    protected String bb() {
        return "spider_alive";
    }

    protected String bc() {
        Random rand = new Random();
        int i = rand.nextInt(4);
        return i == 0 ? "spider_hit" : "spider_hit1";
    }

    protected String bd() {
        return "spider_death";
    }

    public void x() {
        super.x();
        if (!this.q.I && this.panicTimer > 0.0f) {
            arc var1 = pz.a((nr)this, (int)5, (int)4);
            if (var1 != null) {
                this.aC().a(var1.c, var1.d, var1.e, this.bI);
            }
            this.panicTimer -= 1.0f;
        }
        if (this.ae) {
            this.g(300);
        }
        sq var3 = this.q.b((mp)this, 46.0);
        if (!this.q.I && var3 != null && this.isPlayerLooking(var3) && this.aX() > 0) {
            ExtendetPlayer ep = ExtendetPlayer.get(var3);
            if ((var3.bK.h() != null && var3.bK.h().b() == ModBase.Lamp || var3.bL.a(14).c() != null && var3.bL.a(14).c().b() == ModBase.Lamp) && ep.energyGet() > 10.0f && ep.getLampKeyP() && this.panicTimer <= 0.0f) {
                this.panicTimer = 60.0f;
                this.d(2);
            }
        }
    }

    private boolean isPlayerLooking(sq var1) {
        arc var2 = var1.i(1.0f).a();
        arc var3 = this.q.U().a(this.u - var1.u, this.E.b + (double)(this.P / 2.0f) - (var1.v + (double)var1.e()), this.w - var1.w);
        double var4 = var3.b();
        double var6 = var2.b(var3 = var3.a());
        return var6 > 1.0 - 0.025 / var4 ? var1.n((mp)this) : false;
    }
}
