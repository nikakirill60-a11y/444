package ru.metrocraft.port;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;

public final class ToolItem extends ContentItem {
    public ToolItem(Definition d) {
        super(d);
        setMaxStackSize(1);
        if (d.uses > 1) setMaxDamage(d.uses);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        if (player.isSpectator() || player.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        boolean used = false;
        if (definition.heal > 0 && player.getHealth() < player.getMaxHealth()) {
            player.heal(definition.heal);
            used = true;
        } else if (hand == EnumHand.MAIN_HAND) {
            ItemStack target = player.getHeldItemOffhand();
            boolean valid = definition.repairGun && target.getItem() instanceof GunItem
                    || definition.repairArmor && target.getItem() instanceof MetroArmor;
            if (valid && target.isItemDamaged()) {
                target.setItemDamage(0);
                used = true;
            }
        }
        if (used) {
            player.getCooldownTracker().setCooldown(this, 20);
            if (!player.capabilities.isCreativeMode) {
                // Explicit use counter avoids Unbreaking and ItemStack.damageItem's > maxDamage boundary.
                if (definition.uses > 1 && stack.getItemDamage() + 1 < definition.uses) {
                    stack.setItemDamage(stack.getItemDamage() + 1);
                } else stack.shrink(1);
            }
        }
        return new ActionResult<>(used ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> lines, ITooltipFlag flag) {
        lines.add(I18n.format(definition.heal > 0 ? "metroport.tool.heal" : "metroport.tool.repair"));
    }
}
