package ru.metrocraft.port.mechanics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.*;

@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class CapacityRules {
    public static int end(EntityPlayer player){return 9+9*Backpacks.rows(Backpacks.get(player));}
    private static boolean limited(EntityPlayer player){return !player.capabilities.isCreativeMode && !player.isSpectator();}
    @SubscribeEvent(priority=net.minecraftforge.fml.common.eventhandler.EventPriority.LOWEST) public static void pickup(EntityItemPickupEvent event){
        EntityPlayer player=event.getEntityPlayer();
        if(player.world.isRemote || !limited(player))return;
        if(event.getItem().cannotPickup())return;
        String owner=event.getItem().getOwner();
        if(owner!=null && !owner.equals(player.getName()) && event.getItem().lifespan-event.getItem().getAge()>200)return;
        ItemStack stack=event.getItem().getItem(), pickedStack=stack.copy(); int before=stack.getCount();
        // Insert only in unlocked slots. Keep any remainder on the original ground entity.
        for(int i=0;i<end(player)&&!stack.isEmpty();i++){
            ItemStack target=player.inventory.getStackInSlot(i);
            if(!target.isEmpty() && ItemStack.areItemsEqual(stack,target) && ItemStack.areItemStackTagsEqual(stack,target)){
                int n=Math.min(stack.getCount(),Math.max(0,target.getMaxStackSize()-target.getCount()));
                target.grow(n);stack.shrink(n);
            }
        }
        for(int i=0;i<end(player)&&!stack.isEmpty();i++)if(player.inventory.getStackInSlot(i).isEmpty()){
            int n=Math.min(stack.getCount(),stack.getMaxStackSize());
            player.inventory.setInventorySlotContents(i,stack.splitStack(n));
        }
        event.setCanceled(true);
        int picked=before-stack.getCount();
        if(picked>0){
            pickedStack.setCount(picked);
            net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerItemPickupEvent(player,event.getItem(),pickedStack);
            player.onItemPickup(event.getItem(),picked);player.inventory.markDirty();
        }
        if(stack.isEmpty())event.getItem().setDead();else event.getItem().setItem(stack);
    }
    @SubscribeEvent public static void close(PlayerContainerEvent.Close event){
        if(event.getEntityPlayer() instanceof net.minecraft.entity.player.EntityPlayerMP){
            net.minecraft.entity.player.EntityPlayerMP player=(net.minecraft.entity.player.EntityPlayerMP)event.getEntityPlayer();
            player.sendContainerToPlayer(player.inventoryContainer);
        }
    }
    @SubscribeEvent public static void open(PlayerContainerEvent.Open event){
        EntityPlayer player=event.getEntityPlayer();
        if(player.world.isRemote || !limited(player) || event.getContainer() instanceof MetroContainer)return;
        java.util.List<Slot> slots=event.getContainer().inventorySlots;
        for(int i=0;i<slots.size();i++){
            Slot original=slots.get(i);
            // Do not replace unknown mod-specific slots/callback contracts.
            if(original.inventory!=player.inventory || original.getClass()!=Slot.class || original.getSlotIndex()>=36)continue;
            Slot gated=new Slot(player.inventory,original.getSlotIndex(),original.xPos,original.yPos){
                private boolean locked(){return limited(player)&&getSlotIndex()>=end(player);}
                @Override public ItemStack getStack(){return locked()?ItemStack.EMPTY:super.getStack();}
                @Override public boolean isItemValid(ItemStack stack){return !locked()&&original.isItemValid(stack);}
                @Override public boolean canTakeStack(EntityPlayer who){return !locked()&&original.canTakeStack(who);}
            };
            gated.slotNumber=original.slotNumber;slots.set(i,gated);
        }
    }
}
