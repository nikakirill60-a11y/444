package ru.metrocraft.port.patches;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.metrocraft.port.MetroPort;

/** One cosmetic, recoverable patch per chest piece. Its full stack tag travels WITH the armor. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class FactionPatches {
    private static final String KEY="MetroFactionPatch";
    private FactionPatches() {}
    @SubscribeEvent public static void items(RegistryEvent.Register<Item> event) {
        for(String faction:new String[]{"satanists","contrabandists","legion","mgu","bandits","reich","worm_cult","red_line","polis","stalkers","hansa"}) {
            String id="patch_"+faction;
            Item item=new FactionPatchItem(faction).setRegistryName(MetroPort.ID,id)
                    .setTranslationKey(MetroPort.ID+"."+id).setCreativeTab(MetroPort.THINGS);
            event.getRegistry().register(item);
            if(MetroPort.ITEMS.put(id,item)!=null)throw new IllegalStateException("Duplicate patch id: "+id);
        }
    }
    public static boolean accepts(ItemStack armor) {
        return !armor.isEmpty() && armor.getItem() instanceof ItemArmor
                && ((ItemArmor)armor.getItem()).armorType==EntityEquipmentSlot.CHEST;
    }
    public static ItemStack get(ItemStack armor) {
        if(!accepts(armor) || !armor.hasTagCompound() || !armor.getTagCompound().hasKey(KEY,10))return ItemStack.EMPTY;
        ItemStack patch=new ItemStack(armor.getTagCompound().getCompoundTag(KEY).copy());
        if(patch.isEmpty() || !(patch.getItem() instanceof FactionPatchItem))return ItemStack.EMPTY;
        patch.setCount(1); return patch;
    }
    public static ItemStack attach(ItemStack armor,ItemStack patch) {
        if(!accepts(armor) || patch.isEmpty() || !(patch.getItem() instanceof FactionPatchItem))
            throw new IllegalArgumentException("Patch requires chest armor");
        ItemStack old=get(armor),one=patch.copy(); one.setCount(1);
        if(!armor.hasTagCompound())armor.setTagCompound(new NBTTagCompound());
        armor.getTagCompound().setTag(KEY,one.writeToNBT(new NBTTagCompound()));
        return old;
    }
    public static ItemStack detach(ItemStack armor) {
        ItemStack old=get(armor);
        if(!old.isEmpty())armor.getTagCompound().removeTag(KEY);
        return old;
    }
}
