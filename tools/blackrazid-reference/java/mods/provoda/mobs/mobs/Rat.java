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
 *  pd
 *  pf
 *  pg
 *  sb
 *  sq
 */
package mods.provoda.mobs.mobs;

import java.util.Random;

public class Rat
extends sb {
    public Rat(aab var1) {
        super(var1);
        this.bI = 0.45f;
        this.a(0.5f, 0.2f);
        this.aC().a(true);
        this.bo.a(1, (om)new pd((nr)this, 0.38f));
        this.bo.a(4, (om)new pf((ng)this));
        this.bo.a(5, (om)new pg((nr)this, this.bI / 2.0f));
        this.bo.a(6, (om)new or((ng)this, sq.class, 8.0f));
    }

    public boolean bh() {
        return true;
    }

    public int c(mp var1) {
        return 4;
    }

    public int aW() {
        return 15;
    }

    protected void bo() {
        super.bo();
    }

    protected String bb() {
        return "rat_alive";
    }

    protected String bc() {
        Random rand = new Random();
        int i = rand.nextInt(4);
        return i == 0 ? "rat_hit" : "rat_hit1";
    }

    protected String bd() {
        return "rat_death";
    }

    public void x() {
        super.x();
        if (this.ae) {
            this.g(300);
        }
    }
}
