package ru.metrocraft.port.blackrazid.environment;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
/** Internal native temporary light cells, never offered as player inventory items. */
public final class TransientLightBlock extends Block {
    public TransientLightBlock(float light){super(Material.AIR);setLightLevel(light);setLightOpacity(0);setBlockUnbreakable();}
    @Override public net.minecraft.block.state.BlockFaceShape getBlockFaceShape(IBlockAccess w,IBlockState s,BlockPos p,net.minecraft.util.EnumFacing f){return net.minecraft.block.state.BlockFaceShape.UNDEFINED;}
    @Override public boolean isAir(IBlockState state,IBlockAccess w,BlockPos p){return true;}
    @Override public boolean isReplaceable(IBlockAccess w,BlockPos p){return true;}
    @Override public boolean isOpaqueCube(IBlockState s){return false;}
    @Override public boolean isFullCube(IBlockState s){return false;}
    @Override public boolean canCollideCheck(IBlockState s,boolean liquid){return false;}
    @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState s,IBlockAccess w,BlockPos p){return NULL_AABB;}
    @Override public EnumBlockRenderType getRenderType(IBlockState s){return EnumBlockRenderType.INVISIBLE;}
    @Override public boolean hasTileEntity(IBlockState s){return true;}
    @Override public TileEntity createTileEntity(World w,IBlockState s){return new TransientLightTile();}
    @Override public int quantityDropped(java.util.Random random){return 0;}
}
