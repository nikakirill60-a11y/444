package ru.metrocraft.port.blackrazid;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.border.WorldBorder;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static ru.metrocraft.port.blackrazid.NativeWeaponsTest.*;
import ru.metrocraft.port.blackrazid.mutants.*;
import ru.metrocraft.port.blackrazid.survival.*;
import java.util.*;

public class MutantRulesTest {
    @BeforeClass public static void boot(){NativeWeaponsTest.boot();}
    private static EntityPlayer sight(BlackrazidMutant mob,EntityPlayer p){
        mob.setPosition(0,0,5);p.posX=0;p.posY=0;p.posZ=0;
        Vec3d direction=new Vec3d(0,mob.height*.5-p.getEyeHeight(),5).normalize();when(p.getLookVec()).thenReturn(direction);when(p.canEntityBeSeen(mob)).thenReturn(true);return p;
    }
    static World spawnWorld(boolean remote)throws Exception{
        World w=player(remote).world;when(w.getDifficulty()).thenReturn(EnumDifficulty.NORMAL);
        GameRules rules=new GameRules();when(w.getGameRules()).thenReturn(rules);
        when(w.isAreaLoaded(any(BlockPos.class),any(BlockPos.class))).thenReturn(true);
        WorldBorder border=new WorldBorder();when(w.getWorldBorder()).thenReturn(border);
        when(w.checkNoEntityCollision(any(AxisAlignedBB.class),any(Entity.class))).thenReturn(true);
        when(w.spawnEntity(any(Entity.class))).thenReturn(true);return w;
    }
    @Test public void allSixtyOriginalObjPartsParseWithoutGpuOrMissingGeometry()throws Exception{
        String[] paths;
        try(java.io.Reader index=new java.io.InputStreamReader(getClass().getResourceAsStream("/assets/metroport/blackrazid_mutant_meshes.json"),java.nio.charset.StandardCharsets.UTF_8)){
            paths=new com.google.gson.Gson().fromJson(index,String[].class);
        }
        assertEquals(60,paths.length);
        for(String path:paths)try(java.io.InputStream in=getClass().getResourceAsStream("/assets/metroport/models/blackrazid/mutants/"+path)){
            assertNotNull(path,in);ru.metrocraft.port.mcmod.McmodObjMesh mesh=ru.metrocraft.port.mcmod.McmodObjMesh.read(in);int vertices=0;
            for(java.util.List<float[]> group:mesh.groups.values())for(float[] vertex:group){vertices++;assertEquals(8,vertex.length);for(float value:vertex)assertTrue(path,Float.isFinite(value));}
            assertTrue(path,vertices>=3);assertEquals(path,0,vertices%3);
        }
    }
    @Test public void gazeRequiresVisibilityAndNativeNarrowAngle(){
        Vec3d forward=new Vec3d(0,0,1),offset=new Vec3d(0,0,10);
        assertTrue(MutantRules.looking(forward,offset,true));assertFalse(MutantRules.looking(forward,offset,false));
        assertFalse(MutantRules.looking(new Vec3d(1,0,0),offset,true));assertFalse(MutantRules.looking(new Vec3d(0,0,-1),offset,true));
        assertTrue(MutantRules.looking(new Vec3d(.01,0,1),offset,true));assertFalse(MutantRules.looking(new Vec3d(.2,0,1),offset,true));
    }
    @Test public void gazeRejectsNonFiniteZeroAndOutOfRange(){
        Vec3d forward=new Vec3d(0,0,1);
        for(Vec3d offset:new Vec3d[]{Vec3d.ZERO,new Vec3d(0,0,47),new Vec3d(Double.NaN,0,1),new Vec3d(0,Double.POSITIVE_INFINITY,1)})assertFalse(MutantRules.looking(forward,offset,true));
        assertFalse(MutantRules.looking(Vec3d.ZERO,forward,true));assertFalse(MutantRules.looking(null,forward,true));
    }
    @Test public void librarianNativeThresholdIsStrictlyAbove125(){
        assertTrue(MutantRules.librarianSlows(200,true));assertTrue(MutantRules.librarianSlows(126,true));
        for(float health:new float[]{125,124,0,-1,Float.NaN})assertFalse(MutantRules.librarianSlows(health,true));
        assertFalse(MutantRules.librarianSlows(200,false));
    }
    @Test public void spiderRequiresStrictEnergyAndRealLampAndFreshPanic(){
        assertTrue(MutantRules.spiderFears(80,11,true,true,0));
        assertFalse(MutantRules.spiderFears(80,10,true,true,0));assertFalse(MutantRules.spiderFears(80,11,false,true,0));
        assertFalse(MutantRules.spiderFears(80,11,true,false,0));assertFalse(MutantRules.spiderFears(80,11,true,true,1));
        assertFalse(MutantRules.spiderFears(0,11,true,true,0));assertFalse(MutantRules.spiderFears(80,Float.NaN,true,true,0));
    }
    @Test public void threeNativeProfilesHaveDistinctAttributesAndDimensions()throws Exception{
        World w=player(false).world;RatEntity rat=new RatEntity(w);SpiderEntity spider=new SpiderEntity(w);BiblioEntity b=new BiblioEntity(w);
        assertEquals(15,rat.getMaxHealth(),0);assertEquals(80,spider.getMaxHealth(),0);assertEquals(200,b.getMaxHealth(),0);
        assertEquals(4,rat.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue(),0);
        assertEquals(6,spider.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue(),0);
        assertEquals(10,b.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue(),0);
        assertEquals(.5,rat.width,.0001);assertEquals(.2,rat.height,.0001);assertEquals(1,b.width,0);assertEquals(2.5,b.height,0);
        assertTrue(rat.timid());assertFalse(spider.timid());assertEquals(0,rat.targetTasks.taskEntries.size());
        assertTrue(rat.canBreatheUnderwater());assertTrue(spider.canBreatheUnderwater());assertFalse(b.canBreatheUnderwater());
    }
    @Test public void actualLibrarianAppliesWebOnlyAfterAcceptedServerGaze()throws Exception{
        EntityPlayer p=player(false);BiblioEntity b=new BiblioEntity(p.world);b.setPosition(0,0,5);
        java.lang.reflect.Field web=Entity.class.getDeclaredField("isInWeb");web.setAccessible(true);
        Vec3d direction=new Vec3d(0,b.height/1.5-p.getEyeHeight(),5).normalize();when(p.getLookVec()).thenReturn(direction);when(p.canEntityBeSeen(b)).thenReturn(true);
        assertTrue(b.applyGaze(p));assertTrue(web.getBoolean(b));web.setBoolean(b,false);b.setHealth(125);assertFalse(b.applyGaze(p));assertFalse(web.getBoolean(b));
        b.setHealth(200);when(p.canEntityBeSeen(b)).thenReturn(false);assertFalse(b.applyGaze(p));assertFalse(web.getBoolean(b));
        BiblioEntity client=new BiblioEntity(player(true).world);assertFalse(client.applyGaze(p));
    }
    @Test public void actualSpiderUsesSelectedSurvivalNamespaceAndRealHeldLamp()throws Exception{
        EntityPlayer p=player(false);SpiderEntity spider=new SpiderEntity(p.world);sight(spider,p);
        SurvivalState s=new SurvivalState();s.energy=20;s.lamp=true;SurvivalSystem.save(p,s);
        assertFalse(spider.applyLamp(p));ItemStack lamp=new ItemStack(new SurvivalItem(SurvivalItem.Type.LAMP));hold(p,lamp);
        assertTrue(spider.applyLamp(p));assertEquals(60,spider.panicTicks());assertTrue(spider.isBurning());
        assertFalse(spider.applyLamp(p));assertFalse(spider.attackEntityAsMob(p));assertEquals(20,SurvivalSystem.get(p).energy,0);
    }
    @Test public void spiderLampHonorsOcclusionAndDoesNotRunOnClient()throws Exception{
        EntityPlayer p=player(false);SpiderEntity spider=new SpiderEntity(p.world);sight(spider,p);hold(p,new ItemStack(new SurvivalItem(SurvivalItem.Type.LAMP)));
        SurvivalState s=new SurvivalState();s.energy=50;s.lamp=true;SurvivalSystem.save(p,s);when(p.canEntityBeSeen(spider)).thenReturn(false);
        assertFalse(spider.applyLamp(p));assertEquals(0,spider.panicTicks());assertFalse(spider.isBurning());
        assertFalse(new SpiderEntity(player(true).world).applyLamp(p));
    }
    @Test public void spiderPanicPersistsPerEntityAndClampsMalformedNBT()throws Exception{
        World w=player(false).world;SpiderEntity a=new SpiderEntity(w),b=new SpiderEntity(w);NBTTagCompound tag=new NBTTagCompound();a.writeEntityToNBT(tag);
        tag.setInteger("BlackrazidPanic",53);a.readEntityFromNBT(tag);assertEquals(53,a.panicTicks());assertEquals(0,b.panicTicks());
        NBTTagCompound saved=new NBTTagCompound();a.writeEntityToNBT(saved);b.readEntityFromNBT(saved);assertEquals(53,b.panicTicks());
        saved.setInteger("BlackrazidPanic",Integer.MAX_VALUE);b.readEntityFromNBT(saved);assertEquals(60,b.panicTicks());
        saved.setInteger("BlackrazidPanic",-12);b.readEntityFromNBT(saved);assertEquals(0,b.panicTicks());
    }
    @Test public void spawnerNativePeriodsAndWaveSizes()throws Exception{
        World w=spawnWorld(false);MutantSpawner a=new MutantSpawner.SpiderNest(w),b=new MutantSpawner.LibrarianNest(w);
        assertEquals(4000,a.remaining());assertEquals(2,a.batch());assertEquals(10000,b.remaining());assertEquals(1,b.batch());
        assertEquals(2,a.spawnWave());assertEquals(1,b.spawnWave());
        ArgumentCaptor<Entity> capture=ArgumentCaptor.forClass(Entity.class);verify(w,times(3)).spawnEntity(capture.capture());
        assertTrue(capture.getAllValues().get(0) instanceof SpiderEntity);assertTrue(capture.getAllValues().get(2) instanceof BiblioEntity);
    }
    @Test public void spawnerWaitsFullTimerAndIsServerOnly()throws Exception{
        World w=spawnWorld(false);MutantSpawner a=new MutantSpawner.SpiderNest(w);
        for(int i=0;i<3999;i++)a.tickSpawner();verify(w,never()).spawnEntity(any(Entity.class));assertEquals(1,a.remaining());
        a.tickSpawner();verify(w,times(2)).spawnEntity(any(Entity.class));assertEquals(4000,a.remaining());
        World client=spawnWorld(true);MutantSpawner c=new MutantSpawner.SpiderNest(client);for(int i=0;i<4001;i++)c.tickSpawner();
        assertEquals(4000,c.remaining());assertEquals(0,c.spawnWave());verify(client,never()).spawnEntity(any(Entity.class));
    }
    @Test public void spawnerCountdownPersistsAndDoesNotResetOnReload()throws Exception{
        World w=spawnWorld(false);MutantSpawner a=new MutantSpawner.SpiderNest(w);for(int i=0;i<123;i++)a.tickSpawner();
        NBTTagCompound tag=new NBTTagCompound();a.writeEntityToNBT(tag);MutantSpawner b=new MutantSpawner.SpiderNest(w);b.readEntityFromNBT(tag);assertEquals(3877,b.remaining());
        tag.setInteger("BlackrazidSpawnDelay",Integer.MAX_VALUE);b.readEntityFromNBT(tag);assertEquals(4000,b.remaining());
        tag.setInteger("BlackrazidSpawnDelay",-1);b.readEntityFromNBT(tag);assertEquals(0,b.remaining());
        b.readEntityFromNBT(new NBTTagCompound());assertEquals(4000,b.remaining());
    }
    @Test public void spawnerRefusesUnloadedChunksAndBlockedSpawn()throws Exception{
        World w=spawnWorld(false);MutantSpawner a=new MutantSpawner.SpiderNest(w);when(w.isAreaLoaded(any(BlockPos.class),any(BlockPos.class))).thenReturn(false);
        assertEquals(0,a.spawnWave());verify(w,never()).spawnEntity(any(Entity.class));
        when(w.isAreaLoaded(any(BlockPos.class),any(BlockPos.class))).thenReturn(true);when(w.checkNoEntityCollision(any(AxisAlignedBB.class),any(Entity.class))).thenReturn(false);
        assertEquals(0,a.spawnWave());verify(w,never()).spawnEntity(any(Entity.class));
    }
    @Test public void spawnerHonorsCapAndDoesNotOverrunPartialCapacity()throws Exception{
        World w=spawnWorld(false);MutantSpawner a=new MutantSpawner.SpiderNest(w);
        when(w.getEntitiesWithinAABB(eq(BlackrazidMutant.class),any(AxisAlignedBB.class))).thenReturn(Collections.nCopies(16,(BlackrazidMutant)null));
        assertEquals(0,a.spawnWave());verify(w,never()).spawnEntity(any(Entity.class));
        when(w.getEntitiesWithinAABB(eq(BlackrazidMutant.class),any(AxisAlignedBB.class))).thenReturn(Collections.nCopies(15,(BlackrazidMutant)null));
        assertEquals(1,a.spawnWave());verify(w,times(1)).spawnEntity(any(Entity.class));
    }
    @Test public void canceledSpawnGetsBoundedRetryInsteadOfAFullFreeWave()throws Exception{
        World w=spawnWorld(false);MutantSpawner a=new MutantSpawner.SpiderNest(w);NBTTagCompound tag=new NBTTagCompound();tag.setInteger("BlackrazidSpawnDelay",1);a.readEntityFromNBT(tag);
        when(w.spawnEntity(any(Entity.class))).thenReturn(false);a.tickSpawner();assertEquals(20,a.remaining());
        when(w.spawnEntity(any(Entity.class))).thenReturn(true);for(int i=0;i<20;i++)a.tickSpawner();assertEquals(4000,a.remaining());
        verify(w,times(4)).spawnEntity(any(Entity.class));
    }
    @Test public void peacefulAndDisabledMobSpawningPauseCountdown()throws Exception{
        World w=spawnWorld(false);MutantSpawner a=new MutantSpawner.SpiderNest(w);when(w.getDifficulty()).thenReturn(EnumDifficulty.PEACEFUL);
        a.tickSpawner();assertEquals(4000,a.remaining());assertEquals(0,a.spawnWave());when(w.getDifficulty()).thenReturn(EnumDifficulty.NORMAL);
        w.getGameRules().setOrCreateGameRule("doMobSpawning","false");a.tickSpawner();assertEquals(4000,a.remaining());assertEquals(0,a.spawnWave());
        verify(w,never()).spawnEntity(any(Entity.class));
    }
    @Test public void mapNodeRemovalRequiresNativeSnowballOrCreativeOrVoid()throws Exception{
        EntityPlayer p=player(false);MutantSpawner a=new MutantSpawner.SpiderNest(p.world);
        assertFalse(a.attackEntityFrom(DamageSource.causePlayerDamage(p),10));assertFalse(a.isDead);
        hold(p,new ItemStack(Items.SNOWBALL));assertTrue(a.attackEntityFrom(DamageSource.causePlayerDamage(p),1));assertTrue(a.isDead);
        MutantSpawner b=new MutantSpawner.LibrarianNest(p.world);assertTrue(b.attackEntityFrom(DamageSource.OUT_OF_WORLD,1));assertTrue(b.isDead);
        EntityPlayer creative=player(false);creative.capabilities.isCreativeMode=true;assertTrue(new MutantSpawner.SpiderNest(creative.world).attackEntityFrom(DamageSource.causePlayerDamage(creative),1));
    }
}
