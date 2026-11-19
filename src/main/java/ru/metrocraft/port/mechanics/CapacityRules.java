package ru.metrocraft.port.mechanics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.inventory.*;

@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class CapacityRules {
    public static int end(EntityPlayer player){return 9+9*InventoryCapacity.rows(player);}
    private static boolean limited(EntityPlayer player){return !player.capabilities.isCreativeMode && !player.isSpectator();}
    @SubscribeEvent(priority=net.minecraftforge.fml.common.eventhandler.EventPriority.LOWEST) public static void pickup(EntityItemPickupEvent event){
        EntityPlayer player=event.getEntityPlayer();
        if(player.world.isRemote || !limited(player) || event.getItem().cannotPickup())return;
        String owner=event.getItem().getOwner();
        if(owner!=null && !owner.equals(player.getName()) && event.getItem().lifespan-event.getItem().getAge()>200)return;
        ItemStack stack=event.getItem().getItem(),pickedStack=stack.copy();int before=stack.getCount();
        int budget=CarryWeight.fitting(player,stack);
        // Restrict only incoming ground loot. Remainder stays on its original entity.
        for(int pass=0;pass<2;pass++)for(int i=0;i<end(player)&&budget>0&&!stack.isEmpty();i++){
            if(!LoadoutRules.accepts(i,stack))continue;
            ItemStack target=player.inventory.getStackInSlot(i);
            if(pass==0 && !target.isEmpty() && ItemStack.areItemsEqual(stack,target) && ItemStack.areItemStackTagsEqual(stack,target)){
                int n=Math.min(budget,Math.min(stack.getCount(),Math.max(0,LoadoutRules.limit(i,target)-target.getCount())));
                target.grow(n);stack.shrink(n);budget-=n;
            }else if(pass==1 && target.isEmpty()){
                int n=Math.min(budget,Math.min(stack.getCount(),LoadoutRules.limit(i,stack)));
                player.inventory.setInventorySlotContents(i,stack.splitStack(n));budget-=n;
            }
        }
        event.setCanceled(true);int picked=before-stack.getCount();
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
    /** Preserve the wrapped slot's callbacks. Unknown custom mod slots are deliberately untouched. */
    public static void guard(EntityPlayer player,Container container){
        if(container==null || container instanceof MetroContainer)return;
        java.util.List<Slot> slots=container.inventorySlots;
        for(int i=0;i<slots.size();i++){
            Slot original=slots.get(i);int index=original.getSlotIndex();
            if(original.inventory!=player.inventory || original.getClass()!=Slot.class || index>=36)continue;
            Slot guarded=new Slot(player.inventory,index,original.xPos,original.yPos){
                @Override public boolean isItemValid(ItemStack stack){return LoadoutRules.accepts(index,stack)&&original.isItemValid(stack);}
                @Override public boolean canTakeStack(EntityPlayer who){return original.canTakeStack(who);}
                @Override public int getSlotStackLimit(){return Math.min(original.getSlotStackLimit(),index<3?1:64);}
                @Override public int getItemStackLimit(ItemStack stack){return Math.min(original.getItemStackLimit(stack),getSlotStackLimit());}
                @Override public ItemStack onTake(EntityPlayer who,ItemStack stack){return original.onTake(who,stack);}
                @Override public void onSlotChanged(){original.onSlotChanged();}
            };
            guarded.slotNumber=original.slotNumber;slots.set(i,guarded);
        }
    }
    @SubscribeEvent public static void open(PlayerContainerEvent.Open event){guard(event.getEntityPlayer(),event.getContainer());}
    @SubscribeEvent public static void tick(net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent event){
        if(event.phase!=net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END)return;
        guard(event.player,event.player.inventoryContainer);guard(event.player,event.player.openContainer);
    }
}
