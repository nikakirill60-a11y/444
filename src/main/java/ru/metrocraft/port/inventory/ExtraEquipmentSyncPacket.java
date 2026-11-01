package ru.metrocraft.port.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

/** Server -> client only. A dimension/entity snapshot, never a client equipment mutation request. */
public final class ExtraEquipmentSyncPacket implements IMessage {
    private int dimension,entity,kind;private ItemStack stack=ItemStack.EMPTY;
    public ExtraEquipmentSyncPacket(){}
    public ExtraEquipmentSyncPacket(EntityPlayer p,ExtraEquipment.Kind kind,ItemStack stack){dimension=p.dimension;entity=p.getEntityId();this.kind=kind.ordinal();this.stack=stack.copy();}
    @Override public void toBytes(ByteBuf buf){buf.writeInt(dimension);buf.writeInt(entity);buf.writeByte(kind);ByteBufUtils.writeItemStack(buf,stack);}
    @Override public void fromBytes(ByteBuf buf){dimension=buf.readInt();entity=buf.readInt();kind=buf.readUnsignedByte();stack=ByteBufUtils.readItemStack(buf);}
    public static final class Handler implements IMessageHandler<ExtraEquipmentSyncPacket,IMessage>{
        @Override public IMessage onMessage(ExtraEquipmentSyncPacket m,MessageContext ctx){
            net.minecraft.client.Minecraft mc=net.minecraft.client.Minecraft.getMinecraft();net.minecraft.client.multiplayer.WorldClient expected=mc.world;
            mc.addScheduledTask(()->{
                if(expected==null || mc.world!=expected || expected.provider.getDimension()!=m.dimension || m.kind>=ExtraEquipment.Kind.values().length)return;
                net.minecraft.entity.Entity entity=expected.getEntityByID(m.entity);
                if(entity instanceof EntityPlayer)ExtraEquipment.receive((EntityPlayer)entity,ExtraEquipment.Kind.values()[m.kind],m.stack);
            });return null;
        }
    }
}
