package ru.metrocraft.port.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public final class NvgSyncPacket implements IMessage {
    private int entityId;
    private ItemStack stack=ItemStack.EMPTY;
    public NvgSyncPacket() { }
    public NvgSyncPacket(int entityId,ItemStack stack) { this.entityId=entityId; this.stack=stack.copy(); }
    @Override public void toBytes(ByteBuf buf) { buf.writeInt(entityId); ByteBufUtils.writeItemStack(buf,stack); }
    @Override public void fromBytes(ByteBuf buf) { entityId=buf.readInt(); stack=ByteBufUtils.readItemStack(buf); }
    public static final class Handler implements IMessageHandler<NvgSyncPacket,IMessage> {
        @Override
        public IMessage onMessage(NvgSyncPacket message,MessageContext context) {
            ru.metrocraft.port.client.NvgLayer.receive(message.entityId,message.stack);
            return null;
        }
    }
}
