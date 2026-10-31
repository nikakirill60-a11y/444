/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.network.ByteBufUtils
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.EntityFX
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EntityDamageSourceIndirect
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 */
package com.flansmod.common.guns;

import com.flansmod.client.FlansModClient;
import com.flansmod.client.debug.EntityDebugDot;
import com.flansmod.common.FlansMod;
import com.flansmod.common.PlayerData;
import com.flansmod.common.PlayerHandler;
import com.flansmod.common.RotatedAxes;
import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.driveables.EntityPlane;
import com.flansmod.common.driveables.EntitySeat;
import com.flansmod.common.driveables.EntityVehicle;
import com.flansmod.common.driveables.mechas.EntityMecha;
import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.EntityAAGun;
import com.flansmod.common.guns.EntityDamageSourceGun;
import com.flansmod.common.guns.EntityGrenade;
import com.flansmod.common.guns.EntityMG;
import com.flansmod.common.guns.EntityShootable;
import com.flansmod.common.guns.FlansModExplosion;
import com.flansmod.common.guns.raytracing.BlockHit;
import com.flansmod.common.guns.raytracing.BulletHit;
import com.flansmod.common.guns.raytracing.DriveableHit;
import com.flansmod.common.guns.raytracing.EntityHit;
import com.flansmod.common.guns.raytracing.EnumHitboxType;
import com.flansmod.common.guns.raytracing.PlayerBulletHit;
import com.flansmod.common.guns.raytracing.PlayerHitbox;
import com.flansmod.common.guns.raytracing.PlayerSnapshot;
import com.flansmod.common.network.PacketFlak;
import com.flansmod.common.teams.Team;
import com.flansmod.common.teams.TeamsManager;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.vector.Vector3f;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityBullet
extends EntityShootable
implements IEntityAdditionalSpawnData {
    private static int bulletLife = 600;
    public Entity owner;
    private int ticksInAir = 0;
    public BulletType type;
    public InfoType firedFrom;
    public float damage;
    public boolean shotgun = false;
    public int pingOfShooter = 0;
    public static Random bulletRandom = new Random();
    public Entity lockedOnTo;
    public float penetratingPower;

    public EntityBullet(World world) {
        super(world);
        this.func_70105_a(0.5f, 0.5f);
    }

    private EntityBullet(World world, EntityLivingBase shooter, float gunDamage, BulletType bulletType, InfoType shotFrom) {
        this(world);
        this.owner = shooter;
        if (shooter instanceof EntityPlayerMP) {
            this.pingOfShooter = ((EntityPlayerMP)shooter).field_71138_i;
        }
        this.type = bulletType;
        this.firedFrom = shotFrom;
        this.damage = gunDamage;
        this.penetratingPower = this.type.penetratingPower;
    }

    public EntityBullet(World world, EntityLivingBase shooter, float spread, float gunDamage, BulletType type1, float speed, boolean shot, InfoType shotFrom) {
        this(world, Vec3.func_72443_a((double)shooter.field_70165_t, (double)(shooter.field_70163_u + (double)shooter.func_70047_e()), (double)shooter.field_70161_v), shooter.field_70177_z, shooter.field_70125_A, shooter, spread, gunDamage, type1, speed, shotFrom);
        this.shotgun = shot;
    }

    public EntityBullet(World world, Vec3 origin, float yaw, float pitch, EntityLivingBase shooter, float spread, float gunDamage, BulletType type1, InfoType shotFrom) {
        this(world, origin, yaw, pitch, shooter, spread, gunDamage, type1, 3.0f, shotFrom);
    }

    public EntityBullet(World world, Vec3 origin, float yaw, float pitch, EntityLivingBase shooter, float spread, float gunDamage, BulletType type1, float speed, InfoType shotFrom) {
        this(world, shooter, gunDamage, type1, shotFrom);
        this.func_70012_b(origin.field_72450_a, origin.field_72448_b, origin.field_72449_c, yaw, pitch);
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70129_M = 0.0f;
        this.field_70159_w = -MathHelper.func_76126_a((float)(this.field_70177_z / 180.0f * (float)Math.PI)) * MathHelper.func_76134_b((float)(this.field_70125_A / 180.0f * (float)Math.PI));
        this.field_70179_y = MathHelper.func_76134_b((float)(this.field_70177_z / 180.0f * (float)Math.PI)) * MathHelper.func_76134_b((float)(this.field_70125_A / 180.0f * (float)Math.PI));
        this.field_70181_x = -MathHelper.func_76126_a((float)(this.field_70125_A / 180.0f * 3.141593f));
        this.setArrowHeading(this.field_70159_w, this.field_70181_x, this.field_70179_y, spread / 2.0f, speed);
    }

    public EntityBullet(World world, Vector3f origin, Vector3f direction, EntityLivingBase shooter, float spread, float gunDamage, BulletType type1, float speed, InfoType shotFrom) {
        this(world, shooter, gunDamage, type1, shotFrom);
        this.damage = gunDamage;
        this.func_70107_b(origin.x, origin.y, origin.z);
        this.field_70159_w = direction.x;
        this.field_70181_x = direction.y;
        this.field_70179_y = direction.z;
        this.setArrowHeading(this.field_70159_w, this.field_70181_x, this.field_70179_y, spread, speed);
    }

    public EntityBullet(World world, Vec3 origin, float yaw, float pitch, double motX, double motY, double motZ, EntityLivingBase shooter, float gunDamage, BulletType type1, InfoType shotFrom) {
        this(world, shooter, gunDamage, type1, shotFrom);
        this.func_70012_b(origin.field_72450_a, origin.field_72448_b, origin.field_72449_c, yaw, pitch);
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70129_M = 0.0f;
        this.field_70159_w = motX;
        this.field_70181_x = motY;
        this.field_70179_y = motZ;
    }

    protected void func_70088_a() {
    }

    public void setArrowHeading(double d, double d1, double d2, float spread, float speed) {
        float f2 = MathHelper.func_76133_a((double)(d * d + d1 * d1 + d2 * d2));
        d /= (double)f2;
        d1 /= (double)f2;
        d2 /= (double)f2;
        d *= (double)speed;
        d1 *= (double)speed;
        d2 *= (double)speed;
        this.field_70159_w = d += this.field_70146_Z.nextGaussian() * 0.005 * (double)spread * (double)speed;
        this.field_70181_x = d1 += this.field_70146_Z.nextGaussian() * 0.005 * (double)spread * (double)speed;
        this.field_70179_y = d2 += this.field_70146_Z.nextGaussian() * 0.005 * (double)spread * (double)speed;
        float f3 = MathHelper.func_76133_a((double)(d * d + d2 * d2));
        this.field_70126_B = this.field_70177_z = (float)(Math.atan2(d, d2) * 180.0 / 3.1415927410125732);
        this.field_70127_C = this.field_70125_A = (float)(Math.atan2(d1, f3) * 180.0 / 3.1415927410125732);
        this.getLockOnTarget();
    }

    private void getLockOnTarget() {
        if (this.type.lockOnToPlanes || this.type.lockOnToVehicles || this.type.lockOnToMechas || this.type.lockOnToLivings || this.type.lockOnToPlayers) {
            Vector3f motionVec = new Vector3f(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            Entity closestEntity = null;
            float closestAngle = this.type.maxLockOnAngle * (float)Math.PI / 180.0f;
            for (Object obj : this.field_70170_p.field_72996_f) {
                Vector3f relPosVec;
                float angle;
                Entity entity = (Entity)obj;
                if (!(this.type.lockOnToMechas && entity instanceof EntityMecha || this.type.lockOnToVehicles && entity instanceof EntityVehicle || this.type.lockOnToPlanes && entity instanceof EntityPlane || this.type.lockOnToPlayers && entity instanceof EntityPlayer) && (!this.type.lockOnToLivings || !(entity instanceof EntityLivingBase)) || !((angle = Math.abs(Vector3f.angle(motionVec, relPosVec = new Vector3f(entity.field_70165_t - this.field_70165_t, entity.field_70163_u - this.field_70163_u, entity.field_70161_v - this.field_70161_v)))) < closestAngle)) continue;
                closestEntity = entity;
                closestAngle = angle;
            }
            if (closestEntity != null) {
                this.lockedOnTo = closestEntity;
            }
        }
    }

    public void func_70016_h(double d, double d1, double d2) {
        this.field_70159_w = d;
        this.field_70181_x = d1;
        this.field_70179_y = d2;
        if (this.field_70127_C == 0.0f && this.field_70126_B == 0.0f) {
            float f = MathHelper.func_76133_a((double)(d * d + d2 * d2));
            this.field_70126_B = this.field_70177_z = (float)(Math.atan2(d, d2) * 180.0 / 3.1415927410125732);
            this.field_70127_C = this.field_70125_A = (float)(Math.atan2(d1, f) * 180.0 / 3.1415927410125732);
            this.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
        }
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        ++this.ticksInAir;
        if (this.ticksInAir > this.type.fuse && this.type.fuse > 0 && !this.field_70128_L) {
            this.func_70106_y();
        }
        if (this.field_70173_aa > bulletLife) {
            this.func_70106_y();
        }
        if (this.field_70128_L) {
            return;
        }
        ArrayList<BulletHit> hits = new ArrayList<BulletHit>();
        Vector3f origin = new Vector3f(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        Vector3f motion = new Vector3f(this.field_70159_w, this.field_70181_x, this.field_70179_y);
        float speed = motion.length();
        for (int i = 0; i < this.field_70170_p.field_72996_f.size(); ++i) {
            MovingObjectPosition mop;
            Object obj = this.field_70170_p.field_72996_f.get(i);
            if (obj instanceof EntityDriveable) {
                EntityDriveable driveable = (EntityDriveable)obj;
                if (driveable.isDead() || driveable.isPartOfThis(this.owner) || !(this.func_70032_d(driveable) <= driveable.getDriveableType().bulletDetectionRadius + speed)) continue;
                ArrayList<BulletHit> driveableHits = driveable.attackFromBullet(origin, motion);
                hits.addAll(driveableHits);
                continue;
            }
            if (obj instanceof EntityPlayer) {
                MovingObjectPosition mop2;
                EntityPlayer player = (EntityPlayer)obj;
                PlayerData data = PlayerHandler.getPlayerData(player);
                boolean shouldDoNormalHitDetect = false;
                if (data != null) {
                    PlayerSnapshot snapshot;
                    if (player.field_70128_L || data.team == Team.spectators || player == this.owner && this.ticksInAir < 20) continue;
                    int snapshotToTry = this.pingOfShooter / 50;
                    if (snapshotToTry >= data.snapshots.length) {
                        snapshotToTry = data.snapshots.length - 1;
                    }
                    if ((snapshot = data.snapshots[snapshotToTry]) == null) {
                        snapshot = data.snapshots[0];
                    }
                    if (snapshot == null) {
                        shouldDoNormalHitDetect = true;
                    } else {
                        ArrayList<BulletHit> playerHits = snapshot.raytrace(origin, motion);
                        hits.addAll(playerHits);
                    }
                }
                if (data != null && !shouldDoNormalHitDetect || (mop2 = player.field_70121_D.func_72327_a(origin.toVec3(), Vec3.func_72443_a((double)(this.field_70165_t + this.field_70159_w), (double)(this.field_70163_u + this.field_70181_x), (double)(this.field_70161_v + this.field_70179_y)))) == null) continue;
                Vector3f hitPoint = new Vector3f(mop2.field_72307_f.field_72450_a - this.field_70165_t, mop2.field_72307_f.field_72448_b - this.field_70163_u, mop2.field_72307_f.field_72449_c - this.field_70161_v);
                float hitLambda = 1.0f;
                if (motion.x != 0.0f) {
                    hitLambda = hitPoint.x / motion.x;
                } else if (motion.y != 0.0f) {
                    hitLambda = hitPoint.y / motion.y;
                } else if (motion.z != 0.0f) {
                    hitLambda = hitPoint.z / motion.z;
                }
                if (hitLambda < 0.0f) {
                    hitLambda = -hitLambda;
                }
                hits.add(new PlayerBulletHit(new PlayerHitbox(player, new RotatedAxes(), new Vector3f(), new Vector3f(), new Vector3f(), EnumHitboxType.BODY), hitLambda));
                continue;
            }
            Entity entity = (Entity)obj;
            if (entity == this || entity == this.owner || entity.field_70128_L || !(entity instanceof EntityLivingBase) && !(entity instanceof EntityAAGun) && !(entity instanceof EntityGrenade) || (mop = entity.field_70121_D.func_72327_a(origin.toVec3(), Vec3.func_72443_a((double)(this.field_70165_t + this.field_70159_w), (double)(this.field_70163_u + this.field_70181_x), (double)(this.field_70161_v + this.field_70179_y)))) == null) continue;
            Vector3f hitPoint = new Vector3f(mop.field_72307_f.field_72450_a - this.field_70165_t, mop.field_72307_f.field_72448_b - this.field_70163_u, mop.field_72307_f.field_72449_c - this.field_70161_v);
            float hitLambda = 1.0f;
            if (motion.x != 0.0f) {
                hitLambda = hitPoint.x / motion.x;
            } else if (motion.y != 0.0f) {
                hitLambda = hitPoint.y / motion.y;
            } else if (motion.z != 0.0f) {
                hitLambda = hitPoint.z / motion.z;
            }
            if (hitLambda < 0.0f) {
                hitLambda = -hitLambda;
            }
            hits.add(new EntityHit(entity, hitLambda));
        }
        Vec3 posVec = Vec3.func_72443_a((double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v);
        Vec3 nextPosVec = Vec3.func_72443_a((double)(this.field_70165_t + this.field_70159_w), (double)(this.field_70163_u + this.field_70181_x), (double)(this.field_70161_v + this.field_70179_y));
        MovingObjectPosition hit = this.field_70170_p.func_147447_a(posVec, nextPosVec, false, true, true);
        posVec = Vec3.func_72443_a((double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v);
        if (hit != null) {
            Vec3 hitVec = posVec.func_72444_a(hit.field_72307_f);
            float lambda = 1.0f;
            if (this.field_70159_w != 0.0) {
                lambda = (float)(hitVec.field_72450_a / this.field_70159_w);
            } else if (this.field_70181_x != 0.0) {
                lambda = (float)(hitVec.field_72448_b / this.field_70181_x);
            } else if (this.field_70179_y != 0.0) {
                lambda = (float)(hitVec.field_72449_c / this.field_70179_y);
            }
            if (lambda < 0.0f) {
                lambda = -lambda;
            }
            hits.add(new BlockHit(hit, lambda));
        }
        if (!hits.isEmpty()) {
            Collections.sort(hits);
            for (BulletHit bulletHit : hits) {
                if (bulletHit instanceof DriveableHit) {
                    DriveableHit driveableHit = (DriveableHit)bulletHit;
                    this.penetratingPower = driveableHit.driveable.bulletHit(this, driveableHit, this.penetratingPower);
                    if (FlansMod.DEBUG) {
                        this.field_70170_p.func_72838_d((Entity)new EntityDebugDot(this.field_70170_p, new Vector3f(this.field_70165_t + this.field_70159_w * (double)driveableHit.intersectTime, this.field_70163_u + this.field_70181_x * (double)driveableHit.intersectTime, this.field_70161_v + this.field_70179_y * (double)driveableHit.intersectTime), 1000, 0.0f, 0.0f, 1.0f));
                    }
                } else if (bulletHit instanceof PlayerBulletHit) {
                    PlayerBulletHit playerHit = (PlayerBulletHit)bulletHit;
                    this.penetratingPower = playerHit.hitbox.hitByBullet(this, this.penetratingPower);
                    if (FlansMod.DEBUG) {
                        this.field_70170_p.func_72838_d((Entity)new EntityDebugDot(this.field_70170_p, new Vector3f(this.field_70165_t + this.field_70159_w * (double)playerHit.intersectTime, this.field_70163_u + this.field_70181_x * (double)playerHit.intersectTime, this.field_70161_v + this.field_70179_y * (double)playerHit.intersectTime), 1000, 1.0f, 0.0f, 0.0f));
                    }
                } else if (bulletHit instanceof EntityHit) {
                    EntityHit entityHit = (EntityHit)bulletHit;
                    if (entityHit.entity.func_70097_a(this.getBulletDamage(false), this.damage * (float)this.type.damageVsLiving) && entityHit.entity instanceof EntityLivingBase) {
                        EntityLivingBase living = (EntityLivingBase)entityHit.entity;
                        for (PotionEffect effect : this.type.hitEffects) {
                            living.func_70690_d(new PotionEffect(effect));
                        }
                        ++living.field_70720_be;
                        living.field_70172_ad = living.field_70771_an / 2;
                    }
                    if (this.type.setEntitiesOnFire) {
                        entityHit.entity.func_70015_d(20);
                    }
                    this.penetratingPower -= 1.0f;
                    if (FlansMod.DEBUG) {
                        this.field_70170_p.func_72838_d((Entity)new EntityDebugDot(this.field_70170_p, new Vector3f(this.field_70165_t + this.field_70159_w * (double)entityHit.intersectTime, this.field_70163_u + this.field_70181_x * (double)entityHit.intersectTime, this.field_70161_v + this.field_70179_y * (double)entityHit.intersectTime), 1000, 1.0f, 1.0f, 0.0f));
                    }
                } else if (bulletHit instanceof BlockHit) {
                    BlockHit blockHit = (BlockHit)bulletHit;
                    MovingObjectPosition raytraceResult = blockHit.raytraceResult;
                    int xTile = raytraceResult.field_72311_b;
                    int yTile = raytraceResult.field_72312_c;
                    int zTile = raytraceResult.field_72309_d;
                    if (FlansMod.DEBUG) {
                        this.field_70170_p.func_72838_d((Entity)new EntityDebugDot(this.field_70170_p, new Vector3f(raytraceResult.field_72307_f.field_72450_a, raytraceResult.field_72307_f.field_72448_b, raytraceResult.field_72307_f.field_72449_c), 1000, 0.0f, 1.0f, 0.0f));
                    }
                    Block block = this.field_70170_p.func_147439_a(xTile, yTile, zTile);
                    Material mat = block.func_149688_o();
                    if (this.type.breaksGlass && mat == Material.field_151592_s && TeamsManager.canBreakGlass) {
                        this.field_70170_p.func_147468_f(xTile, yTile, zTile);
                        FlansMod.proxy.playBlockBreakSound(xTile, yTile, zTile, block);
                    }
                    this.func_70107_b(hit.field_72307_f.field_72450_a, hit.field_72307_f.field_72448_b, hit.field_72307_f.field_72449_c);
                    this.func_70106_y();
                    break;
                }
                if (!(this.penetratingPower <= 0.0f) && (!this.type.explodeOnImpact || this.ticksInAir <= 1)) continue;
                this.func_70107_b(this.field_70165_t + this.field_70159_w * (double)bulletHit.intersectTime, this.field_70163_u + this.field_70181_x * (double)bulletHit.intersectTime, this.field_70161_v + this.field_70179_y * (double)bulletHit.intersectTime);
                this.func_70106_y();
                break;
            }
        }
        float drag = 0.99f;
        float gravity = 0.02f;
        if (this.func_70090_H()) {
            for (int i = 0; i < 4; ++i) {
                float bubbleMotion = 0.25f;
                this.field_70170_p.func_72869_a("bubble", this.field_70165_t - this.field_70159_w * (double)bubbleMotion, this.field_70163_u - this.field_70181_x * (double)bubbleMotion, this.field_70161_v - this.field_70179_y * (double)bubbleMotion, this.field_70159_w, this.field_70181_x, this.field_70179_y);
            }
            drag = 0.8f;
        }
        this.field_70159_w *= (double)drag;
        this.field_70181_x *= (double)drag;
        this.field_70179_y *= (double)drag;
        this.field_70181_x -= (double)(gravity * this.type.fallSpeed);
        if (this.lockedOnTo != null) {
            double dX = this.lockedOnTo.field_70165_t - this.field_70165_t;
            double dY = this.lockedOnTo.field_70163_u - this.field_70163_u;
            double dZ = this.lockedOnTo.field_70161_v - this.field_70161_v;
            double dXYZ = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
            Vector3f relPosVec = new Vector3f(this.lockedOnTo.field_70165_t - this.field_70165_t, this.lockedOnTo.field_70163_u - this.field_70163_u, this.lockedOnTo.field_70161_v - this.field_70161_v);
            float angle = Math.abs(Vector3f.angle(motion, relPosVec));
            double lockOnPull = angle / 2.0f * this.type.lockOnForce;
            this.field_70159_w += lockOnPull * dX / dXYZ;
            this.field_70181_x += lockOnPull * dY / dXYZ;
            this.field_70179_y += lockOnPull * dZ / dXYZ;
        }
        this.field_70165_t += this.field_70159_w;
        this.field_70163_u += this.field_70181_x;
        this.field_70161_v += this.field_70179_y;
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        float motionXZ = MathHelper.func_76133_a((double)(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y));
        this.field_70177_z = (float)(Math.atan2(this.field_70159_w, this.field_70179_y) * 180.0 / 3.1415927410125732);
        this.field_70125_A = (float)(Math.atan2(this.field_70181_x, motionXZ) * 180.0 / 3.1415927410125732);
        while (this.field_70125_A - this.field_70127_C < -180.0f) {
            this.field_70127_C -= 360.0f;
        }
        while (this.field_70125_A - this.field_70127_C >= 180.0f) {
            this.field_70127_C += 360.0f;
        }
        while (this.field_70177_z - this.field_70126_B < -180.0f) {
            this.field_70126_B -= 360.0f;
        }
        while (this.field_70177_z - this.field_70126_B >= 180.0f) {
            this.field_70126_B += 360.0f;
        }
        this.field_70125_A = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * 0.2f;
        this.field_70177_z = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * 0.2f;
        if (this.type.trailParticles && this.field_70170_p.field_72995_K && this.ticksInAir > 1) {
            this.spawnParticles();
        }
        if (this.field_70170_p.field_72995_K) {
            this.func_70066_B();
        }
    }

    @SideOnly(value=Side.CLIENT)
    private void spawnParticles() {
        double dX = (this.field_70165_t - this.field_70169_q) / 10.0;
        double dY = (this.field_70163_u - this.field_70167_r) / 10.0;
        double dZ = (this.field_70161_v - this.field_70166_s) / 10.0;
        float spread = 0.1f;
        for (int i = 0; i < 10; ++i) {
            EntityFX particle = FlansModClient.getParticle(this.type.trailParticleType, this.field_70170_p, this.field_70169_q + dX * (double)i + this.field_70146_Z.nextGaussian() * (double)spread, this.field_70167_r + dY * (double)i + this.field_70146_Z.nextGaussian() * (double)spread, this.field_70166_s + dZ * (double)i + this.field_70146_Z.nextGaussian() * (double)spread);
            if (particle == null || !Minecraft.func_71410_x().field_71474_y.field_74347_j) continue;
            particle.field_70155_l = 100.0;
        }
    }

    public DamageSource getBulletDamage(boolean headshot) {
        if (this.owner instanceof EntityPlayer) {
            return new EntityDamageSourceGun(this.type.shortName, this, (EntityPlayer)this.owner, this.firedFrom, headshot).func_76349_b();
        }
        return new EntityDamageSourceIndirect(this.type.shortName, (Entity)this, this.owner).func_76349_b();
    }

    private boolean isPartOfOwner(Entity entity) {
        if (this.owner == null) {
            return false;
        }
        if (entity == this.owner || entity == this.owner.field_70153_n || entity == this.owner.field_70154_o) {
            return true;
        }
        if (this.owner instanceof EntityPlayer) {
            if (PlayerHandler.getPlayerData((EntityPlayer)this.owner, this.field_70170_p.field_72995_K ? Side.CLIENT : Side.SERVER) == null) {
                return false;
            }
            EntityMG mg = PlayerHandler.getPlayerData((EntityPlayer)((EntityPlayer)this.owner), (Side)(this.field_70170_p.field_72995_K ? Side.CLIENT : Side.SERVER)).mountingGun;
            if (mg != null && mg == entity) {
                return true;
            }
        }
        return this.owner.field_70154_o instanceof EntitySeat && (((EntitySeat)this.owner.field_70154_o).driveable == null || ((EntitySeat)this.owner.field_70154_o).driveable.isPartOfThis(entity));
    }

    public void func_70106_y() {
        if (this.field_70128_L) {
            return;
        }
        super.func_70106_y();
        if (this.field_70170_p.field_72995_K) {
            return;
        }
        if (this.type.explosionRadius > 0.0f) {
            if (this.owner instanceof EntityPlayer) {
                new FlansModExplosion(this.field_70170_p, this, (EntityPlayer)this.owner, this.firedFrom, this.field_70165_t, this.field_70163_u, this.field_70161_v, this.type.explosionRadius, TeamsManager.explosions);
            } else {
                this.field_70170_p.func_72876_a((Entity)this, this.field_70165_t, this.field_70163_u, this.field_70161_v, this.type.explosionRadius, TeamsManager.explosions);
            }
        }
        if (this.type.fireRadius > 0.0f) {
            for (float i = -this.type.fireRadius; i < this.type.fireRadius; i += 1.0f) {
                for (float k = -this.type.fireRadius; k < this.type.fireRadius; k += 1.0f) {
                    for (int j = -1; j < 1; ++j) {
                        if (this.field_70170_p.func_147439_a((int)(this.field_70165_t + (double)i), (int)(this.field_70163_u + (double)j), (int)(this.field_70161_v + (double)k)).func_149688_o() != Material.field_151579_a) continue;
                        this.field_70170_p.func_147449_b((int)(this.field_70165_t + (double)i), (int)(this.field_70163_u + (double)j), (int)(this.field_70161_v + (double)k), (Block)Blocks.field_150480_ab);
                    }
                }
            }
        }
        if (this.type.flak > 0) {
            FlansMod.getPacketHandler().sendToAllAround(new PacketFlak(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.type.flak, this.type.flakParticles), this.field_70165_t, this.field_70163_u, this.field_70161_v, 200.0f, this.field_71093_bK);
        }
        if (this.type.dropItemOnHit != null) {
            String itemName = this.type.dropItemOnHit;
            int damage = 0;
            if (itemName.contains(".")) {
                damage = Integer.parseInt(itemName.split("\\.")[1]);
                itemName = itemName.split("\\.")[0];
            }
            ItemStack dropStack = InfoType.getRecipeElement(itemName, damage);
            this.func_70099_a(dropStack, 1.0f);
        }
    }

    public void func_70014_b(NBTTagCompound tag) {
        tag.func_74778_a("type", this.type.shortName);
        if (this.owner == null) {
            tag.func_74778_a("owner", "null");
        } else {
            tag.func_74778_a("owner", this.owner.func_70005_c_());
        }
    }

    public void func_70037_a(NBTTagCompound tag) {
        String typeString = tag.func_74779_i("type");
        String ownerName = tag.func_74779_i("owner");
        if (typeString != null) {
            this.type = BulletType.getBullet(typeString);
        }
        if (ownerName != null && !ownerName.equals("null")) {
            this.owner = FMLCommonHandler.instance().getMinecraftServerInstance().func_71203_ab().func_152612_a(ownerName);
        }
    }

    public float func_70053_R() {
        return this.type.hitBoxSize;
    }

    public int func_70070_b(float par1) {
        int j;
        if (this.type.hasLight) {
            return 0xF000F0;
        }
        int i = MathHelper.func_76128_c((double)this.field_70165_t);
        if (this.field_70170_p.func_72899_e(i, 0, j = MathHelper.func_76128_c((double)this.field_70161_v))) {
            double d0 = (this.field_70121_D.field_72337_e - this.field_70121_D.field_72338_b) * 0.66;
            int k = MathHelper.func_76128_c((double)(this.field_70163_u - (double)this.field_70129_M + d0));
            return this.field_70170_p.func_72802_i(i, k, j, 0);
        }
        return 0;
    }

    public void writeSpawnData(ByteBuf data) {
        data.writeDouble(this.field_70159_w);
        data.writeDouble(this.field_70181_x);
        data.writeDouble(this.field_70179_y);
        data.writeInt(this.lockedOnTo == null ? -1 : this.lockedOnTo.func_145782_y());
        ByteBufUtils.writeUTF8String((ByteBuf)data, (String)this.type.shortName);
        if (this.owner == null) {
            ByteBufUtils.writeUTF8String((ByteBuf)data, (String)"null");
        } else {
            ByteBufUtils.writeUTF8String((ByteBuf)data, (String)this.owner.func_70005_c_());
        }
    }

    public void readSpawnData(ByteBuf data) {
        try {
            this.field_70159_w = data.readDouble();
            this.field_70181_x = data.readDouble();
            this.field_70179_y = data.readDouble();
            int lockedOnToID = data.readInt();
            if (lockedOnToID != -1) {
                this.lockedOnTo = this.field_70170_p.func_73045_a(lockedOnToID);
            }
            this.type = BulletType.getBullet(ByteBufUtils.readUTF8String((ByteBuf)data));
            this.penetratingPower = this.type.penetratingPower;
            String name = ByteBufUtils.readUTF8String((ByteBuf)data);
            for (Object obj : this.field_70170_p.field_72996_f) {
                if (!((Entity)obj).func_70005_c_().equals(name)) continue;
                this.owner = (EntityPlayer)obj;
            }
        }
        catch (Exception e) {
            FlansMod.log("Failed to read bullet owner from server.");
            super.func_70106_y();
            e.printStackTrace();
        }
    }

    public boolean func_70027_ad() {
        return false;
    }
}

