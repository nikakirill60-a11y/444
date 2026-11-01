package ru.metrocraft.port.mcmod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ru.metrocraft.port.*;
/** Original condition-zero behavior: unusable until repaired, never delete the whole gun. */
public final class McmodAkItem extends GunItem {
    public McmodAkItem(Definition d){super(d);}
    @Override protected boolean canFire(ItemStack stack){return stack.getItemDamage()<getMaxDamage();}
    @Override public void setDamage(ItemStack stack,int damage){
        super.setDamage(stack,Math.max(0,Math.min(getMaxDamage(),damage)));
        if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("condition",Math.max(0,getMaxDamage()-stack.getItemDamage()));
    }
}
