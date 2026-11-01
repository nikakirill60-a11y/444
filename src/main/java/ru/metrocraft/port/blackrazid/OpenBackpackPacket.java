package ru.metrocraft.port.blackrazid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.MetroContainer;
public final class OpenBackpackPacket implements IMessage {
    @Override public void toBytes(ByteBuf buf){} @Override public void fromBytes(ByteBuf buf){}
    public static final class Handler implements IMessageHandler<OpenBackpackPacket,IMessage>{
        @Override public IMessage onMessage(OpenBackpackPacket packet,MessageContext ctx){
            EntityPlayerMP p=ctx.getServerHandler().player;
            p.getServerWorld().addScheduledTask(()->{
                if(p.isEntityAlive() && p.openContainer instanceof MetroContainer && p.inventory.getItemStack().isEmpty() && BackpackContainer.valid(p,2))
                    p.openGui(MetroPort.INSTANCE,2,p.world,2,0,0);
            });return null;
        }
    }
}
