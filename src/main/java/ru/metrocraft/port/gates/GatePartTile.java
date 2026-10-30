package ru.metrocraft.port.gates;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;

/** Persistent ownership, not a guess based on neighbouring air/block types. */
public final class GatePartTile extends TileEntity {
    public BlockPos origin = BlockPos.ORIGIN;
    public UUID owner;
    public void configure(BlockPos pos, UUID owner) { this.origin = pos.toImmutable(); this.owner = owner; markDirty(); }
    public GateTile controller() {
        if (world == null || owner == null || !world.isBlockLoaded(origin)) return null;
        TileEntity tile = world.getTileEntity(origin);
        return tile instanceof GateTile && owner.equals(((GateTile) tile).owner) ? (GateTile) tile : null;
    }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag); tag.setLong("Origin", origin.toLong());
        if (owner != null) tag.setUniqueId("Owner", owner); return tag;
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag); origin = BlockPos.fromLong(tag.getLong("Origin"));
        owner = tag.hasUniqueId("Owner") ? tag.getUniqueId("Owner") : null;
    }
    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 0, getUpdateTag()); }
    @Override public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); }
}
