package ru.metrocraft.mwf;

import com.modularwarfare.common.network.PacketBase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/** Client sends only trigger + hotbar slot. Server emits authoritative visual samples. */
public final class PacketMetroRotor extends PacketBase {
    int command=255,slot=255,entity,speed,angle;
    public PacketMetroRotor(){}
    public PacketMetroRotor(int slot,boolean pressed){this.command=pressed?1:0;this.slot=slot;}
    public PacketMetroRotor(int entity,int speed,int angle){this.command=2;this.entity=entity;this.speed=speed;this.angle=angle;}
    @Override public void encodeInto(ChannelHandlerContext c,ByteBuf b){b.writeByte(command);b.writeByte(slot);b.writeInt(entity);b.writeInt(speed);b.writeInt(angle);}
    @Override public void decodeInto(ChannelHandlerContext c,ByteBuf b){command=b.readByte()&255;slot=b.readByte()&255;entity=b.readInt();speed=b.readInt();angle=b.readInt();}
    @Override public void handleServerSide(EntityPlayerMP p){if(command<=1&&slot<=8)MotionHooks.trigger(p,slot,command==1);}
    @Override public void handleClientSide(EntityPlayer p){
        if(command!=2||speed<0||speed>1000||angle<0||angle>=360000)return;
        try{MechanicsHooks.call(Class.forName("ru.metrocraft.mwf.MotionClient"),"sample",entity,speed,angle);}
        catch(Exception e){throw new IllegalStateException("Rotor visual sync failed",e);}
    }
}
