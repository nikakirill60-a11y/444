package ru.metrocraft.port.structures;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import java.io.*;
import java.util.*;
import static org.junit.Assert.*;

public class MetroNetworkJobTest {
    @Rule public TemporaryFolder tmp=new TemporaryFolder();
    private static MetroNetworkPlan plan;
    private static final BlockPos ORIGIN=new BlockPos(0,32,0);
    @BeforeClass public static void boot() {Bootstrap.register();plan=MetroNetworkPlan.create(24);}
    private static class Fake implements MetroNetworkJob.Access {
        final Map<BlockPos,IBlockState> blocks=new HashMap<>();final Map<BlockPos,TileEntity> tiles=new HashMap<>();
        BlockPos unloaded,denied,blockedSet;int writes;boolean allowed=true;
        public boolean loaded(BlockPos p) {return !p.equals(unloaded);}
        public boolean inside(BlockPos p) {return p.getY()>0 && p.getY()<255;}
        public boolean editable(BlockPos p) {return allowed && !p.equals(denied);}
        public IBlockState state(BlockPos p) {return blocks.getOrDefault(p,Blocks.STONE.getDefaultState());}
        public TileEntity tile(BlockPos p) {return tiles.get(p);}
        public boolean set(BlockPos p,IBlockState state) {
            if(p.equals(blockedSet))return false;writes++;blocks.put(p,state);tiles.remove(p);if(state.getBlock()==Blocks.WALL_SIGN)tiles.put(p,new TileEntitySign());return true;
        }
        public void sign(BlockPos p,String[] text) {TileEntitySign sign=(TileEntitySign)tiles.get(p);for(int i=0;i<4;i++)sign.signText[i]=new TextComponentString(text[i]);}
    }
    private final List<String> messages=new ArrayList<>();
    private MetroNetworkJob job(Fake world) {return new MetroNetworkJob(world,plan,ORIGIN,0,tmp.getRoot(),UUID.randomUUID(),messages::add);}
    private void run(MetroNetworkJob job) throws Exception {for(int i=0;i<500000 && job.phase()!=MetroNetworkJob.Phase.DONE;i++)job.step();assertEquals(MetroNetworkJob.Phase.DONE,job.phase());}
    private void checked(MetroNetworkJob job) throws Exception {for(int i=0;i<200000 && job.phase()==MetroNetworkJob.Phase.CHECK;i++)job.step();assertEquals(messages.toString(),MetroNetworkJob.Phase.BUILD,job.phase());}
    @Test public void naturalAllowlistRejectsLiquidsBedrockPlayerBlocksAndTileEntities() throws Exception {
        for(IBlockState s:Arrays.asList(Blocks.WATER.getDefaultState(),Blocks.LAVA.getDefaultState(),Blocks.BEDROCK.getDefaultState(),Blocks.CHEST.getDefaultState(),Blocks.PLANKS.getDefaultState(),Blocks.STONEBRICK.getDefaultState()))assertFalse(MetroNetworkJob.natural(s));
        for(IBlockState s:Arrays.asList(Blocks.STONE.getStateFromMeta(3),Blocks.DIRT.getDefaultState(),Blocks.AIR.getDefaultState(),Blocks.IRON_ORE.getDefaultState()))assertTrue(MetroNetworkJob.natural(s));
        Fake world=new Fake();BlockPos first=plan.cells().keySet().iterator().next().add(ORIGIN);world.tiles.put(first,new TileEntityChest());MetroNetworkJob job=job(world);run(job);assertEquals(0,world.writes);
    }
    @Test public void anyPreflightObstructionLeavesWholeWorldUnchanged() throws Exception {
        for(int mode=0;mode<4;mode++) {
            Fake world=new Fake();BlockPos pos=new BlockPos(0,0,-60).add(ORIGIN);
            if(mode==0)world.unloaded=pos;if(mode==1)world.denied=pos;if(mode==2)world.blocks.put(pos,Blocks.FLOWING_WATER.getDefaultState());if(mode==3)world.blocks.put(pos,Blocks.PLANKS.getDefaultState());
            MetroNetworkJob job=job(world);run(job);assertEquals(messages.toString(),0,world.writes);assertFalse(new File(tmp.getRoot(),"minetro/network-backups/"+job.id+".nbt").exists());
        }
    }
    @Test public void backupExistsBeforeFirstWriteAndRoundTripsAllStatesAndSignText() throws Exception {
        Fake world=new Fake();MetroNetworkJob job=job(world);checked(job);assertEquals(0,world.writes);
        MetroNetworkBackup backup=MetroNetworkBackup.load(tmp.getRoot(),job.id);assertTrue(backup.changes.size()>60000);assertEquals(0,backup.dimension);
        assertEquals(job.id,backup.id);assertTrue(backup.changes.stream().anyMatch(c->c.text!=null && Arrays.asList(c.text).contains("СЕВЕРНАЯ")));
        job.step();assertEquals(1,world.writes);
    }
    @Test public void backupIoFailureCannotEditWorld() throws Exception {
        Fake world=new Fake();File bad=tmp.newFile("not-a-directory");MetroNetworkJob job=new MetroNetworkJob(world,plan,ORIGIN,0,bad,UUID.randomUUID(),messages::add);
        try {while(job.phase()==MetroNetworkJob.Phase.CHECK)job.step();fail();}catch(IOException expected) {}
        assertEquals(0,world.writes);
    }
    @Test public void completedBuildCanBeRestoredAfterReloadingBackupFromDisk() throws Exception {
        Fake world=new Fake();MetroNetworkJob job=job(world);run(job);assertTrue(job.written()>60000);
        MetroNetworkBackup copy=MetroNetworkBackup.load(tmp.getRoot(),job.id);
        for(MetroNetworkBackup.Change c:copy.changes)assertSame(c.after,world.state(c.pos));
        MetroNetworkJob undo=new MetroNetworkJob(world,copy,0,tmp.getRoot(),UUID.randomUUID(),messages::add);run(undo);
        assertEquals(0,undo.skipped());assertEquals(job.written(),undo.restored());for(MetroNetworkBackup.Change c:copy.changes)assertSame(c.before,world.state(c.pos));
    }
    @Test public void cancellationDuringCheckDoesNotWriteAndDuringBuildRestoresOnlyWrittenBlocks() throws Exception {
        Fake untouched=new Fake();MetroNetworkJob checking=job(untouched);checking.step();checking.cancel();run(checking);assertEquals(0,untouched.writes);
        Fake world=new Fake();MetroNetworkJob placing=job(world);checked(placing);for(int i=0;i<31;i++)placing.step();assertEquals(31,placing.written());placing.cancel();run(placing);
        assertEquals(31,placing.restored());for(IBlockState state:world.blocks.values())assertSame(Blocks.STONE.getDefaultState(),state);
    }
    @Test public void concurrentPlayerEditCausesRollbackWithoutOverwritingThatEdit() throws Exception {
        Fake world=new Fake();MetroNetworkJob job=job(world);checked(job);for(int i=0;i<8;i++)job.step();
        MetroNetworkBackup copy=MetroNetworkBackup.load(tmp.getRoot(),job.id);BlockPos conflict=copy.changes.get(8).pos;world.blocks.put(conflict,Blocks.DIAMOND_BLOCK.getDefaultState());run(job);
        assertSame(Blocks.DIAMOND_BLOCK.getDefaultState(),world.state(conflict));assertEquals(8,job.restored());
    }
    @Test public void failedSetRollsBackAlreadyWrittenCells() throws Exception {
        Fake world=new Fake();MetroNetworkJob job=job(world);checked(job);for(int i=0;i<5;i++)job.step();
        world.blockedSet=MetroNetworkBackup.load(tmp.getRoot(),job.id).changes.get(5).pos;run(job);assertEquals(5,job.restored());
    }
    @Test public void undoSkipsPlayerBlocksInventoriesAndEditedSigns() throws Exception {
        Fake world=new Fake();MetroNetworkJob job=job(world);run(job);MetroNetworkBackup backup=MetroNetworkBackup.load(tmp.getRoot(),job.id);
        MetroNetworkBackup.Change a=backup.changes.get(0),b=backup.changes.get(1),s=backup.changes.stream().filter(c->c.text!=null).findFirst().get();
        world.blocks.put(a.pos,Blocks.GOLD_BLOCK.getDefaultState());world.tiles.put(b.pos,new TileEntityChest());((TileEntitySign)world.tiles.get(s.pos)).signText[0]=new TextComponentString("Игрок изменил");
        MetroNetworkJob undo=new MetroNetworkJob(world,backup,0,tmp.getRoot(),UUID.randomUUID(),messages::add);run(undo);
        assertEquals(3,undo.skipped());assertSame(Blocks.GOLD_BLOCK.getDefaultState(),world.state(a.pos));assertTrue(world.tiles.get(b.pos) instanceof TileEntityChest);assertEquals("Игрок изменил",((TileEntitySign)world.tiles.get(s.pos)).signText[0].getUnformattedText());
    }
    @Test public void undoNeverLoadsChunksOrCrossesDimensionBoundary() throws Exception {
        Fake world=new Fake();BlockPos pos=new BlockPos(1,32,1);world.blocks.put(pos,Blocks.BRICK_BLOCK.getDefaultState());
        MetroNetworkBackup backup=new MetroNetworkBackup(UUID.randomUUID(),0,Collections.singletonList(new MetroNetworkBackup.Change(pos,Blocks.STONE.getDefaultState(),Blocks.BRICK_BLOCK.getDefaultState(),null)));
        try {new MetroNetworkJob(world,backup,1,tmp.getRoot(),UUID.randomUUID(),messages::add);fail();}catch(IllegalArgumentException expected) {}
        world.unloaded=pos;MetroNetworkJob undo=new MetroNetworkJob(world,backup,0,tmp.getRoot(),UUID.randomUUID(),messages::add);run(undo);assertEquals(1,undo.skipped());assertEquals(0,world.writes);
    }
    @Test public void oneTickCannotWriteWholeStructure() throws Exception {
        Fake world=new Fake();MetroNetworkJob job=job(world);checked(job);job.tick();assertTrue(world.writes<=512);assertEquals(MetroNetworkJob.Phase.BUILD,job.phase());
    }
    @Test public void backupValidationRejectsUnknownUuid() throws Exception {
        try {MetroNetworkBackup.load(tmp.getRoot(),UUID.randomUUID());fail();}catch(IOException expected) {}
    }
}
