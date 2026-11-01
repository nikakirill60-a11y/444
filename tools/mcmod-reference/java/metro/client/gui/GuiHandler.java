/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.IGuiHandler
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 */
package metro.client.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import metro.client.gui.GuiMods;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler
implements IGuiHandler {
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.func_147438_o(x, y, z);
        switch (ID) {
            case 0: {
                return null;
            }
            case 3: {
                return null;
            }
        }
        return null;
    }

    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.func_147438_o(x, y, z);
        switch (ID) {
            case 0: 
            case 1: {
                return new GuiMods();
            }
        }
        return null;
    }
}
