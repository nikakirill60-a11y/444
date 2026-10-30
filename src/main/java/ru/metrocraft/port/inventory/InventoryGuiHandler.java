package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public final class InventoryGuiHandler implements IGuiHandler {
    @Override public Object getServerGuiElement(int id,EntityPlayer player,World world,int x,int y,int z) {
        return id==0 ? new MetroContainer(player) : null;
    }
    @Override public Object getClientGuiElement(int id,EntityPlayer player,World world,int x,int y,int z) {
        return id==0 ? new ru.metrocraft.port.client.MetroInventoryGui(player) : null;
    }
}
