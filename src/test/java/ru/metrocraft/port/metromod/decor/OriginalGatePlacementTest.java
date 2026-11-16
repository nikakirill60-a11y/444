package ru.metrocraft.port.metromod.decor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real ItemBlock transaction with a controlled world, including mid-install failure. */
public class OriginalGatePlacementTest {
    @BeforeClass public static void boot() { Bootstrap.register(); }
    private World world;
    private EntityPlayer player;
    private ItemStack stack;
    private OriginalGateItem item;
    private final Map<BlockPos,IBlockState> states=new HashMap<>();
    private final Map<BlockPos,TileEntity> tiles=new HashMap<>();
    private int writes,failAt;
    @Before public void setup() {
        world=mock(World.class);player=mock(EntityPlayer.class);player.capabilities=new PlayerCapabilities();
        OriginalGateBlock block=new OriginalGateBlock(OriginalGateProfile.get("metromod_germo_1"));
        item=new OriginalGateItem(block);stack=new ItemStack(item,1);
        when(player.getHeldItem(EnumHand.MAIN_HAND)).thenReturn(stack);
        when(player.getHorizontalFacing()).thenReturn(EnumFacing.SOUTH);
        when(player.canPlayerEdit(any(BlockPos.class),any(EnumFacing.class),any(ItemStack.class))).thenReturn(true);
        when(world.isBlockModifiable(eq(player),any(BlockPos.class))).thenReturn(true);
        when(world.isBlockLoaded(any(BlockPos.class))).thenReturn(true);
        when(world.getHeight()).thenReturn(256);when(world.getWorldBorder()).thenReturn(new WorldBorder());
        when(world.getBlockState(any(BlockPos.class))).thenAnswer(i->states.getOrDefault(i.getArgument(0),Blocks.AIR.getDefaultState()));
        when(world.isAirBlock(any(BlockPos.class))).thenAnswer(i->!states.containsKey(i.getArgument(0)));
        when(world.getTileEntity(any(BlockPos.class))).thenAnswer(i->tiles.get(i.getArgument(0)));
        when(world.checkNoEntityCollision(any(AxisAlignedBB.class))).thenReturn(true);
        when(world.getEntitiesWithinAABBExcludingEntity(isNull(),any(AxisAlignedBB.class))).thenReturn(Collections.emptyList());
        when(world.setBlockState(any(BlockPos.class),any(IBlockState.class),anyInt())).thenAnswer(i->{
            BlockPos pos=i.getArgument(0);IBlockState state=i.getArgument(1);++writes;
            if(failAt>0 && writes==failAt)return false;
            if(state.getBlock()==Blocks.AIR){states.remove(pos);tiles.remove(pos);return true;}
            states.put(pos,state);TileEntity tile=state.getBlock().createTileEntity(world,state);
            if(tile!=null){tile.setWorld(world);tile.setPos(pos);tiles.put(pos,tile);}return true;
        });
    }
    private EnumActionResult place() { return item.onItemUse(player,world,new BlockPos(0,64,0),EnumHand.MAIN_HAND,EnumFacing.UP,0,0,0); }
    @Test public void successfulInstallConsumesExactlyOneAndAssignsAllParts() {
        assertEquals(EnumActionResult.SUCCESS,place());assertTrue(stack.isEmpty());assertTrue(states.size()>1);
        OriginalGateTile root=(OriginalGateTile)tiles.get(new BlockPos(0,64,0));assertTrue(root.ready);
        for(TileEntity tile:tiles.values())if(tile instanceof OriginalGatePartTile) {
            OriginalGatePartTile part=(OriginalGatePartTile)tile;assertEquals(root.owner,part.owner);assertEquals(root.getPos(),part.origin);
        }
    }
    @Test public void obstructionAndEditDenialHappenBeforeAnyWriteOrConsumption() {
        when(world.isAirBlock(any(BlockPos.class))).thenReturn(false);
        assertEquals(EnumActionResult.FAIL,place());assertEquals(0,writes);assertEquals(1,stack.getCount());
        when(world.isAirBlock(any(BlockPos.class))).thenReturn(true);
        when(player.canPlayerEdit(any(BlockPos.class),any(EnumFacing.class),any(ItemStack.class))).thenReturn(false);
        assertEquals(EnumActionResult.FAIL,place());assertEquals(0,writes);assertEquals(1,stack.getCount());
    }
    @Test public void midInstallFailureRollsBackOnlyOurCellsAndDoesNotConsume() {
        failAt=3;assertEquals(EnumActionResult.FAIL,place());assertTrue(states.isEmpty());assertTrue(tiles.isEmpty());assertEquals(1,stack.getCount());
    }
    @Test public void occupiedGeometryRejectsPlacementBeforeWriting() {
        when(world.checkNoEntityCollision(any(AxisAlignedBB.class))).thenReturn(false);
        assertEquals(EnumActionResult.FAIL,place());assertEquals(0,writes);assertEquals(1,stack.getCount());
    }
    @Test public void creativePlacementDoesNotConsume() {
        player.capabilities.isCreativeMode=true;assertEquals(EnumActionResult.SUCCESS,place());assertEquals(1,stack.getCount());
    }
    @Test public void clientAndUnloadedChunkCannotInstallOrConsume() throws Exception {
        java.lang.reflect.Field remote=World.class.getDeclaredField("isRemote");remote.setAccessible(true);remote.setBoolean(world,true);
        assertEquals(EnumActionResult.SUCCESS,place());assertEquals(0,writes);assertEquals(1,stack.getCount());
        remote.setBoolean(world,false);when(world.isBlockLoaded(any(BlockPos.class))).thenReturn(false);
        assertEquals(EnumActionResult.FAIL,place());assertEquals(0,writes);assertEquals(1,stack.getCount());
    }
}
