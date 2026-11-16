package ru.metrocraft.port.metromod.decor;

import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real harvest hook + controller collision/NBT; does not run the game client. */
public class OriginalCollisionCutsTest {
    @BeforeClass public static void boot(){Bootstrap.register();OriginalGates.initialize();}
    private OriginalGateTile root;private OriginalDecorTestWorld grid;private EntityPlayer player;
    private void setup(OriginalGateProfile p) {
        root=new OriginalGateTile();root.setPos(new BlockPos(0,64,0));root.configure(UUID.randomUUID(),p,EnumFacing.SOUTH);
        root.setWorld(mock(World.class));root.ready=true;grid=new OriginalDecorTestWorld(root);root.onLoad();
        player=mock(EntityPlayer.class);player.capabilities=new PlayerCapabilities();when(player.getHeldItemMainhand()).thenReturn(ItemStack.EMPTY);
        when(player.canPlayerEdit(any(),any(),any())).thenReturn(true);when(grid.world.isBlockModifiable(eq(player),any())).thenReturn(true);
    }
    private void germo(){setup(OriginalGateProfile.get("metromod_germo_1"));}
    private BlockPos part(){return root.occupied().stream().filter(p->!p.equals(root.getPos())).findFirst().get();}
    private void harvest(BlockPos cell) {
        assertTrue(OriginalGates.PART.removedByPlayer(OriginalGates.PART.getDefaultState(),grid.world,cell,player,false));
        OriginalGates.PART.breakBlock(grid.world,cell,OriginalGates.PART.getDefaultState());
    }
    private void empty(BlockPos cell) {
        assertFalse(root.occupied().contains(cell));
        for(AxisAlignedBB b:root.collisionBoxes())assertFalse(b.intersects(new AxisAlignedBB(cell).shrink(1e-7)));
        List<AxisAlignedBB> hits=new ArrayList<>();OriginalDecorCollisions.add(grid.world,new AxisAlignedBB(cell).shrink(1e-7),hits);assertTrue(hits.isEmpty());
    }
    @Test public void subtractWholeGridCellWithoutLosingAdjacentCollision() {
        OriginalCollisionCuts c=new OriginalCollisionCuts();c.add(BlockPos.ORIGIN);
        List<AxisAlignedBB> result=c.apply(Collections.singletonList(new AxisAlignedBB(-1,-1,-1,2,2,2)),BlockPos.ORIGIN);
        double volume=0;for(AxisAlignedBB b:result){volume+=(b.maxX-b.minX)*(b.maxY-b.minY)*(b.maxZ-b.minZ);assertFalse(b.intersects(new AxisAlignedBB(BlockPos.ORIGIN)));}
        assertEquals(26,volume,1e-10);assertEquals(6,result.size());
    }
    @Test public void invalidSnapshotDoesNotCreateUnboundedCuts() {
        OriginalCollisionCuts c=new OriginalCollisionCuts();c.load(new int[]{1,2});assertEquals(0,c.save().length);
        c.load(new int[]{Integer.MIN_VALUE,0,0});assertEquals(0,c.save().length);
        c.load(new int[12291]);assertEquals(0,c.save().length);
        c.add(new BlockPos(2,3,4));OriginalCollisionCuts copy=new OriginalCollisionCuts();copy.load(c.save());assertArrayEquals(c.save(),copy.save());
    }
    @Test public void realPlayerHarvestWorksForAll44DoorAndGermoProfiles() {
        int count=0;for(OriginalGateProfile p:OriginalGateProfile.all())if(p.is("Door") || p.is("Germo")) {
            setup(p);BlockPos cell=part();harvest(cell);empty(cell);
            assertTrue(grid.world.isAirBlock(cell));assertSame(root,grid.tiles.get(root.getPos()));
            verify(grid.world,never()).destroyBlock(root.getPos(),true);assertEquals(0,OriginalGates.PART.quantityDropped(new Random()));count++;
        }assertEquals(44,count);
    }
    @Test public void cutSurvivesOpenCloseAndStoneInCutCellDoesNotStopGermo() {
        germo();BlockPos cell=part();harvest(cell);grid.states.put(cell,Blocks.STONE.getDefaultState());
        when(grid.world.isBlockPowered(root.getPos())).thenReturn(true);for(int i=0;i<650;i++)root.update();assertEquals(1,root.progress,0);
        when(grid.world.isBlockPowered(root.getPos())).thenReturn(false);for(int i=0;i<650;i++)root.update();assertEquals(0,root.progress,0);
        empty(cell);assertEquals(Blocks.STONE,grid.states.get(cell).getBlock());
    }
    @Test public void saveAndClientPacketRetainCutGeometry() {
        germo();BlockPos cell=part();harvest(cell);NBTTagCompound tag=root.getUpdateTag();
        OriginalGateTile restored=new OriginalGateTile();restored.readFromNBT(tag);
        assertEquals(root.collisionBoxes(),restored.collisionBoxes());assertArrayEquals(tag.getIntArray("CollisionCuts"),restored.getUpdateTag().getIntArray("CollisionCuts"));
        restored.setWorld(mock(World.class));OriginalDecorTestWorld loaded=new OriginalDecorTestWorld(restored);restored.update();
        assertFalse(restored.occupied().contains(cell));assertTrue(loaded.world.isAirBlock(cell));
    }
    @Test public void failedWorldRemovalRollsBackTheCutToo() {
        germo();BlockPos cell=part();doReturn(false).when(grid.world).setBlockState(eq(cell),eq(Blocks.AIR.getDefaultState()),anyInt());
        assertFalse(OriginalGates.PART.removedByPlayer(OriginalGates.PART.getDefaultState(),grid.world,cell,player,false));
        assertTrue(root.occupied().contains(cell));assertEquals(0,root.getUpdateTag().getIntArray("CollisionCuts").length);
    }
    @Test public void protectedHarvestCannotEditOrDeleteAnything() {
        germo();BlockPos cell=part();when(grid.world.isBlockModifiable(player,cell)).thenReturn(false);
        assertFalse(OriginalGates.PART.removedByPlayer(OriginalGates.PART.getDefaultState(),grid.world,cell,player,false));
        assertTrue(root.occupied().contains(cell));assertEquals(0,root.getUpdateTag().getIntArray("CollisionCuts").length);
        verify(grid.world,never()).setBlockState(eq(cell),any(),anyInt());
    }
    @Test public void programmaticReleaseDoesNotRecordAPlayerCut() {
        germo();root.updatingParts=true;OriginalGates.PART.breakBlock(grid.world,part(),OriginalGates.PART.getDefaultState());
        assertEquals(0,root.getUpdateTag().getIntArray("CollisionCuts").length);verify(grid.world,never()).destroyBlock(root.getPos(),true);
    }
    @Test public void supportFloorIsNeverReservedByGroundedDoorPhysics() {
        int count=0;for(OriginalGateProfile p:OriginalGateProfile.all())if(p.is("Door") || p.is("Germo")) {
            for(EnumFacing f:EnumFacing.Plane.HORIZONTAL) {
                for(AxisAlignedBB b:p.initialPhysics(new BlockPos(0,64,0),f,0))assertTrue(p.id,b.minY>=64);
                for(BlockPos cell:p.occupied(new BlockPos(0,64,0),f,0))assertTrue(cell.getY()>=64);
            }count++;
        }assertEquals(44,count);
    }
    @Test public void legacyMigrationKeepsCutsAndNeverReinstallsTheirProxy() {
        germo();BlockPos cell=part();harvest(cell);NBTTagCompound old=root.getUpdateTag();old.setInteger("LayoutVersion",4);root.readFromNBT(old);root.update();
        assertEquals(5,root.getUpdateTag().getInteger("LayoutVersion"));empty(cell);assertTrue(grid.world.isAirBlock(cell));
    }
    @Test public void controllerRemainsSelectableAndCannotBeCutAsAnExtraCell() {
        germo();assertFalse(root.cutCollision(root.getPos()));when(grid.world.isBlockPowered(root.getPos())).thenReturn(true);for(int i=0;i<650;i++)root.update();
        assertNotNull(root.rayTrace(root.getPos(),new Vec3d(.5,64.5,-2),new Vec3d(.5,64.5,2)));
    }
}
