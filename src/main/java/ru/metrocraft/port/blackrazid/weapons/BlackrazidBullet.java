package ru.metrocraft.port.blackrazid.weapons;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import ru.metrocraft.port.mechanics.ProjectileDamage;
import java.util.*;

/** Native zero-gravity, constant dry-air velocity and 200-tick lifetime. No shared general faction data. */
public final class BlackrazidBullet extends Entity {
    private UUID owner;
    private float damage;
    public BlackrazidBullet(World world){super(world);setSize(.01F,.01F);}
    public BlackrazidBullet(World world,EntityPlayer player,float damage,float speed,int accuracy){
        this(world);owner=player.getUniqueID();this.damage=damage;
        setPosition(player.posX,player.posY+player.getEyeHeight()-(player.isSneaking()?.09:0),player.posZ);
        Vec3d v=direction(player.getLookVec(),speed,accuracy,world.rand);motionX=v.x;motionY=v.y;motionZ=v.z;
    }
    /** Native Rodrigues spread; normalized axis fixes the discarded normalize() result in the old source. */
    public static Vec3d direction(Vec3d look,float speed,int accuracy,Random random){
        double angle=(random.nextFloat()-.5)*accuracy*Math.PI/90;
        Vec3d axis=new Vec3d(random.nextFloat()-.5,random.nextFloat()-.5,random.nextFloat()-.5);
        axis=axis.lengthSquared()<1e-12?new Vec3d(1,0,0):axis.normalize();Vec3d v=look.normalize().scale(speed);
        return v.scale(Math.cos(angle)).add(axis.crossProduct(v).scale(Math.sin(angle))).add(axis.scale(axis.dotProduct(v)*(1-Math.cos(angle))));
    }
    @Override protected void entityInit(){}
    @Override public void onUpdate(){
        super.onUpdate();
        if(!finite(motionX) || !finite(motionY) || !finite(motionZ) || ticksExisted>200){setDead();return;}
        Vec3d start=getPositionVector(),end=start.add(motionX,motionY,motionZ);
        if(!world.isBlockLoaded(new BlockPos(end))){setDead();return;}
        if(!world.isRemote){
            EntityPlayer player=owner==null?null:world.getPlayerEntityByUUID(owner);
            if(player==null || !player.isEntityAlive() || player.isSpectator()){setDead();return;}
            RayTraceResult block=world.rayTraceBlocks(start,end,false,true,false);
            double nearest=block==null?Double.MAX_VALUE:start.squareDistanceTo(block.hitVec);Entity hit=null;
            for(Entity target:world.getEntitiesWithinAABBExcludingEntity(this,getEntityBoundingBox().expand(motionX,motionY,motionZ).grow(1))){
                if(target==player || target.isDead || !target.canBeCollidedWith())continue;
                if(target instanceof EntityPlayer && (((EntityPlayer)target).isSpectator() || ((EntityPlayer)target).capabilities.isCreativeMode || !player.canAttackPlayer((EntityPlayer)target)))continue;
                RayTraceResult intercept=target.getEntityBoundingBox().grow(.3).calculateIntercept(start,end);
                if(intercept!=null){double distance=start.squareDistanceTo(intercept.hitVec);if(distance<nearest){nearest=distance;hit=target;}}
            }
            if(hit!=null){ProjectileDamage.queue(world,getUniqueID(),hit,player,this,damage,false);setDead();return;}
            if(block!=null){setDead();return;}
        }
        setPosition(end.x,end.y,end.z);
        if(isInWater()){motionX*=.8/.99;motionY*=.8/.99;motionZ*=.8/.99;}
    }
    private static boolean finite(double v){return Double.isFinite(v) && Math.abs(v)<=32;}
    @Override protected void writeEntityToNBT(NBTTagCompound tag){if(owner!=null)tag.setUniqueId("Owner",owner);tag.setFloat("Damage",damage);tag.setInteger("Age",ticksExisted);}
    @Override protected void readEntityFromNBT(NBTTagCompound tag){owner=tag.hasUniqueId("Owner")?tag.getUniqueId("Owner"):null;float d=tag.getFloat("Damage");damage=Float.isFinite(d)?Math.max(0,Math.min(100,d)):0;ticksExisted=Math.max(0,Math.min(201,tag.getInteger("Age")));}
}
