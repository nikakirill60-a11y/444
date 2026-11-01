package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public final class InventoryGuiHandler implements IGuiHandler {
    @Override public Object getServerGuiElement(int id,EntityPlayer player,World world,int x,int y,int z) {
        if(id==3){
            ru.metrocraft.port.blackrazid.weapons.NativeModuleContainer container=new ru.metrocraft.port.blackrazid.weapons.NativeModuleContainer(player);
            return container.canInteractWith(player)?container:null;
        }
        return id==2 && ru.metrocraft.port.blackrazid.BackpackContainer.valid(player,x) ? new ru.metrocraft.port.blackrazid.BackpackContainer(player,x) : id==0 ? new MetroContainer(player) : id==1 && player.getHeldItemMainhand().getItem() instanceof ru.metrocraft.port.mcmod.McmodAkItem ? new ru.metrocraft.port.mcmod.GunWorkbenchContainer(player) : null;
    }
    @Override public Object getClientGuiElement(int id,EntityPlayer player,World world,int x,int y,int z) {
        if(id==3)return new ru.metrocraft.port.client.BlackrazidModulesGui(player);
        return id==2 ? new ru.metrocraft.port.client.BlackrazidBackpackGui(player,x) : id==0 ? new ru.metrocraft.port.client.MetroInventoryGui(player) : id==1 ? new ru.metrocraft.port.client.GunModsGui(player) : null;
    }
}
