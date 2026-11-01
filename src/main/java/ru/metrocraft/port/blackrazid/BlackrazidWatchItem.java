package ru.metrocraft.port.blackrazid;

import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fml.relauncher.*;
import java.util.List;

/** Time only. Does not expose radiation, oxygen or the removed native player-data system. */
public final class BlackrazidWatchItem extends Item {
    public BlackrazidWatchItem(){setMaxStackSize(1);}
    @Override @SideOnly(Side.CLIENT) public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag){lines.add(net.minecraft.client.resources.I18n.format("metroport.blackrazid.watch.help"));}
}
