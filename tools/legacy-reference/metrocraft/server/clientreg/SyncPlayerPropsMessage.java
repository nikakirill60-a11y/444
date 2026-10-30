/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.PacketBuffer
 */
package metrocraft.server.clientreg;

import cpw.mods.fml.relauncher.Side;
import java.io.IOException;
import metrocraft.entities.clientplayer.ExtendedPlayer;
import metrocraft.server.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class SyncPlayerPropsMessage
extends AbstractMessage.AbstractClientMessage<SyncPlayerPropsMessage> {
    private NBTTagCompound data;

    public SyncPlayerPropsMessage() {
    }

    public SyncPlayerPropsMessage(EntityPlayer player) {
        this.data = new NBTTagCompound();
        ExtendedPlayer.get(player).saveNBTData(this.data);
    }

    @Override
    protected void read(PacketBuffer buffer) throws IOException {
        this.data = buffer.func_150793_b();
    }

    @Override
    protected void write(PacketBuffer buffer) throws IOException {
        buffer.func_150786_a(this.data);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        ExtendedPlayer.get(player).loadNBTData(this.data);
    }
}

