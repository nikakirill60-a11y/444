package ru.metrocraft.port.mcmod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
/** No inventory slots/cursor manipulation: this screen only submits upgrades for this exact held gun. */
public final class GunWorkbenchContainer extends Container {
    private final ItemStack gun;
    private final int selected;
    public GunWorkbenchContainer(EntityPlayer player){gun=player.getHeldItemMainhand();selected=player.inventory.currentItem;}
    @Override public boolean canInteractWith(EntityPlayer player){
        return player.isEntityAlive()&&!player.isSpectator()&&player.inventory.currentItem==selected
                &&player.getHeldItemMainhand()==gun&&gun.getItem() instanceof McmodAkItem;
    }
    @Override public ItemStack transferStackInSlot(EntityPlayer player,int index){return ItemStack.EMPTY;}
}
