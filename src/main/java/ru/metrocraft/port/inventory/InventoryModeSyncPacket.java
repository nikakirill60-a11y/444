package ru.metrocraft.port.inventory;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import ru.metrocraft.port.MetroPort;
public final class InventoryModeSyncPacket implements IMessage {
    private boolean vanilla,open;
    public InventoryModeSyncPacket(){}
    public InventoryModeSyncPacket(boolean vanilla,boolean open){this.vanilla=vanilla;this.open=open;}
    @Override public void toBytes(ByteBuf buf){buf.writeBoolean(vanilla);buf.writeBoolean(open);}
    @Override public void fromBytes(ByteBuf buf){vanilla=buf.readBoolean();open=buf.readBoolean();}
    public static final class Handler implements IMessageHandler<InventoryModeSyncPacket,IMessage>{
        @Override public IMessage onMessage(InventoryModeSyncPacket packet,MessageContext context){
            MetroPort.PROXY.inventoryMode(packet.vanilla,packet.open);return null;
        }
    }
}
