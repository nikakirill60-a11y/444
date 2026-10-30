package ru.metrocraft.port.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import ru.metrocraft.port.MetroPort;

public final class OpenInventoryPacket implements IMessage {
    @Override public void toBytes(ByteBuf buf) { }
    @Override public void fromBytes(ByteBuf buf) { }
    public static final class Handler implements IMessageHandler<OpenInventoryPacket,IMessage> {
        @Override
        public IMessage onMessage(OpenInventoryPacket packet,MessageContext context) {
            EntityPlayerMP player=context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(()->{
                long now=player.world.getTotalWorldTime();
                if(player.isEntityAlive() && !player.isSpectator() && !(player.openContainer instanceof MetroContainer)
                        && now-player.getEntityData().getLong("MetroLastInventoryOpen")>=3) {
                    player.getEntityData().setLong("MetroLastInventoryOpen",now);
                    player.openGui(MetroPort.INSTANCE,0,player.world,0,0,0);
                }
            });
            return null;
        }
    }
}
