package ru.metrocraft.port.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;

/** No item stacks/NBT supplied by the client. Window + monotonic revision prevent stale/replayed clicks. */
public final class GridClickPacket implements IMessage {
    private int window,revision,cell,button,action;private boolean rotated;
    public GridClickPacket(){}
    public GridClickPacket(int window,int revision,int cell,int button,boolean rotated,int action){
        this.window=window;this.revision=revision;this.cell=cell;this.button=button;this.rotated=rotated;this.action=action;
    }
    @Override public void toBytes(ByteBuf b){b.writeInt(window);b.writeInt(revision);b.writeInt(cell);b.writeByte(button);b.writeBoolean(rotated);b.writeByte(action);}
    @Override public void fromBytes(ByteBuf b){window=b.readInt();revision=b.readInt();cell=b.readInt();button=b.readByte();rotated=b.readBoolean();action=b.readByte();}
    public static final class Handler implements IMessageHandler<GridClickPacket,IMessage>{
        @Override public IMessage onMessage(GridClickPacket message,MessageContext context){
            EntityPlayerMP p=context.getServerHandler().player;
            p.getServerWorld().addScheduledTask(()->{
                if(p.openContainer instanceof MetroContainer){
                    MetroContainer c=(MetroContainer)p.openContainer;
                    c.gridClick(p,message.window,message.revision,message.cell,message.button,message.rotated,message.action);
                }
            });return null;
        }
    }
}
