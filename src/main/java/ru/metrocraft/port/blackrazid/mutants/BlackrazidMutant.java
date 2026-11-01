package ru.metrocraft.port.blackrazid.mutants;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.metrocraft.port.MetroPort;

/** Separate BlackRazid entities; no replacement of legacy/MCMOD creatures. */
public abstract class BlackrazidMutant extends EntityMob {
    public BlackrazidMutant(World world){super(world);setPathPriority(PathNodeType.WATER,0);}
    public abstract String species();
    public boolean timid(){return false;}
    protected void profile(double health,double damage,double speed){
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(damage);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32);
    }
    @Override protected void initEntityAI(){
        if(timid())tasks.addTask(1,new EntityAIPanic(this,.38/.45));
        else{
            tasks.addTask(2,new EntityAIAttackMelee(this,1,false));
            targetTasks.addTask(1,new EntityAIHurtByTarget(this,true));
            targetTasks.addTask(2,new EntityAINearestAttackableTarget<EntityPlayer>(this,EntityPlayer.class,0,true,false,null));
        }
        tasks.addTask(4,new EntityAILookIdle(this));
        tasks.addTask(5,new EntityAIWander(this,.5));
        tasks.addTask(6,new EntityAIWatchClosest(this,EntityPlayer.class,8));
    }
    protected boolean looking(EntityPlayer p,double heightFraction){
        if(p==null || !p.isEntityAlive() || p.isSpectator())return false;
        return MutantRules.looking(p.getLookVec(),new Vec3d(posX-p.posX,posY+height*heightFraction-p.posY-p.getEyeHeight(),posZ-p.posZ),p.canEntityBeSeen(this));
    }
    @Override public void onEntityUpdate(){super.onEntityUpdate();if(!world.isRemote && canBreatheUnderwater() && isInWater())setAir(300);}
    @Override public boolean canBreatheUnderwater(){return false;}
    protected SoundEvent sound(String suffix){return MetroPort.SOUNDS.get("blackrazid_"+species()+"_"+suffix);}
    @Override protected SoundEvent getAmbientSound(){return sound("alive");}
    @Override protected SoundEvent getHurtSound(DamageSource source){return sound("hit");}
    @Override protected SoundEvent getDeathSound(){return sound("death");}
    @Override protected ResourceLocation getLootTable(){return null;}
}
