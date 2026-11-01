package ru.metrocraft.port.blackrazid;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.*;
import ru.metrocraft.port.*;

/** Native one-slot suits stay whole; no fictitious leggings/boots/items are registered. */
public final class BlackrazidArmorItem extends MetroArmor {
    public final BlackrazidContent.Entry entry;
    public BlackrazidArmorItem(BlackrazidContent.Entry e){super(definition(e));entry=e;}
    private static Definition definition(BlackrazidContent.Entry e){
        Definition d=new Definition();d.id=e.id;d.kind="armor";d.slot="helmet".equals(e.mode)?"head":"chest";
        d.durability=e.condition;d.defence=e.defence;d.gasMask=e.gasMask;d.knockbackModifier=0;return d;
    }
    @Override public String getArmorTexture(ItemStack stack,Entity entity,EntityEquipmentSlot slot,String type){
        return "metroport:textures/blackrazid/armor/"+entry.texture+".png";
    }
    @Override @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity,ItemStack stack,EntityEquipmentSlot slot,ModelBiped original){
        return ru.metrocraft.port.client.BlackrazidModel.get(entry.model,entry.mode);
    }
}
