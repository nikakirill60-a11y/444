package ru.metrocraft.port.metromod.decor;

import net.minecraft.entity.*;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Executes real motion, profiles, collision and save code; not an OpenGL/gameplay test. */
public class OriginalGateTest {
    @BeforeClass public static void boot() { Bootstrap.register(); OriginalGates.initialize(); }
    private OriginalGateTile gate(OriginalGateProfile profile,EnumFacing facing) {
        World world=mock(World.class);OriginalGateTile tile=new OriginalGateTile();tile.setPos(new BlockPos(0,64,0));
        tile.configure(UUID.randomUUID(),profile,facing);tile.ready=true;tile.setWorld(world);
        when(world.getBlockState(any(BlockPos.class))).thenReturn(net.minecraft.init.Blocks.STONE.getDefaultState());
        when(world.isBlockLoaded(any(BlockPos.class))).thenReturn(true);
        when(world.getEntitiesWithinAABBExcludingEntity(isNull(),any(AxisAlignedBB.class))).thenReturn(Collections.emptyList());
        for(BlockPos cell:tile.footprint())if(!cell.equals(tile.getPos())) {
            OriginalGatePartTile part=new OriginalGatePartTile();part.configure(tile.getPos(),tile.owner);when(world.getTileEntity(cell)).thenReturn(part);
        }
        return tile;
    }
    @Test public void sixOriginalProfilesNotTheOldGateIds() {
        assertEquals(6,OriginalGateProfile.all().size());
        for(OriginalGateProfile p:OriginalGateProfile.all()) {
            assertTrue(p.id.startsWith("metromod_germo_"));assertTrue(p.source.startsWith("germo/"));
            for(OriginalGateProfile.Part part:p.parts) {assertTrue(part.mesh.endsWith(".obj.gz"));assertFalse(part.materials.isEmpty());}
        }
    }
    @Test public void originalEndpointsForVerticalAndSplitLeaves() {
        OriginalGateProfile a=OriginalGateProfile.get("metromod_germo_1"),b=OriginalGateProfile.get("metromod_germo_2"),c=OriginalGateProfile.get("metromod_germo_9");
        assertEquals(3,a.parts[0].move[1],0);assertEquals(0,b.parts[0].move[1],0);assertEquals(3.76,b.parts[1].move[1],0);
        assertEquals(7.15,c.parts[0].move[0],0);assertEquals(-7.15,c.parts[1].move[0],0);
    }
    @Test public void redstoneOpensAndClosesAllSixInEveryFacing() {
        for(OriginalGateProfile p:OriginalGateProfile.all())for(EnumFacing f:EnumFacing.Plane.HORIZONTAL) {
            OriginalGateTile tile=gate(p,f);tile.update();assertEquals(0,tile.progress,0);
            when(tile.getWorld().isBlockPowered(tile.getPos())).thenReturn(true);
            for(int i=0;i<70;i++)tile.update();assertEquals(1,tile.progress,0);
            when(tile.getWorld().isBlockPowered(tile.getPos())).thenReturn(false);
            for(int i=0;i<70;i++)tile.update();assertEquals(0,tile.progress,0);
        }
    }
    @Test public void powerOnPartWorksButUnloadedPartPausesMotion() {
        OriginalGateTile tile=gate(OriginalGateProfile.get("metromod_germo_9"),EnumFacing.NORTH);
        BlockPos cell=tile.footprint().get(1);when(tile.getWorld().isBlockPowered(cell)).thenReturn(true);
        when(tile.getWorld().isBlockLoaded(cell)).thenReturn(false);tile.update();assertEquals(0,tile.progress,0);
        when(tile.getWorld().isBlockLoaded(cell)).thenReturn(true);tile.update();assertTrue(tile.progress>0);
    }
    @Test public void foreignOrWrongOriginPartDestroysOnlyController() {
        OriginalGateTile tile=gate(OriginalGateProfile.get("metromod_germo_4"),EnumFacing.SOUTH);
        BlockPos cell=tile.footprint().get(1);OriginalGatePartTile foreign=new OriginalGatePartTile();
        foreign.configure(tile.getPos().east(30),tile.owner);when(tile.getWorld().getTileEntity(cell)).thenReturn(foreign);
        tile.update();verify(tile.getWorld()).destroyBlock(tile.getPos(),true);assertEquals(0,tile.progress,0);
    }
    @Test public void livingEntitiesBlockSweptLeafButNotNoClipOrDeadEntities() {
        OriginalGateTile tile=gate(OriginalGateProfile.get("metromod_germo_5"),EnumFacing.SOUTH);
        tile.progress=.5F;when(tile.getWorld().isBlockPowered(tile.getPos())).thenReturn(true);
        EntityLivingBase mob=mock(EntityLivingBase.class);when(mob.isEntityAlive()).thenReturn(true);
        when(mob.getEntityBoundingBox()).thenReturn(tile.profile.bounds(tile.getPos(),tile.facing));
        when(tile.getWorld().getEntitiesWithinAABBExcludingEntity(isNull(),any(AxisAlignedBB.class))).thenReturn(Collections.<Entity>singletonList(mob));
        tile.update();assertEquals(.5F,tile.progress,0);mob.noClip=true;tile.update();assertTrue(tile.progress>.5F);
        mob.noClip=false;when(mob.isEntityAlive()).thenReturn(false);float before=tile.progress;tile.update();assertTrue(tile.progress>before);
    }
    @Test public void emptyReservedAirDoesNotBecomeAnInvisibleWall() {
        OriginalGateTile tile=gate(OriginalGateProfile.get("metromod_germo_5"),EnumFacing.SOUTH);tile.progress=1;
        List<AxisAlignedBB> boxes=new ArrayList<>();BlockPos middle=tile.getPos().up();
        tile.addCollision(middle,new AxisAlignedBB(middle),boxes);assertTrue(boxes.isEmpty());
        assertNull(tile.rayTrace(middle,new Vec3d(.5,65.5,-2),new Vec3d(.5,65.5,2)));
    }
    @Test public void footprintContainsEveryMovingBoxAtIntermediateStates() {
        for(OriginalGateProfile p:OriginalGateProfile.all())for(EnumFacing f:EnumFacing.Plane.HORIZONTAL) {
            BlockPos origin=new BlockPos(-15,64,15);Set<BlockPos> cells=new HashSet<>(p.footprint(origin,f));
            assertTrue(cells.contains(origin));assertTrue(cells.size()<4096);
            for(float t:new float[]{0,.25F,.5F,.75F,1})for(AxisAlignedBB b:p.boxes(origin,f,t,false)) {
                assertTrue(cells.contains(new BlockPos(b.minX+1e-6,b.minY+1e-6,b.minZ+1e-6)));
                assertTrue(cells.contains(new BlockPos(b.maxX-1e-6,b.maxY-1e-6,b.maxZ-1e-6)));
            }
        }
    }
    @Test public void collisionIsClippedToTheQueriedReservedCell() {
        OriginalGateTile tile=gate(OriginalGateProfile.get("metromod_germo_1"),EnumFacing.WEST);
        for(BlockPos cell:tile.footprint()) {
            List<AxisAlignedBB> boxes=new ArrayList<>();tile.addCollision(cell,new AxisAlignedBB(cell),boxes);
            for(AxisAlignedBB b:boxes)assertEquals(b,new AxisAlignedBB(cell).intersect(b));
        }
    }
    @Test public void saveAndNetworkTagsRetainProgressIdentityAndFacing() {
        OriginalGateTile tile=gate(OriginalGateProfile.get("metromod_germo_6"),EnumFacing.EAST);tile.progress=.42F;
        OriginalGateTile restored=new OriginalGateTile();restored.readFromNBT(tile.getUpdateTag());
        assertSame(tile.profile,restored.profile);assertEquals(tile.owner,restored.owner);assertEquals(tile.facing,restored.facing);
        assertEquals(.42F,restored.progress,0);assertTrue(restored.ready);
    }
    @Test public void invalidSavedDataCannotProduceNaNBoundsOrUnknownProfiles() {
        for(float bad:new float[]{Float.NaN,Float.POSITIVE_INFINITY,Float.NEGATIVE_INFINITY}) {
            NBTTagCompound tag=new NBTTagCompound();tag.setString("Profile","../../bad");tag.setFloat("Progress",bad);
            OriginalGateTile tile=new OriginalGateTile();tile.readFromNBT(tag);assertEquals(0,tile.progress,0);assertNotNull(tile.profile);
        }
    }
    @Test public void removalOnlyTouchesOwnedPartsWithMatchingOrigin() {
        OriginalGateTile tile=gate(OriginalGateProfile.get("metromod_germo_1"),EnumFacing.SOUTH);
        BlockPos foreignCell=tile.footprint().get(1);OriginalGatePartTile foreign=new OriginalGatePartTile();foreign.configure(tile.getPos().east(20),tile.owner);
        when(tile.getWorld().getTileEntity(foreignCell)).thenReturn(foreign);tile.removeParts();
        verify(tile.getWorld(),never()).setBlockToAir(foreignCell);assertTrue(tile.removing);
    }
    @Test public void clientPredictionCannotCheckOrDestroyServerStructures() throws Exception {
        OriginalGateTile tile=gate(OriginalGateProfile.get("metromod_germo_1"),EnumFacing.SOUTH);
        java.lang.reflect.Field remote=World.class.getDeclaredField("isRemote");remote.setAccessible(true);remote.setBoolean(tile.getWorld(),true);
        clearInvocations(tile.getWorld());tile.update();
        verify(tile.getWorld(),never()).getTileEntity(any(BlockPos.class));verify(tile.getWorld(),never()).destroyBlock(any(BlockPos.class),anyBoolean());
    }
    @Test public void controllerAndPartsArePistonImmovableAndPartsHaveNoDrop() {
        OriginalGateBlock block=new OriginalGateBlock(OriginalGateProfile.get("metromod_germo_1"));
        assertEquals(net.minecraft.block.material.EnumPushReaction.BLOCK,block.getPushReaction(block.getDefaultState()));
        OriginalGatePartBlock part=new OriginalGatePartBlock();assertEquals(0,part.quantityDropped(new Random()));
        assertEquals(net.minecraft.block.material.EnumPushReaction.BLOCK,part.getPushReaction(part.getDefaultState()));
    }
}
