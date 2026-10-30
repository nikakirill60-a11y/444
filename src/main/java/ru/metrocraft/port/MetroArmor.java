package ru.metrocraft.port;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;

public final class MetroArmor extends ItemArmor {
    private final Definition definition;

    private static ArmorMaterial material(Definition d) {
        int protection = Math.max(0, Math.min(16, Math.round(d.defence * 20)));
        int[] reduction = new int[]{0, 0, d.slot.equals("chest") ? protection : 0, d.slot.equals("head") ? protection : 0};
        return EnumHelper.addArmorMaterial("METROPORT_" + d.id.toUpperCase(java.util.Locale.ROOT),
                "minecraft:iron", 1, reduction, 0, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0);
    }

    public MetroArmor(Definition d) {
        super(material(d), 0, d.slot.equals("head") ? EntityEquipmentSlot.HEAD : EntityEquipmentSlot.CHEST);
        definition = d;
        setMaxDamage(Math.max(1, d.durability));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        // Legacy custom-model atlases do not have the UV layout of a vanilla biped.
        return "minecraft:textures/models/armor/iron_layer_1.png";
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack ingredient) {
        return false; // Repair kits, not an unintended iron-ingot repair recipe.
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && definition.nightVision) {
            PotionEffect current = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
            if (current == null || current.getDuration() < 220) {
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> lines, ITooltipFlag flag) {
        if (definition.gasMask) lines.add(I18n.format("metroport.armor.gas"));
    }
}
