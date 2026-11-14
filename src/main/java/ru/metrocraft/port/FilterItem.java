package ru.metrocraft.port;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import ru.metrocraft.port.mechanics.FilterRules;

/** Standard held-use lifecycle: no refill or consumption until server completion. */
public class FilterItem extends Item {
    private static ItemStack target(EntityPlayer player) {
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST}) {
            ItemStack armor = player.getItemStackFromSlot(slot);
            if (armor.isEmpty() || !(armor.getItem() instanceof MetroArmor)
                    || !((MetroArmor) armor.getItem()).isGasMask()) continue;
            int ticks = armor.hasTagCompound() ? armor.getTagCompound().getInteger("MetroFilterTicks") : 0;
            if (FilterRules.canReplace(ticks)) return armor;
        }
        return ItemStack.EMPTY;
    }

    @Override public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (!player.isEntityAlive() || player.isSpectator() || held.isEmpty() || target(player).isEmpty())
            return new ActionResult<>(EnumActionResult.FAIL, held);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }
    @Override public int getMaxItemUseDuration(ItemStack stack) { return FilterRules.REPLACEMENT_TICKS; }
    // No eating sound or MWF hand-pose override. Donor-specific animation is not imported.
    @Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.NONE; }
    @Override public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!player.isEntityAlive() || player.isSpectator() || target(player).isEmpty()) player.stopActiveHand();
        }
    }
    @Override public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (world.isRemote || !(entity instanceof EntityPlayer) || stack.isEmpty() || stack.getItem() != this) return stack;
        EntityPlayer player = (EntityPlayer) entity;
        if (!player.isEntityAlive() || player.isSpectator()) return stack;
        ItemStack armor = target(player);
        if (armor.isEmpty()) return stack;
        if (!armor.hasTagCompound()) armor.setTagCompound(new NBTTagCompound());
        armor.getTagCompound().setInteger("MetroFilterTicks", FilterRules.CAPACITY_TICKS);
        if (!player.capabilities.isCreativeMode) stack.shrink(1);
        player.inventory.markDirty();
        player.inventoryContainer.detectAndSendChanges();
        return stack;
    }
}
