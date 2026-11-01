package ru.metrocraft.port.blackrazid;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import ru.metrocraft.port.inventory.BackpackItem;

/** Contents belong to this physical bag's NBT, never a player/global storage map. */
public final class BackpackStorage extends InventoryBasic {
    private final ItemStack bag;private final Runnable changed;private boolean loading=true;
    public BackpackStorage(ItemStack bag,Runnable changed){
        super("metroport.backpack",false,size(bag));this.bag=bag;this.changed=changed;
        if(bag.hasTagCompound()){
            NBTTagList list=bag.getTagCompound().getTagList("ItemInventory",10);
            for(int i=0;i<list.tagCount();i++){
                NBTTagCompound tag=list.getCompoundTagAt(i);int slot=tag.getInteger("Slot");
                if(slot>=0 && slot<getSizeInventory())setInventorySlotContents(slot,new ItemStack(tag));
            }
        }
        loading=false;
    }
    public static int size(ItemStack bag){
        return bag.getItem() instanceof BlackrazidBackpackItem?((BlackrazidBackpackItem)bag.getItem()).entry.storageSlots:0;
    }
    @Override public boolean isItemValidForSlot(int index,ItemStack stack){return !(stack.getItem() instanceof BackpackItem);}
    @Override public void markDirty(){
        super.markDirty();if(loading)return;
        NBTTagList list=new NBTTagList();
        for(int i=0;i<getSizeInventory();i++)if(!getStackInSlot(i).isEmpty()){
            NBTTagCompound tag=getStackInSlot(i).writeToNBT(new NBTTagCompound());tag.setInteger("Slot",i);list.appendTag(tag);
        }
        if(!bag.hasTagCompound())bag.setTagCompound(new NBTTagCompound());
        bag.getTagCompound().setTag("ItemInventory",list);if(changed!=null)changed.run();
    }
    @Override public ItemStack removeStackFromSlot(int slot){ItemStack result=super.removeStackFromSlot(slot);if(!result.isEmpty())markDirty();return result;}
}
