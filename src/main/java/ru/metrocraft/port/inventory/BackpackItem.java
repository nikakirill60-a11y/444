package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import ru.metrocraft.port.MetroPort;

public class BackpackItem extends Item {
    public final int rows;
    public BackpackItem(int rows) { this.rows=rows; setMaxStackSize(1); }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack held=player.getHeldItem(hand);
        if(player.isSpectator()) return new ActionResult<>(EnumActionResult.FAIL,held);
        if(!world.isRemote) {
            if(ExtraEquipment.get(player,ExtraEquipment.Kind.BLACKRAZID_BACKPACK).isEmpty())
                ExtraEquipment.equipAfterUse(player,ExtraEquipment.Kind.BLACKRAZID_BACKPACK,hand,held);
            player.openGui(MetroPort.INSTANCE,0,world,0,0,0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS,held);
    }
}
