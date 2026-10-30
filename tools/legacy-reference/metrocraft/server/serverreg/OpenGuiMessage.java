/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.PacketBuffer
 */
package metrocraft.server.serverreg;

import cpw.mods.fml.relauncher.Side;
import metrocraft.main.MetroMain;
import metrocraft.server.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class OpenGuiMessage
extends AbstractMessage.AbstractServerMessage<OpenGuiMessage> {
    private int id;

    public OpenGuiMessage() {
    }

    public OpenGuiMessage(int id) {
        this.id = id;
    }

    @Override
    protected void read(PacketBuffer buffer) {
        this.id = buffer.readInt();
    }

    @Override
    protected void write(PacketBuffer buffer) {
        buffer.writeInt(this.id);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        player.openGui((Object)MetroMain.mod, this.id, player.field_70170_p, (int)player.field_70165_t, (int)player.field_70163_u, (int)player.field_70161_v);
    }
}

