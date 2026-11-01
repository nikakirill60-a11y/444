package ru.metrocraft.port.inventory;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroPort;

@Mod.EventBusSubscriber(modid = MetroPort.ID)
public final class Backpacks {
    private static final String KEY = "MetroEquippedBackpack";
    public static ItemStack get(EntityPlayer player) {
        return new ItemStack(player.getEntityData().getCompoundTag(KEY));
    }
    public static void set(EntityPlayer player, ItemStack stack) {
        player.getEntityData().setTag(KEY, stack.writeToNBT(new NBTTagCompound()));
        if (!player.world.isRemote && player instanceof EntityPlayerMP) {
            BackpackSyncPacket packet = new BackpackSyncPacket(player.getEntityId(), stack);
            MetroPort.NETWORK.sendToAllTracking(packet, player);
            MetroPort.NETWORK.sendTo(packet, (EntityPlayerMP)player);
        }
    }
    public static int rows(ItemStack stack) {
        return stack.getItem() instanceof BackpackItem ? ((BackpackItem)stack.getItem()).rows : 0;
    }
    public static InventoryBasic inventory(EntityPlayer player) {
        InventoryBasic inventory = new InventoryBasic("metroport.backpack",false,1) {
            @Override public int getInventoryStackLimit() { return 1; }
            @Override public boolean isItemValidForSlot(int slot, ItemStack stack) { return stack.getItem() instanceof BackpackItem; }
            @Override public void markDirty() { super.markDirty(); Backpacks.set(player,getStackInSlot(0)); }
        };
        inventory.setInventorySlotContents(0,get(player));
        return inventory;
    }
    @SubscribeEvent
    public static void track(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityPlayer && event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayer target=(EntityPlayer)event.getTarget();
            MetroPort.NETWORK.sendTo(new BackpackSyncPacket(target.getEntityId(),get(target)),(EntityPlayerMP)event.getEntityPlayer());
        }
    }
    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        set(event.getEntityPlayer(), get(event.getOriginal()));
    }
    @SubscribeEvent
    public static void death(LivingDropsEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntityLiving().world.isRemote) return;
        EntityPlayer player=(EntityPlayer)event.getEntityLiving();
        if (player.world.getGameRules().getBoolean("keepInventory")) return;
        ItemStack bag=get(player);
        if (ru.metrocraft.port.mechanics.NoDrop.marked(bag)) return;
        if (!bag.isEmpty()) event.getDrops().add(new EntityItem(player.world,player.posX,player.posY,player.posZ,bag));
        set(player,ItemStack.EMPTY);
    }
    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player=event.player;
        if (event.phase != TickEvent.Phase.END || player.world.isRemote) return;
        if (player.ticksExisted == 20) set(player, get(player));
        if (player.capabilities.isCreativeMode || player.isSpectator() || player.ticksExisted % 20 != 0
                || player.openContainer instanceof MetroContainer) return;
        enforceCapacity(player);
    }
    public static void enforceCapacity(EntityPlayer player) {
        if (player.world.isRemote || player.capabilities.isCreativeMode || player.isSpectator()) return;
        int end=9+InventoryCapacity.rows(player)*9;
        for(int i=end;i<36;i++) {
            ItemStack stack=player.inventory.getStackInSlot(i);
            if(stack.isEmpty()) continue;
            for(int j=0;j<end && !stack.isEmpty();j++) {
                ItemStack target=player.inventory.getStackInSlot(j);
                if(!target.isEmpty() && ItemStack.areItemsEqual(target,stack) && ItemStack.areItemStackTagsEqual(target,stack)) {
                    int amount=Math.min(stack.getCount(),target.getMaxStackSize()-target.getCount());
                    if(amount>0){target.grow(amount);stack.shrink(amount);}
                }
            }
            for(int j=0;j<end && !stack.isEmpty();j++) if(player.inventory.getStackInSlot(j).isEmpty()) {
                player.inventory.setInventorySlotContents(j,stack.copy());stack.setCount(0);
            }
            // Never throw away inventory from an existing world or a smaller backpack.
            // Overflow remains visible and take-only until the player clears it.
            player.inventory.setInventorySlotContents(i, stack.isEmpty() ? ItemStack.EMPTY : stack);
        }
        player.inventory.markDirty();
    }
}
