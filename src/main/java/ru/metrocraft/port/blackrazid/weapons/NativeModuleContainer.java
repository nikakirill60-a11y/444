package ru.metrocraft.port.blackrazid.weapons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/** Button-only transaction UI: never exposes a second mutable inventory/cursor for the held weapon. */
public final class NativeModuleContainer extends Container {
    private final ItemStack gun;
    private final int selected;
    public NativeModuleContainer(EntityPlayer p){gun=p.getHeldItemMainhand();selected=p.inventory.currentItem;}
    @Override public boolean canInteractWith(EntityPlayer p){
        return p.isEntityAlive() && !p.isSpectator() && p.inventory.currentItem==selected && (p.getHeldItemMainhand()==gun || p.world.isRemote && p.getHeldItemMainhand().getItem()==gun.getItem() && p.getHeldItemMainhand().getCount()==1)
                && gun.getCount()==1 && gun.getItem() instanceof BlackrazidGunItem && BlackrazidModules.hasSockets(gun)
                && ((BlackrazidGunItem)gun.getItem()).modificationReady(gun) && p.inventory.getItemStack().isEmpty();
    }
    @Override public ItemStack transferStackInSlot(EntityPlayer p,int index){return ItemStack.EMPTY;}
}
