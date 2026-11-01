package ru.metrocraft.port.mcmod;
import net.minecraft.item.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import java.util.List;
public final class WatchItem extends Item {
    public WatchItem(){setMaxStackSize(1);}
    @Override @SideOnly(Side.CLIENT) public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag){lines.add(I18n.format("metroport.watch.help"));}
    public static int minutes(long worldTime){return (int)((Math.floorMod(worldTime,24000L)*1440/24000+360)%1440);}
    public static String time(long worldTime){int minutes=minutes(worldTime);return String.format(java.util.Locale.ROOT,"%02d:%02d",minutes/60,minutes%60);}
}
