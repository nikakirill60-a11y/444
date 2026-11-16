package ru.metrocraft.port.metromod.decor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import java.util.*;
import static org.mockito.Mockito.*;

/** Mutable proxy-block world: movement must actually add/release cells in tests. */
final class OriginalDecorTestWorld {
    /** Explicit legacy migration fixture. Setting cosmetic progress alone must
     * never be able to replace a current donor collision snapshot. */
    static void pose(OriginalGateTile root,float progress) {
        net.minecraft.nbt.NBTTagCompound tag=root.writeToNBT(new net.minecraft.nbt.NBTTagCompound());
        tag.removeTag("DonorGermo");tag.setInteger("LayoutVersion",0);tag.setFloat("Progress",progress);root.readFromNBT(tag);
    }
    final Map<BlockPos,TileEntity> tiles=new HashMap<>();
    final Map<BlockPos,IBlockState> states=new HashMap<>();
    final World world;
    OriginalDecorTestWorld(OriginalGateTile root) {
        world=root.getWorld();tiles.put(root.getPos(),root);
        states.put(root.getPos(),new OriginalGateBlock(root.profile).getDefaultState());
        for(BlockPos cell:root.occupied())if(!cell.equals(root.getPos())) {
            OriginalGatePartTile part=new OriginalGatePartTile();part.configure(root.getPos(),root.owner);part.setPos(cell);part.setWorld(world);
            tiles.put(cell,part);states.put(cell,OriginalGates.PART.getDefaultState());
        }
        when(world.getHeight()).thenReturn(256);when(world.getWorldBorder()).thenReturn(new WorldBorder());
        when(world.isBlockLoaded(any(BlockPos.class))).thenReturn(true);
        when(world.getTileEntity(any(BlockPos.class))).thenAnswer(i->tiles.get(i.getArgument(0)));
        when(world.getBlockState(any(BlockPos.class))).thenAnswer(i->states.getOrDefault(i.getArgument(0),Blocks.AIR.getDefaultState()));
        when(world.isAirBlock(any(BlockPos.class))).thenAnswer(i->!states.containsKey(i.getArgument(0)));
        when(world.getEntitiesWithinAABBExcludingEntity(isNull(),any(AxisAlignedBB.class))).thenReturn(Collections.emptyList());
        when(world.setBlockState(any(BlockPos.class),any(IBlockState.class),anyInt())).thenAnswer(i->{
            BlockPos p=((BlockPos)i.getArgument(0)).toImmutable();IBlockState state=i.getArgument(1);states.put(p,state);
            TileEntity te=state.getBlock().createTileEntity(world,state);
            if(te!=null){te.setWorld(world);te.setPos(p);tiles.put(p,te);}else tiles.remove(p);return true;
        });
        when(world.setBlockToAir(any(BlockPos.class))).thenAnswer(i->{states.remove(i.getArgument(0));tiles.remove(i.getArgument(0));return true;});
    }
}
