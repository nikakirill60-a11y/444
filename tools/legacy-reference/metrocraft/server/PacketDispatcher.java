/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.common.network.NetworkRegistry$TargetPoint
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 */
package metrocraft.server;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import metrocraft.server.AbstractMessage;
import metrocraft.server.clientreg.SyncEquipBackMessage;
import metrocraft.server.clientreg.SyncPlayerPropsMessage;
import metrocraft.server.serverreg.OpenGuiMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketDispatcher {
    private static byte packetId = 0;
    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel("dd-dayz");

    public static final void registerPackets() {
        PacketDispatcher.registerMessage(SyncPlayerPropsMessage.class);
        PacketDispatcher.registerMessage(SyncEquipBackMessage.class);
        PacketDispatcher.registerMessage(OpenGuiMessage.class);
    }

    private static final void registerMessage(Class clazz) {
        if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
            dispatcher.registerMessage(clazz, clazz, (int)packetId, Side.CLIENT);
        } else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
            byte by = packetId;
            packetId = (byte)(by + 1);
            dispatcher.registerMessage(clazz, clazz, (int)by, Side.SERVER);
        } else {
            dispatcher.registerMessage(clazz, clazz, (int)packetId, Side.CLIENT);
            byte by = packetId;
            packetId = (byte)(by + 1);
            dispatcher.registerMessage(clazz, clazz, (int)by, Side.SERVER);
        }
    }

    public static final void sendTo(IMessage message, EntityPlayerMP player) {
        dispatcher.sendTo(message, player);
    }

    public static void sendToAll(IMessage message) {
        dispatcher.sendToAll(message);
    }

    public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        dispatcher.sendToAllAround(message, point);
    }

    public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
        PacketDispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
    }

    public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
        PacketDispatcher.sendToAllAround(message, player.field_70170_p.field_73011_w.field_76574_g, player.field_70165_t, player.field_70163_u, player.field_70161_v, range);
    }

    public static final void sendToDimension(IMessage message, int dimensionId) {
        dispatcher.sendToDimension(message, dimensionId);
    }

    public static final void sendToServer(OpenGuiMessage openGuiMessage) {
        dispatcher.sendToServer((IMessage)openGuiMessage);
    }
}

