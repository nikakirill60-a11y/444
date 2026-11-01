package ru.metrocraft.port.inventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;
public final class WatchSyncPacket implements IMessage {
    private int dimension,entityId,level;
    private ItemStack stack=ItemStack.EMPTY;
    public WatchSyncPacket(){}
    public WatchSyncPacket(EntityPlayer player,ItemStack stack){dimension=player.dimension;entityId=player.getEntityId();level=ru.metrocraft.port.radiation.RadiationSystem.level(player);this.stack=stack.copy();}
    @Override public void toBytes(ByteBuf buf){buf.writeInt(dimension);buf.writeInt(entityId);buf.writeByte(level);ByteBufUtils.writeItemStack(buf,stack);}
    @Override public void fromBytes(ByteBuf buf){dimension=buf.readInt();entityId=buf.readInt();level=Math.max(0,Math.min(3,buf.readUnsignedByte()));stack=ByteBufUtils.readItemStack(buf);}
    public static final class Handler implements IMessageHandler<WatchSyncPacket,IMessage> {
        @Override public IMessage onMessage(WatchSyncPacket packet,MessageContext context){
            ru.metrocraft.port.client.WatchClient.receive(packet.dimension,packet.entityId,packet.stack,packet.level);return null;
        }
    }
}
