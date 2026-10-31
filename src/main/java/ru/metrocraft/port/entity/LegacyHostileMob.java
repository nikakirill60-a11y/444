package ru.metrocraft.port.entity;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** 1.12 task equivalent of the legacy EntityMob's removed chase/attack/wander AI. */
public class LegacyHostileMob extends EntityMob {
    public LegacyHostileMob(World world){super(world);}
    @Override protected void initEntityAI(){
        tasks.addTask(0,new EntityAISwimming(this));
        tasks.addTask(2,new EntityAIAttackMelee(this,1,false));
        tasks.addTask(5,new EntityAIMoveTowardsRestriction(this,1));
        tasks.addTask(7,new EntityAIWanderAvoidWater(this,1));
        tasks.addTask(8,new EntityAIWatchClosest(this,EntityPlayer.class,8));
        tasks.addTask(8,new EntityAILookIdle(this));
        targetTasks.addTask(1,new EntityAIHurtByTarget(this,false));
        targetTasks.addTask(2,new EntityAINearestAttackableTarget<EntityPlayer>(this,EntityPlayer.class,true));
    }
}
