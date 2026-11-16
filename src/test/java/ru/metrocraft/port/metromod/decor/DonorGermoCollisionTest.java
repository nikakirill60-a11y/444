package ru.metrocraft.port.metromod.decor;

import java.util.*;
import net.minecraft.init.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Actual Forge adapter/World fixtures; standalone motion checks are separate. */
public class DonorGermoCollisionTest {
    @BeforeClass public static void boot(){Bootstrap.register();OriginalGates.initialize();}
    private OriginalGateTile root;
    private OriginalDecorTestWorld grid;
    @Before public void setup() {
        root=new OriginalGateTile();root.setPos(new BlockPos(0,64,0));
        root.configure(UUID.randomUUID(),OriginalGateProfile.get("metromod_germo_1"),EnumFacing.SOUTH);
        root.ready=true;root.setWorld(mock(World.class));grid=new OriginalDecorTestWorld(root);
    }
    private void ticks(boolean open,int n) {when(grid.world.isBlockPowered(root.getPos())).thenReturn(open);for(int i=0;i<n;i++)root.update();}
    @Test public void all17HaveSourceMetadataAndClosedPhysicsInEveryFacing() {
        int count=0;for(OriginalGateProfile p:OriginalGateProfile.all())if(p.is("Germo")) {
            count++;assertNotNull(p.donorGermo);
            for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL) {
                DonorGermoMotion state=p.donorGermo.seed(p.id,facing,0);
                assertEquals(0,state.progress(),0);assertFalse(p.donorGermo.boxes(p,state,root.getPos(),facing).isEmpty());
                for(AxisAlignedBB box:p.donorGermo.boxes(p,state,root.getPos(),facing))assertTrue(box.minX<box.maxX && box.minY<box.maxY && box.minZ<box.maxZ);
            }
        }
        assertEquals(17,count);
    }
    @Test public void unitAdapterInvertsCatalogueNotMeshExtrema() {
        OriginalGateProfile p=OriginalGateProfile.get("metromod_germo_2");
        DonorGermoMotion m=new DonorGermoMotion(p.id,0);m.place(2,1,0);
        AxisAlignedBB b=p.donorGermo.boxes(p,m,BlockPos.ORIGIN,EnumFacing.SOUTH).get(0);
        assertEquals(.5+(2-p.donorGermo.offset[0])/p.donorGermo.scale[0],b.minX,1e-12);
        assertEquals(p.ground+(1-p.donorGermo.offset[1])/p.donorGermo.scale[1],b.minY,1e-12);
        assertEquals(.5+(1-p.donorGermo.offset[2])/p.donorGermo.scale[2],b.maxZ,1e-12);
        assertEquals(1/p.donorGermo.scale[0],b.maxX-b.minX,1e-12);
    }
    @Test public void roundingPrecedesAdapterForAllCardinalYaws() {
        OriginalGateProfile p=root.profile;
        for(EnumFacing f:EnumFacing.Plane.HORIZONTAL) {
            DonorGermoMotion m=p.donorGermo.seed(p.id,f,0);
            assertEquals(-f.getHorizontalAngle(),m.state.rotation.y,0);
            List<AxisAlignedBB> boxes=p.donorGermo.boxes(p,m,BlockPos.ORIGIN,f);
            // Every physical box comes from exactly ONE source integer grid cell.
            assertEquals(m.cells().size(),boxes.size());
            EnumFacing right=f.rotateYCCW();int i=0;
            for(DonorGermoMotion.Cell c:m.cells()) {
                AxisAlignedBB box=boxes.get(i++);
                double[] expected={Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
                for(int mask=0;mask<8;mask++) {
                    double sx=c.x+((mask&1)==0?0:1),sy=c.y+((mask&2)==0?0:1),sz=c.z+((mask&4)==0?0:1);
                    double lx=(sx*right.getXOffset()+sz*right.getZOffset()-p.donorGermo.offset[0])/p.donorGermo.scale[0];
                    double lz=(sx*f.getXOffset()+sz*f.getZOffset()-p.donorGermo.offset[2])/p.donorGermo.scale[2];
                    double[] w={.5+lx*right.getXOffset()+lz*f.getXOffset(),p.ground+(sy-p.donorGermo.offset[1])/p.donorGermo.scale[1],.5+lx*right.getZOffset()+lz*f.getZOffset()};
                    for(int k=0;k<3;k++){expected[k]=Math.min(expected[k],w[k]);expected[k+3]=Math.max(expected[k+3],w[k]);}
                }
                assertEquals(new AxisAlignedBB(expected[0],expected[1],expected[2],expected[3],expected[4],expected[5]),box);
            }
        }
    }
    @Test public void cosmeticProgressCannotOpenCurrentSnapshotPhysics() {
        List<AxisAlignedBB> before=root.collisionBoxes();root.progress=1;assertEquals(before,root.collisionBoxes());
    }
    @Test public void stoneInReleasedApertureBlocksCloseWithoutDestroyingOrAdvancingState() {
        ticks(true,650);assertEquals(1,root.progress,0);
        BlockPos stone=root.profile.occupied(root.getPos(),root.facing,0).stream().filter(p->!root.occupied().contains(p)).findFirst().get();
        grid.states.put(stone,Blocks.STONE.getDefaultState());ticks(false,650);assertTrue(root.progress>0);
        NBTTagCompound stopped=root.getUpdateTag().getCompoundTag("DonorGermo");ticks(false,5);
        assertEquals(stopped,root.getUpdateTag().getCompoundTag("DonorGermo"));assertEquals(Blocks.STONE,grid.states.get(stone).getBlock());
        verify(grid.world,never()).destroyBlock(root.getPos(),true);
        grid.states.remove(stone);ticks(false,650);assertEquals(0,root.progress,0);
    }
    @Test public void failedReleaseRollsBackWorldAndDonorVectorsTogether() {
        when(grid.world.isBlockPowered(root.getPos())).thenReturn(true);boolean tested=false;
        for(int tick=0;tick<650;tick++) {
            DonorGermoMotion m=DonorGermoCollision.restore(root.profile.id,root.facing,root.getUpdateTag().getCompoundTag("DonorGermo"));
            DonorGermoMotion next=m.next(true);
            List<BlockPos> wanted=DonorGermoCollision.occupied(root.getPos(),root.profile.donorGermo.boxes(root.profile,next,root.getPos(),root.facing));
            List<BlockPos> released=new ArrayList<>(root.occupied());released.removeAll(wanted);
            if(released.size()>1) {
                Set<BlockPos> before=new HashSet<>(grid.states.keySet());NBTTagCompound saved=root.getUpdateTag().getCompoundTag("DonorGermo");
                // doAnswer is essential here: when(mock.mutator(...)) executes
                // the existing mutable-world Answer DURING stubbing.
                final int[] releases={0};
                doAnswer(invocation->{
                    if(++releases[0]==2)return false;
                    BlockPos cell=invocation.getArgument(0);grid.states.remove(cell);grid.tiles.remove(cell);return true;
                }).when(grid.world).setBlockToAir(any(BlockPos.class));
                root.update();assertEquals("must actually fail after the first successful release",2,releases[0]);
                assertEquals(before,grid.states.keySet());assertEquals(saved,root.getUpdateTag().getCompoundTag("DonorGermo"));tested=true;break;
            }
            root.update();
        }
        assertTrue("Did not reach a donor release transition",tested);
    }
    @Test public void networkSnapshotKeepsCellsFlagsAndPreviousAcrossReversal() {
        ticks(true,100);OriginalGateTile client=new OriginalGateTile();client.setPos(root.getPos());client.readFromNBT(root.getUpdateTag());
        assertEquals(root.collisionBoxes(),client.collisionBoxes());assertEquals(root.getUpdateTag().getCompoundTag("DonorGermo"),client.getUpdateTag().getCompoundTag("DonorGermo"));
        OriginalGateTile restored=new OriginalGateTile();restored.setPos(root.getPos());restored.readFromNBT(root.getUpdateTag());restored.setWorld(mock(World.class));new OriginalDecorTestWorld(restored);
        ticks(false,45);for(int i=0;i<45;i++)restored.update();
        assertEquals(root.getUpdateTag().getCompoundTag("DonorGermo"),restored.getUpdateTag().getCompoundTag("DonorGermo"));
    }
    @Test public void foreignUuidStopsMigrationWithoutAdoptionOrDeletion() {
        BlockPos part=root.occupied().get(1);OriginalGatePartTile foreign=(OriginalGatePartTile)grid.tiles.get(part);foreign.owner=UUID.randomUUID();UUID id=foreign.owner;
        NBTTagCompound tag=root.getUpdateTag();tag.setInteger("LayoutVersion",2);root.readFromNBT(tag);root.update();
        assertEquals(id,foreign.owner);assertSame(foreign,grid.tiles.get(part));verify(grid.world,never()).destroyBlock(root.getPos(),true);verify(grid.world,never()).setBlockToAir(part);
    }
    @Test public void corruptSnapshotFallsBackToBoundedMigrationNotNaNPhysics() {
        NBTTagCompound tag=root.getUpdateTag();tag.getCompoundTag("DonorGermo").setFloat("X",Float.NaN);root.readFromNBT(tag);
        assertEquals(0,root.getUpdateTag().getInteger("LayoutVersion"));assertFalse(root.collisionBoxes().isEmpty());root.update();assertEquals(4,root.getUpdateTag().getInteger("LayoutVersion"));
    }
}
