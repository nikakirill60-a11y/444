package ru.metrocraft.port.blackrazid.grenades;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.event.ForgeEventFactory;
import java.util.*;

public final class GrenadeEntity extends EntityThrowable {
    private static final DataParameter<Boolean> BIG=EntityDataManager.createKey(GrenadeEntity.class,DataSerializers.BOOLEAN);
    private int age;
    public GrenadeEntity(World world){super(world);setSize(.25F,.25F);}
    public GrenadeEntity(World world,EntityPlayer owner,boolean big){super(world,owner);setSize(.25F,.25F);dataManager.set(BIG,big);}
    @Override protected void entityInit(){super.entityInit();dataManager.register(BIG,false);}
    public boolean isBig(){return dataManager.get(BIG);}
    @Override protected float getGravityVelocity(){return .05F;}
    @Override public void onUpdate(){if(!world.isRemote && !world.isBlockLoaded(new BlockPos(posX+motionX,posY+motionY,posZ+motionZ))){setDead();return;}super.onUpdate();if(!world.isRemote && !isDead && ++age>=GrenadeRules.FUSE){explode();setDead();}}
    @Override protected void onImpact(RayTraceResult hit){
        if(hit.entityHit!=null){motionX*=-.5;motionY*=-.5;motionZ*=-.5;return;}
        if(hit.sideHit==null)return;
        switch(hit.sideHit.getAxis()){
            case X:motionX*=-.5;motionY*=.8;motionZ*=.5;break;
            case Z:motionX*=.5;motionY*=.8;motionZ*=-.5;break;
            default:motionX*=Math.abs(motionY)>.1?.8:.95;motionZ*=Math.abs(motionY)>.1?.8:.95;motionY=Math.abs(motionY)>.1?-motionY*.5:0;
        }
        setPosition(hit.hitVec.x+hit.sideHit.getXOffset()*.03,hit.hitVec.y+hit.sideHit.getYOffset()*.03,hit.hitVec.z+hit.sideHit.getZOffset()*.03);velocityChanged=true;
    }
    private void explode(){
        if(world.isRemote)return;
        Vec3d center=getPositionVector();EntityLivingBase owner=getThrower();
        Explosion explosion=new Explosion(world,this,posX,posY,posZ,5.5F,false,false);
        if(ForgeEventFactory.onExplosionStart(world,explosion))return;
        List<Entity> targets=new ArrayList<>(world.getEntitiesWithinAABBExcludingEntity(this,getEntityBoundingBox().grow(GrenadeRules.RADIUS)));
        ForgeEventFactory.onExplosionDetonate(world,explosion,targets,GrenadeRules.RADIUS);
        DamageSource source=new EntityDamageSourceIndirect("explosion.player",this,owner).setExplosion();
        for(Entity target:targets){
            if(target.isDead || target.isImmuneToExplosions())continue;
            if(target instanceof EntityPlayer){
                EntityPlayer p=(EntityPlayer)target;if(p.isSpectator() || p.capabilities.isCreativeMode)continue;
                if(world.getMinecraftServer()!=null && !world.getMinecraftServer().isPVPEnabled())continue;
                if(owner instanceof EntityPlayer && !((EntityPlayer)owner).canAttackPlayer(p))continue;
            }
            double distance=target.getDistance(posX,posY,posZ);if(distance>GrenadeRules.RADIUS)continue;
            double exposure=world.getBlockDensity(center,target.getEntityBoundingBox());int damage=GrenadeRules.damage(isBig(),distance,exposure);
            if(damage>0)target.attackEntityFrom(source,damage);
            Vec3d delta=target.getPositionVector().add(0,target.getEyeHeight(),0).subtract(center);
            if(delta.lengthSquared()>1e-8){double impulse=(1-distance/GrenadeRules.RADIUS)*exposure;Vec3d push=delta.normalize().scale(impulse);target.motionX+=push.x;target.motionY+=push.y;target.motionZ+=push.z;target.velocityChanged=true;}
        }
        world.playSound(null,posX,posY,posZ,net.minecraft.init.SoundEvents.ENTITY_GENERIC_EXPLODE,SoundCategory.BLOCKS,4,.7F);
        if(world instanceof WorldServer)((WorldServer)world).spawnParticle(EnumParticleTypes.EXPLOSION_HUGE,posX,posY,posZ,1,0,0,0,0);
        // Do NOT call newExplosion/doExplosionB: the original override only played effects.
    }
    @Override public void writeEntityToNBT(NBTTagCompound tag){super.writeEntityToNBT(tag);tag.setBoolean("BlackrazidBig",isBig());tag.setInteger("BlackrazidAge",age);}
    @Override public void readEntityFromNBT(NBTTagCompound tag){super.readEntityFromNBT(tag);dataManager.set(BIG,tag.getBoolean("BlackrazidBig"));age=GrenadeRules.age(tag.getInteger("BlackrazidAge"));}
}
