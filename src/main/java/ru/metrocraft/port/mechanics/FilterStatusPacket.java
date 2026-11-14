package ru.metrocraft.port.mechanics;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

/** Dedicated S2C status packet; existing watch item/NBT wire format stays unchanged. */
public final class FilterStatusPacket implements IMessage {
    private int dimension, entityId, ticks = -1;
    public FilterStatusPacket() {}
    public FilterStatusPacket(int dimension, int entityId, int ticks) {
        this.dimension = dimension; this.entityId = entityId; this.ticks = FilterRules.sanitize(ticks);
    }
    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(dimension); buf.writeInt(entityId); buf.writeInt(FilterRules.sanitize(ticks));
    }
    @Override public void fromBytes(ByteBuf buf) {
        dimension = buf.readInt(); entityId = buf.readInt(); ticks = FilterRules.sanitize(buf.readInt());
    }
    public static final class Handler implements IMessageHandler<FilterStatusPacket,IMessage> {
        @Override public IMessage onMessage(FilterStatusPacket packet, MessageContext context) {
            ru.metrocraft.port.client.FilterWatchClient.receive(packet.dimension, packet.entityId, packet.ticks);
            return null;
        }
    }
}
