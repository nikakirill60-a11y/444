package ru.metrocraft.port.metromod.decor;

import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Actual mutable-world regression for the two reported layout failures. */
public class OriginalLayoutTest {
    @BeforeClass public static void boot() { Bootstrap.register();OriginalGates.initialize(); }
    private OriginalGateTile root;
    private OriginalDecorTestWorld grid;
    @Before public void setup() {
        root=new OriginalGateTile();root.setPos(new BlockPos(0,64,0));
        root.configure(UUID.randomUUID(),OriginalGateProfile.get("metromod_germo_1"),EnumFacing.SOUTH);root.setWorld(mock(World.class));root.ready=true;
        grid=new OriginalDecorTestWorld(root);
    }
    private void open() {when(grid.world.isBlockPowered(root.getPos())).thenReturn(true);for(int i=0;i<650;i++)root.update();assertEquals(1,root.progress,0);}
    @Test public void onlyCurrentGeometryNotTheWholeSwingPocketIsOccupied() {
        assertTrue(root.occupied().size()<root.footprint().size());
        for(BlockPos cell:root.footprint())if(!root.occupied().contains(cell))assertTrue(grid.world.isAirBlock(cell));
    }
    @Test public void openingReleasesApertureForRealBuildingBlocks() {
        BlockPos aperture=root.getPos().up();assertFalse(grid.world.isAirBlock(aperture));open();assertTrue(grid.world.isAirBlock(aperture));
        grid.states.put(aperture,Blocks.STONE.getDefaultState());when(grid.world.isBlockPowered(root.getPos())).thenReturn(false);
        for(int i=0;i<650;i++)root.update();assertTrue(root.progress>0);assertEquals(Blocks.STONE,grid.states.get(aperture).getBlock());
        grid.states.remove(aperture);for(int i=0;i<650;i++)root.update();assertEquals(0,root.progress,0);
    }
    @Test public void renderPocketDoesNotReserveInvisibleModelCollision() {
        BlockPos obstacle=root.getPos().up(5);assertTrue(root.footprint().contains(obstacle));assertFalse(root.occupied().contains(obstacle));
        grid.states.put(obstacle,Blocks.STONE.getDefaultState());when(grid.world.isBlockPowered(root.getPos())).thenReturn(true);
        for(int i=0;i<650;i++)root.update();assertEquals(1,root.progress,0);assertEquals(Blocks.STONE,grid.states.get(obstacle).getBlock());
        assertSame(root,grid.tiles.get(root.getPos()));verify(grid.world,never()).destroyBlock(root.getPos(),true);
    }
    @Test public void closedPanelHasNoWalkableSurfaceHoles() {
        for(double x=-1.8;x<=1.8;x+=.3)for(double y=.3;y<=2.5;y+=.3) {
            AxisAlignedBB player=new AxisAlignedBB(x+.5-.29,64+y,.2,x+.5+.29,64+y+1.7,.8);
            assertTrue(root.collisionBoxes().stream().anyMatch(b->b.intersects(player)));
        }
        assertNotNull(root.rayTrace(root.getPos().up(),new Vec3d(.5,65.5,-2),new Vec3d(.5,65.5,2)));
    }
    @Test public void releasedPartCallbacksCannotBreakTheWholeController() {
        root.updatingParts=true;
        BlockPos part=root.occupied().stream().filter(p->!p.equals(root.getPos())).findFirst().get();
        OriginalGates.PART.breakBlock(grid.world,part,OriginalGates.PART.getDefaultState());
        verify(grid.world,never()).destroyBlock(root.getPos(),true);
    }
    @Test public void failedNewProxyInstallationRollsBackWithoutReleasingOldCells() {
        open();Set<BlockPos> before=new HashSet<>(grid.states.keySet());List<BlockPos> added=root.profile.occupied(root.getPos(),root.facing,0);added.removeAll(before);
        assertTrue(added.size()>2);when(grid.world.setBlockState(eq(added.get(2)),eq(OriginalGates.PART.getDefaultState()),anyInt())).thenReturn(false);
        assertFalse(root.fitParts(0));assertEquals(before,grid.states.keySet());assertEquals(1,root.progress,0);
    }
    @Test public void legacyFullPrismMigratesWithoutRemovingPlayerBlocks() {
        BlockPos future=root.getPos().up(5);BlockPos foreign=root.getPos().east(2).up(5);
        for(BlockPos p:root.footprint())if(!root.occupied().contains(p)) {
            OriginalGatePartTile part=new OriginalGatePartTile();part.configure(root.getPos(),root.owner);part.setPos(p);part.setWorld(grid.world);
            grid.tiles.put(p,part);grid.states.put(p,OriginalGates.PART.getDefaultState());
        }
        grid.tiles.remove(foreign);grid.states.put(foreign,Blocks.STONE.getDefaultState());
        NBTTagCompound old=root.writeToNBT(new NBTTagCompound());old.removeTag("LayoutVersion");root.readFromNBT(old);root.update();
        assertTrue(grid.world.isAirBlock(future));assertEquals(Blocks.STONE,grid.states.get(foreign).getBlock());assertEquals(5,root.writeToNBT(new NBTTagCompound()).getInteger("LayoutVersion"));
        verify(grid.world,never()).destroyBlock(root.getPos(),true);
    }
    @Test public void unloadedDestinationPausesWithoutCreatingOrDeletingBlocks() {
        open();BlockPos future=root.profile.occupied(root.getPos(),root.facing,0).stream().filter(p->!root.occupied().contains(p)).findFirst().get();
        when(grid.world.isBlockLoaded(future)).thenReturn(false);Set<BlockPos> before=new HashSet<>(grid.states.keySet());
        assertFalse(root.fitParts(0));assertEquals(before,grid.states.keySet());
    }
    @Test public void eachLeverFollowsClickedWallFaceNotPlayerYaw() {
        for(OriginalGateProfile profile:OriginalGateProfile.all())if(profile.is("Lever"))for(EnumFacing face:EnumFacing.Plane.HORIZONTAL) {
            BlockPos wall=new BlockPos(20,64,20);grid.states.put(wall,Blocks.STONE.getDefaultState());
            OriginalGateItem item=new OriginalGateItem(new OriginalGateBlock(profile));ItemStack stack=new ItemStack(item);
            EntityPlayer player=mock(EntityPlayer.class);player.capabilities=new PlayerCapabilities();player.capabilities.isCreativeMode=true;
            when(player.getHeldItem(EnumHand.MAIN_HAND)).thenReturn(stack);when(player.getHorizontalFacing()).thenReturn(EnumFacing.SOUTH);
            when(player.canPlayerEdit(any(BlockPos.class),any(EnumFacing.class),any(ItemStack.class))).thenReturn(true);
            when(grid.world.isBlockModifiable(eq(player),any(BlockPos.class))).thenReturn(true);when(grid.world.checkNoEntityCollision(any(AxisAlignedBB.class))).thenReturn(true);
            assertEquals(profile.id+" "+face,EnumActionResult.SUCCESS,item.onItemUse(player,grid.world,wall,EnumHand.MAIN_HAND,face,.5F,.5F,.5F));
            OriginalGateTile lever=(OriginalGateTile)grid.tiles.get(wall.offset(face));assertEquals(face,lever.facing);
            for(float t:new float[]{0,.5F,1})for(AxisAlignedBB b:profile.boxes(lever.getPos(),face,t,false))assertFalse(b.intersects(new AxisAlignedBB(wall)));
            // Reset only the switches installed by this iteration.
            for(BlockPos p:new ArrayList<>(grid.states.keySet()))if(!p.equals(root.getPos()) && p.getX()>10){grid.states.remove(p);grid.tiles.remove(p);}
        }
    }
    @Test public void switchesRejectFloorCeilingAndAirInsteadOfFloatingAtCellCenter() {
        OriginalGateProfile p=OriginalGateProfile.all().stream().filter(v->v.is("Lever")).findFirst().get();OriginalGateItem item=new OriginalGateItem(new OriginalGateBlock(p));
        EntityPlayer player=mock(EntityPlayer.class);when(player.getHorizontalFacing()).thenReturn(EnumFacing.SOUTH);
        for(EnumFacing face:new EnumFacing[]{EnumFacing.UP,EnumFacing.DOWN,EnumFacing.NORTH})
            assertEquals(EnumActionResult.FAIL,item.onItemUse(player,grid.world,new BlockPos(20,64,20),EnumHand.MAIN_HAND,face,0,0,0));
    }
    @Test public void onlySixLeversArePublishedAndOnlyThreeNon01LeversAreFlipped(){
        int published=0,flipped=0;
        for(OriginalGateProfile p:OriginalGateProfile.all()){
            assertEquals(p.is("Lever"),DecorAvailability.published(p));
            assertEquals(p.is("Lever")&&!p.source.endsWith("_01"),p.flipped());
            if(DecorAvailability.published(p))published++;
            if(p.flipped())flipped++;
            OriginalGateBlock block=new OriginalGateBlock(p);
            assertEquals(p.is("Lever")?ru.metrocraft.port.MetroPort.DECOR:null,block.getCreativeTab());
            assertEquals(!p.is("Lever"),DecorAvailability.blocked(new ItemStack(new OriginalGateItem(block))));
        }
        assertEquals(6,published);assertEquals(3,flipped);
    }
    @Test public void flippedLeverVerticesUseSameYawAndWallAnchorAsRenderer(){
        for(OriginalGateProfile p:OriginalGateProfile.all())if(p.flipped()){
            OriginalGateProfile.Part part=p.parts[0];double[] authored=part.boxes[0];
            // At zero progress these switch frames are identity. Box order is preserved.
            AxisAlignedBB actual=p.boxes(BlockPos.ORIGIN,EnumFacing.SOUTH,0,false).get(0);
            assertEquals(.5-authored[3]+p.offset[0],actual.minX,1e-5);
            assertEquals(.5-authored[0]+p.offset[0],actual.maxX,1e-5);
            assertTrue(actual.minZ>=.002-1e-6);
        }
    }
}
