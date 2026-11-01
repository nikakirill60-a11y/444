package ru.metrocraft.port.blackrazid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import ru.metrocraft.port.inventory.*;

/** Server holds the actual equipped/held bag and refuses any attempt to move/swap/clone that bag while open. */
public final class BackpackContainer extends Container {
    public final BackpackStorage storage;public final ItemStack bag;
    private final EntityPlayer owner;private final int source,heldIndex;
    public static ItemStack source(EntityPlayer player,int source){
        return source==0?player.getHeldItemMainhand():source==1?player.getHeldItemOffhand():source==2?Backpacks.inventory(player).getStackInSlot(0):ItemStack.EMPTY;
    }
    public static boolean valid(EntityPlayer player,int source){return !player.isSpectator() && BackpackStorage.size(source(player,source))>0;}
    public BackpackContainer(EntityPlayer player,int source){
        owner=player;this.source=source;heldIndex=source==0?player.inventory.currentItem:source==1?40:-1;bag=source(player,source);
        storage=new BackpackStorage(bag,()->{if(!player.world.isRemote){if(source==2)Backpacks.inventory(player).markDirty();else player.inventory.markDirty();}});
        for(int i=0;i<storage.getSizeInventory();i++)addSlotToContainer(new Slot(storage,i,44+(i%5)*18,18+(i/5)*18){
            @Override public boolean isItemValid(ItemStack stack){return storage.isItemValidForSlot(getSlotIndex(),stack);}
        });
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addPlayer(player,9+row*9+col,8+col*18,86+row*18);
        for(int i=0;i<9;i++)addPlayer(player,i,8+i*18,144);
    }
    private void addPlayer(EntityPlayer player,int index,int x,int y){
        addSlotToContainer(new Slot(player.inventory,index,x,y){
            @Override public boolean isItemValid(ItemStack stack){return index!=heldIndex && index<ru.metrocraft.port.mechanics.CapacityRules.end(player);}
            @Override public boolean canTakeStack(EntityPlayer who){return index!=heldIndex;}
        });
    }
    @Override public boolean canInteractWith(EntityPlayer player){return player==owner && player.isEntityAlive()&&!player.isSpectator()&&source(player,source)==bag&&(source!=0||player.inventory.currentItem==heldIndex);}
    @Override public ItemStack transferStackInSlot(EntityPlayer player,int index){return ItemStack.EMPTY;}
    @Override public boolean canMergeSlot(ItemStack stack,Slot slot){return slot.inventory!=owner.inventory||slot.getSlotIndex()!=heldIndex;}
    @Override public ItemStack slotClick(int index,int drag,ClickType type,EntityPlayer player){
        if(!canInteractWith(player))return ItemStack.EMPTY;
        if(type==ClickType.SWAP && drag==heldIndex)return ItemStack.EMPTY;
        if(index>=0&&index<inventorySlots.size()){
            Slot slot=inventorySlots.get(index);if(slot.inventory==owner.inventory&&slot.getSlotIndex()==heldIndex)return ItemStack.EMPTY;
        }
        return super.slotClick(index,drag,type,player);
    }
    @Override public void onContainerClosed(EntityPlayer player){super.onContainerClosed(player);storage.markDirty();}
}
