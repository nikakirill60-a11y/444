package ru.metrocraft.port.blackrazid.environment;

import java.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Validate first, mutate second, consume last. Native ring helpers never overwrite or blindly clear another map block. */
public final class TunnelPlacement {
    private TunnelPlacement(){}
    private static final ThreadLocal<Boolean> MUTATING=ThreadLocal.withInitial(()->false);
    public static TunnelTile at(World w,BlockPos p){if(!w.isBlockLoaded(p))return null;TileEntity t=w.getTileEntity(p);return t instanceof TunnelTile?(TunnelTile)t:null;}
    public static TunnelTile primary(World w,BlockPos p){TunnelTile t=at(w,p);if(t==null)return null;if(t.primary())return w.getBlockState(p).getBlock()==BlackrazidEnvironment.TUNNEL?t:null;if(w.getBlockState(p).getBlock()!=BlackrazidEnvironment.PROXY)return null;TunnelTile parent=at(w,t.owner());return parent!=null&&w.getBlockState(parent.getPos()).getBlock()==BlackrazidEnvironment.TUNNEL&&parent.owns(t)?parent:null;}
    public static boolean place(World w,BlockPos origin,int shape,int direction,EntityPlayer player,ItemStack stack){
        return place(w,origin,shape,direction,player,stack,EnumFacing.UP);
    }
    public static boolean place(World w,BlockPos origin,int shape,int direction,EntityPlayer player,ItemStack stack,EnumFacing clickedFace){
        if(w.isRemote||!TunnelRules.valid(shape)||stack.isEmpty()||player.isSpectator())return false;
        Map<BlockPos,IBlockState> before=new LinkedHashMap<>();int height=w.getHeight();if(height<=0)height=256;
        for(BlockPos offset:TunnelRules.footprint(shape,direction)){
            BlockPos p=origin.add(offset);
            if(p.getY()<0||p.getY()>=height||!w.isBlockLoaded(p)||!w.getWorldBorder().contains(p)||!w.isBlockModifiable(player,p)||!player.canPlayerEdit(p,clickedFace,stack))return false;
            IBlockState old=w.getBlockState(p);
            if(w.getTileEntity(p)!=null||!old.getBlock().isReplaceable(w,p))return false;
            before.put(p,old);
        }
        boolean previous=MUTATING.get();MUTATING.set(true);UUID identity=UUID.randomUUID();boolean success=false;List<BlockPos> written=new ArrayList<>();
        try{
            for(BlockPos p:before.keySet()){
                boolean main=p.equals(origin);IBlockState state=(main?BlackrazidEnvironment.TUNNEL:BlackrazidEnvironment.PROXY).getDefaultState();
                if(main)state=state.withProperty(TunnelBlock.DIRECTION,direction&3);
                if(!w.setBlockState(p,state,2))return false;
                written.add(p);TunnelTile tile=at(w,p);if(tile==null)return false;tile.configure(main,shape,direction,origin,identity);
            }
            for(BlockPos p:before.keySet()){
                TunnelTile tile=at(w,p);if(tile==null||!identity.equals(tile.identity())||!origin.equals(tile.owner())||tile.shape()!=shape||tile.direction()!=(direction&3)||tile.primary()!=p.equals(origin))return false;
                if(w.getBlockState(p).getBlock()!=(p.equals(origin)?BlackrazidEnvironment.TUNNEL:BlackrazidEnvironment.PROXY))return false;
            }
            success=true;
        }finally{
            try{if(!success)for(BlockPos p:written){
                if(!w.isBlockLoaded(p))continue;IBlockState current=w.getBlockState(p);TileEntity raw=w.getTileEntity(p);
                if((current.getBlock()==BlackrazidEnvironment.TUNNEL||current.getBlock()==BlackrazidEnvironment.PROXY)&&(raw==null||(raw instanceof TunnelTile&&identity.equals(((TunnelTile)raw).identity()))))w.setBlockState(p,before.get(p),2);
            }}finally{MUTATING.set(previous);}
        }
        if(!player.capabilities.isCreativeMode)stack.shrink(1);
        for(BlockPos p:before.keySet())w.notifyNeighborsOfStateChange(p,w.getBlockState(p).getBlock(),false);
        return true;
    }
    /** Called before vanilla removes the current tile. Only matching owner/UUID cells are touched. */
    public static void remove(World w,BlockPos broken){
        if(w.isRemote||MUTATING.get())return;TunnelTile dying=at(w,broken);TunnelTile main=dying==null?null:dying.primary()?dying:at(w,dying.owner());if(main==null||(!dying.primary()&&(!main.owns(dying)||w.getBlockState(main.getPos()).getBlock()!=BlackrazidEnvironment.TUNNEL)))return;
        boolean previous=MUTATING.get();MUTATING.set(true);
        try{
            for(BlockPos offset:TunnelRules.footprint(main.shape(),main.direction())){
                BlockPos p=main.getPos().add(offset);TunnelTile tile=at(w,p);if(tile==null)continue;
                net.minecraft.block.Block block=w.getBlockState(p).getBlock();
                if((tile==main&&block==BlackrazidEnvironment.TUNNEL)||(main.owns(tile)&&block==BlackrazidEnvironment.PROXY))w.setBlockToAir(p);
            }
        }finally{MUTATING.set(previous);}
    }
    public static void validate(World w,BlockPos p){
        if(w.isRemote||MUTATING.get())return;TunnelTile main=primary(w,p);if(main==null)return;
        boolean pending=false;
        for(BlockPos offset:TunnelRules.footprint(main.shape(),main.direction())){
            BlockPos cell=main.getPos().add(offset);if(!w.isBlockLoaded(cell)){pending=true;continue;}
            if(primary(w,cell)!=main){remove(w,p);return;}
        }
        if(pending)w.scheduleUpdate(main.getPos(),BlackrazidEnvironment.TUNNEL,20);
    }
}
