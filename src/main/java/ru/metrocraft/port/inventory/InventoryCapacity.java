package ru.metrocraft.port.inventory;
import net.minecraft.entity.player.EntityPlayer;
/** Metro inventory only. Old MetroVanillaInventory tags are intentionally ignored. */
public final class InventoryCapacity {
    private InventoryCapacity() {}
    public static int rows(boolean creative,int backpackRows){return creative?3:Math.max(0,Math.min(3,backpackRows));}
    public static int rows(EntityPlayer player){return rows(player.capabilities.isCreativeMode,Backpacks.rows(Backpacks.get(player)));}
    public static boolean emptyForOpen(EntityPlayer player){
        return player.inventory.getItemStack().isEmpty() && player.inventoryContainer instanceof net.minecraft.inventory.ContainerPlayer
                && ((net.minecraft.inventory.ContainerPlayer)player.inventoryContainer).craftMatrix.isEmpty();
    }
}
