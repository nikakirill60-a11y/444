package ru.metrocraft.port.metromod.decor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import ru.metrocraft.port.MetroPort;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/** Invisible reserved cells; collision is clipped to the controller's actual moving panel/frame. */
public final class OriginalGatePartBlock extends Block {
    public OriginalGatePartBlock() { super(Material.IRON); setRegistryName(MetroPort.ID, "metromod_gate_part"); setHardness(5); setLightOpacity(0); }
    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new OriginalGatePartTile(); }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public boolean isPassable(IBlockAccess world, BlockPos pos) { return world instanceof World?OriginalDecorCollisions.localCollision((World)world,pos)==null:false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.INVISIBLE; }
    @Override public net.minecraft.block.material.EnumPushReaction getPushReaction(IBlockState state) { return net.minecraft.block.material.EnumPushReaction.BLOCK; }
    @Override public int quantityDropped(Random random) { return 0; }
    @Override public Item getItemDropped(IBlockState state, Random random, int fortune) { return net.minecraft.init.Items.AIR; }
    @Override public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        OriginalGateTile tile = OriginalGates.controller(world, pos);
        return tile == null ? ItemStack.EMPTY : new ItemStack(tile.getBlockType());
    }
    @Override @Nullable public AxisAlignedBB getCollisionBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos) {
        return world instanceof World?OriginalDecorCollisions.localCollision((World)world,pos):NULL_AABB;
    }
    @Override public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB query,
                                               List<AxisAlignedBB> list, @Nullable Entity entity, boolean actual) {
        OriginalGateTile tile = OriginalGates.controller(world, pos); if (tile != null) tile.addCollision(pos, query, list);
    }
    @Override @Nullable public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        OriginalGateTile tile = OriginalGates.controller(world, pos);
        return tile == null ? null : tile.rayTrace(pos, start, end);
    }
    @Override public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                              EnumHand hand, EnumFacing side, float x, float y, float z) {
        OriginalGateTile tile = OriginalGates.controller(world, pos);
        if (tile == null) return false;
        return tile.getBlockType().onBlockActivated(world, tile.getPos(), world.getBlockState(tile.getPos()), player, hand, side, x, y, z);
    }
    @Override public boolean canProvidePower(IBlockState state) { return true; }
    private OriginalGateTile root(IBlockAccess world,BlockPos pos) {
        TileEntity part=world.getTileEntity(pos);
        return part instanceof OriginalGatePartTile?((OriginalGatePartTile)part).controller():null;
    }
    @Override public int getWeakPower(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side) {
        OriginalGateTile tile=root(world,pos);return tile==null?0:tile.output();
    }
    @Override public int getStrongPower(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side) { return getWeakPower(state,world,pos,side); }
    @Override public int getLightValue(IBlockState state,IBlockAccess world,BlockPos pos) {
        OriginalGateTile tile=root(world,pos);return tile!=null && tile.profile.is("Light") && tile.enabled()?15:0;
    }
    @Override public boolean isReplaceable(IBlockAccess world,BlockPos pos) {
        if(!(world instanceof World))return false;
        OriginalGateTile root=OriginalGates.controller((World)world,pos);
        return root!=null && !root.occupied().contains(pos);
    }
    @Override public void breakBlock(World world, BlockPos pos, IBlockState state) {
        OriginalGateTile tile = OriginalGates.controller(world, pos);
        // Only the controller drops an item. Parts never multiply gate drops.
        if (!world.isRemote && !world.restoringBlockSnapshots && tile != null && tile.ready && !tile.removing && !tile.updatingParts && tile.occupied().contains(pos)) world.destroyBlock(tile.getPos(), true);
        super.breakBlock(world, pos, state);
    }
}
