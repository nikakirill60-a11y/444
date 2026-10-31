package ru.metrocraft.port;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public final class ShotFeedbackPacket implements IMessage {
    private String gun;private float pitch,yaw;
    public ShotFeedbackPacket(){}
    public ShotFeedbackPacket(String gun,float pitch,float yaw){this.gun=gun;this.pitch=pitch;this.yaw=yaw;}
    @Override public void toBytes(ByteBuf buf){ByteBufUtils.writeUTF8String(buf,gun);buf.writeFloat(pitch);buf.writeFloat(yaw);}
    @Override public void fromBytes(ByteBuf buf){gun=ByteBufUtils.readUTF8String(buf);pitch=buf.readFloat();yaw=buf.readFloat();}
    public static final class Handler implements IMessageHandler<ShotFeedbackPacket,IMessage>{
        @Override public IMessage onMessage(ShotFeedbackPacket packet,MessageContext context){
            MetroPort.PROXY.shotFeedback(packet.gun,packet.pitch,packet.yaw);return null;
        }
    }
}
