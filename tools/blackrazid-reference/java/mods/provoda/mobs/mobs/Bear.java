/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
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

public class Bear
extends sb {
    public Bear(aab var1) {
        super(var1);
        this.bI = 0.45f;
        this.aC().a(false);
        this.bo.a(2, (om)new ou((ng)this, sq.class, this.bI, false));
        this.bo.a(4, (om)new pf((ng)this));
        this.bo.a(5, (om)new pg((nr)this, this.bI / 2.0f));
        this.bo.a(6, (om)new or((ng)this, sq.class, 8.0f));
        this.bp.a(1, (om)new pq((ng)this, true));
        this.bp.a(2, (om)new pr((ng)this, sq.class, 64.0f, 0, true));
    }

    public boolean bh() {
        return true;
    }

    public int c(mp var1) {
        return 8;
    }

    public int aW() {
        return 140;
    }

    protected void bo() {
        super.bo();
    }

    protected String bb() {
        return "bear_alive";
    }

    protected String bc() {
        Random rand = new Random();
        int i = rand.nextInt(4);
        return i == 0 ? "bear_hit" : "bear_hit1";
    }

    protected String bd() {
        return "bear_death";
    }

    public void x() {
        super.x();
        if (this.ae) {
            this.g(300);
        }
    }
}
