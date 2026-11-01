package ru.metrocraft.port.blackrazid;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import ru.metrocraft.port.blackrazid.mutants.*;

/** Real native classes, not mocks of the new mobs or their attributes. */
public class GroundMutantsTest {
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();}
    private static BlackrazidMutant[] mobs(World w){return new BlackrazidMutant[]{
        new KikimoraEntity(w),new StrazhEntity(w),new NosachEntity(w),new DarkEntity(w),new BearEntity(w),new MobusEntity(w),new AquaFemaleEntity(w),new AquaMaleEntity(w)};}
    private static MutantSpawner[] nests(World w){return new MutantSpawner[]{
        new MutantSpawner.KikimoraNest(w),new MutantSpawner.StrazhNest(w),new MutantSpawner.NosachNest(w),new MutantSpawner.DarkNest(w),
        new MutantSpawner.BearNest(w),new MutantSpawner.MobusNest(w),new MutantSpawner.AquaFemaleNest(w),new MutantSpawner.AquaMaleNest(w)};}
    @Test public void allEightNativeProfilesKeepHealthDamageSpeedAndTargetRanges()throws Exception{
        double[][] expected={{40,4,.45,32},{100,8,.48,32},{120,6,.46,32},{200,12,.47,16},{140,8,.45,64},{140,8,.45,64},{90,6,.45,32},{250,10,.45,32}};
        BlackrazidMutant[] all=mobs(NativeWeaponsTest.player(false).world);
        for(int i=0;i<all.length;i++){
            assertEquals(expected[i][0],all[i].getMaxHealth(),0);assertEquals(expected[i][0],all[i].getHealth(),0);
            assertEquals(expected[i][1],all[i].getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue(),0);
            assertEquals(expected[i][2],all[i].getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue(),.00001);
            assertEquals(expected[i][3],all[i].getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(),0);
        }
    }
    @Test public void sourceDimensionsAndWaterRulesAreNotGuessedFromNames()throws Exception{
        BlackrazidMutant[] all=mobs(NativeWeaponsTest.player(false).world);
        assertEquals(.5,all[0].width,.00001);assertEquals(.5,all[0].height,.00001);
        assertEquals(1,all[3].width,0);assertEquals(2.5,all[3].height,0);
        for(int i:new int[]{1,2,4,5,6,7}){assertEquals(.6,all[i].width,.00001);assertEquals(1.8,all[i].height,.00001);}
        for(int i=0;i<6;i++)assertTrue(all[i].canBreatheUnderwater());
        // The source Aquas do not set their air to 300. They are NOT Guardian/WaterMob substitutes.
        for(int i:new int[]{6,7}){assertFalse(all[i].canBreatheUnderwater());assertTrue(all[i].getNavigator() instanceof PathNavigateGround);}
    }
    @Test public void nativeGroundAiIsMeleeRetaliationWanderAndWatch()throws Exception{
        for(BlackrazidMutant mob:mobs(NativeWeaponsTest.player(false).world)){
            assertFalse(mob.timid());assertEquals(2,mob.targetTasks.taskEntries.size());
            assertTrue(mob.tasks.taskEntries.stream().anyMatch(t->t.action instanceof EntityAIAttackMelee));
            assertTrue(mob.tasks.taskEntries.stream().anyMatch(t->t.action instanceof EntityAIWander));
            assertTrue(mob.tasks.taskEntries.stream().anyMatch(t->t.action instanceof EntityAIWatchClosest));
        }
        assertEquals(16,new StrazhEntity(NativeWeaponsTest.player(false).world).watchRange(),0);
        assertEquals(8,new BearEntity(NativeWeaponsTest.player(false).world).watchRange(),0);
    }
    @Test public void actualMeleeUsesEachNativeDamageAndOwnerWithoutTerrainEffects()throws Exception{
        for(BlackrazidMutant mob:mobs(NativeWeaponsTest.player(false).world)){
            Entity target=mock(Entity.class);assertFalse(mob.attackEntityAsMob(target));ArgumentCaptor<DamageSource> cause=ArgumentCaptor.forClass(DamageSource.class);
            verify(target).attackEntityFrom(cause.capture(),eq((float)mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            assertSame(mob,cause.getValue().getTrueSource());
        }
    }
    @Test public void mobusAndBearHaveOwnClassesButSourceSharedSounds()throws Exception{
        World w=NativeWeaponsTest.player(false).world;BearEntity bear=new BearEntity(w);MobusEntity mobus=new MobusEntity(w);
        assertNotEquals(bear.getClass(),mobus.getClass());assertEquals("bear",bear.species());assertEquals("mobus",mobus.species());
        assertEquals("bear",mobus.soundFamily());assertEquals("aquaf",new AquaFemaleEntity(w).soundFamily());assertEquals("aquam",new AquaMaleEntity(w).soundFamily());
    }
    @Test public void eightAdditionalSpawnersUseNativePeriodsBatchAndSpecies()throws Exception{
        int[] periods={4000,4000,6000,8000,8000,8000,6000,10000},sizes={3,3,2,2,2,2,2,1};
        Class<?>[] types={KikimoraEntity.class,StrazhEntity.class,NosachEntity.class,DarkEntity.class,BearEntity.class,MobusEntity.class,AquaFemaleEntity.class,AquaMaleEntity.class};
        for(int i=0;i<periods.length;i++){
            World w=MutantRulesTest.spawnWorld(false);MutantSpawner node=nests(w)[i];assertEquals(periods[i],node.remaining());assertEquals(sizes[i],node.batch());
            assertEquals(sizes[i],node.spawnWave());ArgumentCaptor<Entity> capture=ArgumentCaptor.forClass(Entity.class);verify(w,times(sizes[i])).spawnEntity(capture.capture());
            for(Entity mob:capture.getAllValues()){assertEquals(types[i],mob.getClass());assertEquals(node.posY+1,mob.posY,0);}
            for(int n=1;n<sizes[i];n++)assertTrue(Math.abs(capture.getAllValues().get(n).posX-capture.getAllValues().get(n-1).posX)>1.39);
        }
    }
    @Test public void threeMobWaveNeverDuplicatesPositionsOrOverfillsCap()throws Exception{
        World w=MutantRulesTest.spawnWorld(false);MutantSpawner node=new MutantSpawner.StrazhNest(w);
        when(w.getEntitiesWithinAABB(eq(BlackrazidMutant.class),any(AxisAlignedBB.class))).thenReturn(java.util.Collections.nCopies(14,(BlackrazidMutant)null));
        assertEquals(2,node.spawnWave());ArgumentCaptor<Entity> capture=ArgumentCaptor.forClass(Entity.class);verify(w,times(2)).spawnEntity(capture.capture());
        assertNotEquals(capture.getAllValues().get(0).posX,capture.getAllValues().get(1).posX,0);
    }
    @Test public void partialThreeMobSpawnCostsFullIntervalAndDoesNotInstantlyRetry()throws Exception{
        World w=MutantRulesTest.spawnWorld(false);MutantSpawner node=new MutantSpawner.KikimoraNest(w);NBTTagCompound due=new NBTTagCompound();due.setInteger("BlackrazidSpawnDelay",1);node.readEntityFromNBT(due);
        when(w.spawnEntity(any(Entity.class))).thenReturn(true,false,false);node.tickSpawner();assertEquals(4000,node.remaining());
        for(int i=0;i<20;i++)node.tickSpawner();assertEquals(3980,node.remaining());verify(w,times(3)).spawnEntity(any(Entity.class));
    }
    @Test public void everyNewSpawnerPersistsItsOwnCountdownAndClampsToItsPeriod()throws Exception{
        World w=MutantRulesTest.spawnWorld(false);MutantSpawner[] source=nests(w),restored=nests(w);
        for(int i=0;i<source.length;i++){
            for(int t=0;t<17;t++)source[i].tickSpawner();NBTTagCompound tag=new NBTTagCompound();source[i].writeEntityToNBT(tag);restored[i].readEntityFromNBT(tag);
            assertEquals(source[i].period()-17,restored[i].remaining());tag.setInteger("BlackrazidSpawnDelay",Integer.MAX_VALUE);restored[i].readEntityFromNBT(tag);assertEquals(source[i].period(),restored[i].remaining());
        }
    }
    @Test public void everyNewSpawnerIsServerAuthoritative()throws Exception{
        World w=MutantRulesTest.spawnWorld(true);
        for(MutantSpawner node:nests(w)){assertEquals(0,node.spawnWave());node.tickSpawner();assertEquals(node.period(),node.remaining());}
        verify(w,never()).spawnEntity(any(Entity.class));
    }
}
