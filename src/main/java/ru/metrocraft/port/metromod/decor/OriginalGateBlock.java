package ru.metrocraft.port.metromod.decor;

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

public class OriginalGateBlock extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public final OriginalGateProfile profile;
    public OriginalGateBlock(OriginalGateProfile profile) {
        super(Material.IRON); this.profile = profile;
        setRegistryName(MetroPort.ID, profile.id); setTranslationKey(MetroPort.ID + "." + profile.id);
        setLightOpacity(0); setCreativeTab(DecorAvailability.published(profile)?MetroPort.DECOR:null); setHardness(5); setResistance(20); setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));
    }
    public static EnumFacing horizontal(int index) {
        switch (index & 3) { case 1: return EnumFacing.WEST; case 2: return EnumFacing.NORTH;
            case 3: return EnumFacing.EAST; default: return EnumFacing.SOUTH; }
    }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, FACING); }
    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING, horizontal(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex(); }
    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new OriginalGateTile(); }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public boolean isPassable(IBlockAccess world, BlockPos pos) { return world instanceof World?OriginalDecorCollisions.localCollision((World)world,pos)==null:false; }
    @Override public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.CUTOUT; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.ENTITYBLOCK_ANIMATED; }
    @Override @Nullable public AxisAlignedBB getCollisionBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos) {
        return world instanceof World?OriginalDecorCollisions.localCollision((World)world,pos):NULL_AABB;
    }
    @Override public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB query,
                                                List<AxisAlignedBB> list, @Nullable Entity entity, boolean actual) {
        OriginalGateTile tile = OriginalGates.controller(world, pos);
        if (tile != null) tile.addCollision(pos, query, list);
    }
    @Override @Nullable public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        OriginalGateTile tile = OriginalGates.controller(world, pos);
        return tile == null ? null : tile.rayTrace(pos, start, end);
    }
    @Override public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                              EnumHand hand, EnumFacing side, float x, float y, float z) {
        OriginalGateTile tile=OriginalGates.controller(world,pos);return tile!=null && tile.activate(player);
    }
    @Override public boolean canProvidePower(IBlockState state) { return profile.is("Lever"); }
    @Override public int getWeakPower(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side) {
        TileEntity tile=world.getTileEntity(pos);return tile instanceof OriginalGateTile?((OriginalGateTile)tile).output():0;
    }
    @Override public int getStrongPower(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side) { return getWeakPower(state,world,pos,side); }
    @Override public int getLightValue(IBlockState state,IBlockAccess world,BlockPos pos) {
        TileEntity tile=world.getTileEntity(pos);return profile.is("Light") && tile instanceof OriginalGateTile && ((OriginalGateTile)tile).enabled()?15:0;
    }
    @Override public void breakBlock(World world, BlockPos pos, IBlockState state) {
        OriginalGateTile tile = OriginalGates.controller(world, pos);
        if (!world.isRemote && !world.restoringBlockSnapshots && tile != null) {
            tile.removeParts();
            if(profile.is("Lever"))world.notifyNeighborsOfStateChange(pos,this,false);
        }
        super.breakBlock(world, pos, state);
    }
    @Override public net.minecraft.block.material.EnumPushReaction getPushReaction(IBlockState state) { return net.minecraft.block.material.EnumPushReaction.BLOCK; }
    @Override public ItemStack getItem(World world, BlockPos pos, IBlockState state) { return new ItemStack(this); }
}
