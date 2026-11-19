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

/** Carry-capacity bag. Legacy NBT contents can be recovered, never replenished. */
public final class BlackrazidBackpackItem extends ru.metrocraft.port.inventory.BackpackItem {
    public final BlackrazidContent.Entry entry;
    public BlackrazidBackpackItem(BlackrazidContent.Entry entry){super(0);this.entry=entry;}
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand){
        ItemStack held=player.getHeldItem(hand);
        if(player.isSpectator())return new ActionResult<>(EnumActionResult.FAIL,held);
        if(!world.isRemote){
            if(BackpackContainer.valid(player,hand==EnumHand.MAIN_HAND?0:1))
                player.openGui(MetroPort.INSTANCE,2,world,hand==EnumHand.MAIN_HAND?0:1,0,0);
            else return super.onItemRightClick(world,player,hand);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS,held);
    }
}
