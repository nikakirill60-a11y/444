package ru.metrocraft.port.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;

/** Empty request. A client cannot supply the player, slot, item, effect duration or enabled state. */
public final class NvgTogglePacket implements IMessage {
    @Override public void toBytes(ByteBuf buf) {}
    @Override public void fromBytes(ByteBuf buf) {}
    public static final class Handler implements IMessageHandler<NvgTogglePacket,IMessage> {
        @Override public IMessage onMessage(NvgTogglePacket message,MessageContext context) {
            EntityPlayerMP player=context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(()->NvgControl.toggle(player));return null;
        }
    }
}
