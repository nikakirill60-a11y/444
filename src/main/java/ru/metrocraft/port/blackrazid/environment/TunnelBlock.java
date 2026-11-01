package ru.metrocraft.port.blackrazid.environment;

import java.util.Random;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

/** Native full-cell tunnel anchor/helper collision; geometry is rendered only by its owning anchor. */
public final class TunnelBlock extends Block {
    public static final PropertyInteger DIRECTION=PropertyInteger.create("direction",0,3);
    private final boolean main;
    public TunnelBlock(boolean main){super(Material.ROCK);this.main=main;setBlockUnbreakable();setResistance(6000000);setLightOpacity(0);setSoundType(SoundType.STONE);if(main)setDefaultState(blockState.getBaseState().withProperty(DIRECTION,0));}
    @Override protected BlockStateContainer createBlockState(){return new BlockStateContainer(this,DIRECTION);}
    @Override public IBlockState getStateFromMeta(int meta){return getDefaultState().withProperty(DIRECTION,meta&3);}
    @Override public int getMetaFromState(IBlockState s){return s.getValue(DIRECTION);}
    @Override public boolean hasTileEntity(IBlockState state){return true;}
    @Override public TileEntity createTileEntity(World world,IBlockState state){return new TunnelTile(main);}
    @Override public boolean isOpaqueCube(IBlockState s){return false;}
    @Override public boolean isFullCube(IBlockState s){return false;}
    @Override public EnumBlockRenderType getRenderType(IBlockState s){return EnumBlockRenderType.INVISIBLE;}
    @Override public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess access,BlockPos pos){
        return FULL_BLOCK_AABB;
    }
    @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState s,IBlockAccess a,BlockPos p){return getBoundingBox(s,a,p);}
    @Override public BlockFaceShape getBlockFaceShape(IBlockAccess world,IBlockState state,BlockPos pos,EnumFacing face){return BlockFaceShape.UNDEFINED;}
    @Override public int quantityDropped(Random random){return 0;}
    @Override public ItemStack getPickBlock(IBlockState state,RayTraceResult target,World world,BlockPos pos,net.minecraft.entity.player.EntityPlayer player){TunnelTile t=TunnelPlacement.primary(world,pos);return new ItemStack(BlackrazidEnvironment.KIT,1,t==null?0:t.shape());}
    @Override public boolean removedByPlayer(IBlockState state,World w,BlockPos p,net.minecraft.entity.player.EntityPlayer player,boolean harvest){
        if(!w.isRemote){
            TunnelTile tile=TunnelPlacement.at(w,p);if(tile!=null&&!tile.primary()&&!w.isBlockLoaded(tile.owner()))return false;
            TunnelTile main=TunnelPlacement.primary(w,p);
            if(main!=null)for(BlockPos offset:TunnelRules.footprint(main.shape(),main.direction())){
                BlockPos cell=main.getPos().add(offset);if(!w.isBlockLoaded(cell)||!w.isBlockModifiable(player,cell))return false;
            }
        }
        return super.removedByPlayer(state,w,p,player,harvest);
    }
    @Override public void breakBlock(World world,BlockPos pos,IBlockState state){TunnelPlacement.remove(world,pos);super.breakBlock(world,pos,state);world.removeTileEntity(pos);}
    @Override public void neighborChanged(IBlockState state,World world,BlockPos pos,Block block,BlockPos from){TunnelPlacement.validate(world,pos);}
    @Override public void updateTick(World w,BlockPos p,IBlockState state,Random random){
        if(w.isRemote)return;TunnelTile t=TunnelPlacement.at(w,p);if(t==null)return;
        if(!t.primary()){
            if(!w.isBlockLoaded(t.owner())){w.scheduleUpdate(p,this,20);return;}
            if(TunnelPlacement.primary(w,p)==null)w.setBlockToAir(p);
        }else TunnelPlacement.validate(w,p);
    }
}
