package ru.metrocraft.port.blackrazid.mutants;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.*;

/** Stationary map node. No old general properties, command dispatch, terrain edits or global entity IDs. */
public abstract class MutantSpawner extends Entity {
    public static final int NEARBY_CAP=16,RETRY_TICKS=20;
    private int remaining;
    public MutantSpawner(World world){super(world);setSize(.54F,.09F);setNoGravity(true);remaining=period();}
    public abstract int period();
    public abstract int batch();
    protected abstract BlackrazidMutant create();
    public int remaining(){return Math.max(0,Math.min(period(),remaining));}
    @Override protected void entityInit(){}
    @Override public boolean canBeCollidedWith(){return !isDead;}
    @Override public void onUpdate(){super.onUpdate();tickSpawner();}
    public void tickSpawner(){
        if(world.isRemote || isDead || world.getDifficulty()==EnumDifficulty.PEACEFUL || !world.getGameRules().getBoolean("doMobSpawning"))return;
        remaining=remaining();if(remaining>0 && --remaining>0)return;
        int count=spawnWave();remaining=count>0?period():RETRY_TICKS;
    }
    public int spawnWave(){
        if(world.isRemote || isDead || world.getDifficulty()==EnumDifficulty.PEACEFUL || !world.getGameRules().getBoolean("doMobSpawning"))return 0;
        BlockPos pos=new BlockPos(this);
        if(!world.isAreaLoaded(pos.add(-2,-1,-2),pos.add(2,4,2)))return 0;
        int available=NEARBY_CAP-world.getEntitiesWithinAABB(BlackrazidMutant.class,getEntityBoundingBox().grow(32)).size();
        int accepted=0;
        for(int i=0;i<Math.min(batch(),available);i++){
            BlackrazidMutant mob=create();
            mob.setLocationAndAngles(posX+((i-(batch()-1)/2.0)*1.4),posY+1,posZ,rotationYaw,0);
            AxisAlignedBB box=mob.getEntityBoundingBox();BlockPos min=new BlockPos(box.minX,box.minY,box.minZ),max=new BlockPos(box.maxX,box.maxY,box.maxZ);
            if(!world.isAreaLoaded(min,max)||!world.getWorldBorder().contains(min)||!world.getWorldBorder().contains(max) || !world.getCollisionBoxes(mob,mob.getEntityBoundingBox()).isEmpty()
                || !world.checkNoEntityCollision(mob.getEntityBoundingBox(),mob))continue;
            if(world.spawnEntity(mob))accepted++;
        }
        return accepted;
    }
    @Override public boolean attackEntityFrom(DamageSource source,float amount){
        if(world.isRemote || isDead)return false;
        Entity attacker=source.getTrueSource();
        boolean remove=source==DamageSource.OUT_OF_WORLD;
        if(attacker instanceof EntityPlayer){EntityPlayer p=(EntityPlayer)attacker;
            // Source wk.aE maps to SNOWBALL, not a sword; creative removal is an explicit port adaptation.
            remove|=p.capabilities.isCreativeMode || (!p.getHeldItemMainhand().isEmpty() && p.getHeldItemMainhand().getItem()==Items.SNOWBALL);
        }
        if(!remove)return false;setDead();return true;
    }
    @Override public void writeEntityToNBT(NBTTagCompound tag){tag.setInteger("BlackrazidSpawnDelay",remaining());}
    @Override public void readEntityFromNBT(NBTTagCompound tag){remaining=tag.hasKey("BlackrazidSpawnDelay",99)?Math.max(0,Math.min(period(),tag.getInteger("BlackrazidSpawnDelay"))):period();}
    public static final class DemonNest extends MutantSpawner {
        public DemonNest(World w){super(w);}
        @Override public int period(){return 30000;}
        @Override public int batch(){return 1;}
        @Override protected BlackrazidMutant create(){return new DemonEntity(world);}
    }
    public static final class SpiderNest extends MutantSpawner {
        public SpiderNest(World w){super(w);}
        @Override public int period(){return 4000;}
        @Override public int batch(){return 2;}
        @Override protected BlackrazidMutant create(){return new SpiderEntity(world);}
    }
    public static final class LibrarianNest extends MutantSpawner {
        public LibrarianNest(World w){super(w);}
        @Override public int period(){return 10000;}
        @Override public int batch(){return 1;}
        @Override protected BlackrazidMutant create(){return new BiblioEntity(world);}
    }
    public static final class KikimoraNest extends MutantSpawner {
        public KikimoraNest(World w){super(w);}
        @Override public int period(){return 4000;}
        @Override public int batch(){return 3;}
        @Override protected BlackrazidMutant create(){return new KikimoraEntity(world);}
    }
    public static final class StrazhNest extends MutantSpawner {
        public StrazhNest(World w){super(w);}
        @Override public int period(){return 4000;}
        @Override public int batch(){return 3;}
        @Override protected BlackrazidMutant create(){return new StrazhEntity(world);}
    }
    public static final class NosachNest extends MutantSpawner {
        public NosachNest(World w){super(w);}
        @Override public int period(){return 6000;}
        @Override public int batch(){return 2;}
        @Override protected BlackrazidMutant create(){return new NosachEntity(world);}
    }
    public static final class DarkNest extends MutantSpawner {
        public DarkNest(World w){super(w);}
        @Override public int period(){return 8000;}
        @Override public int batch(){return 2;}
        @Override protected BlackrazidMutant create(){return new DarkEntity(world);}
    }
    public static final class BearNest extends MutantSpawner {
        public BearNest(World w){super(w);}
        @Override public int period(){return 8000;}
        @Override public int batch(){return 2;}
        @Override protected BlackrazidMutant create(){return new BearEntity(world);}
    }
    public static final class MobusNest extends MutantSpawner {
        public MobusNest(World w){super(w);}
        @Override public int period(){return 8000;}
        @Override public int batch(){return 2;}
        @Override protected BlackrazidMutant create(){return new MobusEntity(world);}
    }
    public static final class AquaFemaleNest extends MutantSpawner {
        public AquaFemaleNest(World w){super(w);}
        @Override public int period(){return 6000;}
        @Override public int batch(){return 2;}
        @Override protected BlackrazidMutant create(){return new AquaFemaleEntity(world);}
    }
    public static final class AquaMaleNest extends MutantSpawner {
        public AquaMaleNest(World w){super(w);}
        @Override public int period(){return 10000;}
        @Override public int batch(){return 1;}
        @Override protected BlackrazidMutant create(){return new AquaMaleEntity(world);}
    }
}
