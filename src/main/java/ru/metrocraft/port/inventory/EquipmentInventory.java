package ru.metrocraft.port.inventory;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** One live stack. Saving a slot must NEVER replace it during Container.slotClick. */
public final class EquipmentInventory extends InventoryBasic {
    private final Predicate<ItemStack> accepts;
    private final Consumer<ItemStack> changed;
    private boolean loading;
    public EquipmentInventory(String name,ItemStack initial,Predicate<ItemStack> accepts,Consumer<ItemStack> changed){
        super(name,false,1);this.accepts=accepts;this.changed=changed;load(initial);
    }
    public void load(ItemStack stack){
        loading=true;
        try{super.setInventorySlotContents(0,stack.copy());}finally{loading=false;}
    }
    @Override public int getInventoryStackLimit(){return 1;}
    @Override public boolean isItemValidForSlot(int index,ItemStack stack){return accepts.test(stack);}
    @Override public void markDirty(){super.markDirty();if(!loading && changed!=null)changed.accept(getStackInSlot(0));}
    @Override public ItemStack removeStackFromSlot(int index){ItemStack result=super.removeStackFromSlot(index);if(!result.isEmpty())markDirty();return result;}
}
