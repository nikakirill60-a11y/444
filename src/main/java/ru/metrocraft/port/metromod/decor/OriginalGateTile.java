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
    private double[] customDoorBounds;
    private DonorGermoMotion germo;
    private static final String[] AABB_KEYS={"aabb_minx","aabb_miny","aabb_minz","aabb_maxx","aabb_maxy","aabb_maxz"};
    public boolean enabled() { return ready && (manual || powered); }
    public int output() { return ready && profile.is("Lever") && manual?15:0; }
    public boolean activate(net.minecraft.entity.player.EntityPlayer player) {
        if(!ready || removing || profile.is("Germo") || player.isSpectator())return false;
        if(world.isRemote)return true;
        if(!world.isBlockModifiable(player,pos) || !player.canPlayerEdit(pos,EnumFacing.UP,player.getHeldItemMainhand()))return false;
        if(profile.donorDoor!=null && manual && !powered && !canCloseDoor())return false;
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
    private boolean needsPartSync=true;
    private List<BlockPos> cells;
    private List<AxisAlignedBB> cachedBoxes;
    private float cachedProgress=-1;

    public void configure(UUID id,OriginalGateProfile type,EnumFacing direction) {
        owner=id;profile=type;facing=direction;progress=previous=0;customDoorBounds=null;
        germo=profile.donorGermo==null?null:profile.donorGermo.seed(profile.id,facing,0);
        powered=blocked=manual=ready=removing=false;layoutReady=true;invalidateGeometry();markDirty();OriginalDecorCollisions.track(this);
    }
    @Override public void onLoad() { super.onLoad();OriginalDecorCollisions.track(this); }
    @Override public void invalidate() { OriginalDecorCollisions.untrack(this);super.invalidate(); }
    @Override public void onChunkUnload() { OriginalDecorCollisions.untrack(this);super.onChunkUnload(); }
    private void invalidateGeometry() { cells=null;cachedBoxes=null;cachedProgress=-1; }
    public List<BlockPos> footprint() {
        if(cells==null) {
            LinkedHashSet<BlockPos> all=new LinkedHashSet<>(profile.footprint(pos,facing));
            if(profile.donorGermo!=null)all.addAll(occupied());cells=new ArrayList<>(all);
        }
        return cells;
    }
    private DonorGermoMotion germo() {
        if(germo==null && profile.donorGermo!=null)germo=profile.donorGermo.seed(profile.id,facing,progress);
        return germo;
    }
    public List<BlockPos> occupied() {
        return profile.donorGermo==null?profile.occupied(pos,facing,progress):DonorGermoCollision.occupied(pos,collisionBoxes());
    }
    private boolean owns(BlockPos cell) {
        if(owner==null)return false;
        if(cell.equals(pos))return true;
        net.minecraft.tileentity.TileEntity tile=world.getTileEntity(cell);
        return tile instanceof OriginalGatePartTile && owner.equals(((OriginalGatePartTile)tile).owner) && pos.equals(((OriginalGatePartTile)tile).origin);
    }
    /** Install only the current solid cells. Never replace another block/structure.
     * Allocate first, roll back on failure, then release old cells with callbacks
     * suppressed. This also removes the dev25/26 full-prism reservation on load.
     */
    public boolean fitParts(float value) {
        return fitCells(profile.occupied(pos,facing,value));
    }
    private boolean fitCells(List<BlockPos> wanted) {
        LinkedHashSet<BlockPos> cleanup=new LinkedHashSet<>(footprint());cleanup.addAll(occupied());
        Set<BlockPos> keep=new HashSet<>(wanted);
        for(BlockPos cell:wanted) {
            if(!world.isBlockLoaded(cell) || cell.getY()<0 || cell.getY()>=world.getHeight() || !world.getWorldBorder().contains(cell))return false;
            if(profile.donorDoor==null && !cell.equals(pos) && !owns(cell) && !world.isAirBlock(cell))return false;
        }
        List<BlockPos> added=new ArrayList<>();updatingParts=true;
        try {
            for(BlockPos cell:wanted)if(!cell.equals(pos) && !owns(cell)) {
                // Door proxies serve selection only. Never overwrite a real block
                // built while the door was activated, or adopt another UUID.
                if(profile.donorDoor!=null && !world.isAirBlock(cell))continue;
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
            List<BlockPos> removed=new ArrayList<>();
            for(BlockPos cell:cleanup)if(!keep.contains(cell) && world.isBlockLoaded(cell) && owns(cell)) {
                if(!world.setBlockToAir(cell)) {
                    // Restore only our released cells into still-air destinations.
                    for(BlockPos undo:removed)if(world.isAirBlock(undo) && world.setBlockState(undo,OriginalGates.PART.getDefaultState(),2)) {
                        TileEntity restored=world.getTileEntity(undo);
                        if(restored instanceof OriginalGatePartTile)((OriginalGatePartTile)restored).configure(pos,owner);
                    }
                    for(BlockPos undo:added)if(owns(undo))world.setBlockToAir(undo);
                    return false;
                }
                removed.add(cell);
            }
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
            if(needsPartSync && !world.captureBlockSnapshots) {syncStructure();needsPartSync=false;}
            // The block registry, not arbitrary NBT, chooses the profile.
            if(getBlockType() instanceof OriginalGateBlock && profile!=((OriginalGateBlock)getBlockType()).profile) {
                profile=((OriginalGateBlock)getBlockType()).profile;germo=null;layoutReady=false;invalidateGeometry();
            }
            if(profile.donorGermo!=null) {updateGermo();return;}
            // Old saved structures are migrated without deleting player blocks.
            // Current structures fail safely if a real occupied part is foreign.
            List<BlockPos> current=occupied();
            for(BlockPos cell:current) {
                if(!world.isBlockLoaded(cell)) {blocked=true;soundDirection=0;sync();return;}
                if(profile.donorDoor==null && layoutReady && !cell.equals(pos) && !owns(cell)) {world.destroyBlock(pos,true);return;}
            }
            if(!layoutReady) {
                if(!fitParts(progress)) {blocked=true;sync();return;}
                markDirty();sync();
            }
            boolean signal=false;
            for(BlockPos cell:current)if(!profile.is("Lever"))signal |= world.isBlockPowered(cell);
            if(profile.donorDoor!=null && !signal && powered && !manual && !canCloseDoor())signal=true;
            if(signal!=powered) {powered=signal;markDirty();sync();if(profile.is("Light"))notifyFunctionalChange();}
        }
        if(profile.donorGermo!=null)return; // clients consume authoritative motion snapshots
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
    /** Stateful callbacks generate a candidate only. A blocked tick commits
     * neither cells, segment flags, previous vectors nor animation. */
    private void updateGermo() {
        DonorGermoMotion current=germo();List<AxisAlignedBB> before=collisionBoxes();
        List<BlockPos> installed=occupied();
        // Missing chunks pause; missing/foreign cells pause instead of destroying
        // the controller or adopting a different gate's UUID.
        for(BlockPos cell:installed)if(!world.isBlockLoaded(cell)) {blocked=true;sync();return;}
        if(!layoutReady) {
            if(!canFitGermo(Collections.emptyList(),before) || !fitCells(installed)) {blocked=true;sync();return;}
            markDirty();syncStructure();
        }
        boolean signal=false;for(BlockPos cell:installed)signal |= world.isBlockPowered(cell);
        boolean wasBlocked=blocked;powered=signal;
        DonorGermoMotion candidate=current.next(signal);
        List<AxisAlignedBB> after=profile.donorGermo.boxes(profile,candidate,pos,facing);
        boolean cellsChanged=!Arrays.equals(current.packedCells(),candidate.packedCells());
        boolean missing=false;for(BlockPos cell:installed)if(!owns(cell))missing=true;
        boolean stop=!canFitGermo(before,after) || ((cellsChanged || missing) && !fitCells(DonorGermoCollision.occupied(pos,after)));
        if(stop) {if(!blocked){blocked=true;markDirty();sync();}soundDirection=0;return;}
        boolean changed=Float.floatToIntBits(progress)!=Float.floatToIntBits(candidate.progress());
        germo=candidate;progress=candidate.progress();blocked=false;if(cellsChanged)invalidateGeometry();markDirty();
        int direction=Float.compare(progress,previous);
        if(direction!=0 && direction!=soundDirection) {
            net.minecraft.util.SoundEvent sound=ru.metrocraft.port.metromod.MetroAudio.get(ru.metrocraft.port.metromod.MetroAudio.gate(direction>0),"germo");
            if(sound!=null)world.playSound(null,pos,sound,SoundCategory.BLOCKS,.4F,1);
        }
        soundDirection=direction;
        if(changed || cellsChanged || wasBlocked || ticks%20==0)syncStructure();
    }
    private boolean canFitGermo(List<AxisAlignedBB> before,List<AxisAlignedBB> after) {
        for(AxisAlignedBB box:after) {
            for(BlockPos cell:DonorGermoCollision.occupied(pos,Collections.singletonList(box))) {
                if(!world.isBlockLoaded(cell) || cell.getY()<0 || cell.getY()>=world.getHeight() || !world.getWorldBorder().contains(cell))return false;
                if(!owns(cell) && !world.isAirBlock(cell))return false;
            }
            if(before.contains(box))continue;
            for(Entity entity:world.getEntitiesWithinAABBExcludingEntity(null,box))
                if(entity.isEntityAlive() && !entity.noClip && !(entity instanceof net.minecraft.entity.player.EntityPlayer && ((net.minecraft.entity.player.EntityPlayer)entity).isSpectator())
                        && box.intersects(entity.getEntityBoundingBox()))return false;
        }
        return true;
    }
    /** One broad-phase entity query; exact conservative moving boxes, including the tick's sweep. */
    public boolean blockedBetween(float from,float to) {
        // Original Door collision depends on activation, NOT the animated leaf.
        // Cosmetic swings must not be stopped by walls or invisible render bounds.
        if(profile.donorDoor!=null)return false;
        if(profile.donorGermo!=null)return !canFitGermo(Collections.emptyList(),collisionBoxes());
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
    public void syncStructure() {
        if(world==null || world.isRemote || world.captureBlockSnapshots)return;
        sync();for(BlockPos cell:occupied())if(!cell.equals(pos) && world.isBlockLoaded(cell) && owns(cell)) {
            ((OriginalGatePartTile)world.getTileEntity(cell)).sync();
        }
    }
    public void removeParts() {
        if(removing || owner==null)return;removing=true;
        for(BlockPos cell:footprint()) {
            if(cell.equals(pos) || !world.isBlockLoaded(cell))continue;
            TileEntity tile=world.getTileEntity(cell);
            if(tile instanceof OriginalGatePartTile && owner.equals(((OriginalGatePartTile)tile).owner)
                    && pos.equals(((OriginalGatePartTile)tile).origin))world.setBlockToAir(cell);
        }
    }
    public AxisAlignedBB doorBounds() {
        return profile.donorDoor.bounds(profile,pos,facing,customDoorBounds);
    }
    /** Safety adapter for closing into an existing block/player. It is intentionally
     * separate from the donor AABB rule; never push/delete foreign world blocks.
     */
    private boolean canCloseDoor() {
        AxisAlignedBB bounds=doorBounds();
        for(BlockPos cell:BlockPos.getAllInBox(new BlockPos(bounds.minX+1e-7,bounds.minY+1e-7,bounds.minZ+1e-7),
                new BlockPos(bounds.maxX-1e-7,bounds.maxY-1e-7,bounds.maxZ-1e-7))) {
            if(!world.isBlockLoaded(cell))return false;
            if(owns(cell))continue;
            List<AxisAlignedBB> hits=new ArrayList<>();
            world.getBlockState(cell).addCollisionBoxToList(world,cell,bounds,hits,null,false);
            if(!hits.isEmpty())return false;
        }
        for(Entity entity:world.getEntitiesWithinAABBExcludingEntity(null,bounds))
            if(entity.isEntityAlive() && !entity.noClip && !(entity instanceof net.minecraft.entity.player.EntityPlayer
                    && ((net.minecraft.entity.player.EntityPlayer)entity).isSpectator())
                    && entity.getEntityBoundingBox().intersects(bounds))return false;
        return true;
    }
    public List<AxisAlignedBB> collisionBoxes() {
        if(profile.donorGermo!=null)return profile.donorGermo.boxes(profile,germo(),pos,facing);
        if(profile.donorDoor!=null)return enabled()?Collections.emptyList():Collections.singletonList(doorBounds());
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
        for(AxisAlignedBB box:profile.donorDoor==null?collisionBoxes():Collections.singletonList(doorBounds()))if(box.intersects(cellBox)) {
            RayTraceResult hit=box.intersect(cellBox).calculateIntercept(start,end);
            if(hit!=null && hit.hitVec.squareDistanceTo(start)<distance) {
                distance=hit.hitVec.squareDistanceTo(start);closest=new RayTraceResult(hit.hitVec,hit.sideHit,cell);
            }
        }
        return closest;
    }
    @Override public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bounds=profile.bounds(pos,facing);
        if(profile.donorGermo!=null)for(AxisAlignedBB box:collisionBoxes())bounds=bounds.union(box);
        return profile.donorDoor==null?bounds:bounds.union(doorBounds());
    }
    @Override public double getMaxRenderDistanceSquared() { return 16384; }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);if(owner!=null)tag.setUniqueId("Owner",owner);
        tag.setString("Profile",profile.id);tag.setInteger("Facing",facing.getHorizontalIndex());
        tag.setFloat("Progress",OriginalGateProfile.clamp(progress));tag.setBoolean("Ready",ready);tag.setInteger("LayoutVersion",layoutReady?(profile.donorGermo!=null?4:profile.donorDoor==null?2:3):0);
        if(profile.donorGermo!=null)tag.setTag("DonorGermo",DonorGermoCollision.save(germo()));
        if(customDoorBounds!=null)for(int i=0;i<6;i++)tag.setDouble(AABB_KEYS[i],customDoorBounds[i]);
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
        customDoorBounds=null;
        if(profile.donorDoor!=null && tag.hasKey(AABB_KEYS[0],99)) {
            double[] candidate=new double[6];boolean complete=true;
            for(int i=0;i<6;i++){complete &= tag.hasKey(AABB_KEYS[i],99);candidate[i]=tag.getDouble(AABB_KEYS[i]);}
            if(complete && DonorDoorCollision.valid(candidate))customDoorBounds=candidate;
        }
        germo=profile.donorGermo==null || !tag.hasKey("DonorGermo",10)?null:DonorGermoCollision.restore(profile.id,facing,tag.getCompoundTag("DonorGermo"));
        layoutReady=tag.getInteger("LayoutVersion")>=(profile.donorGermo!=null?4:profile.donorDoor==null?2:3) && (profile.donorGermo==null || germo!=null);needsPartSync=true;removing=false;invalidateGeometry();OriginalDecorCollisions.track(this);
    }
    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos,0,getUpdateTag()); }
    @Override public void onDataPacket(NetworkManager manager,SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); }
}
