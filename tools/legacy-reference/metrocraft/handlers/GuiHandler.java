/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.IGuiHandler
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 */
package metrocraft.handlers;

import cpw.mods.fml.common.network.IGuiHandler;
import metrocraft.containers.ContainerCustomPlayer;
import metrocraft.entities.clientplayer.ExtendedPlayer;
import metrocraft.gui.GuiCustomPlayerInventory;
import metrocraft.main.MetroMain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler
implements IGuiHandler {
    public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z) {
        if (guiId == MetroMain.GUI_CUSTOM_INV) {
            return new ContainerCustomPlayer(player, player.field_71071_by, ExtendedPlayer.get((EntityPlayer)player).inventory);
        }
        return null;
    }

    public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z) {
        if (guiId == MetroMain.GUI_CUSTOM_INV) {
            return new GuiCustomPlayerInventory(player, player.field_71071_by, ExtendedPlayer.get((EntityPlayer)player).inventory);
        }
        return null;
    }
}

