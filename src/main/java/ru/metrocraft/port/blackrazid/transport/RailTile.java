package ru.metrocraft.port.blackrazid.transport;

import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;

/** Main rail and ownership proxy share one registered tile type. Zero coordinates are valid. */
public final class RailTile extends TileEntity {
    private boolean primary,active;
    private int shape,direction;
    private BlockPos owner=BlockPos.ORIGIN;
    private UUID identity=UUID.randomUUID();
    public RailTile(){}
    public RailTile(boolean primary){this.primary=primary;}
    public int shape(){return shape;}
    public int direction(){return direction;}
    public boolean active(){return active;}
    public boolean primary(){return primary;}
    public BlockPos owner(){return primary?pos:owner;}
    public UUID identity(){return identity;}
    public void configure(boolean main,int shape,int direction,BlockPos owner,UUID id){
        if(!RailRules.valid(shape))throw new IllegalArgumentException("Unknown rail shape");
        this.primary=main;this.shape=shape;this.direction=direction&3;this.owner=owner.toImmutable();this.identity=id;changed();
    }
    private void changed(){markDirty();if(world!=null){IBlockState s=world.getBlockState(pos);world.notifyBlockUpdate(pos,s,s,2);}}
    public void updatePower(){if(world!=null&&!world.isRemote&&primary){
        // World.isBlockPowered reads all six neighbors and can otherwise force a neighboring chunk.
        int height=world.getHeight();if(height<=0)height=256;
        for(net.minecraft.util.EnumFacing face:net.minecraft.util.EnumFacing.values()){
            BlockPos neighbor=pos.offset(face);if(neighbor.getY()<0||neighbor.getY()>=height)continue;
            if(!world.isBlockLoaded(neighbor)){world.scheduleUpdate(pos,getBlockType(),20);return;}
        }
        boolean power=world.isBlockPowered(pos);if(active!=power){active=power;changed();}}}
    public boolean owns(RailTile other){return primary&&other!=null&&!other.primary&&identity.equals(other.identity)&&pos.equals(other.owner)&&RailRules.footprint(shape,direction).contains(other.pos.subtract(pos));}
    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);tag.setBoolean("Primary",primary);tag.setInteger("Shape",shape);tag.setInteger("Direction",direction);tag.setBoolean("Active",active);
        tag.setLong("Owner",owner().toLong());tag.setString("RailIdentity",identity.toString());return tag;
    }
    @Override public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);primary=tag.getBoolean("Primary");int id=tag.getInteger("Shape");shape=RailRules.valid(id)?id:0;direction=tag.getInteger("Direction")&3;active=tag.getBoolean("Active");owner=BlockPos.fromLong(tag.getLong("Owner"));
        try{identity=UUID.fromString(tag.getString("RailIdentity"));}catch(IllegalArgumentException e){identity=UUID.randomUUID();}
    }
    @Override public NBTTagCompound getUpdateTag(){return writeToNBT(new NBTTagCompound());}
    @Override public SPacketUpdateTileEntity getUpdatePacket(){return new SPacketUpdateTileEntity(pos,0,getUpdateTag());}
    @Override public void onDataPacket(NetworkManager net,SPacketUpdateTileEntity packet){readFromNBT(packet.getNbtCompound());}
    @Override public boolean shouldRefresh(net.minecraft.world.World world,BlockPos pos,IBlockState old,IBlockState next){return old.getBlock()!=next.getBlock();}
    @Override public AxisAlignedBB getRenderBoundingBox(){
        if(!primary)return new AxisAlignedBB(pos);
        AxisAlignedBB result=new AxisAlignedBB(pos);for(BlockPos offset:RailRules.footprint(shape,direction))result=result.union(new AxisAlignedBB(pos.add(offset)));return result.grow(2);
    }
    @Override public double getMaxRenderDistanceSquared(){return 2048;}
    @Override public void onLoad(){if(world!=null&&!world.isRemote){updatePower();world.scheduleUpdate(pos,getBlockType(),1);}}
}
