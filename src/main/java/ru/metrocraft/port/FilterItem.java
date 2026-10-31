package ru.metrocraft.port;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class FilterItem extends Item {
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (player.isSpectator()) return new ActionResult<>(EnumActionResult.FAIL, held);
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST}) {
            ItemStack armor = player.getItemStackFromSlot(slot);
            if (!(armor.getItem() instanceof MetroArmor) || !((MetroArmor)armor.getItem()).isGasMask()) continue;
            if (!armor.hasTagCompound()) armor.setTagCompound(new NBTTagCompound());
            if (armor.getTagCompound().getInteger("MetroFilterTicks") >= 6000) continue;
            if (!world.isRemote) {
                armor.getTagCompound().setInteger("MetroFilterTicks", 6000);
                if (!player.capabilities.isCreativeMode) held.shrink(1);
                player.inventory.markDirty();
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, held);
        }
        return new ActionResult<>(EnumActionResult.FAIL, held);
    }
}
