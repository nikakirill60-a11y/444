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
    public boolean ready, removing, updatingParts;
    private boolean layoutReady;
    private boolean powered, blocked, manual;
    public boolean enabled() { return ready && (manual || powered); }
    public int output() { return ready && profile.is("Lever") && manual?15:0; }
    public boolean activate(net.minecraft.entity.player.EntityPlayer player) {
        if(!ready || removing || profile.is("Germo") || player.isSpectator())return false;
        if(world.isRemote)return true;
        if(!world.isBlockModifiable(player,pos) || !player.canPlayerEdit(pos,EnumFacing.UP,player.getHeldItemMainhand()))return false;
        manual=!manual;markDirty();sync();notifyFunctionalChange();
        return true;
    }
    private void notifyFunctionalChange() {
        for(BlockPos cell:occupied()) {
            world.notifyNeighborsOfStateChange(cell,world.getBlockState(cell).getBlock(),false);
            if(profile.is("Light"))world.checkLight(cell);
        }
    }
    private int ticks, soundDirection;
    private List<BlockPos> cells;
    private List<AxisAlignedBB> cachedBoxes;
    private float cachedProgress=-1;

    public void configure(UUID id,OriginalGateProfile type,EnumFacing direction) {
        owner=id;profile=type;facing=direction;progress=previous=0;
        powered=blocked=manual=ready=removing=false;layoutReady=true;invalidateGeometry();markDirty();
    }
    private void invalidateGeometry() { cells=null;cachedBoxes=null;cachedProgress=-1; }
    public List<BlockPos> footprint() {
        if(cells==null)cells=profile.footprint(pos,facing);return cells;
    }
    public List<BlockPos> occupied() { return profile.occupied(pos,facing,progress); }
    private boolean owns(BlockPos cell) {
        if(cell.equals(pos))return true;
        net.minecraft.tileentity.TileEntity tile=world.getTileEntity(cell);
        return tile instanceof OriginalGatePartTile && owner.equals(((OriginalGatePartTile)tile).owner) && pos.equals(((OriginalGatePartTile)tile).origin);
    }
    /** Install only the current solid cells. Never replace another block/structure.
     * Allocate first, roll back on failure, then release old cells with callbacks
     * suppressed. This also removes the dev25/26 full-prism reservation on load.
     */
    public boolean fitParts(float value) {
        List<BlockPos> wanted=profile.occupied(pos,facing,value);
        Set<BlockPos> keep=new HashSet<>(wanted);
        for(BlockPos cell:wanted) {
            if(!world.isBlockLoaded(cell) || cell.getY()<0 || cell.getY()>=world.getHeight() || !world.getWorldBorder().contains(cell))return false;
            if(!cell.equals(pos) && !owns(cell) && !world.isAirBlock(cell))return false;
        }
        List<BlockPos> added=new ArrayList<>();updatingParts=true;
        try {
            for(BlockPos cell:wanted)if(!cell.equals(pos) && !owns(cell)) {
                if(!world.setBlockState(cell,OriginalGates.PART.getDefaultState(),2)) {
                    for(BlockPos undo:added)if(owns(undo))world.setBlockToAir(undo);
                    return false;
                }
                net.minecraft.tileentity.TileEntity tile=world.getTileEntity(cell);
                if(!(tile instanceof OriginalGatePartTile)) {
                    if(tile==null && world.getBlockState(cell).getBlock()==OriginalGates.PART)world.setBlockToAir(cell);
                    // No blind rollback of a foreign TE.
                    for(BlockPos undo:added)if(owns(undo))world.setBlockToAir(undo);
                    return false;
                }
                ((OriginalGatePartTile)tile).configure(pos,owner);added.add(cell);
            }
            for(BlockPos cell:footprint())if(!keep.contains(cell) && world.isBlockLoaded(cell) && owns(cell))world.setBlockToAir(cell);
        } finally {updatingParts=false;}
        layoutReady=true;return true;
    }
    public void releaseUnusedPart(BlockPos cell) {
        if(!ready || !world.isBlockLoaded(cell) || occupied().contains(cell) || !owns(cell))return;
        updatingParts=true;try {world.setBlockToAir(cell);}finally {updatingParts=false;}
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
            // Old saved structures are migrated without deleting player blocks.
            // Current structures fail safely if a real occupied part is foreign.
            List<BlockPos> current=occupied();
            for(BlockPos cell:current) {
                if(!world.isBlockLoaded(cell)) {blocked=true;soundDirection=0;sync();return;}
                if(layoutReady && !cell.equals(pos) && !owns(cell)) {world.destroyBlock(pos,true);return;}
            }
            if(!layoutReady) {
                if(!fitParts(progress)) {blocked=true;sync();return;}
                markDirty();sync();
            }
            boolean signal=false;
            for(BlockPos cell:current)if(!profile.is("Lever"))signal |= world.isBlockPowered(cell);
            if(signal!=powered) {powered=signal;markDirty();sync();if(profile.is("Light"))notifyFunctionalChange();}
        }
        float target=(manual || powered)?1:0;
        float next=progress+Math.signum(target-progress)*Math.min(profile.step,Math.abs(target-progress));
        if(!world.isRemote) {
            boolean stop=next!=progress && (blockedBetween(progress,next) || !fitParts(next));
            if(stop!=blocked){blocked=stop;markDirty();sync();}
        }
        if(!blocked && next!=progress) {
            if(!world.isRemote) {
                int direction=next>progress?1:-1;
                if(direction!=soundDirection && profile.is("Germo")) {
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
        // At an internal pose boundary a rotating corner can exceed BOTH tick
        // endpoints. Include those extrema as well as the endpoints; matrices
        // interpolate linearly inside each segment, so this bounds every corner.
        List<List<AxisAlignedBB>> internal=new ArrayList<>();
        if(profile.parts[0].frames!=null) {
            float lo=Math.min(from,to)*64,hi=Math.max(from,to)*64;
            for(int frame=(int)Math.floor(lo)+1;frame<hi;frame++)internal.add(profile.boxes(pos,facing,frame/64F,true));
        }
        for(int i=0;i<before.size();i++) {
            AxisAlignedBB box=before.get(i).union(after.get(i));
            for(List<AxisAlignedBB> pose:internal)box=box.union(pose.get(i));
            swept.add(box);bounds=bounds==null?box:bounds.union(box);
        }
        if(bounds==null)return false;
        for(AxisAlignedBB box:swept)for(BlockPos cell:BlockPos.getAllInBox(new BlockPos(box.minX+1e-7,box.minY+1e-7,box.minZ+1e-7),new BlockPos(box.maxX-1e-7,box.maxY-1e-7,box.maxZ-1e-7))) {
            if(!world.isBlockLoaded(cell))return true;
            if(owns(cell))continue;
            IBlockState state=world.getBlockState(cell);List<AxisAlignedBB> hits=new ArrayList<>();
            state.addCollisionBoxToList(world,cell,box,hits,null,false);
            if(!hits.isEmpty())return true;
        }
        if(profile.is("Lever") || profile.is("Light"))return false;
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
        tag.setFloat("Progress",OriginalGateProfile.clamp(progress));tag.setBoolean("Ready",ready);tag.setInteger("LayoutVersion",layoutReady?2:0);
        tag.setBoolean("Manual",manual);
        tag.setBoolean("Powered",powered);tag.setBoolean("Blocked",blocked);return tag;
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);owner=tag.hasUniqueId("Owner")?tag.getUniqueId("Owner"):null;
        OriginalGateProfile type=OriginalGateProfile.get(tag.getString("Profile"));
        profile=type==null?OriginalGateProfile.get("metromod_germo_1"):type;
        facing=OriginalGateBlock.horizontal(tag.getInteger("Facing"));
        previous=progress=OriginalGateProfile.clamp(tag.getFloat("Progress"));
        ready=tag.getBoolean("Ready");powered=tag.getBoolean("Powered");blocked=tag.getBoolean("Blocked");
        manual=tag.getBoolean("Manual");
        layoutReady=tag.getInteger("LayoutVersion")>=2;removing=false;invalidateGeometry();
    }
    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos,0,getUpdateTag()); }
    @Override public void onDataPacket(NetworkManager manager,SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); }
}
