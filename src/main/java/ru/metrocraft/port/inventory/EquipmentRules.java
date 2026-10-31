package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.mcmod.McmodArmorItem;
import ru.metrocraft.port.MetroArmor;
import ru.metrocraft.port.mechanics.NoDrop;

/** Shared validation + authoritative repair of dispenser/command/third-party equipment bypasses. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class EquipmentRules {
    private EquipmentRules() {}
    public static boolean sealedId(String id) {
        return "himza".equals(id) || "satanarmor".equals(id) || "mcmod_officer_ozk".equals(id);
    }
    public static boolean sealed(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof MetroArmor))return false;
        MetroArmor armor=(MetroArmor)stack.getItem();
        return sealedId(armor instanceof McmodArmorItem?((McmodArmorItem)armor).entry.id:armor.getDefinition().id);
    }
    public static boolean allowed(ItemStack chest,ItemStack head,EntityEquipmentSlot slot,ItemStack candidate) {
        if(NvgEquipment.accepts(candidate))return false; // PNV has exactly one equipment destination now.
        if(slot==EntityEquipmentSlot.HEAD)return !sealed(chest);
        return slot!=EntityEquipmentSlot.CHEST || !sealed(candidate) || head.isEmpty();
    }
    public static boolean allowed(EntityPlayer player,EntityEquipmentSlot slot,ItemStack candidate) {
        return allowed(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST),player.getItemStackFromSlot(EntityEquipmentSlot.HEAD),slot,candidate);
    }
    /** Delegate every mutation/callback to the original armor slot, including Curse of Binding. */
    private static final class Guard extends Slot {
        private final Slot original; private final EntityPlayer owner; private final EntityEquipmentSlot type;
        Guard(Slot original,EntityPlayer player,EntityEquipmentSlot type) {
            super(original.inventory,original.getSlotIndex(),original.xPos,original.yPos);
            this.original=original;this.owner=player;this.type=type;slotNumber=original.slotNumber;
        }
        @Override public boolean isItemValid(ItemStack stack) { return allowed(owner,type,stack)&&original.isItemValid(stack); }
        @Override public boolean canTakeStack(EntityPlayer player) { return original.canTakeStack(player); }
        @Override public ItemStack getStack() { return original.getStack(); }
        @Override public void putStack(ItemStack stack) { original.putStack(stack); }
        @Override public ItemStack decrStackSize(int count) { return original.decrStackSize(count); }
        @Override public ItemStack onTake(EntityPlayer player,ItemStack stack) { return original.onTake(player,stack); }
        @Override public void onSlotChanged() { original.onSlotChanged(); }
        @Override public int getSlotStackLimit() { return original.getSlotStackLimit(); }
        @Override public int getItemStackLimit(ItemStack stack) { return original.getItemStackLimit(stack); }
        @Override public boolean isEnabled() { return original.isEnabled(); }
    }
    public static void guard(EntityPlayer player,Container container) {
        for(int i=0;i<container.inventorySlots.size();i++) {
            Slot slot=container.inventorySlots.get(i);
            if(slot instanceof Guard || slot.inventory!=player.inventory)continue;
            int index=slot.getSlotIndex();
            if(index==39 || index==38)container.inventorySlots.set(i,new Guard(slot,player,index==39?EntityEquipmentSlot.HEAD:EntityEquipmentSlot.CHEST));
        }
    }
    @SubscribeEvent(priority=net.minecraftforge.fml.common.eventhandler.EventPriority.HIGHEST)
    public static void rightClick(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        ItemStack held=event.getItemStack();
        if(NvgEquipment.accepts(held))return; // Its right click equips the NEW slot, not HEAD.
        EntityEquipmentSlot slot=net.minecraft.entity.EntityLiving.getSlotForItemStack(held);
        if((slot==EntityEquipmentSlot.HEAD || slot==EntityEquipmentSlot.CHEST)
                && !allowed(event.getEntityPlayer(),slot,held)) {
            event.setCanceled(true);event.setCancellationResult(net.minecraft.util.EnumActionResult.FAIL);
        }
    }
    @SubscribeEvent public static void opened(PlayerContainerEvent.Open event) { guard(event.getEntityPlayer(),event.getContainer()); }
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent event) {
        if(event.phase!=TickEvent.Phase.END)return;
        EntityPlayer player=event.player;
        guard(player,player.inventoryContainer);guard(player,player.openContainer);
        if(player.world.isRemote)return;
        ItemStack head=player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if(NvgEquipment.accepts(head)) {
            ItemStack moved=head.copy();moved.setCount(1);
            player.setItemStackToSlot(EntityEquipmentSlot.HEAD,ItemStack.EMPTY);
            if(NvgEquipment.get(player).isEmpty())NvgEquipment.set(player,moved);else NoDrop.retain(player,moved);
            if(head.getCount()>1) {ItemStack excess=head.copy();excess.shrink(1);NoDrop.retain(player,excess);}
            player.inventory.markDirty();
        } else if(!head.isEmpty() && sealed(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST))) {
            // Reject a bound helmet conflict by returning the suit, never providing a curse-removal exploit.
            EntityEquipmentSlot rejected=EnchantmentHelper.hasBindingCurse(head)&&!player.capabilities.isCreativeMode
                    ?EntityEquipmentSlot.CHEST:EntityEquipmentSlot.HEAD;
            ItemStack old=player.getItemStackFromSlot(rejected);
            NoDrop.retain(player,old.copy());player.setItemStackToSlot(rejected,ItemStack.EMPTY);player.inventory.markDirty();
        }
    }
}
