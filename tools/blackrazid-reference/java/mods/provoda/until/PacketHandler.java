/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aa
 *  ab
 *  cg
 *  cpw.mods.fml.common.network.IPacketHandler
 *  cpw.mods.fml.common.network.PacketDispatcher
 *  cpw.mods.fml.common.network.Player
 *  dk
 *  ei
 *  mods.provoda.until.ClientTickHandler
 *  mods.provoda.until.ExtendetPlayer
 *  net.minecraft.server.MinecraftServer
 *  sq
 *  t
 *  wm
 */
package mods.provoda.until;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.provoda.ModBase;
import mods.provoda.entity.EntityDrezinaBase;
import mods.provoda.until.ClientTickHandler;
import mods.provoda.until.ExtendetPlayer;
import net.minecraft.server.MinecraftServer;

public class PacketHandler
implements IPacketHandler,
ab {
    public static final byte EXTENDED_PROPERTIES = 1;
    public static final byte OPEN_SERVER_GUI = 2;
    public static final byte KeyRPressed = 3;
    public static final byte KeyRUnpressed = 4;
    public static final byte KeyXPressed = 5;
    public static final byte KeyXUnpressed = 6;

    public void onPacketData(cg manager, dk packet, Player player) {
        byte packetType;
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.c));
        try {
            packetType = inputStream.readByte();
        }
        catch (IOException var7) {
            var7.printStackTrace();
            return;
        }
        if (packet.a.equals("tutchannel")) {
            switch (packetType) {
                case 3: {
                    this.handleReloadKeyPressed(packet, (sq)player, inputStream);
                    break;
                }
                case 4: {
                    this.handleReloadKeyUnpressed(packet, (sq)player, inputStream);
                    break;
                }
                case 5: {
                    this.handleLampKeyPressed(packet, (sq)player, inputStream);
                    break;
                }
                case 6: {
                    this.handleLampKeyUnpressed(packet, (sq)player, inputStream);
                    break;
                }
                case 7: {
                    this.handleOpenGuiPacketRailgun(packet, (sq)player, inputStream);
                    break;
                }
                case 8: {
                    this.handleOpenGuiPacketRevolver(packet, (sq)player, inputStream);
                    break;
                }
                case 9: {
                    this.handleOpenGuiPacketInventory(packet, (sq)player, inputStream);
                    break;
                }
                case 10: {
                    this.handleOpenGuiPacketBackPack(packet, (sq)player, inputStream);
                    break;
                }
                case 11: {
                    this.handleOpenGuiPacketModifi(packet, (sq)player, inputStream);
                    break;
                }
                case 12: {
                    this.handleMed(packet, (sq)player, inputStream);
                    break;
                }
                case 13: {
                    this.handleTestForMed(packet, (sq)player, inputStream);
                    break;
                }
                case 14: {
                    this.handleShootPress(packet, (sq)player, inputStream);
                    break;
                }
                case 15: {
                    this.handleShootUnpess(packet, (sq)player, inputStream);
                    break;
                }
                case 16: {
                    this.handleGetWater(packet, (sq)player, inputStream);
                    break;
                }
                case 17: {
                    this.handleSendCommand(packet, (sq)player, inputStream);
                    break;
                }
                case 18: {
                    this.handleSpeedUpPress(packet, (sq)player, inputStream);
                    break;
                }
                case 19: {
                    this.handleOpenGuiFaction(packet, (sq)player, inputStream);
                    break;
                }
                case 20: {
                    this.handleSpeedDownPress(packet, (sq)player, inputStream);
                    break;
                }
                case 21: {
                    this.handleSetFaction(packet, (sq)player, inputStream);
                    break;
                }
                case 22: {
                    this.handleSpeedStopPress(packet, (sq)player, inputStream);
                    break;
                }
                case 23: {
                    this.handleCreateClanEvent(packet, (sq)player, inputStream);
                    break;
                }
                case 24: {
                    this.handleRequestClanList(packet, (sq)player, inputStream);
                    break;
                }
                default: {
                    System.out.println("[PACKET][WARNING] Unknown packet type " + packetType);
                }
            }
        }
    }

    private void handleRequestClanList(dk packet, sq p, DataInputStream inputStream) {
    }

    private void handleCreateClanEvent(dk packet, sq player, DataInputStream inputStream) {
    }

    private void handleSendCommand(dk var1, sq p, DataInputStream var3) {
        try {
            int e = var3.readInt();
            MinecraftServer minecraftserver = MinecraftServer.D();
            aa icommandmanager = minecraftserver.E();
            if (e == 0) {
                icommandmanager.a((ab)this, "pex user " + p.bS + " " + "group add Red");
            } else if (e == 1) {
                icommandmanager.a((ab)this, "pex user " + p.bS + " " + "group add Red");
            } else if (e == 2) {
                icommandmanager.a((ab)this, "pex user " + p.bS + " " + "group add Nazi");
            } else if (e == 3) {
                icommandmanager.a((ab)this, "pex user " + p.bS + " " + "group add Arbatkonf");
            } else if (e == 4) {
                icommandmanager.a((ab)this, "pex user " + p.bS + " " + "group add Ganza");
            } else if (e == 5) {
                icommandmanager.a((ab)this, "pex user " + p.bS + " " + "group add Konfyear");
            } else if (e == 6) {
                icommandmanager.a((ab)this, "pex user " + p.bS + " " + "group add VDNH");
            } else if (e == 7) {
                icommandmanager.a((ab)this, "pex user " + p.bS + " " + "group add Prest");
            }
        }
        catch (IOException var7) {
            var7.printStackTrace();
        }
    }

    private void handleSetFaction(dk var1, sq p, DataInputStream var3) {
        try {
            int e = var3.readInt();
            ExtendetPlayer ep = ExtendetPlayer.get((sq)p);
            ep.setFaction(e);
        }
        catch (IOException var6) {
            var6.printStackTrace();
        }
    }

    private void handleOpenGuiFaction(dk var1, sq p, DataInputStream var3) {
        ClientTickHandler.guiFaction = true;
    }

    private void handleGetWater(dk packet, sq p, DataInputStream i) {
        ExtendetPlayer ep = ExtendetPlayer.get((sq)p);
    }

    private void handleMed(dk var1, sq var2, DataInputStream var3) {
        if (var2.bK.a[13] != null && var2.bK.a[13].b() == ModBase.Med) {
            wm apt = var2.bK.a[13].m();
            int cur = var2.bK.c;
            if (var2.bK.h() != null) {
                wm ep = var2.bK.h().m();
                --var2.bK.a[13].a;
                var2.bK.a[13] = ep;
            }
            var2.bK.a[cur] = apt;
            ExtendetPlayer ep1 = ExtendetPlayer.get((sq)var2);
            ep1.aptTimer();
        }
    }

    private void handleTestForMed(dk var1, sq var2, DataInputStream var3) {
        if (var2.bK.a[13] != null && var2.bK.a[13].b() != ModBase.Med) {
            var2.bK.a[13] = null;
        }
    }

    private void handleOpenGuiPacketRailgun(dk var1, sq var2, DataInputStream var3) {
        var2.openGui((Object)ModBase.instance, 2, var2.q, (int)var2.u, (int)var2.v, (int)var2.w);
    }

    private void handleOpenGuiPacketRevolver(dk var1, sq var2, DataInputStream var3) {
        var2.openGui((Object)ModBase.instance, 3, var2.q, (int)var2.u, (int)var2.v, (int)var2.w);
    }

    private void handleOpenGuiPacketInventory(dk var1, sq var2, DataInputStream var3) {
        var2.openGui((Object)ModBase.instance, 4, var2.q, (int)var2.u, (int)var2.v, (int)var2.w);
    }

    private void handleOpenGuiPacketBackPack(dk var1, sq var2, DataInputStream var3) {
        var2.openGui((Object)ModBase.instance, 5, var2.q, (int)var2.u, (int)var2.v, (int)var2.w);
    }

    private void handleOpenGuiPacketModifi(dk var1, sq var2, DataInputStream var3) {
        var2.openGui((Object)ModBase.instance, 6, var2.q, (int)var2.u, (int)var2.v, (int)var2.w);
    }

    private void handleReloadKeyPressed(dk packet, sq player, DataInputStream inputStream) {
        ExtendetPlayer ep = ExtendetPlayer.get((sq)player);
        ep.setReloadKeyPressed();
    }

    private void handleReloadKeyUnpressed(dk packet, sq player, DataInputStream inputStream) {
        ExtendetPlayer ep = ExtendetPlayer.get((sq)player);
        ep.setReloadKeyUnpressed();
    }

    private void handleLampKeyPressed(dk packet, sq player, DataInputStream inputStream) {
        ExtendetPlayer ep = ExtendetPlayer.get((sq)player);
        ep.setLampKeyP();
    }

    private void handleLampKeyUnpressed(dk packet, sq player, DataInputStream inputStream) {
        ExtendetPlayer ep = ExtendetPlayer.get((sq)player);
        ep.setLampKeyUp();
    }

    private void handleShootPress(dk packet, sq player, DataInputStream inputStream) {
        ExtendetPlayer ep = ExtendetPlayer.get((sq)player);
        ep.setShootKeyPressed();
    }

    private void handleShootUnpess(dk packet, sq player, DataInputStream inputStream) {
        ExtendetPlayer ep = ExtendetPlayer.get((sq)player);
        ep.setShootKeyUnpressed();
    }

    private void handleSpeedUpPress(dk packet, sq player, DataInputStream inputStream) {
        if (!player.q.I && player.o != null && player.o instanceof EntityDrezinaBase) {
            EntityDrezinaBase loco = (EntityDrezinaBase)player.o;
            if (loco.reverse < 127 && !loco.zeroReverse) {
                loco.reverse = (byte)(loco.reverse + 1);
            }
        }
    }

    private void handleSpeedDownPress(dk packet, sq player, DataInputStream inputStream) {
        if (!player.q.I && player.o != null && player.o instanceof EntityDrezinaBase) {
            EntityDrezinaBase loco = (EntityDrezinaBase)player.o;
            if (!loco.zeroReverse && loco.reverse > -127) {
                loco.reverse = (byte)(loco.reverse - 1);
            }
        }
    }

    private void handleSpeedStopPress(dk packet, sq player, DataInputStream inputStream) {
        if (!player.q.I && player.o != null && player.o instanceof EntityDrezinaBase) {
            EntityDrezinaBase loco = (EntityDrezinaBase)player.o;
            loco.zeroReverse = true;
        }
    }

    public static final void sendCreateClanPacket(String name, String base) {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);
        try {
            var2.writeByte(23);
            var2.writeUTF(name);
            var2.writeUTF(base);
        }
        catch (Exception var5) {
            var5.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])var1.toByteArray()));
    }

    public static final void sendClanListRequestPacket() {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);
        try {
            var2.writeByte(24);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])var1.toByteArray()));
    }

    public static final void sendCommand(int i) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(17);
            outputStream.writeInt(i);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void sendSetFaction(int faction) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(21);
            outputStream.writeInt(faction);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void sendOpenGuiFaction(Player player) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(19);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        PacketDispatcher.sendPacketToPlayer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()), (Player)player);
    }

    public static final void sendShoot(int damage) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(17);
            outputStream.writeInt(damage);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void sendGetEnergy(Player player, int i) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(17);
            outputStream.writeInt(i);
        }
        catch (Exception var5) {
            var5.printStackTrace();
        }
        PacketDispatcher.sendPacketToPlayer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()), (Player)player);
    }

    public static final void sendGetWater(Player player, int i) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(16);
            outputStream.writeInt(i);
        }
        catch (Exception var5) {
            var5.printStackTrace();
        }
        PacketDispatcher.sendPacketToPlayer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()), (Player)player);
    }

    public static final void sendTestForMed(int var0) {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);
        try {
            var2.writeByte(13);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])var1.toByteArray()));
    }

    public static final void sendMed() {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);
        try {
            var2.writeByte(12);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])var1.toByteArray()));
    }

    public static final void sendOpenGuiPacket(int var0) {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);
        try {
            var2.writeByte(7);
            var2.writeInt(var0);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])var1.toByteArray()));
    }

    public static final void sendOpenGuiPacket1(int var0) {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);
        try {
            var2.writeByte(8);
            var2.writeInt(var0);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])var1.toByteArray()));
    }

    public static final void sendOpenGuiPacket2(int var0) {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);
        try {
            var2.writeByte(9);
            var2.writeInt(var0);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])var1.toByteArray()));
    }

    public static final void sendOpenGuiPacket3(int var0) {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);
        try {
            var2.writeByte(10);
            var2.writeInt(var0);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])var1.toByteArray()));
    }

    public static final void sendOpenGuiPacketModifi() {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);
        try {
            var2.writeByte(11);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])var1.toByteArray()));
    }

    public static final void ReloadKeyPressed() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(3);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void ReloadKeyUnpressed() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(4);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void LampKeyPressed() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(5);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void LampKeyUnpressed() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(6);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void ShootKeyPress() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(14);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void ShootKeyUnpress() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(15);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void SpeedUpPress() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(18);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void SpeedDownPress() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(20);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public static final void SpeedStopPress() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(22);
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
        PacketDispatcher.sendPacketToServer((ei)PacketDispatcher.getPacket((String)"tutchannel", (byte[])bos.toByteArray()));
    }

    public String c_() {
        return "Faction";
    }

    public void a(String s) {
    }

    public boolean a(int i, String s) {
        return i <= 2;
    }

    public String a(String s, Object ... var2) {
        return s;
    }

    public t b() {
        return null;
    }
}
