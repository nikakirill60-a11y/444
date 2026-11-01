package ru.metrocraft.port.blackrazid.transport;

import java.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Validate first, mutate second, consume last. No chunk loading, commands, fake health or general handlers. */
public final class RailPlacement {
    private RailPlacement(){}
    private static final ThreadLocal<Boolean> MUTATING=ThreadLocal.withInitial(()->false);
    public static RailTile at(World w,BlockPos p){if(!w.isBlockLoaded(p))return null;TileEntity t=w.getTileEntity(p);return t instanceof RailTile?(RailTile)t:null;}
    public static RailTile primary(World w,BlockPos p){RailTile t=at(w,p);if(t==null)return null;if(t.primary())return w.getBlockState(p).getBlock()==BlackrazidRails.RAIL?t:null;if(w.getBlockState(p).getBlock()!=BlackrazidRails.PROXY)return null;RailTile parent=at(w,t.owner());return parent!=null&&w.getBlockState(parent.getPos()).getBlock()==BlackrazidRails.RAIL&&parent.owns(t)?parent:null;}
    public static boolean place(World w,BlockPos origin,int shape,int direction,EntityPlayer player,ItemStack stack){
        if(w.isRemote||!RailRules.valid(shape)||stack.isEmpty()||player.isSpectator())return false;
        Map<BlockPos,IBlockState> before=new LinkedHashMap<>();int height=w.getActualHeight();if(height<=0)height=256;
        for(BlockPos offset:RailRules.footprint(shape,direction)){
            BlockPos p=origin.add(offset);
            if(p.getY()<0||p.getY()>=height||!w.isBlockLoaded(p)||!w.getWorldBorder().contains(p)||!w.isBlockModifiable(player,p)||!player.canPlayerEdit(p,EnumFacing.UP,stack))return false;
            IBlockState old=w.getBlockState(p);
            if(w.getTileEntity(p)!=null||!old.getBlock().isReplaceable(w,p))return false;
            if(offset.getY()==0&&(!w.isBlockLoaded(p.down())||!w.getBlockState(p.down()).isSideSolid(w,p.down(),EnumFacing.UP)))return false;
            before.put(p,old);
        }
        boolean previous=MUTATING.get();MUTATING.set(true);UUID identity=UUID.randomUUID();boolean success=false;List<BlockPos> written=new ArrayList<>();
        try{
            for(BlockPos p:before.keySet()){
                boolean main=p.equals(origin);IBlockState state=(main?BlackrazidRails.RAIL:BlackrazidRails.PROXY).getDefaultState();
                if(main)state=state.withProperty(WideRailBlock.DIRECTION,direction&3);
                if(!w.setBlockState(p,state,2))return false;
                written.add(p);RailTile tile=at(w,p);if(tile==null)return false;tile.configure(main,shape,direction,origin,identity);
            }
            success=true;
        }finally{
            try{if(!success)for(BlockPos p:written){
                IBlockState current=w.getBlockState(p);
                if(current.getBlock()==BlackrazidRails.RAIL||current.getBlock()==BlackrazidRails.PROXY)w.setBlockState(p,before.get(p),2);
            }}finally{MUTATING.set(previous);}
        }
        RailTile main=at(w,origin);if(main!=null)main.updatePower();
        if(!player.capabilities.isCreativeMode)stack.shrink(1);
        for(BlockPos p:before.keySet())w.notifyNeighborsOfStateChange(p,w.getBlockState(p).getBlock(),false);
        return true;
    }
    /** Called before vanilla removes the current tile. Only matching owner/UUID cells are touched. */
    public static void remove(World w,BlockPos broken){
        if(w.isRemote||MUTATING.get())return;RailTile dying=at(w,broken);RailTile main=dying==null?null:dying.primary()?dying:at(w,dying.owner());if(main==null||(!dying.primary()&&(!main.owns(dying)||w.getBlockState(main.getPos()).getBlock()!=BlackrazidRails.RAIL)))return;
        boolean previous=MUTATING.get();MUTATING.set(true);
        try{
            for(BlockPos offset:RailRules.footprint(main.shape(),main.direction())){
                BlockPos p=main.getPos().add(offset);RailTile tile=at(w,p);
                net.minecraft.block.Block block=w.getBlockState(p).getBlock();
                if((tile==main&&block==BlackrazidRails.RAIL)||(main.owns(tile)&&block==BlackrazidRails.PROXY))w.setBlockToAir(p);
            }
        }finally{MUTATING.set(previous);}
    }
    public static void neighbor(World w,BlockPos p){
        if(w.isRemote||MUTATING.get())return;RailTile main=primary(w,p);if(main==null)return;
        // Defer a support decision for unloaded cells; never turn an unloaded neighbor into a forced chunk load.
        for(BlockPos offset:RailRules.footprint(main.shape(),main.direction()))if(offset.getY()==0){BlockPos cell=main.getPos().add(offset);if(w.isBlockLoaded(cell.down())&&!w.getBlockState(cell.down()).isSideSolid(w,cell.down(),EnumFacing.UP)){remove(w,p);return;}}
        main.updatePower();
    }
}
