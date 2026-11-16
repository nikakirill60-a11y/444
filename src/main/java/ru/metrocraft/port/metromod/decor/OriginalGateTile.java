package ru.metrocraft.port.metromod.decor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import java.util.*;

/** Server-authoritative original gate motion. Inventory/geometry is never sent by custom packets. */
public final class OriginalGateTile extends TileEntity implements ITickable {
    public UUID owner;
    public OriginalGateProfile profile=OriginalGateProfile.get("metromod_germo_1");
    public EnumFacing facing=EnumFacing.SOUTH;
    public float progress, previous;
    public boolean ready, removing;
    private boolean powered, blocked;
    private int ticks, soundDirection;
    private List<BlockPos> cells;
    private List<AxisAlignedBB> cachedBoxes;
    private float cachedProgress=-1;

    public void configure(UUID id,OriginalGateProfile type,EnumFacing direction) {
        owner=id;profile=type;facing=direction;progress=previous=0;
        powered=blocked=ready=removing=false;invalidateGeometry();markDirty();
    }
    private void invalidateGeometry() { cells=null;cachedBoxes=null;cachedProgress=-1; }
    public List<BlockPos> footprint() {
        if(cells==null)cells=profile.footprint(pos,facing);return cells;
    }
    @Override public void update() {
        previous=progress;
        if(!ready || removing || owner==null)return;
        ++ticks;
        if(!world.isRemote) {
            // The block registry, not arbitrary NBT, chooses the profile.
            if(getBlockType() instanceof OriginalGateBlock && profile!=((OriginalGateBlock)getBlockType()).profile) {
                profile=((OriginalGateBlock)getBlockType()).profile;invalidateGeometry();
            }
            boolean signal=false;
            for(BlockPos cell:footprint()) {
                if(!world.isBlockLoaded(cell)) {
                    if(!blocked){blocked=true;markDirty();sync();}soundDirection=0;return;
                }
                if(!cell.equals(pos)) {
                    TileEntity tile=world.getTileEntity(cell);
                    if(!(tile instanceof OriginalGatePartTile) || !owner.equals(((OriginalGatePartTile)tile).owner)
                            || !pos.equals(((OriginalGatePartTile)tile).origin)) {
                        world.destroyBlock(pos,true);return;
                    }
                }
                signal |= world.isBlockPowered(cell);
            }
            if(signal!=powered) {powered=signal;markDirty();sync();}
        }
        float target=powered?1:0;
        float next=progress+Math.signum(target-progress)*Math.min(profile.step,Math.abs(target-progress));
        if(!world.isRemote) {
            boolean stop=next!=progress && blockedBetween(progress,next);
            if(stop!=blocked){blocked=stop;markDirty();sync();}
        }
        if(!blocked && next!=progress) {
            if(!world.isRemote) {
                int direction=next>progress?1:-1;
                if(direction!=soundDirection) {
                    net.minecraft.util.SoundEvent sound=ru.metrocraft.port.metromod.MetroAudio.get(
                            ru.metrocraft.port.metromod.MetroAudio.gate(direction>0),"germo");
                    if(sound!=null)world.playSound(null,pos,sound,SoundCategory.BLOCKS,.4F,1);
                    soundDirection=direction;
                }
            }
            progress=OriginalGateProfile.clamp(next);
            if(!world.isRemote){markDirty();if(ticks%4==0 || progress==target)sync();}
        }
        if(blocked || progress==target)soundDirection=0;
    }
    /** One broad-phase entity query; exact conservative moving boxes, including the tick's sweep. */
    public boolean blockedBetween(float from,float to) {
        List<AxisAlignedBB> before=profile.boxes(pos,facing,from,true),after=profile.boxes(pos,facing,to,true);
        List<AxisAlignedBB> swept=new ArrayList<>();AxisAlignedBB bounds=null;
        for(int i=0;i<before.size();i++) {
            AxisAlignedBB box=before.get(i).union(after.get(i));swept.add(box);bounds=bounds==null?box:bounds.union(box);
        }
        if(bounds==null)return false;
        for(Entity entity:world.getEntitiesWithinAABBExcludingEntity(null,bounds)) {
            if(!(entity instanceof EntityLivingBase) || !entity.isEntityAlive() || entity.noClip
                    || entity instanceof net.minecraft.entity.player.EntityPlayer && ((net.minecraft.entity.player.EntityPlayer)entity).isSpectator())continue;
            for(AxisAlignedBB box:swept)if(box.intersects(entity.getEntityBoundingBox()))return true;
        }
        return false;
    }
    public boolean blockedAt(float value) { return blockedBetween(value,value); }
    public void sync() { IBlockState state=world.getBlockState(pos);world.notifyBlockUpdate(pos,state,state,2); }
    public void removeParts() {
        if(removing || owner==null)return;removing=true;
        for(BlockPos cell:footprint()) {
            if(cell.equals(pos) || !world.isBlockLoaded(cell))continue;
            TileEntity tile=world.getTileEntity(cell);
            if(tile instanceof OriginalGatePartTile && owner.equals(((OriginalGatePartTile)tile).owner)
                    && pos.equals(((OriginalGatePartTile)tile).origin))world.setBlockToAir(cell);
        }
    }
    public List<AxisAlignedBB> collisionBoxes() {
        if(cachedBoxes==null || cachedProgress!=progress) {
            cachedBoxes=profile.boxes(pos,facing,progress,false);cachedProgress=progress;
        }
        return cachedBoxes;
    }
    public void addCollision(BlockPos cell,AxisAlignedBB query,List<AxisAlignedBB> result) {
        AxisAlignedBB cellBox=new AxisAlignedBB(cell);
        for(AxisAlignedBB box:collisionBoxes())if(box.intersects(cellBox)) {
            AxisAlignedBB clipped=box.intersect(cellBox);if(clipped.intersects(query))result.add(clipped);
        }
    }
    public RayTraceResult rayTrace(BlockPos cell,Vec3d start,Vec3d end) {
        RayTraceResult closest=null;double distance=Double.POSITIVE_INFINITY;
        AxisAlignedBB cellBox=new AxisAlignedBB(cell);
        for(AxisAlignedBB box:collisionBoxes())if(box.intersects(cellBox)) {
            RayTraceResult hit=box.intersect(cellBox).calculateIntercept(start,end);
            if(hit!=null && hit.hitVec.squareDistanceTo(start)<distance) {
                distance=hit.hitVec.squareDistanceTo(start);closest=new RayTraceResult(hit.hitVec,hit.sideHit,cell);
            }
        }
        return closest;
    }
    @Override public AxisAlignedBB getRenderBoundingBox() { return profile.bounds(pos,facing); }
    @Override public double getMaxRenderDistanceSquared() { return 16384; }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);if(owner!=null)tag.setUniqueId("Owner",owner);
        tag.setString("Profile",profile.id);tag.setInteger("Facing",facing.getHorizontalIndex());
        tag.setFloat("Progress",OriginalGateProfile.clamp(progress));tag.setBoolean("Ready",ready);
        tag.setBoolean("Powered",powered);tag.setBoolean("Blocked",blocked);return tag;
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);owner=tag.hasUniqueId("Owner")?tag.getUniqueId("Owner"):null;
        OriginalGateProfile type=OriginalGateProfile.get(tag.getString("Profile"));
        profile=type==null?OriginalGateProfile.get("metromod_germo_1"):type;
        facing=OriginalGateBlock.horizontal(tag.getInteger("Facing"));
        previous=progress=OriginalGateProfile.clamp(tag.getFloat("Progress"));
        ready=tag.getBoolean("Ready");powered=tag.getBoolean("Powered");blocked=tag.getBoolean("Blocked");
        removing=false;invalidateGeometry();
    }
    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos,0,getUpdateTag()); }
    @Override public void onDataPacket(NetworkManager manager,SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); }
}
