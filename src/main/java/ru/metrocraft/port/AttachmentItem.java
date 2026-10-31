package ru.metrocraft.port;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import java.util.Arrays;

public final class AttachmentItem extends ContentItem {
    public static final String TAG = "MetroAttachments";
    public AttachmentItem(Definition d) { super(d); }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand), gun = player.getHeldItemOffhand();
        if (hand != EnumHand.MAIN_HAND || !(gun.getItem() instanceof GunItem) || player.isSpectator()) return result(false,held);
        Definition weapon = ((GunItem)gun.getItem()).getDefinition();
        if (!Arrays.asList(weapon.allowedAttachments).contains(definition.id)) return result(false,held);
        NBTTagCompound root = gun.hasTagCompound() ? gun.getTagCompound() : new NBTTagCompound();
        NBTTagCompound attachments = root.getCompoundTag(TAG);
        String old = attachments.getString(definition.attachmentSlot);
        if (player.isSneaking()) {
            if (old.isEmpty()) return result(false,held);
            if (!world.isRemote) { attachments.removeTag(definition.attachmentSlot); giveBack(player, old); }
        } else {
            if (old.equals(definition.id)) return result(false,held);
            if (!world.isRemote) {
                attachments.setString(definition.attachmentSlot, definition.id);
                giveBack(player, old);
                if (!player.capabilities.isCreativeMode) held.shrink(1);
            }
        }
        if (!world.isRemote) {
            root.setTag(TAG, attachments); gun.setTagCompound(root); player.inventory.markDirty();
        }
        return result(true,held);
    }
    private static void giveBack(EntityPlayer player, String id) {
        Item item = MetroPort.ITEMS.get(id);
        if (item instanceof AttachmentItem && !player.capabilities.isCreativeMode) {
            ItemStack returned = new ItemStack(item);
            if (!player.inventory.addItemStackToInventory(returned)) player.dropItem(returned,false);
        }
    }
    private static ActionResult<ItemStack> result(boolean success, ItemStack held) {
        return new ActionResult<>(success ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, held);
    }
    public static float multiplier(ItemStack gun, String stat) {
        float value = 1;
        boolean scope=false;
        if (!(gun.getItem() instanceof GunItem)) return value;
        Definition weapon = ((GunItem)gun.getItem()).getDefinition();
        NBTTagCompound attachments = gun.hasTagCompound()?gun.getTagCompound().getCompoundTag(TAG):new NBTTagCompound();
        for (String slot : attachments.getKeySet()) {
            String id = attachments.getString(slot);
            Item item = MetroPort.ITEMS.get(id);
            if (!(item instanceof AttachmentItem) || !Arrays.asList(weapon.allowedAttachments).contains(id)) continue;
            Definition d = ((AttachmentItem)item).definition;
            if (!slot.equals(d.attachmentSlot)) continue;
            switch (stat) {
                case "damage": value *= d.damageMultiplier; break;
                case "spread": value *= d.spreadMultiplier; break;
                case "reload": value *= d.reloadMultiplier; break;
                case "recoil": value *= d.recoilMultiplier; break;
                case "speed": value *= d.bulletSpeedMultiplier; break;
                case "sound": if(d.silencer)value *= .15F; break;
                case "zoom": value *= d.zoom; if(slot.equals("scope"))scope=true; break;
            }
        }
        return stat.equals("zoom")&&!scope?value*weapon.zoom:value;
    }
}
