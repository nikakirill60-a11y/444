package ru.metrocraft.port.metromod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import java.util.List;

/** Faithful supplies, NOT invented healing consumables. No right-click or use-finish mutation. */
public final class MedicalMaterialItem extends Item {
    public MedicalMaterialItem(int stackLimit){setMaxStackSize(stackLimit);}
    @Override @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack,World world,List<String> lines,ITooltipFlag flag){
        lines.add(I18n.translateToLocal("metroport.metromod.material"));
    }
}
