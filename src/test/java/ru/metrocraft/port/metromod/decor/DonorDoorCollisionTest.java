package ru.metrocraft.port.metromod.decor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Engine-side contract tests, not a claim of live Minecraft gameplay validation. */
public class DonorDoorCollisionTest {
    @BeforeClass public static void boot(){Bootstrap.register();OriginalGates.initialize();}
    private OriginalGateTile root;
    private OriginalDecorTestWorld grid;
    @Before public void setup(){
        OriginalGateProfile p=OriginalGateProfile.get("metromod_door_door_door_hran");
        assertNotNull(p);
        root=new OriginalGateTile();root.setPos(new BlockPos(0,64,0));root.configure(UUID.randomUUID(),p,EnumFacing.SOUTH);
        root.setWorld(mock(World.class));root.ready=true;grid=new OriginalDecorTestWorld(root);
        when(grid.world.isBlockModifiable(any(EntityPlayer.class),any(BlockPos.class))).thenReturn(true);
    }
    private EntityPlayer player(){
        EntityPlayer p=mock(EntityPlayer.class);when(p.getHeldItemMainhand()).thenReturn(ItemStack.EMPTY);
        when(p.canPlayerEdit(any(BlockPos.class),any(EnumFacing.class),any(ItemStack.class))).thenReturn(true);return p;
    }
    @Test public void coverageIsExactly27DoorsAndDoesNotMislabelGermo(){
        int count=0;for(OriginalGateProfile p:OriginalGateProfile.all()){
            assertEquals(p.id,p.is("Door"),p.donorDoor!=null);if(p.donorDoor!=null)count++;
        }assertEquals(27,count);
    }
    @Test public void providerAddsHalfEpsilonToBothEndsInsteadOfVoxelSnapping(){
        double[] result=DonorDoorCollision.providerBox(new double[]{1,2,3,1.004,4,3});
        assertEquals(1-(double)(.01F/2),result[0],1e-12);
        assertEquals(1.004+(double)(.01F/2),result[3],1e-12);
        assertEquals(.014,result[3]-result[0],1e-8);assertEquals(2,result[1],0);assertEquals(4,result[4],0);
    }
    @Test public void closedPhysicsDoesNotFollowAnimXOrSurfaceVoxels(){
        AxisAlignedBB expected=root.doorBounds();
        for(float progress:new float[]{0,.01F,.5F,1}){
            root.progress=progress;assertEquals(Collections.singletonList(expected),root.collisionBoxes());
        }
        assertEquals(1,root.collisionBoxes().size());
    }
    @Test public void activationDisablesCollisionImmediatelyNotAtLastAnimationFrame(){
        assertTrue(root.activate(player()));assertEquals(0,root.progress,0);assertTrue(root.collisionBoxes().isEmpty());
        assertTrue(root.activate(player()));assertEquals(1,root.collisionBoxes().size());
    }
    @Test public void poweredDoorUsesActivationRatherThanAnimationProgress(){
        when(grid.world.isBlockPowered(root.getPos())).thenReturn(true);root.update();
        assertTrue(root.progress<1);assertTrue(root.collisionBoxes().isEmpty());
        root.progress=1;when(grid.world.isBlockPowered(root.getPos())).thenReturn(false);root.update();
        assertTrue(root.progress>0);assertFalse(root.collisionBoxes().isEmpty());
    }
    @Test public void selectionDoesNotDisappearWithPhysicalBounds(){
        AxisAlignedBB bounds=root.doorBounds();BlockPos cell=root.occupied().stream().filter(p->!p.equals(root.getPos())).findFirst().get();
        assertTrue(root.activate(player()));assertTrue(OriginalGates.PART.isReplaceable(grid.world,cell));
        Vec3d start=new Vec3d((bounds.minX+bounds.maxX)/2,(bounds.minY+bounds.maxY)/2,bounds.minZ-2);
        Vec3d end=new Vec3d(start.x,start.y,bounds.maxZ+2);
        BlockPos center=new BlockPos(start.x,start.y,(bounds.minZ+bounds.maxZ)/2);
        assertNotNull(root.rayTrace(center,start,end));assertTrue(root.collisionBoxes().isEmpty());
    }
    @Test public void buildingInOpenDoorDoesNotDeleteControllerAndPreventsClosing(){
        assertTrue(root.activate(player()));
        BlockPos cell=root.occupied().stream().filter(p->!p.equals(root.getPos())).findFirst().get();
        assertTrue(OriginalGates.PART.isReplaceable(grid.world,cell));
        // Exercise vanilla World's placement gate, not only our replaceable getter.
        doCallRealMethod().when(grid.world).mayPlace(eq(Blocks.STONE),eq(cell),eq(false),eq(EnumFacing.UP),isNull());
        when(grid.world.checkNoEntityCollision(any(AxisAlignedBB.class),isNull())).thenReturn(true);
        assertTrue(grid.world.mayPlace(Blocks.STONE,cell,false,EnumFacing.UP,null));
        OriginalGates.PART.breakBlock(grid.world,cell,OriginalGates.PART.getDefaultState());
        grid.states.put(cell,Blocks.STONE.getDefaultState());grid.tiles.remove(cell);
        assertFalse(root.activate(player()));assertTrue(root.enabled());
        for(int i=0;i<70;i++)root.update();
        assertEquals(Blocks.STONE,grid.world.getBlockState(cell).getBlock());
        assertSame(root,grid.tiles.get(root.getPos()));verify(grid.world,never()).destroyBlock(root.getPos(),true);
    }
    @Test public void migrationRemovesOldSwingPocketProxiesButNeverForeignStone(){
        BlockPos old=root.footprint().stream().filter(p->!root.occupied().contains(p)).findFirst().get();
        OriginalGatePartTile part=new OriginalGatePartTile();part.setWorld(grid.world);part.setPos(old);part.configure(root.getPos(),root.owner);
        grid.states.put(old,OriginalGates.PART.getDefaultState());grid.tiles.put(old,part);
        BlockPos foreign=old.up();grid.states.put(foreign,Blocks.STONE.getDefaultState());
        NBTTagCompound tag=root.writeToNBT(new NBTTagCompound());tag.setInteger("LayoutVersion",2);
        root.readFromNBT(tag);root.update();
        assertTrue(grid.world.isAirBlock(old));assertEquals(Blocks.STONE,grid.world.getBlockState(foreign).getBlock());
        assertEquals(3,root.writeToNBT(new NBTTagCompound()).getInteger("LayoutVersion"));
    }
    @Test public void customAabbKeysRoundTripAndOverrideRatherThanExpand(){
        NBTTagCompound tag=root.writeToNBT(new NBTTagCompound());
        String[] keys={"aabb_minx","aabb_miny","aabb_minz","aabb_maxx","aabb_maxy","aabb_maxz"};
        double[] custom={-.2,0,-.03,.2,2,.03};for(int i=0;i<6;i++)tag.setDouble(keys[i],custom[i]);
        root.readFromNBT(tag);
        AxisAlignedBB b=root.doorBounds();assertEquals(.4,b.maxX-b.minX,1e-9);assertEquals(2,b.maxY-b.minY,1e-9);assertEquals(.06,b.maxZ-b.minZ,1e-9);
        NBTTagCompound saved=root.writeToNBT(new NBTTagCompound());for(int i=0;i<6;i++)assertEquals(custom[i],saved.getDouble(keys[i]),0);
    }
    @Test public void malformedOrPartialCustomBoundsFallBackToReviewedDefault(){
        AxisAlignedBB expected=root.doorBounds();NBTTagCompound tag=root.writeToNBT(new NBTTagCompound());
        tag.setDouble("aabb_minx",Double.NaN);root.readFromNBT(tag);assertEquals(expected,root.doorBounds());
        for(String key:new String[]{"aabb_miny","aabb_minz","aabb_maxx","aabb_maxy","aabb_maxz"})tag.setDouble(key,0);
        root.readFromNBT(tag);assertEquals(expected,root.doorBounds());
    }
    @Test public void renderSweepCannotInventPhysicalObstructionForDonorDoors(){
        assertFalse(root.blockedBetween(0,1));
        verify(grid.world,never()).getEntitiesWithinAABBExcludingEntity(isNull(),any(AxisAlignedBB.class));
    }
}
