package ru.metrocraft.port.metromod.decor;

import net.minecraft.init.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real shared adapters exercised against mock worlds; not an in-game/OpenGL test. */
public class OriginalInteractiveTest {
    @BeforeClass public static void boot() { Bootstrap.register();OriginalGates.initialize(); }
    private OriginalGateProfile profile(String kind) {
        for(OriginalGateProfile p:OriginalGateProfile.all())if(p.is(kind))return p;
        throw new AssertionError(kind);
    }
    private OriginalGateTile tile(String kind) {
        OriginalGateTile t=new OriginalGateTile();t.setPos(new BlockPos(0,64,0));t.configure(UUID.randomUUID(),profile(kind),EnumFacing.SOUTH);t.ready=true;
        World world=mock(World.class);t.setWorld(world);
        when(world.getBlockState(any(BlockPos.class))).thenReturn(Blocks.STONE.getDefaultState());
        when(world.isBlockLoaded(any(BlockPos.class))).thenReturn(true);
        when(world.isBlockModifiable(any(EntityPlayer.class),any(BlockPos.class))).thenReturn(true);
        when(world.getTileEntity(t.getPos())).thenReturn(t);
        when(world.getEntitiesWithinAABBExcludingEntity(isNull(),any(AxisAlignedBB.class))).thenReturn(Collections.emptyList());
        for(BlockPos cell:t.footprint())if(!cell.equals(t.getPos())) {
            OriginalGatePartTile part=new OriginalGatePartTile();part.configure(t.getPos(),t.owner);when(world.getTileEntity(cell)).thenReturn(part);
        }
        return t;
    }
    private EntityPlayer player() {
        EntityPlayer p=mock(EntityPlayer.class);when(p.getHeldItemMainhand()).thenReturn(ItemStack.EMPTY);
        when(p.canPlayerEdit(any(BlockPos.class),any(EnumFacing.class),any(ItemStack.class))).thenReturn(true);return p;
    }
    @Test public void exactCoverageAndNoDuplicateSavedIds() {
        Map<String,Integer> counts=new HashMap<>();Set<String> ids=new HashSet<>();
        for(OriginalGateProfile p:OriginalGateProfile.all()) {assertTrue(ids.add(p.id));counts.put(p.kind,counts.getOrDefault(p.kind,0)+1);}
        assertEquals(78,ids.size());assertEquals(Integer.valueOf(14),counts.get("Germo"));assertEquals(Integer.valueOf(27),counts.get("Door"));
        assertEquals(Integer.valueOf(6),counts.get("Lever"));assertEquals(Integer.valueOf(31),counts.get("Light"));assertNull(counts.get("Inventory"));
    }
    @Test public void allFootprintsBoundedInEveryFacing() {
        for(OriginalGateProfile p:OriginalGateProfile.all())for(EnumFacing f:EnumFacing.Plane.HORIZONTAL) {
            List<BlockPos> cells=p.footprint(new BlockPos(0,64,0),f);assertFalse(cells.isEmpty());assertTrue(cells.size()<=4096);
            assertEquals(cells.size(),new HashSet<>(cells).size());assertTrue(cells.contains(new BlockPos(0,64,0)));
        }
    }
    @Test public void sampledSwingIsReservedNotJustEndpoints() {
        OriginalGateProfile p=OriginalGateProfile.get("metromod_germo_3");assertNotNull(p);
        AxisAlignedBB bound=p.bounds(BlockPos.ORIGIN,EnumFacing.SOUTH).grow(1e-7);
        for(int i=0;i<=128;i++)for(AxisAlignedBB b:p.boxes(BlockPos.ORIGIN,EnumFacing.SOUTH,i/128F,false)) {
            assertTrue(bound.contains(new Vec3d(b.minX,b.minY,b.minZ)));assertTrue(bound.contains(new Vec3d(b.maxX,b.maxY,b.maxZ)));
        }
    }
    @Test public void matrixInterpolationMatchesBothSidesContract() {
        OriginalGateProfile.Part p=OriginalGateProfile.get("metromod_germo_3").parts[0];
        assertEquals(65,p.frames.length);assertArrayEquals(p.frames[0],p.matrix(0),1e-8);assertArrayEquals(p.frames[64],p.matrix(1),1e-8);
        double[] m=p.matrix(.5F/64);for(int i=0;i<16;i++)assertEquals((p.frames[0][i]+p.frames[1][i])/2,m[i],1e-8);
    }
    @Test public void leverTogglesOutputAndPersists() {
        OriginalGateTile t=tile("Lever");assertEquals(0,t.output());assertTrue(t.activate(player()));assertEquals(15,t.output());
        for(int i=0;i<70;i++)t.update();assertEquals(1,t.progress,0);
        OriginalGateTile loaded=new OriginalGateTile();loaded.readFromNBT(t.writeToNBT(new NBTTagCompound()));assertEquals(15,loaded.output());
        assertTrue(t.activate(player()));assertEquals(0,t.output());verify(t.getWorld(),atLeastOnce()).notifyNeighborsOfStateChange(any(BlockPos.class),any(net.minecraft.block.Block.class),eq(false));
    }
    @Test public void poweredLeverDoesNotFeedItsOwnInput() {
        OriginalGateTile t=tile("Lever");when(t.getWorld().isBlockPowered(any(BlockPos.class))).thenReturn(true);
        t.update();assertEquals(0,t.output());assertEquals(0,t.progress,0);
    }
    @Test public void lightToggleAndRedstoneUpdateRealLightEngine() {
        OriginalGateTile t=tile("Light");assertFalse(t.enabled());assertTrue(t.activate(player()));assertTrue(t.enabled());
        verify(t.getWorld(),atLeastOnce()).checkLight(t.getPos());assertTrue(t.activate(player()));assertFalse(t.enabled());
        when(t.getWorld().isBlockPowered(t.getPos())).thenReturn(true);t.update();assertTrue(t.enabled());
        when(t.getWorld().isBlockPowered(t.getPos())).thenReturn(false);t.update();assertFalse(t.enabled());
    }
    @Test public void doorManualAndRedstoneUseServerMotion() {
        OriginalGateTile t=tile("Door");assertTrue(t.activate(player()));for(int i=0;i<70;i++)t.update();assertEquals(1,t.progress,0);
        assertTrue(t.activate(player()));for(int i=0;i<70;i++)t.update();assertEquals(0,t.progress,0);
        when(t.getWorld().isBlockPowered(t.getPos())).thenReturn(true);t.update();assertTrue(t.progress>0);
    }
    @Test public void originalGatesStillRejectManualActivation() {
        OriginalGateTile t=tile("Germo");assertFalse(t.activate(player()));assertFalse(t.enabled());
    }
    @Test public void forbiddenActivationCannotChangeState() {
        OriginalGateTile t=tile("Lever");EntityPlayer p=player();when(p.isSpectator()).thenReturn(true);assertFalse(t.activate(p));assertEquals(0,t.output());
        p=player();when(t.getWorld().isBlockModifiable(p,t.getPos())).thenReturn(false);assertFalse(t.activate(p));assertEquals(0,t.output());
    }
    @Test public void tickSweepIncludesInternalPoseExtremum() {
        OriginalGateTile t=tile("Door");OriginalGateProfile p=new OriginalGateProfile();p.ground=0;
        OriginalGateProfile.Part part=new OriginalGateProfile.Part();part.move=new double[]{0,0,0};part.moving=true;
        part.boxes=new double[][]{{0,0,0,.1,1,.1}};part.frames=new double[65][];
        for(int i=0;i<65;i++)part.frames[i]=new double[]{1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
        part.frames[32][3]=1;p.parts=new OriginalGateProfile.Part[]{part};t.profile=p;
        net.minecraft.entity.EntityLivingBase entity=mock(net.minecraft.entity.EntityLivingBase.class);
        when(entity.isEntityAlive()).thenReturn(true);when(entity.getEntityBoundingBox()).thenReturn(new AxisAlignedBB(1.53,64,.52,1.56,65,.55));
        when(t.getWorld().getEntitiesWithinAABBExcludingEntity(isNull(),any(AxisAlignedBB.class))).thenReturn(Collections.singletonList(entity));
        assertTrue(t.blockedBetween(31.8F/64,32.2F/64));
    }

}
