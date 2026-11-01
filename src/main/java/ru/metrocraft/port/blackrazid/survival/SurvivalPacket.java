package ru.metrocraft.port.blackrazid.survival;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import ru.metrocraft.port.MetroPort;

/** Owner-only server snapshot. There is intentionally no client-set-water/energy packet. */
public final class SurvivalPacket implements IMessage {
    public int dimension,entity;public float water,energy;public boolean lamp;
    public SurvivalPacket(){}
    public SurvivalPacket(EntityPlayer p,SurvivalState s){dimension=p.dimension;entity=p.getEntityId();water=s.water;energy=s.energy;lamp=s.lamp;}
    @Override public void toBytes(ByteBuf b){b.writeInt(dimension);b.writeInt(entity);b.writeFloat(water);b.writeFloat(energy);b.writeBoolean(lamp);}
    @Override public void fromBytes(ByteBuf b){dimension=b.readInt();entity=b.readInt();water=SurvivalState.percent(b.readFloat(),100);energy=SurvivalState.percent(b.readFloat(),0);lamp=b.readBoolean();}
    public static final class Handler implements IMessageHandler<SurvivalPacket,IMessage>{
        @Override public IMessage onMessage(SurvivalPacket p,MessageContext c){MetroPort.PROXY.survival(p.dimension,p.entity,p.water,p.energy,p.lamp);return null;}
    }
}
