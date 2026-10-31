package ru.metrocraft.port.exodus;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Rotatable non-full furniture. Models may protrude; placement never edits neighbouring blocks. */
public final class ExodusDecorBlock extends Block {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private final AxisAlignedBB bounds;
    public ExodusDecorBlock(ExodusContent.Entry entry) {
        super(Material.WOOD);
        double[] b = entry.bounds;
        bounds = new AxisAlignedBB(b[0], b[1], b[2], b[3], b[4], b[5]);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setHardness(2); setResistance(5); setLightOpacity(0);
    }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, FACING); }
    @Override public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, ru.metrocraft.port.gates.GateBlock.horizontal(meta));
    }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex(); }
    @Override public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float x,
            float y, float z, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
    @Override public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }
    @Override public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return withRotation(state, mirror.toRotation(state.getValue(FACING)));
    }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public boolean isPassable(IBlockAccess world, BlockPos pos) { return false; }
    @Override public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.CUTOUT; }
    @Override public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case EAST: return new AxisAlignedBB(1-bounds.maxZ,bounds.minY,bounds.minX,1-bounds.minZ,bounds.maxY,bounds.maxX);
            case SOUTH: return new AxisAlignedBB(1-bounds.maxX,bounds.minY,1-bounds.maxZ,1-bounds.minX,bounds.maxY,1-bounds.minZ);
            case WEST: return new AxisAlignedBB(bounds.minZ,bounds.minY,1-bounds.maxX,bounds.maxZ,bounds.maxY,1-bounds.minX);
            default: return bounds;
        }
    }
    @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        AxisAlignedBB b = getBoundingBox(state, world, pos);
        double x0=Math.max(0,b.minX), y0=Math.max(0,b.minY), z0=Math.max(0,b.minZ);
        double x1=Math.min(1,b.maxX), y1=Math.min(1,b.maxY), z1=Math.min(1,b.maxZ);
        return x0<x1 && y0<y1 && z0<z1 ? new AxisAlignedBB(x0,y0,z0,x1,y1,z1) : NULL_AABB;
    }
}
