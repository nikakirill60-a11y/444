package ru.metrocraft.mwf;

import com.modularwarfare.common.network.PacketBase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/** Only a hotbar slot is transmitted. Pressure, gain, cooldown and weapon come from the server. */
public final class PacketMetroPump extends PacketBase {
    private int slot;
    public PacketMetroPump() { this.slot=255; }
    public PacketMetroPump(int slot) { this.slot=slot; }
    @Override public void encodeInto(ChannelHandlerContext context,ByteBuf data) {data.writeByte(slot);}
    @Override public void decodeInto(ChannelHandlerContext context,ByteBuf data) {slot=data.readUnsignedByte();}
    @Override public void handleServerSide(EntityPlayerMP player) {
        // MWF drains receivedPacketsServer from its server-tick handler, not Netty's thread.
        if(slot>=0&&slot<=8)MechanicsHooks.pump(player,slot);
    }
    @Override public void handleClientSide(EntityPlayer player) { /* no server-to-client operation */ }
}
