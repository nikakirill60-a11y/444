package ru.metrocraft.port.mcmod.decor;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public final class McmodDecorBlock extends Block {
    public static final PropertyDirection FACING=BlockHorizontal.FACING;
    public final McmodDecorContent.Entry entry;
    public McmodDecorBlock(McmodDecorContent.Entry entry) {
        super(Material.IRON);this.entry=entry;
        setDefaultState(blockState.getBaseState().withProperty(FACING,EnumFacing.SOUTH));
        setBlockUnbreakable();setResistance(6000000);setLightOpacity(0);
    }
    @Override protected BlockStateContainer createBlockState() {return new BlockStateContainer(this,FACING);}
    @Override public IBlockState getStateFromMeta(int meta) {return getDefaultState().withProperty(FACING,ru.metrocraft.port.gates.GateBlock.horizontal(meta));}
    @Override public int getMetaFromState(IBlockState state) {return state.getValue(FACING).getHorizontalIndex();}
    @Override public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing face,float x,float y,float z,int meta,EntityLivingBase placer) {return getDefaultState().withProperty(FACING,placer.getHorizontalFacing().getOpposite());}
    @Override public IBlockState withRotation(IBlockState state,Rotation rotation) {return state.withProperty(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override public IBlockState withMirror(IBlockState state,Mirror mirror) {return withRotation(state,mirror.toRotation(state.getValue(FACING)));}
    @Override public boolean hasTileEntity(IBlockState state) {return true;}
    @Override public TileEntity createTileEntity(World world,IBlockState state) {return new McmodDecorTile();}
    @Override public EnumBlockRenderType getRenderType(IBlockState state) {return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;}
    @Override public net.minecraft.block.state.BlockFaceShape getBlockFaceShape(IBlockAccess world,IBlockState state,BlockPos pos,EnumFacing face) {return net.minecraft.block.state.BlockFaceShape.UNDEFINED;}
    @Override public boolean isOpaqueCube(IBlockState state) {return false;}
    @Override public boolean isFullCube(IBlockState state) {return false;}
    @Override public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos) {return DecorPose.bounds(entry,getMetaFromState(state));}
    @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos) {return DecorPose.collision(entry,getMetaFromState(state));}
}
