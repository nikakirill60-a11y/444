package ru.metrocraft.port.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

/** Owner-only telemetry, independent of the existing equipment item packet. */
public final class NvgStatePacket implements IMessage {
    private int dimension,entityId;
    private boolean active;
    public NvgStatePacket() {}
    public NvgStatePacket(int dimension,int entityId,boolean active){this.dimension=dimension;this.entityId=entityId;this.active=active;}
    @Override public void toBytes(ByteBuf buf){buf.writeInt(dimension);buf.writeInt(entityId);buf.writeBoolean(active);}
    @Override public void fromBytes(ByteBuf buf){dimension=buf.readInt();entityId=buf.readInt();active=buf.readBoolean();}
    public static final class Handler implements IMessageHandler<NvgStatePacket,IMessage> {
        @Override public IMessage onMessage(NvgStatePacket message,MessageContext context){
            ru.metrocraft.port.client.NvgEffects.receive(message.dimension,message.entityId,message.active);return null;
        }
    }
}
