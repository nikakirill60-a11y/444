package ru.metrocraft.port.inventory;

import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;

/** Only coordinates live in player NBT. The original InventoryPlayer stacks remain authoritative. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class GridInventory {
    private static final String KEY="MetroGridPositionsV1";
    private GridInventory(){}
    private static NBTTagCompound data(EntityPlayer player){
        NBTTagCompound root=player.getEntityData();
        if(!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG,10))root.setTag(EntityPlayer.PERSISTED_NBT_TAG,new NBTTagCompound());
        return root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }
    public static PackingGrid snapshot(EntityPlayer player){
        int[] w=new int[27],h=new int[27];
        for(int i=0;i<27;i++){
            ItemStack stack=player.inventory.getStackInSlot(i+9);
            if(!stack.isEmpty()){w[i]=ItemMetrics.width(stack);h[i]=ItemMetrics.height(stack);}
        }
        PackingGrid grid=new PackingGrid(w,h,data(player).getIntArray(KEY));
        if(!player.world.isRemote)save(player,grid);
        return grid;
    }
    public static void save(EntityPlayer player,PackingGrid grid){
        int[] positions=grid.positions();
        if(!Arrays.equals(data(player).getIntArray(KEY),positions))data(player).setIntArray(KEY,positions);
    }
    /** Called by ordinary chest/crafting slots and ground pickup before inserting a NEW record. */
    public static boolean roomFor(EntityPlayer player,int inventoryIndex,ItemStack candidate){
        if(inventoryIndex<9||inventoryIndex>=36||candidate.isEmpty())return true;
        ItemStack old=player.inventory.getStackInSlot(inventoryIndex);
        PackingGrid grid=snapshot(player);
        if(!old.isEmpty() && ItemStack.areItemsEqual(old,candidate) && ItemStack.areItemStackTagsEqual(old,candidate))return grid.position(inventoryIndex-9)>=0;
        return grid.firstFit(ItemMetrics.width(candidate),ItemMetrics.height(candidate),inventoryIndex-9)>=0;
    }
    @SubscribeEvent public static void clone(PlayerEvent.Clone e){
        data(e.getEntityPlayer()).setIntArray(KEY,data(e.getOriginal()).getIntArray(KEY).clone());
    }
}
