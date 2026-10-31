package ru.metrocraft.port.mcmod;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import ru.metrocraft.port.Definition;
import ru.metrocraft.port.MetroArmor;
import java.util.*;

/** Genuine one-slot armor: whole outfit or separate helmet/vest, never six fake item pieces. */
public final class McmodArmorItem extends MetroArmor {
    public final McmodArmorEntry entry;
    public McmodArmorItem(McmodArmorEntry entry) { super(definition(entry)); this.entry = entry; }
    private static Definition definition(McmodArmorEntry entry) {
        Definition d = new Definition();
        d.id=entry.id; d.kind="armor"; d.slot=entry.helmet()?"head":"chest";
        d.durability=entry.condition; d.defence=(float)entry.defence; d.gasMask=entry.radiation; d.knockbackModifier=0;
        return d;
    }
    static void syncCondition(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("condition", Math.max(0, stack.getMaxDamage()-stack.getItemDamage()));
    }
    @Override public void onArmorTick(World world, net.minecraft.entity.player.EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world,player,stack);
        if (!world.isRemote) syncCondition(stack);
    }
    @Override public void damageArmor(EntityLivingBase entity, ItemStack stack, net.minecraft.util.DamageSource source, int damage, int slot) {
        super.damageArmor(entity,stack,source,damage,slot);
        if (!stack.isEmpty()) syncCondition(stack);
    }
    @Override public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "metroport:textures/mcmod/armor/"+entry.geometryModel()+".png";
    }
    @Override @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped original) {
        return ru.metrocraft.port.client.McmodArmorModel.get(entry);
    }
    @Override @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> lines, ITooltipFlag flag) {
        lines.add(I18n.format("metroport.mcmod.armor."+entry.mode));
        lines.add(I18n.format("metroport.mcmod.armor.protection",String.format(Locale.ROOT,"%.1f",entry.defence*100)));
        lines.add(I18n.format("metroport.mcmod.armor.set",entry.sourceProtect));
        lines.add(I18n.format("metroport.mcmod.armor.condition",Math.max(0,stack.getMaxDamage()-stack.getItemDamage()),entry.condition));
        if(entry.radiation) {
            lines.add(I18n.format("metroport.mcmod.armor.radiation"));
            super.addInformation(stack,world,lines,flag);
        }
    }
}
