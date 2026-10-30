/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketBuffer
 */
package metrocraft.server.clientreg;

import cpw.mods.fml.relauncher.Side;
import metrocraft.entities.clientplayer.ExtendedPlayer;
import metrocraft.main.MetroItems;
import metrocraft.server.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class SyncEquipBackMessage
extends AbstractMessage.AbstractClientMessage<SyncEquipBackMessage> {
    private int playerID;
    private int equipID;

    public SyncEquipBackMessage() {
    }

    public SyncEquipBackMessage(int playerID, int equipID) {
        this.playerID = playerID;
        this.equipID = equipID;
    }

    @Override
    protected void read(PacketBuffer buffer) {
        this.playerID = buffer.readInt();
        this.equipID = buffer.readInt();
    }

    @Override
    protected void write(PacketBuffer buffer) {
        buffer.writeInt(this.playerID);
        buffer.writeInt(this.equipID);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        if (Minecraft.func_71410_x().field_71439_g.func_145782_y() == this.playerID) {
            return;
        }
        Entity playerToUpd = player.field_70170_p.func_73045_a(this.playerID);
        if (playerToUpd == null || !(playerToUpd instanceof EntityPlayer)) {
            return;
        }
        Object itemStack = null;
        itemStack = this.equipID == 1 ? new ItemStack(MetroItems.smallRucksack) : (this.equipID == 2 ? new ItemStack(MetroItems.mediumRucksack) : (this.equipID == 3 ? new ItemStack(MetroItems.bigRucksack) : null));
        ExtendedPlayer.get((EntityPlayer)((EntityPlayer)playerToUpd)).inventory.inventory[1] = itemStack;
    }
}

