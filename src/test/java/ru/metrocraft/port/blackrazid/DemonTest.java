package ru.metrocraft.port.blackrazid;

import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import ru.metrocraft.port.blackrazid.mutants.*;

public class DemonTest {
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();}
    private static World world(boolean client)throws Exception{
        World w=MutantRulesTest.spawnWorld(client);
        java.lang.reflect.Field profiler=World.class.getField("profiler");profiler.setAccessible(true);profiler.set(w,new Profiler());
        java.lang.reflect.Field players=World.class.getField("playerEntities");players.setAccessible(true);players.set(w,new ArrayList<net.minecraft.entity.player.EntityPlayer>());
        when(w.getBlockState(any(BlockPos.class))).thenReturn(Blocks.AIR.getDefaultState());
        return w;
    }
    private static DemonEntity demon(World w){DemonEntity d=new DemonEntity(w);d.setLocationAndAngles(10,80,20,0,0);return d;}
    private static EntityLivingBase victim(){EntityLivingBase e=mock(EntityLivingBase.class);when(e.isEntityAlive()).thenReturn(true);return e;}
    @Test public void nativeHealthDamageBodyAndHeadDimensions()throws Exception{
        DemonEntity d=demon(world(false));assertEquals(800,d.getHealth(),0);assertEquals(800,d.getMaxHealth(),0);
        assertEquals(12,d.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue(),0);
        assertEquals(8,d.width,0);assertEquals(8,d.height,0);assertEquals(6,d.dragonPartHead.width,0);assertEquals(6,d.dragonPartHead.height,0);
        assertTrue(d.isImmuneToFire());assertTrue(d.canBreatheUnderwater());assertFalse(d.noClip);assertTrue(d.hasNoGravity());
    }
    @Test public void replacesGroundAiRatherThanAddingEnderDragonPhases()throws Exception{
        DemonEntity d=demon(world(false));assertTrue(d.tasks.taskEntries.isEmpty());assertTrue(d.targetTasks.taskEntries.isEmpty());
        assertFalse((Object)d instanceof net.minecraft.entity.IEntityOwnable);assertEquals(0,d.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue(),0);
    }
    @Test public void headOwnedByBodyCannotForwardDamageAndPartsArrayIsProtected()throws Exception{
        World w=world(false);DemonEntity d=demon(w);assertSame(w,d.getWorld());assertSame(d,d.dragonPartHead.parent);
        Entity[] parts=d.getParts();assertEquals(1,parts.length);assertSame(d.dragonPartHead,parts[0]);parts[0]=null;assertSame(d.dragonPartHead,d.getParts()[0]);
        assertFalse(d.dragonPartHead.attackEntityFrom(DamageSource.GENERIC,40));assertEquals(800,d.getHealth(),0);assertTrue(d.dragonPartHead.isEntityEqual(d));
    }
    @Test public void bodyRemainsNormallyDamageable()throws Exception{
        DemonEntity d=demon(world(false));assertTrue(d.attackEntityFrom(DamageSource.GENERIC,12));assertEquals(788,d.getHealth(),0);
    }
    @Test public void ringInterpolationWrapsAcross180And64Slots()throws Exception{
        DemonEntity d=demon(world(false));d.ringBufferIndex=0;d.ringBuffer[0][0]=179;d.ringBuffer[63][0]=-179;
        d.ringBuffer[0][1]=100;d.ringBuffer[63][1]=90;double[] offset=d.getMovementOffsets(0,.5F);
        assertEquals(180,offset[0],0);assertEquals(95,offset[1],0);assertArrayEquals(offset,d.getMovementOffsets(64,.5F),0);
        assertTrue(Double.isFinite(d.getMovementOffsets(0,Float.NaN)[0]));
    }
    @Test public void freshHistoryStartsAtBodyInsteadOfWorldOrigin()throws Exception{
        DemonEntity d=demon(world(false));d.rotationYaw=42;assertArrayEquals(new double[]{42,80,0},d.getMovementOffsets(10,1),0);
    }
    @Test public void verticalSteeringCannotDivideByZeroOrExceedNativeSlope(){
        assertEquals(0,DemonEntity.climb(0,0,0),0);assertEquals(.6,DemonEntity.climb(0,10,0),0);assertEquals(-.6,DemonEntity.climb(0,-10,0),0);
        assertEquals(.25,DemonEntity.climb(3,1.25,4),0);assertEquals(0,DemonEntity.climb(Double.NaN,1,0),0);assertEquals(0,DemonEntity.climb(1,Double.POSITIVE_INFINITY,0),0);
    }
    @Test public void headActuallyTracksYawAndBodyPosition()throws Exception{
        DemonEntity d=demon(world(false));d.updateHead();assertEquals(10,d.dragonPartHead.posX,.00001);assertEquals(80,d.dragonPartHead.posY,.00001);assertEquals(14.5,d.dragonPartHead.posZ,.00001);
        d.rotationYaw=90;d.updateHead();assertEquals(15.5,d.dragonPartHead.posX,.00001);assertEquals(20,d.dragonPartHead.posZ,.00001);
    }
    @Test public void contactDealsTenToOnlyOneVictimPerTwentyServerTicks()throws Exception{
        World w=world(false);DemonEntity d=demon(w);EntityLivingBase first=victim(),second=victim();
        when(w.getEntitiesWithinAABBExcludingEntity(eq(d),any(AxisAlignedBB.class))).thenReturn(Arrays.asList(first,second));
        d.tickHeadAttack();ArgumentCaptor<DamageSource> source=ArgumentCaptor.forClass(DamageSource.class);verify(first).attackEntityFrom(source.capture(),eq(10F));assertSame(d,source.getValue().getTrueSource());
        verify(second,never()).attackEntityFrom(any(DamageSource.class),anyFloat());
        for(int i=0;i<19;i++)d.tickHeadAttack();verify(first,times(1)).attackEntityFrom(any(DamageSource.class),eq(10F));
        d.tickHeadAttack();verify(first,times(2)).attackEntityFrom(any(DamageSource.class),eq(10F));
    }
    @Test public void deadEntitiesAndOwnBodyAreNotContactVictims()throws Exception{
        World w=world(false);DemonEntity d=demon(w);EntityLivingBase dead=mock(EntityLivingBase.class),live=victim();Entity nonliving=mock(Entity.class);
        when(w.getEntitiesWithinAABBExcludingEntity(eq(d),any(AxisAlignedBB.class))).thenReturn(Arrays.asList(d,dead,nonliving,live));
        d.tickHeadAttack();assertEquals(800,d.getHealth(),0);verify(dead,never()).attackEntityFrom(any(DamageSource.class),anyFloat());verify(nonliving,never()).attackEntityFrom(any(DamageSource.class),anyFloat());verify(live).attackEntityFrom(any(DamageSource.class),eq(10F));
    }
    @Test public void clientAndDeadDemonNeverQueryOrApplyContactDamage()throws Exception{
        World client=world(true);DemonEntity d=demon(client);d.tickHeadAttack();verify(client,never()).getEntitiesWithinAABBExcludingEntity(any(Entity.class),any(AxisAlignedBB.class));
        World server=world(false);d=demon(server);d.setHealth(0);d.tickHeadAttack();verify(server,never()).getEntitiesWithinAABBExcludingEntity(any(Entity.class),any(AxisAlignedBB.class));
    }
    @Test public void actualFlightMovesWithoutGroundAiAndStaysFinite()throws Exception{
        World w=world(false);DemonEntity d=demon(w);Vec3d start=d.getPositionVector();
        for(int i=0;i<80;i++)d.onLivingUpdate();assertTrue(d.getPositionVector().distanceTo(start)>.1);
        assertTrue(Double.isFinite(d.posX));assertTrue(Double.isFinite(d.posY));assertTrue(Double.isFinite(d.posZ));assertTrue(Float.isFinite(d.animTime));
        assertEquals(6,d.dragonPartHead.width,0);verify(w,never()).spawnEntity(any(Entity.class));
    }
    @Test public void clientUsesNetworkInterpolationNotServerFlight()throws Exception{
        World w=world(true);DemonEntity d=demon(w);d.setPositionAndRotationDirect(20,90,40,40,20,2,false);
        d.onLivingUpdate();assertEquals(15,d.posX,0);assertEquals(85,d.posY,0);assertEquals(30,d.posZ,0);assertEquals(20,d.rotationYaw,.0001);
        d.onLivingUpdate();assertEquals(20,d.posX,0);assertEquals(90,d.posY,0);assertEquals(40,d.posZ,0);assertEquals(40,d.rotationYaw,.0001);
        verify(w,never()).getEntitiesWithinAABBExcludingEntity(any(Entity.class),any(AxisAlignedBB.class));
    }
    @Test public void unloadedFlightCorridorStopsWithoutForcingChunks()throws Exception{
        World w=world(false);DemonEntity d=demon(w);when(w.isAreaLoaded(any(BlockPos.class),any(BlockPos.class))).thenReturn(false);
        d.motionX=.4;d.onLivingUpdate();assertEquals(10,d.posX,0);assertEquals(80,d.posY,0);assertEquals(20,d.posZ,0);assertEquals(0,d.motionX,0);assertTrue(d.forceNewTarget);
        assertFalse(d.canMoveFlight(Double.NaN,0,0));assertFalse(d.canMoveFlight(17,0,0));
    }
    @Test public void flightBoundsAccountForEntireEightBlockBody()throws Exception{
        World w=world(false);DemonEntity d=demon(w);assertTrue(d.canMoveFlight(1,0,1));d.setPosition(29999990,80,20);assertFalse(d.canMoveFlight(0,0,0));d.setPosition(10,252,20);assertFalse(d.canMoveFlight(0,0,0));
    }
    @Test public void ownSpawnerCreatesOneDemonAfterNative30000TickDelay()throws Exception{
        World w=world(false);MutantSpawner.DemonNest node=new MutantSpawner.DemonNest(w);assertEquals(30000,node.period());assertEquals(1,node.batch());
        NBTTagCompound due=new NBTTagCompound();due.setInteger("BlackrazidSpawnDelay",1);node.readEntityFromNBT(due);node.tickSpawner();assertEquals(30000,node.remaining());
        ArgumentCaptor<Entity> entity=ArgumentCaptor.forClass(Entity.class);verify(w,times(1)).spawnEntity(entity.capture());assertTrue(entity.getValue() instanceof DemonEntity);assertEquals(8,entity.getValue().width,0);
    }
    @Test public void ownSpawnerChecksLoadedAreaOfLargeMobNotOnlySmallNode()throws Exception{
        World w=world(false);MutantSpawner.DemonNest node=new MutantSpawner.DemonNest(w);
        when(w.isAreaLoaded(any(BlockPos.class),any(BlockPos.class))).thenAnswer(call->{BlockPos a=call.getArgument(0),b=call.getArgument(1);return b.getY()-a.getY()<7;});
        assertEquals(0,node.spawnWave());verify(w,never()).spawnEntity(any(Entity.class));
    }
    @Test public void saveReloadRetainsTargetAnchorAndContactCooldown()throws Exception{
        World w=world(false);DemonEntity d=demon(w);EntityLivingBase target=victim();when(w.getEntitiesWithinAABBExcludingEntity(any(Entity.class),any(AxisAlignedBB.class))).thenReturn(Collections.singletonList(target));
        d.onLivingUpdate();d.targetX=120;d.targetY=110;d.targetZ=130;NBTTagCompound tag=new NBTTagCompound();d.writeEntityToNBT(tag);
        DemonEntity restored=demon(w);restored.readEntityFromNBT(tag);assertEquals(120,restored.targetX,0);assertEquals(110,restored.targetY,0);assertEquals(130,restored.targetZ,0);assertTrue(restored.forceNewTarget);assertTrue(restored.hasNoGravity());
        clearInvocations(target);for(int i=0;i<19;i++)restored.tickHeadAttack();verify(target,never()).attackEntityFrom(any(DamageSource.class),anyFloat());restored.tickHeadAttack();verify(target).attackEntityFrom(any(DamageSource.class),eq(10F));
    }
    @Test public void corruptTargetNbtCannotPoisonFlightWithNan()throws Exception{
        DemonEntity d=demon(world(false));NBTTagCompound tag=new NBTTagCompound();tag.setBoolean("DemonAnchored",true);tag.setDouble("DemonTargetX",Double.NaN);tag.setDouble("DemonHomeZ",Double.POSITIVE_INFINITY);tag.setInteger("DemonScanDelay",Integer.MAX_VALUE);
        d.readEntityFromNBT(tag);assertTrue(Double.isFinite(d.targetX));assertTrue(Double.isFinite(d.targetY));assertTrue(Double.isFinite(d.targetZ));d.motionX=Double.NaN;d.onLivingUpdate();assertTrue(Double.isFinite(d.motionX));assertTrue(Float.isFinite(d.rotationYaw));
    }
    @Test public void nativeWingTipsKeepBothMaterialGroupsWithoutGpu()throws Exception{
        for(String side:new String[]{"left","right"})try(java.io.InputStream in=getClass().getResourceAsStream("/assets/metroport/models/blackrazid/mutants/demon/"+side+"krilo1.obj")){
            assertNotNull(in);ru.metrocraft.port.mcmod.McmodObjMesh mesh=ru.metrocraft.port.mcmod.McmodObjMesh.read(in);assertEquals(2,mesh.groups.size());
            assertFalse(mesh.groups.get("act34_demon_part01").isEmpty());assertFalse(mesh.groups.get("act34_demon_part02").isEmpty());
        }
    }
}
