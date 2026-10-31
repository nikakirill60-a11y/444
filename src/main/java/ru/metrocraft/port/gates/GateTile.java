package ru.metrocraft.port.gates;

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

public final class GateTile extends TileEntity implements ITickable {
    public UUID owner;
    public GateType type = GateType.MAIN;
    public EnumFacing facing = EnumFacing.SOUTH;
    public float progress, previous;
    private float target;
    private boolean powered, blocked;
    public boolean ready, removing;
    private int ticks;
    private boolean structureLoaded;

    public void configure(UUID owner, GateType type, EnumFacing facing) {
        this.owner = owner; this.type = type; this.facing = facing;
        // Asset names such as "open" are variants, not a second source of control.
        progress = previous = target = 0; powered = false; structureLoaded = false; markDirty();
    }
    static float redstoneTarget(boolean signal) { return signal ? 1 : 0; }
    private void setTarget(float next) {
        if (next == target) return;
        target = next; markDirty();
        net.minecraft.util.SoundEvent sound = ru.metrocraft.port.MetroPort.SOUNDS.get("germo");
        if (sound != null) world.playSound(null, pos, sound, SoundCategory.BLOCKS, .4F, 1);
    }
    @Override public void update() {
        previous = progress;
        if (!ready || removing || owner == null) return;
        ++ticks;
        if (!world.isRemote) {
            boolean signal = false;
            for (BlockPos cell : Gates.footprint(pos, facing, type)) {
                if (!world.isBlockLoaded(cell)) {
                    if (structureLoaded || !blocked || powered) {
                        structureLoaded = false; blocked = true; powered = false; target = 0; sync();
                    }
                    return;
                }
                if (cell.equals(pos)) continue;
                TileEntity part = world.getTileEntity(cell);
                if (!(part instanceof GatePartTile) || !owner.equals(((GatePartTile) part).owner)) {
                    world.destroyBlock(pos, true); return;
                }
                signal |= world.isBlockPowered(cell);
            }
            structureLoaded = true;
            signal |= world.isBlockPowered(pos);
            boolean changed = signal != powered;
            powered = signal;
            setTarget(redstoneTarget(signal));
            if (changed) { markDirty(); sync(); }
        }
        if (!world.isRemote && !structureLoaded) return;
        float next = progress + Math.signum(target - progress) * Math.min(.015F, Math.abs(target - progress));
        if (!world.isRemote) {
            boolean stop = next != progress && blockedAt(next);
            if (stop != blocked) { blocked = stop; sync(); }
        }
        if (!blocked && next != progress) {
            progress = MathHelper.clamp(next, 0, 1);
            if (!world.isRemote) { markDirty(); if (ticks % 4 == 0 || progress == target) sync(); }
        }
    }
    /** Prevent the moving leaf crushing players/mobs, in either direction. */
    public boolean blockedAt(float value) {
        for (AxisAlignedBB box : boxes(value)) for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, box))
            if (entity instanceof EntityLivingBase && entity.isEntityAlive() && !entity.noClip
                    && !(entity instanceof net.minecraft.entity.player.EntityPlayer
                         && ((net.minecraft.entity.player.EntityPlayer) entity).isSpectator())) return true;
        return false;
    }
    public void sync() {
        IBlockState state = world.getBlockState(pos); world.notifyBlockUpdate(pos, state, state, 2);
    }
    public void removeParts() {
        if (removing || owner == null) return;
        removing = true;
        for (BlockPos cell : Gates.footprint(pos, facing, type)) {
            if (cell.equals(pos) || !world.isBlockLoaded(cell)) continue;
            TileEntity tile = world.getTileEntity(cell);
            if (tile instanceof GatePartTile && owner.equals(((GatePartTile) tile).owner)) world.setBlockToAir(cell);
        }
    }
    public static AxisAlignedBB closedBounds(BlockPos pos, EnumFacing facing) {
        return localBox(pos, facing, -2.5, 0, -.5, 2.5, 4, .5);
    }
    private static AxisAlignedBB localBox(BlockPos pos, EnumFacing facing, double w0, double y0, double d0,
                                          double w1, double y1, double d1) {
        EnumFacing right = facing.rotateYCCW();
        double x0 = pos.getX() + .5 + right.getXOffset() * w0 + facing.getXOffset() * d0;
        double z0 = pos.getZ() + .5 + right.getZOffset() * w0 + facing.getZOffset() * d0;
        double x1 = pos.getX() + .5 + right.getXOffset() * w1 + facing.getXOffset() * d1;
        double z1 = pos.getZ() + .5 + right.getZOffset() * w1 + facing.getZOffset() * d1;
        return new AxisAlignedBB(x0, pos.getY() + y0, z0, x1, pos.getY() + y1, z1);
    }
    private List<AxisAlignedBB> boxes(float value) {
        List<AxisAlignedBB> result = new ArrayList<>();
        boolean obj = type == GateType.HINGED;
        double half = obj ? 2 : 2.5, edge = obj ? .18 : .08;
        result.add(localBox(pos, facing, -half, 0, -.5, -half + edge, 4, .5));
        result.add(localBox(pos, facing, half - edge, 0, -.5, half, 4, .5));
        result.add(localBox(pos, facing, -half, 3.9, -.5, half, 4, .5));
        if (!type.swing) {
            result.add(localBox(pos, facing, -2.45, 4 * value, -.35, 2.45, 4 + 4 * value, .35));
        } else {
            // Narrow slices approximate the rotated leaf, not one huge diagonal AABB
            // that would incorrectly block the whole doorway while it is opening.
            double angle = Math.toRadians(90 * value), cos = Math.cos(angle), sin = Math.sin(angle);
            double z0 = obj ? -.667083 : -2.5, z1 = obj ? 3.388717 : 2.5;
            double x0 = obj ? -.631234 : -.56, x1 = obj ? 0 : .56;
            double pivot = obj ? 0 : -2.5, wShift = obj ? -1.638717 : 0, dShift = obj ? .426667 : 0;
            for (int i = 0; i < 24; i++) {
                double a = z0 + (z1 - z0) * i / 24, b = z0 + (z1 - z0) * (i + 1) / 24;
                double minW = Double.POSITIVE_INFINITY, maxW = -minW, minD = minW, maxD = -minW;
                for (double z : new double[]{a, b}) for (double x : new double[]{x0, x1}) {
                    double w = pivot + (z - pivot) * cos - x * sin + wShift;
                    double d = (z - pivot) * sin + x * cos + dShift;
                    minW = Math.min(minW, w); maxW = Math.max(maxW, w);
                    minD = Math.min(minD, d); maxD = Math.max(maxD, d);
                }
                result.add(localBox(pos, facing, minW, obj ? .25 : 0, minD, maxW, obj ? 3.75 : 4, maxD));
            }
        }
        return result;
    }
    public void addCollision(BlockPos cell, AxisAlignedBB query, List<AxisAlignedBB> result) {
        AxisAlignedBB cellBox = new AxisAlignedBB(cell);
        for (AxisAlignedBB box : boxes(progress)) if (box.intersects(cellBox)) {
            AxisAlignedBB clipped = box.intersect(cellBox);
            if (clipped.intersects(query)) result.add(clipped);
        }
    }
    public RayTraceResult rayTrace(BlockPos cell, Vec3d start, Vec3d end) {
        RayTraceResult closest = null; double distance = Double.POSITIVE_INFINITY;
        AxisAlignedBB cellBox = new AxisAlignedBB(cell);
        for (AxisAlignedBB box : boxes(progress)) if (box.intersects(cellBox)) {
            RayTraceResult hit = box.intersect(cellBox).calculateIntercept(start, end);
            if (hit != null && hit.hitVec.squareDistanceTo(start) < distance) {
                distance = hit.hitVec.squareDistanceTo(start);
                closest = new RayTraceResult(hit.hitVec, hit.sideHit, cell);
            }
        }
        return closest;
    }
    @Override public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-7, 0, -7), pos.add(8, 9, 8));
    }
    @Override public double getMaxRenderDistanceSquared() { return 16384; }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (owner != null) tag.setUniqueId("Owner", owner);
        tag.setString("Type", type.name()); tag.setInteger("Facing", facing.getHorizontalIndex());
        tag.setFloat("Progress", progress);
        tag.removeTag("Manual"); tag.removeTag("Target"); // Migrate old saves; never persist manual intent.
        tag.setBoolean("Powered", powered); tag.setBoolean("Blocked", blocked); tag.setBoolean("Ready", ready); return tag;
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        owner = tag.hasUniqueId("Owner") ? tag.getUniqueId("Owner") : null;
        try { type = GateType.valueOf(tag.getString("Type")); } catch (IllegalArgumentException e) { type = GateType.MAIN; }
        facing = GateBlock.horizontal(tag.getInteger("Facing"));
        previous = progress = MathHelper.clamp(tag.getFloat("Progress"), 0, 1);
        // Ignore legacy Manual/Target. Server rechecks the entire loaded structure BEFORE moving.
        powered = tag.getBoolean("Powered"); target = redstoneTarget(powered);
        blocked = tag.getBoolean("Blocked"); ready = tag.getBoolean("Ready");
        structureLoaded = false;
    }
    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 0, getUpdateTag()); }
    @Override public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); }
}
