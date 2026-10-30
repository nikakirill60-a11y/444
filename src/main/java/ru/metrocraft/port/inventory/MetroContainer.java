package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public final class MetroContainer extends Container {
    public final InventoryBasic bag;
    private final EntityPlayer owner;
    public MetroContainer(EntityPlayer player) {
        owner=player;
        bag=Backpacks.inventory(player);
        addSlotToContainer(new Slot(bag,0,24,18) {
            @Override public boolean isItemValid(ItemStack stack) { return stack.getItem() instanceof BackpackItem; }
            @Override public int getSlotStackLimit() { return 1; }
        });
        EntityEquipmentSlot[] armor={EntityEquipmentSlot.HEAD,EntityEquipmentSlot.CHEST,EntityEquipmentSlot.LEGS,EntityEquipmentSlot.FEET};
        for(int i=0;i<4;i++) {
            final EntityEquipmentSlot type=armor[i];
            addSlotToContainer(new Slot(player.inventory,39-i,44,7+i*18) {
                @Override public int getSlotStackLimit(){return 1;}
                @Override public boolean isItemValid(ItemStack stack){return stack.getItem().isValidArmor(stack,type,player);}
                @Override public boolean canTakeStack(EntityPlayer actor){return actor.capabilities.isCreativeMode || !net.minecraft.enchantment.EnchantmentHelper.hasBindingCurse(getStack());}
            });
        }
        addSlotToContainer(new Slot(player.inventory,40,120,61));
        for(int row=0;row<3;row++) for(int col=0;col<9;col++) {
            final int required=row+1;
            addSlotToContainer(new Slot(player.inventory,9+row*9+col,8+col*18,84+row*18) {
                private boolean unlocked(){return required<=Backpacks.rows(bag.getStackInSlot(0));}
                @Override public boolean isItemValid(ItemStack stack){return unlocked();}
                @Override public boolean canTakeStack(EntityPlayer player){return true;}
                @Override public boolean isEnabled(){return true;}
            });
        }
        for(int i=0;i<9;i++) addSlotToContainer(new Slot(player.inventory,i,8+i*18,142));
    }
    @Override public boolean canInteractWith(EntityPlayer player){return player==owner && player.isEntityAlive();}
    @Override public ItemStack transferStackInSlot(EntityPlayer player,int index){return ItemStack.EMPTY;}
    @Override public boolean canMergeSlot(ItemStack stack,Slot slot){return slot.inventory!=bag && slot.isItemValid(stack);}
    @Override public ItemStack slotClick(int index,int drag,ClickType type,EntityPlayer player) {
        return super.slotClick(index,drag,type,player);
    }
    @Override public void onContainerClosed(EntityPlayer player){super.onContainerClosed(player); Backpacks.enforceCapacity(player);}
}
