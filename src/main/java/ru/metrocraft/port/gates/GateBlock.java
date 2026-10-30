package ru.metrocraft.port.gates;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import ru.metrocraft.port.MetroPort;
import javax.annotation.Nullable;
import java.util.List;

public class GateBlock extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public final GateType type;
    public GateBlock(GateType type) {
        super(Material.IRON); this.type = type;
        setRegistryName(MetroPort.ID, type.id); setTranslationKey(MetroPort.ID + "." + type.id);
        setLightOpacity(0); setCreativeTab(MetroPort.THINGS); setHardness(5); setResistance(20); setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));
    }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, FACING); }
    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex(); }
    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new GateTile(); }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public boolean isPassable(IBlockAccess world, BlockPos pos) { return true; }
    @Override public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.CUTOUT; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.ENTITYBLOCK_ANIMATED; }
    @Override public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB query,
                                                List<AxisAlignedBB> list, @Nullable Entity entity, boolean actual) {
        GateTile tile = Gates.controller(world, pos);
        if (tile != null) tile.addCollision(pos, query, list);
    }
    @Override @Nullable public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        GateTile tile = Gates.controller(world, pos);
        return tile == null ? null : tile.rayTrace(pos, start, end);
    }
    @Override public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                              EnumHand hand, EnumFacing side, float x, float y, float z) {
        GateTile tile = Gates.controller(world, pos);
        if (tile != null && player.canPlayerEdit(pos, side, player.getHeldItem(hand))) {
            if (!world.isRemote) tile.toggle();
            return true;
        }
        return false;
    }
    @Override public void breakBlock(World world, BlockPos pos, IBlockState state) {
        GateTile tile = Gates.controller(world, pos);
        if (!world.isRemote && tile != null) tile.removeParts();
        super.breakBlock(world, pos, state);
    }
    @Override public ItemStack getItem(World world, BlockPos pos, IBlockState state) { return new ItemStack(this); }
}
