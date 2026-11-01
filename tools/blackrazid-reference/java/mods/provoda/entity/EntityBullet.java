/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  apa
 *  aqx
 *  ara
 *  arc
 *  bs
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  jc
 *  kx
 *  mg
 *  mp
 *  ne
 *  ng
 *  sb
 *  sq
 *  tb
 */
package mods.provoda.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mods.provoda.until.ExtendetPlayer;

public class EntityBullet
extends tb {
    private int damage = 0;
    private mp shooter;
    private String hitSound = null;

    public EntityBullet(aab par1World) {
        super(par1World);
        this.N = 0.0f;
        this.a(0.01f, 0.01f);
    }

    public EntityBullet(mp shooter, int damage, int accuarcy, float speedFactor, float yaw, float pitch) {
        this(shooter.q);
        this.shooter = shooter;
        this.damage = damage;
        this.b(shooter.u, shooter.v + (double)shooter.e() - (shooter.ag() ? 0.09 : 0.0), shooter.w, yaw, pitch);
        this.a(0.01f, 0.01f);
        float x = -kx.a((float)(this.A / 180.0f * (float)Math.PI)) * kx.b((float)(this.B / 180.0f * (float)Math.PI)) * speedFactor;
        float z = kx.b((float)(this.A / 180.0f * (float)Math.PI)) * kx.b((float)(this.B / 180.0f * (float)Math.PI)) * speedFactor;
        float y = -kx.a((float)(this.B / 180.0f * (float)Math.PI)) * speedFactor;
        float randAngle = (shooter.q.s.nextFloat() - 0.5f) * (float)accuarcy * (float)Math.PI / 90.0f;
        arc axis = arc.a((double)(shooter.q.s.nextFloat() - 0.5f), (double)(shooter.q.s.nextFloat() - 0.5f), (double)(shooter.q.s.nextFloat() - 0.5f));
        axis.a();
        float s = kx.a((float)randAngle);
        float c = kx.b((float)randAngle);
        float oc = 1.0f - c;
        double[] m = new double[]{(double)oc * axis.c * axis.c + (double)c, (double)oc * axis.c * axis.d - axis.e * (double)s, (double)oc * axis.e * axis.c + axis.d * (double)s, (double)oc * axis.c * axis.d + axis.e * (double)s, (double)oc * axis.d * axis.d + (double)c, (double)oc * axis.d * axis.e - axis.c * (double)s, (double)oc * axis.e * axis.c - axis.d * (double)s, (double)oc * axis.d * axis.e + axis.c * (double)s, (double)oc * axis.e * axis.e + (double)c};
        this.x = m[0] * (double)x + m[1] * (double)y + m[2] * (double)z;
        this.y = m[3] * (double)x + m[4] * (double)y + m[5] * (double)z;
        this.z = m[6] * (double)x + m[7] * (double)y + m[8] * (double)z;
        this.b(this.u, this.v, this.w);
    }

    public EntityBullet(mp shooter, double posX, double posY, double posZ, float yaw, float pitch, int damage, float speedFactor, String hitSound, double damageFactor) {
        this(shooter.q);
        this.a(0.01f, 0.01f);
        this.shooter = shooter;
        this.damage = damage;
        this.b(posX, posY, posZ, yaw, pitch);
        this.x = (double)(-kx.a((float)(this.A / 180.0f * (float)Math.PI)) * kx.b((float)(this.B / 180.0f * (float)Math.PI))) * (double)speedFactor;
        this.z = (double)(kx.b((float)(this.A / 180.0f * (float)Math.PI)) * kx.b((float)(this.B / 180.0f * (float)Math.PI))) * (double)speedFactor;
        this.y = (double)(-kx.a((float)(this.B / 180.0f * (float)Math.PI))) * (double)speedFactor;
        this.hitSound = hitSound;
        this.b(this.u, this.v, this.w);
    }

    public void c(double par1, double par3, double par5, float par7, float par8) {
    }

    public void a(bs tag) {
        super.a(tag);
        try {
            this.damage = tag.e("damage");
        }
        catch (Exception var3) {
            this.w();
        }
    }

    public void b(bs tag) {
        super.b(tag);
        tag.a("damage", this.damage);
    }

    public void l_() {
        super.l_();
        float f2 = 0.99f;
        this.x /= (double)f2;
        this.y /= (double)f2;
        this.z /= (double)f2;
        if (this.ac > 200) {
            this.w();
        }
    }

    protected float g() {
        return 0.0f;
    }

    protected void a(ara obj) {
        if (!this.q.I && obj != null) {
            if (obj.g != null && !obj.g.M && !this.M) {
                if (this.ac < 10 && obj.g == this.shooter) {
                    return;
                }
                mp entity = obj.g;
                mg damagesource = this.shooter instanceof sq ? mg.a((sq)((sq)this.shooter)) : (this.shooter instanceof sb ? mg.a((ng)((sb)this.shooter)) : mg.a((sq)((sq)this.shooter)));
                int savedResistantTime = entity.af;
                entity.af = 0;
                if (entity instanceof sq) {
                    sq sq2 = (sq)entity;
                }
                if (!(entity instanceof ne) && this.shooter != null && entity != this.shooter && entity instanceof sq && this.shooter instanceof jc) {
                    sq player;
                    if (entity instanceof sq && this.shooter instanceof sq && ExtendetPlayer.get(player = (sq)entity).getFaction() != ExtendetPlayer.get((sq)this.shooter).getFaction()) {
                        entity.a(damagesource, this.damage);
                    }
                } else {
                    entity.a(damagesource, this.damage);
                }
            }
            if (obj.g == null && apa.r[this.q.a(obj.b, obj.c, obj.d)].b(this.q, obj.b, obj.c, obj.d) == null) {
                return;
            }
            if (!this.q.I && this.hitSound != null && !this.hitSound.isEmpty()) {
                this.q.a((mp)this, this.hitSound, 1.0f, 0.9f + this.q.s.nextFloat() * 0.1f);
            }
            this.w();
        }
    }

    @Deprecated
    private arc getHitPoint() {
        for (int i = 1; i <= 100; ++i) {
            arc from = this.q.U().a(this.u + this.x * 0.01 * (double)(i - 1), this.v * 0.01 * (double)(i - 1), this.w * 0.01 * (double)(i - 1));
            arc to = this.q.U().a(this.u + this.x * 0.01 * (double)i, this.v + this.y * 0.01 * (double)i, this.w + this.z * 0.01 * (double)i);
            mp entity = null;
            List list = this.q.b((mp)this, this.E.a(this.x, this.y, this.z).b(1.0, 1.0, 1.0));
            double d0 = 0.0;
            ng entitylivingbase = this.h();
            for (int j = 0; j < list.size(); ++j) {
                double d1;
                float f;
                aqx axisalignedbb;
                ara movingobjectposition1;
                mp entity1 = (mp)list.get(j);
                if (!entity1.K() || entity1 == entitylivingbase && this.ac < 5 || (movingobjectposition1 = (axisalignedbb = entity1.E.b((double)(f = 0.3f), (double)f, (double)f)).a(from, to)) == null || !((d1 = from.d(movingobjectposition1.f)) < d0) && d0 != 0.0) continue;
                entity = entity1;
                d0 = d1;
            }
            if (entity == null) continue;
            return to;
        }
        return null;
    }

    @Deprecated
    private arc getHeadCenter(sq player) {
        return player.ag() ? arc.a((double)player.u, (double)(player.v + (double)1.7f), (double)player.w) : (!player.G() && !player.ce.b && player.P < 1.0f ? arc.a((double)(player.u + Math.sin(Math.toRadians(player.A)) * 0.3), (double)(player.v + 0.25), (double)(player.w + Math.cos(Math.toRadians(player.A)))) : arc.a((double)player.u, (double)(player.v + 1.75), (double)player.w));
    }

    @SideOnly(value=Side.CLIENT)
    public void addMotion(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @SideOnly(value=Side.CLIENT)
    public void a(double par1, double par3, double par5, float par7, float par8, int par9) {
    }
}
