package ru.metrocraft.port.inventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;
public final class InventoryModePacket implements IMessage {
    private int mode;
    public InventoryModePacket(){}
    public InventoryModePacket(boolean vanilla){mode=vanilla?1:0;}
    @Override public void toBytes(ByteBuf buf){buf.writeByte(mode);}
    @Override public void fromBytes(ByteBuf buf){mode=buf.readUnsignedByte();}
    public static final class Handler implements IMessageHandler<InventoryModePacket,IMessage>{
        @Override public IMessage onMessage(InventoryModePacket packet,MessageContext context){
            EntityPlayerMP player=context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(()->InventoryModes.switchTo(player,packet.mode));return null;
        }
    }
}
