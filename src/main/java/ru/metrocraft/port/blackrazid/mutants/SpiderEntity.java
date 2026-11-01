package ru.metrocraft.port.blackrazid.mutants;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.metrocraft.port.blackrazid.survival.*;

public final class SpiderEntity extends BlackrazidMutant {
    private int panic;
    public SpiderEntity(World world){super(world);}
    @Override public String species(){return "spider";}
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(80,6,.45);}
    @Override public boolean canBreatheUnderwater(){return true;}
    public int panicTicks(){return MutantRules.panic(panic);}
    @Override protected void initEntityAI(){super.initEntityAI();tasks.addTask(0,new EntityAIBase(){
        {setMutexBits(3);}
        @Override public boolean shouldExecute(){return panicTicks()>0;}
        @Override public void updateTask(){Vec3d pos=RandomPositionGenerator.findRandomTarget(SpiderEntity.this,5,4);if(pos!=null)getNavigator().tryMoveToXYZ(pos.x,pos.y,pos.z,1);}
        @Override public void resetTask(){getNavigator().clearPath();}
    });}
    public boolean applyLamp(EntityPlayer player){
        if(world.isRemote || player==null || !isEntityAlive())return false;
        SurvivalState s=SurvivalSystem.get(player);
        if(!MutantRules.spiderFears(getHealth(),s.energy,s.lamp && SurvivalSystem.hasLamp(player),looking(player,.5),panicTicks()))return false;
        panic=60;setFire(2);return true;
    }
    @Override public boolean attackEntityAsMob(Entity target){return panicTicks()==0 && super.attackEntityAsMob(target);}
    @Override public void onEntityUpdate(){
        super.onEntityUpdate();if(world.isRemote || !isEntityAlive())return;
        panic=Math.max(0,panicTicks()-1);applyLamp(world.getClosestPlayerToEntity(this,46));
    }
    @Override public void writeEntityToNBT(NBTTagCompound tag){super.writeEntityToNBT(tag);tag.setInteger("BlackrazidPanic",panicTicks());}
    @Override public void readEntityFromNBT(NBTTagCompound tag){super.readEntityFromNBT(tag);panic=MutantRules.panic(tag.getInteger("BlackrazidPanic"));}
}
