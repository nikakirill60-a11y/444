package ru.metrocraft.port.patches;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import ru.metrocraft.port.mechanics.NoDrop;
import java.util.List;

public final class FactionPatchItem extends Item {
    public final String faction;
    public FactionPatchItem(String faction) { this.faction=faction; setMaxStackSize(64); }
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand) {
        ItemStack held=player.getHeldItem(hand);
        ItemStack armor=player.getHeldItem(hand==EnumHand.MAIN_HAND?EnumHand.OFF_HAND:EnumHand.MAIN_HAND);
        if(!FactionPatches.accepts(armor))armor=player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if(player.isSpectator() || !FactionPatches.accepts(armor))return new ActionResult<>(EnumActionResult.FAIL,held);
        ItemStack old=FactionPatches.get(armor);
        boolean remove=player.isSneaking();
        if(remove?old.isEmpty():(!old.isEmpty() && old.getItem()==this))
            return new ActionResult<>(EnumActionResult.FAIL,held);
        if(!world.isRemote) {
            ItemStack returned=remove?FactionPatches.detach(armor):FactionPatches.attach(armor,held);
            if(!remove && !player.capabilities.isCreativeMode)held.shrink(1);
            // A full inventory never deletes or duplicates the replaced patch: use the persistent return queue.
            if(!returned.isEmpty()) {
                // Defer even when space exists: returning into the last consumed held slot NOW
                // would be overwritten by PlayerInteractionManager's ActionResult write-back.
                NoDrop.retain(player,returned);
            }
            player.inventory.markDirty(); player.inventoryContainer.detectAndSendChanges();
        }
        return new ActionResult<>(EnumActionResult.SUCCESS,held);
    }
    @Override @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag) {
        lines.add(I18n.format("metroport.patch.attach"));
        lines.add(I18n.format("metroport.patch.remove"));
    }
}
