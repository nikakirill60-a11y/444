/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  apa
 *  aqx
 *  bs
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  kx
 *  mg
 *  mods.provoda.entity.EntityDrezinaStock$CouplerType
 *  mp
 *  ng
 *  sq
 */
package mods.provoda.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.UUID;
import mods.provoda.ModBase;
import mods.provoda.entity.EntityDrezinaStock;
import mods.provoda.entity.RotativePoint;
import mods.provoda.tileentitys.tunnels.TileEntityGag;
import mods.provoda.tileentitys.tunnels.TileEntityWideRail;
import mods.provoda.until.LinkResolver;

public class EntityDrezinaStock
extends mp {
    public boolean isOnRail;
    public boolean isOnSlope;
    public boolean wasLastSwitchActive;
    public boolean hasFrontCoupler;
    public boolean hasRearCoupler;
    public boolean needsCouplingsCheck;
    public boolean renderCouplingPositions;
    public boolean isStatic;
    public int railPosX;
    public int railPosY;
    public int railPosZ;
    public float[] tangent;
    public float mass;
    public float maxSpeed;
    public float projectedSpeed;
    public float rotationCos;
    public float rotationSin;
    public float walkableMinX;
    public float walkableMinZ;
    public float walkableMaxX;
    public float walkableMaxZ;
    public float bogieShift;
    public float bogieAngle;
    public RotativePoint riderPos;
    public RotativePoint couplerFront;
    public RotativePoint couplerRear;
    public float[] wheelRadius;
    public float[] wheelAngle;
    public EntityDrezinaStock frontCoupledCart;
    public EntityDrezinaStock rearCoupledCart;
    public UUID frontCartUUID;
    public UUID rearCartUUID;
    public String label;
    public String placer;
    public CouplerType coupler;

    public EntityDrezinaStock(aab par1World) {
        super(par1World);
        this.a(4.0f, 1.0f);
        this.renderCouplingPositions = false;
        this.isStatic = false;
        this.needsCouplingsCheck = true;
        this.N = -0.625f;
        this.Y = 0.25f;
        this.mass = 1.0f;
        this.aa = 1.0f;
        this.maxSpeed = 0.5f;
        this.projectedSpeed = 0.0f;
        this.rotationCos = 1.0f;
        this.rotationSin = 0.0f;
        this.bogieShift = 12.0f;
        this.riderPos = new RotativePoint(this, 0.0f, 0.0f, 0.0f);
        this.couplerFront = new RotativePoint(this, 0.0f, 0.0f, 0.0f);
        this.couplerRear = new RotativePoint(this, 0.0f, 0.0f, 0.0f);
        this.walkableMinX = 0.0f;
        this.walkableMinZ = 0.0f;
        this.walkableMaxX = 0.0f;
        this.walkableMaxZ = 0.0f;
        this.frontCoupledCart = null;
        this.rearCoupledCart = null;
        this.n = null;
        this.tangent = new float[]{0.0f, 0.0f, 0.0f};
        this.wheelRadius = new float[0];
        this.wheelAngle = new float[0];
        this.ah.a(3, (Object)new Float(0.0f));
        this.ah.a(4, (Object)new Float(0.0f));
        this.ah.a(5, (Object)new Float(0.0f));
        this.ah.a(6, (Object)new Float(0.0f));
        this.ah.a(7, (Object)new Float(0.0f));
        this.ah.a(8, (Object)new Float(0.0f));
        this.coupler = CouplerType.NONE;
        this.label = "";
        this.placer = "unknown";
        this.a();
    }

    public void a() {
    }

    public boolean attackEntityFrom(mg par1DamageSource, float par2) {
        if (this.aq()) {
            return false;
        }
        if (!this.M && !this.q.I) {
            sq entityplayer = null;
            if (par1DamageSource.i() instanceof sq) {
                entityplayer = (sq)par1DamageSource.i();
            }
            if (entityplayer != null && entityplayer.ce.d && entityplayer.ag()) {
                this.w();
                this.q.e((mp)this);
                return true;
            }
        }
        return true;
    }

    public void l_() {
        this.x();
    }

    public void x() {
        float i;
        if (this.frontCoupledCart != null && this.frontCoupledCart.frontCoupledCart != this && this.frontCoupledCart.rearCoupledCart != this) {
            this.frontCoupledCart = new EntityDrezinaStock(null);
            this.frontCoupledCart = null;
        }
        if (this.rearCoupledCart != null && this.rearCoupledCart.frontCoupledCart != this && this.rearCoupledCart.rearCoupledCart != this) {
            this.rearCoupledCart = new EntityDrezinaStock(null);
            this.rearCoupledCart = null;
        }
        if (this.o != null && this.o.M) {
            this.o = null;
        }
        this.rotationCos = (float)(-Math.sin(Math.toRadians(this.A)));
        this.rotationSin = (float)Math.cos(Math.toRadians(this.A));
        if (this.needsCouplingsCheck) {
            i = 30.0f;
            List var9 = this.q.a(EntityDrezinaStock.class, this.E.b((double)i, (double)i, (double)i));
            for (EntityDrezinaStock var12 : var9) {
                if (var12 == null || !var12.getPersistentID().equals(this.frontCartUUID) || var12.M) continue;
                this.frontCoupledCart = var12;
                break;
            }
            for (EntityDrezinaStock var12 : var9) {
                if (var12 == null || !var12.getPersistentID().equals(this.rearCartUUID) || var12.M) continue;
                this.rearCoupledCart = var12;
                break;
            }
            this.needsCouplingsCheck = false;
        }
        if (this.q.I) {
            if (this.renderCouplingPositions) {
                this.updateCouplingPos();
                this.q.a("smoke", (double)this.couplerFront.getX(), (double)this.couplerFront.getY(), (double)this.couplerFront.getZ(), 0.0, 0.0, 0.0);
                this.q.a("reddust", (double)this.couplerRear.getX(), (double)this.couplerRear.getY(), (double)this.couplerRear.getZ(), 0.0, 0.0, 0.0);
            }
            this.A = this.ah.a(3);
            this.B = this.ah.a(6);
            this.projectedSpeed = this.ah.a(4);
            this.bogieAngle = this.ah.a(5);
            this.x = this.projectedSpeed * this.rotationCos;
            this.z = this.projectedSpeed * this.rotationSin;
            for (int var8 = 0; var8 < this.wheelAngle.length; var8 = (int)((byte)(var8 + 1))) {
                this.wheelAngle[var8] = (float)kx.g((double)((double)this.wheelAngle[var8] - Math.toDegrees(this.projectedSpeed / this.wheelRadius[var8])));
            }
            if (this.n != null && this.n instanceof ng) {
                i = 0.13f;
                float var91 = ((ng)this.n).getMoveForward() * i;
                float var11 = ((ng)this.n).getMoveStrafing() * i;
                float var13 = (float)Math.cos(Math.toRadians(this.n.A - this.A));
                float var15 = (float)Math.sin(Math.toRadians(this.n.A - this.A));
                this.riderPos.setRelX(this.riderPos.getRelX() + -var15 * var91 + var13 * var11);
                this.riderPos.setRelZ(this.riderPos.getRelZ() + var13 * var91 + var15 * var11);
            }
            if (this.riderPos.getRelX() < this.walkableMinX) {
                this.riderPos.setRelX(this.walkableMinX);
            }
            if (this.riderPos.getRelX() > this.walkableMaxX) {
                this.riderPos.setRelX(this.walkableMaxX);
            }
            if (this.riderPos.getRelZ() < this.walkableMinZ) {
                this.riderPos.setRelZ(this.walkableMinZ);
            }
            if (this.riderPos.getRelZ() > this.walkableMaxZ) {
                this.riderPos.setRelZ(this.walkableMaxZ);
            }
            this.ah.b(7, (Object)String.valueOf(this.riderPos.getRelX()));
            this.ah.b(7, (Object)String.valueOf(this.riderPos.getRelZ()));
        } else {
            if (this.v < -64.0) {
                this.B();
            }
            this.r = this.u;
            this.s = this.v;
            this.t = this.w;
            this.D = this.B;
            this.C = this.A;
            this.x *= 0.95;
            this.y *= 0.95;
            this.z *= 0.95;
            int var101 = kx.c((double)this.u);
            int var121 = kx.c((double)this.v);
            int var14 = kx.c((double)this.w);
            this.g(0.0, -0.04, 0.0);
            List var151 = this.q.b((mp)this, this.E.b((double)0.2f, 0.0, (double)0.2f));
            if (var151 != null && !var151.isEmpty()) {
                for (int var16 = 0; var16 < var151.size(); ++var16) {
                    mp entity = (mp)var151.get(var16);
                    if (entity == this.frontCoupledCart || entity == this.rearCoupledCart || entity == this.n) continue;
                    this.f(entity);
                }
            }
            this.updateCouplingPos();
            this.updateCouplings();
            this.updateOnRail();
            this.projectedSpeed = (float)(this.x * (double)this.rotationCos + this.z * (double)this.rotationSin + this.y * (double)this.tangent[2]);
            for (int var161 = 0; var161 < this.wheelAngle.length; var161 = (int)((byte)(var161 + 1))) {
                this.wheelAngle[var161] = (float)kx.g((double)((double)this.wheelAngle[var161] - Math.toDegrees(this.projectedSpeed / this.wheelRadius[var161])));
            }
            this.riderPos.setRelX(Float.valueOf(this.ah.e(7)).floatValue());
            this.riderPos.setRelZ(Float.valueOf(this.ah.e(8)).floatValue());
            this.ah.b(3, (Object)Float.valueOf(this.A));
            this.ah.b(6, (Object)Float.valueOf(this.B));
            this.ah.b(4, (Object)Float.valueOf(this.projectedSpeed));
        }
        if (this.isStatic) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        }
        this.d(this.x, this.y, this.z);
    }

    public void updateCouplings() {
        if (this.frontCoupledCart != null && !this.frontCoupledCart.M) {
            this.frontCoupledCart.updateCouplingPos();
            if (this.frontCoupledCart.rearCoupledCart == this) {
                LinkResolver.solveLink(this, this.frontCoupledCart, this.couplerFront.getX(), (float)this.v, this.couplerFront.getZ(), this.frontCoupledCart.couplerRear.getX(), (float)this.frontCoupledCart.v, this.frontCoupledCart.couplerRear.getZ(), 0.0f);
            }
            if (this.frontCoupledCart.frontCoupledCart == this) {
                LinkResolver.solveLink(this, this.frontCoupledCart, this.couplerFront.getX(), (float)this.v, this.couplerFront.getZ(), this.frontCoupledCart.couplerFront.getX(), (float)this.frontCoupledCart.v, this.frontCoupledCart.couplerFront.getZ(), 0.0f);
            }
        }
        if (this.rearCoupledCart != null && !this.rearCoupledCart.M) {
            this.rearCoupledCart.updateCouplingPos();
            if (this.rearCoupledCart.rearCoupledCart == this) {
                LinkResolver.solveLink(this, this.rearCoupledCart, this.couplerRear.getX(), (float)this.v, this.couplerRear.getZ(), this.rearCoupledCart.couplerRear.getX(), (float)this.rearCoupledCart.v, this.rearCoupledCart.couplerRear.getZ(), 0.0f);
            }
            if (this.rearCoupledCart.frontCoupledCart == this) {
                LinkResolver.solveLink(this, this.rearCoupledCart, this.couplerRear.getX(), (float)this.v, this.couplerRear.getZ(), this.rearCoupledCart.couplerFront.getX(), (float)this.rearCoupledCart.v, this.rearCoupledCart.couplerFront.getZ(), 0.0f);
            }
        }
    }

    private float limitForce(double force) {
        return (float)Math.copySign(Math.min(Math.abs(force), 3.0), force);
    }

    public void updateCouplingPos() {
        this.couplerFront.onUpdate();
        this.couplerRear.onUpdate();
    }

    public float[] getTangent() {
        TileEntityWideRail railTile;
        if (this.q.a(this.railPosX, this.railPosY, this.railPosZ) == ModBase.rails.cz && (railTile = (TileEntityWideRail)this.q.r(this.railPosX, this.railPosY, this.railPosZ)) != null) {
            if (railTile.mId != 0 && (railTile.mId != 5 && railTile.mId != 6 || this.wasLastSwitchActive) && (railTile.mId != 9 && railTile.mId != 10 || this.wasLastSwitchActive)) {
                if (railTile.mId == 3 || railTile.mId == 4 || (railTile.mId == 5 || railTile.mId == 6) && this.wasLastSwitchActive) {
                    int tg31 = this.q.h(this.railPosX, this.railPosY, this.railPosZ) & 3;
                    float tg11 = railTile.mId != 3 && railTile.mId != 5 ? 1.0f : -1.0f;
                    float[] tg311 = ModBase.rotXZByDir(tg11 * 15.5f, -0.5f, tg31);
                    tg311[0] = tg311[0] + ((float)this.railPosX + 0.5f);
                    tg311[1] = tg311[1] + ((float)this.railPosZ + 0.5f);
                    float dir111 = (float)((double)tg311[0] - this.u);
                    float xs = (float)((double)tg311[1] - this.w);
                    float c1 = (float)Math.atan2(xs, dir111);
                    float c21 = (float)((double)c1 + 1.5707963267948966);
                    float radX = (float)Math.cos(c21);
                    float radZ = (float)Math.sin(c21);
                    float radA = (float)Math.sqrt(dir111 * dir111 + xs * xs);
                    this.b((double)tg311[0] + (double)(dir111 /= radA) * -15.5, (float)this.railPosY + 0.375f, (double)tg311[1] + (double)(xs /= radA) * -15.5);
                    return new float[]{radX, radZ, 0.0f};
                }
                if (railTile.mId == 7 || railTile.mId == 8 || (railTile.mId == 9 || railTile.mId == 10) && this.wasLastSwitchActive || railTile.mId == 11) {
                    float tg3 = (float)(Math.pow(this.u - (double)this.railPosX, 2.0) + Math.pow(this.w - (double)this.railPosZ, 2.0));
                    int tg1 = tg3 < 62.0f ? 0 : 1;
                    int tg31 = this.q.h(this.railPosX, this.railPosY, this.railPosZ) + tg1 * 2 & 3;
                    int dir11 = this.q.h(this.railPosX, this.railPosY, this.railPosZ) & 3;
                    float xs = railTile.mId != 7 && railTile.mId != 9 && (railTile.mId != 11 || !this.wasLastSwitchActive) ? 1.0f : -1.0f;
                    float[] c = ModBase.rotXZByDir(xs * 12.95f, -0.15f, tg31);
                    float[] c2 = ModBase.rotXZByDir(xs * 5.0f * (float)tg1, 15.0f * (float)tg1, dir11);
                    c[0] = c[0] + (c2[0] + (float)this.railPosX + 0.5f);
                    c[1] = c[1] + (c2[1] + (float)this.railPosZ + 0.5f);
                    float radX = (float)((double)c[0] - this.u);
                    float radZ = (float)((double)c[1] - this.w);
                    float radA = (float)Math.atan2(radZ, radX);
                    float perA = (float)((double)radA + 1.5707963267948966);
                    float perX = (float)Math.cos(perA);
                    float perZ = (float)Math.sin(perA);
                    float radG = (float)Math.sqrt(radX * radX + radZ * radZ);
                    this.b(c[0] + (radX /= radG) * -12.9f, (float)this.railPosY + 0.375f, c[1] + (radZ /= radG) * -12.9f);
                    return new float[]{perX, perZ, 0.0f};
                }
                if (railTile.mId != 12 && railTile.mId != 13 && railTile.mId != 14) {
                    return new float[]{this.rotationCos, this.rotationSin, this.tangent[2]};
                }
                byte dir = (byte)this.q.h(this.railPosX, this.railPosY, this.railPosZ);
                int[] tg = ModBase.rotXZByDir(0, 1, (int)dir);
                float tg3 = 1.0f / (railTile.mId == 14 ? 12.0f : 25.0f * (float)(railTile.mId - 11));
                return new float[]{tg[0], tg[1], tg3};
            }
            byte dir = (byte)this.q.h(this.railPosX, this.railPosY, this.railPosZ);
            int[] tg = ModBase.rotXZByDir(0, 1, (int)dir);
            return new float[]{tg[0], tg[1], 0.0f};
        }
        return new float[]{this.rotationCos, this.rotationSin, this.tangent[2]};
    }

    public void updateOnRail() {
        float trackAngle;
        TileEntityGag y1;
        int i = kx.c((double)this.u);
        int j = kx.c((double)this.v);
        int k = kx.c((double)this.w);
        this.isOnRail = true;
        this.isOnSlope = true;
        int id = this.q.a(i, j, k);
        boolean metaData = false;
        int mId = 0;
        if (id == ModBase.rails.cz) {
            this.railPosX = i;
            this.railPosY = j;
            this.railPosZ = k;
            TileEntityWideRail railTile = (TileEntityWideRail)this.q.r(i, j, k);
            int var12 = this.q.h(i, j, k);
            if (railTile != null && railTile.mId == 0) {
                mId = railTile.mId;
                if (var12 != 0 && var12 != 2) {
                    this.b(this.u, (float)this.railPosY + 0.375f, (float)this.railPosZ + 0.5f);
                } else {
                    this.b((float)this.railPosX + 0.5f, (float)this.railPosY + 0.375f, this.w);
                }
            } else if (railTile != null && railTile.mId != 12 && railTile.mId != 13 && railTile.mId != 14) {
                this.b(this.u, (float)this.railPosY + 0.375f, this.w);
            }
            this.wasLastSwitchActive = railTile.activated;
        } else if (id == ModBase.railGag.cz) {
            y1 = (TileEntityGag)this.q.r(i, j, k);
            if (y1 != null && (y1.primary_x != 0 || y1.primary_y != 0 || y1.primary_z != 0) && this.q.a(y1.primary_x, y1.primary_y, y1.primary_z) == ModBase.rails.cz) {
                this.railPosX = y1.primary_x;
                this.railPosY = y1.primary_y;
                this.railPosZ = y1.primary_z;
                TileEntityWideRail railTile = (TileEntityWideRail)this.q.r(y1.primary_x, y1.primary_y, y1.primary_z);
                if (railTile != null) {
                    int var12 = this.q.h(i, j, k);
                    mId = railTile.mId;
                    if (railTile != null && mId == 0) {
                        if (var12 != 0 && var12 != 2) {
                            this.b(this.u, (float)this.railPosY + 0.375f, (float)this.railPosZ + 0.5f);
                        } else {
                            this.b((float)this.railPosX + 0.5f, (float)this.railPosY + 0.375f, this.w);
                        }
                    } else if (railTile != null && railTile.mId != 12 && railTile.mId != 13 && railTile.mId != 14) {
                        this.b(this.u, (float)this.railPosY + 0.375f, this.w);
                    }
                    trackAngle = (float)Math.sqrt(Math.pow(y1.primary_x - i, 2.0) + Math.pow(y1.primary_z - k, 2.0));
                    if ((railTile.mId == 5 || railTile.mId == 6 || railTile.mId == 9 || railTile.mId == 10) && trackAngle > 7.0f) {
                        this.wasLastSwitchActive = y1.primary_x != i && y1.primary_z != k;
                    } else if (railTile.mId == 11 && trackAngle > 5.0f) {
                        this.wasLastSwitchActive = var12 != 0 && var12 != 2 ? (var12 == 1 ? Math.signum(this.w - (double)this.railPosZ) < 0.0 : Math.signum(this.w - (double)this.railPosZ) > 0.0) : (var12 == 0 ? Math.signum(this.u - (double)this.railPosX) < 0.0 : Math.signum(this.u - (double)this.railPosX) > 0.0);
                    }
                }
            }
        } else {
            mId = 0;
            if ((id = this.q.a(i, --j, k)) == ModBase.rails.cz) {
                this.railPosX = i;
                this.railPosY = j;
                this.railPosZ = k;
                TileEntityWideRail railTile = (TileEntityWideRail)this.q.r(i, j, k);
                if (railTile != null) {
                    mId = railTile.mId;
                }
            } else if (id == ModBase.railGag.cz && (y1 = (TileEntityGag)this.q.r(i, j, k)) != null && (y1.primary_x != 0 || y1.primary_y != 0 || y1.primary_z != 0)) {
                this.railPosX = y1.primary_x;
                this.railPosY = y1.primary_y;
                this.railPosZ = y1.primary_z;
                TileEntityWideRail railTile = (TileEntityWideRail)this.q.r(y1.primary_x, y1.primary_y, y1.primary_z);
                if (railTile != null) {
                    mId = railTile.mId;
                }
            }
            this.isOnRail = mId == 12 || mId == 13 || mId == 14;
        }
        this.isOnSlope = mId == 12 || mId == 13 || mId == 14;
        this.tangent = this.getTangent();
        if (this.isOnRail) {
            trackAngle = (float)(this.x * (double)this.tangent[0] + this.z * (double)this.tangent[1] + this.y * (double)this.tangent[2]);
            trackAngle = Math.min(Math.abs(trackAngle), this.maxSpeed) * Math.signum(trackAngle);
            this.x = this.tangent[0] * trackAngle;
            this.z = this.tangent[1] * trackAngle;
            this.y = this.tangent[2] * trackAngle;
        } else {
            trackAngle = (float)(this.x * (double)this.tangent[0] + this.z * (double)this.tangent[1]);
            if (trackAngle > this.maxSpeed) {
                trackAngle = this.maxSpeed;
            }
            if (trackAngle < -this.maxSpeed) {
                trackAngle = -this.maxSpeed;
            }
            this.x = (double)(this.tangent[0] * trackAngle) * 0.85;
            this.z = (double)(this.tangent[1] * trackAngle) * 0.85;
        }
        trackAngle = (float)Math.toDegrees(-Math.atan2(this.tangent[0], this.tangent[1]));
        float var121 = (float)kx.g((double)(trackAngle - this.A));
        float y2 = (float)Math.toDegrees(Math.atan(this.tangent[2]));
        if (var121 <= 90.0f && var121 >= -90.0f) {
            this.b(trackAngle, y2);
        } else {
            this.b((float)kx.g((double)(180.0f + trackAngle)), -y2);
        }
        this.b(this.u, this.v, this.w);
    }

    public void applyForwardMotion(float v) {
        this.g(this.rotationCos * v, 0.0, this.rotationSin * v);
    }

    @SideOnly(value=Side.CLIENT)
    public void a(double par1, double par3, double par5, float par7, float par8, int par9) {
        this.b(par1, par3, par5);
        this.b(par7, par8);
    }

    public void b_(sq player) {
        this.f((mp)player);
    }

    public void f(mp par1Entity) {
        if (!this.q.I) {
            double d1;
            double d011;
            double d2;
            EntityDrezinaStock d01;
            boolean flag = true;
            if (par1Entity instanceof EntityDrezinaStock && (this.frontCoupledCart == (d01 = (EntityDrezinaStock)par1Entity) || this.rearCoupledCart == d01 || d01.frontCoupledCart == this || d01.rearCoupledCart == this)) {
                flag = false;
            }
            if (flag && par1Entity.n != this && par1Entity.o != this && (d2 = kx.a((double)(d011 = par1Entity.u - this.u), (double)(d1 = par1Entity.w - this.w))) >= (double)0.01f) {
                d2 = kx.a((double)d2);
                d011 /= d2;
                d1 /= d2;
                double d3 = 1.0 / d2;
                if (d3 > 1.0) {
                    d3 = 1.0;
                }
                d011 *= d3;
                d1 *= d3;
                par1Entity.g(d011 *= 0.05, 0.0, d1 *= 0.05);
                this.g(-(d011 *= (double)(1.0f - this.aa)), 0.0, -(d1 *= (double)(1.0f - this.aa)));
                float sqrtMotion = (float)Math.sqrt(Math.pow(this.x, 2.0) + Math.pow(this.z, 2.0));
                if ((double)sqrtMotion > 0.05) {
                    par1Entity.a(mg.j, (int)(sqrtMotion * 50.0f));
                }
            }
        }
    }

    public void U() {
        if (this.n != null) {
            this.riderPos.onUpdate();
            this.n.b((double)this.riderPos.getX(), (double)this.riderPos.getY(), (double)this.riderPos.getZ());
        }
    }

    public boolean canBeRidden() {
        return false;
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, apa p_145780_4_) {
    }

    public aqx g(mp e1) {
        EntityDrezinaStock cart;
        if (e1 instanceof EntityDrezinaStock && (this.frontCoupledCart == (cart = (EntityDrezinaStock)e1) || this.rearCoupledCart == cart || cart.frontCoupledCart == this || cart.rearCoupledCart == this)) {
            return null;
        }
        return e1.o != this && e1.n != this && this.o != e1 && this.n != e1 ? this.E : null;
    }

    public aqx D() {
        return this.E;
    }

    public boolean shouldRiderSit() {
        return true;
    }

    public boolean canRiderInteract() {
        return false;
    }

    public double W() {
        return 1.0;
    }

    public boolean K() {
        return true;
    }

    public boolean L() {
        return true;
    }

    public mp[] an() {
        return null;
    }

    protected void a(bs nbttagcompound) {
        if (nbttagcompound.b("frontCoupledUUIDMost") && nbttagcompound.b("frontCoupledUUIDLeast")) {
            this.frontCartUUID = new UUID(nbttagcompound.f("frontCoupledUUIDMost"), nbttagcompound.f("frontCoupledUUIDLeast"));
        }
        if (nbttagcompound.b("rearCoupledUUIDMost") && nbttagcompound.b("rearCoupledUUIDLeast")) {
            this.rearCartUUID = new UUID(nbttagcompound.f("rearCoupledUUIDMost"), nbttagcompound.f("rearCoupledUUIDLeast"));
        }
        this.placer = nbttagcompound.i("placer");
        this.label = nbttagcompound.i("label");
    }

    protected void b(bs nbttagcompound) {
        if (this.frontCoupledCart != null) {
            nbttagcompound.a("frontCoupledUUIDMost", this.frontCoupledCart.getPersistentID().getMostSignificantBits());
            nbttagcompound.a("frontCoupledUUIDLeast", this.frontCoupledCart.getPersistentID().getLeastSignificantBits());
        }
        if (this.rearCoupledCart != null) {
            nbttagcompound.a("rearCoupledUUIDMost", this.rearCoupledCart.getPersistentID().getMostSignificantBits());
            nbttagcompound.a("rearCoupledUUIDLeast", this.rearCoupledCart.getPersistentID().getLeastSignificantBits());
        }
        nbttagcompound.a("placer", this.placer);
        nbttagcompound.a("label", this.label);
    }
}
