package ru.metrocraft.port.blackrazid.mutants;

import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

/** Native Demon flight and single head, NOT an EnderDragon with phases, griefing or boss rewards. */
public final class DemonEntity extends BlackrazidMutant implements IEntityMultiPart {
    public final MultiPartEntityPart dragonPartHead=new MultiPartEntityPart(this,"head",6,6);
    private final Entity[] parts={dragonPartHead};
    public final double[][] ringBuffer=new double[64][3];
    public int ringBufferIndex=-1;
    public double targetX,targetY=100,targetZ;
    public float prevAnimTime,animTime;
    public boolean forceNewTarget,slowed;
    private EntityPlayer flightTarget;
    private int scanDelay;
    private boolean anchored;
    private double homeX,homeZ;

    public DemonEntity(World world){super(world);setSize(8,8);noClip=false;isImmuneToFire=true;ignoreFrustumCheck=true;setNoGravity(true);}
    @Override public String species(){return "demon";}
    @Override public boolean canBreatheUnderwater(){return true;}
    @Override protected void initEntityAI(){} // Source replaces living movement, not a ground melee task.
    @Override protected void applyEntityAttributes(){super.applyEntityAttributes();profile(800,12,0);}
    @Override protected float getSoundVolume(){return 2;}
    @Override public World getWorld(){return world;}
    @Override public Entity[] getParts(){return parts.clone();}
    // The original qw callback explicitly returns false. Body damage remains normal.
    @Override public boolean attackEntityFromPart(MultiPartEntityPart part,DamageSource source,float damage){return false;}

