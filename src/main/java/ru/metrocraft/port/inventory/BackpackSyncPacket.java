package ru.metrocraft.port.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public final class BackpackSyncPacket implements IMessage {
    private int entityId;
    private ItemStack stack=ItemStack.EMPTY;
    public BackpackSyncPacket() { }
    public BackpackSyncPacket(int entityId,ItemStack stack) { this.entityId=entityId; this.stack=stack.copy(); }
    @Override public void toBytes(ByteBuf buf) { buf.writeInt(entityId); ByteBufUtils.writeItemStack(buf,stack); }
    @Override public void fromBytes(ByteBuf buf) { entityId=buf.readInt(); stack=ByteBufUtils.readItemStack(buf); }
    public static final class Handler implements IMessageHandler<BackpackSyncPacket,IMessage> {
        @Override
        public IMessage onMessage(BackpackSyncPacket message,MessageContext context) {
            ru.metrocraft.port.client.BackpackClient.receive(message.entityId,message.stack);
            return null;
        }
    }
}
