package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public final class MetroContainer extends Container {
    public final InventoryBasic bag;
    public final InventoryBasic nvg,watch,lamp,nativeBag;
    private final EntityPlayer owner;
    public MetroContainer(EntityPlayer player) {
        owner=player;ExtraEquipment.migrateLegacyBackpack(player);
        bag=Backpacks.inventory(player);nvg=NvgEquipment.inventory(player);watch=WatchEquipment.inventory(player);
        lamp=ExtraEquipment.inventory(player,ExtraEquipment.Kind.LAMP);nativeBag=ExtraEquipment.inventory(player,ExtraEquipment.Kind.BLACKRAZID_BACKPACK);
        addSlotToContainer(new Slot(bag,0,24,18) {
            @Override public boolean isItemValid(ItemStack stack) { return stack.getItem() instanceof BackpackItem && !(stack.getItem() instanceof ru.metrocraft.port.blackrazid.BlackrazidBackpackItem); }
            @Override public int getSlotStackLimit() { return 1; }
        });
        EntityEquipmentSlot[] armor={EntityEquipmentSlot.HEAD,EntityEquipmentSlot.CHEST,EntityEquipmentSlot.LEGS,EntityEquipmentSlot.FEET};
        for(int i=0;i<4;i++) {
            final EntityEquipmentSlot type=armor[i];
            addSlotToContainer(new Slot(player.inventory,39-i,44,7+i*18) {
                @Override public int getSlotStackLimit(){return 1;}
                @Override public boolean isItemValid(ItemStack stack){return EquipmentRules.allowed(player,type,stack)&&stack.getItem().isValidArmor(stack,type,player);}
                @Override public boolean canTakeStack(EntityPlayer actor){return actor.capabilities.isCreativeMode || !net.minecraft.enchantment.EnchantmentHelper.hasBindingCurse(getStack());}
            });
        }
        addSlotToContainer(new Slot(player.inventory,40,120,61));
        for(int row=0;row<3;row++) for(int col=0;col<9;col++) {
            final int required=row+1;
            addSlotToContainer(new Slot(player.inventory,9+row*9+col,8+col*18,84+row*18) {
                private boolean unlocked(){return required<=InventoryCapacity.rows(player);}
                @Override public boolean isItemValid(ItemStack stack){return unlocked();}
                @Override public boolean canTakeStack(EntityPlayer player){return true;}
                @Override public boolean isEnabled(){return true;}
            });
        }
        for(int i=0;i<9;i++) addSlotToContainer(new Slot(player.inventory,i,8+i*18,142));
        // Append, preserving every existing backpack/armor/inventory slot number.
        addSlotToContainer(new Slot(nvg,0,140,18) {
            @Override public boolean isItemValid(ItemStack stack) {return NvgEquipment.accepts(stack);}
            @Override public int getSlotStackLimit() {return 1;}
            @Override public boolean canTakeStack(EntityPlayer actor) {return actor.capabilities.isCreativeMode || !net.minecraft.enchantment.EnchantmentHelper.hasBindingCurse(getStack());}
        });
        addSlotToContainer(new Slot(watch,0,140,54) {
            @Override public boolean isItemValid(ItemStack stack){return WatchEquipment.accepts(stack);}
            @Override public int getSlotStackLimit(){return 1;}
            @Override public boolean canTakeStack(EntityPlayer actor){return actor.capabilities.isCreativeMode || !net.minecraft.enchantment.EnchantmentHelper.hasBindingCurse(getStack());}
        });
        addSlotToContainer(extraSlot(lamp,ExtraEquipment.Kind.LAMP,184,18));
        addSlotToContainer(extraSlot(nativeBag,ExtraEquipment.Kind.BLACKRAZID_BACKPACK,184,54));
    }
    private Slot extraSlot(InventoryBasic inventory,ExtraEquipment.Kind kind,int x,int y){
        return new Slot(inventory,0,x,y){
            @Override public boolean isItemValid(ItemStack stack){return ExtraEquipment.accepts(kind,stack);}
            @Override public int getSlotStackLimit(){return 1;}
            @Override public boolean canTakeStack(EntityPlayer actor){return actor.capabilities.isCreativeMode || !net.minecraft.enchantment.EnchantmentHelper.hasBindingCurse(getStack());}
        };
    }
    @Override public boolean canInteractWith(EntityPlayer player){return player==owner && player.isEntityAlive();}
    @Override public ItemStack transferStackInSlot(EntityPlayer player,int index){return ItemStack.EMPTY;}
    @Override public boolean canMergeSlot(ItemStack stack,Slot slot){return slot.inventory!=bag && slot.inventory!=nvg && slot.inventory!=watch && slot.inventory!=lamp && slot.inventory!=nativeBag && slot.isItemValid(stack);}
    @Override public ItemStack slotClick(int index,int drag,ClickType type,EntityPlayer player){return super.slotClick(index,drag,type,player);}
    @Override public void onContainerClosed(EntityPlayer player){super.onContainerClosed(player);Backpacks.enforceCapacity(player);}
}
