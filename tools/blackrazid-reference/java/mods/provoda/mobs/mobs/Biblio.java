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
 *  sb
 *  sq
 */
package mods.provoda.mobs.mobs;

import java.util.Random;

public class Biblio
extends sb {
    public Biblio(aab var1) {
        super(var1);
        this.a(1.0f, 2.5f);
        this.be = 0;
        this.bI = 0.46f;
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
        return 10;
    }

    public int aW() {
        return 200;
    }

    protected void bo() {
        super.bo();
    }

    protected String bb() {
        return "biblio_alive";
    }

    protected String bc() {
        Random rand = new Random();
        int i = rand.nextInt(4);
        return i == 0 ? "biblio_hit" : "biblio_hit1";
    }

    protected String bd() {
        return "biblio_death";
    }

    private boolean isPlayerLooking(sq var1) {
        arc var2 = var1.i(1.0f).a();
        arc var3 = this.q.U().a(this.u - var1.u, this.E.b + (double)(this.P / 1.5f) - (var1.v + (double)var1.e()), this.w - var1.w);
        double var4 = var3.b();
        double var6 = var2.b(var3 = var3.a());
        return var6 > 1.0 - 0.025 / var4 ? var1.n((mp)this) : false;
    }

    public void x() {
        sq var1 = this.q.b((mp)this, 46.0);
        super.x();
        if (var1 != null && this.isPlayerLooking(var1) && this.aX() > 125) {
            this.al();
        }
    }
}
