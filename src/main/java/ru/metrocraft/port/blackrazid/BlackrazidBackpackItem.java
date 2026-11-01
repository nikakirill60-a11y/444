package ru.metrocraft.port.blackrazid;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import ru.metrocraft.port.MetroPort;
import java.util.List;

/** Native portable storage, not an additional unlock of 9/18/27 player slots. Equip using the existing bag slot. */
public final class BlackrazidBackpackItem extends ru.metrocraft.port.inventory.BackpackItem {
    public final BlackrazidContent.Entry entry;
    public BlackrazidBackpackItem(BlackrazidContent.Entry entry){super(0);this.entry=entry;}
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand){
        ItemStack held=player.getHeldItem(hand);
        if(player.isSpectator())return new ActionResult<>(EnumActionResult.FAIL,held);
        if(!world.isRemote)player.openGui(MetroPort.INSTANCE,2,world,hand==EnumHand.MAIN_HAND?0:1,0,0);
        return new ActionResult<>(EnumActionResult.SUCCESS,held);
    }
    @Override @SideOnly(Side.CLIENT) public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag){
        lines.add(I18n.format("metroport.blackrazid.storage",entry.storageSlots));lines.add(I18n.format("metroport.blackrazid.help"));
    }
}
