package ru.metrocraft.port.blackrazid.transport;

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

/** Source low rail collision, not a vanilla minecart rail or a static decor alias. */
public final class WideRailBlock extends Block {
    public static final PropertyInteger DIRECTION=PropertyInteger.create("direction",0,3);
    private final boolean main;
    public WideRailBlock(boolean main){super(Material.IRON);this.main=main;setHardness(2.5F);setResistance(5);setSoundType(SoundType.METAL);if(main)setDefaultState(blockState.getBaseState().withProperty(DIRECTION,0));}
    @Override protected BlockStateContainer createBlockState(){return new BlockStateContainer(this,DIRECTION);}
    @Override public IBlockState getStateFromMeta(int meta){return getDefaultState().withProperty(DIRECTION,meta&3);}
    @Override public int getMetaFromState(IBlockState s){return s.getValue(DIRECTION);}
    @Override public boolean hasTileEntity(IBlockState state){return true;}
    @Override public TileEntity createTileEntity(World world,IBlockState state){return new RailTile(main);}
    @Override public boolean isOpaqueCube(IBlockState s){return false;}
    @Override public boolean isFullCube(IBlockState s){return false;}
    @Override public EnumBlockRenderType getRenderType(IBlockState s){return EnumBlockRenderType.INVISIBLE;}
    @Override public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess access,BlockPos pos){
        TileEntity t=access.getTileEntity(pos);double height=main&&t instanceof RailTile&&((RailTile)t).shape()==2?1.375:.05;
        return new AxisAlignedBB(0,0,0,1,height,1);
    }
    @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState s,IBlockAccess a,BlockPos p){return getBoundingBox(s,a,p);}
    @Override public BlockFaceShape getBlockFaceShape(IBlockAccess world,IBlockState state,BlockPos pos,EnumFacing face){return BlockFaceShape.UNDEFINED;}
    @Override public int quantityDropped(Random random){return 0;}
    @Override public ItemStack getPickBlock(IBlockState state,RayTraceResult target,World world,BlockPos pos,net.minecraft.entity.player.EntityPlayer player){RailTile t=RailPlacement.primary(world,pos);return new ItemStack(BlackrazidRails.KIT,1,t==null?0:t.shape());}
    @Override public void breakBlock(World world,BlockPos pos,IBlockState state){RailPlacement.remove(world,pos);super.breakBlock(world,pos,state);world.removeTileEntity(pos);}
    @Override public void neighborChanged(IBlockState state,World world,BlockPos pos,Block block,BlockPos from){RailPlacement.neighbor(world,pos);}
    @Override public void updateTick(World w,BlockPos p,IBlockState state,Random random){
        if(w.isRemote)return;RailTile t=RailPlacement.at(w,p);if(t==null)return;
        if(!t.primary()){
            if(!w.isBlockLoaded(t.owner())){w.scheduleUpdate(p,this,20);return;}
            if(RailPlacement.primary(w,p)==null)w.setBlockToAir(p);
        }else{
            boolean pending=false;
            for(BlockPos offset:RailRules.footprint(t.shape(),t.direction())){
                BlockPos cell=p.add(offset);if(!w.isBlockLoaded(cell)){pending=true;continue;}
                RailTile proxy=RailPlacement.at(w,cell);if(!cell.equals(p)&&!t.owns(proxy)){RailPlacement.remove(w,p);return;}
            }
            RailPlacement.neighbor(w,p);if(pending)w.scheduleUpdate(p,this,20);
        }
    }
}
