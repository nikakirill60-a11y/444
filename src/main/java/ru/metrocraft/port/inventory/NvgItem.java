package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import ru.metrocraft.port.*;

public final class NvgItem extends MetroArmor {
    public NvgItem(Definition definition) {super(definition);}
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand) {
        ItemStack held=player.getHeldItem(hand);
        if(player.isSpectator() || !NvgEquipment.get(player).isEmpty())return new ActionResult<>(EnumActionResult.FAIL,held);
        if(!world.isRemote)NvgEquipment.equipAfterUse(player,hand,held);
        return new ActionResult<>(EnumActionResult.SUCCESS,held);
    }
}
