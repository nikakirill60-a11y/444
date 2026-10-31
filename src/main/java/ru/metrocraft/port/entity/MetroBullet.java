package ru.metrocraft.port.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import ru.metrocraft.port.*;
import java.util.*;

/** Swept, server-authoritative flight using the supplied EntityBullet's drag/gravity. */
public final class MetroBullet extends Entity {
    private UUID shooter, shot;
    private float damage, gravity=.25F, power=1;
    private int age;
    private final Set<UUID> victims=new HashSet<>();
    public MetroBullet(World world){super(world);setSize(.1F,.1F);}
    public MetroBullet(World world,EntityPlayer player,ItemStackData data,Vec3d velocity,UUID shot){
        this(world);this.shooter=player.getUniqueID();this.shot=shot;
        this.damage=data.damage;this.gravity=data.gravity;this.power=data.power;
        Vec3d origin=player.getPositionEyes(1);setPosition(origin.x,origin.y,origin.z);
        motionX=velocity.x;motionY=velocity.y;motionZ=velocity.z;
    }
    public static final class ItemStackData { public float damage,gravity,power; }
    @Override protected void entityInit(){}
    public static Vec3d nextMotion(Vec3d velocity,float drag,float fallSpeed){
        return velocity.scale(drag).add(0,-.02F*fallSpeed,0);
    }
    @Override public void onUpdate(){
        super.onUpdate(); if(world.isRemote)return;
        if(++age>600 || shooter==null || shot==null){setDead();return;}
        EntityPlayer owner=world.getPlayerEntityByUUID(shooter);
        if(owner==null || !owner.isEntityAlive() || owner.isSpectator()){setDead();return;}
        Vec3d start=getPositionVector(), velocity=new Vec3d(motionX,motionY,motionZ), end=start.add(velocity);
        double distance=velocity.lengthVector();
        if(!Double.isFinite(distance) || distance>512){setDead();return;}
        // Do not force-load terrain along a fast bullet's ray.
        for(int i=0;i<=(int)Math.ceil(distance);i++){
            Vec3d p=start.add(velocity.scale(distance==0?0:Math.min(1,i/distance)));
            if(!world.isBlockLoaded(new BlockPos(p))){setDead();return;}
        }
        RayTraceResult wall=world.rayTraceBlocks(start,end,false,true,false);
        if(wall!=null)end=wall.hitVec;
        final Vec3d from=start;
        Map<Entity,Double> hits=new HashMap<>();
        AxisAlignedBB area=new AxisAlignedBB(start.x,start.y,start.z,end.x,end.y,end.z).grow(1);
        for(Entity target:world.getEntitiesWithinAABBExcludingEntity(this,area)){
            if(target==owner || target==owner.getRidingEntity() || !target.canBeCollidedWith() || victims.contains(target.getUniqueID()))continue;
            if(target instanceof EntityPlayer && ((EntityPlayer)target).isSpectator())continue;
            AxisAlignedBB box=target.getEntityBoundingBox().grow(target.getCollisionBorderSize());
            RayTraceResult intercept=box.calculateIntercept(start,end);
            if(box.contains(start))hits.put(target,0D);
            else if(intercept!=null)hits.put(target,start.squareDistanceTo(intercept.hitVec));
        }
        List<Entity> ordered=new ArrayList<>(hits.keySet());ordered.sort(Comparator.comparingDouble(hits::get));
        for(Entity target:ordered){
            if(target instanceof EntityPlayer && !owner.canAttackPlayer((EntityPlayer)target)){setDead();return;}
            victims.add(target.getUniqueID());
            ru.metrocraft.port.mechanics.ProjectileDamage.queue(world,shot,target,owner,this,damage);
            power-=1;
            if(power<=0){setDead();return;}
        }
        setPosition(end.x,end.y,end.z);
        if(wall!=null){
            ((WorldServer)world).spawnParticle(EnumParticleTypes.CRIT,end.x,end.y,end.z,2,.03,.03,.03,0);
            setDead();return;
        }
        Vec3d next=nextMotion(velocity,world.getBlockState(new BlockPos(end)).getMaterial()==net.minecraft.block.material.Material.WATER?.8F:.99F,gravity);
        motionX=next.x;motionY=next.y;motionZ=next.z;
    }
    @Override protected void writeEntityToNBT(NBTTagCompound tag){
        if(shooter!=null)tag.setUniqueId("Shooter",shooter);if(shot!=null)tag.setUniqueId("Shot",shot);
        tag.setFloat("Damage",damage);tag.setFloat("Gravity",gravity);tag.setFloat("Power",power);tag.setInteger("Age",age);
    }
    @Override protected void readEntityFromNBT(NBTTagCompound tag){
        shooter=tag.hasUniqueId("Shooter")?tag.getUniqueId("Shooter"):null;shot=tag.hasUniqueId("Shot")?tag.getUniqueId("Shot"):null;
        damage=tag.getFloat("Damage");gravity=tag.getFloat("Gravity");power=tag.getFloat("Power");age=tag.getInteger("Age");
        // In-flight bullets are intentionally not replayed after a server restart.
        setDead();
    }
}
