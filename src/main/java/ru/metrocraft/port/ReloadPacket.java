package ru.metrocraft.port;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** No client-supplied ammo, item ID, or damage is trusted. */
public final class ReloadPacket implements IMessage {
    @Override public void fromBytes(ByteBuf buf) { }
    @Override public void toBytes(ByteBuf buf) { }

    public static final class Handler implements IMessageHandler<ReloadPacket, IMessage> {
        @Override
        public IMessage onMessage(ReloadPacket message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack held = player.getHeldItemMainhand();
                if (held.getItem() instanceof GunItem && !player.isSpectator()) {
                    ((GunItem) held.getItem()).startReload(held, player);
                }
            });
            return null;
        }
    }
}