    public double[] getMovementOffsets(int delay,float partial){
        if(ringBufferIndex<0)initializeHistory();
        double t=1-(getHealth()<=0?0:(Float.isFinite(partial)?MathHelper.clamp(partial,0,1):1));
        int a=(ringBufferIndex-delay)&63,b=(ringBufferIndex-delay-1)&63;
        return new double[]{ringBuffer[a][0]+MathHelper.wrapDegrees(ringBuffer[b][0]-ringBuffer[a][0])*t,
            ringBuffer[a][1]+(ringBuffer[b][1]-ringBuffer[a][1])*t,ringBuffer[a][2]+(ringBuffer[b][2]-ringBuffer[a][2])*t};
    }
    private void initializeHistory(){for(double[] row:ringBuffer){row[0]=rotationYaw;row[1]=posY;}ringBufferIndex=0;}
    public static double climb(double dx,double dy,double dz){
        double horizontal=Math.sqrt(dx*dx+dz*dz);
        if(!Double.isFinite(horizontal)||!Double.isFinite(dy))return 0;
        return MathHelper.clamp(horizontal<1e-8?Math.signum(dy)*.6:dy/horizontal,-.6,.6);
    }
    private int ceiling(){int height=world.getActualHeight();return height>16?height:256;}
    private void anchor(){
        if(anchored)return;anchored=true;homeX=posX;homeZ=posZ;targetX=posX;targetZ=posZ;targetY=MathHelper.clamp(100,8,ceiling()-9);
    }
    @Override public void onLivingUpdate(){
        if(isDead||getHealth()<=0)return;
        if(world.isRemote){
            float before=MathHelper.cos(prevAnimTime*(float)Math.PI*2),now=MathHelper.cos(animTime*(float)Math.PI*2);
            if(before<=-.3F&&now>=-.3F)world.playSound(posX,posY,posZ,SoundEvents.ENTITY_ENDERDRAGON_FLAP,SoundCategory.HOSTILE,5,.8F+rand.nextFloat()*.3F,false);
        }
        if(!Double.isFinite(motionX)||!Double.isFinite(motionY)||!Double.isFinite(motionZ))motionX=motionY=motionZ=0;
        if(!Float.isFinite(rotationYaw))rotationYaw=0;
        if(!Float.isFinite(randomYawVelocity))randomYawVelocity=0;
        prevAnimTime=animTime;
        double horizontal=Math.sqrt(motionX*motionX+motionZ*motionZ);
        float advance=(float)(.2/(horizontal*10+1));
        animTime+=slowed?advance*.5F:advance*(float)Math.pow(2,MathHelper.clamp(motionY,-10,10));
        rotationYaw=MathHelper.wrapDegrees(rotationYaw);
        if(ringBufferIndex<0)initializeHistory();
        ringBufferIndex=(ringBufferIndex+1)&63;ringBuffer[ringBufferIndex][0]=rotationYaw;ringBuffer[ringBufferIndex][1]=posY;
        if(world.isRemote)interpolateFlight();else{anchor();steerFlight();}
        renderYawOffset=rotationYaw;updateHead();
        if(!world.isRemote)tickHeadAttack();
    }
    private void interpolateFlight(){
        if(newPosRotationIncrements<=0)return;
        double x=posX+(interpTargetX-posX)/newPosRotationIncrements,y=posY+(interpTargetY-posY)/newPosRotationIncrements,z=posZ+(interpTargetZ-posZ)/newPosRotationIncrements;
        rotationYaw+=(float)(MathHelper.wrapDegrees(interpTargetYaw-rotationYaw)/newPosRotationIncrements);
        rotationPitch+=(float)((interpTargetPitch-rotationPitch)/newPosRotationIncrements);--newPosRotationIncrements;
        setPosition(x,y,z);setRotation(rotationYaw,rotationPitch);
    }
    private void steerFlight(){
        if(flightTarget!=null&&(!flightTarget.isEntityAlive()||flightTarget.isSpectator()||flightTarget.world!=world))flightTarget=null;
        double dx=targetX-posX,dy=targetY-posY,dz=targetZ-posZ,distance=dx*dx+dy*dy+dz*dz;
        if(flightTarget!=null){
            targetX=flightTarget.posX;targetZ=flightTarget.posZ;
            double range=Math.hypot(targetX-posX,targetZ-posZ);
            targetY=flightTarget.getEntityBoundingBox().minY+Math.min(10,.4+range/80-1);
        }else{targetX+=rand.nextGaussian()*2;targetZ+=rand.nextGaussian()*2;}
        if(forceNewTarget||!Double.isFinite(distance)||distance<100||distance>22500||collidedHorizontally||collidedVertically)setNewTarget();
        // Use the same target vector for this integration step as the source.
        motionY+=climb(dx,dy,dz)*.1;
        double turn=MathHelper.clamp(MathHelper.wrapDegrees(180-Math.atan2(dx,dz)*180/Math.PI-rotationYaw),-50,50);
        Vec3d aim=new Vec3d(targetX-posX,targetY-posY,targetZ-posZ).normalize();
        Vec3d forward=new Vec3d(MathHelper.sin(rotationYaw*(float)Math.PI/180),motionY,-MathHelper.cos(rotationYaw*(float)Math.PI/180)).normalize();
        float alignment=(float)Math.max(0,(forward.dotProduct(aim)+.5)/1.5);
        randomYawVelocity*=.8F;
        double speed=Math.sqrt(motionX*motionX+motionZ*motionZ),inertia=Math.min(40,speed+1);
        randomYawVelocity+=(float)(turn*(.7/inertia/(speed+1)));rotationYaw+=randomYawVelocity*.1F;
        float blend=(float)(2/(inertia+1));moveRelative(0,0,-1,.06F*(alignment*blend+(1-blend)));
        double scale=slowed?.8:1;
        if(canMoveFlight(motionX*scale,motionY*scale,motionZ*scale))move(MoverType.SELF,motionX*scale,motionY*scale,motionZ*scale);
        else{motionX=motionY=motionZ=0;forceNewTarget=true;}
        double drag=.8+.15*(new Vec3d(motionX,motionY,motionZ).normalize().dotProduct(forward)+1)/2;
        motionX*=drag;motionZ*=drag;motionY*=.91;
    }
    /** Never force-load a flight corridor or clip the large body through the world border. */
    public boolean canMoveFlight(double x,double y,double z){
        if(!Double.isFinite(x)||!Double.isFinite(y)||!Double.isFinite(z)||Math.abs(x)>16||Math.abs(y)>16||Math.abs(z)>16)return false;
        AxisAlignedBB box=getEntityBoundingBox().offset(x,y,z);
        BlockPos min=new BlockPos(box.minX,box.minY,box.minZ),max=new BlockPos(box.maxX,box.maxY,box.maxZ);
        return box.minY>=0&&box.maxY<ceiling()&&world.isAreaLoaded(min,max)&&world.getWorldBorder().contains(min)&&world.getWorldBorder().contains(max);
    }
    private void setNewTarget(){
        forceNewTarget=false;
        List<EntityPlayer> candidates=new ArrayList<>();
        for(EntityPlayer p:world.playerEntities)if(p.isEntityAlive()&&!p.isSpectator())candidates.add(p);
        if(rand.nextInt(2)==0&&!candidates.isEmpty()){flightTarget=candidates.get(rand.nextInt(candidates.size()));return;}
        flightTarget=null;
        // Source roams around world 0,0; anchor to placement instead to support distant maps.
        for(int tries=0;tries<32;tries++){
            targetX=homeX+rand.nextFloat()*120-60;targetZ=homeZ+rand.nextFloat()*120-60;targetY=MathHelper.clamp(70+rand.nextFloat()*50,8,ceiling()-9);
            if(getDistanceSq(targetX,targetY,targetZ)>100)return;
        }
        targetX=homeX+30;targetZ=homeZ+30;
    }
    public void updateHead(){
        double tilt=(getMovementOffsets(5,1)[1]-getMovementOffsets(10,1)[1])*10/180*Math.PI;
        double angle=rotationYaw*Math.PI/180-randomYawVelocity*.01;
        dragonPartHead.onUpdate();
        dragonPartHead.setLocationAndAngles(posX+Math.sin(angle)*5.5*Math.cos(tilt),posY+getMovementOffsets(0,1)[1]-getMovementOffsets(5,1)[1]-Math.sin(tilt)*5.5,posZ-Math.cos(angle)*5.5*Math.cos(tilt),0,0);
    }
    /** One living victim, ten contact damage, once per 20 server ticks; no block destruction. */
    public void tickHeadAttack(){
        if(world.isRemote||isDead||getHealth()<=0)return;
        if(scanDelay>0&&--scanDelay>0)return;scanDelay=20;
        AxisAlignedBB box=dragonPartHead.getEntityBoundingBox().grow(1);
        if(!world.isAreaLoaded(new BlockPos(box.minX,box.minY,box.minZ),new BlockPos(box.maxX,box.maxY,box.maxZ)))return;
        for(Entity e:world.getEntitiesWithinAABBExcludingEntity(this,box))if(e instanceof EntityLivingBase&&e!=this&&e.isEntityAlive()&&(!(e instanceof EntityPlayer)||!((EntityPlayer)e).isSpectator())){
            e.attackEntityFrom(DamageSource.causeMobDamage(this),10);break;
        }
    }
    @Override public void writeEntityToNBT(NBTTagCompound tag){
        super.writeEntityToNBT(tag);tag.setInteger("DemonScanDelay",MathHelper.clamp(scanDelay,0,20));
        tag.setBoolean("DemonAnchored",anchored);tag.setDouble("DemonHomeX",homeX);tag.setDouble("DemonHomeZ",homeZ);
        tag.setDouble("DemonTargetX",targetX);tag.setDouble("DemonTargetY",targetY);tag.setDouble("DemonTargetZ",targetZ);
    }
    @Override public void readEntityFromNBT(NBTTagCompound tag){
        super.readEntityFromNBT(tag);scanDelay=MathHelper.clamp(tag.getInteger("DemonScanDelay"),0,20);flightTarget=null;
        anchored=tag.getBoolean("DemonAnchored");homeX=tag.getDouble("DemonHomeX");homeZ=tag.getDouble("DemonHomeZ");
        targetX=tag.getDouble("DemonTargetX");targetY=tag.getDouble("DemonTargetY");targetZ=tag.getDouble("DemonTargetZ");
        if(!Double.isFinite(homeX)||!Double.isFinite(homeZ)||!Double.isFinite(targetX)||!Double.isFinite(targetY)||!Double.isFinite(targetZ)||Math.abs(targetX)>30000000||Math.abs(targetZ)>30000000||Math.abs(homeX)>30000000||Math.abs(homeZ)>30000000){anchored=false;homeX=posX;homeZ=posZ;targetX=posX;targetY=100;targetZ=posZ;}
        targetY=MathHelper.clamp(targetY,8,ceiling()-9);ringBufferIndex=-1;forceNewTarget=true;setNoGravity(true);
    }
}
