/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aqp
 */
package mods.provoda.until;

public class BlockLightTile
extends aqp {
    public byte timer = 0;

    public void h() {
        if (!this.k.I) {
            if (this.k.a(this.l, this.m, this.n) != 4016 || this.k.a(this.l, this.m, this.n) != 4017 || this.k.a(this.l, this.m, this.n) != 4018 || this.k.a(this.l, this.m, this.n) != 4019) {
                this.k.a(this.l, this.m, this.n, (aqp)null);
            }
            if (this.timer > 3) {
                this.k.a(this.l, this.m, this.n, (aqp)null);
                this.k.i(this.l, this.m, this.n);
            }
            this.timer = (byte)(this.timer + 1);
        }
    }
}
